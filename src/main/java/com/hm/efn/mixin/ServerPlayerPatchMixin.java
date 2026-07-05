package com.hm.efn.mixin;

import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mixin(value = ServerPlayerPatch.class, remap = false)
public class ServerPlayerPatchMixin {
   @Shadow(remap = false)
   private LivingEntity attackTarget;

   @Inject(method = "preTick", at = @At(value = "TAIL", remap = false), remap = false)
   private void efn$preTick(CallbackInfo ci) {
      ServerPlayerPatch localPlayerPatch = (ServerPlayerPatch)(Object)this;
      if (this.attackTarget instanceof DoppelgangerEntity doppelganger && doppelganger.getOwner().equals(localPlayerPatch.getOriginal())) {
         this.attackTarget = null;
      }
   }
}
