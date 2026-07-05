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

public class AllSpark extends NoRenderParticle {
   public AllSpark(ClientLevel world, double x, double y, double z) {
      super(world, x, y, z);

      for (int i = 0; i < 90; i++) {
         Vec3 direction = new Vec3(
               (this.level.random.nextDouble() - 0.5) * 2.0,
               (this.level.random.nextDouble() - 0.5) * 1.4,
               (this.level.random.nextDouble() - 0.5) * 2.0
            )
            .normalize();
         Vec3 pos = new Vec3(x, y, z).add(direction.scale(0.12));
         Vec3 velocity = direction.scale(0.05 + this.level.random.nextDouble() * 0.25 * 3.2);
         this.level
            .addParticle(
               (ParticleOptions)EFNParticles.SPARK_EXPANSIVE.get(),
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
         return new AllSpark(worldIn, x, y, z);
      }
   }
}
