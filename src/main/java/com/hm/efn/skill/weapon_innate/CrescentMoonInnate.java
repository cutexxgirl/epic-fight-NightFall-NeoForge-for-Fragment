package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNExtraDamageInstance;
import com.hm.efn.gameasset.animations.EFNFalchionAnimations;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.DealDamageEvent.Damage;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class CrescentMoonInnate extends EFNWeaponInnateBase {
   private static final UUID DEAL_DAMAGE_EVENT_DAMAGE_UUID = UUID.fromString("1cf5af2b-8ee8-42f1-be0e-2651145140dc");
   private static final UUID TAKE_DAMAGE_EVENT_ATTACK_UUID = UUID.fromString("1cf5af2b-8ee8-42f1-be1e-3651145140dc");
   private static final UUID DEAL_DAMAGE_EVENT_ATTACK_UUID = UUID.fromString("1cf5af2b-8ee8-42f1-be2e-3651145140dc");
   public static final TagKey<DamageType> EFN_FALCHION_VULNERABILITY = EFNExtraDamageInstance.createDamageType("efn_falchion_vulnerability");
   private int maxShiverAmplifier = 9;
   private int maxVulnerabilityAmplifier = 9;

   public CrescentMoonInnate(Builder builder) {
      super(builder);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      if (parameters.contains("max_shiver_amplifier")) {
         this.maxShiverAmplifier = parameters.getInt("max_shiver_amplifier");
      }

      if (parameters.contains("max_vulnerability_amplifier")) {
         this.maxVulnerabilityAmplifier = parameters.getInt("max_vulnerability_amplifier");
      }
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.DEAL_DAMAGE_EVENT_ATTACK,
         DEAL_DAMAGE_EVENT_ATTACK_UUID,
         event -> {
            if (event.getDamageSource().getAnimation().equals(EFNFalchionAnimations.FALCHION_AUTO1)) {
               event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.BYPASS_DODGE);
            }

            if (event.getDamageSource().getAnimation().equals(EFNFalchionAnimations.FALCHION_AUTO2)
               || event.getDamageSource().getAnimation().equals(EFNFalchionAnimations.FALCHION_AUTO3)
               || event.getDamageSource().getAnimation().equals(EFNFalchionAnimations.FALCHION_AIRSLASH)) {
               event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.GUARD_PUNCTURE);
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.DEAL_DAMAGE_EVENT_DAMAGE, DEAL_DAMAGE_EVENT_DAMAGE_UUID, event -> {
         ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)event.getPlayerPatch();
         LivingEntity target = event.getTarget();
         ServerPlayer attacker = (ServerPlayer)serverPlayerPatch.getOriginal();
         if (target.isAlive() && !this.isFalchionVulnerabilityDamage(event) && this.isHoldingWeapon(container)) {
            this.handleFalchionVulnerability(target, attacker, event.getAttackDamage());
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.TAKE_DAMAGE_EVENT_ATTACK,
         TAKE_DAMAGE_EVENT_ATTACK_UUID,
         event -> {
            ServerPlayer serverPlayer = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
            MobEffectInstance falchionBless = serverPlayer.getEffect(EFNMobEffectRegistry.FALCHION_BLESS);
            DamageSource damageSource = event.getDamageSource();
            LivingEntity attacker = damageSource.getDirectEntity() instanceof LivingEntity ? (LivingEntity)damageSource.getDirectEntity() : null;
            int phaseLevel = ((ServerPlayerPatch)event.getPlayerPatch()).getEntityState().getLevel();
            if (phaseLevel > 0
               && phaseLevel < 3
               && this.isBlockableSource(damageSource)
               && attacker != null
               && event.getDamageSource() instanceof EpicFightDamageSource) {
               Vec3 viewVector = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).getViewVector(1.0F);
               Vec3 attackDirection = Optional.ofNullable(damageSource.getSourcePosition())
                  .map(pos -> pos.subtract(((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).position()).normalize())
                  .orElseGet(attacker::getLookAngle);
               if (Objects.requireNonNull(((ServerPlayerPatch)event.getPlayerPatch()).getAnimator().getPlayerFor(null))
                     .getAnimation()
                     .equals(EFNFalchionAnimations.FALCHION_STRIKE)
                  && attackDirection.dot(viewVector) > 0.0) {
                  ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EFNSounds.PARRY.get(), 1.0F, 1.0F);
                  event.cancel();
                  event.setParried(true);
                  event.setResult(ResultType.BLOCKED);
                  ((ServerPlayerPatch)event.getPlayerPatch()).resetActionTick();
                  float currentStamina = ((ServerPlayerPatch)event.getPlayerPatch()).getStamina();
                  ((ServerPlayerPatch)event.getPlayerPatch()).setStamina(currentStamina + 6.0F);
                  serverPlayer.addEffect(new MobEffectInstance(EFNMobEffectRegistry.COMBO_EXECUTE_WINDOW, 20, 0, false, false, false));
                  ((HitParticleType)EFNParticles.ALL_SPARK.get())
                     .spawnParticleWithArgument(serverPlayer.serverLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, attacker);
                  ((HitParticleType)EpicFightParticles.AIR_BURST.get()).spawnParticleWithArgument(serverPlayer.serverLevel(), serverPlayer, attacker);
               }
            }

            if (Objects.requireNonNull(((ServerPlayerPatch)event.getPlayerPatch()).getAnimator().getPlayerFor(null))
               .getAnimation()
               .equals(EFNFalchionAnimations.FALCHION_EX2)) {
               event.setResult(ResultType.BLOCKED);
               event.cancel();
            }

            if (falchionBless != null && event.getResult().dealtDamage()) {
               serverPlayer.addEffect(new MobEffectInstance(EFNMobEffectRegistry.GRADUAL_HEAL, 40, 1, false, false, false));
            }

            MobEffectInstance existingShiver = attacker != null ? attacker.getEffect(EFNMobEffectRegistry.SHIVER) : null;
            if (existingShiver != null && attacker.getRandom().nextFloat() < 0.25F) {
               event.cancel();
               event.setResult(ResultType.MISSED);
            } else {
               if (event.getResult().dealtDamage() && existingShiver != null) {
                  int currentLevel = existingShiver.getAmplifier();
                  if (currentLevel > 0) {
                     int newLevel = Mth.clamp(currentLevel - 1, 0, this.maxShiverAmplifier);
                     int newDuration = existingShiver.getDuration();
                     MobEffectInstance upgradedEffect = new MobEffectInstance(
                        EFNMobEffectRegistry.SHIVER, newDuration, newLevel, false, false, false
                     );
                     attacker.removeEffect(EFNMobEffectRegistry.SHIVER);
                     attacker.addEffect(upgradedEffect, attacker);
                  } else if (currentLevel == 0) {
                     attacker.removeEffect(EFNMobEffectRegistry.SHIVER);
                  }
               }
            }
         },
         -1
      );
   }

   private void handleFalchionVulnerability(LivingEntity target, ServerPlayer attacker, float attackDamage) {
      MobEffectInstance existingVulnerability = target.getEffect(EFNMobEffectRegistry.FALCHION_VULNERABILITY);
      MobEffectInstance existingShiver = target.getEffect(EFNMobEffectRegistry.SHIVER);
      if (existingVulnerability == null) {
         MobEffectInstance newEffect = new MobEffectInstance(EFNMobEffectRegistry.FALCHION_VULNERABILITY, 600, 0, false, false, false);
         target.addEffect(newEffect, attacker);
      } else {
         int currentLevel = existingVulnerability.getAmplifier();
         int newLevel = Math.min(currentLevel + 1, this.maxVulnerabilityAmplifier);
         float extraDamage = newLevel * 0.05F * attackDamage;
         MobEffectInstance upgradedEffect = new MobEffectInstance(
            EFNMobEffectRegistry.FALCHION_VULNERABILITY, 600, newLevel, false, false, false
         );
         target.removeEffect(EFNMobEffectRegistry.FALCHION_VULNERABILITY);
         target.addEffect(upgradedEffect, attacker);
         DamageSource damageSource = EpicFightDamageSources.playerAttack(attacker)
            .addRuntimeTag(EFN_FALCHION_VULNERABILITY)
            .setAnimation(null)
            .setInitialPosition(attacker.position())
            .setStunType(StunType.NONE)
            .setBaseImpact(0.0F)
            .addRuntimeTag(DamageTypeTags.BYPASSES_ARMOR)
            .addRuntimeTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
            .addRuntimeTag(DamageTypeTags.BYPASSES_COOLDOWN)
            .addRuntimeTag(DamageTypeTags.BYPASSES_SHIELD)
            .addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
         target.hurt(damageSource, extraDamage);
         target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 0.8F, 1.2F);
      }

      if (existingShiver == null) {
         MobEffectInstance newEffect = new MobEffectInstance(EFNMobEffectRegistry.SHIVER, 600, 0, false, false, false);
         target.addEffect(newEffect, attacker);
      } else {
         int currentLevel = existingShiver.getAmplifier();
         int newLevel = Math.min(currentLevel + 1, this.maxShiverAmplifier);
         MobEffectInstance upgradedEffect = new MobEffectInstance(EFNMobEffectRegistry.SHIVER, 600, newLevel, false, false, false);
         target.removeEffect(EFNMobEffectRegistry.SHIVER);
         target.addEffect(upgradedEffect, attacker);
      }
   }

   private boolean isFalchionVulnerabilityDamage(Damage event) {
      EpicFightDamageSource damageSource = event.getDamageSource();
      return damageSource != null && damageSource.is(EFN_FALCHION_VULNERABILITY);
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, this);
   }

   private boolean isBlockableSource(DamageSource damageSource) {
      return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.is(DamageTypeTags.IS_FIRE);
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_DAMAGE, DEAL_DAMAGE_EVENT_DAMAGE_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_ATTACK, DEAL_DAMAGE_EVENT_ATTACK_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, TAKE_DAMAGE_EVENT_ATTACK_UUID, -1);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.crescentmoon.tooltip").withStyle(ChatFormatting.AQUA));
      list.add(
         Component.translatable("skill.efn.crescentmoon.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.GRAY)
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.crescentmoon.tooltip2").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
      list.add(
         Component.translatable("skill.efn.crescentmoon.tooltip3")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.GRAY)
            .append(InvincibleKeyMappings.KEY4.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.crescentmoon.tooltip4").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE));
      list.add(Component.translatable("skill.efn.crescentmoon.tooltip5").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
      list.add(Component.translatable("skill.efn.crescentmoon.tooltip6").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE));
      list.add(Component.translatable("skill.efn.crescentmoon.tooltip7").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE));
      return list;
   }
}
