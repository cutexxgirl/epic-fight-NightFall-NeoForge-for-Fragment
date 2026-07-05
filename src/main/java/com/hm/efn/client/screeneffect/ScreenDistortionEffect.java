package com.hm.efn.client.screeneffect;

import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.guhao.vix.client.screeneffect.ScreenEffectBase;
import com.guhao.vix.client.screeneffect.ScreenEffectBase.SE_Pipeline;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.hm.efn.registries.PostPasses;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ScreenDistortionEffect extends ScreenEffectBase {
   static ResourceLocation screen_distortion = OjangUtils.newRL("efn", "screen_distortion");
   public final SE_Pipeline ppl;
   public float maxDistortion = 0.1F;
   public float frequency = 5.0F;
   public float distortionSpeed = 2.0F;

   public ScreenDistortionEffect(Vec3 pos) {
      this(pos, 60, 0.1F, 5.0F, 2.0F);
   }

   public ScreenDistortionEffect(Vec3 pos, int lifetime, float maxDistortion, float frequency, float distortionSpeed) {
      super(screen_distortion, pos);
      this.ppl = new ScreenDistortionEffect.Distortion_Pipeline(this);
      this.lifetime = lifetime;
      this.maxDistortion = maxDistortion;
      this.frequency = frequency;
      this.distortionSpeed = distortionSpeed;
   }

   public Pipeline getPipeline() {
      return this.ppl;
   }

   public static class Distortion_Pipeline extends SE_Pipeline<ScreenDistortionEffect> {
      static ResourceLocation distortion_tmp = OjangUtils.newRL("efn", "distortion_tmp");

      public Distortion_Pipeline(ScreenDistortionEffect effect) {
         super(ScreenDistortionEffect.screen_distortion, effect);
         this.priority = 100;
      }

      public void PostEffectHandler() {
         RenderTarget tmp = TargetManager.getTarget(distortion_tmp);
         PostPasses.blit.process(Minecraft.getInstance().getMainRenderTarget(), tmp);
         float progress = this.getDistortionProgress();
         float currentDistortion = ((ScreenDistortionEffect)this.effect).maxDistortion * this.getDistortionIntensity(progress);
         float time = ((ScreenDistortionEffect)this.effect).age * 0.05F * ((ScreenDistortionEffect)this.effect).distortionSpeed;
         PostPasses.screen_distortion
            .process(tmp, Minecraft.getInstance().getMainRenderTarget(), currentDistortion, ((ScreenDistortionEffect)this.effect).frequency, time, progress);
      }

      private float getDistortionProgress() {
         float normalizedAge = ((ScreenDistortionEffect)this.effect).getNormalizedAgeWithPartialTicks();
         float baseProgress = Mth.sin(normalizedAge * (float) Math.PI);
         if (normalizedAge > 0.8F) {
            float endFade = 1.0F - (normalizedAge - 0.7F) / 0.3F;
            endFade = endFade * endFade * (3.0F - 2.0F * endFade);
            baseProgress *= endFade;
         }

         return baseProgress;
      }

      private float getDistortionIntensity(float progress) {
         float normalizedAge = ((ScreenDistortionEffect)this.effect).getNormalizedAgeWithPartialTicks();
         float baseIntensity = 4.0F * progress * (1.0F - progress);
         if (normalizedAge > 0.75F) {
            float endFade = 1.0F - (normalizedAge - 0.6F) / 0.4F;
            endFade = Mth.sqrt(endFade);
            baseIntensity *= endFade;
         }

         return baseIntensity;
      }
   }
}
