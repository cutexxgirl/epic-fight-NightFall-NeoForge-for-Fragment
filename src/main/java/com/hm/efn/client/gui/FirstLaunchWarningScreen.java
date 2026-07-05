package com.hm.efn.client.gui;

import com.hm.efn.EFN;
import com.hm.efn.EFNClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class FirstLaunchWarningScreen extends Screen {
   private static final ResourceLocation WARNING_ICON = ResourceLocation.fromNamespaceAndPath("efn", "textures/gui/warning_icon.png");
   private static final Component TITLE = Component.translatable("efn.warning.title");
   private static final Component MESSAGE = Component.translatable("efn.warning.message");
   private static final Component RECOMMENDATION = Component.translatable("efn.warning.recommendation");
   private static final Component DOWNLOAD_BUTTON = Component.translatable("efn.warning.download");
   private static final Component IGNORE_BUTTON = Component.translatable("efn.warning.ignore");
   private static final Component CLOSE_BUTTON = Component.translatable("efn.warning.close");
   private static final String DOWNLOAD_URL = "https://www.curseforge.com/minecraft/mc-mods/aaa-particles";
   private static final int MIN_SCREEN_WIDTH = 320;
   private static final int MIN_SCREEN_HEIGHT = 240;
   private static final int MIN_CONTENT_WIDTH = 320;
   private static final int MAX_CONTENT_WIDTH = 800;
   private static final int MIN_CONTENT_HEIGHT = 260;
   private static final int MAX_CONTENT_HEIGHT = 500;
   private static final int MIN_BUTTON_WIDTH = 100;
   private static final int MIN_BUTTON_HEIGHT = 20;
   private static final int BUTTON_SPACING = 10;
   private int contentWidth;
   private int contentHeight;
   private int left;
   private int top;
   private int buttonWidth;
   private int buttonHeight;
   private int textAreaWidth;
   private int textAreaHeight;
   private int buttonY;
   private float scrollOffset = 0.0F;
   private boolean draggingScroll = false;
   private double lastMouseY;
   private int totalTextHeight = 0;

   public FirstLaunchWarningScreen() {
      super(TITLE);
   }

   public void tick() {
      super.tick();
      this.calculateDimensions();
   }

   private void calculateDimensions() {
      int effectiveWidth = Math.max(this.width, 320);
      int effectiveHeight = Math.max(this.height, 240);
      int screenAvailableWidth = effectiveWidth - 40;
      this.contentWidth = Mth.clamp(screenAvailableWidth, 320, 800);
      int screenAvailableHeight = effectiveHeight - 100;
      this.contentHeight = Mth.clamp(screenAvailableHeight, 260, 500);
      this.left = (effectiveWidth - this.contentWidth) / 2;
      this.top = Math.max(20, (effectiveHeight - this.contentHeight) / 3);
      int availableButtonWidth = (this.contentWidth - 30) / 2;
      this.buttonWidth = Math.max(100, Math.min(availableButtonWidth, 200));
      this.buttonHeight = 20;
      this.textAreaWidth = this.contentWidth - 60;
      this.textAreaHeight = Math.max(100, this.contentHeight - 140);
      this.buttonY = Math.min(this.top + this.contentHeight + 20, effectiveHeight - 40);
   }

   protected void init() {
      this.calculateDimensions();
      int centerX = this.width / 2;
      this.addRenderableWidget(Button.builder(DOWNLOAD_BUTTON, button -> {
         EFN.LOGGER.info("User clicked Download AAA Particles button");
         Util.getPlatform().openUri("https://www.curseforge.com/minecraft/mc-mods/aaa-particles");
         this.markAsShownAndClose();
      }).bounds(centerX - this.buttonWidth - 5, this.buttonY, this.buttonWidth, this.buttonHeight).build());
      this.addRenderableWidget(Button.builder(IGNORE_BUTTON, button -> {
         EFN.LOGGER.info("User clicked 'I Understand' button - skipping AAA Particles installation");
         this.markAsShownAndClose();
      }).bounds(centerX + 5, this.buttonY, this.buttonWidth, this.buttonHeight).build());
      this.addRenderableWidget(Button.builder(CLOSE_BUTTON, button -> {
         EFN.LOGGER.info("User clicked Close (X) button - closing warning without action");
         EFNClientConfig.setAAAWarningShown();
         super.onClose();
      }).bounds(this.left + this.contentWidth - 25, this.top + 5, 20, 20).build());
   }

   private void markAsShownAndClose() {
      EFNClientConfig.setAAAWarningShown();
      super.onClose();
   }

   public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.calculateDimensions();
      guiGraphics.fill(0, 0, this.width, this.height, -16777216);
      guiGraphics.fill(0, 0, this.width, this.height, -2013265920);
      this.renderContentBackground(guiGraphics, this.left, this.top, this.contentWidth, this.contentHeight);
      int iconSize = this.getScaledIconSize() * 4;
      guiGraphics.blit(WARNING_ICON, this.left + 20, this.top + 20, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      int titleX = this.left + 20 + iconSize + 10;
      int titleY = this.top + 20 + (iconSize - 9) / 2;
      guiGraphics.drawString(this.font, TITLE, titleX, titleY, 16777045, false);
      int lineY = this.top + 20 + iconSize + 10;
      guiGraphics.fill(this.left + 20, lineY, this.left + this.contentWidth - 20, lineY + 1, 16755200);
      int textAreaTop = lineY + 15;
      guiGraphics.enableScissor(this.left + 20, textAreaTop, this.left + 20 + this.textAreaWidth, textAreaTop + this.textAreaHeight);
      int textY = textAreaTop + (int)this.scrollOffset;
      textY = this.renderText(guiGraphics, textY, MESSAGE.getString(), 16777215);
      textY += 10;
      textY = this.renderText(guiGraphics, textY, RECOMMENDATION.getString(), 16777045);
      String details = Component.translatable("efn.warning.recommendation.details").getString();
      textY = this.renderText(guiGraphics, textY, details, 16755200);
      this.totalTextHeight = textY - (textAreaTop + (int)this.scrollOffset);
      guiGraphics.disableScissor();
      if (this.totalTextHeight > this.textAreaHeight) {
         this.renderScrollBar(guiGraphics, textAreaTop);
      }

      this.renderButtonAreaHint(guiGraphics);
      Component bottomHint = Component.translatable("efn.warning.bottom_hint");
      guiGraphics.drawCenteredString(this.font, bottomHint, this.width / 2, this.buttonY + 25, 11184810);
      poseStack.popPose();
      super.render(guiGraphics, mouseX, mouseY, partialTick);
   }

   private int renderText(GuiGraphics guiGraphics, int startY, String text, int color) {
      String[] lines = this.wrapText(text, this.textAreaWidth - 20);
      int y = startY;

      for (String line : lines) {
         guiGraphics.drawString(this.font, line, this.left + 30, y, color, false);
         y += 9 + 4;
      }

      return y;
   }

   private int getScaledIconSize() {
      float scale = Math.min(this.width, this.height) / 1080.0F;
      return (int)(32.0F * Mth.clamp(scale, 0.5F, 2.0F));
   }

   private String[] wrapText(String text, int maxWidth) {
      List<String> lines = new ArrayList<>();
      StringBuilder builder = new StringBuilder();

      for (String word : text.split(" ")) {
         String test = builder.toString() + (builder.length() > 0 ? " " : "") + word;
         if (this.font.width(test) <= maxWidth) {
            if (builder.length() > 0) {
               builder.append(" ");
            }

            builder.append(word);
         } else if (builder.length() > 0) {
            lines.add(builder.toString());
            builder = new StringBuilder(word);
         } else {
            lines.add(word);
            builder = new StringBuilder();
         }
      }

      if (builder.length() > 0) {
         lines.add(builder.toString());
      }

      return lines.toArray(new String[0]);
   }

   private void renderButtonAreaHint(GuiGraphics guiGraphics) {
      int hintTop = this.buttonY - 5;
      int hintBottom = this.buttonY + this.buttonHeight + 5;
      guiGraphics.fill(0, hintTop, this.width, hintBottom, 1711276032);
      guiGraphics.fill(0, hintTop, this.width, hintTop + 1, 1157627903);
   }

   private void renderContentBackground(GuiGraphics guiGraphics, int left, int top, int width, int height) {
      guiGraphics.fill(left, top, left + width, top + height, -8825528);
      guiGraphics.fill(left, top, left + width, top + 2, -10665929);
      guiGraphics.fill(left, top + height - 2, left + width, top + height, -10665929);
      guiGraphics.fill(left, top, left + 2, top + height, -10665929);
      guiGraphics.fill(left + width - 2, top, left + width, top + height, -10665929);
   }

   private void renderScrollBar(GuiGraphics guiGraphics, int textAreaTop) {
      int scrollBarX = this.left + this.contentWidth - 30;
      float ratio = (float)this.textAreaHeight / this.totalTextHeight;
      int scrollHeight = Math.max(20, (int)(this.textAreaHeight * ratio));
      float scrollRatio = -this.scrollOffset / (this.totalTextHeight - this.textAreaHeight);
      int scrollY = textAreaTop + (int)((this.textAreaHeight - scrollHeight) * scrollRatio);
      guiGraphics.fill(scrollBarX, textAreaTop, scrollBarX + 6, textAreaTop + this.textAreaHeight, -11652050);
      guiGraphics.fill(scrollBarX, scrollY, scrollBarX + 6, scrollY + scrollHeight, -7508381);
   }

   public void onClose() {
      super.onClose();
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void resize(@NotNull Minecraft minecraft, int width, int height) {
      super.resize(minecraft, width, height);
      this.init();
   }
}
