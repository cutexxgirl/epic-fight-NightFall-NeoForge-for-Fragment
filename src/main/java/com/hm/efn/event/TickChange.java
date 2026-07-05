package com.hm.efn.event;

import com.hm.efn.network.TickChangePacket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;

public class TickChange {
   public static volatile float PERCENT = 20.0F;
   public static volatile double millisF = 0.0;
   public static volatile long millis = 0L;
   private static final ScheduledExecutorService service;
   private static final AtomicBoolean isRunning = new AtomicBoolean(false);

   public static void init() {
      if (isRunning.compareAndSet(false, true)) {
         service.scheduleAtFixedRate(TickChange::update, 1L, 1L, TimeUnit.MILLISECONDS);
         System.out.println("EFN Tick system has been initialized.");
      }
   }

   public static void shutdown() {
      if (isRunning.compareAndSet(true, false)) {
         System.out.println("EFN Tick system is shutting down...");
         service.shutdown();

         try {
            if (!service.awaitTermination(5L, TimeUnit.SECONDS)) {
               System.err.println("EFN Tick system did not terminate gracefully, forcing shutdown.");
               service.shutdownNow();
            } else {
               System.out.println("EFN Tick system has been shut down successfully.");
            }
         } catch (InterruptedException e) {
            System.err.println("EFN Tick system shutdown was interrupted, forcing shutdown.");
            service.shutdownNow();
            Thread.currentThread().interrupt();
         }
      }
   }

   private static void update() {
      float p = PERCENT / 20.0F;
      millisF += p;
      millis = (long)millisF;
   }

   public static void requestChange(float percent) {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         TickChange.ClientProxy.requestChange(percent);
      }
   }

   public static void applyChangeFromServer(float percent) {
      PERCENT = percent;
      PacketDistributor.sendToAllPlayers(new TickChangePacket(percent));
   }

   public static void updateFromServer(float percent) {
      PERCENT = percent;
   }

   public static void changeAll(float percent) {
      PERCENT = percent;
      if (FMLEnvironment.dist == Dist.CLIENT) {
         TickChange.ClientProxy.syncChangeAll(percent);
      }
   }

   public static void jump(int ticks) {
      millisF += ticks * 50L;
   }

   static {
      ThreadFactory threadFactory = runnable -> {
         Thread thread = new Thread(runnable, "EFN-Tick-Updater-Thread");
         thread.setDaemon(true);
         return thread;
      };
      service = Executors.newSingleThreadScheduledExecutor(threadFactory);
   }

   private static class ClientProxy {
      private static void requestChange(float percent) {
         if (Minecraft.getInstance().isLocalServer()) {
            TickChange.PERCENT = percent;
         } else {
            PacketDistributor.sendToServer(new TickChangePacket(percent));
         }
      }

      private static void syncChangeAll(float percent) {
         if (!Minecraft.getInstance().isLocalServer()) {
            PacketDistributor.sendToServer(new TickChangePacket(percent));
         }
      }
   }
}
