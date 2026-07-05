package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.EFN;
import com.hm.efn.EFNCommonConfig;
import com.hm.efn.event.TickChange;
import com.hm.efn.gameasset.animations.EFNDualSwordAnimations;
import com.hm.efn.gameasset.combos.Aetherialdusk;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class AetherialDuskDualSwordInnate extends EFNWeaponInnateBase {
   private static final UUID DODGE_SUCCESS_UUID = UUID.fromString("a496c93a-42cb-12eb-b378-0242ac170004");
   private static final UUID DAMAGE_EVENT_UUID = UUID.fromString("b496c93a-42cb-21eb-b378-0242ac170005");

   public AetherialDuskDualSwordInnate(Builder builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      SkillDataManager data = container.getDataManager();
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.DODGE_SUCCESS_EVENT,
         DODGE_SUCCESS_UUID,
         event -> {
            ServerPlayerPatch playerPatch = (ServerPlayerPatch)container.getExecutor();
            ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
            if (player.isAlive() && player.getVehicle() == null && this.isHoldingWeapon(container)) {
               playerPatch.playAnimationSynchronized(EFNDualSwordAnimations.NF_DUAL_DODGE, 0.0F);
               playerPatch.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.4F, 0.8F, 1.2F);
               player.addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, false, false, false));
               player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 5, false, false, false));
               if ((Boolean)EFNCommonConfig.ENABLE_DUALSOWRD_DODGE_TIMESLOWDOWN.get()) {
                  Level level = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).level();
                  MinecraftServer server = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).getServer();
                  if (server == null) {
                     return;
                  }

                  int globalPlayerCount = server.getPlayerCount();
                  boolean isDedicatedServer = level.getServer() != null && level.getServer().isDedicatedServer();
                  if (!isDedicatedServer) {
                     ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 3, false, false, false));
                     int BegindelayTicks = (Integer)EFNCommonConfig.DUALSOWRD_DODGE_TIMESLOW_DELAY_BEGIN.get();
                     int EndTicks = (Integer)EFNCommonConfig.DUALSOWRD_DODGE_TIMESLOW_DELAY_END.get();
                     int amplifier = (Integer)EFNCommonConfig.DUALSOWRD_DODGE_TIMESLOW_AMPLIFIER.get();
                     if (globalPlayerCount <= 1) {
                        EFN.queueServerWork(BegindelayTicks, () -> {
                           EFN.queueServerWork(BegindelayTicks, () -> TickChange.requestChange(amplifier));
                           EFN.queueServerWork(EndTicks, () -> TickChange.requestChange(20.0F));
                        });
                     }
                  }
               }

               InvincibleAttachments.getPlayer(player).setCurrentNode(Aetherialdusk.DodgeCounter);
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, DAMAGE_EVENT_UUID, event -> {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)container.getExecutor();
         ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
         if (player.isAlive() && this.isHoldingWeapon(container)) {
            AnimationPlayer dodgeAnim = playerPatch.getAnimator().getPlayerFor(EFNDualSwordAnimations.NF_DUAL_DODGE);
            if (dodgeAnim != null && dodgeAnim.getAnimation() == EFNDualSwordAnimations.NF_DUAL_DODGE) {
               event.cancel();
               event.setResult(ResultType.MISSED);
            }
         }
      }, -1);
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, this);
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.DODGE_SUCCESS_EVENT, DODGE_SUCCESS_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, DAMAGE_EVENT_UUID, -1);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.aetherialdusk.tooltip").withStyle(ChatFormatting.AQUA));
      list.add(
         Component.translatable("skill.efn.aetherialdusk.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.AQUA)
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.aetherialdusk.tooltip2"));
      list.add(Component.translatable("skill.efn.aetherialdusk.tooltip3").withStyle(ChatFormatting.BLUE));
      list.add(Component.translatable("skill.efn.aetherialdusk.tooltip4"));
      return list;
   }
}
