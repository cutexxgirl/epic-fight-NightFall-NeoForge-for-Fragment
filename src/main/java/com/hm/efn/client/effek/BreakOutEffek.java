package com.hm.efn.client.effek;

import com.guhao.vix.particles.AAAEffekParticle;
import java.util.Random;
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
public class BreakOutEffek {
   public static final ResourceLocation BREAK_OUT_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "breakout");

   public static void playBreakOut(BreakOutEffek.Type type, Level level, double x, double y, double z, float radius, Entity entity) {
      Random random = new Random();
      ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
         .bindOnEntity(entity)
         .position(x, y, z)
         .rotation(0.0F, random.nextFloat(-90.0F, 90.0F), 0.0F)
         .scale(radius / type.intrinsicRadius());
      AAALevel.addParticle(level, true, info);
   }

   public static Particle createParticleWrapper(
      BreakOutEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final BreakOutEffek.Type LEVEL1 = new BreakOutEffek.Type(BreakOutEffek.BREAK_OUT_EFFEK, 2.5F);
      public static final BreakOutEffek.Type LEVEL2 = new BreakOutEffek.Type(BreakOutEffek.BREAK_OUT_EFFEK, 1.0F);
      public static final BreakOutEffek.Type LEVEL3 = new BreakOutEffek.Type(BreakOutEffek.BREAK_OUT_EFFEK, 0.8F);
   }
}
