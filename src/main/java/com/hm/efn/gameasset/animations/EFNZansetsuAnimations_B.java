package com.hm.efn.gameasset.animations;

import com.hm.efn.animations.types.murasama.MurasamaAnimationUtils;
import com.hm.efn.animations.types.murasama.ZansetsuAttackAnimation;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.particle.EFNParticles;
import com.merlin204.avalon.util.AvalonEventUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

public class EFNZansetsuAnimations_B {
   public static final Collider HF_BLADE_LONG_HITBOX = new MultiOBBCollider(3, 0.8, 0.8, 1.5, 0.0, 0.0, -0.95);
   public static final PlaybackSpeedModifier EFNZansetsuPlaybackSpeedModifier = (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
      if (self.isLinkAnimation()) {
         return 1.0F;
      }

      boolean shouldRelease = false;
      boolean isZansetsuActive = false;
      if (entitypatch instanceof PlayerPatch<?> playerPatch) {
         SkillContainer skill = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (skill != null) {
            SkillDataManager dataManager = skill.getDataManager();
            if (dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
               isZansetsuActive = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
            }

            if (dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED)) {
               shouldRelease = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED);
            }
         }
      }

      float currentFrame = elapsedTime * 60.0F;
      boolean shouldSlowDown = isZansetsuActive && !shouldRelease && currentFrame <= 6.0F;
      if (!shouldSlowDown) {
         return 1.1F;
      } else {
         float PHASE1_START_FRAME = 0.0F;
         float PHASE1_END_FRAME = 4.0F;
         float PHASE2_START_FRAME = 4.0F;
         float PHASE2_END_FRAME = 6.0F;
         float MIN_SPEED = 1.0E-12F;
         if (currentFrame >= 0.0F && currentFrame <= 4.0F) {
            float t = (currentFrame - 0.0F) / 4.0F;
            float oneMinusT = 1.0F - t;
            float oneMinusT2 = oneMinusT * oneMinusT;
            float oneMinusT3 = oneMinusT2 * oneMinusT;
            float t2 = t * t;
            float t3 = t2 * t;
            float p0 = 1.0F;
            float p1 = 0.8F;
            float p2 = 0.7F;
            float p3 = 0.5F;
            return oneMinusT3 * p0 + 3.0F * oneMinusT2 * t * p1 + 3.0F * oneMinusT * t2 * p2 + t3 * p3;
         } else if (currentFrame >= 4.0F && currentFrame <= 6.0F) {
            float t = (currentFrame - 4.0F) / 2.0F;
            float oneMinusT = 1.0F - t;
            float oneMinusT2 = oneMinusT * oneMinusT;
            float oneMinusT3 = oneMinusT2 * oneMinusT;
            float t2 = t * t;
            float t3 = t2 * t;
            float p0 = 0.15F;
            float p1 = 0.11F;
            float p2 = 0.11F;
            float p3 = 1.0E-12F;
            return oneMinusT3 * p0 + 3.0F * oneMinusT2 * t * p1 + 3.0F * oneMinusT * t2 * p2 + t3 * p3;
         } else {
            return 1.0E-12F;
         }
      }
   };
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_LR_DOWN;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_LR_MID;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_LR_UP;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_LR_DOWN_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_LR_MID_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_LR_UP_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_RL_DOWN;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_RL_MID;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_RL_UP;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_RL_DOWN_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_RL_MID_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_HORIZONTAL_RL_UP_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_DIAGONAL_LR_DOWN;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_DIAGONAL_LR_UP;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_DIAGONAL_LR_DOWN_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_DIAGONAL_LR_UP_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_DIAGONAL_RL_DOWN;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_DIAGONAL_RL_UP;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_DIAGONAL_RL_DOWN_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_DIAGONAL_RL_UP_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_DU_L;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_DU_M;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_DU_R;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_DU_L_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_DU_M_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_DU_R_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_UD_L;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_UD_M;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_UD_R;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_UD_L_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_UD_M_AIR;
   public static AnimationAccessor<ZansetsuAttackAnimation> HF_BLADE_SLASH_VERTICAL_UD_R_AIR;

   public static void build(AnimationBuilder builder) {
      HF_BLADE_SLASH_HORIZONTAL_LR_DOWN = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/left_right/slash_lr_down",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_LR_MID = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/left_right/slash_lr_mid",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_LR_UP = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/left_right/slash_lr_up",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_LR_DOWN_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/left_right/slash_lr_down_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_LR_MID_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/left_right/slash_lr_mid_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_LR_UP_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/left_right/slash_lr_up_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_RL_DOWN = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/right_left/slash_rl_down",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_RL_MID = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/right_left/slash_rl_mid",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_RL_UP = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/right_left/slash_rl_up",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_RL_DOWN_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/right_left/slash_rl_down_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_RL_MID_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/right_left/slash_rl_mid_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_HORIZONTAL_RL_UP_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/horizontal/right_left/slash_rl_up_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_DIAGONAL_LR_DOWN = builder.nextAccessor(
         "biped/hf_blade/slash/diagonal/left_right/slash_lr_down",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_DIAGONAL_LR_UP = builder.nextAccessor(
         "biped/hf_blade/slash/diagonal/left_right/slash_lr_up",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_DIAGONAL_LR_DOWN_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/diagonal/left_right/slash_lr_down_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_DIAGONAL_LR_UP_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/diagonal/left_right/slash_lr_up_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_DIAGONAL_RL_DOWN = builder.nextAccessor(
         "biped/hf_blade/slash/diagonal/right_left/slash_rl_down",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_DIAGONAL_RL_UP = builder.nextAccessor(
         "biped/hf_blade/slash/diagonal/right_left/slash_rl_up",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_DIAGONAL_RL_DOWN_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/diagonal/right_left/slash_rl_down_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_DIAGONAL_RL_UP_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/diagonal/right_left/slash_rl_up_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_DU_L = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/down_up/slash_left",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_DU_M = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/down_up/slash_mid",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_DU_R = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/down_up/slash_right",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_DU_L_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/down_up/slash_left_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_DU_M_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/down_up/slash_mid_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_DU_R_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/down_up/slash_right_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_UD_L = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/up_down/slash_left",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_UD_M = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/up_down/slash_mid",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_UD_R = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/up_down/slash_right",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_UD_L_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/up_down/slash_left_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_UD_M_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/up_down/slash_mid_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
      HF_BLADE_SLASH_VERTICAL_UD_R_AIR = builder.nextAccessor(
         "biped/hf_blade/slash/vertical/up_down/slash_right_air",
         accessor -> (ZansetsuAttackAnimation)new ZansetsuAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  7,
                  16,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.HF_BLADE_HIT)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNZansetsuPlaybackSpeedModifier)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 3.0F)})
      );
   }
}
