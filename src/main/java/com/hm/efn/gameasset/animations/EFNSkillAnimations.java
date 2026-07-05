package com.hm.efn.gameasset.animations;

import com.hm.efn.animations.types.EFNGuardAnimation;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.EFNVFXManagers;
import com.hm.efn.entity.geoEntity.SoulHuntLightning;
import com.hm.efn.util.ParticleEffectInvoker;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Event;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class EFNSkillAnimations {
   public static final Collider STOMP_COLL = new OBBCollider(10.0, 4.5, 10.0, 0.0, 3.2, 0.0);
   public static final Collider EXECUTION_COLL = new OBBCollider(1.6, 2.0, 2.5, 0.0, 0.0, -1.0);
   public static final Event VEX_HUNT = (entitypatch, self, params) -> {
      if (entitypatch != null && entitypatch.getOriginal() != null) {
         if (!entitypatch.isLogicalClient()) {
            if (!(entitypatch.getOriginal() instanceof LivingEntity livingEntity)) {
               return;
            }

            if (!(livingEntity instanceof Player player)) {
               return;
            }

            Level level = player.level();
            AABB searchArea = new AABB(
               player.getX() - 10.0,
               player.getY() - 10.0,
               player.getZ() - 10.0,
               player.getX() + 10.0,
               player.getY() + 10.0,
               player.getZ() + 10.0
            );

            for (Vex vex : level.getEntitiesOfClass(Vex.class, searchArea)) {
               if (vex.isAlive()) {
                  float maxHealth = vex.getMaxHealth();
                  float damage = maxHealth * 0.5F;
                  DamageSource damageSource = level.damageSources().playerAttack(player);
                  vex.hurt(damageSource, damage);
                  MobEffectInstance slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1000, 255, false, false, false);
                  vex.addEffect(slowness);
                  vex.setNoGravity(false);
                  vex.setNoAi(true);
                  Vec3 lookVec = player.getLookAngle();
                  Vec3 frontPos = player.position().add(0.0, player.getEyeHeight() * 0.2, 0.0).add(lookVec.scale(1.0));
                  vex.setPos(frontPos.x, frontPos.y, frontPos.z);
                  vex.setDeltaMovement(0.0, -2.0, 0.0);
                  if (level instanceof ServerLevel serverLevel) {
                     serverLevel.sendParticles(ParticleTypes.FIREWORK, vex.getX(), vex.getY(), vex.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                  }
               }
            }
         }
      }
   };
   public static AnimationAccessor<AvalonAttackAnimation> STOMP;
   public static AnimationAccessor<AvalonAttackAnimation> EXECUTION;
   public static AnimationAccessor<EFNGuardAnimation> EFN_GUARD_ACTIVE_HIT1;
   public static AnimationAccessor<EFNGuardAnimation> EFN_GUARD_ACTIVE_HIT2;
   public static AnimationAccessor<EFNGuardAnimation> EFN_GUARD_ACTIVE_HIT3;

   public static InTimeEvent invokeLightning(int startFrame, float scale) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(
         start,
         (entityPatch, self, params) -> {
            if (entityPatch.getOriginal() instanceof LivingEntity owner) {
               if (owner.level() instanceof ServerLevel level) {
                  Entity target = entityPatch.getTarget();
                  Vec3 spawnPos;
                  if (target != null && EFNVFXManagers.isTargetInRange(owner, target, 3.5)) {
                     spawnPos = target.position().add(0.0, 0.0, 0.0);
                  } else {
                     spawnPos = EFNVFXManagers.calculateEffectPosition(owner, 2.0, -1.3F, 0.0);
                  }

                  SoulHuntLightning soulHuntLightning = new SoulHuntLightning(
                     (EntityType<? extends PathfinderMob>)EFNEntity.SOULHUNT_LIGHTNING.get(), level, spawnPos.x, spawnPos.y, spawnPos.z
                  );
                  soulHuntLightning.setOwner(owner);
                  soulHuntLightning.setScale(scale);
                  soulHuntLightning.setXRotOffset(0.0F);
                  soulHuntLightning.setYRotOffset(owner.getYRot());
                  soulHuntLightning.setZRotOffset(0.0F);
                  soulHuntLightning.setStartYRot(owner.getYRot());
                  soulHuntLightning.setYRot(owner.getYRot());
                  soulHuntLightning.setYBodyRot(owner.getYRot());
                  soulHuntLightning.setYHeadRot(owner.getYRot());
                  soulHuntLightning.setXRot(0.0F);
                  level.addFreshEntity(soulHuntLightning);
               }
            }
         },
         Side.SERVER
      );
   }

   private static Vec3 calculateSpawnPosition(LivingEntity owner, double xOffset, double yOffset, double zOffset) {
      float yawRadians = (float)Math.toRadians(-owner.getYRot());
      Vec3 lookVec = new Vec3(Math.sin(yawRadians), 0.0, Math.cos(yawRadians)).normalize();
      Vec3 rightVec = new Vec3(Math.sin(yawRadians + (Math.PI / 2)), 0.0, Math.cos(yawRadians + (Math.PI / 2))).normalize();
      Vec3 upVec = new Vec3(0.0, 1.0, 0.0);
      Vec3 basePos = owner.position().add(0.0, owner.getEyeHeight(), 0.0);
      return basePos.add(lookVec.scale(zOffset)).add(rightVec.scale(xOffset)).add(upVec.scale(yOffset));
   }

   public static void build(AnimationBuilder builder) {
      EFN_GUARD_ACTIVE_HIT1 = builder.nextAccessor(
         "biped/nf_skill/biped_flashblock1",
         accessor -> (EFNGuardAnimation)new EFNGuardAnimation(0.07F, 0.35F, accessor, Armatures.BIPED)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(2, 6, 4.0F, 2.0F, 4.0F)})
      );
      EFN_GUARD_ACTIVE_HIT2 = builder.nextAccessor(
         "biped/nf_skill/biped_flashblock2",
         accessor -> (EFNGuardAnimation)new EFNGuardAnimation(0.07F, 0.35F, accessor, Armatures.BIPED)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(2, 6, 4.0F, 2.0F, 4.0F)})
      );
      EFN_GUARD_ACTIVE_HIT3 = builder.nextAccessor(
         "biped/nf_skill/biped_flashblock3",
         accessor -> (EFNGuardAnimation)new EFNGuardAnimation(0.1F, 1.1F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 0.2F}))
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(3, 11, 6.0F, 3.0F, 6.0F)})
      );
      EXECUTION = builder.nextAccessor(
         "biped/nf_skill/biped_execute",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     76, 85, 96, InteractionHand.MAIN_HAND, 1.5F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, EXECUTION_COLL
                  )
               }
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.EVISCERATE_LOST_HEALTH.create(new float[]{0.2F})))
            .addProperty(AttackAnimationProperty.REACH, 0.5F)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, false)
            .newTimePair(0.0F, 1.5F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.MISSED)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.createSoulRingEffect(5, 30),
                  particleTrail(16, 40, InteractionHand.OFF_HAND, new Vec3(0.0, 0.0, 0.5), new Vec3(0.0, 0.0, 0.5), 1.0F, 2, ParticleTypes.SOUL_FIRE_FLAME, 0.1F),
                  particleTrail(16, 40, InteractionHand.OFF_HAND, new Vec3(0.0, 0.0, 0.6), new Vec3(0.0, 0.0, 0.6), 1.0F, 3, ParticleTypes.SMOKE, 0.25F),
                  InTimeEvent.create(0.05F, (entityPatch, self, params) -> entityPatch.playSound(SoundEvents.WITHER_AMBIENT, 1.3F, 0.0F, 0.0F), Side.SERVER),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 13, 10, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(0.5F, (entityPatch, self, params) -> entityPatch.playSound(SoundEvents.WARDEN_NEARBY_CLOSEST, 1.3F, 0.0F, 0.0F), Side.SERVER),
                  InTimeEvent.create(
                     1.2F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DARKNESS, 30, 5, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(1.2666F, (entityPatch, self, params) -> entityPatch.playSound(SoundEvents.WARDEN_SONIC_BOOM, 1.6F, 0.0F, 0.0F), Side.SERVER),
                  invokeLightning(70, 1.0F),
                  createVerticalSonicBoom(74, ((HumanoidArmature)Armatures.BIPED.get()).handL),
                  AvalonEventUtils.simpleCameraShake(74, 20, 8.0F, 4.0F, 8.0F),
                  ParticleEffectInvoker.simpleGroundSplit(78, 1.0, 0.0, 0.0, 0.0, 3.5F, true)
               }
            )
      );
      STOMP = builder.nextAccessor(
         "biped/nf_skill/biped_stomp",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     38, 45, 60, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, STOMP_COLL
                  )
               }
            )
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(10.0F))
            .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(45, 15, 5.0F, 3.0F, 5.0F),
                  ParticleEffectInvoker.simpleGroundSplit(45, 1.0, 0.0, 0.0, 0.0, 8.0F, true),
                  ParticleEffectInvoker.createSmokeRingEffect(10, 45),
                  InTimeEvent.create(0.7F, VEX_HUNT, Side.SERVER),
                  InTimeEvent.create(0.75F, (entitypatch, self, params) -> {
                     if (!((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                        LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                        ServerLevel level = (ServerLevel)attacker.level();
                        double centerX = attacker.getX();
                        double centerY = attacker.getY();
                        double centerZ = attacker.getZ();
                        double baseRadius = 10.0;
                        double maxRadius = 25.0;
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
                                 ParticleTypes.ASH, centerX + xOffset, centerY + 0.1 + yOffset, centerZ + zOffset, 1, motionX, 0.05, motionZ, 0.8
                              );
                           }
                        }

                        level.sendParticles(ParticleTypes.SOUL, centerX, centerY + 0.5, centerZ, 50, 1.5, 0.5, 1.5, 0.7);
                        level.sendParticles(ParticleTypes.FLASH, centerX, centerY + 1.0, centerZ, 1, 0.0, 0.0, 0.0, 1.0);
                     }
                  }, Side.SERVER),
                  InTimeEvent.create(0.01F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.WITHER_AMBIENT, 1.0F, 1.0F, 1.0F), Side.SERVER),
                  InTimeEvent.create(0.75F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.35F, 1.0F, 1.0F), Side.SERVER),
                  InTimeEvent.create(0.75F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.WITHER_HURT, 1.35F, 1.0F, 1.0F), Side.SERVER)
               }
            )
            .newTimePair(0.0F, 2.1474836E9F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.MISSED)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, (Function<DamageSource, ResultType>)damageSource -> ResultType.BLOCKED)
      );
   }

   public static InPeriodEvent particleTrail(
      int startFrame,
      int endFrame,
      InteractionHand hand,
      Vec3 startOffset,
      Vec3 endOffset,
      float timeInterpolation,
      int particleCount,
      ParticleOptions particleOptions,
      float random
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      Joint joint = null;
      switch (hand) {
         case MAIN_HAND:
            joint = ((HumanoidArmature)Armatures.BIPED.get()).handR;
            break;
         case OFF_HAND:
            joint = ((HumanoidArmature)Armatures.BIPED.get()).handL;
      }

      Joint finalJoint = joint;
      return InPeriodEvent.create(
         start,
         end,
         (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = player.getPrevElapsedTime();
            float elapsedTime = player.getElapsedTime();
            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3 trailStartOffset = startOffset;
            Vec3 trailEndOffset = endOffset;
            Vec3f trailDirection = new Vec3f(
               (float)(trailEndOffset.x - trailStartOffset.x),
               (float)(trailEndOffset.y - trailStartOffset.y),
               (float)(trailEndOffset.z - trailStartOffset.z)
            );

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
               for (int i = 0; i <= particleCount; i++) {
                  float ratio = (float)i / particleCount;
                  Vec3f pointOffset = new Vec3f(
                     (float)(trailStartOffset.x + trailDirection.x * ratio),
                     (float)(trailStartOffset.y + trailDirection.y * ratio),
                     (float)(trailStartOffset.z + trailDirection.z * ratio)
                  );
                  double randX = (Math.random() - 0.5) * random;
                  double randY = (Math.random() - 0.5) * random;
                  double randZ = (Math.random() - 0.5) * random;
                  Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                  if (((LivingEntity)entityPatch.getOriginal()).level().isClientSide) {
                     ((LivingEntity)entityPatch.getOriginal())
                        .level()
                        .addParticle(particleOptions, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0, 0.0, 0.0);
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent createVerticalSonicBoom(int startFrame, Joint startJoint) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (!((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
            ServerLevel level = (ServerLevel)((LivingEntity)entityPatch.getOriginal()).level();
            LivingEntity caster = (LivingEntity)entityPatch.getOriginal();
            Vec3 startPos = AvalonAnimationUtils.getJointWorldPos(entityPatch, startJoint);
            Vec3 horizontalLook = caster.getLookAngle();
            horizontalLook = new Vec3(horizontalLook.x, 0.0, horizontalLook.z).normalize();
            Vec3 forwardOffset = horizontalLook.scale(0.65);
            Vec3 finalStartPos = startPos.add(forwardOffset).add(0.0, 0.0, 0.0);
            generateSonicExplosion(level, entityPatch, finalStartPos);
         }
      }, Side.SERVER);
   }

   private static void generateSonicExplosion(ServerLevel level, LivingEntityPatch<?> entityPatch, Vec3 center) {
      generateSonicCore(level, center);
      generateSculkSoulBurst(level, entityPatch, center);
      generateFireAndFlash(level, entityPatch, center);
      generateSmokeEffect(level, entityPatch, center);
   }

   private static void generateSonicCore(ServerLevel level, Vec3 center) {
      level.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.FLASH, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
   }

   private static void generateSculkSoulBurst(ServerLevel level, LivingEntityPatch<?> entityPatch, Vec3 center) {
      int n = 120;
      double r = 0.8;
      double t = 0.01;

      for (int i = 0; i < n; i++) {
         double theta = (Math.PI * 2) * Math.random();
         double phi = (Math.random() - 0.5) * Math.PI * t / r;
         double x = r * Math.cos(phi) * Math.cos(theta);
         double y = r * Math.cos(phi) * Math.sin(theta);
         double z = r * Math.sin(phi);
         Vec3f direction = new Vec3f((float)x, (float)y, (float)z);
         OpenMatrix4f rotation = new OpenMatrix4f().rotate((float)(-Math.toRadians(entityPatch.getYRot())), new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.transform3v(rotation, direction, direction);
         level.sendParticles(
            ParticleTypes.SCULK_SOUL,
            center.x + direction.x * 3.0F,
            center.y + direction.y * 3.0F,
            center.z + direction.z * 3.0F,
            1,
            direction.x * 2.0F,
            direction.y * 2.0F,
            direction.z * 2.0F,
            0.15
         );
      }

      int var21 = 80;
      r = 1.2;

      for (int i = 0; i < var21; i++) {
         double theta = (Math.PI * 2) * Math.random();
         double phi = (Math.random() - 0.5) * Math.PI * t / r;
         double x = r * Math.cos(phi) * Math.cos(theta);
         double y = r * Math.cos(phi) * Math.sin(theta);
         double z = r * Math.sin(phi);
         Vec3f direction = new Vec3f((float)x, (float)y, (float)z);
         OpenMatrix4f rotation = new OpenMatrix4f().rotate((float)(-Math.toRadians(entityPatch.getYRot() + 45.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.transform3v(rotation, direction, direction);
         level.sendParticles(
            ParticleTypes.SCULK_SOUL,
            center.x + direction.x * 4.0F,
            center.y + direction.y * 4.0F,
            center.z + direction.z * 4.0F,
            1,
            direction.x * 1.5F,
            direction.y * 1.5F,
            direction.z * 1.5F,
            0.2
         );
      }
   }

   private static void generateFireAndFlash(ServerLevel level, LivingEntityPatch<?> entityPatch, Vec3 center) {
      int n = 80;
      double r = 0.6;

      for (int i = 0; i < n; i++) {
         double theta = (Math.PI * 2) * Math.random();
         double phi = Math.acos(Math.random());
         double x = r * Math.sin(phi) * Math.cos(theta);
         double y = r * Math.sin(phi) * Math.sin(theta);
         double z = r * Math.cos(phi);
         float randomVelocity = (float)Math.random();
         Vec3f direction = new Vec3f((float)x * randomVelocity * 1.5F, (float)y * randomVelocity * 1.5F, (float)z * randomVelocity * 3.0F);
         OpenMatrix4f rotation = new OpenMatrix4f().rotate((float)(-Math.toRadians(entityPatch.getYRot())), new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.transform3v(rotation, direction, direction);
         level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, center.x, center.y, center.z, 1, direction.x, direction.y, direction.z, 0.08);
      }

      int var20 = 50;

      for (int i = 0; i < var20; i++) {
         double theta = (Math.PI * 2) * Math.random();
         double phi = Math.acos(Math.random());
         double x = r * Math.sin(phi) * Math.cos(theta);
         double y = r * Math.sin(phi) * Math.sin(theta);
         double z = r * Math.cos(phi);
         float randomVelocity = (float)Math.random();
         Vec3f direction = new Vec3f((float)x * randomVelocity * 1.2F, (float)y * randomVelocity * 1.2F, (float)z * randomVelocity * 2.0F);
         OpenMatrix4f rotation = new OpenMatrix4f().rotate((float)(-Math.toRadians(entityPatch.getYRot())), new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.transform3v(rotation, direction, direction);
      }
   }

   private static void generateSmokeEffect(ServerLevel level, LivingEntityPatch<?> entityPatch, Vec3 center) {
      int n = 60;
      double r = 0.8;

      for (int i = 0; i < n; i++) {
         double theta = (Math.PI * 2) * Math.random();
         double phi = Math.acos(Math.random());
         double x = r * Math.sin(phi) * Math.cos(theta);
         double y = r * Math.sin(phi) * Math.sin(theta);
         double z = r * Math.cos(phi);
         float randomVelocity = (float)Math.random();
         Vec3f direction = new Vec3f((float)x * randomVelocity * 1.2F, (float)y * randomVelocity * 1.2F, (float)z * randomVelocity * 2.0F);
         OpenMatrix4f rotation = new OpenMatrix4f().rotate((float)(-Math.toRadians(entityPatch.getYRot())), new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.transform3v(rotation, direction, direction);
         level.sendParticles(ParticleTypes.SMOKE, center.x, center.y, center.z, 1, direction.x, direction.y, direction.z, 0.15);
      }

      int var20 = 30;

      for (int i = 0; i < var20; i++) {
         double theta = (Math.PI * 2) * Math.random();
         double phi = Math.acos(Math.random());
         double x = r * Math.sin(phi) * Math.cos(theta);
         double y = r * Math.sin(phi) * Math.sin(theta);
         double z = r * Math.cos(phi);
         level.sendParticles(ParticleTypes.SOUL, center.x + x, center.y + y, center.z + z, 1, x * 0.4, y * 0.4, z * 0.4, 0.08);
      }
   }
}
