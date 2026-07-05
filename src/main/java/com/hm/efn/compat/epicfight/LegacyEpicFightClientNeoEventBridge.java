package com.hm.efn.compat.epicfight;

import net.neoforged.neoforge.common.NeoForge;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.client.neoevent.PatchedRenderersEvent;

public final class LegacyEpicFightClientNeoEventBridge {
   private LegacyEpicFightClientNeoEventBridge() {
   }

   public static void register() {
      EpicFightClientEventHooks.Registry.PATCHED_ITEM.registerEvent(
         LegacyEpicFightClientNeoEventBridge::postPatchedItem,
         "efn:legacy_patched_item_neo_event_bridge"
      );
      EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(
         LegacyEpicFightClientNeoEventBridge::postAddPatchedEntity,
         "efn:legacy_add_patched_entity_neo_event_bridge"
      );
   }

   private static void postPatchedItem(RegisterPatchedRenderersEvent.Item event) {
      NeoForge.EVENT_BUS.post(new PatchedRenderersEvent.RegisterItemRenderer(event));
   }

   private static void postAddPatchedEntity(RegisterPatchedRenderersEvent.AddEntity event) {
      NeoForge.EVENT_BUS.post(new PatchedRenderersEvent.Add(event));
   }
}
