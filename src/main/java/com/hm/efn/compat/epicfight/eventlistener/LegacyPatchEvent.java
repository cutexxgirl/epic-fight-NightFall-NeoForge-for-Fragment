package com.hm.efn.compat.epicfight.eventlistener;

import yesman.epicfight.api.event.CancelableEvent;
import yesman.epicfight.api.event.LivingEntityPatchEvent;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

abstract class LegacyPatchEvent<T extends LivingEntityPatchEvent> {
   protected final T delegate;

   protected LegacyPatchEvent(T delegate) {
      this.delegate = delegate;
   }

   public LivingEntityPatch<?> getEntityPatch() {
      return this.delegate.getEntityPatch();
   }

   public PlayerPatch<?> getPlayerPatch() {
      return (PlayerPatch<?>)this.delegate.getEntityPatch();
   }

   public boolean isCanceled() {
      return this.delegate.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      if (canceled && this.delegate instanceof CancelableEvent) {
         this.delegate.cancel();
      }
   }

   public void cancel() {
      this.setCanceled(true);
   }
}
