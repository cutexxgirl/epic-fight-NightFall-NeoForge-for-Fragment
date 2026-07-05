package com.hm.efn.client.gui;

import com.hm.efn.EFN;
import com.hm.efn.util.ModDetectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ScreenEvent.Opening;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "efn", value = Dist.CLIENT)
public class WarningEventHandler {
   private static boolean hasShownThisSession = false;
   private static int tickCounter = 0;
   private static final int DELAY_TICKS = 20;
   private static boolean isInitializing = true;

   @SubscribeEvent
   public static void onClientTick(Post event) {
      if (true) {
         Minecraft mc = Minecraft.getInstance();
         if (isInitializing) {
            tickCounter++;
            if (tickCounter >= 20) {
               isInitializing = false;
               EFN.LOGGER.info("Game initialization complete, checking for warning display...");
               if (mc.screen instanceof TitleScreen) {
                  checkAndShowWarning(mc);
               } else {
                  EFN.LOGGER.info("Not on TitleScreen, waiting for screen change...");
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onScreenOpen(Opening event) {
      if (event.getScreen() instanceof TitleScreen && !hasShownThisSession) {
         EFN.LOGGER.info("TitleScreen opened, checking for warning...");
         Minecraft mc = Minecraft.getInstance();
         mc.tell(() -> checkAndShowWarning(mc));
      }
   }

   private static void checkAndShowWarning(Minecraft mc) {
      if (!hasShownThisSession && mc.level == null) {
         boolean shouldShow = ModDetectionUtil.shouldShowWarning();
         EFN.LOGGER.info("Should show warning: {} (Target mod installed: {})", shouldShow, ModDetectionUtil.isTargetModInstalled());
         if (shouldShow) {
            EFN.LOGGER.info("Conditions met, showing warning screen...");
            mc.tell(() -> {
               final Screen previousScreen = mc.screen;
               mc.setScreen(new FirstLaunchWarningScreen() {
                  @Override
                  public void onClose() {
                     super.onClose();
                     mc.setScreen(previousScreen);
                  }
               });
               hasShownThisSession = true;
               EFN.LOGGER.info("Warning screen shown successfully");
            });
         }
      }
   }
}
