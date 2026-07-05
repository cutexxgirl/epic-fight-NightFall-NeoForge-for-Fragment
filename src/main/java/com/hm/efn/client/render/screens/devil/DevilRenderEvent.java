package com.hm.efn.client.render.screens.devil;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderGuiEvent.Post;
import net.neoforged.bus.api.SubscribeEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@OnlyIn(Dist.CLIENT)
public class DevilRenderEvent {
   private static final ResourceLocation DEVIL_BOUND = ResourceLocation.fromNamespaceAndPath("efn", "textures/gui/devil_bound.png");
   private static final ResourceLocation DEVIL_FILL = ResourceLocation.fromNamespaceAndPath("efn", "textures/gui/devil_fill.png");
   private static final ResourceLocation DEVIL_LIQUID = ResourceLocation.fromNamespaceAndPath("efn", "textures/gui/devil_liquid.png");
   private static final int IMAGE_WIDTH = 96;
   private static final int IMAGE_HEIGHT = 26;
   private static final int MARGIN = 32;

   @SubscribeEvent
   public static void onRenderDevilBar(Post event) {
      Minecraft mc = Minecraft.getInstance();
      GuiGraphics guiGraphics = event.getGuiGraphics();
      LocalPlayerPatch localPlayerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(mc.player, LocalPlayerPatch.class);
      if (localPlayerPatch != null) {
         SkillContainer container = localPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (container != null && container.getDataManager().hasData(EFNSKillDataKeys.DOPPELGANGER_TICK)) {
            int stack = container.getStack();
            float resourceRatio = container.getResource() / container.getMaxResource();
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            int posX = screenWidth - 96 - 32 + 16;
            int posY = screenHeight - 26 - 32;
            guiGraphics.blit(DEVIL_BOUND, posX, posY, 0.0F, 0.0F, 96, 26, 96, 26);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(0.25F, 0.25F, 1.0F, 1.0F);

            for (int i = 0; i < stack; i++) {
               int px = i >= 5 ? (i - 5) * 13 : i * 13;
               int py = i >= 5 ? 6 : 0;
               guiGraphics.blit(DEVIL_FILL, posX + 21 + px, posY + 10 + py, 21 + px, 10.0F, 12, 5, 96, 26);
            }

            int px = stack >= 5 ? (stack - 5) * 13 : stack * 13;
            int py = stack >= 5 ? 6 : 0;
            int totalPix = Math.round(5.0F * resourceRatio);
            guiGraphics.blit(DEVIL_FILL, posX + 21 + px, posY + 10 + py + (5 - totalPix), 21.0F, 10 + py + (5 - totalPix), 12, totalPix, 96, 26);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
         }
      }
   }
}
