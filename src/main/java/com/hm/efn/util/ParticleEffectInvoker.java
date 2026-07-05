package com.hm.efn.util;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.Dirt_1_Effek;
import com.hm.efn.client.effek.Dirt_2_Effek;
import com.hm.efn.client.effek.Stone_1_Effek;
import com.hm.efn.client.effek.Stone_2_Effek;
import com.hm.efn.particle.EFNParticles;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class ParticleEffectInvoker {
   public static InTimeEvent spawnExpParticle(int startFrame, double forwardOffset, double rightOffset, double upOffset) {
      return spawnCustomParticle(startFrame, forwardOffset, rightOffset, upOffset, (ParticleOptions)EFNParticles.EXP.get());
   }

   public static InTimeEvent spawnFlashParticle(int startFrame, double forwardOffset, double rightOffset, double upOffset) {
      return spawnCustomParticle(startFrame, forwardOffset, rightOffset, upOffset, (ParticleOptions)EFNParticles.FLASH.get());
   }

   public static InTimeEvent spawnSlashParticle(int startFrame, double forwardOffset, double rightOffset, double upOffset) {
      return spawnCustomParticle(startFrame, forwardOffset, rightOffset, upOffset, (ParticleOptions)EFNParticles.SLASH.get());
   }

   public static InTimeEvent spawnSlashParticleSmart(int startFrame) {
      return smartCustomParticle(startFrame, (ParticleOptions)EFNParticles.SLASH.get(), 1.5);
   }

   public static InTimeEvent spawnJudgementCutMiniSmart(int startFrame) {
      return smartCustomParticle(startFrame, (ParticleOptions)EFNParticles.JUDGEMENT_CUT_PARTICLE_RED.get(), 0.0);
   }

   public static InTimeEvent spawnParticleAtJoint(int startFrame, Joint joint, ParticleOptions particleType, int count, double speed, Vec3 offset) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(
         time,
         (entityPatch, self, params) -> {
            if (entityPatch != null) {
               if (((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
                  AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
                  if (player != null) {
                     float elapsedTime = player.getPrevElapsedTime();
                     Vec3f offsetVec3f = new Vec3f((float)offset.x, (float)offset.y, (float)offset.z);
                     Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(entityPatch, joint, elapsedTime, offsetVec3f);

                     for (int i = 0; i < count; i++) {
                        ((LivingEntity)entityPatch.getOriginal())
                           .level()
                           .addParticle(particleType, worldPos.x, worldPos.y, worldPos.z, 0.0, 0.0, 0.0);
                     }
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent spawnParticleAtJoint(int startFrame, Joint joint, ParticleOptions particleType, int count) {
      return spawnParticleAtJoint(startFrame, joint, particleType, count, 0.0, Vec3.ZERO);
   }

   public static InTimeEvent spawnParticleAtJoint(int startFrame, Joint joint, ParticleOptions particleType, int count, Vec3 offset) {
      return spawnParticleAtJoint(startFrame, joint, particleType, count, 0.0, offset);
   }

   public static InTimeEvent spawnParticleAtJoint(int startFrame, Joint joint, ParticleOptions particleType, int count, double speed) {
      return spawnParticleAtJoint(startFrame, joint, particleType, count, speed, Vec3.ZERO);
   }

   public static InTimeEvent spawnCustomParticle(int startFrame, double forwardOffset, double rightOffset, double upOffset, ParticleOptions particleType) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         EntityPatch<?> patch = entityPatch;
         if (patch.getOriginal() instanceof LivingEntity living) {
            if (living.level() instanceof ServerLevel level) {
               Vec3 var15 = calculateParticlePosition(living, forwardOffset, rightOffset, upOffset);
               level.sendParticles(particleType, var15.x, var15.y, var15.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent smartCustomParticle(int startFrame, ParticleOptions particleType, double targetHeightAdjust) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var10 = calculateSmartParticlePosition(owner, targetHeightAdjust);
               level.sendParticles(particleType, var10.x, var10.y, var10.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
         }
      }, Side.SERVER);
   }

   private static Vec3 calculateSmartParticlePosition(LivingEntity owner, double targetHeightAdjust) {
      Entity target = ((LivingEntityPatch)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class)).getTarget();
      return target != null && isTargetInRange(owner, target, 7.5)
         ? target.position().add(0.0, targetHeightAdjust, 0.0)
         : calculateParticlePosition(owner, 7.5, 0.0, 0.0);
   }

   private static boolean isTargetInRange(LivingEntity owner, Entity target, double range) {
      double distanceSq = owner.distanceToSqr(target);
      return distanceSq <= range * range;
   }

   public static InTimeEvent spawnAdvancedParticle(
      int startFrame, double forwardOffset, double rightOffset, double upOffset, ParticleOptions particleType, int count, double randomSpread, double speed
   ) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         EntityPatch<?> patch = entityPatch;
         if (patch.getOriginal() instanceof LivingEntity living) {
            if (living.level() instanceof ServerLevel level) {
               Vec3 var20 = calculateParticlePosition(living, forwardOffset, rightOffset, upOffset);
               level.sendParticles(particleType, var20.x, var20.y, var20.z, count, randomSpread, randomSpread, randomSpread, speed);
            }
         }
      }, Side.SERVER);
   }

   public static Vec3 calculateParticlePosition(LivingEntity entity, double forward, double right, double up) {
      Vec3 horizontalLook = new Vec3(Math.sin(-entity.getYRot() * (Math.PI / 180.0)), 0.0, Math.cos(entity.getYRot() * (Math.PI / 180.0))).normalize();
      Vec3 rightVec = new Vec3(-horizontalLook.z, 0.0, horizontalLook.x).normalize();
      return entity.getEyePosition().add(horizontalLook.scale(forward)).add(rightVec.scale(right)).add(0.0, up, 0.0);
   }

   public static void spawnFireballParticles(ServerLevel level, Vec3 position) {
      spawnFireballCore(level, position);
      spawnFireballInnerRing(level, position);
      spawnFireballOuterRing(level, position);
      spawnFireballVerticalEmission(level, position);
   }

   private static void spawnFireballCore(ServerLevel level, Vec3 position) {
      level.sendParticles(ParticleTypes.FLAME, position.x, position.y + 0.3, position.z, 20, 0.3, 0.2, 0.3, 0.08);
      level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, position.x, position.y + 0.4, position.z, 8, 0.1, 0.1, 0.1, 0.05);
      level.sendParticles(ParticleTypes.LAVA, position.x, position.y + 0.2, position.z, 5, 0.15, 0.1, 0.15, 0.0);
   }

   private static void spawnFireballInnerRing(ServerLevel level, Vec3 center) {
      int particles = 24;
      float radius = 0.6F;

      for (int i = 0; i < particles; i++) {
         float angle = (float)((Math.PI * 2) * i / particles);
         float x = radius * Mth.cos(angle);
         float z = radius * Mth.sin(angle);
         Vec3 pos = center.add(x, 0.2, z);
         Vec3 motion = new Vec3(-z * 0.15, 0.1, x * 0.15);
         level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 1, motion.x, motion.y, motion.z, 0.0);
      }
   }

   private static void spawnFireballOuterRing(ServerLevel level, Vec3 center) {
      int particles = 18;
      float radius = 1.2F;

      for (int i = 0; i < particles; i++) {
         float angle = (float)((Math.PI * 2) * i / particles);
         float x = radius * Mth.cos(angle);
         float z = radius * Mth.sin(angle);
         Vec3 pos = center.add(x, 0.1, z);
         Vec3 motion = new Vec3(x * 0.08, 0.08, z * 0.08);
         SimpleParticleType particleType = i % 3 == 0 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
         level.sendParticles(particleType, pos.x, pos.y, pos.z, 1, motion.x, motion.y, motion.z, 0.0);
      }
   }

   private static void spawnFireballVerticalEmission(ServerLevel level, Vec3 center) {
      for (int i = 0; i < 12; i++) {
         level.sendParticles(
            ParticleTypes.FLAME,
            center.x + (Math.random() - 0.5) * 0.4,
            center.y + 0.1,
            center.z + (Math.random() - 0.5) * 0.4,
            1,
            (Math.random() - 0.5) * 0.1,
            Math.random() * 0.3 + 0.2,
            (Math.random() - 0.5) * 0.1,
            0.05
         );
      }

      for (int i = 0; i < 8; i++) {
         level.sendParticles(
            ParticleTypes.ELECTRIC_SPARK,
            center.x + (Math.random() - 0.5) * 0.6,
            center.y + 0.05,
            center.z + (Math.random() - 0.5) * 0.6,
            1,
            (Math.random() - 0.5) * 0.15,
            Math.random() * 0.25 + 0.15,
            (Math.random() - 0.5) * 0.15,
            0.03
         );
      }
   }

   public static void spawnEnhancedFireballParticles(ServerLevel level, Vec3 position) {
      spawnConcentricFireRing(level, position, 1.5F, 12, 0.12F, ParticleTypes.FLAME);
      spawnConcentricFireRing(level, position, 2.0F, 15, 0.08F, ParticleTypes.END_ROD);
      spawnConcentricFireRing(level, position, 2.5F, 20, 0.05F, ParticleTypes.LAVA);
      level.sendParticles(ParticleTypes.LAVA, position.x, position.y + 0.3, position.z, 8, 0.25, 0.2, 0.25, 0.02);

      for (int i = 0; i < 6; i++) {
         double angle = (Math.PI * 2) * i / 6.0;
         double distance = 0.8;
         double x = position.x + distance * Math.cos(angle);
         double z = position.z + distance * Math.sin(angle);
         level.sendParticles(ParticleTypes.FLAME, x, position.y, z, 3, 0.1, 0.4, 0.1, 0.07);
      }
   }

   private static void spawnConcentricFireRing(ServerLevel level, Vec3 center, float radius, int particles, float speed, ParticleOptions particleType) {
      for (int i = 0; i < particles; i++) {
         float angle = (float)((Math.PI * 2) * i / particles);
         float x = radius * Mth.cos(angle);
         float z = radius * Mth.sin(angle);
         Vec3 pos = center.add(x, 0.15, z);
         Vec3 motion = new Vec3(-z * speed, 0.06, x * speed);
         level.sendParticles(particleType, pos.x, pos.y, pos.z, 1, motion.x, motion.y, motion.z, 0.0);
      }
   }

   public static void spawnFireballTrail(ServerLevel level, Vec3 startPos, Vec3 endPos) {
      Vec3 direction = endPos.subtract(startPos).normalize();
      double distance = startPos.distanceTo(endPos);
      int particles = (int)(distance * 5.0);

      for (int i = 0; i < particles; i++) {
         double progress = (double)i / particles;
         Vec3 particlePos = startPos.add(direction.scale(distance * progress));
         level.sendParticles(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, 1, 0.1, 0.1, 0.1, 0.02);
      }
   }

   public static InTimeEvent createMagmaEruption(float startFrame) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(
         time,
         (entitypatch, self, params) -> {
            if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
               LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
               ClientLevel level = (ClientLevel)entity.level();
               Vec3 center = entity.position().add(0.0, 0.1, 0.0);

               for (int i = 0; i < 12; i++) {
                  double spread = 0.7;
                  Vec3 spawnPos = new Vec3(
                     center.x + (Math.random() - 0.5) * spread,
                     center.y + (Math.random() - 0.5) * spread * 0.5,
                     center.z + (Math.random() - 0.5) * spread
                  );
                  Vec3 velocity = new Vec3((Math.random() - 0.5) * 0.25, Math.random() * 0.4 + 0.3, (Math.random() - 0.5) * 0.25);
                  ParticleOptions type = Math.random() < 0.8 ? ParticleTypes.LAVA : ParticleTypes.FLAME;
                  level.addParticle(type, spawnPos.x, spawnPos.y, spawnPos.z, velocity.x, velocity.y, velocity.z);
               }

               for (int spiral = 0; spiral < 8; spiral++) {
                  float baseAngle = (float)(spiral * Math.PI * 0.25);

                  for (int layer = 0; layer < 5; layer++) {
                     float progress = layer / 4.0F;
                     double angle = baseAngle + progress * Math.PI * 2.0;
                     float radius = 0.3F + progress * 0.7F;
                     float yPos = (float)(center.y + progress * 2.5F);
                     Vec3 pos = new Vec3(center.x + radius * Math.cos(angle), yPos, center.z + radius * Math.sin(angle));
                     level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, Math.cos(angle) * 0.1F, 0.2F, Math.sin(angle) * 0.1F);
                  }
               }

               for (int i = 0; i < 24; i++) {
                  double angle = Math.random() * 2.0 * Math.PI;
                  double radius = Math.random() * 1.8;
                  Vec3 pos = new Vec3(center.x + radius * Math.cos(angle), center.y, center.z + radius * Math.sin(angle));
                  level.addParticle(ParticleTypes.LAVA, pos.x, pos.y, pos.z, Math.cos(angle) * 0.05, 0.02, Math.sin(angle) * 0.05);
                  if (i % 3 == 0) {
                     level.addParticle(ParticleTypes.SMALL_FLAME, pos.x, pos.y + 0.1, pos.z, Math.cos(angle) * 0.1, 0.15, Math.sin(angle) * 0.1);
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent createShockwave(float startFrame) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(
         time,
         (entitypatch, self, params) -> {
            if (!((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
               LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
               ServerLevel level = (ServerLevel)attacker.level();
               Vec3 center = new Vec3(attacker.getX(), attacker.getY() + 0.2, attacker.getZ());

               for (int wave = 0; wave < 3; wave++) {
                  double progress = wave / 2.0;
                  double radius = Mth.lerp(progress, 4.5, 9.0);

                  for (int i = 0; i < 80; i++) {
                     double angle = (Math.PI * 2) * i / 80.0;
                     Vec3 pos = new Vec3(
                           radius * Math.cos(angle + progress * 1.3 * Math.PI),
                           Math.sin(angle * 2.2 + wave * 0.6) * 0.9,
                           radius * Math.sin(angle + progress * 1.3 * Math.PI)
                        )
                        .add(center);
                     Vec3 motion = pos.subtract(center).normalize().scale(0.4 * (0.6 + 0.4 * (1.0 - progress))).add(0.0, 0.18, 0.0);
                     level.sendParticles(
                        ParticleTypes.FLAME,
                        pos.x,
                        pos.y,
                        pos.z,
                        1,
                        motion.x * 0.35,
                        motion.y * 0.7,
                        motion.z * 0.35,
                        0.9
                     );
                  }
               }

               level.sendParticles(ParticleTypes.END_ROD, center.x, center.y + 0.5, center.z, 25, 1.6, 0.7, 1.6, 0.8);
            }
         },
         Side.SERVER
      );
   }

   public static InTimeEvent createBeastRoarEffect(float startFrame) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(
         time,
         (entitypatch, self, params) -> {
            if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
               LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
               ClientLevel level = (ClientLevel)entity.level();
               Vec3 origin = entity.getEyePosition(1.0F).add(entity.getLookAngle().scale(0.5));

               for (int ring = 0; ring < 3; ring++) {
                  float radius = 1.5F + ring * 1.8F;
                  int particles = 18 + ring * 6;

                  for (int i = 0; i < particles; i++) {
                     float angle = (float)((Math.PI * 2) * i / particles);
                     Vec3 offset = new Vec3(radius * Mth.cos(angle), 0.2 * (ring - 1), radius * Mth.sin(angle));
                     level.addParticle(
                        ParticleTypes.END_ROD,
                        origin.x + offset.x,
                        origin.y + offset.y,
                        origin.z + offset.z,
                        offset.x * 0.15F,
                        ring == 1 ? 0.05F : -0.02F,
                        offset.z * 0.15F
                     );
                  }
               }

               for (int i = 0; i < 25; i++) {
                  Vec3 randomOffset = new Vec3((Math.random() - 0.5) * 3.0, (Math.random() - 0.3) * 1.5, (Math.random() - 0.5) * 3.0);
                  level.addParticle(
                     ParticleTypes.CLOUD,
                     origin.x + randomOffset.x,
                     origin.y + randomOffset.y,
                     origin.z + randomOffset.z,
                     randomOffset.x * 0.08F,
                     randomOffset.y * 0.05F + 0.1F,
                     randomOffset.z * 0.08F
                  );
               }

               if (entity.onGround()) {
                  for (int i = 0; i < 12; i++) {
                     double angle = Math.random() * 2.0 * Math.PI;
                     double distance = 1.5 + Math.random() * 3.0;
                     Vec3 groundPos = new Vec3(
                        origin.x + distance * Math.cos(angle), origin.y - 1.2, origin.z + distance * Math.sin(angle)
                     );
                     level.addParticle(
                        ParticleTypes.POOF,
                        groundPos.x,
                        groundPos.y + 0.05,
                        groundPos.z,
                        (Math.random() - 0.5) * 0.1,
                        0.15 + Math.random() * 0.2,
                        (Math.random() - 0.5) * 0.1
                     );
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent createRoarShockwave(float startFrame) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entitypatch, self, params) -> {
         if (!((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
            LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
            ServerLevel level = (ServerLevel)entity.level();
            Vec3 direction = entity.getLookAngle();
            Vec3 origin = entity.getEyePosition(1.0F);

            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(8.0), e -> e != entity)) {
               Vec3 toTarget = target.position().subtract(origin).normalize();
               if (direction.dot(toTarget) > 0.7) {
                  target.knockback(0.8F, direction.x * -1.0, direction.z * -1.0);
               }
            }
         }
      }, Side.SERVER);
   }

   public static InPeriodEvent createLavaRingEffect(int startFrame, int endFrame) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      return InPeriodEvent.create(
         start,
         end,
         (entitypatch, self, params) -> {
            if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
               LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
               ClientLevel level = (ClientLevel)entity.level();
               Vec3 center = entity.position().add(0.0, 0.1, 0.0);

               for (int i = 0; i < 36; i++) {
                  float angle = (float)((Math.PI * 2) * i / 36.0);
                  float radius = 3.5F;
                  level.addParticle(
                     ParticleTypes.LAVA,
                     center.x + radius * Mth.cos(angle),
                     center.y,
                     center.z + radius * Mth.sin(angle),
                     0.0,
                     0.05F,
                     0.0
                  );
                  if (i % 3 == 0) {
                     level.addParticle(
                        ParticleTypes.FLAME,
                        center.x + radius * Mth.cos(angle),
                        center.y + 0.1F,
                        center.z + radius * Mth.sin(angle),
                        0.0,
                        0.1F,
                        0.0
                     );
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InPeriodEvent createSmokeRingEffect(int startFrame, int endFrame) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      return InPeriodEvent.create(
         start,
         end,
         (entitypatch, self, params) -> {
            if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
               LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
               ClientLevel level = (ClientLevel)entity.level();
               Vec3 center = entity.position().add(0.0, 0.1, 0.0);
               int particleCount = 72;
               float radius = 7.5F;

               for (int i = 0; i < particleCount; i++) {
                  float angle = (float)((Math.PI * 2) * i / particleCount);
                  double x = center.x + radius * Math.cos(angle);
                  double z = center.z + radius * Math.sin(angle);
                  level.addParticle(ParticleTypes.SOUL, x, center.y, z, 0.0, 0.05F, 0.0);
                  if (i % 2 == 0) {
                     level.addParticle(ParticleTypes.SMOKE, x, center.y + 0.15F, z, 0.0, 0.08F, 0.0);
                  }
               }

               for (int i = 0; i < 8; i++) {
                  float randomAngle = level.random.nextFloat() * (float) Math.PI * 2.0F;
                  double randomRadius = radius * (0.8F + level.random.nextFloat() * 0.4F);
                  level.addParticle(
                     ParticleTypes.SMOKE,
                     center.x + randomRadius * Math.cos(randomAngle),
                     center.y + 0.2F,
                     center.z + randomRadius * Math.sin(randomAngle),
                     (level.random.nextFloat() - 0.5F) * 0.1F,
                     0.15F,
                     (level.random.nextFloat() - 0.5F) * 0.1F
                  );
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InPeriodEvent createSoulRingEffect(int startFrame, int endFrame) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      return InPeriodEvent.create(
         start,
         end,
         (entitypatch, self, params) -> {
            if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
               LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
               ClientLevel level = (ClientLevel)entity.level();
               Vec3 entityPos = entity.position();
               Vec3 horizontalLook = entity.getLookAngle();
               horizontalLook = new Vec3(horizontalLook.x, 0.0, horizontalLook.z).normalize();
               Vec3 center = entityPos.add(horizontalLook.scale(2.0)).add(0.0, 0.1, 0.0);
               int particleCount = 36;
               float radius = 5.0F;

               for (int i = 0; i < particleCount; i++) {
                  float angle = (float)((Math.PI * 2) * i / particleCount);
                  double x = center.x + radius * Math.cos(angle);
                  double z = center.z + radius * Math.sin(angle);
                  level.addParticle(ParticleTypes.SCULK_SOUL, x, center.y, z, 0.0, 0.05F, 0.0);
                  if (i % 2 == 0) {
                     level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, center.y + 0.15F, z, 0.0, 0.08F, 0.0);
                  }
               }

               for (int i = 0; i < 8; i++) {
                  float randomAngle = level.random.nextFloat() * (float) Math.PI * 2.0F;
                  double randomRadius = radius * (0.8F + level.random.nextFloat() * 0.4F);
                  level.addParticle(
                     ParticleTypes.SOUL,
                     center.x + randomRadius * Math.cos(randomAngle),
                     center.y + 0.2F,
                     center.z + randomRadius * Math.sin(randomAngle),
                     (level.random.nextFloat() - 0.5F) * 0.1F,
                     0.15F,
                     (level.random.nextFloat() - 0.5F) * 0.1F
                  );
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent createChargedRingExplosion(float startFrame, float ringRadius, float explosionIntensity) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entitypatch, self, params) -> {
         if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
            LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
            ClientLevel level = (ClientLevel)entity.level();
            Vec3 center = entity.position().add(0.0, 0.7, 0.0);
            generateChargedRing(level, center, ringRadius, explosionIntensity);
            generateDualConeExplosion(level, entitypatch, center, explosionIntensity);
            generateShockwaveEffect(level, center, explosionIntensity);
         }
      }, Side.CLIENT);
   }

   private static void generateChargedRing(ClientLevel level, Vec3 center, float radius, float intensity) {
      int ringParticles = (int)(24.0F * intensity);

      for (int ring = 0; ring < 2; ring++) {
         float currentRadius = radius * (0.9F + ring * 0.1F);
         int particles = ringParticles / (ring + 1);

         for (int i = 0; i < particles; i++) {
            float angle = (float)((Math.PI * 2) * i / particles);
            float height = (ring - 0.5F) * 0.2F;
            double x = currentRadius * Math.cos(angle);
            double z = currentRadius * Math.sin(angle);
            Vec3 particlePos = center.add(x, height, z);
            ParticleOptions particleType;
            if (i % 4 == 0) {
               particleType = new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.2F);
            } else {
               particleType = ParticleTypes.SMOKE;
            }

            Vec3 motion = new Vec3(x * 0.08F * intensity, 0.03F + (ring - 0.5F) * 0.02F, z * 0.08F * intensity);
            level.addParticle(particleType, particlePos.x, particlePos.y, particlePos.z, motion.x, motion.y, motion.z);
         }
      }
   }

   private static void generateDualConeExplosion(ClientLevel level, LivingEntityPatch<?> entityPatch, Vec3 center, float intensity) {
      int coneParticles = (int)(120.0F * intensity);
      double r = 0.4 * intensity;
      double t = 0.008;

      for (int group = 0; group < 2; group++) {
         float angle = group == 0 ? 110.0F : 70.0F;

         for (int i = 0; i < coneParticles; i++) {
            double theta = (Math.PI * 2) * Math.random();
            double phi = (Math.random() - 0.2) * Math.PI * t / r;
            Vec3f direction = new Vec3f(
               (float)(r * Math.cos(phi) * Math.cos(theta)), (float)(r * Math.cos(phi) * Math.sin(theta)) * 1.3F, (float)(r * Math.sin(phi))
            );
            OpenMatrix4f rotation = new OpenMatrix4f()
               .rotate((float)Math.toRadians(-entityPatch.getYRot() + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F))
               .rotate((float)Math.toRadians(angle), new Vec3f(1.0F, 0.0F, 0.0F));
            OpenMatrix4f.transform3v(rotation, direction, direction);
            float speedVariation = 0.2F + 0.2F * (float)Math.random();
            direction.scale(speedVariation * intensity);
            ParticleOptions particle;
            if (Math.random() < 0.7) {
               Vector3f color = getRandomDustColor();
               particle = new DustParticleOptions(color, 1.0F + (float)Math.random() * 0.6F);
            } else {
               particle = ParticleTypes.SMOKE;
            }

            level.addParticle(particle, center.x, center.y, center.z, direction.x * 1.1F, direction.y * 1.2F, direction.z * 1.1F);
            if (Math.random() < 0.2F * intensity) {
               Vec3f dustDir = new Vec3f(direction.x, direction.y, direction.z).scale(0.5F);
               Vector3f trailColor = getRandomDustColor();
               level.addParticle(
                  new DustParticleOptions(trailColor, 0.8F), center.x, center.y, center.z, dustDir.x, dustDir.y * 1.2F, dustDir.z
               );
            }
         }
      }
   }

   private static void generateShockwaveEffect(ClientLevel level, Vec3 center, float intensity) {
      int shockwaveLayers = (int)(2.0F * intensity);

      for (int wave = 0; wave < shockwaveLayers; wave++) {
         float progress = (float)wave / shockwaveLayers;
         float radius = 1.0F + progress * 1.5F * intensity;
         int particles = (int)(16.0F * intensity);

         for (int i = 0; i < particles; i++) {
            float angle = (float)((Math.PI * 2) * i / particles);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Vec3 particlePos = center.add(x, 0.1F, z);
            Vec3 motion = new Vec3(x * 0.05F * (1.0F - progress) * intensity, 0.01F, z * 0.05F * (1.0F - progress) * intensity);
            level.addParticle(
               ParticleTypes.SMOKE, particlePos.x, particlePos.y, particlePos.z, motion.x, motion.y, motion.z
            );
            if (i % 4 == 0) {
               Vector3f color = getRandomDustColor();
               level.addParticle(
                  new DustParticleOptions(color, 1.2F),
                  particlePos.x,
                  particlePos.y + 0.05F,
                  particlePos.z,
                  motion.x * 0.6F,
                  motion.y * 0.6F,
                  motion.z * 0.6F
               );
            }
         }
      }

      generateCenterBurst(level, center, intensity);
   }

   private static void generateCenterBurst(ClientLevel level, Vec3 center, float intensity) {
      int burstParticles = (int)(20.0F * intensity);

      for (int i = 0; i < burstParticles; i++) {
         double phi = Math.random() * Math.PI;
         double theta = Math.random() * 2.0 * Math.PI;
         double burstRadius = 0.3 + Math.random() * 0.7 * intensity;
         double x = burstRadius * Math.sin(phi) * Math.cos(theta);
         double y = burstRadius * Math.cos(phi);
         double z = burstRadius * Math.sin(phi) * Math.sin(theta);
         Vec3 particlePos = center.add(x, y, z);
         Vec3 motion = particlePos.subtract(center).normalize().scale(0.15 + Math.random() * 0.2 * intensity);
         ParticleOptions particleType;
         if (Math.random() < 0.4) {
            particleType = ParticleTypes.SMOKE;
         } else {
            Vector3f color = getRandomDustColor();
            particleType = new DustParticleOptions(color, 1.5F);
         }

         level.addParticle(particleType, particlePos.x, particlePos.y, particlePos.z, motion.x, motion.y, motion.z);
      }
   }

   private static Vector3f getRandomDustColor() {
      return switch ((int)(Math.random() * 4.0)) {
         case 0 -> new Vector3f(1.0F, 0.0F, 0.0F);
         case 1 -> new Vector3f(0.9F, 0.1F, 0.1F);
         case 2 -> new Vector3f(0.7F, 0.0F, 0.0F);
         case 3 -> new Vector3f(0.5F, 0.0F, 0.0F);
         default -> new Vector3f(1.0F, 0.0F, 0.0F);
      };
   }

   public static InTimeEvent createBlueChargedRingExplosion(float startFrame, float ringRadius, float explosionIntensity) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entitypatch, self, params) -> {
         if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
            LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
            ClientLevel level = (ClientLevel)entity.level();
            Vec3 center = entity.position().add(0.0, 0.7, 0.0);
            generateBlueChargedRing(level, center, ringRadius, explosionIntensity);
            generateDualConeExplosion_B(level, entitypatch, center, explosionIntensity);
            generateShockwaveEffect_B(level, center, explosionIntensity);
         }
      }, Side.CLIENT);
   }

   private static void generateBlueChargedRing(ClientLevel level, Vec3 center, float radius, float intensity) {
      int ringParticles = (int)(24.0F * intensity);

      for (int ring = 0; ring < 2; ring++) {
         float currentRadius = radius * (0.9F + ring * 0.1F);
         int particles = ringParticles / (ring + 1);

         for (int i = 0; i < particles; i++) {
            float angle = (float)((Math.PI * 2) * i / particles);
            float height = (ring - 0.5F) * 0.2F;
            double x = currentRadius * Math.cos(angle);
            double z = currentRadius * Math.sin(angle);
            Vec3 particlePos = center.add(x, height, z);
            ParticleOptions particleType;
            if (i % 4 == 0) {
               particleType = new DustParticleOptions(new Vector3f(0.0F, 0.8F, 1.0F), 1.2F);
            } else {
               particleType = ParticleTypes.SMOKE;
            }

            Vec3 motion = new Vec3(x * 0.08F * intensity, 0.03F + (ring - 0.5F) * 0.02F, z * 0.08F * intensity);
            level.addParticle(particleType, particlePos.x, particlePos.y, particlePos.z, motion.x, motion.y, motion.z);
         }
      }
   }

   private static void generateDualConeExplosion_B(ClientLevel level, LivingEntityPatch<?> entityPatch, Vec3 center, float intensity) {
      int coneParticles = (int)(120.0F * intensity);
      double r = 0.4 * intensity;
      double t = 0.008;

      for (int group = 0; group < 2; group++) {
         float angle = group == 0 ? 110.0F : 70.0F;

         for (int i = 0; i < coneParticles; i++) {
            double theta = (Math.PI * 2) * Math.random();
            double phi = (Math.random() - 0.2) * Math.PI * t / r;
            Vec3f direction = new Vec3f(
               (float)(r * Math.cos(phi) * Math.cos(theta)), (float)(r * Math.cos(phi) * Math.sin(theta)) * 1.3F, (float)(r * Math.sin(phi))
            );
            OpenMatrix4f rotation = new OpenMatrix4f()
               .rotate((float)Math.toRadians(-entityPatch.getYRot() + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F))
               .rotate((float)Math.toRadians(angle), new Vec3f(1.0F, 0.0F, 0.0F));
            OpenMatrix4f.transform3v(rotation, direction, direction);
            float speedVariation = 0.2F + 0.2F * (float)Math.random();
            direction.scale(speedVariation * intensity);
            ParticleOptions particle;
            if (Math.random() < 0.7) {
               Vector3f color = getRandomDustColor_B();
               particle = new DustParticleOptions(color, 1.0F + (float)Math.random() * 0.6F);
            } else {
               particle = ParticleTypes.SMOKE;
            }

            level.addParticle(particle, center.x, center.y, center.z, direction.x * 1.1F, direction.y * 1.2F, direction.z * 1.1F);
            if (Math.random() < 0.2F * intensity) {
               Vec3f dustDir = new Vec3f(direction.x, direction.y, direction.z).scale(0.5F);
               Vector3f trailColor = getRandomDustColor_B();
               level.addParticle(
                  new DustParticleOptions(trailColor, 0.8F), center.x, center.y, center.z, dustDir.x, dustDir.y * 1.2F, dustDir.z
               );
            }
         }
      }
   }

   private static void generateShockwaveEffect_B(ClientLevel level, Vec3 center, float intensity) {
      int shockwaveLayers = (int)(2.0F * intensity);

      for (int wave = 0; wave < shockwaveLayers; wave++) {
         float progress = (float)wave / shockwaveLayers;
         float radius = 1.0F + progress * 1.5F * intensity;
         int particles = (int)(16.0F * intensity);

         for (int i = 0; i < particles; i++) {
            float angle = (float)((Math.PI * 2) * i / particles);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Vec3 particlePos = center.add(x, 0.1F, z);
            Vec3 motion = new Vec3(x * 0.05F * (1.0F - progress) * intensity, 0.01F, z * 0.05F * (1.0F - progress) * intensity);
            level.addParticle(
               ParticleTypes.SMOKE, particlePos.x, particlePos.y, particlePos.z, motion.x, motion.y, motion.z
            );
            if (i % 4 == 0) {
               Vector3f color = getRandomDustColor_B();
               level.addParticle(
                  new DustParticleOptions(color, 1.2F),
                  particlePos.x,
                  particlePos.y + 0.05F,
                  particlePos.z,
                  motion.x * 0.6F,
                  motion.y * 0.6F,
                  motion.z * 0.6F
               );
            }
         }
      }

      generateCenterBurst_B(level, center, intensity);
   }

   private static void generateCenterBurst_B(ClientLevel level, Vec3 center, float intensity) {
      int burstParticles = (int)(20.0F * intensity);

      for (int i = 0; i < burstParticles; i++) {
         double phi = Math.random() * Math.PI;
         double theta = Math.random() * 2.0 * Math.PI;
         double burstRadius = 0.3 + Math.random() * 0.7 * intensity;
         double x = burstRadius * Math.sin(phi) * Math.cos(theta);
         double y = burstRadius * Math.cos(phi);
         double z = burstRadius * Math.sin(phi) * Math.sin(theta);
         Vec3 particlePos = center.add(x, y, z);
         Vec3 motion = particlePos.subtract(center).normalize().scale(0.15 + Math.random() * 0.2 * intensity);
         ParticleOptions particleType;
         if (Math.random() < 0.4) {
            particleType = ParticleTypes.SMOKE;
         } else {
            Vector3f color = getRandomDustColor_B();
            particleType = new DustParticleOptions(color, 1.5F);
         }

         level.addParticle(particleType, particlePos.x, particlePos.y, particlePos.z, motion.x, motion.y, motion.z);
      }
   }

   private static Vector3f getRandomDustColor_B() {
      return switch ((int)(Math.random() * 4.0)) {
         case 0 -> new Vector3f(0.0F, 0.8F, 1.0F);
         case 1 -> new Vector3f(0.1F, 0.4F, 1.0F);
         case 2 -> new Vector3f(0.0F, 0.2F, 0.9F);
         case 3 -> new Vector3f(0.4F, 1.0F, 1.0F);
         default -> new Vector3f(0.0F, 0.5F, 1.0F);
      };
   }

   public static InTimeEvent createDragonFlashBurst(float startFrame, float viewOffset) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entitypatch, self, params) -> {
         LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
         float yaw = entitypatch.getYRot();
         double radians = Math.toRadians(yaw);
         double cosYaw = Math.cos(radians);
         double sinYaw = Math.sin(radians);
         Vec3 viewDir = new Vec3(-sinYaw, 0.0, cosYaw).scale(viewOffset);
         Vec3 center = entity.position().add(0.0, 0.1, 0.0).add(viewDir);
         if (entity.level().isClientSide()) {
            ClientLevel level = (ClientLevel)entity.level();
            generateGroundRingBurst(level, center);
            generateVerticalBeam(level, center);
            generateDissipationEffect(level, center);
         } else {
            ServerLevel serverLevel = (ServerLevel)entity.level();
            dealAreaDamage(serverLevel, center, entity, getTotalAttackDamage(entitypatch), 2.0F, StunType.LONG, true);
         }
      }, Side.BOTH);
   }

   private static void generateGroundRingBurst(ClientLevel level, Vec3 center) {
      level.addParticle(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0.0, 0.0, 0.0);
      int ringSegments = 24;
      float majorRadius = 1.5F;
      float minorRadius = 0.5F;

      for (int i = 0; i < ringSegments; i++) {
         float angle = (float)((Math.PI * 2) * i / ringSegments);
         float x = majorRadius * (float)Math.cos(angle);
         float z = minorRadius * (float)Math.sin(angle);
         Vec3 ringPos = center.add(x, 0.05, z);
         float speedMultiplier = 0.3F + (float)Math.random() * 0.1F;
         float speedX = x / majorRadius * speedMultiplier * 0.9F;
         float speedZ = z / minorRadius * speedMultiplier * 0.9F;
         boolean isLeftRight = Math.abs(angle) < 0.5 || Math.abs(angle - Math.PI) < 0.5;
         if (isLeftRight) {
            for (int j = 0; j < 2; j++) {
               float offset = (float)Math.random() * 0.15F - 0.075F;
               Vec3 offsetPos = ringPos.add(offset, 0.0, offset);
               if (j == 0) {
                  ParticleOptions particleType = ParticleTypes.CLOUD;
                  level.addParticle(
                     particleType, offsetPos.x, offsetPos.y, offsetPos.z, speedX * 0.9F, 0.2F + (float)Math.random() * 0.2F, speedZ * 0.9F
                  );
               } else {
                  ParticleOptions particleType = ParticleTypes.END_ROD;
                  level.addParticle(
                     particleType,
                     offsetPos.x,
                     offsetPos.y,
                     offsetPos.z,
                     speedX * 0.6F,
                     0.12F + (float)Math.random() * 0.15F,
                     speedZ * 0.6F
                  );
               }
            }
         }

         switch (i % 6) {
            case 0:
            case 1: {
               ParticleOptions particleType = ParticleTypes.CLOUD;
               level.addParticle(
                  particleType, ringPos.x, ringPos.y, ringPos.z, speedX * 0.5F, 0.1F + (float)Math.random() * 0.15F, speedZ * 0.5F
               );
               break;
            }
            case 2:
            case 3: {
               ParticleOptions particleType = ParticleTypes.CHERRY_LEAVES;
               level.addParticle(
                  particleType, ringPos.x, ringPos.y, ringPos.z, speedX * 0.3F, 0.08F + (float)Math.random() * 0.12F, speedZ * 0.3F
               );
               break;
            }
            case 4: {
               ParticleOptions particleType = ParticleTypes.ENCHANT;
               level.addParticle(
                  particleType, ringPos.x, ringPos.y, ringPos.z, speedX * 0.7F, 0.15F + (float)Math.random() * 0.2F, speedZ * 0.7F
               );
               break;
            }
            case 5: {
               ParticleOptions particleType = ParticleTypes.CHERRY_LEAVES;
               level.addParticle(
                  particleType, ringPos.x, ringPos.y, ringPos.z, speedX * 0.25F, 0.06F + (float)Math.random() * 0.1F, speedZ * 0.25F
               );
            }
         }
      }

      for (int side = 0; side < 2; side++) {
         float direction = side == 0 ? 1.0F : -1.0F;

         for (int i = 0; i < 8; i++) {
            float offsetX = direction * (majorRadius * 0.7F + (float)Math.random() * 0.3F);
            float offsetZ = (float)Math.random() * 0.6F - 0.3F;
            Vec3 wavePos = center.add(offsetX, 0.08F + (float)Math.random() * 0.15F, offsetZ);
            level.addParticle(
               ParticleTypes.CLOUD,
               wavePos.x,
               wavePos.y,
               wavePos.z,
               direction * (0.2F + (float)Math.random() * 0.15F),
               0.08F + (float)Math.random() * 0.12F,
               (float)Math.random() * 0.08F - 0.04F
            );
            level.addParticle(ParticleTypes.SMOKE, wavePos.x - direction * 0.2F, wavePos.y, wavePos.z, direction * 0.15F, 0.06F, 0.0);
         }
      }

      for (int ring = 0; ring < 2; ring++) {
         float ringRadius = 0.4F + ring * 0.4F;
         int particles = 12;

         for (int i = 0; i < particles; i++) {
            float angle = (float)((Math.PI * 2) * i / particles);
            float x = ringRadius * (float)Math.cos(angle);
            float z = ringRadius * (float)Math.sin(angle);
            x *= 1.3F;
            Vec3 ripplePos = center.add(x, 0.02F, z);
            level.addParticle(ParticleTypes.SMOKE, ripplePos.x, ripplePos.y, ripplePos.z, x * 0.03F, 0.015F, z * 0.03F);
         }
      }
   }

   private static void generateVerticalBeam(ClientLevel level, Vec3 center) {
      int beamLayers = 8;
      float beamRadius = 0.5F;
      float beamHeight = 2.5F;

      for (int layer = 0; layer < beamLayers; layer++) {
         float yPos = (float)(center.y + beamHeight * layer / beamLayers);
         float layerRadius = beamRadius * (1.0F - (float)layer / beamLayers * 0.3F);
         int particlesPerLayer = 12;

         for (int i = 0; i < particlesPerLayer; i++) {
            float angle = (float)((Math.PI * 2) * i / particlesPerLayer);
            float x = layerRadius * (float)Math.cos(angle);
            float z = layerRadius * (float)Math.sin(angle);
            Vec3 particlePos = center.add(x, yPos, z);
            float upwardSpeed = 0.4F + (float)Math.random() * 0.3F;
            switch (i % 6) {
               case 0:
               case 1: {
                  ParticleOptions particleType = ParticleTypes.CHERRY_LEAVES;
                  level.addParticle(
                     particleType,
                     particlePos.x,
                     particlePos.y + 2.0,
                     particlePos.z,
                     (float)Math.random() * 0.1F - 0.05F,
                     upwardSpeed,
                     (float)Math.random() * 0.1F - 0.05F
                  );
                  break;
               }
               case 2: {
                  ParticleOptions particleType = ParticleTypes.END_ROD;
                  level.addParticle(particleType, particlePos.x, particlePos.y, particlePos.z, 0.0, upwardSpeed * 1.2F, 0.0);
                  break;
               }
               case 3: {
                  ParticleOptions particleType = ParticleTypes.ENCHANT;
                  level.addParticle(
                     particleType,
                     particlePos.x,
                     particlePos.y,
                     particlePos.z,
                     (float)Math.random() * 0.15F - 0.075F,
                     upwardSpeed * 0.8F,
                     (float)Math.random() * 0.15F - 0.075F
                  );
                  break;
               }
               case 4: {
                  ParticleOptions particleType = ParticleTypes.SMOKE;
                  level.addParticle(
                     particleType,
                     particlePos.x,
                     particlePos.y,
                     particlePos.z,
                     (float)Math.random() * 0.08F - 0.04F,
                     upwardSpeed * 0.6F,
                     (float)Math.random() * 0.08F - 0.04F
                  );
                  break;
               }
               case 5: {
                  ParticleOptions particleType = ParticleTypes.CLOUD;
                  level.addParticle(
                     particleType,
                     particlePos.x,
                     particlePos.y,
                     particlePos.z,
                     (float)Math.random() * 0.12F - 0.06F,
                     upwardSpeed * 0.5F,
                     (float)Math.random() * 0.12F - 0.06F
                  );
               }
            }
         }
      }

      for (int i = 0; i < 15; i++) {
         float yOffset = (float)Math.random() * beamHeight * 0.6F;
         level.addParticle(ParticleTypes.END_ROD, center.x, center.y + yOffset, center.z, 0.0, 0.5, 0.0);
      }
   }

   private static void generateDissipationEffect(ClientLevel level, Vec3 center) {
      int dissipationParticles = 40;
      float dissipationRadius = 1.5F;

      for (int i = 0; i < dissipationParticles; i++) {
         double phi = Math.random() * Math.PI;
         double theta = Math.random() * 2.0 * Math.PI;
         double radius = dissipationRadius * Math.random();
         double x = radius * Math.sin(phi) * Math.cos(theta);
         double y = radius * Math.cos(phi) * 0.5;
         double z = radius * Math.sin(phi) * Math.sin(theta);
         Vec3 particlePos = center.add(x, y + 2.0, z);
         Vec3 motion = particlePos.subtract(center.add(0.0, 2.0, 0.0)).normalize().scale(0.05 + Math.random() * 0.1);

         level.addParticle(switch (i % 4) {
            case 0 -> ParticleTypes.SMOKE;
            case 1 -> ParticleTypes.CLOUD;
            case 2 -> ParticleTypes.ENCHANT;
            default -> ParticleTypes.WHITE_ASH;
         }, particlePos.x, particlePos.y, particlePos.z, motion.x, motion.y * 0.5, motion.z);
      }

      int haloParticles = 16;
      float haloRadius = 0.8F;

      for (int i = 0; i < haloParticles; i++) {
         float angle = (float)((Math.PI * 2) * i / haloParticles);
         float x = haloRadius * (float)Math.cos(angle);
         float z = haloRadius * (float)Math.sin(angle);
         Vec3 haloPos = center.add(x, 4.2, z);
         level.addParticle(ParticleTypes.CLOUD, haloPos.x, haloPos.y, haloPos.z, x * 0.1F, 0.05F, z * 0.1F);
      }
   }

   public static InTimeEvent simpleGroundSplit(
      int startFrame, double viewOffset, double xOffset, double yOffset, double zOffset, float radius, boolean teamProtect
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(
         start,
         (entityPatch, self, params) -> groundSplit(entityPatch, viewOffset, xOffset, yOffset, zOffset, radius, teamProtect, false, true, false),
         Side.BOTH
      );
   }

   public static InTimeEvent CustomGroundSplit(
      int startFrame,
      double viewOffset,
      double xOffset,
      double yOffset,
      double zOffset,
      float radius,
      boolean teamProtect,
      boolean nosound,
      boolean noParticle,
      boolean hurtEntities
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(
         start,
         (entityPatch, self, params) -> groundSplit(entityPatch, viewOffset, xOffset, yOffset, zOffset, radius, teamProtect, nosound, noParticle, hurtEntities),
         Side.BOTH
      );
   }

   public static void groundSplit(
      LivingEntityPatch<?> entityPatch,
      double viewOffset,
      double xOffset,
      double yOffset,
      double zOffset,
      float radius,
      boolean teamProtect,
      boolean nosound,
      boolean noParticle,
      boolean hurtEntities
   ) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      float damage = getTotalAttackDamage(entityPatch) * 0.5F;
      Vec3 pos = entity.position();
      float yaw = entityPatch.getYRot();
      double radians = Math.toRadians(yaw);
      double cosYaw = Math.cos(radians);
      double sinYaw = Math.sin(radians);
      double worldX = xOffset * cosYaw + zOffset * sinYaw;
      double worldZ = -xOffset * sinYaw + zOffset * cosYaw;
      Vec3 viewDir = new Vec3(-sinYaw, 0.0, cosYaw).scale(viewOffset);
      Vec3 totalOffset = viewDir.add(worldX, yOffset, worldZ);
      Vec3 target = pos.add(totalOffset.x, -1.0 + totalOffset.y, totalOffset.z);
      Vec3 damagetarget = pos.add(totalOffset.x, totalOffset.y, totalOffset.z);
      if (entity.level() instanceof ServerLevel level && target != null) {
         LevelUtil.circleSlamFracture(entity, level, target, radius, nosound, noParticle, hurtEntities);
      }

      if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.GROUND_SLAM_VFX.get()) {
         float sacle = 0.36F;
         if (radius <= 3.0F) {
            Stone_1_Effek.playStone_1(
               Stone_1_Effek.Type.LEVEL1, ((LivingEntity)entityPatch.getOriginal()).level(), target.x, target.y, target.z, radius * sacle
            );
            Dirt_1_Effek.playDirt_1(
               Dirt_1_Effek.Type.LEVEL1, ((LivingEntity)entityPatch.getOriginal()).level(), target.x, target.y, target.z, radius * sacle
            );
         } else {
            Stone_2_Effek.playStone_2(
               Stone_2_Effek.Type.LEVEL1,
               ((LivingEntity)entityPatch.getOriginal()).level(),
               target.x,
               target.y,
               target.z,
               (radius - 2.0F) * sacle
            );
            Dirt_2_Effek.playDirt_2(
               Dirt_2_Effek.Type.LEVEL1,
               ((LivingEntity)entityPatch.getOriginal()).level(),
               target.x,
               target.y,
               target.z,
               (radius - 2.0F) * sacle
            );
         }
      }
   }

   public static void dealAreaDamage(ServerLevel level, Vec3 center, LivingEntity source, float damage, float radius, StunType stunType, boolean teamProtect) {
      if (!(radius <= 0.0F)) {
         AABB area = new AABB(
            center.x() - radius,
            center.y() - radius,
            center.z() - radius,
            center.x() + radius,
            center.y() + radius,
            center.z() + radius
         );
         if (teamProtect) {
            List<LivingEntity> entities = level.getEntitiesOfClass(
               LivingEntity.class,
               area,
               entityx -> entityx.isAlive()
                  && entityx.distanceToSqr(center) <= radius * radius
                  && entityx.getType().getCategory() != source.getType().getCategory()
                  && entityx != source
            );

            for (LivingEntity entity : new ArrayList<>(entities)) {
               if (entity.invulnerableTime >= 0 && source != null) {
                  entity.invulnerableTime = 0;
                  entity.hurt(
                     EpicFightDamageSources.shockwave(source)
                        .addRuntimeTag(EpicFightDamageTypeTags.FINISHER)
                        .setAnimation(Animations.EMPTY_ANIMATION)
                        .setInitialPosition(center)
                        .setStunType(stunType)
                        .setBaseImpact(damage / 5.0F),
                     damage
                  );
               }

               entity.invulnerableTime = 0;
            }
         } else {
            List<LivingEntity> entities = level.getEntitiesOfClass(
               LivingEntity.class,
               area,
               entityx -> entityx.isAlive() && entityx.distanceToSqr(center) <= radius * radius && entityx.getType() != source.getType() && entityx != source
            );

            for (LivingEntity entity : new ArrayList<>(entities)) {
               if (entity.invulnerableTime >= 0 && source != null) {
                  entity.invulnerableTime = 0;
                  entity.hurt(
                     EpicFightDamageSources.shockwave(source)
                        .addRuntimeTag(EpicFightDamageTypeTags.FINISHER)
                        .setAnimation(Animations.EMPTY_ANIMATION)
                        .setInitialPosition(center)
                        .setStunType(stunType)
                        .setBaseImpact(damage / 5.0F),
                     damage
                  );
                  entity.invulnerableTime = 0;
               }
            }
         }
      }
   }

   private static float getTotalAttackDamage(LivingEntityPatch<?> entityPatch) {
      LivingEntity owner = (LivingEntity)entityPatch.getOriginal();
      double baseDamage = owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
      return (float)baseDamage;
   }
}
