package com.hm.efn.api;

import com.merlin204.avalon.entity.IAvalonMeshEntity;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class ProjectileVFXEntity extends Projectile implements IAvalonMeshEntity {
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(
      ProjectileVFXEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> Y_ROT_OFFSET = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> X_ROT_OFFSET = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> Z_ROT_OFFSET = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> START_Y_ROT = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Boolean> PLAY_ANIMATION = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> SHOULD_RENDER = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<String> MESH_PATH = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.STRING);
   protected static final EntityDataAccessor<String> ARMATURE_PATH = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.STRING);
   protected static final EntityDataAccessor<String> TEXTURE_PATH = SynchedEntityData.defineId(ProjectileVFXEntity.class, EntityDataSerializers.STRING);
   protected static final EntityDataAccessor<String> LIGHT_TEXTURE_PATH = SynchedEntityData.defineId(
      ProjectileVFXEntity.class, EntityDataSerializers.STRING
   );
   protected ArmatureAccessor<? extends Armature> ARMATURE_ACCESSOR;
   protected AssetAccessor<? extends SkinnedMesh> MESH;
   protected ResourceLocation TEXTURE;
   protected ResourceLocation LIGHT_TEXTURE;
   protected AnimationAccessor<? extends StaticAnimation> DEFAULT_ANIMATION;

   protected ProjectileVFXEntity(EntityType<? extends Projectile> pEntityType, Level pLevel, float scale) {
      super(pEntityType, pLevel);
      this.getEntityData().set(SCALE, scale);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public ProjectileVFXEntity(EntityType<? extends ProjectileVFXEntity> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.setNoGravity(true);
      this.ARMATURE_ACCESSOR = null;
      this.MESH = null;
      this.TEXTURE = null;
      this.DEFAULT_ANIMATION = null;
      this.LIGHT_TEXTURE = null;
   }

   public ProjectileVFXEntity(
      EntityType<? extends ProjectileVFXEntity> entityType,
      LivingEntity owner,
      float scale,
      Vec3f rotOffset,
      ArmatureAccessor<? extends Armature> armatureAccessor,
      AssetAccessor<? extends SkinnedMesh> mesh,
      ResourceLocation texture,
      ResourceLocation lightTexture,
      AnimationAccessor<? extends StaticAnimation> defaultAnimation
   ) {
      super(entityType, owner.level());
      this.setOwner(owner);
      this.noCulling = true;
      this.LIGHT_TEXTURE = lightTexture;
      this.getEntityData().set(SCALE, scale);
      this.getEntityData().set(X_ROT_OFFSET, rotOffset.x);
      this.getEntityData().set(Y_ROT_OFFSET, rotOffset.y);
      this.getEntityData().set(Z_ROT_OFFSET, rotOffset.z);
      this.noPhysics = true;
      this.setNoGravity(true);
      this.ARMATURE_ACCESSOR = armatureAccessor;
      float ownerYRot = this.getOwner().getYRot();
      this.MESH = mesh;
      this.TEXTURE = texture;
      this.DEFAULT_ANIMATION = defaultAnimation;
      this.entityData.set(ARMATURE_PATH, this.ARMATURE_ACCESSOR.registryName().toString());
      this.entityData.set(MESH_PATH, this.MESH.registryName().toString());
      this.entityData.set(TEXTURE_PATH, this.TEXTURE.toString());
      this.entityData.set(LIGHT_TEXTURE_PATH, this.LIGHT_TEXTURE.toString());
   }

   @Nullable
   public Armature getArmature() {
      return this.ARMATURE_ACCESSOR != null ? this.ARMATURE_ACCESSOR.get() : null;
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return this.MESH;
   }

   @Nullable
   public ResourceLocation getTexture() {
      return this.TEXTURE;
   }

   public float getScale() {
      return (Float)this.entityData.get(SCALE);
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      return null;
   }

   public void tick() {
      super.tick();
      this.fallDistance = 0.0F;
      this.noPhysics = true;
      this.setNoGravity(true);
      boolean playAnimation = this.getPlayAnimation();
      if (!playAnimation) {
         float ownerYRot = this.getStartYRot();
         this.setYRot(ownerYRot);
         this.setYBodyRot(ownerYRot);
         this.setYHeadRot(ownerYRot);
      }

      if (this.level().isClientSide) {
         if (this.MESH == null) {
            this.MESH = MeshAccessor.create(
               ResourceLocation.parse((String)this.entityData.get(MESH_PATH)).getNamespace(),
               ResourceLocation.parse((String)this.entityData.get(MESH_PATH)).getPath(),
               jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new)
            );
         }

         if (this.ARMATURE_ACCESSOR == null) {
            this.ARMATURE_ACCESSOR = ArmatureAccessor.create(
               ResourceLocation.parse((String)this.entityData.get(ARMATURE_PATH)).getNamespace(),
               ResourceLocation.parse((String)this.entityData.get(ARMATURE_PATH)).getPath(),
               Armature::new
            );
         }

         if (this.TEXTURE == null) {
            this.TEXTURE = ResourceLocation.parse((String)this.entityData.get(TEXTURE_PATH));
         }

         if (this.LIGHT_TEXTURE == null) {
            this.LIGHT_TEXTURE = ResourceLocation.parse((String)this.entityData.get(LIGHT_TEXTURE_PATH));
         }
      }
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return this.LIGHT_TEXTURE;
   }

   public float getXRotOffset() {
      return (Float)this.entityData.get(X_ROT_OFFSET);
   }

   public float getZRotOffset() {
      return (Float)this.entityData.get(Z_ROT_OFFSET);
   }

   public float getYRotOffset() {
      return (Float)this.entityData.get(Y_ROT_OFFSET);
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return Animations.EMPTY_ANIMATION;
   }

   public void setOwnerUUID(@Nullable UUID pUuid) {
      this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
   }

   public int getOwnerID() {
      return (Integer)this.entityData.get(DATA_OWNER_ID);
   }

   public void setOwnerID(int id) {
      this.entityData.set(DATA_OWNER_ID, id);
   }

   public float getStartYRot() {
      return (Float)this.entityData.get(START_Y_ROT);
   }

   public void setStartYRot(float f) {
      this.entityData.set(START_Y_ROT, f);
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      builder.define(DATA_OWNER_UUID, Optional.empty());
      builder.define(DATA_OWNER_ID, 0);
      builder.define(SCALE, 1.0F);
      builder.define(Y_ROT_OFFSET, 0.0F);
      builder.define(X_ROT_OFFSET, 0.0F);
      builder.define(Z_ROT_OFFSET, 0.0F);
      builder.define(START_Y_ROT, 0.0F);
      builder.define(PLAY_ANIMATION, false);
      builder.define(SHOULD_RENDER, false);
      builder.define(ARMATURE_PATH, "");
      builder.define(MESH_PATH, "");
      builder.define(TEXTURE_PATH, "");
      builder.define(LIGHT_TEXTURE_PATH, "");
   }

   public boolean canChangeDimensions() {
      return false;
   }

   public boolean hurt(@NotNull DamageSource source, float p_21017_) {
      return false;
   }

   protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
      return false;
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }

   protected void moveToOwner(LivingEntity owner) {
      this.setYRot(owner.yBodyRot);
      this.setYBodyRot(owner.yBodyRot);
      this.setYHeadRot(owner.yBodyRot);
      this.setPos(owner.position());
   }

   public LivingEntityPatch<?> getOwnerPatch() {
      return (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this.getOwner(), LivingEntityPatch.class);
   }

   public LivingEntityPatch<?> getPatch() {
      return (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);
   }

   public <T extends EntityPatch<?>> T getPatch(Class<T> type) {
      return (T)EpicFightCapabilities.getEntityPatch(this, type);
   }

   public boolean getPlayAnimation() {
      return (Boolean)this.entityData.get(PLAY_ANIMATION);
   }

   public void setPlayAnimation(boolean b) {
      this.entityData.set(PLAY_ANIMATION, b);
   }

   public boolean getShouldRender() {
      return (Boolean)this.entityData.get(SHOULD_RENDER);
   }

   public void setShouldRender(boolean b) {
      this.entityData.set(SHOULD_RENDER, b);
   }
}
