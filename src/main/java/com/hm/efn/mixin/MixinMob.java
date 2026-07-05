package com.hm.efn.mixin;

import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MixinMob {
   @Inject(at = @At(value = "HEAD", remap = false), method = "serverAiStep", cancellable = true, remap = false)
   private void serverAiStep(CallbackInfo info) {
      Mob self = (Mob)(Object)this;
      MobEffectInstance stopEffect = self.getEffect(EFNMobEffectRegistry.STOP);
      MobEffectInstance yamatoStunEffect = self.getEffect(EFNMobEffectRegistry.STUN);
      MobEffectInstance heavyRainStunEffect = self.getEffect(EFNMobEffectRegistry.HEAVY_RAIN_STUN);
      if (stopEffect != null || yamatoStunEffect != null || heavyRainStunEffect != null) {
         info.cancel();
      }
   }

   @Inject(at = @At(value = "HEAD", remap = false), method = "tickHeadTurn", cancellable = true, remap = false)
   private void efn$tickHeadTurn(float p_21538_, float p_21539_, CallbackInfoReturnable<Float> callback) {
      Mob self = (Mob)(Object)this;
      MobEffectInstance stopEffect = self.getEffect(EFNMobEffectRegistry.STOP);
      MobEffectInstance yamatoStunEffect = self.getEffect(EFNMobEffectRegistry.STUN);
      MobEffectInstance heavyRainStunEffect = self.getEffect(EFNMobEffectRegistry.HEAVY_RAIN_STUN);
      if (stopEffect != null || yamatoStunEffect != null || heavyRainStunEffect != null) {
         callback.setReturnValue(0.0F);
      }
   }

   @Inject(at = @At(value = "HEAD", remap = false), method = "createBodyControl", cancellable = true, remap = false)
   protected void efn$createBodyControl(CallbackInfoReturnable<BodyRotationControl> cir) {
      Mob self = (Mob)(Object)this;
      MobEffectInstance stopEffect = self.getEffect(EFNMobEffectRegistry.STOP);
      MobEffectInstance yamatoStunEffect = self.getEffect(EFNMobEffectRegistry.STUN);
      MobEffectInstance heavyRainStunEffect = self.getEffect(EFNMobEffectRegistry.HEAVY_RAIN_STUN);
      if (stopEffect != null || yamatoStunEffect != null || heavyRainStunEffect != null) {
         cir.setReturnValue(null);
      }
   }

   @Inject(at = @At(value = "HEAD", remap = false), method = "tick", cancellable = true, remap = false)
   protected void efn$tick(CallbackInfo ci) {
      Mob self = (Mob)(Object)this;
      if (self.getTarget() == self || self instanceof DoppelgangerEntity doppelganger && self.getTarget() == doppelganger.getOwner()) {
         self.setTarget(null);
      }

      MobEffectInstance yamatoStunEffect = self.getEffect(EFNMobEffectRegistry.STUN);
      MobEffectInstance heavyRainStunEffect = self.getEffect(EFNMobEffectRegistry.HEAVY_RAIN_STUN);
      if (yamatoStunEffect != null || heavyRainStunEffect != null) {
         self.setTarget(null);
         self.getNavigation().stop();
      }
   }

   @Inject(at = @At(value = "HEAD", remap = false), method = "doHurtTarget", cancellable = true, remap = false)
   private void efn$doHurtTarget(Entity target, CallbackInfoReturnable<Boolean> cir) {
      Mob self = (Mob)(Object)this;
      MobEffectInstance yamatoStunEffect = self.getEffect(EFNMobEffectRegistry.STUN);
      MobEffectInstance heavyRainStunEffect = self.getEffect(EFNMobEffectRegistry.HEAVY_RAIN_STUN);
      if (yamatoStunEffect != null || heavyRainStunEffect != null) {
         cir.setReturnValue(false);
         cir.cancel();
      }
   }
}
