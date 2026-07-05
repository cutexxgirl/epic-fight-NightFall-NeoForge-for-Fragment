package com.guhao.vix.client.pipeline;

import net.minecraft.resources.ResourceLocation;

public abstract class PostMeshParticleRenderType extends PostParticleRenderType {
   static ResourceLocation tempTarget = ResourceLocation.parse("vix:depth_cull_temp2");

   public PostMeshParticleRenderType(ResourceLocation renderTypeID, ResourceLocation texture) {
      super(renderTypeID, texture);
   }
}
