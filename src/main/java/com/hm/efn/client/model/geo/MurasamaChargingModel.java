package com.hm.efn.client.model.geo;

import com.hm.efn.entity.geoEntity.MurasamaCharging;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MurasamaChargingModel extends DefaultedEntityGeoModel<MurasamaCharging> {
   public MurasamaChargingModel() {
      super(ResourceLocation.fromNamespaceAndPath("efn", "murasama_charging"));
   }

   public RenderType getRenderType(MurasamaCharging animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(this.getTextureResource(animatable));
   }

   public ResourceLocation getTextureResource(MurasamaCharging animatable) {
      return super.getTextureResource(animatable);
   }
}
