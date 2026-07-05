package com.hm.efn.mixin;

import com.hm.efn.impl.IEpicFightCameraAPI;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;

@Mixin(value = EpicFightCameraAPI.class, remap = false)
public abstract class MixinEpicFightCameraAPI implements IEpicFightCameraAPI {
   @Shadow(remap = false)
   private LivingEntity focusingEntity;

   @Shadow(remap = false)
   private void sendTargeting(LivingEntity target) {
   }

   @Override
   public void efn$forceSetFocusingEntity(LivingEntity target) {
      this.focusingEntity = target;
      this.sendTargeting(target);
   }
}
