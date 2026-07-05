package com.hm.efn.entity.effect;

import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.epicfight.AvalonFactions;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.damagesource.StunType;

public class CoTachiSlashPatch extends VFXEntityPatch<CoTachiSlashEntity> {
   private static final String ROOT_JOINT = "xyz";
   private float zRotation = 0.0F;
   private boolean matrixApplied = false;
   private OpenMatrix4f cachedMatrix;

   public CoTachiSlashPatch(CoTachiSlashEntity entity) {
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
      if (!((CoTachiSlashEntity)this.original).level().isClientSide) {
         ((CoTachiSlashEntity)this.original).getEntityData().set(CoTachiSlashEntity.DATA_Z_ROT, degrees);
      }
   }

   public void poseTick(DynamicAnimation animation, Pose pose, float elapsedTime, float partialTicks) {
      if (this.original != null && pose != null) {
         CoTachiSlashEntity.applyRotationToJoint(pose, this.getArmature(), "xyz", 0.0F, 180.0F, this.zRotation);
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
