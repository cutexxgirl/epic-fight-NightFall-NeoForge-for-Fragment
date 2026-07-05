package com.hm.efn.client.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public class BlackWhiteContrastSimple extends PostPassBase {
   public BlackWhiteContrastSimple(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(
      RenderTarget inTarget,
      RenderTarget outTarget,
      float contrast,
      float brightness,
      float time,
      float intensity,
      float speed,
      float mode,
      float impactThreshold,
      float impactThresholdLerp,
      float focalU,
      float focalV,
      float chromaticStrength,
      float lensDistortStrength
   ) {
      this.prevProcess(inTarget, outTarget);
      inTarget.unbindWrite();
      RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
      this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
      this.effect.safeGetUniform("ProjMat").set(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.safeGetUniform("OutSize").set(outTarget.width, outTarget.height);
      this.effect.safeGetUniform("Contrast").set(contrast);
      this.effect.safeGetUniform("Brightness").set(brightness);
      this.effect.safeGetUniform("Time").set(time);
      this.effect.safeGetUniform("Intensity").set(intensity);
      this.effect.safeGetUniform("Speed").set(speed);
      this.effect.safeGetUniform("Mode").set(mode);
      this.effect.safeGetUniform("ImpactThreshold").set(impactThreshold);
      this.effect.safeGetUniform("ImpactThresholdLerp").set(impactThresholdLerp);
      this.effect.safeGetUniform("FocalUV").set(focalU, focalV);
      this.effect.safeGetUniform("ChromaticStrength").set(chromaticStrength);
      this.effect.safeGetUniform("LensDistortStrength").set(lensDistortStrength);
      this.effect.apply();
      this.pushVertex(inTarget, outTarget);
      this.effect.clear();
      outTarget.unbindWrite();
      inTarget.unbindRead();
   }

   public void process(
      RenderTarget inTarget,
      RenderTarget outTarget,
      float contrast,
      float brightness,
      float time,
      float intensity,
      float speed,
      float mode,
      float impactThreshold,
      float impactThresholdLerp,
      float focalU,
      float focalV
   ) {
      this.process(
         inTarget, outTarget, contrast, brightness, time, intensity, speed, mode, impactThreshold, impactThresholdLerp, focalU, focalV, 0.003F, -0.25F
      );
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float contrast, float brightness, float time) {
      this.process(inTarget, outTarget, contrast, brightness, time, 0.8F, 1.0F, 1.0F, 0.45F, 0.15F, 0.5F, 0.5F);
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float contrast, float brightness) {
      this.process(inTarget, outTarget, contrast, brightness, 0.0F);
   }
}
