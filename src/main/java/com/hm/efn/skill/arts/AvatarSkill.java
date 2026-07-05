package com.hm.efn.skill.arts;

import com.hm.efn.EFN;
import com.hm.efn.event.TickChange;
import com.hm.efn.gameasset.EFNAnimations;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class AvatarSkill extends PassiveSkill {
   private static final UUID EVENT_UUID = UUID.fromString("99e5c782-fdaf-99eb-9a03-0242ac130003");

   public AvatarSkill(SkillBuilder<?> builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.DODGE_SUCCESS_EVENT,
            EVENT_UUID,
            event -> {
               container.getExecutor();
               if (Objects.requireNonNull(((ServerPlayerPatch)event.getPlayerPatch()).getAnimator().getPlayerFor(null)).getAnimation() != EFNAnimations.COOL) {
                  ((ServerPlayerPatch)event.getPlayerPatch()).playAnimationInstantly(EFNAnimations.COOL);
                  Level level = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).level();
                  MinecraftServer server = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).getServer();
                  if (server != null) {
                     int globalPlayerCount = server.getPlayerCount();
                     boolean isDedicatedServer = level.getServer() != null && level.getServer().isDedicatedServer();
                     if (globalPlayerCount <= 1 && !isDedicatedServer) {
                        ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                           .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 7, 3, false, false, false));
                        EFN.queueServerWork(5, () -> TickChange.requestChange(3.0F));
                        EFN.queueServerWork(15, () -> TickChange.requestChange(20.0F));
                     }
                  }
               }
            }
         );
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
   }
}
