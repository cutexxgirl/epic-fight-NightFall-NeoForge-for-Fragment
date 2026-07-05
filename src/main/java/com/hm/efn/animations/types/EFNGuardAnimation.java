package com.hm.efn.animations.types;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public class EFNGuardAnimation extends ActionAnimation {
   public EFNGuardAnimation(float transitionTime, AnimationAccessor<? extends EFNGuardAnimation> accessor, AssetAccessor<? extends Armature> armature) {
      this(transitionTime, Float.MAX_VALUE, accessor, armature);
   }

   public EFNGuardAnimation(
      float transitionTime, float lockTime, AnimationAccessor<? extends EFNGuardAnimation> accessor, AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, lockTime, accessor, armature);
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, lockTime)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, true)
         .addState(EntityState.INACTION, true);
      this.addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true);
   }

   public EFNGuardAnimation(float transitionTime, float lockTime, String path, AssetAccessor<? extends Armature> armature) {
      super(transitionTime, lockTime, path, armature);
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, lockTime)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, true)
         .addState(EntityState.INACTION, true);
      this.addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true);
   }
}
