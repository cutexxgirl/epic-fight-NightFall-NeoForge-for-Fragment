package com.hm.efn.entity.effect;

import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.epicfight.AvalonFactions;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.damagesource.StunType;

public class TrailPatch extends VFXEntityPatch<TrailEntity> {
   private static final String ROOT_JOINT = "Root";
   private float zRotation = 0.0F;
   private boolean matrixApplied = false;
   private OpenMatrix4f cachedMatrix;

   public TrailPatch(TrailEntity entity) {
      super(entity);
   }

   public boolean applyStun(StunType stunType, float stunTime) {
      return false;
   }

   public Faction getFaction() {
      return AvalonFactions.EMPTY;
   }

   public void setZRotation(float degrees) {
      this.zRotation = degrees;
      if (!((TrailEntity)this.original).level().isClientSide) {
         ((TrailEntity)this.original).getEntityData().set(TrailEntity.DATA_Z_ROT, degrees);
      }
   }

   public void poseTick(DynamicAnimation animation, Pose pose, float elapsedTime, float partialTicks) {
      if (this.original != null && pose != null) {
         TrailEntity.applyRotationToJoint(pose, this.getArmature(), "Root", this.zRotation, 90.0F, 0.0F);
      }
   }

   public OpenMatrix4f getModelMatrix(float partialTicks) {
      OpenMatrix4f originalMatrix = super.getModelMatrix(partialTicks);
      if (this.getOwnerPatch() == null || !this.isLogicalClient()) {
         return originalMatrix;
      } else if (!this.matrixApplied) {
         OpenMatrix4f ownerMatrix = this.getOwnerPatch().getModelMatrix(partialTicks);
         this.cachedMatrix = new OpenMatrix4f();
         OpenMatrix4f.mul(ownerMatrix, originalMatrix, this.cachedMatrix);
         this.matrixApplied = true;
         return this.cachedMatrix;
      } else {
         return this.cachedMatrix != null ? this.cachedMatrix : originalMatrix;
      }
   }
}
