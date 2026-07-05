package com.hm.efn.gameasset.animations;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.SculkEffek;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.util.EffectEntityInvoker;
import com.hm.efn.util.EffekUnits;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.particle.AvalonParticles;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.Set;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class EFNDualSwordAnimations {
   public static final Collider DUAL_SWORD_AUTO_3 = new OBBCollider(0.95, 0.95, 1.35, 0.0, 0.8, -0.8);
   public static final Collider DUAL_SWORD_AUTO_4 = new OBBCollider(1.6, 1.6, 1.6, 0.0, 0.5, -0.5);
   public static final Collider DUALSWORD_DODGE = new OBBCollider(2.2, 2.2, 2.2, 0.0, 0.0, 0.0);
   public static AnimationAccessor<StaticAnimation> NF_DUAL_IDLE;
   public static AnimationAccessor<MovementAnimation> NF_DUAL_WALK;
   public static AnimationAccessor<MovementAnimation> NF_DUAL_RUN;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_AUTO4;
   public static AnimationAccessor<ComboAttackAnimation> NF_DUAL_DASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_AIRSLASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_SKILL;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_SKILL_EXTEND;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_STORMATK;
   public static AnimationAccessor<AvalonAttackAnimation> NF_DUAL_DODGE;

   public static void build(AnimationBuilder builder) {
      NF_DUAL_IDLE = builder.nextAccessor("biped/nf_dual/nf_dual_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      NF_DUAL_WALK = builder.nextAccessor("biped/nf_dual/nf_dual_walk", accessor -> new MovementAnimation(0.1F, true, accessor, Armatures.BIPED));
      NF_DUAL_RUN = builder.nextAccessor("biped/nf_dual/nf_dual_run", accessor -> new MovementAnimation(0.1F, true, accessor, Armatures.BIPED));
      NF_DUAL_AUTO1 = builder.nextAccessor(
         "biped/nf_dual/nf_dual_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               0.7F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     15, 21, 24, InteractionHand.MAIN_HAND, 0.5F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     25, 31, 36, InteractionHand.MAIN_HAND, 0.5F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonSecluded_Attack(12, 1.0, 0.0, -1.9F, 0.8F, -90.0F), EffectEntityInvoker.catchEntities(8, 21, 1.5F, 1.3, 0.5F)
               }
            )
      );
      NF_DUAL_AUTO2 = builder.nextAccessor(
         "biped/nf_dual/nf_dual_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               0.7F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     16, 22, 25, InteractionHand.MAIN_HAND, 0.5F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     25, 31, 36, InteractionHand.MAIN_HAND, 0.5F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonSecluded_Attack(11, 0.8, -1.8F, 2.0, 1.0F, 60.0F), EffectEntityInvoker.catchEntities(10, 22, 1.3F, 2.0, 0.5F)
               }
            )
      );
      NF_DUAL_AUTO3 = builder.nextAccessor(
         "biped/nf_dual/nf_dual_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               0.8F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     19, 27, 34, InteractionHand.MAIN_HAND, 1.1F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, DUAL_SWORD_AUTO_3
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonSecluded_Attack(9, 1.0, 1.3F, -1.5, 1.0F, 220.0F), EffectEntityInvoker.catchEntities(10, 20, 2.0F, 1.8, 0.5F)
               }
            )
            .newTimePair(0.0F, 0.2F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
      );
      NF_DUAL_AUTO4 = builder.nextAccessor(
         "biped/nf_dual/nf_dual_auto4",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               0.7F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     24, 33, 43, InteractionHand.MAIN_HAND, 1.2F, 1.2F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, DUAL_SWORD_AUTO_4
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonSecluded_Attack(18, 0.0, 0.7F, -2.2F, 1.2F, -110.0F),
                  EffectEntityInvoker.summonSecluded_Attack(18, 0.0, -2.5, -0.5, 1.2F, 0.0F),
                  EffectEntityInvoker.catchEntities(10, 33, 2.0F, 1.7, 0.5F)
               }
            )
            .newTimePair(0.0F, 0.1F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
      );
      NF_DUAL_DASH = builder.nextAccessor(
         "biped/nf_dual/nf_dual_dash",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.0F, 0.2F, 0.33F, 0.33F, 0.33F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, null),
                  new Phase(0.33F, 0.33F, 0.5F, 0.65F, Float.MAX_VALUE, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
      );
      NF_DUAL_AIRSLASH = builder.nextAccessor(
         "biped/nf_dual/nf_dual_airslash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     16, 21, 36, InteractionHand.MAIN_HAND, 0.6F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, ColliderPreset.TACHI
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     37, 43, 55, InteractionHand.MAIN_HAND, 0.8F, 0.8F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, ColliderPreset.GREATSWORD
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 0.4F}))
      );
      NF_DUAL_SKILL = builder.nextAccessor(
         "biped/nf_dual/nf_dual_skill",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               0.9F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     19, 26, 35, InteractionHand.MAIN_HAND, 1.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, DUAL_SWORD_AUTO_4
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addStateRemoveOld(EntityState.LOOK_TARGET, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.8F}))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.FIXED_HEAD_ROTATION, true)
            .addProperty(ActionAnimationProperty.MOVE_ON_LINK, true)
            .addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonSecluded_Attack(11, 0.7, 1.0, 3.55, 1.5F, 90.0F), EffectEntityInvoker.catchEntities(10, 33, 4.0F, 3.0, 0.5F)
               }
            )
      );
      NF_DUAL_SKILL_EXTEND = builder.nextAccessor(
         "biped/nf_dual/nf_dual_skill_hit",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     26, 35, 35, InteractionHand.MAIN_HAND, 0.5F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, DUAL_SWORD_AUTO_4
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     37, 44, 44, InteractionHand.MAIN_HAND, 0.5F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, DUAL_SWORD_AUTO_4
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     73, 83, 93, InteractionHand.MAIN_HAND, 1.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, DUAL_SWORD_AUTO_4
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addStateRemoveOld(EntityState.LOOK_TARGET, true)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.45F}))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 90.0F, 0.0F, 0.0F), Side.SERVER
                  ),
                  EffectEntityInvoker.catchEntities(55, 73, 4.0F, 2.1, 0.5F),
                  EffectEntityInvoker.summonSecluded_Skill(67, -1.6F, -4.45F, 0.0, 2.0F, 0.0F),
                  EffectEntityInvoker.summonSecluded_Skill(67, -1.6F, -2.45F, 4.0, 2.0F, 60.0F),
                  EffectEntityInvoker.catchEntities(67, 77, 3.0F, 1.5, 0.5F),
                  InTimeEvent.create(
                     1.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1.3F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(1.2F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.WARDEN_NEARBY_CLOSE, 200.0F, 0.0F, 0.0F), Side.SERVER),
                  InTimeEvent.create(
                     1.25F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.DUAL_SWORD_SKILL_VFX.get()) {
                           SculkEffek.playSculk(
                              SculkEffek.Type.LEVEL2,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).getX(),
                              ((LivingEntity)entityPatch.getOriginal()).getY() + 0.66,
                              ((LivingEntity)entityPatch.getOriginal()).getZ(),
                              1.0F
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     1.2F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DARKNESS, 30, 5, false, false, false)),
                     Side.BOTH
                  )
               }
            )
            .newTimePair(0.0F, 0.15F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
      );
      NF_DUAL_STORMATK = builder.nextAccessor(
         "biped/nf_dual/nf_dual_stormatk",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.01F,
               accessor,
               Armatures.BIPED,
               0.8F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     13, 18, 19, InteractionHand.MAIN_HAND, 0.5F, 0.7F, ((HumanoidArmature)Armatures.BIPED.get()).toolL, ColliderPreset.TACHI
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     20, 26, 30, InteractionHand.MAIN_HAND, 0.5F, 0.7F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, ColliderPreset.TACHI
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .newTimePair(0.0F, 0.2F)
            .addState(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonSecluded_Attack(17, 1.7, 0.7F, -2.5, 1.2F, -110.0F),
                  EffectEntityInvoker.summonSecluded_Attack(17, 1.7, -2.5, -0.8F, 1.2F, 0.0F),
                  EffectEntityInvoker.catchEntities(15, 30, 2.0F, 2.0, 0.5F)
               }
            )
      );
      NF_DUAL_DODGE = builder.nextAccessor(
         "biped/nf_dual/nf_dual_dodgestorm",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.01F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     1, 20, 25, InteractionHand.MAIN_HAND, 0.2F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, DUALSWORD_DODGE
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_DUAL)
            .newTimePair(0.0F, 2.1474836E9F)
            .addState(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonSecludedAtTarget(2, 1.0F, 0.0F, 4.0F, 0.5F, 5.0F, -1.1F),
                  EffectEntityInvoker.setDodgeCounterNodeEvent(20, Integer.MAX_VALUE),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)AvalonParticles.AVALON_ENTITY_AFTER_IMAGE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
   }
}
