package com.hm.efn.util.yamato;

import com.hm.efn.EFNCommonConfig;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNScytheAnimations;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.animations.EFNStunAnimations;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.PartEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class DMC_V_JC_Server {
   public static void HandleAtk1(LivingEntityPatch<?> entityPatch) {
      if (!entityPatch.getCurrentlyActuallyHitEntities().isEmpty()) {
         entityPatch.getCurrentlyActuallyHitEntities().forEach(entity -> {
            if (isAttack(entity, entityPatch)) {
               entity.addEffect(new MobEffectInstance(EFNMobEffectRegistry.STOP, 58, 1));
            }
         });
      }
   }

   public static void post1(LivingEntityPatch<?> entityPatch) {
      playSound(entityPatch.getOriginal(), (SoundEvent)EFNSounds.DMC5_JC1.get());
   }

   public static void post2(LivingEntityPatch<?> entityPatch) {
   }

   public static void post3(LivingEntityPatch<?> entityPatch) {
   }

   private static double getDamageRate() {
      return (Double)EFNCommonConfig.JUDGEMENT_CUT_END_DAMAGE_RATE.get();
   }

   private static double getCutRate() {
      return (Double)EFNCommonConfig.JUDGEMENT_CUT_END_CUT_RATE.get();
   }

   private static double getBossDamageMultiplier() {
      return (Double)EFNCommonConfig.JUDGEMENT_CUT_END_BOSS_DAMAGE_MULTIPLIER.get();
   }

   private static double getHealthPercentage() {
      return (Double)EFNCommonConfig.JUDGEMENT_CUT_END_HEALTH_PERCENTAGE.get();
   }

   public static void postAttack(LivingEntityPatch<?> entityPatch) {
      List<LivingEntity> hitEntities = entityPatch.getCurrentlyActuallyHitEntities();
      if (!hitEntities.isEmpty()) {
         hitEntities.forEach(entity -> {
            if (isAttack(entity, entityPatch)) {
               HurtEntity(entityPatch, entity, EFNAnimations.DMC5_V_JC, (float)getDamageRate(), (float)getCutRate());
            }
         });
      }
   }

   public static void playSound(Entity entity, SoundEvent soundEvent) {
      entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, entity.getSoundSource(), 1.0F, 1.0F);
   }

   public static void HurtEntity(
      LivingEntityPatch<?> attacker, Entity target, AnimationAccessor<? extends AttackAnimation> animation, float damageRate, float cutRate
   ) {
      EpicFightDamageSource source = attacker.getDamageSource(animation, InteractionHand.MAIN_HAND);
      LivingEntity rootEntity = getTrueEntity(target);
      if (EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class) == null
         || !(
            ((LivingEntityPatch)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class)).getAnimator().getPlayerFor(null).getAnimation().get() instanceof DodgeAnimation
         )) {
         Level level = ((LivingEntity)attacker.getOriginal()).level();
         if (!level.isClientSide) {
            int prevInvulTime = target.invulnerableTime;
            target.invulnerableTime = 0;
            attacker.attack(source, target, InteractionHand.MAIN_HAND);
            if (rootEntity != null) {
               float dmg = cutRate * (rootEntity.getMaxHealth() * (float)getHealthPercentage());
               if (isBoss(rootEntity)) {
                  dmg *= (float)getBossDamageMultiplier();
               }

               target.invulnerableTime = 0;
               if (isBoss(rootEntity)) {
                  float newHealth = rootEntity.getHealth() - dmg;
                  if (newHealth <= 0.0F) {
                     rootEntity.setHealth(0.0F);
                     rootEntity.die(source);
                  } else {
                     rootEntity.setHealth(newHealth);
                     LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
                     if (livingEntityPatch != null && ((LivingEntity)livingEntityPatch.getOriginal()).isAlive()) {
                        livingEntityPatch.playAnimation(EFNStunAnimations.BIPED_HITUP_1, 0.0F);
                     }
                  }
               } else {
                  target.hurt(level.damageSources().generic(), dmg);
                  LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
                  if (livingEntityPatch != null
                     && ((LivingEntity)livingEntityPatch.getOriginal()).getHealth() > dmg
                     && ((LivingEntity)livingEntityPatch.getOriginal()).isAlive()) {
                     livingEntityPatch.playAnimation(EFNStunAnimations.BIPED_HITUP_1, 0.0F);
                  }
               }
            }

            target.invulnerableTime = prevInvulTime;
         }
      }
   }

   private static boolean isBoss(LivingEntity entity) {
      String className = entity.getClass().getName();
      return entity.getMaxHealth() >= 300.0F
         || className.contains("IABoss_monster")
         || className.contains("LLibrary_Boss_Monster")
         || className.contains("IABossMonsters")
         || className.contains("BossMonsters");
   }

   public static LivingEntity getTrueEntity(Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         return livingEntity;
      } else {
         return entity instanceof PartEntity<?> partEntity && partEntity.getParent() instanceof LivingEntity livingEntity ? livingEntity : null;
      }
   }

   public static void prev(LivingEntityPatch<?> ep) {
      ep.playSound((SoundEvent)EFNSounds.DMC5_JC0.get(), 1.0F, 1.0F, 1.0F);
      Level level = ((LivingEntity)ep.getOriginal()).level();
      double range = 20.0;

      for (Entity entity : level.getEntities(ep.getOriginal(), ((LivingEntity)ep.getOriginal()).getBoundingBox().inflate(range), entityx -> true)) {
         if (entity instanceof DoppelgangerEntity doppelganger && doppelganger.getOwner() != null && doppelganger.getOwner().equals(ep.getOriginal())) {
            ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(ep.getOriginal(), ServerPlayerPatch.class);
            serverPlayerPatch.playSound((SoundEvent)EFNSounds.DOPPELGANGER_CLOSE.get(), 1.0F, 1.0F, 1.0F);
            DoppelgangerEntity.spawnDarkParticles(((ServerPlayer)serverPlayerPatch.getOriginal()).serverLevel(), entity.position());
            entity.discard();
         }
      }

      updateDoppelgangerStatus(ep);
   }

   private static void updateDoppelgangerStatus(LivingEntityPatch<?> ep) {
      if (ep instanceof ServerPlayerPatch serverPlayerPatch) {
         SkillContainer innateContainer = serverPlayerPatch.getSkill(Yamato.yamato);
         if (innateContainer != null) {
            SkillDataManager data = innateContainer.getDataManager();
            if (data.hasData(EFNSKillDataKeys.HAVE_DOPPELGANGER)) {
               boolean hasDoppelganger = checkDoppelgangerExists(ep);
               data.setDataSync(EFNSKillDataKeys.HAVE_DOPPELGANGER, hasDoppelganger);
            }
         }
      }
   }

   private static boolean checkDoppelgangerExists(LivingEntityPatch<?> ep) {
      Level level = ((LivingEntity)ep.getOriginal()).level();
      double range = 20.0;

      for (Entity entity : level.getEntities(ep.getOriginal(), ((LivingEntity)ep.getOriginal()).getBoundingBox().inflate(range), entityx -> true)) {
         if (entity instanceof DoppelgangerEntity doppelganger && doppelganger.getOwner() != null && doppelganger.getOwner().equals(ep.getOriginal())) {
            return true;
         }
      }

      return false;
   }

   public static void post4(LivingEntityPatch<?> ep) {
      playSound(ep.getOriginal(), (SoundEvent)EFNSounds.DMC5_JC2.get());
   }

   private static boolean isAttack(LivingEntity target, LivingEntityPatch<?> attackerPatch) {
      if (target == null) {
         return false;
      }

      if (!target.isAlive()) {
         return false;
      }

      LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
      if (attacker == null) {
         return false;
      }

      if (target.equals(attacker)) {
         return false;
      }

      double attackRange = 24.0;
      if (target.distanceTo(attacker) > attackRange) {
         return false;
      }

      if (EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class) != null) {
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
         AssetAccessor<? extends DynamicAnimation> currentAnim = null;
         if (targetPatch != null) {
            currentAnim = Objects.requireNonNull(targetPatch.getAnimator().getPlayerFor(null)).getAnimation();
         }

         if (currentAnim != null
            && (
               ((DynamicAnimation)currentAnim.get()).equals(EFNSkillAnimations.EXECUTION)
                  || ((DynamicAnimation)currentAnim.get()).equals(EFNSkillAnimations.STOMP)
                  || ((DynamicAnimation)currentAnim.get()).equals(EFNScytheAnimations.SCYTHE_SCARLET_END)
            )) {
            return false;
         }
      }

      if (target instanceof OwnableEntity targetOwnable && targetOwnable.getOwner() != null && targetOwnable.getOwner().equals(attacker)) {
         return false;
      } else if (attacker instanceof OwnableEntity attackerOwnable && attackerOwnable.getOwner() != null && attackerOwnable.getOwner().equals(target)) {
         return false;
      } else if (target instanceof DoppelgangerEntity targetDoppelganger
         && targetDoppelganger.getOwner() != null
         && targetDoppelganger.getOwner().equals(attacker)) {
         return false;
      } else if (attacker instanceof DoppelgangerEntity attackerDoppelganger
         && attackerDoppelganger.getOwner() != null
         && attackerDoppelganger.getOwner().equals(target)) {
         return false;
      } else if (target instanceof TamableAnimal targetTameable
         && targetTameable.isTame()
         && targetTameable.getOwnerUUID() != null
         && attacker.getUUID().equals(targetTameable.getOwnerUUID())) {
         return false;
      } else if (attacker instanceof TamableAnimal attackerTameable
         && attackerTameable.isTame()
         && attackerTameable.getOwnerUUID() != null
         && target.getUUID().equals(attackerTameable.getOwnerUUID())) {
         return false;
      } else if (target instanceof IronGolem ironGolem && ironGolem.isPlayerCreated() && attacker instanceof Player) {
         return false;
      } else if (target instanceof DoppelgangerEntity targetDoppel
         && attacker instanceof DoppelgangerEntity attackerDoppel
         && targetDoppel.getOwner() != null
         && attackerDoppel.getOwner() != null
         && targetDoppel.getOwner().equals(attackerDoppel.getOwner())) {
         return false;
      } else {
         if (target instanceof DoppelgangerEntity doppel && attacker instanceof OwnableEntity && doppel.getOwner() != null) {
            OwnableEntity ownable = (OwnableEntity)attacker;
            if (doppel.getOwner().equals(ownable.getOwner())) {
               return false;
            }
         }

         return target.isInvulnerable() ? false : !(target instanceof Player targetPlayer && (targetPlayer.isCreative() || targetPlayer.isSpectator()));
      }
   }
}
