package com.hm.efn.client.events;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Pre;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "efn", value = Dist.CLIENT)
public class RenderEvents {
   static Queue<UUID> waitToRemove = Queues.newArrayDeque();
   static ConcurrentHashMap<UUID, RenderEvents.TickTimer> hiddenEntity = new ConcurrentHashMap<>();

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void onWorldUnload(Unload event) {
      if (event.getLevel().isClientSide()) {
         hiddenEntity.clear();
         waitToRemove.clear();
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void clientTick(Post event) {
      if (!Minecraft.getInstance().isPaused()) {
         if (true) {
            hiddenEntity.forEach((k, v) -> {
               v.tick();
               if (v.isEnded()) {
                  waitToRemove.add(k);
               }
            });

            while (!waitToRemove.isEmpty()) {
               UUID uuid = waitToRemove.poll();
               hiddenEntity.remove(uuid);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void OnLivingEntityRender(Pre<?, ?> event) {
      if (!hiddenEntity.isEmpty()) {
         if (hiddenEntity.containsKey(event.getEntity().getUUID())) {
            event.setCanceled(true);
         }
      }
   }

   public static void HiddenEntity(LivingEntity entity, int maxTime) {
      if (hiddenEntity.containsKey(entity.getUUID())) {
         hiddenEntity.get(entity.getUUID()).setTime(maxTime);
      } else {
         hiddenEntity.put(entity.getUUID(), new RenderEvents.TickTimer(maxTime));
      }
   }

   public static void UnhiddenEntity(LivingEntity entity) {
      hiddenEntity.remove(entity.getUUID());
   }

   static class TickTimer {
      int time;

      TickTimer(int time) {
         this.time = time;
      }

      public void tick() {
         if (this.time > 0) {
            this.time--;
         }
      }

      public void setTime(int time) {
         this.time = time;
      }

      public boolean isEnded() {
         return this.time <= 0;
      }
   }
}
