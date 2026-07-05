package com.hm.efn.gameasset;

import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import yesman.epicfight.world.damagesource.ExtraDamageInstance.ExtraDamage;

public class EFNExtraDamageInstance {
   public static final ExtraDamage EX_DAMAGE_BY_COB_EFFECT = new ExtraDamage((attacker, itemStack, target, baseDamage, floats) -> {
      MobEffectInstance instance = target.getEffect(EFNMobEffectRegistry.CURSE_OF_BLOOD);
      MobEffectInstance instance2 = attacker.getEffect(EFNMobEffectRegistry.BLOOD_BLESSINGS);
      if (instance != null) {
         float aM = floats[0];
         float dM = floats[1];
         int amplifier = instance.getAmplifier() + 1;
         int duration = Mth.clamp(instance.getDuration(), 0, 600);
         return baseDamage * (aM * amplifier + dM * duration);
      } else if (instance2 != null) {
         float aM = floats[0];
         float dM = floats[1];
         int amplifier = instance2.getAmplifier() + 1;
         int duration = Mth.clamp(instance2.getDuration(), 0, 600);
         return baseDamage * (aM * amplifier + dM * duration);
      } else {
         return 0.0F;
      }
   }, (levelReader, itemstack, tooltips, baseDamage, params) -> {});
   public static final ExtraDamage LOST_HEALTH_DAMAGE_WITH_SCALING_CAP = new ExtraDamage((attacker, itemstack, target, baseDamage, params) -> {
      float damageCoefficient = params[0];
      float baseDamageCap = params[1];
      float scalingInterval = params[2];
      float startThreshold = 200.0F;
      float increasePerInterval = 5.0F;
      float lostHealth = target.getMaxHealth() - target.getHealth();
      float extraDamage = lostHealth * damageCoefficient;
      float maxHealth = target.getMaxHealth();
      float dynamicDamageCap = baseDamageCap;
      if (maxHealth > startThreshold) {
         float excessHealth = maxHealth - startThreshold;
         int intervalCount = (int)Math.floor(excessHealth / scalingInterval);
         dynamicDamageCap += intervalCount * increasePerInterval;
      }

      return Math.min(extraDamage, dynamicDamageCap);
   }, (levelReader, itemstack, tooltips, baseDamage, params) -> {});
   public static final ExtraDamage EXTRA_DAMAGE = new ExtraDamage(
      (attacker, itemstack, target, baseDamage, params) -> params[0], (levelReader, itemstack, tooltips, baseDamage, params) -> {}
   );
   public static final ExtraDamage EXTRA_DAMAGE_BLAST_SUMMONED_SWORD = new ExtraDamage(
      (attacker, itemstack, target, baseDamage, params) -> attacker instanceof SinSummonedSwordEntity sinSummonedSwordEntity
            && sinSummonedSwordEntity.isBlast()
         ? params[0]
         : 0.0F,
      (levelReader, itemstack, tooltips, baseDamage, params) -> {}
   );
   public static final ExtraDamage EXTRA_PERCENTAGE_DAMAGE = new ExtraDamage(
      (attacker, itemstack, target, baseDamage, params) -> baseDamage * params[0], (levelReader, itemstack, tooltips, baseDamage, params) -> {}
   );
   public static final ExtraDamage MAX_HEALTH_PERCENTAGE_DAMAGE = new ExtraDamage((attacker, itemstack, target, baseDamage, params) -> {
      float damageCoefficient = params[0];
      float baseDamageCap = params[1];
      float scalingInterval = params[2];
      float startThreshold = 200.0F;
      float increasePerInterval = 5.0F;
      float maxHealth = target.getMaxHealth();
      float extraDamage = maxHealth * damageCoefficient;
      float dynamicDamageCap = baseDamageCap;
      if (maxHealth > startThreshold) {
         float excessHealth = maxHealth - startThreshold;
         int intervalCount = (int)Math.floor(excessHealth / scalingInterval);
         dynamicDamageCap += intervalCount * increasePerInterval;
      }

      return Math.min(extraDamage, dynamicDamageCap);
   }, (levelReader, itemstack, tooltips, baseDamage, params) -> {});
   public static final ExtraDamage EX_DAMAGE_FALCHIONVULNERABILITY = new ExtraDamage((attacker, itemStack, target, baseDamage, floats) -> {
      MobEffectInstance instance = target.getEffect(EFNMobEffectRegistry.FALCHION_VULNERABILITY);
      if (instance != null) {
         float damagePerLevel = floats[0];
         int amplifier = instance.getAmplifier() + 1;
         return baseDamage * damagePerLevel * amplifier;
      } else {
         return 0.0F;
      }
   }, (levelReader, itemstack, tooltips, baseDamage, params) -> {});

   public static TagKey<DamageType> createDamageType(String name) {
      return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("efn", name));
   }
}
