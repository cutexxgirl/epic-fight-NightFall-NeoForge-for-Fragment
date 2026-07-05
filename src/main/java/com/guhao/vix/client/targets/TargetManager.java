package com.guhao.vix.client.targets;

import com.guhao.vix.VIX;
import com.guhao.vix.client.pipeline.PostParticleRenderType;
import com.guhao.vix.util.Function.ScreenResizeEventHandler;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class TargetManager {
   private static final Logger LOGGER = VIX.LOGGER;
   private static final Map<ResourceLocation, ManagedTarget> allTargets = new HashMap<>();
   private static final Queue<RenderTarget> freePool = new ArrayDeque<>();
   public static ScreenResizeEventHandler OnResize = (width, height) -> {
   };
   private static final int MAX_FREE_POOL = 10;
   private static int lastW;
   private static int lastH;
   private static boolean dirty;
   private static int idleFrameCount;

   public static RenderTarget getTarget(ResourceLocation id) {
      dirty = true;
      ManagedTarget managedTarget = allTargets.get(id);
      if (managedTarget != null && managedTarget.target != null) {
         managedTarget.retain();
         resizeIfNeeded(managedTarget.target);
         return managedTarget.target;
      }

      RenderTarget target = getOrCreateRawTarget();
      resizeIfNeeded(target);
      if (managedTarget != null) {
         managedTarget.setTarget(target);
      } else {
         managedTarget = new ManagedTarget(id, target);
         allTargets.put(id, managedTarget);
      }

      managedTarget.retain();
      return target;
   }

   public static void ReleaseAll() {
      if (!dirty) {
         idleFrameCount++;
         if (idleFrameCount > 30 && !freePool.isEmpty()) {
            RenderTarget target;
            while ((target = freePool.poll()) != null) {
               target.destroyBuffers();
            }

            LOGGER.debug("VIX-Optimizer: Cleared freePool due to inactivity. VRAM released.");
         }

         return;
      }

      idleFrameCount = 0;
      dirty = false;
      RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
      boolean resized = lastW != mainTarget.width || lastH != mainTarget.height;
      if (resized) {
         lastW = mainTarget.width;
         lastH = mainTarget.height;

         for (RenderTarget target : freePool) {
            resizeIfNeeded(target, mainTarget);
         }

         OnResize.consume(mainTarget.width, mainTarget.height);
      }

      Iterator<ManagedTarget> iterator = allTargets.values().iterator();
      while (iterator.hasNext()) {
         ManagedTarget managedTarget = iterator.next();
         if (resized && managedTarget.target != null) {
            resizeIfNeeded(managedTarget.target, mainTarget);
         }

         managedTarget.releaseAll();
         if (managedTarget.target != null) {
            if (freePool.size() < MAX_FREE_POOL) {
               managedTarget.target.clear(Minecraft.ON_OSX);
               freePool.offer(managedTarget.target);
            } else {
               managedTarget.target.destroyBuffers();
            }

            managedTarget.setTarget(null);
         }

         iterator.remove();
      }

      while (freePool.size() > MAX_FREE_POOL) {
         RenderTarget target = freePool.poll();
         if (target != null) {
            target.destroyBuffers();
         }
      }
   }

   public static void ReleaseTarget(ResourceLocation id) {
      ManagedTarget managedTarget = allTargets.get(id);
      if (managedTarget != null) {
         managedTarget.release();
      }
   }

   private static RenderTarget getOrCreateRawTarget() {
      RenderTarget target = freePool.poll();
      if (target == null) {
         target = PostParticleRenderType.createTempTarget(Minecraft.getInstance().getMainRenderTarget());
      }

      return target;
   }

   private static void resizeIfNeeded(RenderTarget target) {
      if (target != null) {
         resizeIfNeeded(target, Minecraft.getInstance().getMainRenderTarget());
      }
   }

   private static void resizeIfNeeded(RenderTarget target, RenderTarget source) {
      if (target == null) {
         return;
      }

      if (target.width != source.width || target.height != source.height) {
         target.resize(source.width, source.height, Minecraft.ON_OSX);
      }
   }

   static class ManagedTarget {
      final ResourceLocation id;
      RenderTarget target;
      int referenceCount;

      ManagedTarget(ResourceLocation id, RenderTarget target) {
         this.id = id;
         this.target = target;
      }

      boolean isActive() {
         return this.referenceCount > 0;
      }

      void retain() {
         this.referenceCount++;
      }

      void release() {
         if (this.referenceCount > 0) {
            this.referenceCount--;
         }
      }

      void releaseAll() {
         this.referenceCount = 0;
      }

      void setTarget(RenderTarget target) {
         this.target = target;
      }
   }
}
