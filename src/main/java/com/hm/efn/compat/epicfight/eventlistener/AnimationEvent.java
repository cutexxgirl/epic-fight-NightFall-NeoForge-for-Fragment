package com.hm.efn.compat.epicfight.eventlistener;

import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.event.LivingEntityPatchEvent;

public final class AnimationEvent extends LegacyPatchEvent<LivingEntityPatchEvent> {
   private final StaticAnimation animation;

   private AnimationEvent(LivingEntityPatchEvent delegate, StaticAnimation animation) {
      super(delegate);
      this.animation = animation;
   }

   public static AnimationEvent begin(yesman.epicfight.api.event.types.animation.AnimationBeginEvent event) {
      return new AnimationEvent(event, event.getAnimation().get());
   }

   public static AnimationEvent end(yesman.epicfight.api.event.types.animation.AnimationEndEvent event) {
      return new AnimationEvent(event, event.getAnimation().get());
   }

   public StaticAnimation getAnimation() {
      return this.animation;
   }
}
