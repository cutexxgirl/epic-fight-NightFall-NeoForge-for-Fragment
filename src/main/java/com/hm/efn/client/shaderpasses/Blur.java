package com.hm.efn.client.shaderpasses;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.packs.resources.ResourceManager;

public class Blur extends PostPassBase {
   public Blur(ResourceManager rsmgr) throws IOException {
      super(new EffectInstance(rsmgr, "efn:blur"));
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, float blurDirX, float blurDirY, int radius) {
      inTarget.unbindWrite();
      RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
      this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
      this.effect.safeGetUniform("ProjMat").set(PostEffectPipelines.shaderOrthoMatrix);
      this.effect.safeGetUniform("OutSize").set(outTarget.width, outTarget.height);
      this.effect.safeGetUniform("BlurDir").set(blurDirX, blurDirY);
      this.effect.safeGetUniform("Radius").set(radius);
      this.effect.apply();
      outTarget.clear(Minecraft.ON_OSX);
      outTarget.bindWrite(false);
      RenderSystem.depthFunc(519);
      BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION);
      bufferbuilder.addVertex(0.0F, 0.0F, 700.0F);
      bufferbuilder.addVertex(inTarget.width, 0.0F, 700.0F);
      bufferbuilder.addVertex(inTarget.width, inTarget.height, 700.0F);
      bufferbuilder.addVertex(0.0F, inTarget.height, 700.0F);
      BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      RenderSystem.depthFunc(515);
      this.effect.clear();
      outTarget.unbindWrite();
      inTarget.unbindRead();
   }
}
