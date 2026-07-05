package com.hm.efn.client.particle.DMC;

import com.guhao.vix.util.RenderUtils;
import com.hm.efn.gameasset.EFNAnimations;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PhantomsParticle extends NoRenderParticle {
   private final LivingEntityPatch<?> entityPatch;
   protected float lastAng = 0.0F;

   public PhantomsParticle(ClientLevel clientLevel, double x, double y, double z, LivingEntityPatch<?> livingEntityPatch) {
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
            double x_ = Math.sin(this.lastAng / 180.0F * Math.PI) * r;
            double y_ = Mth.nextFloat(this.random, -1.0F, 2.0F);
            double z_ = Math.cos(this.lastAng / 180.0F * Math.PI) * r;
            float ang2 = Mth.nextFloat(this.random, 180.0F + this.lastAng - 20.0F, 180.0F + this.lastAng + 20.0F);
            r = Mth.nextFloat(this.random, 0.0F, 1.2F) + 6.0F;
            double dx = Math.sin(ang2 / 180.0F * Math.PI) * r - x_;
            double dy;
            if (y_ > 1.2F) {
               dy = Mth.nextFloat(this.random, -1.0F, -0.2F);
            } else if (y_ < -0.2F) {
               dy = Mth.nextFloat(this.random, 1.2F, 2.0F);
            } else {
               dy = Mth.nextFloat(this.random, -0.2F, 1.2F);
            }

            double dz = Math.cos(ang2 / 180.0F * Math.PI) * r - y_;
            double len = new Vec3(dx, dy, dz).length();
            int lft = 5;
            double v = len / lft;
            dx /= len / v;
            dy /= len / v;
            dz /= len / v;
            DynamicEntityAfterImgParticle particle = DynamicEntityAfterImgParticle.create(
               this.entityPatch, EFNAnimations.DMC5_V_JC, x_ + this.x, y_ + this.y + 2.0, z_ + this.z, dx, dy, dz, lft, 1.7F
            );
            RenderUtils.AddParticle(this.level, particle);
         }
      }
   }

   public boolean shouldCull() {
      return false;
   }
}
