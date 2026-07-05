package com.hm.efn.compat.epicfight.eventlistener;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;

public final class AttackPhaseEndEvent extends LegacyPatchEvent<yesman.epicfight.api.event.types.animation.AttackPhaseEndEvent> {
   public AttackPhaseEndEvent(yesman.epicfight.api.event.types.animation.AttackPhaseEndEvent delegate) {
      super(delegate);
   }

   public AnimationAccessor<? extends AttackAnimation> getAnimation() {
      return this.delegate.getAnimation();
   }

   public AttackAnimation.Phase getPhase() {
      return this.delegate.getPhase();
   }

   public int getPhaseOrder() {
      return this.delegate.getPhaseOrder();
   }

   public boolean isAnimationTerminated() {
      return this.delegate.isAnimationTerminated();
   }
}
