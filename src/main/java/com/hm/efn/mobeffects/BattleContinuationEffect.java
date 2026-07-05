package com.hm.efn.mobeffects;

import com.hm.efn.gameasset.EFNExtraDamageInstance;
import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "efn")
public class BattleContinuationEffect extends MobEffect {
   public static final TagKey<DamageType> BATTLE_CONTINUATION_POST_EFFECT = EFNExtraDamageInstance.createDamageType("battle_continuation_post_effect");

   public BattleContinuationEffect() {
      super(MobEffectCategory.BENEFICIAL, 16739179);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide) {
         if (entity.hasEffect(EFNMobEffectRegistry.DIE)) {
            return true;
         }

         MobEffectInstance effect = entity.getEffect(EFNMobEffectRegistry.BATTLE_CONTINUATION);
         entity.addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, false, false, false));
         entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false, false));
         if (effect != null) {
            int remainingDuration = effect.getDuration();
            this.removeHarmfulEffects(entity);
            if (remainingDuration <= 100 && remainingDuration % 20 == 0) {
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_NEARBY_CLOSEST, SoundSource.PLAYERS, 1.5F, 1.0F);
            }

            if (entity.getHealth() <= 2.0F) {
               entity.setHealth(2.0F);
            }
         }
      }

      return super.applyEffectTick(entity, amplifier);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   private void removeHarmfulEffects(LivingEntity entity) {
      List<net.minecraft.core.Holder<MobEffect>> effectsToRemove = new ArrayList<>();

      for (MobEffectInstance effectInstance : entity.getActiveEffects()) {
         net.minecraft.core.Holder<MobEffect> effect = effectInstance.getEffect();
         if (effect.value().getCategory() == MobEffectCategory.HARMFUL) {
            effectsToRemove.add(effect);
         }
      }

      for (net.minecraft.core.Holder<MobEffect> effect : effectsToRemove) {
         entity.removeEffect(effect);
      }
   }

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      if (event.getEffectInstance().getEffect() == EFNMobEffectRegistry.BATTLE_CONTINUATION) {
         LivingEntity entity = event.getEntity();
         Level level = entity.level();
         if (!level.isClientSide()) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F);
            ((ServerLevel)level).sendParticles(ParticleTypes.TOTEM_OF_UNDYING, entity.getX(), entity.getY(0.5), entity.getZ(), 30, 0.5, 0.5, 0.5, 0.5);
            entity.heal(entity.getMaxHealth());
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onEffectRemoved(Remove event) {
      if (event.getEffect() == EFNMobEffectRegistry.BATTLE_CONTINUATION) {
         LivingEntity entity = event.getEntity();
         if (entity != null && !entity.level().isClientSide) {
            entity.addEffect(new MobEffectInstance(EFNMobEffectRegistry.DIE, 60, 1));
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onEffectExpired(Expired event) {
      if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof BattleContinuationEffect) {
         LivingEntity entity = event.getEntity();
         if (entity != null && !entity.level().isClientSide) {
            entity.addEffect(new MobEffectInstance(EFNMobEffectRegistry.DIE, 60, 1));
         }
      }
   }
}
