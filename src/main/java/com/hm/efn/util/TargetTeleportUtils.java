package com.hm.efn.util;

import net.minecraft.core.registries.BuiltInRegistries;

import com.hm.efn.EFN;
import com.hm.efn.EFNCommonConfig;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import yesman.epicfight.api.animation.property.AnimationEvent.Event;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class TargetTeleportUtils {
   private static final boolean HAS_DUMMY_MOD = ModList.get().isLoaded("dummmmmmy");
   public static final Event YAMATO_TRICKER = (entitypatch, self, params) -> {
      if (!entitypatch.isLogicalClient() && entitypatch.getOriginal() instanceof ServerPlayer player) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         if (playerPatch != null) {
            ExecuteYamatoTricker(playerPatch);
         }
      }
   };
   public static final Event YAMATO_CATCHER = (entitypatch, self, params) -> {
      if (!entitypatch.isLogicalClient() && entitypatch.getOriginal() instanceof ServerPlayer player) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         if (playerPatch != null) {
            ExecuteYamatoCatcher(playerPatch);
         }
      }
   };

   public static void ExecuteYamatoTricker(LivingEntityPatch<?> entitypatch) {
      ExecuteYamatoTricker(entitypatch, null);
   }

   public static void ExecuteYamatoTricker(LivingEntityPatch<?> entitypatch, LivingEntity hitTarget) {
      if (entitypatch != null && !entitypatch.isLogicalClient() && entitypatch.getOriginal() instanceof ServerPlayer player) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         if (playerPatch == null) {
            return;
         }

         List<LivingEntity> recentEntities = collectTeleportTargets(playerPatch, player, hitTarget);
         playerPatch.getCurrentlyActuallyHitEntities().clear();
         player.setLastHurtMob(null);
         if (recentEntities.isEmpty()) {
            return;
         }

         LivingEntity target = getPriorityTarget(playerPatch, recentEntities);
         if (target == null || target.isRemoved() || target.level() != player.level()) {
            return;
         }

         double baseOffset = 1.0;
         float targetYaw = target.getYRot();
         double radianYaw = Math.toRadians(targetYaw);
         double targetX = target.getX() - Math.sin(radianYaw) * baseOffset;
         double targetZ = target.getZ() + Math.cos(radianYaw) * baseOffset;
         double targetY = target.getY() + 0.7;
         if (attemptTeleport(player, target, targetX, targetY, targetZ)) {
            spawnTeleportEffects(player);
            player.addEffect(new MobEffectInstance(EFNMobEffectRegistry.VERTICALSTOP, 10, 1, false, false, false));
            player.getCooldowns().addCooldown(Items.ENDER_PEARL, 5);
         }
      }
   }

   public static void ExecuteYamatoCatcher(LivingEntityPatch<?> entitypatch) {
      ExecuteYamatoCatcher(entitypatch, null);
   }

   public static void ExecuteYamatoCatcher(LivingEntityPatch<?> entitypatch, LivingEntity hitTarget) {
      if (entitypatch != null && !entitypatch.isLogicalClient() && entitypatch.getOriginal() instanceof ServerPlayer player) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         if (playerPatch == null) {
            return;
         }

         List<LivingEntity> recentEntities = collectTeleportTargets(playerPatch, player, hitTarget);

         double catchRange = 30.0;
         float catchAngle = 180.0F;
         double knockbackResistanceThreshold = 0.45;
         List<String> blacklist = new ArrayList<>((Collection<? extends String>)EFNCommonConfig.YAMATO_CATCHER_BLACKLIST.get());
         List<String> whitelist = new ArrayList<>((Collection<? extends String>)EFNCommonConfig.YAMATO_CATCHER_WHITELIST.get());
         List<LivingEntity> validTargets = new ArrayList<>();
         Vec3 playerLook = player.getLookAngle().normalize();
         Vec3 playerPos = player.position();
         LivingEntity epicFightTarget = playerPatch.getTarget();
         if (epicFightTarget != null && isValidCatcherTarget(epicFightTarget, player, 30.0, 180.0F, playerPos, playerLook, 0.45, blacklist, whitelist)) {
            validTargets.add(epicFightTarget);
         }

         for (LivingEntity entity : recentEntities) {
            if (entity != epicFightTarget && isValidCatcherTarget(entity, player, 30.0, 180.0F, playerPos, playerLook, 0.45, blacklist, whitelist)) {
               validTargets.add(entity);
            }
         }

         validTargets.sort((e1, e2) -> {
            if (e1 == epicFightTarget) {
               return -1;
            } else {
               return e2 == epicFightTarget ? 1 : Double.compare(e1.distanceTo(player), e2.distanceTo(player));
            }
         });
         if (!validTargets.isEmpty()) {
            LivingEntity target = validTargets.get(0);
            Vec3 catchPos = playerPos.add(playerLook.scale(1.5)).add(0.0, 1.3, 0.0);
            if (attemptCatchEntity(player, target, catchPos)) {
               target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false, false));
               target.addEffect(new MobEffectInstance(EFNMobEffectRegistry.VERTICALSTOP, 10, 0, false, false, false));
               target.addEffect(new MobEffectInstance(EFNMobEffectRegistry.HORIZONTAL_STOP, 10, 0, false, false, false));
               spawnCatchEffects(player, target);
               player.getCooldowns().addCooldown(Items.ENDER_PEARL, 5);
            }
         }
      }
   }

   private static List<LivingEntity> collectTeleportTargets(ServerPlayerPatch playerPatch, ServerPlayer player, LivingEntity directTarget) {
      List<LivingEntity> targets = new ArrayList<>();
      addTargetCandidate(targets, directTarget);
      playerPatch.getCurrentlyActuallyHitEntities().forEach(entity -> addTargetCandidate(targets, entity));
      addTargetCandidate(targets, player.getLastHurtMob());
      addTargetCandidate(targets, playerPatch.getTarget());
      return targets;
   }

   private static void addTargetCandidate(List<LivingEntity> targets, LivingEntity entity) {
      if (entity != null && !targets.contains(entity)) {
         targets.add(entity);
      }
   }

   private static boolean isValidCatcherTarget(
      LivingEntity entity,
      ServerPlayer player,
      double catchRange,
      float catchAngle,
      Vec3 playerPos,
      Vec3 playerLook,
      double knockbackResistanceThreshold,
      List<String> blacklist,
      List<String> whitelist
   ) {
      if (entity == null || entity.isRemoved() || entity.level() != player.level()) {
         return false;
      }

      if (isTargetDummy(entity)) {
         return false;
      }

      if (entity instanceof DoppelgangerEntity || entity instanceof VFXEntity) {
         return false;
      }

      if (entity.distanceTo(player) > catchRange) {
         return false;
      }

      if (!isInFront(playerPos, playerLook, entity.position(), catchAngle)) {
         return false;
      }

      ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
      if (entityId != null) {
         String entityIdString = entityId.toString();
         if (blacklist.contains(entityIdString)) {
            return false;
         }

         if (whitelist.contains(entityIdString)) {
            return true;
         }
      }

      double knockbackResistance = entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
      return knockbackResistance < knockbackResistanceThreshold;
   }

   private static LivingEntity getPriorityTarget(ServerPlayerPatch playerPatch, List<LivingEntity> recentEntities) {
      try {
         List<Entity> entityList = new ArrayList<>(recentEntities);
         LivingEntity epicFightTarget = playerPatch.getTarget();
         if (epicFightTarget != null && !entityList.contains(epicFightTarget)) {
            entityList.add(epicFightTarget);
         }

         entityList.removeIf(TargetTeleportUtils::isTargetDummy);
         entityList.removeIf(entityx -> entityx instanceof DoppelgangerEntity);
         entityList.removeIf(entityx -> entityx instanceof VFXEntity);
         HitEntityList hostilityList = new HitEntityList(playerPatch, entityList, Priority.HOSTILITY);
         double hostileRange = 25.0;
         double neutralRange = 5.0;
         List<LivingEntity> ultimatePriority = new ArrayList<>();
         List<LivingEntity> firstPriority = new ArrayList<>();
         List<LivingEntity> secondPriority = new ArrayList<>();
         List<LivingEntity> others = new ArrayList<>();

         while (hostilityList.next()) {
            Entity entity = hostilityList.getEntity();
            if (entity instanceof LivingEntity livingEntity) {
               double distance = entity.distanceTo(playerPatch.getOriginal());
               boolean isHostile = isHostileTarget(playerPatch, entity);
               double maxRange = isHostile ? 25.0 : 5.0;
               if (!(distance > maxRange)) {
                  if (epicFightTarget != null && entity == epicFightTarget) {
                     boolean isRecentHit = recentEntities.contains(entity);
                     boolean isMobTargeting = entity instanceof Mob mob && mob.getTarget() == playerPatch.getOriginal();
                     if (isRecentHit && isMobTargeting) {
                        ultimatePriority.add(livingEntity);
                        continue;
                     }
                  }

                  if (((ServerPlayer)playerPatch.getOriginal()).getLastHurtByMob() == entity) {
                     firstPriority.add(livingEntity);
                  } else if (entity instanceof Mob mob && mob.getTarget() == playerPatch.getOriginal()) {
                     firstPriority.add(livingEntity);
                  } else if (recentEntities.contains(entity)) {
                     firstPriority.add(livingEntity);
                  } else if (entity == epicFightTarget) {
                     secondPriority.add(livingEntity);
                  } else {
                     others.add(livingEntity);
                  }
               }
            }
         }

         if (!ultimatePriority.isEmpty()) {
            return ultimatePriority.get(0);
         } else if (!firstPriority.isEmpty()) {
            return firstPriority.get(0);
         } else if (!secondPriority.isEmpty()) {
            return secondPriority.get(0);
         } else {
            return others.isEmpty() ? null : others.get(0);
         }
      } catch (ClassCastException e) {
         EFN.LOGGER.warn("ClassCastException in target selection (likely llama targeting issue), using fallback: {}", e.getMessage());
         return getFallbackTarget(playerPatch, recentEntities);
      } catch (Exception e) {
         EFN.LOGGER.error("Unexpected error in getPriorityTarget", e);
         return getFallbackTarget(playerPatch, recentEntities);
      }
   }

   private static LivingEntity getFallbackTarget(ServerPlayerPatch playerPatch, List<LivingEntity> recentEntities) {
      try {
         recentEntities.removeIf(
            entity -> entity == null
               || entity.isRemoved()
               || isTargetDummy(entity)
               || entity instanceof DoppelgangerEntity
               || entity instanceof VFXEntity
         );
         if (recentEntities.isEmpty()) {
            return null;
         }

         recentEntities.sort((e1, e2) -> {
            double dist1 = e1.distanceToSqr(playerPatch.getOriginal());
            double dist2 = e2.distanceToSqr(playerPatch.getOriginal());
            return Double.compare(dist1, dist2);
         });
         return recentEntities.get(0);
      } catch (Exception e) {
         EFN.LOGGER.error("Error in fallback target selection", e);
         return null;
      }
   }

   private static boolean isHostileTarget(LivingEntityPatch<?> playerPatch, Entity entity) {
      return ((LivingEntity)playerPatch.getOriginal()).getLastHurtByMob() == entity
         || entity instanceof Mob mob && mob.getTarget() == playerPatch.getOriginal()
         || entity == playerPatch.getTarget();
   }

   private static boolean attemptTeleport(ServerPlayer player, LivingEntity target, double x, double y, double z) {
      MutableBlockPos checkPos = new MutableBlockPos(x, y, z);

      for (int i = 0; i < 5; i++) {
         BlockState block = target.level().getBlockState(checkPos);
         boolean canPass = block.isAir() || block.getCollisionShape(target.level(), checkPos).isEmpty();
         if (canPass) {
            player.teleportTo((ServerLevel)player.level(), x, y, z, player.getYRot(), player.getXRot());
            return true;
         }

         checkPos.move(Direction.UP);
         y++;
      }

      return false;
   }

   private static void spawnTeleportEffects(ServerPlayer player) {
      ((ServerLevel)player.level()).sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 0.5, player.getZ(), 15, 0.3, 0.3, 0.3, 0.1);
      player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.7F, 1.2F);
   }

   private static boolean isInFront(Vec3 viewerPos, Vec3 lookVec, Vec3 targetPos, float maxAngleDeg) {
      Vec3 toTarget = targetPos.subtract(viewerPos).normalize();
      double dot = Math.max(-1.0, Math.min(1.0, lookVec.dot(toTarget)));
      double angle = Math.toDegrees(Math.acos(dot));
      return angle <= maxAngleDeg;
   }

   private static boolean attemptCatchEntity(ServerPlayer player, LivingEntity target, Vec3 catchPos) {
      if (!target.isRemoved() && target.level() == player.level()) {
         ServerLevel serverLevel = (ServerLevel)player.level();
         boolean teleportSuccess;
         if (target instanceof ServerPlayer) {
            target.teleportTo(serverLevel, catchPos.x, catchPos.y, catchPos.z, Set.of(), target.getYRot(), target.getXRot());
            teleportSuccess = true;
         } else {
            target.teleportTo(catchPos.x, catchPos.y, catchPos.z);
            teleportSuccess = true;
         }

         target.setDeltaMovement(Vec3.ZERO);
         target.hurtMarked = true;
         target.setOnGround(true);
         return teleportSuccess;
      } else {
         return false;
      }
   }

   private static void spawnCatchEffects(ServerPlayer player, LivingEntity target) {
      ServerLevel level = (ServerLevel)player.level();
      level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
      level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.7F, 1.5F);
      level.sendParticles(ParticleTypes.END_ROD, target.getX(), target.getY() + 0.5, target.getZ(), 15, 0.3, 0.3, 0.3, 0.05);
   }

   private static boolean isTargetDummy(Entity entity) {
      return HAS_DUMMY_MOD && entity != null && "net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity".equals(entity.getClass().getName());
   }
}
