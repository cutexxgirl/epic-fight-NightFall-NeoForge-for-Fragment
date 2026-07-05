package com.hm.efn.animations.types.sekiro;

import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;

public class SekiroArtsAnimation extends AvalonAttackAnimation {
   public SekiroArtsAnimation(
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
   }

   public SekiroArtsAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
   }

   public SekiroArtsAnimation(
      float transitionTime, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
   }

   public SekiroArtsAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
   }

   public SekiroArtsAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
   }

   protected void bindPhaseState(Phase phase) {
      float preDelay = phase.preDelay;
      this.stateSpectrumBlueprint
         .newTimePair(0.0F, preDelay)
         .addState(EntityState.PHASE_LEVEL, 1)
         .newTimePair(phase.start, phase.contact)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .newTimePair(phase.start, phase.recovery + 0.1F)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .newTimePair(phase.start, phase.end)
         .addState(EntityState.INACTION, true)
         .newTimePair(phase.antic, phase.end)
         .addState(EntityState.TURNING_LOCKED, true)
         .newTimePair(preDelay, phase.contact)
         .addState(EntityState.ATTACKING, true)
         .addState(EntityState.PHASE_LEVEL, 2)
         .newTimePair(phase.contact, phase.end)
         .addState(EntityState.PHASE_LEVEL, 3);
   }
}
