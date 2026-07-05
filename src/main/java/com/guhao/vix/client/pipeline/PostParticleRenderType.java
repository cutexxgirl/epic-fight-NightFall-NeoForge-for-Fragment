package com.guhao.vix.client.pipeline;

import com.guhao.vix.util.RenderUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public abstract class PostParticleRenderType implements ParticleRenderType {
   static ResourceLocation tempTarget = ResourceLocation.parse("vix:depth_cull_temp");
   protected final ResourceLocation renderTypeID;
   protected final ResourceLocation texture;
   public int priority;

   public PostParticleRenderType(ResourceLocation renderTypeID, ResourceLocation texture) {
      this.renderTypeID = renderTypeID;
      this.texture = texture;
      this.priority = 0;
   }

   public static RenderTarget createTempTarget(RenderTarget source) {
      RenderTarget target = new TextureTarget(source.width, source.height, true, Minecraft.ON_OSX);
      target.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      target.clear(Minecraft.ON_OSX);
      return target;
   }

   @Override
   public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.setShader(this::getShader);
      if (this.texture != null) {
         RenderUtils.GLSetTexture(this.texture);
      }

      this.getPipeline().start();
      return tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
   }

   protected ShaderInstance getShader() {
      return GameRenderer.getPositionColorTexLightmapShader();
   }

   public void callPipeline() {
      this.getPipeline().call();
   }

   public boolean tryCallPipeline() {
      if (PostEffectPipelines.isActive()) {
         return false;
      }

      this.callPipeline();
      return true;
   }

   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
   }

   public abstract PostEffectPipelines.Pipeline getPipeline();

   @Override
   public String toString() {
      return this.renderTypeID.toString();
   }
}
