package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.EFNExtraDamageInstance;
import com.hm.efn.gameasset.animations.EFNBroadBladeAnimations;
import com.hm.efn.mobeffects.BattleContinuationEffect;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.ValueModifier;
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
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class BroadBladeInnate extends EFNWeaponInnateBase {
   private static final UUID TAKE_DAMAGE_EVENT_ATTACK = UUID.fromString("a1b2c3d4-e5f6-22ed-a15b-0242ac160030");
   private static final UUID DEAL_DAMAGE_EVENT_DAMAGE = UUID.fromString("a1b2c3d4-e5f6-22ed-a16b-0242ac160030");
   private static final UUID PLAYER_KILLED_EVENT = UUID.fromString("a1b2c3d4-e5f6-22ed-a17b-0242ac160030");
   private static final UUID DEAL_DAMAGE_EVENT_ATTACK = UUID.fromString("a1b2c3d4-e5f6-22ed-a18b-0242ac160030");
   public static final TagKey<DamageType> EFN_BROADBLADE_EXTRA_DAMAGE = EFNExtraDamageInstance.createDamageType("efn_broadblade_extra_damage");
   private boolean needenchantment_revolution;

   public BroadBladeInnate(Builder builder) {
      super(builder);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.needenchantment_revolution = parameters.getBoolean("needenchantment_revolution");
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.TAKE_DAMAGE_EVENT_ATTACK,
         TAKE_DAMAGE_EVENT_ATTACK,
         event -> {
            if (this.isHoldingWeapon(container)) {
               if (((ServerPlayerPatch)event.getPlayerPatch()).getEntityState().getLevel() == 1
                  && event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
                  epicFightDamageSource.setStunType(StunType.NONE);
               }

               if (Objects.requireNonNull(((ServerPlayerPatch)event.getPlayerPatch()).getAnimator().getPlayerFor(null))
                  .getAnimation()
                  .equals(EFNBroadBladeAnimations.BROADBLADE_AUTO7)) {
                  event.setResult(ResultType.BLOCKED);
                  event.cancel();
               }

               if (Objects.requireNonNull(((ServerPlayerPatch)event.getPlayerPatch()).getAnimator().getPlayerFor(null))
                  .getAnimation()
                  .equals(EFNBroadBladeAnimations.BROADBLADE_COUNTER)) {
                  event.setResult(ResultType.BLOCKED);
                  event.cancel();
               }

               if (((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                     .hasEffect(EFNMobEffectRegistry.BATTLE_CONTINUATION)
                  && !event.getDamageSource().is(BattleContinuationEffect.BATTLE_CONTINUATION_POST_EFFECT)) {
                  event.cancel();
                  event.setResult(ResultType.MISSED);
               }
            }
         },
         -1
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.PLAYER_KILLED_EVENT, PLAYER_KILLED_EVENT, event -> {
         if (this.isHoldingWeapon(container)) {
            ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)event.getPlayerPatch();
            ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
            serverPlayer.addEffect(new MobEffectInstance(EFNMobEffectRegistry.GRADUAL_HEAL, 40, 4));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.DEAL_DAMAGE_EVENT_ATTACK,
         DEAL_DAMAGE_EVENT_ATTACK,
         event -> {
            if (this.isHoldingWeapon(container)) {
               ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)event.getPlayerPatch();
               ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
               LivingEntity target = event.getTarget();
               Random random = new Random();
               ItemStack mainHandItem = serverPlayer.getMainHandItem();
               boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.BROAD_BLADE_ENHANCE) > 0;
               boolean canUse;
               if (this.needenchantment_revolution) {
                  canUse = hasEnchantment;
               } else {
                  canUse = true;
               }

               if (random.nextFloat() < 0.3F && !event.getDamageSource().is(EFN_BROADBLADE_EXTRA_DAMAGE) && canUse) {
                  event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.BYPASS_DODGE);
                  event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.GUARD_PUNCTURE);
                  event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.UNBLOCKALBE);
                  event.getDamageSource().addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
                  event.getDamageSource().addRuntimeTag(DamageTypeTags.BYPASSES_ARMOR);
                  event.getDamageSource().addRuntimeTag(DamageTypeTags.BYPASSES_INVULNERABILITY);
                  event.getDamageSource().addRuntimeTag(DamageTypeTags.BYPASSES_RESISTANCE);
                  event.getDamageSource().addRuntimeTag(DamageTypeTags.BYPASSES_ENCHANTMENTS);
                  event.getDamageSource().addRuntimeTag(DamageTypeTags.BYPASSES_EFFECTS);
                  event.getDamageSource().addRuntimeTag(DamageTypeTags.BYPASSES_COOLDOWN);
                  event.getDamageSource().addRuntimeTag(DamageTypeTags.BYPASSES_SHIELD);
                  float extraDamage = target.getMaxHealth() * 0.05F;
                  DamageSource damageSource = EpicFightDamageSources.playerAttack(serverPlayer)
                     .setAnimation(null)
                     .addRuntimeTag(EFN_BROADBLADE_EXTRA_DAMAGE)
                     .setInitialPosition(serverPlayer.position())
                     .setStunType(StunType.NONE)
                     .setBaseImpact(0.0F)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_ARMOR)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_COOLDOWN)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_RESISTANCE)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_EFFECTS)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_SHIELD)
                     .addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
                  target.hurt(damageSource, extraDamage);
                  target.level()
                     .playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 0.8F, 1.2F);
                  serverPlayer.serverLevel()
                     .sendParticles(ParticleTypes.ENCHANTED_HIT, target.getX(), target.getY() + 1.0, target.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                  ((HitParticleType)EpicFightParticles.BLADE_RUSH_SKILL.get())
                     .spawnParticleWithArgument(serverPlayer.serverLevel(), null, null, target, serverPlayer);
                  serverPlayer.playSound(SoundEvents.WITHER_HURT, 0.2F, 0.4F + 0.25F * random.nextFloat());
               }
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.DEAL_DAMAGE_EVENT_HURT,
         DEAL_DAMAGE_EVENT_DAMAGE,
         event -> {
            if (this.isHoldingWeapon(container)) {
               ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)event.getPlayerPatch();
               ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
               LivingEntity target = event.getTarget();
               Random rand = new Random();
               Animator animator = serverPlayerPatch.getAnimator();
               AnimationPlayer animationPlayer = animator.getPlayerFor(null);
               if (animationPlayer != null && animationPlayer.getAnimation().get() instanceof AttackAnimation attackAnimation) {
                  float elapsedTime = animationPlayer.getElapsedTime();
                  float prevElapsedTime = animationPlayer.getPrevElapsedTime();
                  Phase phase = attackAnimation.getPhaseByTime(elapsedTime);
                  List<Entity> collidingEntities = phase.getCollidingEntities(
                     serverPlayerPatch, attackAnimation, prevElapsedTime, elapsedTime, attackAnimation.getPlaySpeed(serverPlayerPatch, attackAnimation)
                  );
                  int hitCount = collidingEntities.size();
                  if (hitCount > 0) {
                     if (hitCount == 1 && !event.getDamageSource().is(EFN_BROADBLADE_EXTRA_DAMAGE)) {
                        event.getDamageSource().attachDamageModifier(ValueModifier.multiplier(1.5F));
                        serverPlayer.playSound(SoundEvents.TRIDENT_THROW.value(), 1.1F, 0.625F + 0.1F * rand.nextFloat());
                     }

                     if (hitCount > 2 && !event.getDamageSource().is(EFN_BROADBLADE_EXTRA_DAMAGE)) {
                        float extraDamage = target.getMaxHealth() * 0.02F;
                        DamageSource damageSource = EpicFightDamageSources.playerAttack(serverPlayer)
                           .setAnimation(null)
                           .setInitialPosition(serverPlayer.position())
                           .addRuntimeTag(EFN_BROADBLADE_EXTRA_DAMAGE)
                           .setStunType(StunType.NONE)
                           .setBaseImpact(0.0F)
                           .addRuntimeTag(DamageTypeTags.BYPASSES_ARMOR)
                           .addRuntimeTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                           .addRuntimeTag(DamageTypeTags.BYPASSES_RESISTANCE)
                           .addRuntimeTag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                           .addRuntimeTag(DamageTypeTags.BYPASSES_EFFECTS)
                           .addRuntimeTag(DamageTypeTags.BYPASSES_COOLDOWN)
                           .addRuntimeTag(DamageTypeTags.BYPASSES_SHIELD)
                           .addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
                        target.hurt(damageSource, extraDamage);
                        target.level()
                           .playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 0.8F, 1.2F);
                     }
                  }
               }

               if (((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                  .hasEffect(EFNMobEffectRegistry.BATTLE_CONTINUATION)) {
                  event.getDamageSource().addExtraDamage(EFNExtraDamageInstance.EXTRA_PERCENTAGE_DAMAGE.create(new float[]{1.0F}));
               }
            }
         }
      );
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, TAKE_DAMAGE_EVENT_ATTACK, -1);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.DEAL_DAMAGE_EVENT_HURT, DEAL_DAMAGE_EVENT_DAMAGE);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.PLAYER_KILLED_EVENT, PLAYER_KILLED_EVENT);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.DEAL_DAMAGE_EVENT_ATTACK, DEAL_DAMAGE_EVENT_ATTACK);
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, this);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      ItemStack mainHandItem = ((Player)playerpatch.getOriginal()).getMainHandItem();
      boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.BROAD_BLADE_ENHANCE) > 0;
      boolean canUse;
      if (this.needenchantment_revolution) {
         canUse = hasEnchantment;
      } else {
         canUse = true;
      }

      list.add(Component.translatable("skill.efn.broadblade.tooltip").withStyle(ChatFormatting.AQUA));
      list.add(
         Component.translatable("skill.efn.broadblade.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.GRAY)
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
            .append(" ")
            .append(Component.translatable("skill.efn.broadblade.tooltip2"))
      );
      list.add(Component.translatable("skill.efn.broadblade.tooltip3").withStyle(ChatFormatting.BOLD));
      list.add(Component.translatable("skill.efn.broadblade.tooltip4").withStyle(ChatFormatting.BOLD));
      list.add(Component.translatable("skill.efn.broadblade.tooltip5").withStyle(ChatFormatting.BOLD));
      list.add(Component.translatable("skill.efn.broadblade.tooltip6").withStyle(ChatFormatting.BOLD));
      list.add(Component.translatable("skill.efn.broadblade.tooltip7").withStyle(ChatFormatting.BOLD));
      list.add(Component.translatable("skill.efn.broadblade.tooltip8").withStyle(ChatFormatting.BOLD));
      if (canUse) {
         list.add(Component.translatable("skill.efn.broadblade.tooltip9").withStyle(ChatFormatting.BOLD));
      } else {
         list.add(Component.translatable("skill.efn.broadblade.tooltip11").withStyle(ChatFormatting.BOLD));
      }

      if (ModList.get().isLoaded("efn_enhance")) {
         list.add(Component.translatable("skill.efn.broadblade.tooltip10").withStyle(ChatFormatting.BOLD));
      }

      return list;
   }
}
