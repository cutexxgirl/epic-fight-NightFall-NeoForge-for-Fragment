package com.hm.efn.client.particle.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;

public abstract class TexturedSkinModelParticle extends SkinModelParticle<SkinnedMesh> {
   protected final ResourceLocation texture;

   public TexturedSkinModelParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      AssetAccessor<SkinnedMesh> particleMeshProvider,
      ResourceLocation texture
   ) {
      super(level, x, y, z, xd, yd, zd, particleMeshProvider);
      this.texture = texture;
   }

   @Override
   public void prepareDraw(PoseStack poseStack, float partialTicks) {
      RenderSystem.setShaderTexture(0, this.texture);
      RenderSystem.enableBlend();
   }
}
