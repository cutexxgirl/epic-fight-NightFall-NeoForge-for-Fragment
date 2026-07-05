package com.hm.efn.client.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.server.packs.resources.ResourceManager;

public class DepthCull extends PostPassBase {
   public DepthCull(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(RenderTarget inTarget, RenderTarget depthSrc, RenderTarget outTarget) {
      this.prevProcess(inTarget, outTarget);
      inTarget.unbindWrite();
      RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
      this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
      this.effect.setSampler("SrcDepth", inTarget::getDepthTextureId);
      this.effect.setSampler("GlobalDepth", depthSrc::getDepthTextureId);
      this.effect.safeGetUniform("ProjMat").set(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.safeGetUniform("OutSize").set(outTarget.width, outTarget.height);
      this.effect.apply();
      this.pushVertex(inTarget, outTarget);
      this.effect.clear();
      outTarget.unbindWrite();
      inTarget.unbindRead();
   }
}
