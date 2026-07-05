package com.hm.efn.mixin;

import com.hm.efn.EFN;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.network.EpicFightServerBoundPayloadHandler;
import yesman.epicfight.network.client.CPHandleSkillData;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = EpicFightServerBoundPayloadHandler.class, remap = false)
public interface EpicFightServerBoundPayloadHandlerMixin {
   @Inject(method = "handleExecuteSkill", at = @At("HEAD"))
   private static void efn$traceExecuteSkill(CPSkillRequest packet, IPayloadContext context, CallbackInfo ci) {
      EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), PlayerPatch.class).ifPresent(playerPatch -> {
         SkillContainer container = playerPatch.getSkill(packet.skillSlot());
         EFN.LOGGER.info(
            "[EFN/InputTrace] server skill request slot={} work={} skill={} args={} player={}",
            packet.skillSlot(),
            packet.workType(),
            container == null || container.getSkill() == null ? "null" : container.getSkill().getRegistryName(),
            packet.arguments(),
            context.player().getScoreboardName()
         );
      });
   }

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
