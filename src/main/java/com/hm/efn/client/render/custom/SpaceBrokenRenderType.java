package com.hm.efn.client.render.custom;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.hm.efn.client.render.EFNRenderType;
import com.hm.efn.registries.PostPasses;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class SpaceBrokenRenderType extends PostParticleRenderType {
   static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl1 = new SpaceBrokenRenderType.Pipeline(
      OjangUtils.newRL("efn", "mesh_space_broken_0"), 10
   );
   static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl2 = new SpaceBrokenRenderType.Pipeline(
      OjangUtils.newRL("efn", "mesh_space_broken_1"), 11
   );
   final int layer;
   final int vertex;

   public SpaceBrokenRenderType(ResourceLocation name, ResourceLocation texture, int layer, int vertexCount) {
      super(name, texture);
      this.layer = layer;
      this.vertex = vertexCount;
      this.priority = 1000;
   }

   public SpaceBrokenRenderType(ResourceLocation name, int layer) {
      super(name, EFNRenderType.GetTexture("particle/sparks"));
      this.layer = layer;
      this.vertex = 3;
      this.priority = 1000;
   }

   @Override
   protected ShaderInstance getShader() {
      return GameRenderer.getPositionColorTexLightmapShader();
   }

   @Override
   public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
      return ParticleRenderBridge.begin(
         tesselator,
         textureManager,
         this.texture,
         this::getShader,
         this.getPipeline(),
         this.vertex == 3 ? Mode.TRIANGLES : Mode.QUADS,
         DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
         false,
         false
      );
   }

   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
   }

   public com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline getPipeline() {
      return this.layer == 0 ? ppl1 : ppl2;
   }

   public static class Pipeline extends com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline {
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("efn", "mesh_space_broken_tmp");

      public Pipeline(ResourceLocation name, int priority) {
         super(name);
         this.priority = priority;
      }

      public void start() {
         if (this.started) {
            if (PostEffectPipelines.isActive()) {
               this.bufferTarget.bindWrite(false);
            }
         } else {
            if (this.bufferTarget == null) {
               this.bufferTarget = TargetManager.getTarget(this.name);
               this.bufferTarget.clear(Minecraft.ON_OSX);
            }

            RenderTarget main = PostEffectPipelines.getSource();
            if (PostEffectPipelines.isActive()) {
               this.bufferTarget.copyDepthFrom(main);
               PostEffectPipelines.PostEffectQueue.add(this);
               this.bufferTarget.bindWrite(false);
               this.started = true;
            }
         }
      }

      public void suspend() {
         if (PostEffectPipelines.isActive()) {
            this.bufferTarget.unbindWrite();
            this.bufferTarget.unbindRead();
            RenderTarget rt = PostEffectPipelines.getSource();
            rt.bindWrite(false);
         } else {
            PostEffectPipelines.getSource().bindWrite(false);
         }
      }

      void handlePasses(RenderTarget src) {
         RenderTarget tmp = TargetManager.getTarget(tmpTarget);
         RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
         PostPasses.space_broken.process(main, src, tmp);
         PostPasses.blit.process(tmp, main);
         TargetManager.ReleaseTarget(tmpTarget);
      }

      public void PostEffectHandler() {
         this.handlePasses(this.bufferTarget);
      }
   }
}
