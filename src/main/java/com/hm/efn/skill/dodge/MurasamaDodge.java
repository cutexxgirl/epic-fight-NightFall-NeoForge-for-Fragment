package com.hm.efn.skill.dodge;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Murasama;
import com.hm.efn.util.EFNSkillChecks;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.InputUtils;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.skill.dodge.DodgeSkill.Builder;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class MurasamaDodge extends DodgeSkill {
   private static final UUID EVENT_UUID = UUID.fromString("a1b2c3d4-e5f6-11ed-a50b-0242ac120002");

   public MurasamaDodge(Builder builder) {
      super(builder);
   }

   private static AnimationAccessor<? extends StaticAnimation> getDodgeAnimation(int index) {
      return switch (index) {
         case 0 -> EFNDodgeAnimations.MURASAMA_ROLL_F;
         case 1 -> EFNDodgeAnimations.MURASAMA_ROLL_B;
         case 2 -> EFNDodgeAnimations.MURASAMA_ROLL_F_AIR;
         default -> null;
      };
   }

   private boolean isHoldingMurasama(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, Murasama.Murasama)
         || EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, HfBlade.HfBlade);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.MOVEMENT_INPUT_EVENT,
         EVENT_UUID,
         event -> {
            if (((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).getVehicle() == null
               && ((LocalPlayerPatch)event.getPlayerPatch()).isEpicFightMode()
               && !((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).getAbilities().flying
               && !((LocalPlayerPatch)event.getPlayerPatch()).isHoldingAny()
               && !((LocalPlayerPatch)event.getPlayerPatch()).getEntityState().inaction()) {
               boolean isOnGround = ((LocalPlayer)((LocalPlayerPatch)event.getPlayerPatch()).getOriginal()).onGround();
               if (isOnGround) {
                  int currentJumpState = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.JUMP_COUNT);
                  if (currentJumpState == -1) {
                     container.getDataManager().setData(EFNSKillDataKeys.JUMP_COUNT, 0);
                  }
               }
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.FALL_EVENT, EVENT_UUID, event -> container.getDataManager().setData(EFNSKillDataKeys.JUMP_COUNT, 0)
      );
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().isLogicalClient() && ((ServerPlayer)container.getServerExecutor().getOriginal()).onGround()) {
         container.getDataManager().setData(EFNSKillDataKeys.JUMP_COUNT, 0);
      }
   }

   public void onRemoved(SkillContainer container) {
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.FALL_EVENT, EVENT_UUID);
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
      int glideCounter = (Integer)skillContainer.getDataManager().getDataValue(EFNSKillDataKeys.JUMP_COUNT);
      boolean shouldConsumeGlide = false;
      boolean isForwardDodge = vertic >= 0 && horizon == 0;
      int directionIndex;
      if (isInAir) {
         directionIndex = vertic >= 0 ? 2 : 1;
         shouldConsumeGlide = true;
      } else {
         directionIndex = vertic >= 0 ? 0 : 1;
      }

      if (shouldConsumeGlide) {
         skillContainer.getDataManager().setData(EFNSKillDataKeys.JUMP_COUNT, -1);
      }

      arguments.putInt("directionIndex", directionIndex);
      arguments.putFloat("degree", degree);
      arguments.putBoolean("isInAir", isInAir);
      arguments.putInt("glideCounter", glideCounter);
      arguments.putBoolean("shouldConsumeGlide", shouldConsumeGlide);
      arguments.putBoolean("isForwardDodge", isForwardDodge);
   }

   public boolean canExecute(SkillContainer container) {
      if (!this.isHoldingMurasama(container)) {
         return false;
      }

      int glideCounter = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.JUMP_COUNT);
      return glideCounter != -1;
   }

   public boolean isExecutableState(PlayerPatch<?> executor) {
      if (!executor.isLogicalClient()) {
         return true;
      }

      EntityState playerState = executor.getEntityState();
      return !((Player)executor.getOriginal()).onClimbable() && ((Player)executor.getOriginal()).getVehicle() == null && playerState.canUseSkill();
   }

   public void executeOnServer(SkillContainer skillContainer, CompoundTag args) {
      ServerPlayerPatch executor = skillContainer.getServerExecutor();
      int animationIndex = args.getInt("directionIndex");
      float degree = args.getFloat("degree");
      boolean clientIsInAir = args.getBoolean("isInAir");
      int clientGlideCounter = args.getInt("glideCounter");
      boolean clientShouldConsumeGlide = args.getBoolean("shouldConsumeGlide");
      boolean isForwardDodge = args.getBoolean("isForwardDodge");
      boolean serverIsInAir = !((ServerPlayer)executor.getOriginal()).onGround();
      skillContainer.getDataManager().setData(EFNSKillDataKeys.JUMP_COUNT, clientGlideCounter);
      boolean canGlide = serverIsInAir && clientGlideCounter != -1;
      if (!serverIsInAir || canGlide) {
         if (clientShouldConsumeGlide && canGlide) {
            skillContainer.getDataManager().setData(EFNSKillDataKeys.JUMP_COUNT, -1);
         }

         if (animationIndex < 0 || animationIndex > 2) {
            animationIndex = 0;
         }

         AnimationAccessor<? extends StaticAnimation> animation = getDodgeAnimation(animationIndex);
         if (animation == null) {
            return;
         }

         skillContainer.getDataManager().setDataSync(EFNSKillDataKeys.MURASAMA_FORWARD_DODGE, isForwardDodge);
         executor.playSound((SoundEvent)EpicFightSounds.ROLL.get(), 1.0F, 1.0F);
         executor.playAnimationSynchronized(animation, 0.0F);
         executor.setModelYRot(degree, true);
      }
   }
}
