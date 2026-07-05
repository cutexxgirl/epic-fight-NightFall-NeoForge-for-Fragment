package com.hm.efn.datagen.loot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class EFNLoot {
   private static final Set<ResourceKey<LootTable>> LOOT_TABLES = new HashSet<>();
   public static final Set<ResourceKey<LootTable>> IMMUTABLE_LOOT_TABLES = Collections.unmodifiableSet(LOOT_TABLES);

   private static ResourceKey<LootTable> register(String id) {
      return register(ResourceLocation.fromNamespaceAndPath("efn", id));
   }

   private static ResourceKey<LootTable> register(ResourceLocation id) {
      ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);
      if (LOOT_TABLES.add(key)) {
         return key;
      } else {
         throw new IllegalArgumentException(id + " is already a registered built-in loot table");
      }
   }
}
