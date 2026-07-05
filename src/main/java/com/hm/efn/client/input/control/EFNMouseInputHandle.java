package com.hm.efn.client.input.control;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME, value = Dist.CLIENT)
public class EFNMouseInputHandle {
   private static final Minecraft MC = Minecraft.getInstance();
   private static final int MAX_COOLDOWN = 1;
   private static double lastMouseX = 0.0;
   private static double lastMouseY = 0.0;
   private static double deltaMouseX = 0.0;
   private static double movedDistance = 0.0;
   private static double lastMovedDistanceX = 0.0;
   private static double lastMovedDistanceY = 0.0;
   private static int startTick = 0;
   private static int cooldownRemaining = 0;

   private EFNMouseInputHandle() {
   }

   @SubscribeEvent
   public static void onClientTick(Pre event) {
      if (true) {
         if (MC.screen == null && MC.player != null && MC.level != null) {
            handleMouseInput();
         }
      }
   }

   private static void handleMouseInput() {
      if (cooldownRemaining > 0) {
         cooldownRemaining--;
         lastMouseX = MC.mouseHandler.xpos();
      } else {
         double currentMouseX = MC.mouseHandler.xpos();
         double currentMouseY = MC.mouseHandler.ypos();
         double distanceX = currentMouseX - lastMouseX;
         double distanceY = currentMouseY - lastMouseY;
         double currentDeltaMouseX = distanceX == 0.0 ? 0.0 : (distanceX > 0.0 ? 1 : -1);
         lastMovedDistanceX = lastMovedDistanceX + Mth.wrapDegrees(distanceX / 5.0);
         lastMovedDistanceY = lastMovedDistanceY + Mth.wrapDegrees(distanceY / 5.0);
         movedDistance = movedDistance + Math.abs(distanceX);
         double mouseSpeed = 0.0;
         if (MC.player != null) {
            mouseSpeed = movedDistance / (MC.player.tickCount - startTick + 1);
         }

         if (shouldResetMouseDetection(currentMouseX, currentDeltaMouseX, mouseSpeed)) {
            resetMouseDetection();
         }

         if (shouldTriggerEvent()) {
            onMouseInputTriggered(currentDeltaMouseX);
         }

         updateStateVariables(currentMouseX, currentMouseY, currentDeltaMouseX);
      }
   }

   private static boolean shouldResetMouseDetection(double currentMouseX, double currentDeltaMouseX, double mouseSpeed) {
      return currentMouseX == lastMouseX || deltaMouseX != 0.0 && currentDeltaMouseX != deltaMouseX || mouseSpeed < getMinMouseSpeed();
   }

   private static void resetMouseDetection() {
      movedDistance = 0.0;
      if (MC.player != null) {
         startTick = MC.player.tickCount;
      }
   }

   private static boolean shouldTriggerEvent() {
      return movedDistance >= getChangeDistance();
   }

   private static void onMouseInputTriggered(double deltaMouseX) {
      movedDistance = 0.0;
      cooldownRemaining = 1;
   }

   private static void updateStateVariables(double currentMouseX, double currentMouseY, double currentDeltaMouseX) {
      deltaMouseX = currentDeltaMouseX;
      lastMouseX = currentMouseX;
      lastMouseY = currentMouseY;
   }

   private static double getChangeDistance() {
      return 2.0;
   }

   private static double getMinMouseSpeed() {
      return 2.0;
   }

   public static float getLastMovedDistanceX() {
      return (float)lastMovedDistanceX;
   }

   public static void setLastMovedDistanceX(float distance) {
      lastMovedDistanceX = distance;
   }

   public static float getLastMovedDistanceY() {
      return (float)lastMovedDistanceY;
   }

   public static void setLastMovedDistanceY(float distance) {
      lastMovedDistanceY = distance;
   }

   public static double getMovedDistance() {
      return movedDistance;
   }

   public static double getCurrentMouseSpeed() {
      return MC.player == null ? 0.0 : movedDistance / (MC.player.tickCount - startTick + 1);
   }

   public static int getStartTick() {
      return startTick;
   }

   public static int getPlayerTickCount() {
      return MC.player != null ? MC.player.tickCount : 0;
   }

   public static double getDeltaMouseX() {
      return deltaMouseX;
   }

   public static boolean isInCooldown() {
      return cooldownRemaining > 0;
   }

   public static int getCooldownRemaining() {
      return cooldownRemaining;
   }
}
