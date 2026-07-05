package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.animations.types.murasama.ZansetsuAttackAnimation;
import com.hm.efn.client.input.keymapping.EFNKeyMappings;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.hm.efn.gameasset.animations.EFNZansetsuAnimations;
import com.hm.efn.gameasset.combos.Murasama;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.gameassets.InvincibleSkillDataKeys;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.PlayerInputState;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.DealDamageEvent.Hurt;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class MurasamaInnate extends EFNWeaponInnateBase {
   private static final UUID DODGE_TRIGGER_UUID = UUID.fromString("e5f6a1b2-c8d1-11ed-a51b-0242ac120219");
   private static final UUID DODGE_SUCCESS_UUID = UUID.fromString("e5f6a1b2-c8d1-33ed-a51b-0242ac120219");
   private static final UUID DODGE_CANCEL_UUID = UUID.fromString("e5f6a1b2-c8d1-22ed-a52b-0242ac120219");
   private static final UUID ATTACK_COUNT_UUID = UUID.fromString("e5f6a1b2-c8d1-44ed-a52b-0242ac120219");
   private static final UUID INPUT_RESET_UUID = UUID.fromString("e5f6a1b2-c8d1-11ed-a51b-0342ac120219");
   private static final UUID PARRY_CHARGE_UUID = UUID.fromString("a1b2c3d4-5678-90ef-1145-56789abcdef1");
   private static final UUID GUARD_STAMINA_UUID = UUID.fromString("0721c6de-4074-11ee-be56-1242ac120011");
   private static final UUID LOCK_MOVEMENT_UUID = UUID.fromString("0721c6de-4074-22ee-be56-1252ac120011");
   private static final Vec2f[] CLOCK_POS = new Vec2f[]{
      new Vec2f(0.5F, 0.5F), new Vec2f(0.5F, 0.0F), new Vec2f(0.0F, 0.0F), new Vec2f(0.0F, 1.0F), new Vec2f(1.0F, 1.0F), new Vec2f(1.0F, 0.0F)
   };
   private int zandatsu_slash_count_require;
   private float zandatsu_target_health_require;

   public MurasamaInnate(Builder builder) {
      super(builder);
   }

   private static void StunTarget(Hurt event) {
      LivingEntity target = event.getTarget();
      if (target != null) {
         target.addEffect(new MobEffectInstance(EFNMobEffectRegistry.VERTICALSTOP, 40, 0, false, false, false));
         target.addEffect(new MobEffectInstance(EFNMobEffectRegistry.HORIZONTAL_STOP, 40, 0, false, false, false));
         target.addEffect(new MobEffectInstance(EFNMobEffectRegistry.STUN, 40, 0, false, false, false));
         target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, false));
      }
   }

   private static void zansetsuTimer(SkillContainer container) {
      SkillDataManager manager = container.getDataManager();
      if (manager != null) {
         if (manager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER)) {
            int current = (Integer)manager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER);
            if (current > 0) {
               manager.setData(EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER, current - 1);
               if (current - 1 == 0 && !container.getExecutor().isLogicalClient()) {
                  manager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED, false);
                  manager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE, false);
                  manager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE, false);
                  manager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER, 0);
                  SkillContainer zansetsuSkill = container.getExecutor().getSkill(EFNSkills.ZANSETSU);
                  if (zansetsuSkill != null) {
                     zansetsuSkill.getDataManager().setDataSync(EFNSKillDataKeys.ARTS_KEY, false);
                  }
               }
            }
         }
      }
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.zandatsu_slash_count_require = parameters.getInt("zandatsu_slash_count_require");
      this.zandatsu_target_health_require = parameters.getFloat("zandatsu_target_health_require");
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      SkillDataManager data = container.getDataManager();
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.SKILL_CAST_EVENT,
         DODGE_TRIGGER_UUID,
         event -> {
            if (EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.DODGE)
               && container.getExecutor() instanceof ServerPlayerPatch playerPatch) {
               ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
               if (this.isHoldingWeapon(container)) {
                  InvincibleAttachments.getPlayer(player).setCurrentNode(Murasama.DodgeCounter);
               }
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.SKILL_CONSUME_EVENT, GUARD_STAMINA_UUID, event -> {
         if (event.getSkill() instanceof GuardSkill && container.getExecutor() instanceof ServerPlayerPatch && this.isHoldingWeapon(container)) {
            event.setAmount(0.0F);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.MOVEMENT_INPUT_EVENT, LOCK_MOVEMENT_UUID, event -> {
         if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
            ControlEngine.setSprintingKeyStateNotDown();
            PlayerInputState current = event.getInputState();
            PlayerInputState updated = current.withForwardImpulse(current.forwardImpulse() * 0.5F).withLeftImpulse(current.leftImpulse() * 0.5F);
            InputManager.setInputState(updated);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.TAKE_DAMAGE_EVENT_ATTACK,
         PARRY_CHARGE_UUID,
         event -> {
            if (event.isParried() && !container.getExecutor().isLogicalClient()) {
               SkillContainer weaponSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(SkillSlots.WEAPON_INNATE);
               if (weaponSkillContainer != null && weaponSkillContainer.getSkill() != null) {
                  float currentCharge = weaponSkillContainer.getResource();
                  float maxCharge = weaponSkillContainer.getMaxResource();
                  float newCharge = Math.min(maxCharge, currentCharge + maxCharge * 0.34F);
                  weaponSkillContainer.getSkill().setConsumptionSynchronize(weaponSkillContainer, newCharge);
               }
            }

            if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
               Animator animator = container.getExecutor().getAnimator();
               AnimationPlayer animationPlayer = animator.getPlayerFor(null);
               if (animationPlayer == null) {
                  return;
               }

               AssetAccessor<? extends StaticAnimation> currentAnimation = ((DynamicAnimation)animationPlayer.getAnimation().get()).getRealAnimation();
               String animationName = currentAnimation.toString();
               boolean isSlashAnimation = animationName.contains("efn:biped/hf_murasama/slash/");
               if (isSlashAnimation) {
                  DamageSource damageSource = event.getDamageSource();
                  Entity offender = getOffender(damageSource);
                  boolean isBlockableSource = event.getDamageSource().is(DamageTypeTags.IS_PROJECTILE)
                     || event.getDamageSource().is(DamageTypeTags.IS_EXPLOSION);
                  boolean attacking = ((ServerPlayerPatch)event.getPlayerPatch()).getEntityState().attacking();
                  if (event.getDamage() > 0.0F && isBlockableSource && attacking) {
                     LivingEntity attacker = damageSource.getDirectEntity() instanceof LivingEntity ? (LivingEntity)damageSource.getDirectEntity() : null;
                     Vec3 viewVector = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).getViewVector(1.0F);
                     Vec3 attackDirection = Optional.ofNullable(damageSource.getSourcePosition())
                        .map(pos -> pos.subtract(((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).position()).normalize())
                        .orElseGet(() -> attacker != null ? attacker.getLookAngle() : viewVector);
                     if (attackDirection.dot(viewVector) > 0.0) {
                         event.cancel();
                         event.setParried(true);
                         event.setResult(ResultType.BLOCKED);
                        ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
                        Entity directEntity = event.getDamageSource().getDirectEntity();
                        if (directEntity != null && (directEntity instanceof Projectile || event.getDamageSource().is(DamageTypeTags.IS_PROJECTILE))) {
                           directEntity.discard();
                           ServerPlayer serverPlayer = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
                           serverPlayer.serverLevel()
                              .sendParticles(
                                 ParticleTypes.CRIT, directEntity.getX(), directEntity.getY(), directEntity.getZ(), 5, 0.2, 0.2, 0.2, 0.1
                              );
                        }

                        ServerPlayer serverPlayer = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
                        ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
                           .spawnParticleWithArgument(serverPlayer.serverLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, offender);
                        Player player = (Player)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
                        double lookX = -Math.sin(Math.toRadians(player.getYRot())) * 2.0;
                        double lookZ = Math.cos(Math.toRadians(player.getYRot())) * 2.0;
                        serverPlayer.serverLevel()
                           .sendParticles(
                              (HitParticleType)EFNParticles.ALL_SPARK.get(),
                              player.getX() + lookX,
                              player.getY() + 1.2,
                              player.getZ() + lookZ,
                              1,
                              0.0,
                              0.0,
                              0.0,
                              0.0
                           );
                     }
                  }
               }
            }

            if (!this.isPlayerSprinting(event.getPlayerPatch())
               || !event.getDamageSource().is(DamageTypeTags.IS_PROJECTILE) && !event.getDamageSource().is(DamageTypeTags.IS_EXPLOSION)) {
               if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
                  Animator animator = container.getExecutor().getAnimator();
                  AnimationPlayer animationPlayer = animator.getPlayerFor(null);
                  if (animationPlayer == null) {
                     return;
                  }

                  AssetAccessor<? extends StaticAnimation> currentAnimation = ((DynamicAnimation)animationPlayer.getAnimation().get()).getRealAnimation();
                  String animationName = currentAnimation.toString();
                  boolean isSlashAnimation = animationName.contains("efn:biped/hf_murasama/slash/");
                  boolean attacking = ((ServerPlayerPatch)event.getPlayerPatch()).getEntityState().attacking();
                  if (event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
                     epicFightDamageSource.setStunType(StunType.NONE);
                  }

                  if (isSlashAnimation && attacking) {
                     event.setResult(ResultType.MISSED);
                  }
               }
            } else {
               Animator animator = ((ServerPlayerPatch)event.getPlayerPatch()).getAnimator();
               AnimationPlayer animationPlayer = animator.getPlayerFor(null);
               boolean isAlreadyPlayingGuardDash = false;
               if (animationPlayer != null) {
                  DynamicAnimation currentAnimation = (DynamicAnimation)animationPlayer.getAnimation().get();
                  isAlreadyPlayingGuardDash = currentAnimation.equals(EFNMurasamaAnimations.HF_MURASAMA_GUARD_DASH.get());
               }

               if (!isAlreadyPlayingGuardDash) {
                  ((ServerPlayerPatch)event.getPlayerPatch()).playAnimationSynchronized(EFNMurasamaAnimations.HF_MURASAMA_GUARD_DASH, 0.0F);
               }

               event.cancel();
               event.setResult(ResultType.BLOCKED);
               ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EpicFightSounds.CLASH.get(), 1.0F, 1.0F);
               Player player = (Player)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
               if (player instanceof ServerPlayer serverPlayer) {
                  double lookX = -Math.sin(Math.toRadians(player.getYRot())) * 2.0;
                  double lookZ = Math.cos(Math.toRadians(player.getYRot())) * 2.0;
                  serverPlayer.serverLevel()
                     .sendParticles(
                        (HitParticleType)EFNParticles.ALL_SPARK.get(),
                        player.getX() + lookX,
                        player.getY() + 1.2,
                        player.getZ() + lookZ,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         },
         -1
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.DODGE_SUCCESS_EVENT, DODGE_SUCCESS_UUID, event -> {
         if (this.isHoldingWeapon(container)) {
            SkillContainer weaponSkillContainer = container.getExecutor().getSkill(SkillSlots.WEAPON_INNATE);
            SkillContainer dodgeSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(EFNSkills.MURASAMA_DODGE);
            if (weaponSkillContainer != null && weaponSkillContainer.getSkill() != null) {
               float currentCharge = weaponSkillContainer.getResource();
               float maxCharge = weaponSkillContainer.getMaxResource();
               float newCharge = Math.min(maxCharge, currentCharge + maxCharge * 0.5F);
               weaponSkillContainer.getSkill().setConsumptionSynchronize(weaponSkillContainer, newCharge);
            }

            DynamicAnimation forwardDodgeAnim = (DynamicAnimation)EFNDodgeAnimations.MURASAMA_ROLL_F.get();
            Animator animator = container.getExecutor().getAnimator();
            AnimationPlayer animationPlayer = animator.getPlayerFor(null);
            if (animationPlayer != null) {
               DynamicAnimation currentAnimation = (DynamicAnimation)animationPlayer.getAnimation().get();
               if (currentAnimation.equals(forwardDodgeAnim)) {
                  float prevElapsedTime = animationPlayer.getPrevElapsedTime();
                  boolean isForwardDodge = false;
                  if (dodgeSkillContainer != null) {
                     isForwardDodge = (Boolean)dodgeSkillContainer.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_FORWARD_DODGE);
                  }

                  if (prevElapsedTime <= 0.15F && isForwardDodge && !event.getDamageSource().is(DamageTypeTags.IS_PROJECTILE)) {
                     container.getExecutor().playAnimationSynchronized(EFNDodgeAnimations.MURASAMA_ROLL_F_EX, 0.0F);
                     container.getExecutor().resetActionTick();
                     container.getExecutor().playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                  }
               }
            }
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.SKILL_CAST_EVENT,
         DODGE_CANCEL_UUID,
         event -> {
            if (EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.DODGE) && !event.isStateExecutable()) {
               DynamicAnimation animation = (DynamicAnimation)Objects.requireNonNull(container.getExecutor().getAnimator().getPlayerFor(null))
                  .getRealAnimation()
                  .get();
               if (animation instanceof ActionAnimation
                  && !(animation instanceof DodgeAnimation)
                  && !(animation instanceof ZansetsuAttackAnimation)
                  && !animation.getRealAnimation().equals(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU)
                  && !animation.getRealAnimation().equals(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU_AIR)
                  && !event.isStateExecutable()) {
                  event.setStateExecutable(true);
               }
            }

            if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
               && (
                  EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.DODGE)
                     || EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.MOVER)
                     || EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.GUARD)
               )
               && event.isStateExecutable()) {
               event.cancel();
               event.setStateExecutable(false);
               event.setSkillExecutable(false);
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.ATTACK_PHASE_END_EVENT,
         INPUT_RESET_UUID,
         event -> {
            if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
               && event.getAnimation().get() instanceof ZansetsuAttackAnimation) {
               data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED, false);
            }

            if (((AttackAnimation)event.getAnimation().get()).equals(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU.get())
               || ((AttackAnimation)event.getAnimation().get()).equals(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU_AIR.get())) {
               data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE, false);
               SkillContainer weaponSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(SkillSlots.WEAPON_INNATE);
               if (weaponSkillContainer != null && weaponSkillContainer.getSkill() != null) {
                  weaponSkillContainer.getSkill().setStackSynchronize(weaponSkillContainer, weaponSkillContainer.getSkill().getMaxStack());
               }

               container.getExecutor().setStamina(container.getExecutor().getMaxStamina());
               ((Player)container.getExecutor().getOriginal()).setHealth(((Player)container.getExecutor().getOriginal()).getMaxHealth());
               this.removeNegativeEffects((ServerPlayer)container.getServerExecutor().getOriginal());
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.DEAL_DAMAGE_EVENT_HURT, ATTACK_COUNT_UUID, event -> {
         if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
            Animator animator = container.getExecutor().getAnimator();
            AnimationPlayer animationPlayer = animator.getPlayerFor(null);
            if (animationPlayer != null) {
               AssetAccessor<? extends StaticAnimation> currentAnimation = ((DynamicAnimation)animationPlayer.getAnimation().get()).getRealAnimation();
               String animationName = currentAnimation.toString();
               boolean isSlashAnimation = animationName.contains("efn:biped/hf_murasama/slash/");
               if (isSlashAnimation) {
                  if (event.getDamageSource().getAnimation() != null) {
                     AnimationAccessor<? extends StaticAnimation> damageSourceAnimation = event.getDamageSource().getAnimation();
                     String damageAnimationName = damageSourceAnimation.toString();
                     if (!damageAnimationName.contains("efn:biped/hf_murasama/slash/")) {
                        return;
                     }
                  }

                  int currentCount = (Integer)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER);
                  data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER, currentCount + 1);
                  if (currentCount + 1 >= this.zandatsu_slash_count_require) {
                     data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE, true);
                  }

                  if (!container.getExecutor().isLogicalClient()) {
                     StunTarget(event);
                  }
               }
            }
         }
      });
   }

   @OnlyIn(Dist.CLIENT)
   private void lockItemSwitching() {
      Minecraft minecraft = Minecraft.getInstance();
      Options options = minecraft.options;
      if (minecraft.player != null && minecraft.screen == null) {
         for (int i = 0; i < 9; i++) {
            while (options.keyHotbarSlots[i].consumeClick()) {
            }
         }

         while (options.keyDrop.consumeClick()) {
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void onMouseScroll(MouseScrollingEvent event) {
      Minecraft minecraft = Minecraft.getInstance();
      Player player = minecraft.player;
      if (player != null && minecraft.screen == null) {
         PlayerPatch playerPatch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch != null) {
            SkillContainer innateContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (innateContainer != null && innateContainer.getSkill() instanceof MurasamaInnate) {
               SkillDataManager data = innateContainer.getDataManager();
               boolean isZansetsuActive = data.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
                  && (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
               if (isZansetsuActive) {
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void onKeyInput(Key event) {
      Minecraft minecraft = Minecraft.getInstance();
      Player player = minecraft.player;
      if (player != null && minecraft.screen == null) {
         PlayerPatch playerPatch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch != null) {
            SkillContainer innateContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (innateContainer != null && innateContainer.getSkill() instanceof MurasamaInnate) {
               SkillDataManager data = innateContainer.getDataManager();
               boolean isZansetsuActive = data.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
                  && (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
               if (isZansetsuActive) {
                  Options options = minecraft.options;
                  boolean isHotbarKey = false;

                  for (KeyMapping key : options.keyHotbarSlots) {
                     if (event.getKey() == key.getKey().getValue()) {
                        isHotbarKey = true;
                        break;
                     }
                  }

                  boolean isDropKey = event.getKey() == options.keyDrop.getKey().getValue();
               }
            }
         }
      }
   }

   private boolean isPlayerSprinting(PlayerPatch<?> playerPatch) {
      return playerPatch == null ? false : ((Player)playerPatch.getOriginal()).isSprinting();
   }

   private void removeNegativeEffects(ServerPlayer player) {
      if (player != null) {
         List<Holder<MobEffect>> effectsToRemove = new ArrayList<>();

         for (MobEffectInstance effectInstance : player.getActiveEffects()) {
            Holder<MobEffect> effect = effectInstance.getEffect();
            if (this.isNegativeEffect(effect)) {
               effectsToRemove.add(effect);
            }
         }

          for (Holder<MobEffect> effect : effectsToRemove) {
            player.removeEffect(effect);
         }
      }
   }

   private boolean isNegativeEffect(Holder<MobEffect> effect) {
      MobEffectCategory category = effect.value().getCategory();
      return category == MobEffectCategory.HARMFUL
         ? true
         : effect == MobEffects.HUNGER || effect == MobEffects.UNLUCK || effect == MobEffects.BAD_OMEN || effect == MobEffects.DARKNESS;
   }

   private void checkLowHealthTargets(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      LivingEntity target = container.getServerExecutor().getTarget();
      LivingEntity lastHurtMob = ((ServerPlayer)container.getServerExecutor().getOriginal()).getLastHurtMob();
      List<LivingEntity> hitEntities = container.getServerExecutor().getCurrentlyActuallyHitEntities();
      if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
         if (!(Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE)) {
            if (hitEntities != null) {
               for (LivingEntity livingHitEntities : hitEntities) {
                  if (livingHitEntities != null && livingHitEntities.isAlive() && this.isHealthBelowPercent(livingHitEntities)) {
                     data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE, true);
                     return;
                  }
               }
            }

            if (target != null && target.isAlive() && this.isHealthBelowPercent(target)) {
               data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE, true);
            }

            if (lastHurtMob != null && lastHurtMob.isAlive() && this.isHealthBelowPercent(lastHurtMob)) {
               data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE, true);
            }
         }
      }
   }

   private boolean isHealthBelowPercent(LivingEntity entity) {
      float healthPercent = entity.getHealth() / entity.getMaxHealth();
      return healthPercent <= this.zandatsu_target_health_require;
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      SkillDataManager data = container.getDataManager();
      if (!container.getExecutor().isLogicalClient()) {
         this.updateAirborneState(container);
         this.handleAirborneVerticalLock(container);
         this.handleCombatStaminaRegen(container.getExecutor());
         this.checkAnimationProgressForDirectionChange(container);
         this.resetMurasamaCombo(container);
         this.checkLowHealthTargets(container);
      }

      if (container.getExecutor().isLogicalClient()) {
         this.handleKeyInput(container);
         this.handleZandatsuInput(container);
         this.handleInputModeSwitch(container);
         boolean isZansetsuActive = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
         if (isZansetsuActive) {
            ((LocalPlayer)container.getClientExecutor().getOriginal()).setSprinting(false);
            this.lockItemSwitching();
         }
      }

      zansetsuTimer(container);
      if (!(Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
         this.resetZansetsuStates(container.getDataManager());
      }
   }

   private void updateAirborneState(SkillContainer container) {
      ServerPlayerPatch serverPlayerPatch = container.getServerExecutor();
      ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
      SkillDataManager data = container.getDataManager();
      boolean isAirborne = !this.isPlayerOnGround(serverPlayer);
      if (data.hasData(EFNSKillDataKeys.MURASAMA_AIR_BORNE)) {
         boolean prevAirborne = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_AIR_BORNE);
         if (prevAirborne != isAirborne) {
            data.setDataSync(EFNSKillDataKeys.MURASAMA_AIR_BORNE, isAirborne);
         }
      }
   }

   private boolean isPlayerOnGround(ServerPlayer serverPlayer) {
      if (serverPlayer == null) {
         return false;
      } else if (serverPlayer.isInWater() || serverPlayer.isInLava()) {
         return false;
      } else if (!serverPlayer.onGround()) {
         return false;
      } else {
         return serverPlayer.onClimbable() ? false : !serverPlayer.isPassenger();
      }
   }

   private void handleAirborneVerticalLock(SkillContainer container) {
      if (!container.getExecutor().isLogicalClient()) {
         if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
            ServerPlayer var7 = (ServerPlayer)serverPlayerPatch.getOriginal();
            SkillDataManager data = container.getDataManager();
            boolean isZansetsuActive = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
            boolean isAirborne = data.hasData(EFNSKillDataKeys.MURASAMA_AIR_BORNE)
               && (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_AIR_BORNE);
            if (isZansetsuActive && isAirborne) {
               this.applyVerticalStopEffect(var7);
            }
         }
      }
   }

   private void applyVerticalStopEffect(ServerPlayer player) {
      if (player != null) {
         if (!player.hasEffect(EFNMobEffectRegistry.VERTICALSTOP)) {
            MobEffectInstance verticalStopEffect = new MobEffectInstance(EFNMobEffectRegistry.VERTICALSTOP, 15, 0, false, false, false);
            player.addEffect(verticalStopEffect);
         }
      }
   }

   private void resetMurasamaCombo(SkillContainer container) {
      if (!container.getExecutor().isLogicalClient()) {
         boolean shouldReset = container.getExecutor().getTickSinceLastAction() > 7
            || (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
         if (shouldReset) {
            this.resetCombo(container);
         }
      }
   }

   private void handleCombatStaminaRegen(PlayerPatch<?> playerPatch) {
      if (playerPatch.getEntityState().inaction()) {
         float stamina = playerPatch.getStamina();
         float maxStamina = playerPatch.getMaxStamina();
         if (stamina < maxStamina) {
            float regenAmount = maxStamina * 0.07F / 20.0F;
            playerPatch.setStamina(Math.min(stamina + regenAmount, maxStamina));
            if (playerPatch.getStaminaRegenAwaitTicks() > 10) {
               playerPatch.setStaminaRegenAwaitTicks(10);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void handleKeyInput(SkillContainer container) {
      if (container.getExecutor().isLogicalClient()) {
         SkillDataManager data = container.getDataManager();
         if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
            boolean isAttackPressed = Minecraft.getInstance().mouseHandler.isLeftPressed();
            boolean wasAttackPressed = data.hasData(EFNSKillDataKeys.MURASAMA_ATTACK_KEY_PRESSED)
               && (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ATTACK_KEY_PRESSED);
            if (isAttackPressed != wasAttackPressed) {
               data.setDataSync(EFNSKillDataKeys.MURASAMA_ATTACK_KEY_PRESSED, isAttackPressed);
            }

            int stateLevel = container.getExecutor().getEntityState().getLevel();
            if (isAttackPressed
               && this.isInAttackableState(stateLevel)
               && !(Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED)) {
               data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED, true);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void handleZandatsuInput(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE)) {
         if ((Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            boolean isRightMouseDown = GLFW.glfwGetMouseButton(windowHandle, 1) == 1;
            if (isRightMouseDown) {
               boolean inAir = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_AIR_BORNE);
               this.resetZansetsuStates(container.getDataManager());
               if (!inAir) {
                  container.getExecutor().playAnimationSynchronized(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU, 0.1F);
               } else {
                  LivingEntity target = container.getExecutor().getTarget();
                  if (target != null) {
                     double playerY = ((Player)container.getExecutor().getOriginal()).getY();
                     double targetEyeY = target.getEyeY();
                     if (playerY < targetEyeY) {
                        double targetHeight = targetEyeY - 0.5;
                        ((Player)container.getExecutor().getOriginal())
                           .moveTo(
                              ((Player)container.getExecutor().getOriginal()).getX(),
                              targetHeight,
                              ((Player)container.getExecutor().getOriginal()).getZ()
                           );
                     }
                  }

                  container.getExecutor().playAnimationSynchronized(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU_AIR, 0.1F);
               }
            }
         }
      }
   }

   private boolean isInAttackableState(int stateLevel) {
      return stateLevel == 0 || stateLevel == 1;
   }

   @OnlyIn(Dist.CLIENT)
   private void handleInputModeSwitch(SkillContainer container) {
      if (container.getExecutor().isLogicalClient()) {
         SkillDataManager data = container.getDataManager();
         boolean isZansetsuActive = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
         if (isZansetsuActive) {
            boolean isSwitchKeyPressed = EFNKeyMappings.ZANSETSU_INPUT_SWITCH.isDown();
            if (!data.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_INPUT_MODE)) {
               data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_INPUT_MODE, false);
            }

            boolean currentMode = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_INPUT_MODE);
            if (currentMode != isSwitchKeyPressed) {
               data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_INPUT_MODE, isSwitchKeyPressed);
               if (isSwitchKeyPressed) {
                  this.resetWheelState(data);
               } else {
                  this.resetGestureState(data);
               }
            }
         }
      }
   }

   private void resetWheelState(SkillDataManager data) {
      if (data.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR)) {
         data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR, 0);
      }
   }

   private void resetGestureState(SkillDataManager data) {
   }

   private void checkAnimationProgressForDirectionChange(SkillContainer container) {
      if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
         Animator animator = container.getExecutor().getAnimator();
         AnimationPlayer animationPlayer = animator.getPlayerFor(null);
         if (animationPlayer != null) {
            DynamicAnimation currentAnimation = (DynamicAnimation)animationPlayer.getAnimation().get();
            if (currentAnimation instanceof ZansetsuAttackAnimation) {
               float prevElapsedTime = animationPlayer.getPrevElapsedTime();
               float elapsedTime = animationPlayer.getElapsedTime();
               float prevFrame = prevElapsedTime * 60.0F;
               float currentFrame = elapsedTime * 60.0F;
               boolean leftPressed = container.getDataManager().hasData(EFNSKillDataKeys.MURASAMA_ATTACK_KEY_PRESSED)
                  && (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_ATTACK_KEY_PRESSED);
               if (prevFrame < 17.0F && currentFrame >= 17.0F && leftPressed) {
                  SkillDataManager data = container.getDataManager();
                  int currentSector = (Integer)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR);
                  boolean hasTriggered = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_OPPOSITE_TRIGGERED);
                  int lastSector = (Integer)data.getDataValue(EFNSKillDataKeys.MURASAMA_LAST_TRIGGERED_SECTOR);
                  if (!hasTriggered || lastSector != currentSector) {
                     boolean isPairCompleted = false;
                     if (data.hasData(EFNSKillDataKeys.MURASAMA_PAIR_FLAG)) {
                        isPairCompleted = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_PAIR_FLAG);
                     }

                     int nextSector;
                     if (!isPairCompleted) {
                        nextSector = this.getOppositeSector(currentSector);
                        data.setDataSync(EFNSKillDataKeys.MURASAMA_PAIR_FLAG, true);
                     } else {
                        int previousStartSector = this.getOppositeSector(currentSector);
                        float randomRoll = ((Player)container.getExecutor().getOriginal()).getRandom().nextFloat();
                        if (randomRoll < 0.3F) {
                           nextSector = previousStartSector;
                        } else {
                           nextSector = ((Player)container.getExecutor().getOriginal()).getRandom().nextInt(16);

                           while (nextSector == currentSector || nextSector == previousStartSector) {
                              nextSector = ((Player)container.getExecutor().getOriginal()).getRandom().nextInt(16);
                           }
                        }

                        data.setDataSync(EFNSKillDataKeys.MURASAMA_PAIR_FLAG, false);
                     }

                     data.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR, nextSector);
                     this.playOppositeZansetsuAnimation(container, nextSector, -0.15F);
                     data.setDataSync(EFNSKillDataKeys.MURASAMA_OPPOSITE_TRIGGERED, true);
                     data.setDataSync(EFNSKillDataKeys.MURASAMA_LAST_TRIGGERED_SECTOR, currentSector);
                  }
               }

               if (animationPlayer.isEnd() || currentFrame <= 7.0F) {
                  container.getDataManager().setDataSync(EFNSKillDataKeys.MURASAMA_OPPOSITE_TRIGGERED, false);
                  container.getDataManager().setDataSync(EFNSKillDataKeys.MURASAMA_LAST_TRIGGERED_SECTOR, -1);
               }
            }
         }
      }
   }

   private int getOppositeSector(int sector) {
      return (sector + 8) % 16;
   }

   private void playOppositeZansetsuAnimation(SkillContainer container, int sector, float transitionTime) {
      if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
         AnimationAccessor<? extends StaticAnimation> oppositeAnimation = this.getZansetsuAnimationForSector(container, sector);
         if (oppositeAnimation != null) {
            serverPlayerPatch.playAnimationSynchronized(oppositeAnimation, transitionTime);
         }
      }
   }

   private AnimationAccessor<? extends StaticAnimation> getZansetsuAnimationForSector(SkillContainer container, int sector) {
      boolean inAir = false;
      if (container.getDataManager().hasData(EFNSKillDataKeys.MURASAMA_AIR_BORNE)) {
         inAir = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_AIR_BORNE);
      }

      return inAir ? this.getZansetsuAnimationForSector_Air(sector) : this.getZansetsuAnimationForSector_Ground(sector);
   }

   private void resetZansetsuStates(SkillDataManager dataManager) {
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_PAIR_FLAG);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_OPPOSITE_TRIGGERED);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_LAST_TRIGGERED_SECTOR);
   }

   private <T> void resetStateKey(SkillDataManager dataManager, DeferredHolder<SkillDataKey<?>, ? extends SkillDataKey<T>> dataKey) {
      T defaultValue = dataKey.value().defaultValue();
      if (!Objects.equals(dataManager.getDataValue(dataKey), defaultValue)) {
         if (dataManager.hasData(dataKey)) {
            dataManager.setDataSync(dataKey, defaultValue);
         }
      }
   }

   private AnimationAccessor<? extends StaticAnimation> getZansetsuAnimationForSector_Ground(int sector) {
      return switch (sector) {
         case 0 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_M;
         case 1 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_L;
         case 2 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_DOWN;
         case 3 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_DOWN;
         case 4 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_MID;
         case 5 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_UP;
         case 6 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_UP;
         case 7 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_L;
         case 8 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_M;
         case 9 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_R;
         case 10 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_UP;
         case 11 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_UP;
         case 12 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_MID;
         case 13 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_DOWN;
         case 14 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_DOWN;
         case 15 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_R;
         default -> null;
      };
   }

   private AnimationAccessor<? extends StaticAnimation> getZansetsuAnimationForSector_Air(int sector) {
      return switch (sector) {
         case 0 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_M_AIR;
         case 1 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_L_AIR;
         case 2 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_DOWN_AIR;
         case 3 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_DOWN_AIR;
         case 4 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_MID_AIR;
         case 5 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_UP_AIR;
         case 6 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_UP_AIR;
         case 7 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_L_AIR;
         case 8 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_M_AIR;
         case 9 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_R_AIR;
         case 10 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_UP_AIR;
         case 11 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_UP_AIR;
         case 12 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_MID_AIR;
         case 13 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_DOWN_AIR;
         case 14 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_DOWN_AIR;
         case 15 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_R_AIR;
         default -> null;
      };
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, this);
   }

   public static Entity getOffender(DamageSource damageSource) {
      return damageSource.getDirectEntity() == null ? damageSource.getEntity() : damageSource.getDirectEntity();
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, DODGE_TRIGGER_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, DODGE_CANCEL_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DODGE_SUCCESS_EVENT, DODGE_SUCCESS_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_HURT, ATTACK_COUNT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ATTACK_PHASE_END_EVENT, INPUT_RESET_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CONSUME_EVENT, GUARD_STAMINA_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.MOVEMENT_INPUT_EVENT, LOCK_MOVEMENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, PARRY_CHARGE_UUID, -1);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      list.add(Component.translatable("skill.efn.murasama.tooltip").withStyle(ChatFormatting.BOLD));
      list.add(
         Component.translatable("skill.efn.murasama.tooltip1")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.RED)
            .append("X·")
            .append(InvincibleKeyMappings.KEY1.getTranslatedKeyMessage())
            .append(" ")
            .append("Y·")
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.murasama.tooltip2").withStyle(ChatFormatting.GRAY));
      list.add(Component.translatable("skill.efn.murasama.tooltip3").withStyle(ChatFormatting.GRAY));
      list.add(Component.translatable("skill.efn.murasama.tooltip4").withStyle(ChatFormatting.GRAY));
      list.add(Component.translatable("skill.efn.murasama.tooltip5").withStyle(ChatFormatting.GRAY));
      list.add(
         Component.translatable("skill.efn.murasama.tooltip6")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.AQUA)
            .append("·")
            .append(InvincibleKeyMappings.KEY4.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.murasama.tooltip7").withStyle(ChatFormatting.GRAY));
      list.add(Component.translatable("skill.efn.murasama.tooltip8").withStyle(ChatFormatting.GRAY));
      list.add(
         Component.translatable("skill.efn.murasama.tooltip9")
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.GOLD)
            .append("·")
            .append(Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage())
            .append("+")
            .append(InvincibleKeyMappings.KEY4.getTranslatedKeyMessage())
      );
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      guiGraphics.pose().pushPose();
      boolean creative = ((Player)container.getExecutor().getOriginal()).isCreative();
      boolean fullstack = creative || container.isFull();
      if (!container.isDisabled() && container.getSkill().checkExecuteCondition(container)) {
         boolean canUse = true;
      } else {
         boolean canUse = false;
      }

      float cooldownRatio = !fullstack && !container.isActivated() ? container.getResource(partialTick) : 1.0F;
      float iconSize = 32.0F;
      float bottom = y + iconSize;
      float right = x + iconSize;
      float middle = x + iconSize * 0.5F;
      float lastVertexX = 0.0F;
      float lastVertexY = 0.0F;
      float lastTexX = 0.0F;
      float lastTexY = 0.0F;
      int vertexNum;
      if (cooldownRatio < 0.125F) {
         vertexNum = 6;
         lastTexX = cooldownRatio / 0.25F;
         lastTexY = 0.0F;
         lastVertexX = middle + iconSize * lastTexX;
         lastVertexY = y;
         lastTexX += 0.5F;
      } else if (cooldownRatio < 0.375F) {
         vertexNum = 5;
         lastTexX = 1.0F;
         lastTexY = (cooldownRatio - 0.125F) / 0.25F;
         lastVertexX = right;
         lastVertexY = y + iconSize * lastTexY;
      } else if (cooldownRatio < 0.625F) {
         vertexNum = 4;
         lastTexX = (cooldownRatio - 0.375F) / 0.25F;
         lastTexY = 1.0F;
         lastVertexX = right - iconSize * lastTexX;
         lastVertexY = bottom;
         lastTexX = 1.0F - lastTexX;
      } else if (cooldownRatio < 0.875F) {
         vertexNum = 3;
         lastTexX = 0.0F;
         lastTexY = (cooldownRatio - 0.625F) / 0.25F;
         lastVertexX = x;
         lastVertexY = bottom - iconSize * lastTexY;
         lastTexY = 1.0F - lastTexY;
      } else {
         vertexNum = 2;
         lastTexX = (cooldownRatio - 0.875F) / 0.25F;
         lastTexY = 0.0F;
         lastVertexX = x + iconSize * lastTexX;
         lastVertexY = y;
      }

      RenderSystem.enableBlend();
      RenderSystem.setShaderTexture(0, container.getSkill().getSkillTexture());
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Tesselator tessellator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);

      for (int j = 0; j < vertexNum; j++) {
         bufferbuilder.addVertex(guiGraphics.pose().last().pose(), x + iconSize * CLOCK_POS[j].x, y + iconSize * CLOCK_POS[j].y, 0.0F)
            .setUv(CLOCK_POS[j].x, CLOCK_POS[j].y)
            ;
      }

      bufferbuilder.addVertex(guiGraphics.pose().last().pose(), lastVertexX, lastVertexY, 0.0F).setUv(lastTexX, lastTexY);
      BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glCullFace(1028);
      bufferbuilder = tessellator.begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);

      for (int var38 = 0; var38 < 2; var38++) {
         bufferbuilder.addVertex(guiGraphics.pose().last().pose(), x + iconSize * CLOCK_POS[var38].x, y + iconSize * CLOCK_POS[var38].y, 0.0F)
            .setUv(CLOCK_POS[var38].x, CLOCK_POS[var38].y)
            ;
      }

      for (int var39 = CLOCK_POS.length - 1; var39 >= vertexNum; var39--) {
         bufferbuilder.addVertex(guiGraphics.pose().last().pose(), x + iconSize * CLOCK_POS[var39].x, y + iconSize * CLOCK_POS[var39].y, 0.0F)
            .setUv(CLOCK_POS[var39].x, CLOCK_POS[var39].y)
            ;
      }

      bufferbuilder.addVertex(guiGraphics.pose().last().pose(), lastVertexX, lastVertexY, 0.0F).setUv(lastTexX, lastTexY);
      BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      GL11.glCullFace(1029);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      SkillDataManager dataManager = container.getDataManager();
      boolean isZansetsuActive = dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
         && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
      if (isZansetsuActive) {
         if (dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER)) {
            int currentCount = (Integer)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER);
            int remainingHits = Math.max(0, this.zandatsu_slash_count_require - currentCount);
            String hitsText = String.valueOf(remainingHits);
            int hitsTextWidth = gui.getFont().width(hitsText);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(1.2F, 1.2F, 1.0F);
            float scaledX = (x + 14.0F - hitsTextWidth * 0.6F) / 1.2F;
            float scaledY = (y + 14.0F) / 1.2F;
            guiGraphics.drawString(gui.getFont(), hitsText, scaledX, scaledY, 16777215, true);
            guiGraphics.pose().popPose();
         }

         String healthText = String.format("%.0f%%", this.zandatsu_target_health_require * 100.0F);
         int healthTextWidth = gui.getFont().width(healthText);
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(x + 13.0F - healthTextWidth / 2.0F, y + 25.0F, 0.0F);
         guiGraphics.drawString(gui.getFont(), healthText, 0, 0, 16733525, true);
         guiGraphics.pose().popPose();
      } else {
         if (container.getSkill().getMaxStack() > 1) {
            String stackText = String.valueOf(container.getStack());
            int stackTextWidth = gui.getFont().width(stackText);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(1.2F, 1.2F, 1.0F);
            float scaledX = (x + 14.0F - stackTextWidth * 0.6F) / 1.2F;
            float scaledY = (y + 14.0F) / 1.2F;
            guiGraphics.drawString(gui.getFont(), stackText, scaledX, scaledY, 16777215, true);
            guiGraphics.pose().popPose();
         }

         String resourceText;
         if (!container.isActivated()
            || container.getSkill().getActivateType() != ActivateType.DURATION && container.getSkill().getActivateType() != ActivateType.DURATION_INFINITE) {
            if (!fullstack) {
               resourceText = (int)(cooldownRatio * 100.0F) + "%";
            } else {
               resourceText = "100%";
            }
         } else {
            resourceText = String.format("%.0fs", container.getRemainDuration() / 20.0F);
         }

         int resourceTextWidth = gui.getFont().width(resourceText);
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(x + 13.0F - resourceTextWidth / 2.0F, y + 25.0F, 0.0F);
         guiGraphics.drawString(gui.getFont(), resourceText, 0, 0, 16733525, true);
         guiGraphics.pose().popPose();
      }

      SkillDataManager manager = container.getDataManager();
      if (manager.hasData(InvincibleSkillDataKeys.COOLDOWN)) {
         int cooldown = (Integer)manager.getDataValue(InvincibleSkillDataKeys.COOLDOWN);
         if (cooldown > 0) {
            Font font = gui.getFont();
            String string = String.format("%.1fs", cooldown / 20.0);
            int stringWidth = font.width(string);
            guiGraphics.drawString(font, string, x + iconSize - stringWidth - 2.0F, y + 2.0F, 16777045, true);
         }
      }

      guiGraphics.pose().popPose();
   }
}
