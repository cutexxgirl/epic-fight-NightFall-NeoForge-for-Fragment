package com.hm.efn.client.particle;

import com.guhao.vix.particles.TexturedSkinModelParticle;
import com.hm.efn.client.model.EFNMeshes;
import com.hm.efn.client.particle.efnparticletype.EFNParticleRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class YamatoLastSphere extends TexturedSkinModelParticle {
   private LivingEntityPatch<?> caster;
   private final AssetAccessor<SkinnedMesh> meshAccessor;
   private boolean isDestroyed = false;
   private static final float TEXTURE_FLOW_U = 0.005F;
   private static final float TEXTURE_FLOW_V = 0.005F;
   private static final float ROT_SPEED_YAW = 0.005F;
   private static final float ROT_SPEED_PITCH = 0.003F;
   private static final float ROT_SPEED_ROLL = 0.002F;
   private float uvOffsetU = 0.0F;
   private float uvOffsetV = 0.0F;

   public YamatoLastSphere(
      ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AssetAccessor<SkinnedMesh> particleMesh, ResourceLocation texture
   ) {
      super(level, x, y, z, xd, yd, zd, particleMesh, texture);
      this.meshAccessor = particleMesh;
      this.lifetime = 44;
      this.hasPhysics = false;
      this.roll = (float)xd;
      this.pitch = (float)zd;
      this.scale = 12.0F;
      this.alpha = 0.36F;
      Entity entity = level.getEntity((int)Double.doubleToLongBits(yd));
      if (entity != null) {
         this.caster = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
      }
   }

   public void tick() {
      this.m_5989_();
      if (this.age >= this.lifetime) {
         this.remove();
      }
   }

   public void remove() {
      if (!this.isDestroyed) {
         this.isDestroyed = true;
      }

      super.remove();
   }

   public boolean shouldCull() {
      return false;
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return EFNParticleRenderTypes.EFN_PARTICLE_MODEL_NO_NORMAL;
   }

   public void render(@NotNull VertexConsumer vertexBuffer, Camera camera, float pt) {
      this.yaw += 0.005F;
      this.pitch += 0.003F;
      this.roll += 0.002F;
      this.uvOffsetU += 0.005F;
      this.uvOffsetV += 0.005F;
      if (this.scale <= 85.0F) {
         this.scale = this.scale + Math.max(30 - this.age, 0) * 0.12F;
      }

      float a = (float)(this.lifetime - this.age) / this.lifetime;
      this.alpha = Math.min(a, 0.36F);
      PoseStack poseStack = new PoseStack();
      poseStack.pushPose();
      this.setupPoseStack(poseStack, camera, pt);
      this.prepareDraw(poseStack, pt);
      float uOff = this.uvOffsetU;
      float vOff = this.uvOffsetV;
      DrawingFunction flowDrawing = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, alpha, u, v, overlay) -> DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP
         .draw(builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, alpha, u + uOff, v + vOff, overlay);
      ((Mesh)this.particleMeshProvider.get())
         .draw(poseStack, vertexBuffer, flowDrawing, this.getLightColor(pt), this.rCol, this.gCol, this.bCol, this.alpha, OverlayTexture.NO_OVERLAY);
      this.revert(poseStack);
      poseStack.popPose();
      if (this.caster != null && this.caster.getStunShield() <= 0.0F) {
         this.remove();
      }
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
      float yaw = Mth.lerp(partialTicks, this.yawO, this.yaw);
      float pitch = Mth.lerp(partialTicks, this.pitchO, this.pitch);
      float roll = Mth.lerp(partialTicks, this.oRoll, this.roll);
      Vec3 vec3 = camera.getPosition();
      float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
      float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
      float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
      float scale = Mth.lerp(partialTicks, this.scaleO, this.scale);
      poseStack.translate(x, y, z);
      poseStack.mulPose(QuaternionUtils.XP.rotationDegrees(pitch));
      poseStack.mulPose(QuaternionUtils.ZP.rotationDegrees(roll));
      poseStack.mulPose(QuaternionUtils.YP.rotationDegrees(yaw));
      float time = (this.age + partialTicks) * 0.1F;
      float intensity = 0.05F;
      float distortX = 1.0F + Mth.sin(time * 1.3F + 0.5F) * intensity;
      float distortY = 1.0F + Mth.sin(time * 0.9F) * intensity;
      float distortZ = 1.0F + Mth.cos(time * 1.1F + 1.0F) * intensity;
      poseStack.scale(scale * distortX, scale * distortY, scale * distortZ);
   }

   public int getLightColor(float p_107086_) {
      int i = super.getLightColor(p_107086_);
      int k = i >> 16 & 0xFF;
      return 240 | k << 16;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Provider(SpriteSet spriteSet) {
      }

      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new YamatoLastSphere(
            level, x, y, z, xSpeed, ySpeed, zSpeed, EFNMeshes.YAMATO_SPHERE, ResourceLocation.fromNamespaceAndPath("efn", "textures/models/particle/yamato_particle.png")
         );
      }
   }
}
