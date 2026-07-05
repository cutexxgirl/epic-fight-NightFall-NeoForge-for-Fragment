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
import net.minecraft.world.entity.Entity;

public class AAAFlowingEffekParticle extends Particle {
   private static final int MAX_LIFE_TIME = 36000;
   private static final int MAX_WAIT_TIME = 100;
   private int localAge;
   private ParticleEmitter emitter;
   private final Entity entity;
   private final float ofx;
   private final float ofy;
   private final float ofz;

   public AAAFlowingEffekParticle(
      ClientLevel level,
      ResourceLocation effectId,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      Entity entity,
      float ofx,
      float ofy,
      float ofz
   ) {
      super(level, x, y, z, xd, yd, zd);
      this.entity = entity;
      this.ofx = ofx;
      this.ofy = ofy;
      this.ofz = ofz;
      this.hasPhysics = false;
      this.lifetime = MAX_LIFE_TIME;
      EffectRegistry.load(effectId).thenAccept(definition -> this.emitter = definition.play());
   }

   public AAAFlowingEffekParticle(
      ClientLevel level,
      ResourceLocation effectId,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      Entity entity,
      float ofx,
      float ofy,
      float ofz,
      boolean ignored
   ) {
      this(level, effectId, x, y, z, xd, yd, zd, entity, ofx, ofy, ofz);
   }

   public Optional<ParticleEmitter> getEmitter() {
      return Optional.ofNullable(this.emitter);
   }

   @Override
   public void render(VertexConsumer vertexBuffer, Camera camera, float partialTicks) {
      this.m_5744_(vertexBuffer, camera, partialTicks);
   }

   public void m_5744_(VertexConsumer vertexBuffer, Camera camera, float partialTicks) {
      if (this.entity != null && !this.entity.isRemoved() && this.emitter != null && this.emitter.exists()) {
         float x = (float)(Mth.lerp((double)partialTicks, this.entity.xo, this.entity.getX()) + this.ofx);
         float y = (float)(Mth.lerp((double)partialTicks, this.entity.yo, this.entity.getY()) + this.ofy);
         float z = (float)(Mth.lerp((double)partialTicks, this.entity.zo, this.entity.getZ()) + this.ofz);
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
      if (this.entity == null || this.entity.isRemoved()) {
         this.remove();
      } else if (this.localAge > this.lifetime) {
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
