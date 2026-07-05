package com.hm.efn.animations.types.broadblade;

import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordGetter;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;

public class BroadBladeAttackAnimation extends AvalonAttackAnimation {
   public static final MoveCoordGetter BROADBLADE_MODEL_COORD = (animation, entitypatch, coord, prevElapsedTime, elapsedTime) -> {
      JointTransform oJt = coord.getInterpolatedTransform(prevElapsedTime);
      JointTransform jt = coord.getInterpolatedTransform(elapsedTime);
      Vec4f prevpos = new Vec4f(oJt.translation());
      Vec4f currentpos = new Vec4f(jt.translation());
      OpenMatrix4f rotationTransform = entitypatch.getModelMatrix(1.0F).removeTranslation().removeScale();
      OpenMatrix4f localTransform = entitypatch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
      rotationTransform.mulBack(localTransform);
      currentpos.transform(rotationTransform);
      prevpos.transform(rotationTransform);
      boolean hasNoGravity = ((LivingEntity)entitypatch.getOriginal()).isNoGravity();
      boolean moveVertical = animation.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false)
         || animation.getProperty(ActionAnimationProperty.COORD).isPresent();
      float dx = prevpos.x - currentpos.x;
      float dy = !moveVertical && !hasNoGravity ? 0.0F : currentpos.y - prevpos.y;
      float dz = prevpos.z - currentpos.z;
      dx = Math.abs(dx) > 1.0E-4F ? dx : 0.0F;
      dz = Math.abs(dz) > 1.0E-4F ? dz : 0.0F;
      return new Vec3f(dx * 1.35F, dy, dz * 1.35F);
   };

   public BroadBladeAttackAnimation(
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
      this.addProperty(ActionAnimationProperty.COORD_GET, BROADBLADE_MODEL_COORD);
   }

   public BroadBladeAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
      this.addProperty(ActionAnimationProperty.COORD_GET, BROADBLADE_MODEL_COORD);
   }

   public BroadBladeAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
      this.addProperty(ActionAnimationProperty.COORD_GET, BROADBLADE_MODEL_COORD);
   }

   public BroadBladeAttackAnimation(
      float transitionTime, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
      this.addProperty(ActionAnimationProperty.COORD_GET, BROADBLADE_MODEL_COORD);
   }

   public BroadBladeAttackAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
      this.addProperty(ActionAnimationProperty.COORD_GET, BROADBLADE_MODEL_COORD);
   }

   protected void bindPhaseState(Phase phase) {
      float preDelay = phase.preDelay;
      this.stateSpectrumBlueprint
         .newTimePair(0.0F, preDelay)
         .addState(EntityState.PHASE_LEVEL, 1)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .newTimePair(phase.start, phase.recovery + 0.25F)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .newTimePair(phase.start, phase.end)
         .addState(EntityState.INACTION, true)
         .newTimePair(preDelay, phase.contact)
         .addState(EntityState.ATTACKING, true)
         .addState(EntityState.PHASE_LEVEL, 2)
         .newTimePair(phase.contact, phase.end)
         .addState(EntityState.PHASE_LEVEL, 3);
   }
}
