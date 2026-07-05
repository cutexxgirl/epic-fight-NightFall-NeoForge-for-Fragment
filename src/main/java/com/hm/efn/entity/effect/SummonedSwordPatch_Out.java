package com.hm.efn.entity.effect;

import com.hm.efn.client.sound.EFNSounds;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.epicfight.AvalonFactions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.DodgeLocationIndicator;

public class SummonedSwordPatch_Out extends VFXEntityPatch<SummonedSwordEntity_Out> {
   private float rotationX = 0.0F;
   private float rotationY = 0.0F;
   private float rotationZ = 0.0F;

   public SummonedSwordPatch_Out(SummonedSwordEntity_Out entity) {
      super(entity);
   }

   public boolean applyStun(StunType stunType, float stunTime) {
      return false;
   }

   public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
      if (target instanceof DodgeLocationIndicator) {
         return AttackResult.missed(0.0F);
      } else if (((SummonedSwordEntity_Out)this.original).getOwner() != null && target == ((SummonedSwordEntity_Out)this.original).getOwner()) {
         return AttackResult.missed(0.0F);
      } else if (target instanceof SummonedSwordEntity_Out summonedSword
         && summonedSword.getOwner() != null
         && summonedSword.getOwner() == ((SummonedSwordEntity_Out)this.original).getOwner()) {
         return AttackResult.missed(0.0F);
      } else if (target instanceof BlastSummonedSwordEntity blastSummonedSwordEntity
         && blastSummonedSwordEntity.getOwner() != null
         && blastSummonedSwordEntity.getOwner() == ((SummonedSwordEntity_Out)this.original).getOwner()) {
         return AttackResult.missed(0.0F);
      } else {
         return this.getOwnerPatch() != null && this.shouldUseOwnerAttack()
            ? this.getOwnerPatch().attack(damageSource, target, hand)
            : super.attack(damageSource, target, hand);
      }
   }

   public SoundEvent getSwingSound(InteractionHand hand) {
      return (SoundEvent)EFNSounds.NOSOUND.get();
   }

   public SoundEvent getWeaponHitSound(InteractionHand hand) {
      return (SoundEvent)EFNSounds.NOSOUND.get();
   }

   public Faction getFaction() {
      return AvalonFactions.EMPTY;
   }

   public void setRotation(float xDeg, float yDeg, float zDeg) {
      this.rotationX = xDeg % 360.0F;
      this.rotationY = yDeg % 360.0F;
      this.rotationZ = zDeg % 360.0F;
   }

   public float getRotationX() {
      return this.rotationX;
   }

   public float getRotationY() {
      return this.rotationY;
   }

   public float getRotationZ() {
      return this.rotationZ;
   }
}
