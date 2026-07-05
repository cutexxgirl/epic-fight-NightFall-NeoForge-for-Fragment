package com.hm.efn.gameasset;

import com.hm.efn.gameasset.animations.EFNBroadBladeAnimations;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.event.types.registry.SkillBuilderModificationEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.guard.GuardSkill.Builder;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class EFNWeaponsGuardSkillCompat {
   public static void Guard() {
   }

   public static void onGuardSkillCreate(SkillBuilderModificationEvent event) {
      if (event.getRegistryName().equals(ResourceLocation.fromNamespaceAndPath("epicfight", "guard"))) {
         addBroadBladeGuardMotion(event);
      }
   }

   public static void onParrySkillCreate(SkillBuilderModificationEvent event) {
      if (event.getRegistryName().equals(ResourceLocation.fromNamespaceAndPath("epicfight", "parrying"))) {
         addBroadBladeGuardMotion(event);
      }
   }

   public static void onImpactGuardSkillCreate(SkillBuilderModificationEvent event) {
      if (event.getRegistryName().equals(ResourceLocation.fromNamespaceAndPath("epicfight", "impact_guard"))) {
         addBroadBladeGuardMotion(event);
      }
   }

   private static void addBroadBladeGuardMotion(SkillBuilderModificationEvent event) {
      if (event.getSkillBuilder() instanceof Builder builder) {
         builder.addGuardMotion(
            WeaponCategories.LONGSWORD,
            (item, player) -> item.getStyle(player) == EFNStyles.BOARD_BLADE ? EFNBroadBladeAnimations.BROADBLADE_GUARD_HIT : Animations.LONGSWORD_GUARD_HIT
         );
      }
   }
}
