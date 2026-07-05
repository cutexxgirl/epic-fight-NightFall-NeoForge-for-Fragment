package com.hm.efn.entity.effect;

import com.hm.efn.entity.EFNEntity;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNWeaponCategories;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.CullableUtil;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class SummonedSwordEntity_In extends VFXEntity {
   private static final EntityDataAccessor<Optional<UUID>> DATA_TARGET_UUID = SynchedEntityData.defineId(
      SummonedSwordEntity_In.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(SummonedSwordEntity_In.class, EntityDataSerializers.INT);
   private LivingEntity initialTarget;
   private Vec3 offset = Vec3.ZERO;
   private boolean effectsApplied = false;

   public SummonedSwordEntity_In(LivingEntity owner, LivingEntity target, float scale, Vec3 offset) {
      super((EntityType)EFNEntity.SUMMONED_SWORD_IN.get(), owner, scale);
      this.initialTarget = target;
      this.noCulling = true;
      this.setInvulnerable(true);
      this.offset = offset != null ? offset : Vec3.ZERO;
      this.noPhysics = true;
      this.setNoGravity(true);
      if (target != null) {
         this.setTargetUUID(target.getUUID());
         this.setTargetID(target.getId());
      }
   }

   public SummonedSwordEntity_In(EntityType<? extends VFXEntity> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.noCulling = true;
      this.setInvulnerable(true);
      this.setNoGravity(true);
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_TARGET_UUID, Optional.empty());
      builder.define(DATA_TARGET_ID, 0);
   }

   @Nullable
   public LivingEntity getInitialTarget() {
      if (this.initialTarget != null && this.initialTarget.isAlive()) {
         return this.initialTarget;
      }

      UUID targetUUID = this.getTargetUUID();
      if (targetUUID != null) {
         Entity entity = this.level().getPlayerByUUID(targetUUID);
         if (entity instanceof LivingEntity livingEntity) {
            this.initialTarget = livingEntity;
            return livingEntity;
         }

         int targetId = this.getTargetID();
         if (targetId != 0 && this.level().getEntity(targetId) instanceof LivingEntity livingEntity) {
            this.initialTarget = livingEntity;
            return livingEntity;
         }
      }

      return null;
   }

   public void setInitialTarget(@Nullable LivingEntity target) {
      this.initialTarget = target;
      if (target != null) {
         this.setTargetUUID(target.getUUID());
         this.setTargetID(target.getId());
      } else {
         this.setTargetUUID(null);
         this.setTargetID(0);
      }
   }

   @Nullable
   public UUID getTargetUUID() {
      return (UUID)((Optional)this.entityData.get(DATA_TARGET_UUID)).orElse(null);
   }

   public void setTargetUUID(@Nullable UUID uuid) {
      this.entityData.set(DATA_TARGET_UUID, Optional.ofNullable(uuid));
   }

   public int getTargetID() {
      return (Integer)this.entityData.get(DATA_TARGET_ID);
   }

   public void setTargetID(int id) {
      this.entityData.set(DATA_TARGET_ID, id);
   }

   @Nullable
   public LivingEntity getOwnerEntity() {
      return this.getOwner();
   }

   public static boolean isHoldingYamato(Player player) {
      return player == null
         ? false
         : Stream.of(player.getMainHandItem(), player.getOffhandItem())
            .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
            .filter(Objects::nonNull)
            .anyMatch(cap -> cap.getWeaponCategory() == EFNWeaponCategories.EFN_YAMATO);
   }

   public static void summonAtTargetWaist(ServerPlayerPatch ownerPatch, LivingEntity target, Vec3 offset, float scale) {
      if (target != null && target.isAlive()) {
         LivingEntity owner = (LivingEntity)ownerPatch.getOriginal();
         ServerLevel level = (ServerLevel)owner.level();
         Vec3 waistPos = target.position().add(0.0, target.getBbHeight() * 0.7, 0.0);
         Vec3 spawnPos = waistPos.add(offset);
         SummonedSwordEntity_In sword = new SummonedSwordEntity_In(owner, target, scale, offset);
         sword.setPos(spawnPos);
         sword.setYRot(target.getYRot());
         sword.setXRot(0.0F);
         level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
         level.addFreshEntity(sword);
      }
   }

   public void tick() {
      super.tick();
      CullableUtil.setAlwaysVisible(this);
      if (!this.level().isClientSide) {
         if (this.initialTarget == null || !this.initialTarget.isAlive()) {
            this.initialTarget = this.getInitialTarget();
         }

         LivingEntity owner = this.getOwnerEntity();
         if (!this.effectsApplied && owner != null && owner.isAlive()) {
            this.applyEffects();
            this.effectsApplied = true;
         }

         if (owner instanceof Player player && !isHoldingYamato(player)) {
            this.removeEffects();
            this.discard();
         } else if (owner != null && owner.isAlive()) {
            this.moveToInitialTarget();
         } else {
            this.discard();
            this.removeEffects();
         }
      }
   }

   private void applyEffects() {
      LivingEntity owner = this.getOwnerEntity();
      if (owner != null && !owner.level().isClientSide()) {
         MobEffectInstance yamato = new MobEffectInstance(EFNMobEffectRegistry.YAMATO, 300, 1, false, false, false);
         owner.addEffect(yamato);
      }
   }

   private void removeEffects() {
      LivingEntity owner = this.getOwnerEntity();
      if (owner != null && !owner.level().isClientSide() && this.effectsApplied) {
         owner.removeEffect(EFNMobEffectRegistry.YAMATO);
         this.effectsApplied = false;
      }
   }

   protected void moveToInitialTarget() {
      LivingEntity target = this.getInitialTarget();
      if (target == null || !target.isAlive()) {
         LivingEntity owner = this.getOwnerEntity();
         if (owner != null) {
            ServerPlayerPatch ownerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(owner, ServerPlayerPatch.class);
            if (ownerPatch != null) {
               target = ownerPatch.getTarget();
            }
         }

         if (target == null || !target.isAlive()) {
            return;
         }
      }

      this.setYRot(target.yBodyRot);
      this.setYBodyRot(target.yBodyRot);
      this.setYHeadRot(target.yBodyRot);
      this.setPos(target.position().add(0.0, target.getBbHeight() * 0.6, 0.0));
   }

   @Deprecated
   protected void moveToOwnerTarget() {
      this.moveToInitialTarget();
   }

   public void addAdditionalSaveData(@NotNull CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getTargetUUID() != null) {
         compound.putUUID("TargetUUID", this.getTargetUUID());
      }

      compound.putInt("TargetID", this.getTargetID());
      if (this.offset != null) {
         compound.putDouble("OffsetX", this.offset.x);
         compound.putDouble("OffsetY", this.offset.y);
         compound.putDouble("OffsetZ", this.offset.z);
      } else {
         compound.putDouble("OffsetX", 0.0);
         compound.putDouble("OffsetY", 0.0);
         compound.putDouble("OffsetZ", 0.0);
      }
   }

   public void readAdditionalSaveData(@NotNull CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.hasUUID("TargetUUID")) {
         this.setTargetUUID(compound.getUUID("TargetUUID"));
      }

      if (compound.contains("TargetID")) {
         this.setTargetID(compound.getInt("TargetID"));
      }

      if (compound.contains("OffsetX") && compound.contains("OffsetY") && compound.contains("OffsetZ")) {
         this.offset = new Vec3(compound.getDouble("OffsetX"), compound.getDouble("OffsetY"), compound.getDouble("OffsetZ"));
      }
   }

   @NotNull
   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(100.0);
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return EFNAnimations.SUMMONED_SWORD_CIRCLE;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      return EFNAnimations.SUMMONED_SWORD_IN;
   }

   @Nullable
   public Armature getArmature() {
      return ArmatureAccessor.create("efn", "entity/effect/summoned_sword_circle", Armature::new).get();
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return MeshAccessor.create("efn", "entity/effect/summoned_sword_circle", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }

   @Nullable
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/summoned_sword.png");
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/summoned_sword.png");
   }
}
