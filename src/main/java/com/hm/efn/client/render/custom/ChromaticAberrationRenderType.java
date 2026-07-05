package com.hm.efn.client.render.custom;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
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

public class ChromaticAberrationRenderType extends PostParticleRenderType {
   public static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new ChromaticAberrationRenderType.Pipeline(
      OjangUtils.newRL("efn", "chromatic_aberration_o"), 100
   );
   private final float strength;
   private final float speed;
   private final float directionX;
   private final float directionY;
   private final float bladeLength;

   public ChromaticAberrationRenderType(
      ResourceLocation name, float strength, float speed, float directionX, float directionY, float bladeLength, ResourceLocation texture
   ) {
      super(name, texture);
      this.strength = strength;
      this.speed = speed;
      this.directionX = directionX;
      this.directionY = directionY;
      this.bladeLength = bladeLength;
      this.priority = 1000;
   }

   protected ShaderInstance getShader() {
      return GameRenderer.getParticleShader();
   }

   @Override
   public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
      return ParticleRenderBridge.begin(
         tesselator, textureManager, this.texture, this::getShader, this.getPipeline(), Mode.QUADS, DefaultVertexFormat.PARTICLE, false, false
      );
   }

   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
   }

   public com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline getPipeline() {
      return ppl;
   }

   public static class Pipeline extends com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline {
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("efn", "chromatic_aberration_tmp");
      private float currentTime = 0.0F;
      private float progress = 0.0F;
      private boolean animationStarted = false;

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
               if (!this.animationStarted) {
                  this.currentTime = 0.0F;
                  this.progress = 0.0F;
                  this.animationStarted = true;
               }
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

      void handleDisturbanceEffect(RenderTarget src) {
         RenderTarget tmp = TargetManager.getTarget(tmpTarget);
         RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
         this.updateAnimation();
         float strength = 0.18F;
         float speed = 3.0F;
         float directionX = 1.0F;
         float directionY = 0.0F;
         float bladeLength = 0.5F;
         PostPasses.chromatic_aberration
            .process(main, src, tmp, strength * this.getCurrentStrength(), this.currentTime, this.progress, directionX, directionY, bladeLength);
         PostPasses.blit.process(tmp, main);
         TargetManager.ReleaseTarget(tmpTarget);
         this.bufferTarget = null;
      }

      private void updateAnimation() {
         if (this.animationStarted) {
            this.currentTime += 0.05F;
            this.progress = this.calculateProgress();
            if (this.progress <= 0.0F) {
               this.animationStarted = false;
            }
         }
      }

      private float calculateProgress() {
         float duration = 2.0F;
         float normalizedTime = this.currentTime / duration;
         if (normalizedTime < 0.3F) {
            return normalizedTime / 0.3F;
         } else {
            return normalizedTime < 1.0F ? 1.0F - (normalizedTime - 0.3F) / 0.7F : 0.0F;
         }
      }

      private float getCurrentStrength() {
         float normalizedTime = this.currentTime / 2.0F;
         if (normalizedTime > 0.5F) {
            float fade = 1.0F - (normalizedTime - 0.5F) / 0.5F;
            return fade * fade;
         } else {
            return 1.0F;
         }
      }

      public void PostEffectHandler() {
         this.handleDisturbanceEffect(this.bufferTarget);
      }
   }
}
