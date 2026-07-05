package com.guhao.vix.client.screeneffect;

import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class ScreenEffectBase {
   public final ResourceLocation ID;
   public int age = 0;
   public int lifetime = 20;
   public Vec3 pos;
   protected float partialAge = 0.0F;

   public ScreenEffectBase(ResourceLocation id, Vec3 pos) {
      this.ID = id;
      this.pos = pos;
   }

   public void tick() {
      this.age++;
   }

   public void updateWithPartialTicks(float partialTicks) {
      this.partialAge = this.age + partialTicks;
   }

   public boolean shouldRemoved() {
      return this.age > this.lifetime;
   }

   public void setRemove() {
      this.age = this.lifetime + 1;
   }

   public float getAgeWithPartialTicks() {
      return this.partialAge;
   }

   public float getNormalizedAgeWithPartialTicks() {
      return this.partialAge / this.lifetime;
   }

   public abstract Pipeline getPipeline();

   public boolean shouldPost(Camera camera, Frustum frustum) {
      return frustum != null && frustum.isVisible(this.getAABB()) && camera.getPosition().subtract(this.pos).length() < 256.0;
   }

   public AABB getAABB() {
      return new AABB(this.pos.subtract(0.2, 0.2, 0.2), this.pos.add(0.2, 0.2, 0.2));
   }

   @Override
   public int hashCode() {
      return this.ID.hashCode();
   }

   public abstract static class SE_Pipeline<E extends ScreenEffectBase> extends Pipeline {
      public final E effect;
      public float partialTicks = 0.0F;

      public SE_Pipeline(ResourceLocation name, E effect) {
         super(name);
         this.effect = effect;
      }

      protected float getPartialTicks() {
         return this.partialTicks;
      }

      public void setPartialTicks(float partialTicks) {
         this.partialTicks = partialTicks;
         this.effect.updateWithPartialTicks(partialTicks);
      }

      @Override
      public void HandlePostEffect() {
         try {
            this.PostEffectHandler();
         } finally {
            this.cleanup();
         }
      }

      public abstract void PostEffectHandler();
   }
}
