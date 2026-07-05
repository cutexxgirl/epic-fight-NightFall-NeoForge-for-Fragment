package com.hm.efn.animations.types;

import com.google.common.collect.Lists;
import com.hm.efn.animations.property.EFNAnimationProperties;
import com.hm.efn.client.events.RenderEvents;
import com.hm.efn.util.ScanAttackConsumer;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationVariables;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationVariables.SharedVariableKey;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PoseModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class ScanAttackAnimation extends AttackAnimation {
   public static final SharedVariableKey<List<Entity>> SCANNED_ENTITY = AnimationVariables.unsynchShared(animator -> Lists.newArrayList(), false);
   protected ScanAttackConsumer attackConsumer = (animation, entitypatch, prevElapsedTime, elapsedTime, prevState, state, phase) -> {};

   public ScanAttackAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends ScanAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
   }

   public ScanAttackAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends ScanAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, accessor, armature);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
   }

   public ScanAttackAnimation(
      float convertTime, AnimationAccessor<? extends ScanAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases
   ) {
      super(convertTime, accessor, armature, phases);
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false);
   }

   public ScanAttackAnimation(
      float convertTime,
      float antic,
      float contact,
      float recovery,
      InteractionHand hand,
      @javax.annotation.Nullable Collider collider,
      Joint scanner,
      AnimationAccessor<? extends ScanAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, accessor, armature, new Phase[]{new Phase(0.0F, antic, contact, recovery, Float.MAX_VALUE, hand, scanner, collider)});
      this.addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true);
   }

   public static List<Entity> getScannedEntities(LivingEntityPatch<?> entityPatch) {
      return (List<Entity>)entityPatch.getAnimator().getVariables().getOrDefaultSharedVariable(SCANNED_ENTITY);
   }

   public void begin(LivingEntityPatch<?> entitypatch) {
      super.begin(entitypatch);
      getScannedEntities(entitypatch).clear();
   }

   public void end(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      super.end(entitypatch, nextAnimation, isEnd);
      entitypatch.removeHurtEntities();
      getScannedEntities(entitypatch).clear();
   }

   public void tick(LivingEntityPatch<?> entitypatch) {
      super.tick(entitypatch);
      if (entitypatch.isLogicalClient() && this.getProperty(EFNAnimationProperties.INVISIBLE_PHASE).isPresent()) {
         if (((EFNAnimationProperties.SpecialPhase)this.getProperty(EFNAnimationProperties.INVISIBLE_PHASE).get())
            .isInPhase(entitypatch.getAnimator().getPlayerFor(this.getAccessor()).getElapsedTime())) {
            RenderEvents.HiddenEntity((LivingEntity)entitypatch.getOriginal(), 10);
         } else {
            RenderEvents.UnhiddenEntity((LivingEntity)entitypatch.getOriginal());
         }
      }
   }

   public ScanAttackAnimation setAttackConsumer(ScanAttackConsumer consumer) {
      this.attackConsumer = consumer;
      return this;
   }

   public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
      if (this.getProperty(ActionAnimationProperty.COORD).isEmpty()) {
         JointTransform jt = pose.orElseEmpty("Root");
         Vec3f jointPosition = jt.translation();
         OpenMatrix4f toRootTransformApplied = entitypatch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
         OpenMatrix4f toOrigin = OpenMatrix4f.invert(toRootTransformApplied, null);
         Vec3f worldPosition = OpenMatrix4f.transform3v(toRootTransformApplied, jointPosition, null);
         if (!this.getProperty(EFNAnimationProperties.MOVE_ROOT_PHASE).isPresent()
            || !((EFNAnimationProperties.SpecialPhase)this.getProperty(EFNAnimationProperties.MOVE_ROOT_PHASE).get()).isInPhase(time)) {
            worldPosition.x = 0.0F;
            worldPosition.y = this.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false) && worldPosition.y > 0.0F ? 0.0F : worldPosition.y;
            worldPosition.z = 0.0F;
         }

         OpenMatrix4f.transform3v(toOrigin, worldPosition, worldPosition);
         jointPosition.x = worldPosition.x;
         jointPosition.y = worldPosition.y;
         jointPosition.z = worldPosition.z;
      }

      PoseModifier modifier = (PoseModifier)this.getProperty(StaticAnimationProperty.POSE_MODIFIER).orElse(null);
      if (modifier != null) {
         modifier.modify(animation, pose, entitypatch, time, partialTicks);
      }
   }

   protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> dynamicAnimation) {
      Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
      AnimationPlayer animPlayer = entitypatch.getAnimator().getPlayerFor(dynamicAnimation);
      if (animPlayer == null) {
         return vec3;
      }

      float t = animPlayer.getElapsedTime();
      if (this.getProperty(EFNAnimationProperties.MOVE_ROOT_PHASE).isPresent()
         && ((EFNAnimationProperties.SpecialPhase)this.getProperty(EFNAnimationProperties.MOVE_ROOT_PHASE).get()).isInPhase(t)) {
         vec3 = vec3.multiply(0.0, 0.0, 0.0);
      }

      return vec3;
   }

   public void attackTick(LivingEntityPatch<?> entitypatch, AssetAccessor<? extends DynamicAnimation> animation) {
      AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(this.getAccessor());
      float elapsedTime = player.getElapsedTime();
      float prevElapsedTime = player.getPrevElapsedTime();
      EntityState state = this.getState(entitypatch, elapsedTime);
      EntityState prevState = this.getState(entitypatch, prevElapsedTime);
      Phase phase = this.getPhaseByTime(elapsedTime);
      if (state.getLevel() == 1 && !state.turningLocked() && entitypatch instanceof MobPatch<?> mobpatch) {
         ((Mob)mobpatch.getOriginal()).getNavigation().stop();
         ((LivingEntity)entitypatch.getOriginal()).attackAnim = 2.0F;
         LivingEntity target = entitypatch.getTarget();
         if (target != null) {
            entitypatch.rotateTo(target, entitypatch.getYRotLimit(), false);
         }
      }

      if (prevState.attacking() || state.attacking() || prevState.getLevel() < 2 && state.getLevel() > 2) {
         if (!prevState.attacking()) {
            entitypatch.removeHurtEntities();
         }

         this.ScanTarget(entitypatch, prevElapsedTime, elapsedTime, prevState, state, phase);
      }
   }

   public void ScanTarget(LivingEntityPatch<?> entitypatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase) {
      LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
      float prevPoseTime = prevState.attacking() ? prevElapsedTime : phase.preDelay;
      float poseTime = state.attacking() ? elapsedTime : phase.contact;
      if (phase != null) {
         List<Entity> list = phase.getCollidingEntities(entitypatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entitypatch, this));
         if (list != null) {
            list.removeIf(e -> e == null || !e.isAlive());
         }

         if (list != null && !list.isEmpty()) {
            if (!list.isEmpty()) {
               Entity firstEntity = list.get(0);
               if (firstEntity != null) {
                  ((LivingEntity)entitypatch.getOriginal()).setLastHurtMob(firstEntity);
               }
            }

            HitEntityList hitEntities = new HitEntityList(entitypatch, list, Priority.DISTANCE);

            while (entitypatch.getCurrentlyActuallyHitEntities().size() < this.getMaxStrikes(entitypatch, phase) && hitEntities.next()) {
               Entity entity1 = hitEntities.getEntity();
               if (entity1 != null && entity1.isAlive()) {
                  LivingEntity trueEntity = this.getTrueEntity(entity1);
                  if (trueEntity != null
                     && !entitypatch.isTargetInvulnerable(entity1)
                     && (entity1 instanceof LivingEntity || entity1 instanceof PartEntity)
                     && entity.hasLineOfSight(entity1)
                     && !getScannedEntities(entitypatch).contains(entity1)) {
                     if (!entitypatch.getCurrentlyActuallyHitEntities().contains(trueEntity)) {
                        entitypatch.getCurrentlyActuallyHitEntities().add(trueEntity);
                     }

                     getScannedEntities(entitypatch).add(entity1);
                  }
               }
            }
         }

         if (entitypatch.getCurrentlyActuallyHitEntities() != null && !entitypatch.getCurrentlyActuallyHitEntities().contains(entitypatch.getOriginal())) {
            entitypatch.getCurrentlyActuallyHitEntities().add((LivingEntity)entitypatch.getOriginal());
         }
      }
   }

   public void hurtCollidingEntities(
      LivingEntityPatch<?> entitypatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase
   ) {
   }

   public boolean isComboAttackAnimation() {
      return true;
   }
}
