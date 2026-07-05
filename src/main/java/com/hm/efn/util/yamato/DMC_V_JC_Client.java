package com.hm.efn.util.yamato;

import com.guhao.vix.camera.CameraEvents;
import com.guhao.vix.camera.VIXCameraShake;
import com.guhao.vix.client.event.ScreenEffectEngine;
import com.guhao.vix.util.RenderUtils;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.Dirt_2_Effek;
import com.hm.efn.client.effek.SparkEffek;
import com.hm.efn.client.effek.Stone_2_Effek;
import com.hm.efn.client.particle.DMC.AirWaveParticle;
import com.hm.efn.client.particle.DMC.PhantomsParticle;
import com.hm.efn.client.particle.DMC.PhantomsParticle_Return;
import com.hm.efn.client.particle.DMC.SpaceBrokenParticle;
import com.hm.efn.client.screeneffect.BlackWhiteFlashEffect;
import com.hm.efn.client.screeneffect.ColorDispersionEffect;
import com.hm.efn.client.screeneffect.ScreenDistortionEffect;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.util.EffekUnits;
import com.hm.efn.util.ParticleEffectInvoker;
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
import net.neoforged.fml.ModList;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DMC_V_JC_Client {
   public static void prev(LivingEntityPatch<?> entityPatch) {
      Vec3 pos = ((LivingEntity)entityPatch.getOriginal()).position();
      VIXCameraShake.shake(220, 4.0F, ((LivingEntity)entityPatch.getOriginal()).position(), 64.0F);
      if ((Boolean)EFNClientConfig.ENABLE_JUDGEMENTCUT_END_CAMERA_ANIMATIONS.get() && !ModList.get().isLoaded("efn_enhance")) {
         CameraEvents.SetAnim(EFNAnimations.DMC, (LivingEntity)entityPatch.getOriginal(), true, EFNAnimations.DMC5_V_JC);
      }

      AirWaveParticle particle = new AirWaveParticle(Minecraft.getInstance().level, pos.x, pos.y, pos.z, 2, 5);
      RenderUtils.AddParticle(Minecraft.getInstance().level, particle);
      if ((Boolean)EFNClientConfig.ENABLE_JUDGEMENTCUT_END_PARTICLES.get()) {
         ((LivingEntity)entityPatch.getOriginal())
            .level()
            .addAlwaysVisibleParticle((ParticleOptions)EFNParticles.YAMATO_SPHERE.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
         ((LivingEntity)entityPatch.getOriginal())
            .level()
            .addAlwaysVisibleParticle((ParticleOptions)EFNParticles.YAMATO_LAST_SPHERE.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
         ((LivingEntity)entityPatch.getOriginal())
            .level()
            .addAlwaysVisibleParticle((ParticleOptions)EFNParticles.YAMATO_FLOOR.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
      }

      ColorDispersionEffect effect = new ColorDispersionEffect(pos);
      effect.lifetime = 44;
      effect.type = ColorDispersionEffect.Type.PREV;
      ScreenEffectEngine.PushScreenEffect(effect);
   }

   public static void HandleAtk1(LivingEntityPatch<?> entityPatch) {
      Vec3 pos = ((LivingEntity)entityPatch.getOriginal()).position();
      double var10004 = pos.y - 1.95;
      PhantomsParticle particle = new PhantomsParticle(Minecraft.getInstance().level, pos.x, var10004, pos.z, entityPatch);
      particle.setLifetime(30);
      RenderUtils.AddParticle(Minecraft.getInstance().level, particle);
      var10004 = pos.y - 1.9;
      PhantomsParticle_Return particle2 = new PhantomsParticle_Return(Minecraft.getInstance().level, pos.x, var10004, pos.z, entityPatch);
      particle2.setLifetime(30);
      RenderUtils.AddParticle(Minecraft.getInstance().level, particle2);
   }

   public static void post1(LivingEntityPatch<?> entityPatch) {
      Level worldIn = ((LivingEntity)entityPatch.getOriginal()).level();
      Vec3 pos = ((LivingEntity)entityPatch.getOriginal()).position();
      worldIn.addParticle((ParticleOptions)EFNParticles.JUDGEMENT_CUT_PARTICLE.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
      Vec3 spawnPos = ParticleEffectInvoker.calculateParticlePosition((LivingEntity)entityPatch.getOriginal(), -0.52, -0.27, -0.7);
      worldIn.addParticle((ParticleOptions)EFNParticles.FLASH.get(), spawnPos.x, spawnPos.y, spawnPos.z, 0.0, 0.0, 0.0);
      worldIn.addParticle((ParticleOptions)EFNParticles.YAMATO_CUT_1.get(), spawnPos.x, spawnPos.y, spawnPos.z, 0.0, 0.0, 0.0);
      ColorDispersionEffect effect = new ColorDispersionEffect(pos);
      effect.lifetime = 57;
      ScreenEffectEngine.PushScreenEffect(effect);
      BlackWhiteFlashEffect effect2 = new BlackWhiteFlashEffect(pos, BlackWhiteFlashEffect.ImpactMode.LIGHT);
      ScreenEffectEngine.PushScreenEffectADD(effect2);
      if ((Boolean)EFNClientConfig.ENABLE_JUDGEMENTCUT_END_SCREENDISTORTIONEFFECT.get()) {
         ScreenDistortionEffect distortion = new ScreenDistortionEffect(((LivingEntity)entityPatch.getOriginal()).position(), 6, 0.3F, 12.0F, 4.8F);
         ScreenEffectEngine.PushScreenEffect(distortion);
      }

      if (((Boolean)EFNClientConfig.ENABLE_JUDGEMENTCUT_END_CAMERA_ANIMATIONS.get()).equals(true)) {
         CameraShake.shake(220, 7.0F, 10.0F, ((LivingEntity)entityPatch.getOriginal()).position(), 3.7F);
      }
   }

   public static void post2(LivingEntityPatch<?> entityPatch) {
      Level worldIn = ((LivingEntity)entityPatch.getOriginal()).level();
      Vec3 pos = ((LivingEntity)entityPatch.getOriginal()).position();
      worldIn.addParticle((ParticleOptions)EFNParticles.JUDGEMENT_CUT_PARTICLE.get(), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
   }

   public static void post3(LivingEntityPatch<?> entityPatch) {
      Level worldIn = ((LivingEntity)entityPatch.getOriginal()).level();
      Vec3 pos = ((LivingEntity)entityPatch.getOriginal()).position();
      RenderUtils.AddParticle(
         (ClientLevel)worldIn,
         new SpaceBrokenParticle((ClientLevel)worldIn, pos.x, pos.y, pos.z, ((LivingEntity)entityPatch.getOriginal()).yBodyRot, 56, 0)
      );
      RenderUtils.AddParticle(
         (ClientLevel)worldIn,
         new SpaceBrokenParticle(
            (ClientLevel)worldIn, pos.x, pos.y + 3.0, pos.z, ((LivingEntity)entityPatch.getOriginal()).yBodyRot, 56, 0
         )
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
         RenderUtils.AddParticle((ClientLevel)worldIn, new SpaceBrokenParticle((ClientLevel)worldIn, px, py, pz, yaw, 56, 0));
         RenderUtils.AddParticle((ClientLevel)worldIn, new SpaceBrokenParticle((ClientLevel)worldIn, px, py, pz, yaw, 56, 1));
      }

      RenderUtils.AddParticle(
         (ClientLevel)worldIn,
         new SpaceBrokenParticle((ClientLevel)worldIn, pos.x, pos.y, pos.z, ((LivingEntity)entityPatch.getOriginal()).yBodyRot, 56, 1)
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

      level.addParticle(
         (ParticleOptions)EFNParticles.FLASH.get(), entity.position().x, entity.position().y + 0.5, entity.position().z, 0.0, 0.1, 0.0
      );
      Level worldIn = ((LivingEntity)entityPatch.getOriginal()).level();
      worldIn.addParticle(
         (ParticleOptions)EFNParticles.FLASH.get(),
         ((LivingEntity)entityPatch.getOriginal()).getX(),
         ((LivingEntity)entityPatch.getOriginal()).getEyeY() - 0.3,
         ((LivingEntity)entityPatch.getOriginal()).getZ(),
         0.0,
         0.0,
         0.0
      );
      VIXCameraShake.shake(100, 6.0F, ((LivingEntity)entityPatch.getOriginal()).position(), 64.0F);
      if (EffekUnits.VFXENABLE()) {
         Stone_2_Effek.playStone_2(
            Stone_2_Effek.Type.LEVEL1,
            ((LivingEntity)entityPatch.getOriginal()).level(),
            ((LivingEntity)entityPatch.getOriginal()).getX(),
            ((LivingEntity)entityPatch.getOriginal()).getY(),
            ((LivingEntity)entityPatch.getOriginal()).getZ(),
            1.0F
         );
         SparkEffek.playSpark(
            SparkEffek.Type.LEVEL2,
            ((LivingEntity)entityPatch.getOriginal()).level(),
            ((LivingEntity)entityPatch.getOriginal()).getX(),
            ((LivingEntity)entityPatch.getOriginal()).getY(),
            ((LivingEntity)entityPatch.getOriginal()).getZ(),
            1.0F
         );
         Dirt_2_Effek.playDirt_2(
            Dirt_2_Effek.Type.LEVEL1,
            ((LivingEntity)entityPatch.getOriginal()).level(),
            ((LivingEntity)entityPatch.getOriginal()).getX(),
            ((LivingEntity)entityPatch.getOriginal()).getY(),
            ((LivingEntity)entityPatch.getOriginal()).getZ(),
            1.0F
         );
      }
   }
}
