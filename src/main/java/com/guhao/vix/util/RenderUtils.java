package com.guhao.vix.util;

import com.guhao.vix.VIX;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;

public class RenderUtils {
   public static final int EmissiveLightPos = 15728880;
   private static final int GL_CLAMP_TO_EDGE = 33071;
   private static final int GL_TEXTURE_LOD_BIAS = 34049;
   private static final int GL_TEXTURE_MAX_ANISOTROPY_EXT = 34046;
   private static int lastTexId = -1;

   public static void GLSetTexture(ResourceLocation resourceLocation) {
      if (resourceLocation == null) {
         return;
      }

      TextureManager textureManager = Minecraft.getInstance().getTextureManager();
      AbstractTexture texture = textureManager.getTexture(resourceLocation);
      int textureId = texture.getId();
      if (textureId != lastTexId) {
         RenderSystem.bindTexture(textureId);
         RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
         RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -1.0F);
         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 16.0F);
         lastTexId = textureId;
      }

      RenderSystem.setShaderTexture(0, textureId);
   }

   public static void resetTextureParams() {
      if (lastTexId != -1) {
         RenderSystem.bindTexture(lastTexId);
         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0.0F);
         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 1.0F);
         lastTexId = -1;
      }
   }

   public static void AddParticle(ClientLevel level, Particle particle) {
      try {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.level != level) {
            VIX.LOGGER.warn("[ParticleEngine] Tried to add a particle to a non-current level");
            return;
         }

         minecraft.particleEngine.add(particle);
      } catch (Exception exception) {
         VIX.LOGGER.debug("Particle add failed: {}", exception.getMessage());
      }
   }

   public static void RenderQuadFaceOnCamera(
      VertexConsumer vertexConsumer,
      Camera camera,
      float x,
      float y,
      float z,
      float red,
      float green,
      float blue,
      float alpha,
      float size,
      float ignored
   ) {
      RenderQuadFaceOnCamera2(vertexConsumer, camera, x, y, z, red, green, blue, alpha, size);
   }

   public static void RenderQuadFaceOnCamera2(
      VertexConsumer vertexConsumer,
      Camera camera,
      float x,
      float y,
      float z,
      float red,
      float green,
      float blue,
      float alpha,
      float size
   ) {
      Vec3 cameraPosition = camera.getPosition();
      float renderX = (float)(x - cameraPosition.x());
      float renderY = (float)(y - cameraPosition.y());
      float renderZ = (float)(z - cameraPosition.z());
      Quaternionf rotation = camera.rotation();

      addCameraQuadVertex(vertexConsumer, rotation, renderX, renderY, renderZ, -size, -size, 0.0F, 0.0F, red, green, blue, alpha);
      addCameraQuadVertex(vertexConsumer, rotation, renderX, renderY, renderZ, -size, size, 0.0F, 1.0F, red, green, blue, alpha);
      addCameraQuadVertex(vertexConsumer, rotation, renderX, renderY, renderZ, size, size, 1.0F, 1.0F, red, green, blue, alpha);
      addCameraQuadVertex(vertexConsumer, rotation, renderX, renderY, renderZ, size, -size, 1.0F, 0.0F, red, green, blue, alpha);
   }

   public static void translateStack(PoseStack stack, OpenMatrix4f matrix) {
      stack.translate(matrix.m30, matrix.m31, matrix.m32);
   }

   public static void rotateStack(PoseStack stack, OpenMatrix4f matrix) {
      OpenMatrix4f transposed = OpenMatrix4f.transpose(matrix, new OpenMatrix4f());
      stack.mulPose(getQuaternionFromMatrix(transposed));
   }

   public static void scaleStack(PoseStack stack, OpenMatrix4f matrix) {
      OpenMatrix4f transposed = OpenMatrix4f.transpose(matrix, new OpenMatrix4f());
      Vector3f scale = getScaleVectorFromMatrix(transposed);
      stack.scale(scale.x(), scale.y(), scale.z());
   }

   private static void addCameraQuadVertex(
      VertexConsumer vertexConsumer,
      Quaternionf rotation,
      float renderX,
      float renderY,
      float renderZ,
      float localX,
      float localY,
      float u,
      float v,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      Vector3f vertex = new Vector3f(localX, localY, -0.2F);
      vertex.rotate(rotation);
      vertex.add(renderX, renderY, renderZ);
      vertexConsumer.addVertex(vertex.x(), vertex.y(), vertex.z()).setUv(u, v).setColor(red, green, blue, alpha).setLight(EmissiveLightPos);
   }

   private static Vector3f getScaleVectorFromMatrix(OpenMatrix4f matrix) {
      Vec3f x = new Vec3f(matrix.m00, matrix.m10, matrix.m20);
      Vec3f y = new Vec3f(matrix.m01, matrix.m11, matrix.m21);
      Vec3f z = new Vec3f(matrix.m02, matrix.m12, matrix.m22);
      return new Vector3f(x.length(), y.length(), z.length());
   }

   private static Quaternionf getQuaternionFromMatrix(OpenMatrix4f matrix) {
      OpenMatrix4f transposed = matrix.transpose(null);
      Matrix4f jomlMatrix = new Matrix4f(
         transposed.m00,
         transposed.m10,
         transposed.m20,
         transposed.m30,
         transposed.m01,
         transposed.m11,
         transposed.m21,
         transposed.m31,
         transposed.m02,
         transposed.m12,
         transposed.m22,
         transposed.m32,
         transposed.m03,
         transposed.m13,
         transposed.m23,
         transposed.m33
      );
      return jomlMatrix.getUnnormalizedRotation(new Quaternionf());
   }
}
