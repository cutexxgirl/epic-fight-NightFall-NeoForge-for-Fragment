package com.hm.efn.client.particle.rendertype;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.hm.efn.client.render.EFNRenderType;
import com.hm.efn.client.render.custom.BloomParticleRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class EFNTextureSheetParticle extends TextureSheetParticle {
   private BloomParticleRenderType renderType;
   private float bloomScale = 1.0F;

   protected EFNTextureSheetParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
      super(pLevel, pX, pY, pZ);
   }

   protected EFNTextureSheetParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
      super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
   }

   public void setBloomScale(float scale) {
      this.bloomScale = Math.max(0.1F, Math.min(2.0F, scale));
   }

   public int getLightColor(float partialTick) {
      BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
      return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      if (this.renderType == null) {
         this.renderType = EFNRenderType.getBloomRenderTypeByTexture(this.sprite.atlasLocation());
      }

      return (ParticleRenderType)(this.renderType != null ? this.renderType : ParticleRenderType.NO_RENDER);
   }

   public void render(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float partialTick) {
      if (PostEffectPipelines.isActive()) {
         EFNRenderType.getBloomRenderTypeByTexture(this.sprite.atlasLocation()).callPipeline();
         Vec3 cameraPos = camera.getPosition();
         float x = (float)(Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
         float y = (float)(Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
         float z = (float)(Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());
         Quaternionf rotation;
         if (this.roll == 0.0F) {
            rotation = camera.rotation();
         } else {
            rotation = new Quaternionf(camera.rotation());
            rotation.rotateZ(Mth.lerp(partialTick, this.oRoll, this.roll));
         }

         Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
         };
         float size = this.quadSize;

         for (int i = 0; i < 4; i++) {
            vertices[i].rotate(rotation);
            vertices[i].mul(size * this.bloomScale);
            vertices[i].add(x, y, z);
         }

         float u0 = this.sprite.getU0();
         float u1 = this.sprite.getU1();
         float v0 = this.sprite.getV0();
         float v1 = this.sprite.getV1();
         int light = this.getLightColor(partialTick);
         vertexConsumer.addVertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u1, v1)
            .setLight(light)
            ;
         vertexConsumer.addVertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u1, v0)
            .setLight(light)
            ;
         vertexConsumer.addVertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u0, v0)
            .setLight(light)
            ;
         vertexConsumer.addVertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u0, v1)
            .setLight(light)
            ;
      }
   }
}
