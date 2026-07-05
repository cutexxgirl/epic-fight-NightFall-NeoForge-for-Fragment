package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNThornWheelAnimations;
import com.hm.efn.mobeffects.BloodExplosionEffect;
import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.PlayerInputState;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class ThornWheelSkill extends WeaponInnateSkill {
   private static final UUID ANIMATION_EVENT_UUID = UUID.fromString("d34db32f-5a1d-4b3a-8f1c-2929810c2d1a");
   private static final UUID MOVEMENT_EVENT_UUID = UUID.fromString("d34db31f-5a1d-4b3a-5f2c-1919820c2d1a");
   private static final UUID BLOOD_EXPLOSION_DAMAGE_UUID = UUID.fromString("d34db33f-5a1d-4b3a-9f1c-2929810c2d1a");

   public ThornWheelSkill(WeaponInnateSkill.Builder<?> builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      SkillDataManager data = container.getDataManager();
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.MOVEMENT_INPUT_EVENT, MOVEMENT_EVENT_UUID, event -> {
         if ((Boolean)data.getDataValue(EFNSKillDataKeys.THORNWHEEL_LOOPING)) {
            PlayerInputState current = event.getInputState();
            PlayerInputState updated = current.withForwardImpulse(current.forwardImpulse() * 0.35F).withLeftImpulse(current.leftImpulse() * 0.35F);
            InputManager.setInputState(updated);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.ATTACK_PHASE_END_EVENT, ANIMATION_EVENT_UUID, event -> {
         if (event.getAnimation().equals(EFNThornWheelAnimations.THORNWHEEL_SKILL_LOOP)) {
            float maxStamina = container.getExecutor().getMaxStamina();
            float stamina = container.getExecutor().getStamina();
            float staminaCost = maxStamina * 0.05F;
            container.getExecutor().setStamina(stamina - staminaCost);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.DEAL_DAMAGE_EVENT_HURT,
         BLOOD_EXPLOSION_DAMAGE_UUID,
         event -> {
            ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)event.getPlayerPatch();
            ServerPlayer attacker = (ServerPlayer)serverPlayerPatch.getOriginal();
            LivingEntity target = event.getTarget();
            AnimationAccessor<? extends StaticAnimation> attackAnimation = event.getDamageSource().getAnimation();
            List<AnimationAccessor<? extends AttackAnimation>> attackMotions = serverPlayerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND)
               .getAutoAttackMotion(serverPlayerPatch);
            if (attackMotions.contains(attackAnimation)) {
               this.handleBloodExplosionBuff(target, attacker, 10);
            } else if (attackAnimation == EFNThornWheelAnimations.THORNWHEEL_SKILL_START || attackAnimation == EFNThornWheelAnimations.THORNWHEEL_SKILL_LOOP) {
               this.handleBloodExplosionBuff(target, attacker, 3);
            }
         }
      );
   }

   private void handleBloodExplosionBuff(LivingEntity target, Player attacker, int amplifierAdder) {
      MobEffectInstance existingEffect = target.getEffect(EFNMobEffectRegistry.BLOOD_EXPLOSION);
      if (existingEffect == null) {
         BloodExplosionEffect.setAttacker(target, attacker);
         MobEffectInstance newEffect = new MobEffectInstance(EFNMobEffectRegistry.BLOOD_EXPLOSION, 600, amplifierAdder - 1, false, true, true);
         target.addEffect(newEffect, attacker);
      } else {
         int current = existingEffect.getAmplifier();
         if (current < 59) {
            BloodExplosionEffect.setAttacker(target, attacker);
            int amplifier = Mth.clamp(current + amplifierAdder, 0, 59);
            int remainingDuration = amplifier >= 59 ? 100 : existingEffect.getDuration();
            MobEffectInstance upgradedEffect = new MobEffectInstance(
               EFNMobEffectRegistry.BLOOD_EXPLOSION, remainingDuration, amplifier, false, true, true
            );
            target.removeEffect(EFNMobEffectRegistry.BLOOD_EXPLOSION);
            target.addEffect(upgradedEffect, attacker);
         }
      }

      target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 0.8F, 1.2F);
   }

   public void executeOnServer(SkillContainer container, CompoundTag args) {
      container.getExecutor().playAnimationSynchronized(EFNThornWheelAnimations.THORNWHEEL_SKILL_START, 0.0F);
      super.executeOnServer(container, args);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      SkillDataManager dataManager = container.getDataManager();
      PlayerPatch<?> executor = container.getExecutor();
      if (container.getExecutor().isLogicalClient()) {
         boolean isHolding = EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown();
         dataManager.setDataSync(EFNSKillDataKeys.INNATE_PRESS, isHolding);
      }

      if (!container.getExecutor().isLogicalClient()) {
         boolean hasEnoughStamina = ((Player)executor.getOriginal()).isCreative() || executor.getStamina() > 3.0F;
         dataManager.setDataSync(EFNSKillDataKeys.THORNWHEEL_LOOP_AVAILABLE, hasEnoughStamina);
         boolean isHolding = dataManager.hasData(EFNSKillDataKeys.INNATE_PRESS)
            && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.INNATE_PRESS);
         boolean canLoop = dataManager.hasData(EFNSKillDataKeys.THORNWHEEL_LOOP_AVAILABLE)
            && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.THORNWHEEL_LOOP_AVAILABLE);
         boolean isLoop = canLoop && isHolding;
         dataManager.setDataSync(EFNSKillDataKeys.THORNWHEEL_LOOPING, isLoop);
      }
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.MOVEMENT_INPUT_EVENT, MOVEMENT_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ATTACK_PHASE_END_EVENT, ANIMATION_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_HURT, BLOOD_EXPLOSION_DAMAGE_UUID);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(EpicFightKeyMappings.WEAPON_INNATE_SKILL.getName()).getString();
      list.add(Component.translatable("skill.efn.thornwheel.tooltip").withStyle(ChatFormatting.RED));
      list.add(
         Component.translatable("skill.efn.thornwheel.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .append(EpicFightKeyMappings.WEAPON_INNATE_SKILL.getTranslatedKeyMessage())
            .append("——")
            .append(Component.translatable("skill.efn.thornwheel.tooltip2"))
      );
      list.add(Component.translatable("skill.efn.thornwheel.tooltip3"));
      return list;
   }
}
