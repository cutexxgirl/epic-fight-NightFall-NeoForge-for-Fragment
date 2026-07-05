package com.hm.efn.util;

import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public final class EFNSkillChecks {
   private EFNSkillChecks() {
   }

   public static Skill skill(SkillContainer container) {
      return container == null ? null : container.getSkill();
   }

   public static boolean hasCategory(SkillContainer container, SkillCategory category) {
      Skill skill = skill(container);
      return skill != null && skill.getCategory() == category;
   }

   public static Skill innateSkill(PlayerPatch<?> executor, ItemStack itemStack) {
      CapabilityItem capability = EpicFightCapabilities.getItemStackCapability(itemStack);
      return capability == null ? null : capability.getInnateSkill(executor, itemStack);
   }

   public static boolean isInnateSkill(PlayerPatch<?> executor, ItemStack itemStack, Skill expectedSkill) {
      return innateSkill(executor, itemStack) == expectedSkill;
   }
}
