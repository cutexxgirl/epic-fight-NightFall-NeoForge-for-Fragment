package com.hm.efn.client.model;

import com.guhao.vix.client.NoTextureJsonModel;
import com.guhao.vix.util.OjangUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ACGModel {
   public static NoTextureJsonModel SpaceBrokenModel;
   public static NoTextureJsonModel Sphere;
   public static NoTextureJsonModel BlackHoleEdge;

   public static void LoadOtherModel() {
      SpaceBrokenModel = NoTextureJsonModel.loadFromJson(OjangUtils.newRL("efn", "models/effect/spacebroken.json"));
      Sphere = NoTextureJsonModel.loadFromJson(OjangUtils.newRL("efn", "models/effect/sphere.json"));
      BlackHoleEdge = NoTextureJsonModel.loadFromJson(OjangUtils.newRL("efn", "models/effect/blackhole_edge.json"));
   }
}
