package com.hm.efn.comboevents;

import com.hm.efn.entity.effect.BlastSummonedSwordEntity;
import com.hm.efn.entity.effect.DamoclesSwordEntity;
import com.hm.efn.entity.effect.HeavyRainSwordEntity;
import com.hm.efn.entity.effect.SummonedSwordEntity_Out;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNWeaponCategories;
import com.p1nero.invincible.api.events.BaseEvent;
import com.p1nero.invincible.api.events.TimePeriodEvent;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.api.combo.ComboNode.ComboTypes;
import com.p1nero.invincible.client.InputManager;
import com.p1nero.invincible.skill.ComboBasicAttack;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class TimeEvents {
   private static final Logger LOGGER = LogManager.getLogger("ComboEvents");

   public static TimeStampedEvent timeConsumeStamina(float time, float consumeStamina) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            float currentStamina = serverPlayerPatch.getStamina();
            serverPlayerPatch.setStamina(currentStamina < consumeStamina ? 0.0F : currentStamina - consumeStamina);
         }
      });
   }

   public static TimeStampedEvent timeAddTargetEffect(float time, Supplier<MobEffect> effect, int level, int duration) {
      return new TimeStampedEvent(time, livingEntityPatch -> {
         LivingEntity target = livingEntityPatch.getTarget();
         if (target != null) {
            target.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect.get()), duration, level));
         }
      });
   }

   public static TimeStampedEvent applyEffectAtTime(MobEffect effect, int duration, int amplifier, float timestamp) {
      return applyEffectAtTime(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), duration, amplifier, timestamp);
   }

   public static TimeStampedEvent applyEffectAtTime(Holder<MobEffect> effect, int duration, int amplifier, float timestamp) {
      return new TimeStampedEvent(timestamp, entityPatch -> {
         LivingEntity target = (LivingEntity)entityPatch.getOriginal();
         if (target != null) {
            int finalDuration = duration == -1 ? Integer.MAX_VALUE : duration;
            target.addEffect(new MobEffectInstance(effect, finalDuration, amplifier, false, false));
         }
      });
   }

   public static TimeStampedEvent removeEffectAtTime(Supplier<MobEffect> effectSupplier, float timestamp) {
      return new TimeStampedEvent(timestamp, entityPatch -> {
         LivingEntity target = (LivingEntity)entityPatch.getOriginal();
         if (target != null) {
            target.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effectSupplier.get()));
         }
      });
   }

   public static TimeStampedEvent playSound(SoundEvent sound, float timestamp, float volume) {
      return new TimeStampedEvent(timestamp, entityPatch -> {
         if (!entityPatch.isLogicalClient()) {
            entityPatch.playSound(sound, volume, 0.0F);
         }
      });
   }

   public static TimeStampedEvent timeConsumeStack(float time, int stackCost) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null && container.getStack() >= stackCost) {
               container.getSkill().setStackSynchronize(container, container.getStack() - stackCost);
            }
         }
      });
   }

   public static TimeStampedEvent timeConsumeConsumption(float time, float amount) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null) {
               float newValue = Math.max(0.0F, container.getResource() - amount);
               container.getSkill().setConsumptionSynchronize(container, newValue);
            }
         }
      });
   }

   public static TimePeriodEvent TimePeriodSimulationComboNodeEvent(float start, float end, ComboNode node) {
      return new TimePeriodEvent(start, end, (playerPatch, target, invinciblePlayer) -> {
         if (playerPatch.getOriginal() instanceof ServerPlayer serverPlayer) {
            ComboBasicAttack.executeNodeOnServer(serverPlayer, node);
         }
      });
   }

   public static TimeStampedEvent TimeSimulationComboNodeEvent(float time, ComboNode node) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
            ComboBasicAttack.executeNodeOnServer(serverPlayer, node);
         }
      });
   }

   public static TimeStampedEvent TimeClearReservedKeysEvent(float time) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof LocalPlayerPatch localPlayerPatch && localPlayerPatch.isLogicalClient()) {
            InputManager.clearReservedKeys();
            InputManager.clearKeyCache();
         }
      });
   }

   public static TimePeriodEvent TimeClearReservedKeysEvent(float start, float end) {
      return new TimePeriodEvent(start, end, (playerPatch, target, invinciblePlayer) -> {
         if (playerPatch instanceof LocalPlayerPatch localPlayerPatch && localPlayerPatch.isLogicalClient()) {
            InputManager.clearReservedKeys();
            InputManager.clearKeyCache();
         }
      });
   }

   public static TimeStampedEvent TimeSimulationAttackEvent(float time) {
      return new TimeStampedEvent(
         time,
         entityPatch -> {
            if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
               SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
               if (serverPlayer != null
                  && dataManager != null
                  && dataManager.hasData(EFNSKillDataKeys.KEY1_LONG_PRESS)
                  && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.KEY1_LONG_PRESS)) {
                  ComboBasicAttack.executeOnServer(serverPlayer, ComboTypes.KEY_1);
               }
            }
         }
      );
   }

   public static TimeStampedEvent TimeSimulationSkillEvent(float time) {
      return new TimeStampedEvent(
         time,
         entityPatch -> {
            if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
               SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
               if (serverPlayer != null
                  && dataManager != null
                  && dataManager.hasData(EFNSKillDataKeys.KEY3_LONG_PRESS)
                  && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.KEY3_LONG_PRESS)) {
                  ComboBasicAttack.executeOnServer(serverPlayer, ComboTypes.KEY_3);
               }
            }
         }
      );
   }

   public static InTimeEvent createComboNodeEvent(float startFrame, ComboNode node) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entitypatch, self, params) -> {
         if (entitypatch instanceof ServerPlayerPatch serverPlayerPatch) {
            ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
            if (isHoldingYamato(player)) {
               ComboBasicAttack.executeNodeOnServer(player, node);
            }
         }
      }, Side.SERVER);
   }

   private static boolean isHoldingYamato(ServerPlayer player) {
      return Stream.of(player.getMainHandItem(), player.getOffhandItem())
         .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
         .filter(Objects::nonNull)
         .anyMatch(cap -> cap.getWeaponCategory() == EFNWeaponCategories.EFN_YAMATO);
   }

   public static TimeStampedEvent summonSingleSwordAtPlayerWaist(float time, Vec3 offset, float scale) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SummonedSwordEntity_Out.summonAtWaist(serverPlayerPatch, offset, scale);
         }
      });
   }

   public static TimeStampedEvent summonHeavyRainLite(float time) {
      return new TimeStampedEvent(
         time,
         entityPatch -> {
            if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               LivingEntity target = entityPatch.getTarget();
               ItemStack mainHandItem = ((ServerPlayer)serverPlayerPatch.getOriginal()).getMainHandItem();
               boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_SUMMONED_SWORD_END) > 0;
               if (hasEnchantment) {
                  if (target != null) {
                     HeavyRainSwordEntity.summonCustom(
                        ((ServerPlayer)serverPlayerPatch.getOriginal()).level(),
                        (LivingEntity)serverPlayerPatch.getOriginal(),
                        target,
                        7,
                        1,
                        5,
                        new double[][]{{1.0, 0.0}, {3.0, 0.8}, {4.0, 1.6}, {3.0, 2.5}}
                     );
                  } else {
                     HeavyRainSwordEntity.summonCustom(
                        ((ServerPlayer)serverPlayerPatch.getOriginal()).level(),
                        (LivingEntity)serverPlayerPatch.getOriginal(),
                        7,
                        1,
                        5,
                        new double[][]{{1.0, 0.0}, {3.0, 0.8}, {4.0, 1.6}, {3.0, 2.5}}
                     );
                  }
               }
            }
         }
      );
   }

   public static TimeStampedEvent summonDamoclesSword(float time) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            LivingEntity target = entityPatch.getTarget();
            ItemStack mainHandItem = ((ServerPlayer)serverPlayerPatch.getOriginal()).getMainHandItem();
            boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_SUMMONED_SWORD_END) > 0;
            if (hasEnchantment) {
               if (target != null) {
                  DamoclesSwordEntity.summon(((ServerPlayer)serverPlayerPatch.getOriginal()).level(), (LivingEntity)serverPlayerPatch.getOriginal(), target);
               } else {
                  DamoclesSwordEntity.summon(((ServerPlayer)serverPlayerPatch.getOriginal()).level(), (LivingEntity)serverPlayerPatch.getOriginal());
               }
            }
         }
      });
   }

   public static TimeStampedEvent summonBlastSwordLite(float time) {
      return new TimeStampedEvent(
         time,
         entityPatch -> {
            if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               ItemStack mainHandItem = ((ServerPlayer)serverPlayerPatch.getOriginal()).getMainHandItem();
               boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_SUMMONED_SWORD_END) > 0;
               if (hasEnchantment) {
                  BlastSummonedSwordEntity.summon(
                     ((ServerPlayer)serverPlayerPatch.getOriginal()).level(), (LivingEntity)serverPlayerPatch.getOriginal(), 4, 5, 2, 1
                  );
               }
            }
         }
      );
   }

   public static TimeStampedEvent summonBlastSwordMid(float time) {
      return new TimeStampedEvent(
         time,
         entityPatch -> {
            if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               ItemStack mainHandItem = ((ServerPlayer)serverPlayerPatch.getOriginal()).getMainHandItem();
               boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_SUMMONED_SWORD_END) > 0;
               if (hasEnchantment) {
                  BlastSummonedSwordEntity.summon(
                     ((ServerPlayer)serverPlayerPatch.getOriginal()).level(), (LivingEntity)serverPlayerPatch.getOriginal(), 5, 5, 2, 2
                  );
               }
            }
         }
      );
   }

   public static TimeStampedEvent playTargetAnimationAtTime(Supplier<? extends StaticAnimation> animation, float convertTime, float timestamp) {
      return new TimeStampedEvent(timestamp, entityPatch -> {
         LivingEntity target = entityPatch.getTarget();
         if (target != null) {
            LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
            if (targetPatch != null) {
               targetPatch.playAnimationSynchronized(animation.get().getAccessor(), convertTime);
            }
         }
      });
   }

   public static TimeStampedEvent startComboCounter(float time) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
            dataManager.setDataSync(EFNSKillDataKeys.COMBO_COUNTER, 0.0F);
         }
      });
   }

   public static TimeStampedEvent resetComboCounter(float time) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
            dataManager.setDataSync(EFNSKillDataKeys.COMBO_COUNTER, 0.0F);
         }
      });
   }

   public static TimeStampedEvent pauseComboCounter(float time) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
            float current = (Float)dataManager.getDataValue(EFNSKillDataKeys.COMBO_COUNTER);
            dataManager.setDataSync(EFNSKillDataKeys.COMBO_COUNTER, current);
         }
      });
   }

   public static TimeStampedEvent setMurasamaSheath(float time, boolean isSheath, boolean updateMotion) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
            dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_SHEATH, isSheath);
            ItemStack mainHandItem = ((Player)entityPatch.getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
            CompoundTag tag = mainHandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putInt("murasama_sheath", isSheath ? 1 : 0);
            mainHandItem.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            if (updateMotion && entityPatch.getOriginal() instanceof ServerPlayer player) {
               serverPlayerPatch.modifyLivingMotionByCurrentItem();
            }

            if (!isSheath) {
               dataManager.setDataSync(EFNSKillDataKeys.MANUAL_UNSHEATH, true);
            }
         }
      });
   }

   public static BaseEvent setMurasamaSheath(boolean isSheath, boolean updateMotion) {
      return BaseEvent.createServerEvent((playerPatch, target, invinciblePlayer) -> {
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skill = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
            if (skill != null) {
               SkillDataManager dataManager = skill.getDataManager();
               var sheathKey = EFNSKillDataKeys.MURASAMA_SHEATH;
               if (sheathKey != null && dataManager.hasData(sheathKey)) {
                  dataManager.setDataSync(sheathKey, isSheath);
               }

               if (updateMotion) {
                  serverPlayerPatch.modifyLivingMotionByCurrentItem();
               }
            }
         }
      });
   }

   public static TimeStampedEvent setZansetsuState(float time, boolean skillActive, int durationSeconds) {
      return new TimeStampedEvent(
         time,
         entityPatch -> {
            if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
               dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE, skillActive);
               if (entityPatch.getOriginal() instanceof ServerPlayer player) {
                  dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER, durationSeconds * 20);
               }
            }
         }
      );
   }

   public static TimeStampedEvent resetZansetsu(float time) {
      return new TimeStampedEvent(time, entityPatch -> {
         if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
            dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE, false);
            dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE, false);
            dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER, 0);
            dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER, 0);
         }
      });
   }
}
