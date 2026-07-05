package com.hm.efn.entity.effect;

import com.hm.efn.EFN;
import com.hm.efn.EFNCommonConfig;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.GeoVFXEntity;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordPatch;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import com.hm.efn.compat.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class HeavyRainSwordEntity extends Entity {
   private int standbyTicks = 10;
   private int launchInterval = 1;
   private int spawnsPerTick = 4;
   private double[][] ringsConfig = null;
   private static final double INITIAL_FALL_SPEED = -1.5;
   private static final double GRAVITY_ACCELERATION = 1.5;
   private static final double MAX_FALL_SPEED = -10.0;
   private UUID ownerUUID;
   private final List<UUID> childSwords = new ArrayList<>();
   private final Queue<UUID> launchQueue = new LinkedList<>();
   private final List<UUID> launchedSwords = new ArrayList<>();
   private final Map<UUID, HeavyRainSwordEntity.StuckSwordData> stuckSwords = new HashMap<>();
   private final Map<UUID, Vec3> swordOffsets = new HashMap<>();
   private final Queue<HeavyRainSwordEntity.PendingSpawnData> spawnQueue = new LinkedList<>();
   private int launchTickCounter = 0;
   private HeavyRainSwordEntity.State currentState = HeavyRainSwordEntity.State.STANDBY;

   public HeavyRainSwordEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noCulling = true;
      this.noPhysics = true;
   }

   public HeavyRainSwordEntity(Level level, LivingEntity owner, Vec3 targetPos) {
      super((EntityType)EFNEntity.HEAVY_RAIN_SUMMONED_SWORD.get(), level);
      this.setOwner(owner);
      this.noPhysics = true;
      this.noCulling = true;
      this.setPos(targetPos.x, targetPos.y + 7.0, targetPos.z);
   }

   public static void summon(Level level, LivingEntity owner, @Nullable LivingEntity target) {
      summonCustom(level, owner, target, 10, 1, 4, null);
   }

   public static void summon(Level level, LivingEntity owner) {
      summonCustom(level, owner, 10, 1, 4, null);
   }

   public static void summonCustom(
      Level level, LivingEntity owner, @Nullable LivingEntity target, int standbyTicks, int launchInterval, int spawnsPerTick, @Nullable double[][] customRings
   ) {
      if (!level.isClientSide) {
         if (target != null && target.isAlive()) {
            spawnController(level, owner, target.position(), standbyTicks, launchInterval, spawnsPerTick, customRings);
         } else {
            summonCustom(level, owner, standbyTicks, launchInterval, spawnsPerTick, customRings);
         }
      }
   }

   public static void summonCustom(Level level, LivingEntity owner, int standbyTicks, int launchInterval, int spawnsPerTick, @Nullable double[][] customRings) {
      if (!level.isClientSide) {
         Vec3 look = owner.getLookAngle();
         Vec3 horizontalLook = new Vec3(look.x, 0.0, look.z).normalize();
         if (horizontalLook.lengthSqr() == 0.0) {
            horizontalLook = new Vec3(0.0, 0.0, 1.0);
         }

         Vec3 targetPos = owner.position().add(horizontalLook.scale(2.0));
         spawnController(level, owner, targetPos, standbyTicks, launchInterval, spawnsPerTick, customRings);
      }
   }

   private static void spawnController(
      Level level, LivingEntity owner, Vec3 targetPos, int standbyTicks, int launchInterval, int spawnsPerTick, @Nullable double[][] rings
   ) {
      HeavyRainSwordEntity controller = new HeavyRainSwordEntity(level, owner, targetPos);
      controller.setConfig(standbyTicks, launchInterval, spawnsPerTick, rings);
      level.addFreshEntity(controller);
      controller.spawnChildSwords();
   }

   public void setConfig(int standbyTicks, int launchInterval, int spawnsPerTick, @Nullable double[][] customRings) {
      this.standbyTicks = standbyTicks;
      this.launchInterval = launchInterval;
      this.spawnsPerTick = spawnsPerTick;
      this.ringsConfig = customRings;
   }

   public void setOwner(LivingEntity owner) {
      this.ownerUUID = owner.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      return this.ownerUUID != null && this.level() instanceof ServerLevel ? (LivingEntity)((ServerLevel)this.level()).getEntity(this.ownerUUID) : null;
   }

   private void forceLockRotationDownwards(SinSummonedSwordEntity sword) {
      sword.setXRot(90.0F);
      sword.setYRot(0.0F);
      sword.xRotO = 90.0F;
      sword.yRotO = 0.0F;
      sword.setSyncXRot(90.0F);
      sword.yHeadRot = 0.0F;
      sword.yHeadRotO = 0.0F;
      sword.yBodyRot = 0.0F;
      sword.yBodyRotO = 0.0F;
   }

   private double[][] getRings() {
      if (this.ringsConfig != null) {
         return this.ringsConfig;
      }

      EFNCommonConfig.HeavyRainScaleMode mode = (EFNCommonConfig.HeavyRainScaleMode)EFNCommonConfig.HEAVY_RAIN_SCALE_MODE.get();

      return switch (mode) {
         case LOW -> new double[][]{{1.0, 0.0}, {2.0, 0.8}, {4.0, 1.8}};
         case MEDIUM -> new double[][]{{1.0, 0.0}, {3.0, 0.8}, {4.0, 1.6}, {6.0, 2.5}};
         case CUSTOM -> this.parseCustomRings((List<? extends String>)EFNCommonConfig.HEAVY_RAIN_CUSTOM_ARRAY.get());
         default -> new double[][]{{2.0, 0.0}, {3.0, 0.7}, {8.0, 1.5}, {8.0, 3.5}, {7.0, 4.5}};
      };
   }

   private double[][] parseCustomRings(List<? extends String> configs) {
      List<double[]> result = new ArrayList<>();

      for (String config : configs) {
         try {
            String[] parts = config.split(",");
            if (parts.length == 2) {
               double count = Double.parseDouble(parts[0].trim());
               double radius = Double.parseDouble(parts[1].trim());
               if (count > 0.0) {
                  result.add(new double[]{count, radius});
               }
            }
         } catch (Exception var10) {
         }
      }

      return result.isEmpty() ? new double[][]{{2.0, 0.0}, {3.0, 0.7}, {8.0, 1.5}, {8.0, 3.5}, {7.0, 4.5}} : result.toArray(new double[0][]);
   }

   private void spawnChildSwords() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         this.discard();
      } else if (this.level() instanceof ServerLevel serverLevel) {
         double[][] var30 = this.getRings();
         int totalSwordCount = 0;

         for (double[] ring : var30) {
            totalSwordCount += (int)ring[0];
         }

         AABB searchBox = new AABB(
            this.getX() - 4.5, this.getY() - 10.0, this.getZ() - 4.5, this.getX() + 4.5, this.getY() + 2.0, this.getZ() + 4.5
         );
         List<LivingEntity> targets = serverLevel.getEntitiesOfClass(
            LivingEntity.class,
            searchBox,
            livingEntity -> livingEntity.isAlive()
               && livingEntity != owner
               && !(livingEntity instanceof SinSummonedSwordEntity)
               && !(livingEntity instanceof DoppelgangerEntity)
               && !(livingEntity instanceof VFXEntity)
               && !(livingEntity instanceof GeoVFXEntity)
         );
         int currentRing = 0;
         int swordsInCurrentRing = 0;
         double currentRingBaseAngle = this.random.nextDouble() * 2.0 * Math.PI;

         for (int i = 0; i < totalSwordCount; i++) {
            Vec3 offset;
            if (i < targets.size()) {
               LivingEntity target = targets.get(i);
               double offsetX = target.getX() - this.getX();
               double offsetZ = target.getZ() - this.getZ();
               double offsetY = (this.random.nextDouble() - 0.5) * 1.5;
               offset = new Vec3(offsetX, offsetY, offsetZ);
            } else {
               if (swordsInCurrentRing >= var30[currentRing][0]) {
                  if (++currentRing >= var30.length) {
                     break;
                  }

                  swordsInCurrentRing = 0;
                  currentRingBaseAngle = this.random.nextDouble() * 2.0 * Math.PI;
               }

               int countInRing = (int)var30[currentRing][0];
               double baseRadius = var30[currentRing][1];
               double angleStep = (Math.PI * 2) / countInRing;
               double theta = currentRingBaseAngle + swordsInCurrentRing * angleStep;
               double jitter = baseRadius > 0.0 ? this.random.nextDouble() * 0.2 : 0.0;
               double finalRadius = baseRadius + jitter;
               double offsetX = finalRadius * Math.cos(theta);
               double offsetZ = finalRadius * Math.sin(theta);
               double offsetY = (this.random.nextDouble() - 0.5) * 1.5;
               offset = new Vec3(offsetX, offsetY, offsetZ);
               swordsInCurrentRing++;
            }

            this.spawnQueue.add(new HeavyRainSwordEntity.PendingSpawnData(offset));
         }
      }
   }

   private void processSpawnQueue() {
      if (!this.spawnQueue.isEmpty()) {
         LivingEntity owner = this.getOwner();
         if (owner != null && this.level() instanceof ServerLevel serverLevel) {
            for (int var7 = this.spawnsPerTick; var7 > 0 && !this.spawnQueue.isEmpty(); var7--) {
               HeavyRainSwordEntity.PendingSpawnData data = this.spawnQueue.poll();
               SinSummonedSwordEntity sword = new SinSummonedSwordEntity(
                  (EntityType<? extends Mob>)EFNEntity.SIN_SUMMONED_SWORD.get(), owner, 1.45F, this.level(), true
               );
               this.swordOffsets.put(sword.getUUID(), data.offset);
               Vec3 spawnPos = this.position().add(data.offset);
               sword.setPos(spawnPos);
               this.forceLockRotationDownwards(sword);
               sword.setDeltaMovement(Vec3.ZERO);
               sword.setHeavyRain(true);
               sword.setNoAim(true);
               if ((Boolean)EFNCommonConfig.HEAVY_RAIN_ENABLE_PARTICLES_SPAWN.get()) {
                  serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, spawnPos.x, spawnPos.y, spawnPos.z, 2, 0.1, 0.1, 0.1, 0.02);
               }

               this.level().addFreshEntity(sword);
               this.childSwords.add(sword.getUUID());
            }
         }
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         LivingEntity owner = this.getOwner();
         if (owner != null && !owner.isDeadOrDying()) {
            switch (this.currentState) {
               case STANDBY:
                  this.tickStandby();
                  break;
               case LAUNCHING:
                  this.tickLaunching();
                  break;
               case FINISHED:
                  this.tickFinished();
            }
         } else {
            this.cleanup();
         }
      }
   }

   private void tickStandby() {
      this.processSpawnQueue();
      this.updateStandbySwordsPosition();
      if (this.spawnQueue.isEmpty() && this.tickCount >= this.standbyTicks) {
         this.prepareToLaunch();
      }
   }

   private void tickLaunching() {
      this.updateStandbySwordsPosition();
      this.checkLaunchedSwordsCollision();
      this.tickStuckSwords();
      this.launchTickCounter++;
      if (this.launchTickCounter >= this.launchInterval) {
         this.launchTickCounter = 0;
         this.launchNextSword();
      }

      if (this.launchQueue.isEmpty() && this.childSwords.isEmpty()) {
         this.currentState = HeavyRainSwordEntity.State.FINISHED;
      }
   }

   private void tickFinished() {
      this.checkLaunchedSwordsCollision();
      this.tickStuckSwords();
      if (this.launchedSwords.isEmpty() && this.stuckSwords.isEmpty()) {
         this.discard();
      }
   }

   private void tickStuckSwords() {
      ServerLevel serverLevel = (ServerLevel)this.level();
      List<UUID> toRemove = new ArrayList<>();

      for (Entry<UUID, HeavyRainSwordEntity.StuckSwordData> entry : this.stuckSwords.entrySet()) {
         UUID uuid = entry.getKey();
         HeavyRainSwordEntity.StuckSwordData data = entry.getValue();
         data.ticks++;
         Entity entity = serverLevel.getEntity(uuid);
         if (entity != null) {
            entity.tickCount = 0;
         }

         boolean entityVanished = entity == null || !entity.isAlive();
         boolean timerFinished = data.ticks >= data.maxTicks;
         if (entityVanished || timerFinished) {
            LivingEntity owner = this.getOwner();
            if (owner != null) {
               DamageSource damageSource = EpicFightDamageSources.mobAttack(owner)
                  .setAnimation(null)
                  .setInitialPosition(data.pos)
                  .setStunType(StunType.NONE)
                  .setBaseImpact(2.0F)
                  .addRuntimeTag(SinSummonedSwordPatch.HEAVY_RAIN_SWORD_DAMAGE)
                  .addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
               AABB damageBox = new AABB(
                  data.pos.x - 0.8,
                  data.pos.y - 0.2,
                  data.pos.z - 0.8,
                  data.pos.x + 0.8,
                  data.pos.y + 1.6,
                  data.pos.z + 0.8
               );
               List<LivingEntity> targets = serverLevel.getEntitiesOfClass(
                  LivingEntity.class,
                  damageBox,
                  livingEntity -> livingEntity.isAlive()
                     && livingEntity != owner
                     && (livingEntity instanceof Monster || livingEntity instanceof Mob mob && mob.getTarget() == owner)
               );
               float explosionDamage = 5.0F;

               for (LivingEntity target : targets) {
                  target.hurt(damageSource, explosionDamage);
               }
            }

            float pitch = 1.4F + this.random.nextFloat() * 0.3F;
            serverLevel.playSound(null, data.pos.x, data.pos.y, data.pos.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.5F, pitch);
            if ((Boolean)EFNCommonConfig.HEAVY_RAIN_ENABLE_PARTICLES_DISAPPEAR.get()) {
               serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, data.pos.x, data.pos.y + 0.5, data.pos.z, 8, 0.2, 0.4, 0.2, 0.05);
            }

            if (entity != null && entity.isAlive()) {
               entity.discard();
            }

            toRemove.add(uuid);
         }
      }

      for (UUID uuid : toRemove) {
         this.stuckSwords.remove(uuid);
      }
   }

   private void prepareToLaunch() {
      List<UUID> sortedSwords = new ArrayList<>(this.childSwords);
      sortedSwords.sort((uuid1, uuid2) -> {
         Vec3 offset1 = this.swordOffsets.getOrDefault(uuid1, Vec3.ZERO);
         Vec3 offset2 = this.swordOffsets.getOrDefault(uuid2, Vec3.ZERO);
         double distSq1 = offset1.x * offset1.x + offset1.z * offset1.z;
         double distSq2 = offset2.x * offset2.x + offset2.z * offset2.z;
         return Double.compare(distSq1, distSq2);
      });
      this.launchQueue.addAll(sortedSwords);
      this.currentState = HeavyRainSwordEntity.State.LAUNCHING;
   }

   private void launchNextSword() {
      if (!this.launchQueue.isEmpty()) {
         UUID swordUUID = this.launchQueue.poll();
         if (swordUUID != null) {
            if (((ServerLevel)this.level()).getEntity(swordUUID) instanceof SinSummonedSwordEntity sword) {
               this.childSwords.remove(swordUUID);
               this.launchedSwords.add(swordUUID);
               sword.launch(null);
               this.forceLockRotationDownwards(sword);
               Vec3 initialVelocity = new Vec3(0.0, -1.5, 0.0);
               sword.setDeltaMovement(initialVelocity);
               sword.setLockedTrajectory(initialVelocity);
               sword.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 1.0F);
            }
         }
      }
   }

   private void updateStandbySwordsPosition() {
      ServerLevel serverLevel = (ServerLevel)this.level();
      List<UUID> toRemove = new ArrayList<>();

      for (UUID swordUUID : this.childSwords) {
         Entity entity = serverLevel.getEntity(swordUUID);
         if (entity instanceof SinSummonedSwordEntity sword && sword.isInStandby()) {
            Vec3 offset = this.swordOffsets.get(swordUUID);
            if (offset != null) {
               sword.setPos(this.position().add(offset));
               this.forceLockRotationDownwards(sword);
               sword.setDeltaMovement(Vec3.ZERO);
            }
         } else if (entity == null || !entity.isAlive()) {
            toRemove.add(swordUUID);
         }
      }

      this.childSwords.removeAll(toRemove);
      this.launchQueue.removeAll(toRemove);
   }

   private void checkLaunchedSwordsCollision() {
      ServerLevel serverLevel = (ServerLevel)this.level();
      List<UUID> toRemove = new ArrayList<>();

      for (UUID uuid : this.launchedSwords) {
         if (serverLevel.getEntity(uuid) instanceof SinSummonedSwordEntity sword) {
            if (sword.isRemoved()) {
               toRemove.add(uuid);
            } else {
               this.forceLockRotationDownwards(sword);
               Vec3 currentPos = sword.position();
               Vec3 currentVelocity = sword.getDeltaMovement();
               double newYVelocity = Math.max(-10.0, currentVelocity.y - 1.5);
               Vec3 newVelocity = new Vec3(0.0, newYVelocity, 0.0);
               sword.setDeltaMovement(newVelocity);
               sword.setLockedTrajectory(newVelocity);
               Vec3 nextPos = currentPos.add(newVelocity);
               BlockHitResult hit = serverLevel.clip(new ClipContext(currentPos, nextPos, Block.COLLIDER, Fluid.NONE, sword));
               if (hit.getType() == Type.BLOCK) {
                  BlockPos hitBlockPos = hit.getBlockPos();
                  BlockState blockState = serverLevel.getBlockState(hitBlockPos);
                  SoundType soundType = blockState.getSoundType(serverLevel, hitBlockPos, sword);
                  Vec3 hitPos = hit.getLocation();
                  sword.setPos(hitPos.x, hitPos.y + 0.4, hitPos.z);
                  sword.setDeltaMovement(Vec3.ZERO);
                  sword.setLockedTrajectory(Vec3.ZERO);
                  this.forceLockRotationDownwards(sword);
                  sword.tickCount = 0;
                  serverLevel.playSound(
                     null, hitPos.x, hitPos.y, hitPos.z, soundType.getBreakSound(), SoundSource.PLAYERS, 1.2F, soundType.getPitch() * 0.8F
                  );
                  if ((Boolean)EFNCommonConfig.HEAVY_RAIN_ENABLE_PARTICLES_GROUND.get()) {
                     serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, hitPos.x, hitPos.y + 0.5, hitPos.z, 5, 0.2, 0.2, 0.2, 0.05);
                  }

                  if ((Boolean)EFNCommonConfig.HEAVY_RAIN_ENABLE_FRACTURE.get()) {
                     LevelUtil.circleSlamFracture(
                        this.getOwner(), this.level(), new Vec3(hitPos.x, hitPos.y - 0.2, hitPos.z), 1.0, true, true, false
                     );
                  }

                  EFN.queueServerWork(3, () -> sword.setStuckInBlock(true));
                  toRemove.add(uuid);
                  int stayTicks = 50 + this.random.nextInt(7);
                  this.stuckSwords
                     .put(uuid, new HeavyRainSwordEntity.StuckSwordData(new Vec3(hitPos.x, hitPos.y + 0.4, hitPos.z), stayTicks));
               }
            }
         } else {
            toRemove.add(uuid);
         }
      }

      this.launchedSwords.removeAll(toRemove);
   }

   private void cleanup() {
      if (this.level() instanceof ServerLevel serverLevel) {
         List<UUID> allSwords = new ArrayList<>();
         allSwords.addAll(this.childSwords);
         allSwords.addAll(this.launchQueue);
         allSwords.addAll(this.launchedSwords);
         allSwords.addAll(this.stuckSwords.keySet());

         for (UUID swordUUID : allSwords) {
            Entity sword = serverLevel.getEntity(swordUUID);
            if (sword != null) {
               sword.discard();
            }
         }
      }

      this.discard();
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

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }

      if (pCompound.contains("State")) {
         this.currentState = HeavyRainSwordEntity.State.valueOf(pCompound.getString("State"));
      }

      this.standbyTicks = pCompound.getInt("StandbyTicks");
      this.launchInterval = pCompound.getInt("LaunchInterval");
      this.spawnsPerTick = pCompound.getInt("SpawnsPerTick");
      this.launchTickCounter = pCompound.getInt("LaunchTickCounter");
      if (pCompound.contains("RingsConfig")) {
         ListTag ringsList = pCompound.getList("RingsConfig", 10);
         this.ringsConfig = new double[ringsList.size()][2];

         for (int i = 0; i < ringsList.size(); i++) {
            CompoundTag ringTag = ringsList.getCompound(i);
            this.ringsConfig[i][0] = ringTag.getDouble("Count");
            this.ringsConfig[i][1] = ringTag.getDouble("Radius");
         }
      }

      this.childSwords.clear();
      pCompound.getList("ChildSwords", 8).forEach(tag -> this.childSwords.add(UUID.fromString(tag.getAsString())));
      this.launchQueue.clear();
      pCompound.getList("LaunchQueue", 8).forEach(tag -> this.launchQueue.add(UUID.fromString(tag.getAsString())));
      this.launchedSwords.clear();
      pCompound.getList("LaunchedSwords", 8).forEach(tag -> this.launchedSwords.add(UUID.fromString(tag.getAsString())));
      this.swordOffsets.clear();
      pCompound.getList("SwordOffsets", 10).forEach(tag -> {
         CompoundTag entry = (CompoundTag)tag;
         this.swordOffsets.put(entry.getUUID("UUID"), new Vec3(entry.getDouble("X"), entry.getDouble("Y"), entry.getDouble("Z")));
      });
      this.stuckSwords.clear();
      pCompound.getList("StuckSwords", 10)
         .forEach(
            tag -> {
               CompoundTag entry = (CompoundTag)tag;
               this.stuckSwords
                  .put(
                     entry.getUUID("UUID"),
                     new HeavyRainSwordEntity.StuckSwordData(
                        new Vec3(entry.getDouble("X"), entry.getDouble("Y"), entry.getDouble("Z")), entry.getInt("MaxTicks")
                     )
                  );
               this.stuckSwords.get(entry.getUUID("UUID")).ticks = entry.getInt("Ticks");
            }
         );
      this.spawnQueue.clear();
      pCompound.getList("SpawnQueue", 10).forEach(tag -> {
         CompoundTag entry = (CompoundTag)tag;
         this.spawnQueue.add(new HeavyRainSwordEntity.PendingSpawnData(new Vec3(entry.getDouble("X"), entry.getDouble("Y"), entry.getDouble("Z"))));
      });
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }

      pCompound.putString("State", this.currentState.name());
      pCompound.putInt("StandbyTicks", this.standbyTicks);
      pCompound.putInt("LaunchInterval", this.launchInterval);
      pCompound.putInt("SpawnsPerTick", this.spawnsPerTick);
      pCompound.putInt("LaunchTickCounter", this.launchTickCounter);
      if (this.ringsConfig != null) {
         ListTag ringsList = new ListTag();

         for (double[] ring : this.ringsConfig) {
            CompoundTag ringTag = new CompoundTag();
            ringTag.putDouble("Count", ring[0]);
            ringTag.putDouble("Radius", ring[1]);
            ringsList.add(ringTag);
         }

         pCompound.put("RingsConfig", ringsList);
      }

      ListTag childList = new ListTag();
      this.childSwords.forEach(uuid -> childList.add(StringTag.valueOf(uuid.toString())));
      pCompound.put("ChildSwords", childList);
      ListTag queueList = new ListTag();
      this.launchQueue.forEach(uuid -> queueList.add(StringTag.valueOf(uuid.toString())));
      pCompound.put("LaunchQueue", queueList);
      ListTag launchedList = new ListTag();
      this.launchedSwords.forEach(uuid -> launchedList.add(StringTag.valueOf(uuid.toString())));
      pCompound.put("LaunchedSwords", launchedList);
      ListTag offsetList = new ListTag();
      this.swordOffsets.forEach((uuid, vec) -> {
         CompoundTag entry = new CompoundTag();
         entry.putUUID("UUID", uuid);
         entry.putDouble("X", vec.x);
         entry.putDouble("Y", vec.y);
         entry.putDouble("Z", vec.z);
         offsetList.add(entry);
      });
      pCompound.put("SwordOffsets", offsetList);
      ListTag stuckList = new ListTag();
      this.stuckSwords.forEach((uuid, data) -> {
         CompoundTag entry = new CompoundTag();
         entry.putUUID("UUID", uuid);
         entry.putInt("Ticks", data.ticks);
         entry.putInt("MaxTicks", data.maxTicks);
         entry.putDouble("X", data.pos.x);
         entry.putDouble("Y", data.pos.y);
         entry.putDouble("Z", data.pos.z);
         stuckList.add(entry);
      });
      pCompound.put("StuckSwords", stuckList);
      ListTag spawnList = new ListTag();
      this.spawnQueue.forEach(data -> {
         CompoundTag entry = new CompoundTag();
         entry.putDouble("X", data.offset.x);
         entry.putDouble("Y", data.offset.y);
         entry.putDouble("Z", data.offset.z);
         spawnList.add(entry);
      });
      pCompound.put("SpawnQueue", spawnList);
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   private static class PendingSpawnData {
      final Vec3 offset;

      PendingSpawnData(Vec3 offset) {
         this.offset = offset;
      }
   }

   private enum State {
      STANDBY,
      LAUNCHING,
      FINISHED;
   }

   private static class StuckSwordData {
      int ticks;
      int maxTicks;
      Vec3 pos;

      StuckSwordData(Vec3 pos, int maxTicks) {
         this.pos = pos;
         this.ticks = 0;
         this.maxTicks = maxTicks;
      }
   }
}
