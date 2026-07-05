package com.hm.efn.entity.effect;

import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.merlin204.avalon.entity.vfx.VFXEntityPatch;
import com.merlin204.avalon.epicfight.AvalonFactions;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.damagesource.StunType;

public class SummonedSwordPatch extends VFXEntityPatch<SummonedSwordEntity> {
   private int tickCount = 0;

   public SummonedSwordPatch(SummonedSwordEntity entity) {
      super(entity);
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

   public boolean applyStun(StunType stunType, float stunTime) {
      return false;
   }

   public Faction getFaction() {
      return AvalonFactions.EMPTY;
   }

   public void preTick(Pre event) {
      super.preTick(event);
      this.tickCount++;
      if (this.tickCount >= 1 && this.tickCount <= 4) {
         float sacle = 0.3F;
         if (this.target() != null) {
            this.setAttakTargetSync(this.target());
            Vec3 targetpos = new Vec3(this.target().getX(), this.target().getEyeY() - 0.2, this.target().getZ());
            Vec3 vector = targetpos.subtract(((SummonedSwordEntity)this.getOriginal()).position()).scale(sacle);
            ((SummonedSwordEntity)this.getOriginal()).setDeltaMovement(((SummonedSwordEntity)this.getOriginal()).getDeltaMovement().add(vector));
            return;
         }

         Vec3 lookAngle = ((SummonedSwordEntity)this.getOriginal()).getLookAngle();
         Vec3 acceleration = lookAngle.scale(sacle * 8.25F);
         ((SummonedSwordEntity)this.getOriginal()).setDeltaMovement(((SummonedSwordEntity)this.getOriginal()).getDeltaMovement().add(acceleration));
      }
   }

   public LivingEntity target() {
      if (this.getOwnerPatch().getTarget() != null) {
         return this.getOwnerPatch().getTarget();
      }

      if (this.getTarget() != null) {
         return this.getTarget();
      }

      Level level = ((SummonedSwordEntity)this.getOriginal()).level();
      double range = 16.0;
      List<Entity> nearbyEntities = level.getEntities(
         this.getOwnerPatch().getOriginal(),
         ((LivingEntity)this.getOwnerPatch().getOriginal()).getBoundingBox().inflate(range),
         entityx -> isHostileMob(entityx) && entityx != this.getOwnerPatch().getOriginal() && !(entityx instanceof DoppelgangerEntity)
      );
      Entity nearestTarget = null;
      double minDistance = Double.MAX_VALUE;
      Entity owner = this.getOwnerPatch().getOriginal();

      for (Entity entity : nearbyEntities) {
         double distance = owner.distanceToSqr(entity);
         if (level.clip(new ClipContext(owner.getEyePosition(1.0F), entity.getEyePosition(1.0F), Block.COLLIDER, Fluid.NONE, owner)).getType() == Type.MISS
            && distance < minDistance) {
            minDistance = distance;
            nearestTarget = entity;
         }
      }

      return nearestTarget instanceof LivingEntity ? (LivingEntity)nearestTarget : null;
   }
}
