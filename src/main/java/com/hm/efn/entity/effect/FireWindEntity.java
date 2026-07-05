package com.hm.efn.entity.effect;

import com.hm.efn.entity.EFNEntity;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.util.CullableUtil;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class FireWindEntity extends VFXEntity {
   private static final int MAX_LIFETIME_TICKS = 120;
   private LivingEntity owner;
   private Vec3 offset;
   private boolean shouldRemove = false;

   public FireWindEntity(LivingEntity owner, float scale, Vec3 offset) {
      super((EntityType)EFNEntity.FIREWIND.get(), owner, scale);
      this.owner = owner;
      this.noCulling = true;
      this.offset = offset;
      this.noPhysics = true;
      this.setInvulnerable(true);
      this.setNoGravity(true);
   }

   public FireWindEntity(EntityType<? extends VFXEntity> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.noCulling = true;
      this.setInvulnerable(true);
      this.setNoGravity(true);
   }

   public void markForRemoval() {
      this.shouldRemove = true;
   }

   public void tick() {
      super.tick();
      CullableUtil.setAlwaysVisible(this);
      if (this.shouldRemove) {
         this.discard();
      } else {
         if (!this.level().isClientSide && (this.tickCount >= MAX_LIFETIME_TICKS || this.owner == null || !this.owner.isAlive())) {
            this.discard();
            return;
         }

         if (!this.level().isClientSide && this.owner != null) {
            Vec3 targetPos = this.owner.position().add(this.offset.x, this.offset.y, this.offset.z);
            Vec3 currentPos = this.position();
            Vec3 newPos = currentPos.add(targetPos.subtract(currentPos).scale(1.0));
            this.setPos(newPos);
         }
      }
   }

   @NotNull
   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(100.0);
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return EFNAnimations.FIREWIND_TWO;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      return EFNAnimations.FIREWIND_ONE;
   }

   @Nullable
   public Armature getArmature() {
      return ArmatureAccessor.create("efn", "entity/effect/firewind", Armature::new).get();
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return MeshAccessor.create("efn", "entity/effect/firewind", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }

   @Nullable
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/firewind.png");
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/firewind.png");
   }

   @Nullable
   public FireWindPatch getEntityPatch() {
      return this.getPatch(FireWindPatch.class);
   }
}
