package com.hm.efn.compat.epicfight.eventlistener;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class PlayerKilledEvent extends LegacyPatchEvent<yesman.epicfight.api.event.types.entity.KillEntityEvent> {
   public PlayerKilledEvent(yesman.epicfight.api.event.types.entity.KillEntityEvent delegate) {
      super(delegate);
   }

   public LivingEntity getKilledEntity() {
      return this.delegate.getKilledEntity();
   }

   public DamageSource getDamageSource() {
      return this.delegate.getDamageSource();
   }
}
