package com.hm.efn.client.effek;

import com.guhao.vix.particles.AAAEffekParticle;
import java.util.Random;
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
public class Dirt_1_Effek {
   public static final ResourceLocation DIRT_1_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "dirt1");

   public static void playDirt_1(Dirt_1_Effek.Type type, Level level, double x, double y, double z, float radius) {
      Random random = new Random();
      ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
         .position(x, y, z)
         .rotation(0.0F, random.nextFloat(-90.0F, 90.0F), 0.0F)
         .scale(radius / type.intrinsicRadius());
      AAALevel.addParticle(level, true, info);
   }

   public static Particle createParticleWrapper(
      Dirt_1_Effek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final Dirt_1_Effek.Type LEVEL1 = new Dirt_1_Effek.Type(Dirt_1_Effek.DIRT_1_EFFEK, 1.0F);
      public static final Dirt_1_Effek.Type LEVEL2 = new Dirt_1_Effek.Type(Dirt_1_Effek.DIRT_1_EFFEK, 0.7F);
      public static final Dirt_1_Effek.Type LEVEL3 = new Dirt_1_Effek.Type(Dirt_1_Effek.DIRT_1_EFFEK, 0.36F);
   }
}
