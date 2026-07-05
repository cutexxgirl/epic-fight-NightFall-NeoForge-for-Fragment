package com.hm.efn.registries;

import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.hm.efn.EFN;
import com.hm.efn.client.shaderpasses.AirDisturbance;
import com.hm.efn.client.shaderpasses.BlackWhiteContrastSimple;
import com.hm.efn.client.shaderpasses.Blur;
import com.hm.efn.client.shaderpasses.ChromaticAberration;
import com.hm.efn.client.shaderpasses.ColorDispersion;
import com.hm.efn.client.shaderpasses.DepthCull;
import com.hm.efn.client.shaderpasses.DownSampling;
import com.hm.efn.client.shaderpasses.HsvFilter;
import com.hm.efn.client.shaderpasses.MaskComposite;
import com.hm.efn.client.shaderpasses.RedColor;
import com.hm.efn.client.shaderpasses.ScreenDistortion;
import com.hm.efn.client.shaderpasses.SpaceBroken;
import com.hm.efn.client.shaderpasses.UEComposite;
import com.hm.efn.client.shaderpasses.UnityComposite;
import com.hm.efn.client.shaderpasses.UpSampling;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@OnlyIn(Dist.CLIENT)
public class PostPasses {
   public static PostPassBase blit;
   public static Blur blur;
   public static PostPassBase composite;
   public static SpaceBroken space_broken;
   public static DepthCull depth_cull;
   public static DownSampling downSampler;
   public static UpSampling upSampler;
   public static UnityComposite unity_composite;
   public static UEComposite ue_composite;
   public static ColorDispersion color_dispersion;
   public static HsvFilter hsv_filter;
   public static MaskComposite mask_composite;
   public static ScreenDistortion screen_distortion;
   public static AirDisturbance air_disturbance;
   public static RedColor red_color;
   public static BlackWhiteContrastSimple black_white_contrast;
   public static ChromaticAberration chromatic_aberration;

   public static void register(RegisterShadersEvent event) {
      ResourceManager rm = Minecraft.getInstance().getResourceManager();

      try {
         EFN.LOGGER.info("Loading EFN shaders...");
         composite = new PostPassBase("efn:composite", rm);
         blit = new PostPassBase("efn:blit", rm);
         downSampler = new DownSampling("efn:down_sampling", rm);
         upSampler = new UpSampling("efn:up_sampling", rm);
         unity_composite = new UnityComposite("efn:unity_composite", rm);
         ue_composite = new UEComposite("efn:ue_composite", rm);
         space_broken = new SpaceBroken("efn:space_broken", rm);
         depth_cull = new DepthCull("efn:depth_cull", rm);
         color_dispersion = new ColorDispersion("efn:color_dispersion", rm);
         hsv_filter = new HsvFilter("efn:hsv_filter", rm);
         mask_composite = new MaskComposite("efn:mask_composite", rm);
         blur = new Blur(rm);
         screen_distortion = new ScreenDistortion("efn:screen_distortion", rm);
         air_disturbance = new AirDisturbance("efn:air_disturbance", rm);
         red_color = new RedColor("efn:red_color", rm);
         black_white_contrast = new BlackWhiteContrastSimple("efn:black_white_simple", rm);
         chromatic_aberration = new ChromaticAberration("efn:chromatic_aberration", rm);
         EFN.LOGGER.info("All EFN shaders loaded successfully");
      } catch (IOException e) {
         EFN.LOGGER.error("Failed to load EFN shaders", e);
      }
   }
}
