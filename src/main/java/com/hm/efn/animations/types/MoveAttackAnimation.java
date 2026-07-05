package com.hm.efn.animations.types;

import com.hm.efn.gameasset.EFNJointMask;
import java.util.Optional;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer.Priority;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.datastructure.ParameterizedHashMap;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MoveAttackAnimation extends AttackAnimation {
   public MoveAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends AttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
   }

   public MoveAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends AttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, accessor, armature);
   }

   public MoveAttackAnimation(
      float transitionTime, AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
   }

   public MoveAttackAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      String path,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
   }

   public MoveAttackAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature, Phase... phases) {
      super(convertTime, path, armature, phases);
   }

   public ParameterizedHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> livingEntityPatch, float time) {
      ParameterizedHashMap<StateFactor<?>> stateMap = super.getStatesMap(livingEntityPatch, time);
      stateMap.put(EntityState.MOVEMENT_LOCKED, false);
      stateMap.put(EntityState.UPDATE_LIVING_MOTION, true);
      return stateMap;
   }

   public boolean shouldPlayerMove(LocalPlayerPatch localPlayerPatch) {
      return !localPlayerPatch.isLogicalClient()
         ? true
         : ((LocalPlayer)localPlayerPatch.getOriginal()).input.forwardImpulse == 0.0F
            && ((LocalPlayer)localPlayerPatch.getOriginal()).input.leftImpulse == 0.0F;
   }

   public Optional<JointMaskEntry> getJointMaskEntry(LivingEntityPatch<?> entitypatch, boolean useCurrentMotion) {
      return entitypatch.isLogicalClient() && entitypatch.getClientAnimator().getPriorityFor(this.getAccessor()) == Priority.HIGHEST
         ? Optional.of(EFNJointMask.WHEEL_ATTACK_MASK)
         : super.getJointMaskEntry(entitypatch, useCurrentMotion);
   }
}
