package com.hm.efn.capability;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;

@EventBusSubscriber(modid = "efn")
public class OriginalSkillEvents {
   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      if (event.getOriginal().hasData(OriginalSkillCapability.INSTANCE)) {
         OriginalSkillCapability.get(event.getEntity()).copyFrom(OriginalSkillCapability.get(event.getOriginal()));
      }
   }
}
