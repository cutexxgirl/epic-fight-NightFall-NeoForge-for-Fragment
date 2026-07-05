package com.hm.efn.util;

import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.entity.effect.CoTachiSlashEntity;
import com.hm.efn.entity.effect.CoTachiSlashPatch;
import com.hm.efn.entity.effect.FireWindEntity;
import com.hm.efn.entity.effect.FireWindPatch;
import com.hm.efn.entity.effect.SecludedEntity;
import com.hm.efn.entity.effect.SecludedPatch;
import com.hm.efn.entity.effect.TrailEntity;
import com.hm.efn.entity.effect.TrailPatch;
import com.hm.efn.gameasset.combos.Aetherialdusk;
import com.hm.efn.gameasset.combos.Yamato;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EffectEntityInvoker {
   private static final boolean HAS_DUMMY_MOD = ModList.get().isLoaded("dummmmmmy");
   private static final Map<LivingEntity, FireWindEntity> activeFireWinds = new WeakHashMap<>();
   private static final Map<LivingEntity, Boolean> secludedAttackEnabledMap = new WeakHashMap<>();

   public static void setSecludedAttackEnabled(LivingEntity player, boolean enabled) {
      secludedAttackEnabledMap.put(player, enabled);
   }

   public static boolean isSecludedAttackEnabled(LivingEntity player) {
      return secludedAttackEnabledMap.getOrDefault(player, true);
   }

   public static InTimeEvent summonTrail(int startFrame, double forwardDist, double heightOffset, double sideOffset, float scale, float zRotation) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var16 = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
               TrailEntity entity = new TrailEntity(owner, scale, new Vec3(sideOffset, heightOffset, forwardDist));
               entity.setPos(var16);
               level.addFreshEntity(entity);
               level.getServer().execute(() -> {
                  TrailPatch patch = entity.getPatch();
                  if (patch != null) {
                     patch.setZRotation(zRotation);
                  }
               });
            }
         }
      }, Side.BOTH);
   }

   public static InTimeEvent summonTrailAtTarget(int startFrame, float scale, float zRotation, double heightAdjustment) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         LivingEntity caster = (LivingEntity)entityPatch.getOriginal();
         if (caster.level() instanceof ServerLevel level) {
            Entity target = entityPatch.getTarget();
            if (target != null) {
               Vec3 spawnPos = target.position().add(0.0, heightAdjustment, 0.0);
               TrailEntity entity = new TrailEntity(caster, scale, Vec3.ZERO);
               entity.setPos(spawnPos);
               level.addFreshEntity(entity);
               level.getServer().execute(() -> {
                  TrailPatch patch = entity.getPatch();
                  if (patch != null) {
                     patch.setZRotation(zRotation);
                  }
               });
            }
         }
      }, Side.BOTH);
   }

   public static InTimeEvent summonTachiSlash(int startFrame, double forwardDist, double heightOffset, double sideOffset, float scale, float zRotation) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var16 = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
               CoTachiSlashEntity entity = new CoTachiSlashEntity(owner, scale, new Vec3(sideOffset, heightOffset, forwardDist));
               entity.setPos(var16);
               level.addFreshEntity(entity);
               level.getServer().execute(() -> {
                  CoTachiSlashPatch patch = (CoTachiSlashPatch)entity.getPatch();
                  if (patch != null) {
                     patch.setZRotation(zRotation);
                  }
               });
            }
         }
      }, Side.BOTH);
   }

   public static InTimeEvent summonSecluded_Skill(int startFrame, double forwardDist, double heightOffset, double sideOffset, float scale, float zRotation) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               if (isSecludedAttackEnabled(owner)) {
                  Vec3 var16 = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
                  SecludedEntity entity = new SecludedEntity(owner, scale, new Vec3(sideOffset, heightOffset, forwardDist));
                  entity.setPos(var16);
                  level.addFreshEntity(entity);
                  level.getServer().execute(() -> {
                     SecludedPatch patch = entity.getPatch();
                     if (patch != null) {
                        patch.setZRotation(zRotation);
                     }
                  });
               }
            }
         }
      }, Side.BOTH);
   }

   public static InTimeEvent summonSecluded_Attack(int startFrame, double forwardDist, double heightOffset, double sideOffset, float scale, float zRotation) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               if (isSecludedAttackEnabled(owner)) {
                  Vec3 spawnPos = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
                  SecludedEntity entity = new SecludedEntity(owner, scale, new Vec3(sideOffset, heightOffset, forwardDist));
                  entity.setPos(spawnPos);
                  level.addFreshEntity(entity);
                  level.getServer().execute(() -> {
                     SecludedPatch patch = entity.getPatch();
                     if (patch != null) {
                        patch.setZRotation(zRotation);
                     }
                  });
               }
            }
         }
      }, Side.BOTH);
   }

   public static InTimeEvent summonSecludedAtTarget(
      int startFrame, float scale, float zRotation, float attractRadius, float minKnockbackResistance, float constantPullStrength, double heightAdjustment
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         LivingEntity caster = (LivingEntity)entityPatch.getOriginal();
         if (caster.level() instanceof ServerLevel level) {
            if (isSecludedAttackEnabled(caster)) {
               Entity target = entityPatch.getTarget();
               if (target != null) {
                  Vec3 spawnPos = target.position().add(0.0, heightAdjustment, 0.0);
                  SecludedEntity entity = new SecludedEntity(caster, scale, Vec3.ZERO);
                  entity.setPos(spawnPos);
                  level.addFreshEntity(entity);
                  if (attractRadius > 0.0F) {
                     attractTargets(level, spawnPos, attractRadius, minKnockbackResistance, constantPullStrength);
                  }

                  level.getServer().execute(() -> {
                     SecludedPatch patch = entity.getPatch();
                     if (patch != null) {
                        patch.setZRotation(zRotation);
                     }
                  });
               }
            }
         }
      }, Side.BOTH);
   }

   private static void attractTargets(ServerLevel level, Vec3 centerPos, float radius, float minKnockbackResistance, float strength) {
      BlockPos centerBlockPos = new BlockPos((int)centerPos.x, (int)centerPos.y, (int)centerPos.z);
      AABB area = new AABB(centerBlockPos).inflate(radius);
      level.getEntities(
            (Entity)null,
            area,
            e -> e instanceof LivingEntity living && !(e instanceof Player) && !e.isSpectator() && living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) < minKnockbackResistance
         )
         .forEach(target -> {
            Vec3 toCenter = centerPos.subtract(target.position()).normalize();
            target.setDeltaMovement(target.getDeltaMovement().add(toCenter.scale(strength)));
            target.hurtMarked = true;
         });
   }

   public static InPeriodEvent setDodgeCounterNodeEvent(int startFrame, int endFrame) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      return InPeriodEvent.create(start, end, (livingEntityPatch, staticAnimation, objects) -> {
         LivingEntity player = (LivingEntity)livingEntityPatch.getOriginal();
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
          if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
             if (playerPatch != null && playerPatch.getSkill(Aetherialdusk.Aetherialdusk) != null) {
                InvincibleAttachments.getPlayer(serverPlayer).setCurrentNode(Aetherialdusk.DodgeCounter);
             }
          }
      }, Side.SERVER);
   }

   public static InPeriodEvent setYamatoDodgeNode(int startFrame, int endFrame) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      return InPeriodEvent.create(start, end, (livingEntityPatch, staticAnimation, objects) -> {
         LivingEntity player = (LivingEntity)livingEntityPatch.getOriginal();
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
          if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
             if (playerPatch != null && playerPatch.getSkill(Yamato.yamato) != null) {
                InvincibleAttachments.getPlayer(serverPlayer).setCurrentNode(Yamato.Yamato_root);
             }
          }
      }, Side.SERVER);
   }

   public static InPeriodEvent catchEntities(int startFrame, int endFrame, float radius, double forwardDist, float minKnockbackResistance) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      return InPeriodEvent.create(
         start,
         end,
         (livingEntityPatch, staticAnimation, objects) -> {
            LivingEntity caster = (LivingEntity)livingEntityPatch.getOriginal();
            if (caster.level() instanceof ServerLevel level) {
               if (isSecludedAttackEnabled(caster)) {
                  Vec3 targetPos = calculateEffectPosition(caster, forwardDist, 0.0, 0.0);
                  double groundY = findGroundHeight(level, caster, forwardDist);
                  Vec3 finalPos = new Vec3(targetPos.x, groundY, targetPos.z);
                  Vec3 lookVec = caster.getLookAngle();
                  level.getEntities(
                        caster,
                        caster.getBoundingBox().inflate(radius),
                        entity -> {
                           if (!(
                              entity instanceof LivingEntity living
                                 && !(entity instanceof SecludedEntity)
                                 && !(entity instanceof DoppelgangerEntity)
                                 && !isTargetDummy(entity)
                                 && !(entity instanceof Player)
                                 && !entity.isSpectator()
                                 && !living.isInvulnerable()
                                 && living.canBeSeenAsEnemy()
                           )) {
                              return false;
                           } else {
                              if (living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) > minKnockbackResistance) {
                                 return false;
                              }

                              Vec3 toEntity = entity.position().subtract(caster.position());
                              double distance = toEntity.length();
                              if (distance > radius) {
                                 return false;
                              }

                              toEntity = toEntity.normalize();
                              return lookVec.dot(toEntity) > 0.94;
                           }
                        }
                     )
                     .forEach(entity -> {
                        Vec3 toTarget = finalPos.subtract(entity.position());
                        if (!(toTarget.lengthSqr() > radius * radius)) {
                           Vec3 safePos = ensureInsideWorld(level, finalPos, entity.getBoundingBox());
                           level.getServer().execute(() -> {
                              double verifiedY = findGroundHeight(level, entity, 0.0);
                              entity.moveTo(finalPos.x, verifiedY, finalPos.z);
                              entity.setDeltaMovement(Vec3.ZERO);
                              entity.hurtMarked = true;
                              entity.setOnGround(true);
                           });
                        }
                     });
               }
            }
         },
         Side.BOTH
      );
   }

   private static Vec3 ensureInsideWorld(ServerLevel level, Vec3 targetPos, AABB entityBox) {
      double minX = entityBox.getXsize() / 2.0;
      double minZ = entityBox.getZsize() / 2.0;
      return new Vec3(
         Mth.clamp(targetPos.x, minX, level.getWorldBorder().getMaxX() - minX),
         targetPos.y,
         Mth.clamp(targetPos.z, minZ, level.getWorldBorder().getMaxZ() - minZ)
      );
   }

   private static double findGroundHeight(Level level, Entity entity, double forward) {
      Vec3 startPos = entity.position().add(entity.getLookAngle().scale(forward)).add(0.0, 1.0, 0.0);
      BlockHitResult result = level.clip(new ClipContext(startPos, startPos.add(0.0, -64.0, 0.0), Block.COLLIDER, Fluid.NONE, entity));
      return result.getLocation().y + 0.1;
   }

   private static Vec3 calculateEffectPosition(LivingEntity owner, double forward, double height, double side) {
      Vec3 horizontalLook = new Vec3(Math.sin(-owner.getYRot() * (Math.PI / 180.0)), 0.0, Math.cos(owner.getYRot() * (Math.PI / 180.0))).normalize();
      Vec3 right = new Vec3(-horizontalLook.z, 0.0, horizontalLook.x).normalize();
      return owner.getEyePosition().add(horizontalLook.scale(forward)).add(right.scale(side)).add(0.0, height, 0.0);
   }

   public static InTimeEvent summonFireWind(
      int startFrame, double xOffset, double yOffset, double zOffset, float scale, float initRotX, float initRotY, float initRotZ
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity livingEntity) {
            if (livingEntity.level() instanceof ServerLevel serverLevel) {
               FireWindEntity fireWind = new FireWindEntity(livingEntity, scale, new Vec3(xOffset, yOffset, zOffset));
               FireWindPatch patch = fireWind.getEntityPatch();
               if (patch != null) {
                  patch.setRotation(initRotX, initRotY, initRotZ);
               }

               fireWind.setPos(livingEntity.position().add(xOffset, yOffset, zOffset));
               serverLevel.addFreshEntity(fireWind);
               activeFireWinds.put(livingEntity, fireWind);
            }
         }
      }, Side.BOTH);
   }

   public static InTimeEvent clearFireWind(int startFrame) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
         clearExistingFireWind(entity);
      }, Side.SERVER);
   }

   private static void clearExistingFireWind(LivingEntity owner) {
      FireWindEntity existing = activeFireWinds.get(owner);
      if (existing != null && !existing.isRemoved()) {
         existing.markForRemoval();
         activeFireWinds.remove(owner);
      }
   }

   private static TrailEntity findExistingTrail(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         Iterator var4 = level.getEntities(owner, owner.getBoundingBox().inflate(10.0), e -> e instanceof TrailEntity trail && trail.getOwner() == owner).iterator();
         if (var4.hasNext()) {
            Entity entity = (Entity)var4.next();
            return (TrailEntity)entity;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static InTimeEvent clearTrail(int startFrame) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               for (Entity entity : level.getEntities(owner, owner.getBoundingBox().inflate(50.0), e -> e instanceof TrailEntity trail && trail.getOwner() == owner)) {
                  entity.discard();
               }
            }
         }
      }, Side.SERVER);
   }

   @Nullable
   public static FireWindPatch getFireWindPatch(LivingEntity owner) {
      FireWindEntity entity = activeFireWinds.get(owner);
      return entity != null ? entity.getEntityPatch() : null;
   }

   private static boolean isTargetDummy(Entity entity) {
      return HAS_DUMMY_MOD && entity != null && "net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity".equals(entity.getClass().getName());
   }
}
