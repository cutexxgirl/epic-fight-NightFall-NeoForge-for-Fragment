package com.hm.efn.animations.types.yamato;

import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import com.merlin204.avalon.epicfight.animations.AutoDiscardAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SummonedSwordAnimation extends AutoDiscardAttackAnimation {
   public SummonedSwordAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature, play_speed, damageMulti);
      this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD_WITH_X_ROT);
      this.addProperty(ActionAnimationProperty.COORD_SET_TICK, null);
      this.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}));
   }

   public SummonedSwordAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
      this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD_WITH_X_ROT);
      this.addProperty(ActionAnimationProperty.COORD_SET_TICK, null);
      this.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}));
   }

   public SummonedSwordAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
      this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD_WITH_X_ROT);
      this.addProperty(ActionAnimationProperty.COORD_SET_TICK, null);
      this.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}));
   }

   public SummonedSwordAnimation(
      float transitionTime, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
      this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD_WITH_X_ROT);
      this.addProperty(ActionAnimationProperty.COORD_SET_TICK, null);
      this.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}));
   }

   public SummonedSwordAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
      this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD_WITH_X_ROT);
      this.addProperty(ActionAnimationProperty.COORD_SET_TICK, null);
      this.addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, Float.MAX_VALUE}));
   }

   protected void bindPhaseState(Phase phase) {
      float preDelay = phase.preDelay;
      this.stateSpectrumBlueprint
         .newTimePair(0.0F, preDelay)
         .addState(EntityState.PHASE_LEVEL, 1)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .newTimePair(phase.start, phase.recovery + 0.1F)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .newTimePair(phase.start, phase.end)
         .addState(EntityState.INACTION, true)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addState(EntityState.TURNING_LOCKED, false)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addState(EntityState.ATTACKING, true)
         .addState(EntityState.PHASE_LEVEL, 2)
         .newTimePair(phase.contact, phase.end)
         .addState(EntityState.PHASE_LEVEL, 3);
   }

   protected void move(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
   }

   protected boolean validateMovement(LivingEntityPatch<?> livingEntityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
      return true;
   }

   protected void attackTick(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> animation) {
      if (!(entitypatch.getOriginal() instanceof SinSummonedSwordEntity sword && sword.isInStandby())) {
         super.attackTick(entitypatch, animation);
      }
   }
}
