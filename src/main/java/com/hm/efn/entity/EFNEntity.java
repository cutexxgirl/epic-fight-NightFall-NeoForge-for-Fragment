package com.hm.efn.entity;

import java.util.function.Supplier;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.entity.effect.BlastSummonedSwordEntity;
import com.hm.efn.entity.effect.CoTachiSlashEntity;
import com.hm.efn.entity.effect.DamoclesSwordEntity;
import com.hm.efn.entity.effect.FireWindEntity;
import com.hm.efn.entity.effect.HeavyRainSwordEntity;
import com.hm.efn.entity.effect.SecludedEntity;
import com.hm.efn.entity.effect.SummonedSwordEntity;
import com.hm.efn.entity.effect.SummonedSwordEntity_In;
import com.hm.efn.entity.effect.SummonedSwordEntity_Out;
import com.hm.efn.entity.effect.TrailEntity;
import com.hm.efn.entity.effect.TriggerEntity;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import com.hm.efn.entity.falchion.GuardianEntity;
import com.hm.efn.entity.geoEntity.Excalibur;
import com.hm.efn.entity.geoEntity.HfBladeCharging;
import com.hm.efn.entity.geoEntity.HfBladeSlash;
import com.hm.efn.entity.geoEntity.JudgementCutNormal;
import com.hm.efn.entity.geoEntity.JudgementCutPerfect;
import com.hm.efn.entity.geoEntity.MurasamaCharging;
import com.hm.efn.entity.geoEntity.MurasamaSlash;
import com.hm.efn.entity.geoEntity.SoulHuntLightning;
import com.hm.efn.entity.skill.CrimsonSlashEntity;
import com.hm.efn.entity.skill.FalchionSkillArea;
import com.hm.efn.entity.skill.SoulHuntOrb;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFNEntity {
   public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "efn");
   public static final Supplier<EntityType<FireWindEntity>> FIREWIND = register(
      "firewind", Builder.<FireWindEntity>of(FireWindEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(64).updateInterval(1).noSave().fireImmune()
   );
   public static final Supplier<EntityType<SummonedSwordEntity>> SUMMONED_SWORD = register(
      "summoned_sword", Builder.<SummonedSwordEntity>of(SummonedSwordEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(64).updateInterval(1).noSave().fireImmune()
   );
   public static final Supplier<EntityType<SinSummonedSwordEntity>> SIN_SUMMONED_SWORD = register(
      "sin_summoned_sword", Builder.<SinSummonedSwordEntity>of(SinSummonedSwordEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(64).updateInterval(1).noSave().fireImmune()
   );
   public static final Supplier<EntityType<BlastSummonedSwordEntity>> BLAST_SUMMONED_SWORD = register(
      "blast_summoned_sword",
      Builder.<BlastSummonedSwordEntity>of(BlastSummonedSwordEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(64).updateInterval(1).noSave()
   );
   public static final Supplier<EntityType<HeavyRainSwordEntity>> HEAVY_RAIN_SUMMONED_SWORD = register(
      "heavy_rain_summoned_sword",
      Builder.<HeavyRainSwordEntity>of(HeavyRainSwordEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(64).updateInterval(1).noSave()
   );
   public static final Supplier<EntityType<DamoclesSwordEntity>> DAMOCLES_SWORD = register(
      "damocles_sword",
      Builder.<DamoclesSwordEntity>of(DamoclesSwordEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(64).updateInterval(1).noSave()
   );
   public static final Supplier<EntityType<SummonedSwordEntity_In>> SUMMONED_SWORD_IN = register(
      "summoned_sword_in", Builder.<SummonedSwordEntity_In>of(SummonedSwordEntity_In::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(64).updateInterval(1).noSave().fireImmune()
   );
   public static final Supplier<EntityType<SummonedSwordEntity_Out>> SUMMONED_SWORD_OUT = register(
      "summoned_sword_out",
      Builder.<SummonedSwordEntity_Out>of(SummonedSwordEntity_Out::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(64).updateInterval(1).noSave().fireImmune()
   );
   public static final Supplier<EntityType<SecludedEntity>> SECLUDED = register(
      "secluded", Builder.<SecludedEntity>of(SecludedEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(64).updateInterval(1).noSave().fireImmune()
   );
   public static final Supplier<EntityType<CoTachiSlashEntity>> CO_TACHI_SLASH = register(
      "co_tachi_slash", Builder.<CoTachiSlashEntity>of(CoTachiSlashEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(64).updateInterval(1).noSave().fireImmune()
   );
   public static final Supplier<EntityType<TrailEntity>> TRAIL = register(
      "trail", Builder.<TrailEntity>of(TrailEntity::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(64).updateInterval(1).noSave().fireImmune()
   );
   public static final Supplier<EntityType<SoulHuntOrb>> SOUL_HUNT_ORB = ENTITIES.register(
      "soul_hunt_orb",
      () -> Builder.<SoulHuntOrb>of(SoulHuntOrb::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(1).noSummon().noSave().build("soul_hunt_orb")
   );
   public static final Supplier<EntityType<FalchionSkillArea>> FALCHION_SKILL_AREA = ENTITIES.register(
      "falchion_skill_area",
      () -> Builder.<FalchionSkillArea>of(FalchionSkillArea::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .updateInterval(1)
         .noSummon()
         .noSave()
         .build("falchion_skill_area")
   );
   public static final Supplier<EntityType<DoppelgangerEntity>> DOPPELGANGER = ENTITIES.register(
      "doppelganger",
      () -> Builder.<DoppelgangerEntity>of(DoppelgangerEntity::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(10).build("doppelganger")
   );
   public static final Supplier<EntityType<GuardianEntity>> GUARDIAN = ENTITIES.register(
      "guardian",
      () -> Builder.<GuardianEntity>of(GuardianEntity::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(64).updateInterval(1).noSave().build("guardian")
   );
   public static final Supplier<EntityType<MurasamaSlash>> MURASAMA_SLASH = ENTITIES.register(
      "murasama_slash",
      () -> Builder.<MurasamaSlash>of(MurasamaSlash::createEntity, MobCategory.MISC)
         .sized(3.5F, 2.0F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .fireImmune()
         .build("murasama_slash")
   );
   public static final Supplier<EntityType<MurasamaCharging>> MURASAMA_CHARGING = ENTITIES.register(
      "murasama_charging",
      () -> Builder.<MurasamaCharging>of(MurasamaCharging::createEntity, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .fireImmune()
         .build("murasama_charging")
   );
   public static final Supplier<EntityType<HfBladeSlash>> HF_BLADE_SLASH = ENTITIES.register(
      "hf_blade_slash",
      () -> Builder.<HfBladeSlash>of(HfBladeSlash::createEntity, MobCategory.MISC)
         .sized(3.5F, 2.0F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .fireImmune()
         .build("hf_blade_slash")
   );
   public static final Supplier<EntityType<HfBladeCharging>> HF_BLADE_CHARGING = ENTITIES.register(
      "hf_blade_charging",
      () -> Builder.<HfBladeCharging>of(HfBladeCharging::createEntity, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .fireImmune()
         .build("hf_blade_charging")
   );
   public static final Supplier<EntityType<JudgementCutNormal>> JUDGEMENTCUT_NORMAL = ENTITIES.register(
      "judgementcut_normal",
      () -> Builder.<JudgementCutNormal>of(JudgementCutNormal::createEntity, MobCategory.MISC)
         .sized(3.0F, 4.0F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .fireImmune()
         .build("judgementcut_normal")
   );
   public static final Supplier<EntityType<JudgementCutPerfect>> JUDGEMENTCUT_PERFECT = ENTITIES.register(
      "judgementcut_perfect",
      () -> Builder.<JudgementCutPerfect>of(JudgementCutPerfect::createEntity, MobCategory.MISC)
         .sized(3.5F, 4.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .fireImmune()
         .build("judgementcut_perfect")
   );
   public static final Supplier<EntityType<SoulHuntLightning>> SOULHUNT_LIGHTNING = ENTITIES.register(
      "soulhunt_lightning",
      () -> Builder.<SoulHuntLightning>of(SoulHuntLightning::createEntity, MobCategory.MISC)
         .sized(3.5F, 4.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .fireImmune()
         .build("soulhunt_lightning")
   );
   public static final Supplier<EntityType<Excalibur>> EXCALIBUR = ENTITIES.register(
      "excalibur",
      () -> Builder.<Excalibur>of(Excalibur::createEntity, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSave()
         .fireImmune()
         .build("excalibur")
   );
   public static final Supplier<EntityType<CrimsonSlashEntity>> CRIMSON_SLASH = ENTITIES.register(
      "crimson_slash",
      () -> Builder.<CrimsonSlashEntity>of(CrimsonSlashEntity::new, MobCategory.MISC)
         .sized(3.0F, 0.4F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .fireImmune()
         .noSave()
         .build("crimson_slash")
   );
   public static final Supplier<EntityType<TriggerEntity>> TRIGGER = ENTITIES.register(
      "trigger",
      () -> Builder.<TriggerEntity>of(TriggerEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .updateInterval(1)
         .noSummon()
         .noSave()
         .fireImmune()
         .build("trigger")
   );

   private static <T extends Entity> Supplier<EntityType<T>> register(String name, Builder<T> entityTypeBuilder) {
      return ENTITIES.register(name, () -> entityTypeBuilder.build(ResourceLocation.fromNamespaceAndPath("efn", name).toString()));
   }
}
