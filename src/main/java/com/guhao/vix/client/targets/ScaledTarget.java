package com.guhao.vix.client.targets;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;

public class ScaledTarget extends RenderTarget {
   private final float scaleW;
   private final float scaleH;

   public ScaledTarget(float scaleW, float scaleH, int width, int height, boolean useDepth, boolean clearError) {
      super(useDepth);
      this.scaleW = scaleW;
      this.scaleH = scaleH;
      RenderSystem.assertOnRenderThreadOrInit();
      this.resize(width, height, clearError);
   }

   @Override
   public void resize(int width, int height, boolean clearError) {
      super.resize((int)(width * this.scaleW), (int)(height * this.scaleH), clearError);
   }
}
