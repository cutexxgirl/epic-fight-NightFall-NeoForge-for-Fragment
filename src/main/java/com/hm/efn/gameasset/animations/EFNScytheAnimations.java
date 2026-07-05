package com.hm.efn.gameasset.animations;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.MistEffek;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.EFNVFXManagers;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.network.EFNNetworkHandler;
import com.hm.efn.network.SoulAfterimagePacket;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.ScytheSkill;
import com.hm.efn.util.EffekUnits;
import com.hm.efn.util.crimson_moon.Crimson_Moon_Client;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.E4;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AirSlashAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.DashAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

public class EFNScytheAnimations {
   public static AnimationAccessor<StaticAnimation> SCYTHE_IDLE_COMBAT;
   public static AnimationAccessor<MovementAnimation> SCYTHE_WALK_COMBAT;
   public static AnimationAccessor<MovementAnimation> SCYTHE_RUN_COMBAT;
   public static AnimationAccessor<StaticAnimation> SCYTHE_IDLE_NORMAL;
   public static AnimationAccessor<MovementAnimation> SCYTHE_WALK_NORMAL;
   public static AnimationAccessor<MovementAnimation> SCYTHE_RUN_NORMAL;
   public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO1;
   public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO2;
   public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO3;
   public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO4;
   public static AnimationAccessor<ComboAttackAnimation> SCYTHE_AUTO5;
   public static AnimationAccessor<AirSlashAnimation> SCYTHE_AIR_SLASH;
   public static AnimationAccessor<DashAttackAnimation> SCYTHE_DASH;
   public static AnimationAccessor<StaticAnimation> SCYTHE_SHEATHED;
   public static AnimationAccessor<StaticAnimation> SCYTHE_UNSHEATHED;
   public static AnimationAccessor<AttackAnimation> SCYTHE_HARVEST;
   public static AnimationAccessor<AttackAnimation> SCYTHE_SCARLET_END;
   public static AnimationAccessor<StaticAnimation> SCYTHE_BLOCK;
   public static final E4<Vec3f, Joint, Double, Float> FRACTURE_GROUND_NONSMOKER = (entitypatch, animation, params) -> {
      Vec3 position = ((LivingEntity)entitypatch.getOriginal()).position();
      OpenMatrix4f modelTransform = entitypatch.getArmature()
         .getBoundTransformFor(((StaticAnimation)animation.get()).getPoseByTime(entitypatch, (Float)params.fourth(), 1.0F), (Joint)params.second())
         .mulFront(
            OpenMatrix4f.createTranslation((float)position.x, (float)position.y, (float)position.z)
               .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS).mulBack(entitypatch.getModelMatrix(1.0F)))
         );
      Level level = ((LivingEntity)entitypatch.getOriginal()).level();
      Vec3 weaponEdge = OpenMatrix4f.transform(modelTransform, ((Vec3f)params.first()).toDoubleVector());
      BlockHitResult hitResult = level.clip(
         new ClipContext(position.add(0.0, 0.1, 0.0), weaponEdge, Block.COLLIDER, Fluid.NONE, entitypatch.getOriginal())
      );
      Vec3 slamStartPos;
      if (hitResult.getType() == Type.BLOCK) {
         Direction direction = hitResult.getDirection();
         BlockPos collidePos = hitResult.getBlockPos().offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
         if (!LevelUtil.canTransferShockWave(level, collidePos, level.getBlockState(collidePos))) {
            collidePos = collidePos.below();
         }

         slamStartPos = new Vec3(collidePos.getX(), collidePos.getY(), collidePos.getZ());
      } else {
         slamStartPos = weaponEdge.subtract(0.0, 1.0, 0.0);
      }

      LevelUtil.circleSlamFracture((LivingEntity)entitypatch.getOriginal(), level, slamStartPos, (Double)params.third(), false, true);
   };
   private static final MultiOBBCollider SCYTHE_COLLIDER = new MultiOBBCollider(
      new OBBCollider[]{
         new OBBCollider(0.25, 0.25, 1.3, 0.0, 0.0, 1.0),
         new OBBCollider(0.25, 0.25, 1.3, 0.0, 0.0, 1.0),
         new OBBCollider(0.7, 0.9, 0.7, 0.0, -0.5, -0.45),
         new OBBCollider(0.7, 0.9, 0.7, 0.0, -0.5, -0.45)
      }
   );
   private static final MultiOBBCollider SCYTHE_SKILL_COLLIDER = new MultiOBBCollider(3, 0.5, 0.5, 5.0, 0.0, 0.0, -4.0);
   private static final MultiOBBCollider SCYTHE_SKILL2_COLLIDER = new MultiOBBCollider(3, 8.0, 2.5, 8.0, 0.0, 2.5, 0.0);

   public static void build(AnimationBuilder builder) {
      PlaybackSpeedModifier FINISHER = (self, entityPatch, speed, prevElapsedTime, elapsedTime) -> 1.0F;
      PlaybackSpeedModifier HARVEST = (self, entityPatch, speed, prevElapsedTime, elapsedTime) -> {
         float bonus = 0.0F;
         LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
         MobEffectInstance customEffect = entity.getEffect(EFNMobEffectRegistry.ATTACK_SPEED_INCREASE);
         if (customEffect != null) {
            bonus += (customEffect.getAmplifier() + 1) * 0.1F;
         }

         MobEffectInstance hasteEffect = entity.getEffect(MobEffects.DIG_SPEED);
         if (hasteEffect != null) {
            bonus += (hasteEffect.getAmplifier() + 1) * 0.1F;
         }

         return Math.min(1.0F + bonus, 1.5F);
      };
      ArmatureAccessor<HumanoidArmature> biped = Armatures.BIPED;
      Joint mainHand = ((HumanoidArmature)biped.get()).toolR;
      SCYTHE_IDLE_COMBAT = builder.nextAccessor("biped/scythe/living/scythe_idle_combat", accessor -> new StaticAnimation(0.2F, true, accessor, biped));
      SCYTHE_WALK_COMBAT = builder.nextAccessor("biped/scythe/living/scythe_walk_combat", accessor -> new MovementAnimation(0.2F, true, accessor, biped));
      SCYTHE_RUN_COMBAT = builder.nextAccessor("biped/scythe/living/scythe_run_combat", accessor -> new MovementAnimation(0.2F, true, accessor, biped));
      SCYTHE_IDLE_NORMAL = builder.nextAccessor("biped/scythe/living/scythe_idle_normal", accessor -> new StaticAnimation(0.2F, true, accessor, biped));
      SCYTHE_WALK_NORMAL = builder.nextAccessor("biped/scythe/living/scythe_walk_normal", accessor -> new MovementAnimation(0.2F, true, accessor, biped));
      SCYTHE_RUN_NORMAL = builder.nextAccessor("biped/scythe/living/scythe_run_normal", accessor -> new MovementAnimation(0.2F, true, accessor, biped));
      SCYTHE_AUTO1 = builder.nextAccessor(
         "biped/scythe/combat/scythe_auto1",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(0.25F, 0.11666667F, 0.575F, 0.71F, 1.0F, SCYTHE_COLLIDER, mainHand, accessor, biped)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_1.get())
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
            .newTimePair(0.0F, 0.735F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(
               new AnimationEvent[]{EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH, 7, 2.0, -0.5, 0.0, 3.0F, new Vec3f(0.0F, 0.0F, -20.0F))}
            )
      );
      SCYTHE_AUTO2 = builder.nextAccessor(
         "biped/scythe/combat/scythe_auto2",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(0.1F, 0.11666667F, 0.58F, 0.7F, 1.3F, SCYTHE_COLLIDER, mainHand, accessor, biped)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
            .newTimePair(0.0F, 0.725F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH_ANTI, 7, 2.0, -0.5, 0.0, 3.2F, new Vec3f(0.0F, 0.0F, -30.0F))
               }
            )
      );
      SCYTHE_AUTO3 = builder.nextAccessor(
         "biped/scythe/combat/scythe_auto3",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(0.1F, 0.11666667F, 0.58F, 0.75F, 1.33F, SCYTHE_COLLIDER, mainHand, accessor, biped)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_4.get())
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
            .newTimePair(0.0F, 0.75F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(
               new AnimationEvent[]{EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH, 7, 2.0, -0.15, 0.0, 3.2F, new Vec3f(0.0F, 20.0F, 40.0F))}
            )
      );
      SCYTHE_AUTO4 = builder.nextAccessor(
         "biped/scythe/combat/scythe_auto4",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(0.01F, 0.33333334F, 0.75F, 0.86F, 1.42F, SCYTHE_COLLIDER, mainHand, accessor, biped)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
            .newTimePair(0.0F, 0.885F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(
               new AnimationEvent[]{EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH, 20, 2.0, -0.35, 0.0, 3.2F, new Vec3f(0.0F, 0.0F, 25.0F))}
            )
      );
      SCYTHE_AUTO5 = builder.nextAccessor(
         "biped/scythe/combat/scythe_auto5",
         accessor -> (ComboAttackAnimation)new ComboAttackAnimation(
               0.1F,
               accessor,
               biped,
               new Phase[]{
                  new Phase(0.0F, 0.11666667F, 0.52F, 0.71F, 1.0F, 1.0F, mainHand, SCYTHE_COLLIDER)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get()),
                  new Phase(1.0F, 1.14F, 1.31F, 2.3F, 2.3F, mainHand, SCYTHE_COLLIDER)
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_3.get())
               }
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
            .newTimePair(0.0F, 1.8F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH_ANTI, 7, 2.25, -0.25, 0.0, 3.3F, new Vec3f(0.0F, 0.0F, -30.0F)),
                  EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH_ANTI, 40, 2.9F, -0.1, 0.2F, 3.3F, new Vec3f(0.0F, 20.0F, 50.0F))
               }
            )
      );
      SCYTHE_AIR_SLASH = builder.nextAccessor(
         "biped/scythe/combat/scythe_airslash",
         accessor -> (AirSlashAnimation)new AirSlashAnimation(0.1F, 0.56F, 0.65F, 1.28F, SCYTHE_COLLIDER, mainHand, accessor, biped)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_3.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.65F}))
            .newTimePair(0.0F, 0.75F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH, 15, 1.25, -1.5, -0.5, 3.0F, new Vec3f(0.0F, -20.0F, -35.0F))
               }
            )
      );
      SCYTHE_DASH = builder.nextAccessor(
         "biped/scythe/combat/scythe_dash",
         accessor -> (DashAttackAnimation)new DashAttackAnimation(0.1F, 0.61F, 0.61F, 0.71F, 1.35F, SCYTHE_COLLIDER, mainHand, accessor, biped)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
            .newTimePair(0.0F, 0.81F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH_ANTI, 10, 2.5, -0.5, 0.0, 3.2F, new Vec3f(0.0F, 0.0F, 30.0F))
               }
            )
      );
      SCYTHE_HARVEST = builder.nextAccessor(
         "biped/scythe/skill/scythe_harvest",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               biped,
               new Phase[]{
                  new Phase(0.0F, 0.66F, 0.81F, 0.81F, 0.81F, mainHand, SCYTHE_COLLIDER)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_4.get()),
                  new Phase(0.81F, 0.81F, 1.0F, 1.0F, 1.0F, mainHand, SCYTHE_COLLIDER)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_4.get()),
                  new Phase(1.0F, 1.05F, 1.21F, 2.13F, 2.13F, mainHand, SCYTHE_COLLIDER)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, HARVEST)
            .newTimePair(0.0F, 1.63F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .newTimePair(0.0F, 1.83F)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addEvents(
               new AnimationEvent[]{
                  EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH, 25, 0.5, -0.5, 0.0, 4.0F, new Vec3f(0.0F, 0.0F, 17.0F)),
                  EFNVFXManagers.summonScytheComboVFX(EFNVFXManagers.CRIMSON_SLASH, 35, 0.5, -0.5, 0.0, 4.0F, new Vec3f(0.0F, 0.0F, -17.0F))
               }
            )
      );
      SCYTHE_SCARLET_END = builder.nextAccessor(
         "biped/scythe/skill/scythe_scarlet_end",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               biped,
               new Phase[]{
                  new Phase(0.0F, 0.96F, 1.08F, 1.08F, 1.08F, mainHand, SCYTHE_SKILL_COLLIDER)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.5F))
                     .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(100.0F))
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG),
                  new Phase(1.08F, 1.21F, 1.35F, 2.35F, 2.35F, mainHand, SCYTHE_SKILL_COLLIDER)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.5F))
                     .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(100.0F))
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG),
                  new Phase(2.35F, 3.0F, 3.5F, 10.0F, 10.0F, ((HumanoidArmature)biped.get()).rootJoint, SCYTHE_SKILL2_COLLIDER)
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
                     .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.BLOOD_HARVEST)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                     .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(100.0F))
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, FINISHER)
            .newTimePair(0.96F, 10.0F)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .newTimePair(0.0F, 10.0F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.MISSED)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
            .addEvents(
               StaticAnimationProperty.ON_BEGIN_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create(
                     (livingEntityPatch, assetAccessor, animationParameters) -> livingEntityPatch.playSound(SoundEvents.WITHER_SPAWN, 1.0F, 0.0F, 0.0F),
                     Side.CLIENT
                  )
               }
            )
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.26F,
                     (livingEntityPatch, assetAccessor, animationParameters) -> livingEntityPatch.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 0.0F, 0.0F),
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (livingEntityPatch, assetAccessor, animationParameters) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SCYTHE_FINISHER_VFX.get()) {
                           MistEffek.playMist(
                              MistEffek.Type.MIST2,
                              ((LivingEntity)livingEntityPatch.getOriginal()).level(),
                              0.0,
                              0.0,
                              0.0,
                              1.0F,
                              livingEntityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (livingEntityPatch, assetAccessor, animationParameters) -> ((LivingEntity)livingEntityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 70, 1)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (livingEntityPatch, assetAccessor, animationParameters) -> ((LivingEntity)livingEntityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DARKNESS, 70, 1)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     1.0F,
                     (livingEntityPatch, assetAccessor, animationParameters) -> livingEntityPatch.playSound(SoundEvents.WITHER_AMBIENT, 1.0F, 0.0F, 0.0F),
                     Side.CLIENT
                  ),
                  InTimeEvent.create(1.0F, (livingEntityPatch, assetAccessor, animationParameters) -> Crimson_Moon_Client.post1(livingEntityPatch), Side.CLIENT),
                  InTimeEvent.create(1.4F, (livingEntityPatch, assetAccessor, animationParameters) -> Crimson_Moon_Client.post2(livingEntityPatch), Side.CLIENT),
                  InTimeEvent.create(3.0F, (livingEntityPatch, assetAccessor, animationParameters) -> Crimson_Moon_Client.post3(livingEntityPatch), Side.CLIENT),
                  InTimeEvent.create(3.0F, (livingEntityPatch, assetAccessor, animationParameters) -> Crimson_Moon_Client.post4(livingEntityPatch), Side.CLIENT),
                  InTimeEvent.create(3.0F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                     for (LivingEntity targets : livingEntityPatch.getCurrentlyActuallyHitEntities()) {
                        SoulAfterimagePacket packet = new SoulAfterimagePacket(targets);
                        EFNNetworkHandler.sendToAllPlayersTrackingEntity(targets, packet);
                     }
                  }, Side.SERVER),
                  InTimeEvent.create(3.0F, (entityPatch, self, params) -> entityPatch.playSound(SoundEvents.WARDEN_SONIC_BOOM, 1.6F, 0.0F, 0.0F), Side.SERVER),
                  InPeriodEvent.create(0.26F, 1.0F, (livingEntityPatch, assetAccessor, animationParameters) -> {
                     LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
                     ServerLevel serverLevel = (ServerLevel)entity.level();
                     Vec3 pos = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
                     DustParticleOptions dustParticleOptions = new DustParticleOptions(new Vector3f(0.0F, 0.0F, 0.0F), 1.0F);
                     serverLevel.sendParticles(dustParticleOptions, pos.x, pos.y, pos.z, 10, 0.5, 1.0, 0.5, 1.0);
                     DustParticleOptions dustParticleOptions1 = new DustParticleOptions(new Vector3f(1.2F, 0.25F, 0.15F), 1.0F);
                     serverLevel.sendParticles(dustParticleOptions1, pos.x, pos.y, pos.z, 10, 0.5, 1.0, 0.5, 1.0);
                  }, Side.SERVER),
                  InTimeEvent.create(3.0F, FRACTURE_GROUND_NONSMOKER, Side.CLIENT)
                     .params(new Vec3f(0.0F, -0.25F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, 6.0, 3.0F)
               }
            )
      );
      SCYTHE_BLOCK = builder.nextAccessor("biped/scythe/skill/scythe_block", accessor -> new StaticAnimation(0.2F, true, accessor, biped));
      SCYTHE_SHEATHED = builder.nextAccessor(
         "biped/scythe/skill/scythe_sheathed",
         accessor -> new StaticAnimation(0.15F, false, accessor, biped)
            .newTimePair(0.0F, 10.0F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addEvents(
               StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  if (livingEntityPatch instanceof ServerPlayerPatch playerPatch) {
                     SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                     if (container != null && container.getSkill() instanceof ScytheSkill scytheSkill && !scytheSkill.isDisabled(container)) {
                        container.getDataManager().setDataSync(EFNSKillDataKeys.SCYTHE_SHEATH, true);
                        playerPatch.modifyLivingMotionByCurrentItem();
                     }
                  }
               }, Side.SERVER)}
            )
      );
      SCYTHE_UNSHEATHED = builder.nextAccessor(
         "biped/scythe/skill/scythe_unsheathed",
         accessor -> new StaticAnimation(0.15F, false, accessor, biped)
            .newTimePair(0.0F, 10.0F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addEvents(
               StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> {
                  if (livingEntityPatch instanceof ServerPlayerPatch playerPatch) {
                     SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                     if (container != null && container.getSkill() instanceof ScytheSkill scytheSkill && !scytheSkill.isDisabled(container)) {
                        container.getDataManager().setDataSync(EFNSKillDataKeys.SCYTHE_SHEATH, false);
                        playerPatch.modifyLivingMotionByCurrentItem();
                     }
                  }
               }, Side.SERVER)}
            )
      );
   }
}
