package com.hm.efn.entity.effect;

import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.epicfight.AvalonFactions;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.damagesource.StunType;

public class FireWindPatch extends VFXEntityPatch<FireWindEntity> {
   private float rotationX = 0.0F;
   private float rotationY = 0.0F;
   private float rotationZ = 0.0F;

   public FireWindPatch(FireWindEntity entity) {
      super(entity);
   }

   public boolean applyStun(StunType stunType, float stunTime) {
      return false;
   }

   public Faction getFaction() {
      return AvalonFactions.EMPTY;
   }

   public void setRotation(float xDeg, float yDeg, float zDeg) {
      this.rotationX = xDeg % 360.0F;
      this.rotationY = yDeg % 360.0F;
      this.rotationZ = zDeg % 360.0F;
   }
}
