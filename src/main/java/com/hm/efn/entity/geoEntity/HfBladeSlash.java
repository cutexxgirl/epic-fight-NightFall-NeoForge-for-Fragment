package com.hm.efn.entity.geoEntity;

import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.Spark2Effek;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.client.sound.MovingSound;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.GeoVFXEntity;
import com.hm.efn.util.EffekUnits;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.DodgeLocationIndicator;

public class HfBladeSlash extends GeoVFXEntity {
   private int lastAttackTick = 0;
   private int totalAttacks = 0;
   private static final int MAX_LIFETIME = 27;

   public static HfBladeSlash createEntity(EntityType<? extends GeoVFXEntity> type, Level level) {
      return new HfBladeSlash(type, level);
   }

   public HfBladeSlash(EntityType<? extends GeoVFXEntity> type, Level level) {
      super(type, level);
   }

   public HfBladeSlash(EntityType<? extends GeoVFXEntity> type, Level level, double x, double y, double z) {
      super(type, level, x, y, z);
   }

   public HfBladeSlash(EntityType<? extends GeoVFXEntity> type, Level level, double x, double y, double z, float yRot) {
      super(type, level, x, y, z, yRot);
   }

   public HfBladeSlash(EntityType<? extends GeoVFXEntity> type, Level level, double x, double y, double z, float yRot, LivingEntity owner) {
      super(type, level, x, y, z, yRot, owner);
   }

   @Override
   protected void initializeDefaults() {
      super.initializeDefaults();
      this.setAttackInterval(3);
      this.setAttackCount(2);
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
                  float maxHealthPercentDamage = target.getMaxHealth() * 0.01F;
                  float finalDamage = this.getAttackDamage() + maxHealthPercentDamage;
                  DamageSource damageSource = EpicFightDamageSources.mobAttack(owner)
                     .setAnimation(null)
                     .setInitialPosition(this.getOwner().position())
                     .setStunType(StunType.HOLD)
                     .setBaseImpact(0.0F)
                     .addExtraDamage(ExtraDamageInstance.EVISCERATE_LOST_HEALTH.create(new float[]{0.005F}))
                     .addExtraDamage(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0]))
                     .addRuntimeTag(DamageTypeTags.BYPASSES_ARMOR)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_COOLDOWN)
                     .addRuntimeTag(DamageTypeTags.BYPASSES_SHIELD)
                     .addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
                  level.playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)EpicFightSounds.WHOOSH.get(), this.getSoundSource(), 1.0F, 1.0F);
                  if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
                     return;
                  }

                  target.hurt(damageSource, finalDamage);
                  target.invulnerableTime = 0;
               }
            }
         }
      }
   }

   @Override
   protected boolean shouldDiscard() {
      return this.getLifetime() >= 27;
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

      if (this.tickCount == 2 && this.level().isClientSide) {
         this.playMovingSound(0.7F, 1.0F);
         if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()) {
            Spark2Effek.playSpark2(Spark2Effek.Type.LEVEL1, this.level(), 0.0, 1.5, 0.0, 1.0F, this);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void playMovingSound(float volume, float pitch) {
      Minecraft.getInstance().getSoundManager().play(new MovingSound((SoundEvent)EFNSounds.ELECTRIC.get(), this, volume, pitch));
   }

   public static HfBladeSlash create(
      Level level, LivingEntity owner, Vec3 position, float scale, float xRotOffset, float yRotOffset, float zRotOffset, float startYRot
   ) {
      HfBladeSlash slash = new HfBladeSlash(
         (EntityType<? extends GeoVFXEntity>)EFNEntity.HF_BLADE_SLASH.get(), level, position.x, position.y, position.z, startYRot, owner
      );
      slash.setScale(scale);
      slash.setXRotOffset(xRotOffset);
      slash.setYRotOffset(yRotOffset);
      slash.setZRotOffset(zRotOffset);
      slash.setStartYRot(startYRot);
      return slash;
   }

   public static HfBladeSlash create(Level level, LivingEntity owner, Vec3 position) {
      float startYRot = owner != null ? owner.getYRot() : 0.0F;
      return create(level, owner, position, 1.0F, 0.0F, 0.0F, 0.0F, startYRot);
   }
}
