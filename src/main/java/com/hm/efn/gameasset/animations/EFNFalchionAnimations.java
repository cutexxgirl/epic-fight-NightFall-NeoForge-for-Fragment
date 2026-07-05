package com.hm.efn.gameasset.animations;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.Background_2_Effek;
import com.hm.efn.client.effek.FalchionEffek;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.falchion.GuardianEntity;
import com.hm.efn.entity.skill.FalchionSkillArea;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.util.EffekUnits;
import com.hm.efn.util.ParticleEffectInvoker;
import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class EFNFalchionAnimations {
   public static final Collider FALCHION_ATTACK_COLL = new MultiOBBCollider(3, 0.6, 0.6, 1.2, 0.0, 0.0, -1.45F);
   public static final Collider FALCHION_SKILL_COLL = new OBBCollider(5.0, 0.8, 5.0, 0.0, 0.0, 0.0);
   public static final Collider FALCHION_HEAVY_COLL = new MultiOBBCollider(3, 0.6, 0.6, -1.0, 0.0, 0.5, 0.0);
   public static AnimationAccessor<StaticAnimation> FALCHION_IDLE;
   public static AnimationAccessor<StaticAnimation> FALCHION_GUARDIAN_IDLE;
   public static AnimationAccessor<StaticAnimation> FALCHION_GUARD;
   public static AnimationAccessor<AvalonMovementAnimation> FALCHION_WALK;
   public static AnimationAccessor<AvalonMovementAnimation> FALCHION_RUN;
   public static AnimationAccessor<AttackAnimation> FALCHION_AUTO1;
   public static AnimationAccessor<AttackAnimation> FALCHION_AUTO2;
   public static AnimationAccessor<AttackAnimation> FALCHION_AUTO3;
   public static AnimationAccessor<AttackAnimation> FALCHION_EX1;
   public static AnimationAccessor<AttackAnimation> FALCHION_EX2;
   public static AnimationAccessor<AttackAnimation> FALCHION_DASHATTACK;
   public static AnimationAccessor<AttackAnimation> FALCHION_AIRSLASH;
   public static AnimationAccessor<AttackAnimation> FALCHION_STRIKE;
   public static AnimationAccessor<AttackAnimation> FALCHION_SKILL;

   public static void build(AnimationBuilder builder) {
      FALCHION_IDLE = builder.nextAccessor("biped/falchion/falchion_idle", accessor -> new StaticAnimation(0.15F, true, accessor, Armatures.BIPED));
      FALCHION_GUARDIAN_IDLE = builder.nextAccessor(
         "biped/falchion/falchion_guardian_idle", accessor -> new StaticAnimation(0.1F, true, accessor, Armatures.BIPED)
      );
      FALCHION_GUARD = builder.nextAccessor(
         "biped/falchion/falchion_guard",
         accessor -> new StaticAnimation(0.15F, true, accessor, Armatures.BIPED)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
      );
      FALCHION_WALK = builder.nextAccessor(
         "biped/falchion/falchion_walk", accessor -> new AvalonMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.0F)
      );
      FALCHION_RUN = builder.nextAccessor("biped/falchion/falchion_run", accessor -> new AvalonMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.3F));
      FALCHION_AUTO1 = builder.nextAccessor(
         "biped/falchion/falchion_auto1",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(
                        0.0F,
                        0.725F,
                        0.725F,
                        0.95F,
                        1.235F,
                        Float.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        new JointColliderPair[]{new JointColliderPair(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)}
                     )
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.725F))
               }
            )
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
      );
      FALCHION_AUTO2 = builder.nextAccessor(
         "biped/falchion/falchion_auto2",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(
                        0.0F,
                        0.55F,
                        0.55F,
                        0.85F,
                        1.1F,
                        Float.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        new JointColliderPair[]{new JointColliderPair(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)}
                     )
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.55F))
               }
            )
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
      );
      FALCHION_AUTO3 = builder.nextAccessor(
         "biped/falchion/falchion_auto3",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(
                        0.0F,
                        0.625F,
                        0.625F,
                        0.925F,
                        1.3F,
                        Float.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        new JointColliderPair[]{new JointColliderPair(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)}
                     )
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.625F))
               }
            )
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
      );
      FALCHION_EX1 = builder.nextAccessor(
         "biped/falchion/falchion_ex1",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(
                        0.0F,
                        0.6F,
                        0.6F,
                        0.9F,
                        1.2F,
                        Float.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        new JointColliderPair[]{new JointColliderPair(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)}
                     )
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               }
            )
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
      FALCHION_EX2 = builder.nextAccessor(
         "biped/falchion/falchion_ex2",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.2F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.0F, 0.05F, 0.25F, 0.4F, 0.4F, 0.4F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.15F)),
                  new Phase(0.4F, 0.45F, 0.5F, 0.7F, 1.2F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               }
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
      );
      FALCHION_AIRSLASH = builder.nextAccessor(
         "biped/falchion/falchion_airslash",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.2F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.0F, 0.05F, 0.3F, 0.45F, 0.6F, 0.6F, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.15F)),
                  new Phase(0.6F, 0.65F, 0.7F, 0.85F, 1.3F, Float.MAX_VALUE, InteractionHand.MAIN_HAND, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.6F))
               }
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.05F)
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
      );
      FALCHION_DASHATTACK = builder.nextAccessor(
         "biped/falchion/falchion_dash",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(
                        0.0F,
                        0.55F,
                        0.55F,
                        0.79F,
                        1.0F,
                        Float.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        new JointColliderPair[]{new JointColliderPair(((HumanoidArmature)Armatures.BIPED.get()).toolR, null)}
                     )
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.55F))
               }
            )
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
      );
      FALCHION_STRIKE = builder.nextAccessor(
         "biped/falchion/falchion_strike",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.02F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(
                        0.0F,
                        0.1F,
                        0.15F,
                        0.3F,
                        0.6F,
                        Float.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        new JointColliderPair[]{new JointColliderPair(((HumanoidArmature)Armatures.BIPED.get()).toolR, FALCHION_HEAVY_COLL)}
                     )
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.3F))
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
               }
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 0.95F)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, ReusableSources.COMBO_ATTACK_DIRECTION_MODIFIER)
            .newTimePair(0.0F, 0.03F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
            .newTimePair(0.0F, 0.3F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
      );
      FALCHION_SKILL = builder.nextAccessor(
         "biped/falchion/falchion_skill",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(
                        0.0F,
                        0.55F,
                        0.45F,
                        0.7F,
                        1.0F,
                        Float.MAX_VALUE,
                        InteractionHand.MAIN_HAND,
                        new JointColliderPair[]{new JointColliderPair(((HumanoidArmature)Armatures.BIPED.get()).rootJoint, FALCHION_SKILL_COLL)}
                     )
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.55F))
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                     .addProperty(
                        AttackPhaseProperty.SOURCE_TAG,
                        Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE, EpicFightDamageTypeTags.BYPASS_DODGE)
                     )
               }
            )
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 0.75F)
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleSound(10, SoundEvents.WITHER_AMBIENT, 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(30, SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 1.0F),
                  ParticleEffectInvoker.CustomGroundSplit(30, 0.0, 0.0, 0.0, 0.0, 4.0F, true, false, true, false),
                  InTimeEvent.create(
                     0.5F,
                     (livingEntityPatch, assetAccessor, animationParameters) -> {
                        Level level = ((LivingEntity)livingEntityPatch.getOriginal()).level();
                        FalchionSkillArea falchionSkillArea = new FalchionSkillArea((EntityType<?>)EFNEntity.FALCHION_SKILL_AREA.get(), level);
                        falchionSkillArea.setPos(
                           ((LivingEntity)livingEntityPatch.getOriginal()).position().x,
                           ((LivingEntity)livingEntityPatch.getOriginal()).position().y - 0.3F,
                           ((LivingEntity)livingEntityPatch.getOriginal()).position().z
                        );
                        level.addFreshEntity(falchionSkillArea);
                        if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                           GuardianEntity guardianEntity1 = new GuardianEntity((ServerPlayer)serverPlayerPatch.getOriginal());
                           GuardianEntity guardianEntity2 = new GuardianEntity((ServerPlayer)serverPlayerPatch.getOriginal());
                           Vec3 vec3 = ((ServerPlayer)serverPlayerPatch.getOriginal()).position();
                           float yRot = serverPlayerPatch.getYRot();
                           float distance = 2.0F;
                           Vec3 lookVec = ((ServerPlayer)serverPlayerPatch.getOriginal()).getLookAngle();
                           Vec3 rightVec = new Vec3(-lookVec.z, 0.0, lookVec.x).normalize();
                           Vec3 leftVec = rightVec.scale(-1.0);
                           Vec3 leftPos = vec3.add(leftVec.scale(distance));
                           Vec3 rightPos = vec3.add(rightVec.scale(distance));
                           guardianEntity1.moveTo(leftPos.x, leftPos.y, leftPos.z);
                           guardianEntity2.moveTo(rightPos.x, rightPos.y, rightPos.z);
                           guardianEntity1.setYRot(yRot);
                           guardianEntity1.setYBodyRot(yRot);
                           guardianEntity1.setYHeadRot(yRot);
                           guardianEntity2.setYRot(yRot);
                           guardianEntity2.setYBodyRot(yRot);
                           guardianEntity2.setYHeadRot(yRot);
                           level.addFreshEntity(guardianEntity1);
                           level.addFreshEntity(guardianEntity2);
                        }
                     },
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.35F,
                     (entityPatch, assetAccessor, animationParameters) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.FALCHION_SKILL_VFX.get()) {
                           FalchionEffek.playFalchion(
                              FalchionEffek.Type.LEVEL1, ((LivingEntity)entityPatch.getOriginal()).level(), 0.0, 0.5, 0.0, 1.0F, entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.5F,
                     (entityPatch, assetAccessor, animationParameters) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.FALCHION_SKILL_VFX.get()) {
                           Background_2_Effek.playBackground_2(
                              Background_2_Effek.Type.LEVEL3,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).getX(),
                              ((LivingEntity)entityPatch.getOriginal()).getY(),
                              ((LivingEntity)entityPatch.getOriginal()).getZ(),
                              1.0F
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
   }
}
