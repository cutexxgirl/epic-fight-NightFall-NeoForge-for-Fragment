package com.hm.efn.client;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.mixin.CameraAccessor;
import com.hm.efn.util.CameraLockUtil;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFov;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(modid = "efn", value = Dist.CLIENT)
public class CameraAnim {
   public static final Vec3f DEFAULT_AIMING_CORRECTION = new Vec3f(1.5F, 0.0F, 1.25F);
   private static final Minecraft MC = Minecraft.getInstance();
   private static final int ZOOM_MAX_COUNT = 60;
   private static Vec3f aimingCorrection = DEFAULT_AIMING_CORRECTION;
   private static boolean aiming;
   private static int zoomOutTimer = 0;
   private static int zoomCount;
   private static long transitionStartTime = -1L;
   private static float startElevation = 0.0F;
   private static float startForwardOffset = 0.0F;
   private static float startSideOffset = 0.0F;
   private static float currentElevation = 0.0F;
   private static float currentForwardOffset = 0.0F;
   private static float currentSideOffset = 0.0F;
   private static boolean isTransitioning = false;
   private static boolean isZansetsuCameraActive = false;
   private static float originalXRot = 0.0F;
   private static long viewLockTransitionStartTime = -1L;
   private static float viewLockStartXRot = 0.0F;
   private static float viewLockTargetXRot = 0.0F;
   private static boolean isViewLockTransitioning = false;
   private static float originalFov = -1.0F;
   private static long fovTransitionStartTime = -1L;
   private static float fovTransitionStartValue = 0.0F;
   private static float fovTransitionTargetValue = 0.0F;
   private static boolean isFovTransitioning = false;
   private static float currentFov = 0.0F;
   private static LivingEntity targetEntity = null;
   private static float targetLockYRot = 0.0F;
   private static float targetLockYRotO = 0.0F;
   private static boolean isTargetLockEnabled = false;

   public static boolean isZooming() {
      return zoomOutTimer > 0;
   }

   public static boolean isZansetsuCameraActive() {
      return isZansetsuCameraActive;
   }

   public static void zoomIn(Vec3f aimingCorrection, int timer) {
      aiming = true;
      zoomCount = zoomCount == 0 ? 1 : zoomCount;
      zoomOutTimer = timer;
      CameraAnim.aimingCorrection = aimingCorrection;
   }

   public static void zoomIn(Vec3f aimingCorrection) {
      aiming = true;
      zoomCount = zoomCount == 0 ? 1 : zoomCount;
      zoomOutTimer = 0;
      CameraAnim.aimingCorrection = aimingCorrection;
   }

   public static void zoomOut() {
      aiming = false;
      zoomOutTimer = 0;
   }

   public static void zoomOut(int timer) {
      zoomOutTimer = timer;
   }

   private static boolean isPlayerInAir() {
      if (MC.player == null) {
         return false;
      }

      PlayerPatch<?> entityPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(MC.player, PlayerPatch.class);
      if (entityPatch == null) {
         return false;
      }

      SkillContainer skill = entityPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (skill == null) {
         return false;
      }

      SkillDataManager dataManager = skill.getDataManager();
      return !dataManager.hasData(EFNSKillDataKeys.MURASAMA_AIR_BORNE)
         ? false
         : (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_AIR_BORNE);
   }

   @SubscribeEvent
   public static void onCameraTypeChange(ComputeCameraAngles event) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.options.getCameraType() == CameraType.FIRST_PERSON && (isZansetsuCameraActive || isViewLockTransitioning || isFovTransitioning)) {
         resetAllCameraEffects();
      }
   }

   @SubscribeEvent
   public static void cameraSetupEvent(ComputeCameraAngles event) {
      Minecraft mc = Minecraft.getInstance();
      Player player = mc.player;
      float partialTicks = (float)event.getPartialTick();
      boolean wasInFirstPerson = false;
      if (mc.options.getCameraType() == CameraType.FIRST_PERSON && (isZansetsuCameraActive || isViewLockTransitioning || isFovTransitioning || isTargetLockEnabled)
         )
       {
         resetAllCameraEffects();
         wasInFirstPerson = true;
      }

      if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
         float targetElevation = 0.0F;
         float targetForwardOffset = 0.0F;
         float targetSideOffset = 0.0F;
         boolean wasZansetsuCameraActive = isZansetsuCameraActive;
         boolean shouldZansetsuBeActive = false;
         if (player != null) {
            PlayerPatch<?> entityPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
            if (entityPatch != null) {
               SkillContainer skill = entityPatch.getSkill(SkillSlots.WEAPON_INNATE);
               if (skill != null) {
                  SkillDataManager dataManager = skill.getDataManager();
                  if (dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
                     && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
                     shouldZansetsuBeActive = true;
                  }
               }
            }

            if (shouldZansetsuBeActive && !wasZansetsuCameraActive) {
               CameraLockUtil.beginSequenceAndUnlock();
               isZansetsuCameraActive = true;
            } else if (!shouldZansetsuBeActive && wasZansetsuCameraActive) {
               CameraLockUtil.clearSequence();
               isZansetsuCameraActive = false;
               resetTargetLock();
            }

            if (isZansetsuCameraActive && (Boolean)EFNClientConfig.ZANSETSU_CAMERA_ENABLED.get()) {
               targetElevation = ((Double)EFNClientConfig.ZANSETSU_CAMERA_ELEVATION.get()).floatValue();
               targetForwardOffset = ((Double)EFNClientConfig.ZANSETSU_CAMERA_FORWARD_OFFSET.get()).floatValue();
               targetSideOffset = ((Double)EFNClientConfig.ZANSETSU_CAMERA_SIDE_OFFSET.get()).floatValue();
               boolean isInAir = isPlayerInAir();
               if (isInAir) {
                  if (entityPatch != null) {
                     handleAirborneTargeting(entityPatch, player, partialTicks);
                  }
               } else if (entityPatch != null) {
                  handleTargetLock(entityPatch, player, partialTicks);
               }
            } else {
               targetElevation = 0.0F;
               targetForwardOffset = 0.0F;
               targetSideOffset = 0.0F;
            }
         }

         if (wasInFirstPerson) {
            isZansetsuCameraActive = false;
         }

         handlePlayerViewLock(player, wasZansetsuCameraActive, partialTicks);
         handleFovAdjustment(wasZansetsuCameraActive, partialTicks);
         updateTransition(targetElevation, targetForwardOffset, targetSideOffset, partialTicks);
         if (currentElevation > 0.01F || currentForwardOffset > 0.01F || Math.abs(currentSideOffset) > 0.01F) {
            adjustCameraPosition(event.getCamera(), currentElevation, currentForwardOffset, currentSideOffset);
         }

         if (zoomCount > 0) {
            setCameraAnimThirdPerson(event, mc.options.getCameraType(), partialTicks);
            if (!mc.isPaused()) {
               zoomCount = aiming ? zoomCount + 1 : zoomCount - 1;
               zoomCount = Math.min(60, zoomCount);
               zoomOutTimer--;
               if (zoomOutTimer < 0) {
                  aiming = false;
               }
            }
         }
      }
   }

   private static LivingEntity getBestZansetsuTarget(PlayerPatch<?> entityPatch, Player player) {
      LivingEntity focusEntity = EpicFightCameraAPI.getInstance().getFocusingEntity();
      if (focusEntity != null && focusEntity.isAlive()) {
         return focusEntity;
      }

      List<Entity> triedEntities = entityPatch.getCurrentlyAttackTriedEntities();
      if (triedEntities != null && !triedEntities.isEmpty()) {
         LivingEntity closestTarget = null;
         double minDistance = Double.MAX_VALUE;

         for (Entity entity : triedEntities) {
            if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive()) {
               double dist = player.distanceToSqr(livingEntity);
               if (dist < minDistance) {
                  minDistance = dist;
                  closestTarget = livingEntity;
               }
            }
         }

         if (closestTarget != null) {
            return closestTarget;
         }
      }

      return entityPatch.getTarget();
   }

   private static void handleTargetLock(PlayerPatch<?> entityPatch, Player player, float partialTicks) {
      LivingEntity currentTarget = getBestZansetsuTarget(entityPatch, player);
      boolean isInAir = isPlayerInAir();
      if (currentTarget != targetEntity) {
         if (currentTarget == null) {
            resetTargetLock();
            return;
         }

         targetEntity = currentTarget;
         isTargetLockEnabled = true;
         targetLockYRotO = player.getYRot();
         targetLockYRot = player.getYRot();
      }

      if (targetEntity != null && !targetEntity.isRemoved() && targetEntity.isAlive()) {
         double distance = player.distanceTo(targetEntity);
         if (distance > 20.0) {
            resetTargetLock();
         } else {
            Vec3 playerPosition = player.getEyePosition();
            Vec3 targetPosition = targetEntity.getEyePosition();
            Vec3 toTarget = targetPosition.subtract(playerPosition);
            float targetYRot = (float)Math.atan2(toTarget.z, toTarget.x) * (180.0F / (float)Math.PI) - 90.0F;
            float targetXRot = (float)(
               -(Math.atan2(toTarget.y, Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z)) * (180.0 / Math.PI))
            );
            targetLockYRotO = targetLockYRot;
            float yDiff = Mth.wrapDegrees(targetYRot - targetLockYRotO);
            float normalizedDiff = Math.min(Math.abs(yDiff) / 180.0F, 1.0F);
            float easedProgress = smoothStep(normalizedDiff);
            float yLerp = yDiff * easedProgress * 0.4F;
            targetLockYRot = targetLockYRotO + yLerp;
            float currentYRot = Mth.rotLerp(partialTicks, targetLockYRotO, targetLockYRot);
            player.setYRot(currentYRot);
            if (isInAir) {
               handleAirborneXRot(player, targetXRot, partialTicks);
            }
         }
      } else {
         resetTargetLock();
      }
   }

   private static void handleAirborneTargeting(PlayerPatch<?> entityPatch, Player player, float partialTicks) {
      LivingEntity currentTarget = getBestZansetsuTarget(entityPatch, player);
      if (currentTarget != null) {
         Vec3 playerPosition = player.getEyePosition();
         Vec3 targetPosition = currentTarget.getEyePosition();
         Vec3 toTarget = targetPosition.subtract(playerPosition);
         float targetYRot = (float)Math.atan2(toTarget.z, toTarget.x) * (180.0F / (float)Math.PI) - 90.0F;
         float targetXRot = (float)(
            -(Math.atan2(toTarget.y, Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z)) * (180.0 / Math.PI))
         );
         float currentYRot = player.getYRot();
         float yDiff = Mth.wrapDegrees(targetYRot - currentYRot);
         float yLerp = yDiff * 0.15F;
         player.setYRot(currentYRot + yLerp);
         handleAirborneXRot(player, targetXRot, partialTicks);
      }
   }

   private static void handleAirborneXRot(Player player, float targetXRot, float partialTicks) {
      float clampedXRot = Mth.clamp(targetXRot, -45.0F, 45.0F);
      float currentXRot = player.getXRot();
      float xDiff = Mth.wrapDegrees(clampedXRot - currentXRot);
      float xLerp = xDiff * 0.3F;
      float newXRot = currentXRot + xLerp;
      newXRot = Mth.clamp(newXRot, -45.0F, 45.0F);
      player.setXRot(newXRot);
   }

   private static float smoothStep(float x) {
      return x * x * x * (x * (x * 6.0F - 15.0F) + 10.0F);
   }

   private static void resetTargetLock() {
      targetEntity = null;
      isTargetLockEnabled = false;
      targetLockYRot = 0.0F;
      targetLockYRotO = 0.0F;
   }

   private static void handleFovAdjustment(boolean wasZansetsuCameraActive, float partialTicks) {
      if ((Boolean)EFNClientConfig.ZANSETSU_FOV_ENABLED.get()) {
         float targetFov = ((Double)EFNClientConfig.ZANSETSU_FOV_VALUE.get()).floatValue();
         if (isZansetsuCameraActive && !wasZansetsuCameraActive) {
            originalFov = getCurrentGameFov();
            startFovTransition(originalFov, targetFov);
         } else if (!isZansetsuCameraActive && wasZansetsuCameraActive) {
            startFovTransition(getCurrentGameFov(), originalFov);
         }

         if (isFovTransitioning) {
            updateFovTransition(partialTicks);
         }
      }
   }

   private static float getCurrentGameFov() {
      Minecraft mc = Minecraft.getInstance();
      return ((Integer)mc.options.fov().get()).intValue();
   }

   private static void startFovTransition(float startFov, float targetFov) {
      fovTransitionStartTime = System.currentTimeMillis();
      fovTransitionStartValue = startFov;
      fovTransitionTargetValue = targetFov;
      isFovTransitioning = true;
   }

   private static void updateFovTransition(float partialTicks) {
      if (isFovTransitioning) {
         float fovTransitionDuration = ((Double)EFNClientConfig.ZANSETSU_FOV_TRANSITION_DURATION.get()).floatValue();
         float progress = Mth.clamp((float)(System.currentTimeMillis() - fovTransitionStartTime) / (fovTransitionDuration * 1000.0F), 0.0F, 1.0F);
         progress = easeInOutCubic(progress);
         currentFov = Mth.lerp(progress, fovTransitionStartValue, fovTransitionTargetValue);
         if (progress >= 1.0F) {
            isFovTransitioning = false;
            currentFov = fovTransitionTargetValue;
         }
      }
   }

   @SubscribeEvent
   public static void onComputeFov(ComputeFov event) {
      if ((Boolean)EFNClientConfig.ZANSETSU_FOV_ENABLED.get()) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
            if (isZansetsuCameraActive || isFovTransitioning) {
               if (currentFov <= 0.0F) {
                  currentFov = getCurrentGameFov();
               }

               event.setFOV(currentFov);
            }
         }
      }
   }

   private static void handlePlayerViewLock(Player player, boolean wasZansetsuCameraActive, float partialTicks) {
      if (player != null) {
         boolean isViewLockEnabled = (Boolean)EFNClientConfig.ZANSETSU_VIEW_LOCK_ENABLED.get();
         float targetLockedXRot = ((Double)EFNClientConfig.ZANSETSU_CAMERA_LOCKED_XROT.get()).floatValue();
         boolean isInAir = isPlayerInAir();
         if (isZansetsuCameraActive && !wasZansetsuCameraActive) {
            originalXRot = player.getXRot();
            if (isViewLockEnabled && !isInAir) {
               startViewLockTransition(player.getXRot(), targetLockedXRot);
            }
         } else if (!isZansetsuCameraActive && wasZansetsuCameraActive) {
            if (isViewLockEnabled && !isInAir) {
               startViewLockTransition(player.getXRot(), originalXRot);
            } else {
               player.setXRot(originalXRot);
            }
         }

         if (isZansetsuCameraActive && isViewLockEnabled && !isInAir) {
            if (isViewLockTransitioning) {
               updateViewLockTransition(player, partialTicks);
            } else {
               player.setXRot(targetLockedXRot);
            }
         } else if (isViewLockTransitioning && isViewLockEnabled && !isInAir) {
            updateViewLockTransition(player, partialTicks);
         }
      }
   }

   private static void startViewLockTransition(float startXRot, float targetXRot) {
      viewLockTransitionStartTime = System.currentTimeMillis();
      viewLockStartXRot = startXRot;
      viewLockTargetXRot = targetXRot;
      isViewLockTransitioning = true;
   }

   private static void updateViewLockTransition(Player player, float partialTicks) {
      if (isViewLockTransitioning) {
         float viewLockTransitionDuration = ((Double)EFNClientConfig.ZANSETSU_VIEW_LOCK_TRANSITION_DURATION.get()).floatValue();
         float progress = Mth.clamp((float)(System.currentTimeMillis() - viewLockTransitionStartTime) / (viewLockTransitionDuration * 1000.0F), 0.0F, 1.0F);
         progress = easeOutCubic(progress);
         float currentXRot = Mth.lerp(progress, viewLockStartXRot, viewLockTargetXRot);
         player.setXRot(currentXRot);
         if (progress >= 1.0F) {
            isViewLockTransitioning = false;
            player.setXRot(viewLockTargetXRot);
         }
      }
   }

   private static float easeOutCubic(float x) {
      return (float)(1.0 - Math.pow(1.0F - x, 3.0));
   }

   private static void updateTransition(float targetElevation, float targetForwardOffset, float targetSideOffset, float partialTicks) {
      boolean shouldElevate = targetElevation > 0.0F || targetForwardOffset > 0.0F || Math.abs(targetSideOffset) > 0.01F;
      if (transitionStartTime < 0L
         || shouldElevate != isTransitioning && (float)(System.currentTimeMillis() - transitionStartTime) > getTransitionDuration() * 1000.0F) {
         transitionStartTime = System.currentTimeMillis();
         startElevation = currentElevation;
         startForwardOffset = currentForwardOffset;
         startSideOffset = currentSideOffset;
         isTransitioning = shouldElevate;
      }

      float transitionDuration = getTransitionDuration();
      float progress = Mth.clamp((float)(System.currentTimeMillis() - transitionStartTime) / (transitionDuration * 1000.0F), 0.0F, 1.0F);
      progress = easeInOutCubic(progress);
      currentElevation = Mth.lerp(progress, startElevation, targetElevation);
      currentForwardOffset = Mth.lerp(progress, startForwardOffset, targetForwardOffset);
      currentSideOffset = Mth.lerp(progress, startSideOffset, targetSideOffset);
   }

   private static float easeInOutCubic(float x) {
      return x < 0.5F ? 4.0F * x * x * x : 1.0F - (float)Math.pow(-2.0F * x + 2.0F, 3.0) / 2.0F;
   }

   private static void adjustCameraPosition(Camera camera, float elevation, float forwardOffset, float sideOffset) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
         try {
            Vec3 lookVector = new Vec3(camera.getLookVector().x, 0.0, camera.getLookVector().z).normalize();
            Vec3 rightVector = new Vec3(-lookVector.z, 0.0, lookVector.x).normalize();
            Vec3 newPos = camera.getPosition()
               .add(0.0, elevation, 0.0)
               .add(lookVector.scale(forwardOffset))
               .add(rightVector.scale(sideOffset));
            if (isPositionValid(newPos)) {
               ((CameraAccessor)camera).invokeSetPosition(newPos.x, newPos.y, newPos.z);
            }
         } catch (Exception e) {
            EpicFightMod.LOGGER.error("Camera adjustment failed", e);
         }
      }
   }

   private static boolean isPositionValid(Vec3 pos) {
      if (Minecraft.getInstance().level == null) {
         return false;
      }

      BlockPos blockPos = BlockPos.containing(pos);
      return Minecraft.getInstance().level.getBlockState(blockPos).isAir();
   }

   private static void setCameraAnimThirdPerson(ComputeCameraAngles event, CameraType pov, double partialTicks) {
      if (ClientEngine.getInstance().getPlayerPatch() != null && Minecraft.getInstance().level != null) {
         if (pov == CameraType.THIRD_PERSON_BACK || pov == CameraType.THIRD_PERSON_FRONT) {
            Camera camera = event.getCamera();
            Entity entity = Minecraft.getInstance().getCameraEntity();
            if (entity != null) {
               Vec3 vector = camera.getPosition();
               double totalX = vector.x();
               double totalY = vector.y();
               double totalZ = vector.z();
               if (pov == CameraType.THIRD_PERSON_BACK) {
                  double posX = vector.x();
                  double posY = vector.y();
                  double posZ = vector.z();
                  double entityPosX = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
                  double entityPosY = entity.yOld + (entity.getY() - entity.yOld) * partialTicks + entity.getEyeHeight();
                  double entityPosZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;
                  float intpol = zoomCount / 60.0F;
                  Vec3f interpolatedCorrection = new Vec3f(aimingCorrection.x * intpol, aimingCorrection.y * intpol, aimingCorrection.z * intpol);
                  OpenMatrix4f rotationMatrix = ClientEngine.getInstance().getPlayerPatch().getMatrix((float)partialTicks);
                  Vec3f rotateVec = OpenMatrix4f.transform3v(rotationMatrix, interpolatedCorrection, null);
                  double d3 = Math.sqrt(rotateVec.x * rotateVec.x + rotateVec.y * rotateVec.y + rotateVec.z * rotateVec.z);
                  double smallest = d3;
                  double d00 = posX + rotateVec.x;
                  double d11 = posY - rotateVec.y;
                  double d22 = posZ + rotateVec.z;

                  for (int i = 0; i < 8; i++) {
                     float f = ((i & 1) * 2 - 1) * 0.1F;
                     float f1 = ((i >> 1 & 1) * 2 - 1) * 0.1F;
                     float f2 = ((i >> 2 & 1) * 2 - 1) * 0.1F;
                     HitResult raytraceresult = Minecraft.getInstance()
                        .level
                        .clip(
                           new ClipContext(
                              new Vec3(entityPosX + f, entityPosY + f1, entityPosZ + f2),
                              new Vec3(d00 + f + f2, d11 + f1, d22 + f2),
                              Block.COLLIDER,
                              Fluid.NONE,
                              entity
                           )
                        );
                     double d7 = raytraceresult.getLocation().distanceTo(new Vec3(entityPosX, entityPosY, entityPosZ));
                     if (d7 < smallest) {
                        smallest = d7;
                     }
                  }

                  float dist = d3 == 0.0 ? 0.0F : (float)(smallest / d3);
                  totalX += rotateVec.x * dist;
                  totalY -= rotateVec.y * dist;
                  totalZ += rotateVec.z * dist;
               }

               BlockPos cameraPos = new BlockPos((int)totalX, (int)totalY, (int)totalZ);
               if (Minecraft.getInstance().level.getBlockState(cameraPos).is(Blocks.AIR)) {
                  ((CameraAccessor)camera).invokeSetPosition(totalX, totalY, totalZ);
               }
            }
         }
      }
   }

   public static void setSideOffset(float sideOffset) {
      transitionStartTime = System.currentTimeMillis();
      startSideOffset = currentSideOffset;
      currentSideOffset = sideOffset;
   }

   public static float getCurrentSideOffset() {
      return currentSideOffset;
   }

   private static float getTransitionDuration() {
      return ((Double)EFNClientConfig.ZANSETSU_CAMERA_TRANSITION_DURATION.get()).floatValue();
   }

   private static float getViewLockTransitionDuration() {
      return ((Double)EFNClientConfig.ZANSETSU_VIEW_LOCK_TRANSITION_DURATION.get()).floatValue();
   }

   private static float getLockedXRot() {
      return ((Double)EFNClientConfig.ZANSETSU_CAMERA_LOCKED_XROT.get()).floatValue();
   }

   public static void resetAllOffsets() {
      transitionStartTime = System.currentTimeMillis();
      startElevation = currentElevation;
      startForwardOffset = currentForwardOffset;
      startSideOffset = currentSideOffset;
      currentElevation = 0.0F;
      currentForwardOffset = 0.0F;
      currentSideOffset = 0.0F;
      isTransitioning = false;
   }

   public static void resetPlayerViewLock() {
      resetAllCameraEffects();
   }

   public static void resetAllCameraEffects() {
      CameraLockUtil.clearSequence();
      isZansetsuCameraActive = false;
      isViewLockTransitioning = false;
      Player player = Minecraft.getInstance().player;
      if (player != null && originalXRot != 0.0F) {
         player.setXRot(originalXRot);
      }

      isFovTransitioning = false;
      if (originalFov > 0.0F) {
         currentFov = originalFov;
      } else {
         currentFov = getCurrentGameFov();
      }

      resetTargetLock();
      resetAllOffsets();
      aiming = false;
      zoomCount = 0;
      zoomOutTimer = 0;
   }
}
