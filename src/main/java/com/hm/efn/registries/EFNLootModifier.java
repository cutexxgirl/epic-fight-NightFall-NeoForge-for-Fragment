package com.hm.efn.registries;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EFNLootModifier {
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
      NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "efn"
   );
}
