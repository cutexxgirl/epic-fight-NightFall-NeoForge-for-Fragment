package com.hm.efn.entity.effect;

import com.hm.efn.entity.EFNEntity;
import com.hm.efn.gameasset.EFNAnimations;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class CoTachiSlashEntity extends VFXEntity {
   // co_tachi_slash_1: 1.0s / 1.3 speed ~= 16 ticks.
   private static final int FALLBACK_LIFETIME_TICKS = 16;
   public static final EntityDataAccessor<Float> DATA_Z_ROT = SynchedEntityData.defineId(CoTachiSlashEntity.class, EntityDataSerializers.FLOAT);

   public CoTachiSlashEntity(LivingEntity owner, float scale, Vec3 offset) {
      super((EntityType)EFNEntity.CO_TACHI_SLASH.get(), owner, scale);
      this.noCulling = true;
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public CoTachiSlashEntity(EntityType<? extends VFXEntity> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.noCulling = true;
      this.setNoGravity(true);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && !this.isRemoved() && this.tickCount >= FALLBACK_LIFETIME_TICKS) {
         this.discard();
      }
   }

   public static void applyRotationToJoint(Pose pose, Armature armature, String jointName, float xRot, float yRot, float zRot) {
      if (armature != null && pose != null) {
         Joint joint = armature.searchJointByName(jointName);
         if (joint != null) {
            Vec3f xAxis = OpenMatrix4f.transform3v(joint.getLocalTransform(), Vec3f.X_AXIS, null);
            Vec3f yAxis = OpenMatrix4f.transform3v(joint.getLocalTransform(), Vec3f.Y_AXIS, null);
            Vec3f zAxis = OpenMatrix4f.transform3v(joint.getLocalTransform(), Vec3f.Z_AXIS, null);
            OpenMatrix4f rotation = OpenMatrix4f.createRotatorDeg(yRot, yAxis).rotateDeg(xRot, xAxis).rotateDeg(zRot, zAxis);
            pose.orElseEmpty(jointName).frontResult(JointTransform.fromMatrix(rotation), OpenMatrix4f::mul);
         }
      }
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_Z_ROT, 0.0F);
   }

   public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (DATA_Z_ROT.equals(key) && this.level().isClientSide) {
         CoTachiSlashPatch patch = (CoTachiSlashPatch)this.getPatch();
         if (patch != null) {
            patch.setZRotation((Float)this.entityData.get(DATA_Z_ROT));
         }
      }
   }

   @NotNull
   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(100.0);
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return EFNAnimations.CO_TACHI_SLASH_IDLE;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      return EFNAnimations.CO_TACHI_SLASH_ONE;
   }

   @Nullable
   public Armature getArmature() {
      return ArmatureAccessor.create("efn", "entity/effect/co_tachi_slash", Armature::new).get();
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return MeshAccessor.create("efn", "entity/effect/co_tachi_slash", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }

   @Nullable
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/co_tachi_slash.png");
   }

   @Nullable
   public CoTachiSlashPatch getEntityPatch() {
      return this.getPatch(CoTachiSlashPatch.class);
   }
}
