package com.hm.efn.gameasset;

import com.hm.efn.gameasset.combos.ExampleCombo;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.event.types.registry.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class ExampleWeaponCapabilityPresets {
   public static final Function<Item, ? extends Builder<?>> EXAMPLE = item -> WeaponCapability.builder()
      .category(WeaponCategories.TACHI)
      .styleProvider(playerpatch -> Styles.TWO_HAND)
      .collider(ColliderPreset.TACHI)
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{Animations.TACHI_AUTO1, Animations.TACHI_AUTO2, Animations.TACHI_AUTO3, Animations.TACHI_DASH, Animations.LONGSWORD_AIR_SLASH}
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> ExampleCombo.exampleCombo)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_UCHIGATANA)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_WALK_UCHIGATANA)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_RUN_UCHIGATANA)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.UCHIGATANA_GUARD);

   public static void onWeaponCapabilityPresetRegistry(WeaponCapabilityPresetRegistryEvent event) {
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "example"), EXAMPLE);
   }
}
