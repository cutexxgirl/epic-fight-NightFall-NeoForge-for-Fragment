package com.hm.efn.mobeffects;

import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

final class EffectAttributeModifiers {
   private EffectAttributeModifiers() {
   }

   static ResourceLocation id(UUID uuid) {
      return ResourceLocation.fromNamespaceAndPath("efn", uuid.toString().replace('-', '_'));
   }

   static ResourceLocation id(String legacyId) {
      return ResourceLocation.fromNamespaceAndPath("efn", legacyId.toLowerCase().replace('-', '_'));
   }

   static void addTransient(LivingEntity entity, Holder<Attribute> attribute, UUID uuid, double amount, Operation operation) {
      AttributeInstance instance = entity.getAttribute(attribute);
      if (instance != null) {
         instance.addOrUpdateTransientModifier(new AttributeModifier(id(uuid), amount, operation));
      }
   }

   static void remove(LivingEntity entity, Holder<Attribute> attribute, UUID uuid) {
      AttributeInstance instance = entity.getAttribute(attribute);
      if (instance != null) {
         instance.removeModifier(id(uuid));
      }
   }
}
