package com.hm.efn.entity.effect;

import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.bus.api.SubscribeEvent;
import com.hm.efn.compat.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class BlastSummonedSwordEntity extends Entity {
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BlastSummonedSwordEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Byte> CONTROLLER_STATE = SynchedEntityData.defineId(BlastSummonedSwordEntity.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Integer> TOTAL_SWORDS = SynchedEntityData.defineId(BlastSummonedSwordEntity.class, EntityDataSerializers.INT);
   private int standbyTicks = 10;
   private int launchInterval = 2;
   private int spawnsPerTick = 4;
   private UUID ownerUUID;
   private UUID targetUUID;
   private final List<UUID> childSwords = new ArrayList<>();
   private final Queue<UUID> launchQueue = new LinkedList<>();
   private final Map<UUID, Integer> swordFormationIndices = new HashMap<>();
   private final Queue<BlastSummonedSwordEntity.PendingSpawnData> spawnQueue = new LinkedList<>();
   private int launchTickCounter = 0;
   private BlastSummonedSwordEntity.State currentState = BlastSummonedSwordEntity.State.STANDBY;
   private boolean isRegisteredToBus = false;
   private final Object eventBusLock = new Object();
   private final Map<Integer, Integer> clientSwordBinding = new HashMap<>();

   public BlastSummonedSwordEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noCulling = true;
      this.noPhysics = true;
   }

   public BlastSummonedSwordEntity(Level level, LivingEntity owner) {
      this((EntityType<?>)EFNEntity.BLAST_SUMMONED_SWORD.get(), level);
      this.setOwner(owner);
      this.noPhysics = true;
      this.noCulling = true;
      this.setPos(owner.getX(), owner.getEyeY(), owner.getZ());
   }

   public static void summon(Level level, LivingEntity owner) {
      summon(level, owner, 8, 10, 2, 4);
   }

   public static void summon(Level level, LivingEntity owner, int totalSwords, int standbyTicks, int launchInterval, int spawnsPerTick) {
      if (!level.isClientSide) {
         BlastSummonedSwordEntity controller = new BlastSummonedSwordEntity(level, owner);
         controller.setConfig(totalSwords, standbyTicks, launchInterval, spawnsPerTick);
         level.addFreshEntity(controller);
         controller.spawnChildSwords();
      }
   }

   public void setConfig(int totalSwords, int standbyTicks, int launchInterval, int spawnsPerTick) {
      this.entityData.set(TOTAL_SWORDS, totalSwords);
      this.standbyTicks = standbyTicks;
      this.launchInterval = launchInterval;
      this.spawnsPerTick = spawnsPerTick;
   }

   public int getTotalSwords() {
      return (Integer)this.entityData.get(TOTAL_SWORDS);
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      builder.define(OWNER_ID, -1);
      builder.define(CONTROLLER_STATE, (byte)BlastSummonedSwordEntity.State.STANDBY.ordinal());
      builder.define(TOTAL_SWORDS, 8);
   }

   public void setOwner(LivingEntity owner) {
      this.ownerUUID = owner.getUUID();
      this.entityData.set(OWNER_ID, owner.getId());
   }

   @Nullable
   public LivingEntity getOwner() {
      if (!this.level().isClientSide && this.ownerUUID != null) {
         if (((ServerLevel)this.level()).getEntity(this.ownerUUID) instanceof LivingEntity living) {
            if ((Integer)this.entityData.get(OWNER_ID) != living.getId()) {
               this.entityData.set(OWNER_ID, living.getId());
            }

            return living;
         }
      } else if (this.level().isClientSide) {
         int id = (Integer)this.entityData.get(OWNER_ID);
         if (id != -1 && this.level().getEntity(id) instanceof LivingEntity living) {
            return living;
         }
      }

      return null;
   }

   public BlastSummonedSwordEntity.State getCurrentState() {
      return BlastSummonedSwordEntity.State.values()[this.entityData.get(CONTROLLER_STATE)];
   }

   private void setCurrentState(BlastSummonedSwordEntity.State state) {
      this.currentState = state;
      this.entityData.set(CONTROLLER_STATE, (byte)state.ordinal());
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.targetUUID != null && this.level() instanceof ServerLevel ? (LivingEntity)((ServerLevel)this.level()).getEntity(this.targetUUID) : null;
   }

   private void spawnChildSwords() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         this.discard();
      } else if (this.level() instanceof ServerLevel) {
         int total = this.getTotalSwords();
         int totalRows = (int)Math.ceil(total / 2.0);

         for (int i = 0; i < total; i++) {
            boolean isLeft = i < totalRows;
            int verticalIndex = i % totalRows;
            this.spawnQueue.add(new BlastSummonedSwordEntity.PendingSpawnData(i, isLeft, verticalIndex));
         }
      }
   }

   private void processSpawnQueue() {
      if (!this.spawnQueue.isEmpty()) {
         LivingEntity owner = this.getOwner();
         if (owner != null && this.level() instanceof ServerLevel serverLevel) {
            int var8 = this.spawnsPerTick;
            int totalRows = (int)Math.ceil(this.getTotalSwords() / 2.0);

            while (var8 > 0 && !this.spawnQueue.isEmpty()) {
               BlastSummonedSwordEntity.PendingSpawnData data = this.spawnQueue.poll();
               SinSummonedSwordEntity sword = new SinSummonedSwordEntity(
                  (EntityType<? extends Mob>)EFNEntity.SIN_SUMMONED_SWORD.get(), owner, 1.35F, this.level(), true
               );
               this.swordFormationIndices.put(sword.getUUID(), data.index);
               Vec3 spawnPos = this.calculateStandbyPosition(
                  owner, data.isLeft, data.verticalIndex, totalRows, owner.position(), owner.getYHeadRot(), owner.getXRot()
               );
               sword.setPos(spawnPos);
               sword.setYRot(owner.getYHeadRot());
               sword.setXRot(owner.getXRot());
               sword.setBlast(true);
               sword.setShootSpeed(4);
               serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, spawnPos.x, spawnPos.y, spawnPos.z, 5, 0.1, 0.1, 0.1, 0.02);
               owner.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.5F, 1.0F);
               this.level().addFreshEntity(sword);
               this.childSwords.add(sword.getUUID());
               var8--;
            }
         }
      }
   }

   public void tick() {
      super.tick();
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         this.setPos(owner.getEyePosition());
         if (this.level().isClientSide) {
            synchronized (this.eventBusLock) {
               if (!this.isRegisteredToBus && !this.isRemoved()) {
                  NeoForge.EVENT_BUS.register(this);
                  this.isRegisteredToBus = true;
               }
            }
         } else if (owner.isDeadOrDying()) {
            this.cleanup();
         } else {
            this.findSharedTarget();
            switch (this.getCurrentState()) {
               case STANDBY:
                  this.tickStandby();
                  break;
               case LAUNCHING:
                  this.tickLaunching();
               case FINISHED:
            }
         }
      }
   }

   @SubscribeEvent
   public void onClientTick(Post event) {
      if (true) {
         if (!this.isRemoved() && this.level().isClientSide) {
            LivingEntity owner = this.getOwner();
            if (owner != null) {
               LivingEntity target = null;
               LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
               if (ownerPatch != null) {
                  target = ownerPatch.getTarget();
               }

               List<SinSummonedSwordEntity> availableSwords = this.level()
                  .getEntitiesOfClass(
                     SinSummonedSwordEntity.class,
                     owner.getBoundingBox().inflate(10.0),
                     swordx -> swordx.isInStandby() && swordx.getOwner() == owner && swordx.isBlast()
                  );
               this.clientSwordBinding.entrySet().removeIf(entryx -> {
                  Entity e = this.level().getEntity((Integer)entryx.getValue());
                  return !(e instanceof SinSummonedSwordEntity) || !((SinSummonedSwordEntity)e).isInStandby();
               });
               int total = this.getTotalSwords();
               int totalRows = (int)Math.ceil(total / 2.0);

               for (SinSummonedSwordEntity sword : availableSwords) {
                  if (!this.clientSwordBinding.containsValue(sword.getId())) {
                     for (int i = 0; i < total; i++) {
                        if (!this.clientSwordBinding.containsKey(i)) {
                           this.clientSwordBinding.put(i, sword.getId());
                           break;
                        }
                     }
                  }
               }

               for (Entry<Integer, Integer> entry : this.clientSwordBinding.entrySet()) {
                  int index = entry.getKey();
                  if (this.level().getEntity(entry.getValue()) instanceof SinSummonedSwordEntity sword) {
                     boolean isLeft = index < totalRows;
                     int verticalIndex = index % totalRows;
                     Vec3 currentPos = this.calculateStandbyPosition(
                        owner, isLeft, verticalIndex, totalRows, owner.position(), owner.getYHeadRot(), owner.getXRot()
                     );
                     Vec3 oldPos = this.calculateStandbyPosition(
                        owner, isLeft, verticalIndex, totalRows, new Vec3(owner.xo, owner.yo, owner.zo), owner.yHeadRotO, owner.xRotO
                     );
                     sword.setPosRaw(currentPos.x, currentPos.y, currentPos.z);
                     sword.xo = oldPos.x;
                     sword.yo = oldPos.y;
                     sword.zo = oldPos.z;
                     float currentYaw;
                     float currentPitch;
                     float oldYaw;
                     float oldPitch;
                     if (target != null && target.isAlive()) {
                        Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.7, target.getZ());
                        Vec3 dir = targetPos.subtract(currentPos).normalize();
                        currentYaw = (float)(Mth.atan2(dir.z, dir.x) * (180.0 / Math.PI)) - 90.0F;
                        currentPitch = (float)(-(Mth.atan2(dir.y, dir.horizontalDistance()) * (180.0 / Math.PI)));
                        Vec3 oldTargetPos = new Vec3(target.xo, target.yo + target.getBbHeight() * 0.7, target.zo);
                        Vec3 oldDir = oldTargetPos.subtract(oldPos).normalize();
                        oldYaw = (float)(Mth.atan2(oldDir.z, oldDir.x) * (180.0 / Math.PI)) - 90.0F;
                        oldPitch = (float)(-(Mth.atan2(oldDir.y, oldDir.horizontalDistance()) * (180.0 / Math.PI)));
                     } else {
                        currentYaw = owner.getYHeadRot();
                        currentPitch = owner.getXRot();
                        oldYaw = owner.yHeadRotO;
                        oldPitch = owner.xRotO;
                     }

                     sword.setYRot(currentYaw);
                     sword.setXRot(currentPitch);
                     sword.yRotO = oldYaw;
                     sword.xRotO = oldPitch;
                     sword.setYBodyRot(currentYaw);
                     sword.setYHeadRot(currentYaw);
                  }
               }
            }
         } else {
            synchronized (this.eventBusLock) {
               if (this.isRegisteredToBus) {
                  NeoForge.EVENT_BUS.unregister(this);
                  this.isRegisteredToBus = false;
               }
            }
         }
      }
   }

   public void remove(@NotNull RemovalReason pReason) {
      super.remove(pReason);
      synchronized (this.eventBusLock) {
         if (this.level().isClientSide && this.isRegisteredToBus) {
            NeoForge.EVENT_BUS.unregister(this);
            this.isRegisteredToBus = false;
         }
      }
   }

   private void tickStandby() {
      this.processSpawnQueue();
      this.updateAllChildSwordsPosition();
      if (this.spawnQueue.isEmpty() && this.tickCount >= this.standbyTicks) {
         this.prepareToLaunch();
      }
   }

   private void tickLaunching() {
      this.updateAllChildSwordsPosition();
      this.launchTickCounter++;
      if (this.launchTickCounter >= this.launchInterval) {
         this.launchTickCounter = 0;
         this.launchNextSword();
      }

      if (this.launchQueue.isEmpty() && this.childSwords.isEmpty()) {
         this.setCurrentState(BlastSummonedSwordEntity.State.FINISHED);
         this.discard();
      }
   }

   private void findSharedTarget() {
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
         if (ownerPatch != null) {
            LivingEntity target = ownerPatch.getTarget();
            if (target != null && target.isAlive()) {
               this.targetUUID = target.getUUID();
            } else {
               this.targetUUID = null;
            }
         } else {
            this.targetUUID = null;
         }
      }
   }

   private void prepareToLaunch() {
      List<UUID> shuffledSwords = new ArrayList<>(this.childSwords);
      Collections.shuffle(shuffledSwords);
      this.launchQueue.addAll(shuffledSwords);
      this.setCurrentState(BlastSummonedSwordEntity.State.LAUNCHING);
   }

   private void launchNextSword() {
      if (!this.launchQueue.isEmpty()) {
         UUID swordUUID = this.launchQueue.poll();
         if (swordUUID != null) {
            if (((ServerLevel)this.level()).getEntity(swordUUID) instanceof SinSummonedSwordEntity sword) {
               this.childSwords.remove(swordUUID);
               LivingEntity target = this.getTarget();
               if (target != null && target.isAlive()) {
                  sword.aimAtEntity(target);
               } else {
                  LivingEntity owner = this.getOwner();
                  if (owner != null) {
                     float ownerYRot = owner.getYHeadRot();
                     float ownerXRot = owner.getXRot();
                     sword.setYRot(ownerYRot);
                     sword.setXRot(ownerXRot);
                     sword.setSyncXRot(ownerXRot);
                  }
               }

               sword.launch(target);
               sword.playSound(SoundEvents.FIRECHARGE_USE, 0.8F, 1.0F);
            }
         }
      }
   }

   private Vec3 calculateStandbyPosition(LivingEntity owner, boolean isLeft, int verticalIndex, int totalRows, Vec3 basePos, float yaw, float pitch) {
      double sideOffset = isLeft ? -0.95 : 0.95;
      double midVertical = (totalRows - 1) / 2.0;
      double verticalOffset = (verticalIndex - midVertical) * 0.35;
      double forwardOffset = (midVertical - verticalIndex) * 0.2;
      float yawRad = yaw * (float) (Math.PI / 180.0);
      Vec3 rightDir = new Vec3(-Math.cos(yawRad), 0.0, -Math.sin(yawRad));
      float pitchRad = pitch * (float) (Math.PI / 180.0);
      float f1 = (float)(-Math.sin(yawRad) * Math.cos(pitchRad));
      float f2 = (float)(-Math.sin(pitchRad));
      float f3 = (float)(Math.cos(yawRad) * Math.cos(pitchRad));
      Vec3 lookVec = new Vec3(f1, f2, f3);
      return basePos.add(rightDir.scale(sideOffset))
         .add(0.0, owner.getEyeHeight() + verticalOffset - 0.4, 0.0)
         .add(lookVec.scale(forwardOffset))
         .subtract(lookVec.scale(0.85));
   }

   private void updateAllChildSwordsPosition() {
      LivingEntity owner = this.getOwner();
      LivingEntity target = this.getTarget();
      if (owner != null) {
         ServerLevel serverLevel = (ServerLevel)this.level();
         List<UUID> toRemove = new ArrayList<>();
         int total = this.getTotalSwords();
         int totalRows = (int)Math.ceil(total / 2.0);

         for (UUID swordUUID : this.childSwords) {
            Entity entity = serverLevel.getEntity(swordUUID);
            if (entity instanceof SinSummonedSwordEntity sword && sword.isInStandby()) {
               Integer formationIndex = this.swordFormationIndices.get(swordUUID);
               if (formationIndex != null) {
                  boolean isLeft = formationIndex < totalRows;
                  int verticalIndex = formationIndex % totalRows;
                  Vec3 currentPos = this.calculateStandbyPosition(owner, isLeft, verticalIndex, totalRows, owner.position(), owner.getYHeadRot(), owner.getXRot());
                  sword.setPos(currentPos.x, currentPos.y, currentPos.z);
                  if (target != null && target.isAlive()) {
                     sword.aimAtEntity(target);
                  } else {
                     float ownerYRot = owner.getYHeadRot();
                     float ownerXRot = owner.getXRot();
                     sword.setYRot(ownerYRot);
                     sword.setXRot(ownerXRot);
                     sword.setSyncXRot(ownerXRot);
                     sword.setYBodyRot(ownerYRot);
                     sword.setYHeadRot(ownerYRot);
                  }
               }
            } else if (entity == null || !entity.isAlive()) {
               toRemove.add(swordUUID);
               this.swordFormationIndices.remove(swordUUID);
            }
         }

         this.childSwords.removeAll(toRemove);
         this.launchQueue.removeAll(toRemove);
      }
   }

   private void cleanup() {
      if (this.level() instanceof ServerLevel serverLevel) {
         List<UUID> allSwords = new ArrayList<>(this.childSwords);
         allSwords.addAll(this.launchQueue);

         for (UUID swordUUID : allSwords) {
            Entity sword = serverLevel.getEntity(swordUUID);
            if (sword != null) {
               sword.discard();
            }
         }
      }

      this.discard();
   }

   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public void setOwnerUUID(UUID ownerUUID) {
      this.ownerUUID = ownerUUID;
   }

   public UUID getTargetUUID() {
      return this.targetUUID;
   }

   public void setTargetUUID(UUID targetUUID) {
      this.targetUUID = targetUUID;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean hurt(@NotNull DamageSource source, float amount) {
      return false;
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }

      if (pCompound.hasUUID("Target")) {
         this.targetUUID = pCompound.getUUID("Target");
      }

      if (pCompound.contains("State", 8)) {
         try {
            this.setCurrentState(BlastSummonedSwordEntity.State.valueOf(pCompound.getString("State")));
         } catch (IllegalArgumentException e) {
            this.setCurrentState(BlastSummonedSwordEntity.State.FINISHED);
         }
      }

      if (pCompound.contains("TotalSwords")) {
         this.entityData.set(TOTAL_SWORDS, pCompound.getInt("TotalSwords"));
      }

      if (pCompound.contains("StandbyTicks")) {
         this.standbyTicks = pCompound.getInt("StandbyTicks");
      }

      if (pCompound.contains("LaunchInterval")) {
         this.launchInterval = pCompound.getInt("LaunchInterval");
      }

      if (pCompound.contains("SpawnsPerTick")) {
         this.spawnsPerTick = pCompound.getInt("SpawnsPerTick");
      }

      this.launchTickCounter = pCompound.getInt("LaunchTicker");
      this.childSwords.clear();
      ListTag childListTag = pCompound.getList("ChildSwords", 8);
      childListTag.forEach(tag -> this.childSwords.add(UUID.fromString(tag.getAsString())));
      this.launchQueue.clear();
      ListTag queueListTag = pCompound.getList("LaunchQueue", 8);
      queueListTag.forEach(tag -> this.launchQueue.add(UUID.fromString(tag.getAsString())));
      this.swordFormationIndices.clear();
      ListTag mapTag = pCompound.getList("FormationIndices", 10);
      mapTag.forEach(tag -> {
         CompoundTag entryTag = (CompoundTag)tag;
         if (entryTag.hasUUID("UUID")) {
            this.swordFormationIndices.put(entryTag.getUUID("UUID"), entryTag.getInt("Index"));
         }
      });
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }

      if (this.targetUUID != null) {
         pCompound.putUUID("Target", this.targetUUID);
      }

      pCompound.putString("State", this.getCurrentState().name());
      pCompound.putInt("LaunchTicker", this.launchTickCounter);
      pCompound.putInt("TotalSwords", this.getTotalSwords());
      pCompound.putInt("StandbyTicks", this.standbyTicks);
      pCompound.putInt("LaunchInterval", this.launchInterval);
      pCompound.putInt("SpawnsPerTick", this.spawnsPerTick);
      ListTag childListTag = new ListTag();
      this.childSwords.forEach(uuid -> childListTag.add(StringTag.valueOf(uuid.toString())));
      pCompound.put("ChildSwords", childListTag);
      ListTag queueListTag = new ListTag();
      this.launchQueue.forEach(uuid -> queueListTag.add(StringTag.valueOf(uuid.toString())));
      pCompound.put("LaunchQueue", queueListTag);
      ListTag mapTag = new ListTag();
      this.swordFormationIndices.forEach((uuid, index) -> {
         CompoundTag entryTag = new CompoundTag();
         entryTag.putUUID("UUID", uuid);
         entryTag.putInt("Index", index);
         mapTag.add(entryTag);
      });
      pCompound.put("FormationIndices", mapTag);
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   private static class PendingSpawnData {
      final int index;
      final boolean isLeft;
      final int verticalIndex;

      PendingSpawnData(int index, boolean isLeft, int verticalIndex) {
         this.index = index;
         this.isLeft = isLeft;
         this.verticalIndex = verticalIndex;
      }
   }

   public enum State {
      STANDBY,
      LAUNCHING,
      FINISHED;
   }
}
