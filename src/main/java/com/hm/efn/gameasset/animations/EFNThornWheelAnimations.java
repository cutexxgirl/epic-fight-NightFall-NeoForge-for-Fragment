package com.hm.efn.gameasset.animations;

import com.hm.efn.animations.types.MoveAttackAnimation;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkills;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Event;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;

public class EFNThornWheelAnimations {
   public static final Collider THORNWHEEL = new MultiOBBCollider(5, 0.75, 0.25, 0.75, 0.0, 0.0, 0.0);
   public static AnimationAccessor<StaticAnimation> THORNWHEEL_IDLE;
   public static AnimationAccessor<AvalonAttackAnimation> THORNWHEEL_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> THORNWHEEL_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> THORNWHEEL_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> THORNWHEEL_AUTO1_MOB;
   public static AnimationAccessor<AvalonAttackAnimation> THORNWHEEL_AUTO2_MOB;
   public static AnimationAccessor<AvalonAttackAnimation> THORNWHEEL_AUTO3_MOB;
   public static AnimationAccessor<AttackAnimation> THORNWHEEL_SKILL_START;
   public static AnimationAccessor<MoveAttackAnimation> THORNWHEEL_SKILL_LOOP;
   public static AnimationAccessor<ActionAnimation> THORNWHEEL_SKILL_END;
   public static AnimationAccessor<AttackAnimation> THORNWHEEL_SKILL_START_N;
   public static AnimationAccessor<MoveAttackAnimation> THORNWHEEL_SKILL_LOOP_N;
   public static AnimationAccessor<ActionAnimation> THORNWHEEL_SKILL_END_N;
   public static final Event LOOPED_ATTACK = (entitypatch, self, params) -> {
      if (entitypatch instanceof PlayerPatch<?> playerPatch) {
         SkillContainer skill = playerPatch.getSkill(EFNSkills.THORNWHEEL);
         if (skill != null) {
            SkillDataManager dataManager = skill.getDataManager();
            boolean isHolding = dataManager.hasData(EFNSKillDataKeys.INNATE_PRESS)
               && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.INNATE_PRESS);
            boolean canLoop = dataManager.hasData(EFNSKillDataKeys.THORNWHEEL_LOOP_AVAILABLE)
               && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.THORNWHEEL_LOOP_AVAILABLE);
            if (isHolding && canLoop) {
               AnimationPlayer animPlayer = entitypatch.getAnimator().getPlayerFor(self);
               if (animPlayer != null) {
                  animPlayer.setElapsedTimeCurrent(Math.max(0.0F, animPlayer.getElapsedTime() - 0.4F));
               }
            }
         }
      }
   };
   public static final Event LOOPED_ATTACK_N = (entitypatch, self, params) -> {
      boolean isHolding = EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown();
      if (entitypatch instanceof PlayerPatch<?> playerPatch && isHolding) {
         AnimationPlayer animPlayer = entitypatch.getAnimator().getPlayerFor(self);
         if (animPlayer != null) {
            animPlayer.setElapsedTimeCurrent(Math.max(0.0F, animPlayer.getElapsedTime() - 0.4F));
         }
      }
   };
   public static final Event PLAY_LOOP_ANIMATION = (entitypatch, self, params) -> {
      if (entitypatch != null && entitypatch.getOriginal() != null) {
         entitypatch.stopPlaying(THORNWHEEL_SKILL_START);
         entitypatch.playAnimationSynchronized(THORNWHEEL_SKILL_LOOP, 0.0F);
      }
   };
   public static final Event PLAY_END_ANIMATION = (entitypatch, self, params) -> {
      if (entitypatch != null && entitypatch.getOriginal() != null) {
         entitypatch.stopPlaying(THORNWHEEL_SKILL_LOOP);
         entitypatch.playAnimationSynchronized(THORNWHEEL_SKILL_END, 0.0F);
      }
   };
   public static final Event PLAY_LOOP_ANIMATION_N = (entitypatch, self, params) -> {
      if (entitypatch != null && entitypatch.getOriginal() != null) {
         entitypatch.stopPlaying(THORNWHEEL_SKILL_START_N);
         entitypatch.playAnimationSynchronized(THORNWHEEL_SKILL_LOOP_N, 0.0F);
      }
   };
   public static final Event PLAY_END_ANIMATION_N = (entitypatch, self, params) -> {
      if (entitypatch != null && entitypatch.getOriginal() != null) {
         entitypatch.stopPlaying(THORNWHEEL_SKILL_LOOP_N);
         entitypatch.playAnimationSynchronized(THORNWHEEL_SKILL_END_N, 0.0F);
      }
   };
   static ArmatureAccessor<Armature> Thornwheel = ArmatureAccessor.create("efn", "weapon/thornwheel", Armature::new);
   static Joint wheel = Thornwheel.get().searchJointByName("wheel");
   static Joint tool = Thornwheel.get().searchJointByName("Tool_R");

   public static void build(AnimationBuilder builder) {
      THORNWHEEL_IDLE = builder.nextAccessor("biped/thornwheel/thornwheel_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      THORNWHEEL_AUTO1 = builder.nextAccessor(
         "biped/thornwheel/thornwheel_attack1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Thornwheel,
               1.0F,
               1.0F,
               new AvalonPhase[]{AvalonAnimationUtils.createSimplePhase(40, 50, 60, InteractionHand.MAIN_HAND, 1.0F, 1.0F, tool, null)}
            )
            .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.adder(3.0F))
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      THORNWHEEL_AUTO2 = builder.nextAccessor(
         "biped/thornwheel/thornwheel_attack2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Thornwheel,
               1.0F,
               1.0F,
               new AvalonPhase[]{AvalonAnimationUtils.createSimplePhase(40, 48, 65, InteractionHand.MAIN_HAND, 1.2F, 1.2F, tool, null)}
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      THORNWHEEL_AUTO3 = builder.nextAccessor(
         "biped/thornwheel/thornwheel_attack3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Thornwheel,
               1.0F,
               1.0F,
               new AvalonPhase[]{AvalonAnimationUtils.createSimplePhase(41, 47, 60, InteractionHand.MAIN_HAND, 1.3F, 2.5F, tool, null)}
            )
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.UNBLOCKALBE))
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(45, 1.0, 0.0, 0.0, 0.0, 2.0F, true)})
      );
      THORNWHEEL_AUTO1_MOB = builder.nextAccessor(
         "biped/thornwheel/thornwheel_attack1_mob",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Thornwheel,
               1.0F,
               1.0F,
               new AvalonPhase[]{AvalonAnimationUtils.createSimplePhase(40, 50, 60, InteractionHand.MAIN_HAND, 1.0F, 1.0F, tool, null)}
            )
            .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.adder(3.0F))
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      THORNWHEEL_AUTO2_MOB = builder.nextAccessor(
         "biped/thornwheel/thornwheel_attack2_mob",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Thornwheel,
               1.0F,
               1.0F,
               new AvalonPhase[]{AvalonAnimationUtils.createSimplePhase(40, 48, 65, InteractionHand.MAIN_HAND, 1.2F, 1.2F, tool, null)}
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      THORNWHEEL_AUTO3_MOB = builder.nextAccessor(
         "biped/thornwheel/thornwheel_attack3_mob",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Thornwheel,
               1.0F,
               1.0F,
               new AvalonPhase[]{AvalonAnimationUtils.createSimplePhase(41, 47, 60, InteractionHand.MAIN_HAND, 1.3F, 2.5F, tool, null)}
            )
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.UNBLOCKALBE))
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(45, 1.0, 0.0, 0.0, 0.0, 2.0F, true)})
      );
      THORNWHEEL_SKILL_START = builder.nextAccessor(
         "biped/thornwheel/thornwheel_start",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               Thornwheel,
               new Phase[]{
                  new Phase(0.0F, 0.7F, 0.8F, 0.9F, 0.9F, wheel, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(0.9F, 0.9F, 1.0F, 1.1F, 1.1F, wheel, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(1.1F, 1.1F, 1.2F, 1.3F, 1.3F, wheel, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(1.3F, 1.3F, 1.4F, 1.5F, 1.5F, wheel, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(1.5F, 1.5F, 1.6F, 1.7F, 1.7F, wheel, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(1.7F, 1.7F, 1.75F, 1.8F, 1.8F, wheel, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
               }
            )
            .addProperty(AttackAnimationProperty.REACH, 1.0F)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
            .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
            .addStateRemoveOld(EntityState.UPDATE_LIVING_MOTION, false)
            .addStateRemoveOld(EntityState.INACTION, true)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(1.75F, PLAY_LOOP_ANIMATION, Side.SERVER)})
      );
      THORNWHEEL_SKILL_LOOP = builder.nextAccessor(
         "biped/thornwheel/thornwheel_loop",
         accessor -> (MoveAttackAnimation)new MoveAttackAnimation(
               0.01F,
               accessor,
               Thornwheel,
               new Phase(0.1F, 0.1F, 0.15F, 0.2F, 0.2F, wheel, THORNWHEEL)
                  .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                  .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
               new Phase(0.2F, 0.2F, 0.25F, 0.3F, 0.3F, wheel, THORNWHEEL)
                  .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                  .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
               new Phase(0.3F, 0.3F, 0.35F, 0.4F, 0.4F, wheel, THORNWHEEL)
                  .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                  .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
               new Phase(0.4F, 0.4F, 0.45F, 0.5F, 0.5F, wheel, THORNWHEEL)
                  .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.25F))
                  .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.25F))
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(0.45F, LOOPED_ATTACK, Side.BOTH)})
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(0.46F, PLAY_END_ANIMATION, Side.SERVER)})
      );
      THORNWHEEL_SKILL_END = builder.nextAccessor(
         "biped/thornwheel/thornwheel_end",
         accessor -> (ActionAnimation)new ActionAnimation(0.01F, 0.5F, accessor, Thornwheel)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      THORNWHEEL_SKILL_START_N = builder.nextAccessor(
         "biped/thornwheel/thornwheel_start_n",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.7F, 0.7F, 0.8F, 0.9F, 0.9F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(0.9F, 0.9F, 1.0F, 1.1F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(1.1F, 1.1F, 1.2F, 1.3F, 1.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(1.3F, 1.3F, 1.4F, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(1.5F, 1.5F, 1.6F, 1.7F, 1.7F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
                  new Phase(1.7F, 1.7F, 1.75F, 1.8F, 1.8F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
               }
            )
            .addProperty(AttackAnimationProperty.REACH, 1.0F)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
            .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
            .addStateRemoveOld(EntityState.UPDATE_LIVING_MOTION, false)
            .addStateRemoveOld(EntityState.INACTION, true)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(1.75F, PLAY_LOOP_ANIMATION_N, Side.SERVER)})
      );
      THORNWHEEL_SKILL_LOOP_N = builder.nextAccessor(
         "biped/thornwheel/thornwheel_loop_n",
         accessor -> (MoveAttackAnimation)new MoveAttackAnimation(
               0.01F,
               accessor,
               Armatures.BIPED,
               new Phase(0.1F, 0.1F, 0.15F, 0.2F, 0.2F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
               new Phase(0.2F, 0.2F, 0.25F, 0.3F, 0.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
               new Phase(0.3F, 0.3F, 0.35F, 0.4F, 0.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE),
               new Phase(0.4F, 0.4F, 0.45F, 0.5F, 0.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, THORNWHEEL)
                  .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(0.45F, LOOPED_ATTACK_N, Side.BOTH)})
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(0.46F, PLAY_END_ANIMATION_N, Side.SERVER)})
      );
      THORNWHEEL_SKILL_END_N = builder.nextAccessor(
         "biped/thornwheel/thornwheel_end_n",
         accessor -> (ActionAnimation)new ActionAnimation(0.01F, 0.5F, accessor, Armatures.BIPED)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
   }
}
