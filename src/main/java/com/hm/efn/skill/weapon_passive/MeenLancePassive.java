package com.hm.efn.skill.weapon_passive;

import com.hm.efn.gameasset.animations.EFNLanceAnimations;
import com.hm.efn.gameasset.combos.Meenlance;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class MeenLancePassive extends PassiveSkill {
   private static final UUID MEEN_CHARGE_ANIMATION_LISTENER_UUID = UUID.fromString("e5f6a1b2-c3d4-11ed-a05b-0242ac114513");
   private static final UUID EVENT_UUID = UUID.fromString("e5f6a1b2-c3d4-11ed-a05b-0242ac114514");
   private final boolean isBeingRemoved = false;

   public MeenLancePassive(SkillBuilder<?> builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, MEEN_CHARGE_ANIMATION_LISTENER_UUID, event -> {
         if (event.getPlayerPatch() instanceof ServerPlayerPatch serverPatch && serverPatch.getOriginal() instanceof ServerPlayer) {
            this.handleMeenlanceAnimation(serverPatch, event.getAnimation());
         }
      });
   }

   private void handleMeenlanceAnimation(ServerPlayerPatch playerPatch, StaticAnimation animation) {
      if (playerPatch != null && !((ServerPlayer)playerPatch.getOriginal()).level().isClientSide()) {
         ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
         if (animation == EFNLanceAnimations.NF_MEEN_CHARGE1.get()) {
            this.updateComboNode(player, Meenlance.MeenExtendRoot_1);
         } else if (animation == EFNLanceAnimations.NF_MEEN_CHARGE2.get()) {
            this.updateComboNode(player, Meenlance.MeenExtendRoot_2);
         } else if (animation == EFNLanceAnimations.NF_MEEN_CHARGE3.get()) {
            this.updateComboNode(player, Meenlance.MeenExtendRoot_3);
         } else if (animation == EFNLanceAnimations.NF_MEEN_AUTO3.get()) {
            this.updateComboNode(player, Meenlance.MeenExtendRoot_4);
         }
      }
   }

   private void updateComboNode(ServerPlayer player, ComboNode node) {
      InvincibleAttachments.getPlayer(player).setCurrentNode(node);
   }

   public void onRemoved(SkillContainer container) {
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, MEEN_CHARGE_ANIMATION_LISTENER_UUID);
   }
}
