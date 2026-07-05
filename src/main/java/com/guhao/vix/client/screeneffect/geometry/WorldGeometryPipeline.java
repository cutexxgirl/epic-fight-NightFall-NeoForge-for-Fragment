package com.guhao.vix.client.screeneffect.geometry;

import com.guhao.vix.client.screeneffect.ScreenEffectBase;
import com.guhao.vix.client.screeneffect.WorldGeometryEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public abstract class WorldGeometryPipeline<E extends WorldGeometryEffect> extends ScreenEffectBase.SE_Pipeline<E> {
   public WorldGeometryPipeline(ResourceLocation name, E effect) {
      super(name, effect);
   }

   @Override
   public void PostEffectHandler() {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getMainRenderTarget().bindWrite(false);
      PoseStack poseStack = new PoseStack();
      Camera camera = minecraft.gameRenderer.getMainCamera();
      Quaternionf rotation = new Quaternionf(camera.rotation()).conjugate();
      poseStack.mulPose(rotation);
      Vec3 cameraPosition = camera.getPosition();
      poseStack.translate(-cameraPosition.x(), -cameraPosition.y(), -cameraPosition.z());
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      this.renderGeometry(poseStack);
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
   }

   protected abstract void renderGeometry(PoseStack poseStack);
}
