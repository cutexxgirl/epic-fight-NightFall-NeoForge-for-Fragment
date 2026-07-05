package com.hm.efn.entity.geoEntity;

import com.hm.efn.entity.GeoVFXEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.DodgeLocationIndicator;

public class JudgementCutPerfect extends GeoVFXEntity {
   private int lastAttackTick = 0;
   private int totalAttacks = 0;
   private static final int MAX_LIFETIME = 20;

   public static JudgementCutPerfect createEntity(EntityType<? extends GeoVFXEntity> type, Level level) {
      return new JudgementCutPerfect(type, level);
   }

   public JudgementCutPerfect(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   public JudgementCutPerfect(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z) {
      super(type, level, x, y, z);
   }

   public JudgementCutPerfect(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot) {
      super(type, level, x, y, z, yRot);
   }

   public JudgementCutPerfect(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot, LivingEntity owner) {
      super(type, level, x, y, z, yRot, owner);
   }

   @Override
   protected void initializeDefaults() {
      super.initializeDefaults();
      this.setAttackInterval(5);
      this.setAttackCount(3);
   }

   @Override
   protected void performAttack() {
      if (!this.level().isClientSide) {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            ServerLevel level = (ServerLevel)this.level();

            for (LivingEntity target : level.getEntitiesOfClass(
               LivingEntity.class,
               this.getBoundingBox().inflate(0.1),
               entity -> !(entity instanceof DodgeLocationIndicator)
                  && !(entity instanceof GeoVFXEntity)
                  && entity.isAlive()
                  && entity != owner
                  && entity != this
                  && this.getBoundingBox().intersects(entity.getBoundingBox())
            )) {
               if (target.invulnerableTime >= 0) {
                  target.invulnerableTime = 0;
                  DamageSource damageSource = EpicFightDamageSources.mobAttack(owner)
                     .setAnimation(Animations.EMPTY_ANIMATION)
                     .setInitialPosition(this.getOwner().position())
                     .setStunType(StunType.SHORT)
                     .setBaseArmorNegation(100.0F)
                     .setBaseImpact(this.getAttackDamage() / 5.0F);
                  level.playSound(
                     null, this.getX(), this.getY(), this.getZ(), (SoundEvent)EpicFightSounds.WHOOSH_ROD.get(), this.getSoundSource(), 1.0F, 1.0F
                  );
                  target.hurt(damageSource, this.getAttackDamage());
                  target.invulnerableTime = 0;
               }
            }
         }
      }
   }

   @Override
   protected boolean shouldDiscard() {
      return this.getLifetime() >= 20;
   }

   @Override
   protected boolean shouldPerformAttack() {
      return this.totalAttacks < this.getAttackCount() && this.tickCount - this.lastAttackTick >= this.getAttackInterval();
   }

   @Override
   protected void onVFXUpdate() {
      if (this.shouldPerformAttack()) {
         this.performAttack();
         this.lastAttackTick = this.tickCount;
         this.totalAttacks++;
      }
   }

   @Override
   public void tick() {
      super.tick();
   }
}
