package com.hm.efn.client.render.custom;

import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.guhao.vix.util.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public final class ParticleRenderBridge {
   private ParticleRenderBridge() {
   }

   public static BufferBuilder begin(
      Tesselator tesselator,
      TextureManager textureManager,
      ResourceLocation texture,
      Supplier<ShaderInstance> shader,
      Pipeline pipeline,
      VertexFormat.Mode mode,
      VertexFormat format,
      boolean cull,
      boolean lightLayer
   ) {
      RenderSystem.enableBlend();
      if (cull) {
         RenderSystem.enableCull();
      } else {
         RenderSystem.disableCull();
      }

      if (lightLayer) {
         Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
      }

      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.setShader(shader);
      if (texture != null) {
         RenderUtils.GLSetTexture(texture);
      }

      pipeline.start();
      return tesselator.begin(mode, format);
   }
}
