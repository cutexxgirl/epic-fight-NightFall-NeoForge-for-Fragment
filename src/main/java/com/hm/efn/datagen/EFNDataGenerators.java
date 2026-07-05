package com.hm.efn.datagen;

import com.hm.efn.datagen.loot.EFNLootTableProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class EFNDataGenerators {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      PackOutput output = generator.getPackOutput();
      ExistingFileHelper helper = event.getExistingFileHelper();
      CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
      generator.addProvider(event.includeServer(), EFNLootTableProvider.create(output, lookupProvider));
   }
}
