package com.hm.efn.client.renderer.geoItem;

import com.hm.efn.item.geo.ExcaliburItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ExcaliburItemRenderer extends GeoItemRenderer<ExcaliburItem> {
   private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath("efn", "animations/excalibur.animation.json");

   public ExcaliburItemRenderer() {
      super(new DefaultedItemGeoModel(ResourceLocation.fromNamespaceAndPath("efn", "excalibur")));
   }

   public ResourceLocation getTextureLocation(ExcaliburItem excalibur) {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/item/weapon/excalibur.png");
   }

   public ResourceLocation getAnimationFileLocation(ExcaliburItem excalibur) {
      return this.animations;
   }
}
