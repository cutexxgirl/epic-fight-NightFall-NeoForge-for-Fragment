package com.hm.efn.entity.skill;

import com.hm.efn.client.particle.effect.Abstract3DParticleOptions;
import com.hm.efn.entity.Abstract3DParticleEntity;
import com.hm.efn.particle.EFNParticles;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CrimsonSlashEntity extends Abstract3DParticleEntity {
   public CrimsonSlashEntity(EntityType<? extends Abstract3DParticleEntity> type, Level level) {
      super(type, level);
   }

   @Override
   protected void spawnClientParticle() {
      if (this.level().isClientSide) {
         this.level()
            .addParticle(
               new Abstract3DParticleOptions((ParticleType<Abstract3DParticleOptions>)EFNParticles.CRIMSON_SLASH.get(), this.getId()),
               this.getX(),
               this.getY(),
               this.getZ(),
               0.0,
               0.0,
               0.0
            );
      }
   }

   @Override
   protected boolean shouldPerformAttack() {
      return false;
   }

   @Override
   protected void performAttack() {
   }

   @Override
   public int getMaxLifetime() {
      return 7;
   }
}
