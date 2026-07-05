package com.hm.efn.client.effek;

import com.guhao.vix.particles.AAAEffekParticle;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BurstRedEffek {
   public static final ResourceLocation BURST_RED_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "burst_red");

   public static void playBurstRed(BurstRedEffek.Type type, Level level, double x, double y, double z, float rx, float ry, float rz, float radius) {
      ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
         .position(x, y, z)
         .rotation(rx, ry, rz)
         .scale(radius / type.intrinsicRadius());
      AAALevel.addParticle(level, true, info);
   }

   public static Particle createParticleWrapper(
      BurstRedEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final BurstRedEffek.Type LEVEL1 = new BurstRedEffek.Type(BurstRedEffek.BURST_RED_EFFEK, 4.5F);
      public static final BurstRedEffek.Type LEVEL2 = new BurstRedEffek.Type(BurstRedEffek.BURST_RED_EFFEK, 1.0F);
      public static final BurstRedEffek.Type LEVEL3 = new BurstRedEffek.Type(BurstRedEffek.BURST_RED_EFFEK, 0.8F);
   }
}
