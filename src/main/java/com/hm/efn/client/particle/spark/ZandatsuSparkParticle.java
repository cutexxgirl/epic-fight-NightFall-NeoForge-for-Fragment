package com.hm.efn.client.particle.spark;

import com.hm.efn.particle.EFNParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;

@OnlyIn(Dist.CLIENT)
public class ZandatsuSparkParticle extends TextureSheetParticle {
   private final ZandatsuSparkParticle.PhysicsType physicsType;
   private final float baseSize;
   private final float rotationSpeed;

   public ZandatsuSparkParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, ZandatsuSparkParticle.PhysicsType physicsType) {
      super(level, x, y, z);
      this.x = x;
      this.y = y;
      this.z = z;
      this.rCol = 0.1F + this.random.nextFloat() * 0.2F;
      this.gCol = 0.7F + this.random.nextFloat() * 0.3F;
      this.bCol = 0.9F + this.random.nextFloat() * 0.2F;
      this.baseSize = physicsType == ZandatsuSparkParticle.PhysicsType.NORMAL
         ? (this.random.nextFloat() * 0.032F + 0.016F) * 0.4F
         : (this.random.nextFloat() * 0.048F + 0.032F) * 0.4F;
      this.quadSize = this.baseSize;
      this.lifetime = (physicsType == ZandatsuSparkParticle.PhysicsType.NORMAL ? 35 : 12) + this.random.nextInt(25);
      this.hasPhysics = true;
      this.gravity = physicsType == ZandatsuSparkParticle.PhysicsType.NORMAL
         ? 3.5F
         : (physicsType == ZandatsuSparkParticle.PhysicsType.EXPANSIVE ? 0.1F : 0.4F);
      this.roll = this.random.nextFloat() * 360.0F;
      this.oRoll = this.roll;
      this.rotationSpeed = (this.random.nextFloat() - 0.5F) * 0.8F;
      Vec3 deltaMovement = physicsType.function.getDeltaMovement(xd, yd, zd);
      this.xd = deltaMovement.x * (1.5 + this.random.nextDouble() * 1.2);
      this.yd = deltaMovement.y * (1.5 + this.random.nextDouble() * 1.2);
      this.zd = deltaMovement.z * (1.5 + this.random.nextDouble() * 1.2);
      this.physicsType = physicsType;
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return EpicFightParticleRenderTypes.LIGHTNING;
   }

   public void tick() {
      super.tick();
      this.oRoll = this.roll;
      this.roll = this.roll + this.rotationSpeed;
      float lifeProgress = (float)this.age / this.lifetime;
      if (this.physicsType == ZandatsuSparkParticle.PhysicsType.EXPANSIVE) {
         if (lifeProgress < 0.4F) {
            this.quadSize = this.baseSize * (1.0F + lifeProgress * 1.2F);
         } else {
            this.quadSize = this.baseSize * (1.48F - (lifeProgress - 0.4F) * 0.8F);
         }
      } else if (this.physicsType == ZandatsuSparkParticle.PhysicsType.CONTRACTIVE) {
         this.quadSize = this.baseSize * (1.0F - lifeProgress * 0.6F);
      } else {
         this.quadSize = this.baseSize * (1.0F + lifeProgress * 0.2F - lifeProgress * lifeProgress * 0.6F);
      }

      if (lifeProgress < 0.4F) {
         float intensity = 1.0F + lifeProgress * 0.5F;
         this.rCol = 0.15F * intensity;
         this.gCol = 0.85F * intensity;
         this.bCol = intensity;
      } else if (lifeProgress < 0.8F) {
         float phase = (lifeProgress - 0.4F) / 0.4F;
         this.rCol = 0.15F + 0.25F * phase;
         this.gCol = 0.85F - 0.35F * phase;
         this.bCol = 1.0F - 0.2F * phase;
      } else {
         float fade = (lifeProgress - 0.8F) / 0.2F;
         this.rCol *= 1.0F - fade;
         this.gCol *= 1.0F - fade;
         this.bCol *= 1.0F - fade;
      }

      if (this.physicsType == ZandatsuSparkParticle.PhysicsType.EXPANSIVE) {
         this.xd *= 0.96;
         this.yd *= 0.96;
         this.zd *= 0.96;
      } else if (this.physicsType == ZandatsuSparkParticle.PhysicsType.CONTRACTIVE) {
         this.xd *= 0.98;
         this.yd *= 0.98;
         this.zd *= 0.98;
      } else {
         this.xd *= 0.95;
         this.yd *= 0.95;
         this.zd *= 0.95;
      }

      if (this.random.nextInt(3) == 0) {
         this.xd = this.xd + (this.random.nextDouble() - 0.5) * 0.04;
         this.yd = this.yd + (this.random.nextDouble() - 0.5) * 0.04;
         this.zd = this.zd + (this.random.nextDouble() - 0.5) * 0.04;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ContractiveDustProvider implements ParticleProvider<SimpleParticleType> {
      protected SpriteSet sprite;

      public ContractiveDustProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         ZandatsuSparkParticle SparkParticle = new ZandatsuSparkParticle(
            worldIn, x, y, z, xSpeed, ySpeed, zSpeed, ZandatsuSparkParticle.PhysicsType.CONTRACTIVE
         );
         SparkParticle.pickSprite(this.sprite);
         return SparkParticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ContractiveMetaParticle extends NoRenderParticle {
      private final double radius;
      private final int density;

      public ContractiveMetaParticle(ClientLevel level, double x, double y, double z, double radius, int lifetime, int density) {
         super(level, x, y, z);
         this.radius = radius;
         this.lifetime = lifetime;
         this.density = (int)(density * 1.3);
      }

      public void tick() {
         super.tick();

         for (int x = -1; x <= 1; x += 2) {
            for (int y = -1; y <= 1; y += 2) {
               for (int z = -1; z <= 1; z += 2) {
                  for (int i = 0; i < this.density; i++) {
                     Vec3 rand = new Vec3(Math.random() * x, Math.random() * y * 0.8, Math.random() * z).normalize().scale(this.radius);
                     this.level
                        .addParticle(
                           (ParticleOptions)EFNParticles.SPARK_CONTRACTILE_ZANDATSU.get(),
                           this.x + rand.x,
                           this.y + rand.y,
                           this.z + rand.z,
                           -rand.x,
                           -rand.y,
                           -rand.z
                        );
                  }
               }
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public static class Provider implements ParticleProvider<SimpleParticleType> {
         public Particle createParticle(
            SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
         ) {
            return new ZandatsuSparkParticle.ContractiveMetaParticle(
               worldIn, x, y, z, xSpeed, (int)Double.doubleToLongBits(ySpeed), (int)Double.doubleToLongBits(zSpeed)
            );
         }
      }
   }

   @FunctionalInterface
   interface DeltaMovementFunction {
      Vec3 getDeltaMovement(double var1, double var3, double var5);
   }

   @OnlyIn(Dist.CLIENT)
   public static class ExpansiveDustProvider implements ParticleProvider<SimpleParticleType> {
      protected SpriteSet sprite;

      public ExpansiveDustProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         ZandatsuSparkParticle SparkParticle = new ZandatsuSparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, ZandatsuSparkParticle.PhysicsType.EXPANSIVE);
         SparkParticle.pickSprite(this.sprite);
         return SparkParticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ExpansiveMetaParticle extends NoRenderParticle {
      public ExpansiveMetaParticle(ClientLevel level, double x, double y, double z, double radius, int density) {
         super(level, x, y, z);
         int newDensity = (int)(density * 1.3);

         for (int vx = -1; vx <= 1; vx += 2) {
            for (int vz = -1; vz <= 1; vz += 2) {
               for (int i = 0; i < newDensity; i++) {
                  Vec3 rand = new Vec3(Math.random() * vx, Math.random() * 0.8, Math.random() * vz).normalize().scale(radius);
                  level.addParticle((ParticleOptions)EFNParticles.SPARK_EXPANSIVE_ZANDATSU.get(), x, y, z, rand.x, rand.y, rand.z);
               }
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public static class Provider implements ParticleProvider<SimpleParticleType> {
         public Particle createParticle(
            SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
         ) {
            return new ZandatsuSparkParticle.ExpansiveMetaParticle(worldIn, x, y, z, xSpeed, (int)Double.doubleToLongBits(ySpeed));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NormalDustProvider implements ParticleProvider<SimpleParticleType> {
      protected SpriteSet sprite;

      public NormalDustProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         ZandatsuSparkParticle SparkParticle = new ZandatsuSparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, ZandatsuSparkParticle.PhysicsType.NORMAL);
         SparkParticle.pickSprite(this.sprite);
         return SparkParticle;
      }
   }

   public enum PhysicsType {
      EXPANSIVE(Vec3::new),
      CONTRACTIVE((dx, dy, dz) -> new Vec3(dx * 0.08, dy * 0.08, dz * 0.08)),
      NORMAL(Vec3::new);

      final ZandatsuSparkParticle.DeltaMovementFunction function;

      PhysicsType(ZandatsuSparkParticle.DeltaMovementFunction function) {
         this.function = function;
      }
   }
}
