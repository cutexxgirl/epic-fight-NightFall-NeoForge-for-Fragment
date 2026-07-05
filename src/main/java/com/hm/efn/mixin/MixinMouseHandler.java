package com.hm.efn.mixin;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MouseHandler.class, priority = 999999999)
public class MixinMouseHandler {
   @Inject(at = @At(value = "HEAD", remap = false), method = "onPress", cancellable = true, remap = false)
   private void onPress(long screen, int button, int action, int mods, CallbackInfo callback) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen == null) {
         LocalPlayer player = mc.player;
         if (player != null && player.hasEffect(EFNMobEffectRegistry.STOP)) {
            callback.cancel();
         }
      }
   }

   @Inject(at = @At(value = "HEAD", remap = false), method = "onMove", cancellable = true, remap = false)
   public void onMove(long p_91562_, double p_91563_, double p_91564_, CallbackInfo callback) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player != null && mc.screen == null && player.hasEffect(EFNMobEffectRegistry.STOP)) {
         callback.cancel();
      }
   }
}
