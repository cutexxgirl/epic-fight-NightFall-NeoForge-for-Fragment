package com.hm.efn.compat.epicfight;

import net.neoforged.neoforge.common.NeoForge;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;

public final class LegacyEpicFightNeoEventBridge {
   private LegacyEpicFightNeoEventBridge() {
   }

   public static void register() {
      EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(
         LegacyEpicFightNeoEventBridge::postEntityPatchRegistry,
         "efn:legacy_entity_patch_neo_event_bridge"
      );
   }

   private static void postEntityPatchRegistry(EntityPatchRegistryEvent event) {
      NeoForge.EVENT_BUS.post(new yesman.epicfight.api.neoevent.EntityPatchRegistryEvent(event));
   }
}
