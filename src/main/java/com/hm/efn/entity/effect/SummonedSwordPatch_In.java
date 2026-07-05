package com.hm.efn.entity.effect;

import com.hm.efn.client.sound.EFNSounds;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.epicfight.AvalonFactions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public class SummonedSwordPatch_In extends VFXEntityPatch<SummonedSwordEntity_In> {
   private float rotationX = 0.0F;
   private float rotationY = 0.0F;
   private float rotationZ = 0.0F;

   public SummonedSwordPatch_In(SummonedSwordEntity_In entity) {
      super(entity);
   }

   public boolean applyStun(StunType stunType, float stunTime) {
      return false;
   }

   public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
      if (this.getOwnerPatch() != null && this.shouldUseOwnerAttack()) {
         ServerPlayerPatch ownerPatch = (ServerPlayerPatch)this.getOwnerPatch();
         if (((SummonedSwordEntity_In)this.original).getOwner() != null && target == ((SummonedSwordEntity_In)this.original).getOwner()) {
            return AttackResult.missed(0.0F);
         } else if (target instanceof SummonedSwordEntity_In summonedSwordEntity_In
            && summonedSwordEntity_In.getOwner() != null
            && summonedSwordEntity_In.getOwner() == ((SummonedSwordEntity_In)this.original).getOwner()) {
            return AttackResult.missed(0.0F);
         } else {
            return target instanceof BlastSummonedSwordEntity blastSummonedSwordEntity
                  && blastSummonedSwordEntity.getOwner() != null
                  && blastSummonedSwordEntity.getOwner() == ((SummonedSwordEntity_In)this.original).getOwner()
               ? AttackResult.missed(0.0F)
               : this.getOwnerPatch().attack(damageSource, target, hand);
         }
      } else {
         return super.attack(damageSource, target, hand);
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
}
