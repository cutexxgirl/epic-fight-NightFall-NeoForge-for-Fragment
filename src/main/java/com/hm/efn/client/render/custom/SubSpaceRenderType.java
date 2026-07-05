package com.hm.efn.client.render.custom;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.hm.efn.registries.PostPasses;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class SubSpaceRenderType extends PostParticleRenderType {
   public static final SubSpaceRenderType.PPL ppl = new SubSpaceRenderType.PPL(OjangUtils.newRL("efn", "sub_space"));

   public SubSpaceRenderType(ResourceLocation renderTypeID, ResourceLocation texture) {
      super(renderTypeID, texture);
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

   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
   }

   public Pipeline getPipeline() {
      return ppl;
   }

   public static class PPL extends Pipeline {
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("efn", "sub_space_tmp");

      public PPL(ResourceLocation name) {
         super(name);
      }

      public RenderTarget getBufferTarget() {
         return this.bufferTarget;
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

            if (PostEffectPipelines.isActive()) {
               PostEffectPipelines.PostEffectQueue.add(this);
               this.bufferTarget.bindWrite(false);
               this.started = true;
            }
         }
      }

      public void PostEffectHandler() {
         RenderTarget mask = SubMaskRenderType.ppl.getBufferTarget();
         if (mask != null) {
            RenderTarget tmp = TargetManager.getTarget(tmpTarget);
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
            PostPasses.mask_composite.process(main, mask, this.bufferTarget, tmp);
            PostPasses.blit.process(tmp, main);
            TargetManager.ReleaseTarget(tmpTarget);
         }
      }
   }
}
