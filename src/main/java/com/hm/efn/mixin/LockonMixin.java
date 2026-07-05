package com.hm.efn.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;

@Mixin(EpicFightCameraAPI.class)
public abstract class LockonMixin {
   @Inject(method = "predicateFocusableEntity", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private void efn$removeProjectilesFromTargeting(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof Projectile || entity instanceof AbstractHurtingProjectile) {
         cir.setReturnValue(false);
         cir.cancel();
      }
   }
}
