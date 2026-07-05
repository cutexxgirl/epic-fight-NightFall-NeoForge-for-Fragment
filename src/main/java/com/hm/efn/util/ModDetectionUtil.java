package com.hm.efn.util;

import com.hm.efn.EFN;
import com.hm.efn.EFNClientConfig;
import net.neoforged.fml.ModList;

public class ModDetectionUtil {
   public static final String TARGET_MOD_ID = "aaa_particles";

   public static boolean isTargetModInstalled() {
      boolean installed = ModList.get().isLoaded("aaa_particles");
      EFN.LOGGER.info("Mod '{}' installed: {}", "aaa_particles", installed);
      return installed;
   }

   public static boolean shouldShowWarning() {
      if (isTargetModInstalled()) {
         return false;
      }

      try {
         boolean enabled = EFNClientConfig.isAAAWarningEnabled();
         boolean alreadyShown = EFNClientConfig.hasShownAAAWarning();
         EFN.LOGGER.info("Warning check - Enabled: {}, Already shown: {}", enabled, alreadyShown);
         return EFNClientConfig.shouldShowAAAWarning();
      } catch (Exception e) {
         EFN.LOGGER.error("Error in warning check: {}", e.getMessage());
         return false;
      }
   }
}
