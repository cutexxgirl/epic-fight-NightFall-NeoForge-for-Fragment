package com.hm.efn.client.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public class AirDisturbance extends PostPassBase {
   public AirDisturbance(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(
      RenderTarget inTarget,
      RenderTarget mask,
      RenderTarget outTarget,
      float strength,
      float time,
      float progress,
      float directionX,
      float directionY,
      float bladeLength
   ) {
      this.prevProcess(inTarget, outTarget);
      inTarget.unbindWrite();
      RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
      this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
      this.effect.setSampler("Mask", mask::getColorTextureId);
      this.effect.safeGetUniform("ProjMat").set(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.safeGetUniform("OutSize").set(outTarget.width, outTarget.height);
      this.effect.safeGetUniform("Strength").set(strength);
      this.effect.safeGetUniform("Time").set(time);
      this.effect.safeGetUniform("Progress").set(progress);
      this.effect.safeGetUniform("Direction").set(directionX, directionY);
      this.effect.safeGetUniform("BladeLength").set(bladeLength);
      this.effect.apply();
      this.pushVertex(inTarget, outTarget);
      this.effect.clear();
      outTarget.unbindWrite();
      inTarget.unbindRead();
   }
}
