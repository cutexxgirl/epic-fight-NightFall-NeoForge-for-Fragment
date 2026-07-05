package com.hm.efn.client.particle.rendertype;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.hm.efn.client.render.EFNRenderType;
import com.hm.efn.client.render.custom.BloomParticleRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.client.particle.HitParticle;

@OnlyIn(Dist.CLIENT)
public class EFNRingHitParticle extends HitParticle {
   BloomParticleRenderType renderType = EFNRenderType.getBloomRenderTypeByTexture(this.sprite.atlasLocation());

   public EFNRingHitParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
      super(world, x, y, z, animatedSprite);
   }

   public int getLightColor(float partialTick) {
      BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
      return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
   }

   public boolean shouldCull() {
      return false;
   }

   public ParticleRenderType getRenderType() {
      return this.renderType;
   }

   public void render(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float tick) {
      if (PostEffectPipelines.isActive()) {
         EFNRenderType.getBloomRenderTypeByTexture(this.sprite.atlasLocation()).callPipeline();
         Vec3 cameraPos = camera.getPosition();
         float x = (float)(this.x - cameraPos.x());
         float y = (float)(this.y - cameraPos.y());
         float z = (float)(this.z - cameraPos.z());
         Quaternionf rotation = camera.rotation();
         Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-1.0F, 0.0F, -1.0F), new Vector3f(-1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, -1.0F)
         };
         float size = this.quadSize;

         for (int i = 0; i < 4; i++) {
            vertices[i].rotate(rotation);
            vertices[i].mul(size);
            vertices[i].add(x, y, z);
         }

         float u0 = this.sprite.getU0();
         float u1 = this.sprite.getU1();
         float v0 = this.sprite.getV0();
         float v1 = this.sprite.getV1();
         int light = this.getLightColor(tick);
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
