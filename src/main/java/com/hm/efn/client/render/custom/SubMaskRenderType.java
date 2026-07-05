package com.hm.efn.client.render.custom;

import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class SubMaskRenderType extends PostParticleRenderType {
   public static final SubMaskRenderType.PPL ppl = new SubMaskRenderType.PPL(OjangUtils.newRL("efn", "sub_mask"));

   public SubMaskRenderType(ResourceLocation renderTypeID, ResourceLocation texture) {
      super(renderTypeID, texture);
   }

   @Override
   public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
      return ParticleRenderBridge.begin(
         tesselator,
         textureManager,
         this.texture,
         this::getShader,
         this.getPipeline(),
         Mode.TRIANGLES,
         DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
         true,
         true
      );
   }

   public RenderTarget getTarget() {
      if (ppl.getBufferTarget() == null) {
         RenderTarget ret = TargetManager.getTarget(ppl.name);
         ret.setClearColor(0.0F, 0.0F, 0.0F, 1.0F);
         ret.clear(Minecraft.ON_OSX);
         return ret;
      } else {
         return ppl.getBufferTarget();
      }
   }

   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
   }

   public Pipeline getPipeline() {
      return ppl;
   }

   public static class PPL extends Pipeline {
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("efn", "sub_mask_tmp");

      public PPL(ResourceLocation name) {
         super(name);
         this.priority = 1000;
      }

      public RenderTarget getBufferTarget() {
         return this.bufferTarget;
      }

      public void PostEffectHandler() {
      }
   }
}
