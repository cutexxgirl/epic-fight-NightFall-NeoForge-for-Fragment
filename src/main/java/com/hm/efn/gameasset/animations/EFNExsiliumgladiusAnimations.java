package com.hm.efn.gameasset.animations;

import com.hm.efn.gameasset.EFNAnimations;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
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
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;

public class EFNExsiliumgladiusAnimations {
   public static final Collider EXSILIUMGLADIUS_ABBB_HIT = new OBBCollider(2.0, 2.0, 9.5, 0.0, 0.8, -8.0);
   public static final Collider EXSILIUMGLADIUS_AABB_HIT = new OBBCollider(2.0, 2.0, 2.0, 0.0, 0.0, 0.0);
   public static AnimationAccessor<StaticAnimation> EXSILIUMGLADIUS_IDLE;
   public static AnimationAccessor<AvalonMovementAnimation> EXSILIUMGLADIUS_WALK;
   public static AnimationAccessor<AvalonMovementAnimation> EXSILIUMGLADIUS_RUN;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AIRSLASH;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_DASH;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_A;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AA;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AAA;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AAAA;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AAAB;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AAB;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AABB;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AABA;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_AB;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_ABA;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_ABAA;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_ABAB;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_ABB;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_ABBA;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_ABBB;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_D;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_DD;
   public static AnimationAccessor<AvalonAttackAnimation> EXSILIUMGLADIUS_DDD;

   public static void build(AnimationBuilder builder) {
      EXSILIUMGLADIUS_IDLE = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_idle", accessor -> new StaticAnimation(0.1F, true, accessor, Armatures.BIPED)
      );
      EXSILIUMGLADIUS_WALK = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_walk", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, Armatures.BIPED, 1.5F)
      );
      EXSILIUMGLADIUS_RUN = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_run", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, Armatures.BIPED, 2.0F)
      );
      EXSILIUMGLADIUS_DASH = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_dash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     20,
                     36,
                     40,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_AIRSLASH = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_airatk",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     30,
                     40,
                     45,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 0.3F}))
      );
      EXSILIUMGLADIUS_A = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_a",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     18,
                     30,
                     34,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_AA = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_aa",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     24,
                     36,
                     40,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_AAA = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_aaa",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     24,
                     38,
                     42,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_AAAA = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_aaaa",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     24,
                     38,
                     45,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_AAAB = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_aaab",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     30,
                     38,
                     42,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_AAB = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_aab",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     31,
                     42,
                     47,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(33, 1.8, 0.0, 0.0, 0.0, 3.0F, true)})
      );
      EXSILIUMGLADIUS_AB = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_ab",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     30,
                     38,
                     43,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_ABB = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_abb",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     31,
                     65,
                     70,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_ABBA = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_abba",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     24,
                     36,
                     41,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_ABBB = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_abbb",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     32,
                     42,
                     47,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).rootJoint, EXSILIUMGLADIUS_ABBB_HIT),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).rootJoint, EXSILIUMGLADIUS_ABBB_HIT)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(2.1474836E9F))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleGroundSplit(37, 2.0, 0.0, 0.0, 0.0, 3.0F, true),
                  AvalonEventUtils.simpleGroundSplit(37, 6.0, 0.0, 0.0, 0.0, 2.0F, true),
                  AvalonEventUtils.simpleGroundSplit(37, 10.0, 0.0, 0.0, 0.0, 2.0F, true),
                  AvalonEventUtils.simpleGroundSplit(37, 14.0, 0.0, 0.0, 0.0, 2.0F, true),
                  AvalonEventUtils.simpleCameraShake(37, 12, 5.0F, 5.0F, 5.0F),
                  InTimeEvent.create(0.55F, (entitypatch, self, params) -> {
                     if (!((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                        LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                        ServerLevel level = (ServerLevel)attacker.level();
                        Vec3 lookVec = attacker.getLookAngle();
                        double centerX = attacker.getX();
                        double centerY = attacker.getY() + 0.5;
                        double centerZ = attacker.getZ();
                        double baseRadius = 8.0;
                        double maxRadius = 15.0;
                        int waveCount = 3;
                        int particlesPerWave = 80;
                        double speed = 0.4;

                        for (int wave = 0; wave < waveCount; wave++) {
                           double radius = baseRadius + (maxRadius - baseRadius) * wave / (waveCount - 1);

                           for (int i = 0; i < particlesPerWave; i++) {
                              double angle = (Math.PI * 2) * i / particlesPerWave;
                              double randomOffset = 0.3 * (level.random.nextDouble() - 0.5);
                              double xOffset = radius * Math.cos(angle) + randomOffset;
                              double zOffset = radius * Math.sin(angle) + randomOffset;
                              double motionX = xOffset * speed / radius;
                              double motionZ = zOffset * speed / radius;
                              double yOffset = 0.5 * Math.sin(angle * 2.0 + wave * 0.5);
                              level.sendParticles(ParticleTypes.SMOKE, centerX + xOffset, centerY + yOffset, centerZ + zOffset, 1, motionX, 0.05, motionZ, 0.8);
                           }
                        }

                        level.sendParticles(ParticleTypes.END_ROD, centerX, centerY, centerZ, 50, 1.5, 0.5, 1.5, 0.7);
                        int segments = 5;
                        double height = 6.0;
                        int verticalParticles = 25;
                        int ringParticles = 15;

                        for (int seg = 1; seg <= segments; seg++) {
                           double forwardDist = seg * 2.0;
                           double segX = centerX + lookVec.x * forwardDist;
                           double segZ = centerZ + lookVec.z * forwardDist;

                           for (int i = 0; i < verticalParticles; i++) {
                              double yPos = centerY + height * i / verticalParticles;
                              double randomSpread = 0.4 * (level.random.nextDouble() - 0.5);
                              double spreadX = lookVec.z * randomSpread;
                              double spreadZ = -lookVec.x * randomSpread;
                              level.sendParticles(ParticleTypes.SMOKE, segX + spreadX, yPos, segZ + spreadZ, 1, 0.0, 0.08, 0.0, 0.6);
                           }

                           for (int i = 0; i < ringParticles; i++) {
                              double angle = (Math.PI * 2) * i / ringParticles;
                              double radius = 1.5;
                              double xOffset = radius * Math.cos(angle);
                              double zOffset = radius * Math.sin(angle);
                              double worldX = segX + lookVec.z * xOffset - lookVec.x * zOffset;
                              double worldZ = segZ + lookVec.x * xOffset + lookVec.z * zOffset;
                              level.sendParticles(ParticleTypes.SMOKE, worldX, centerY + height, worldZ, 1, 0.0, 0.15, 0.0, 0.7);
                           }
                        }
                     }
                  }, Side.SERVER)
               }
            )
      );
      EXSILIUMGLADIUS_ABA = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_aba",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     30,
                     38,
                     42,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_ABAA = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_abaa",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     8,
                     19,
                     30,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_ABAB = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_abab",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     36,
                     46,
                     51,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_AABA = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_aaba",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     19,
                     30,
                     40,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_AABB = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_aabb",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     37,
                     48,
                     52,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).rootJoint, EXSILIUMGLADIUS_AABB_HIT),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).rootJoint, EXSILIUMGLADIUS_AABB_HIT)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(2.1474836E9F))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleGroundSplit(46, 2.0, 0.0, 0.0, 0.0, 4.0F, true),
                  AvalonEventUtils.simpleCameraShake(46, 12, 5.0F, 5.0F, 5.0F),
                  InTimeEvent.create(0.73F, (entitypatch, self, params) -> {
                     if (!((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                        LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                        ServerLevel level = (ServerLevel)attacker.level();
                        double centerX = attacker.getX();
                        double centerY = attacker.getY();
                        double centerZ = attacker.getZ();
                        double baseRadius = 8.0;
                        double maxRadius = 15.0;
                        int waveCount = 3;
                        int particlesPerWave = 80;
                        double speed = 0.4;

                        for (int wave = 0; wave < waveCount; wave++) {
                           double radius = baseRadius + (maxRadius - baseRadius) * wave / (waveCount - 1);

                           for (int i = 0; i < particlesPerWave; i++) {
                              double angle = (Math.PI * 2) * i / particlesPerWave;
                              double randomOffset = 0.3 * (level.random.nextDouble() - 0.5);
                              double xOffset = radius * Math.cos(angle) + randomOffset;
                              double zOffset = radius * Math.sin(angle) + randomOffset;
                              double motionX = xOffset * speed / radius;
                              double motionZ = zOffset * speed / radius;
                              double yOffset = 0.5 * Math.sin(angle * 2.0 + wave * 0.5);
                              level.sendParticles(
                                 ParticleTypes.SMOKE, centerX + xOffset, centerY + 0.1 + yOffset, centerZ + zOffset, 1, motionX, 0.05, motionZ, 0.8
                              );
                           }
                        }

                        level.sendParticles(ParticleTypes.END_ROD, centerX, centerY + 0.5, centerZ, 50, 1.5, 0.5, 1.5, 0.7);
                     }
                  }, Side.SERVER)
               }
            )
      );
      EXSILIUMGLADIUS_D = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_d",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     30,
                     84,
                     84,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_DD = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_dd",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     36,
                     96,
                     96,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
      EXSILIUMGLADIUS_DDD = builder.nextAccessor(
         "biped/exsiliumgladius_reborn/exsiliumgladius_ddd",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     2,
                     60,
                     60,
                     InteractionHand.MAIN_HAND,
                     1.0F,
                     1.0F,
                     new JointColliderPair[]{
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolR, null),
                        JointColliderPair.of(((HumanoidArmature)Armatures.BIPED.get()).toolL, null)
                     }
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_EXSILIUMGLADIUS)
      );
   }
}
