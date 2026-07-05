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
public class MistEffek {
   public static final ResourceLocation MIST_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "mist");

   public static void playMist(MistEffek.Type type, Level level, double x, double y, double z, float radius, Entity entity) {
      ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
         .position(x, y, z)
         .bindOnEntity(entity)
         .parameter(3, 0.39215687F)
         .scale(radius / type.intrinsicRadius());
      AAALevel.addParticle(level, true, info);
   }

   public static Particle createParticleWrapper(
      MistEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final MistEffek.Type MIST = new MistEffek.Type(MistEffek.MIST_EFFEK, 10.0F);
      public static final MistEffek.Type MIST2 = new MistEffek.Type(MistEffek.MIST_EFFEK, 0.5F);
   }
}
