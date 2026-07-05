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
public class DragonFlashEffek {
   public static final ResourceLocation DRAGONFLASH_EFFEK = ResourceLocation.fromNamespaceAndPath("efn", "dragon_flash");

   public static void playDragonFlash(
      DragonFlashEffek.Type type, Level level, double x, double y, double z, float rx, float ry, float rz, float radius, Entity entity
   ) {
      ParticleEmitterInfo info = ParticleEmitterInfo.create(level, type.effekId())
         .position(x, y, z)
         .bindOnEntity(entity)
         .rotation(rx, ry, rz)
         .scale(radius / type.intrinsicRadius());
      AAALevel.addParticle(level, true, info);
   }

   public static Particle createParticleWrapper(
      DragonFlashEffek.Type type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, float radius
   ) {
      float scale = radius / type.intrinsicRadius();
      AAAEffekParticle particle = new AAAEffekParticle(level, type.effekId(), x, y, z, dx, dy, dz);
      if (particle.getEmitter().isPresent()) {
         ((ParticleEmitter)particle.getEmitter().get()).setScale(scale, scale, scale);
      }

      return particle;
   }

   public record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final DragonFlashEffek.Type LEVEL1 = new DragonFlashEffek.Type(DragonFlashEffek.DRAGONFLASH_EFFEK, 2.0F);
      public static final DragonFlashEffek.Type LEVEL2 = new DragonFlashEffek.Type(DragonFlashEffek.DRAGONFLASH_EFFEK, 1.75F);
      public static final DragonFlashEffek.Type LEVEL3 = new DragonFlashEffek.Type(DragonFlashEffek.DRAGONFLASH_EFFEK, 1.25F);
   }
}
