package com.hm.efn.client.model.geo;

import com.hm.efn.entity.geoEntity.HfBladeCharging;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HfBladeChargingModel extends DefaultedEntityGeoModel<HfBladeCharging> {
   public HfBladeChargingModel() {
      super(ResourceLocation.fromNamespaceAndPath("efn", "hf_blade_charging"));
   }

   public RenderType getRenderType(HfBladeCharging animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(this.getTextureResource(animatable));
   }

   public ResourceLocation getTextureResource(HfBladeCharging animatable) {
      return super.getTextureResource(animatable);
   }
}
