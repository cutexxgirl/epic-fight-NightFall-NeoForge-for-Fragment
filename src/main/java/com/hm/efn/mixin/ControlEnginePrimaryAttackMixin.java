package com.hm.efn.mixin;

import com.hm.efn.client.input.VanillaAttackInputFallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.ControlEngine;

@Mixin(value = ControlEngine.class, remap = false)
public abstract class ControlEnginePrimaryAttackMixin {
   @Inject(method = "handleSeparateWeaponInnateSkill", at = @At("HEAD"), cancellable = true, remap = false)
   private void efn$skipPrimaryAttackWeaponInnate(CallbackInfo ci) {
      if (VanillaAttackInputFallback.shouldSuppressSeparateWeaponInnate()) {
         ci.cancel();
      }
   }
}
