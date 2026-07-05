package com.hm.efn.animations.types.murasama;

import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public class MurasamaLivingAnimation extends StaticAnimation {
   public MurasamaLivingAnimation() {
   }

   public MurasamaLivingAnimation(boolean isRepeat, AnimationAccessor<? extends StaticAnimation> accessor, AssetAccessor<? extends Armature> armature) {
      super(isRepeat, accessor, armature);
   }

   public MurasamaLivingAnimation(
      float transitionTime, boolean isRepeat, AnimationAccessor<? extends StaticAnimation> accessor, AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, isRepeat, accessor, armature);
   }

   public MurasamaLivingAnimation(float transitionTime, boolean isRepeat, String path, AssetAccessor<? extends Armature> armature) {
      super(transitionTime, isRepeat, path, armature);
   }

   public MurasamaLivingAnimation(
      ResourceLocation fileLocation, float transitionTime, boolean isRepeat, String registryName, AssetAccessor<? extends Armature> armature
   ) {
      super(fileLocation, transitionTime, isRepeat, registryName, armature);
   }
}
