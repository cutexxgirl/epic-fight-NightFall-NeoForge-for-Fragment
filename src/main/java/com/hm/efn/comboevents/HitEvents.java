package com.hm.efn.comboevents;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

import com.hm.efn.EFNCommonConfig;
import com.hm.efn.entity.effect.SummonedSwordEntity_In;
import com.hm.efn.entity.effect.SummonedSwordEntity_Out;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import com.hm.efn.mixin.ArmaturesAccessor;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.p1nero.invincible.api.events.BaseEvent;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.skill.ComboBasicAttack;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class HitEvents {
   protected final BiConsumer<LivingEntityPatch<?>, Entity> event;

   public HitEvents(BiConsumer<LivingEntityPatch<?>, Entity> event) {
      this.event = event;
   }

   public static BaseEvent createHitSound(SoundEvent sound) {
      return createHitSound(() -> sound);
   }

   public static BaseEvent createHitSound(Supplier<? extends SoundEvent> soundSupplier) {
      return new BaseEvent((entityPatch, target) -> {
         if (!entityPatch.isLogicalClient()) {
            SoundEvent sound = soundSupplier.get();
            if (sound != null) {
               entityPatch.playSound(sound, 0.0F, 0.0F);
            }
         }
      });
   }

   public static BaseEvent createHitSoundTarget(SoundEvent sound, float volume, float pitch, float delay) {
      return createHitSoundTarget(() -> sound, volume, pitch, delay);
   }

   public static BaseEvent createHitSoundTarget(Supplier<? extends SoundEvent> soundSupplier, float volume, float pitch, float delay) {
      return BaseEvent.createServerEvent((serverPlayerPatch, target, invinciblePlayer) -> {
         SoundEvent sound = soundSupplier.get();
         if (target != null && sound != null) {
            if (target instanceof LivingEntity livingTarget) {
               EpicFightCapabilities.getUnparameterizedEntityPatch(livingTarget, HurtableEntityPatch.class).ifPresent(hitEntityPatch -> {
                  if (hitEntityPatch != null) {
                     hitEntityPatch.playSound(sound, volume, pitch, delay);
                  }
               });
            }
         }
      });
   }

   public static BaseEvent createParticleHitEvent(ParticleOptions particle, int count) {
      return new BaseEvent(
         (entityPatch, target) -> {
            if (!entityPatch.isLogicalClient()) {
               Level level = ((Player)entityPatch.getOriginal()).level();
               if (level instanceof ServerLevel) {
                  ((ServerLevel)level)
                     .sendParticles(
                        particle,
                        ((Player)entityPatch.getOriginal()).getX(),
                        ((Player)entityPatch.getOriginal()).getY(),
                        ((Player)entityPatch.getOriginal()).getZ(),
                        count,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }
      );
   }

   public static BaseEvent knockBackEntity(Vec3 sourceLocation, float power) {
      return new BaseEvent(
         (entityPatch, target) -> {
            if (entityPatch != null && target instanceof LivingEntity livingTarget) {
               double d1 = sourceLocation.x() - livingTarget.getX();

               double d0;
               for (d0 = sourceLocation.z() - livingTarget.getZ(); d1 * d1 + d0 * d0 < 1.0E-4; d0 = (Math.random() - Math.random()) * 0.01) {
                  d1 = (Math.random() - Math.random()) * 0.01;
               }

               float finalPower = power * (1.0F - (float)livingTarget.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
               if (finalPower > 0.0F) {
                  livingTarget.hurtMarked = true;
                  livingTarget.hasImpulse = true;
                  Vec3 currentMotion = livingTarget.getDeltaMovement();
                  Vec3 knockbackVec = new Vec3(d1, 0.0, d0).normalize().scale(finalPower);
                  livingTarget.setDeltaMovement(
                     currentMotion.x / 2.0 - knockbackVec.x,
                     livingTarget.onGround() ? Math.min(0.4, currentMotion.y / 2.0) : currentMotion.y,
                     currentMotion.z / 2.0 - knockbackVec.z
                  );
               }
            }
         }
      );
   }

   public static BaseEvent summonSingleSwordAtPlayerWaist(Vec3 offset, float scale) {
      return BaseEvent.createServerEvent((playerPatch, entity) -> {
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SummonedSwordEntity_Out.summonAtWaist(serverPlayerPatch, offset, scale);
         }
      });
   }

   public static BaseEvent summonSingleSwordAtTargetWaist(Vec3 offset, float scale) {
      return BaseEvent.createServerEvent((playerPatch, entity) -> {
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            LivingEntity target = playerPatch.getTarget();
            if (target != null && target.isAlive()) {
               SummonedSwordEntity_In.summonAtTargetWaist(serverPlayerPatch, target, offset, scale);
               target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 4));
            }
         }
      });
   }

   public static BaseEvent addEffectToPlayer(Supplier<MobEffect> effectSupplier, int duration, int amplifier) {
      return BaseEvent.createServerEvent((playerPatch, entity) -> {
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
            player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effectSupplier.get()), duration, amplifier, false, false, false));
         }
      });
   }

   public static BaseEvent consumeStackBegin(int stackCost) {
      return BaseEvent.createServerEvent((serverPlayerPatch, target) -> {
         SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (container != null && container.getStack() >= stackCost) {
            container.getSkill().setStackSynchronize(container, container.getStack() - stackCost);
         }
      });
   }

   public static BaseEvent knockBackEntityEnhanced(float verticalPower, float horizontalPower, int cooldownTicks) {
      return new BaseEvent(
         (attackerPatch, target) -> {
            if (!attackerPatch.isLogicalClient() && target instanceof LivingEntity livingTarget) {
               CompoundTag targetData = livingTarget.getPersistentData();
               String cooldownKey = "efn_knockback_cooldown";
               long currentTick = livingTarget.level().getGameTime();
               if (!targetData.contains(cooldownKey) || currentTick - targetData.getLong(cooldownKey) >= cooldownTicks) {
                  Vec3 attackerPos = ((Player)attackerPatch.getOriginal()).position();
                  Vec3 targetPos = livingTarget.position();
                  Vec3 direction = new Vec3(targetPos.x - attackerPos.x, 0.0, targetPos.z - attackerPos.z).normalize();
                  float resistance = (float)livingTarget.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                  float finalHorizontal = horizontalPower * (1.0F - resistance);
                  float finalVertical = verticalPower * (1.0F - resistance);
                  if (finalHorizontal > 0.0F || finalVertical > 0.0F) {
                     livingTarget.hurtMarked = true;
                     livingTarget.hasImpulse = true;
                     Vec3 currentMotion = livingTarget.getDeltaMovement();
                     Vec3 knockback = new Vec3(direction.x * finalHorizontal, finalVertical, direction.z * finalHorizontal);
                     livingTarget.setDeltaMovement(
                        currentMotion.x / 2.0 + knockback.x,
                        livingTarget.onGround() ? Math.max(currentMotion.y, knockback.y) : currentMotion.y + knockback.y * 0.5,
                        currentMotion.z / 2.0 + knockback.z
                     );
                     targetData.putLong(cooldownKey, currentTick);
                  }
               }
            }
         }
      );
   }

   public static BaseEvent knockBackEntityEnhanced(float verticalPower, float horizontalPower) {
      return knockBackEntityEnhanced(verticalPower, horizontalPower, 20);
   }

   public static BaseEvent knockBackFromEntity(float power) {
      return new BaseEvent((entityPatch, target, invinciblePlayer) -> {
         if (entityPatch != null && target instanceof LivingEntity livingTarget) {
            LivingEntity source = (LivingEntity)entityPatch.getOriginal();
            knockBackEntity(source.position(), power).testAndExecute(entityPatch, target, invinciblePlayer);
         }
      });
   }

   public static BaseEvent consumeStamina(float consumeStamina) {
      return new BaseEvent((entityPatch, target) -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            float currentStamina = serverPlayerPatch.getStamina();
            serverPlayerPatch.setStamina(currentStamina < consumeStamina ? 0.0F : currentStamina - consumeStamina);
         }
      });
   }

   public static BaseEvent createLostHealthDamage_Meen(float percentage) {
      return new BaseEvent((attackerPatch, target) -> {
         if (!attackerPatch.isLogicalClient() && attackerPatch.getOriginal() instanceof ServerPlayer player && target instanceof LivingEntity livingTarget) {
            MobEffectInstance effect = player.getEffect(EFNMobEffectRegistry.MEEN_LANCE);
            if (effect != null && effect.getDuration() > 0) {
               processLostHealthDamage(player, livingTarget, percentage, 20.0F);
            }
         }
      });
   }

   public static BaseEvent createLostHealthDamage_Blood(float percentage) {
      return new BaseEvent((attackerPatch, target) -> {
         if (!attackerPatch.isLogicalClient() && attackerPatch.getOriginal() instanceof ServerPlayer player && target instanceof LivingEntity livingTarget) {
            MobEffectInstance effect = player.getEffect(EFNMobEffectRegistry.BLODDLUST);
            if (effect != null && effect.getDuration() > 0) {
               processLostHealthDamage(player, livingTarget, percentage, 20.0F);
            }
         }
      });
   }

   private static void processPercentageDamage(Player player, LivingEntity target, float damage) {
      CompoundTag data = target.getPersistentData();
      if (!data.contains("efn_percentageDamageProcessing")) {
         try {
            data.putBoolean("efn_percentageDamageProcessing", true);
            if (damage > 0.0F) {
               target.hurt(target.damageSources().playerAttack(player), damage);
            }
         } finally {
            data.remove("efn_percentageDamageProcessing");
         }
      }
   }

   public static BaseEvent createLostHealthDamage(float percentage) {
      return new BaseEvent((attackerPatch, target) -> {
         if (!attackerPatch.isLogicalClient() && attackerPatch.getOriginal() instanceof ServerPlayer player && target instanceof LivingEntity livingTarget) {
            processLostHealthDamage(player, livingTarget, percentage, 30.0F);
         }
      });
   }

   private static void processLostHealthDamage(Player player, LivingEntity target, float percentage, float maxDamage) {
      CompoundTag data = target.getPersistentData();
      if (!data.contains("efn_lostHealthDamageProcessing")) {
         try {
            data.putBoolean("efn_lostHealthDamageProcessing", true);
            float maxHealth = target.getMaxHealth();
            float currentHealth = target.getHealth();
            float damage = (maxHealth - currentHealth) * percentage;
            damage = Math.min(damage, maxDamage);
            damage = Math.max(damage, 1.0F);
            if (damage > 0.0F) {
               target.hurt(target.damageSources().playerAttack(player), damage);
            }
         } finally {
            data.remove("efn_lostHealthDamageProcessing");
         }
      }
   }

   public static BaseEvent addEffect(Holder<MobEffect> effect, int level, int duration) {
      return new BaseEvent((entityPatch, target) -> {
         if (!entityPatch.isLogicalClient() && target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(effect, duration, level));
         }
      });
   }

   public static BaseEvent addEffect(MobEffect effect, int level, int duration) {
      return addEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), level, duration);
   }

   public static BaseEvent playTargetAnimation(Supplier<? extends StaticAnimation> animation, float convertTime) {
      return new BaseEvent((entityPatch, entity) -> {
         if (entityPatch != null && entity instanceof LivingEntity livingTarget) {
            LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingTarget, LivingEntityPatch.class);
            if (targetPatch != null) {
               if (targetPatch.isStunned()) {
                  Armature armature = targetPatch.getArmature();
                  Map<EntityType<?>, AssetAccessor<? extends Armature>> map = ArmaturesAccessor.getEntityTypeArmatureMapper();
                  AssetAccessor<? extends Armature> armatureAccessor = map.get(((LivingEntity)targetPatch.getOriginal()).getType());
                  boolean isHumanoidSkeleton = armature instanceof HumanoidArmature;
                  boolean isBipedSkeleton = armatureAccessor == Armatures.BIPED;
                  boolean canPlayAnimation = isHumanoidSkeleton || isBipedSkeleton;
                  if (canPlayAnimation) {
                     targetPatch.playAnimationSynchronized(animation.get().getAccessor(), convertTime);
                  }
               }
            }
         }
      });
   }

   public static BaseEvent playTargetAnimationWithGroundCheck(
      Supplier<? extends StaticAnimation> groundAnimation, Supplier<? extends StaticAnimation> airAnimation, float convertTime, float cooldown
   ) {
      return new BaseEvent((entityPatch, entity) -> {
         if (entityPatch != null && entity instanceof LivingEntity livingTarget) {
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(livingTarget.getType());
            if (entityId == null || !((List)EFNCommonConfig.YAMATO_STUNANIMATION_BLACKLIST.get()).contains(entityId.toString())) {
               LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingTarget, LivingEntityPatch.class);
               if (targetPatch != null) {
                  if (targetPatch.isStunned()) {
                     CompoundTag persistentData = livingTarget.getPersistentData();
                     boolean isGrounded = livingTarget.onGround();
                     String animationName = String.valueOf((isGrounded ? groundAnimation : airAnimation).get().getRegistryName());
                     String cooldownKey = "efn_anim_cooldown_" + animationName;
                     long currentTick = livingTarget.level().getGameTime();
                     long lastTriggerTick = persistentData.getLong(cooldownKey);
                     if (!((float)(currentTick - lastTriggerTick) < cooldown * 20.0F)) {
                        StaticAnimation selectedAnimation = isGrounded ? groundAnimation.get() : airAnimation.get();
                        persistentData.putLong(cooldownKey, currentTick);
                        targetPatch.playAnimationSynchronized(selectedAnimation.getAccessor(), convertTime);
                     }
                  }
               }
            }
         }
      });
   }

   public static BaseEvent consumeStack(int stackCost) {
      return new BaseEvent((entityPatch, target) -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null && container.getStack() >= stackCost) {
               container.getSkill().setStackSynchronize(container, container.getStack() - stackCost);
            }
         }
      });
   }

   public static BaseEvent consumeConsumption(float amount) {
      return new BaseEvent((entityPatch, target) -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null) {
               float newValue = Math.max(0.0F, container.getResource() - amount);
               container.getSkill().setConsumptionSynchronize(container, newValue);
            }
         }
      });
   }

   public static BaseEvent HitSimulationComboNodeEvent(ComboNode comboNode) {
      return new BaseEvent((entityPatch, target) -> {
         if (entityPatch.getOriginal() instanceof ServerPlayer serverPlayer && !(target instanceof SinSummonedSwordEntity)) {
            ComboBasicAttack.executeNodeOnServer(serverPlayer, comboNode);
         }
      });
   }
}
