package com.hm.efn.client.model.geo;

import com.hm.efn.entity.geoEntity.SoulHuntLightning;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SoulHuntLightningModel extends DefaultedEntityGeoModel<SoulHuntLightning> {
   public SoulHuntLightningModel() {
      super(ResourceLocation.fromNamespaceAndPath("efn", "soulhunt_lightning"));
   }

   public RenderType getRenderType(SoulHuntLightning animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(this.getTextureResource(animatable));
   }

   public ResourceLocation getTextureResource(SoulHuntLightning animatable) {
      return super.getTextureResource(animatable);
   }
}
