package com.hm.efn.client.render.custom;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class BloomTrailRenderType extends BloomParticleRenderType {
   public BloomTrailRenderType(ResourceLocation renderTypeID, ResourceLocation tex) {
      super(renderTypeID, tex);
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
}
