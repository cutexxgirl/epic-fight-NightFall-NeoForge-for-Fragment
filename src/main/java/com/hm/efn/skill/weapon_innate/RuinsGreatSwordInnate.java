package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.modules.HoldableSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import com.hm.efn.compat.epicfight.eventlistener.MovementInputEvent;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class RuinsGreatSwordInnate extends EFNWeaponInnateBase {
   public static final int MAX_CHARGING_TICKS = 46;
   public static final int MIN_CHARGE_THRESHOLD = 20;
   private static final int STAMINA_CONSUME_INTERVAL = 4;
   private static final float STAMINA_CONSUME_AMOUNT = 0.7F;
   private static final float MIN_STAMINA_THRESHOLD = 1.0F;
   private static final int COOLDOWN_TICKS = 20;
   private static final UUID MOVEMENT_LOCK_UUID = UUID.fromString("d34db31f-5a1d-4b3a-9f1c-1919810c2d1a");
   private static final UUID ANIMATION_EVENT_UUID = UUID.fromString("d34db32f-5a1d-4b3a-9f2c-1919810c2d1a");
   private static final UUID STAMINA_CHECK_UUID = UUID.fromString("d34db33f-5a1d-4b3a-9f3c-1919810c2d1a");
   private static final UUID DISABLE_GUARD_UUID = UUID.fromString("d34db33f-5a1d-1b2a-9f2c-8e2245142d1a");
   private static final UUID ANIMATION_INTERRUPT_LISTENER_UUID = UUID.fromString("d34db23f-5a1d-4b3a-9f2c-1212810c2d1a");
   private boolean hasAutoTriggered = false;

   private static AnimationAccessor<? extends StaticAnimation> chargingAnimation() {
      return EFNGreatSwordAnimations.NG_GREATSWORD_CHARGING;
   }

   private static AnimationAccessor<? extends AttackAnimation> fullChargeAnimation() {
      return EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_FIRST;
   }

   private static AnimationAccessor<? extends AttackAnimation> lowChargeAnimation() {
      return EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MIN;
   }

   public RuinsGreatSwordInnate(Builder builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      SkillDataManager data = container.getDataManager();
      this.initializeChargeState(data);
      this.hasAutoTriggered = false;
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.MOVEMENT_INPUT_EVENT, MOVEMENT_LOCK_UUID, event -> {
         if (this.isCharging(container.getDataManager())) {
            MovementInputEvent inputEvent = event;
            Input input = inputEvent.getMovementInput();
            input.forwardImpulse = 0.0F;
            input.leftImpulse = 0.0F;
            input.jumping = false;
            input.shiftKeyDown = false;
            if (event.getPlayerPatch() instanceof LocalPlayerPatch localPlayerPatch && localPlayerPatch.getOriginal() instanceof LocalPlayer player) {
               player.setSprinting(false);
            }
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.ANIMATION_END_EVENT, ANIMATION_EVENT_UUID, event -> {
         StaticAnimation animation = event.getAnimation();
         PlayerPatch<?> executor = container.getExecutor();
         SkillDataManager dataManager = container.getDataManager();
         if (this.isAnimation(animation, fullChargeAnimation()) || this.isAnimation(animation, lowChargeAnimation())) {
            this.resetChargeState(dataManager, executor.getOriginal());
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.ACTION_EVENT_SERVER,
            STAMINA_CHECK_UUID,
            event -> {
               if (this.isCharging(container.getDataManager()) && container.getExecutor() instanceof ServerPlayerPatch serverPatch) {
                  float currentStamina = serverPatch.getStamina();
                  if (currentStamina < 1.0F) {
                     this.forceReleaseDueToLowStamina(container, serverPatch);
                  }
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.SKILL_CAST_EVENT,
            DISABLE_GUARD_UUID,
            event -> {
               if (this.isCharging(container.getDataManager()) && EFNSkillChecks.skill(event.getSkillContainer()) instanceof HoldableSkill) {
                  event.cancel();
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.ANIMATION_BEGIN_EVENT,
            ANIMATION_INTERRUPT_LISTENER_UUID,
            event -> {
               if (this.isCharging(container.getDataManager()) && !this.isChargeRelatedAnimation(event.getAnimation())) {
                  this.cancelCharging(container);
               }
            }
         );
   }

   public void onRemoved(SkillContainer container) {
      this.hasAutoTriggered = false;
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.MOVEMENT_INPUT_EVENT, MOVEMENT_LOCK_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ANIMATION_END_EVENT, ANIMATION_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ACTION_EVENT_SERVER, STAMINA_CHECK_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, DISABLE_GUARD_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, ANIMATION_INTERRUPT_LISTENER_UUID);
      super.onRemoved(container);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().getEntityState().inaction()) {
         this.handleCooldown(container);
         if (container.getExecutor().isLogicalClient()) {
            this.handleClientInput(container);
         } else {
            this.validateServerState(container);
         }
      }
   }

   private void handleCooldown(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      Integer cooldown = (Integer)data.getDataValue(EFNSKillDataKeys.COOLDOWN);
      if (cooldown != null && cooldown > 0) {
         data.setDataSync(EFNSKillDataKeys.COOLDOWN, cooldown - 1);
      }
   }

   private void handleClientInput(SkillContainer container) {
      LocalPlayer player = (LocalPlayer)container.getExecutor().getOriginal();
      SkillDataManager data = container.getDataManager();
      if (!player.level().isClientSide || Minecraft.getInstance().screen == null) {
         ItemStack heldItem = player.getMainHandItem();
         boolean isHoldingGreatsword = EFNSkillChecks.isInnateSkill(container.getExecutor(), heldItem, this);
         if (!isHoldingGreatsword && this.isCharging(data)) {
            this.resetChargeState(data, player);
         } else {
            if (isHoldingGreatsword) {
               boolean isKeyDown = false;
               boolean wasPressing = this.isPressing(data);
               if (player.level().isClientSide) {
                  long windowHandle = Minecraft.getInstance().getWindow().getWindow();
                  boolean isRightMouseDown = GLFW.glfwGetMouseButton(windowHandle, 1) == 1;
                  boolean isLeftMouseDown = GLFW.glfwGetMouseButton(windowHandle, 0) == 1;
                  isKeyDown = isRightMouseDown && isLeftMouseDown;
               }

               if (isKeyDown != wasPressing) {
                  if (player.level().isClientSide) {
                     data.setDataSync(EFNSKillDataKeys.IS_PRESSING, isKeyDown);
                  }

                  if (isKeyDown) {
                     this.startCharging(container, player, data);
                  } else {
                     int chargeTime = this.getChargeTicks(data);
                     if (!this.hasAutoTriggered) {
                        this.releaseAttack(container, chargeTime, player, false);
                     }
                  }
               }
            }
         }
      }
   }

   private void startCharging(SkillContainer container, LocalPlayer player, SkillDataManager data) {
      PlayerPatch<?> executor = container.getExecutor();
      Integer cooldown = (Integer)data.getDataValue(EFNSKillDataKeys.COOLDOWN);
      if (cooldown == null || cooldown <= 0) {
         if (!this.isPlayingChargeAnimation(executor)) {
            if (!executor.getEntityState().inaction() && !this.isCharging(data)) {
               data.setDataSync(EFNSKillDataKeys.IS_CHARGING, true);
               data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
               this.hasAutoTriggered = false;
               SkillContainer skillContainer = container.getExecutor().getSkill(SkillSlots.GUARD);
               if (skillContainer != null && skillContainer.getSkill() instanceof HoldableSkill holdableSkill && skillContainer.isActivated()) {
                  skillContainer.deactivate();
               }

               AnimationAccessor<? extends StaticAnimation> charging = chargingAnimation();
               if (charging != null) {
                  executor.playAnimationSynchronized(charging, 0.0F);
               }
            }
         }
      }
   }

   private void releaseAttack(SkillContainer container, int chargeTime, Object player, boolean isAutoTrigger) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillDataManager data = container.getDataManager();
      if (!this.isPlayingChargeAnimation(executor)) {
         this.resetChargeState(data, player);
         this.playAttackAnimation(container, chargeTime, isAutoTrigger);
      }
   }

   private void playAttackAnimation(SkillContainer container, int chargeTime, boolean isAutoTrigger) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillDataManager data = container.getDataManager();
      if (!executor.getEntityState().inaction()) {
         boolean isFullCharge = false;
         AnimationAccessor<? extends AttackAnimation> anim;
         if (chargeTime >= 46) {
            anim = fullChargeAnimation();
            isFullCharge = true;
         } else if (chargeTime >= 20) {
            anim = fullChargeAnimation();
            executor.playSound((SoundEvent)SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 100.0F, 0.0F, 0.0F);
            isFullCharge = true;
         } else {
            anim = lowChargeAnimation();
            executor.playSound((SoundEvent)SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 100.0F, 0.0F, 0.0F);
         }

         this.resetChargeState(data, executor.getOriginal());
         if (anim != null) {
            executor.playAnimationSynchronized(anim, -0.05F);
         }
         if (!executor.isLogicalClient() && isFullCharge) {
            ServerPlayer serverPlayer = (ServerPlayer)executor.getOriginal();
            Vec3 pos = serverPlayer.position();
            serverPlayer.serverLevel().sendParticles(ParticleTypes.ENCHANT, pos.x, pos.y + 1.0, pos.z, 15, 0.5, 0.2, 0.5, 0.5);
         }
      }
   }

   private void resetChargeState(SkillDataManager data, Object player) {
      if (player instanceof ServerPlayer) {
         data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
         data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
         data.setDataSync(EFNSKillDataKeys.IS_PRESSING, false);
      } else if (player instanceof LocalPlayer) {
         data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
         data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
         data.setDataSync(EFNSKillDataKeys.IS_PRESSING, false);
      } else {
         data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
         data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
         data.setDataSync(EFNSKillDataKeys.IS_PRESSING, false);
      }

      this.hasAutoTriggered = true;
   }

   private void validateServerState(SkillContainer container) {
      ServerPlayerPatch serverPatch = (ServerPlayerPatch)container.getExecutor();
      SkillDataManager data = container.getDataManager();
      Integer cooldown = (Integer)data.getDataValue(EFNSKillDataKeys.COOLDOWN);
      if (cooldown == null || cooldown <= 0) {
         if (this.isCharging(data)) {
            container.getExecutor().resetActionTick();
            int currentTime = this.getChargeTicks(data);
            int newTime = Math.min(currentTime + 1, 46);
            data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, newTime);
            float currentStamina = serverPatch.getStamina();
            if (newTime % 4 == 0) {
               if (!(currentStamina >= 0.7F)) {
                  this.forceReleaseDueToLowStamina(container, serverPatch);
                  return;
               }

               serverPatch.setStamina(currentStamina - 0.7F);
            }

            if (currentStamina < 1.0F) {
               this.forceReleaseDueToLowStamina(container, serverPatch);
            }
         }
      }
   }

   private void forceReleaseDueToLowStamina(SkillContainer container, ServerPlayerPatch serverPatch) {
      SkillDataManager data = container.getDataManager();
      Integer cooldown = (Integer)data.getDataValue(EFNSKillDataKeys.COOLDOWN);
      if (cooldown == null || cooldown <= 0) {
         int chargeTime = this.getChargeTicks(data);
         data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
         data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
         data.setDataSync(EFNSKillDataKeys.IS_PRESSING, false);
         data.setDataSync(EFNSKillDataKeys.COOLDOWN, 20);
         this.hasAutoTriggered = true;
         if (chargeTime > 0) {
            AnimationAccessor<? extends AttackAnimation> lowCharge = lowChargeAnimation();
            if (lowCharge != null) {
               serverPatch.playAnimationSynchronized(lowCharge, 0.1F);
            }
         }

         serverPatch.playSound(SoundEvents.IRON_GOLEM_HURT, 1.0F, 0.0F, 0.0F);
      }
   }

   private boolean isPlayingChargeAnimation(PlayerPatch<?> playerPatch) {
      return this.isPlayingAnimation(playerPatch, chargingAnimation())
         || this.isPlayingAnimation(playerPatch, fullChargeAnimation())
         || this.isPlayingAnimation(playerPatch, lowChargeAnimation());
   }

   private boolean isChargeRelatedAnimation(StaticAnimation animation) {
      return this.isAnimation(animation, chargingAnimation())
         || this.isAnimation(animation, fullChargeAnimation())
         || this.isAnimation(animation, lowChargeAnimation());
   }

   private boolean isPlayingAnimation(PlayerPatch<?> playerPatch, AnimationAccessor<? extends StaticAnimation> animation) {
      if (animation == null) {
         return false;
      }

      AnimationPlayer animationPlayer = playerPatch.getAnimator().getPlayerFor(animation);
      return animationPlayer != null && animationPlayer.getAnimation() == animation.get();
   }

   private boolean isAnimation(StaticAnimation animation, AnimationAccessor<? extends StaticAnimation> accessor) {
      return animation != null && accessor != null && (animation == accessor.get() || animation.getRealAnimation().equals(accessor));
   }

   private void initializeChargeState(SkillDataManager data) {
      if (data.getDataValue(EFNSKillDataKeys.IS_CHARGING) == null) {
         data.setData(EFNSKillDataKeys.IS_CHARGING, false);
      }

      if (data.getDataValue(EFNSKillDataKeys.IS_PRESSING) == null) {
         data.setData(EFNSKillDataKeys.IS_PRESSING, false);
      }

      if (data.getDataValue(EFNSKillDataKeys.CHARGE_TICKS) == null) {
         data.setData(EFNSKillDataKeys.CHARGE_TICKS, 0);
      }
   }

   private boolean isCharging(SkillDataManager data) {
      return Boolean.TRUE.equals(data.getDataValue(EFNSKillDataKeys.IS_CHARGING));
   }

   private boolean isPressing(SkillDataManager data) {
      return Boolean.TRUE.equals(data.getDataValue(EFNSKillDataKeys.IS_PRESSING));
   }

   private int getChargeTicks(SkillDataManager data) {
      Integer value = (Integer)data.getDataValue(EFNSKillDataKeys.CHARGE_TICKS);
      return value != null ? value : 0;
   }

   private void cancelCharging(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      if (data.hasData(EFNSKillDataKeys.IS_CHARGING) && data.hasData(EFNSKillDataKeys.IS_PRESSING)) {
         data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
         data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
         if (container.getExecutor() instanceof ServerPlayerPatch serverPatch && data.hasData(EFNSKillDataKeys.IS_PRESSING)) {
            data.setDataSync(EFNSKillDataKeys.IS_PRESSING, false);
         }

         this.hasAutoTriggered = false;
      }
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.ruinsgreatsword.tooltip").withStyle(ChatFormatting.AQUA));
      list.add(
         Component.translatable("skill.efn.ruinsgreatsword.tooltip1")
            .withStyle(ChatFormatting.AQUA)
            .append(Component.literal(keyName))
            .append(": ")
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.ruinsgreatsword.tooltip2"));
      list.add(Component.translatable("skill.efn.ruinsgreatsword.tooltip3").withStyle(ChatFormatting.AQUA));
      list.add(Component.translatable("skill.efn.ruinsgreatsword.tooltip4"));
      return list;
   }
}
