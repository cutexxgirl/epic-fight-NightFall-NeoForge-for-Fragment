package com.hm.efn.animations.types.murasama;

import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent;
import com.merlin204.avalon.epicfight.api.AnimationAttackResultEvent.SimpleEvent;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

public class MurasamaAttackAnimation extends AvalonAttackAnimation {
   private void initMurasamaProperties() {
      this.addProperty(StaticAnimationProperty.POSE_MODIFIER, EFNMurasamaAnimations.EFN_COMBO_ATTACK_DIRECTION_MODIFIER);
      this.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])));
      this.addAttackResultEvents(new AnimationAttackResultEvent[]{SimpleEvent.create(this::onAttackResult)});
   }

   public MurasamaAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      Supplier<? extends StaticAnimation> hitAnimation,
      MurasamaPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
      this.initMurasamaProperties();
   }

   public MurasamaAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature, play_speed, damageMulti);
      this.initMurasamaProperties();
   }

   public MurasamaAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, play_speed, damageMulti, phases);
      this.initMurasamaProperties();
   }

   public MurasamaAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, damageMulti, phases);
      this.initMurasamaProperties();
   }

   public MurasamaAttackAnimation(
      float transitionTime, AnimationAccessor<? extends ComboAttackAnimation> accessor, AssetAccessor<? extends Armature> armature, AvalonPhase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
      this.initMurasamaProperties();
   }

   public MurasamaAttackAnimation(
      float convertTime, String path, AssetAccessor<? extends Armature> armature, float play_speed, float damageMulti, AvalonPhase... phases
   ) {
      super(convertTime, path, armature, play_speed, damageMulti, phases);
      this.initMurasamaProperties();
   }

   public MurasamaAttackAnimation(
      float transitionTime,
      AnimationAccessor<? extends ComboAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float play_speed,
      float damageMulti,
      MurasamaPhase... phases
   ) {
      this(transitionTime, accessor, armature, play_speed, damageMulti, null, phases);
   }

   protected void bindPhaseState(Phase phase) {
      float preDelay = phase.preDelay;
      this.stateSpectrumBlueprint
         .newTimePair(0.0F, preDelay)
         .addState(EntityState.PHASE_LEVEL, 1)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .newTimePair(phase.start, phase.recovery + 0.45F)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .newTimePair(phase.start, phase.recovery)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .newTimePair(phase.start, phase.end)
         .addState(EntityState.INACTION, true)
         .newTimePair(phase.antic, phase.end)
         .addState(EntityState.TURNING_LOCKED, true)
         .newTimePair(preDelay, phase.contact)
         .addState(EntityState.ATTACKING, true)
         .addState(EntityState.PHASE_LEVEL, 2)
         .newTimePair(phase.contact, phase.end)
         .addState(EntityState.PHASE_LEVEL, 3);
   }

   private void onAttackResult(LivingEntityPatch<?> attackerPatch, Entity targetEntity, AttackResult attackResult) {
      if (attackResult.resultType == ResultType.SUCCESS && !(attackResult.damage <= 0.0F)) {
         LivingEntity livingTarget = this.getTrueEntity(targetEntity);
         if (livingTarget != null && livingTarget.isAlive()) {
            LivingEntityPatch<?> targetPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingTarget, LivingEntityPatch.class);
            if (targetPatch != null) {
               if (!(livingTarget instanceof Player)) {
                  livingTarget.lookAt(Anchor.FEET, ((LivingEntity)attackerPatch.getOriginal()).position());
               }

               float elapsedTime = Objects.requireNonNull(attackerPatch.getAnimator().getPlayerFor(this.getAccessor())).getElapsedTime();
               if (this.getPhaseByTime(elapsedTime) instanceof MurasamaPhase murasamaPhase) {
                  murasamaPhase.playHitAnimation(targetPatch, attackerPatch);
               }
            }
         }
      }
   }
}
