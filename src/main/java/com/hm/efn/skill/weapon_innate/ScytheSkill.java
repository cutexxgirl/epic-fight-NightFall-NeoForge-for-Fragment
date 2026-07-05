package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNExtraDamageInstance;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNScytheAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EFNSkillChecks;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.common.ComboAttacks;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.registry.entries.EpicFightSkillDataKeys;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class ScytheSkill extends WeaponInnateSkill {
   private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath("efn", "textures/gui/crimson_moon/background.png");
   private static final ResourceLocation CHARGE = ResourceLocation.fromNamespaceAndPath("efn", "textures/gui/crimson_moon/charge.png");
   private static final ResourceLocation CHARGE_FULL = ResourceLocation.fromNamespaceAndPath("efn", "textures/gui/crimson_moon/charge_full.png");
   private static final UUID EVENT_UUID = UUID.fromString("1cf5af2b-8ee8-42f1-be0e-7651266640dc");
   private static final float MAX_BLOOD_POWER = 1000.0F;
   public static final float NORMAL_CHARGE_PERCENT = 0.02F;
   public static final float SKILL_CHARGE_PERCENT = 0.06F;
   public static final float EX_EFFECT_DAMAGE_BY_AMPLIFIER = 0.08F;
   public static final float EX_EFFECT_DAMAGE_BY_REMAIN_TICK = 0.003F;
   public static final int MAX_DAMAGE_INCREASE = 90;
   public static final int DAMAGE_INCREASE = 15;
   public static final int MAX_A_SPEED_INCREASE = 45;
   public static final int A_SPEED_INCREASE = 15;
   public static final int MAX_REDUCTION = 75;
   public static final int REDUCTION = 25;

   public ScytheSkill(WeaponInnateSkill.Builder<?> builder) {
      super(builder.setResource(Resource.NONE));
   }

   public static float getMaxBloodPower() {
      return 1000.0F;
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.SKILL_CAST_EVENT,
            EVENT_UUID,
            event -> {
               if (!this.isDisabled(container)
                  || container.getExecutor().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() != WeaponCategories.FIST) {
                  boolean sheath = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_SHEATH);
                  if (sheath && container.getExecutor().getEntityState().canUseSkill()) {
                     Skill skill = EFNSkillChecks.skill(event.getSkillContainer());
                     if (skill instanceof ComboAttacks
                        || skill instanceof ScytheSkill
                        || event.getSkillContainer() == container.getExecutor().getSkill(SkillSlots.GUARD)) {
                        event.cancel();
                        container.getExecutor().playAnimationSynchronized(EFNScytheAnimations.SCYTHE_UNSHEATHED, 0.0F);
                     }
                  }
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.SKILL_CONSUME_EVENT,
            EVENT_UUID,
            event -> {
               if (!this.isDisabled(container)) {
                  if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_SHEATH)
                     && event.getSkill().getCategory() == SkillCategories.DODGE) {
                     event.setAmount(event.getAmount() * 0.25F);
                  }
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.DEAL_DAMAGE_EVENT_ATTACK,
            EVENT_UUID,
            event -> {
               if (!this.isDisabled(container)) {
                  EpicFightDamageSource source = event.getDamageSource();
                  LivingEntity target = event.getTarget();
                  Player player = (Player)container.getExecutor().getOriginal();
                  AnimationAccessor<? extends StaticAnimation> animation = source.getAnimation();
                  if (animation == EFNScytheAnimations.SCYTHE_SCARLET_END) {
                     event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.BYPASS_DODGE);
                     event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.GUARD_PUNCTURE);
                     event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.UNBLOCKALBE);
                     event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
                     event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.IS_MELEE);
                     event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.COUNTER);
                     AnimationPlayer animationPlayer = container.getExecutor().getAnimator().getPlayerFor(null);
                     if (animationPlayer != null) {
                        AttackAnimation attackAnimation = (AttackAnimation)animation.get();
                        int length = attackAnimation.phases.length;
                        int phase = attackAnimation.getPhaseOrderByTime(animationPlayer.getElapsedTime());
                        if (phase != length - 1) {
                           source.addExtraDamage(EFNExtraDamageInstance.EX_DAMAGE_BY_COB_EFFECT.create(new float[]{0.064F, 0.0F}));
                        } else {
                           source.addExtraDamage(EFNExtraDamageInstance.EX_DAMAGE_BY_COB_EFFECT.create(new float[]{0.111999996F, 0.003F}));
                        }
                     }
                  }

                  if (animation == EFNScytheAnimations.SCYTHE_HARVEST) {
                     if (!target.hasEffect(EFNMobEffectRegistry.CURSE_OF_BLOOD)
                        && !player.hasEffect(EFNMobEffectRegistry.BLOOD_BLESSINGS)) {
                        source.attachDamageModifier(ValueModifier.multiplier(0.25F));
                     } else {
                        source.attachDamageModifier(ValueModifier.multiplier(1.25F));
                     }
                  }
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.DEAL_DAMAGE_EVENT_DAMAGE,
            EVENT_UUID,
            event -> {
               if (!this.isDisabled(container)) {
                  EpicFightDamageSource source = event.getDamageSource();
                  Player player = (Player)container.getExecutor().getOriginal();
                  LivingEntity target = event.getTarget();
                  AnimationAccessor<? extends StaticAnimation> animation = source.getAnimation();
                  if (animation == EFNScytheAnimations.SCYTHE_SCARLET_END) {
                     MobEffectInstance instance = target.getEffect(EFNMobEffectRegistry.CURSE_OF_BLOOD);
                     MobEffectInstance instance2 = player.getEffect(EFNMobEffectRegistry.BLOOD_BLESSINGS);
                     int amplifier = 0;
                     if (instance != null) {
                        amplifier = instance.getAmplifier() + 1;
                     } else if (instance2 != null) {
                        amplifier = instance2.getAmplifier() + 1;
                     }

                     player.heal(player.getMaxHealth() * 0.03F * amplifier);
                     Vec3 pos = player.position().add(0.0, player.getBbHeight() / 2.0F, 0.0);
                     ((ServerLevel)player.level()).sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 10, 0.35F, 1.0, 0.35F, 1.0);
                     AnimationPlayer animationPlayer = container.getExecutor().getAnimator().getPlayerFor(null);
                     if (animationPlayer != null) {
                        AttackAnimation attackAnimation = (AttackAnimation)animation.get();
                        int length = attackAnimation.phases.length;
                        int phase = attackAnimation.getPhaseOrderByTime(animationPlayer.getElapsedTime());
                        if (phase == length - 1) {
                           if (target.getEffect(EFNMobEffectRegistry.CURSE_OF_BLOOD) != null) {
                              target.removeEffect(EFNMobEffectRegistry.CURSE_OF_BLOOD);
                           } else if (player.getEffect(EFNMobEffectRegistry.BLOOD_BLESSINGS) != null) {
                              player.removeEffect(EFNMobEffectRegistry.BLOOD_BLESSINGS);
                           }
                        }
                     }
                  } else {
                     float current = (Float)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_BLOOD_POWER);
                     float powerIncreased = 20.0F;
                     if (event.getDamageSource().is(EFNAnimations.EFN_CRIMSON_SLASH)) {
                        powerIncreased *= 0.6F;
                     }

                     boolean canBeAffected = target.canBeAffected(new MobEffectInstance(EFNMobEffectRegistry.CURSE_OF_BLOOD));
                     if (animation == EFNScytheAnimations.SCYTHE_AUTO1
                        || animation == EFNScytheAnimations.SCYTHE_AUTO3
                        || animation == EFNScytheAnimations.SCYTHE_AUTO5
                        || animation == EFNAnimations.CRIMSON_SLASH
                        || animation == EFNAnimations.CRIMSON_SLASH_ANTI) {
                        int level = -1;
                        if (canBeAffected) {
                           MobEffectInstance instance = target.getEffect(EFNMobEffectRegistry.CURSE_OF_BLOOD);
                           if (instance != null) {
                              level = instance.getAmplifier();
                           }

                           level = Mth.clamp(level + 1, 0, 255);
                           target.addEffect(new MobEffectInstance(EFNMobEffectRegistry.CURSE_OF_BLOOD, 400, level));
                        } else {
                           MobEffectInstance instance = player.getEffect(EFNMobEffectRegistry.BLOOD_BLESSINGS);
                           if (instance != null) {
                              level = instance.getAmplifier();
                           }

                           level = Mth.clamp(level + 1, 0, 255);
                           player.addEffect(new MobEffectInstance(EFNMobEffectRegistry.BLOOD_BLESSINGS, 400, level));
                        }
                     } else if (animation == EFNScytheAnimations.SCYTHE_DASH
                        || animation == EFNScytheAnimations.SCYTHE_AIR_SLASH
                        || animation == EFNScytheAnimations.SCYTHE_HARVEST) {
                        boolean shouldAddEffect = false;
                         Holder<MobEffect> pEffect = canBeAffected
                            ? EFNMobEffectRegistry.CURSE_OF_BLOOD
                            : EFNMobEffectRegistry.BLOOD_BLESSINGS;
                        LivingEntity removeTarget = (LivingEntity)(canBeAffected ? target : player);
                        MobEffectInstance effect = removeTarget.getEffect(pEffect);
                        if (effect != null) {
                           int targetLevel = effect.getAmplifier() - 1;
                           if (targetLevel >= 0) {
                              removeTarget.forceAddEffect(new MobEffectInstance(pEffect, effect.getDuration(), targetLevel), target);
                           } else {
                              removeTarget.removeEffect(pEffect);
                           }

                           shouldAddEffect = true;
                        }

                        if (shouldAddEffect) {
                           if (animation == EFNScytheAnimations.SCYTHE_DASH) {
                              int level = -1;
                              MobEffectInstance instance = player.getEffect(EFNMobEffectRegistry.DAMAGE_REDUCTION);
                              if (instance != null) {
                                 level = instance.getAmplifier();
                              }

                              player.addEffect(
                                 new MobEffectInstance(EFNMobEffectRegistry.DAMAGE_REDUCTION, 300, Mth.clamp(level + 25, 0, 75))
                              );
                           }

                           if (animation == EFNScytheAnimations.SCYTHE_AIR_SLASH) {
                              int level = -1;
                              MobEffectInstance instance = player.getEffect(EFNMobEffectRegistry.ATTACK_SPEED_INCREASE);
                              if (instance != null) {
                                 level = instance.getAmplifier();
                              }

                              player.addEffect(
                                 new MobEffectInstance(EFNMobEffectRegistry.ATTACK_SPEED_INCREASE, 300, Mth.clamp(level + 15, 0, 44))
                              );
                           }

                           if (animation == EFNScytheAnimations.SCYTHE_HARVEST) {
                              int level = -1;
                              MobEffectInstance instance = player.getEffect(EFNMobEffectRegistry.ATTACK_DAMAGE_INCREASE);
                              if (instance != null) {
                                 level = instance.getAmplifier();
                              }

                              player.addEffect(
                                 new MobEffectInstance(EFNMobEffectRegistry.ATTACK_DAMAGE_INCREASE, 300, Mth.clamp(level + 15, 0, 89))
                              );
                              player.addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 60));
                              target.level().playSound(null, target.blockPosition(), (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get(), SoundSource.HOSTILE);
                              powerIncreased += 60.0F;
                           }
                        }
                     }

                     container.getDataManager()
                        .setDataSync(EFNSKillDataKeys.SCYTHE_BLOOD_POWER, Mth.clamp(current + powerIncreased, 0.0F, 1000.0F));
                  }
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.TAKE_DAMAGE_EVENT_ATTACK,
            EVENT_UUID,
            event -> {
               if (event.isParried()) {
                  container.getExecutor().getSkill(SkillSlots.COMBO_ATTACKS).getDataManager().setDataSync(EpicFightSkillDataKeys.COMBO_COUNTER, 2);
                  float current = (Float)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_BLOOD_POWER);
                  container.getDataManager().setDataSync(EFNSKillDataKeys.SCYTHE_BLOOD_POWER, Mth.clamp(current + 10.0F, 0.0F, 1000.0F));
               }

               if (((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).hasEffect(EFNMobEffectRegistry.DAMAGE_REDUCTION)
                  && ((DynamicAnimation)Objects.requireNonNull(((ServerPlayerPatch)event.getPlayerPatch()).getAnimator().getPlayerFor(null))
                        .getAnimation()
                        .get())
                     .getRealAnimation()
                     .equals(EFNScytheAnimations.SCYTHE_HARVEST)
                  && event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
                  epicFightDamageSource.setStunType(StunType.NONE);
               }

               if (((DynamicAnimation)Objects.requireNonNull(((ServerPlayerPatch)event.getPlayerPatch()).getAnimator().getPlayerFor(null)).getAnimation().get())
                  .getRealAnimation()
                  .equals(EFNScytheAnimations.SCYTHE_SCARLET_END)) {
                  event.cancel();
                  event.setResult(ResultType.BLOCKED);
                  event.setParried(true);
               }
            }
         );
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CONSUME_EVENT, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_DAMAGE, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_ATTACK, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
   }

   @OnlyIn(Dist.CLIENT)
   public void gatherArguments(SkillContainer skillContainer, ControlEngine controlEngine, CompoundTag arguments) {
      arguments.putBoolean("shiftDown", Minecraft.getInstance().options.keyShift.isDown());
   }

   public void executeOnServer(SkillContainer container, CompoundTag args) {
      boolean sheath = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_SHEATH);
      float power = (Float)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_BLOOD_POWER);
      if (!sheath) {
         if (args.getBoolean("shiftDown") && !container.getExecutor().getEntityState().inaction()) {
            container.getExecutor().playAnimationSynchronized(EFNScytheAnimations.SCYTHE_SHEATHED, 0.0F);
         } else if (power >= 1000.0F && container.getExecutor().getHoldingSkill() instanceof GuardSkill) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.SCYTHE_BLOOD_POWER, 0.0F);
            container.getExecutor().playAnimationSynchronized(EFNScytheAnimations.SCYTHE_SCARLET_END, 0.0F);
         } else {
            container.getExecutor().playAnimationSynchronized(EFNScytheAnimations.SCYTHE_HARVEST, 0.0F);
         }
      }

      super.executeOnServer(container, args);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.isDisabled()
         && container.getExecutor().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() != WeaponCategories.FIST
         && (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_SHEATH)) {
         Player player = (Player)container.getExecutor().getOriginal();
         if (!player.level().isClientSide()) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2, false, false, true));
            float current = (Float)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_BLOOD_POWER);
            float healthRatio = player.getHealth() / player.getMaxHealth();
            float healPercent = Mth.clamp(1.0F - healthRatio, 0.0F, 0.2F);
            float cost = Mth.clamp(healPercent / 0.2F * 1000.0F * 0.1F, 0.0F, 100.0F);
            float finalHeal = current >= cost ? 0.2F : current / 100.0F * 0.2F;
            if (finalHeal > 0.0F && healthRatio < 1.0F && player.tickCount % 20 == 0) {
               player.heal(player.getMaxHealth() * finalHeal);
               container.getDataManager().setDataSync(EFNSKillDataKeys.SCYTHE_BLOOD_POWER, Mth.clamp(current - cost, 0.0F, 1000.0F));
               Vec3 pos = player.position().add(0.0, player.getBbHeight() / 2.0F, 0.0);
               ((ServerLevel)player.level()).sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 10, 0.35F, 1.0, 0.35F, 1.0);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      guiGraphics.pose().pushPose();
      float current = (Float)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_BLOOD_POWER);
      float chargePercent = Mth.clamp(current / 1000.0F, 0.0F, 1.0F);
      String powerPercent = String.format("%.0f", chargePercent * 100.0F) + "%";
      if (chargePercent < 1.0F) {
         guiGraphics.drawString(gui.getFont(), powerPercent, x - gui.getFont().width(powerPercent) / 2.0F, y + 32.0F - 9.0F, 16777215, true);
      }

      if (chargePercent == 1.0F) {
         guiGraphics.blit(CHARGE_FULL, (int)x, (int)y, 0.0F, 0.0F, 32, 32, 32, 32);
      } else {
         guiGraphics.blit(CHARGE, (int)x, (int)y, 0.0F, 0.0F, 32, 32, 32, 32);
         guiGraphics.blit(BG, (int)x, (int)y, 0.0F, 0.0F, 32, Math.round(32.0F * (1.0F - chargePercent)), 32, 32);
      }

      guiGraphics.pose().popPose();
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName1 = Component.translatable(Minecraft.getInstance().options.keyShift.getName()).getString();
      String keyName2 = Component.translatable(EpicFightKeyMappings.WEAPON_INNATE_SKILL.getName()).getString();
      boolean longPress = EpicFightKeyMappings.WEAPON_INNATE_SKILL.getKey().getValue() == EpicFightKeyMappings.ATTACK.getKey().getValue();
      list.add(
         Component.translatable("skill.efn.scythe_skill.tooltip")
            .withStyle(ChatFormatting.RED)
            .withStyle(ChatFormatting.BOLD)
            .append(": ")
            .append(longPress ? Component.translatable("skill.efn.scythe_skill.longPress") : Component.empty())
            .append(EpicFightKeyMappings.WEAPON_INNATE_SKILL.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip1").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip2"));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip3"));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip4"));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip5"));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip6"));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip7"));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip8"));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip9"));
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip10"));
      list.add(
         Component.translatable("skill.efn.scythe_skill.tooltip11")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.BLUE)
            .append(keyName1)
            .append(" + ")
            .append(longPress ? Component.translatable("skill.efn.scythe_skill.longPress") : Component.empty())
            .append(EpicFightKeyMappings.WEAPON_INNATE_SKILL.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.scythe_skill.tooltip12"));
      return list;
   }
}
