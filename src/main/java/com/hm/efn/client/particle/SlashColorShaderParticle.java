package com.hm.efn.client.particle;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.hm.efn.client.render.EFNRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
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
public class SlashColorShaderParticle extends HitParticle {
   private float hue;
   private final Vector3f[] reusableVerts = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};

   public SlashColorShaderParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
      super(world, x, y, z, animatedSprite);
      this.hue = this.random.nextFloat();
      this.updateColorFromHue();
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.quadSize = 2.1F;
      this.lifetime = 18;
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
      if (PostEffectPipelines.isActive()) {
         EFNRenderType.ChromaticAberrationRenderType(this.sprite.atlasLocation()).callPipeline();
         Vec3 view = renderInfo.getPosition();
         float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - view.x());
         float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - view.y());
         float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - view.z());
         Quaternionf quaternion = this.roll == 0.0F
            ? renderInfo.rotation()
            : new Quaternionf(renderInfo.rotation()).rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
         float size = this.getQuadSize(partialTicks);
         int light = this.getLightColor(partialTicks);
         this.renderColoredQuad(buffer, quaternion, x + this.rf(), y + this.rf(), z + this.rf(), size, 1.0F, 0.0F, 0.0F, light);
         this.renderColoredQuad(buffer, quaternion, x + this.rf(), y + this.rf(), z + this.rf(), size, 0.0F, 1.0F, 0.0F, light);
         this.renderColoredQuad(buffer, quaternion, x + this.rf(), y + this.rf(), z + this.rf(), size, 0.0F, 0.0F, 1.0F, light);
      }
   }

   private float rf() {
      return this.random.nextFloat() * 0.72F - 0.36F;
   }

   private void renderColoredQuad(VertexConsumer buffer, Quaternionf rotation, float x, float y, float z, float size, float r, float g, float b, int light) {
      this.reusableVerts[0].set(-1.0F, -1.0F, 0.0F);
      this.reusableVerts[1].set(-1.0F, 1.0F, 0.0F);
      this.reusableVerts[2].set(1.0F, 1.0F, 0.0F);
      this.reusableVerts[3].set(1.0F, -1.0F, 0.0F);

      for (int i = 0; i < 4; i++) {
         this.reusableVerts[i].rotate(rotation);
         this.reusableVerts[i].mul(size);
         this.reusableVerts[i].add(x, y, z);
      }

      float u0 = this.getU0();
      float u1 = this.getU1();
      float v0 = this.getV0();
      float v1 = this.getV1();
      float finalR = r * this.rCol;
      float finalG = g * this.gCol;
      float finalB = b * this.bCol;
      buffer.addVertex(this.reusableVerts[0].x(), this.reusableVerts[0].y(), this.reusableVerts[0].z())
         .setUv(u1, v1)
         .setColor(finalR, finalG, finalB, this.alpha)
         .setLight(light)
         ;
      buffer.addVertex(this.reusableVerts[1].x(), this.reusableVerts[1].y(), this.reusableVerts[1].z())
         .setUv(u1, v0)
         .setColor(finalR, finalG, finalB, this.alpha)
         .setLight(light)
         ;
      buffer.addVertex(this.reusableVerts[2].x(), this.reusableVerts[2].y(), this.reusableVerts[2].z())
         .setUv(u0, v0)
         .setColor(finalR, finalG, finalB, this.alpha)
         .setLight(light)
         ;
      buffer.addVertex(this.reusableVerts[3].x(), this.reusableVerts[3].y(), this.reusableVerts[3].z())
         .setUv(u0, v1)
         .setColor(finalR, finalG, finalB, this.alpha)
         .setLight(light)
         ;
   }

   public ParticleRenderType getRenderType() {
      return EFNRenderType.ChromaticAberrationRenderType(this.sprite.atlasLocation());
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new SlashColorShaderParticle(worldIn, x, y, z, this.spriteSet);
      }
   }
}
