package com.hm.efn.skill;

import com.hm.efn.capability.AdvanceWeaponCapability;
import com.hm.efn.capability.OriginalSkillCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.event.types.player.ChangeInnateSkillEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class ExclusiveSkillHandler {
   public static void onWeaponSkillChange(ChangeInnateSkillEvent event) {
      ServerPlayerPatch serverPlayerPatch = event.getPlayerPatch();
      if (!serverPlayerPatch.isLogicalClient()) {
         if (event.getHand() == InteractionHand.MAIN_HAND) {
            CapabilityItem toCap = event.getToItemCapability();
            ItemStack newItem = event.getTo();
            AdvanceWeaponCapability.ExclusiveSkillData exclusiveDodge = toCap instanceof AdvanceWeaponCapability advCap
               ? advCap.getExclusiveDodge(serverPlayerPatch, newItem)
               : null;
            handleExclusiveSkill(serverPlayerPatch, SkillSlots.DODGE, exclusiveDodge);
            AdvanceWeaponCapability.ExclusiveSkillData exclusiveGuard = toCap instanceof AdvanceWeaponCapability advCap
               ? advCap.getExclusiveGuard(serverPlayerPatch, newItem)
               : null;
            handleExclusiveSkill(serverPlayerPatch, SkillSlots.GUARD, exclusiveGuard);
            AdvanceWeaponCapability.ExclusiveSkillData exclusiveIdentity = toCap instanceof AdvanceWeaponCapability advCap
               ? advCap.getExclusiveIdentity(serverPlayerPatch, newItem)
               : null;
            handleExclusiveSkill(serverPlayerPatch, SkillSlots.IDENTITY, exclusiveIdentity);
            AdvanceWeaponCapability.ExclusiveSkillData exclusiveMover = toCap instanceof AdvanceWeaponCapability advCap
               ? advCap.getExclusiveMover(serverPlayerPatch, newItem)
               : null;
            handleExclusiveSkill(serverPlayerPatch, SkillSlots.MOVER, exclusiveMover);
         }
      }
   }

   private static void handleExclusiveSkill(ServerPlayerPatch serverPlayerPatch, SkillSlot slot, AdvanceWeaponCapability.ExclusiveSkillData exclusiveSkillData) {
      ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
      SkillContainer container = serverPlayerPatch.getSkill(slot);
      Skill currentSkill = container.getSkill();
      int entityId = player.getId();
      String slotName = slot.toString();
      OriginalSkillCapability.IOriginalSkillMemory cap = OriginalSkillCapability.get(player);
      if (exclusiveSkillData != null && exclusiveSkillData.skill() != null) {
         Skill targetSkill = exclusiveSkillData.skill();
         boolean forceReplace = exclusiveSkillData.isFocusReplace();
         if (currentSkill != targetSkill) {
            boolean isSlotEmpty = currentSkill == null;
            if (forceReplace || isSlotEmpty) {
               if (!cap.hasSkill(slotName)) {
                  cap.saveSkill(slotName, currentSkill == null ? null : currentSkill.toString());
               }

               container.setSkill(targetSkill);
               container.setDisabled(false);
                EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(slot, entityId, Skill.holderOrNull(targetSkill)), player);
            }
         }
      } else if (cap.hasSkill(slotName)) {
         String savedSkillId = cap.getSkill(slotName);
         Skill originalSkill = null;
         if (savedSkillId != null && !"none".equals(savedSkillId)) {
            originalSkill = EpicFightRegistries.SKILL.get(ResourceLocation.parse(savedSkillId));
         }

         if (currentSkill != originalSkill) {
            container.setSkill(originalSkill);
             EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(slot, entityId, Skill.holderOrNull(originalSkill)), player);
            if (originalSkill == null) {
               container.setDisabled(true);
            }
         }

         cap.removeSkill(slotName);
      }
   }
}
