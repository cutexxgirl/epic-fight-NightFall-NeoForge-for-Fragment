package com.hm.efn.client.particle;

import com.hm.efn.EFNClientConfig;
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
import org.jetbrains.annotations.NotNull;
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
public class NoWeaponEntityAfterimageParticle extends CustomModelParticle<SkinnedMesh> {
   protected final EntitySnapshot<?> entitySnapshot;
   protected final Consumer<NoWeaponEntityAfterimageParticle> ticktask;
   protected float alphaO;

   public NoWeaponEntityAfterimageParticle(
      ClientLevel level,
      double x,
      double y,
      double z,
      double xd,
      double yd,
      double zd,
      EntitySnapshot<?> entitySnapshot,
      Consumer<NoWeaponEntityAfterimageParticle> ticktask
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
   public static class NoWeaponBlueAfterimageParticle extends NoWeaponEntityAfterimageParticle {
      public NoWeaponBlueAfterimageParticle(
         ClientLevel level,
         double x,
         double y,
         double z,
         double xd,
         double yd,
         double zd,
         EntitySnapshot<?> entitySnapshot,
         Consumer<NoWeaponEntityAfterimageParticle> ticktask
      ) {
         super(level, x, y, z, xd, yd, zd, entitySnapshot, ticktask);
         this.rCol = 0.05F;
         this.gCol = 0.4F;
         this.bCol = 0.75F;
      }

      @Override
      public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
         float alpha = Mth.lerp(partialTicks, this.alphaO, this.alpha);
         int lightColor = this.getLightColor(partialTicks);
         PoseStack poseStack = new PoseStack();
         this.setupPoseStack(poseStack, camera, partialTicks);
         BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
         this.entitySnapshot
            .renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
         buffers.endLastBatch();
         this.entitySnapshot
            .render(
               poseStack,
               buffers,
               EpicFightRenderTypes.entityAfterimageWhite(),
               DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP,
               lightColor,
               this.rCol,
               this.gCol,
               this.bCol,
               alpha * 0.8F
            );
         buffers.endLastBatch();
         this.revert(poseStack);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NoWeaponBlueAfterimageProvider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
         LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (entityPatch != null && (Boolean)EFNClientConfig.AFTERIMAGE.get()) {
            EntitySnapshot<?> entitySnapshot = entityPatch.captureEntitySnapshot();
            if (entitySnapshot != null) {
               NoWeaponEntityAfterimageParticle.NoWeaponBlueAfterimageParticle mainAfterimage = new NoWeaponEntityAfterimageParticle.NoWeaponBlueAfterimageParticle(
                  level,
                  x,
                  y,
                  z,
                  xSpeed,
                  ySpeed,
                  zSpeed,
                  entitySnapshot,
                  particle -> particle.alpha = (float)(particle.lifetime - particle.age) / particle.lifetime
               );
               mainAfterimage.setLifetime(4);
               return mainAfterimage;
            }
         }

         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NoWeaponRedAfterimageParticle extends NoWeaponEntityAfterimageParticle {
      public NoWeaponRedAfterimageParticle(
         ClientLevel level,
         double x,
         double y,
         double z,
         double xd,
         double yd,
         double zd,
         EntitySnapshot<?> entitySnapshot,
         Consumer<NoWeaponEntityAfterimageParticle> ticktask
      ) {
         super(level, x, y, z, xd, yd, zd, entitySnapshot, ticktask);
         this.rCol = 1.0F;
         this.gCol = 0.2F;
         this.bCol = 0.2F;
      }

      @Override
      public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
         float alpha = Mth.lerp(partialTicks, this.alphaO, this.alpha);
         int lightColor = this.getLightColor(partialTicks);
         PoseStack poseStack = new PoseStack();
         this.setupPoseStack(poseStack, camera, partialTicks);
         BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
         this.entitySnapshot
            .renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
         buffers.endLastBatch();
         this.entitySnapshot
            .render(
               poseStack,
               buffers,
               EpicFightRenderTypes.entityAfterimageWhite(),
               DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP,
               lightColor,
               this.rCol,
               this.gCol,
               this.bCol,
               alpha * 0.8F
            );
         buffers.endLastBatch();
         this.revert(poseStack);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NoWeaponRedAfterimageProvider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
         LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (entityPatch != null && (Boolean)EFNClientConfig.AFTERIMAGE.get()) {
            EntitySnapshot<?> entitySnapshot = entityPatch.captureEntitySnapshot();
            if (entitySnapshot != null) {
               float yaw = entity.getYRot();
               double offsetX = -Math.sin(Math.toRadians(yaw)) * 0.7;
               double offsetZ = Math.cos(Math.toRadians(yaw)) * 0.7;
               NoWeaponEntityAfterimageParticle.NoWeaponRedAfterimageParticle mainAfterimage = new NoWeaponEntityAfterimageParticle.NoWeaponRedAfterimageParticle(
                  level,
                  x + offsetX,
                  y,
                  z + offsetZ,
                  xSpeed,
                  ySpeed,
                  zSpeed,
                  entitySnapshot,
                  particle -> particle.alpha = (float)(particle.lifetime - particle.age) / particle.lifetime
               );
               mainAfterimage.setLifetime(1);
               double offsetX1 = -Math.sin(Math.toRadians(yaw)) * 0.8;
               double offsetZ1 = Math.cos(Math.toRadians(yaw)) * 0.8;
               NoWeaponEntityAfterimageParticle.NoWeaponRedAfterimageParticle childAfterimage1 = new NoWeaponEntityAfterimageParticle.NoWeaponRedAfterimageParticle(
                  level,
                  x + offsetX1,
                  y,
                  z + offsetZ1,
                  xSpeed,
                  ySpeed,
                  zSpeed,
                  entitySnapshot,
                  particle -> particle.alpha = (float)(particle.lifetime - particle.age) / particle.lifetime
               );
               childAfterimage1.setLifetime(1);
               Minecraft.getInstance().particleEngine.add(childAfterimage1);
               double offsetX2 = -Math.sin(Math.toRadians(yaw)) * 0.9;
               double offsetZ2 = Math.cos(Math.toRadians(yaw)) * 0.9;
               NoWeaponEntityAfterimageParticle.NoWeaponRedAfterimageParticle childAfterimage2 = new NoWeaponEntityAfterimageParticle.NoWeaponRedAfterimageParticle(
                  level,
                  x + offsetX2,
                  y,
                  z + offsetZ2,
                  xSpeed,
                  ySpeed,
                  zSpeed,
                  entitySnapshot,
                  particle -> particle.alpha = (float)(particle.lifetime - particle.age) / particle.lifetime
               );
               childAfterimage2.setLifetime(1);
               Minecraft.getInstance().particleEngine.add(childAfterimage2);
               return mainAfterimage;
            }
         }

         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NoWeaponShortBlueAfterimageProvider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
         LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (entityPatch != null && (Boolean)EFNClientConfig.AFTERIMAGE.get()) {
            EntitySnapshot<?> entitySnapshot = entityPatch.captureEntitySnapshot();
            if (entitySnapshot != null) {
               NoWeaponEntityAfterimageParticle.NoWeaponBlueAfterimageParticle mainAfterimage = new NoWeaponEntityAfterimageParticle.NoWeaponBlueAfterimageParticle(
                  level,
                  x,
                  y,
                  z,
                  xSpeed,
                  ySpeed,
                  zSpeed,
                  entitySnapshot,
                  particle -> particle.alpha = (float)(particle.lifetime - particle.age) / particle.lifetime
               );
               mainAfterimage.setLifetime(1);
               mainAfterimage.setAlpha(0.5F);
               return mainAfterimage;
            }
         }

         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NoWeaponWhiteAfterimageParticle extends NoWeaponEntityAfterimageParticle {
      public NoWeaponWhiteAfterimageParticle(
         ClientLevel level,
         double x,
         double y,
         double z,
         double xd,
         double yd,
         double zd,
         EntitySnapshot<?> entitySnapshot,
         Consumer<NoWeaponEntityAfterimageParticle> ticktask
      ) {
         super(level, x, y, z, xd, yd, zd, entitySnapshot, ticktask);
      }

      @Override
      public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
         float alpha = Mth.lerp(partialTicks, this.alphaO, this.alpha);
         int lightColor = this.getLightColor(partialTicks);
         PoseStack poseStack = new PoseStack();
         this.setupPoseStack(poseStack, camera, partialTicks);
         BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
         this.entitySnapshot
            .renderTextured(poseStack, buffers, EpicFightRenderTypes::entityAfterimageStencil, DrawingFunction.POSITION_TEX, 0, 0.0F, 0.0F, 0.0F, 1.0F);
         buffers.endLastBatch();
         this.entitySnapshot
            .render(
               poseStack,
               buffers,
               EpicFightRenderTypes.entityAfterimageWhite(),
               DrawingFunction.POSITION_TEX_COLOR_LIGHTMAP,
               lightColor,
               this.rCol,
               this.gCol,
               this.bCol,
               alpha
            );
         buffers.endLastBatch();
         this.revert(poseStack);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NoWeaponWhiteAfterimageProvider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
         LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (entitypatch != null && (Boolean)EFNClientConfig.AFTERIMAGE.get()) {
            EntitySnapshot<?> entitySnapshot = entitypatch.captureEntitySnapshot();
            if (entitySnapshot != null) {
               NoWeaponEntityAfterimageParticle.NoWeaponWhiteAfterimageParticle mainAfterimage = new NoWeaponEntityAfterimageParticle.NoWeaponWhiteAfterimageParticle(
                  level,
                  x,
                  y,
                  z,
                  xSpeed,
                  ySpeed,
                  zSpeed,
                  entitySnapshot,
                  particle -> particle.alpha = (float)(particle.lifetime - particle.age) / particle.lifetime
               );
               mainAfterimage.setLifetime(5);
               return mainAfterimage;
            }
         }

         return null;
      }
   }
}
