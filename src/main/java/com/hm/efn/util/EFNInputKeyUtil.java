package com.hm.efn.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class EFNInputKeyUtil {
   private EFNInputKeyUtil() {
   }

   public static boolean matches(KeyMapping keyMapping, boolean mouseInput, int keyCode) {
      if (keyMapping == null || keyCode == InputConstants.UNKNOWN.getValue()) {
         return false;
      }

      InputConstants.Key key = keyMapping.getKey();
      if (key == null || key.getValue() == InputConstants.UNKNOWN.getValue()) {
         return false;
      }

      if (mouseInput) {
         return keyMapping.matchesMouse(keyCode) || key.getType() == InputConstants.Type.MOUSE && key.getValue() == keyCode;
      }

      return key.getType() == InputConstants.Type.KEYSYM && key.getValue() == keyCode;
   }

   public static boolean isDown(KeyMapping keyMapping) {
      if (keyMapping == null) {
         return false;
      }

      InputConstants.Key key = keyMapping.getKey();
      if (key == null || key.getValue() == InputConstants.UNKNOWN.getValue()) {
         return false;
      }

      long window = Minecraft.getInstance().getWindow().getWindow();
      if (key.getType() == InputConstants.Type.MOUSE) {
         return GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
      }

      if (key.getType() == InputConstants.Type.KEYSYM) {
         return GLFW.glfwGetKey(window, key.getValue()) == GLFW.GLFW_PRESS;
      }

      return keyMapping.isDown();
   }
}
