package com.hm.efn.client.particle;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.DisorderEffek;
import com.hm.efn.client.particle.rendertype.EFNHitParticle;
import com.hm.efn.client.render.EFNRenderType;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.util.EffekUnits;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class SlashParticle extends EFNHitParticle {
   public SlashParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
      super(world, x, y, z, animatedSprite);
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.quadSize = 2.0F;
      this.lifetime = 14;
      this.level.addParticle((ParticleOptions)EFNParticles.COLOR_SLASH.get(), x, y, z, 0.0, 0.0, 0.0);
      this.level.addParticle((ParticleOptions)EFNParticles.COLOR_RING_SLASH.get(), x, y, z, 0.0, 0.0, 0.0);
      this.level.addParticle((ParticleOptions)EFNParticles.SLASH_RING.get(), x, y, z, 0.0, 0.0, 0.0);
      this.level.addParticle((ParticleOptions)EFNParticles.COLOR_SLASH2.get(), x, y + 0.2, z, 0.0, 0.0, 0.0);
      if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.YAMATO_JUDGEMENT_CUT_VFX.get()) {
         DisorderEffek.playDisorder(DisorderEffek.Type.LEVEL1, this.level, x, y - 1.5, z, 0.7F);
      }
   }

   private Vector4f applyVertexDistortion(Vec3 position, float time, float intensity, Matrix4f matrix) {
      float timeFactor = time * 1.5F;
      float x = (float)position.x;
      float y = (float)position.y;
      float z = (float)position.z;
      float offsetX = Mth.sin(timeFactor + x * 0.5F) * intensity + Mth.cos(timeFactor * 0.8F + z * 0.3F) * intensity * 0.6F;
      float offsetY = Mth.sin(timeFactor * 1.2F + x * 0.2F) * intensity * 0.4F;
      float offsetZ = Mth.cos(timeFactor * 0.7F + z * 0.4F) * intensity - Mth.sin(timeFactor * 0.9F) * intensity * 0.3F;
      return new Vector4f(x + offsetX, y + offsetY, z + offsetZ, 1.0F).mul(matrix);
   }

   @Override
   public void render(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float partialTick) {
      if (PostEffectPipelines.isActive()) {
         EFNRenderType.getBloomRenderTypeByTexture(this.sprite.atlasLocation()).callPipeline();
         Vec3 cameraPos = camera.getPosition();
         float x = (float)(this.x - cameraPos.x());
         float y = (float)(this.y - cameraPos.y());
         float z = (float)(this.z - cameraPos.z());
         Quaternionf rotation = camera.rotation();
         Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
         };
         float size = this.quadSize;
         float time = (this.age + partialTick) * 3.0F;
         float intensity = 0.18F;
         Matrix4f transformMatrix = new Matrix4f();
         transformMatrix.translation(x, y, z);
         transformMatrix.rotate(rotation);
         transformMatrix.scale(size);
         Vector4f[] distortedVertices = new Vector4f[4];

         for (int i = 0; i < 4; i++) {
            Vec3 vertexPos = new Vec3(vertices[i].x(), vertices[i].y(), vertices[i].z());
            distortedVertices[i] = this.applyVertexDistortion(vertexPos, time, intensity, transformMatrix);
         }

         float u0 = this.sprite.getU0();
         float u1 = this.sprite.getU1();
         float v0 = this.sprite.getV0();
         float v1 = this.sprite.getV1();
         int light = this.getLightColor(partialTick);
         vertexConsumer.addVertex(distortedVertices[0].x(), distortedVertices[0].y(), distortedVertices[0].z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u1, v1)
            .setLight(light)
            ;
         vertexConsumer.addVertex(distortedVertices[1].x(), distortedVertices[1].y(), distortedVertices[1].z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u1, v0)
            .setLight(light)
            ;
         vertexConsumer.addVertex(distortedVertices[2].x(), distortedVertices[2].y(), distortedVertices[2].z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u0, v0)
            .setLight(light)
            ;
         vertexConsumer.addVertex(distortedVertices[3].x(), distortedVertices[3].y(), distortedVertices[3].z())
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setUv(u0, v1)
            .setLight(light)
            ;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new SlashParticle(worldIn, x, y, z, this.spriteSet);
      }
   }
}
