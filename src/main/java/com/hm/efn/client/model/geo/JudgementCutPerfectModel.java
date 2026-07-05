package com.hm.efn.client.model.geo;

import com.hm.efn.entity.geoEntity.JudgementCutPerfect;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class JudgementCutPerfectModel extends DefaultedEntityGeoModel<JudgementCutPerfect> {
   public JudgementCutPerfectModel() {
      super(ResourceLocation.fromNamespaceAndPath("efn", "judgement_cut_perfect"));
   }

   public RenderType getRenderType(JudgementCutPerfect animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(this.getTextureResource(animatable));
   }

   public ResourceLocation getTextureResource(JudgementCutPerfect animatable) {
      return super.getTextureResource(animatable);
   }
}
