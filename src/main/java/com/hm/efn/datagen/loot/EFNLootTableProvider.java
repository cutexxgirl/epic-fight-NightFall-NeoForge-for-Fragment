package com.hm.efn.datagen.loot;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class EFNLootTableProvider {
   public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
      return new LootTableProvider(
         output, EFNLoot.IMMUTABLE_LOOT_TABLES, List.of(new SubProviderEntry(EFNEntityLootTables::new, LootContextParamSets.ENTITY)), lookupProvider
      );
   }
}
