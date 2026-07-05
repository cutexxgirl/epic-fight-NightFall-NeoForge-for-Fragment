package com.hm.efn.skill.weapon_passive;

import com.hm.efn.animations.types.murasama.MurasamaAttackAnimation;
import com.hm.efn.animations.types.murasama.MurasamaLivingAnimation;
import com.hm.efn.animations.types.murasama.MurasamaMovementAnimation;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.dodge.MurasamaDodge;
import com.hm.efn.util.EFNSkillChecks;
import com.hm.efn.util.ItemStackData;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.action.MinecraftInputAction;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.modules.HoldableSkill;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import com.hm.efn.compat.epicfight.eventlistener.ActionEvent;
import com.hm.efn.compat.epicfight.eventlistener.MovementInputEvent;
import yesman.epicfight.api.event.EntityEventListener;
import yesman.epicfight.api.event.types.player.SkillCastEvent;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class HfBladePassive extends PassiveSkill {
   private static final UUID EVENT_UUID = UUID.fromString("052a9bb2-7541-11ee-b962-0242ac120003");
   private static final UUID RESET_EVENT_UUID = UUID.fromString("052a9bb2-7541-11ee-b962-0721ac120003");
   private static final UUID SWORDOUT_EVENT_HURT_UUID = UUID.fromString("052a9bb2-7541-22ee-b962-0731ac110001");
   private static final UUID SWORDOUT_EVENT_GUARD_UUID = UUID.fromString("052a9bb2-7541-22ee-b962-0721ac120003");
   private static final UUID SWORDOUT_EVENT_DODGE_UUID = UUID.fromString("052a9bb2-7541-33ee-b962-0712ac120003");
   private static final UUID SHEATH_DAMAGE_EVENT_UUID = UUID.fromString("052a9bb2-7541-33ee-b962-0721ac120003");
   private static final UUID SHEATH_RESETEVENT_UUID = UUID.fromString("052a9bb2-7541-11ee-b962-0212ac120003");
   private static final UUID SPRINT_SHEATH_UUID = UUID.fromString("052a9bb2-7541-44ee-b962-0721ac120003");
   private static final double TARGET_HEIGHT = 2.0;
   private static final double ACCELERATION_FACTOR = 1.5;
   private static final int DOUBLE_TAP_WINDOW = 5;
   private static final double MIN_AIR_HEIGHT = 1.5;
   private int backwardForwardWindow;
   private int successWindow;

   public HfBladePassive(SkillBuilder<?> builder) {
      super(builder);
   }

   private static double calculateJumpPower() {
      double gravity = 0.08;
      double basePower = Math.sqrt(2.0 * gravity * 2.0);
      return basePower * 1.5;
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      if (parameters.contains("backward_forward_window")) {
         this.backwardForwardWindow = parameters.getInt("backward_forward_window");
      }

      if (parameters.contains("success_window")) {
         this.successWindow = parameters.getInt("success_window");
      }
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      this.setupEventListeners(container);
      this.setMurasamaSheathMesh(container, false);
   }

   private void setupEventListeners(SkillContainer container) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, event -> this.handleMovementInputEvent(container, event));
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.MOVEMENT_INPUT_EVENT, SPRINT_SHEATH_UUID, event -> this.handleSprintSheathAnimation(container, event));
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.FALL_EVENT, EVENT_UUID, event -> {
         this.handleFallEvent(container);
         if (event.getPlayerPatch().isLogicalClient()) {
            container.getDataManager().setData(EFNSKillDataKeys.JUMP_KEY_PRESSED_LAST_TICK, false);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.ACTION_EVENT_SERVER, RESET_EVENT_UUID, event -> this.handleActionEventServer(container, event));
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_GUARD_UUID, event -> {
         if (EFNSkillChecks.skill(event.getSkillContainer()) instanceof HoldableSkill) {
            this.murasamaSwordOut(container);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.TAKE_DAMAGE_EVENT_HURT, SWORDOUT_EVENT_HURT_UUID, event -> this.murasamaSwordOut(container), -1);
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_DODGE_UUID, event -> {
         if (EFNSkillChecks.skill(event.getSkillContainer()) instanceof MurasamaDodge) {
            this.murasamaSwordOut(container);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.DEAL_DAMAGE_EVENT_HURT, SHEATH_DAMAGE_EVENT_UUID, event -> {
         if (!container.getExecutor().isLogicalClient()) {
            ServerPlayer serverPlayer = (ServerPlayer)container.getServerExecutor().getOriginal();
            Holder<MobEffect> effect = EFNMobEffectRegistry.MURASAMA_SHEATH;
            if (serverPlayer.hasEffect(effect)) {
               serverPlayer.removeEffect(effect);
               container.getExecutor().playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
            }
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.ANIMATION_BEGIN_EVENT, SHEATH_RESETEVENT_UUID, event -> {
         if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_KICK_Y)) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE, false);
            container.getDataManager().setData(EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS, 0);
         }

         if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_SWORD_OUT)) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.MANUAL_UNSHEATH, true);
         }

         if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_GUARD_DASH)) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.MANUAL_UNSHEATH, true);
            this.murasamaSwordOut(container);
         }

         if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_TAUNT)) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.MANUAL_UNSHEATH, true);
            this.murasamaSwordOut(container);
         }

         if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_JUMP_SECOND)) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.MANUAL_UNSHEATH, true);
            this.murasamaSwordOut(container);
         }

         if (event.getAnimation().getRealAnimation().equals(EFNDodgeAnimations.MURASAMA_ROLL_F_AIR)) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.MANUAL_UNSHEATH, true);
            this.murasamaSwordOut(container);
         }

         if (event.getAnimation() instanceof MurasamaLivingAnimation) {
            this.setMurasamaSheathMesh(container, false);
         }

         if (event.getAnimation() instanceof MurasamaMovementAnimation) {
            this.setMurasamaSheathMesh(container, false);
         }
      });
   }

   public void setMurasamaSheathMesh(SkillContainer container, boolean isSheath) {
      ItemStack mainHandItem = ((Player)container.getExecutor().getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
      ItemStackData.putInt(mainHandItem, "murasama_sheath", isSheath ? 1 : 0);
   }

   private boolean isZansetsuActive(SkillContainer container) {
      SkillContainer innateSkill = container.getExecutor().getSkill(SkillSlots.WEAPON_INNATE);
      SkillDataManager innateDataManager = innateSkill.getDataManager();
      return innateDataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
         && (Boolean)innateDataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
   }

   private void handleSprintSheathAnimation(SkillContainer container, MovementInputEvent event) {
      PlayerPatch<?> playerPatch = event.getPlayerPatch();
      Player player = (Player)playerPatch.getOriginal();
      boolean isSheathed = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_SHEATH);
      boolean isDoubleTapSprint = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT);
      boolean isSprinting = player.isSprinting();
      boolean isManuallyUnsheathed = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MANUAL_UNSHEATH);
      if (!isSprinting && isManuallyUnsheathed) {
         container.getDataManager().setDataSync(EFNSKillDataKeys.MANUAL_UNSHEATH, false);
      }

      if (!isManuallyUnsheathed && !isSheathed && !isDoubleTapSprint && isSprinting) {
         int sprintTimer = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.SPRINT_SHEATH_TIMER);
         if (isSprinting) {
            container.getDataManager().setData(EFNSKillDataKeys.SPRINT_SHEATH_TIMER, ++sprintTimer);
            if (sprintTimer >= 3) {
               Animator animator = playerPatch.getAnimator();
               AnimationPlayer animationPlayer = animator.getPlayerFor(null);
               boolean isAlreadyPlaying = false;
               if (animationPlayer != null) {
                  DynamicAnimation currentAnimation = (DynamicAnimation)animationPlayer.getAnimation().get();
                  isAlreadyPlaying = currentAnimation.equals(EFNHfBladeAnimations.HF_BLADE_SHEATH_IN_RUN.get());
               }

               if (!isAlreadyPlaying) {
                  playerPatch.playAnimationSynchronized(EFNHfBladeAnimations.HF_BLADE_SHEATH_IN_RUN, 0.0F);
               }

               container.getDataManager().setData(EFNSKillDataKeys.SPRINT_SHEATH_TIMER, 0);
            }
         }
      } else {
         container.getDataManager().setData(EFNSKillDataKeys.SPRINT_SHEATH_TIMER, 0);
      }
   }

   public void murasamaSwordOut(SkillContainer container) {
      if (!container.getExecutor().isLogicalClient()) {
         if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_SHEATH)) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.MURASAMA_SHEATH, false);
            container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
            container.getDataManager().setDataSync(EFNSKillDataKeys.MANUAL_UNSHEATH, true);
         }

         this.setMurasamaSheathMesh(container, false);
      }
   }

   private void handleMovementInputEvent(SkillContainer container, MovementInputEvent event) {
      HfBladePassive.MovementInputState inputState = this.getMovementInputState(container, event);
      if (this.validateMovementInputPreconditions(container, event)) {
         this.handleDoubleTapSprint(container, inputState, event);
         this.handleDoubleJump(container, inputState, event);
      }

      if (container.getExecutor().isLogicalClient()) {
         this.updateClientWindowTime(container);
      }

      this.updateKeyStates(container, inputState);
   }

   private void updateClientWindowTime(SkillContainer container) {
      boolean isForwardBackwardWindowActive = (Boolean)container.getDataManager()
         .getDataValue(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE);
      if (isForwardBackwardWindowActive) {
         int remainingTicks = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS);
         if (remainingTicks > 0) {
            container.getDataManager().setData(EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS, --remainingTicks);
         }
      }
   }

   private boolean validateMovementInputPreconditions(SkillContainer container, MovementInputEvent event) {
      if (((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).getVehicle() != null) {
         return false;
      } else if (!((LocalPlayerPatch)event.getPlayerPatch()).isEpicFightMode()) {
         return false;
      } else if (((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).getAbilities().flying) {
         return false;
      } else {
         return ((LocalPlayerPatch)event.getPlayerPatch()).isHoldingAny() ? false : !((LocalPlayerPatch)event.getPlayerPatch()).getEntityState().attacking();
      }
   }

   private HfBladePassive.MovementInputState getMovementInputState(SkillContainer container, MovementInputEvent event) {
      boolean upPressed = event.getInputState().up();
      boolean downPressed = event.getInputState().down();
      boolean jumpPressed = event.getInputState().jumping();
      boolean isSprinting = ((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).isSprinting();
      boolean upPressedPrev = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.FORWARD_PRESSED_LAST_TICK);
      boolean downPressedPrev = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.BACKWARD_PRESSED_LAST_TICK);
      boolean jumpPressedPrev = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.JUMP_KEY_PRESSED_LAST_TICK);
      return new HfBladePassive.MovementInputState(upPressed, downPressed, jumpPressed, isSprinting, upPressedPrev, downPressedPrev, jumpPressedPrev);
   }

   private void handleDoubleTapSprint(SkillContainer container, HfBladePassive.MovementInputState inputState, MovementInputEvent event) {
      boolean isDoubleTapSprint = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT);
      boolean wasDoubleTapSprint = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.WAS_DOUBLE_TAP_SPRINT);
      int doubleTapTimer = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.DOUBLE_TAP_TIMER);
      boolean isSheathed = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_SHEATH);
      if (!isSheathed) {
         if (wasDoubleTapSprint && !inputState.isSprinting()) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT, false);
         }

         if (inputState.upPressed() && !inputState.upPressedPrev()) {
            if (doubleTapTimer > 0 && doubleTapTimer <= 5) {
               this.triggerDoubleTapSprint(container, event);
               container.getDataManager().setData(EFNSKillDataKeys.DOUBLE_TAP_TIMER, 0);
            } else {
               container.getDataManager().setData(EFNSKillDataKeys.DOUBLE_TAP_TIMER, 1);
               container.getDataManager().setDataSync(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT, false);
            }
         }

         if (doubleTapTimer > 0) {
            if (++doubleTapTimer > 5) {
               doubleTapTimer = 0;
            }

            container.getDataManager().setData(EFNSKillDataKeys.DOUBLE_TAP_TIMER, doubleTapTimer);
         }

         if (isDoubleTapSprint != wasDoubleTapSprint) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.WAS_DOUBLE_TAP_SPRINT, isDoubleTapSprint);
         }
      }
   }

   private void handleDoubleJump(SkillContainer container, HfBladePassive.MovementInputState inputState, MovementInputEvent event) {
      boolean jumpPressed = isJumpActionPressed();
      boolean jumpPressedPrev = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.JUMP_KEY_PRESSED_LAST_TICK);
      if (jumpPressed && !jumpPressedPrev) {
         boolean isInAir = !((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).onGround();
         boolean hasDoubleJumped = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.JUMP_COUNT) > 0;
         if (isInAir && !hasDoubleJumped) {
            double verticalVelocity = ((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).getDeltaMovement().y;
            boolean isFallingDown = verticalVelocity < 0.0;
            if (this.checkAirborneDistance((Player)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()) && isFallingDown) {
               this.executeDoubleJump(container, event);
            }
         }
      }
   }

   private void updateKeyStates(SkillContainer container, HfBladePassive.MovementInputState inputState) {
      container.getDataManager().setData(EFNSKillDataKeys.FORWARD_PRESSED_LAST_TICK, inputState.upPressed());
      container.getDataManager().setData(EFNSKillDataKeys.BACKWARD_PRESSED_LAST_TICK, inputState.downPressed());
      container.getDataManager().setData(EFNSKillDataKeys.UP_KEY_PRESSED_LAST_TICK, inputState.upPressed());
      container.getDataManager().setData(EFNSKillDataKeys.JUMP_KEY_PRESSED_LAST_TICK, isJumpActionPressed());
   }

   private void triggerForwardBackwardSuccess(SkillContainer container) {
      container.getDataManager().setDataSync(EFNSKillDataKeys.BACKWARD_FORWARD_SUCCESS, true);
      container.getDataManager().setDataSync(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE, true);
      container.getDataManager().setDataSync(EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS, this.successWindow);
   }

   private void triggerDoubleTapSprint(SkillContainer container, MovementInputEvent event) {
      container.getDataManager().setDataSync(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT, true);
   }

   private void applySprintSpeedBoost(SkillContainer container) {
      if (!container.getExecutor().isLogicalClient()) {
         ServerPlayer serverPlayer = (ServerPlayer)container.getServerExecutor().getOriginal();
         boolean isDoubleTapSprint = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT);
         boolean isSheath = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_SHEATH);
         if (isDoubleTapSprint) {
            MobEffectInstance speedEffect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 1, false, false, false);
            serverPlayer.addEffect(speedEffect);
         }

         if (isSheath && serverPlayer.isSprinting()) {
            MobEffectInstance speedEffect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 1, false, false, false);
            serverPlayer.addEffect(speedEffect);
         }

         if (!isSheath && serverPlayer.isSprinting()) {
            MobEffectInstance speedEffect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 0, false, false, false);
            serverPlayer.addEffect(speedEffect);
         }
      }
   }

   private void removeSprintSpeedBoost(SkillContainer container) {
      if (!container.getExecutor().isLogicalClient()) {
         ServerPlayer player = (ServerPlayer)container.getExecutor().getOriginal();
         player.removeEffect(MobEffects.MOVEMENT_SPEED);
      }
   }

   private void executeDoubleJump(SkillContainer container, MovementInputEvent event) {
      SkillCastEvent skillCastEvent = new SkillCastEvent(container.getExecutor(), container, null);
      com.hm.efn.util.EFNEventBridge.triggerEvents(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, skillCastEvent);
      if (!skillCastEvent.isCanceled()) {
         container.getDataManager().setDataSync(EFNSKillDataKeys.JUMP_COUNT, 1);
         Vec3 deltaMove = ((Player)container.getExecutor().getOriginal()).getDeltaMovement();
         double jumpPower = calculateJumpPower();
         ((Player)container.getExecutor().getOriginal())
            .setDeltaMovement(deltaMove.x, jumpPower + ((Player)container.getExecutor().getOriginal()).getJumpBoostPower(), deltaMove.z);
         ((LocalPlayerPatch)event.getPlayerPatch()).playAnimationSynchronized(EFNHfBladeAnimations.HF_BLADE_JUMP_SECOND, 0.1F);
      }
   }

   private void handleFallEvent(SkillContainer container) {
      container.getDataManager().setDataSync(EFNSKillDataKeys.JUMP_COUNT, 0);
   }

   private void handleActionEventServer(SkillContainer container, ActionEvent event) {
      if (!container.getExecutor().isLogicalClient()) {
         boolean isDoubleTapSprint = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT);
         if (isDoubleTapSprint && !event.getAnimation().isEmpty()) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT, false);
            container.getDataManager().setDataSync(EFNSKillDataKeys.WAS_DOUBLE_TAP_SPRINT, false);
            this.removeSprintSpeedBoost(container);
            if (container.getServerExecutor() != null) {
               Objects.requireNonNull(container.getServerExecutor().getAnimator().getPlayerFor(null)).reset();
               container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
            }
         }
      }
   }

   private boolean checkAirborneDistance(Player player) {
      MutableBlockPos pos = new MutableBlockPos(player.getX(), player.getY() - 0.1, player.getZ());
      int searchDepth = 0;

      BlockState blockState;
      do {
         blockState = player.level().getBlockState(pos);
         pos.move(0, -1, 0);
         searchDepth++;
      } while ((blockState.isAir() || blockState.getBlock() instanceof BushBlock) && searchDepth < 10);

      double distance = player.getY() - pos.getY() - 1.0;
      return distance >= 1.5;
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (this.isZansetsuActive(container)) {
         this.resetSheathState(container);
         this.murasamaSwordOut(container);
      }

      if (container.getExecutor().isLogicalClient()) {
         this.handleRawCommandInputs(container);
      }

      if (!container.getExecutor().isLogicalClient()) {
         this.handleServerUpdate(container);
         Animator animator = container.getServerExecutor().getAnimator();
         AssetAccessor<? extends StaticAnimation> currentAnim = ((DynamicAnimation)Objects.requireNonNull(animator.getPlayerFor(null)).getAnimation().get())
            .getRealAnimation();
         if (((ServerPlayer)container.getServerExecutor().getOriginal()).onGround()) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.JUMP_COUNT, 0);
            if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.JUMP_KEY_PRESSED_LAST_TICK)) {
               container.getDataManager().setData(EFNSKillDataKeys.JUMP_KEY_PRESSED_LAST_TICK, false);
            }
         }

         if (currentAnim.equals(EFNDodgeAnimations.MURASAMA_ROLL_F_AIR)) {
            this.updateComboNode((ServerPlayer)container.getServerExecutor().getOriginal(), HfBlade.Air_Dodge);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void handleRawCommandInputs(SkillContainer container) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.screen == null) {
         if (mc.player.getVehicle() == null && container.getExecutor().isEpicFightMode() && !mc.player.getAbilities().flying) {
            SkillDataManager data = container.getDataManager();
            CompoundTag persistentData = mc.player.getPersistentData();
            boolean wPressed = mc.options.keyUp.isDown();
            boolean sPressed = mc.options.keyDown.isDown();
            boolean wPrev = persistentData.getBoolean("efn_raw_w");
            boolean sPrev = persistentData.getBoolean("efn_raw_s");
            int timer = data.hasData(EFNSKillDataKeys.BACKWARD_FORWARD_TIMER)
               ? (Integer)data.getDataValue(EFNSKillDataKeys.BACKWARD_FORWARD_TIMER)
               : 0;
            boolean windowActive = data.hasData(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE)
               && (Boolean)data.getDataValue(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE);
            if (!windowActive) {
               if (sPressed && !sPrev) {
                  data.setData(EFNSKillDataKeys.BACKWARD_FORWARD_TIMER, 1);
               }

               if (timer > 0 && timer <= this.backwardForwardWindow) {
                  if (wPressed && !wPrev) {
                     this.triggerForwardBackwardSuccess(container);
                     data.setData(EFNSKillDataKeys.BACKWARD_FORWARD_TIMER, 0);
                  } else {
                     data.setData(EFNSKillDataKeys.BACKWARD_FORWARD_TIMER, timer + 1);
                  }
               } else if (timer > this.backwardForwardWindow) {
                  data.setData(EFNSKillDataKeys.BACKWARD_FORWARD_TIMER, 0);
               }
            }

            persistentData.putBoolean("efn_raw_w", wPressed);
            persistentData.putBoolean("efn_raw_s", sPressed);
         }
      }
   }

   private void resetSheathState(SkillContainer container) {
      if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_SHEATH)) {
         container.getDataManager().setDataSync(EFNSKillDataKeys.MURASAMA_SHEATH, false);
         if (!container.getExecutor().isLogicalClient()) {
            container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
         }
      }

      this.setMurasamaSheathMesh(container, false);
   }

   private void handleServerUpdate(SkillContainer container) {
      ServerPlayer serverPlayer = (ServerPlayer)container.getServerExecutor().getOriginal();
      this.updateDoubleTapSprintState(container);
      this.updateBackwardForwardWindowState(container, serverPlayer);
      this.updateSheathBuff(container);
      this.applySprintSpeedBoost(container);
   }

   private void updateSheathBuff(SkillContainer container) {
      ItemStack mainHandItem = ((Player)container.getExecutor().getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
      if (ItemStackData.contains(mainHandItem, "murasama_sheath")) {
         boolean isSheathed = ItemStackData.getInt(mainHandItem, "murasama_sheath") == 1;
         ServerPlayer player = (ServerPlayer)container.getServerExecutor().getOriginal();
         var effect = EFNMobEffectRegistry.MURASAMA_SHEATH;
         if (isSheathed) {
            if (!player.hasEffect(effect)) {
               player.addEffect(new MobEffectInstance(effect, -1, 0, false, false, true));
               container.getExecutor().playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F);
               player.getPersistentData().putInt("EFNMurasamaSheathTicks", 0);
               player.getPersistentData().putBoolean("EFNMurasamaMaxSoundPlayed", false);
            }
         } else if (player.hasEffect(effect)
            && !(
               Objects.requireNonNull(container.getServerExecutor().getServerAnimator().getPlayerFor(null)).getAnimation().get() instanceof MurasamaAttackAnimation
            )) {
            player.removeEffect(effect);
         }

         if (!this.isHoldingWeapon(container) && player.hasEffect(effect)) {
            player.removeEffect(effect);
         }
      }
   }

   private void updateDoubleTapSprintState(SkillContainer container) {
      boolean isDoubleTapSprint = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT);
      boolean wasDoubleTapSprint = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.WAS_DOUBLE_TAP_SPRINT);
      if (isDoubleTapSprint != wasDoubleTapSprint) {
         container.getExecutor().playAnimationSynchronized(EFNHfBladeAnimations.HF_BLADE_RUN_COMBAT_2, 0.0F);
         container.getServerExecutor().getAnimator().resetLivingAnimations();
         container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
         container.getDataManager().setDataSync(EFNSKillDataKeys.WAS_DOUBLE_TAP_SPRINT, isDoubleTapSprint);
      }
   }

   private void updateBackwardForwardWindowState(SkillContainer container, ServerPlayer player) {
      boolean isBackwardForwardWindowActive = (Boolean)container.getDataManager()
         .getDataValue(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE);
      if (isBackwardForwardWindowActive) {
         int remainingTicks = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS);
         if (remainingTicks <= 0) {
            this.resetBackwardForwardWindow(container, player);
         } else {
            container.getDataManager().setData(EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS, --remainingTicks);
         }
      }
   }

   private void resetBackwardForwardWindow(SkillContainer container, ServerPlayer player) {
      container.getDataManager().setDataSync(EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE, false);
      container.getDataManager().setDataSync(EFNSKillDataKeys.BACKWARD_FORWARD_SUCCESS, false);
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, HfBlade.HfBlade);
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      if (!container.getExecutor().isLogicalClient()) {
         this.removeSprintSpeedBoost(container);
         ServerPlayer player = (ServerPlayer)container.getServerExecutor().getOriginal();
          Holder<MobEffect> effect = EFNMobEffectRegistry.MURASAMA_SHEATH;
         if (player.hasEffect(effect)) {
            player.removeEffect(effect);
         }
      }

      this.removeEventListeners(container);
   }

   private void removeEventListeners(SkillContainer container) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.FALL_EVENT, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.ACTION_EVENT_SERVER, RESET_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_GUARD_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_DODGE_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.TAKE_DAMAGE_EVENT_HURT, SWORDOUT_EVENT_HURT_UUID, -1);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.DEAL_DAMAGE_EVENT_HURT, SHEATH_DAMAGE_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.ANIMATION_BEGIN_EVENT, SHEATH_RESETEVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.MOVEMENT_INPUT_EVENT, SPRINT_SHEATH_UUID);
   }

   private void updateComboNode(ServerPlayer serverPlayer, ComboNode comboNode) {
      InvincibleAttachments.getPlayer(serverPlayer).setCurrentNode(comboNode);
   }

   private static boolean isJumpActionPressed() {
      return InputManager.isActionActive(MinecraftInputAction.JUMP);
   }

   private record MovementInputState(
      boolean upPressed, boolean downPressed, boolean jumpPressed, boolean isSprinting, boolean upPressedPrev, boolean downPressedPrev, boolean jumpPressedPrev
   ) {
   }
}
