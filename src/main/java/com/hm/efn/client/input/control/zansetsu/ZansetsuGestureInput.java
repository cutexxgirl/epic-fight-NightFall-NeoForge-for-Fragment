package com.hm.efn.client.input.control.zansetsu;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.input.control.EFNMouseInputHandle;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.hm.efn.gameasset.animations.EFNZansetsuAnimations;
import com.hm.efn.gameasset.animations.EFNZansetsuAnimations_B;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Murasama;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import org.slf4j.Logger;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME, value = Dist.CLIENT)
public class ZansetsuGestureInput {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Minecraft MC = Minecraft.getInstance();
   private static final Random RANDOM = new Random();
   private static final int COOLDOWN_TICKS = 4;
   private static boolean gestureActive = false;
   private static float gestureStartX;
   private static float gestureStartY;
   private static float lastGestureX;
   private static float lastGestureY;
   private static float gestureSpeed;
   private static int gestureStartTick;
   private static int speedCalculationTick;
   private static int gestureCooldown;
   private static int sameDirectionCooldown;
   private static int horizontalGestureCooldown;
   private static ZansetsuGestureInput.GestureDirection lastGestureDirection = null;

   @SubscribeEvent
   public static void onClientTick(Pre event) {
      if (MC.player != null) {
         if (MC.screen != null) {
            if (gestureActive) {
               resetGesture(true);
            }
         } else {
            if (gestureCooldown > 0) {
               gestureCooldown--;
            }

            if (sameDirectionCooldown > 0) {
               sameDirectionCooldown--;
            }

            if (horizontalGestureCooldown > 0) {
               horizontalGestureCooldown--;
            }

            PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(MC.player, PlayerPatch.class);
            if (playerPatch != null) {
               SkillContainer murasama = playerPatch.getSkill(Murasama.Murasama);
               SkillContainer hfBlade = playerPatch.getSkill(HfBlade.HfBlade);
               SkillContainer activeSkill = null;
               boolean isHfBlade = false;
               if (murasama != null && (Boolean)murasama.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
                  activeSkill = murasama;
               } else if (hfBlade != null && (Boolean)hfBlade.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
                  activeSkill = hfBlade;
                  isHfBlade = true;
               }

               if (activeSkill == null) {
                  if (gestureActive) {
                     resetGesture(true);
                  }
               } else {
                  SkillDataManager dataManager = activeSkill.getDataManager();
                  if ((Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_INPUT_MODE) && !isPlayingZandatsu(playerPatch)) {
                     updateGestureSpeed();
                     if (gestureCooldown <= 0) {
                        handleGestureInput(playerPatch, dataManager, isHfBlade);
                     }
                  } else {
                     if (gestureActive) {
                        resetGesture(true);
                     }
                  }
               }
            }
         }
      }
   }

   private static void handleGestureInput(PlayerPatch<?> pp, SkillDataManager dm, boolean isHfBlade) {
      float mx = EFNMouseInputHandle.getLastMovedDistanceX();
      float my = EFNMouseInputHandle.getLastMovedDistanceY();
      if (!gestureActive && hasSignificantMovement(mx, my)) {
         gestureActive = true;
         gestureStartX = mx;
         gestureStartY = my;
         if (MC.player != null) {
            gestureStartTick = MC.player.tickCount;
         }
      } else if (gestureActive) {
         processActiveGesture(mx, my, pp, dm, isHfBlade);
      }
   }

   private static void processActiveGesture(float cx, float cy, PlayerPatch<?> pp, SkillDataManager dm, boolean isHfBlade) {
      float dx = cx - gestureStartX;
      float dy = cy - gestureStartY;
      float dist = (float)Math.sqrt(dx * dx + dy * dy);
      float speed = gestureSpeed * ((Double)EFNClientConfig.GESTURE_SPEED_SENSITIVITY.get()).floatValue();
      float reqDist = horizontalGestureCooldown > 0
         ? ((Double)EFNClientConfig.GESTURE_VERTICAL_AFTER_HORIZONTAL_DISTANCE.get()).floatValue()
         : ((Double)EFNClientConfig.GESTURE_MIN_DISTANCE.get()).floatValue();
      float reqSpeed = horizontalGestureCooldown > 0
         ? ((Double)EFNClientConfig.GESTURE_VERTICAL_AFTER_HORIZONTAL_SPEED.get()).floatValue()
         : ((Double)EFNClientConfig.GESTURE_MIN_SPEED.get()).floatValue();
      if (dist >= reqDist && speed >= reqSpeed) {
         ZansetsuGestureInput.GestureDirection dir = calculateDirection(dx, dy);
         if (dir == null) {
            resetGesture(true);
            return;
         }

         if (isSameDirectionAsLast(dir)) {
            if (MC.player != null) {
               gestureStartTick = MC.player.tickCount;
            }

            return;
         }

         executeGestureAction(dir, pp, dm, isHfBlade);
         resetGesture(false);
         gestureCooldown = 4;
         lastGestureDirection = dir;
         sameDirectionCooldown = (Integer)EFNClientConfig.GESTURE_SAME_DIRECTION_COOLDOWN_TICKS.get();
         if (dir == ZansetsuGestureInput.GestureDirection.LEFT || dir == ZansetsuGestureInput.GestureDirection.RIGHT) {
            horizontalGestureCooldown = (Integer)EFNClientConfig.GESTURE_HORIZONTAL_COOLDOWN_TICKS.get();
         }
      } else if (MC.player != null && MC.player.tickCount - gestureStartTick > (Integer)EFNClientConfig.GESTURE_TIMEOUT_TICKS.get()) {
         resetGesture(true);
      }
   }

   private static void executeGestureAction(ZansetsuGestureInput.GestureDirection dir, PlayerPatch<?> pp, SkillDataManager dm, boolean isHfBlade) {
      try {
         AnimationAccessor<? extends StaticAnimation> anim = getRandomAnimation(
            dir, (Boolean)dm.getDataValue(EFNSKillDataKeys.MURASAMA_AIR_BORNE), isHfBlade
         );
         if (anim != null) {
            pp.playAnimationSynchronized(anim, -0.12F);
            dm.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED, true);
         }
      } catch (Exception e) {
         LOGGER.error("Gesture Action Failed", e);
      }
   }

   private static AnimationAccessor<? extends StaticAnimation> getRandomAnimation(ZansetsuGestureInput.GestureDirection dir, boolean air, boolean isHfBlade) {
      List<AnimationAccessor<? extends StaticAnimation>> pool;
      if (isHfBlade) {
         pool = air ? getAnimationPoolForDirection_Air_B(dir) : getAnimationPoolForDirection_B(dir);
      } else {
         pool = air ? getAnimationPoolForDirection_Air(dir) : getAnimationPoolForDirection(dir);
      }

      return pool != null && !pool.isEmpty() ? pool.get(RANDOM.nextInt(pool.size())) : null;
   }

   private static List<AnimationAccessor<? extends StaticAnimation>> getAnimationPoolForDirection(ZansetsuGestureInput.GestureDirection d) {
      return switch (d) {
         case UP -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_M);
         case DOWN -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_M);
         case LEFT -> List.of(
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_MID,
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_UP,
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_DOWN
         );
         case RIGHT -> List.of(
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_MID,
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_UP,
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_DOWN
         );
         case UP_LEFT -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_UP, EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_L);
         case UP_RIGHT -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_UP, EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_R);
         case DOWN_LEFT -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_DOWN, EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_L);
         case DOWN_RIGHT -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_DOWN, EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_R);
      };
   }

   private static List<AnimationAccessor<? extends StaticAnimation>> getAnimationPoolForDirection_Air(ZansetsuGestureInput.GestureDirection d) {
      return switch (d) {
         case UP -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_M_AIR);
         case DOWN -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_M_AIR);
         case LEFT -> List.of(
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_MID_AIR,
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_UP_AIR,
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_DOWN_AIR
         );
         case RIGHT -> List.of(
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_MID_AIR,
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_UP_AIR,
            EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_DOWN_AIR
         );
         case UP_LEFT -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_UP_AIR, EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_L_AIR);
         case UP_RIGHT -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_UP_AIR, EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_R_AIR);
         case DOWN_LEFT -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_DOWN_AIR, EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_L_AIR);
         case DOWN_RIGHT -> List.of(EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_DOWN_AIR, EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_R_AIR);
      };
   }

   private static List<AnimationAccessor<? extends StaticAnimation>> getAnimationPoolForDirection_B(ZansetsuGestureInput.GestureDirection d) {
      return switch (d) {
         case UP -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_M);
         case DOWN -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_M);
         case LEFT -> List.of(
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_MID,
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_UP,
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_DOWN
         );
         case RIGHT -> List.of(
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_MID,
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_UP,
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_DOWN
         );
         case UP_LEFT -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_RL_UP, EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_L);
         case UP_RIGHT -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_LR_UP, EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_R);
         case DOWN_LEFT -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_RL_DOWN, EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_L);
         case DOWN_RIGHT -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_LR_DOWN, EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_R);
      };
   }

   private static List<AnimationAccessor<? extends StaticAnimation>> getAnimationPoolForDirection_Air_B(ZansetsuGestureInput.GestureDirection d) {
      return switch (d) {
         case UP -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_M_AIR);
         case DOWN -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_M_AIR);
         case LEFT -> List.of(
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_MID_AIR,
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_UP_AIR,
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_DOWN_AIR
         );
         case RIGHT -> List.of(
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_MID_AIR,
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_UP_AIR,
            EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_DOWN_AIR
         );
         case UP_LEFT -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_RL_UP_AIR, EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_L_AIR);
         case UP_RIGHT -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_LR_UP_AIR, EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_R_AIR);
         case DOWN_LEFT -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_RL_DOWN_AIR, EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_L_AIR);
         case DOWN_RIGHT -> List.of(EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_LR_DOWN_AIR, EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_R_AIR);
      };
   }

   private static void updateGestureSpeed() {
      float cx = EFNMouseInputHandle.getLastMovedDistanceX();
      float cy = EFNMouseInputHandle.getLastMovedDistanceY();
      float dist = (float)Math.sqrt(Math.pow(cx - lastGestureX, 2.0) + Math.pow(cy - lastGestureY, 2.0));
      if (dist > 1.0F) {
         float dt = 0.0F;
         if (MC.player != null) {
            dt = (MC.player.tickCount - speedCalculationTick) * 0.05F;
         }

         gestureSpeed = dt > 0.0F ? Math.max(dist / dt, gestureSpeed * 0.7F) : dist / 0.05F;
         lastGestureX = cx;
         lastGestureY = cy;
         if (MC.player != null) {
            speedCalculationTick = MC.player.tickCount;
         }
      }
   }

   private static boolean hasSignificantMovement(float dx, float dy) {
      return Math.sqrt(dx * dx + dy * dy) > (Double)EFNClientConfig.GESTURE_SIGNIFICANT_MOVEMENT_THRESHOLD.get()
         && gestureSpeed > (Double)EFNClientConfig.GESTURE_MIN_SPEED.get() * 0.5;
   }

   private static ZansetsuGestureInput.GestureDirection calculateDirection(float dx, float dy) {
      double a = (Math.toDegrees(Math.atan2(dy, dx)) + 360.0) % 360.0;
      if (a < 22.5 || a >= 337.5) {
         return ZansetsuGestureInput.GestureDirection.RIGHT;
      } else if (a < 67.5) {
         return ZansetsuGestureInput.GestureDirection.DOWN_RIGHT;
      } else if (a < 112.5) {
         return ZansetsuGestureInput.GestureDirection.DOWN;
      } else if (a < 157.5) {
         return ZansetsuGestureInput.GestureDirection.DOWN_LEFT;
      } else if (a < 202.5) {
         return ZansetsuGestureInput.GestureDirection.LEFT;
      } else if (a < 247.5) {
         return ZansetsuGestureInput.GestureDirection.UP_LEFT;
      } else {
         return a < 292.5 ? ZansetsuGestureInput.GestureDirection.UP : ZansetsuGestureInput.GestureDirection.UP_RIGHT;
      }
   }

   private static boolean isPlayingZandatsu(PlayerPatch<?> pp) {
      DynamicAnimation anim = (DynamicAnimation)Objects.requireNonNull(pp.getAnimator().getPlayerFor(null)).getAnimation().get();
      return anim == EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU.get() || anim == EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU_AIR.get();
   }

   private static boolean isSameDirectionAsLast(ZansetsuGestureInput.GestureDirection d) {
      return lastGestureDirection != null && sameDirectionCooldown > 0 && ZansetsuGestureInput.DirectionGroups.areDirectionsRelated(lastGestureDirection, d);
   }

   private static void resetGesture(boolean f) {
      gestureActive = false;
      if (f) {
         lastGestureDirection = null;
      }
   }

   private static class DirectionGroups {
      static boolean areDirectionsRelated(ZansetsuGestureInput.GestureDirection d1, ZansetsuGestureInput.GestureDirection d2) {
         return d1 == d2 ? true : Math.abs(d1.ordinal() - d2.ordinal()) <= 1;
      }
   }

   public enum GestureDirection {
      UP,
      DOWN,
      LEFT,
      RIGHT,
      UP_LEFT,
      UP_RIGHT,
      DOWN_LEFT,
      DOWN_RIGHT;
   }
}
