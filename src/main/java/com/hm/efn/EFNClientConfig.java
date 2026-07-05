package com.hm.efn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

public class EFNClientConfig {
   public static final Builder BUILDER = new Builder();
   public static final ModConfigSpec SPEC;
   public static final BooleanValue ENABLE_JUDGEMENTCUT_END_CAMERA_ANIMATIONS = BUILDER.comment(
         new String[]{
            "Enable/disable camera animations for JudgementCut-End",
            "When enabled, JudgementCut-End will play cinematic camera animations",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .translation("efn.config.enable_judgementcut_end_camera_animations")
      .define("enableCameraAnimations", true);
   public static final BooleanValue ENABLE_JUDGEMENTCUT_END_MODEL_FIX = BUILDER.comment(
         new String[]{
            "Enable/disable weapon model fix for JudgementCut-End",
            "When enabled, JudgementCut-End will use the DMC4 Yamato Model",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .define("enableModelFix", true);
   public static final BooleanValue ENABLE_ZANDATSU_CAMERA_ANIMATIONS = BUILDER.comment(
         new String[]{
            "Enable/disable camera animations for Zandatsu",
            "When enabled, Zandatsu will play cinematic camera animations",
            "This is a CLIENT-SIDE only setting",
            "Default: false (disabled)"
         }
      )
      .define("enableCameraAnimations", false);
   public static final BooleanValue ENABLE_JUDGEMENTCUT_END_PARTICLES = BUILDER.comment(
         new String[]{
            "Enable/disable particle effects for JudgementCut-End",
            "Disable Judgement Cut End's particle effects that may cause rendering incompatibility",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .translation("efn.config.enable_judgementcut_end_particles")
      .define("enableJudgementCutEndParticles", true);
   public static final BooleanValue ENABLE_JUDGEMENTCUT_END_SCREENDISTORTIONEFFECT = BUILDER.comment(
         new String[]{
            "Enable/disable screendistortion effects for JudgementCut-End",
            "Disable Judgement Cut End's screendistortion effects",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .define("enableJudgementCutEndScreendistortion", true);
   public static final BooleanValue ENABLE_BLOODLUST_POSTEFFECT = BUILDER.comment(
         new String[]{
            "Enable/disable post-effect when bloodlust's skill active",
            "When enabled, bloodlust's skill will play post-effect",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .translation("efn.config.enable_bloodlust_posteffect")
      .define("enableBloodlustPostEffect", true);
   public static final BooleanValue ENABLE_BEASTCLAW_POSTEFFECT = BUILDER.comment(
         new String[]{
            "Enable/disable post-effect when beastclaw's skill active",
            "When enabled, beastclaw's skill will play post-effect",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .define("enableBeastclawPostEffect", true);
   public static final DoubleValue GESTURE_MIN_DISTANCE = BUILDER.comment(
         new String[]{"Minimum gesture distance required to trigger a slash", "Higher values require larger mouse movements", "Default: 40.0"}
      )
      .defineInRange("minGestureDistance", 30.0, 10.0, 200.0);
   public static final DoubleValue GESTURE_MIN_SPEED = BUILDER.comment(
         new String[]{"Minimum gesture speed required to trigger a slash", "Higher values require faster mouse movements", "Default: 40.0"}
      )
      .defineInRange("minGestureSpeed", 30.0, 10.0, 200.0);
   public static final DoubleValue GESTURE_SIGNIFICANT_MOVEMENT_THRESHOLD = BUILDER.comment(
         new String[]{
            "Threshold for detecting significant movement to start gesture recognition", "Lower values make gesture detection more sensitive", "Default: 20.0"
         }
      )
      .defineInRange("significantMovementThreshold", 15.0, 5.0, 100.0);
   public static final DoubleValue GESTURE_VERTICAL_AFTER_HORIZONTAL_DISTANCE = BUILDER.comment(
         new String[]{
            "Increased distance requirement for vertical gestures after horizontal gestures",
            "Helps prevent accidental vertical gestures after horizontal ones",
            "Default: 50.0"
         }
      )
      .defineInRange("verticalAfterHorizontalDistance", 40.0, 30.0, 150.0);
   public static final DoubleValue GESTURE_VERTICAL_AFTER_HORIZONTAL_SPEED = BUILDER.comment(
         new String[]{
            "Increased speed requirement for vertical gestures after horizontal gestures",
            "Helps prevent accidental vertical gestures after horizontal ones",
            "Default: 50.0"
         }
      )
      .defineInRange("verticalAfterHorizontalSpeed", 40.0, 30.0, 150.0);
   public static final DoubleValue GESTURE_SPEED_SENSITIVITY = BUILDER.comment(
         new String[]{"Speed sensitivity multiplier for gesture detection", "Higher values make speed requirements more strict", "Default: 1.0"}
      )
      .defineInRange("speedSensitivity", 1.0, 0.1, 5.0);
   public static final IntValue GESTURE_TIMEOUT_TICKS = BUILDER.comment(
         new String[]{"Timeout ticks for gesture recognition", "How long to wait before resetting an incomplete gesture (in ticks)", "Default: 8"}
      )
      .defineInRange("gestureTimeoutTicks", 10, 1, 20);
   public static final IntValue GESTURE_SAME_DIRECTION_COOLDOWN_TICKS = BUILDER.comment(
         new String[]{"Cooldown ticks for same direction gestures", "Prevents triggering the same direction repeatedly (in ticks)", "Default: 8"}
      )
      .defineInRange("sameDirectionCooldownTicks", 0, 0, 40);
   public static final IntValue GESTURE_HORIZONTAL_COOLDOWN_TICKS = BUILDER.comment(
         new String[]{"Cooldown ticks after horizontal gestures", "Prevents accidental vertical gestures after horizontal ones (in ticks)", "Default: 20"}
      )
      .defineInRange("horizontalCooldownTicks", 0, 0, 60);
   public static final BooleanValue ZANSETSU_CAMERA_ENABLED = BUILDER.comment(
         new String[]{
            "Enable/disable all camera adjustments during Zansetsu mode",
            "When enabled, all camera effects (offset, view lock, shake) will be active",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .define("zansetsuCameraEnabled", true);
   public static final DoubleValue ZANSETSU_CAMERA_ELEVATION = BUILDER.comment(
         new String[]{
            "Camera elevation (vertical offset) during Zansetsu mode", "Positive values move the camera upward, negative values downward", "Default: -0.2"
         }
      )
      .defineInRange("zansetsuCameraElevation", -0.2, -5.0, 5.0);
   public static final DoubleValue ZANSETSU_CAMERA_FORWARD_OFFSET = BUILDER.comment(
         new String[]{"Camera forward offset during Zansetsu mode", "Positive values move the camera forward, negative values backward", "Default: 0.4"}
      )
      .defineInRange("zansetsuCameraForwardOffset", 0.4, -5.0, 10.0);
   public static final DoubleValue ZANSETSU_CAMERA_SIDE_OFFSET = BUILDER.comment(
         new String[]{"Camera side offset during Zansetsu mode", "Positive values move the camera to the right, negative values to the left", "Default: 0.7"}
      )
      .defineInRange("zansetsuCameraSideOffset", 0.7, -5.0, 5.0);
   public static final DoubleValue ZANSETSU_CAMERA_TRANSITION_DURATION = BUILDER.comment(
         new String[]{"Camera position transition duration in seconds", "How long it takes for the camera to move to its Zansetsu position", "Default: 0.25"}
      )
      .defineInRange("zansetsuCameraTransitionDuration", 0.25, 0.1, 2.0);
   public static final BooleanValue ZANSETSU_FOV_ENABLED = BUILDER.comment(
         new String[]{
            "Enable/disable FOV adjustment during Zansetsu mode",
            "When enabled, the field of view will be adjusted during Zansetsu mode",
            "This provides a more focused combat perspective",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .define("zansetsuFovEnabled", true);
   public static final DoubleValue ZANSETSU_FOV_VALUE = BUILDER.comment(
         new String[]{
            "FOV value during Zansetsu mode",
            "The field of view value to use during Zansetsu mode",
            "Lower values create a more zoomed-in effect",
            "Default: 30.0"
         }
      )
      .defineInRange("zansetsuFovValue", 30.0, 30.0, 110.0);
   public static final DoubleValue ZANSETSU_FOV_TRANSITION_DURATION = BUILDER.comment(
         new String[]{"FOV transition duration in seconds", "How long it takes for the FOV to transition to Zansetsu value", "Default: 0.3"}
      )
      .defineInRange("zansetsuFovTransitionDuration", 0.3, 0.1, 2.0);
   public static final BooleanValue ZANSETSU_VIEW_LOCK_ENABLED = BUILDER.comment(
         new String[]{
            "Enable/disable view lock during Zansetsu mode",
            "When enabled, the camera's vertical angle will be locked during Zansetsu mode",
            "This provides a more stable combat perspective",
            "This is a CLIENT-SIDE only setting",
            "Default: true (enabled)"
         }
      )
      .define("zansetsuViewLockEnabled", true);
   public static final DoubleValue ZANSETSU_CAMERA_LOCKED_XROT = BUILDER.comment(
         new String[]{
            "Locked X rotation (vertical angle) during Zansetsu mode",
            "The vertical angle to lock the camera to during Zansetsu mode",
            "0 = horizontal, positive = looking down, negative = looking up",
            "Default: 0.0 (horizontal)"
         }
      )
      .defineInRange("zansetsuCameraLockedXRot", 0.0, -90.0, 90.0);
   public static final DoubleValue ZANSETSU_VIEW_LOCK_TRANSITION_DURATION = BUILDER.comment(
         new String[]{"View lock transition duration in seconds", "How long it takes for the camera view to lock/unlock during Zansetsu mode", "Default: 0.4"}
      )
      .defineInRange("zansetsuViewLockTransitionDuration", 0.4, 0.1, 2.0);
   public static final BooleanValue VFX_PLUS = BUILDER.comment(
         new String[]{"When enabled, AAAParticle will work to enhance VFX", "This is a CLIENT-SIDE only setting", "Default: true (enabled)"}
      )
      .translation("efn.config.enable_vfx")
      .define("enableVFXPlus", true);
   public static final BooleanValue GROUND_SLAM_VFX = BUILDER.comment("Enable/disable Ground-Slam VFX").define("enableGroundSlamVFX", true);
   public static final BooleanValue YAMATO_JUDGEMENT_CUT_VFX = BUILDER.comment("Enable/disable yamato-judgement-cut VFX")
      .define("enableYamatoJudgementCutVFX", true);
   public static final BooleanValue CLAW_SKILL_VFX = BUILDER.comment("Enable/disable Claw-Skill VFX").define("enableClawSkillVFX", true);
   public static final BooleanValue DUAL_SWORD_SKILL_VFX = BUILDER.comment("Enable/disable DualSword-Skill VFX").define("enableDualSwordSkillVFX", true);
   public static final BooleanValue TACHI_SKILL_VFX = BUILDER.comment("Enable/disable Tachi-Skill VFX").define("enableTachiSkillVFX", true);
   public static final BooleanValue FALCHION_SKILL_VFX = BUILDER.comment("Enable/disable Falchion-Skill VFX").define("enableFalchionSkillVFX", true);
   public static final BooleanValue GREATSWORD_CHARGE_VFX = BUILDER.comment("Enable/disable GreatSword-ChargeSkill VFX")
      .define("enableGreatSwordChargeSkillVFX", true);
   public static final BooleanValue SPEAR_CHARGE_VFX = BUILDER.comment("Enable/disable Spear-Charge VFX").define("enableSpearChargeVFX", true);
   public static final BooleanValue SPEAR_FINISHER_VFX = BUILDER.comment("Enable/disable Spear-Finisher VFX").define("enableSpearFinisherVFX", true);
   public static final BooleanValue MURASAMA_CHARGE_VFX = BUILDER.comment("Enable/disable HfBlade-Charge VFX").define("enableMurasamaChargeVFX", true);
   public static final BooleanValue MURASAMA_FLASH_VFX = BUILDER.comment("Enable/disable HfBlade-Flash VFX").define("enableMurasamaFlashVFX", true);
   public static final BooleanValue MURASAMA_AAA_VFX = BUILDER.comment("Enable/disable all AAA Effek VFX for Murasama (BurstRed, BreakOut, Charging)")
      .define("enableMurasamaAAAVFX", true);
   public static final BooleanValue HFBLADE_AAA_VFX = BUILDER.comment("Enable/disable all AAA Effek VFX for HfBlade (BurstBlue, BreakOut_B, Charging)")
      .define("enableHfBladeAAAVFX", true);
   public static final BooleanValue SCYTHE_FINISHER_VFX = BUILDER.comment("Enable/disable Scythe-Finisher VFX").define("enableScytheFinisherVFX", true);
   public static final BooleanValue SEKIRO_MORTAL_BLADE_VFX = BUILDER.comment("Enable/disable MortalBlade VFX").define("enableMortalBladeVFX", true);
   public static final BooleanValue SEKIRO_DRAGONFLASH_VFX = BUILDER.comment("Enable/disable DragonFlash VFX").define("enableDragonFlashVFX", true);
   public static final BooleanValue ENABLE_AAA_WARNING = BUILDER.comment(
         new String[]{
            "Enable the warning when AAA Particles mod is not installed",
            "When enabled, shows a warning on first launch if AAA Particles is missing",
            "Default: true (enabled)"
         }
      )
      .define("enableAAAWarning", true);
   public static final BooleanValue HAS_SHOWN_AAA_WARNING = BUILDER.comment(
         new String[]{"Internal: Whether the AAA Particles warning has been shown", "Do not modify manually - this is managed by the mod"}
      )
      .define("hasShownAAAWarning", false);
   public static final BooleanValue IMPACT_FRAME = BUILDER.comment("if enable it, some weapons will have impact frame effect").define("impact_frame", true);
   public static final BooleanValue AFTERIMAGE = BUILDER.comment("if enable it, some weapons will have unique afterimage").define("afterimage", true);
   private static boolean internalWarningShown = false;
   private static boolean configInitialized = false;
   private static final String WARNING_FILE_NAME = "efn_aaa_warning.json";
   private static final Path WARNING_FILE_PATH = FMLPaths.CONFIGDIR.get().resolve("efn_aaa_warning.json");

   public static void initWarningSystem() {
      if (!configInitialized) {
         File warningFile = WARNING_FILE_PATH.toFile();
         if (warningFile.exists()) {
            try {
               String content = FileUtils.readFileToString(warningFile, StandardCharsets.UTF_8);
               internalWarningShown = Boolean.parseBoolean(content.trim());
            } catch (IOException e) {
               internalWarningShown = getConfigBoolean(HAS_SHOWN_AAA_WARNING, false);
            }
         } else {
            internalWarningShown = getConfigBoolean(HAS_SHOWN_AAA_WARNING, false);
         }

         configInitialized = true;
      }
   }

   public static boolean shouldShowAAAWarning() {
      return isAAAWarningEnabled() && !internalWarningShown;
   }

   public static boolean isAAAWarningEnabled() {
      return getConfigBoolean(ENABLE_AAA_WARNING, true);
   }

   public static boolean hasShownAAAWarning() {
      return getConfigBoolean(HAS_SHOWN_AAA_WARNING, internalWarningShown);
   }

   public static void setAAAWarningShown() {
      internalWarningShown = true;

      try {
         FileUtils.writeStringToFile(WARNING_FILE_PATH.toFile(), String.valueOf(true), StandardCharsets.UTF_8);
      } catch (IOException e) {
         System.err.println("Failed to save AAA warning state: " + e.getMessage());
      }

      try {
         HAS_SHOWN_AAA_WARNING.set(true);
      } catch (IllegalStateException | NullPointerException e) {
         EFN.LOGGER.warn("Could not persist AAA warning state to NeoForge config yet: {}", e.getMessage());
      }
   }

   private static boolean getConfigBoolean(BooleanValue value, boolean fallback) {
      try {
         return value.get();
      } catch (IllegalStateException | NullPointerException e) {
         return fallback;
      }
   }

   static {
      BUILDER.push("VFX Plus");
      BUILDER.pop();
      BUILDER.push("judgementcut_end_camera");
      BUILDER.pop();
      BUILDER.push("judgementcut_end_weapon_model");
      BUILDER.pop();
      BUILDER.push("zandatsu_camera_animations");
      BUILDER.pop();
      BUILDER.push("judgementcut_end_particles");
      BUILDER.pop();
      BUILDER.push("judgementcut_end_screendistortion_effect");
      BUILDER.pop();
      BUILDER.push("bloodlust_posteffect");
      BUILDER.pop();
      BUILDER.push("beastclaw_posteffect");
      BUILDER.pop();
      BUILDER.push("zansetsu_gesture_sensitivity");
      BUILDER.pop();
      BUILDER.push("zansetsu_camera_settings");
      BUILDER.pop();
      BUILDER.push("zansetsu_camera_offset");
      BUILDER.pop();
      BUILDER.push("zansetsu_view_lock");
      BUILDER.pop();
      BUILDER.push("aaa_particles_warning");
      BUILDER.pop();
      BUILDER.push("impact_frame");
      BUILDER.pop();
      BUILDER.push("afterimage");
      BUILDER.pop();
      SPEC = BUILDER.build();
   }

   @EventBusSubscriber(modid = "efn", bus = Bus.MOD)
   public static class EFNClientModLoadingContext {
      @SubscribeEvent
      public static void onLoad(Loading event) {
         if (event.getConfig().getType() == Type.CLIENT) {
            EFNClientConfig.initWarningSystem();
         }
      }
   }
}
