package com.hm.efn.entity.effect;

import com.hm.efn.client.effek.ASEffek;
import com.hm.efn.client.effek.DEFEffek;
import com.hm.efn.client.effek.DMGEffek;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffekUnits;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class TriggerEntity extends Entity {
   private static final EntityDataAccessor<Optional<UUID>> DATA_PARENT_UUID = SynchedEntityData.defineId(TriggerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<String> DATA_EFFECT_ID = SynchedEntityData.defineId(TriggerEntity.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Vector3f> DATA_OFFSET = SynchedEntityData.defineId(TriggerEntity.class, EntityDataSerializers.VECTOR3);
   @Nullable
   private LivingEntity parentEntity;
   @Nullable
   private Holder<MobEffect> requiredEffect;
   private Vec3 offset = Vec3.ZERO;
   private float value = 1.0F;

   public TriggerEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noCulling = true;
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public void setParentAndEffect(LivingEntity parent, Holder<MobEffect> effect, Vec3 offset) {
      this.parentEntity = parent;
      this.requiredEffect = effect;
      this.offset = offset;
      if (!this.level().isClientSide()) {
         this.entityData.set(DATA_PARENT_UUID, Optional.of(parent.getUUID()));
         if (effect != null) {
            ResourceLocation effectKey = effect.unwrapKey().map(key -> key.location()).orElse(BuiltInRegistries.MOB_EFFECT.getKey(effect.value()));
            this.entityData.set(DATA_EFFECT_ID, effectKey != null ? effectKey.toString() : "");
         } else {
            this.entityData.set(DATA_EFFECT_ID, "");
         }

         this.entityData.set(DATA_OFFSET, new Vector3f((float)offset.x, (float)offset.y, (float)offset.z));
      }
   }

   public void push(@NotNull Entity pEntity) {
   }

   public boolean isPushable() {
      return false;
   }

   public static TriggerEntity createAndSpawn(LivingEntity parent, Holder<MobEffect> effect, Vec3 offset) {
      if (parent.level().isClientSide()) {
         return null;
      }

      TriggerEntity trigger = new TriggerEntity((EntityType<?>)EFNEntity.TRIGGER.get(), parent.level());
      Vec3 spawnPos = parent.position().add(offset);
      trigger.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
      trigger.xOld = spawnPos.x;
      trigger.yOld = spawnPos.y;
      trigger.zOld = spawnPos.z;
      trigger.setParentAndEffect(parent, effect, offset);
      parent.level().addFreshEntity(trigger);
      return trigger;
   }

   public void tick() {
      Vector3f offsetVec = (Vector3f)this.entityData.get(DATA_OFFSET);
      this.offset = new Vec3(offsetVec.x, offsetVec.y, offsetVec.z);
      if (this.requiredEffect == null) {
         String effectId = (String)this.entityData.get(DATA_EFFECT_ID);
         if (!effectId.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(effectId);
            if (id != null) {
               this.requiredEffect = BuiltInRegistries.MOB_EFFECT.getHolder(id).orElse(null);
            }
         }
      }

      LivingEntity parent = this.getParentEntityFromUUID();
      if (parent != null && parent.isAlive()) {
         this.parentEntity = parent;
         if (this.level().isClientSide()) {
            float widthScale = parent.getBbWidth() / 0.6F;
            float heightScale = parent.getBbHeight() / 1.8F;
            this.value = Math.max(widthScale, heightScale);
            Vec3 targetPos = parent.position().add(this.offset);
            double oldX = this.getX();
            double oldY = this.getY();
            double oldZ = this.getZ();
            this.setPos(targetPos.x, targetPos.y, targetPos.z);
            this.xOld = oldX;
            this.yOld = oldY;
            this.zOld = oldZ;
         }
      }

      if (!this.level().isClientSide()) {
         if (this.shouldRemove()) {
            this.discard();
         }
      } else {
         if (this.tickCount == 1 && EffekUnits.VFXENABLE() && this.requiredEffect != null) {
            if (!this.requiredEffect.equals(EFNMobEffectRegistry.ATTACK_DAMAGE_INCREASE) && !this.requiredEffect.equals(MobEffects.DAMAGE_BOOST)) {
               if (this.requiredEffect.equals(EFNMobEffectRegistry.ATTACK_SPEED_INCREASE)) {
                  ASEffek.playAS(ASEffek.Type.LEVEL1, this.level(), 0.0, 0.0, 0.0, 0.5F * this.value, this);
               } else if (this.requiredEffect.equals(EFNMobEffectRegistry.DAMAGE_REDUCTION) || this.requiredEffect.equals(MobEffects.DAMAGE_RESISTANCE)) {
                  DEFEffek.playDEF(DEFEffek.Type.LEVEL1, this.level(), 0.0, 0.0, 0.0, 0.5F * this.value, this);
               }
            } else {
               DMGEffek.playDMG(DMGEffek.Type.LEVEL1, this.level(), 0.0, 0.0, 0.0, 0.5F * this.value, this);
            }
         }
      }
   }

   @Nullable
   private LivingEntity getParentEntityFromUUID() {
      UUID parentUUID = this.entityData.get(DATA_PARENT_UUID).orElse(null);
      if (parentUUID == null) {
         return null;
      }

      LivingEntity entity = this.level().getPlayerByUUID(parentUUID);
      if (entity != null) {
         return entity;
      }

      if (this.level() instanceof ServerLevel serverLevel) {
         Entity e = serverLevel.getEntity(parentUUID);
         if (e instanceof LivingEntity && e.getUUID().equals(parentUUID)) {
            return (LivingEntity)e;
         }
      }

      for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(128.0), e -> e.getUUID().equals(parentUUID))) {
         return e;
      }

      return null;
   }

   private boolean shouldRemove() {
      if (this.parentEntity == null || !this.parentEntity.isAlive()) {
         return true;
      } else {
         return this.requiredEffect != null ? !this.parentEntity.hasEffect(this.requiredEffect) : false;
      }
   }

   public void setOffset(Vec3 offset) {
      this.offset = offset;
      this.entityData.set(DATA_OFFSET, new Vector3f((float)offset.x, (float)offset.y, (float)offset.z));
   }

   public Vec3 getOffset() {
      Vector3f offsetVec = (Vector3f)this.entityData.get(DATA_OFFSET);
      return new Vec3(offsetVec.x, offsetVec.y, offsetVec.z);
   }

   @Nullable
   public Holder<MobEffect> getRequiredEffect() {
      if (this.requiredEffect == null) {
         String effectId = (String)this.entityData.get(DATA_EFFECT_ID);
         if (!effectId.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(effectId);
            if (id != null) {
               this.requiredEffect = BuiltInRegistries.MOB_EFFECT.getHolder(id).orElse(null);
            }
         }
      }

      return this.requiredEffect;
   }

   @Nullable
   public UUID getParentUUID() {
      return this.entityData.get(DATA_PARENT_UUID).orElse(null);
   }

   @Nullable
   public LivingEntity getParentEntity() {
      return this.parentEntity;
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean shouldBeSaved() {
      return false;
   }

   public boolean shouldShowName() {
      return false;
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      builder.define(DATA_PARENT_UUID, Optional.empty());
      builder.define(DATA_EFFECT_ID, "");
      builder.define(DATA_OFFSET, new Vector3f());
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      if (tag.hasUUID("ParentUUID")) {
         this.entityData.set(DATA_PARENT_UUID, Optional.of(tag.getUUID("ParentUUID")));
      }

      if (tag.contains("RequiredEffect")) {
         this.entityData.set(DATA_EFFECT_ID, tag.getString("RequiredEffect"));
         ResourceLocation effectId = ResourceLocation.tryParse(tag.getString("RequiredEffect"));
         if (effectId != null) {
            this.requiredEffect = BuiltInRegistries.MOB_EFFECT.getHolder(effectId).orElse(null);
         }
      }

      if (tag.contains("OffsetX")) {
         Vec3 offset = new Vec3(tag.getDouble("OffsetX"), tag.getDouble("OffsetY"), tag.getDouble("OffsetZ"));
         this.offset = offset;
         this.entityData.set(DATA_OFFSET, new Vector3f((float)offset.x, (float)offset.y, (float)offset.z));
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      this.entityData.get(DATA_PARENT_UUID).ifPresent(uuid -> tag.putUUID("ParentUUID", uuid));
      String effectId = (String)this.entityData.get(DATA_EFFECT_ID);
      if (!effectId.isEmpty()) {
         tag.putString("RequiredEffect", effectId);
      }

      if (!this.offset.equals(Vec3.ZERO)) {
         tag.putDouble("OffsetX", this.offset.x);
         tag.putDouble("OffsetY", this.offset.y);
         tag.putDouble("OffsetZ", this.offset.z);
      }
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
      return new ClientboundAddEntityPacket(this, entity);
   }
}
