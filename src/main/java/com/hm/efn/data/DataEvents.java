package com.hm.efn.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public final class DataEvents {
   @SubscribeEvent
   public static void efn$gatherData(GatherDataEvent event) {
   }
}
