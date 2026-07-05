package com.hm.efn.mixin;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyboardHandler.class, priority = 999999999)
public class MixinKeyboardHandler {
   @Inject(at = @At(value = "HEAD", remap = false), method = "keyPress(JIIII)V", cancellable = true, remap = false)
   public void efn$keyPress(long screen, int key, int scanCode, int action, int modifier, CallbackInfo callback) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen == null) {
         LocalPlayer player = mc.player;
         if (player != null && player.hasEffect(EFNMobEffectRegistry.STOP)) {
            callback.cancel();
         }
      }
   }
}
