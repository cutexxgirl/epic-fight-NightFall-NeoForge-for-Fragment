package com.hm.efn.entity.effect;

import com.hm.efn.entity.EFNEntity;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNWeaponCategories;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.registry.entries.EpicFightMobEffects;

public class SummonedSwordEntity_Out extends VFXEntity {
   private static final int MAX_LIFE_TIME = 600;
   private LivingEntity owner;
   private Vec3 offset;
   private Vec3 lastOwnerPos;
   private Vec3 lastOwnerMotion;
   private int lifeTime = 0;
   private boolean shouldRemove = false;
   private boolean effectsApplied = false;

   public SummonedSwordEntity_Out(LivingEntity owner, float scale, Vec3 offset) {
      super((EntityType)EFNEntity.SUMMONED_SWORD_OUT.get(), owner, scale);
      this.owner = owner;
      this.noCulling = true;
      this.offset = offset;
      this.noPhysics = true;
      this.setInvulnerable(true);
      this.lastOwnerPos = owner.position();
      this.lastOwnerMotion = Vec3.ZERO;
      this.setNoGravity(true);
   }

   public SummonedSwordEntity_Out(EntityType<? extends VFXEntity> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.noCulling = true;
      this.setInvulnerable(true);
      this.setNoGravity(true);
   }

   public static boolean isHoldingYamato(Player player) {
      return player == null
         ? false
         : Stream.of(player.getMainHandItem(), player.getOffhandItem())
            .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
            .filter(Objects::nonNull)
            .anyMatch(cap -> cap.getWeaponCategory() == EFNWeaponCategories.EFN_YAMATO);
   }

   public static void summonAtWaist(ServerPlayerPatch ownerPatch, Vec3 offset, float scale) {
      LivingEntity owner = (LivingEntity)ownerPatch.getOriginal();
      ServerLevel level = (ServerLevel)owner.level();
      Vec3 waistPos = owner.position().add(0.0, owner.getBbHeight() * 0.6, 0.0);
      Vec3 spawnPos = waistPos.add(offset);
      SummonedSwordEntity_Out sword = new SummonedSwordEntity_Out(owner, scale, offset);
      sword.setPos(spawnPos);
      sword.setYRot(owner.getYRot());
      sword.setXRot(0.0F);
      level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.PLAYERS, 1.0F, 1.0F);
      level.addFreshEntity(sword);
   }

   public void markForRemoval() {
      this.shouldRemove = true;
   }

   public void tick() {
      super.tick();
      this.fallDistance = 0.0F;
      if (this.owner instanceof Player player && !isHoldingYamato(player)) {
         this.removeEffects();
         this.discard();
      } else {
         if (!this.effectsApplied && this.owner != null && this.owner.isAlive()) {
            this.applyEffects();
            this.effectsApplied = true;
         }

         LivingEntity owner = this.getOwner();
         if (owner != null && owner.isAlive()) {
            this.moveToOwner(owner);
         }

         if (owner != null && !owner.isAlive()) {
            this.removeEffects();
            this.discard();
         }

         this.lifeTime++;
         if (this.lifeTime >= 600) {
            this.removeEffects();
            this.discard();
         }
      }
   }

   protected void moveToOwner(LivingEntity owner) {
      this.setYRot(owner.yBodyRot);
      this.setYBodyRot(owner.yBodyRot);
      this.setYHeadRot(owner.yBodyRot);
      this.setPos(owner.position().add(0.0, owner.getBbHeight() * 0.6, 0.0));
   }

   private void applyEffects() {
      if (this.owner != null && !this.owner.level().isClientSide()) {
         MobEffectInstance resistance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 3, false, false, false);
         MobEffectInstance regeneration = new MobEffectInstance(MobEffects.REGENERATION, 600, 3, false, false, false);
         MobEffectInstance sin_stun_immunity = new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 600, 3, false, false, false);
         MobEffectInstance stun_immunity = new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY, 600, 3, false, false, false);
         this.owner.addEffect(resistance);
         this.owner.addEffect(regeneration);
         this.owner.addEffect(sin_stun_immunity);
         this.owner.addEffect(stun_immunity);
      }
   }

   private void removeEffects() {
         if (this.owner != null && !this.owner.level().isClientSide() && this.effectsApplied) {
            this.owner.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            this.owner.removeEffect(MobEffects.REGENERATION);
            this.owner.removeEffect(EFNMobEffectRegistry.SIN_STUN_IMMUNITY);
            this.owner.removeEffect(EpicFightMobEffects.STUN_IMMUNITY);
            this.effectsApplied = false;
         }
   }

   @NotNull
   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(100.0);
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return EFNAnimations.SUMMONED_SWORD_CIRCLE;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      return EFNAnimations.SUMMONED_SWORD_OUT;
   }

   @Nullable
   public Armature getArmature() {
      return ArmatureAccessor.create("efn", "entity/effect/summoned_sword_circle", Armature::new).get();
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return MeshAccessor.create("efn", "entity/effect/summoned_sword_circle", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }

   @Nullable
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/summoned_sword.png");
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/summoned_sword.png");
   }

   public Vec3 getLastOwnerMotion() {
      return this.lastOwnerMotion;
   }

   public Vec3 getLastOwnerPos() {
      return this.lastOwnerPos;
   }

   public Vec3 getOffset() {
      return this.offset;
   }

   public boolean isShouldRemove() {
      return this.shouldRemove;
   }
}
