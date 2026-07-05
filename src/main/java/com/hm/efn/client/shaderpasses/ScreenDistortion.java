package com.hm.efn.client.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public class ScreenDistortion extends PostPassBase {
   public ScreenDistortion(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float distortionStrength, float frequency, float time, float progress) {
      this.prevProcess(inTarget, outTarget);
      inTarget.unbindWrite();
      RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
      this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
      this.effect.safeGetUniform("ProjMat").set(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.safeGetUniform("OutSize").set(outTarget.width, outTarget.height);
      this.effect.safeGetUniform("DistortionStrength").set(distortionStrength);
      this.effect.safeGetUniform("Frequency").set(frequency);
      this.effect.safeGetUniform("Time").set(time);
      this.effect.safeGetUniform("Progress").set(progress);
      this.effect.apply();
      this.pushVertex(inTarget, outTarget);
      this.effect.clear();
      outTarget.unbindWrite();
      inTarget.unbindRead();
   }
}
