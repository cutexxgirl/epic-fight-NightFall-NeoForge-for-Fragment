package com.hm.efn.mixin;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyMapping.class, priority = 999999999)
public class MixinKeyMapping {
   @Shadow(remap = false)
   boolean isDown;

   @Inject(at = @At(value = "HEAD", remap = false), method = "isDown()Z", cancellable = true, remap = false)
   public void efn$isDown(CallbackInfoReturnable<Boolean> callback) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player != null && player.hasEffect(EFNMobEffectRegistry.STOP)) {
         callback.setReturnValue(false);
         callback.cancel();
         if (this.isDown) {
            this.isDown = false;
         }
      }
   }

   @Inject(at = @At(value = "HEAD", remap = false), method = "consumeClick()Z", cancellable = true, remap = false)
   public void efn$consumeClick(CallbackInfoReturnable<Boolean> callback) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player != null && player.hasEffect(EFNMobEffectRegistry.STOP)) {
         callback.setReturnValue(false);
         callback.cancel();
      }
   }

   @Inject(at = @At(value = "HEAD", remap = false), method = "matches", cancellable = true, remap = false)
   public void efn$matches(int key, int scancode, CallbackInfoReturnable<Boolean> callback) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player != null && player.hasEffect(EFNMobEffectRegistry.STOP)) {
         callback.setReturnValue(false);
         callback.cancel();
      }
   }

   @Inject(at = @At(value = "HEAD", remap = false), method = "matchesMouse", cancellable = true, remap = false)
   public void efn$matchesMouse(int button, CallbackInfoReturnable<Boolean> callback) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player != null && player.hasEffect(EFNMobEffectRegistry.STOP)) {
         callback.setReturnValue(false);
         callback.cancel();
      }
   }
}
