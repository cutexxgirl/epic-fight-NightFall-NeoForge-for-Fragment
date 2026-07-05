package com.hm.efn.compat.epicfight.eventlistener;

public final class FallEvent extends LegacyPatchEvent<yesman.epicfight.api.event.types.entity.FallEvent> {
   public FallEvent(yesman.epicfight.api.event.types.entity.FallEvent delegate) {
      super(delegate);
   }

   public float getDamageMultiplier() {
      return this.delegate.getDamageMultiplier();
   }

   public float getDistance() {
      return this.delegate.getDistance();
   }

   public void setPlayFallAnimation(boolean flag) {
      this.delegate.setPlayFallAnimation(flag);
   }
}
