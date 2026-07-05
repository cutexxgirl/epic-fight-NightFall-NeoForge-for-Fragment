package com.hm.efn.mixin;

import com.hm.efn.EFN;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.network.EpicFightClientBoundPayloadHandler;
import yesman.epicfight.network.server.SPSkillFeedback;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = EpicFightClientBoundPayloadHandler.class, remap = false)
public interface EpicFightClientBoundPayloadHandlerMixin {
   @Inject(method = "handleSkillFeedback", at = @At("HEAD"), cancellable = true)
   private static void efn$dropEmptySkillFeedback(SPSkillFeedback packet, IPayloadContext context, CallbackInfo ci) {
      EpicFightCapabilities.getUnparameterizedEntityPatch(context.player(), PlayerPatch.class).ifPresent(playerPatch -> {
         SkillContainer container = playerPatch.getSkill(packet.skillSlot());
         if (container == null || container.getSkill() == null) {
            EFN.LOGGER.warn(
               "[EFN/InputTrace] dropped empty client skill feedback slot={} type={} args={} player={}",
               packet.skillSlot(),
               packet.feedbackType(),
               packet.arguments(),
               context.player().getScoreboardName()
            );
            ci.cancel();
         }
      });
   }
}
