package com.hm.efn.mixin;

import com.hm.efn.EFN;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.network.EpicFightClientBoundPayloadHandler;
import yesman.epicfight.network.server.SPAnimatorControl;
import yesman.epicfight.network.server.SPSkillFeedback;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = EpicFightClientBoundPayloadHandler.class, remap = false)
public interface EpicFightClientBoundPayloadHandlerMixin {
   @Inject(method = "handleAnimatorControl", at = @At("HEAD"))
   private static void efn$traceAnimatorControl(SPAnimatorControl packet, IPayloadContext context, CallbackInfo ci) {
      Player player = context.player();
      if (player != null && packet.entityId() == player.getId()) {
         EFN.LOGGER.info(
            "[EFN/InputTrace] client animator control action={} animation={} layer={} priority={} transition={} player={}",
            packet.action(),
            packet.animation() == null ? "null" : packet.animation().registryName(),
            packet.layer(),
            packet.priority(),
            packet.transitionTimeModifier(),
            player.getScoreboardName()
         );
      }
   }

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
