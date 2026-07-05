package com.hm.efn.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MovingSound extends AbstractTickableSoundInstance {
   private final Entity entity;
   private boolean entityRemoved = false;
   private double lastX;
   private double lastY;
   private double lastZ;

   public MovingSound(SoundEvent soundEvent, Entity entity) {
      this(soundEvent, entity, 1.0F, 1.0F);
   }

   public MovingSound(SoundEvent soundEvent, Entity entity, float volume, float pitch) {
      super(soundEvent, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
      this.entity = entity;
      this.volume = volume;
      this.pitch = pitch;
      this.x = entity.getX();
      this.y = entity.getY();
      this.z = entity.getZ();
      this.lastX = this.x;
      this.lastY = this.y;
      this.lastZ = this.z;
      this.looping = false;
      this.attenuation = Attenuation.LINEAR;
      this.relative = false;
      this.delay = 0;
   }

   public void tick() {
      if (this.entity != null && !this.entity.isRemoved()) {
         this.x = this.entity.getX();
         this.y = this.entity.getY();
         this.z = this.entity.getZ();
         this.lastX = this.x;
         this.lastY = this.y;
         this.lastZ = this.z;
         Vec3 delta = this.entity.getDeltaMovement();
         double speed = delta.length();
         if (speed > 0.1) {
            this.pitch = 1.0F + (float)(speed * 0.3F);
         }
      } else if (!this.entityRemoved) {
         this.entityRemoved = true;
         this.x = this.lastX;
         this.y = this.lastY;
         this.z = this.lastZ;
      }
   }

   public boolean canPlaySound() {
      return true;
   }

   public boolean isStopped() {
      return false;
   }
}
