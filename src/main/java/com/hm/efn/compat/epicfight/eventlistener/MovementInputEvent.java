package com.hm.efn.compat.epicfight.eventlistener;

import net.minecraft.client.player.Input;
import yesman.epicfight.api.client.input.PlayerInputState;
import yesman.epicfight.api.client.event.types.control.MappedMovementInputUpdateEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

public final class MovementInputEvent extends LegacyPatchEvent<MappedMovementInputUpdateEvent> {
   public MovementInputEvent(MappedMovementInputUpdateEvent delegate) {
      super(delegate);
   }

   @Override
   public LocalPlayerPatch getPlayerPatch() {
      return (LocalPlayerPatch)this.delegate.getEntityPatch();
   }

   public Input getMovementInput() {
      return this.delegate.getMovementInput();
   }

   public PlayerInputState getInputState() {
      return this.delegate.getInputState();
   }
}
