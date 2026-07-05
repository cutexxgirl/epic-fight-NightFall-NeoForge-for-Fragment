package com.hm.efn.mixin;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;

@Mixin(value = Skill.class, remap = false)
public class SkillMixin {
   @Inject(method = "setSkillConsumptionSynchronize", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private static void setSkillConsumptionSynchronize(SkillContainer container, float fVal, CallbackInfo ci) {
      if (container != null
         && container.getDataManager().hasData(EFNSKillDataKeys.HAVE_DOPPELGANGER)
         && (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.HAVE_DOPPELGANGER)) {
         ci.cancel();
      }
   }
}
