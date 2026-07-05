package com.hm.efn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.client.events.engine.ControlEngine;

@Mixin(value = ControlEngine.class, remap = false)
public interface ControlEngineAccessor {
   @Accessor("weaponInnatePressCounter")
   void efn$setWeaponInnatePressCounter(int value);

   @Accessor("weaponInnatePressToggle")
   void efn$setWeaponInnatePressToggle(boolean value);

   @Accessor("attackLightPressToggle")
   void efn$setAttackLightPressToggle(boolean value);
}
