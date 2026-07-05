package com.guhao.vix.client.shaderpasses;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;

public class PostPassBase {
   protected EffectInstance effect;

   public PostPassBase(EffectInstance effect) {
      this.effect = effect;
   }

   public PostPassBase(String resourceLocation, ResourceManager resourceManager) throws IOException {
      this(new EffectInstance((ResourceProvider)resourceManager, resourceLocation));
   }

   protected static Matrix4f orthographic(RenderTarget target) {
      return new Matrix4f().setOrtho(0.0F, (float)target.width, 0.0F, (float)target.height, 0.1F, 1000.0F);
   }

   protected void setParameter(EffectInstance effect, RenderTarget inTarget, RenderTarget outTarget) {
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget) {
      this.process(inTarget, outTarget, null);
   }

   public void process(RenderTarget inTarget, RenderTarget outTarget, Consumer<EffectInstance> uniformConsumer) {
      this.prevProcess(inTarget, outTarget);
      inTarget.unbindWrite();
      RenderSystem.viewport(0, 0, outTarget.width, outTarget.height);
      this.effect.setSampler("DiffuseSampler", inTarget::getColorTextureId);
      this.setUniform("ProjMat", uniform -> uniform.set(orthographic(outTarget)));
      this.setUniform("OutSize", uniform -> uniform.set((float)outTarget.width, (float)outTarget.height));
      this.setParameter(this.effect, inTarget, outTarget);

      if (uniformConsumer != null) {
         uniformConsumer.accept(this.effect);
      }

      this.effect.apply();

      try {
         this.pushVertex(inTarget, outTarget);
      } finally {
         this.effect.clear();
         outTarget.unbindWrite();
         inTarget.unbindRead();
      }
   }

   public void prevProcess(RenderTarget inTarget, RenderTarget outTarget) {
   }

   public void close() {
      if (this.effect != null) {
         this.effect.close();
         this.effect = null;
      }
   }

   public void pushVertex(RenderTarget inTarget, RenderTarget outTarget) {
      outTarget.clear(Minecraft.ON_OSX);
      outTarget.bindWrite(false);
      RenderSystem.depthFunc(519);
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION);
      bufferBuilder.addVertex(0.0F, 0.0F, 500.0F);
      bufferBuilder.addVertex((float)outTarget.width, 0.0F, 500.0F);
      bufferBuilder.addVertex((float)outTarget.width, (float)outTarget.height, 500.0F);
      bufferBuilder.addVertex(0.0F, (float)outTarget.height, 500.0F);
      BufferUploader.draw(bufferBuilder.buildOrThrow());
      RenderSystem.depthFunc(515);
   }

   private void setUniform(String name, Consumer<AbstractUniform> setter) {
      setter.accept(this.effect.safeGetUniform(name));
   }
}
