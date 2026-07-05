package com.hm.efn.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Mesh.DrawingFunction;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.client.particle.CustomModelParticle;
import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class EFNEntityAfterimageParticle extends CustomModelParticle<SkinnedMesh> {
   protected final EntitySnapshot<?> entitySnapshot;
   protected final Consumer<EFNEntityAfterimageParticle> ticktask;
   protected float alphaO;

   public EFNEntityAfterimageParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      EntitySnapshot<?> entitySnapshot,
      Consumer<EFNEntityAfterimageParticle> ticktask
   ) {
      super(level, x, y, z, xd, yd, zd, null);
      this.entitySnapshot = entitySnapshot;
      this.ticktask = ticktask;
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.alphaO = 1.0F;
      this.alpha = 1.0F;
      this.yawO = entitySnapshot.getYRot();
      this.yaw = entitySnapshot.getYRot();
   }

   public void tick() {
      super.tick();
      this.alphaO = this.alpha;
      this.ticktask.accept(this);
   }

   public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
      float alpha = Mth.lerp(partialTicks, this.alphaO, this.alpha);
      int lightColor = this.getLightColor(partialTicks);
      PoseStack poseStack = new PoseStack();
      this.setupPoseStack(poseStack, camera, partialTicks);
      BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
      this.entitySnapshot
         .renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
      this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageStencil(), DrawingFunction.POSITION_TEX, lightColor, 1.0F);
      buffers.endLastBatch();
      this.entitySnapshot
         .renderTextured(
            poseStack,
            buffers,
            EpicFightRenderTypes::entityAfterimageTranslucent,
            DrawingFunction.NEW_ENTITY,
            lightColor,
            this.rCol,
            this.gCol,
            this.bCol,
            alpha
         );
      this.entitySnapshot.renderItems(poseStack, buffers, EpicFightRenderTypes.itemAfterimageTranslucent(), DrawingFunction.NEW_ENTITY, lightColor, alpha);
      buffers.endLastBatch();
      this.revert(poseStack);
   }

   public ParticleRenderType getRenderType() {
      return EpicFightParticleRenderTypes.ENTITY_PARTICLE;
   }

   protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTick) {
      poseStack.pushPose();
      Vec3 cameraPosition = camera.getPosition();
      float x = (float)(Mth.lerp(partialTick, this.xo, this.x) - cameraPosition.x());
      float y = (float)(Mth.lerp(partialTick, this.yo, this.y) - cameraPosition.y());
      float z = (float)(Mth.lerp(partialTick, this.zo, this.z) - cameraPosition.z());
      poseStack.translate(x, y, z);
      Quaternionf rotation = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
      rotation.mul(QuaternionUtils.YP.rotationDegrees(180.0F));
      poseStack.mulPose(rotation);
      poseStack.mulPose(OpenMatrix4f.exportToMojangMatrix(this.entitySnapshot.getModelMatrix()));
      float scale = Mth.lerp(partialTick, this.scaleO, this.scale);
      poseStack.translate(0.0F, this.entitySnapshot.getHeightHalf(), 0.0F);
      poseStack.scale(scale, scale, scale);
      poseStack.translate(0.0F, -this.entitySnapshot.getHeightHalf(), 0.0F);
   }

   protected void revert(PoseStack poseStack) {
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public static class SoulAfterImageParticleProvider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
         LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (entitypatch != null) {
            EntitySnapshot<?> entitySnapshot = entitypatch.captureEntitySnapshot();
            if (entitySnapshot != null) {
               EFNEntityAfterimageParticle soulparticle = new EFNEntityAfterimageParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, entitySnapshot, particle -> {
                  particle.alpha -= 0.025F;
                  particle.scale += (-0.0025F * particle.age * particle.age + 1.0F) * 0.02F;
               });
               soulparticle.setLifetime(20);
               soulparticle.setAlpha(0.65F);
               return soulparticle;
            }
         }

         return null;
      }
   }
}
