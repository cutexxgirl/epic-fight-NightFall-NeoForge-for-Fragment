package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class PioneerInnate extends EFNWeaponInnateBase {
   public PioneerInnate(Builder builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().isLogicalClient() && container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
         SkillDataManager manager = container.getDataManager();
         if (manager.hasData(EFNSKillDataKeys.COMBO_COUNTER)) {
            float current = (Float)manager.getDataValue(EFNSKillDataKeys.COMBO_COUNTER);
            if (current < 1.0F) {
               float newValue = current + 0.05F;
               manager.setDataSync(EFNSKillDataKeys.COMBO_COUNTER, newValue);
            } else {
               manager.setDataSync(EFNSKillDataKeys.COMBO_COUNTER, 0.0F);
            }
         }
      }
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.pioneer.tooltip").withStyle(ChatFormatting.GRAY));
      list.add(
         Component.translatable("skill.efn.pioneer.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.GRAY)
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.pioneer.tooltip2"));
      return list;
   }
}
