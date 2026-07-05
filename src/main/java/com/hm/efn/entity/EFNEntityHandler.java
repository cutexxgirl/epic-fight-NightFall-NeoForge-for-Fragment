package com.hm.efn.entity;

import com.hm.efn.client.model.EFNArmatures;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.entity.doppelganger.DoppelgangerPatch;
import com.hm.efn.entity.doppelganger.DoppelgangerRender;
import com.hm.efn.entity.effect.CoTachiSlashEntity;
import com.hm.efn.entity.effect.CoTachiSlashPatch;
import com.hm.efn.entity.effect.FireWindEntity;
import com.hm.efn.entity.effect.FireWindPatch;
import com.hm.efn.entity.effect.SecludedEntity;
import com.hm.efn.entity.effect.SecludedPatch;
import com.hm.efn.entity.effect.SummonedSwordEntity;
import com.hm.efn.entity.effect.SummonedSwordEntity_In;
import com.hm.efn.entity.effect.SummonedSwordEntity_Out;
import com.hm.efn.entity.effect.SummonedSwordPatch;
import com.hm.efn.entity.effect.SummonedSwordPatch_In;
import com.hm.efn.entity.effect.SummonedSwordPatch_Out;
import com.hm.efn.entity.effect.TrailEntity;
import com.hm.efn.entity.effect.TrailPatch;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordPatch;
import com.hm.efn.entity.falchion.GuardianEntity;
import com.hm.efn.entity.falchion.GuardianPatch;
import com.hm.efn.entity.falchion.GuardianRender;
import com.hm.efn.entity.geoEntity.Excalibur;
import com.hm.efn.entity.geoEntity.HfBladeCharging;
import com.hm.efn.entity.geoEntity.HfBladeSlash;
import com.hm.efn.entity.geoEntity.JudgementCutNormal;
import com.hm.efn.entity.geoEntity.JudgementCutPerfect;
import com.hm.efn.entity.geoEntity.MurasamaCharging;
import com.hm.efn.entity.geoEntity.MurasamaSlash;
import com.hm.efn.entity.geoEntity.SoulHuntLightning;
import com.hm.efn.entity.renderer.DoppelgangerDarkRenderer;
import com.hm.efn.entity.renderer.EntityEmptyRenderer;
import com.hm.efn.entity.renderer.GuardianDarkRenderer;
import com.merlin204.avalon.entity.client.renderer.EmptyRenderer;
import com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonRendererPatch;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent.AddEntity;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class EFNEntityHandler {
   public static void handleEntityPatchRegistry(EntityPatchRegistryEvent event) {
      event.registerEntityPatch((EntityType<FireWindEntity>)EFNEntity.FIREWIND.get(), FireWindPatch::new);
      event.registerEntityPatch((EntityType<SecludedEntity>)EFNEntity.SECLUDED.get(), SecludedPatch::new);
      event.registerEntityPatch((EntityType<CoTachiSlashEntity>)EFNEntity.CO_TACHI_SLASH.get(), CoTachiSlashPatch::new);
      event.registerEntityPatch((EntityType<DoppelgangerEntity>)EFNEntity.DOPPELGANGER.get(), DoppelgangerPatch::new);
      event.registerEntityPatch((EntityType<GuardianEntity>)EFNEntity.GUARDIAN.get(), GuardianPatch::new);
      event.registerEntityPatch((EntityType<SummonedSwordEntity>)EFNEntity.SUMMONED_SWORD.get(), SummonedSwordPatch::new);
      event.registerEntityPatch((EntityType<SinSummonedSwordEntity>)EFNEntity.SIN_SUMMONED_SWORD.get(), SinSummonedSwordPatch::new);
      event.registerEntityPatch((EntityType<SummonedSwordEntity_Out>)EFNEntity.SUMMONED_SWORD_OUT.get(), SummonedSwordPatch_Out::new);
      event.registerEntityPatch((EntityType<SummonedSwordEntity_In>)EFNEntity.SUMMONED_SWORD_IN.get(), SummonedSwordPatch_In::new);
      event.registerEntityPatch((EntityType<TrailEntity>)EFNEntity.TRAIL.get(), TrailPatch::new);
   }

   @SubscribeEvent
   public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
      event.put((EntityType)EFNEntity.FIREWIND.get(), FireWindEntity.getDefaultAttribute());
      event.put((EntityType)EFNEntity.SECLUDED.get(), SecludedEntity.getDefaultAttribute());
      event.put((EntityType)EFNEntity.CO_TACHI_SLASH.get(), CoTachiSlashEntity.getDefaultAttribute());
      event.put((EntityType)EFNEntity.DOPPELGANGER.get(), DoppelgangerEntity.getDefaultAttribute());
      event.put((EntityType)EFNEntity.GUARDIAN.get(), GuardianEntity.getDefaultAttribute());
      event.put((EntityType)EFNEntity.SUMMONED_SWORD.get(), SummonedSwordEntity.getDefaultAttribute());
      event.put((EntityType)EFNEntity.SIN_SUMMONED_SWORD.get(), SinSummonedSwordEntity.getDefaultAttribute());
      event.put((EntityType)EFNEntity.SUMMONED_SWORD_OUT.get(), SummonedSwordEntity_Out.getDefaultAttribute());
      event.put((EntityType)EFNEntity.SUMMONED_SWORD_IN.get(), SummonedSwordEntity_In.getDefaultAttribute());
      event.put((EntityType)EFNEntity.TRAIL.get(), TrailEntity.getDefaultAttribute());
      event.put((EntityType)EFNEntity.MURASAMA_SLASH.get(), MurasamaSlash.getDefaultAttribute());
      event.put((EntityType)EFNEntity.MURASAMA_CHARGING.get(), MurasamaCharging.getDefaultAttribute());
      event.put((EntityType)EFNEntity.HF_BLADE_SLASH.get(), HfBladeSlash.getDefaultAttribute());
      event.put((EntityType)EFNEntity.HF_BLADE_CHARGING.get(), HfBladeCharging.getDefaultAttribute());
      event.put((EntityType)EFNEntity.EXCALIBUR.get(), Excalibur.getDefaultAttribute());
      event.put((EntityType)EFNEntity.JUDGEMENTCUT_NORMAL.get(), JudgementCutNormal.getDefaultAttribute());
      event.put((EntityType)EFNEntity.JUDGEMENTCUT_PERFECT.get(), JudgementCutPerfect.getDefaultAttribute());
      event.put((EntityType)EFNEntity.SOULHUNT_LIGHTNING.get(), SoulHuntLightning.getDefaultAttribute());
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void handleClientSetup(FMLClientSetupEvent event) {
      EntityRenderers.register((EntityType)EFNEntity.FIREWIND.get(), EmptyRenderer::new);
      EntityRenderers.register((EntityType)EFNEntity.SECLUDED.get(), EmptyRenderer::new);
      EntityRenderers.register((EntityType)EFNEntity.CO_TACHI_SLASH.get(), EmptyRenderer::new);
      EntityRenderers.register((EntityType)EFNEntity.DOPPELGANGER.get(), DoppelgangerRender::new);
      EntityRenderers.register((EntityType)EFNEntity.GUARDIAN.get(), GuardianRender::new);
      EntityRenderers.register((EntityType)EFNEntity.SUMMONED_SWORD.get(), EmptyRenderer::new);
      EntityRenderers.register((EntityType)EFNEntity.SIN_SUMMONED_SWORD.get(), EmptyRenderer::new);
      EntityRenderers.register((EntityType)EFNEntity.SUMMONED_SWORD_OUT.get(), EmptyRenderer::new);
      EntityRenderers.register((EntityType)EFNEntity.SUMMONED_SWORD_IN.get(), EmptyRenderer::new);
      EntityRenderers.register((EntityType)EFNEntity.TRAIL.get(), EmptyRenderer::new);
      EntityRenderers.register((EntityType)EFNEntity.TRIGGER.get(), EntityEmptyRenderer::new);
   }

   @OnlyIn(Dist.CLIENT)
   public static void handlePatchedRenderers(AddEntity event) {
      Context context = event.getContext();
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.FIREWIND.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.SECLUDED.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.CO_TACHI_SLASH.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.SUMMONED_SWORD.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.SIN_SUMMONED_SWORD.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.SUMMONED_SWORD_OUT.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.SUMMONED_SWORD_IN.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.TRAIL.get(), entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.DOPPELGANGER.get(),
         entityType -> new DoppelgangerDarkRenderer(Meshes.BIPED, context, entityType).initLayerLast(context, entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)EFNEntity.GUARDIAN.get(), entityType -> new GuardianDarkRenderer(Meshes.BIPED, context, entityType).initLayerLast(context, entityType)
      );
   }

   @SubscribeEvent
   public static void onCommonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(EFNArmatures::registerArmatures);
   }
}
