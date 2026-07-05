package com.hm.efn.mixin.compat.lionfishapi;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.github.L_Ender.lionfishapi.server.animation.AnimationHandler", remap = false)
public class AnimationHandlerMixin {
   @Inject(method = "updateAnimations", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   public void updateAnimations(Entity entity, CallbackInfo ci) {
      if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(EFNMobEffectRegistry.STOP)) {
         ci.cancel();
      }
   }
}
