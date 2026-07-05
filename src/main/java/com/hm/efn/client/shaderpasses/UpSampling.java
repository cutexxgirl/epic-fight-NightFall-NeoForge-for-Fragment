package com.hm.efn.client.shaderpasses;

import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.packs.resources.ResourceManager;

public class UpSampling extends PostPassBase {
   public UpSampling(EffectInstance effect) {
      super(effect);
   }

   public UpSampling(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, RenderTarget downTexture) {
      inTarget.unbindWrite();
      RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
      this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
      this.effect.safeGetUniform("ProjMat").set(orthographic(outTarget));
      this.effect.safeGetUniform("OutSize").set(outTarget.width, outTarget.height);
      this.effect.setSampler("DownTexture", downTexture::getColorTextureId);
      this.effect.apply();
      this.pushVertex(inTarget, outTarget);
      this.effect.clear();
      outTarget.unbindWrite();
      inTarget.unbindRead();
   }
}
