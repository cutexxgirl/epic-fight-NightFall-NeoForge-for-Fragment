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

public class AirDisturbanceRenderType extends PostParticleRenderType {
   static final com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline ppl = new AirDisturbanceRenderType.Pipeline(
      OjangUtils.newRL("efn", "air_disturbance"), 150
   );

   public AirDisturbanceRenderType(ResourceLocation name, ResourceLocation location) {
      super(name, location);
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
      private static final ResourceLocation tmpTarget = OjangUtils.newRL("efn", "air_disturbance_tmp");
      private float currentTime = 0.0F;
      private float progress = 0.0F;
      private boolean animationStarted = false;
      private float lastStrength = 0.0F;

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
         float baseStrength = 0.007F;
         float speedFactor = 1.0F;
         if (Minecraft.getInstance().player != null) {
            float speed = (float)Minecraft.getInstance().player.getDeltaMovement().length();
            speedFactor = 0.6F + Math.min(0.8F, speed * 1.5F);
         }

         float targetStrength = baseStrength * this.getCurrentStrength() * speedFactor;
         this.lastStrength = this.lastStrength * 0.7F + targetStrength * 0.3F;
         float directionX = 1.0F;
         float directionY = 0.3F;
         if (Minecraft.getInstance().player != null) {
            float yaw = Minecraft.getInstance().player.getYRot();
            directionX = (float)Math.cos(Math.toRadians(yaw));
            directionY = (float)Math.sin(Math.toRadians(yaw)) * 0.5F;
         }

         float bladeLength = 0.65F * (0.8F + this.progress * 0.4F);
         PostPasses.air_disturbance.process(main, src, tmp, this.lastStrength, this.currentTime, this.progress, directionX, directionY, bladeLength);
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
