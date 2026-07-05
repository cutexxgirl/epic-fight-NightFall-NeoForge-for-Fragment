package com.hm.efn.client.particle.spark;

import com.hm.efn.particle.EFNParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ZandatsuAllSpark extends NoRenderParticle {
   public ZandatsuAllSpark(ClientLevel world, double x, double y, double z) {
      super(world, x, y, z);
      int particleCount = 180;
      this.createExplosionLayer(x, y, z, 0.5, particleCount / 3);
      this.createExplosionLayer(x, y, z, 1.0, particleCount / 2);
      this.createExplosionLayer(x, y, z, 1.8, particleCount / 3);
      this.createLargeParticles(x, y, z, 15);
   }

   private void createExplosionLayer(double x, double y, double z, double scale, int count) {
      for (int i = 0; i < count; i++) {
         Vec3 direction = new Vec3(
               (this.level.random.nextDouble() - 0.5) * 2.0,
               (this.level.random.nextDouble() - 0.5) * 2.0,
               (this.level.random.nextDouble() - 0.5) * 2.0
            )
            .normalize();
         Vec3 pos = new Vec3(x, y, z).add(direction.scale(0.3 * scale));
         Vec3 velocity = direction.scale(0.15 + this.level.random.nextDouble() * 0.5 * scale);
         if (this.level.random.nextFloat() < 0.7F) {
            this.level
               .addParticle(
                  (ParticleOptions)EFNParticles.SPARK_EXPANSIVE_ZANDATSU.get(),
                  pos.x,
                  pos.y,
                  pos.z,
                  velocity.x,
                  velocity.y,
                  velocity.z
               );
         } else {
            this.level
               .addParticle(
                  (ParticleOptions)EFNParticles.SPARK_CONTRACTILE_ZANDATSU.get(),
                  pos.x,
                  pos.y,
                  pos.z,
                  velocity.x,
                  velocity.y,
                  velocity.z
               );
         }
      }
   }

   private void createLargeParticles(double x, double y, double z, int count) {
      for (int i = 0; i < count; i++) {
         Vec3 direction = new Vec3(
               (this.level.random.nextDouble() - 0.5) * 2.0,
               (this.level.random.nextDouble() - 0.5) * 1.5,
               (this.level.random.nextDouble() - 0.5) * 2.0
            )
            .normalize();
         Vec3 pos = new Vec3(x, y, z).add(direction.scale(0.2));
         Vec3 velocity = direction.scale(0.3 + this.level.random.nextDouble() * 0.4);
         this.level
            .addParticle(
               (ParticleOptions)EFNParticles.NORMAL_SPARK_ZANDATSU.get(),
               pos.x,
               pos.y,
               pos.z,
               velocity.x,
               velocity.y,
               velocity.z
            );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ZandatsuAllSpark(worldIn, x, y, z);
      }
   }
}
