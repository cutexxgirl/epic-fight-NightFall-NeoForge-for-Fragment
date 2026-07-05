package com.hm.efn.entity.geoEntity;

import com.hm.efn.animations.types.murasama.MurasamaAttackAnimation;
import com.hm.efn.entity.GeoVFXEntity;
import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MurasamaCharging extends GeoVFXEntity {
   private static final int MAX_LIFETIME = 28;
   private Vec3 offset = Vec3.ZERO;
   private static final MurasamaAttackAnimation[] CHARGING_ANIMATIONS = new MurasamaAttackAnimation[]{
      (MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_Y_CHARGE.get(),
      (MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_Y_CHARGE_THROUGH.get(),
      (MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_Y_CHARGE_AIR.get(),
      (MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XY_CHARGE.get(),
      (MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XXY_CHARGE.get(),
      (MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XXXY_CHARGE.get()
   };

   public static MurasamaCharging createEntity(EntityType<? extends GeoVFXEntity> type, Level level) {
      return new MurasamaCharging(type, level);
   }

   public MurasamaCharging(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   public MurasamaCharging(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z) {
      super(type, level, x, y, z);
   }

   public MurasamaCharging(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot) {
      super(type, level, x, y, z, yRot);
   }

   public MurasamaCharging(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot, LivingEntity owner) {
      super(type, level, x, y, z, yRot, owner);
      if (owner != null) {
         this.offset = this.position().subtract(owner.position());
      }
   }

   @Override
   protected void initializeDefaults() {
      super.initializeDefaults();
      this.setAnimationSpeed(2.0F);
   }

   @Override
   protected void performAttack() {
   }

   @Override
   protected boolean shouldDiscard() {
      return this.getLifetime() >= 28 || this.ownerStunned() || !this.isPlayingChargingAnim();
   }

   private boolean ownerStunned() {
      LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this.getOwner(), LivingEntityPatch.class);
      return livingEntityPatch != null && livingEntityPatch.isStunned();
   }

   private boolean isPlayingChargingAnim() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         return false;
      }

      LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
      if (livingEntityPatch != null) {
         for (MurasamaAttackAnimation animation : CHARGING_ANIMATIONS) {
            AnimationPlayer animPlayer = livingEntityPatch.getAnimator().getPlayerFor(null);
            if (animPlayer != null && ((DynamicAnimation)animPlayer.getAnimation().get()).isLinkAnimation()) {
               return true;
            }

            if (animPlayer != null && animPlayer.getAnimation().get() == animation) {
               return true;
            }
         }
      }

      return false;
   }
}
