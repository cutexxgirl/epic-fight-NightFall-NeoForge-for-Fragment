package com.hm.efn.client.particle.efnparticletype;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface EFNParticleRenderTypes {
   ParticleRenderType EFN_PARTICLE_SHARPEN = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.enableBlend();
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         RenderSystem.defaultBlendFunc();
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "EFN_PARTICLE_SHARPEN";
      }
   };
   ParticleRenderType EFN_PARTICLE_BLUR = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.enableBlend();
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         RenderSystem.defaultBlendFunc();
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "EFN_PARTICLE_BLUR";
      }
   };
   ParticleRenderType EFN_PARTICLE_MODEL_NO_NORMAL = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.disableCull();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.setShader(GameRenderer::getParticleShader);
         return tesselator.begin(Mode.TRIANGLES, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "EFN_PARTICLE_MODEL_NO_NORMAL";
      }
   };
   ParticleRenderType PARTICLE_SHEET_GLOWING_INTENSE = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.blendEquation(32774);
         RenderSystem.setShader(GameRenderer::getParticleShader);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "PARTICLE_SHEET_GLOWING_INTENSE";
      }
   };
   ParticleRenderType PARTICLE_SHEET_GLOWING_BALANCED = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 771);
         RenderSystem.blendEquation(32774);
         RenderSystem.setShader(GameRenderer::getParticleShader);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "PARTICLE_SHEET_GLOWING_BALANCED";
      }
   };
   ParticleRenderType PARTICLE_SHEET_GLOWING_ADAPTIVE = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(770, 771, 1, 1);
         RenderSystem.setShader(GameRenderer::getParticleShader);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "PARTICLE_SHEET_GLOWING_ADAPTIVE";
      }
   };
   ParticleRenderType PARTICLE_SHEET_GLOWING_MASTER = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(1, 771, 1, 0);
         RenderSystem.setShader(GameRenderer::getParticleShader);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "PARTICLE_SHEET_GLOWING_MASTER";
      }
   };
   ParticleRenderType ENTITY_PARTICLE_GLOWING = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendEquation(32774);
         RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.setShader(GameRenderer::getRendertypeTranslucentShader);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.texParameter(3553, 10241, 9987);
         RenderSystem.texParameter(3553, 10240, 9729);
         RenderSystem.texParameter(3553, 10242, 33071);
         RenderSystem.texParameter(3553, 10243, 33071);
         RenderSystem.polygonOffset(-3.0F, -3.0F);
         RenderSystem.enablePolygonOffset();
         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return "epicfight:ENTITY_PARTICLE_GLOWING_ULTIMATE";
      }
   };
}
