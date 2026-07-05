package com.hm.efn.skill.dodge;

import com.hm.efn.gameasset.EFNWeaponCategories;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.InputUtils;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.registry.entries.EpicFightSkillDataKeys;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.skill.dodge.DodgeSkill.Builder;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class YamatoDodge extends DodgeSkill {
   private static final UUID JUMP_EVENT_UUID = UUID.fromString("a1b2c3d4-e5f6-11ed-a50b-0242ac120123");

   public YamatoDodge(Builder builder) {
      super(builder);
   }

   private static AnimationAccessor<? extends StaticAnimation> getDodgeAnimation(int index) {
      return switch (index) {
         case 0 -> EFNDodgeAnimations.YAMATO_STEP_F;
         case 1 -> EFNDodgeAnimations.YAMATO_STEP_B;
         case 2 -> EFNDodgeAnimations.YAMATO_STEP_L;
         case 3 -> EFNDodgeAnimations.YAMATO_STEP_R;
         case 4 -> EFNDodgeAnimations.YAMATO_STEP_U;
         case 5 -> EFNDodgeAnimations.YAMATO_STEP_D;
         default -> null;
      };
   }

   private static int normalizeAnimationIndex(int animationIndex, boolean serverIsInAir) {
      return animationIndex < 0 || animationIndex > 5 ? (serverIsInAir ? 5 : 4) : animationIndex;
   }

   public static boolean isHoldingYamato(Player player) {
      return player == null
         ? false
         : Stream.of(player.getMainHandItem(), player.getOffhandItem())
            .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
            .filter(Objects::nonNull)
            .anyMatch(cap -> cap.getWeaponCategory() == EFNWeaponCategories.EFN_YAMATO);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      EntityEventListener listener = container.getExecutor().getEventListener();
      container.getDataManager().registerData(EpicFightSkillDataKeys.JUMP_COUNT);
      container.getDataManager().setData(EpicFightSkillDataKeys.JUMP_COUNT, 0);
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.MOVEMENT_INPUT_EVENT,
         JUMP_EVENT_UUID,
         event -> {
            if (((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).getVehicle() == null
               && ((LocalPlayerPatch)event.getPlayerPatch()).isEpicFightMode()
               && !((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).getAbilities().flying
               && !((LocalPlayerPatch)event.getPlayerPatch()).isHoldingAny()
               && !((LocalPlayerPatch)event.getPlayerPatch()).getEntityState().inaction()) {
               boolean jumpPressed = Minecraft.getInstance().options.keyJump.isDown();
               boolean isOnGround = ((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).onGround();
               if (isOnGround && jumpPressed) {
                  container.getDataManager().setData(EpicFightSkillDataKeys.JUMP_COUNT, 1);
               }
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.FALL_EVENT, JUMP_EVENT_UUID, event -> container.getDataManager().setData(EpicFightSkillDataKeys.JUMP_COUNT, 0)
      );
   }

   public void onRemoved(SkillContainer container) {
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.MOVEMENT_INPUT_EVENT, JUMP_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.FALL_EVENT, JUMP_EVENT_UUID);
   }

   public boolean canExecute(SkillContainer container) {
      return isHoldingYamato((Player)container.getExecutor().getOriginal());
   }

   public boolean isExecutableState(PlayerPatch<?> executor) {
      EntityState playerState = executor.getEntityState();
      return playerState.canUseSkill()
         && !((Player)executor.getOriginal()).onClimbable()
         && ((Player)executor.getOriginal()).getVehicle() == null;
   }

   @OnlyIn(Dist.CLIENT)
   public void gatherArguments(SkillContainer skillContainer, ControlEngine controlEngine, CompoundTag arguments) {
      LocalPlayerPatch executor = skillContainer.getClientExecutor();
      Input input = ((LocalPlayer)executor.getOriginal()).input;
      float pulse = (float)executor.getOriginal().getAttributeValue(Attributes.SNEAKING_SPEED);
      InputUtils.sneakingTick((LocalPlayer)executor.getOriginal(), false, pulse);
      int forward = input.up ? 1 : 0;
      int backward = input.down ? -1 : 0;
      int left = input.left ? 1 : 0;
      int right = input.right ? -1 : 0;
      int vertic = forward + backward;
      int horizon = left + right;
      float yRot = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
      float degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon) + yRot;
      boolean isInAir = !((LocalPlayer)executor.getOriginal()).onGround();
      int jumpCounter = (Integer)skillContainer.getDataManager().getDataValue(EpicFightSkillDataKeys.JUMP_COUNT);
      boolean canDoubleJump = isInAir && jumpCounter > 0;
      int directionIndex;
      float finalDegree;
      if (isInAir && vertic < 0) {
         directionIndex = 5;
         finalDegree = yRot;
      } else if (isInAir && vertic == 0 && horizon == 0 && canDoubleJump) {
         directionIndex = 4;
         finalDegree = yRot;
         skillContainer.getDataManager().setData(EpicFightSkillDataKeys.JUMP_COUNT, 0);
      } else if (!isInAir && vertic == 0 && horizon == 0) {
         directionIndex = 4;
         finalDegree = yRot;
      } else {
         if (vertic == 0) {
            if (horizon == 0) {
               directionIndex = 0;
            } else {
               directionIndex = horizon >= 0 ? 2 : 3;
            }
         } else {
            directionIndex = vertic >= 0 ? 0 : 1;
         }

         finalDegree = vertic == 0 && horizon != 0 ? yRot : degree;
      }

      arguments.putInt("directionIndex", directionIndex);
      arguments.putFloat("degree", finalDegree);
      arguments.putBoolean("isInAir", isInAir);
      arguments.putInt("jumpCounter", jumpCounter);
   }

   public void executeOnServer(SkillContainer skillContainer, CompoundTag args) {
      ServerPlayerPatch executor = skillContainer.getServerExecutor();
      int animationIndex = args.getInt("directionIndex");
      float degree = args.getFloat("degree");
      boolean clientIsInAir = args.getBoolean("isInAir");
      int clientJumpCounter = args.getInt("jumpCounter");
      boolean serverIsInAir = !((ServerPlayer)executor.getOriginal()).onGround();
      skillContainer.getDataManager().setData(EpicFightSkillDataKeys.JUMP_COUNT, clientJumpCounter);
      boolean canDoubleJump = serverIsInAir && clientJumpCounter > 0;
      if (serverIsInAir && animationIndex == 1) {
         animationIndex = 5;
      }

      if (serverIsInAir && animationIndex == 4) {
         if (!canDoubleJump) {
            animationIndex = 0;
         } else {
            skillContainer.getDataManager().setData(EpicFightSkillDataKeys.JUMP_COUNT, 0);
            ((ServerPlayer)executor.getOriginal())
               .setDeltaMovement(((ServerPlayer)executor.getOriginal()).getDeltaMovement().x, 0.5, ((ServerPlayer)executor.getOriginal()).getDeltaMovement().z);
         }
      }

      animationIndex = normalizeAnimationIndex(animationIndex, serverIsInAir);
      AnimationAccessor<? extends StaticAnimation> animation = getDodgeAnimation(animationIndex);
      if (animation == null) {
         return;
      }

      skillContainer.getExecutor().playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
      executor.playAnimationSynchronized(animation, 0.0F);
      executor.setModelYRot(degree, true);
   }
}
