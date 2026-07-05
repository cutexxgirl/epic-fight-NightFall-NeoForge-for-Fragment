package com.hm.efn.api;

import com.merlin204.avalon.epicfight.AvalonFactions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.projectile.ProjectilePatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public abstract class ProjectileVFXEntityPatch<T extends ProjectileVFXEntity> extends ProjectilePatch<T> {
   @Nullable
   private LivingEntityPatch<?> ownerPatch;

   protected ProjectileVFXEntityPatch(T original) {
      super(original);
   }

   protected void setMaxStrikes(T t, int i) {
   }

   public EpicFightDamageSource createEpicFightDamageSource() {
      return null;
   }

   public Faction getFaction() {
      return AvalonFactions.EMPTY;
   }

   public void onConstructed(T entityIn) {
      this.original = entityIn;
   }

   public void tick(Pre event) {
      float ownerYRot = ((ProjectileVFXEntity)this.original).getStartYRot();
      ((ProjectileVFXEntity)this.original).setYRot(ownerYRot);
      boolean playAnimation = ((ProjectileVFXEntity)this.getOriginal()).getPlayAnimation();
      if ((!this.isLogicalClient() || ((ProjectileVFXEntity)this.original).getDefaultAnimation() != null || playAnimation) && !playAnimation) {
         ((ProjectileVFXEntity)this.original).setPlayAnimation(true);
         ((ProjectileVFXEntity)this.original).setShouldRender(true);
      }
   }

   public OpenMatrix4f getModelMatrix(float partialTicks) {
      return super.getModelMatrix(partialTicks)
         .scale(
            ((ProjectileVFXEntity)this.original).getScale(), ((ProjectileVFXEntity)this.original).getScale(), ((ProjectileVFXEntity)this.original).getScale()
         );
   }

   public OpenMatrix4f getMatrix(float partialTicks) {
      return super.getMatrix(partialTicks)
         .scale(
            ((ProjectileVFXEntity)this.original).getScale(), ((ProjectileVFXEntity)this.original).getScale(), ((ProjectileVFXEntity)this.original).getScale()
         );
   }

   public Armature getArmature() {
      return ((ProjectileVFXEntity)this.getOriginal()).getArmature();
   }

   public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
      return null;
   }

   @Nullable
   public LivingEntityPatch<?> getOwnerPatch() {
      if (this.ownerPatch != null) {
         return this.ownerPatch;
      } else if (((ProjectileVFXEntity)this.getOriginal()).getOwner() != null) {
         this.ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(
            ((ProjectileVFXEntity)this.getOriginal()).getOwner(), LivingEntityPatch.class
         );
         return this.ownerPatch;
      } else {
         return null;
      }
   }

   public boolean shouldUseOwnerAttack() {
      return true;
   }

   protected void initAnimator(Animator animator) {
      animator.addLivingAnimation(LivingMotions.IDLE, ((ProjectileVFXEntity)this.original).getIdleAnimation());
   }

   public boolean isTargetInvulnerable(Entity entity) {
      if (entity.equals(((ProjectileVFXEntity)this.getOriginal()).getOwner())) {
         return true;
      }

      if (entity instanceof ProjectileVFXEntity projectileVFXEntity) {
         ProjectileVFXEntity artifactSpiritEntity = projectileVFXEntity;
         if (this.getOwnerPatch() != null) {
            return ((LivingEntity)this.getOwnerPatch().getOriginal()).equals(artifactSpiritEntity.getOwner());
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean flashTargetIndicator(LocalPlayerPatch playerPatch) {
      return false;
   }
}
