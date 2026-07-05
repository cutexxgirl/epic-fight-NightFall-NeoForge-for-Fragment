package com.hm.efn.client.effek;

import com.guhao.vix.particles.AAAEffekParticle;
import com.guhao.vix.particles.AAAFlowingEffekParticle;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChargingEffek {
   public static final ResourceLocation CHARGING_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "charging");

   public static void playCharging(ChargingEffek.Type type, Entity entity, float x, float y, float z, float radius) {
      ClientLevel level = Minecraft.getInstance().level;
      Particle particle = createFlowingParticleWrapper(
         type, level, entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0, entity, x, y, z, radius
      );
      Minecraft.getInstance().particleEngine.add(particle);
   }

   public static void playCharging2(ChargingEffek.Type type, Entity entity, double x, double y, double z, float radius, Level level) {
      ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
         .position(x, y, z)
         .bindOnEntity(entity)
         .scale(radius / type.intrinsicRadius());
      AAALevel.addParticle(level, true, info);
   }

   public static Particle createParticleWrapper(
      ChargingEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public static Particle createFlowingParticleWrapper(
      ChargingEffek.Type type,
      ClientLevel level,
      double x,
      double y,
      double z,
      double dx,
      double dy,
      double dz,
      Entity entity,
      float ofx,
      float ofy,
      float ofz,
      float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAFlowingEffekParticle particle = new AAAFlowingEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz, entity, ofx, ofy, ofz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final ChargingEffek.Type LEVEL1 = new ChargingEffek.Type(ChargingEffek.CHARGING_EFFEK, 0.9F);
      public static final ChargingEffek.Type LEVEL2 = new ChargingEffek.Type(ChargingEffek.CHARGING_EFFEK, 0.7F);
      public static final ChargingEffek.Type LEVEL3 = new ChargingEffek.Type(ChargingEffek.CHARGING_EFFEK, 0.36F);
   }
}
