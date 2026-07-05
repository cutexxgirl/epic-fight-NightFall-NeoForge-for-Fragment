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
public class SparkParticle extends TextureSheetParticle {
   private final SparkParticle.PhysicsType physicsType;
   private final float baseSize;
   private final float rotationSpeed;

   public SparkParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SparkParticle.PhysicsType physicsType) {
      super(level, x, y, z);
      this.x = x;
      this.y = y;
      this.z = z;
      this.rCol = 1.0F;
      this.gCol = 0.7F + this.random.nextFloat() * 0.3F;
      this.bCol = 0.2F + this.random.nextFloat() * 0.2F;
      this.baseSize = physicsType == SparkParticle.PhysicsType.NORMAL
         ? (this.random.nextFloat() * 0.02F + 0.01F) * 0.4F
         : (this.random.nextFloat() * 0.03F + 0.02F) * 0.4F;
      this.quadSize = this.baseSize;
      this.lifetime = (physicsType == SparkParticle.PhysicsType.NORMAL ? 20 : 5) + this.random.nextInt(10);
      this.hasPhysics = true;
      this.gravity = physicsType == SparkParticle.PhysicsType.NORMAL ? 9.8F : (physicsType == SparkParticle.PhysicsType.EXPANSIVE ? 0.4F : 1.2F);
      this.roll = this.random.nextFloat() * 360.0F;
      this.oRoll = this.roll;
      this.rotationSpeed = (this.random.nextFloat() - 0.5F) * 0.5F;
      Vec3 deltaMovement = physicsType.function.getDeltaMovement(xd, yd, zd);
      this.xd = deltaMovement.x * (0.7 + this.random.nextDouble() * 0.6);
      this.yd = deltaMovement.y * (0.7 + this.random.nextDouble() * 0.6);
      this.zd = deltaMovement.z * (0.7 + this.random.nextDouble() * 0.6);
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
      if (this.physicsType == SparkParticle.PhysicsType.EXPANSIVE) {
         this.quadSize = this.baseSize * (1.0F + lifeProgress * 0.8F);
      } else {
         this.quadSize = this.baseSize * (1.0F - lifeProgress * 0.5F);
      }

      this.gCol = 0.7F * (1.0F - lifeProgress * 0.9F);
      this.bCol = 0.2F * (1.0F - lifeProgress * 0.7F);
      if (this.physicsType == SparkParticle.PhysicsType.EXPANSIVE) {
         this.xd *= 0.88;
         this.yd *= 0.88;
         this.zd *= 0.88;
      } else if (this.physicsType == SparkParticle.PhysicsType.CONTRACTIVE) {
         this.xd *= 0.95;
         this.yd *= 0.95;
         this.zd *= 0.95;
      } else {
         this.xd *= 0.92;
         this.yd *= 0.92;
         this.zd *= 0.92;
      }

      if (this.random.nextInt(8) == 0) {
         this.xd = this.xd + (this.random.nextDouble() - 0.5) * 0.015;
         this.yd = this.yd + (this.random.nextDouble() - 0.5) * 0.015;
         this.zd = this.zd + (this.random.nextDouble() - 0.5) * 0.015;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ContractiveDustProvider implements ParticleProvider<SimpleParticleType> {
      protected SpriteSet sprite;

      public ContractiveDustProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         SparkParticle particle = new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, SparkParticle.PhysicsType.CONTRACTIVE);
         particle.pickSprite(this.sprite);
         return particle;
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
                           (ParticleOptions)EFNParticles.SPARK_CONTRACTILE.get(),
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
            return new SparkParticle.ContractiveMetaParticle(
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
         SparkParticle particle = new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, SparkParticle.PhysicsType.EXPANSIVE);
         particle.pickSprite(this.sprite);
         return particle;
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
                  level.addParticle((ParticleOptions)EFNParticles.SPARK_EXPANSIVE.get(), x, y, z, rand.x, rand.y, rand.z);
               }
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public static class Provider implements ParticleProvider<SimpleParticleType> {
         public Particle createParticle(
            SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
         ) {
            return new SparkParticle.ExpansiveMetaParticle(worldIn, x, y, z, xSpeed, (int)Double.doubleToLongBits(ySpeed));
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
         SparkParticle particle = new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, SparkParticle.PhysicsType.NORMAL);
         particle.pickSprite(this.sprite);
         return particle;
      }
   }

   public enum PhysicsType {
      EXPANSIVE(Vec3::new),
      CONTRACTIVE((dx, dy, dz) -> new Vec3(dx * 0.02, dy * 0.02, dz * 0.02)),
      NORMAL(Vec3::new);

      final SparkParticle.DeltaMovementFunction function;

      PhysicsType(SparkParticle.DeltaMovementFunction function) {
         this.function = function;
      }
   }
}
