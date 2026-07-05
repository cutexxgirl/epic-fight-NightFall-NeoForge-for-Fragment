package com.hm.efn.client.particle.DMC;

import com.guhao.vix.util.RenderUtils;
import com.hm.efn.gameasset.EFNAnimations;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PhantomsParticle_Return extends NoRenderParticle {
   private final LivingEntityPatch<?> entityPatch;
   protected float lastAng = 0.0F;

   public PhantomsParticle_Return(ClientLevel clientLevel, double x, double y, double z, LivingEntityPatch<?> livingEntityPatch) {
      super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
      this.lastAng = Mth.nextFloat(this.random, 0.0F, 360.0F);
      this.entityPatch = livingEntityPatch;
      this.hasPhysics = false;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else if (((LivingEntity)this.entityPatch.getOriginal()).isAlive() && this.age % 2 == 0) {
         for (int i = 0; i < 1; i++) {
            this.lastAng = Mth.nextFloat(this.random, 120.0F + this.lastAng - 15.0F, 120.0F + this.lastAng + 15.0F);
            float r = Mth.nextFloat(this.random, 0.0F, 1.2F) + 6.0F;
            double startX = Math.sin(this.lastAng / 180.0F * Math.PI) * r;
            double startY = Mth.nextFloat(this.random, 2.0F, 5.0F);
            double startZ = Math.cos(this.lastAng / 180.0F * Math.PI) * r;
            Vec3 toPlayer = new Vec3(
                  ((LivingEntity)this.entityPatch.getOriginal()).getX() - (startX + this.x),
                  ((LivingEntity)this.entityPatch.getOriginal()).getY() - 2.0 - (startY + this.y),
                  ((LivingEntity)this.entityPatch.getOriginal()).getZ() - (startZ + this.z)
               )
               .normalize();
            double speed = 1.5 + this.random.nextDouble() * 0.3;
            int lifetime = (int)((r - 0.4) / speed);
            DynamicEntityAfterImgParticle particle = DynamicEntityAfterImgParticle.create(
               this.entityPatch,
               EFNAnimations.DMC5_V_JC,
               startX + this.x,
               startY + this.y + 2.0,
               startZ + this.z,
               toPlayer.x * speed,
               toPlayer.y * speed,
               toPlayer.z * speed,
               lifetime,
               2.25F
            );
            RenderUtils.AddParticle(this.level, particle);
         }
      }
   }

   public boolean shouldCull() {
      return false;
   }
}
