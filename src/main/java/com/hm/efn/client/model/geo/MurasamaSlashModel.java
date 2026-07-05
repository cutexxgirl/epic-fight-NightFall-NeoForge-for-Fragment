package com.hm.efn.client.model.geo;

import com.hm.efn.entity.geoEntity.MurasamaSlash;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MurasamaSlashModel extends DefaultedEntityGeoModel<MurasamaSlash> {
   public MurasamaSlashModel() {
      super(ResourceLocation.fromNamespaceAndPath("efn", "murasama_slash"));
   }

   public RenderType getRenderType(MurasamaSlash animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(this.getTextureResource(animatable));
   }

   public ResourceLocation getTextureResource(MurasamaSlash animatable) {
      return super.getTextureResource(animatable);
   }
}
