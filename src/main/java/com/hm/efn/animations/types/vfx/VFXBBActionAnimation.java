package com.hm.efn.animations.types.vfx;

import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class VFXBBActionAnimation extends ActionAnimation {
   protected float[] bakedTimes;
   private final float play_speed;

   public VFXBBActionAnimation(
      float transitionTime, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature, float playSpeed
   ) {
      super(transitionTime, accessor, armature);
      this.initializeVFXProperties();
      this.play_speed = playSpeed;
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

   public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
      return this.play_speed;
   }
}
