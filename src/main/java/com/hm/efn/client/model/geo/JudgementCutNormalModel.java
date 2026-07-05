package com.hm.efn.client.model.geo;

import com.hm.efn.entity.geoEntity.JudgementCutNormal;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class JudgementCutNormalModel extends DefaultedEntityGeoModel<JudgementCutNormal> {
   public JudgementCutNormalModel() {
      super(ResourceLocation.fromNamespaceAndPath("efn", "judgement_cut_normal"));
   }

   public RenderType getRenderType(JudgementCutNormal animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(this.getTextureResource(animatable));
   }

   public ResourceLocation getTextureResource(JudgementCutNormal animatable) {
      return super.getTextureResource(animatable);
   }
}
