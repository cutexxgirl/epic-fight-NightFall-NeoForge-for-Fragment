package com.hm.efn.compat.epicfight.eventlistener;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public final class DodgeSuccessEvent extends LegacyPatchEvent<yesman.epicfight.api.event.types.entity.DodgeEvent> {
   public DodgeSuccessEvent(yesman.epicfight.api.event.types.entity.DodgeEvent delegate) {
      super(delegate);
   }

   public DamageSource getDamageSource() {
      return this.delegate.getDamageSource();
   }

   public Vec3 getLocation() {
      return this.delegate.getLocation();
   }
}
