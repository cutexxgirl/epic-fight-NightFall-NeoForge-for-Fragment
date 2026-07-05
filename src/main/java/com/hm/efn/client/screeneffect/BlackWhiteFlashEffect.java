package com.hm.efn.client.screeneffect;

import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.guhao.vix.client.screeneffect.ScreenEffectBase;
import com.guhao.vix.client.screeneffect.ScreenEffectBase.SE_Pipeline;
import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.OjangUtils;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.registries.PostPasses;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class BlackWhiteFlashEffect extends ScreenEffectBase {
   static ResourceLocation bw_contrast = OjangUtils.newRL("efn", "bw_contrast");
   public final SE_Pipeline ppl;
   public boolean should_render = true;
   private final BlackWhiteFlashEffect.ImpactMode impactMode;
   private final float contrast;
   private final float brightness;

   public BlackWhiteFlashEffect(Vec3 pos) {
      this(pos, BlackWhiteFlashEffect.ImpactMode.HEAVY);
   }

   public BlackWhiteFlashEffect(Vec3 pos, BlackWhiteFlashEffect.ImpactMode mode) {
      this(pos, mode, 2.25F, 0.65F);
   }

   public BlackWhiteFlashEffect(Vec3 pos, BlackWhiteFlashEffect.ImpactMode mode, float contrast, float brightness) {
      super(bw_contrast, pos);
      this.impactMode = mode;
      this.contrast = contrast;
      this.brightness = brightness;
      this.ppl = new BlackWhiteFlashEffect.BWC_Pipeline(this);
      this.lifetime = (int)Math.ceil(mode.durationSec * 20.0) + 1;
   }

   public void tick() {
      if (++this.age > this.lifetime) {
         this.should_render = false;
      }
   }

   public Pipeline getPipeline() {
      return this.ppl;
   }

   public static class BWC_Pipeline extends SE_Pipeline<BlackWhiteFlashEffect> {
      static ResourceLocation bw_tmp = OjangUtils.newRL("efn", "bw_tmp");
      private long startNanos = 0L;

      public BWC_Pipeline(BlackWhiteFlashEffect effect) {
         super(BlackWhiteFlashEffect.bw_contrast, effect);
         this.priority = 9999;
      }

      private static float[] worldToScreenUV(Vec3 worldPos) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level == null) {
            return new float[]{0.5F, 0.5F};
         }

         Camera camera = mc.gameRenderer.getMainCamera();
         Vec3 camPos = camera.getPosition();
         float dx = (float)(worldPos.x - camPos.x);
         float dy = (float)(worldPos.y - camPos.y);
         float dz = (float)(worldPos.z - camPos.z);
         float yaw = (float)Math.toRadians(-camera.getYRot());
         float pitch = (float)Math.toRadians(-camera.getXRot());
         float cosYaw = (float)Math.cos(yaw);
         float sinYaw = (float)Math.sin(yaw);
         float cosPitch = (float)Math.cos(pitch);
         float sinPitch = (float)Math.sin(pitch);
         float fx = cosPitch * sinYaw;
         float fy = -sinPitch;
         float fz = cosPitch * cosYaw;
         float rx = cosYaw;
         float rz = -sinYaw;
         float ux = sinPitch * sinYaw;
         float uy = cosPitch;
         float uz = sinPitch * cosYaw;
         float dotForward = dx * fx + dy * fy + dz * fz;
         if (dotForward <= 0.01F) {
            return new float[]{0.5F, 0.5F};
         }

         float dotRight = dx * rx + dz * rz;
         float dotUp = dx * ux + dy * uy + dz * uz;
         float fov = 70.0F;
         float aspect = (float)mc.getWindow().getWidth() / mc.getWindow().getHeight();
         float halfHeight = dotForward * (float)Math.tan(Math.toRadians(fov / 2.0));
         float halfWidth = halfHeight * aspect;
         float screenX = dotRight / halfWidth;
         float screenY = -dotUp / halfHeight;
         float uvX = screenX * 0.5F + 0.5F;
         float uvY = screenY * 0.5F + 0.5F;
         return new float[]{Math.max(0.0F, Math.min(1.0F, uvX)), Math.max(0.0F, Math.min(1.0F, uvY))};
      }

      public void PostEffectHandler() {
         if ((Boolean)EFNClientConfig.IMPACT_FRAME.get()) {
            long now = System.nanoTime();
            if (this.startNanos == 0L) {
               this.startNanos = now;
            }

            BlackWhiteFlashEffect.ImpactMode mode = ((BlackWhiteFlashEffect)this.effect).impactMode;
            double elapsedSec = (now - this.startNanos) / 1.0E9;
            float time = (float)Math.min(elapsedSec / mode.durationSec, 1.0);
            if (((BlackWhiteFlashEffect)this.effect).should_render && !(time >= 1.0F)) {
               RenderTarget tmp = TargetManager.getTarget(bw_tmp);
               PostPasses.blit.process(Minecraft.getInstance().getMainRenderTarget(), tmp);
               float[] focalUV = worldToScreenUV(((BlackWhiteFlashEffect)this.effect).pos);
               PostPasses.black_white_contrast
                  .process(
                     tmp,
                     Minecraft.getInstance().getMainRenderTarget(),
                     ((BlackWhiteFlashEffect)this.effect).contrast,
                     ((BlackWhiteFlashEffect)this.effect).brightness,
                     time,
                     mode.intensity,
                     mode.speed,
                     mode.modeValue,
                     mode.impactThreshold,
                     mode.impactThresholdLerp,
                     focalUV[0],
                     focalUV[1],
                     mode.chromaticStrength,
                     mode.lensDistortStrength
                  );
            }
         }
      }
   }

   public enum ImpactMode {
      LIGHT(0.15F, 0.0F, 1.3F, 0.9F, 0.35F, 0.1F, 0.05F, -0.3F),
      HEAVY(0.5F, 1.0F, 1.0F, 0.85F, 0.45F, 0.12F, 0.03F, -0.25F);

      final float durationSec;
      final float modeValue;
      final float speed;
      final float intensity;
      final float impactThreshold;
      final float impactThresholdLerp;
      final float chromaticStrength;
      final float lensDistortStrength;

      ImpactMode(
         float durationSec,
         float modeValue,
         float speed,
         float intensity,
         float impactThreshold,
         float impactThresholdLerp,
         float chromaticStrength,
         float lensDistortStrength
      ) {
         this.durationSec = durationSec;
         this.modeValue = modeValue;
         this.speed = speed;
         this.intensity = intensity;
         this.impactThreshold = impactThreshold;
         this.impactThresholdLerp = impactThresholdLerp;
         this.chromaticStrength = chromaticStrength;
         this.lensDistortStrength = lensDistortStrength;
      }
   }
}
