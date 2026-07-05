package com.hm.efn.client.particle;

import com.guhao.vix.client.pipeline.PostEffectPipelines;
import com.guhao.vix.particles.TexturedSkinModelParticle;
import com.hm.efn.client.model.EFNMeshes;
import com.hm.efn.client.render.EFNRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class Block extends TexturedSkinModelParticle {
   private LivingEntityPatch<?> caster;

   public Block(
      ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AssetAccessor<SkinnedMesh> particleMesh, ResourceLocation texture
   ) {
      super(level, x, y, z, xd, yd, zd, particleMesh, texture);
      this.lifetime = 64;
      this.hasPhysics = false;
      this.roll = (float)xd;
      this.pitch = (float)zd;
      this.alpha = 1.0F;
      Entity entity = level.getEntity((int)Double.doubleToLongBits(yd));
      if (entity != null) {
         this.caster = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      }

      this.scale = 12.0F;
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return EFNRenderType.MeshSpaceBrokenEnd;
   }

   public void tick() {
      this.m_5989_();
   }

   public void render(@NotNull VertexConsumer vertexBuffer, Camera camera, float pt) {
      if (PostEffectPipelines.isActive()) {
         EFNRenderType.MeshSpaceBrokenEnd.callPipeline();
         this.m_5744_(vertexBuffer, camera, pt);
         if (this.caster != null && this.caster.getStunShield() <= 0.0F) {
            this.remove();
         }
      }
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
      float yaw = Mth.lerp(partialTicks, this.yawO, this.yaw);
      Vec3 vec3 = camera.getPosition();
      float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
      float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
      float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
      float scale = Mth.lerp(partialTicks, this.scaleO, this.scale);
      poseStack.translate(x, y, z);
      poseStack.mulPose(QuaternionUtils.XP.rotationDegrees(this.pitch));
      poseStack.mulPose(QuaternionUtils.ZP.rotationDegrees(this.roll));
      poseStack.mulPose(QuaternionUtils.YP.rotationDegrees(yaw));
      poseStack.scale(scale, scale, scale);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new Block(level, x, y, z, xSpeed, ySpeed, zSpeed, EFNMeshes.BLOCK, ResourceLocation.fromNamespaceAndPath("efn", "textures/models/particle/yamato_particle.png"));
      }
   }
}
