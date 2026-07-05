package com.hm.efn.entity.effect;

import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import java.util.Objects;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class YamatoDriveFireBall extends LargeFireball {
   private static final int MAX_LIFETIME_TICKS = 100;

   public YamatoDriveFireBall(EntityType<? extends LargeFireball> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public YamatoDriveFireBall(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, int pExplosionPower) {
      super(pLevel, pShooter, new Vec3(pOffsetX, pOffsetY, pOffsetZ), pExplosionPower);
   }

   protected void onHit(@NotNull HitResult pResult) {
      if (!this.level().isClientSide) {
         boolean flag = EventHooks.canEntityGrief(this.level(), this.getOwner());
         this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, flag, ExplosionInteraction.NONE);
         this.handleCustomExplosion();
         this.discard();
      }
   }

   protected void onHitEntity(@NotNull EntityHitResult pResult) {
   }

   private void handleCustomExplosion() {
      Vec3 explosionPos = this.position();
      Level level = this.level();
      LivingEntity owner = this.getOwner() instanceof LivingEntity ? (LivingEntity)this.getOwner() : null;
      if (!level.isClientSide()) {
         ServerLevel serverLevel = (ServerLevel)level;
         float originalRadius = 1.0F;
         float expandedRadius = originalRadius * 3.5F;
         AABB explosionArea = new AABB(
            explosionPos.x - expandedRadius,
            explosionPos.y - expandedRadius,
            explosionPos.z - expandedRadius,
            explosionPos.x + expandedRadius,
            explosionPos.y + expandedRadius,
            explosionPos.z + expandedRadius
         );

         for (Entity entity : level.getEntities(null, explosionArea)) {
            if (entity instanceof LivingEntity target && owner != null && target != owner) {
               this.applyCustomDamage(owner, target);
            }
         }

         this.spawnEnhancedExplosionParticles(serverLevel, explosionPos, expandedRadius);
         Vec3 groundPos = this.getGroundPosition(explosionPos, level);
         this.spawnGroundFireParticles(serverLevel, groundPos);
      }
   }

   private void applyCustomDamage(LivingEntity owner, LivingEntity target) {
      float ownerAttackDamage = (float)Objects.requireNonNull(owner.getAttribute(Attributes.ATTACK_DAMAGE)).getValue();
      float baseDamage = 6.0F;
      float finalDamage = baseDamage + ownerAttackDamage * 0.5F;
      DamageSource damageSource;
      if (owner instanceof Player player) {
         damageSource = target.damageSources().playerAttack(player);
      } else {
         damageSource = target.damageSources().mobAttack(owner);
      }

      if (owner instanceof DoppelgangerEntity doppelgangerEntity) {
         LivingEntity doppelgangerOwner = doppelgangerEntity.getOwner();
         if (target == doppelgangerOwner) {
            return;
         }
      }

      target.hurt(damageSource, finalDamage);
   }

   private void spawnEnhancedExplosionParticles(ServerLevel level, Vec3 center, float radius) {
      level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
      int flameParticles = (int)(20.0F * (radius / 2.0F));
      level.sendParticles(
         ParticleTypes.FLAME, center.x, center.y, center.z, flameParticles, radius * 0.8F, radius * 0.8F, radius * 0.8F, 0.1
      );
      level.sendParticles(ParticleTypes.LAVA, center.x, center.y, center.z, 10, radius * 0.4F, radius * 0.4F, radius * 0.4F, 0.02);
   }

   private void spawnGroundFireParticles(ServerLevel level, Vec3 center) {
      int particles = 20;
      float radius = 2.0F;

      for (int i = 0; i < particles; i++) {
         float angle = (float)((Math.PI * 2) * i / particles);
         float x = radius * Mth.cos(angle);
         float z = radius * Mth.sin(angle);
         Vec3 particlePos = center.add(x, 0.1, z);
         if (i % 3 == 0) {
            level.sendParticles(ParticleTypes.LAVA, particlePos.x, particlePos.y + 0.2, particlePos.z, 1, 0.15, 0.1, 0.15, 0.01);
         }

         if (i % 5 == 0) {
            level.sendParticles(ParticleTypes.LAVA, particlePos.x, particlePos.y, particlePos.z, 1, 0.05, 0.02, 0.05, 0.0);
         }
      }

      level.sendParticles(ParticleTypes.FLAME, center.x, center.y + 0.3, center.z, 10, 0.3, 0.2, 0.3, 0.05);
   }

   private Vec3 getGroundPosition(Vec3 startPos, Level level) {
      double dpx = startPos.x;
      double dpy = startPos.y;
      double dpz = startPos.z;
      MutableBlockPos pos = new MutableBlockPos(dpx, dpy, dpz);

      while (true) {
         BlockState block = level.getBlockState(pos);
         if (!(block.getBlock() instanceof BushBlock) && !block.isAir() || block.is(Blocks.VOID_AIR) || dpy <= -64.0) {
            return new Vec3(dpx, dpy + 1.0, dpz);
         }

         pos.setY(pos.getY() - 1);
         dpy--;
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         Entity owner = this.getOwner();
         if (this.tickCount >= MAX_LIFETIME_TICKS || !(owner instanceof LivingEntity livingOwner) || !livingOwner.isAlive()) {
            this.discard();
            return;
         }
      }

      if (this.level().isClientSide) {
         for (int i = 0; i < 2; i++) {
            this.level()
               .addParticle(
                  ParticleTypes.FLAME,
                  this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                  this.getY() + (this.random.nextDouble() - 0.5) * 0.5,
                  this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                  0.0,
                  0.0,
                  0.0
               );
         }
      }
   }
}
