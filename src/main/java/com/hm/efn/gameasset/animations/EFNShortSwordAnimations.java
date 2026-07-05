package com.hm.efn.gameasset.animations;

import com.hm.efn.gameasset.EFNAnimations;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.Set;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

public class EFNShortSwordAnimations {
   public static AnimationAccessor<AvalonAttackAnimation> NF_SHORTSWORD_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SHORTSWORD_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SHORTSWORD_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SHORTSWORD_AUTO4;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SHORTSWORD_AUTO5;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SHORTSWORD_AUTO6;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SHORTSWORD_DASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SHORTSWORD_AIRSLASH;
   public static AnimationAccessor<StaticAnimation> NF_SHORTSWORD_SKILL;

   public static void build(AnimationBuilder builder) {
      NF_SHORTSWORD_SKILL = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_skill",
         accessor -> new StaticAnimation(0.05F, false, accessor, Armatures.BIPED)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SHORTSWORD)
      );
      NF_SHORTSWORD_AUTO1 = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     10, 13, 18, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SHORTSWORD)
      );
      NF_SHORTSWORD_AUTO2 = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(5, 9, 14, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SHORTSWORD)
      );
      NF_SHORTSWORD_AUTO3 = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     6, 10, 11, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     12, 16, 19, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 0.95F)
      );
      NF_SHORTSWORD_AUTO4 = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_auto4",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     10, 14, 23, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     24, 28, 30, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SHORTSWORD)
      );
      NF_SHORTSWORD_AUTO5 = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_auto5",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     11, 14, 19, InteractionHand.MAIN_HAND, 1.1F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SHORTSWORD)
      );
      NF_SHORTSWORD_AUTO6 = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_auto6",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     32, 37, 45, InteractionHand.MAIN_HAND, 1.2F, 1.2F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SHORTSWORD)
      );
      NF_SHORTSWORD_DASH = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_dash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     14, 19, 21, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     25, 30, 38, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SHORTSWORD)
      );
      NF_SHORTSWORD_AIRSLASH = builder.nextAccessor(
         "biped/nf_shortsword/nf_shortsword_airslash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     12, 18, 19, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     22, 28, 38, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SHORTSWORD)
      );
   }
}
