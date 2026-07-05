package com.guhao.vix.client.pipeline;

import com.guhao.vix.client.targets.TargetManager;
import com.guhao.vix.util.RenderUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.PriorityQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PostEffectPipelines {
   public static final Queue<Pipeline> PostEffectQueue = new ConcurrentLinkedQueue<>();
   public static final PriorityQueue<Pipeline> PostEffectQueueInternal = new PriorityQueue<>();
   public static RenderTarget depth;
   public static Matrix4f shaderOrthoMatrix = new Matrix4f();
   static ResourceLocation depth_target = ResourceLocation.parse("vix:depth_target");
   static ResourceLocation shared_depth_rl = ResourceLocation.parse("vix:shared_depth");
   private static boolean Active;
   private static boolean depthShared;

   public static void RenderPost() {
      if (PostEffectQueue.isEmpty() && !depthShared) {
         Active = false;
         return;
      }

      if (!PostEffectQueue.isEmpty()) {
         RenderSystem.enableBlend();
         Pipeline pipeline;
         while ((pipeline = PostEffectQueue.poll()) != null) {
            PostEffectQueueInternal.add(pipeline);
         }

         updateOrthoMatrix();
         while (!PostEffectQueueInternal.isEmpty()) {
            PostEffectQueueInternal.poll().HandlePostEffect();
         }
      }

      restoreSharedDepth();
      Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      close();
      TargetManager.ReleaseAll();
   }

   static void updateOrthoMatrix() {
      RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
      shaderOrthoMatrix.setOrtho(0.0F, mainTarget.width, 0.0F, mainTarget.height, 0.1F, 1000.0F);
   }

   public static boolean isActive() {
      return Active;
   }

   public static void active() {
      Active = true;
   }

   public static void saveSharedDepth() {
      if (depthShared) {
         return;
      }

      RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
      depth = TargetManager.getTarget(shared_depth_rl);
      depth.copyDepthFrom(mainTarget);
      depth.unbindWrite();
      depthShared = true;
   }

   public static void restoreSharedDepth() {
      if (!depthShared) {
         return;
      }

      Minecraft.getInstance().getMainRenderTarget().copyDepthFrom(depth);
      TargetManager.ReleaseTarget(shared_depth_rl);
      depth = null;
      depthShared = false;
   }

   static RenderTarget getDepthSource() {
      return depthShared && depth != null ? depth : getSource();
   }

   static boolean isDepthShared() {
      return depthShared;
   }

   public static void close() {
      if (!Active) {
         return;
      }

      RenderUtils.resetTextureParams();
      if (depth != null && !depthShared) {
         TargetManager.ReleaseTarget(depth_target);
         depth = null;
      }

      if (depthShared) {
         TargetManager.ReleaseTarget(shared_depth_rl);
         depth = null;
         depthShared = false;
      }

      drainQueue(PostEffectQueue);
      drainQueue(PostEffectQueueInternal);
      Active = false;
   }

   private static void drainQueue(Queue<Pipeline> queue) {
      Pipeline pipeline;
      while ((pipeline = queue.poll()) != null) {
         if (pipeline.name != null && pipeline.bufferTarget != null) {
            TargetManager.ReleaseTarget(pipeline.name);
         }

         pipeline.bufferTarget = null;
         pipeline.started = false;
         pipeline.called = false;
      }
   }

   public static RenderTarget getSource() {
      return Minecraft.getInstance().getMainRenderTarget();
   }

   public abstract static class Pipeline implements Comparable<Pipeline> {
      public final ResourceLocation name;
      public int priority;
      protected boolean called;
      protected boolean started;
      public RenderTarget bufferTarget;

      public Pipeline(ResourceLocation name) {
         this.name = name;
      }

      @Override
      public int compareTo(Pipeline other) {
         return Integer.compare(this.priority, other.priority);
      }

      public void start() {
         if (this.started) {
            if (Active) {
               this.bufferTarget.copyDepthFrom(getDepthSource());
               this.bufferTarget.bindWrite(false);
            }

            return;
         }

         if (this.bufferTarget == null) {
            this.bufferTarget = TargetManager.getTarget(this.name);
         }

         if (Active) {
            this.bufferTarget.copyDepthFrom(getDepthSource());
            PostEffectQueue.add(this);
            this.bufferTarget.bindWrite(false);
            this.started = true;
         } else if (this.bufferTarget != null) {
            TargetManager.ReleaseTarget(this.name);
            this.bufferTarget = null;
         }
      }

      public void call() {
         if (Active) {
            this.called = true;
         }
      }

      public void suspend() {
         if (Active) {
            this.bufferTarget.unbindWrite();
            this.bufferTarget.unbindRead();
            if (!isDepthShared()) {
               RenderTarget source = getSource();
               source.copyDepthFrom(this.bufferTarget);
               source.bindWrite(false);
            } else {
               getSource().bindWrite(false);
            }
         } else {
            if (this.bufferTarget != null) {
               TargetManager.ReleaseTarget(this.name);
               this.bufferTarget = null;
            }

            getSource().bindWrite(false);
         }
      }

      public abstract void PostEffectHandler();

      protected final void cleanup() {
         if (this.name != null && this.bufferTarget != null) {
            TargetManager.ReleaseTarget(this.name);
         }

         this.bufferTarget = null;
         this.started = false;
         this.called = false;
      }

      public void HandlePostEffect() {
         try {
            if (this.called) {
               this.PostEffectHandler();
            }
         } finally {
            this.cleanup();
         }
      }
   }
}
