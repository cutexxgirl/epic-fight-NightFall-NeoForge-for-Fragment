package com.hm.efn.util;

import com.hm.efn.EFNClientConfig;
import net.neoforged.fml.ModList;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EffekUnits {
   public static boolean VFXENABLE() {
      return (Boolean)EFNClientConfig.VFX_PLUS.get() && ModList.get().isLoaded("aaa_particles");
   }

   public static float getRY(LivingEntityPatch<?> livingEntityPatch) {
      float yRot = livingEntityPatch.getYRot();
      float converted = -yRot - 90.0F;
      converted %= 360.0F;
      if (converted < 0.0F) {
         converted += 360.0F;
      }

      return (float)Math.toRadians(converted);
   }
}
