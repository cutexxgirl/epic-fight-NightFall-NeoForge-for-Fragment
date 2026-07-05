package com.hm.efn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.guard.GuardSkill;

@Mixin(GuardSkill.class)
public class GuardSkillMixin {
   @Inject(method = "resetHolding", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private void efn$safeResetHolding(SkillContainer container, CallbackInfo ci) {
      if (container == null) {
         System.err.println("GuardSkill.resetHolding called with null container, skipping...");
         ci.cancel();
      }
   }
}
