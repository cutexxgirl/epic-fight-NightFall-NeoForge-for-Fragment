package com.hm.efn.client.effek;

import com.guhao.vix.particles.AAAEffekParticle;
import com.hm.efn.EFN;
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
public class FireWorks2Effek {
   public static final ResourceLocation FIREWORKS2_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "fireworks2");

   public static void playFireworks2(FireWorks2Effek.Type type, Level level, double x, double y, double z, float scale) {
      if (level != null) {
         try {
            ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId()).position(x, y, z).scale(scale);
            AAALevel.addParticle(level, 512.0, info);
            if (level.isClientSide) {
               level.addAlwaysVisibleParticle((ParticleOptions)EFNParticles.TRIGGER.get(), x, y, z, 0.0, 0.0, 0.0);
            }
         } catch (Exception e) {
            EFN.LOGGER.error("Failed to play fireworks2 effect", e);
         }
      }
   }

   public static Particle createParticleWrapper(
      FireWorks2Effek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final FireWorks2Effek.Type LEVEL1 = new FireWorks2Effek.Type(FireWorks2Effek.FIREWORKS2_EFFEK, 0.5F);
   }
}
