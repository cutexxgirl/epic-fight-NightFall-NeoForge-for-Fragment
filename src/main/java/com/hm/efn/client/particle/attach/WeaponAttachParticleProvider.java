package com.hm.efn.client.particle.attach;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class WeaponAttachParticleProvider implements ParticleProvider<SimpleParticleType> {
   private final SpriteSet spriteSet;

   public WeaponAttachParticleProvider(SpriteSet spriteSet) {
      this.spriteSet = spriteSet;
   }

   @Nullable
   public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
      int entityId = (int)dx;
      int lifetime = (int)dz;
      if (lifetime <= -999) {
         WeaponAttachParticle.clearFor(entityId);
         return null;
      }

      if (level.getEntity(entityId) instanceof LivingEntity livingEntity) {
         LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
         if (patch != null) {
            Vec3f offset = new Vec3f((float)x, (float)y, (float)z);
            String jointName = dy > 0.5 ? "Tool_L" : "Tool_R";
            Vec3f localRotation = new Vec3f(0.0F, 90.0F, 0.0F);
            return new WeaponAttachParticle(level, patch, jointName, offset, localRotation, this.spriteSet, lifetime);
         }
      }

      return null;
   }
}
