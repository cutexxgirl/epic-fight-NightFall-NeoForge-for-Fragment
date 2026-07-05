package com.hm.efn.client.screeneffect;

import com.guhao.vix.client.screeneffect.ScreenEffectBase;
import com.guhao.vix.client.screeneffect.ScreenEffectBase.SE_Pipeline;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.hm.efn.registries.PostPasses;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class HsvFilterEffect extends ScreenEffectBase {
   static final float recovery = 15.0F;
   static ResourceLocation hsv_filter = OjangUtils.newRL("efn", "hsv_filter");
   public final SE_Pipeline ppl;
   public boolean should_render;
   public HsvFilterEffect.Type type = HsvFilterEffect.Type.POST;
   public float alpha = 1.0F;

   public HsvFilterEffect(Vec3 pos, int lt, boolean should_render) {
      super(hsv_filter, pos);
      this.ppl = new HsvFilterEffect.HsvFilter_Pipeline(this);
      this.lifetime = lt;
      this.should_render = should_render;
   }

   float getTimeChannel() {
      return this.age < 5 ? this.age / 5.0F : Math.max(Math.min(1.0F, (this.lifetime - this.age) / 15.0F), 0.0F);
   }

   public float getAlpha() {
      return this.alpha * this.getTimeChannel();
   }

   public boolean shouldPost(Camera camera, Frustum clippingHelper) {
      if (!this.should_render) {
         return false;
      }

      double distance = this.pos.subtract(camera.getPosition()).length();
      if (distance >= 24.0) {
         this.alpha = (float)Math.max(0.0, -0.125 * distance + 4.0);
      }

      return distance < 32.0 && this.alpha >= 0.01F;
   }

   public SE_Pipeline getPipeline() {
      return this.ppl;
   }

   public static class HsvFilter_Pipeline extends SE_Pipeline<HsvFilterEffect> {
      static ResourceLocation hsv_filter_tmp = OjangUtils.newRL("efn", "hsv_filter_tmp");

      public HsvFilter_Pipeline(HsvFilterEffect effect) {
         super(HsvFilterEffect.hsv_filter, effect);
         this.priority = 101;
      }

      public void PostEffectHandler() {
         RenderTarget tmp = TargetManager.getTarget(hsv_filter_tmp);
         PostPasses.blit.process(Minecraft.getInstance().getMainRenderTarget(), tmp);
         PostPasses.hsv_filter.process(tmp, Minecraft.getInstance().getMainRenderTarget(), 1.0F, 0.97F, ((HsvFilterEffect)this.effect).getAlpha());
      }
   }

   public enum Type {
      PREV,
      POST;
   }
}
