package com.hm.efn.gameasset.animations;

import com.hm.efn.gameasset.EFNAnimations;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.Set;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class EFNSwordAnimations {
   public static final Collider Pisword = new OBBCollider(0.6, 0.6, 0.6, 0.0, 0.2, -0.2);
   public static AnimationAccessor<StaticAnimation> NF_SWORD_IDLE;
   public static AnimationAccessor<StaticAnimation> NF_SWORD_GUARD;
   public static AnimationAccessor<MovementAnimation> NF_SWORD_WALK;
   public static AnimationAccessor<MovementAnimation> NF_SWORD_RUN;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_AUTO4;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_DASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_AIRSLASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_SKILL;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_SKILL_FIRST;
   public static AnimationAccessor<AvalonAttackAnimation> NF_SWORD_SKILL_SECOND;

   public static void build(AnimationBuilder builder) {
      NF_SWORD_IDLE = builder.nextAccessor("biped/nf_sword/nf_sword_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      NF_SWORD_WALK = builder.nextAccessor("biped/nf_sword/nf_sword_walk", accessor -> new MovementAnimation(0.1F, true, accessor, Armatures.BIPED));
      NF_SWORD_RUN = builder.nextAccessor("biped/nf_sword/nf_sword_run", accessor -> new MovementAnimation(0.1F, true, accessor, Armatures.BIPED));
      NF_SWORD_GUARD = builder.nextAccessor("biped/nf_sword/nf_sword_guard", accessor -> new StaticAnimation(0.15F, true, accessor, Armatures.BIPED));
      NF_SWORD_AUTO1 = builder.nextAccessor(
         "biped/nf_sword/nf_sword_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     16, 22, 24, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
      );
      NF_SWORD_AUTO2 = builder.nextAccessor(
         "biped/nf_sword/nf_sword_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     18, 24, 26, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
      );
      NF_SWORD_AUTO3 = builder.nextAccessor(
         "biped/nf_sword/nf_sword_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     24, 31, 32, InteractionHand.MAIN_HAND, 1.1F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
      );
      NF_SWORD_AUTO4 = builder.nextAccessor(
         "biped/nf_sword/nf_sword_auto4",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     18, 25, 32, InteractionHand.MAIN_HAND, 1.1F, 1.1F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
      );
      NF_SWORD_DASH = builder.nextAccessor(
         "biped/nf_sword/nf_sword_dash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     24, 30, 50, InteractionHand.MAIN_HAND, 1.2F, 1.2F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
      );
      NF_SWORD_AIRSLASH = builder.nextAccessor(
         "biped/nf_sword/nf_sword_airslash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     20, 27, 43, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.2F, 0.3F}))
      );
      NF_SWORD_SKILL = builder.nextAccessor(
         "biped/nf_sword/nf_sword_skill",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                        23, 30, 40, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, ColliderPreset.TACHI
                     )
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
                     .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0]))),
                  AvalonAnimationUtils.createSimplePhase(
                     41, 45, 60, InteractionHand.MAIN_HAND, 0.2F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).elbowR, Pisword
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
      );
      NF_SWORD_SKILL_FIRST = builder.nextAccessor(
         "biped/nf_sword/nf_sword_skill1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     23, 28, 40, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, ColliderPreset.TACHI
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
      );
      NF_SWORD_SKILL_SECOND = builder.nextAccessor(
         "biped/nf_sword/nf_sword_skill2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     6, 15, 24, InteractionHand.MAIN_HAND, 0.2F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).elbowR, Pisword
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_SWORD)
      );
   }
}
