package com.hm.efn.compat.epicfight.eventlistener;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.MainFrameAnimation;

public final class ActionEvent extends LegacyPatchEvent<yesman.epicfight.api.event.types.animation.StartActionEvent> {
   public ActionEvent(yesman.epicfight.api.event.types.animation.StartActionEvent delegate) {
      super(delegate);
   }

   public AnimationAccessor<? extends MainFrameAnimation> getAnimation() {
      return this.delegate.getAnimation();
   }

   public void resetActionTick(boolean flag) {
      this.delegate.resetActionTick(flag);
   }

   public boolean shouldResetActionTick() {
      return this.delegate.shouldResetActionTick();
   }
}
