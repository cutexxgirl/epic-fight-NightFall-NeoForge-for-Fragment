package com.hm.efn.client.effek;

import com.guhao.vix.particles.AAAEffekParticle;
import com.hm.efn.particle.EFNParticles;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Background_1_Effek {
   public static final ResourceLocation BACKGROUND_1_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "background1");

   public static void playBackground_1(Background_1_Effek.Type type, Level level, double x, double y, double z, float radius) {
      ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(radius / type.intrinsicRadius());
      AAALevel.addParticle(level, true, info);
      level.addAlwaysVisibleParticle((ParticleOptions)EFNParticles.TRIGGER.get(), true, x, y, z, 0.0, 0.0, 0.0);
   }

   public static Particle createParticleWrapper(
      Background_1_Effek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final Background_1_Effek.Type LEVEL1 = new Background_1_Effek.Type(Background_1_Effek.BACKGROUND_1_EFFEK, 0.9F);
      public static final Background_1_Effek.Type LEVEL2 = new Background_1_Effek.Type(Background_1_Effek.BACKGROUND_1_EFFEK, 0.7F);
      public static final Background_1_Effek.Type LEVEL3 = new Background_1_Effek.Type(Background_1_Effek.BACKGROUND_1_EFFEK, 0.36F);
   }
}
