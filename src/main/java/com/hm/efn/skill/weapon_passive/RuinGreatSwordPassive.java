package com.hm.efn.skill.weapon_passive;

import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.gameasset.combos.EFNComboNodes;
import com.hm.efn.gameasset.combos.Ruinsgreatsword;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.mojang.text2speech.Narrator;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class RuinGreatSwordPassive extends PassiveSkill {
   private static final float COUNTER_WINDOW_MIN = 0.3F;
   private static final float COUNTER_WINDOW_MAX = 1.1F;
   private static final float CHARGE_WINDOW_START = 0.0F;
   private static final float CHARGE_WINDOW_END = 0.3F;
   private static final UUID CHARGE_ANIMATION_LISTENER_UUID = UUID.fromString("e5f6a1b2-c3d4-11ed-a05b-0242ac114511");
   private static final UUID DAMAGE_EVENT_UUID = UUID.fromString("e5f6a1b2-c3d4-11ed-a51b-0242ac114512");

   public RuinGreatSwordPassive(SkillBuilder<?> builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      PlayerPatch<?> executer = container.getExecutor();
      Player player = (Player)executer.getOriginal();
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, DAMAGE_EVENT_UUID, event -> {
         if (container.getExecutor() instanceof ServerPlayerPatch playerPatch) {
            if (!player.isSpectator()) {
               AnimationPlayer clashAnim = playerPatch.getAnimator().getPlayerFor(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH);
               if (clashAnim != null && clashAnim.getAnimation() == EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH) {
                  int phaseLevel = ((ServerPlayerPatch)event.getPlayerPatch()).getEntityState().getLevel();
                  if (phaseLevel > 0 && phaseLevel < 3) {
                     event.cancel();
                     event.setResult(ResultType.BLOCKED);
                     playerPatch.playAnimationSynchronized(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH_HIT, 0.0F);
                     return;
                  }
               }

               AnimationPlayer chargeAnim = playerPatch.getAnimator().getPlayerFor(EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_FIRST);
               if (chargeAnim != null && chargeAnim.getAnimation() == EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_FIRST) {
                  float elapsedTime = chargeAnim.getElapsedTime();
                  if (elapsedTime >= 0.0F && elapsedTime <= 0.3F) {
                     event.cancel();
                     event.setResult(ResultType.BLOCKED);
                     playerPatch.playAnimationSynchronized(EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_FIRST, -0.15F);
                     playerPatch.playSound((SoundEvent)EpicFightSounds.ENTITY_MOVE.get(), 130.0F, 0.0F, 0.0F);
                     player.addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, false, false, false));
                     player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 5, false, false, false));
                     player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 5, false, false, false));
                     if (Ruinsgreatsword.RuinExtendRoot_1_GP == null) {
                        Ruinsgreatsword.RuinExtendRoot_1_GP = EFNComboNodes.create(() -> EFNGreatSwordAnimations.NG_GREATSWORD_IDLE);
                     }

                     InvincibleAttachments.getPlayer(player).setCurrentNode(Ruinsgreatsword.RuinExtendRoot_1_GP);
                  }
               }
            }
         }
      }, 0);
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, CHARGE_ANIMATION_LISTENER_UUID, event -> {
         if (event.getPlayerPatch() instanceof ServerPlayerPatch serverPatch && serverPatch.getOriginal() instanceof ServerPlayer) {
            this.handleChargeAnimationStart(serverPatch, event.getAnimation());
         }
      });
   }

   private void handleChargeAnimationStart(ServerPlayerPatch playerPatch, StaticAnimation animation) {
      try {
         if (playerPatch == null || playerPatch.getOriginal() == null) {
            return;
         }

         ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
         player.level();
         if (player.isRemoved()) {
            return;
         }
      } catch (Exception e) {
         Narrator.LOGGER.error("动画出错", e);
      }

      if (playerPatch.getOriginal() instanceof ServerPlayer) {
         ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
         if (animation == EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MIN.get()) {
            InvincibleAttachments.getPlayer(player).setCurrentNode(Ruinsgreatsword.RuinExtendRoot_2_ChargeMin);
            this.spawnChargeParticles(player, 0.5F);
         } else if (animation == EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_FIRST.get()) {
            InvincibleAttachments.getPlayer(player).setCurrentNode(Ruinsgreatsword.RuinExtendRoot_3_ChargeMax);
            this.spawnChargeParticles(player, 1.0F);
            playerPatch.playSound((SoundEvent)SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.1F, 0.0F, 0.0F);
         } else if (animation == EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH_HIT.get()) {
            InvincibleAttachments.getPlayer(player).setCurrentNode(Ruinsgreatsword.RuinExtendRoot_1_Clash);
         }
      }
   }

   private void spawnChargeParticles(ServerPlayer player, float intensity) {
      Vec3 pos = player.position();
      ((ServerLevel)player.level())
         .sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1.5, pos.z, (int)(15.0F * intensity), 0.5, 0.5, 0.5, 0.2 * intensity);
   }

   public void onRemoved(SkillContainer container) {
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, CHARGE_ANIMATION_LISTENER_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, DAMAGE_EVENT_UUID, 0);
   }
}
