package com.hm.efn.util;

import java.lang.reflect.Method;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;

public class CullableUtil {
   private static final boolean HAS_ENTITYCULLING = ModList.get().isLoaded("entityculling");
   private static final Class<?> CULLABLE_CLASS = findCullableClass();

   public static boolean isCulled(Entity entity) {
      return getBoolean(entity, "isCulled", false);
   }

   public static boolean isForcedVisible(Entity entity) {
      return !isCullable(entity) || getBoolean(entity, "isForcedVisible", true);
   }

   public static void setCulled(Entity entity, boolean value) {
      invoke(entity, "setCulled", new Class[]{boolean.class}, new Object[]{value});
   }

   public static void setTimeout(Entity entity) {
      invoke(entity, "setTimeout", new Class[0], new Object[0]);
   }

   public static void setAlwaysVisible(Entity entity) {
      setCulled(entity, false);
      invoke(entity, "setOutOfCamera", new Class[]{boolean.class}, new Object[]{false});
      setTimeout(entity);
   }

   private static Class<?> findCullableClass() {
      if (!HAS_ENTITYCULLING) {
         return null;
      }

      try {
         return Class.forName("dev.tr7zw.entityculling.versionless.access.Cullable");
      } catch (ClassNotFoundException ignored) {
         return null;
      }
   }

   private static boolean isCullable(Entity entity) {
      return HAS_ENTITYCULLING && CULLABLE_CLASS != null && CULLABLE_CLASS.isInstance(entity);
   }

   private static boolean getBoolean(Entity entity, String methodName, boolean defaultValue) {
      if (!isCullable(entity)) {
         return defaultValue;
      }

      try {
         Method method = CULLABLE_CLASS.getMethod(methodName);
         return (Boolean)method.invoke(entity);
      } catch (ReflectiveOperationException ignored) {
         return defaultValue;
      }
   }

   private static void invoke(Entity entity, String methodName, Class<?>[] parameterTypes, Object[] args) {
      if (!isCullable(entity)) {
         return;
      }

      try {
         Method method = CULLABLE_CLASS.getMethod(methodName, parameterTypes);
         method.invoke(entity, args);
      } catch (ReflectiveOperationException ignored) {
      }
   }
}
