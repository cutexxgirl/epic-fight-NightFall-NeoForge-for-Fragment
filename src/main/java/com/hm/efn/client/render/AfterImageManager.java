package com.hm.efn.client.render;

import com.hm.efn.client.particle.afterimage.SmoothEntityAfterimageParticle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME, value = Dist.CLIENT)
public class AfterImageManager {
   private static final Map<Integer, AfterImageManager.TrailConfig> ACTIVE_TRAILS = new ConcurrentHashMap<>();

   @SubscribeEvent
   public static void onWorldUnload(Unload event) {
      if (event.getLevel().isClientSide()) {
         ACTIVE_TRAILS.clear();
      }
   }

   public static void requestTrail(LivingEntity entity, long emitDurationMs, float particleDurationMs, float initialAlpha, float r, float g, float b) {
      if (entity != null && entity.level().isClientSide) {
         ACTIVE_TRAILS.put(
            entity.getId(), new AfterImageManager.TrailConfig(Util.getMillis() + emitDurationMs, particleDurationMs, initialAlpha, r, g, b, true)
         );
      }
   }

   public static void requestWeaponTrail(LivingEntity entity, long emitDurationMs, float particleDurationMs, float initialAlpha) {
      if (entity != null && entity.level().isClientSide) {
         ACTIVE_TRAILS.put(
            entity.getId(), new AfterImageManager.TrailConfig(Util.getMillis() + emitDurationMs, particleDurationMs, initialAlpha, 1.0F, 1.0F, 1.0F, false)
         );
      }
   }

   public static void stopTrail(LivingEntity entity) {
      if (entity != null) {
         ACTIVE_TRAILS.remove(entity.getId());
      }
   }

   @SubscribeEvent
   public static void onRenderLevel(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_ENTITIES) {
         if (!ACTIVE_TRAILS.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
               long currentTime = Util.getMillis();
               float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);
               ACTIVE_TRAILS.entrySet()
                  .removeIf(
                     entry -> {
                        int entityId = entry.getKey();
                        AfterImageManager.TrailConfig config = entry.getValue();
                        if (currentTime > config.endTimeMs) {
                           return true;
                        } else if (!(mc.level.getEntity(entityId) instanceof LivingEntity targetEntity && targetEntity.isAlive())) {
                           return true;
                        } else {
                           if (currentTime - config.lastSpawnTimeMs < 16L) {
                              return false;
                           }

                           LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(targetEntity, LivingEntityPatch.class);
                           if (patch != null) {
                              EntitySnapshot<?> snapshot = patch.captureEntitySnapshot();
                              if (snapshot != null) {
                                 double exactX = Mth.lerp(partialTicks, targetEntity.xo, targetEntity.getX());
                                 double exactY = Mth.lerp(partialTicks, targetEntity.yo, targetEntity.getY());
                                 double exactZ = Mth.lerp(partialTicks, targetEntity.zo, targetEntity.getZ());
                                 SmoothEntityAfterimageParticle particle = new SmoothEntityAfterimageParticle(
                                    mc.level,
                                    exactX,
                                    exactY,
                                    exactZ,
                                    snapshot,
                                    config.particleDurationMs,
                                    config.initialAlpha,
                                    config.r,
                                    config.g,
                                    config.b,
                                    config.renderBody
                                 );
                                 mc.particleEngine.add(particle);
                                 config.lastSpawnTimeMs = currentTime;
                              }
                           }

                           return false;
                        }
                     }
                  );
            }
         }
      }
   }

   private static class TrailConfig {
      final long endTimeMs;
      final float particleDurationMs;
      final float initialAlpha;
      final float r;
      final float g;
      final float b;
      final boolean renderBody;
      long lastSpawnTimeMs = 0L;

      TrailConfig(long endTimeMs, float particleDurationMs, float initialAlpha, float r, float g, float b, boolean renderBody) {
         this.endTimeMs = endTimeMs;
         this.particleDurationMs = particleDurationMs;
         this.initialAlpha = initialAlpha;
         this.r = r;
         this.g = g;
         this.b = b;
         this.renderBody = renderBody;
      }
   }
}
