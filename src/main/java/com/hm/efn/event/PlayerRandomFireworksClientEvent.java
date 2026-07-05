package com.hm.efn.event;

import com.hm.efn.EFN;
import com.hm.efn.client.effek.FireWorks2Effek;
import com.hm.efn.client.effek.FireWorksEffek;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.util.EffekUnits;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "efn", value = Dist.CLIENT, bus = Bus.GAME)
public class PlayerRandomFireworksClientEvent {
   private static final double fluency = 50.0;
   private static final int SOUND_INTERVAL = 470;
   private static final int MIN_FIREWORK_INTERVAL = 15;
   private static final int MAX_FIREWORK_INTERVAL = 30;
   private static boolean enabled = false;
   private static final Map<UUID, PlayerRandomFireworksClientEvent.ClientPlayerData> clientPlayerData = new HashMap<>();

   public static void setEnabled(boolean flag) {
      enabled = flag;
   }

   public static boolean isEnabled() {
      return enabled;
   }

   @SubscribeEvent
   public static void onTick(Post event) {
      handleClientTick(event);
   }

   @SubscribeEvent
   public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
      synchronized (clientPlayerData) {
         clientPlayerData.clear();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void handleClientTick(Post event) {
      if (enabled) {
         Minecraft mc = Minecraft.getInstance();
         LocalPlayer player = mc.player;
         if (player != null && mc.level != null) {
            UUID playerId = player.getUUID();
            Level level = mc.level;
            PlayerRandomFireworksClientEvent.ClientPlayerData data;
            synchronized (clientPlayerData) {
               data = clientPlayerData.computeIfAbsent(playerId, k -> new PlayerRandomFireworksClientEvent.ClientPlayerData());
            }

            data.fireworkTimer++;
            int randomInterval = ThreadLocalRandom.current().nextInt(15, 31);
            if (data.fireworkTimer >= randomInterval) {
               if (ThreadLocalRandom.current().nextInt(100) < 50.0) {
                  generateRandomAAAFireworksForPlayer(player, level);
               }

               data.fireworkTimer = 0;
            }

            data.soundTimer++;
            if (data.soundTimer >= 470) {
               playClientFireworkSound(player, level);
               data.soundTimer = 0;
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void playClientFireworkSound(LocalPlayer player, Level level) {
      try {
         BlockPos playerPos = player.blockPosition();
         RandomSource random = RandomSource.create();
         level.playSound(
            player,
            playerPos.getX() + 0.5,
            playerPos.getY() + 1.0,
            playerPos.getZ() + 0.5,
            (SoundEvent)EFNSounds.FIREWORKS.get(),
            SoundSource.AMBIENT,
            1.0F,
            0.8F + random.nextFloat() * 0.4F
         );
         if (random.nextInt(100) < 30) {
            level.playSound(
               player,
               playerPos.getX() + (random.nextDouble() - 0.5) * 10.0,
               playerPos.getY() + (random.nextDouble() - 0.5) * 5.0,
               playerPos.getZ() + (random.nextDouble() - 0.5) * 10.0,
               (SoundEvent)EFNSounds.FIREWORKS.get(),
               SoundSource.AMBIENT,
               0.4F,
               0.9F + random.nextFloat() * 0.2F
            );
         }
      } catch (Exception e) {
         EFN.LOGGER.error("Failed to play firework sound", e);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void generateRandomAAAFiresworksForPlayer(Player player, Level level, RandomSource random) {
      try {
         BlockPos playerPos = player.blockPosition();
         int radius = random.nextIntBetweenInclusive(20, 80);
         int offsetX = random.nextIntBetweenInclusive(-radius, radius);
         int offsetZ = random.nextIntBetweenInclusive(-radius, radius);
         BlockPos centerPos = playerPos.offset(offsetX, 0, offsetZ);
         if (EffekUnits.VFXENABLE() && level instanceof ClientLevel clientLevel) {
            float variation = (float)ThreadLocalRandom.current().nextDouble(-0.3, 0.3);
            Particle fireworksParticle = FireWorksEffek.createParticleWrapper(
               FireWorksEffek.Type.LEVEL1, clientLevel, centerPos.getX(), centerPos.getY(), centerPos.getZ(), 0.0, 0.0, 0.0, 1.0F + variation
            );
            Minecraft.getInstance().particleEngine.add(fireworksParticle);
            clientLevel.addParticle((ParticleOptions)EFNParticles.TRIGGER.get(), player.getX(), player.getY(), player.getZ(), 0.0, 0.0, 0.0);
            if (random.nextInt(100) < 10) {
               float variation2 = (float)ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
               Particle fireworks2Particle = FireWorks2Effek.createParticleWrapper(
                  FireWorks2Effek.Type.LEVEL1,
                  clientLevel,
                  centerPos.getX(),
                  centerPos.getY() + 24,
                  centerPos.getZ(),
                  0.0,
                  0.0,
                  0.0,
                  1.0F + variation2
               );
               Minecraft.getInstance().particleEngine.add(fireworks2Particle);
            }
         }
      } catch (Exception e) {
         EFN.LOGGER.error("Failed to generate fireworks", e);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void generateRandomAAAFireworksForPlayer(Player player, Level level) {
      RandomSource random = RandomSource.create();
      if (EffekUnits.VFXENABLE()) {
         generateRandomAAAFiresworksForPlayer(player, level, random);
      }
   }

   private static class ClientPlayerData {
      int fireworkTimer = 0;
      int soundTimer = 0;
      long lastFireworkTick = 0L;
      long lastSoundTick = 0L;

      ClientPlayerData() {
      }
   }
}
