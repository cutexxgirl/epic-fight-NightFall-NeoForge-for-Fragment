package com.hm.efn.entity.effect;

import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.gameasset.EFNAnimations;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;

public class SummonedSwordEntity extends VFXEntity {
   private final float targetYRot = 0.0F;
   private int tickCount = 0;
   private boolean hasAppliedTargetRotation = false;

   public SummonedSwordEntity(LivingEntity owner, float scale, Vec3 offset) {
      super((EntityType)EFNEntity.SUMMONED_SWORD.get(), owner, scale);
      this.noCulling = true;
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public SummonedSwordEntity(EntityType<? extends VFXEntity> entityType, Level level) {
      super(entityType, level);
      this.noCulling = true;
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   private static float normalizeAngle(float angle) {
      angle %= 360.0F;
      if (angle > 180.0F) {
         angle -= 360.0F;
      } else if (angle < -180.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   private static float calculateTargetYRotation(Vec3 spawnPos, LivingEntity target, ServerPlayer player) {
      if (target != null) {
         Vec3 targetPos = target.getEyePosition();
         Vec3 toTarget = targetPos.subtract(spawnPos);
         float rawAngle = (float)Math.toDegrees(Math.atan2(toTarget.x, toTarget.z));
         float minecraftYRot = -rawAngle;
         return normalizeAngle(minecraftYRot);
      } else {
         return player.getYRot();
      }
   }

   private static boolean isHostileMob(Entity entity) {
      if (!(entity instanceof LivingEntity)) {
         return false;
      } else {
         return entity instanceof DoppelgangerEntity
            ? false
            : entity instanceof Enemy
               || entity instanceof Monster
               || entity.getType() == EntityType.SLIME
               || entity.getType() == EntityType.MAGMA_CUBE
               || entity.getType() == EntityType.PHANTOM
               || entity.getType() == EntityType.GHAST;
      }
   }

   public void tick() {
      super.tick();
      this.tickCount++;
      if (!this.level().isClientSide && !this.hasAppliedTargetRotation && this.tickCount <= 20) {
         this.applyTargetRotation();
      }
   }

   private void applyTargetRotation() {
      float rawCurrentYRot = this.getYRot();
      float currentYRot = normalizeAngle(rawCurrentYRot);
      float normalizedTargetYRot = normalizeAngle(0.0F);
      float shortestPath = this.calculateShortestRotation(currentYRot, normalizedTargetYRot);
      float newYRot = currentYRot + shortestPath * 0.3F;
      this.setYRot(newYRot);
      this.setYBodyRot(newYRot);
      this.setYHeadRot(newYRot);
      float remainingPath = this.calculateShortestRotation(newYRot, normalizedTargetYRot);
      if (Math.abs(remainingPath) < 0.5F || this.tickCount <= 20) {
         this.setYRot(normalizedTargetYRot);
         this.setYBodyRot(normalizedTargetYRot);
         this.setYHeadRot(normalizedTargetYRot);
         this.hasAppliedTargetRotation = true;
      }
   }

   private float calculateShortestRotation(float current, float target) {
      float diff = target - current;
      if (diff > 180.0F) {
         diff -= 360.0F;
      } else if (diff < -180.0F) {
         diff += 360.0F;
      }

      return diff;
   }

   public float getTargetYRot() {
      return 0.0F;
   }

   public boolean hasAppliedTargetRotation() {
      return this.hasAppliedTargetRotation;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return EFNAnimations.SUMMONED_SWORD_IDLE;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      return EFNAnimations.SUMMONED_SWORD_IDLE;
   }

   @Nullable
   public Armature getArmature() {
      return ArmatureAccessor.create("efn", "entity/effect/summoned_sword", Armature::new).get();
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return MeshAccessor.create("efn", "entity/effect/summoned_sword", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }

   @Nullable
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/summoned_sword.png");
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/summoned_sword.png");
   }
}
