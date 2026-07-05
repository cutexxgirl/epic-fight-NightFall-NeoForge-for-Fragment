package com.hm.efn.mixin;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AnimationPlayer.class, remap = false)
public abstract class AnimationPlayerMixin {
   @Shadow(remap = false)
   protected float elapsedTime;
   @Shadow(remap = false)
   protected float prevElapsedTime;

   @Inject(method = "tick", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   public void tick(LivingEntityPatch<?> entitypatch, CallbackInfo ci) {
      try {
         if (entitypatch == null || entitypatch.getOriginal() == null) {
            return;
         }

         if (((LivingEntity)entitypatch.getOriginal()).hasEffect(EFNMobEffectRegistry.STOP)) {
            ci.cancel();
            this.prevElapsedTime = this.elapsedTime;
         }
      } catch (Exception var4) {
      }
   }
}
