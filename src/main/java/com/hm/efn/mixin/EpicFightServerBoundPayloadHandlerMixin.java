package com.hm.efn.mixin;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.network.EpicFightServerBoundPayloadHandler;
import yesman.epicfight.network.client.CPHandleSkillData;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = EpicFightServerBoundPayloadHandler.class, remap = false)
public interface EpicFightServerBoundPayloadHandlerMixin {
   @Inject(method = "handleSkillData", at = @At("HEAD"), cancellable = true)
   private static void efn$dropStaleEfnSkillData(CPHandleSkillData packet, IPayloadContext context, CallbackInfo ci) {
      if (!packet.skillDataKey().unwrapKey().map(key -> "efn".equals(key.location().getNamespace())).orElse(false)) {
         return;
      }

      EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), PlayerPatch.class).ifPresent(playerPatch -> {
         SkillContainer container = playerPatch.getSkill(packet.skillSlot());
         if (container == null || !container.getDataManager().hasData(packet.skillDataKey())) {
            ci.cancel();
         }
      });
   }
}
