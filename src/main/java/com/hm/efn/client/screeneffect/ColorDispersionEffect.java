package com.hm.efn.client.screeneffect;

import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.guhao.vix.client.screeneffect.ScreenEffectBase;
import com.guhao.vix.client.screeneffect.ScreenEffectBase.SE_Pipeline;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.hm.efn.registries.PostPasses;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ColorDispersionEffect extends ScreenEffectBase {
   static ResourceLocation color_dispersion = OjangUtils.newRL("efn", "color_dispersion");
   public final SE_Pipeline ppl;
   public ColorDispersionEffect.Type type = ColorDispersionEffect.Type.POST;

   public ColorDispersionEffect(Vec3 pos) {
      super(color_dispersion, pos);
      this.ppl = new ColorDispersionEffect.CD_Pipeline(this);
      this.lifetime = 40;
   }

   public Pipeline getPipeline() {
      return this.ppl;
   }

   public static class CD_Pipeline extends SE_Pipeline<ColorDispersionEffect> {
      static ResourceLocation color_dispersion_tmp = OjangUtils.newRL("efn", "color_dispersion_tmp");

      public CD_Pipeline(ColorDispersionEffect effect) {
         super(ColorDispersionEffect.color_dispersion, effect);
         this.priority = 100;
      }

      public void PostEffectHandler() {
         RenderTarget tmp = TargetManager.getTarget(color_dispersion_tmp);
         PostPasses.blit.process(Minecraft.getInstance().getMainRenderTarget(), tmp);
         float t = Math.max(0.0F, 1.0F - 1.0F / ((ColorDispersionEffect)this.effect).lifetime * ((ColorDispersionEffect)this.effect).age);
         t = Mth.sqrt(t);
         float rm;
         float gm;
         float bm;
         if (((ColorDispersionEffect)this.effect).type == ColorDispersionEffect.Type.POST) {
            int phase = ((ColorDispersionEffect)this.effect).age / 8;
            if (phase == 0) {
               rm = 0.7F;
               gm = 0.7F;
               bm = 1.1F;
            } else if (phase == 1) {
               rm = 0.5F;
               gm = 0.5F;
               bm = 1.2F;
            } else if (phase == 2) {
               rm = 0.8F;
               gm = 0.8F;
               bm = 1.2F;
            } else {
               rm = 0.0F;
               gm = 0.0F;
               bm = 0.0F;
            }
         } else {
            rm = 0.5F;
            gm = 0.5F;
            bm = 1.2F;
         }

         PostPasses.color_dispersion.process(tmp, Minecraft.getInstance().getMainRenderTarget(), 1.0F + 0.15F * t, 1.0F + 0.075F * t, 1.0F, rm, gm, bm);
      }
   }

   public enum Type {
      PREV,
      POST;
   }
}
