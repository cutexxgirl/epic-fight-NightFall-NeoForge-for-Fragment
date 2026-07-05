package com.hm.efn.gameasset.animations;

import com.hm.efn.animations.types.yamato.YamatoAnimationUtils;
import com.hm.efn.animations.types.yamato.YamatoAttackAnimation;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.EFNVFXManagers;
import com.hm.efn.entity.effect.YamatoDriveFireBall;
import com.hm.efn.entity.geoEntity.JudgementCutNormal;
import com.hm.efn.entity.geoEntity.JudgementCutPerfect;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffectEntityInvoker;
import com.hm.efn.util.ParticleEffectInvoker;
import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.Event;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class EFNYamatoAnimations {
   public static final Collider JUDGEMENT_CUT = new OBBCollider(1.5, 1.5, 3.0, 0.0, 0.8, -3.0);
   public static final Collider RAPIADSLASH = new OBBCollider(1.5, 1.5, 1.5, 0.0, 1.0, 0.0);
   public static final Collider RAPIADSLASH_END = new OBBCollider(2.0, 2.0, 2.0, 0.0, 1.0, 0.0);
   public static final Collider STOMP = new OBBCollider(2.7, 2.7, 2.7, 0.0, 1.0, 0.0);
   public static final Collider VOLCANOL = new OBBCollider(2.5, 2.5, 2.5, 0.0, 0.5, 0.0);
   public static final Collider KILLERBEE = new MultiOBBCollider(4, 1.0, 1.0, 1.0, 0.0, 0.9, 0.0);
   public static final Collider AERIALRAVE = new MultiOBBCollider(3, 0.8, 0.8, 1.5, 0.0, 0.0, -0.95);
   public static final Event YAMATO_IN = (entitypatch, self, params) -> {
      if (entitypatch != null && entitypatch.getOriginal() != null) {
         if (entitypatch.isLogicalClient() && EFNSounds.YAMATO_IN.get() != null) {
            entitypatch.playSound((SoundEvent)EFNSounds.YAMATO_IN.get(), 1.0F, 0.0F, 0.0F);
         }
      }
   };
   public static final Event LOOPED_FALLING = (entitypatch, self, params) -> {
      LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
      if (!entity.onGround()) {
         Level level = entity.level();
         int minBuildHeight = level.getMinBuildHeight();
         int dpx = Mth.floor(entity.getX());
         int currentY = Mth.floor(entity.getY());
         int dpz = Mth.floor(entity.getZ());
         double distToGround = 999.0;
         if (currentY >= minBuildHeight) {
            MutableBlockPos mutablePos = new MutableBlockPos(dpx, currentY, dpz);

            while (mutablePos.getY() >= minBuildHeight) {
               BlockState state = level.getBlockState(mutablePos);
               if (!state.isAir() && !(state.getBlock() instanceof BushBlock)) {
                  distToGround = entity.getY() - mutablePos.getY();
                  break;
               }

               mutablePos.move(Direction.DOWN);
            }
         }

         boolean isFlying = entity instanceof Player player && player.getAbilities().flying;
         if (!isFlying && distToGround > 1.5) {
            AnimationPlayer animPlayer = entitypatch.getAnimator().getPlayerFor(self);
            if (animPlayer != null) {
               animPlayer.setElapsedTimeCurrent(animPlayer.getElapsedTime() - 0.05F);
            }
         }
      }
   };
   public static final Event GROUND_SLAM_LAND_A = (entitypatch, self, params) -> {
      LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
      if (entity instanceof Player player) {
         entity.level().playSound(player, entity, (SoundEvent)EpicFightSounds.SLAM_HEAVY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
      }

      Vec3 viewOffset = calculateHorizontalOffset(entity, 0.7F, 0.0F);
      float posX = (float)(entity.getX() + viewOffset.x);
      float posY = (float)entity.getY();
      float posZ = (float)(entity.getZ() + viewOffset.z);
      BlockState groundBlock = entity.level().getBlockState(new MutableBlockPos(posX, posY, posZ));

      while ((groundBlock.getBlock() instanceof BushBlock || groundBlock.isAir()) && !groundBlock.is(Blocks.BEDROCK)) {
         groundBlock = entity.level().getBlockState(new MutableBlockPos(posX, --posY, posZ));
      }

      posY = (int)posY;
      Vec3 center = new Vec3(posX, posY, posZ);
      if (!entity.level().isClientSide()) {
         ServerLevel level = (ServerLevel)entity.level();
         LevelUtil.circleSlamFracture(entity, level, center, 2.1, false, true, false);

         for (int i = 0; i < 35; i++) {
            double angle = (Math.PI * 2) * level.random.nextDouble();
            double radius = 1.8 * level.random.nextDouble();
            Vec3 particlePos = center.add(radius * Math.cos(angle), 0.5, radius * Math.sin(angle));
            level.sendParticles(ParticleTypes.ENCHANT, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 1.6, 0.0, 0.05);
         }
      }
   };
   public static final Event GROUNDSLAM = (entitypatch, self, params) -> {
      LivingEntity livingEntity = (LivingEntity)entitypatch.getOriginal();
      Level level = livingEntity.level();
      Vec3 horizontalOffset = calculateHorizontalOffset(livingEntity, 0.7F, 0.3F);
      Vec3 floorPos = getfloor(
         entitypatch, self, new Vec3f((float)horizontalOffset.x, 0.0F, (float)horizontalOffset.z), ((HumanoidArmature)Armatures.BIPED.get()).legR
      );
      BlockState blockState = level.getBlockState(new MutableBlockPos(floorPos.x, floorPos.y, floorPos.z));
      if (entitypatch instanceof PlayerPatch) {
         level.playSound(
            (Player)livingEntity,
            livingEntity,
            blockState.is(Blocks.STONE) ? SoundEvents.ANVIL_LAND : (SoundEvent)EpicFightSounds.SLAM_HEAVY.get(),
            SoundSource.PLAYERS,
            1.0F,
            1.0F
         );
      }

      if (!level.isClientSide()) {
         ServerLevel serverLevel = (ServerLevel)level;
         Vec3 center = new Vec3(floorPos.x + horizontalOffset.x, (int)floorPos.y, floorPos.z + horizontalOffset.z);
         LevelUtil.circleSlamFracture(livingEntity, serverLevel, center, 1.8, false, true, false);

         for (int i = 0; i < 35; i++) {
            double angle = (Math.PI * 2) * serverLevel.random.nextDouble();
            double radius = 1.8 * serverLevel.random.nextDouble();
            Vec3 particlePos = center.add(radius * Math.cos(angle), 0.5, radius * Math.sin(angle));
            serverLevel.sendParticles(ParticleTypes.ENCHANT, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 1.6, 0.0, 0.05);
         }
      }
   };
   public static final Event GROUNDTHRUST_DRIVE = (entitypatch, self, params) -> {
      LivingEntity livingEntity = (LivingEntity)entitypatch.getOriginal();
      Level level = livingEntity.level();
      Vec3 horizontalOffset = calculateHorizontalOffset(livingEntity, 0.7F, 0.3F);
      Vec3 floorPos = getfloor(
         entitypatch,
         self,
         new Vec3f((float)horizontalOffset.x, 0.0F, (float)horizontalOffset.z),
         ((HumanoidArmature)Armatures.BIPED.get()).rootJoint
      );
      BlockState blockState = level.getBlockState(new MutableBlockPos(floorPos.x, floorPos.y, floorPos.z));
      if (entitypatch instanceof PlayerPatch) {
         level.playSound(
            (Player)livingEntity,
            livingEntity,
            blockState.is(Blocks.STONE) ? SoundEvents.ANVIL_LAND : (SoundEvent)EpicFightSounds.SLAM_HEAVY.get(),
            SoundSource.PLAYERS,
            1.0F,
            1.0F
         );
      }

      if (!level.isClientSide()) {
         ServerLevel serverLevel = (ServerLevel)level;
         Vec3 center = new Vec3(floorPos.x + horizontalOffset.x, (int)floorPos.y, floorPos.z + horizontalOffset.z);
         LevelUtil.circleSlamFracture(livingEntity, serverLevel, center, 3.0, false, true, false);

         for (int i = 0; i < 35; i++) {
            double angle = (Math.PI * 2) * serverLevel.random.nextDouble();
            double radius = 1.8 * serverLevel.random.nextDouble();
            Vec3 particlePos = center.add(radius * Math.cos(angle), 0.5, radius * Math.sin(angle));
            serverLevel.sendParticles(ParticleTypes.ENCHANT, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 1.6, 0.0, 0.05);
         }
      }
   };
   public static AnimationAccessor<StaticAnimation> YAMATO_IDLE;
   public static AnimationAccessor<AvalonMovementAnimation> YAMATO_WALK;
   public static AnimationAccessor<AvalonMovementAnimation> YAMATO_RUN;
   public static AnimationAccessor<StaticAnimation> YAMATO_KNEEL;
   public static AnimationAccessor<StaticAnimation> YAMATO_SNEAK;
   public static AnimationAccessor<StaticAnimation> YAMATO_JUMP;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_NORMAL_AUTO1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_NORMAL_AUTO2;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_NORMAL_AUTO3;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_DIVORCE_AUTO1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_DIVORCE_AUTO2;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_DIVORCE_AUTO3;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_EXTEND_AUTO3;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_EXTEND_AUTO4;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_EXTEND_AUTO5;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALRAVE_AUTO1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALRAVE_AUTO2;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALRAVE_AUTO3;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_VOLCANOL_ALL;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_VOLCANOL_CHARGE;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_VOLCANOL;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDEMENCUT;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDEMENCUT_CHARGE;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDEMENCUT_JUST;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDEMENCUT_JUST_MOB;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDEMENCUT_ALL;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_REPAIDSLASH;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_REPAIDSLASH_MOB;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_FLARECUT;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_FLARECUT_REPAID;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_FLARECUT_RISING;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_UPPERSLASH;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_UPPERSLASH_HOLD;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_DRIVE;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_DRIVE_MOB;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_STOMP;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AIRFLUSH;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_HELMBREAKER;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_ORBIT_1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_ORBIT_2;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_KILLERBEE;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_KILLERBEE_HIT;

   public static InTimeEvent summonJudgementCutPerfect(int startFrame, float scale, float damage) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(
         start,
         (entityPatch, self, params) -> {
            if (entityPatch.getOriginal() instanceof LivingEntity owner) {
               if (owner.level() instanceof ServerLevel level) {
                  Entity target = entityPatch.getTarget();
                  Vec3 spawnPos;
                  if (target != null && EFNVFXManagers.isTargetInRange(owner, target, 7.5)) {
                     spawnPos = target.position().add(0.0, 0.2, 0.0);
                  } else {
                     spawnPos = EFNVFXManagers.calculateEffectPosition(owner, 7.5, -1.3F, 0.0);
                  }

                  JudgementCutPerfect judgementCutPerfect = new JudgementCutPerfect(
                     (EntityType<? extends PathfinderMob>)EFNEntity.JUDGEMENTCUT_PERFECT.get(), level, spawnPos.x, spawnPos.y, spawnPos.z
                  );
                  judgementCutPerfect.setOwner(owner);
                  judgementCutPerfect.setScale(scale);
                  float ownerAttackDamage = (float)owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                  float bonusDamage = ownerAttackDamage * 0.1F;
                  float enchantmentBonus = 0.0F;
                  ItemStack mainHandItem = owner.getMainHandItem();
                  if (!mainHandItem.isEmpty()) {
                     int sweepingEdgeLevel = EFNEnchantment.getLevel(mainHandItem, Enchantments.SWEEPING_EDGE);
                     enchantmentBonus += sweepingEdgeLevel * 1.0F;
                     int sharpnessLevel = EFNEnchantment.getLevel(mainHandItem, Enchantments.SHARPNESS);
                     enchantmentBonus += sharpnessLevel * 0.5F;
                  }

                  float totalDamage = damage + bonusDamage + enchantmentBonus;
                  judgementCutPerfect.setAttackDamage(totalDamage);
                  judgementCutPerfect.setXRotOffset(0.0F);
                  judgementCutPerfect.setYRotOffset(owner.getYRot());
                  judgementCutPerfect.setZRotOffset(0.0F);
                  judgementCutPerfect.setStartYRot(owner.getYRot());
                  judgementCutPerfect.setYRot(owner.getYRot());
                  judgementCutPerfect.setYBodyRot(owner.getYRot());
                  judgementCutPerfect.setYHeadRot(owner.getYRot());
                  judgementCutPerfect.setXRot(0.0F);
                  level.addFreshEntity(judgementCutPerfect);
               }
            }
         },
         Side.SERVER
      );
   }

   public static InTimeEvent summonJudgementCutNormal(int startFrame, float scale, float damage) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(
         start,
         (entityPatch, self, params) -> {
            if (entityPatch.getOriginal() instanceof LivingEntity owner) {
               if (owner.level() instanceof ServerLevel level) {
                  Entity target = entityPatch.getTarget();
                  Vec3 spawnPos;
                  if (target != null && EFNVFXManagers.isTargetInRange(owner, target, 7.5)) {
                     spawnPos = target.position().add(0.0, 0.2, 0.0);
                  } else {
                     spawnPos = EFNVFXManagers.calculateEffectPosition(owner, 7.5, -1.3F, 0.0);
                  }

                  JudgementCutNormal judgementCutNormal = new JudgementCutNormal(
                     (EntityType<? extends PathfinderMob>)EFNEntity.JUDGEMENTCUT_NORMAL.get(), level, spawnPos.x, spawnPos.y, spawnPos.z
                  );
                  judgementCutNormal.setOwner(owner);
                  judgementCutNormal.setScale(scale);
                  float ownerAttackDamage = (float)owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
                  float bonusDamage = ownerAttackDamage * 0.1F;
                  float enchantmentBonus = 0.0F;
                  ItemStack mainHandItem = owner.getMainHandItem();
                  if (!mainHandItem.isEmpty()) {
                     int sweepingEdgeLevel = EFNEnchantment.getLevel(mainHandItem, Enchantments.SWEEPING_EDGE);
                     enchantmentBonus += sweepingEdgeLevel * 1.0F;
                     int sharpnessLevel = EFNEnchantment.getLevel(mainHandItem, Enchantments.SHARPNESS);
                     enchantmentBonus += sharpnessLevel * 0.5F;
                  }

                  float totalDamage = damage + bonusDamage + enchantmentBonus;
                  judgementCutNormal.setAttackDamage(totalDamage);
                  judgementCutNormal.setXRotOffset(0.0F);
                  judgementCutNormal.setYRotOffset(owner.getYRot());
                  judgementCutNormal.setZRotOffset(0.0F);
                  judgementCutNormal.setStartYRot(owner.getYRot());
                  judgementCutNormal.setYRot(owner.getYRot());
                  judgementCutNormal.setYBodyRot(owner.getYRot());
                  judgementCutNormal.setYHeadRot(owner.getYRot());
                  judgementCutNormal.setXRot(0.0F);
                  level.addFreshEntity(judgementCutNormal);
               }
            }
         },
         Side.SERVER
      );
   }

   public static InTimeEvent shootFireballDownward(int startFrame, float damageMultiplier) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         if (entityPatch.getOriginal() instanceof LivingEntity owner) {
            if (owner.level() instanceof ServerLevel level) {
               Vec3 var20 = owner.position();
               Vec3 spawnPos = new Vec3(var20.x, var20.y, var20.z);
               Vec3 groundPos = getGroundPosition(spawnPos, level);
               double fallDistance = Math.max(0.5, spawnPos.y - groundPos.y);
               double targetTime = 0.1;
               double baseSpeed = fallDistance / (targetTime * 20.0);
               double gravityCompensation = fallDistance / 2.0 * 0.5;
               double finalSpeed = baseSpeed + gravityCompensation;
               finalSpeed = Math.max(0.8, Math.min(30.0, finalSpeed));
               YamatoDriveFireBall yamatoDriveFireBall = new YamatoDriveFireBall(level, owner, 0.0, -0.5, 0.0, 1);
               yamatoDriveFireBall.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
               yamatoDriveFireBall.setDeltaMovement(0.0, -finalSpeed, 0.0);
               level.addFreshEntity(yamatoDriveFireBall);
               ParticleEffectInvoker.spawnEnhancedFireballParticles(level, new Vec3(var20.x, var20.y + 0.3, var20.z));
               owner.playSound(SoundEvents.FIRECHARGE_USE, 0.8F, 1.0F);
            }
         }
      }, Side.SERVER);
   }

   private static Vec3 getGroundPosition(Vec3 startPos, Level level) {
      double dpx = startPos.x;
      double dpy = startPos.y;
      double dpz = startPos.z;
      MutableBlockPos pos = new MutableBlockPos(dpx, dpy, dpz);

      while (true) {
         BlockState block = level.getBlockState(pos);
         if (!(block.getBlock() instanceof BushBlock) && !block.isAir() || block.is(Blocks.VOID_AIR) || dpy <= -64.0) {
            return new Vec3(dpx, dpy + 1.0, dpz);
         }

         pos.setY(pos.getY() - 1);
         dpy--;
      }
   }

   public static void build(AnimationBuilder builder) {
      YAMATO_IDLE = builder.nextAccessor("biped/yamato/dmcyamato_idle", accessor -> new StaticAnimation(0.15F, true, accessor, Armatures.BIPED));
      YAMATO_WALK = builder.nextAccessor("biped/yamato/dmcyamato_walk", accessor -> new AvalonMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.0F));
      YAMATO_RUN = builder.nextAccessor("biped/yamato/dmcyamato_run", accessor -> new AvalonMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 0.85F));
      YAMATO_KNEEL = builder.nextAccessor("biped/yamato/dmcyamato_kneel", accessor -> new StaticAnimation(0.15F, true, accessor, Armatures.BIPED));
      YAMATO_SNEAK = builder.nextAccessor("biped/yamato/dmcyamato_sneak", accessor -> new StaticAnimation(0.15F, true, accessor, Armatures.BIPED));
      YAMATO_JUMP = builder.nextAccessor("biped/yamato/dmcyamato_jump", accessor -> new StaticAnimation(0.15F, false, accessor, Armatures.BIPED));
      YAMATO_NORMAL_AUTO1 = builder.nextAccessor(
         "biped/yamato/dmcyamato_slasher_1",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  10,
                  15,
                  18,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolL,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_ROD.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(8, 3, 1.0F, 2.0F, 1.0F)})
      );
      YAMATO_NORMAL_AUTO2 = builder.nextAccessor(
         "biped/yamato/dmcyamato_slasher_2",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  3,
                  14,
                  23,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolL,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_ROD.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(3, 3, 1.0F, 2.0F, 1.0F)})
      );
      YAMATO_NORMAL_AUTO3 = builder.nextAccessor(
         "biped/yamato/dmcyamato_slasher_3",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  9,
                  17,
                  35,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HITBACK_1,
                  EFNStunAnimations.BIPED_HITBACK_AIR
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 4, 2.0F, 2.0F, 1.0F), InTimeEvent.create(2.483F, YAMATO_IN, Side.BOTH)})
      );
      YAMATO_DIVORCE_AUTO1 = builder.nextAccessor(
         "biped/yamato/dmcyamato_divorce_1",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  24,
                  33,
                  38,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_4.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(24, 3, 1.0F, 2.0F, 1.0F), InTimeEvent.create(2.583F, YAMATO_IN, Side.BOTH)})
      );
      YAMATO_DIVORCE_AUTO2 = builder.nextAccessor(
         "biped/yamato/dmcyamato_divorce_2",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  38,
                  50,
                  56,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(38, 3, 1.0F, 2.0F, 1.0F), InTimeEvent.create(2.3F, YAMATO_IN, Side.BOTH)})
      );
      YAMATO_DIVORCE_AUTO3 = builder.nextAccessor(
         "biped/yamato/dmcyamato_divorce_3",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  35,
                  43,
                  76,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HITBACK_1,
                  EFNStunAnimations.BIPED_HITBACK_1
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_3.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(35, 4, 2.0F, 2.0F, 1.0F), InTimeEvent.create(2.716F, YAMATO_IN, Side.BOTH)})
      );
      YAMATO_EXTEND_AUTO3 = builder.nextAccessor(
         "biped/yamato/dmcyamato_slasher_crosscut_1",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  7,
                  14,
                  15,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               ),
               YamatoAnimationUtils.createCustomStunPhase(
                  17,
                  24,
                  30,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.11F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1.0F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(2.733F, YAMATO_IN, Side.BOTH),
                  EffectEntityInvoker.catchEntities(2, 24, 4.0F, 0.8F, 0.25F)
               }
            )
      );
      YAMATO_EXTEND_AUTO4 = builder.nextAccessor(
         "biped/yamato/dmcyamato_slasher_crosscut_2",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  25,
                  32,
                  38,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HITDOWN_0,
                  EFNStunAnimations.BIPED_HITDOWN_AIR
               ),
               YamatoAnimationUtils.createCustomStunPhase(
                  38,
                  45,
                  50,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HITUP_GROUND_0,
                  EFNStunAnimations.BIPED_HITUP_GROUND_0
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(2.35F, YAMATO_IN, Side.BOTH), EffectEntityInvoker.catchEntities(15, 45, 4.0F, 1.8F, 0.25F)})
      );
      YAMATO_EXTEND_AUTO5 = builder.nextAccessor(
         "biped/yamato/dmcyamato_slasher_crosscut_3",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  13,
                  20,
                  30,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HITBACK_1,
                  EFNStunAnimations.BIPED_HITBACK_AIR
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_2)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(13, 3, 4.0F, 2.0F, 1.0F), InTimeEvent.create(2.6F, YAMATO_IN, Side.BOTH)})
      );
      YAMATO_AERIALRAVE_AUTO1 = builder.nextAccessor(
         "biped/yamato/dmcyamato_aerialrave_1",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  7,
                  14,
                  15,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, EFNMurasamaAnimations.EFN_COMBO_ATTACK_DIRECTION_MODIFIER)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.76F, YAMATO_IN, Side.BOTH),
                  InTimeEvent.create(
                     0.2F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.VERTICALSTOP, 5, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.0F}))
            .newTimePair(0.0F, 0.25F)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
      );
      YAMATO_AERIALRAVE_AUTO2 = builder.nextAccessor(
         "biped/yamato/dmcyamato_aerialrave_2",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  8,
                  14,
                  15,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_2.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, EFNMurasamaAnimations.EFN_COMBO_ATTACK_DIRECTION_MODIFIER)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.83F, YAMATO_IN, Side.BOTH),
                  InTimeEvent.create(
                     0.2F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.VERTICALSTOP, 5, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.0F}))
            .newTimePair(0.0F, 0.25F)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
      );
      YAMATO_AERIALRAVE_AUTO3 = builder.nextAccessor(
         "biped/yamato/dmcyamato_aerialrave_3",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  12,
                  18,
                  40,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITBACK_1,
                  EFNStunAnimations.BIPED_HITBACK_AIR
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, EFNMurasamaAnimations.EFN_COMBO_ATTACK_DIRECTION_MODIFIER)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(12, 4, 2.0F, 2.0F, 1.0F),
                  InTimeEvent.create(0.83F, YAMATO_IN, Side.BOTH),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.VERTICALSTOP, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.4F}))
            .newTimePair(0.0F, 0.45F)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
      );
      YAMATO_ORBIT_1 = builder.nextAccessor(
         "biped/yamato/dmcyamato_orbit_1",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  7,
                  21,
                  21,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               ),
               YamatoAnimationUtils.createCustomStunPhase(
                  22,
                  28,
                  29,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.83F, YAMATO_IN, Side.BOTH),
                  InPeriodEvent.create(
                     0.15F,
                     0.45F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        float yRot = entityPatch.getYRot();
                        float yawRadians = (float)Math.toRadians(yRot);
                        float offsetX = (float)(-Math.sin(yawRadians) * 0.0);
                        float offsetZ = (float)(Math.cos(yawRadians) * 0.0);
                        double particleX = entity.getX() + offsetX;
                        double particleY = entity.getY();
                        double particleZ = entity.getZ() + offsetZ;
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              particleX,
                              particleY,
                              particleZ,
                              Double.longBitsToDouble(entity.getId()),
                              0.1F,
                              0.1F
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 1.35F}))
            .newTimePair(0.0F, 0.45F)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
      );
      YAMATO_ORBIT_2 = builder.nextAccessor(
         "biped/yamato/dmcyamato_orbit_2",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.01F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  10,
                  27,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITDOWN_0,
                  EFNStunAnimations.BIPED_HITDOWN_AIR
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_4.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(1.116F, YAMATO_IN, Side.BOTH),
                  InPeriodEvent.create(
                     0.0F,
                     0.4F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        float yRot = entityPatch.getYRot();
                        float yawRadians = (float)Math.toRadians(yRot);
                        float offsetX = (float)(-Math.sin(yawRadians) * 0.0);
                        float offsetZ = (float)(Math.cos(yawRadians) * 0.0);
                        double particleX = entity.getX() + offsetX;
                        double particleY = entity.getY();
                        double particleZ = entity.getZ() + offsetZ;
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              particleX,
                              particleY,
                              particleZ,
                              Double.longBitsToDouble(entity.getId()),
                              0.1F,
                              0.1F
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 1.0F}))
            .newTimePair(0.0F, 0.5F)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      YAMATO_KILLERBEE = builder.nextAccessor(
         "biped/yamato/dmcyamato_killerbee",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  17,
                  26,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).legR,
                  KILLERBEE,
                  EFNStunAnimations.BIPED_HITDOWN_0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               ),
               YamatoAnimationUtils.createCustomStunPhase(
                  29,
                  33,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  RAPIADSLASH,
                  EFNStunAnimations.BIPED_HITDOWN_0,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.FIXED_HEAD_ROTATION, true)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(ActionAnimationProperty.DEST_LOCATION_PROVIDER, MoveCoordFunctions.NO_DEST)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
            .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 0.45F}))
            .addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(
               StaticAnimationProperty.PLAY_SPEED_MODIFIER,
               (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 0.45F && elapsedTime < 0.5F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                     while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                     }

                     float distanceToGround = (float)org.joml.Math.max(org.joml.Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                     LivingEntity livingentity = (LivingEntity)entitypatch.getOriginal();
                     Vec3f direction = new Vec3f(1.3F, -0.4F, 0.0F);
                     OpenMatrix4f rotation = new OpenMatrix4f()
                        .rotate(-org.joml.Math.toRadians(((LivingEntity)entitypatch.getOriginal()).yBodyRotO + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F));
                     OpenMatrix4f.transform3v(rotation, direction, direction);
                     if (distanceToGround > 0.5F) {
                        livingentity.move(MoverType.SELF, direction.toDoubleVector());
                        return 0.05F;
                     } else {
                        return 1.0F * speed;
                     }
                  } else {
                     return 1.0F * speed;
                  }
               }
            )
            .addEvents(
               new AnimationEvent[]{
                  TimeEvents.createComboNodeEvent(27.0F, Yamato.Judgement_Cut),
                  InTimeEvent.create(0.55F, GROUNDSLAM, Side.SERVER),
                  InPeriodEvent.create(
                     0.15F,
                     0.45F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        float yRot = entityPatch.getYRot();
                        float yawRadians = (float)Math.toRadians(yRot);
                        float offsetX = (float)(-Math.sin(yawRadians) * 0.1);
                        float offsetZ = (float)(Math.cos(yawRadians) * 0.1);
                        double particleX = entity.getX() + offsetX;
                        double particleY = entity.getY();
                        double particleZ = entity.getZ() + offsetZ;
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              particleX,
                              particleY,
                              particleZ,
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
            .newTimePair(0.0F, 2.1474836E9F)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      YAMATO_KILLERBEE_HIT = builder.nextAccessor(
         "biped/yamato/dmcyamato_killerbee_hit",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  0,
                  1,
                  3,
                  InteractionHand.MAIN_HAND,
                  0.1F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).legR,
                  KILLERBEE,
                  EFNStunAnimations.BIPED_HITDOWN_0,
                  EFNStunAnimations.BIPED_HITUP_1
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 0.5F}))
            .newTimePair(0.0F, 0.3F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
            .newTimePair(0.0F, 0.05F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
      );
      YAMATO_UPPERSLASH = builder.nextAccessor(
         "biped/yamato/dmcyamato_upperslash",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  13,
                  19,
                  30,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITUP_2,
                  EFNStunAnimations.BIPED_HITUP_2
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(2.066F, YAMATO_IN, Side.BOTH)})
      );
      YAMATO_UPPERSLASH_HOLD = builder.nextAccessor(
         "biped/yamato/dmcyamato_upperslash_hold",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  13,
                  31,
                  32,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITUP_3,
                  EFNStunAnimations.BIPED_HITUP_3
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.95F, YAMATO_IN, Side.BOTH),
                  InPeriodEvent.create(
                     0.1F,
                     0.35F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        float yRot = entityPatch.getYRot();
                        float yawRadians = (float)Math.toRadians(yRot);
                        float offsetX = (float)(-Math.sin(yawRadians) * 0.0);
                        float offsetZ = (float)(Math.cos(yawRadians) * 0.0);
                        double particleX = entity.getX() + offsetX;
                        double particleY = entity.getY();
                        double particleZ = entity.getZ() + offsetZ;
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              particleX,
                              particleY,
                              particleZ,
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 0.65F}))
      );
      YAMATO_DRIVE = builder.nextAccessor(
         "biped/yamato/dmcyamato_drive",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  11,
                  16,
                  20,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITDOWN_0,
                  EFNStunAnimations.BIPED_HITDOWN_AIR
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.3F, GROUNDTHRUST_DRIVE, Side.SERVER),
                  InTimeEvent.create(0.766666F, YAMATO_IN, Side.BOTH),
                  shootFireballDownward(15, 1.5F)
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 1.0F}))
      );
      YAMATO_DRIVE_MOB = builder.nextAccessor(
         "biped/yamato/dmcyamato_drive_mob",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  11,
                  16,
                  20,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITDOWN_0,
                  EFNStunAnimations.BIPED_HITDOWN_AIR
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.3F, GROUNDTHRUST_DRIVE, Side.SERVER),
                  InTimeEvent.create(0.766666F, YAMATO_IN, Side.BOTH),
                  shootFireballDownward(15, 1.5F)
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 1.0F}))
      );
      YAMATO_STOMP = builder.nextAccessor(
         "biped/yamato/dmcyamato_stomp",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  21,
                  33,
                  46,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  STOMP,
                  EFNStunAnimations.BIPED_HITBACK_1,
                  EFNStunAnimations.BIPED_HITDOWN_AIR
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.createLavaRingEffect(29, 35),
                  ParticleEffectInvoker.createMagmaEruption(30.0F),
                  InTimeEvent.create(2.0F, YAMATO_IN, Side.BOTH),
                  InTimeEvent.create(0.85F, LOOPED_FALLING, Side.BOTH),
                  InTimeEvent.create(0.48F, GROUND_SLAM_LAND_A, Side.SERVER),
                  InPeriodEvent.create(
                     0.15F,
                     0.55F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.STOP_MOVEMENT, false)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.7F}))
            .newTimePair(0.0F, 0.8F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      YAMATO_AIRFLUSH = builder.nextAccessor(
         "biped/yamato/dmcyamato_aerialflush",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  13,
                  18,
                  20,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITBACK_1,
                  EFNStunAnimations.BIPED_HITBACK_AIR
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, EFNMurasamaAnimations.EFN_COMBO_ATTACK_DIRECTION_MODIFIER)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(0.99F, YAMATO_IN, Side.BOTH)})
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 1.0F}))
            .newTimePair(0.0F, 0.3F)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
      );
      YAMATO_HELMBREAKER = builder.nextAccessor(
         "biped/yamato/dmcyamato_helmbreaker",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  10,
                  24,
                  35,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITDOWN_0,
                  EFNStunAnimations.BIPED_HITDOWN_AIR
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.1F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     2.15F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EFNSounds.YAMATO_IN.get(), 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT
                  ),
                  InTimeEvent.create(0.4F, LOOPED_FALLING, Side.BOTH),
                  InPeriodEvent.create(
                     0.1F,
                     0.35F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        float yRot = entityPatch.getYRot();
                        float yawRadians = (float)Math.toRadians(yRot);
                        float offsetX = (float)(-Math.sin(yawRadians) * 0.0);
                        float offsetZ = (float)(Math.cos(yawRadians) * 0.0);
                        double particleX = entity.getX() + offsetX;
                        double particleY = entity.getY();
                        double particleZ = entity.getZ() + offsetZ;
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              particleX,
                              particleY,
                              particleZ,
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 0.02F}))
      );
      YAMATO_REPAIDSLASH = builder.nextAccessor(
         "biped/yamato/dmcyamato_rapidslash",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  10,
                  35,
                  43,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  RAPIADSLASH,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               ),
               YamatoAnimationUtils.createCustomStunPhase(
                  44,
                  50,
                  63,
                  InteractionHand.MAIN_HAND,
                  1.5F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  RAPIADSLASH_END,
                  EFNStunAnimations.BIPED_HITUP_2,
                  EFNStunAnimations.BIPED_HITUP_1
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_ROD.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, false)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
            .newTimePair(0.0F, 0.3F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .newTimePair(0.25F, 0.9F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(10, 30, 2.0F, 2.0F, 1.0F),
                  AvalonEventUtils.simpleCameraShake(44, 4, 3.0F, 2.0F, 3.0F),
                  InTimeEvent.create(
                     0.73F,
                     (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1.0F, 0.0F, 0.0F),
                     Side.LOCAL_CLIENT
                  ),
                  InTimeEvent.create(2.5F, YAMATO_IN, Side.BOTH),
                  InPeriodEvent.create(
                     0.1F,
                     0.55F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        float yRot = entityPatch.getYRot();
                        float yawRadians = (float)Math.toRadians(yRot);
                        float offsetX = (float)(-Math.sin(yawRadians) * 0.3);
                        float offsetZ = (float)(Math.cos(yawRadians) * 0.3);
                        double particleX = entity.getX() + offsetX;
                        double particleY = entity.getY();
                        double particleZ = entity.getZ() + offsetZ;
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              particleX,
                              particleY,
                              particleZ,
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      YAMATO_REPAIDSLASH_MOB = builder.nextAccessor(
         "biped/yamato/dmcyamato_rapidslash_mob",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  10,
                  45,
                  63,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  RAPIADSLASH,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_ROD.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, false)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.73F,
                     (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1.0F, 0.0F, 0.0F),
                     Side.LOCAL_CLIENT
                  ),
                  InTimeEvent.create(2.5F, YAMATO_IN, Side.BOTH),
                  InPeriodEvent.create(
                     0.1F,
                     0.55F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        float yRot = entityPatch.getYRot();
                        float yawRadians = (float)Math.toRadians(yRot);
                        float offsetX = (float)(-Math.sin(yawRadians) * 0.3);
                        float offsetZ = (float)(Math.cos(yawRadians) * 0.3);
                        double particleX = entity.getX() + offsetX;
                        double particleY = entity.getY();
                        double particleZ = entity.getZ() + offsetZ;
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              particleX,
                              particleY,
                              particleZ,
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      YAMATO_FLARECUT = builder.nextAccessor(
         "biped/yamato/dmcyamato_flare_cut",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  22,
                  28,
                  28,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  ColliderPreset.GREATSWORD,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               ),
               YamatoAnimationUtils.createCustomStunPhase(
                  28,
                  37,
                  38,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  ColliderPreset.GREATSWORD,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               ),
               YamatoAnimationUtils.createCustomStunPhase(
                  43,
                  53,
                  60,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  ColliderPreset.GREATSWORD,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_ROD.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.36F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1.0F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(3.46F, YAMATO_IN, Side.BOTH)
               }
            )
      );
      YAMATO_FLARECUT_RISING = builder.nextAccessor(
         "biped/yamato/dmcyamato_flare_just",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  13,
                  22,
                  30,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITUP_2,
                  EFNStunAnimations.BIPED_HITUP_2
               ),
               YamatoAnimationUtils.createCustomStunPhase(
                  31,
                  41,
                  54,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  AERIALRAVE,
                  EFNStunAnimations.BIPED_HITUP_2,
                  EFNStunAnimations.BIPED_HITUP_2
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_ROD.get())
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.22F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1.0F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(1.4F, YAMATO_IN, Side.BOTH),
                  InPeriodEvent.create(
                     0.1F,
                     0.6F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        float yRot = entityPatch.getYRot();
                        float yawRadians = (float)Math.toRadians(yRot);
                        float offsetX = (float)(-Math.sin(yawRadians) * 0.0);
                        float offsetZ = (float)(Math.cos(yawRadians) * 0.0);
                        double particleX = entity.getX() + offsetX;
                        double particleY = entity.getY();
                        double particleZ = entity.getZ() + offsetZ;
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE_SHORT.get(),
                              particleX,
                              particleY,
                              particleZ,
                              Double.longBitsToDouble(entity.getId()),
                              0.1F,
                              0.1F
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 1.4F}))
            .newTimePair(0.0F, 0.5F)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
            .newTimePair(0.0F, 2.1474836E9F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      YAMATO_FLARECUT_REPAID = builder.nextAccessor(
         "biped/yamato/dmcyamato_flare_rapid",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.05F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(
                  47, 56, 64, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, AERIALRAVE
               ),
               AvalonAnimationUtils.createSimplePhase(
                  65, 75, 88, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, AERIALRAVE
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_ROD.get())
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.78F, (entitypatch, self, params) -> entitypatch.playSound((SoundEvent)EpicFightSounds.WHOOSH_SHARP.get(), 1.0F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(1.86666F, YAMATO_IN, Side.BOTH)
               }
            )
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.78F, 1.86666F}))
      );
      YAMATO_VOLCANOL_ALL = builder.nextAccessor(
         "biped/yamato/dmcyamato_volcano",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(
                  60, 66, 67, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, RAPIADSLASH_END
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(61, 4, 2.0F, 2.0F, 1.0F),
                  AvalonEventUtils.simpleGroundSplit(61, 0.5, 0.0, 0.0, 0.0, 3.5F, true),
                  InTimeEvent.create(2.383F, YAMATO_IN, Side.BOTH),
                  InTimeEvent.create(
                     0.01F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT
                  ),
                  InTimeEvent.create(1.0F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT)
               }
            )
      );
      YAMATO_VOLCANOL = builder.nextAccessor(
         "biped/yamato/dmcyamato_volcano_cut",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  52,
                  58,
                  66,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  VOLCANOL,
                  EFNStunAnimations.BIPED_HITUP_2,
                  EFNStunAnimations.BIPED_HITUP_2
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(52, 4, 2.0F, 2.0F, 1.0F),
                  ParticleEffectInvoker.simpleGroundSplit(52, 0.5, 0.0, 0.0, 0.0, 3.5F, true),
                  ParticleEffectInvoker.createLavaRingEffect(5, 30),
                  ParticleEffectInvoker.createMagmaEruption(52.0F),
                  InTimeEvent.create(2.25F, YAMATO_IN, Side.BOTH),
                  InTimeEvent.create(0.01F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT),
                  InTimeEvent.create(0.2F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.FIRE_AMBIENT, 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT),
                  InTimeEvent.create(0.866F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT),
                  InTimeEvent.create(0.866F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT)
               }
            )
      );
      YAMATO_VOLCANOL_CHARGE = builder.nextAccessor(
         "biped/yamato/dmcyamato_volcano_hold",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               YamatoAnimationUtils.createCustomStunPhase(
                  16,
                  22,
                  23,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  2.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  VOLCANOL,
                  EFNStunAnimations.BIPED_HITUP_3,
                  EFNStunAnimations.BIPED_HITUP_3
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.26F,
                     (entitypatch, self, params) -> {
                        if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                           LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                           ClientLevel level = (ClientLevel)attacker.level();
                           OpenMatrix4f transformMatrix = entitypatch.getArmature()
                              .getBoundTransformFor(entitypatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).rootJoint);
                           OpenMatrix4f.mul(
                              new OpenMatrix4f().rotate(-((float)Math.toRadians(attacker.yBodyRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F)),
                              transformMatrix,
                              transformMatrix
                           );
                           transformMatrix.translate(new Vec3f(0.0F, 0.3F, -0.5F));
                           float intensity = 1.0F;
                           int coneParticles = (int)(150.0F * intensity);
                           double r = 0.5 * intensity;
                           double t = 0.006;

                           for (int group = 0; group < 2; group++) {
                              float angle = group == 0 ? 110.0F : 70.0F;

                              for (int i = 0; i < coneParticles; i++) {
                                 double theta = (Math.PI * 2) * Math.random();
                                 double phi = (Math.random() - 0.2) * Math.PI * t / r;
                                 Vec3f direction = new Vec3f(
                                    (float)(r * Math.cos(phi) * Math.cos(theta)),
                                    (float)(r * Math.cos(phi) * Math.sin(theta)) * 1.5F,
                                    (float)(r * Math.sin(phi))
                                 );
                                 OpenMatrix4f rotation = new OpenMatrix4f()
                                    .rotate((float)Math.toRadians(-attacker.yBodyRotO + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F))
                                    .rotate((float)Math.toRadians(angle), new Vec3f(1.0F, 0.0F, 0.0F));
                                 OpenMatrix4f.transform3v(rotation, direction, direction);
                                 float speedVariation = 0.3F + 0.3F * (float)Math.random();
                                 direction.scale(speedVariation);
                                 ParticleOptions particle;
                                 if (Math.random() < 0.7) {
                                    particle = ParticleTypes.FLAME;
                                 } else if (Math.random() < 0.9) {
                                    particle = ParticleTypes.SMALL_FLAME;
                                 } else {
                                    particle = ParticleTypes.END_ROD;
                                 }

                                 level.addParticle(
                                    particle,
                                    transformMatrix.m30 + attacker.getX(),
                                    transformMatrix.m31 + attacker.getY() + 0.7F,
                                    transformMatrix.m32 + attacker.getZ(),
                                    direction.x * 1.3F,
                                    direction.y * 1.5F,
                                    direction.z * 1.3F
                                 );
                                 if (Math.random() < 0.2F) {
                                    Vec3f lavaDir = new Vec3f(direction.x, direction.y, direction.z).scale(0.7F);
                                    level.addParticle(
                                       ParticleTypes.LANDING_LAVA,
                                       transformMatrix.m30 + attacker.getX(),
                                       transformMatrix.m31 + attacker.getY(),
                                       transformMatrix.m32 + attacker.getZ(),
                                       lavaDir.x,
                                       lavaDir.y * 1.4F,
                                       lavaDir.z
                                    );
                                 }
                              }
                           }
                        }
                     },
                     Side.CLIENT
                  ),
                  AvalonEventUtils.simpleCameraShake(16, 4, 2.0F, 2.0F, 1.0F),
                  ParticleEffectInvoker.createLavaRingEffect(0, 8),
                  ParticleEffectInvoker.createMagmaEruption(16.0F),
                  ParticleEffectInvoker.simpleGroundSplit(17, 0.5, 0.0, 0.0, 0.0, 3.5F, true),
                  InTimeEvent.create(1.65F, YAMATO_IN, Side.BOTH),
                  InTimeEvent.create(0.26F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT),
                  InTimeEvent.create(0.26F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 0.0F, 0.0F), Side.LOCAL_CLIENT)
               }
            )
            .newTimePair(0.0F, 2.1474836E9F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      YAMATO_JUDEMENCUT = builder.nextAccessor(
         "biped/yamato/dmcyamato_judgementcut",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(
                  5, 13, 19, InteractionHand.MAIN_HAND, 0.5F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, JUDGEMENT_CUT
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(5, 4, 2.0F, 2.0F, 1.0F),
                  ParticleEffectInvoker.spawnSlashParticleSmart(6),
                  InTimeEvent.create(2.783F, YAMATO_IN, Side.BOTH),
                  summonJudgementCutNormal(3, 1.0F, 3.0F)
               }
            )
      );
      YAMATO_JUDEMENCUT_CHARGE = builder.nextAccessor(
         "biped/yamato/dmcyamato_judgementcut_charge",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(
                  5, 13, 19, InteractionHand.MAIN_HAND, 1.7F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, JUDGEMENT_CUT
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(5, 4, 2.0F, 2.0F, 1.0F),
                  ParticleEffectInvoker.spawnSlashParticleSmart(6),
                  InTimeEvent.create(2.783F, YAMATO_IN, Side.BOTH),
                  summonJudgementCutNormal(3, 1.0F, 4.0F)
               }
            )
      );
      YAMATO_JUDEMENCUT_JUST = builder.nextAccessor(
         "biped/yamato/dmcyamato_judgementcut_just",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(
                  5, 13, 19, InteractionHand.MAIN_HAND, 3.8F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, JUDGEMENT_CUT
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(5, 4, 2.0F, 2.0F, 1.0F),
                  ParticleEffectInvoker.spawnSlashParticleSmart(6),
                  InTimeEvent.create(2.783F, YAMATO_IN, Side.BOTH),
                  summonJudgementCutPerfect(3, 1.0F, 3.0F)
               }
            )
            .newTimePair(0.0F, 2.1474836E9F)
            .addState(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
      );
      YAMATO_JUDEMENCUT_JUST_MOB = builder.nextAccessor(
         "biped/yamato/dmcyamato_judgementcut_just_mob",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(
                  5, 13, 19, InteractionHand.MAIN_HAND, 3.8F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, JUDGEMENT_CUT
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(5, 4, 2.0F, 2.0F, 1.0F),
                  ParticleEffectInvoker.spawnSlashParticleSmart(6),
                  InTimeEvent.create(2.783F, YAMATO_IN, Side.BOTH),
                  summonJudgementCutPerfect(3, 1.0F, 3.0F)
               }
            )
      );
      YAMATO_JUDEMENCUT_ALL = builder.nextAccessor(
         "biped/yamato/dmcyamato_judgementcut_all",
         accessor -> (YamatoAttackAnimation)new YamatoAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(
                  633, 641, 661, InteractionHand.MAIN_HAND, 1.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, JUDGEMENT_CUT
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EFNParticles.YAMATO_HIT_1)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.spawnFlashParticle(6, -0.52, -0.27, -0.7),
                  AvalonEventUtils.simpleCameraShake(633, 3, 4.0F, 2.0F, 1.0F),
                  ParticleEffectInvoker.spawnSlashParticleSmart(633),
                  InTimeEvent.create(13.25F, YAMATO_IN, Side.BOTH),
                  summonJudgementCutNormal(630, 1.0F, 3.0F)
               }
            )
            .newTimePair(0.0F, 10.0F)
            .addStateRemoveOld(EntityState.PHASE_LEVEL, 3)
      );
   }

   private static Vec3 calculateHorizontalOffset(LivingEntity entity, float forward, float side) {
      float yRotRad = (float)Math.toRadians(entity.getYRot());
      float xOffset = -forward * (float)Math.sin(yRotRad) + side * (float)Math.cos(yRotRad);
      float zOffset = forward * (float)Math.cos(yRotRad) + side * (float)Math.sin(yRotRad);
      return new Vec3(xOffset, 0.0, zOffset);
   }

   public static Vec3 getfloor(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends StaticAnimation> self, Vec3f WeaponOffset, Joint joint) {
      float dpx = WeaponOffset.x + (float)((LivingEntity)entitypatch.getOriginal()).getX();
      float dpy = WeaponOffset.y + (float)((LivingEntity)entitypatch.getOriginal()).getY();
      float dpz = WeaponOffset.z + (float)((LivingEntity)entitypatch.getOriginal()).getZ();
      if (joint != null) {
         OpenMatrix4f transformMatrix = entitypatch.getArmature().getBoundTransformFor(entitypatch.getAnimator().getPose(1.0F), joint);
         transformMatrix.translate(WeaponOffset);
         OpenMatrix4f CORRECTION = new OpenMatrix4f()
            .rotate(-((float)Math.toRadians(((LivingEntity)entitypatch.getOriginal()).getYRot() + 180.0F)), new Vec3f(0.0F, 0.0F, 0.0F));
         OpenMatrix4f.mul(CORRECTION, transformMatrix, transformMatrix);
         dpx = transformMatrix.m30 + (float)((LivingEntity)entitypatch.getOriginal()).getX();
         dpy = transformMatrix.m31 + (float)((LivingEntity)entitypatch.getOriginal()).getY();
         dpz = transformMatrix.m32 + (float)((LivingEntity)entitypatch.getOriginal()).getZ();
      }

      MutableBlockPos pos = new MutableBlockPos(dpx, dpy, dpz);

      while (true) {
         BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(pos);
         if (!(block.getBlock() instanceof BushBlock) && !block.isAir() || block.is(Blocks.VOID_AIR) || dpy <= -64.0F) {
            return new Vec3(dpx, dpy, dpz);
         }

         pos.setY(pos.getY() - 1);
         dpy--;
      }
   }

   private static void spawnTriangularDirectionalParticles(Level level, double x, double y, double z, float yRot, int count, float radius) {
      Random random = new Random();

      for (int i = 0; i < count; i++) {
         int triangleSide = i % 3;
         double angleOffset = triangleSide * (Math.PI * 2.0 / 3.0);
         double distanceAlongEdge = random.nextDouble();
         double theta = angleOffset;
         double phi = (random.nextDouble() - 0.5) * 0.2;
         double dx = radius * Math.cos(phi) * Math.cos(theta) * distanceAlongEdge;
         double dy = radius * Math.cos(phi) * Math.sin(theta) * distanceAlongEdge;
         double dz = radius * Math.sin(phi);
         Vec3f direction = new Vec3f((float)dx, (float)dy, (float)dz);
         OpenMatrix4f rotation = new OpenMatrix4f().rotate((float)(-Math.toRadians(yRot + 90.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
         OpenMatrix4f.transform3v(rotation, direction, direction);
         level.addParticle(ParticleTypes.ENCHANT, x + direction.x, y + direction.y, z + direction.z, direction.x / 3.0F, direction.y / 3.0F, direction.z / 3.0F);
      }
   }

   private static void spawnSphericalParticles(Level level, double x, double y, double z, int count, float radius) {
      Random random = new Random();

      for (int i = 0; i < count; i++) {
         double theta = (Math.PI * 2) * random.nextDouble();
         double phi = Math.acos(2.0 * random.nextDouble() - 1.0);
         double dx = radius * Math.sin(phi) * Math.cos(theta);
         double dy = radius * Math.sin(phi) * Math.sin(theta);
         double dz = radius * Math.cos(phi);
         level.addParticle(ParticleTypes.ENCHANT, x + dx, y + dy, z + dz, dx * 0.015, dy * 0.015, dz * 0.015);
      }
   }
}
