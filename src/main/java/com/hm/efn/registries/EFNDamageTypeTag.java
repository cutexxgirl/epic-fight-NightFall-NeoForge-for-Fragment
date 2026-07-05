package com.hm.efn.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class EFNDamageTypeTag {
   public static final TagKey<DamageType> SIN_STUN_IMMUNITY = create("sin_stun_immunity");
   public static final TagKey<DamageType> INVINCIBILITY = create("invincibility");

   private static TagKey<DamageType> create(String name) {
      return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("epicfight", name));
   }
}
