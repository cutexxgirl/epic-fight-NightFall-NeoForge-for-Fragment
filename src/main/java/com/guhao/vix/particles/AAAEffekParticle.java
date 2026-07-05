package com.guhao.vix.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AAAEffekParticle extends Particle {
   private static final int MAX_LIFE_TIME = 200;
   private static final int MAX_WAIT_TIME = 100;
   private int localAge;
   private ParticleEmitter emitter;
   private boolean hasRequestedScale;
   private float requestedScaleX = 1.0F;
   private float requestedScaleY = 1.0F;
   private float requestedScaleZ = 1.0F;
   private boolean hasRequestedRotation;
   private float requestedRotX;
   private float requestedRotY;
   private float requestedRotZ;

   public AAAEffekParticle(
      ClientLevel level,
      ResourceLocation effectId,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd
   ) {
      super(level, x, y, z, xd, yd, zd);
      this.hasPhysics = false;
      this.lifetime = MAX_LIFE_TIME;
      EffectRegistry.load(effectId).thenAccept(definition -> this.emitter = definition.play());
   }

   public AAAEffekParticle(
      ClientLevel level,
      ResourceLocation effectId,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      boolean ignored
   ) {
      this(level, effectId, x, y, z, xd, yd, zd);
   }

   public Optional<ParticleEmitter> getEmitter() {
      return Optional.ofNullable(this.emitter);
   }

   public AAAEffekParticle setLifetimeTicks(int lifetime) {
      this.lifetime = Math.max(1, lifetime);
      return this;
   }

   public AAAEffekParticle setEmitterScale(float x, float y, float z) {
      this.hasRequestedScale = true;
      this.requestedScaleX = x;
      this.requestedScaleY = y;
      this.requestedScaleZ = z;
      if (this.emitter != null && this.emitter.exists()) {
         this.emitter.setScale(x, y, z);
      }

      return this;
   }

   public AAAEffekParticle setEmitterRotation(float x, float y, float z) {
      this.hasRequestedRotation = true;
      this.requestedRotX = x;
      this.requestedRotY = y;
      this.requestedRotZ = z;
      if (this.emitter != null && this.emitter.exists()) {
         this.emitter.setRotation(x, y, z);
      }

      return this;
   }

   @Override
   public void render(VertexConsumer vertexBuffer, Camera camera, float partialTicks) {
      this.m_5744_(vertexBuffer, camera, partialTicks);
   }

   public void m_5744_(VertexConsumer vertexBuffer, Camera camera, float partialTicks) {
      if (this.emitter != null && this.emitter.exists()) {
         float x = (float)Mth.lerp((double)partialTicks, this.xo, this.x);
         float y = (float)Mth.lerp((double)partialTicks, this.yo, this.y);
         float z = (float)Mth.lerp((double)partialTicks, this.zo, this.z);
         this.emitter.setPosition(x, y, z);
         this.applyRequestedTransform();
      }
   }

   private void applyRequestedTransform() {
      if (this.emitter != null && this.emitter.exists()) {
         if (this.hasRequestedScale) {
            this.emitter.setScale(this.requestedScaleX, this.requestedScaleY, this.requestedScaleZ);
         }

         if (this.hasRequestedRotation) {
            this.emitter.setRotation(this.requestedRotX, this.requestedRotY, this.requestedRotZ);
         }
      }
   }

   @Override
   public void tick() {
      this.m_5989_();
   }

   public void m_5989_() {
      if (this.removed) {
         return;
      }

      ParticleEmitter currentEmitter = this.emitter;
      if (this.localAge > this.lifetime) {
         this.remove();
      } else if (currentEmitter == null && this.localAge > MAX_WAIT_TIME) {
         this.remove();
      } else if (currentEmitter != null && !currentEmitter.exists()) {
         this.remove();
      } else {
         this.age = this.localAge++;
      }

      super.tick();
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.NO_RENDER;
   }

   @Override
   public void remove() {
      if (this.emitter != null && this.emitter.exists()) {
         this.emitter.stop();
      }

      super.remove();
   }

   public ParticleRenderType m_7556_() {
      return this.getRenderType();
   }
}
