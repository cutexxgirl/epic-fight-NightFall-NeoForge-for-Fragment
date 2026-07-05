package com.hm.efn.util;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "efn", value = Dist.CLIENT)
public class TriangleParticleEffect {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() == Minecraft.getInstance().player) {
         Player player = event.getEntity();
         Level world = player.level();
         if (player.hasEffect(EFNMobEffectRegistry.RING)) {
            handleTriangleParticles(player, world);
         }
      }
   }

   private static void handleTriangleParticles(Player player, Level world) {
      long gameTime = world.getGameTime();
      long cycleTime = gameTime % 100L;
      Vec3[] uprightTriangle = new Vec3[]{new Vec3(0.0, 0.0, 2.0), new Vec3(Math.sqrt(3.0), 0.0, -1.0), new Vec3(-Math.sqrt(3.0), 0.0, -1.0)};
      Vec3[] invertedTriangle = new Vec3[]{new Vec3(0.0, 0.0, -2.0), new Vec3(-Math.sqrt(3.0), 0.0, 1.0), new Vec3(Math.sqrt(3.0), 0.0, 1.0)};
      if (cycleTime >= 4L && cycleTime <= 32L && cycleTime % 4L == 0L) {
         spawnTriangleParticles(player, uprightTriangle, true);
      } else if (cycleTime >= 52L && cycleTime <= 84L && cycleTime % 4L == 0L) {
         spawnTriangleParticles(player, invertedTriangle, false);
      }
   }

   private static void spawnTriangleParticles(Player player, Vec3[] triangle, boolean clockwise) {
      Vec3 playerPos = player.position();
      double baseY = playerPos.y + 0.2;

      for (int i = 0; i < triangle.length; i++) {
         Vec3 current = triangle[i];
         double x = playerPos.x + current.x;
         double z = playerPos.z + current.z;
         int nextIndex = clockwise ? (i - 1 + triangle.length) % triangle.length : (i + 1) % triangle.length;
         Vec3 next = triangle[nextIndex];
         double dx = (next.x - current.x) * 0.1;
         double dz = (next.z - current.z) * 0.1;
         player.level().addParticle(ParticleTypes.END_ROD, x, baseY, z, dx, 0.0, dz);
      }
   }
}
