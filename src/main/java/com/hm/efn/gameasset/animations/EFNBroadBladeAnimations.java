package com.hm.efn.gameasset.animations;

import com.guhao.vix.camera.CameraEvents;
import com.hm.efn.animations.types.broadblade.BroadBladeAttackAnimation;
import com.hm.efn.animations.types.stun.EFNStunAnimation;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.E0;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.damagesource.StunType;

public class EFNBroadBladeAnimations {
   public static final Collider BROADBLADE = new MultiOBBCollider(3, 0.4, 0.4, 1.0, 0.0, 0.0, -1.0);
   public static final PlaybackSpeedModifier PLAYBACK_SPEED_MODIFIER = (dynamicAnimation, livingEntityPatch, v, v1, v2) -> {
      if (dynamicAnimation.isLinkAnimation()) {
         return 1.0F;
      } else {
         return ((LivingEntity)livingEntityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.BATTLE_CONTINUATION) ? 1.5F : 1.15F;
      }
   };
   public static AnimationAccessor<StaticAnimation> BROADBLADE_IDLE;
   public static AnimationAccessor<StaticAnimation> BROADBLADE_KNEEL;
   public static AnimationAccessor<StaticAnimation> BROADBLADE_GUARD;
   public static AnimationAccessor<GuardAnimation> BROADBLADE_GUARD_HIT;
   public static AnimationAccessor<AvalonMovementAnimation> BROADBLADE_SNEAK;
   public static AnimationAccessor<AvalonMovementAnimation> BROADBLADE_WALK;
   public static AnimationAccessor<AvalonMovementAnimation> BROADBLADE_RUN;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AUTO1;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AUTO2;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AUTO3;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AUTO4;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AUTO5;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AUTO6;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AUTO7;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AUTO8;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_DASHSLASH;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_AIRSLASH;
   public static AnimationAccessor<BroadBladeAttackAnimation> BROADBLADE_COUNTER;
   public static AnimationAccessor<EFNStunAnimation> BROADBLADE_EXECUTED;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<Armature> Broadblade = ArmatureAccessor.create("efn", "weapon/broadblade", Armature::new);
      Joint weapon = Broadblade.get().searchJointByName("Tool_R");
      BROADBLADE_IDLE = builder.nextAccessor("biped/broadblade/broadblade_idle", accessor -> new StaticAnimation(0.1F, true, accessor, Broadblade));
      BROADBLADE_GUARD = builder.nextAccessor("biped/broadblade/broadblade_guard", accessor -> new StaticAnimation(0.15F, true, accessor, Broadblade));
      BROADBLADE_GUARD_HIT = builder.nextAccessor("biped/broadblade/broadblade_guard_hit", accessor -> new GuardAnimation(0.1F, 0.15F, accessor, Broadblade));
      BROADBLADE_WALK = builder.nextAccessor(
         "biped/broadblade/broadblade_walk", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, Broadblade, 1.7F)
      );
      BROADBLADE_RUN = builder.nextAccessor("biped/broadblade/broadblade_run", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, Broadblade, 1.0F));
      BROADBLADE_KNEEL = builder.nextAccessor("biped/broadblade/broadblade_kneel", accessor -> new StaticAnimation(0.1F, true, accessor, Broadblade));
      BROADBLADE_SNEAK = builder.nextAccessor(
         "biped/broadblade/broadblade_sneak", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, Broadblade, 1.0F)
      );
      BROADBLADE_AUTO1 = builder.nextAccessor(
         "biped/broadblade/broadblade_auto1",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F,
               accessor,
               Broadblade,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(12, 18, 45, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(44, 50, 51, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_AUTO2 = builder.nextAccessor(
         "biped/broadblade/broadblade_auto2",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F, accessor, Broadblade, 1.0F, 1.0F, AvalonAnimationUtils.createSimplePhase(39, 45, 46, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_AUTO3 = builder.nextAccessor(
         "biped/broadblade/broadblade_auto3",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F,
               accessor,
               Broadblade,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(17, 26, 49, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(50, 58, 74, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(75, 80, 81, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_AUTO4 = builder.nextAccessor(
         "biped/broadblade/broadblade_auto4",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F,
               accessor,
               Broadblade,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(10, 16, 64, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(65, 70, 71, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_AUTO5 = builder.nextAccessor(
         "biped/broadblade/broadblade_auto5",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F,
               accessor,
               Broadblade,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(20, 30, 24, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(42, 52, 62, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(63, 70, 71, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_AUTO6 = builder.nextAccessor(
         "biped/broadblade/broadblade_auto6",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F,
               accessor,
               Broadblade,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(10, 20, 27, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(28, 41, 73, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(74, 80, 81, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_AUTO7 = builder.nextAccessor(
         "biped/broadblade/broadblade_auto7",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F,
               accessor,
               Broadblade,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(10, 22, 29, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(30, 40, 41, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_AUTO8 = builder.nextAccessor(
         "biped/broadblade/broadblade_auto8",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F,
               accessor,
               Broadblade,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(11, 21, 30, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(31, 41, 53, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null),
               AvalonAnimationUtils.createSimplePhase(54, 60, 64, InteractionHand.MAIN_HAND, 2.5F, 1.0F, weapon, null)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL),
               AvalonAnimationUtils.createSimplePhase(65, 69, 77, InteractionHand.MAIN_HAND, 2.5F, 1.0F, weapon, null)
                  .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                  .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
                  .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_DASHSLASH = builder.nextAccessor(
         "biped/broadblade/broadblade_dash_slash",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.15F, accessor, Broadblade, 1.0F, 1.0F, AvalonAnimationUtils.createSimplePhase(10, 25, 35, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
      );
      BROADBLADE_AIRSLASH = builder.nextAccessor(
         "biped/broadblade/broadblade_air_slash",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.15F, accessor, Broadblade, 1.0F, 1.0F, AvalonAnimationUtils.createSimplePhase(5, 15, 25, InteractionHand.MAIN_HAND, 1.0F, 1.0F, weapon, null)
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, PLAYBACK_SPEED_MODIFIER)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 0.25F}))
      );
      BROADBLADE_COUNTER = builder.nextAccessor(
         "biped/broadblade/broadblade_counter",
         accessor -> (BroadBladeAttackAnimation)new BroadBladeAttackAnimation(
               0.1F, accessor, Broadblade, 1.0F, 1.0F, AvalonAnimationUtils.createSimplePhase(30, 40, 50, InteractionHand.MAIN_HAND, 1.5F, 1.0F, weapon, null)
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.25F)
      );
      BROADBLADE_EXECUTED = builder.nextAccessor(
         "biped/broadblade/broadblade_executed",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.1F, Float.MAX_VALUE, accessor, Armatures.BIPED)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
   }

   private static InTimeEvent<E0> cameraAnim() {
      return InTimeEvent.create(0.95F, (E0)(entityPatch, animation, params) -> {
         if (entityPatch instanceof LocalPlayerPatch localPlayerPatch && localPlayerPatch.isLastAttackSuccess()) {
            CameraEvents.SetAnim(EFNAnimations.BROADBLADE_EXECUTE, (LivingEntity)localPlayerPatch.getOriginal(), true, BROADBLADE_AUTO8);
         }
      }, Side.CLIENT);
   }
}
