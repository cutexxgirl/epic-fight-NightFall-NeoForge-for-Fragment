package com.guhao.vix.camera;

import com.hm.efn.EFN;
import com.guhao.vix.util.MathUtils;
import com.hm.efn.mixin.CameraAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(modid = EFN.MODID, value = Dist.CLIENT)
public class CameraEvents {
   private static final Vec3 Vec3UP = new Vec3(0.0, 1.0, 0.0);
   public static CameraAnimation currentAnim;
   private static float yawLock = 0.0F;
   private static Vec3 posLock = Vec3.ZERO;
   private static int tick = 0;
   private static int linkTick = 0;
   private static int maxLinkTick = 3;
   private static boolean isEnd = true;
   private static boolean linking = false;
   private static LivingEntity original;
   private static LivingEntityPatch<?> patched;
   private static float fovO = 0.0F;
   private static boolean isLockPos = false;
   private static CameraAnimation.Pose pose_;
   private static double originalY = 0.0;
   private static boolean useDynamicY = false;

   @SubscribeEvent
   public static void TransformCam(ViewportEvent.ComputeCameraAngles event) {
      CameraType cameraType = Minecraft.getInstance().options.getCameraType();
      if (cameraType == CameraType.FIRST_PERSON || cameraType == CameraType.THIRD_PERSON_FRONT) {
         return;
      }

      if ((isEnd && !linking) || currentAnim == null) {
         return;
      }

      if (original == null || original.isRemoved()) {
         isEnd = true;
         linking = false;
         return;
      }

      Camera camera = event.getCamera();
      double partialTicks = event.getPartialTick();
      if (linking) {
         CameraAnimation.Pose pose = pose_;
         if (pose == null) {
            return;
         }

         float progress = Math.min(1.0F, (linkTick + (float)partialTicks) / Math.max(1.0F, maxLinkTick));
         Vec3 ownerPosition = original.getEyePosition((float)partialTicks);
         Vec3 cameraPosition = camera.getPosition();
         Vec3 targetPosition = calculateFinalCameraPosition(pose, ownerPosition, partialTicks);
         Vec3 currentOffset = cameraPosition.subtract(ownerPosition);
         Vec3 targetOffset = targetPosition.subtract(ownerPosition);
         Vec3 currentCylindrical = MathUtils.ToCylindricalCoordinate(currentOffset);
         Vec3 targetCylindrical = MathUtils.ToCylindricalCoordinate(targetOffset);
         Vec3 finalPosition = MathUtils.ToCartesianCoordinates(MathUtils.LerpMinCylindrical(targetCylindrical, currentCylindrical, progress))
            .add(ownerPosition);
         float yawDelta = (event.getYaw() - yawLock - pose.rotY) % 360.0F;
         if (yawDelta > 0.0F) {
            yawDelta -= 360.0F;
            if (Math.abs(yawDelta) > yawDelta + 360.0F) {
               yawDelta += 360.0F;
            }
         }

         ((CameraAccessor)camera).invokeSetPosition(finalPosition.x(), finalPosition.y(), finalPosition.z());
         event.setYaw(yawLock - pose.rotY + yawDelta * progress);
         event.setPitch(MathUtils.lerpBetween(pose.rotX, event.getPitch(), progress));
         event.setRoll(MathUtils.lerpBetween(pose.rotZ, event.getRoll(), progress));
      } else {
         float elapsedTime = getElapsedAnimationTime(partialTicks);
         CameraAnimation.Pose pose = currentAnim.getPose(elapsedTime);
         pose_ = pose;
         Vec3 finalPosition = calculateFinalCameraPosition(pose, original.getEyePosition((float)partialTicks), partialTicks);
         ((CameraAccessor)camera).invokeSetPosition(finalPosition.x(), finalPosition.y(), finalPosition.z());
         event.setYaw(yawLock - pose.rotY);
         event.setPitch(pose.rotX);
         event.setRoll(pose.rotZ);
      }
   }

   private static Vec3 calculateFinalCameraPosition(CameraAnimation.Pose pose, Vec3 ownerPosition, double partialTicks) {
      Vec3 rotatedPosition = pose.pos.yRot((float)Math.toRadians(-yawLock - 90.0F));
      if (isLockPos) {
         double y = useDynamicY ? ownerPosition.y() : posLock.y();
         double yOffset = y - originalY;
         return new Vec3(rotatedPosition.x() + posLock.x(), rotatedPosition.y() + posLock.y() + yOffset, rotatedPosition.z() + posLock.z());
      }

      return rotatedPosition.add(ownerPosition);
   }

   @SubscribeEvent
   public static void AnimateCamFov(ViewportEvent.ComputeFov event) {
      CameraType cameraType = Minecraft.getInstance().options.getCameraType();
      if (cameraType == CameraType.FIRST_PERSON || cameraType == CameraType.THIRD_PERSON_FRONT) {
         return;
      }

      if ((isEnd && !linking) || currentAnim == null) {
         return;
      }

      if (original == null || original.isRemoved()) {
         isEnd = true;
         linking = false;
         return;
      }

      double partialTicks = event.getPartialTick();
      if (linking) {
         CameraAnimation.Pose pose = pose_;
         if (pose != null) {
            float progress = Math.min(1.0F, (linkTick + (float)partialTicks) / Math.max(1.0F, maxLinkTick));
            event.setFOV(MathUtils.lerpBetween(pose.fov, fovO, progress));
         }
      } else {
         CameraAnimation.Pose pose = currentAnim.getPose((tick + (float)partialTicks) / 20.0F);
         pose_ = pose;
         event.setFOV(pose.fov);
      }
   }

   @SubscribeEvent
   public static void Tick(ClientTickEvent.Post event) {
      if (Minecraft.getInstance().isPaused()) {
         return;
      }

      if (!isEnd) {
         tick++;
      }

      if (linking) {
         linkTick++;
      }

      if (!isEnd && currentAnim != null && tick / 20.0F >= currentAnim.totalTime) {
         isEnd = true;
         linking = true;
         tick = 0;
         linkTick = 0;
      }

      if (linking && currentAnim != null && linkTick >= maxLinkTick) {
         isEnd = true;
         linking = false;
         linkTick = 0;
         tick = 0;
      }
   }

   public static void SetAnim(CameraAnimation cameraAnimation, LivingEntity entity, boolean lockPos, AnimationAccessor<?> animation) {
      SetAnim(cameraAnimation, entity, lockPos, animation, true);
   }

   public static void SetAnim(CameraAnimation cameraAnimation, LivingEntity entity, boolean lockPos, AnimationAccessor<?> animation, boolean dynamicY) {
      if (!isLocalPlayer(entity)) {
         return;
      }

      setAnimationState(cameraAnimation, entity, null, lockPos, dynamicY, 0);
   }

   public static void SetAnimWithTime(
      CameraAnimation cameraAnimation,
      LivingEntity entity,
      boolean lockPos,
      AnimationAccessor<?> animation,
      boolean dynamicY,
      int startTick
   ) {
      if (!isLocalPlayer(entity)) {
         return;
      }

      setAnimationState(cameraAnimation, entity, null, lockPos, dynamicY, startTick);
   }

   public static void UpdateCoord(LivingEntity entity) {
      if (isLocalPlayer(entity)) {
         posLock = entity.position();
      }
   }

   public static void SetAnim(CameraAnimation cameraAnimation, LivingEntityPatch<?> entityPatch, boolean lockPos) {
      SetAnim(cameraAnimation, entityPatch, lockPos, true);
   }

   public static void SetAnim(CameraAnimation cameraAnimation, LivingEntityPatch<?> entityPatch, boolean lockPos, boolean dynamicY) {
      if (entityPatch == null || !(entityPatch.getOriginal() instanceof LivingEntity entity) || !isLocalPlayer(entity)) {
         return;
      }

      setAnimationState(cameraAnimation, entity, entityPatch, lockPos, dynamicY, 0);
   }

   private static boolean isLocalPlayer(LivingEntity entity) {
      return entity instanceof Player && entity == Minecraft.getInstance().player;
   }

   private static void setAnimationState(
      CameraAnimation cameraAnimation,
      LivingEntity entity,
      LivingEntityPatch<?> entityPatch,
      boolean lockPos,
      boolean dynamicY,
      int startTick
   ) {
      patched = entityPatch;
      original = entity;
      yawLock = entity.getViewYRot(0.0F);
      posLock = entity.position();
      originalY = posLock.y();
      useDynamicY = dynamicY;
      linking = false;
      isEnd = false;
      tick = startTick;
      linkTick = 0;
      maxLinkTick = 8;
      currentAnim = cameraAnimation;
      isLockPos = lockPos;
      fovO = ((Integer)Minecraft.getInstance().options.fov().get()).floatValue();
   }

   private static float getElapsedAnimationTime(double partialTicks) {
      if (patched == null) {
         return (tick + (float)partialTicks) / 20.0F;
      }

      AnimationPlayer animationPlayer = patched.getAnimator().getPlayerFor(null);
      if (animationPlayer == null) {
         return (tick + (float)partialTicks) / 20.0F;
      }

      return (float)(animationPlayer.getPrevElapsedTime()
         + (animationPlayer.getElapsedTime() - animationPlayer.getPrevElapsedTime()) * partialTicks);
   }
}
