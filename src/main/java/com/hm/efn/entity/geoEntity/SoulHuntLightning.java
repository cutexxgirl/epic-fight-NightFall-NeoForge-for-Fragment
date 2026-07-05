package com.hm.efn.entity.geoEntity;

import com.hm.efn.entity.GeoVFXEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class SoulHuntLightning extends GeoVFXEntity {
   private static final int MAX_LIFETIME = 13;

   public static SoulHuntLightning createEntity(EntityType<? extends GeoVFXEntity> type, Level level) {
      return new SoulHuntLightning(type, level);
   }

   public SoulHuntLightning(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   public SoulHuntLightning(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z) {
      super(type, level, x, y, z);
   }

   public SoulHuntLightning(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot) {
      super(type, level, x, y, z, yRot);
   }

   public SoulHuntLightning(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot, LivingEntity owner) {
      super(type, level, x, y, z, yRot, owner);
   }

   @Override
   protected void initializeDefaults() {
      super.initializeDefaults();
      this.setAnimationSpeed(3.0F);
   }

   @Override
   protected void performAttack() {
   }

   @Override
   protected boolean shouldDiscard() {
      return this.getLifetime() >= 13;
   }
}
