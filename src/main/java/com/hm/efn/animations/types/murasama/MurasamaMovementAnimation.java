package com.hm.efn.animations.types.murasama;

import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public class MurasamaMovementAnimation extends AvalonMovementAnimation {
   public MurasamaMovementAnimation(
      boolean isRepeat, AnimationAccessor<? extends AvalonMovementAnimation> accessor, AssetAccessor<? extends Armature> armature, float speed
   ) {
      super(isRepeat, accessor, armature, speed);
   }

   public MurasamaMovementAnimation(
      float transitionTime,
      boolean isRepeat,
      AnimationAccessor<? extends AvalonMovementAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float speed
   ) {
      super(transitionTime, isRepeat, accessor, armature, speed);
   }

   public MurasamaMovementAnimation(float transitionTime, boolean isRepeat, String path, AssetAccessor<? extends Armature> armature, float speed) {
      super(transitionTime, isRepeat, path, armature, speed);
   }
}
