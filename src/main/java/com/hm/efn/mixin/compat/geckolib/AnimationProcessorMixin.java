package com.hm.efn.mixin.compat.geckolib;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

@Mixin(value = AnimationProcessor.class, remap = false)
public class AnimationProcessorMixin<T extends GeoAnimatable> {
   @Inject(method = "tickAnimation", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   public void tickAnimation(
      T animatable,
      GeoModel<T> model,
      AnimatableManager<T> animatableManager,
      double animTime,
      AnimationState<T> state,
      boolean crashWhenCantFindBone,
      CallbackInfo ci
   ) {
      if (animatable instanceof GeoEntity geoEntity
         && geoEntity instanceof LivingEntity livingEntity
         && livingEntity.hasEffect(EFNMobEffectRegistry.STOP)) {
         ci.cancel();
      }
   }
}
