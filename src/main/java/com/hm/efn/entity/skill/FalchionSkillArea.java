package com.hm.efn.entity.skill;

import com.hm.efn.gameasset.combos.CrescentMoon;
import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.hm.efn.compat.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class FalchionSkillArea extends Entity {
   private int lifeTime = 600;
   private float maxRadius = 15.0F;
   private final int particleCount = 72;
   private int expandDuration = 40;
   private float currentRadius = 0.0F;
   private boolean isExpanding = true;
   private final int[] particleExpansionProgress;

   public FalchionSkillArea(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
      this.noCulling = true;
      this.particleExpansionProgress = new int[72];

      for (int i = 0; i < 72; i++) {
         this.particleExpansionProgress[i] = -i * 2;
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.tickCount >= this.lifeTime) {
            this.discard();
            return;
         }

         this.applyEffectToPlayersInRadius();
         if (this.tickCount % 10 == 0) {
            this.level()
               .playSound(
                  null,
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  SoundEvents.BEACON_AMBIENT,
                  SoundSource.AMBIENT,
                  1.5F,
                  0.8F + this.random.nextFloat() * 0.4F
               );
         }
      } else {
         this.updateExpansion();
         this.renderParticles();
      }
   }

   private void applyEffectToPlayersInRadius() {
      Vec3 center = this.position();
      this.level()
         .getEntitiesOfClass(
            ServerPlayer.class,
            new AABB(
               center.x - this.maxRadius,
               center.y - 3.0,
               center.z - this.maxRadius,
               center.x + this.maxRadius,
               center.y + 3.0,
               center.z + this.maxRadius
            ),
            player -> {
               double distanceSq = player.distanceToSqr(center.x, center.y, center.z);
               return distanceSq <= this.maxRadius * this.maxRadius;
            }
         )
         .forEach(player -> {
            this.applyEffect(player);
            MobEffectInstance falchionBless = player.getEffect(EFNMobEffectRegistry.FALCHION_BLESS);
            if (this.tickCount % 5 == 0 && falchionBless != null) {
               this.addPlayerFeedbackParticles(player);
            }
         });
   }

   private void applyEffect(ServerPlayer serverPlayer) {
      ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
      if (serverPlayerPatch != null) {
         SkillContainer skillContainer = serverPlayerPatch.getSkill(CrescentMoon.crescentmoon);
         if (skillContainer != null && skillContainer.hasSkill()) {
            serverPlayer.addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 5, 0, false, false, false));
            serverPlayer.addEffect(new MobEffectInstance(EFNMobEffectRegistry.FALCHION_BLESS, 5, 0, false, false, false));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, 0, false, false, false));
         }
      }
   }

   private void addPlayerFeedbackParticles(Player player) {
      if (this.level().isClientSide()) {
         Vec3 playerPos = player.position();

         for (int i = 0; i < 3; i++) {
            float angle = this.random.nextFloat() * (float) Math.PI * 2.0F;
            double radius = 0.5 + this.random.nextFloat() * 0.3;
            double x = playerPos.x + radius * Math.cos(angle);
            double z = playerPos.z + radius * Math.sin(angle);
            double y = playerPos.y + 0.1 + this.random.nextFloat() * player.getBbHeight();
            this.level().addParticle(ParticleTypes.SCULK_SOUL, x, y, z, 0.0, 0.02F, 0.0);
         }

         if (this.random.nextInt(3) == 0) {
            this.level()
               .addParticle(ParticleTypes.SOUL_FIRE_FLAME, playerPos.x, playerPos.y + player.getBbHeight() * 0.5, playerPos.z, 0.0, 0.05F, 0.0);
         }
      }
   }

   private void updateExpansion() {
      if (this.isExpanding) {
         float expandProgress = Math.min(1.0F, (float)this.tickCount / this.expandDuration);
         float easedProgress = this.easeOutCubic(expandProgress);
         this.currentRadius = this.maxRadius * easedProgress;

         for (int i = 0; i < 72; i++) {
            if (this.particleExpansionProgress[i] < this.tickCount) {
               this.particleExpansionProgress[i] = this.tickCount;
            }
         }

         if (this.tickCount >= this.expandDuration) {
            this.isExpanding = false;
            this.currentRadius = this.maxRadius;
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.AMBIENT, 1.0F, 1.0F);
         }
      }
   }

   private float easeOutCubic(float x) {
      return 1.0F - (float)Math.pow(1.0F - x, 3.0);
   }

   private float easeInOutCubic(float x) {
      return x < 0.5 ? 4.0F * x * x * x : 1.0F - (float)Math.pow(-2.0F * x + 2.0F, 3.0) / 2.0F;
   }

   private void renderParticles() {
      Vec3 center = this.position();
      this.createExpandingRingParticles(center);
      if (!this.isExpanding) {
         this.createInnerParticles(center);
         this.createRisingParticles(center);
         this.createRuneParticles(center);
      }
   }

   private void createExpandingRingParticles(Vec3 center) {
      for (int i = 0; i < 72; i++) {
         float angle = (float)((Math.PI * 2) * i / 72.0);
         float particleProgress = 0.0F;
         if (this.particleExpansionProgress[i] >= 0) {
            int progressTicks = this.tickCount - this.particleExpansionProgress[i];
            particleProgress = Math.min(1.0F, progressTicks / 10.0F);
         }

         float easedProgress = this.easeOutCubic(particleProgress);
         float particleRadius = this.currentRadius * easedProgress;
         if (!(particleProgress <= 0.0F)) {
            float pulse = 1.0F;
            if (particleProgress >= 1.0F) {
               pulse = 0.95F + 0.05F * (float)Math.sin((this.tickCount + i * 3) * 0.1F);
               particleRadius *= pulse;
            }

            double x = center.x + particleRadius * Math.cos(angle);
            double z = center.z + particleRadius * Math.sin(angle);
            double y = center.y + 0.1;
            float sizeMultiplier = 0.5F + easedProgress * 0.5F;
            this.level().addParticle(ParticleTypes.SCULK_SOUL, x, y, z, 0.0, 0.02F * sizeMultiplier, 0.0);
            if (i % 3 == 0 && particleProgress >= 1.0F) {
               this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y + 0.2F, z, 0.0, 0.03F, 0.0);
            }

            if (i % 6 == 0 && particleProgress >= 1.0F) {
               this.level()
                  .addParticle(
                     ParticleTypes.SOUL, x, y + 0.1F, z, (this.random.nextFloat() - 0.5F) * 0.05F, 0.08F, (this.random.nextFloat() - 0.5F) * 0.05F
                  );
            }

            if (particleProgress < 1.0F) {
               float expansionSpeed = 0.2F;
               double expansionX = center.x + (particleRadius + expansionSpeed) * Math.cos(angle);
               double expansionZ = center.z + (particleRadius + expansionSpeed) * Math.sin(angle);
               this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, expansionX, y + 0.1F, expansionZ, 0.0, 0.05F, 0.0);
               if (i % 9 == 0) {
                  for (int j = 0; j < 3; j++) {
                     double waveRadius = particleRadius + j * 0.5F;
                     double waveX = center.x + waveRadius * Math.cos(angle);
                     double waveZ = center.z + waveRadius * Math.sin(angle);
                     this.level().addParticle(ParticleTypes.SCULK_SOUL, waveX, y + 0.05F * j, waveZ, 0.0, 0.01F, 0.0);
                  }
               }
            }
         }
      }

      if (this.isExpanding && this.tickCount % 2 == 0) {
         this.createExpansionWave(center);
      }
   }

   private void createExpansionWave(Vec3 center) {
      float waveProgress = Math.min(1.0F, (float)this.tickCount / this.expandDuration);
      float waveRadius = this.maxRadius * waveProgress;

      for (int wave = 0; wave < 3; wave++) {
         float currentWaveRadius = waveRadius - wave * 0.5F;
         if (!(currentWaveRadius <= 0.0F)) {
            int waveParticles = 24;

            for (int i = 0; i < waveParticles; i++) {
               float angle = (float)((Math.PI * 2) * i / waveParticles);
               double x = center.x + currentWaveRadius * Math.cos(angle);
               double z = center.z + currentWaveRadius * Math.sin(angle);
               double y = center.y + 0.1 + wave * 0.1F;
               SimpleParticleType particleType = wave == 0 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SCULK_SOUL;
               this.level().addParticle(particleType, x, y, z, 0.0, 0.02F, 0.0);
            }
         }
      }
   }

   private void createRuneParticles(Vec3 center) {
      int runeCount = 10;
      float runeRadius = this.currentRadius * 0.8F;
      float rotationAngle = this.tickCount % 100 * 0.0628F;

      for (int i = 0; i < runeCount; i++) {
         float baseAngle = (float)((Math.PI * 2) * i / runeCount);
         float angle = baseAngle + rotationAngle;
         double x = center.x + runeRadius * Math.cos(angle);
         double z = center.z + runeRadius * Math.sin(angle);
         double y = center.y + 0.5;
         this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.05F, 0.0);

         for (int j = 0; j < 3; j++) {
            float subAngle = baseAngle + (float)((Math.PI * 2) * j / 3.0);
            double subRadius = 0.5;
            float subRotation = this.tickCount % 60 * 0.1047F;
            double subX = x + subRadius * Math.cos(subAngle + subRotation);
            double subZ = z + subRadius * Math.sin(subAngle + subRotation);
            this.level().addParticle(ParticleTypes.SCULK_SOUL, subX, y + 0.1F, subZ, 0.0, 0.02F, 0.0);
         }
      }
   }

   private void createInnerParticles(Vec3 center) {
      int innerParticleCount = 15;

      for (int i = 0; i < innerParticleCount; i++) {
         float randomAngle = this.random.nextFloat() * (float) Math.PI * 2.0F;
         float randomRadius = this.random.nextFloat() * this.currentRadius * 0.7F;
         double x = center.x + randomRadius * Math.cos(randomAngle);
         double z = center.z + randomRadius * Math.sin(randomAngle);
         double y = center.y + this.random.nextFloat() * 0.8F;
         if (this.random.nextFloat() < 0.6F) {
            this.level()
               .addParticle(ParticleTypes.SCULK_SOUL, x, y, z, (this.random.nextFloat() - 0.5F) * 0.1F, 0.05F, (this.random.nextFloat() - 0.5F) * 0.1F);
         } else {
            this.level()
               .addParticle(ParticleTypes.SOUL, x, y, z, (this.random.nextFloat() - 0.5F) * 0.08F, 0.08F, (this.random.nextFloat() - 0.5F) * 0.08F);
         }
      }
   }

   private void createRisingParticles(Vec3 center) {
      int risingParticleCount = 10;

      for (int i = 0; i < risingParticleCount; i++) {
         float randomAngle = this.random.nextFloat() * (float) Math.PI * 2.0F;
         float randomRadius = this.random.nextFloat() * this.currentRadius;
         double startX = center.x + randomRadius * Math.cos(randomAngle);
         double startZ = center.z + randomRadius * Math.sin(randomAngle);
         double startY = center.y;
         double targetY = center.y + 3.0 + this.random.nextFloat() * 0.8F;

         for (int j = 0; j < 3; j++) {
            double progress = j / 3.0;
            double currentY = startY + (targetY - startY) * progress;
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, startX, currentY, startZ, 0.0, 0.1F, 0.0);
         }
      }
   }

   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      if (tag.contains("LifeTime")) {
         this.lifeTime = tag.getInt("LifeTime");
      }

      if (tag.contains("MaxRadius")) {
         this.maxRadius = tag.getFloat("MaxRadius");
      }

      if (tag.contains("ExpandDuration")) {
         this.expandDuration = tag.getInt("ExpandDuration");
      }

      if (tag.contains("CurrentRadius")) {
         this.currentRadius = tag.getFloat("CurrentRadius");
      }

      if (tag.contains("IsExpanding")) {
         this.isExpanding = tag.getBoolean("IsExpanding");
      }
   }

   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      tag.putInt("LifeTime", this.lifeTime);
      tag.putFloat("MaxRadius", this.maxRadius);
      tag.putInt("ExpandDuration", this.expandDuration);
      tag.putFloat("CurrentRadius", this.currentRadius);
      tag.putBoolean("IsExpanding", this.isExpanding);
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public int getLifeTime() {
      return this.lifeTime;
   }

   public void setLifeTime(int lifeTime) {
      this.lifeTime = lifeTime;
   }

   public float getMaxRadius() {
      return this.maxRadius;
   }

   public void setMaxRadius(float maxRadius) {
      this.maxRadius = maxRadius;
   }

   public int getExpandDuration() {
      return this.expandDuration;
   }

   public void setExpandDuration(int expandDuration) {
      this.expandDuration = expandDuration;
   }

   public boolean isExpanding() {
      return this.isExpanding;
   }

   public float getCurrentRadius() {
      return this.currentRadius;
   }
}
