package com.hm.efn.client.effek;

import com.guhao.vix.particles.AAAEffekParticle;
import java.util.Random;
import net.minecraft.client.Minecraft;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SculkEffek {
   public static final ResourceLocation SCULK_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "sculk");
   private static final int SCULK_LIFETIME_TICKS = 24;

   public static void playSculk(SculkEffek.Type type, Level level, double x, double y, double z, float radius) {
      Random random = new Random();
      if (level instanceof ClientLevel clientLevel) {
         float scale = radius / type.intrinsicRadius();
         AAAEffekParticle particle = new AAAEffekParticle(clientLevel, type.effekId(), x, y, z, 0.0, 0.0, 0.0)
            .setLifetimeTicks(SCULK_LIFETIME_TICKS)
            .setEmitterRotation(0.0F, random.nextFloat(-90.0F, 90.0F), 0.0F)
            .setEmitterScale(scale, scale, scale);
         Minecraft.getInstance().particleEngine.add(particle);
      }
   }

   public static Particle createParticleWrapper(
      SculkEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz).setLifetimeTicks(SCULK_LIFETIME_TICKS);
      particle.setEmitterScale(scale, scale, scale);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final SculkEffek.Type LEVEL1 = new SculkEffek.Type(SculkEffek.SCULK_EFFEK, 2.5F);
      public static final SculkEffek.Type LEVEL2 = new SculkEffek.Type(SculkEffek.SCULK_EFFEK, 1.0F);
      public static final SculkEffek.Type LEVEL3 = new SculkEffek.Type(SculkEffek.SCULK_EFFEK, 0.8F);
   }
}
