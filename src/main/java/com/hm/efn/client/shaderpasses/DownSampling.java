package com.hm.efn.client.shaderpasses;

import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.packs.resources.ResourceManager;

public class DownSampling extends PostPassBase {
   public DownSampling(EffectInstance effect) {
      super(effect);
   }

   public DownSampling(String resourceLocation, ResourceManager resmgr) throws IOException {
      super(resourceLocation, resmgr);
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, Consumer<EffectInstance> uniformConsumer) {
      this.prevProcess(inTarget, outTarget);
      inTarget.unbindWrite();
      RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
      this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
      this.effect.safeGetUniform("ProjMat").set(orthographic(outTarget));
      this.effect.safeGetUniform("OutSize").set(outTarget.width, outTarget.height);
      this.effect.safeGetUniform("InSize").set(inTarget.width, inTarget.height);
      if (uniformConsumer != null) {
         uniformConsumer.accept(this.effect);
      }

      this.effect.apply();
      this.pushVertex(inTarget, outTarget);
      this.effect.clear();
      outTarget.unbindWrite();
      inTarget.unbindRead();
   }
}
