package com.hm.efn.animations.types.yamato;

import net.minecraft.core.registries.BuiltInRegistries;

import com.hm.efn.EFNCommonConfig;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_passive.YamatoPassive;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent.SimpleEvent;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.registry.entries.EpicFightMobEffects;
import com.hm.efn.compat.epicfight.eventlistener.TakeDamageEvent.Attack;

public class YamatoAttackAnimation extends AvalonAttackAnimation {
   private void initYamatoProperties() {
      this.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])));
      this.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(120.0F));
      this.addAttackResultEvents(new AnimationAttackResultEvent[]{SimpleEvent.create(this::onAttackResult)});
   }

   public YamatoAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      Supplier<? extends StaticAnimation> hitAnimation,
      YamatoPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
      this.initYamatoProperties();
   }

   public YamatoAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature, play_speed, damageMulti);
      this.initYamatoProperties();
   }

   public YamatoAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
      this.initYamatoProperties();
   }

   public YamatoAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
      this.initYamatoProperties();
   }

   public YamatoAttackAnimation(
      float transitionTime, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
      this.initYamatoProperties();
   }

   public YamatoAttackAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
      this.initYamatoProperties();
   }

   protected void bindPhaseState(Phase phase) {
      float preDelay = phase.preDelay;
      this.stateSpectrumBlueprint
         .newTimePair(0.0F, preDelay)
         .addState(EntityState.PHASE_LEVEL, 1)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.SKILL_EXECUTABLE, true)
         .newTimePair(phase.start, phase.recovery + 0.25F)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .newTimePair(phase.start, phase.end)
         .addState(EntityState.INACTION, true)
         .newTimePair(phase.antic, phase.end)
         .addState(EntityState.TURNING_LOCKED, true)
         .newTimePair(preDelay, phase.contact)
         .addState(EntityState.ATTACKING, true)
         .addState(EntityState.PHASE_LEVEL, 2)
         .newTimePair(phase.contact, phase.end)
         .addState(EntityState.PHASE_LEVEL, 3);
   }

   private void onAttackResult(LivingEntityPatch<?> attackerPatch, Entity targetEntity, AttackResult attackResult) {
      LivingEntity livingTarget = this.getTrueEntity(targetEntity);
      if (livingTarget != null && livingTarget.isAlive()) {
         if (!(livingTarget instanceof Player)) {
            livingTarget.lookAt(Anchor.FEET, ((LivingEntity)attackerPatch.getOriginal()).position());
         }

         if (attackResult.resultType == ResultType.SUCCESS && !(attackResult.damage <= 0.0F)) {
            LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingTarget, LivingEntityPatch.class);
            if (targetPatch != null) {
               float elapsedTime = Objects.requireNonNull(attackerPatch.getAnimator().getPlayerFor(this.getAccessor())).getElapsedTime();
               if (this.getPhaseByTime(elapsedTime) instanceof YamatoPhase yamatoPhase) {
                  yamatoPhase.playHitAnimation(targetPatch, attackerPatch);
               }
            }

            this.applyYamatoStunToMonster(livingTarget);
         }
      }
   }

   private void applyYamatoStunToMonster(LivingEntity target) {
      if (target instanceof Monster && YamatoPassive.wasRecentlyParried(target)) {
         YamatoPassive.clearParryRecord(target);
         if (this.canBeStunnedByYamato(target)) {
            target.addEffect(
               new MobEffectInstance(
                  EFNMobEffectRegistry.STUN, (Integer)EFNCommonConfig.YAMATO_PARRY_STUN_DURATION.get(), 0, false, false, true
               )
            );
            Attack parryEvent = YamatoPassive.getParryEvent(target);
            if (parryEvent != null && parryEvent.getPlayerPatch() != null) {
               SkillContainer container = ((ServerPlayerPatch)parryEvent.getPlayerPatch()).getSkill(Yamato.yamato);
               if (container != null) {
                  container.getDataManager()
                     .setData(EFNSKillDataKeys.STUN_COOLDOWN, (Integer)EFNCommonConfig.YAMATO_PARRY_STUN_COOLDOWN.get());
               }
            }

            if (target.level() instanceof ServerLevel serverLevel) {
               LightningBolt lightningBolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(serverLevel);
               if (lightningBolt != null) {
                  lightningBolt.moveTo(target.getX(), target.getY(), target.getZ());
                  lightningBolt.setVisualOnly(true);
                  serverLevel.addFreshEntity(lightningBolt);
               }

               serverLevel.sendParticles(
                  ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), 15, 0.5, 0.5, 0.5, 0.05
               );
               serverLevel.playSound(
                  null,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  SoundEvents.LIGHTNING_BOLT_THUNDER,
                  SoundSource.WEATHER,
                  1.5F,
                  0.8F + serverLevel.random.nextFloat() * 0.4F
               );
               serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 1.2F, 1.0F);
            }
         }
      }
   }

   private boolean canBeStunnedByYamato(LivingEntity entity) {
      if (!entity.isInvulnerable()
         && !entity.hasEffect(EFNMobEffectRegistry.SIN_STUN_IMMUNITY)
         && !entity.hasEffect(EpicFightMobEffects.STUN_IMMUNITY)) {
         ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
         if (entityId == null) {
            return false;
         }

         String idString = entityId.toString();
         String modId = entityId.getNamespace();

         for (Object whitelistedObj : (List)EFNCommonConfig.YAMATO_GLOBALSTUN_WHITELIST.get()) {
            String whitelisted = whitelistedObj.toString();
            if (whitelisted.equals(modId + ":all") || whitelisted.equals(idString)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
