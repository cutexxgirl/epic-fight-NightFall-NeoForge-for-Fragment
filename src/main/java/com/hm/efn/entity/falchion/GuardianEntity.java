package com.hm.efn.entity.falchion;

import com.hm.efn.entity.EFNEntity;
import com.hm.efn.registries.EFNItem;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.registry.entries.EpicFightAttributes;
import yesman.epicfight.world.item.WeaponItem;

public class GuardianEntity extends TamableAnimal {
   @Nullable
   private PlayerPatch<?> ownerPatch;

   public GuardianEntity(ServerPlayer owner) {
      super((EntityType)EFNEntity.GUARDIAN.get(), owner.level());
      this.tame(owner);
      this.setPersistenceRequired();
   }

   public GuardianEntity(EntityType<GuardianEntity> guardianEntityEntityType, Level level) {
      super(guardianEntityEntityType, level);
   }

   public boolean isFood(@NotNull ItemStack stack) {
      return false;
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Animal.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 9.99999999E8)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(EpicFightAttributes.MAX_STRIKES, 6.0)
         .build();
   }

   public void tame(@NotNull Player player) {
      super.tame(player);
      this.setItemSlot(EquipmentSlot.MAINHAND, ((WeaponItem)EFNItem.FLAG_BEARER.get()).getDefaultInstance());
   }

   @Nullable
   public PlayerPatch<?> getOwnerPatch() {
      if (this.ownerPatch != null) {
         return this.ownerPatch;
      } else if (this.getOwner() != null) {
         this.ownerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(this.getOwner(), PlayerPatch.class);
         return this.ownerPatch;
      } else {
         return null;
      }
   }

   public void tick() {
      super.tick();
      this.setNoAi(true);
      int lifeTime = 600;
      if (this.tickCount == 2 && this.getOwner() != null) {
         ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(this.getOwner(), ServerPlayerPatch.class);
         if (serverPlayerPatch != null) {
            spawnParticles(((ServerPlayer)serverPlayerPatch.getOriginal()).serverLevel(), this.position());
         }
      }

      if (this.tickCount >= lifeTime && this.getOwner() != null) {
         ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(this.getOwner(), ServerPlayerPatch.class);
         if (serverPlayerPatch != null) {
            spawnParticles(((ServerPlayer)serverPlayerPatch.getOriginal()).serverLevel(), this.position());
            this.remove(RemovalReason.DISCARDED);
         }

         this.remove(RemovalReason.DISCARDED);
      }

      if (this.getOwner() == null || !this.getOwner().isAlive() || this.getOwner().getHealth() <= 0.0F) {
         ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(this.getOwner(), ServerPlayerPatch.class);
         if (serverPlayerPatch != null) {
            spawnParticles(((ServerPlayer)serverPlayerPatch.getOriginal()).serverLevel(), this.position());
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }

   public static void spawnParticles(ServerLevel level, Vec3 position) {
      ParticleOptions particle = ParticleTypes.GLOW;
      int count = 50;
      double speed = 0.1;
      double spread = 0.5;
      level.sendParticles(particle, position.x, position.y + 1.0, position.z, count, spread, spread, spread, speed);
      level.sendParticles(ParticleTypes.SCULK_SOUL, position.x, position.y + 1.0, position.z, 20, 0.3, 0.3, 0.3, 0.2);
      spawnCircularParticles(level, position);
   }

   public static void spawnCircularParticles(ServerLevel level, Vec3 position) {
      ParticleOptions particle = ParticleTypes.SOUL_FIRE_FLAME;
      int circleParticles = 20;
      double radius = 1.5;
      double height = 2.0;

      for (int i = 0; i < circleParticles; i++) {
         double angle = (Math.PI * 2) * i / circleParticles;
         double x = position.x + radius * Math.cos(angle);
         double z = position.z + radius * Math.sin(angle);
         double y = position.y + height;
         level.sendParticles(particle, x, y, z, 1, 0.1, 0.1, 0.1, 0.05);
      }
   }

   public void push(@NotNull Entity pEntity) {
   }

   protected void pushEntities() {
   }

   protected void doPush(@NotNull Entity entity) {
   }

   public boolean isPushable() {
      return false;
   }

   public boolean shouldBeSaved() {
      return true;
   }

   public boolean hurt(@NotNull DamageSource source, float p_27568_) {
      return false;
   }

   public boolean canBeSeenAsEnemy() {
      return false;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
      return null;
   }
}
