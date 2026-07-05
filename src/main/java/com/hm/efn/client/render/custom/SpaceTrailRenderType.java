package com.hm.efn.client.render.custom;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class SpaceTrailRenderType extends SpaceBrokenRenderType {
   public SpaceTrailRenderType(ResourceLocation name, ResourceLocation texture, int layer, int vertexCount) {
      super(name, texture, layer, vertexCount);
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

   @Override
   public void setupBufferBuilder(BufferBuilder bufferBuilder) {
   }
}
