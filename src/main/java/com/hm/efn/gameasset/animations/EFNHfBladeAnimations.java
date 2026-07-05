package com.hm.efn.gameasset.animations;

import com.guhao.vix.camera.CameraEvents;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.animations.types.broadblade.BroadBladeAttackAnimation;
import com.hm.efn.animations.types.murasama.MurasamaAnimationUtils;
import com.hm.efn.animations.types.murasama.MurasamaAttackAnimation;
import com.hm.efn.animations.types.murasama.MurasamaLivingAnimation;
import com.hm.efn.animations.types.murasama.MurasamaMovementAnimation;
import com.hm.efn.client.effek.BreakOutEffek_B;
import com.hm.efn.client.effek.BurstBlueEffek;
import com.hm.efn.client.effek.ChargingEffek;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.GeoVFXEntity;
import com.hm.efn.entity.geoEntity.HfBladeCharging;
import com.hm.efn.entity.geoEntity.HfBladeSlash;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.mixin.NearestAttackableTargetGoalMixin;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffekUnits;
import com.hm.efn.util.ItemStackData;
import com.hm.efn.util.ParticleEffectInvoker;
import com.hm.efn.util.TeleportGroundUtils;
import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.Event;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.PoseModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordGetter;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordSetter;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class EFNHfBladeAnimations {
   public static final Collider HF_BLADE_XXX_KICK_HITBOX = new MultiOBBCollider(3, 0.95, 0.95, 1.0, 0.0, 0.8, -0.7);
   public static final Collider HF_BLADE_KICK_Y_ELBOW_HITBOX = new MultiOBBCollider(3, 0.75, 0.75, 0.8, 0.0, 0.8, -0.5);
   public static final Collider HF_BLADE_KICK_Y_KICK_HITBOX = new MultiOBBCollider(3, 0.65, 0.8, 0.9, 0.0, 1.0, -0.6);
   public static final Collider HF_BLADE_Y_CHARGE_DASH_HITBOX = new MultiOBBCollider(3, 1.3, 1.3, 2.0, 0.0, 1.0, -1.5);
   public static final Collider HF_BLADE_Y_CHARGE_SLASH_HITBOX = new MultiOBBCollider(3, 1.5, 1.5, 1.5, 0.0, 1.0, 0.0);
   public static final Collider HF_BLADE_AIR_Y_CHARGE_DASH_HITBOX = new MultiOBBCollider(3, 1.5, 1.5, 2.0, 0.0, 1.0, -1.0);
   public static final Collider HF_BLADE_AIR_Y_CHARGE_SLASH_HITBOX = new MultiOBBCollider(3, 2.0, 2.0, 2.0, 0.0, 1.0, 0.0);
   public static final Collider HF_BLADE_LONG_HITBOX = new MultiOBBCollider(3, 0.8, 0.8, 1.5, 0.0, 0.0, -0.95);
   public static final Event BLADE_IN = (entitypatch, self, params) -> {
      if (entitypatch != null && entitypatch.getOriginal() != null) {
         if (entitypatch.isLogicalClient()) {
            entitypatch.playSound((SoundEvent)EpicFightSounds.SWORD_IN.get(), 1.0F, 0.0F, 0.0F);
         }
      }
   };
   public static final PoseModifier EFN_COMBO_ATTACK_DIRECTION_MODIFIER = (self, pose, entitypatch, time, partialTicks) -> {
      if (self.isStaticAnimation() && !(entitypatch instanceof PlayerPatch<?> playerpatch && playerpatch.isFirstPerson())) {
         float pitch = entitypatch.getAttackDirectionPitch(partialTicks);
         pitch = Math.min(30.0F, Math.max(-30.0F, pitch));
         float followFactor = 0.6F;
         float adjustedPitch = pitch * followFactor;
         JointTransform chest = pose.orElseEmpty("Chest");
         chest.frontResult(JointTransform.rotation(QuaternionUtils.XP.rotationDegrees(-adjustedPitch)), OpenMatrix4f::mulAsOriginInverse);
         if (entitypatch instanceof PlayerPatch) {
            float xRot = MathUtils.lerpBetween(
               ((LivingEntity)entitypatch.getOriginal()).xRotO, ((LivingEntity)entitypatch.getOriginal()).getXRot(), partialTicks
            );
            float limitedXRot = Math.min(20.0F, Math.max(-20.0F, xRot));
            float headFollowFactor = 0.3F;
            float headPitch = (adjustedPitch + limitedXRot) * headFollowFactor;
            OpenMatrix4f toOriginalRotation = entitypatch.getArmature()
               .getBoundTransformFor(pose, entitypatch.getArmature().searchJointByName("Head"))
               .removeScale()
               .removeTranslation()
               .invert();
            Vec3f xAxis = OpenMatrix4f.transform3v(toOriginalRotation, Vec3f.X_AXIS, null);
            OpenMatrix4f headRotation = OpenMatrix4f.createRotatorDeg(-headPitch, xAxis);
            pose.orElseEmpty("Head").frontResult(JointTransform.fromMatrix(headRotation), OpenMatrix4f::mul);
         }
      }
   };
   public static final MoveCoordSetter EFN_RAW_COORD_WITH_X_ROT = (self, entitypatch, transformSheet) -> {
      TransformSheet sheet = self.getCoord().copyAll();
      float xRot = ((LivingEntity)entitypatch.getOriginal()).getXRot();
      float clampedXRot = Mth.clamp(xRot, -60.0F, 30.0F);

      for (Keyframe keyframe : sheet.getKeyframes()) {
         keyframe.transform().translation().rotate(-clampedXRot, Vec3f.X_AXIS);
      }

      transformSheet.readFrom(sheet);
   };
   public static final PoseModifier EFN_ROOT_X_MODIFIER = (self, pose, entitypatch, time, partialTicks) -> {
      float pitch = -((LivingEntity)entitypatch.getOriginal()).getXRot();
      float clampedPitch = Mth.clamp(pitch, -90.0F, 30.0F);
      JointTransform chest = pose.orElseEmpty("Root");
      chest.frontResult(JointTransform.rotation(QuaternionUtils.XP.rotationDegrees(-clampedPitch)), OpenMatrix4f::mulAsOriginInverse);
   };
   public static AnimationAccessor<MurasamaLivingAnimation> HF_BLADE_IDLE_COMBAT;
   public static AnimationAccessor<StaticAnimation> HF_BLADE_IDLE_SHEATH;
   public static AnimationAccessor<MurasamaLivingAnimation> HF_BLADE_IDLE_AIR;
   public static AnimationAccessor<MurasamaLivingAnimation> HF_BLADE_SWORD_OUT;
   public static AnimationAccessor<MurasamaLivingAnimation> HF_BLADE_SHEATH_IN;
   public static AnimationAccessor<MurasamaLivingAnimation> HF_BLADE_SHEATH_IN_RUN;
   public static AnimationAccessor<MurasamaLivingAnimation> HF_BLADE_GUARD;
   public static AnimationAccessor<MurasamaLivingAnimation> HF_BLADE_GUARD_DASH;
   public static AnimationAccessor<GuardAnimation> HF_BLADE_GUARD_HIT;
   public static AnimationAccessor<StaticAnimation> HF_BLADE_JUMP_FIRST;
   public static AnimationAccessor<StaticAnimation> HF_BLADE_FALL_FIRST;
   public static AnimationAccessor<ActionAnimation> HF_BLADE_JUMP_SECOND;
   public static AnimationAccessor<ActionAnimation> HF_BLADE_TAUNT;
   public static AnimationAccessor<MurasamaMovementAnimation> HF_BLADE_WALK_COMBAT;
   public static AnimationAccessor<AvalonMovementAnimation> HF_BLADE_WALK_SHEATH;
   public static AnimationAccessor<MurasamaMovementAnimation> HF_BLADE_RUN_COMBAT_1;
   public static AnimationAccessor<MurasamaMovementAnimation> HF_BLADE_RUN_COMBAT_2;
   public static AnimationAccessor<AvalonMovementAnimation> HF_BLADE_RUN_SHEATH;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_COUNTER;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_DASH_Y;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_DASH_Y_SP;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_KICK_Y;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_Y;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_Y_CHARGE;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_Y_CHARGE_THROUGH;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_DASH_X;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_X;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XY;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XY_CHARGE;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XX;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XXY;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XXY_CHARGE;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XXX;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XXXY;
   public static final Event BLADE_OUT = (entitypatch, self, params) -> {
      if (entitypatch != null && entitypatch.getOriginal() != null) {
         float randomPitch = 0.8F + (float)Math.random() * 0.4F;
         float randomVolume = 2.0F + (float)Math.random() * 0.5F;
         entitypatch.playSound(SoundEvents.FIREWORK_ROCKET_BLAST, randomVolume, randomPitch, 1.0F);
         if (!(
            !EffekUnits.VFXENABLE()
               | !(Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()
               | entitypatch.getAnimator().getPlayerFor(null).getAnimation().get() == HF_BLADE_DASH_Y.get()
               | entitypatch.getAnimator().getPlayerFor(null).getAnimation().get() == HF_BLADE_Y.get()
               | entitypatch.getAnimator().getPlayerFor(null).getAnimation().get() == HF_BLADE_XY.get()
               | entitypatch.getAnimator().getPlayerFor(null).getAnimation().get() == HF_BLADE_XXY.get()
               | entitypatch.getAnimator().getPlayerFor(null).getAnimation().get() == HF_BLADE_XXXY.get()
         )) {
            Random random = new Random();
            BurstBlueEffek.playBurstRed(
               BurstBlueEffek.Type.LEVEL1,
               entitypatch.getOriginal().level(),
               entitypatch.getOriginal().position().x(),
               entitypatch.getOriginal().position().y() + 0.45,
               entitypatch.getOriginal().position().z(),
               (float)random.nextDouble(-Math.PI, Math.PI),
               EffekUnits.getRY(entitypatch),
               0.0F,
               1.0F
            );
         }
      }
   };
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XXXY_CHARGE;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XXXX;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_X_AIR;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_XX_AIR;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_Y_AIR;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_Y_CHARGE_AIR;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_ZANDATSU;
   public static AnimationAccessor<MurasamaAttackAnimation> HF_BLADE_ZANDATSU_AIR;
   public static final MoveCoordGetter BLADE_MODEL_COORD = (animation, entitypatch, coord, prevElapsedTime, elapsedTime) -> {
      LivingEntity livingentity = (LivingEntity)entitypatch.getOriginal();
      JointTransform oJt = coord.getInterpolatedTransform(prevElapsedTime);
      JointTransform jt = coord.getInterpolatedTransform(elapsedTime);
      Vec4f prevpos = new Vec4f(oJt.translation());
      Vec4f currentpos = new Vec4f(jt.translation());
      OpenMatrix4f rotationTransform = entitypatch.getModelMatrix(1.0F).removeTranslation().removeScale();
      OpenMatrix4f localTransform = entitypatch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
      rotationTransform.mulBack(localTransform);
      currentpos.transform(rotationTransform);
      prevpos.transform(rotationTransform);
      boolean hasNoGravity = ((LivingEntity)entitypatch.getOriginal()).isNoGravity();
      boolean moveVertical = animation.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false)
         || animation.getProperty(ActionAnimationProperty.COORD).isPresent();
      float dx = prevpos.x - currentpos.x;
      float dy = !moveVertical && !hasNoGravity ? 0.0F : currentpos.y - prevpos.y;
      float dz = prevpos.z - currentpos.z;
      dx = Math.abs(dx) > 1.0E-4F ? dx : 0.0F;
      dz = Math.abs(dz) > 1.0E-4F ? dz : 0.0F;
      BlockPos blockpos = new MutableBlockPos(livingentity.getX(), livingentity.getBoundingBox().minY - 1.0, livingentity.getZ());
      BlockState blockState = livingentity.level().getBlockState(blockpos);
      AttributeInstance movementSpeed = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
      boolean soulboost = blockState.is(BlockTags.SOUL_SPEED_BLOCKS) && EFNEnchantment.getLevel(livingentity.getItemBySlot(EquipmentSlot.FEET), Enchantments.SOUL_SPEED) > 0;
      float speedFactor = (float)(soulboost ? 1.0 : livingentity.level().getBlockState(blockpos).getBlock().getSpeedFactor());
      float moveMultiplier = (float)(
         animation.getProperty(ActionAnimationProperty.AFFECT_SPEED).orElse(false) ? movementSpeed.getValue() / movementSpeed.getBaseValue() : 1.0
      );
      return new Vec3f(dx * moveMultiplier * speedFactor * 1.1F, dy, dz * moveMultiplier * speedFactor * 1.1F);
   };

   public static SimpleEvent<?> MurasamaSheathEvent(boolean isSheath, boolean updateMotion) {
      return SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> {
         if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skill = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
            if (skill != null) {
               SkillDataManager dataManager = skill.getDataManager();
               if (dataManager.hasData(EFNSKillDataKeys.MURASAMA_SHEATH)) {
                  dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_SHEATH, isSheath);
                  if (updateMotion) {
                     serverPlayerPatch.modifyLivingMotionByCurrentItem(false);
                  }
               }
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent setMurasamaSheathMesh(float triggerTime, boolean isSheath) {
      return InTimeEvent.create(triggerTime, (entityPatch, self, params) -> {
         CapabilityItem mainHandCap = entityPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
         if (!mainHandCap.isEmpty()) {
            ItemStack mainHandItem = ((LivingEntity)entityPatch.getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
            ItemStackData.putInt(mainHandItem, "murasama_sheath", isSheath ? 1 : 0);
         }
      }, Side.BOTH);
   }

   public static SimpleEvent setMurasamaSheathMesh(boolean isSheath) {
      return SimpleEvent.create((entityPatch, self, params) -> {
         CapabilityItem mainHandCap = entityPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
         if (!mainHandCap.isEmpty()) {
            ItemStack mainHandItem = ((LivingEntity)entityPatch.getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
            ItemStackData.putInt(mainHandItem, "murasama_sheath", isSheath ? 1 : 0);
         }
      }, Side.BOTH);
   }

   public static InPeriodEvent createTauntEffect(int startFrame, int endFrame, float radius, boolean teamProtect) {
      float startTime = startFrame / 60.0F;
      float endTime = endFrame / 60.0F;
      return InPeriodEvent.create(startTime, endTime, (entityPatch, self, params) -> {
         if (!((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
            LivingEntity source = (LivingEntity)entityPatch.getOriginal();
            if (hasMurasamakill(source)) {
               return;
            }

            ServerLevel level = (ServerLevel)source.level();
            Vec3 center = source.position();
            tauntEntitiesInArea(level, center, source, radius, teamProtect);
         }
      }, Side.SERVER);
   }

   private static void tauntEntitiesInArea(ServerLevel level, Vec3 center, LivingEntity source, float radius, boolean teamProtect) {
      AABB area = new AABB(
         center.x() - radius,
         center.y() - radius,
         center.z() - radius,
         center.x() + radius,
         center.y() + radius,
         center.z() + radius
      );

      for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, entityx -> {
         if (entityx.isAlive() && entityx != source) {
            double distanceSq = entityx.distanceToSqr(center);
            if (distanceSq > radius * radius) {
               return false;
            }

            if (teamProtect) {
               if (entityx.getType().getCategory() == source.getType().getCategory()) {
                  return false;
               }
            } else if (entityx.getType() == source.getType()) {
               return false;
            }

            return canBeTaunted(entityx, source);
         } else {
            return false;
         }
      })) {
         applyTauntEffect(entity, source);
      }
   }

   private static boolean canBeTaunted(LivingEntity target, LivingEntity source) {
      if (!target.isAlive() || target == source) {
         return false;
      }

      if (target instanceof Enemy) {
         return true;
      }

      if (target instanceof NeutralMob) {
         return true;
      }

      if (target instanceof IronGolem) {
         return true;
      }

      if (!(target instanceof Wolf wolf)) {
         if (target.getType().getCategory() == MobCategory.MONSTER) {
            return true;
         } else {
            return hasAttackCapability(target) ? true : isAlreadyHostile(target, source);
         }
      } else {
         return wolf.isAngry() && wolf.getTarget() != source;
      }
   }

   private static boolean hasAttackCapability(LivingEntity target) {
      if (target instanceof Mob mob) {
         if (!mob.targetSelector.getAvailableGoals().isEmpty()) {
            return true;
         }

         if (!mob.goalSelector.getAvailableGoals().isEmpty()) {
            for (WrappedGoal goal : mob.goalSelector.getAvailableGoals()) {
               if (goal.getGoal() instanceof MeleeAttackGoal || goal.getGoal() instanceof RangedAttackGoal) {
                  return true;
               }
            }
         }
      }

      if (target.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
         double attackDamage = target.getAttributeValue(Attributes.ATTACK_DAMAGE);
         if (attackDamage > 0.0) {
            return true;
         }
      }

      return false;
   }

   private static boolean isAlreadyHostile(LivingEntity target, LivingEntity source) {
      if (target instanceof Mob mob && mob.getTarget() == source) {
         return true;
      } else {
         LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
         if (targetPatch != null && targetPatch.getTarget() == source) {
            return true;
         }

         if (target.getLastHurtByMob() == source) {
            return true;
         }

         if (source instanceof Player player) {
            PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
            if (playerPatch != null && playerPatch.getTarget() == target) {
               return true;
            }
         }

         return false;
      }
   }

   private static void applyTauntEffect(LivingEntity target, LivingEntity source) {
      if (!hasMurasamakill(source)) {
         if (target instanceof Mob mob) {
            mob.setTarget(source);
            if (mob instanceof NeutralMob neutralMob) {
               if (neutralMob instanceof Wolf wolf) {
                  wolf.setTarget(source);
               }

               neutralMob.setTarget(source);
            }
         }

         MobEffectInstance vulnerabilityEffect = new MobEffectInstance(EFNMobEffectRegistry.VULNERABILITY, 400, 1, false, false, true);
         target.addEffect(vulnerabilityEffect);
         MobEffectInstance powerEffect = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 1, false, false, true);
         target.addEffect(powerEffect);
         if (target.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
               ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(), 2, 0.3, 0.3, 0.3, 0.05
            );
         }
      }
   }

   public static InTimeEvent createInstantTauntEffect(int frame, float radius, boolean teamProtect) {
      float time = frame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         if (!((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
            LivingEntity source = (LivingEntity)entityPatch.getOriginal();
            ServerLevel level = (ServerLevel)source.level();
            Vec3 center = source.position();
            tauntEntitiesInArea(level, center, source, radius, teamProtect);
         }
      }, Side.SERVER);
   }

   public static boolean hasMurasamakill(LivingEntity entity) {
      if (!(entity instanceof Player)) {
         return true;
      }

      PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
      return playerPatch == null || playerPatch.getSkill(HfBlade.HfBlade) == null;
   }

   public static InTimeEvent invokeMurasamaCharging(int startFrame, float scale, double xOffset, double yOffset, double zOffset, float yRotOffset) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(
         time,
         (entityPatch, self, params) -> {
            if (!((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
               LivingEntity owner = (LivingEntity)entityPatch.getOriginal();
               ServerLevel level = (ServerLevel)owner.level();
               Vec3 spawnPosition = calculateSpawnPosition(owner, xOffset, yOffset, zOffset);
               float finalYRot = entityPatch.getYRot() + yRotOffset;
               HfBladeCharging charging = new HfBladeCharging(
                  (EntityType<? extends PathfinderMob>)EFNEntity.HF_BLADE_CHARGING.get(),
                  level,
                  spawnPosition.x,
                  spawnPosition.y,
                  spawnPosition.z
               );
               charging.setOwner(owner);
               charging.setScale(scale);
               charging.setXRotOffset(0.0F);
               charging.setYRotOffset(yRotOffset);
               charging.setZRotOffset(0.0F);
               charging.setStartYRot(finalYRot);
               charging.setYRot(finalYRot);
               charging.setYBodyRot(finalYRot);
               charging.setYHeadRot(finalYRot);
               charging.setXRot(0.0F);
               level.addFreshEntity(charging);
            }
         },
         Side.SERVER
      );
   }

   public static InTimeEvent invokeMurasamaSlash(int startFrame, float scale, float damage, double xOffset, double yOffset, double zOffset, float yRotOffset) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(
         time,
         (entityPatch, self, params) -> {
            LivingEntity owner = (LivingEntity)entityPatch.getOriginal();
            Vec3 spawnPosition = calculateSpawnPosition(owner, xOffset, yOffset, zOffset);
            float finalYRot = owner.getYRot() + yRotOffset;
            HfBladeSlash slash = new HfBladeSlash(
               (EntityType<? extends GeoVFXEntity>)EFNEntity.HF_BLADE_SLASH.get(),
               owner.level(),
               spawnPosition.x,
               spawnPosition.y,
               spawnPosition.z
            );
            slash.setOwner(owner);
            slash.setScale(scale);
            float ownerAttackDamage = (float)owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float bonusDamage = ownerAttackDamage * 0.1F;
            float totalDamage = damage + bonusDamage;
            slash.setAttackDamage(totalDamage);
            slash.setXRotOffset(0.0F);
            slash.setYRotOffset(yRotOffset);
            slash.setZRotOffset(0.0F);
            slash.setStartYRot(finalYRot);
            slash.setYRot(finalYRot);
            slash.setYBodyRot(finalYRot);
            slash.setYHeadRot(finalYRot);
            slash.setXRot(0.0F);
            owner.level().addFreshEntity(slash);
         },
         Side.SERVER
      );
   }

   public static InTimeEvent invokeMovingMurasamaSlash(
      int startFrame, float scale, float damage, double xOffset, double yOffset, double zOffset, float yRotOffset, float moveSpeed, float moveDistance
   ) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         LivingEntity owner = (LivingEntity)entityPatch.getOriginal();
         Vec3 spawnPosition = calculateSpawnPosition(owner, xOffset, yOffset, zOffset);
         float finalYRot = owner.getYRot() + yRotOffset;
         HfBladeSlash slash = new HfBladeSlash((EntityType<? extends GeoVFXEntity>)EFNEntity.HF_BLADE_SLASH.get(), owner.level());
         slash.setOwner(owner);
         slash.setScale(scale);
         float ownerAttackDamage = (float)owner.getAttributeValue(Attributes.ATTACK_DAMAGE);
         float bonusDamage = ownerAttackDamage * 0.1F;
         float totalDamage = damage + bonusDamage;
         slash.setAttackDamage(totalDamage);
         slash.setAttackCount(6);
         slash.setAttackInterval(4);
         slash.setXRotOffset(0.0F);
         slash.setYRotOffset(yRotOffset);
         slash.setZRotOffset(0.0F);
         slash.setStartYRot(finalYRot);
         slash.setMoveSpeed(moveSpeed);
         slash.setMoveDistance(moveDistance);
         slash.setTraveledDistance(0.0F);
         slash.moveTo(spawnPosition.x, spawnPosition.y, spawnPosition.z);
         slash.setYRot(finalYRot);
         slash.setYBodyRot(finalYRot);
         slash.setYHeadRot(finalYRot);
         slash.setXRot(0.0F);
         owner.level().addFreshEntity(slash);
      }, Side.SERVER);
   }

   private static Vec3 calculateSpawnPosition(LivingEntity owner, double xOffset, double yOffset, double zOffset) {
      float yawRadians = (float)Math.toRadians(-owner.getYRot());
      Vec3 lookVec = new Vec3(Math.sin(yawRadians), 0.0, Math.cos(yawRadians)).normalize();
      Vec3 rightVec = new Vec3(Math.sin(yawRadians + (Math.PI / 2)), 0.0, Math.cos(yawRadians + (Math.PI / 2))).normalize();
      Vec3 upVec = new Vec3(0.0, 1.0, 0.0);
      Vec3 basePos = owner.position().add(0.0, owner.getEyeHeight(), 0.0);
      return basePos.add(lookVec.scale(zOffset)).add(rightVec.scale(xOffset)).add(upVec.scale(yOffset));
   }

   public static InTimeEvent createAreaDamageWithPullEffect(
      int startFrame, float damage, float radius, StunType stunType, boolean teamProtect, float pullStrength
   ) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         if (!((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
            LivingEntity source = (LivingEntity)entityPatch.getOriginal();
            ServerLevel level = (ServerLevel)source.level();
            Vec3 center = source.position();
            ParticleEffectInvoker.dealAreaDamage(level, center, source, damage, radius, stunType, teamProtect);
            pullEntitiesToRing(level, center, source, radius, teamProtect, pullStrength);
         }
      }, Side.SERVER);
   }

   private static void pullEntitiesToRing(ServerLevel level, Vec3 center, LivingEntity source, float damageRadius, boolean teamProtect, float pullStrength) {
      float targetRingRadius = 3.5F;
      AABB area = new AABB(
         center.x() - damageRadius,
         center.y() - damageRadius,
         center.z() - damageRadius,
         center.x() + damageRadius,
         center.y() + damageRadius,
         center.z() + damageRadius
      );

      for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, entityx -> {
         if (entityx.isAlive() && entityx != source) {
            double distanceSq = entityx.distanceToSqr(center);
            if (distanceSq > damageRadius * damageRadius) {
               return false;
            }

            if (teamProtect) {
               if (entityx.getType().getCategory() == source.getType().getCategory()) {
                  return false;
               }
            } else if (entityx.getType() == source.getType()) {
               return false;
            }

            return isHostileOrAttacking(entityx, source);
         } else {
            return false;
         }
      })) {
         double currentDistance = Math.sqrt(entity.distanceToSqr(center));
         if (currentDistance < targetRingRadius) {
            pushEntityToRing(entity, center, targetRingRadius, pullStrength);
         } else {
            pullEntityToRing(entity, center, targetRingRadius, pullStrength);
         }
      }
   }

   private static boolean isHostileOrAttacking(LivingEntity target, LivingEntity source) {
      LivingEntityPatch<?> sourcePatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(source, LivingEntityPatch.class);
      LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
      if (sourcePatch == null) {
         return isHostileOrAttackingFallback(target, source);
      }

      if (source.getLastHurtByMob() == target || sourcePatch.getTarget() == target) {
         return true;
      }

      if (targetPatch != null && target.is(sourcePatch.getTarget())) {
         return true;
      }

      if (target instanceof Mob mob) {
         LivingEntity mobTarget = mob.getTarget();
         if (mobTarget != null && source.is(mobTarget)) {
            return true;
         }

         GoalSelector targetingAi = mob.targetSelector;

         for (WrappedGoal goal : targetingAi.getAvailableGoals()) {
            if (goal.getGoal() instanceof NearestAttackableTargetGoal<?> targetGoal) {
               TargetingConditions conditions = ((NearestAttackableTargetGoalMixin)targetGoal).getTargetConditions();
               if (conditions.test(mob, source)) {
                  return true;
               }
            }
         }
      }

      return isHostileOrAttackingFallback(target, source);
   }

   private static boolean isHostileOrAttackingFallback(LivingEntity target, LivingEntity source) {
      if (target.getType().getCategory() == MobCategory.MONSTER) {
         return true;
      }

      if (target instanceof Enemy) {
         return true;
      }

      if (target.getLastHurtByMob() == source) {
         return true;
      }

      if (target instanceof Mob mob) {
         LivingEntity mobTarget = mob.getTarget();
         if (mobTarget == source) {
            return true;
         }
      }

      return target instanceof NeutralMob neutralMob ? neutralMob.getTarget() == source : false;
   }

   private static void pushEntityToRing(LivingEntity entity, Vec3 center, float targetRadius, float pushStrength) {
      Vec3 entityPos = entity.position();
      Vec3 toEntity = entityPos.subtract(center);
      double currentDistance = toEntity.length();
      if (!(Math.abs(currentDistance - targetRadius) < 0.1)) {
         Vec3 pushDirection = toEntity.normalize();
         double pushFactor = (targetRadius - currentDistance) / targetRadius;
         double pushSpeed = pushStrength * pushFactor * 0.4;
         float knockbackResistance = (float)entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
         float effectivePushStrength = pushStrength * (1.0F - knockbackResistance);
         if (!(knockbackResistance >= pushStrength)) {
            Vec3 pushVelocity = pushDirection.scale(pushSpeed * effectivePushStrength);
            entity.push(pushVelocity.x, pushVelocity.y, pushVelocity.z);
            entity.hurtMarked = true;
            entity.invulnerableTime = 0;
         }
      }
   }

   private static void pullEntityToRing(LivingEntity entity, Vec3 center, float targetRadius, float pullStrength) {
      Vec3 entityPos = entity.position();
      Vec3 toEntity = entityPos.subtract(center);
      double currentDistance = toEntity.length();
      if (!(Math.abs(currentDistance - targetRadius) < 0.1)) {
         Vec3 targetDirection = toEntity.normalize();
         Vec3 targetPos = center.add(targetDirection.scale(targetRadius));
         Vec3 pullDirection = targetPos.subtract(entityPos).normalize();
         double distanceFactor = Math.min((currentDistance - targetRadius) / targetRadius, 2.0);
         double pullSpeed = pullStrength * distanceFactor * 0.3;
         float knockbackResistance = (float)entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
         float effectivePullStrength = pullStrength * (1.0F - knockbackResistance);
         if (!(knockbackResistance >= pullStrength)) {
            Vec3 pullVelocity = pullDirection.scale(pullSpeed * effectivePullStrength);
            entity.push(pullVelocity.x, pullVelocity.y, pullVelocity.z);
            entity.hurtMarked = true;
            entity.invulnerableTime = 0;
         }
      }
   }

   public static void spawnParticleAtJoint(LivingEntityPatch<?> entityPatch, Joint joint, ParticleOptions particleOptions, float partialTicks) {
      if (entityPatch != null && joint != null) {
         LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
         Level level = entity.level();
         if (level.isClientSide()) {
            Vec3 jointWorldPos = getJointWorldPos(entityPatch, joint, Vec3f.ZERO, partialTicks);
            level.addParticle(particleOptions, jointWorldPos.x, jointWorldPos.y, jointWorldPos.z, 0.0, 0.0, 0.0);
         }
      }
   }

   public static InTimeEvent createSpawnParticleAtJoint(int startFrame, Joint joint, ParticleOptions particleOptions) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> spawnParticleAtJoint(entityPatch, joint, particleOptions, time), Side.CLIENT);
   }

   public static InTimeEvent createMurasamaChargeParticleAtJoint(int startFrame) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(
         time,
         (entityPatch, self, params) -> {
            Vec3 jointWorldPos = getJointWorldPos(entityPatch, ((HumanoidArmature)Armatures.BIPED.get()).toolL, Vec3f.ZERO, time);
            if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()) {
               BreakOutEffek_B.playBreakOut(
                  BreakOutEffek_B.Type.LEVEL1,
                  ((LivingEntity)entityPatch.getOriginal()).level(),
                  jointWorldPos.x - ((LivingEntity)entityPatch.getOriginal()).getX(),
                  jointWorldPos.y - ((LivingEntity)entityPatch.getOriginal()).getY(),
                  jointWorldPos.z - ((LivingEntity)entityPatch.getOriginal()).getZ(),
                  1.0F,
                  entityPatch.getOriginal()
               );
            }
         },
         Side.CLIENT
      );
   }

   public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint, Vec3f offset, float partialTicks) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(entityPatch.getAnimator().getPose(partialTicks), joint);
      transformMatrix.translate(offset);
      OpenMatrix4f rotation = new OpenMatrix4f().rotate(-((float)Math.toRadians(entityPatch.getYRot() + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
      OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
      return new Vec3(
         transformMatrix.m30 + (float)entity.getX(), transformMatrix.m31 + (float)entity.getY(), transformMatrix.m32 + (float)entity.getZ()
      );
   }

   public static void build(AnimationBuilder builder) {
      HF_BLADE_IDLE_COMBAT = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_idle_combat", accessor -> new MurasamaLivingAnimation(0.15F, true, accessor, Armatures.BIPED)
      );
      HF_BLADE_IDLE_AIR = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_idle_air", accessor -> new MurasamaLivingAnimation(0.15F, true, accessor, Armatures.BIPED)
      );
      HF_BLADE_IDLE_SHEATH = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_idle_sheath",
         accessor -> new StaticAnimation(0.15F, true, accessor, Armatures.BIPED)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.particleTrail(
                     0, 200, InteractionHand.OFF_HAND, new Vec3(0.0, 0.0, 0.0), new Vec3(0.0, 0.0, 0.0), 1.0F, 1, ParticleTypes.ELECTRIC_SPARK, 0.2F
                  )
               }
            )
      );
      HF_BLADE_SWORD_OUT = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_sword_out",
         accessor -> (MurasamaLivingAnimation)new MurasamaLivingAnimation(0.1F, false, accessor, Armatures.BIPED)
            .addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{MurasamaSheathEvent(false, false)})
            .addEvents(StaticAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{MurasamaSheathEvent(false, true)})
            .addEvents(new AnimationEvent[]{setMurasamaSheathMesh(0.15F, false)})
      );
      HF_BLADE_SHEATH_IN = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_sheath_in",
         accessor -> (MurasamaLivingAnimation)new MurasamaLivingAnimation(0.15F, false, accessor, Armatures.BIPED)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(0.8F, BLADE_IN, Side.BOTH), setMurasamaSheathMesh(0.9F, true)})
            .addEvents(StaticAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{MurasamaSheathEvent(true, true)})
            .addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{MurasamaSheathEvent(true, true)})
      );
      HF_BLADE_SHEATH_IN_RUN = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_sheath_in_run",
         accessor -> (MurasamaLivingAnimation)new MurasamaLivingAnimation(0.15F, false, accessor, Armatures.BIPED)
            .addEvents(new AnimationEvent[]{InTimeEvent.create(1.0F, BLADE_IN, Side.BOTH), setMurasamaSheathMesh(1.1F, true)})
            .addEvents(StaticAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{MurasamaSheathEvent(true, true)})
            .addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{MurasamaSheathEvent(true, true)})
      );
      HF_BLADE_GUARD = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_guard", accessor -> new MurasamaLivingAnimation(0.15F, true, accessor, Armatures.BIPED)
      );
      HF_BLADE_GUARD_DASH = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_guard_dash", accessor -> new MurasamaLivingAnimation(0.15F, false, accessor, Armatures.BIPED)
      );
      HF_BLADE_GUARD_HIT = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_guard_hit", accessor -> new GuardAnimation(0.05F, 0.2F, accessor, Armatures.BIPED)
      );
      HF_BLADE_JUMP_FIRST = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_jump_first", accessor -> new StaticAnimation(0.08F, false, accessor, Armatures.BIPED)
      );
      HF_BLADE_FALL_FIRST = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_fall_first",
         accessor -> new StaticAnimation(0.2F, false, accessor, Armatures.BIPED).addEvents(new AnimationEvent[]{setMurasamaSheathMesh(0.1F, false)})
      );
      HF_BLADE_JUMP_SECOND = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_jump_second",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, 0.55F, accessor, Armatures.BIPED)
            .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, false)
            .newTimePair(0.0F, 9.223372E18F)
            .addStateRemoveOld(EntityState.INACTION, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.5F)
            .addEvents(
               StaticAnimationProperty.ON_BEGIN_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create(
                     (entitypatch, animation, params) -> {
                        Vec3 pos = ((LivingEntity)entitypatch.getOriginal()).position();
                        entitypatch.playSound((SoundEvent)EpicFightSounds.TUMBLE.get(), 0.0F, 0.0F);
                        ((LivingEntity)entitypatch.getOriginal())
                           .level()
                           .addAlwaysVisibleParticle(
                              (ParticleOptions)EpicFightParticles.AIR_BURST.get(),
                              pos.x,
                              pos.y + ((LivingEntity)entitypatch.getOriginal()).getBbHeight() * 0.5,
                              pos.z,
                              0.0,
                              -1.0,
                              2.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      HF_BLADE_TAUNT = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_taunt",
         accessor -> (ActionAnimation)new ActionAnimation(0.05F, 6.5F, accessor, Armatures.BIPED)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.05F, ReusableSources.PLAY_SOUND, Side.SERVER).params(SoundEvents.ARMOR_EQUIP_CHAIN), createTauntEffect(0, 400, 10.0F, true)
               }
            )
            .newTimePair(0.0F, 9.223372E18F)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, true)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, true)
      );
      HF_BLADE_WALK_COMBAT = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_walk_combat", accessor -> new MurasamaMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 2.0F)
      );
      HF_BLADE_WALK_SHEATH = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_walk_sheath", accessor -> new AvalonMovementAnimation(true, accessor, Armatures.BIPED, 2.0F)
      );
      HF_BLADE_RUN_COMBAT_1 = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_run_combat_1", accessor -> new MurasamaMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.07F)
      );
      HF_BLADE_RUN_COMBAT_2 = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_run_combat_2",
         accessor -> (MurasamaMovementAnimation)new MurasamaMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.1F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.1F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.FIRE_AMBIENT, 1.0F, 1.0F, 1.0F), Side.BOTH),
                  AvalonEventUtils.particleTrail(
                     0, 200, InteractionHand.MAIN_HAND, new Vec3(0.0, 0.0, -1.4F), new Vec3(0.0, 0.0, -1.5), 1.0F, 1, ParticleTypes.FLAME, 0.5F
                  ),
                  AvalonEventUtils.particleTrail(
                     0, 200, InteractionHand.MAIN_HAND, new Vec3(0.0, 0.0, -1.45F), new Vec3(0.0, 0.0, -1.55F), 1.0F, 1, ParticleTypes.SMOKE, 0.5F
                  )
               }
            )
      );
      HF_BLADE_RUN_SHEATH = builder.nextAccessor(
         "biped/hf_blade/living/hf_blade_run_sheath", accessor -> new AvalonMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.3F)
      );
      HF_BLADE_COUNTER = builder.nextAccessor(
         "biped/hf_blade/combat/hf_blade_counter",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  16,
                  22,
                  30,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_KICK_Y_KICK_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  EFNStunAnimations.BIPED_HITUP_1
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.COUNTER))
            .addProperty(AttackAnimationProperty.REACH, 0.1F)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(
               StaticAnimationProperty.PLAY_SPEED_MODIFIER,
               (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> dynamicAnimation.isLinkAnimation() ? 1.15F : 1.15F
            )
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(16, 5, 2.0F, 2.0F, 2.0F), AvalonEventUtils.chaseToTarget(3, 22, 0.3F, 2.0F)})
      );
      HF_BLADE_ZANDATSU = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_zandatsu",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  20,
                  30,
                  49,
                  InteractionHand.MAIN_HAND,
                  2.0F,
                  2.0F,
                  2.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  50,
                  65,
                  240,
                  InteractionHand.MAIN_HAND,
                  2.0F,
                  2.0F,
                  2.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_KNOCKDOWN,
                  Animations.BIPED_KNOCKDOWN
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
            .addProperty(
               AttackPhaseProperty.SOURCE_TAG,
               Set.of(EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.BYPASS_DODGE, EpicFightDamageTypeTags.UNBLOCKALBE)
            )
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.EVISCERATE_LOST_HEALTH.create(new float[]{0.3F})))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.05F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)EpicFightSounds.NEUTRALIZE_BOSSES.get()),
                  InTimeEvent.create(1.5F, ReusableSources.PLAY_SOUND, Side.SERVER).params(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value()),
                  InTimeEvent.create(2.25F, ReusableSources.PLAY_SOUND, Side.SERVER).params(SoundEvents.WARDEN_SONIC_BOOM),
                  createSpawnParticleAtJoint(110, ((HumanoidArmature)Armatures.BIPED.get()).handL, ParticleTypes.SONIC_BOOM),
                  createSpawnParticleAtJoint(135, ((HumanoidArmature)Armatures.BIPED.get()).handL, (ParticleOptions)EFNParticles.ALL_SPARK_ZANDATSU.get())
               }
            )
            .addEvents(StaticAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{SimpleEvent.create((entityPatch, animation, params) -> {
               if (!((LivingEntity)entityPatch.getOriginal()).onGround()) {
                  teleportPlayerToGround(entityPatch);
               }

               if ((Boolean)EFNClientConfig.ENABLE_ZANDATSU_CAMERA_ANIMATIONS.get()) {
                  CameraEvents.SetAnim(EFNAnimations.ZANDATSU, (LivingEntity)entityPatch.getOriginal(), true, HF_BLADE_ZANDATSU);
               }
            }, Side.CLIENT)})
            .newTimePair(0.0F, 9.223372E18F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      HF_BLADE_ZANDATSU_AIR = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_zandatsu_air",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  10,
                  20,
                  39,
                  InteractionHand.MAIN_HAND,
                  2.0F,
                  2.0F,
                  2.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  40,
                  50,
                  207,
                  InteractionHand.MAIN_HAND,
                  2.0F,
                  2.0F,
                  2.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_KNOCKDOWN,
                  Animations.BIPED_KNOCKDOWN
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
            .addProperty(
               AttackPhaseProperty.SOURCE_TAG,
               Set.of(EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.BYPASS_DODGE, EpicFightDamageTypeTags.UNBLOCKALBE)
            )
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.EVISCERATE_LOST_HEALTH.create(new float[]{0.3F})))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.0F}))
            .addProperty(ActionAnimationProperty.MOVE_ON_LINK, true)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, null)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               new AnimationEvent[]{
                  InPeriodEvent.create(
                     0.8F,
                     1.65F,
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
                  ),
                  TeleportGroundUtils.create(105, -0.1F),
                  InTimeEvent.create(0.05F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)EpicFightSounds.NEUTRALIZE_BOSSES.get()),
                  InTimeEvent.create(2.2F, ReusableSources.PLAY_SOUND, Side.SERVER).params(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value()),
                  InTimeEvent.create(3.0F, ReusableSources.PLAY_SOUND, Side.SERVER).params(SoundEvents.WARDEN_SONIC_BOOM),
                  createSpawnParticleAtJoint(160, ((HumanoidArmature)Armatures.BIPED.get()).handL, ParticleTypes.SONIC_BOOM),
                  createSpawnParticleAtJoint(185, ((HumanoidArmature)Armatures.BIPED.get()).handL, (ParticleOptions)EFNParticles.ALL_SPARK_ZANDATSU.get())
               }
            )
            .newTimePair(0.0F, 9.223372E18F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.LOOK_TARGET, false)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      HF_BLADE_DASH_Y = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_dash_y",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  9,
                  21,
                  55,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  57,
                  67,
                  73,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_AIR_R0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 2.0F),
                  AvalonEventUtils.simpleCameraShake(56, 5, 2.0F, 2.0F, 2.0F),
                  InTimeEvent.create(0.15F, BLADE_OUT, Side.LOCAL_CLIENT),
                  InTimeEvent.create(1.0F, BLADE_OUT, Side.LOCAL_CLIENT)
               }
            )
      );
      HF_BLADE_DASH_Y_SP = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_dash_y_sp",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.07F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  10,
                  17,
                  25,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HITUP_3,
                  EFNStunAnimations.BIPED_HITUP_3
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(10, 5, 2.0F, 2.0F, 2.0F),
                  AvalonEventUtils.particleTrail(
                     0, 17, InteractionHand.MAIN_HAND, new Vec3(0.0, 0.0, -1.4F), new Vec3(0.0, 0.0, -1.5), 10.0F, 1, ParticleTypes.LAVA, 0.2F
                  ),
                  InTimeEvent.create(0.05F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.FIRECHARGE_USE, 1.5F, 0.0F, 0.0F), Side.SERVER)
               }
            )
            .newTimePair(0.0F, 0.33333334F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
      );
      HF_BLADE_KICK_Y = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_kick_y",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  12,
                  17,
                  30,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_KICK_Y_ELBOW_HITBOX,
                  Animations.BIPED_HIT_SHORT,
                  Animations.BIPED_HIT_SHORT
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  31,
                  36,
                  41,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_KICK_Y_KICK_HITBOX,
                  EFNStunAnimations.BIPED_HITUP_2,
                  EFNStunAnimations.BIPED_HITUP_2
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
            .addEvents(
               new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(12, 5, 2.0F, 2.0F, 2.0F), AvalonEventUtils.simpleCameraShake(31, 5, 2.0F, 2.0F, 2.0F)}
            )
      );
      HF_BLADE_Y = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_y",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  34,
                  46,
                  50,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
            .addProperty(
               StaticAnimationProperty.PLAY_SPEED_MODIFIER,
               (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> dynamicAnimation.isLinkAnimation() ? 1.0F : 1.05F
            )
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(33, 5, 2.0F, 2.0F, 3.0F),
                  InTimeEvent.create(0.26F, BLADE_IN, Side.BOTH),
                  InTimeEvent.create(0.553F, BLADE_OUT, Side.LOCAL_CLIENT)
               }
            )
      );
      HF_BLADE_Y_CHARGE = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_y_charge",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  65,
                  76,
                  77,
                  InteractionHand.MAIN_HAND,
                  0.5F,
                  0.5F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_Y_CHARGE_DASH_HITBOX,
                  Animations.BIPED_HIT_SHORT,
                  Animations.BIPED_HIT_SHORT
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  78,
                  88,
                  95,
                  InteractionHand.MAIN_HAND,
                  0.5F,
                  0.5F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_Y_CHARGE_SLASH_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackAnimationProperty.AFFECT_SPEED, true)
            .addProperty(AttackAnimationProperty.COORD_GET, BLADE_MODEL_COORD)
            .addProperty(AttackAnimationProperty.REACH, 0.3F)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.1F)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.createBlueChargedRingExplosion(1.0F, 2.5F, 1.5F),
                  createAreaDamageWithPullEffect(10, 1.0F, 4.0F, StunType.LONG, true, 0.5F),
                  AvalonEventUtils.simpleSound(10, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(57, SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F),
                  AvalonEventUtils.simpleCameraShake(65, 10, 3.0F, 2.0F, 8.0F),
                  invokeMurasamaCharging(3, 1.0F, -0.25, -1.8, -0.2, 90.0F),
                  invokeMurasamaSlash(78, 1.0F, 2.0F, 0.0, -1.3, 1.2, 0.0F),
                  createSpawnParticleAtJoint(65, ((HumanoidArmature)Armatures.BIPED.get()).handL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  createSpawnParticleAtJoint(66, ((HumanoidArmature)Armatures.BIPED.get()).handL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  InPeriodEvent.create(
                     1.1F,
                     1.3F,
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
                  ),
                  InTimeEvent.create(1.1F, (entityPatch, animation, params) -> {
                     LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                     double x = entity.getX();
                     double y = entity.getY() + 1.0;
                     double z = entity.getZ();
                     entity.level().addParticle(ParticleTypes.EXPLOSION, x, y, z, 0.0, 0.0, 0.0);
                  }, Side.CLIENT),
                  InTimeEvent.create(
                     0.01F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL1, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(1.08333F, BLADE_OUT, Side.LOCAL_CLIENT),
                  createMurasamaChargeParticleAtJoint(5)
               }
            )
            .newTimePair(1.0F, 1.3F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      HF_BLADE_Y_CHARGE_THROUGH = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_y_charge_through",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  65,
                  76,
                  77,
                  InteractionHand.MAIN_HAND,
                  0.5F,
                  0.5F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_Y_CHARGE_DASH_HITBOX,
                  Animations.BIPED_HIT_SHORT,
                  Animations.BIPED_HIT_SHORT
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  78,
                  88,
                  95,
                  InteractionHand.MAIN_HAND,
                  0.5F,
                  0.5F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_Y_CHARGE_SLASH_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
            .addProperty(AttackAnimationProperty.AFFECT_SPEED, true)
            .addProperty(AttackAnimationProperty.COORD_GET, BLADE_MODEL_COORD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.1F)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.createBlueChargedRingExplosion(1.0F, 2.5F, 1.5F),
                  createAreaDamageWithPullEffect(10, 1.0F, 4.0F, StunType.LONG, true, 0.5F),
                  AvalonEventUtils.simpleSound(10, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(57, SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F),
                  AvalonEventUtils.simpleCameraShake(65, 10, 3.0F, 2.0F, 8.0F),
                  invokeMurasamaCharging(3, 1.0F, -0.25, -1.8, -0.2, 90.0F),
                  invokeMurasamaSlash(78, 1.0F, 2.0F, 0.0, -1.3, 1.2, 0.0F),
                  createSpawnParticleAtJoint(65, ((HumanoidArmature)Armatures.BIPED.get()).handL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  createSpawnParticleAtJoint(66, ((HumanoidArmature)Armatures.BIPED.get()).handL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  InTimeEvent.create(
                     0.01F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL1, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InPeriodEvent.create(
                     1.1F,
                     1.3F,
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
                  ),
                  InTimeEvent.create(1.1F, (entityPatch, animation, params) -> {
                     LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                     double x = entity.getX();
                     double y = entity.getY() + 1.0;
                     double z = entity.getZ();
                     entity.level().addParticle(ParticleTypes.EXPLOSION, x, y, z, 0.0, 0.0, 0.0);
                  }, Side.CLIENT),
                  InTimeEvent.create(1.08333F, BLADE_OUT, Side.LOCAL_CLIENT),
                  createMurasamaChargeParticleAtJoint(5)
               }
            )
            .newTimePair(1.0F, 1.3F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      HF_BLADE_Y_AIR = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_air_y",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  29,
                  39,
                  50,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HITDOWN_1,
                  EFNStunAnimations.BIPED_HITDOWN_AIR
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.59F}))
            .addEvents(
               new AnimationEvent[]{
                  InPeriodEvent.create(
                     0.5F,
                     0.65F,
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
                  ),
                  TeleportGroundUtils.create(36, -0.1F),
                  InTimeEvent.create(0.65F, EFNYamatoAnimations.GROUND_SLAM_LAND_A, Side.SERVER),
                  InTimeEvent.create(0.48333F, BLADE_OUT, Side.BOTH)
               }
            )
      );
      HF_BLADE_Y_CHARGE_AIR = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_air_y_charge",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  61,
                  65,
                  66,
                  InteractionHand.MAIN_HAND,
                  0.5F,
                  0.5F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_AIR_Y_CHARGE_DASH_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  67,
                  72,
                  82,
                  InteractionHand.MAIN_HAND,
                  0.5F,
                  0.5F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_AIR_Y_CHARGE_SLASH_HITBOX,
                  EFNStunAnimations.BIPED_HITDOWN_1,
                  EFNStunAnimations.BIPED_HIT_AIR_L0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.0F}))
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, EFN_RAW_COORD_WITH_X_ROT)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, null)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
            .addProperty(StaticAnimationProperty.POSE_MODIFIER, EFN_ROOT_X_MODIFIER)
            .addProperty(AttackAnimationProperty.AFFECT_SPEED, true)
            .addProperty(AttackAnimationProperty.COORD_GET, BLADE_MODEL_COORD)
            .addProperty(ActionAnimationProperty.COORD_GET, BroadBladeAttackAnimation.BROADBLADE_MODEL_COORD)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.createBlueChargedRingExplosion(1.0F, 2.5F, 1.5F),
                  createAreaDamageWithPullEffect(10, 1.0F, 4.0F, StunType.LONG, true, 0.5F),
                  AvalonEventUtils.simpleSound(8, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(58, SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F),
                  invokeMurasamaCharging(3, 1.0F, -0.5, -1.5, 0.0, 90.0F),
                  invokeMurasamaSlash(67, 1.0F, 2.0F, 0.0, -2.0, 1.2, 0.0F),
                  createSpawnParticleAtJoint(61, ((HumanoidArmature)Armatures.BIPED.get()).handL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  createSpawnParticleAtJoint(62, ((HumanoidArmature)Armatures.BIPED.get()).handL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  InTimeEvent.create(
                     0.01F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL1, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InPeriodEvent.create(
                     1.0F,
                     1.15F,
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
                  ),
                  InTimeEvent.create(1.0F, (entityPatch, animation, params) -> {
                     LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                     RandomSource random = entity.getRandom();
                     double x = entity.getX() + (random.nextDouble() - random.nextDouble()) * 2.0;
                     double y = entity.getY();
                     double z = entity.getZ() + (random.nextDouble() - random.nextDouble()) * 2.0;
                     entity.level().addParticle(ParticleTypes.EXPLOSION, x, y, z, random.nextDouble() * 0.005, 0.0, 0.0);
                  }, Side.CLIENT),
                  InTimeEvent.create(1.01F, BLADE_OUT, Side.LOCAL_CLIENT),
                  createMurasamaChargeParticleAtJoint(5)
               }
            )
            .newTimePair(1.0F, 1.2F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, EFNAnimations.INVINCIBLE_SOURCE_VALIDATOR)
      );
      HF_BLADE_X_AIR = builder.nextAccessor(
         "biped/hf_blade/combat/hf_blade_air_x",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  15,
                  22,
                  23,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HITUP_3,
                  EFNStunAnimations.BIPED_HITUP_2
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.7F}))
      );
      HF_BLADE_XX_AIR = builder.nextAccessor(
         "biped/hf_blade/combat/hf_blade_air_xx",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  18,
                  26,
                  33,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HITDOWN_1,
                  EFNStunAnimations.BIPED_HITDOWN_AIR
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_2.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.4F}))
      );
      HF_BLADE_DASH_X = builder.nextAccessor(
         "biped/hf_blade/combat/hf_blade_dash_x",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  16,
                  36,
                  42,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(16, 5, 2.0F, 2.0F, 2.0F)})
      );
      HF_BLADE_X = builder.nextAccessor(
         "biped/hf_blade/combat/hf_blade_x",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  16,
                  24,
                  25,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(16, 5, 2.0F, 2.0F, 2.0F)})
      );
      HF_BLADE_XY = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_xy",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  31,
                  36,
                  42,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HITUP_1,
                  EFNStunAnimations.BIPED_HITUP_1
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 10)
            .addProperty(
               StaticAnimationProperty.PLAY_SPEED_MODIFIER,
               (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> dynamicAnimation.isLinkAnimation() ? 1.0F : 1.15F
            )
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(31, 5, 2.0F, 2.0F, 4.0F),
                  InTimeEvent.create(0.3F, BLADE_IN, Side.BOTH),
                  InTimeEvent.create(0.52F, BLADE_OUT, Side.LOCAL_CLIENT)
               }
            )
      );
      HF_BLADE_XY_CHARGE = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_xy_charge",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  80,
                  94,
                  105,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HITUP_3,
                  EFNStunAnimations.BIPED_HITUP_3
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 2.0F}))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.createBlueChargedRingExplosion(1.0F, 2.5F, 1.5F),
                  createAreaDamageWithPullEffect(10, 1.0F, 4.0F, StunType.LONG, true, 0.5F),
                  AvalonEventUtils.simpleSound(10, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(67, SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F),
                  AvalonEventUtils.simpleCameraShake(80, 10, 3.0F, 3.0F, 8.0F),
                  invokeMurasamaCharging(3, 1.0F, -0.15, -1.8, -0.2, 90.0F),
                  invokeMurasamaSlash(91, 1.0F, 2.0F, 0.0, 0.2, 1.0, 0.0F),
                  createSpawnParticleAtJoint(80, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  createSpawnParticleAtJoint(81, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  InTimeEvent.create(
                     0.01F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL1, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InPeriodEvent.create(
                     1.3F,
                     1.6F,
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
                  ),
                  InTimeEvent.create(1.3333F, BLADE_OUT, Side.LOCAL_CLIENT),
                  createMurasamaChargeParticleAtJoint(5)
               }
            )
      );
      HF_BLADE_XX = builder.nextAccessor(
         "biped/hf_blade/combat/hf_blade_xx",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.075F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  12,
                  22,
                  23,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_4.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 3)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(13, 5, 2.0F, 2.0F, 2.0F), AvalonEventUtils.chaseToTarget(6, 21, 0.5F, 1.5F)})
      );
      HF_BLADE_XXY = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_xxy",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.12F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  18,
                  24,
                  31,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 10)
            .addProperty(
               StaticAnimationProperty.PLAY_SPEED_MODIFIER,
               (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> dynamicAnimation.isLinkAnimation() ? 1.15F : 1.15F
            )
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(18, 5, 2.0F, 2.0F, 4.0F),
                  InTimeEvent.create(0.13F, BLADE_IN, Side.BOTH),
                  InTimeEvent.create(0.3F, BLADE_OUT, Side.LOCAL_CLIENT)
               }
            )
      );
      HF_BLADE_XXY_CHARGE = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_xxy_charge",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  64,
                  69,
                  70,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_HIT_SHORT,
                  Animations.BIPED_HIT_SHORT
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  71,
                  76,
                  86,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  Animations.BIPED_HIT_SHORT,
                  Animations.BIPED_HIT_SHORT
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.1F)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.createBlueChargedRingExplosion(1.0F, 2.5F, 1.5F),
                  createAreaDamageWithPullEffect(10, 1.0F, 4.0F, StunType.HOLD, true, 0.5F),
                  createAreaDamageWithPullEffect(30, 1.0F, 4.0F, StunType.HOLD, true, 0.5F),
                  AvalonEventUtils.simpleSound(10, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(57, SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F),
                  AvalonEventUtils.simpleCameraShake(64, 10, 3.0F, 3.0F, 8.0F),
                  invokeMurasamaCharging(2, 1.0F, 0.25, -1.7, -0.15, 90.0F),
                  invokeMurasamaSlash(65, 1.0F, 1.0F, 0.0, -1.5, 0.0, 0.0F),
                  invokeMurasamaSlash(65, 1.0F, 1.0F, 0.0, -1.5, 0.0, 180.0F),
                  createSpawnParticleAtJoint(64, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  createSpawnParticleAtJoint(65, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  InTimeEvent.create(1.06F, BLADE_OUT, Side.LOCAL_CLIENT),
                  createMurasamaChargeParticleAtJoint(5),
                  InTimeEvent.create(
                     0.01F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL1, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      HF_BLADE_XXX = builder.nextAccessor(
         "biped/hf_blade/combat/hf_blade_xxx",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  9,
                  17,
                  18,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).rootJoint,
                  HF_BLADE_XXX_KICK_HITBOX,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLUNT_HIT_HARD.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.AIR_BURST)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.3F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(9, 5, 2.0F, 2.0F, 2.0F)})
      );
      HF_BLADE_XXXY = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_xxxy",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  41,
                  47,
                  62,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0
               ),
               MurasamaAnimationUtils.createCustomStunPhase(
                  63,
                  73,
                  78,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0,
                  EFNStunAnimations.BIPED_HIT_GROUND_R0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(
               StaticAnimationProperty.PLAY_SPEED_MODIFIER,
               (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> dynamicAnimation.isLinkAnimation() ? 1.3F : 1.3F
            )
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(39, 5, 2.0F, 2.0F, 4.0F),
                  AvalonEventUtils.simpleCameraShake(63, 5, 2.0F, 2.0F, 4.0F),
                  InTimeEvent.create(0.31F, BLADE_IN, Side.BOTH),
                  InTimeEvent.create(0.65F, BLADE_OUT, Side.LOCAL_CLIENT)
               }
            )
      );
      HF_BLADE_XXXY_CHARGE = builder.nextAccessor(
         "biped/hf_blade/skill/hf_blade_xxxy_charge",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.08F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  61,
                  75,
                  85,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  HF_BLADE_LONG_HITBOX,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0,
                  EFNStunAnimations.BIPED_HIT_GROUND_L0
               )
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.1F)
            .addEvents(
               new AnimationEvent[]{
                  invokeMurasamaCharging(2, 1.0F, -0.15, -0.8, 0.0, 90.0F),
                  invokeMovingMurasamaSlash(61, 1.5F, 3.0F, 0.0, -2.0, 0.0, 0.0F, 1.5F, 15.0F),
                  ParticleEffectInvoker.createBlueChargedRingExplosion(1.0F, 2.5F, 1.5F),
                  createAreaDamageWithPullEffect(10, 1.0F, 4.0F, StunType.LONG, true, 0.5F),
                  AvalonEventUtils.simpleSound(10, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(57, SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F),
                  AvalonEventUtils.simpleCameraShake(61, 10, 3.0F, 3.0F, 8.0F),
                  createSpawnParticleAtJoint(61, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  createSpawnParticleAtJoint(62, ((HumanoidArmature)Armatures.BIPED.get()).toolL, (ParticleOptions)EFNParticles.ALL_SPARK.get()),
                  InTimeEvent.create(1.01F, BLADE_OUT, Side.LOCAL_CLIENT),
                  createMurasamaChargeParticleAtJoint(5),
                  InTimeEvent.create(
                     0.01F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.HFBLADE_AAA_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL1, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      HF_BLADE_XXXX = builder.nextAccessor(
         "biped/hf_blade/combat/hf_blade_xxxx",
         accessor -> (MurasamaAttackAnimation)new MurasamaAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               MurasamaAnimationUtils.createCustomStunPhase(
                  37,
                  47,
                  52,
                  InteractionHand.MAIN_HAND,
                  1.0F,
                  1.0F,
                  ((HumanoidArmature)Armatures.BIPED.get()).toolR,
                  null,
                  Animations.BIPED_HIT_LONG,
                  Animations.BIPED_HIT_LONG
               )
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_4.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.2F)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleCameraShake(39, 8, 2.0F, 2.0F, 5.0F)})
      );
   }

   private static void teleportPlayerToGround(LivingEntityPatch<?> entityPatch) {
      if (entityPatch != null && entityPatch.getOriginal() instanceof ServerPlayer player) {
         Vec3 var4 = TeleportGroundUtils.getSimpleGroundPosition(player, 0.0F);
         Vec3 targetPos = new Vec3(player.getX(), var4.y, player.getZ());
         teleportToPosition(player, targetPos);
      }
   }

   private static void teleportToPosition(ServerPlayer player, Vec3 targetPos) {
      Vec3 moveVec = new Vec3(targetPos.x - player.getX(), targetPos.y - player.getY(), targetPos.z - player.getZ());
      player.move(MoverType.SELF, moveVec);
      player.connection.teleport(targetPos.x, targetPos.y, targetPos.z, player.getYRot(), player.getXRot());
      player.fallDistance = 0.0F;
      player.setDeltaMovement(0.0, 0.0, 0.0);
   }
}
