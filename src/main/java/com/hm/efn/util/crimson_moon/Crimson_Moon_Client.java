package com.hm.efn.util.crimson_moon;

import com.guhao.vix.camera.VIXCameraShake;
import com.guhao.vix.client.event.ScreenEffectEngine;
import com.guhao.vix.util.RenderUtils;
import com.hm.efn.client.particle.DMC.SpaceBrokenParticle;
import com.hm.efn.client.screeneffect.BlackWhiteFlashEffect;
import com.hm.efn.particle.EFNParticles;
import com.merlin204.avalon.client.CameraShake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class Crimson_Moon_Client {
   public static void post1(LivingEntityPatch<?> entityPatch) {
      Level worldIn = ((LivingEntity)entityPatch.getOriginal()).level();
      Vec3 pos = ((LivingEntity)entityPatch.getOriginal()).position();
      worldIn.addParticle((ParticleOptions)EFNParticles.JUDGEMENT_CUT_PARTICLE_RED.get(), pos.x, pos.y - 0.5, pos.z, 0.0, 0.0, 0.0);
      CameraShake.shake(200, 7.0F, 9.0F, ((LivingEntity)entityPatch.getOriginal()).position(), 6.0F);
   }

   public static void post2(LivingEntityPatch<?> entityPatch) {
      Level worldIn = ((LivingEntity)entityPatch.getOriginal()).level();
      Vec3 pos = ((LivingEntity)entityPatch.getOriginal()).position();
      worldIn.addParticle((ParticleOptions)EFNParticles.JUDGEMENT_CUT_PARTICLE_RED.get(), pos.x, pos.y - 0.5, pos.z, 0.0, 0.0, 0.0);
   }

   public static void post3(LivingEntityPatch<?> entityPatch) {
      Level worldIn = ((LivingEntity)entityPatch.getOriginal()).level();
      Vec3 pos = ((LivingEntity)entityPatch.getOriginal()).position();
      RenderUtils.AddParticle(
         (ClientLevel)worldIn,
         new SpaceBrokenParticle((ClientLevel)worldIn, pos.x, pos.y, pos.z, ((LivingEntity)entityPatch.getOriginal()).yBodyRot, 1, 0)
      );
      RenderUtils.AddParticle(
         (ClientLevel)worldIn,
         new SpaceBrokenParticle((ClientLevel)worldIn, pos.x, pos.y + 3.0, pos.z, ((LivingEntity)entityPatch.getOriginal()).yBodyRot, 1, 0)
      );
      int particleCount = 4;
      float radius = 5.0F;
      float yOffset = 0.5F;

      for (int i = 0; i < particleCount; i++) {
         float angle = (float)(i * ((Math.PI * 2) / particleCount));
         double px = pos.x + radius * Math.sin(angle);
         double pz = pos.z + radius * Math.cos(angle);
         double py = pos.y + yOffset;
         float yaw = (float)Math.toDegrees(angle) + 90.0F;
         RenderUtils.AddParticle((ClientLevel)worldIn, new SpaceBrokenParticle((ClientLevel)worldIn, px, py, pz, yaw, 1, 0));
         RenderUtils.AddParticle((ClientLevel)worldIn, new SpaceBrokenParticle((ClientLevel)worldIn, px, py, pz, yaw, 1, 1));
      }

      RenderUtils.AddParticle(
         (ClientLevel)worldIn,
         new SpaceBrokenParticle((ClientLevel)worldIn, pos.x, pos.y, pos.z, ((LivingEntity)entityPatch.getOriginal()).yBodyRot, 1, 1)
      );
   }

   public static void post4(LivingEntityPatch<?> entityPatch) {
      ClientLevel level = (ClientLevel)((LivingEntity)entityPatch.getOriginal()).level();
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      BlockPos centerPos = entity.blockPosition();
      int radius = 4;
      int particleCountPerBlock = 3;

      for (int x = -radius; x <= radius; x++) {
         for (int z = -radius; z <= radius; z++) {
            BlockPos pos = centerPos.offset(x, -1, z);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && !state.is(Blocks.MOVING_PISTON)) {
               for (int i = 0; i < particleCountPerBlock; i++) {
                  double px = pos.getX() + level.random.nextDouble();
                  double py = pos.getY() + 1.0;
                  double pz = pos.getZ() + level.random.nextDouble();
                  BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
                  Particle particle = Minecraft.getInstance()
                     .particleEngine
                     .createParticle(
                        option,
                        px,
                        py,
                        pz,
                        (level.random.nextDouble() - 0.5) * 0.36,
                        level.random.nextDouble() * 0.5 + 0.2,
                        (level.random.nextDouble() - 0.5) * 0.36
                     );
                  if (particle != null) {
                     particle.setLifetime(40 + level.random.nextInt(30));
                  }
               }
            }
         }
      }

      VIXCameraShake.shake(60, 8.0F, ((LivingEntity)entityPatch.getOriginal()).position(), 6.0F);
      BlackWhiteFlashEffect effect2 = new BlackWhiteFlashEffect(((LivingEntity)entityPatch.getOriginal()).position(), BlackWhiteFlashEffect.ImpactMode.HEAVY);
      ScreenEffectEngine.PushScreenEffectADD(effect2);
   }
}
