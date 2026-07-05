package com.hm.efn.mixin;

import com.hm.efn.event.TickChange;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Util.class, priority = 1000)
public abstract class UtilMixin {
   @Inject(method = "getMillis", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private static void efn$modifyTime(CallbackInfoReturnable<Long> cir) {
      if (TickChange.PERCENT != 20.0F || TickChange.millisF > 0.0) {
         cir.setReturnValue(TickChange.millis);
      }
   }
}
