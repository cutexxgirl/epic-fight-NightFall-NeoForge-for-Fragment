package com.hm.efn.event;

import com.hm.efn.EFN;
import com.hm.efn.network.FireworksPacket;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.FireworkExplosion.Shape;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME)
public class PlayerRandomFireworksServerEvent {
   private static final double fluency = 75.0;
   private static final Map<ServerPlayer, Integer> playerTimers = new HashMap<>();
   private static final int MIN_FIREWORK_INTERVAL = 20;
   private static final int MAX_FIREWORK_INTERVAL = 40;
   private static boolean enabled = false;
   private static final int[] FIREWORK_COLORS = new int[]{16711680, 65280, 255, 16776960, 16711935, 65535, 16740096, 16777215, 16738740, 9055202};
   private static final int[] RAINBOW_COLORS = new int[]{16711680, 16744192, 16776960, 65280, 255, 4915330, 9109759};

   public static void setEnabled(boolean flag) {
      enabled = flag;
   }

   public static boolean isEnabled() {
      return enabled;
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         boolean enabled = isEnabled();
         EFN.queueServerWork(10, () -> EFN.sendToPlayer(new FireworksPacket(enabled), player));
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         playerTimers.remove(player);
      }
   }

   @SubscribeEvent
   public static void onServerTick(Post event) {
      if (true) {
         if (enabled) {
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
               ServerLevel level = player.serverLevel();
               playerTimers.putIfAbsent(player, 0);
               int timer = playerTimers.get(player);
               int randomInterval = ThreadLocalRandom.current().nextInt(20, 41);
               if (++timer >= randomInterval) {
                  if (ThreadLocalRandom.current().nextInt(100) < 75.0) {
                     generateRandomFireworksForPlayer(player, level);
                  }

                  playerTimers.put(player, 0);
               } else {
                  playerTimers.put(player, timer);
               }
            }
         }
      }
   }

   private static void generateRandomFireworksForPlayer(ServerPlayer player, ServerLevel level) {
      RandomSource random = RandomSource.create();
      BlockPos playerPos = player.blockPosition();
      PlayerRandomFireworksServerEvent.ShowType showType = PlayerRandomFireworksServerEvent.ShowType.values()[random.nextInt(
         PlayerRandomFireworksServerEvent.ShowType.values().length
      )];
      int radius = random.nextIntBetweenInclusive(20, 80);
      int count = random.nextIntBetweenInclusive(20, 50);
      int height = random.nextIntBetweenInclusive(3, 10);
      int offsetX = random.nextIntBetweenInclusive(-radius, radius);
      int offsetZ = random.nextIntBetweenInclusive(-radius, radius);
      BlockPos centerPos = playerPos.offset(offsetX, 0, offsetZ);
      switch (showType) {
         case RANDOM_CLUSTER:
            generateRandomClusterFireworks(level, centerPos, radius, count, height, random);
            break;
         case CIRCULAR_SHOW:
            generateCircularFireworks(level, centerPos, radius, count, height, random);
            break;
         case HEART_SHAPE:
            generateHeartFireworks(level, centerPos, radius / 10, height, random);
            break;
         case SPIRAL_PATTERN:
            generateSpiralFireworks(level, centerPos, radius, count, height, random);
            break;
         case GRID_PATTERN:
            generateGridFireworks(level, centerPos, radius, count, height, random);
            break;
         case CROSS_PATTERN:
            generateCrossFireworks(level, centerPos, radius, count, height, random);
            break;
         case DIAMOND_SHAPE:
            generateDiamondFireworks(level, centerPos, radius, count, height, random);
            break;
         case RAINBOW_SHOWER:
            generateRainbowFireworks(level, centerPos, radius, count, height, random);
      }

      if (random.nextInt(100) < 50) {
         generateBonusFireworks(level, playerPos, random);
      }
   }

   private static void generateRandomClusterFireworks(Level level, BlockPos centerPos, int radius, int count, int height, RandomSource random) {
      int halfRadius = radius / 2;

      for (int i = 0; i < count; i++) {
         int offsetX = random.nextIntBetweenInclusive(-halfRadius, halfRadius);
         int offsetZ = random.nextIntBetweenInclusive(-halfRadius, halfRadius);
         int offsetY = random.nextIntBetweenInclusive(-3, 3);
         BlockPos spawnPos = centerPos.offset(offsetX, height + offsetY, offsetZ);
         spawnSingleRandomFirework(level, spawnPos, random, random.nextIntBetweenInclusive(1, 3), random.nextBoolean(), true);
      }
   }

   private static void generateCircularFireworks(Level level, BlockPos centerPos, int radius, int count, int height, RandomSource random) {
      double angleStep = (Math.PI * 2) / count;

      for (int i = 0; i < count; i++) {
         double angle = angleStep * i;
         double randomRadius = radius * (0.5 + random.nextDouble() * 0.5);
         int x = (int)(centerPos.getX() + randomRadius * Math.cos(angle));
         int z = (int)(centerPos.getZ() + randomRadius * Math.sin(angle));
         BlockPos spawnPos = new BlockPos(x, centerPos.getY() + height, z);
         spawnSingleRandomFirework(level, spawnPos, random, random.nextIntBetweenInclusive(1, 3), random.nextBoolean(), true);
      }
   }

   private static void generateHeartFireworks(Level level, BlockPos centerPos, int size, int height, RandomSource random) {
      int[] colors = new int[]{16711680, 16738740, 16729344};
      int[] fadeColors = new int[]{9109504};

      for (double t = 0.0; t <= Math.PI * 2; t += 0.2) {
         double x = 16.0 * Math.pow(Math.sin(t), 3.0);
         double z = 13.0 * Math.cos(t) - 5.0 * Math.cos(2.0 * t) - 2.0 * Math.cos(3.0 * t) - Math.cos(4.0 * t);
         BlockPos spawnPos = new BlockPos(
            centerPos.getX() + (int)(x * size / 8.0),
            centerPos.getY() + height + random.nextIntBetweenInclusive(-2, 2),
            centerPos.getZ() + (int)(z * size / 8.0)
         );
         spawnSingleCustomFirework(level, spawnPos, Shape.STAR, colors, fadeColors, true, true, random.nextIntBetweenInclusive(2, 3));
      }
   }

   private static void generateSpiralFireworks(Level level, BlockPos centerPos, int radius, int count, int height, RandomSource random) {
      double countReciprocal = 1.0 / count;

      for (int i = 0; i < count; i++) {
         double angle = (Math.PI * 2) * i / 10.0;
         double spiralRadius = radius * i * countReciprocal;
         double spiralHeight = height + i * 0.5;
         int x = (int)(centerPos.getX() + spiralRadius * Math.cos(angle));
         int z = (int)(centerPos.getZ() + spiralRadius * Math.sin(angle));
         BlockPos spawnPos = new BlockPos(x, centerPos.getY() + (int)spiralHeight, z);
         spawnSingleRandomFirework(level, spawnPos, random, random.nextIntBetweenInclusive(1, 3), random.nextBoolean(), true);
      }
   }

   private static void generateGridFireworks(Level level, BlockPos centerPos, int radius, int count, int height, RandomSource random) {
      int gridSize = (int)Math.sqrt(count);
      int spacing = radius * 2 / gridSize;
      int startX = centerPos.getX() - radius;
      int startZ = centerPos.getZ() - radius;

      for (int i = 0; i < gridSize; i++) {
         for (int j = 0; j < gridSize && i * gridSize + j < count; j++) {
            int x = startX + i * spacing;
            int z = startZ + j * spacing;
            int y = centerPos.getY() + height + random.nextIntBetweenInclusive(-2, 2);
            BlockPos spawnPos = new BlockPos(x, y, z);
            Shape shape;
            if ((i + j) % 3 == 0) {
               shape = Shape.STAR;
            } else if ((i + j) % 3 == 1) {
               shape = Shape.BURST;
            } else {
               shape = Shape.LARGE_BALL;
            }

            int colorIndex = (i + j) % 4;

            int[] colors = switch (colorIndex) {
               case 0 -> new int[]{FIREWORK_COLORS[0]};
               case 1 -> new int[]{FIREWORK_COLORS[1]};
               case 2 -> new int[]{FIREWORK_COLORS[2]};
               case 3 -> new int[]{FIREWORK_COLORS[3]};
               default -> new int[]{FIREWORK_COLORS[7]};
            };
            spawnSingleCustomFirework(level, spawnPos, shape, colors, null, (i + j) % 2 == 0, true, 2);
         }
      }
   }

   private static void generateCrossFireworks(Level level, BlockPos centerPos, int radius, int count, int height, RandomSource random) {
      int arms = 4;
      int perArm = count / arms;
      double angleStep = Math.PI / 2;

      for (int arm = 0; arm < arms; arm++) {
         double angle = angleStep * arm;

         for (int i = 0; i < perArm; i++) {
            double distance = (double)(radius * i) / perArm;
            int x = (int)(centerPos.getX() + distance * Math.cos(angle));
            int z = (int)(centerPos.getZ() + distance * Math.sin(angle));
            BlockPos spawnPos = new BlockPos(x, centerPos.getY() + height, z);

            spawnSingleCustomFirework(level, spawnPos, Shape.STAR, switch (arm) {
               case 0 -> new int[]{FIREWORK_COLORS[0]};
               case 1 -> new int[]{FIREWORK_COLORS[1]};
               case 2 -> new int[]{FIREWORK_COLORS[2]};
               case 3 -> new int[]{FIREWORK_COLORS[3]};
               default -> new int[]{FIREWORK_COLORS[7]};
            }, null, true, true, 2);
         }
      }
   }

   private static void generateDiamondFireworks(Level level, BlockPos centerPos, int radius, int count, int height, RandomSource random) {
      int[] xPoints = new int[]{0, radius / 2, radius, radius / 2, 0, -radius / 2, -radius, -radius / 2};
      int[] zPoints = new int[]{radius, radius / 2, 0, -radius / 2, -radius, -radius / 2, 0, radius / 2};
      int maxPoints = Math.min(count, xPoints.length);
      int centerX = centerPos.getX();
      int centerZ = centerPos.getZ();
      int baseY = centerPos.getY() + height;

      for (int i = 0; i < maxPoints; i++) {
         int idx = i % xPoints.length;

         for (int j = 0; j < 3; j++) {
            int x = centerX + xPoints[idx];
            int z = centerZ + zPoints[idx];
            int y = baseY + j * 2;
            BlockPos spawnPos = new BlockPos(x, y, z);
            spawnSingleRandomFirework(level, spawnPos, random, 3, true, true);
         }
      }
   }

   private static void generateRainbowFireworks(Level level, BlockPos centerPos, int radius, int count, int height, RandomSource random) {
      for (int i = 0; i < count; i++) {
         int offsetX = random.nextIntBetweenInclusive(-radius, radius);
         int offsetZ = random.nextIntBetweenInclusive(-radius, radius);
         BlockPos spawnPos = centerPos.offset(offsetX, height + random.nextIntBetweenInclusive(0, 10), offsetZ);
         int colorIndex = i % RAINBOW_COLORS.length;
         spawnSingleCustomFirework(level, spawnPos, Shape.BURST, new int[]{RAINBOW_COLORS[colorIndex]}, null, false, true, 1);
      }
   }

   private static void generateBonusFireworks(Level level, BlockPos playerPos, RandomSource random) {
      int bonusCount = random.nextIntBetweenInclusive(3, 8);

      for (int i = 0; i < bonusCount; i++) {
         int offsetX = random.nextIntBetweenInclusive(-5, 5);
         int offsetZ = random.nextIntBetweenInclusive(-5, 5);
         int offsetY = random.nextIntBetweenInclusive(1, 3);
         BlockPos spawnPos = playerPos.offset(offsetX, offsetY, offsetZ);
         spawnSingleRandomFirework(level, spawnPos, random, 1, random.nextBoolean(), true);
      }
   }

   private static void spawnSingleRandomFirework(Level level, BlockPos pos, RandomSource random, int flightTime, boolean flicker, boolean trail) {
      ItemStack fireworkStack = createRandomFireworkStack(random, flightTime, flicker);
      spawnFireworkEntity(level, pos, fireworkStack, random);
   }

   private static void spawnSingleCustomFirework(
      Level level, BlockPos pos, Shape shape, int[] colors, int[] fadeColors, boolean flicker, boolean trail, int flightTime
   ) {
      ItemStack fireworkStack = createCustomFirework(shape, colors, fadeColors, flicker, flightTime);
      RandomSource random = RandomSource.create();
      spawnFireworkEntity(level, pos, fireworkStack, random);
   }

   private static ItemStack createRandomFireworkStack(RandomSource random, int flightTime, boolean flicker) {
      ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
      CompoundTag fireworksTag = new CompoundTag();
      int boostedFlight = Math.min(flightTime + random.nextIntBetweenInclusive(1, 2), 4);
      fireworksTag.putByte("Flight", (byte)boostedFlight);
      ListTag explosionsList = new ListTag();
      int explosionCount = random.nextIntBetweenInclusive(2, 4);

      for (int i = 0; i < explosionCount; i++) {
         boolean useFlicker = flicker || random.nextBoolean();
         explosionsList.add(createRandomExplosionTag(random, useFlicker));
      }

      setFireworks(fireworkStack, boostedFlight, explosionsList);
      return fireworkStack;
   }

   private static ItemStack createCustomFirework(Shape shape, int[] colors, int[] fadeColors, boolean flicker, int flightTime) {
      ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
      CompoundTag fireworksTag = new CompoundTag();
      fireworksTag.putByte("Flight", (byte)(flightTime + 1));
      ListTag explosionsList = new ListTag();

      for (int i = 0; i < 2; i++) {
         CompoundTag explosionTag = new CompoundTag();
         if (i == 0) {
            explosionTag.putByte("Type", (byte)shape.getId());
            explosionTag.putBoolean("Flicker", flicker);
            explosionTag.putBoolean("Trail", true);
            explosionTag.putIntArray("Colors", colors);
            if (fadeColors != null && fadeColors.length > 0) {
               explosionTag.putIntArray("FadeColors", fadeColors);
            }
         } else {
            Shape[] shapes = Shape.values();
            Shape altShape = shapes[ThreadLocalRandom.current().nextInt(shapes.length)];
            explosionTag.putByte("Type", (byte)altShape.getId());
            explosionTag.putBoolean("Flicker", true);
            explosionTag.putBoolean("Trail", true);
            explosionTag.putIntArray("Colors", colors);
            explosionTag.putIntArray("FadeColors", new int[]{16766720});
         }

         explosionsList.add(explosionTag);
      }

      setFireworks(fireworkStack, flightTime + 1, explosionsList);
      return fireworkStack;
   }

   private static void setFireworks(ItemStack fireworkStack, int flightDuration, ListTag explosionsList) {
      List<FireworkExplosion> explosions = new ArrayList<>();

      for (int i = 0; i < explosionsList.size(); i++) {
         explosions.add(createExplosion(explosionsList.getCompound(i)));
      }

      fireworkStack.set(DataComponents.FIREWORKS, new Fireworks(flightDuration, explosions));
   }

   private static FireworkExplosion createExplosion(CompoundTag explosionTag) {
      return new FireworkExplosion(
         Shape.byId(explosionTag.getByte("Type")),
         IntArrayList.wrap(explosionTag.getIntArray("Colors")),
         IntArrayList.wrap(explosionTag.getIntArray("FadeColors")),
         explosionTag.getBoolean("Trail"),
         explosionTag.getBoolean("Flicker")
      );
   }

   private static CompoundTag createRandomExplosionTag(RandomSource random, boolean flicker) {
      CompoundTag explosionTag = new CompoundTag();
      Shape[] shapes = Shape.values();
      Shape shape = shapes[random.nextInt(shapes.length)];
      explosionTag.putByte("Type", (byte)shape.getId());
      explosionTag.putBoolean("Flicker", flicker);
      explosionTag.putBoolean("Trail", true);
      int colorCount = random.nextIntBetweenInclusive(2, 4);
      int[] colors = new int[colorCount];

      for (int i = 0; i < colorCount; i++) {
         colors[i] = FIREWORK_COLORS[random.nextInt(FIREWORK_COLORS.length)];
      }

      explosionTag.putIntArray("Colors", colors);
      int fadeColorCount = random.nextIntBetweenInclusive(1, 3);
      int[] fadeColors = new int[fadeColorCount];

      for (int i = 0; i < fadeColorCount; i++) {
         fadeColors[i] = FIREWORK_COLORS[random.nextInt(FIREWORK_COLORS.length)];
      }

      explosionTag.putIntArray("FadeColors", fadeColors);
      return explosionTag;
   }

   private static void spawnFireworkEntity(Level level, BlockPos pos, ItemStack fireworkStack, RandomSource random) {
      FireworkRocketEntity firework = new FireworkRocketEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, fireworkStack);
      firework.setDeltaMovement((random.nextDouble() - 0.5) * 0.15, 0.8 + random.nextDouble() * 0.4, (random.nextDouble() - 0.5) * 0.15);
      level.addFreshEntity(firework);
   }

   private enum ShowType {
      RANDOM_CLUSTER,
      CIRCULAR_SHOW,
      HEART_SHAPE,
      SPIRAL_PATTERN,
      GRID_PATTERN,
      CROSS_PATTERN,
      DIAMOND_SHAPE,
      RAINBOW_SHOWER;
   }
}
