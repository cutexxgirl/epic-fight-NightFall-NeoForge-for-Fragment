package com.hm.efn.client.input.control.zansetsu;

import com.hm.efn.client.input.control.EFNMouseInputHandle;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.hm.efn.gameasset.animations.EFNZansetsuAnimations;
import com.hm.efn.gameasset.animations.EFNZansetsuAnimations_B;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Murasama;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME, value = Dist.CLIENT)
public class ZansetsuWheelInput {
   private static final int TOTAL_SECTORS = 16;
   private static final float SECTOR_ANGLE = 22.5F;
   private static final int BASE_SECTOR = 0;
   private static final Minecraft MC = Minecraft.getInstance();
   private static int currentSector = 0;
   private static int previousSector = 0;
   private static float accumulatedAngle = 0.0F;
   private static boolean wheelActive = false;
   private static double lastProcessedMouseX = 0.0;
   private static boolean directionChanged = false;
   private static boolean wasRightClickPressed = false;

   private ZansetsuWheelInput() {
   }

   @SubscribeEvent
   public static void onClientTick(Pre event) {
      if (MC.player != null) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(MC.player, PlayerPatch.class);
         if (playerPatch != null) {
            SkillContainer murasama = playerPatch.getSkill(Murasama.Murasama);
            SkillContainer hfBlade = playerPatch.getSkill(HfBlade.HfBlade);
            SkillContainer activeSkill = null;
            boolean isHfBlade = false;
            if (murasama != null && (Boolean)murasama.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
               activeSkill = murasama;
               isHfBlade = false;
            } else if (hfBlade != null && (Boolean)hfBlade.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
               activeSkill = hfBlade;
               isHfBlade = true;
            }

            if (activeSkill != null && activeSkill.getDataManager() != null) {
               SkillDataManager dataManager = activeSkill.getDataManager();
               boolean useGestureMode = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_INPUT_MODE);
               if (useGestureMode) {
                  if (wheelActive) {
                     deactivateWheel(playerPatch, dataManager);
                  }
               } else {
                  syncWheelStateFromDataManager(playerPatch, dataManager);
                  if (wheelActive) {
                     checkRightClickToggle(playerPatch, dataManager, isHfBlade);
                     processWheelInput(playerPatch, dataManager, isHfBlade);
                     monitorWheelState(dataManager);
                     checkAndPlayCorrectAnimation(playerPatch, dataManager, isHfBlade);
                  }
               }
            } else {
               if (wheelActive) {
                  deactivateWheel(playerPatch, null);
               }
            }
         }
      }
   }

   private static void syncWheelStateFromDataManager(PlayerPatch<?> playerPatch, SkillDataManager dataManager) {
      boolean isZansetsuActive = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
      if (isZansetsuActive && !wheelActive) {
         activateWheel(dataManager);
      } else if (!isZansetsuActive && wheelActive) {
         deactivateWheel(playerPatch, dataManager);
      }
   }

   private static void checkRightClickToggle(PlayerPatch<?> playerPatch, SkillDataManager dataManager, boolean isHfBlade) {
      if (MC.options.keyUse.isDown() && !wasRightClickPressed && playerPatch.getEntityState().canUseSkill()) {
         int oppositeSector = getOppositeSector(currentSector);
         setCurrentSector(oppositeSector, dataManager, playerPatch, isHfBlade);
      }

      wasRightClickPressed = MC.options.keyUse.isDown();
   }

   private static void processWheelInput(PlayerPatch<?> playerPatch, SkillDataManager dataManager, boolean isHfBlade) {
      if (wheelActive && playerPatch.getEntityState().canUseSkill()) {
         float currentMouseX = EFNMouseInputHandle.getLastMovedDistanceX();
         if (checkDirectionChange(EFNMouseInputHandle.getDeltaMouseX())) {
            directionChanged = true;
            return;
         }

         if (directionChanged) {
            return;
         }

         float angleDelta = currentMouseX - (float)lastProcessedMouseX;
         accumulatedAngle += angleDelta;
         int sectorChange = accumulatedAngle >= 22.5F ? 1 : (accumulatedAngle <= -22.5F ? -1 : 0);
         if (sectorChange != 0) {
            previousSector = currentSector;
            currentSector = getWrappedSector(currentSector + sectorChange);
            syncCurrentSectorToDataManager(currentSector, dataManager);
            playAnimationForPlayer(playerPatch, getExpectedAnimation(currentSector, isPlayerInAir(dataManager), isHfBlade), 0.0F);
            accumulatedAngle -= sectorChange * 22.5F;
         }

         lastProcessedMouseX = currentMouseX;
      }
   }

   private static void checkAndPlayCorrectAnimation(PlayerPatch<?> playerPatch, SkillDataManager dataManager, boolean isHfBlade) {
      AssetAccessor<? extends StaticAnimation> currentAnim = ((DynamicAnimation)Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null))
            .getAnimation()
            .get())
         .getRealAnimation();
      if (!currentAnim.equals(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU) && !currentAnim.equals(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU_AIR)) {
         boolean inAir = isPlayerInAir(dataManager);
         AnimationAccessor<? extends StaticAnimation> expected = getExpectedAnimation(currentSector, inAir, isHfBlade);
         AnimationAccessor<? extends StaticAnimation> opposite = getExpectedAnimation(getOppositeSector(currentSector), inAir, isHfBlade);
         if (currentAnim != expected && currentAnim != opposite && !((StaticAnimation)currentAnim.get()).isLinkAnimation()) {
            playAnimationForPlayer(playerPatch, expected, 0.0F);
         }
      }
   }

   private static AnimationAccessor<? extends StaticAnimation> getExpectedAnimation(int sector, boolean inAir, boolean isHfBlade) {
      return isHfBlade ? getExpectedAnimationForSector_B(sector, inAir) : getExpectedAnimationForSector(sector, inAir);
   }

   private static AnimationAccessor<? extends StaticAnimation> getExpectedAnimationForSector(int sector, boolean inAir) {
      if (inAir) {
         return switch (sector) {
            case 0 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_M_AIR;
            case 1 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_L_AIR;
            case 2 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_DOWN_AIR;
            case 3 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_DOWN_AIR;
            case 4 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_MID_AIR;
            case 5 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_UP_AIR;
            case 6 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_UP_AIR;
            case 7 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_L_AIR;
            case 8 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_M_AIR;
            case 9 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_R_AIR;
            case 10 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_UP_AIR;
            case 11 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_UP_AIR;
            case 12 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_MID_AIR;
            case 13 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_DOWN_AIR;
            case 14 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_DOWN_AIR;
            case 15 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_R_AIR;
            default -> null;
         };
      } else {
         return switch (sector) {
            case 0 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_M;
            case 1 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_L;
            case 2 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_DOWN;
            case 3 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_DOWN;
            case 4 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_MID;
            case 5 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_RL_UP;
            case 6 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_RL_UP;
            case 7 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_L;
            case 8 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_M;
            case 9 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_DU_R;
            case 10 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_UP;
            case 11 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_UP;
            case 12 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_MID;
            case 13 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_HORIZONTAL_LR_DOWN;
            case 14 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_DIAGONAL_LR_DOWN;
            case 15 -> EFNZansetsuAnimations.HF_MURASAMA_SLASH_VERTICAL_UD_R;
            default -> null;
         };
      }
   }

   private static AnimationAccessor<? extends StaticAnimation> getExpectedAnimationForSector_B(int sector, boolean inAir) {
      if (inAir) {
         return switch (sector) {
            case 0 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_M_AIR;
            case 1 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_L_AIR;
            case 2 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_RL_DOWN_AIR;
            case 3 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_DOWN_AIR;
            case 4 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_MID_AIR;
            case 5 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_UP_AIR;
            case 6 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_RL_UP_AIR;
            case 7 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_L_AIR;
            case 8 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_M_AIR;
            case 9 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_R_AIR;
            case 10 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_LR_UP_AIR;
            case 11 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_UP_AIR;
            case 12 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_MID_AIR;
            case 13 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_DOWN_AIR;
            case 14 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_LR_DOWN_AIR;
            case 15 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_R_AIR;
            default -> null;
         };
      } else {
         return switch (sector) {
            case 0 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_M;
            case 1 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_L;
            case 2 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_RL_DOWN;
            case 3 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_DOWN;
            case 4 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_MID;
            case 5 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_RL_UP;
            case 6 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_RL_UP;
            case 7 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_L;
            case 8 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_M;
            case 9 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_DU_R;
            case 10 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_LR_UP;
            case 11 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_UP;
            case 12 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_MID;
            case 13 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_HORIZONTAL_LR_DOWN;
            case 14 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_DIAGONAL_LR_DOWN;
            case 15 -> EFNZansetsuAnimations_B.HF_BLADE_SLASH_VERTICAL_UD_R;
            default -> null;
         };
      }
   }

   private static void playAnimationForPlayer(PlayerPatch<?> pp, AnimationAccessor<? extends StaticAnimation> anim, float t) {
      if (anim != null) {
         pp.playAnimationSynchronized(anim, t);
      }
   }

   private static boolean isPlayerInAir(SkillDataManager dm) {
      return (Boolean)dm.getDataValue(EFNSKillDataKeys.MURASAMA_AIR_BORNE);
   }

   private static void activateWheel(SkillDataManager dm) {
      wheelActive = true;
      currentSector = 0;
      accumulatedAngle = 0.0F;
      lastProcessedMouseX = EFNMouseInputHandle.getLastMovedDistanceX();
      directionChanged = false;
      syncCurrentSectorToDataManager(0, dm);
   }

   private static void deactivateWheel(PlayerPatch<?> pp, SkillDataManager dm) {
      wheelActive = false;
      if (dm != null && !(Boolean)dm.getDataValue(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE)) {
         pp.playAnimationSynchronized(isPlayerInAir(dm) ? EFNMurasamaAnimations.HF_MURASAMA_IDLE_AIR : EFNMurasamaAnimations.HF_MURASAMA_IDLE_COMBAT, 0.1F);
         pp.playAnimationSynchronized(isPlayerInAir(dm) ? EFNHfBladeAnimations.HF_BLADE_IDLE_AIR : EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT, 0.1F);
      }
   }

   private static void syncCurrentSectorToDataManager(int s, SkillDataManager dm) {
      if ((Integer)dm.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR) != s) {
         dm.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR, s);
      }
   }

   private static void monitorWheelState(SkillDataManager dm) {
      int ds = (Integer)dm.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR);
      if (ds != currentSector) {
         currentSector = ds;
      }
   }

   private static boolean checkDirectionChange(double d) {
      double init = EFNMouseInputHandle.getDeltaMouseX();
      return init != 0.0 && d != 0.0 && init != d;
   }

   private static int getWrappedSector(int s) {
      return (s % 16 + 16) % 16;
   }

   public static void setCurrentSector(int s, SkillDataManager dm, PlayerPatch<?> pp, boolean isHfBlade) {
      currentSector = s;
      syncCurrentSectorToDataManager(s, dm);
      playAnimationForPlayer(pp, getExpectedAnimation(s, isPlayerInAir(dm), isHfBlade), 0.0F);
   }

   public static int getOppositeSector(int s) {
      return (s + 8) % 16;
   }
}
