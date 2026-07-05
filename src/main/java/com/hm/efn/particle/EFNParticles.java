package com.hm.efn.particle;

import java.util.function.Supplier;
import com.hm.efn.client.particle.effect.Abstract3DParticleOptions;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.particle.HitParticleType;

public class EFNParticles {
   public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, "efn");
   public static final DeferredHolder<ParticleType<?>, HitParticleType> ARC_HIT = PARTICLES.register(
      "arc_hit", () -> new HitParticleType(true, HitParticleType.MIDDLE_OF_ENTITIES, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> ARC_CUT = PARTICLES.register("arc_cut", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, HitParticleType> YAMATO_HIT_1 = PARTICLES.register(
      "yamato_hit1", () -> new HitParticleType(true, HitParticleType.CENTER_OF_TARGET, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> YAMATO_CUT_1 = PARTICLES.register("yamato_cut1", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, HitParticleType> YAMATO_HIT_2 = PARTICLES.register(
      "yamato_hit2", () -> new HitParticleType(true, HitParticleType.CENTER_OF_TARGET, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> YAMATO_CUT_2 = PARTICLES.register("yamato_cut2", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, HitParticleType> BLOOD_HIT = PARTICLES.register(
      "blood_hit", () -> new HitParticleType(true, HitParticleType.CENTER_OF_TARGET, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> BLOOD_CUT = PARTICLES.register("blood_cut", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, HitParticleType> MURASAMA_HIT = PARTICLES.register(
      "murasama_hit", () -> new HitParticleType(true, HitParticleType.CENTER_OF_TARGET, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> MURASAMA_CUT = PARTICLES.register("murasama_cut", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, HitParticleType> HF_BLADE_HIT = PARTICLES.register(
      "hf_blade_hit", () -> new HitParticleType(true, HitParticleType.CENTER_OF_TARGET, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> HF_BLADE_CUT = PARTICLES.register("hf_blade_cut", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> EXP = PARTICLES.register("exp", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> SLASH = PARTICLES.register("slash", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> SLASH_RING = PARTICLES.register("slash_ring", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> FLASH = PARTICLES.register("flash", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> COLOR_SLASH = PARTICLES.register("color_slash", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> COLOR_SLASH2 = PARTICLES.register("color_slash2", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> COLOR_RING_SLASH = PARTICLES.register("color_slash_ring", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> FIRESLASH = PARTICLES.register("fireslash", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, HitParticleType> FIRE_HIT = PARTICLES.register(
      "fire_hit", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> FIRE_CUT = PARTICLES.register("fire_cut", () -> new SimpleParticleType(true));
   public static final Supplier<ParticleType<Abstract3DParticleOptions>> CRIMSON_SLASH = PARTICLES.register(
      "crimson_slash", () -> new ParticleType<Abstract3DParticleOptions>(true) {
         @NotNull
         public MapCodec<Abstract3DParticleOptions> codec() {
            return Abstract3DParticleOptions.codec(this);
         }

         @NotNull
         public StreamCodec<? super RegistryFriendlyByteBuf, Abstract3DParticleOptions> streamCodec() {
            return Abstract3DParticleOptions.streamCodec(this);
         }
      }
   );
   public static final Supplier<SimpleParticleType> BLOOM_TRAIL = PARTICLES.register("bloom_trail", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> SPACE_TRAIL = PARTICLES.register("space_trail", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> AIR_TRAIL = PARTICLES.register("air_trail", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> DMC_JC_BLADE_TRAIL = PARTICLES.register("dmc_jc_blade_trail", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> DMC_JC_BLADE_TRAIL_RED = PARTICLES.register(
      "dmc_jc_blade_trail_red", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> DMC_JC_BLADE_TRAIL_BLUE = PARTICLES.register(
      "dmc_jc_blade_trail_blue", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> AIR_WAVE = PARTICLES.register("air_wave", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> JUDGEMENT_CUT_PARTICLE = PARTICLES.register(
      "judgement_cut_particle", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> JUDGEMENT_CUT_PARTICLE_RED = PARTICLES.register(
      "judgement_cut_particle_red", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> JUDGEMENT_CUT_PARTICLE_BLUE = PARTICLES.register(
      "judgement_cut_particle_blue", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> YAMATO_SPHERE = PARTICLES.register("yamato_sphere", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> YAMATO_LAST_SPHERE = PARTICLES.register("yamato_last_sphere", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> YAMATO_FLOOR = PARTICLES.register("yamato_floor", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> BLOCK = PARTICLES.register("block", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> SOUL_AFTERIMAGE = PARTICLES.register("soul_afterimage", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> NOWEAPON_AFTERIMAGE = PARTICLES.register("noweapon_afterimage", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> NOWEAPON_AFTERIMAGE_RED = PARTICLES.register(
      "noweapon_afterimage_red", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> NOWEAPON_AFTERIMAGE_BLUE = PARTICLES.register(
      "noweapon_afterimage_blue", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> NOWEAPON_AFTERIMAGE_BLUE_SHORT = PARTICLES.register(
      "noweapon_afterimage_blue_short", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, HitParticleType> EFN_PARRY_FLASH_MAIN = PARTICLES.register("efn_parry_flash_main", () -> new HitParticleType(true));
   public static final Supplier<SimpleParticleType> EFN_PARRY_FLASH_MAIN_RENDER = PARTICLES.register(
      "efn_parry_flash_main_render", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> SPARK_EXPANSIVE = PARTICLES.register("spark_expansive", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> SPARK_CONTRACTILE = PARTICLES.register("spark_contractive", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> NORMAL_SPARK = PARTICLES.register("spark_normal", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, HitParticleType> ALL_SPARK = PARTICLES.register(
      "all_spark", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> SPARK_EXPANSIVE_ZANDATSU = PARTICLES.register(
      "spark_expansive_zandatsu", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> SPARK_CONTRACTILE_ZANDATSU = PARTICLES.register(
      "spark_contractive_zandatsu", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> NORMAL_SPARK_ZANDATSU = PARTICLES.register(
      "spark_normal_zandatsu", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, HitParticleType> ALL_SPARK_ZANDATSU = PARTICLES.register(
      "all_spark_zandatsu", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> MORTAL_BLADE = PARTICLES.register("mortal_blade", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> MORTAL_BLADE_CHARGE_BLACK = PARTICLES.register(
      "mortal_blade_charge_black", () -> new SimpleParticleType(true)
   );
   public static final Supplier<SimpleParticleType> MORTAL_BLADE_CHARGE_RED = PARTICLES.register(
      "mortal_blade_charge_red", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, HitParticleType> BLOOD_HARVEST = PARTICLES.register(
      "blood_harvest", () -> new HitParticleType(true, HitParticleType.MIDDLE_OF_ENTITIES, HitParticleType.ZERO)
   );
   public static final DeferredHolder<ParticleType<?>, HitParticleType> BLOOD_BRUST = PARTICLES.register(
      "blood_brust", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO)
   );
   public static final Supplier<SimpleParticleType> SAKURA_DANCE = PARTICLES.register("sakura_dance", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> TRIGGER = PARTICLES.register("trigger", () -> new SimpleParticleType(true));
   public static final Supplier<SimpleParticleType> WEAPON_ATTACH_EXAMPLE = PARTICLES.register(
      "weapon_attach_example", () -> new SimpleParticleType(true)
   );
}
