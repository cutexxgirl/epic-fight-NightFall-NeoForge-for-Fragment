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

public class RedColorEffect extends ScreenEffectBase {
   static final float recovery = 15.0F;
   static ResourceLocation red_color = OjangUtils.newRL("efn", "red_color");
   public final SE_Pipeline ppl;
   public boolean should_render;
   public RedColorEffect.Type type = RedColorEffect.Type.POST;
   public float alpha = 1.0F;
   public float redIntensity = 0.5F;

   public RedColorEffect(Vec3 pos, int lt, boolean should_render) {
      super(red_color, pos);
      this.ppl = new RedColorEffect.RedColor_Pipeline(this);
      this.lifetime = lt;
      this.should_render = should_render;
   }

   public RedColorEffect(Vec3 pos, int lt, boolean should_render, float redIntensity) {
      this(pos, lt, should_render);
      this.redIntensity = redIntensity;
   }

   float getTimeChannel() {
      return this.age < 5 ? this.age / 5.0F : Math.max(Math.min(1.0F, (this.lifetime - this.age) / 15.0F), 0.0F);
   }

   public float getAlpha() {
      return this.alpha * this.getTimeChannel();
   }

   public float getRedIntensity() {
      return this.redIntensity * this.getTimeChannel();
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

   private float redIntensity2() {
      return this.redIntensity;
   }

   public static class RedColor_Pipeline extends SE_Pipeline<RedColorEffect> {
      static ResourceLocation red_color_tmp = OjangUtils.newRL("efn", "red_color_tmp");

      public RedColor_Pipeline(RedColorEffect effect) {
         super(RedColorEffect.red_color, effect);
         this.priority = 99;
      }

      public void PostEffectHandler() {
         RenderTarget tmp = TargetManager.getTarget(red_color_tmp);
         PostPasses.blit.process(Minecraft.getInstance().getMainRenderTarget(), tmp);
         PostPasses.red_color
            .process(
               tmp,
               Minecraft.getInstance().getMainRenderTarget(),
               ((RedColorEffect)this.effect).getRedIntensity(),
               ((RedColorEffect)this.effect).getAlpha(),
               (((RedColorEffect)this.effect).age + this.partialTicks) * 0.05F,
               ((RedColorEffect)this.effect).redIntensity2()
            );
      }
   }

   public enum Type {
      PREV,
      POST;
   }
}
