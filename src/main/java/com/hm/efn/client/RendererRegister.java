package com.hm.efn.client;

import com.hm.efn.client.renderer.RenderFalchion;
import com.hm.efn.client.renderer.RenderItemEmissive;
import com.hm.efn.client.renderer.RenderJudgementCutEnd;
import com.hm.efn.client.renderer.RenderMortalBlade;
import com.hm.efn.client.renderer.RenderMurasama;
import com.hm.efn.client.renderer.RenderYamato_DMC;
import com.merlin204.avalon.entity.client.renderer.patch.item.RenderAnimationItem;
import com.merlin204.avalon.entity.client.renderer.patch.item.RenderChangeMeshItem;
import com.merlin204.avalon.entity.client.renderer.patch.item.RenderMeshItem;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent.Item;

public class RendererRegister {
   @OnlyIn(Dist.CLIENT)
   public static void registerRenderers(Item event) {
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("efn", "mesh_item"), RenderYamato_DMC::new);
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("efn", "mesh_item_murasama"), RenderMurasama::new);
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("efn", "mesh_item_mortalblade"), RenderMortalBlade::new);
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("efn", "mesh_item_falchion"), RenderFalchion::new);
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("efn", "emissive_render"), RenderItemEmissive::new);
      event.addItemRenderer(ResourceLocation.withDefaultNamespace("judgement_cut_end"), RenderJudgementCutEnd::new);
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("epic_fight_avalon", "mesh_item"), RenderMeshItem::new);
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("epic_fight_avalon", "animation_item"), RenderAnimationItem::new);
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("epic_fight_avalon", "change_mesh_item"), RenderChangeMeshItem::new);
   }
}
