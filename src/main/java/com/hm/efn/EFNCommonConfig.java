package com.hm.efn;

import com.google.common.collect.Lists;
import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;

public class EFNCommonConfig {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC;
   public static final BooleanValue ENABLE_ANIMATION_PLAYSPEED_LOCK = BUILDER.comment("Enable maximum animation speed limit")
      .define("enable_animation_playspeed_lock", true);
   public static final ConfigValue<List<? extends String>> YAMATO_STUNANIMATION_BLACKLIST = BUILDER.comment(
         new String[]{
            "Entities that won't play Yamato's stun animations (format: modid:entity_id)",
            "This is a SERVER-SIDE setting",
            "Example: [\"minecraft:wither\", \"minecraft:ender_dragon\"]"
         }
      )
      .translation("efn.config.animation_blacklist")
      .defineList("animationBlacklist", Lists.newArrayList(new String[]{"minecraft:wither", "minecraft:ender_dragon"}), obj -> obj instanceof String);
   public static final ConfigValue<List<? extends String>> YAMATO_STUNANIMATION_WHITELIST = BUILDER.comment(
         new String[]{
            "Entities that can play Yamato's stun animations (format: modid:entity_id)",
            "This is a SERVER-SIDE setting",
            "Example: [\"minecraft:player\", \"minecraft:zombie\", \"minecraft:skeleton\"]"
         }
      )
      .translation("efn.config.animation_whitelist")
      .defineList(
         "animationWhitelist",
         Lists.newArrayList(new String[]{"wom:evil_skeleton", "wom:hollow", "wom:saulomonk", "wom:lycanth"}),
         obj -> obj instanceof String
      );
   public static final ConfigValue<List<? extends String>> YAMATO_GLOBALSTUN_WHITELIST = BUILDER.comment(
         new String[]{
            "Entities that can be Global Focus stunned by Yamato successful parry + attack combo (format: modid:entity_id)",
            "These entities will receive YamatoStunEffect when hit",
            "This is a SERVER-SIDE setting",
            "Use 'modid:all' to include all entities from a mod",
            "Example: [\"minecraft:zombie\", \"cataclysm:all\", \"iceandfire:all\"]"
         }
      )
      .translation("efn.config.yamato_globalstun_whitelist")
      .defineList("yamatoStunWhitelist", Lists.newArrayList(new String[]{"cataclysm:all", "wom:all"}), obj -> obj instanceof String);
   public static final ConfigValue<List<? extends String>> MURASAMA_STUNANIMATION_BLACKLIST = BUILDER.comment(
         new String[]{
            "Entities that won't play HfBlade's stun animations (format: modid:entity_id)",
            "This is a SERVER-SIDE setting",
            "Example: [\"minecraft:wither\", \"minecraft:ender_dragon\"]"
         }
      )
      .translation("efn.config.animation_blacklist")
      .defineList("animationBlacklist", Lists.newArrayList(new String[]{"minecraft:wither", "minecraft:ender_dragon"}), obj -> obj instanceof String);
   public static final ConfigValue<List<? extends String>> MURASAMA_STUNANIMATION_WHITELIST = BUILDER.comment(
         new String[]{
            "Entities that can play HfBlade's stun animations (format: modid:entity_id)",
            "This is a SERVER-SIDE setting",
            "Example: [\"minecraft:player\", \"minecraft:zombie\", \"minecraft:skeleton\"]"
         }
      )
      .translation("efn.config.animation_whitelist")
      .defineList(
         "animationWhitelist",
         Lists.newArrayList(new String[]{"wom:evil_skeleton", "wom:hollow", "wom:saulomonk", "wom:lycanth"}),
         obj -> obj instanceof String
      );
   public static final IntValue YAMATO_PARRY_STUN_DURATION = BUILDER.comment(
         new String[]{"Duration of stun effect applied after successful parry + attack combo (in ticks, 20 ticks = 1 second)", "This is a SERVER-SIDE setting"}
      )
      .translation("efn.config.yamato_parry_stun_duration")
      .defineInRange("yamatoParryStunDuration", 20, 1, 200);
   public static final IntValue YAMATO_PARRY_STUN_COOLDOWN = BUILDER.comment(
         new String[]{"Cooldown between parry stuns (in ticks, 20 ticks = 1 second)", "This is a SERVER-SIDE setting"}
      )
      .translation("efn.config.yamato_parry_stun_cooldown")
      .defineInRange("yamatoParryStunCooldown", 60, 1, 1200);
   public static final ConfigValue<List<? extends String>> YAMATO_CATCHER_BLACKLIST = BUILDER.comment(
         new String[]{
            "Entities that CANNOT be caught by Yamato Catcher (format: modid:entity_id)",
            "These entities will be ignored regardless of knockback resistance",
            "This is a SERVER-SIDE setting",
            "Example: [\"minecraft:wither\", \"minecraft:ender_dragon\"]"
         }
      )
      .translation("efn.config.yamato_catcher_blacklist")
      .defineList("yamatoCatcherBlacklist", Lists.newArrayList(new String[]{"minecraft:wither", "minecraft:ender_dragon"}), obj -> obj instanceof String);
   public static final ConfigValue<List<? extends String>> YAMATO_CATCHER_WHITELIST = BUILDER.comment(
         new String[]{
            "Entities that CAN be caught by Yamato Catcher (format: modid:entity_id)",
            "These entities can be caught regardless of knockback resistance",
            "This is a SERVER-SIDE setting",
            "Example: [\"minecraft:player\", \"minecraft:zombie\"]"
         }
      )
      .translation("efn.config.yamato_catcher_whitelist")
      .defineList(
         "yamatoCatcherWhitelist",
         Lists.newArrayList(
            new String[]{
               "minecraft:zombie",
               "minecraft:drowned",
               "minecraft:husk",
               "minecraft:skeleton",
               "minecraft:wither_skeleton",
               "minecraft:stray",
               "minecraft:bogged",
               "minecraft:Pillager",
               "minecraft:evoker",
               "minecraft:vindicator",
               "minecraft:blaze",
               "minecraft:piglin",
               "minecraft:zombified_piglin",
               "minecraft:enderman"
            }
         ),
         obj -> obj instanceof String
      );
   public static final BooleanValue ENABLE_YAMATO_PARRY_TIMESLOWDOWN = BUILDER.comment(
         new String[]{
            "Yamato parry timeslowdown", "Disable time slowdown effect that Yamato parry", "This is a SERVER-SIDE setting", "Default: true (enabled)"
         }
      )
      .translation("efn.config.enable_yamato_parry_timeslowdown")
      .define("enableYamatoParryTimeSlowdown", true);
   public static final IntValue YAMATO_PARRY_TIMESLOW_DELAY_BEGIN = BUILDER.comment(
         new String[]{
            "Delay before time slowdown effect starts (in ticks)",
            "20 ticks = 1 second. This creates a brief pause before the slowdown",
            "This is a SERVER-SIDE setting",
            "Default: 5 ticks (0.25 seconds)"
         }
      )
      .translation("efn.config.yamato_parry_timeslow_delay")
      .defineInRange("yamatoParryTimeSlowDelay", 5, 0, 40);
   public static final IntValue YAMATO_PARRY_TIMESLOW_DELAY_END = BUILDER.comment(
         new String[]{
            "End tick of time slowdown effect (in ticks)",
            "20 ticks = 1 second. When the time slowdown end",
            "This is a SERVER-SIDE setting",
            "Default: 6 ticks"
         }
      )
      .translation("efn.config.yamato_parry_timeslow_duration")
      .defineInRange("yamatoParryTimeSlowDuration", 6, 1, 100);
   public static final IntValue YAMATO_PARRY_TIMESLOW_AMPLIFIER = BUILDER.comment(
         new String[]{
            "Strength level of time slowdown effect",
            "1 = 1% speed, 2 = 2% speed, 3 = 3% speed, 4 = 4% speed, 5 = 5% speed",
            "This is a SERVER-SIDE setting",
            "Default: 3"
         }
      )
      .translation("efn.config.yamato_parry_timeslow_amplifier")
      .defineInRange("yamatoParryTimeSlowAmplifier", 3, 1, 20);
   public static final BooleanValue ENABLE_DUALSOWRD_DODGE_TIMESLOWDOWN = BUILDER.comment(
         new String[]{
            "Dualsword dodge timeslowdown", "Disable time slowdown effect that Dualsword dodge", "This is a SERVER-SIDE setting", "Default: true (enabled)"
         }
      )
      .translation("efn.config.enable_dualsword_dodge_timeslowdown")
      .define("enableDualswordDodgeTimeSlowdown", true);
   public static final IntValue DUALSOWRD_DODGE_TIMESLOW_DELAY_BEGIN = BUILDER.comment(
         new String[]{
            "Delay before time slowdown effect starts (in ticks)",
            "20 ticks = 1 second. This creates a brief pause before the slowdown",
            "This is a SERVER-SIDE setting",
            "Default: 4 ticks"
         }
      )
      .translation("efn.config.dualsword_dodge_timeslow_delay")
      .defineInRange("DualswordDodgeTimeSlowDelay", 4, 0, 100);
   public static final IntValue DUALSOWRD_DODGE_TIMESLOW_DELAY_END = BUILDER.comment(
         new String[]{
            "End time of slowdown effect (in ticks)", "20 ticks = 1 second. When the time slowdown end", "This is a SERVER-SIDE setting", "Default: 7 ticks"
         }
      )
      .translation("efn.config.dualsword_dodge_timeslow_duration")
      .defineInRange("DualswordDodgeTimeSlowDuration", 7, 1, 100);
   public static final IntValue DUALSOWRD_DODGE_TIMESLOW_AMPLIFIER = BUILDER.comment(
         new String[]{
            "Strength level of time slowdown effect",
            "1 = 1% speed, 2 = 2% speed, 3 = 3% speed, 4 = 4% speed, 5 = 5% speed",
            "This is a SERVER-SIDE setting",
            "Default: 5"
         }
      )
      .translation("efn.config.dualsword_dodge_timeslow_amplifier")
      .defineInRange("DualswordDodgeTimeSlowAmplifier", 5, 1, 20);
   public static final DoubleValue JUDGEMENT_CUT_END_DAMAGE_RATE = BUILDER.comment(
         new String[]{"Base damage rate for Judgement Cut End", "This multiplies the base damage calculation", "This is a SERVER-SIDE setting", "Default: 2.0"}
      )
      .translation("efn.config.judgement_cut_end_damage_rate")
      .defineInRange("judgementCutEndDamageRate", 2.0, 0.1, 100.0);
   public static final DoubleValue JUDGEMENT_CUT_END_CUT_RATE = BUILDER.comment(
         new String[]{
            "Cut rate for Judgement Cut End (percentage of max health)",
            "This determines how much percentage damage is dealt based on target's max health",
            "This is a SERVER-SIDE setting",
            "Default: 0.4 (40%)"
         }
      )
      .translation("efn.config.judgement_cut_end_cut_rate")
      .defineInRange("judgementCutEndCutRate", 0.4, 0.01, 1.0);
   public static final DoubleValue JUDGEMENT_CUT_END_BOSS_DAMAGE_MULTIPLIER = BUILDER.comment(
         new String[]{
            "Damage multiplier against Cataclysm bosses",
            "This multiplies the damage when attacking Cataclysm bosses",
            "This is a SERVER-SIDE setting",
            "Default: 1.0 (no multiplier)"
         }
      )
      .translation("efn.config.judgement_cut_end_boss_damage_multiplier")
      .defineInRange("judgementCutEndBossDamageMultiplier", 1.0, 0.1, 10.0);
   public static final DoubleValue JUDGEMENT_CUT_END_HEALTH_PERCENTAGE = BUILDER.comment(
         new String[]{
            "Health percentage used in damage calculation",
            "This is the percentage of max health used in the formula: cutRate * (maxHealth * healthPercentage)",
            "This is a SERVER-SIDE setting",
            "Default: 0.4 (40%)"
         }
      )
      .translation("efn.config.judgement_cut_end_health_percentage")
      .defineInRange("judgementCutEndHealthPercentage", 0.4, 0.01, 1.0);
   public static final IntValue YAMATO_REWARD_KILL_THRESHOLD = BUILDER.comment(
         new String[]{
            "Number of kills required to receive the Yamato JudgementCut-End reward",
            "Set to 0 to disable the reward system",
            "This is a SERVER-SIDE setting",
            "Default: 100 kills"
         }
      )
      .translation("efn.config.yamato_reward_kill_threshold")
      .defineInRange("yamatoRewardKillThreshold", 100, 0, 10000);
   public static final BooleanValue YAMATO_REWARD_ENABLED = BUILDER.comment(
         new String[]{
            "Enable/disable the skill book reward for Yamato JudgementCut-End",
            "When enabled, players will receive JudgementCut-End skill book after reaching the kill threshold",
            "This is a SERVER-SIDE setting",
            "Default: true (enabled)"
         }
      )
      .translation("efn.config.yamato_reward_enabled")
      .define("yamatoRewardEnabled", true);
   public static final BooleanValue MURASAMA_DOUBLETAP_SPRINT_COMPATMODE = BUILDER.comment(
         new String[]{
            "Enable/disable the compatmode for HfBlade's double tap Sprint conditon",
            "When enabled, HfBlade's double tap Sprint conditon will always be true(not require to double tap Sprint",
            "Default: false (disabled)"
         }
      )
      .define("murasamaDoubleTapSprintConditoCompat", false);
   public static final BooleanValue HEAVY_RAIN_ENABLE_FRACTURE = BUILDER.comment(
         new String[]{
            "Enable the ground fracture effect when Heavy Rain swords hit the ground", "Disable this if you experience lag during the skill.", "Default: true"
         }
      )
      .define("enableFracture", true);
   public static final BooleanValue HEAVY_RAIN_ENABLE_PARTICLES_SPAWN = BUILDER.comment(
         new String[]{"Enable particle effects for Heavy Rain swords spawn", "Disable this to save rendering performance.", "Default: true"}
      )
      .define("enableParticlesSpawn", true);
   public static final BooleanValue HEAVY_RAIN_ENABLE_PARTICLES_DISAPPEAR = BUILDER.comment(
         new String[]{"Enable particle effects for Heavy Rain swords disappearing", "Disable this to save rendering performance.", "Default: true"}
      )
      .define("enableParticlesDisappear", true);
   public static final BooleanValue HEAVY_RAIN_ENABLE_PARTICLES_GROUND = BUILDER.comment(
         new String[]{"Enable particle effects for Heavy Rain swords hitting the ground", "Disable this to save rendering performance.", "Default: true"}
      )
      .define("enableParticlesGround", true);
   public static final EnumValue<EFNCommonConfig.HeavyRainScaleMode> HEAVY_RAIN_SCALE_MODE = BUILDER.comment(
         new String[]{
            "Scale mode for the Heavy Rain sword array.",
            "DEFAULT: 28 swords, 5 rings (High Performance Impact)",
            "MEDIUM: 14 swords, 4 rings (Balanced)",
            "LOW: 7 swords, 3 rings (Performance Friendly)",
            "CUSTOM: Uses the custom array settings below"
         }
      )
      .defineEnum("scaleMode", EFNCommonConfig.HeavyRainScaleMode.DEFAULT);
   public static final ConfigValue<List<? extends String>> HEAVY_RAIN_CUSTOM_ARRAY = BUILDER.comment(
         new String[]{
            "Custom sword array configuration. Only used if scaleMode is set to CUSTOM.",
            "Format: [\"swordCount,radius\"] for each ring.",
            "Example: [\"1,0.0\", \"4,1.0\", \"8,2.0\"]"
         }
      )
      .defineList("customArray", Lists.newArrayList(new String[]{"2,0.0", "3,0.7", "8,1.5", "8,3.5", "7,4.5"}), obj -> obj instanceof String);

   static {
      BUILDER.push("animation_speed");
      BUILDER.pop();
      BUILDER.push("yamato_catcher");
      BUILDER.pop();
      BUILDER.push("yamato_global_stun");
      BUILDER.pop();
      BUILDER.push("yamato_parry_stun");
      BUILDER.pop();
      BUILDER.push("animation_yamato");
      BUILDER.pop();
      BUILDER.push("animation_yamato");
      BUILDER.pop();
      BUILDER.push("animation_murasama");
      BUILDER.pop();
      BUILDER.push("animation_murasama");
      BUILDER.pop();
      BUILDER.push("yamato_reward");
      BUILDER.pop();
      BUILDER.push("murasama");
      BUILDER.pop();
      BUILDER.push("yamato_parry_timeslow");
      BUILDER.pop();
      BUILDER.push("dualsword_dodge_timeslow");
      BUILDER.pop();
      BUILDER.push("judgementcut_end_damage");
      BUILDER.pop();
      BUILDER.push("heavy_rain_sword");
      BUILDER.pop();
      SPEC = BUILDER.build();
   }

   @EventBusSubscriber(modid = "efn", bus = Bus.MOD)
   public static class EFNModLoadingContext {
      @SubscribeEvent
      public static void onLoad(Loading event) {
      }
   }

   public enum HeavyRainScaleMode {
      DEFAULT,
      MEDIUM,
      LOW,
      CUSTOM;
   }
}
