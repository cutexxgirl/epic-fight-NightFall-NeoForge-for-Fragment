package com.hm.efn.client.model.geo;

import com.hm.efn.entity.geoEntity.HfBladeSlash;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HfBladeSlashModel extends DefaultedEntityGeoModel<HfBladeSlash> {
   public HfBladeSlashModel() {
      super(ResourceLocation.fromNamespaceAndPath("efn", "hf_blade_slash"));
   }

   public RenderType getRenderType(HfBladeSlash animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(this.getTextureResource(animatable));
   }

   public ResourceLocation getTextureResource(HfBladeSlash animatable) {
      return super.getTextureResource(animatable);
   }
}
