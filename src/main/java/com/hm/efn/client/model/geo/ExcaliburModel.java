package com.hm.efn.client.model.geo;

import com.hm.efn.entity.geoEntity.Excalibur;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ExcaliburModel extends DefaultedEntityGeoModel<Excalibur> {
   public ExcaliburModel() {
      super(ResourceLocation.fromNamespaceAndPath("efn", "excalibur"));
   }

   public RenderType getRenderType(Excalibur animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(this.getTextureResource(animatable));
   }

   public ResourceLocation getTextureResource(Excalibur animatable) {
      return super.getTextureResource(animatable);
   }
}
