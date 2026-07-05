package com.hm.efn.gameasset.animations;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.BurstEffek;
import com.hm.efn.client.effek.ChargingEffek;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffekUnits;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.particle.AvalonParticles;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class EFNGreatSwordAnimations {
   public static final Collider CHARGE_SKILL1 = new OBBCollider(1.0, 1.0, 2.0, 0.0, 0.0, -1.0);
   public static final Collider AIRSLASH = new OBBCollider(1.2, 1.2, 2.2, 0.0, 0.0, -1.0);
   public static final Collider GREATSWORD_AIRSLASH_SECOND = new OBBCollider(1.5, 1.0, 1.5, 0.0, 1.0, 0.0);
   public static final Collider GREATSWORD_CLASH_HIT_FIRST = new OBBCollider(1.2, 1.2, 2.2, 0.0, 0.0, -1.0);
   public static final Collider GREATSWORD_CLASH_HIT_SECOND = new OBBCollider(2.3, 2.0, 2.3, 0.0, 0.0, 0.0);
   public static AnimationAccessor<StaticAnimation> NG_GREATSWORD_IDLE;
   public static AnimationAccessor<MovementAnimation> NG_GREATSWOED_WALK;
   public static AnimationAccessor<MovementAnimation> NG_GREATSWORD_RUN;
   public static AnimationAccessor<ComboAttackAnimation> NG_GREATSWORD_AUTO1;
   public static AnimationAccessor<ComboAttackAnimation> NG_GREATSWORD_AUTO2;
   public static AnimationAccessor<ComboAttackAnimation> NG_GREATSWORD_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> NG_GREATSWORD_CHARGESKILL;
   public static AnimationAccessor<AvalonAttackAnimation> NG_GREATSWORD_DASH;
   public static AnimationAccessor<AttackAnimation> NG_GREATSWORD_AIRSLASH;
   public static AnimationAccessor<AttackAnimation> NG_GREATSWORD_AIRSLASH_NEW;
   public static AnimationAccessor<ComboAttackAnimation> NG_GREATSWORD_SKILL_CLASH;
   public static AnimationAccessor<AttackAnimation> NG_GREATSWORD_SKILL_CLASH_HIT;
   public static AnimationAccessor<ActionAnimation> NG_GREATSWORD_CHARGING;
   public static AnimationAccessor<ActionAnimation> NG_GREATSWORD_CHARGING_MOB;
   public static AnimationAccessor<AvalonAttackAnimation> NG_GREATSWORD_CHARG1MAX_FIRST;
   public static AnimationAccessor<AvalonAttackAnimation> NG_GREATSWORD_CHARG1MAX_SECOND;
   public static AnimationAccessor<AvalonAttackAnimation> NG_GREATSWORD_CHARG1MAX_GP;
   public static AnimationAccessor<AvalonAttackAnimation> NG_GREATSWORD_CHARG1MIN;

   public static void build(AnimationBuilder builder) {
      NG_GREATSWORD_IDLE = builder.nextAccessor("biped/ng_greatsword/ng_great_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      NG_GREATSWOED_WALK = builder.nextAccessor("biped/ng_greatsword/ng_great_walk", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      NG_GREATSWORD_RUN = builder.nextAccessor("biped/ng_greatsword/ng_great_run", accessor -> new MovementAnimation(true, accessor, Armatures.BIPED));
      NG_GREATSWORD_AUTO1 = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_auto1",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(
               0.1F, 0.6F, 0.8F, 0.9F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
            .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(20.0F))
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
      );
      NG_GREATSWORD_AUTO2 = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_auto2",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(
               0.1F, 0.5F, 0.83F, 0.93F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
            .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.05F))
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(20.0F))
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
      );
      NG_GREATSWORD_AUTO3 = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_auto3",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(
               0.1F, 0.45F, 0.75F, 1.2F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
            .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(20.0F))
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
      );
      NG_GREATSWORD_CHARGING = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_charge1ing",
         accessor -> (ActionAnimation)new ActionAnimation(0.01F, accessor, Armatures.BIPED)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, true)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, true)
            .addStateRemoveOld(EntityState.CAN_USE_ITEM, false)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
            .addStateRemoveOld(EntityState.INACTION, false)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               ActionAnimationProperty.ON_END_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create(
                     (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(NG_GREATSWORD_CHARG1MAX_FIRST), Side.SERVER
                  )
               }
            )
            .addEvents(
               ActionAnimationProperty.ON_BEGIN_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create(
                     (entityPatch, staticAnimation, objects) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.GREATSWORD_CHARGE_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL2, entityPatch.getOriginal(), 0.0, 0.7F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NG_GREATSWORD_CHARGING_MOB = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_charge1ing_mob",
         accessor -> (ActionAnimation)new ActionAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               ActionAnimationProperty.ON_END_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create(
                     (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(NG_GREATSWORD_CHARG1MAX_FIRST), Side.SERVER
                  )
               }
            )
            .addEvents(
               ActionAnimationProperty.ON_BEGIN_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create(
                     (entityPatch, staticAnimation, objects) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.GREATSWORD_CHARGE_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL2, entityPatch.getOriginal(), 0.0, 0.7F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NG_GREATSWORD_CHARG1MIN = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_charge1min",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     36, 45, 60, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
      );
      NG_GREATSWORD_CHARG1MAX_FIRST = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_charge1max",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.01F,
               accessor,
               Armatures.BIPED,
               2.0F,
               2.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     45, 53, 80, InteractionHand.MAIN_HAND, 1.2F, 1.2F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, CHARGE_SKILL1
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.35F,
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
                  ),
                  AvalonEventUtils.simpleCameraShake(40, 50, 5.0F, 4.0F, 5.0F),
                  InTimeEvent.create(
                     0.6F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()) {
                           if ((Boolean)EFNClientConfig.GREATSWORD_CHARGE_VFX.get()) {
                              Random random = new Random();
                              BurstEffek.playBurst(
                                 BurstEffek.Type.LEVEL2,
                                 ((LivingEntity)entityPatch.getOriginal()).level(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().x(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().y(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().z(),
                                 (float)random.nextDouble(-Math.PI, Math.PI),
                                 EffekUnits.getRY(entityPatch),
                                 0.0F,
                                 0.5F
                              );
                           }
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(0.6F, (entitypatch, self, params) -> {
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
                  }, Side.SERVER),
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 5, 10, false, false, false)),
                     Side.BOTH
                  ),
                  AvalonEventUtils.simpleGroundSplit(40, 2.0, 0.0, 0.0, 0.0, 3.0F, true),
                  AvalonEventUtils.simpleCameraShake(40, 40, 3.0F, 3.0F, 3.0F)
               }
            )
      );
      NG_GREATSWORD_CHARG1MAX_SECOND = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_charge1max_2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     31, 39, 79, InteractionHand.MAIN_HAND, 0.8F, 0.8F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, GREATSWORD_CLASH_HIT_FIRST
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     79, 85, 120, InteractionHand.MAIN_HAND, 0.8F, 0.8F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, GREATSWORD_CLASH_HIT_SECOND
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.6F, (entitypatch, self, params) -> {
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
                  }, Side.SERVER),
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 50, 2, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 50, 10, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(0.3F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                     .params(new Vec3f(0.0F, 0.3F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 2.0, 0.5F),
                  InTimeEvent.create(1.4F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                     .params(new Vec3f(0.0F, 0.3F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, 4.0, 0.55F),
                  InTimeEvent.create(
                     1.4F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()) {
                           if ((Boolean)EFNClientConfig.GREATSWORD_CHARGE_VFX.get()) {
                              BurstEffek.playBurst(
                                 BurstEffek.Type.LEVEL2,
                                 ((LivingEntity)entityPatch.getOriginal()).level(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().x(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().y(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().z(),
                                 0.0F,
                                 EffekUnits.getRY(entityPatch),
                                 (float) (Math.PI / 2),
                                 0.425F
                              );
                           }
                        }
                     },
                     Side.CLIENT
                  ),
                  AvalonEventUtils.simpleCameraShake(80, 40, 4.0F, 4.0F, 4.0F)
               }
            )
      );
      NG_GREATSWORD_CHARG1MAX_GP = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_charge1max_gp",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               0.5F,
               2.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     7, 15, 40, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, GREATSWORD_AIRSLASH_SECOND
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 2, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 30, 10, false, false, false)),
                     Side.BOTH
                  ),
                  AvalonEventUtils.simpleCameraShake(7, 20, 2.0F, 2.0F, 2.0F),
                  AvalonEventUtils.simpleGroundSplit(7, 0.0, 0.0, 0.0, 0.0, 2.5F, true)
               }
            )
      );
      NG_GREATSWORD_CHARGESKILL = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_chargeskill",
         accessor -> new AvalonAttackAnimation(
            0.1F,
            accessor,
            Armatures.BIPED,
            1.0F,
            1.0F,
            new AvalonPhase[]{
               AvalonAnimationUtils.createSimplePhase(90, 104, 120, InteractionHand.MAIN_HAND, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
            }
         )
      );
      NG_GREATSWORD_DASH = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_dash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     33, 44, 70, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
      );
      NG_GREATSWORD_AIRSLASH_NEW = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_airslash",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               new Phase[]{new Phase(0.0F, 0.8F, 1.16F, 1.16F, 1.8F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, AIRSLASH)}
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.1F))
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(30.0F))
            .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F))
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.9F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                     .params(new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, 3.0, 0.0F),
                  InTimeEvent.create(
                     0.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.ENTITY_MOVE.get(), 0.0F, 0.0F), Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.9F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()) {
                           if ((Boolean)EFNClientConfig.GREATSWORD_CHARGE_VFX.get()) {
                              Random random = new Random();
                              BurstEffek.playBurst(
                                 BurstEffek.Type.LEVEL2,
                                 ((LivingEntity)entityPatch.getOriginal()).level(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().x(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().y(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().z(),
                                 (float)random.nextDouble(-Math.PI, Math.PI),
                                 EffekUnits.getRY(entityPatch),
                                 0.0F,
                                 0.6F
                              );
                           }
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NG_GREATSWORD_AIRSLASH = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_airslash",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.0F, 0.86F, 1.0F, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.7F))
                     .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(10.0F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE),
                  new Phase(1.0F, 1.6F, 1.9F, 2.4F, Float.MAX_VALUE, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, GREATSWORD_AIRSLASH_SECOND)
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F))
                     .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(50.0F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               }
            )
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(1.8F, (entitypatch, self, params) -> {
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
                  }, Side.SERVER),
                  InTimeEvent.create(0.8F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                     .params(new Vec3f(0.0F, 0.3F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 2.0, 0.5F),
                  InTimeEvent.create(1.8F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                     .params(new Vec3f(0.0F, 0.3F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, 4.0, 0.55F),
                  InTimeEvent.create(
                     0.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.ENTITY_MOVE.get(), 0.0F, 0.0F), Side.CLIENT
                  )
               }
            )
      );
      NG_GREATSWORD_SKILL_CLASH = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_skill_clash",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(
               0.1F, 0.75F, 0.95F, 2.4F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
            .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, false)
            .addState(EntityState.SKILL_EXECUTABLE, true)
            .addState(EntityState.INACTION, false)
      );
      NG_GREATSWORD_SKILL_CLASH_HIT = builder.nextAccessor(
         "biped/ng_greatsword/ng_great_skill_clash_hit",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.0F, 0.85F, 1.11F, 1.78F, 1.78F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, GREATSWORD_CLASH_HIT_FIRST)
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                     .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(60.0F))
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE),
                  new Phase(1.78F, 1.78F, 1.88F, 2.5F, Float.MAX_VALUE, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, GREATSWORD_CLASH_HIT_SECOND)
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                     .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(60.0F))
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(5.0F))
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
               }
            )
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_RUIN)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(1.7F, (entitypatch, self, params) -> {
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
                  }, Side.SERVER),
                  InTimeEvent.create(0.7F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                     .params(new Vec3f(0.0F, 0.3F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 2.0, 0.5F),
                  InTimeEvent.create(1.85F, ReusableSources.FRACTURE_GROUND_SIMPLE, Side.CLIENT)
                     .params(new Vec3f(0.0F, 0.3F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, 4.0, 0.55F),
                  InTimeEvent.create(
                     1.85F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()) {
                           if ((Boolean)EFNClientConfig.GREATSWORD_CHARGE_VFX.get()) {
                              BurstEffek.playBurst(
                                 BurstEffek.Type.LEVEL2,
                                 ((LivingEntity)entityPatch.getOriginal()).level(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().x(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().y(),
                                 ((LivingEntity)entityPatch.getOriginal()).position().z(),
                                 0.0F,
                                 EffekUnits.getRY(entityPatch),
                                 (float) (Math.PI / 2),
                                 0.55F
                              );
                           }
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 50, 10, true, false, true)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 50, 10, true, false, true)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.1F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.ENTITY_MOVE.get(), 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.1F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(1.8F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.05F, 0.0F, 0.0F), Side.SERVER)
               }
            )
      );
   }
}
