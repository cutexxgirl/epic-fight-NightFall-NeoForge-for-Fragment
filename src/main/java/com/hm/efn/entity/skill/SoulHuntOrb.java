package com.hm.efn.entity.skill;

import com.hm.efn.entity.EFNEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.hm.efn.compat.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class SoulHuntOrb extends Entity {
   private Player dest;
   private Vec3 randVec;
   private Vec3 spiralOffset;
   private double spiralAngle;
   private double spiralRadius;
   private double spiralSpeed;
   private Vec3[] followerOffsets;
   private double[] followerAngles;
   private int[] followerDelays;
   private boolean soundPlayed = false;

   public SoulHuntOrb(EntityType<?> type, Level level) {
      super(type, level);
      this.initFollowerParticles();
   }

   public SoulHuntOrb(Player dest, double x, double y, double z) {
      this((EntityType<?>)EFNEntity.SOUL_HUNT_ORB.get(), dest.level());
      this.setPos(x, y, z);
      this.dest = dest;
      Vec3 toPlayer = this.dest.position().add(0.0, this.dest.getBbHeight() * 0.5, 0.0).subtract(this.position());
      Vec3 perpendicular = toPlayer.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
      if (perpendicular.lengthSqr() < 0.1) {
         perpendicular = toPlayer.cross(new Vec3(1.0, 0.0, 0.0)).normalize();
      }

      double randomAngle = this.random.nextDouble() * Math.PI * 2.0;
      double randomDistance = this.random.nextDouble() * 2.0 + 1.0;
      this.randVec = perpendicular.scale(Math.cos(randomAngle) * randomDistance)
         .add(new Vec3(0.0, Math.sin(randomAngle) * randomDistance, 0.0))
         .normalize();
      this.spiralOffset = Vec3.ZERO;
      this.spiralAngle = this.random.nextDouble() * Math.PI * 2.0;
      this.spiralRadius = this.random.nextDouble() * 0.8 + 0.3;
      this.spiralSpeed = (this.random.nextDouble() * 0.3 + 0.2) * (this.random.nextBoolean() ? 1 : -1);
      this.initFollowerParticles();
   }

   private void initFollowerParticles() {
      this.followerOffsets = new Vec3[2];
      this.followerAngles = new double[2];
      this.followerDelays = new int[2];

      for (int i = 0; i < 2; i++) {
         this.followerOffsets[i] = new Vec3(
            (this.random.nextDouble() - 0.5) * 2.0, (this.random.nextDouble() - 0.5) * 2.0, (this.random.nextDouble() - 0.5) * 2.0
         );
         this.followerAngles[i] = this.random.nextDouble() * Math.PI * 2.0;
         this.followerDelays[i] = this.random.nextInt(10) + 5;
      }
   }

   public void tick() {
      this.noPhysics = true;
      if (!this.level().isClientSide()) {
         if (this.dest == null || !this.dest.isAlive()) {
            this.discard();
            return;
         }

         Vec3 destCenter = this.dest.position().add(0.0, this.dest.getBbHeight() * 0.5, 0.0);
         double progress = Math.min(1.0, this.tickCount / 30.0);
         double easeProgress = 1.0 - Math.pow(1.0 - progress, 2.0);
         Vec3 toPlayer = destCenter.subtract(this.position());
         double distanceToPlayer = toPlayer.length();
         if (distanceToPlayer < 0.75) {
            if (!this.soundPlayed) {
               this.level()
                  .playSound(null, this.dest.getX(), this.dest.getY(), this.dest.getZ(), SoundEvents.WARDEN_NEARBY_CLOSE, SoundSource.AMBIENT, 1.6F, 1.0F);
               this.soundPlayed = true;
            }

            this.discard();
            return;
         }

         if (this.tickCount > 60) {
            this.discard();
            return;
         }

         Vec3 directMove = toPlayer.normalize().scale(easeProgress * 0.35);
         this.spiralAngle = this.spiralAngle + this.spiralSpeed;
         double spiralX = Math.cos(this.spiralAngle) * this.spiralRadius * (1.0 - easeProgress);
         double spiralZ = Math.sin(this.spiralAngle) * this.spiralRadius * (1.0 - easeProgress);
         this.spiralOffset = new Vec3(spiralX, Math.sin(this.spiralAngle * 0.7) * this.spiralRadius * 0.5 * (1.0 - easeProgress), spiralZ);
         double randomStrength = (1.0 - easeProgress) * 0.15;
         Vec3 randomOffset = new Vec3(
            (this.random.nextDouble() - 0.5) * randomStrength,
            (this.random.nextDouble() - 0.5) * randomStrength,
            (this.random.nextDouble() - 0.5) * randomStrength
         );
         Vec3 finalMove = directMove.add(this.spiralOffset).add(randomOffset);
         this.move(MoverType.SELF, finalMove);
      } else {
         this.renderParticles();
      }
   }

   private void renderParticles() {
      Vec3 currentPos = this.position();
      this.level()
         .addParticle(
            ParticleTypes.SCULK_SOUL,
            currentPos.x,
            currentPos.y,
            currentPos.z,
            (this.random.nextDouble() - 0.5) * 0.02,
            (this.random.nextDouble() - 0.5) * 0.02,
            (this.random.nextDouble() - 0.5) * 0.02
         );

      for (int i = 0; i < 2; i++) {
         if (this.tickCount > this.followerDelays[i]) {
            this.followerAngles[i] = this.followerAngles[i] + (this.random.nextDouble() * 0.3 + 0.1);
            double orbitRadius = 0.5 + this.random.nextDouble() * 0.3;
            Vec3 followerPos = currentPos.add(
               Math.cos(this.followerAngles[i]) * orbitRadius,
               Math.sin(this.followerAngles[i] * 1.5) * orbitRadius * 0.7,
               Math.sin(this.followerAngles[i]) * orbitRadius
            );
            followerPos = followerPos.add(
               (this.random.nextDouble() - 0.5) * 0.2, (this.random.nextDouble() - 0.5) * 0.2, (this.random.nextDouble() - 0.5) * 0.2
            );
            this.level()
               .addParticle(
                  ParticleTypes.SOUL_FIRE_FLAME,
                  followerPos.x,
                  followerPos.y,
                  followerPos.z,
                  (this.random.nextDouble() - 0.5) * 0.05,
                  (this.random.nextDouble() - 0.5) * 0.05,
                  (this.random.nextDouble() - 0.5) * 0.05
               );
            if (this.random.nextInt(8) == 0) {
               this.level()
                  .addParticle(
                     ParticleTypes.SMOKE,
                     followerPos.x,
                     followerPos.y,
                     followerPos.z,
                     (this.random.nextDouble() - 0.5) * 0.1,
                     (this.random.nextDouble() - 0.5) * 0.1,
                     (this.random.nextDouble() - 0.5) * 0.1
                  );
            }
         }
      }

      if (this.tickCount % 4 == 0) {
         this.level().addParticle(ParticleTypes.SOUL, currentPos.x, currentPos.y, currentPos.z, 0.0, 0.0, 0.0);
      }
   }

   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
   }

   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
