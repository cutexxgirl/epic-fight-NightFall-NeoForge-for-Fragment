package com.hm.efn.animations.types.stun;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public class EFNStunAnimation extends ActionAnimation {
   public EFNStunAnimation(float transitionTime, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature) {
      super(transitionTime, accessor, armature);
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, 0.25F)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addState(EntityState.HURT_LEVEL, 1)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.INACTION, true);
      this.addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true);
   }

   public EFNStunAnimation(
      float transitionTime, float stunTime, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, stunTime, accessor, armature);
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, stunTime)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addState(EntityState.HURT_LEVEL, 1)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.INACTION, true);
      this.addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true);
   }

   public EFNStunAnimation(float transitionTime, float stunTime, String path, AssetAccessor<? extends Armature> armature) {
      super(transitionTime, stunTime, path, armature);
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, stunTime)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addState(EntityState.HURT_LEVEL, 1)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.INACTION, true);
      this.addProperty(StaticAnimationProperty.FIXED_HEAD_ROTATION, true);
   }
}
