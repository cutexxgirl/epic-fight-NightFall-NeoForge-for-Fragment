package com.hm.efn.client.entity.gecko;

import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.hm.efn.client.render.custom.BloomParticleRenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class GeoBloomEntityRenderer<T extends Entity & GeoAnimatable> extends PostGeoEntityRenderer<T> {
   public GeoBloomEntityRenderer(Context renderManager, GeoModel<T> model) {
      super(renderManager, model);
   }

   @Override
   public Pipeline getPipeline() {
      return BloomParticleRenderType.ppl;
   }
}
