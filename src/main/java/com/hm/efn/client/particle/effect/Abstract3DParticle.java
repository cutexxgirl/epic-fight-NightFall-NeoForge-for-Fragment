package com.hm.efn.client.particle.effect;

import com.hm.efn.entity.Abstract3DParticleEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Abstract3DParticle extends TextureSheetParticle {
   protected final SpriteSet animatedSprite;
   protected final Abstract3DParticleEntity boundEntity;
   protected final float baseQuadSize;
   private final Matrix4f reusableMatrix = new Matrix4f();
   private final Vector3f[] reusableVertices = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};

   protected Abstract3DParticle(ClientLevel level, Abstract3DParticleEntity boundEntity, SpriteSet animatedSprite) {
      super(level, boundEntity.getX(), boundEntity.getY(), boundEntity.getZ());
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.boundEntity = boundEntity;
      this.animatedSprite = animatedSprite;
      this.setSpriteFromAge(animatedSprite);
      this.lifetime = boundEntity.getMaxLifetime();
      this.baseQuadSize = boundEntity.getScale();
   }

   public void tick() {
      if (this.boundEntity != null && this.boundEntity.isAlive()) {
         this.setPos(this.boundEntity.getX(), this.boundEntity.getY(), this.boundEntity.getZ());
         if (this.age++ >= this.lifetime) {
            this.remove();
         } else {
            this.setSpriteFromAge(this.animatedSprite);
         }
      } else {
         this.remove();
      }
   }

   public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTicks) {
      if (this.boundEntity != null) {
         Vec3 camPos = camera.getPosition();
         Vec3 exactEntityPos = this.boundEntity.getPosition(partialTicks);
         float renderX = (float)(exactEntityPos.x - camPos.x);
         float renderY = (float)(exactEntityPos.y - camPos.y);
         float renderZ = (float)(exactEntityPos.z - camPos.z);
         this.reusableMatrix.identity();
         this.reusableMatrix.translate(renderX, renderY, renderZ);
         this.reusableMatrix.rotate(Axis.YP.rotationDegrees(this.boundEntity.getYRotOffset()));
         this.reusableMatrix.rotate(Axis.XP.rotationDegrees(this.boundEntity.getXRotOffset()));
         this.reusableMatrix.rotate(Axis.ZP.rotationDegrees(this.boundEntity.getZRotOffset()));
         float currentSize = this.baseQuadSize * this.boundEntity.getScale();
         float u0 = this.getU0();
         float u1 = this.getU1();
         float v0 = this.getV0();
         float v1 = this.getV1();
         int light = this.getLightColor(partialTicks);
         this.reusableVertices[0].set(currentSize, -currentSize, 0.0F);
         this.reusableVertices[1].set(-currentSize, -currentSize, 0.0F);
         this.reusableVertices[2].set(-currentSize, currentSize, 0.0F);
         this.reusableVertices[3].set(currentSize, currentSize, 0.0F);

         for (int i = 0; i < 4; i++) {
            this.reusableVertices[i].mulPosition(this.reusableMatrix);
         }

         buffer.addVertex(this.reusableVertices[0].x(), this.reusableVertices[0].y(), this.reusableVertices[0].z())
            .setUv(u1, v1)
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setLight(light)
            ;
         buffer.addVertex(this.reusableVertices[1].x(), this.reusableVertices[1].y(), this.reusableVertices[1].z())
            .setUv(u0, v1)
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setLight(light)
            ;
         buffer.addVertex(this.reusableVertices[2].x(), this.reusableVertices[2].y(), this.reusableVertices[2].z())
            .setUv(u0, v0)
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setLight(light)
            ;
         buffer.addVertex(this.reusableVertices[3].x(), this.reusableVertices[3].y(), this.reusableVertices[3].z())
            .setUv(u1, v0)
            .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
            .setLight(light)
            ;
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public int getLightColor(float partialTick) {
      return 15728880;
   }
}
