package com.hm.efn.animations.types;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class DeferredDamageAttackAnimation extends ScanAttackAnimation {
   boolean hasSheathed = false;

   public DeferredDamageAttackAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends DeferredDamageAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
   }

   public DeferredDamageAttackAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends DeferredDamageAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, accessor, armature);
   }

   public DeferredDamageAttackAnimation(
      float convertTime, AnimationAccessor<? extends DeferredDamageAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases
   ) {
      super(convertTime, accessor, armature, phases);
   }

   public DeferredDamageAttackAnimation(
      float convertTime,
      float antic,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint scanner,
      AnimationAccessor<? extends DeferredDamageAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, contact, recovery, hand, collider, scanner, accessor, armature);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
   }

   public AnimationClip getAnimationClip() {
      return super.getAnimationClip();
   }

   @Override
   public void attackTick(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> animation) {
      AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(this.getAccessor());
      float elapsedTime = player.getElapsedTime();
      float prevElapsedTime = player.getPrevElapsedTime();
      EntityState state = this.getState(entitypatch, elapsedTime);
      EntityState prevState = this.getState(entitypatch, prevElapsedTime);
      Phase phase = this.getPhaseByTime(elapsedTime);
      if (state.getLevel() == 1 && !state.turningLocked() && entitypatch instanceof MobPatch<?> mobpatch) {
         ((Mob)mobpatch.getOriginal()).getNavigation().stop();
         ((LivingEntity)entitypatch.getOriginal()).attackAnim = 2.0F;
         LivingEntity target = entitypatch.getTarget();
         if (target != null) {
            entitypatch.rotateTo(target, entitypatch.getYRotLimit(), false);
         }
      }

      if (prevState.attacking() || state.attacking() || prevState.getLevel() < 2 && state.getLevel() > 2) {
         this.ScanTarget(entitypatch, prevElapsedTime, elapsedTime, prevState, state, phase);
      }
   }

   protected void bindPhaseState(Phase phase) {
      this.stateSpectrumBlueprint
         .newTimePair(phase.start, phase.preDelay)
         .addState(EntityState.PHASE_LEVEL, 1)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .newTimePair(phase.start, phase.end)
         .addState(EntityState.INACTION, true)
         .newTimePair(phase.preDelay, phase.contact + 0.01F)
         .addState(EntityState.ATTACKING, true)
         .addState(EntityState.PHASE_LEVEL, 2)
         .newTimePair(phase.contact + 0.01F, phase.end)
         .addState(EntityState.PHASE_LEVEL, 3)
         .newTimePair(phase.start, phase.end)
         .addState(EntityState.TURNING_LOCKED, true);
   }
}
