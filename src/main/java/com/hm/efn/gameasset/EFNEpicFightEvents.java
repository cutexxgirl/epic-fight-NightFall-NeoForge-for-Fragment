package com.hm.efn.gameasset;

import com.hm.efn.client.RendererRegister;
import com.hm.efn.compat.epicfight.LegacyEpicFightClientNeoEventBridge;
import com.hm.efn.compat.epicfight.LegacyEpicFightNeoEventBridge;
import com.hm.efn.entity.EFNEntityHandler;
import com.hm.efn.mobeffects.HeavyRainStunEffect;
import com.hm.efn.mobeffects.SinStunImmunity;
import com.hm.efn.skill.ExclusiveSkillHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.event.EpicFightEventHooks;

public final class EFNEpicFightEvents {
   private EFNEpicFightEvents() {
   }

   public static void register() {
      LegacyEpicFightNeoEventBridge.register();
      EpicFightEventHooks.Player.CHANGE_INNATE_SKILL.registerEvent(ExclusiveSkillHandler::onWeaponSkillChange, "efn:exclusive_skill_handler");
      EpicFightEventHooks.Entity.ON_STUNNED.registerEvent(SinStunImmunity::onEntityStun, "efn:sin_stun_immunity");
      EpicFightEventHooks.Entity.ON_STUNNED.registerEvent(HeavyRainStunEffect::onEntityStun, "efn:heavy_rain_stun");
      EpicFightEventHooks.Registry.WEAPON_CAPABILITY_PRESET.registerEvent(
         EFNWeaponCapabilityPresets::register,
         "efn:weapon_capability_presets"
      );
      EpicFightEventHooks.Registry.WEAPON_CAPABILITY_PRESET.registerEvent(
         ExampleWeaponCapabilityPresets::onWeaponCapabilityPresetRegistry,
         "efn:example_weapon_capability_presets"
      );
      EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(EFNEntityHandler::handleEntityPatchRegistry, "efn:entity_patch_registry");
      EpicFightEventHooks.Registry.MODIFY_SKILL_BUILDER.registerEvent(EFNWeaponsGuardSkillCompat::onGuardSkillCreate, "efn:guard_skill_compat");
      EpicFightEventHooks.Registry.MODIFY_SKILL_BUILDER.registerEvent(EFNWeaponsGuardSkillCompat::onParrySkillCreate, "efn:parry_skill_compat");
      EpicFightEventHooks.Registry.MODIFY_SKILL_BUILDER.registerEvent(
         EFNWeaponsGuardSkillCompat::onImpactGuardSkillCreate,
         "efn:impact_guard_skill_compat"
      );

      if (FMLEnvironment.dist == Dist.CLIENT) {
         registerClient();
      }
   }

   private static void registerClient() {
      LegacyEpicFightClientNeoEventBridge.register();
      EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(EFNEntityHandler::handlePatchedRenderers, "efn:patched_entity_renderers");
      EpicFightClientEventHooks.Registry.PATCHED_ITEM.registerEvent(RendererRegister::registerRenderers, "efn:patched_item_renderers");
   }
}
