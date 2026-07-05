package com.hm.efn.client.particle;

import com.hm.efn.client.particle.efnparticletype.EFNParticleRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import yesman.epicfight.client.particle.HitParticle;

@OnlyIn(Dist.CLIENT)
public class SlashColorRingParticle extends HitParticle {
   private float hue = this.random.nextFloat();

   public SlashColorRingParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
      super(world, x, y, z, animatedSprite);
      this.updateColorFromHue();
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.quadSize = 1.8F;
      this.lifetime = 14;
      this.alpha = 0.2F;
      this.setSpriteFromAge(animatedSprite);
   }

   private void updateColorFromHue() {
      int rgb = Color.HSBtoRGB(this.hue, 1.0F, 1.0F);
      this.rCol = (rgb >> 16 & 0xFF) / 255.0F;
      this.gCol = (rgb >> 8 & 0xFF) / 255.0F;
      this.bCol = (rgb & 0xFF) / 255.0F;
   }

   public void tick() {
      super.tick();
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.animatedSprite);
      }

      float ageRatio = (float)this.age / this.lifetime;
      this.hue = (this.hue + ageRatio * 0.1F) % 1.0F;
      this.updateColorFromHue();
   }

   public void render(@NotNull VertexConsumer buffer, @NotNull Camera renderInfo, float partialTicks) {
      Vec3 view = renderInfo.getPosition();
      float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - view.x());
      float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - view.y());
      float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - view.z());
      Quaternionf quaternion = this.roll == 0.0F
         ? renderInfo.rotation()
         : new Quaternionf(renderInfo.rotation()).rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
      float size = this.getQuadSize(partialTicks);
      int light = this.getLightColor(partialTicks);
      Random r = new Random();
      this.renderColoredQuad(
         buffer, quaternion, x + r.nextFloat(-0.36F, 0.36F), y + r.nextFloat(-0.36F, 0.36F), z + r.nextFloat(-0.36F, 0.36F), size, 1.0F, 0.0F, 0.0F, light
      );
      this.renderColoredQuad(
         buffer, quaternion, x + r.nextFloat(-0.36F, 0.36F), y + r.nextFloat(-0.36F, 0.36F), z + r.nextFloat(-0.36F, 0.36F), size, 0.0F, 1.0F, 0.0F, light
      );
      this.renderColoredQuad(
         buffer, quaternion, x + r.nextFloat(-0.36F, 0.36F), y + r.nextFloat(-0.36F, 0.36F), z + r.nextFloat(-0.36F, 0.36F), size, 0.0F, 0.0F, 1.0F, light
      );
   }

   private void renderColoredQuad(VertexConsumer buffer, Quaternionf rotation, float x, float y, float z, float size, float r, float g, float b, int light) {
      Vector3f[] vectors = new Vector3f[]{
         new Vector3f(-1.0F, 0.0F, -1.0F), new Vector3f(-1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, -1.0F)
      };

      for (int i = 0; i < 4; i++) {
         vectors[i].rotate(rotation);
         vectors[i].mul(size);
         vectors[i].add(x, y, z);
      }

      float u0 = this.getU0();
      float u1 = this.getU1();
      float v0 = this.getV0();
      float v1 = this.getV1();
      float finalR = r * this.rCol;
      float finalG = g * this.gCol;
      float finalB = b * this.bCol;
      buffer.addVertex(vectors[0].x(), vectors[0].y(), vectors[0].z()).setUv(u1, v1).setColor(finalR, finalG, finalB, this.alpha).setLight(light);
      buffer.addVertex(vectors[1].x(), vectors[1].y(), vectors[1].z()).setUv(u1, v0).setColor(finalR, finalG, finalB, this.alpha).setLight(light);
      buffer.addVertex(vectors[2].x(), vectors[2].y(), vectors[2].z()).setUv(u0, v0).setColor(finalR, finalG, finalB, this.alpha).setLight(light);
      buffer.addVertex(vectors[3].x(), vectors[3].y(), vectors[3].z()).setUv(u0, v1).setColor(finalR, finalG, finalB, this.alpha).setLight(light);
   }

   public ParticleRenderType getRenderType() {
      return EFNParticleRenderTypes.EFN_PARTICLE_BLUR;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new SlashColorRingParticle(worldIn, x, y, z, this.spriteSet);
      }
   }
}
