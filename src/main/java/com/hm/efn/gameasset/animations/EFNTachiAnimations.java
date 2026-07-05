package com.hm.efn.gameasset.animations;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.BurstRedEffek;
import com.hm.efn.entity.EFNVFXManagers;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffectConditionParticleTrail;
import com.hm.efn.util.EffekUnits;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

public class EFNTachiAnimations {
   public static AnimationAccessor<StaticAnimation> NF_TACHI_IDLE;
   public static AnimationAccessor<MovementAnimation> NF_TACHI_WALK;
   public static AnimationAccessor<MovementAnimation> NF_TACHI_RUN;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_AUTO4;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_AUTO5;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_DASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_AIRSLASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_BLOODLUST;
   public static AnimationAccessor<AvalonAttackAnimation> NF_TACHI_BLOODLUST_END;

   public static void build(AnimationBuilder builder) {
      NF_TACHI_IDLE = builder.nextAccessor("biped/nf_tachi/nf_tachi_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      NF_TACHI_WALK = builder.nextAccessor("biped/nf_tachi/nf_tachi_walk", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      NF_TACHI_RUN = builder.nextAccessor("biped/nf_tachi/nf_tachi_run", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      NF_TACHI_AUTO1 = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     18, 24, 31, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonConditionalAnimationTextureVFX(
                     EFNVFXManagers.BLOOD_SLASH, 5, 1.5, -1.3, 0.2F, 1.7F, new Vec3f(0.0F, 0.0F, 0.0F), EFNMobEffectRegistry.BLODDLUST
                  )
               }
            )
      );
      NF_TACHI_AUTO2 = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     19, 24, 31, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonConditionalAnimationTextureVFX(
                     EFNVFXManagers.BLOOD_SLASH, 5, 1.5, -1.3, -0.65F, 1.7F, new Vec3f(0.0, 0.0, -30.0), EFNMobEffectRegistry.BLODDLUST
                  )
               }
            )
      );
      NF_TACHI_AUTO3 = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     23, 28, 36, InteractionHand.MAIN_HAND, 1.05F, 1.05F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonConditionalAnimationTextureVFX(
                     EFNVFXManagers.BLOOD_SLASH, 8, 1.5, -1.3, -1.1F, 1.7F, new Vec3f(0.0, 0.0, -60.0), EFNMobEffectRegistry.BLODDLUST
                  )
               }
            )
      );
      NF_TACHI_AUTO4 = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_auto4",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     23, 32, 39, InteractionHand.MAIN_HAND, 1.05F, 1.05F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonConditionalAnimationTextureVFX(
                     EFNVFXManagers.BLOOD_SLASH, 8, 1.5, -1.0, 2.0, 1.7F, new Vec3f(0.0, 0.0, 90.0), EFNMobEffectRegistry.BLODDLUST
                  )
               }
            )
      );
      NF_TACHI_AUTO5 = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_auto5",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.08F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     22, 29, 40, InteractionHand.MAIN_HAND, 1.1F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonConditionalAnimationTextureVFX(
                     EFNVFXManagers.BLOOD_SLASH, 11, 1.5, -1.3, 0.2F, 1.7F, new Vec3f(0.0, 0.0, 0.0), EFNMobEffectRegistry.BLODDLUST
                  )
               }
            )
      );
      NF_TACHI_DASH = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_dash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.08F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     19, 27, 37, InteractionHand.MAIN_HAND, 1.2F, 1.2F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonConditionalAnimationTextureVFX(
                     EFNVFXManagers.BLOOD_SLASH, 5, 1.5, -1.3, -1.2F, 1.7F, new Vec3f(0.0, 0.0, -60.0), EFNMobEffectRegistry.BLODDLUST
                  )
               }
            )
      );
      NF_TACHI_AIRSLASH = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_airslash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.08F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     27, 39, 55, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.2F}))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonConditionalAnimationTextureVFX(
                     EFNVFXManagers.BLOOD_SLASH, 19, 1.5, -3.0, 0.3F, 1.4F, new Vec3f(0.0, 0.0, 0.0), EFNMobEffectRegistry.BLODDLUST
                  )
               }
            )
      );
      NF_TACHI_BLOODLUST = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_bloodlust",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     38, 48, 55, InteractionHand.MAIN_HAND, 1.7F, 1.7F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
            .addEvents(
               new AnimationEvent[]{
                  EffectConditionParticleTrail.buffedParticleTrail(
                     30,
                     65,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -1.1F),
                     new Vec3(0.0, 0.0, -1.4F),
                     8.0F,
                     4,
                     ParticleTypes.CRIMSON_SPORE,
                     0.5F,
                     EFNMobEffectRegistry.BLODDLUST
                  ),
                  EFNVFXManagers.summonConditionalAnimationTextureVFX(
                     EFNVFXManagers.BLOOD_SLASH, 23, 1.5, -1.3, -0.65F, 1.7F, new Vec3f(0.0, 0.0, -30.0), EFNMobEffectRegistry.BLODDLUST
                  ),
                  InTimeEvent.create(
                     0.45F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()) {
                           if ((Boolean)EFNClientConfig.TACHI_SKILL_VFX.get()) {
                              BurstRedEffek.playBurstRed(
                                 BurstRedEffek.Type.LEVEL3,
                                 ((LivingEntity)entityPatch.getOriginal()).level(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().x(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().y(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().z(),
                                 0.0F,
                                 EffekUnits.getRY(entityPatch),
                                 (float) (Math.PI / 2),
                                 1.0F
                              );
                           }
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NF_TACHI_BLOODLUST_END = builder.nextAccessor(
         "biped/nf_tachi/nf_tachi_bloodlust_end",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     23, 34, 47, InteractionHand.MAIN_HAND, 1.45F, 1.45F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_TACHI)
      );
   }
}
