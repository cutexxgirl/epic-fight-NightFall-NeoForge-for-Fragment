package com.hm.efn.animations.types.vfx;

import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class VFXBBBasicAttackAnimation extends ComboAttackAnimation {
   protected float[] bakedTimes;

   public VFXBBBasicAttackAnimation(
      float transitionTime,
      float antic,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends VFXBBBasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, contact, recovery, collider, colliderJoint, accessor, armature);
      this.addProperty(StaticAnimationProperty.POSE_MODIFIER, null);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
      this.initializeVFXProperties();
   }

   public VFXBBBasicAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends VFXBBBasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
      this.addProperty(StaticAnimationProperty.POSE_MODIFIER, null);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
      this.initializeVFXProperties();
   }

   public VFXBBBasicAttackAnimation(
      float transitionTime,
      float antic,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends VFXBBBasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, contact, recovery, hand, collider, colliderJoint, accessor, armature);
      this.addProperty(StaticAnimationProperty.POSE_MODIFIER, null);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
      this.initializeVFXProperties();
   }

   public VFXBBBasicAttackAnimation(
      float transitionTime, AnimationAccessor<? extends VFXBBBasicAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
      this.addProperty(StaticAnimationProperty.POSE_MODIFIER, null);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
      this.initializeVFXProperties();
   }

   public VFXBBBasicAttackAnimation(float transitionTime, String path, AssetAccessor<? extends Armature> armature, Phase... phases) {
      super(transitionTime, path, armature, phases);
      this.addProperty(StaticAnimationProperty.POSE_MODIFIER, null);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
      this.initializeVFXProperties();
   }

   private void initializeVFXProperties() {
      if (this.getAnimationClip() != null && this.getAnimationClip().getJointTransform("Root") != null) {
         Keyframe[] rootKeyframes = this.getAnimationClip().getJointTransform("Root").getKeyframes();
         this.bakedTimes = new float[rootKeyframes.length];

         for (int i = 0; i < rootKeyframes.length; i++) {
            this.bakedTimes[i] = rootKeyframes[i].time();
         }
      }
   }

   public Pose getRawPoseWithoutInterpolate(float time) {
      if (this.bakedTimes != null && this.bakedTimes.length != 0) {
         Pose pose = new Pose();
         int begin = 0;
         int end = this.bakedTimes.length - 1;

         while (end - begin > 1) {
            int i = begin + (end - begin) / 2;
            if (this.bakedTimes[i] <= time && this.bakedTimes[i + 1] > time) {
               begin = i;
               end = i + 1;
               break;
            }

            if (this.bakedTimes[i] > time) {
               end = i;
            } else if (this.bakedTimes[i + 1] <= time) {
               begin = i;
            }
         }

         for (String jointName : this.getAnimationClip().getJointTransforms().keySet()) {
            Keyframe[] keyframes = ((TransformSheet)this.getAnimationClip().getJointTransforms().get(jointName)).getKeyframes();
            if (begin < keyframes.length) {
               JointTransform jt = keyframes[begin].transform().copy();
               pose.putJointData(jointName, jt);
            }
         }

         return pose;
      } else {
         return super.getRawPose(time);
      }
   }

   public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
      float actualPartialTicks = 0.0F;
      Pose pose = this.getRawPoseWithoutInterpolate(time);
      this.modifyPose(this, pose, entitypatch, time, actualPartialTicks);
      return pose;
   }

   public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
      float actualPartialTicks = 0.0F;
      super.modifyPose(animation, pose, entitypatch, time, actualPartialTicks);
   }

   public void end(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      super.end(entitypatch, nextAnimation, isEnd);
      if (!entitypatch.isLogicalClient()) {
         LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
         if (!entity.isRemoved()) {
            entity.discard();
         }
      }
   }
}
