package com.hm.efn.mixin;

import java.util.Objects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.EntitySnapshot.PlayerSnapshot;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = EntitySnapshot.class, remap = false)
public abstract class EntitySnapshotMixin {
   @Inject(method = "capturePlayer", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private static void onCapturePlayer(AbstractClientPlayerPatch<?> abstractClientPlayerPatch, CallbackInfoReturnable<PlayerSnapshot> cir) {
      if (abstractClientPlayerPatch != null) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(abstractClientPlayerPatch.getOriginal(), PlayerPatch.class);
         if (playerPatch != null && !Objects.equals(playerPatch.getArmature().toString(), Armatures.BIPED.registryName().toString())) {
            cir.setReturnValue(null);
            cir.cancel();
         }
      }
   }
}
