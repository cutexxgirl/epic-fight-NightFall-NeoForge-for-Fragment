package com.hm.efn.mobeffects;

import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BloodExplosionEffect extends MobEffect {
   private static final String ATTACKER_UUID_TAG = "BloodExplosionAttacker";

   public BloodExplosionEffect() {
      super(MobEffectCategory.NEUTRAL, 16711680);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, EffectAttributeModifiers.id("7107DE5E-7CE8-4030-940E-514C1F160890"), -0.5, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      super.onEffectStarted(entity, amplifier);
      if (amplifier >= 59) {
         if (entity.getPersistentData().contains("BloodExplosionAttacker")) {
            String attackerUUID = entity.getPersistentData().getString("BloodExplosionAttacker");
            Player attacker = entity.level().getPlayerByUUID(UUID.fromString(attackerUUID));
            this.triggerBloodExplosion(entity, attacker);
         } else {
            this.triggerBloodExplosion(entity, null);
         }
      }
   }

   public double getAttributeModifierValue(int pAmplifier, AttributeModifier pModifier) {
      return pModifier.amount();
   }

   private void triggerBloodExplosion(LivingEntity entity, Player attacker) {
      if (!entity.level().isClientSide) {
         float maxHealth = entity.getMaxHealth();
         float damage = maxHealth * 0.05F;
         DamageSource damageSource = new DamageSource(entity.level().damageSources().generic().typeHolder(), attacker, attacker, null);
         entity.hurt(damageSource, damage);
         if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               SoundEvents.GENERIC_EXPLODE.value(),
               SoundSource.HOSTILE,
               1.0F,
               0.8F + entity.getRandom().nextFloat() * 0.4F
            );
            Vec3 pos = entity.position().add(0.0, entity.getBbHeight() * 0.5, 0.0);

            for (int i = 0; i < 30; i++) {
               double angle = entity.getRandom().nextDouble() * Math.PI * 2.0;
               double radius = entity.getRandom().nextDouble() * 1.5;
               double x = pos.x + Math.cos(angle) * radius;
               double z = pos.z + Math.sin(angle) * radius;
               double y = pos.y + entity.getRandom().nextDouble();
               serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
               serverLevel.sendParticles(ParticleTypes.CRIT, x, y, z, 1, 0.0, 0.0, 0.0, 0.1);
            }

            for (int i = 0; i < 20; i++) {
               double offsetX = (entity.getRandom().nextDouble() - 0.5) * 2.0;
               double offsetY = entity.getRandom().nextDouble() * 1.5;
               double offsetZ = (entity.getRandom().nextDouble() - 0.5) * 2.0;
               serverLevel.sendParticles(
                  ParticleTypes.CLOUD, entity.getX(), entity.getY() + 1.0, entity.getZ(), 0, offsetX * 0.1, offsetY * 0.1, offsetZ * 0.1, 0.5
               );
            }
         }
      }
   }

   public static void setAttacker(LivingEntity target, Player attacker) {
      if (attacker != null) {
         target.getPersistentData().putString("BloodExplosionAttacker", attacker.getUUID().toString());
      }
   }
}
