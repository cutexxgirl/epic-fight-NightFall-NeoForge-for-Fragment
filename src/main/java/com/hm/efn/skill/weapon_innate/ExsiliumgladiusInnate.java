package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class ExsiliumgladiusInnate extends EFNWeaponInnateBase {
   public ExsiliumgladiusInnate(Builder builder) {
      super(builder);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.exsiliumgladius.tooltip").withStyle(ChatFormatting.AQUA));
      list.add(
         Component.translatable("skill.efn.exsiliumgladius.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.AQUA)
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.exsiliumgladius.tooltip2"));
      return list;
   }
}
