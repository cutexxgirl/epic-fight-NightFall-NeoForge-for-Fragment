package com.hm.efn.skill.guard;

import com.google.common.collect.Maps;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNStyles;
import com.hm.efn.gameasset.animations.EFNBroadBladeAnimations;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.particle.EFNParticles;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.joml.Vector3d;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.registry.entries.EpicFightSkillDataKeys;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.GuardSkill.BlockType;
import yesman.epicfight.skill.guard.GuardSkill.Builder;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;
import com.hm.efn.compat.epicfight.eventlistener.TakeDamageEvent.Attack;

public class EFNParryingSkill extends GuardSkill {
   private static final UUID EVENT_UUID = UUID.fromString("2f5f6a7d-7b4e-42a1-8e52-000000000001");
   private final Map<EntityType<?>, Float> entityHighImpactThresholds = Maps.newHashMap();
   private int ORIGINAL_PARRYWINDOW;
   private int SHAKEPENALTYMINWINDOW;
   private float SHAKEPENALTYMULTIPLIER;
   private int PARRY_DELAY_PROTECTION;
   private int PARRY_DIRECTION_RESET_TIME;
   private int SHAKEDETECTIONTIME;
   private int SHAKEDETECTIONCOUNT;
   private int SHAKEAUTORESETTIME;
   private float HIGH_IMPACT_THRESHOLD;

   public EFNParryingSkill(Builder builder) {
      super(builder);
   }

   public static Builder createActiveGuardBuilder() {
      return GuardSkill.createGuardBuilder(EFNParryingSkill::new)
         .addAdvancedGuardMotion(
            WeaponCategories.SWORD,
            (itemCap, playerpatch) -> List.of(
               EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3
            )
         )
         .addAdvancedGuardMotion(
            WeaponCategories.LONGSWORD,
            (itemCap, playerpatch) -> List.of(
               EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3
            )
         )
         .addAdvancedGuardMotion(
            WeaponCategories.UCHIGATANA,
            (itemCap, playerpatch) -> List.of(
               EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3
            )
         )
         .addAdvancedGuardMotion(
            WeaponCategories.TACHI,
            (itemCap, playerpatch) -> List.of(
               EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3
            )
         )
         .addAdvancedGuardMotion(
            WeaponCategories.GREATSWORD,
            (itemCap, playerpatch) -> List.of(
               EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3
            )
         )
         .addAdvancedGuardMotion(
            WeaponCategories.SPEAR,
            (itemCap, playerpatch) -> List.of(
               EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3
            )
         )
         .addAdvancedGuardMotion(
            WeaponCategories.AXE,
            (itemCap, playerpatch) -> List.of(
               EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3
            )
         )
         .addGuardMotion(
            WeaponCategories.LONGSWORD,
            (item, player) -> item.getStyle(player) == EFNStyles.BOARD_BLADE ? EFNBroadBladeAnimations.BROADBLADE_GUARD_HIT : Animations.LONGSWORD_GUARD_HIT
         );
   }

   private int getCurrentParryWindow(SkillContainer container) {
      int shakePenaltyCount = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.EFN_SHAKE_PENALTY_COUNT);
      if (shakePenaltyCount <= this.SHAKEDETECTIONCOUNT) {
         return this.ORIGINAL_PARRYWINDOW;
      }

      int effectivePenaltyCount = shakePenaltyCount - this.SHAKEDETECTIONCOUNT;
      int newParryWindow = (int)Math.floor(this.ORIGINAL_PARRYWINDOW * Math.pow(this.SHAKEPENALTYMULTIPLIER, effectivePenaltyCount));
      return Math.max(newParryWindow, this.SHAKEPENALTYMINWINDOW);
   }

   public void startHolding(SkillContainer container) {
      super.startHolding(container);
      if (!container.getExecutor().isLogicalClient()) {
         int currentTick = ((ServerPlayer)container.getServerExecutor().getOriginal()).tickCount;
         SkillDataManager dataManager = container.getDataManager();
         dataManager.setData(EFNSKillDataKeys.EFN_LAST_ACTIVE, currentTick);
         dataManager.setData(EFNSKillDataKeys.EFN_DELAY_PROTECTION_END, 0);
         int lastGuardStartTick = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_LAST_GUARD_START_TICK);
         int lastParryTime = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_LAST_PARRY_TIME);
         if (container.getExecutor().getTarget() != null && lastGuardStartTick > 0) {
            int timeSinceLastGuard = currentTick - lastGuardStartTick;
            boolean isQuickRetry = timeSinceLastGuard <= this.SHAKEDETECTIONTIME;
            boolean justParried = currentTick - lastParryTime <= this.SHAKEDETECTIONTIME;
            if (isQuickRetry && !justParried) {
               this.applyShakePenalty(container);
               dataManager.setData(EFNSKillDataKeys.EFN_SHAKE_DETECTED, true);
            }
         }

         dataManager.setData(EFNSKillDataKeys.EFN_LAST_GUARD_START_TICK, currentTick);
         dataManager.setData(EFNSKillDataKeys.EFN_SHAKE_DETECTED, false);
         this.checkAutoResetShakePenalty(container, currentTick);
         if (!FMLEnvironment.production
            && !container.getExecutor().isLogicalClient()
            && container.getExecutor().getOriginal() instanceof ServerPlayer serverPlayer) {
            int currentParryWindow = this.getCurrentParryWindow(container);
            int shakePenaltyCount = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_SHAKE_PENALTY_COUNT);
            if (shakePenaltyCount > this.SHAKEDETECTIONCOUNT) {
               int effectivePenalty = shakePenaltyCount - this.SHAKEDETECTIONCOUNT;
               float staminaMultiplier = 1.0F + effectivePenalty * 0.5F;
               String text = String.format("§c⚠️ 抖刀惩罚:层数: %d | 弹反窗口: %d帧 | 耐力消耗: x%.1f", effectivePenalty, currentParryWindow, staminaMultiplier);
               serverPlayer.displayClientMessage(Component.literal(text), true);
            } else if (shakePenaltyCount > 0) {
               String text = String.format("§e⚠️ 警告:当前抖刀次数: (%d/%d)", shakePenaltyCount, this.SHAKEDETECTIONCOUNT);
               serverPlayer.displayClientMessage(Component.literal(text), true);
            } else {
               String text = String.format("§a✅ 完美招架可用 | 招架窗口: %d帧", this.ORIGINAL_PARRYWINDOW);
               serverPlayer.displayClientMessage(Component.literal(text), true);
            }
         }
      }
   }

   public void resetHolding(SkillContainer container) {
      super.resetHolding(container);
      if (container != null && !container.getExecutor().isLogicalClient()) {
         int currentTick = ((ServerPlayer)container.getServerExecutor().getOriginal()).tickCount;
         int shakePenaltyCount = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.EFN_SHAKE_PENALTY_COUNT);
         if (shakePenaltyCount <= this.SHAKEDETECTIONCOUNT) {
            int delayProtectionEnd = currentTick + this.PARRY_DELAY_PROTECTION;
            container.getDataManager().setData(EFNSKillDataKeys.EFN_DELAY_PROTECTION_END, delayProtectionEnd);
         } else {
            container.getDataManager().setData(EFNSKillDataKeys.EFN_DELAY_PROTECTION_END, 0);
         }
      }
   }

   public void holdTick(SkillContainer container) {
      super.holdTick(container);
      if (container.getExecutor().isLogicalClient()) {
         boolean isGuardKeyDown = EpicFightKeyMappings.GUARD.isDown();
         if (!isGuardKeyDown) {
            container.getExecutor().cancelItemUse();
            ControlEngine controlEngine = ControlEngine.getInstance();
            if (controlEngine != null) {
               container.sendCancelRequest(container.getClientExecutor(), controlEngine);
            }
         }
      }
   }

   private void applyShakePenalty(SkillContainer container) {
      if (container.getExecutor().getTarget() != null) {
         SkillDataManager dataManager = container.getDataManager();
         int shakePenaltyCount = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_SHAKE_PENALTY_COUNT) + 1;
         dataManager.setData(EFNSKillDataKeys.EFN_SHAKE_PENALTY_COUNT, shakePenaltyCount);
         boolean isFirstShake = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.EFN_FIRST_SHAKE);
         if (isFirstShake) {
            dataManager.setData(EFNSKillDataKeys.EFN_FIRST_SHAKE, false);
         }
      }
   }

   private void resetShakePenalty(SkillContainer container) {
      SkillDataManager dataManager = container.getDataManager();
      dataManager.setData(EFNSKillDataKeys.EFN_SHAKE_PENALTY_COUNT, 0);
      dataManager.setData(EFNSKillDataKeys.EFN_SHAKE_DETECTED, false);
      dataManager.setData(EFNSKillDataKeys.EFN_FIRST_SHAKE, true);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, 1);
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, event -> {
         CapabilityItem itemCapability = ((ServerPlayerPatch)event.getPlayerPatch()).getHoldingItemCapability(InteractionHand.MAIN_HAND);
         int currentTick = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).tickCount;
         int delayProtectionEnd = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.EFN_DELAY_PROTECTION_END);
         boolean inDelayProtection = currentTick <= delayProtectionEnd;
         boolean shouldGuard = container.isActivated() && ((ServerPlayerPatch)event.getPlayerPatch()).getHoldingSkill() == this || inDelayProtection;
         if (shouldGuard) {
            DamageSource damageSource = event.getDamageSource();
            boolean isFront = false;
            Vec3 sourceLocation = damageSource.getSourcePosition();
            if (sourceLocation != null) {
               Vec3 viewVector = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).getViewVector(1.0F);
               viewVector = viewVector.subtract(0.0, viewVector.y, 0.0).normalize();
               Vec3 toSourceLocation = sourceLocation.subtract(((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).position()).normalize();
               if (toSourceLocation.dot(viewVector) > 0.0) {
                  isFront = true;
               }
            }

            if (isFront) {
               float impact = 0.5F;
               float knockback = 0.25F;
               if (event.getDamageSource() instanceof EpicFightDamageSource epicfightDamageSource) {
                  if (epicfightDamageSource.is(EpicFightDamageTypeTags.GUARD_PUNCTURE)) {
                     return;
                  }

                  impact = epicfightDamageSource.calculateImpact();
                  knockback += Math.min(impact * 0.1F, 1.0F);
               }

               this.guard(container, itemCapability, event, knockback, impact, false);
            }
         }
      }, 1);
   }

   public void guard(SkillContainer container, CapabilityItem itemCapability, Attack event, float knockback, float impact, boolean advanced) {
      if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.ADVANCED_GUARD)) {
         DamageSource damageSource = event.getDamageSource();
         if (this.isBlockableSource(damageSource, true)) {
            ServerPlayer serverPlayer = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
            int currentTick = serverPlayer.tickCount;
            int lastActiveTick = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.EFN_LAST_ACTIVE);
            int delayProtectionEnd = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.EFN_DELAY_PROTECTION_END);
            SkillDataManager dataManager = container.getDataManager();
            int currentParryWindow = this.getCurrentParryWindow(container);
            int timeSinceLastActive = currentTick - lastActiveTick;
            boolean inParryWindow = timeSinceLastActive < currentParryWindow;
            int shakePenaltyCount = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_SHAKE_PENALTY_COUNT);
            boolean isShakePeriod = shakePenaltyCount > this.SHAKEDETECTIONCOUNT;
            boolean inDelayProtection = !isShakePeriod && currentTick <= delayProtectionEnd;
            boolean successParrying = inParryWindow || inDelayProtection;
            boolean canGuardNormally = true;
            float penalty = (Float)container.getDataManager().getDataValue(EpicFightSkillDataKeys.PENALTY);
            EFNParryingSkill.AttackDirection currentAttackDir = EFNParryingSkill.AttackDirection.FRONT_ATTACK;
            if (successParrying) {
               event.setParried(true);
               ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EFNSounds.PARRY.get(), 0.5F, 0.0F, 0.0F);
               penalty = 0.1F;
               knockback *= 0.1F;
               dataManager.setData(EFNSKillDataKeys.EFN_LAST_ACTIVE, 0);
               dataManager.setData(EFNSKillDataKeys.EFN_DELAY_PROTECTION_END, 0);
               dataManager.setData(EFNSKillDataKeys.EFN_LAST_SUCCESSFUL_DEFENSE_TICK, currentTick);
               dataManager.setData(EFNSKillDataKeys.EFN_LAST_PARRY_TIME, currentTick);
               this.resetShakePenalty(container);
               if (damageSource.getDirectEntity() instanceof LivingEntity targetEntity) {
                  currentAttackDir = this.analyzeTargetColliderJoints(event.getPlayerPatch(), targetEntity);
               }
            } else {
               if (!canGuardNormally) {
                  return;
               }

               ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
               float basePenalizer = this.getPenalizer(itemCapability);
               if (isShakePeriod) {
                  int effectivePenaltyCount = shakePenaltyCount - this.SHAKEDETECTIONCOUNT;
                  basePenalizer *= 1.0F + effectivePenaltyCount * 0.5F;
               }

               penalty += basePenalizer;
               container.getDataManager().setDataSync(EpicFightSkillDataKeys.PENALTY, penalty);
               dataManager.setData(EFNSKillDataKeys.EFN_LAST_SUCCESSFUL_DEFENSE_TICK, currentTick);
            }

            if (damageSource.getDirectEntity() instanceof LivingEntity livingentity) {
               knockback = EnchantmentHelper.modifyKnockback(
                  (ServerLevel)livingentity.level(), livingentity.getItemInHand(livingentity.getUsedItemHand()), livingentity, damageSource, knockback
               );
            }

            if (damageSource.getDirectEntity() != null) {
               ((ServerPlayerPatch)event.getPlayerPatch()).knockBackEntity(damageSource.getDirectEntity().position(), knockback);
            } else {
               ((ServerPlayerPatch)event.getPlayerPatch()).knockBackEntity(serverPlayer.position(), knockback);
            }

            float consumeAmount = penalty * impact;
            boolean canAfford = ((ServerPlayerPatch)event.getPlayerPatch()).consumeForSkill(this, Resource.STAMINA, consumeAmount);
            BlockType blockType;
            if (successParrying) {
               blockType = BlockType.ADVANCED_GUARD;
            } else if (canGuardNormally && canAfford) {
               blockType = BlockType.GUARD;
            } else {
               blockType = BlockType.GUARD_BREAK;
            }

            float currentHighImpactThreshold = this.HIGH_IMPACT_THRESHOLD;
            if (damageSource.getDirectEntity() instanceof LivingEntity livingEntity) {
               currentHighImpactThreshold = this.getHighImpactThresholdForEntity(livingEntity);
            }

            AnimationAccessor<? extends StaticAnimation> animation = this.getGuardMotion(
               container, event.getPlayerPatch(), itemCapability, blockType, impact, currentHighImpactThreshold, currentAttackDir
            );
            if (animation != null) {
               ((ServerPlayerPatch)event.getPlayerPatch()).playAnimationSynchronized(animation, 0.0F);
               if (successParrying && damageSource.getDirectEntity() != null) {
                  this.spawnParryFlashParticle(serverPlayer, damageSource.getDirectEntity(), animation);
               }
            }

            if (blockType == BlockType.GUARD_BREAK) {
               ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EpicFightSounds.NEUTRALIZE_MOBS.get(), 3.0F, 0.0F, 0.1F);
            }

            this.dealEvent(event.getPlayerPatch(), event.unwrap(), advanced);
            return;
         }
      }

      super.guard(container, itemCapability, (ServerPlayerPatch)event.getPlayerPatch(), event.unwrap(), knockback, impact, false);
   }

   private void spawnParryFlashParticle(ServerPlayer serverPlayer, Entity target, AnimationAccessor<? extends StaticAnimation> animation) {
      Vector3d particleArgs = this.getParticleArgumentsForAnimation(animation);
      ((HitParticleType)EFNParticles.EFN_PARRY_FLASH_MAIN.get())
         .spawnParticleWithArgument(
            serverPlayer.serverLevel(),
            (player, entity) -> this.getParticlePositionForAnimation(player, entity, animation),
            (player, entity) -> particleArgs,
            serverPlayer,
            target
         );
      ((HitParticleType)EFNParticles.ALL_SPARK.get())
         .spawnParticleWithArgument(
            serverPlayer.serverLevel(),
            (player, entity) -> this.getParticlePositionForAnimation(player, entity, animation),
            HitParticleType.ZERO,
            serverPlayer,
            target
         );
   }

   private Vector3d getParticlePositionForAnimation(Entity player, Entity target, AnimationAccessor<? extends StaticAnimation> animation) {
      Vec3 lookVec = player.getLookAngle();
      Vec3 playerPos = player.position().add(0.0, player.getBbHeight() * 0.6, 0.0);
      Vec3 targetPos = target.position().add(0.0, target.getBbHeight() * 0.6, 0.0);
      Vec3 middlePos = playerPos.add(targetPos.subtract(playerPos).scale(0.5));
      Vec3 toMiddle = middlePos.subtract(playerPos);
      double distanceSqr = toMiddle.lengthSqr();
      double maxDistance = 1.0;
      Vec3 limitedMiddlePos;
      if (distanceSqr > maxDistance * maxDistance) {
         Vec3 direction = toMiddle.normalize();
         limitedMiddlePos = playerPos.add(direction.scale(maxDistance));
      } else {
         limitedMiddlePos = middlePos;
      }

      Vector3d basePosition = new Vector3d(limitedMiddlePos.x, limitedMiddlePos.y, limitedMiddlePos.z);
      if (animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1) {
         Vec3 leftOffset = new Vec3(lookVec.z, 0.0, -lookVec.x).scale(0.2);
         Vector3d finalPos = new Vector3d(basePosition.x + leftOffset.x, basePosition.y, basePosition.z + leftOffset.z);
         return this.limitDistanceFromPlayer(playerPos, finalPos, maxDistance);
      } else if (animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2) {
         Vec3 rightOffset = new Vec3(-lookVec.z, 0.0, lookVec.x).scale(0.2);
         Vector3d finalPos = new Vector3d(basePosition.x + rightOffset.x, basePosition.y, basePosition.z + rightOffset.z);
         return this.limitDistanceFromPlayer(playerPos, finalPos, maxDistance);
      } else {
         return basePosition;
      }
   }

   private Vector3d limitDistanceFromPlayer(Vec3 playerPos, Vector3d particlePos, double maxDistance) {
      Vec3 toParticle = new Vec3(particlePos.x, particlePos.y, particlePos.z).subtract(playerPos);
      if (toParticle.lengthSqr() > maxDistance * maxDistance) {
         Vec3 direction = toParticle.normalize();
         Vec3 limitedPos = playerPos.add(direction.scale(maxDistance));
         return new Vector3d(limitedPos.x, limitedPos.y, limitedPos.z);
      } else {
         return particlePos;
      }
   }

   private Vector3d getParticleArgumentsForAnimation(AnimationAccessor<? extends StaticAnimation> animation) {
      if (animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1) {
         return new Vector3d(1.0, -0.6, 0.0);
      } else if (animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2) {
         return new Vector3d(1.0, 0.6, 0.0);
      } else {
         return animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3 ? new Vector3d(1.2, 0.7, 0.0) : new Vector3d(1.0, 0.0, 0.0);
      }
   }

   private EFNParryingSkill.AttackDirection analyzeTargetColliderJoints(PlayerPatch<?> playerPatch, LivingEntity targetEntity) {
      try {
         EntityPatch<?> targetPatch = EpicFightCapabilities.getEntityPatch(targetEntity, EntityPatch.class);
         if (targetPatch instanceof LivingEntityPatch<?> livingTargetPatch) {
            AnimationPlayer animPlayer = livingTargetPatch.getAnimator().getPlayerFor(null);
            if (animPlayer != null) {
               DynamicAnimation currentAnimation = (DynamicAnimation)animPlayer.getAnimation().get();
               if (currentAnimation instanceof AttackAnimation attackAnimation) {
                  float elapsedTime = animPlayer.getElapsedTime();
                  Phase currentPhase = attackAnimation.getPhaseByTime(elapsedTime);
                  JointColliderPair[] colliderPairs = currentPhase.getColliders();
                  JointColliderPair[] var11 = colliderPairs;
                  int var12 = var11.length;
                  byte var13 = 0;
                  if (var13 < var12) {
                     JointColliderPair pair = var11[var13];
                     Joint colliderJoint = (Joint)pair.getFirst();
                     return this.determineJointAttackSide(playerPatch, livingTargetPatch, attackAnimation, colliderJoint, elapsedTime, targetEntity);
                  }
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return EFNParryingSkill.AttackDirection.FRONT_ATTACK;
   }

   private EFNParryingSkill.AttackDirection determineJointAttackSide(
      PlayerPatch<?> playerPatch,
      LivingEntityPatch<?> targetPatch,
      AttackAnimation attackAnimation,
      Joint colliderJoint,
      float elapsedTime,
      LivingEntity targetEntity
   ) {
      Vec3 playerPos = ((Player)playerPatch.getOriginal()).position();
      Vec3 playerLookVec = ((Player)playerPatch.getOriginal()).getViewVector(1.0F);
      float totalTime = attackAnimation.getTotalTime();
      float startTime = Math.max(elapsedTime - 0.3F, 0.0F);
      float endTime = Math.min(elapsedTime + 0.2F, totalTime);
      float[] sampleTimes = new float[]{
         startTime,
         Math.max(elapsedTime - 0.2F, 0.0F),
         Math.max(elapsedTime - 0.1F, 0.0F),
         elapsedTime - 0.05F,
         elapsedTime,
         elapsedTime + 0.05F,
         elapsedTime + 0.1F,
         endTime
      };
      Vec3[] samplePositions = new Vec3[sampleTimes.length];

      for (int i = 0; i < sampleTimes.length; i++) {
         samplePositions[i] = AvalonAnimationUtils.getJointWorldRawPos(targetPatch, colliderJoint, sampleTimes[i]);
      }

      Vec3 overallMovement = samplePositions[samplePositions.length - 1].subtract(samplePositions[0]);
      if (overallMovement.lengthSqr() < 1.0E-4) {
         Vec3 currentPos = samplePositions[4];
         Vec3 toJoint = currentPos.subtract(playerPos);
         return this.determineStaticPositionSide(playerLookVec, toJoint);
      } else {
         EFNParryingSkill.AttackType thrustResult = this.determineIfThrustAttackExtended(
            overallMovement, playerLookVec, samplePositions, sampleTimes, targetEntity
         );
         return thrustResult != null
            ? EFNParryingSkill.AttackDirection.FRONT_ATTACK
            : this.determineSideByCrossProduct(playerLookVec, samplePositions[0], samplePositions[samplePositions.length - 1]);
      }
   }

   private EFNParryingSkill.AttackType determineIfThrustAttackExtended(
      Vec3 movement, Vec3 playerLookVec, Vec3[] samplePositions, float[] sampleTimes, LivingEntity targetEntity
   ) {
      Vec3 movementDirection = movement.normalize();
      Vec3 playerLookHorizontal = new Vec3(playerLookVec.x, 0.0, playerLookVec.z).normalize();
      Vec3 movementHorizontal = new Vec3(movementDirection.x, 0.0, movementDirection.z).normalize();
      double dotProduct = movementHorizontal.dot(playerLookHorizontal);
      double movementLengthSqr = movement.lengthSqr();
      double stability = this.calculateMovementStability(samplePositions, playerLookHorizontal);
      double forwardDominance = this.calculateForwardDominance(samplePositions, playerLookHorizontal);
      double verticalRatio = this.calculateVerticalRatio(movement, samplePositions);
      boolean isTargetAirborne = this.isTargetAirborne(targetEntity, samplePositions);
      if (verticalRatio > 0.9) {
         return EFNParryingSkill.AttackType.VERTICAL_SLAM;
      }

      boolean isThrust = false;
      double verticalThreshold = isTargetAirborne ? 0.4 : 0.3;
      if (verticalRatio > verticalThreshold) {
         return null;
      }

      boolean hasGoodDirection = Math.abs(dotProduct) > 0.75;
      boolean hasGoodStability = stability > 0.8;
      boolean hasGoodForwardDominance = forwardDominance > 0.7;
      boolean hasReasonableDistance = movementLengthSqr > 0.64;
      if (isTargetAirborne) {
         hasGoodForwardDominance = forwardDominance > 0.6;
         hasGoodStability = stability > 0.7;
      }

      if (hasGoodDirection && hasGoodStability && hasGoodForwardDominance && hasReasonableDistance) {
         double thrustScore = Math.abs(dotProduct) * 0.25 + stability * 0.35 + forwardDominance * 0.3;
         double scoreThreshold = isTargetAirborne ? 0.75 : 0.78;
         isThrust = thrustScore > scoreThreshold;
         if (stability > 0.95 && forwardDominance > 0.8) {
            isThrust = true;
         }

         if (Math.abs(dotProduct) > 0.99 && stability > 0.8) {
            isThrust = true;
         }
      }

      if (stability < 0.6 || forwardDominance < 0.5) {
         isThrust = false;
      }

      if (isThrust) {
         return dotProduct > 0.0 ? EFNParryingSkill.AttackType.THRUST_FRONT : EFNParryingSkill.AttackType.THRUST_BACK;
      } else {
         return null;
      }
   }

   private boolean isTargetAirborne(LivingEntity targetEntity, Vec3[] samplePositions) {
      if (targetEntity == null) {
         return false;
      } else {
         return !targetEntity.onGround() ? true : this.analyzeAirborneFromTrajectory(samplePositions);
      }
   }

   private boolean analyzeAirborneFromTrajectory(Vec3[] samplePositions) {
      if (samplePositions.length < 3) {
         return false;
      }

      int peakIndex = this.findPeakIndex(samplePositions);
      if (peakIndex > 0 && peakIndex < samplePositions.length - 1) {
         double riseHeight = samplePositions[peakIndex].y - samplePositions[0].y;
         double fallHeight = samplePositions[peakIndex].y - samplePositions[samplePositions.length - 1].y;
         if (riseHeight > 0.3 && fallHeight > 0.2) {
            return true;
         }
      }

      double minY = samplePositions[0].y;
      double maxY = samplePositions[0].y;

      for (Vec3 pos : samplePositions) {
         minY = Math.min(minY, pos.y);
         maxY = Math.max(maxY, pos.y);
      }

      return maxY - minY > 1.0;
   }

   private int findPeakIndex(Vec3[] positions) {
      int peakIndex = 0;
      double maxHeight = positions[0].y;

      for (int i = 1; i < positions.length; i++) {
         if (positions[i].y > maxHeight) {
            maxHeight = positions[i].y;
            peakIndex = i;
         }
      }

      return peakIndex;
   }

   private double calculateVerticalRatio(Vec3 overallMovement, Vec3[] samplePositions) {
      if (samplePositions.length < 2) {
         return 0.0;
      }

      double maxVerticalMovement = 0.0;
      double totalVerticalVariation = 0.0;

      for (int i = 1; i < samplePositions.length; i++) {
         double verticalChange = Math.abs(samplePositions[i].y - samplePositions[i - 1].y);
         maxVerticalMovement = Math.max(maxVerticalMovement, verticalChange);
         totalVerticalVariation += verticalChange;
      }

      double totalHeightChange = Math.abs(samplePositions[samplePositions.length - 1].y - samplePositions[0].y);
      double averageVerticalVariation = totalVerticalVariation / (samplePositions.length - 1);
      double verticalScore = maxVerticalMovement * 0.4 + averageVerticalVariation * 0.3 + totalHeightChange * 0.3;
      return Math.min(verticalScore / 2.0, 1.0);
   }

   private double calculateMovementStability(Vec3[] positions, Vec3 referenceDirection) {
      if (positions.length < 3) {
         return 1.0;
      }

      double totalStability = 0.0;
      int validSegments = 0;

      for (int i = 1; i < positions.length; i++) {
         Vec3 segmentMovement = positions[i].subtract(positions[i - 1]);
         if (segmentMovement.lengthSqr() > 1.0E-4) {
            Vec3 segmentDirection = segmentMovement.normalize();
            Vec3 segmentDirectionHorizontal = new Vec3(segmentDirection.x, 0.0, segmentDirection.z).normalize();
            double consistency = Math.abs(segmentDirectionHorizontal.dot(referenceDirection));
            totalStability += consistency;
            validSegments++;
         }
      }

      return validSegments > 0 ? totalStability / validSegments : 1.0;
   }

   private double calculateForwardDominance(Vec3[] positions, Vec3 forwardDirection) {
      if (positions.length < 2) {
         return 1.0;
      }

      double totalForward = 0.0;
      double totalLateral = 0.0;

      for (int i = 1; i < positions.length; i++) {
         Vec3 segment = positions[i].subtract(positions[i - 1]);
         if (segment.lengthSqr() > 1.0E-4) {
            Vec3 segmentHorizontal = new Vec3(segment.x, 0.0, segment.z);
            double forwardComponent = segmentHorizontal.dot(forwardDirection);
            totalForward += Math.abs(forwardComponent);
            Vec3 lateralComponent = segmentHorizontal.subtract(forwardDirection.scale(forwardComponent));
            totalLateral += lateralComponent.length();
         }
      }

      double totalMovement = totalForward + totalLateral;
      return totalMovement > 0.0 ? totalForward / totalMovement : 1.0;
   }

   private EFNParryingSkill.AttackDirection determineSideByCrossProduct(Vec3 playerLookVec, Vec3 startPos, Vec3 endPos) {
      Vec3 movement = endPos.subtract(startPos);
      Vec3 movementDirection = movement.normalize();
      Vec3 playerLookHorizontal = new Vec3(playerLookVec.x, 0.0, playerLookVec.z).normalize();
      Vec3 movementHorizontal = new Vec3(movementDirection.x, 0.0, movementDirection.z).normalize();
      double crossY = playerLookHorizontal.x * movementHorizontal.z - playerLookHorizontal.z * movementHorizontal.x;
      double dotProduct = playerLookHorizontal.dot(movementHorizontal);
      double adjustedThreshold = Math.abs(dotProduct) > 0.9 ? 0.1 : 0.15;
      if (crossY > adjustedThreshold) {
         return EFNParryingSkill.AttackDirection.LEFT_ATTACK;
      } else if (crossY < -adjustedThreshold) {
         return EFNParryingSkill.AttackDirection.RIGHT_ATTACK;
      } else if (crossY > adjustedThreshold * 0.5) {
         return EFNParryingSkill.AttackDirection.LEFT_SLIGHT_ATTACK;
      } else {
         return crossY < -adjustedThreshold * 0.5 ? EFNParryingSkill.AttackDirection.RIGHT_SLIGHT_ATTACK : EFNParryingSkill.AttackDirection.FRONT_ATTACK;
      }
   }

   private EFNParryingSkill.AttackDirection determineStaticPositionSide(Vec3 playerLookVec, Vec3 toJoint) {
      Vec3 playerLookHorizontal = new Vec3(playerLookVec.x, 0.0, playerLookVec.z).normalize();
      Vec3 toJointHorizontal = new Vec3(toJoint.x, 0.0, toJoint.z).normalize();
      double crossY = playerLookHorizontal.x * toJointHorizontal.z - playerLookHorizontal.z * toJointHorizontal.x;
      if (crossY > 0.05) {
         return EFNParryingSkill.AttackDirection.LEFT_SIDE;
      } else {
         return crossY < -0.05 ? EFNParryingSkill.AttackDirection.RIGHT_SIDE : EFNParryingSkill.AttackDirection.FRONT_SIDE;
      }
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().isLogicalClient()) {
         int currentTick = ((ServerPlayer)container.getServerExecutor().getOriginal()).tickCount;
         SkillDataManager dataManager = container.getDataManager();
         int delayProtectionEnd = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_DELAY_PROTECTION_END);
         if (delayProtectionEnd > 0 && currentTick > delayProtectionEnd) {
            dataManager.setData(EFNSKillDataKeys.EFN_DELAY_PROTECTION_END, 0);
         }

         int lastParryTime = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_LAST_PARRY_TIME);
         if (currentTick - lastParryTime > this.PARRY_DIRECTION_RESET_TIME) {
            dataManager.setData(EFNSKillDataKeys.EFN_STARTING_PARRY, -1);
            dataManager.setData(EFNSKillDataKeys.EFN_PARRY_MOTION_COUNTER, 0);
         }

         this.checkAutoResetShakePenalty(container, currentTick);
      }
   }

   private void checkAutoResetShakePenalty(SkillContainer container, int currentTick) {
      SkillDataManager dataManager = container.getDataManager();
      int shakePenaltyCount = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_SHAKE_PENALTY_COUNT);
      if (shakePenaltyCount > 0) {
         int lastGuardStartTick = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_LAST_GUARD_START_TICK);
         int timeSinceLastShake = currentTick - lastGuardStartTick;
         if (timeSinceLastShake >= this.SHAKEAUTORESETTIME) {
            this.resetShakePenalty(container);
         }
      }
   }

   public boolean isExecutableState(PlayerPatch<?> executor) {
      return executor.isEpicFightMode() && executor.getEntityState().canUseSkill() && !executor.isHoldingAny();
   }

   protected boolean isBlockableSource(DamageSource damageSource, boolean advanced) {
      return damageSource.is(DamageTypeTags.IS_PROJECTILE) && advanced
         || !damageSource.is(EpicFightDamageTypeTags.UNBLOCKALBE)
            && !damageSource.is(DamageTypes.MAGIC)
            && !damageSource.is(DamageTypeTags.IS_FIRE);
   }

   @Nullable
   protected AnimationAccessor<? extends StaticAnimation> getGuardMotion(
      SkillContainer container,
      PlayerPatch<?> playerpatch,
      CapabilityItem itemCapability,
      BlockType blockType,
      float impact,
      float highImpactThreshold,
      EFNParryingSkill.AttackDirection attackDirection
   ) {
      AnimationAccessor<? extends StaticAnimation> animation = itemCapability.getGuardMotion(this, blockType, playerpatch);
      if (animation != null) {
         return animation;
      }

      if (blockType == BlockType.ADVANCED_GUARD) {
         Object motion = this.getGuardMotionMap(blockType)
            .getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null)
            .apply(itemCapability, playerpatch);
         if (motion instanceof List<?> motions) {
            @SuppressWarnings("unchecked")
            List<AnimationAccessor<? extends StaticAnimation>> typedMotions = (List<AnimationAccessor<? extends StaticAnimation>>)motions;
            if (impact >= highImpactThreshold && typedMotions.size() >= 3) {
               return typedMotions.get(2);
            }

            return this.selectGuardAnimationBasedOnNewLogic(container, typedMotions, attackDirection);
         }
      }

      return super.getGuardMotion(container, playerpatch, itemCapability, blockType);
   }

   private AnimationAccessor<? extends StaticAnimation> selectGuardAnimationBasedOnNewLogic(
      SkillContainer container, List<AnimationAccessor<? extends StaticAnimation>> motions, EFNParryingSkill.AttackDirection attackDirection
   ) {
      if (motions.size() < 2) {
         return motions.get(0);
      } else {
         SkillDataManager dataManager = container.getDataManager();
         int currentTick = ((ServerPlayer)container.getServerExecutor().getOriginal()).tickCount;
         dataManager.setData(EFNSKillDataKeys.EFN_LAST_PARRY_TIME, currentTick);
         AnimationAccessor<? extends StaticAnimation> leftGuard = motions.get(0);
         AnimationAccessor<? extends StaticAnimation> rightGuard = motions.get(1);
         int startingParry = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_STARTING_PARRY);
         int parryCounter = (Integer)dataManager.getDataValue(EFNSKillDataKeys.EFN_PARRY_MOTION_COUNTER);
         if (startingParry != -1 && parryCounter != 0) {
            boolean useLeftGuard = startingParry == 0 == (parryCounter % 2 == 0);
            dataManager.setData(EFNSKillDataKeys.EFN_PARRY_MOTION_COUNTER, parryCounter + 1);
            return useLeftGuard ? leftGuard : rightGuard;
         } else {
            boolean useLeftGuard = this.shouldUseLeftGuardForAttack(attackDirection);
            startingParry = useLeftGuard ? 0 : 1;
            dataManager.setData(EFNSKillDataKeys.EFN_STARTING_PARRY, startingParry);
            dataManager.setData(EFNSKillDataKeys.EFN_PARRY_MOTION_COUNTER, 1);
            return startingParry == 0 ? leftGuard : rightGuard;
         }
      }
   }

   private boolean shouldUseLeftGuardForAttack(EFNParryingSkill.AttackDirection attackDirection) {
      return switch (attackDirection) {
         case LEFT_ATTACK, LEFT_SLIGHT_ATTACK, LEFT_SIDE -> true;
         case RIGHT_ATTACK, RIGHT_SLIGHT_ATTACK, RIGHT_SIDE -> false;
         default -> Math.random() < 0.5;
      };
   }

   @Nullable
   protected AnimationAccessor<? extends StaticAnimation> getGuardMotion(
      SkillContainer container, PlayerPatch<?> playerpatch, CapabilityItem itemCapability, BlockType blockType, float impact
   ) {
      return this.getGuardMotion(
         container, playerpatch, itemCapability, blockType, impact, this.HIGH_IMPACT_THRESHOLD, EFNParryingSkill.AttackDirection.FRONT_ATTACK
      );
   }

   @Nullable
   protected AnimationAccessor<? extends StaticAnimation> getGuardMotion(
      SkillContainer container, PlayerPatch<?> playerpatch, CapabilityItem itemCapability, BlockType blockType
   ) {
      return this.getGuardMotion(
         container, playerpatch, itemCapability, blockType, 0.0F, this.HIGH_IMPACT_THRESHOLD, EFNParryingSkill.AttackDirection.FRONT_ATTACK
      );
   }

   private float getHighImpactThresholdForEntity(LivingEntity entity) {
      return entity == null ? this.HIGH_IMPACT_THRESHOLD : this.entityHighImpactThresholds.getOrDefault(entity.getType(), this.HIGH_IMPACT_THRESHOLD);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.entityHighImpactThresholds.clear();
      int PARRY_WINDOW = parameters.getInt("parry_window");
      if (PARRY_WINDOW <= 0) {
         PARRY_WINDOW = 6;
      }

      this.ORIGINAL_PARRYWINDOW = PARRY_WINDOW;
      this.HIGH_IMPACT_THRESHOLD = parameters.getFloat("high_impact_threshold");
      if (this.HIGH_IMPACT_THRESHOLD <= 0.0F) {
         this.HIGH_IMPACT_THRESHOLD = 4.0F;
      }

      this.PARRY_DELAY_PROTECTION = parameters.getInt("parry_delay_protection");
      if (this.PARRY_DELAY_PROTECTION <= 0) {
         this.PARRY_DELAY_PROTECTION = 2;
      }

      this.PARRY_DIRECTION_RESET_TIME = parameters.getInt("parry_direction_reset_time");
      if (this.PARRY_DIRECTION_RESET_TIME <= 0) {
         this.PARRY_DIRECTION_RESET_TIME = 40;
      }

      this.SHAKEPENALTYMINWINDOW = parameters.getInt("shake_penalty_min_window");
      if (this.SHAKEPENALTYMINWINDOW <= 0) {
         this.SHAKEPENALTYMINWINDOW = 1;
      }

      this.SHAKEPENALTYMULTIPLIER = parameters.getFloat("shake_penalty_multiplier");
      if (this.SHAKEPENALTYMULTIPLIER <= 0.0F) {
         this.SHAKEPENALTYMULTIPLIER = 0.5F;
      }

      this.SHAKEDETECTIONTIME = parameters.getInt("shake_detection_time");
      if (this.SHAKEDETECTIONTIME <= 0) {
         this.SHAKEDETECTIONTIME = 10;
      }

      if (parameters.contains("shake_detection_count")) {
         this.SHAKEDETECTIONCOUNT = parameters.getInt("shake_detection_count");
      } else {
         this.SHAKEDETECTIONCOUNT = 2;
      }

      this.SHAKEAUTORESETTIME = parameters.getInt("shake_auto_reset_time");
      if (this.SHAKEAUTORESETTIME <= 0) {
         this.SHAKEAUTORESETTIME = 20;
      }

      CompoundTag entityThresholds = parameters.getCompound("entity_high_impact_thresholds");

      for (String registryName : entityThresholds.getAllKeys()) {
         EntityType<?> entityType = (EntityType<?>)EntityType.byString(registryName).orElse(null);
         if (entityType != null) {
            float threshold = entityThresholds.getFloat(registryName);
            this.entityHighImpactThresholds.put(entityType, threshold);
         }
      }
   }

   public Skill getPriorSkill() {
      return EpicFightSkills.GUARD.get();
   }

   protected boolean isAdvancedGuard() {
      return true;
   }

   public Set<WeaponCategory> getAvailableWeaponCategories() {
      return this.advancedGuardMotions.keySet();
   }

   public enum AttackDirection {
      LEFT_ATTACK("left_attack"),
      RIGHT_ATTACK("right_attack"),
      LEFT_SLIGHT_ATTACK("left_slight_attack"),
      RIGHT_SLIGHT_ATTACK("right_slight_attack"),
      FRONT_ATTACK("front_attack"),
      LEFT_SIDE("left_side"),
      RIGHT_SIDE("right_side"),
      FRONT_SIDE("front_side");

      private final String displayName;

      AttackDirection(String displayName) {
         this.displayName = displayName;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }

   public enum AttackType {
      THRUST_FRONT("thrust_front"),
      THRUST_BACK("thrust_back"),
      VERTICAL_SLAM("vertical_slam"),
      NONE("none");

      private final String displayName;

      AttackType(String displayName) {
         this.displayName = displayName;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }
}
