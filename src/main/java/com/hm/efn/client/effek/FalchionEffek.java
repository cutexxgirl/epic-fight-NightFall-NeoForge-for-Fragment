package com.hm.efn.client.effek;

import com.guhao.vix.particles.AAAEffekParticle;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FalchionEffek {
   public static final ResourceLocation FALCHION_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "falchion");

   public static void playFalchion(FalchionEffek.Type type, Level level, double x, double y, double z, float radius, Entity entity) {
      ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
         .position(x, y, z)
         .bindOnEntity(entity)
         .scale(radius / type.intrinsicRadius());
      AAALevel.addParticle(level, true, info);
   }

   public static Particle createParticleWrapper(
      FalchionEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final FalchionEffek.Type LEVEL1 = new FalchionEffek.Type(FalchionEffek.FALCHION_EFFEK, 1.5F);
   }
}
