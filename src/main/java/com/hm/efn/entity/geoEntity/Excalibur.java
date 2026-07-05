package com.hm.efn.entity.geoEntity;

import com.hm.efn.entity.GeoVFXEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;

public class Excalibur extends GeoVFXEntity {
   private static final int MAX_LIFETIME = 60;

   public static Excalibur createEntity(EntityType<? extends GeoVFXEntity> type, Level level) {
      return new Excalibur(type, level);
   }

   public Excalibur(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   public Excalibur(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z) {
      super(type, level, x, y, z);
   }

   public Excalibur(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot) {
      super(type, level, x, y, z, yRot);
   }

   public Excalibur(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot, LivingEntity owner) {
      super(type, level, x, y, z, yRot, owner);
   }

   @Override
   protected void performAttack() {
   }

   @Override
   protected boolean shouldDiscard() {
      return this.getLifetime() >= 60;
   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return super.getAnimatableInstanceCache();
   }
}
