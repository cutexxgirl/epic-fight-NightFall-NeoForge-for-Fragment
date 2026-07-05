package com.guhao.vix.util;

import net.minecraft.resources.ResourceLocation;

public class OjangUtils {
   public static ResourceLocation newRL(String location) {
      return ResourceLocation.parse(location);
   }

   public static ResourceLocation newRL(String namespace, String path) {
      return ResourceLocation.fromNamespaceAndPath(namespace, path);
   }
}
