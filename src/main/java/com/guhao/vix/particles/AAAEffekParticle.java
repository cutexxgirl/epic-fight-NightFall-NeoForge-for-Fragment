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
   private static final int MAX_LIFE_TIME = 36000;
   private static final int MAX_WAIT_TIME = 100;
   private int localAge;
   private ParticleEmitter emitter;

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

   public ParticleRenderType m_7556_() {
      return this.getRenderType();
   }
}
