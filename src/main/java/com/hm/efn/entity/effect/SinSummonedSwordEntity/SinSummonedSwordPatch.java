package com.hm.efn.entity.effect.SinSummonedSwordEntity;

import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.entity.effect.BlastSummonedSwordEntity;
import com.hm.efn.entity.effect.SummonedSwordEntity_In;
import com.hm.efn.entity.effect.SummonedSwordEntity_Out;
import com.hm.efn.gameasset.EFNExtraDamageInstance;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.TargetTeleportUtils;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.epicfight.AvalonFactions;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.DodgeLocationIndicator;

public class SinSummonedSwordPatch<T extends SinSummonedSwordEntity> extends MobPatch<T> {
   private int tickCount = 0;
   @Nullable
   private LivingEntityPatch<?> ownerPatch;
   public static final TagKey<DamageType> HEAVY_RAIN_SWORD_DAMAGE = EFNExtraDamageInstance.createDamageType("efn_heavy_rain_sword_damage");
   public static final TagKey<DamageType> BLAST_SWORD_DAMAGE = EFNExtraDamageInstance.createDamageType("efn_blast_sword_damage");
   public static final TagKey<DamageType> SUMMONED_SWORD_DAMAGE = EFNExtraDamageInstance.createDamageType("efn_summoned_sword_damage");
   public static final TagKey<DamageType> DAMOCLES_SWORD_DAMAGE = EFNExtraDamageInstance.createDamageType("efn_damocles_sword_damage");
   private boolean hasCachedZRot = false;
   private float cachedZRot = 0.0F;

   public SinSummonedSwordPatch(T entity) {
      super(entity);
   }

   public boolean isHostileMob(Entity entity) {
      if (!(entity instanceof LivingEntity)) {
         return false;
      } else {
         return entity instanceof DoppelgangerEntity
            ? false
            : entity instanceof Enemy
               || entity instanceof Monster
               || entity instanceof Mob mob && mob.getTarget() == ((SinSummonedSwordEntity)this.original).getOwner()
               || entity.getType() == EntityType.SLIME
               || entity.getType() == EntityType.MAGMA_CUBE
               || entity.getType() == EntityType.PHANTOM
               || entity.getType() == EntityType.GHAST;
      }
   }

   public boolean applyStun(StunType stunType, float stunTime) {
      return false;
   }

   public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
      return null;
   }

   public Faction getFaction() {
      return AvalonFactions.EMPTY;
   }

   public void onConstructed(T entityIn) {
      this.original = entityIn;
      this.armature = this.getArmature();
      Animator animator = EpicFightSharedConstants.getAnimator(this);
      this.animator = animator;
      this.initAnimator(animator);
      animator.postInit();
   }

   public void updateMotion(boolean b) {
      if (b) {
         this.currentLivingMotion = LivingMotions.IDLE;
      }
   }

   public void preTick(Pre event) {
      super.preTick();
      this.tickCount++;
      boolean playAnimation = ((SinSummonedSwordEntity)this.getOriginal()).getPlayAnimation();
      if ((!this.isLogicalClient() || ((SinSummonedSwordEntity)this.original).getDefaultAnimation() != null || playAnimation) && !playAnimation) {
         ((SinSummonedSwordEntity)this.original).setPlayAnimation(true);
         if (this.isLogicalClient()) {
            this.getClientAnimator().playAnimation(((SinSummonedSwordEntity)this.original).getDefaultAnimation(), 0.0F);
         } else {
            this.playAnimationSynchronized(((SinSummonedSwordEntity)this.original).getDefaultAnimation(), 0.0F);
         }

         ((SinSummonedSwordEntity)this.original).setShouldRender(true);
      }

      if (((SinSummonedSwordEntity)this.getOriginal()).isInStandby()) {
         if (!((SinSummonedSwordEntity)this.getOriginal()).isBlast() && !((SinSummonedSwordEntity)this.getOriginal()).isNoAim()) {
            LivingEntity target = this.target();
            if (target != null) {
               ((SinSummonedSwordEntity)this.getOriginal()).aimAtEntity(target);
            } else if (this.getOwnerPatch() != null) {
               float ownerY = ((LivingEntity)this.getOwnerPatch().getOriginal()).getYHeadRot();
               float ownerX = ((LivingEntity)this.getOwnerPatch().getOriginal()).getXRot();
               ((SinSummonedSwordEntity)this.getOriginal()).setYRot(ownerY);
               ((SinSummonedSwordEntity)this.getOriginal()).setYBodyRot(ownerY);
               ((SinSummonedSwordEntity)this.getOriginal()).setYHeadRot(ownerY);
               ((SinSummonedSwordEntity)this.getOriginal()).setSyncXRot(ownerX);
            }
         }
      } else {
         ((SinSummonedSwordEntity)this.getOriginal()).setYBodyRot(((SinSummonedSwordEntity)this.getOriginal()).getYRot());
         ((SinSummonedSwordEntity)this.getOriginal()).yBodyRotO = ((SinSummonedSwordEntity)this.getOriginal()).getYRot();
         ((SinSummonedSwordEntity)this.getOriginal()).setYHeadRot(((SinSummonedSwordEntity)this.getOriginal()).getYRot());
         ((SinSummonedSwordEntity)this.getOriginal()).yHeadRotO = ((SinSummonedSwordEntity)this.getOriginal()).getYRot();
      }
   }

   @Nullable
   public LivingEntityPatch<?> getOwnerPatch() {
      if (this.ownerPatch != null) {
         return this.ownerPatch;
      } else if (((SinSummonedSwordEntity)this.getOriginal()).getOwner() != null) {
         this.ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(
            ((SinSummonedSwordEntity)this.getOriginal()).getOwner(), LivingEntityPatch.class
         );
         return this.ownerPatch;
      } else {
         return null;
      }
   }

   public OpenMatrix4f getModelMatrix(float partialTicks) {
      return super.getModelMatrix(partialTicks)
         .scale(
            ((SinSummonedSwordEntity)this.original).getScale(),
            ((SinSummonedSwordEntity)this.original).getScale(),
            ((SinSummonedSwordEntity)this.original).getScale()
         );
   }

   public OpenMatrix4f getMatrix(float partialTicks) {
      return super.getMatrix(partialTicks)
         .scale(
            ((SinSummonedSwordEntity)this.original).getScale(),
            ((SinSummonedSwordEntity)this.original).getScale(),
            ((SinSummonedSwordEntity)this.original).getScale()
         );
   }

   public Armature getArmature() {
      return ((SinSummonedSwordEntity)this.original).getArmature();
   }

   public LivingEntity target() {
      if (Objects.requireNonNull(this.getOwnerPatch()).getTarget() != null) {
         return this.getOwnerPatch().getTarget();
      }

      if (this.getTarget() != null) {
         return this.getTarget();
      }

      Level level = ((SinSummonedSwordEntity)this.getOriginal()).level();
      double range = 16.0;
      List<Entity> nearbyEntities = level.getEntities(
         this.getOwnerPatch().getOriginal(),
         ((LivingEntity)this.getOwnerPatch().getOriginal()).getBoundingBox().inflate(range),
         entityx -> this.isHostileMob(entityx) && entityx != this.getOwnerPatch().getOriginal() && !(entityx instanceof DoppelgangerEntity)
      );
      Entity nearestTarget = null;
      double minDistance = Double.MAX_VALUE;
      Entity owner = this.getOwnerPatch().getOriginal();

      for (Entity entity : nearbyEntities) {
         double distance = owner.distanceToSqr(entity);
         if (level.clip(new ClipContext(owner.getEyePosition(1.0F), entity.getEyePosition(1.0F), Block.COLLIDER, Fluid.NONE, owner)).getType() == Type.MISS
            && distance < minDistance) {
            minDistance = distance;
            nearestTarget = entity;
         }
      }

      return nearestTarget instanceof LivingEntity ? (LivingEntity)nearestTarget : null;
   }

   public void poseTick(DynamicAnimation animation, Pose pose, float elapsedTime, float partialTick) {
      float interpolatedPitch;
      if (!((SinSummonedSwordEntity)this.original).isHeavyRain() && !((SinSummonedSwordEntity)this.original).isDamoclesMain()) {
         float prevXRot = ((SinSummonedSwordEntity)this.original).xRotO;
         float currentXRot = ((SinSummonedSwordEntity)this.original).getXRot();
         interpolatedPitch = prevXRot + (currentXRot - prevXRot) * partialTick;
      } else {
         interpolatedPitch = 90.0F;
      }

      float zRot = 0.0F;
      if (((SinSummonedSwordEntity)this.original).isHeavyRain()) {
         if (!this.hasCachedZRot) {
            long seed = ((SinSummonedSwordEntity)this.original).getUUID().getMostSignificantBits()
               ^ ((SinSummonedSwordEntity)this.original).getUUID().getLeastSignificantBits();
            Random random = new Random(seed);
            this.cachedZRot = random.nextFloat() * 360.0F;
            this.hasCachedZRot = true;
         }

         zRot = this.cachedZRot;
      }

      AvalonAnimationUtils.joinRotationInPose(pose, this, "Root", -interpolatedPitch, 0.0F, zRot);
   }

   public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
      if (this.getOwnerPatch() == null || !this.shouldUseOwnerAttack()) {
         return super.attack(damageSource, target, hand);
      } else if (((SinSummonedSwordEntity)this.getOriginal()).isInStandby()
         || ((SinSummonedSwordEntity)this.getOriginal()).isStuckInBlock()
         || ((SinSummonedSwordEntity)this.getOriginal()).hasHitTarget()) {
         return AttackResult.missed(0.0F);
      } else if (target instanceof DodgeLocationIndicator) {
         return AttackResult.missed(0.0F);
      } else if (target instanceof DoppelgangerEntity doppelgangerEntity
         && doppelgangerEntity.getOwner() != null
         && doppelgangerEntity.getOwner() == ((SinSummonedSwordEntity)this.original).getOwner()) {
         return AttackResult.missed(0.0F);
      } else {
         if (target instanceof SummonedSwordEntity_In) {
            return AttackResult.missed(0.0F);
         }

         if (target instanceof SummonedSwordEntity_Out) {
            return AttackResult.missed(0.0F);
         }

         if (target instanceof BlastSummonedSwordEntity) {
            return AttackResult.missed(0.0F);
         }

         if (target instanceof SinSummonedSwordEntity) {
            return AttackResult.missed(0.0F);
         }

         if (((SinSummonedSwordEntity)this.original).getOwner() != null && target == ((SinSummonedSwordEntity)this.original).getOwner()) {
            return AttackResult.missed(0.0F);
         }

         if (((SinSummonedSwordEntity)this.getOriginal()).isBlast() || ((SinSummonedSwordEntity)this.getOriginal()).isDamoclesSub()) {
            damageSource.setBaseArmorNegation(80.0F);
            damageSource.addRuntimeTag(BLAST_SWORD_DAMAGE);
            damageSource.addExtraDamage(EFNExtraDamageInstance.EXTRA_DAMAGE.create(new float[]{2.5F}));
         }

         if (((SinSummonedSwordEntity)this.getOriginal()).isHeavyRain()) {
            damageSource.setBaseArmorNegation(100.0F);
            damageSource.setStunType(StunType.SHORT);
            damageSource.addRuntimeTag(HEAVY_RAIN_SWORD_DAMAGE);
            damageSource.addExtraDamage(EFNExtraDamageInstance.EXTRA_DAMAGE.create(new float[]{1.0F}));
            if (target instanceof LivingEntity livingTarget && target != this.getOwnerPatch().getOriginal()) {
               LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingTarget, LivingEntityPatch.class);
               if (targetPatch != null) {
                  if (targetPatch.isStunned()) {
                     targetPatch.applyStun(StunType.HOLD, 3.0F);
                     livingTarget.addEffect(new MobEffectInstance(EFNMobEffectRegistry.HEAVY_RAIN_STUN, 60, 0, false, false, false));
                  }
               } else {
                  livingTarget.addEffect(new MobEffectInstance(EFNMobEffectRegistry.HEAVY_RAIN_STUN, 60, 0, false, false, false));
               }
            }
         }

         if (((SinSummonedSwordEntity)this.getOriginal()).isDamoclesMain()) {
            damageSource.setBaseArmorNegation(100.0F);
            damageSource.setBaseImpact(10.0F);
            damageSource.addRuntimeTag(DAMOCLES_SWORD_DAMAGE);
            damageSource.setStunType(StunType.HOLD);
            damageSource.addExtraDamage(EFNExtraDamageInstance.EXTRA_DAMAGE.create(new float[]{15.0F}));
         }

         AttackResult result = this.getOwnerPatch().attack(damageSource, target, hand);
         if (result.resultType.shouldCount() && !((SinSummonedSwordEntity)this.getOriginal()).hasHitTarget()) {
            ((SinSummonedSwordEntity)this.getOriginal()).setHasHitTarget(true);
            LivingEntity owner = (LivingEntity)this.getOwnerPatch().getOriginal();
            if (owner != null) {
               ServerPlayerPatch ownerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(owner, ServerPlayerPatch.class);
               if (ownerPatch != null) {
                  if (((SinSummonedSwordEntity)this.getOriginal()).isAngel()) {
                     TargetTeleportUtils.ExecuteYamatoTricker(ownerPatch);
                     owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.AMETHYST_BLOCK_HIT, owner.getSoundSource(), 1.0F, 1.0F);
                     owner.addEffect(new MobEffectInstance(EFNMobEffectRegistry.INVINCIBILITY_EFFECT, 30, 0, false, false, false));
                  } else if (((SinSummonedSwordEntity)this.getOriginal()).isDemon()) {
                     TargetTeleportUtils.ExecuteYamatoCatcher(ownerPatch);
                     owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.WITHER_SHOOT, owner.getSoundSource(), 1.0F, 1.0F);
                     owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 2, false, false, false));
                  }
               }
            }
         }

         return result;
      }
   }

   public boolean shouldUseOwnerAttack() {
      return true;
   }

   public SoundEvent getSwingSound(InteractionHand hand) {
      return (SoundEvent)EFNSounds.NOSOUND.get();
   }

   public SoundEvent getWeaponHitSound(InteractionHand hand) {
      return (SoundEvent)EFNSounds.NOSOUND.get();
   }

   protected void initAnimator(Animator animator) {
      super.initAnimator(animator);
      animator.addLivingAnimation(LivingMotions.IDLE, ((SinSummonedSwordEntity)this.original).getIdleAnimation());
   }

   @Nullable
   public EpicFightDamageSource getEpicFightDamageSource() {
      return this.getOwnerPatch() != null ? this.getOwnerPatch().getEpicFightDamageSource() : super.getEpicFightDamageSource();
   }

   public EpicFightDamageSource getDamageSource(AnimationAccessor<? extends StaticAnimation> animation, InteractionHand hand) {
      return this.getOwnerPatch() != null ? this.getOwnerPatch().getDamageSource(animation, hand) : super.getDamageSource(animation, hand);
   }

   public boolean isTargetInvulnerable(Entity entity) {
      if (entity.equals(((SinSummonedSwordEntity)this.getOriginal()).getOwner())) {
         return true;
      } else {
         return entity instanceof VFXEntity artifactSpiritEntity && this.getOwnerPatch() != null
            ? ((LivingEntity)this.getOwnerPatch().getOriginal()).equals(artifactSpiritEntity.getOwner())
            : false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean flashTargetIndicator(LocalPlayerPatch playerPatch) {
      return false;
   }
}
