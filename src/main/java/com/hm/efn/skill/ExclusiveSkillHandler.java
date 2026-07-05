package com.hm.efn.skill;

import com.hm.efn.EFN;
import com.hm.efn.capability.AdvanceWeaponCapability;
import com.hm.efn.capability.OriginalSkillCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import yesman.epicfight.api.event.types.player.ChangeInnateSkillEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@EventBusSubscriber(modid = EFN.MODID)
public class ExclusiveSkillHandler {
    public static void onWeaponSkillChange(ChangeInnateSkillEvent event) {
      ServerPlayerPatch serverPlayerPatch = event.getPlayerPatch();
      if (!serverPlayerPatch.isLogicalClient()) {
         if (event.getHand() == InteractionHand.MAIN_HAND) {
            resyncExclusiveSkills(serverPlayerPatch, event.getTo(), event.getToItemCapability());
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         EFN.queueServerWork(5, () -> resyncExclusiveSkills(player));
      }
   }

   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && event.getSlot() == EquipmentSlot.MAINHAND) {
         EFN.queueServerWork(1, () -> resyncExclusiveSkills(player));
      }
   }

   public static void resyncExclusiveSkills(ServerPlayer player) {
      ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
      if (serverPlayerPatch != null && !serverPlayerPatch.isLogicalClient()) {
         ItemStack mainHand = player.getMainHandItem();
         resyncExclusiveSkills(serverPlayerPatch, mainHand, EpicFightCapabilities.getItemStackCapability(mainHand));
      }
   }

   private static void resyncExclusiveSkills(ServerPlayerPatch serverPlayerPatch, ItemStack itemStack, CapabilityItem itemCapability) {
      AdvanceWeaponCapability advCap = itemCapability instanceof AdvanceWeaponCapability capability ? capability : null;
      handleExclusiveSkill(serverPlayerPatch, SkillSlots.DODGE, advCap != null ? advCap.getExclusiveDodge(serverPlayerPatch, itemStack) : null);
      handleExclusiveSkill(serverPlayerPatch, SkillSlots.GUARD, advCap != null ? advCap.getExclusiveGuard(serverPlayerPatch, itemStack) : null);
      handleExclusiveSkill(serverPlayerPatch, SkillSlots.IDENTITY, advCap != null ? advCap.getExclusiveIdentity(serverPlayerPatch, itemStack) : null);
      handleExclusiveSkill(serverPlayerPatch, SkillSlots.MOVER, advCap != null ? advCap.getExclusiveMover(serverPlayerPatch, itemStack) : null);
   }

   private static void handleExclusiveSkill(ServerPlayerPatch serverPlayerPatch, SkillSlot slot, AdvanceWeaponCapability.ExclusiveSkillData exclusiveSkillData) {
      ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
      SkillContainer container = serverPlayerPatch.getSkill(slot);
      if (container == null) {
         return;
      }

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
