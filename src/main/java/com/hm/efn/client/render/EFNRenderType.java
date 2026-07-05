package com.hm.efn.client.render;

import com.guhao.vix.util.OjangUtils;
import com.guhao.vix.util.RenderUtils;
import com.hm.efn.client.render.custom.AirDisturbanceRenderType;
import com.hm.efn.client.render.custom.BloomParticleRenderType;
import com.hm.efn.client.render.custom.BloomTrailRenderType;
import com.hm.efn.client.render.custom.ChromaticAberrationRenderType;
import com.hm.efn.client.render.custom.MeshSpaceBrokenRenderType;
import com.hm.efn.client.render.custom.ParticleRenderBridge;
import com.hm.efn.client.render.custom.SpaceBrokenRenderType;
import com.hm.efn.client.render.custom.SpaceTrailRenderType;
import com.hm.efn.client.render.custom.SubMaskRenderType;
import com.hm.efn.client.render.custom.SubSpaceRenderType;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EFNRenderType {
   public static final ResourceLocation WHITE = ResourceLocation.fromNamespaceAndPath("efn", "textures/particle/white.png");
   private static final int MAX_CACHED_RENDER_TYPES = 256;
   public static final Map<ResourceLocation, BloomParticleRenderType> BloomRenderTypes = new LinkedHashMap<ResourceLocation, BloomParticleRenderType>(
      16, 0.75F, true
   ) {
      @Override
      protected boolean removeEldestEntry(Entry<ResourceLocation, BloomParticleRenderType> eldest) {
         return this.size() > 256;
      }
   };
   public static final Map<ResourceLocation, EFNRenderType.EFNQuadParticleRenderType> QuadRenderTypes = new LinkedHashMap<ResourceLocation, EFNRenderType.EFNQuadParticleRenderType>(
      16, 0.75F, true
   ) {
      @Override
      protected boolean removeEldestEntry(Entry<ResourceLocation, EFNRenderType.EFNQuadParticleRenderType> eldest) {
         return this.size() > 256;
      }
   };
   public static final Map<ResourceLocation, EFNRenderType.EFNTriangleParticleRenderType> TriangleRenderTypes = new LinkedHashMap<ResourceLocation, EFNRenderType.EFNTriangleParticleRenderType>(
      16, 0.75F, true
   ) {
      @Override
      protected boolean removeEldestEntry(Entry<ResourceLocation, EFNRenderType.EFNTriangleParticleRenderType> eldest) {
         return this.size() > 256;
      }
   };
   public static final ResourceLocation NoneTexture = GetTexture("none");
   public static final ParticleRenderType TRANSLUCENT = new ParticleRenderType() {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.enableDepthTest();
         RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
         RenderUtils.GLSetTexture(EFNRenderType.NoneTexture);
         return tesselator.begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      }

      @Override
      public String toString() {
         return "EFN:TRANSLUCENT";
      }
   };
   private static final int spaceIdx = 0;
   public static BloomParticleRenderType BLOOM_POSITION_COLOR_LIGHTMAP = new BloomParticleRenderType(OjangUtils.newRL("efn", "b_fpcl"), WHITE) {
      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         return ParticleRenderBridge.begin(
            tesselator,
            textureManager,
            this.texture,
            this::getShader,
            this.getPipeline(),
            Mode.QUADS,
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
            false,
            false
         );
      }

      public void setupBufferBuilder(BufferBuilder bufferBuilder) {
      }

      protected ShaderInstance getShader() {
         return GameRenderer.getPositionColorLightmapShader();
      }
   };
   public static SpaceBrokenRenderType SpaceBroken1 = new SpaceBrokenRenderType(OjangUtils.newRL("efn", "space_broken"), 0);
   public static SpaceBrokenRenderType SpaceBroken2 = new SpaceBrokenRenderType(OjangUtils.newRL("efn", "space_broken"), 1);
   public static SpaceBrokenRenderType SpaceBrokenEnd = new SpaceBrokenRenderType(
      OjangUtils.newRL("efn", "space_broken_end"), GetTexture("particle/glass"), 0, 4
   );
   public static SpaceTrailRenderType SpaceBrokenTrail = new SpaceTrailRenderType(
      OjangUtils.newRL("efn", "space_broken_end"), GetTexture("particle/glass2"), 1, 4
   );
   public static MeshSpaceBrokenRenderType MeshSpaceBrokenEnd = new MeshSpaceBrokenRenderType(
      OjangUtils.newRL("efn", "space_broken_end"), GetTexture("particle/glass"), 0, 4
   );
   public static SubMaskRenderType SubMask = new SubMaskRenderType(OjangUtils.newRL("efn", "sub_mask"), GetTexture("none"));
   public static SubSpaceRenderType SubSpace_BlackHole = new SubSpaceRenderType(OjangUtils.newRL("efn", "sub_space"), GetTexture("none"));
   private static int bloomIdx = 0;
   private static int quadIdx = 0;
   private static int triangleIdx = 0;

   public static ResourceLocation GetTexture(String path) {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/" + path + ".png");
   }

   public static AirDisturbanceRenderType airDisturbanceRenderType(ResourceLocation texture) {
      return new AirDisturbanceRenderType(OjangUtils.newRL("efn", "air_trail"), texture);
   }

   public static BloomParticleRenderType getBloomRenderTypeByTexture(ResourceLocation texture) {
      if (BloomRenderTypes.containsKey(texture)) {
         return BloomRenderTypes.get(texture);
      }

      BloomParticleRenderType bloomType = new BloomParticleRenderType(OjangUtils.newRL("efn", "bp_" + bloomIdx++), texture);
      BloomRenderTypes.put(texture, bloomType);
      return bloomType;
   }

   public static BloomTrailRenderType getBloomTrailRT(ResourceLocation texture) {
      if (BloomRenderTypes.containsKey(texture)) {
         return (BloomTrailRenderType)BloomRenderTypes.get(texture);
      }

      BloomTrailRenderType bloomType = new BloomTrailRenderType(OjangUtils.newRL("efn", "bt_" + bloomIdx++), texture);
      BloomRenderTypes.put(texture, bloomType);
      return bloomType;
   }

   public static EFNRenderType.EFNQuadParticleRenderType getRenderTypeByTexture(ResourceLocation texture) {
      if (QuadRenderTypes.containsKey(texture)) {
         return QuadRenderTypes.get(texture);
      }

      EFNRenderType.EFNQuadParticleRenderType rdt = new EFNRenderType.EFNQuadParticleRenderType("efn:quad_particle_" + quadIdx++, texture);
      QuadRenderTypes.put(texture, rdt);
      return rdt;
   }

   public static EFNRenderType.EFNTriangleParticleRenderType getTriangleRenderTypeByTexture(ResourceLocation texture) {
      if (TriangleRenderTypes.containsKey(texture)) {
         return TriangleRenderTypes.get(texture);
      }

      EFNRenderType.EFNTriangleParticleRenderType rdt = new EFNRenderType.EFNTriangleParticleRenderType("efn:triangle_particle_" + triangleIdx++, texture);
      TriangleRenderTypes.put(texture, rdt);
      return rdt;
   }

   public static ShaderInstance getPositionColorLightmapShader() {
      return GameRenderer.getPositionColorLightmapShader();
   }

   public static ShaderInstance getPositionColorTexShader() {
      return GameRenderer.getPositionColorTexLightmapShader();
   }

   public static ChromaticAberrationRenderType ChromaticAberrationRenderType(ResourceLocation resourceLocation) {
      return new ChromaticAberrationRenderType(OjangUtils.newRL("efn", "chromatic_aberration"), 0.15F, 0.1F, 1.0F, 0.0F, 0.5F, resourceLocation);
   }

   public static class EFNQuadParticleRenderType implements ParticleRenderType {
      private final ResourceLocation Texture;
      private final String Name;

      public EFNQuadParticleRenderType(String name, ResourceLocation tex) {
         this.Texture = tex;
         this.Name = name;
      }

      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.setShader(GameRenderer::getParticleShader);
         if (this.Texture != null) {
            RenderUtils.GLSetTexture(this.Texture);
         }

         return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      @Override
      public String toString() {
         return this.Name;
      }
   }

   public static class EFNTriangleParticleRenderType implements ParticleRenderType {
      private final ResourceLocation Texture;
      private final String Name;

      public EFNTriangleParticleRenderType(String name, ResourceLocation tex) {
         this.Texture = tex;
         this.Name = name;
      }

      @Override
      public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
         RenderSystem.enableBlend();
         RenderSystem.enableCull();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
         if (this.Texture != null) {
            RenderUtils.GLSetTexture(this.Texture);
         }

         return tesselator.begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      }

      @Override
      public String toString() {
         return this.Name;
      }
   }
}
