package com.hm.efn.client.render.custom;

import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.client.targets.ScaledTarget;
import com.guhao.vix.util.OjangUtils;
import com.hm.efn.registries.PostPasses;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class BloomParticleRenderType extends PostParticleRenderType {
   public static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new BloomParticleRenderType.Pipeline(
      OjangUtils.newRL("efn", "bloom_particle")
   );

   public BloomParticleRenderType(ResourceLocation renderTypeID, ResourceLocation tex) {
      super(renderTypeID, tex);
   }

   @Override
   protected ShaderInstance getShader() {
      return GameRenderer.getParticleShader();
   }

   @Override
   public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
      return ParticleRenderBridge.begin(
         tesselator, textureManager, this.texture, this::getShader, this.getPipeline(), Mode.QUADS, DefaultVertexFormat.PARTICLE, false, false
      );
   }

   private static int NumMul(int a, float b) {
      return (int)(a * Math.max(Math.min(b, 1.5F), 0.8F));
   }

   public com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline getPipeline() {
      return ppl;
   }

   public static class Pipeline extends com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline {
      RenderTarget[] blur;
      RenderTarget[] blur_;
      RenderTarget temp;

      public Pipeline(ResourceLocation name) {
         super(name);
      }

      void handlePasses(RenderTarget src) {
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         RenderSystem.texParameter(3553, 10240, 9729);
         RenderSystem.texParameter(3553, 10241, 9729);
         PostPasses.downSampler.process(src, this.blur[0]);
         PostPasses.downSampler.process(this.blur[0], this.blur[1]);
         PostPasses.downSampler.process(this.blur[1], this.blur[2]);
         PostPasses.downSampler.process(this.blur[2], this.blur[3]);
         PostPasses.downSampler.process(this.blur[3], this.blur[4]);
         PostPasses.upSampler.process(this.blur[4], this.blur_[3], this.blur[3]);
         PostPasses.upSampler.process(this.blur_[3], this.blur_[2], this.blur[2]);
         PostPasses.upSampler.process(this.blur_[2], this.blur_[1], this.blur[1]);
         PostPasses.upSampler.process(this.blur_[1], this.blur_[0], this.blur[0]);
         PostPasses.unity_composite.process(this.blur_[0], this.temp, src, Minecraft.getInstance().getMainRenderTarget());
         PostPasses.blit.process(this.temp, Minecraft.getInstance().getMainRenderTarget());
      }

      void initTargets() {
         int cnt = 5;
         if (this.blur == null) {
            this.blur = new RenderTarget[cnt];
            float s = 1.0F;

            for (int i = 0; i < this.blur.length; i++) {
               s /= 2.0F;
               this.blur[i] = new ScaledTarget(s, s, this.bufferTarget.width, this.bufferTarget.height, false, Minecraft.ON_OSX);
               this.blur[i].setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               this.blur[i].clear(Minecraft.ON_OSX);
               if (this.bufferTarget.isStencilEnabled()) {
                  this.blur[i].enableStencil();
               }
            }
         }

         if (this.blur_ == null) {
            this.blur_ = new RenderTarget[cnt - 1];
            float s = 1.0F;

            for (int i = 0; i < this.blur_.length; i++) {
               s /= 2.0F;
               this.blur_[i] = new ScaledTarget(s, s, this.bufferTarget.width, this.bufferTarget.height, false, Minecraft.ON_OSX);
               this.blur_[i].setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               this.blur_[i].clear(Minecraft.ON_OSX);
               if (this.bufferTarget.isStencilEnabled()) {
                  this.blur[i].enableStencil();
               }
            }
         }

         if (this.temp == null) {
            this.temp = PostParticleRenderType.createTempTarget(this.bufferTarget);
         }

         if (this.temp.width != this.bufferTarget.width || this.temp.height != this.bufferTarget.height) {
            for (int i = 0; i < this.blur.length; i++) {
               this.blur[i].resize(this.bufferTarget.width, this.bufferTarget.height, Minecraft.ON_OSX);
            }

            for (int i = 0; i < this.blur_.length; i++) {
               this.blur_[i].resize(this.bufferTarget.width, this.bufferTarget.height, Minecraft.ON_OSX);
            }

            this.temp.resize(this.bufferTarget.width, this.bufferTarget.height, Minecraft.ON_OSX);
         }
      }

      public void PostEffectHandler() {
         this.initTargets();
         this.handlePasses(this.bufferTarget);
      }
   }
}
