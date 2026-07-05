package com.hm.efn.entity;

import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.ScytheSkill;
import com.merlin204.avalon.avalon.vfx.type.AnimationTextureAvalonVFXManager;
import com.merlin204.avalon.avalon.vfx.type.StaticAvalonVFXManager;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNVFXManagers {
   public static final AnimationTextureAvalonVFXManager BLOOD_SLASH = new AnimationTextureAvalonVFXManager(
      "efn:entity/effect/blood_slash",
      "efn:entity/effect/blood_slash",
      "efn:textures/entity/blood_slash",
      "efn:textures/entity/blood_slash",
      1,
      5,
      1.0F,
      EFNAnimations.BLOOD_SLASH
   );
   public static final AnimationTextureAvalonVFXManager CRIMSON_SLASH = new AnimationTextureAvalonVFXManager(
      "efn:entity/effect/crimson_slash",
      "efn:entity/effect/crimson_slash",
      "efn:textures/entity/crimson_slash",
      "efn:textures/entity/crimson_slash",
      1,
      7,
      1.0F,
      EFNAnimations.CRIMSON_SLASH
   );
   public static final AnimationTextureAvalonVFXManager CRIMSON_SLASH_ANTI = new AnimationTextureAvalonVFXManager(
      "efn:entity/effect/crimson_slash",
      "efn:entity/effect/crimson_slash",
      "efn:textures/entity/crimson_slash_anti",
      "efn:textures/entity/crimson_slash_anti",
      1,
      7,
      1.0F,
      EFNAnimations.CRIMSON_SLASH
   );
   public static final StaticAvalonVFXManager DRAGON_FLASH_SLASH = new StaticAvalonVFXManager(
      "efn:entity/effect/dragon_flash_slash",
      "efn:entity/effect/dragon_flash_slash",
      "efn:textures/entity/trail.png",
      "efn:textures/entity/trail.png",
      EFNAnimations.DRAGON_FLASH_SLASH
   );

   public static InTimeEvent summonVFX(
      StaticAvalonVFXManager vfxManager, int startFrame, double forwardDist, double heightOffset, double sideOffset, float scale, Vec3f rotOffset
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var16 = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
               vfxManager.spawnVFXEntity(owner, var16, rotOffset, scale);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtPlayer_Murasama(StaticAvalonVFXManager vfxManager, int startFrame, double forward, double up, double right, float scale) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var15 = calculateEffectPosition(owner, forward, up, right);
               vfxManager.spawnVFXEntity(owner, var15, new Vec3f(0.0F, 90.0F, 0.0F), scale);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtPlayer_Fushigiri(
      StaticAvalonVFXManager vfxManager, int startFrame, double forward, double up, double right, float scale
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var15 = calculateEffectPosition(owner, forward, up, right);
               vfxManager.spawnVFXEntity(owner, var15, new Vec3f(0.0F, 0.0F, 0.0F), scale);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtJoint(StaticAvalonVFXManager vfxManager, int startFrame, Joint joint, Vec3f offset, float scale) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               if (joint != null) {
                  Vec3 spawnPos = AvalonAnimationUtils.getJointWorldPos(entityPatch, joint, offset, start);
                  vfxManager.spawnVFXEntity(owner, spawnPos, Vec3f.ZERO, scale);
               }
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtJoint(
      AnimationTextureAvalonVFXManager vfxManager, int startFrame, Joint joint, Vec3f offset, float scale, Vec3f rotOffset
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               if (joint != null) {
                  Vec3 spawnPos = AvalonAnimationUtils.getJointWorldPos(entityPatch, joint, offset, start);
                  vfxManager.spawnVFXEntity(owner, spawnPos, rotOffset, scale);
               }
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtJoint(StaticAvalonVFXManager vfxManager, int startFrame, Joint joint, float scale) {
      return summonVFXAtJoint(vfxManager, startFrame, joint, Vec3f.ZERO, scale);
   }

   public static InTimeEvent summonVFXAtJoint(AnimationTextureAvalonVFXManager vfxManager, int startFrame, Joint joint, float scale, Vec3f rotOffset) {
      return summonVFXAtJoint(vfxManager, startFrame, joint, Vec3f.ZERO, scale, rotOffset);
   }

   public static InTimeEvent summonConditionalAnimationTextureVFX(
      StaticAvalonVFXManager vfxManager,
      int startFrame,
      double forwardDist,
      double heightOffset,
      double sideOffset,
      float scale,
      Vec3f rotOffset,
      Holder<MobEffect> potionEffect
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               if (owner.hasEffect(potionEffect)) {
                  Vec3 spawnPos = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
                  vfxManager.spawnVFXEntity(owner, spawnPos, rotOffset, scale);
               }
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonScytheComboVFX(
      AnimationTextureAvalonVFXManager vfxManager, int startFrame, double forwardDist, double heightOffset, double sideOffset, float scale, Vec3f rotOffset
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(
         start,
         (entityPatch, self, params) -> {
            if (entityPatch.getOriginal() instanceof LivingEntity owner) {
               if (owner.level() instanceof ServerLevel level) {
                  boolean var32 = false;
                  MobEffectInstance damageIncreaseEffect = owner.getEffect(EFNMobEffectRegistry.ATTACK_DAMAGE_INCREASE);
                  boolean hasSweepingEdgeEnchantment = EFNEnchantment.getLevel(((LivingEntity)entityPatch.getOriginal()).getMainHandItem(), Enchantments.SWEEPING_EDGE) >= 3;
                  if (damageIncreaseEffect != null && hasSweepingEdgeEnchantment) {
                     var32 = true;
                  }

                  if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                     boolean hasScytheEnchantment = EFNEnchantment.getLevel(((ServerPlayer)serverPlayerPatch.getOriginal()).getMainHandItem(), EFNEnchantment.SCYTHE_ENHANCE) > 0;
                     SkillContainer scytheSkillContainer = serverPlayerPatch.getSkill(EFNSkills.SCYTHE);
                     if (scytheSkillContainer != null) {
                        SkillDataManager scytheSkillDataManager = scytheSkillContainer.getDataManager();
                        if (scytheSkillDataManager != null) {
                           float bloodPower = (Float)scytheSkillDataManager.getDataValue(EFNSKillDataKeys.SCYTHE_BLOOD_POWER);
                           if (bloodPower >= ScytheSkill.getMaxBloodPower() && hasScytheEnchantment) {
                              var32 = true;
                           }
                        }
                     }
                  }

                  if (var32) {
                     Vec3 baseSpawnPos = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
                     Vec3 ownerPos = owner.position();
                     double adjustedForwardDist = forwardDist;
                     boolean hasObstruction = false;
                     BlockHitResult blockHit = level.clip(new ClipContext(ownerPos, baseSpawnPos, Block.COLLIDER, Fluid.NONE, owner));
                     if (blockHit.getType() != Type.MISS) {
                        double hitDistance = blockHit.getLocation().distanceTo(ownerPos);
                        if (hitDistance < forwardDist) {
                           adjustedForwardDist = Math.max(hitDistance - 2.5, -1.0);
                           hasObstruction = true;
                        }
                     }

                     if (!hasObstruction) {
                        AABB pathBox = new AABB(ownerPos, baseSpawnPos).inflate(1.5);
                        List<Entity> entitiesInPath = level.getEntities(
                           owner,
                           pathBox,
                           entityx -> entityx instanceof LivingEntity
                              && entityx != owner
                              && entityx.isPickable()
                              && ((LivingEntity)entityx).canBeSeenAsEnemy()
                              && !(entityx instanceof VFXEntity)
                        );
                        if (!entitiesInPath.isEmpty()) {
                           double nearestDistance = Double.MAX_VALUE;

                           for (Entity entity : entitiesInPath) {
                              double distance = entity.distanceTo(owner);
                              if (distance < nearestDistance) {
                                 nearestDistance = distance;
                              }
                           }

                           if (nearestDistance < forwardDist) {
                              adjustedForwardDist = Math.max(nearestDistance - 2.5, -0.65);
                           }
                        }
                     }

                     Vec3 finalSpawnPos = calculateEffectPosition(owner, adjustedForwardDist, heightOffset, sideOffset);
                     vfxManager.spawnVFXEntity(owner, finalSpawnPos, rotOffset, scale);
                  }
               }
            }
         },
         Side.SERVER
      );
   }

   public static InTimeEvent summonConditionalAnimationTextureVFX(
      AnimationTextureAvalonVFXManager vfxManager,
      int startFrame,
      double forwardDist,
      double heightOffset,
      double sideOffset,
      float scale,
      Vec3f rotOffset,
      Holder<MobEffect> potionEffect
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               if (owner.hasEffect(potionEffect)) {
                  Vec3 spawnPos = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
                  vfxManager.spawnVFXEntity(owner, spawnPos, rotOffset, scale);
               }
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtPosition(
      StaticAvalonVFXManager vfxManager, int startFrame, double worldX, double worldY, double worldZ, float scale, Vec3f rotOffset
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var16 = new Vec3(worldX, worldY, worldZ);
               vfxManager.spawnVFXEntity(owner, var16, rotOffset, scale);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtTarget(StaticAvalonVFXManager vfxManager, int startFrame, float scale, Vec3f rotOffset, double heightAdjust) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Entity target = entityPatch.getTarget();
               if (target != null) {
                  Vec3 spawnPos = target.position().add(0.0, heightAdjust, 0.0);
                  vfxManager.spawnVFXEntity(owner, spawnPos, rotOffset, scale);
               }
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtEntity(
      StaticAvalonVFXManager vfxManager, int startFrame, double forwardDist, double heightOffset, double sideOffset, float scale
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var15 = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
               vfxManager.spawnVFXEntity(owner, var15, Vec3f.ZERO, scale);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXAtEntity(
      AnimationTextureAvalonVFXManager vfxManager, int startFrame, double forwardDist, double heightOffset, double sideOffset, float scale
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var15 = calculateEffectPosition(owner, forwardDist, heightOffset, sideOffset);
               vfxManager.spawnVFXEntity(owner, var15, Vec3f.ZERO, scale);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXSmart(StaticAvalonVFXManager vfxManager, int startFrame, float scale, Vec3f rotOffset, double targetHeightAdjust) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Entity target = entityPatch.getTarget();
               Vec3 spawnPos;
               if (target != null && isTargetInRange(owner, target, 5.5)) {
                  spawnPos = target.position().add(0.0, targetHeightAdjust, 0.0);
               } else {
                  spawnPos = calculateEffectPosition(owner, 4.0, -1.3F, 0.0);
               }

               vfxManager.spawnVFXEntity(owner, spawnPos, rotOffset, scale);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent summonVFXSmartAdvanced(
      StaticAvalonVFXManager vfxManager,
      int startFrame,
      float scale,
      Vec3f rotOffset,
      double targetHeightAdjust,
      double maxTargetRange,
      double defaultForwardDist,
      double defaultHeightOffset,
      double defaultSideOffset
   ) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Entity target = entityPatch.getTarget();
               Vec3 spawnPos;
               if (target != null && isTargetInRange(owner, target, maxTargetRange)) {
                  spawnPos = target.position().add(0.0, targetHeightAdjust, 0.0);
               } else {
                  spawnPos = calculateEffectPosition(owner, defaultForwardDist, defaultHeightOffset, defaultSideOffset);
               }

               vfxManager.spawnVFXEntity(owner, spawnPos, rotOffset, scale);
            }
         }
      }, Side.SERVER);
   }

   public static boolean isTargetInRange(LivingEntity owner, Entity target, double range) {
      double distanceSq = owner.distanceToSqr(target);
      return distanceSq <= range * range;
   }

   public static Vec3 calculateEffectPosition(LivingEntity owner, double forward, double height, double side) {
      Vec3 horizontalLook = new Vec3(Math.sin(-owner.getYRot() * (Math.PI / 180.0)), 0.0, Math.cos(owner.getYRot() * (Math.PI / 180.0))).normalize();
      Vec3 right = new Vec3(-horizontalLook.z, 0.0, horizontalLook.x).normalize();
      return owner.getEyePosition().add(horizontalLook.scale(forward)).add(right.scale(side)).add(0.0, height, 0.0);
   }
}
