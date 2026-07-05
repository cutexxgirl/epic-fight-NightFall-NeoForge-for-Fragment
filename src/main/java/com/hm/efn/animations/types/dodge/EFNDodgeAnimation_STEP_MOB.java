package com.hm.efn.animations.types.dodge;

import java.util.function.Function;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.entity.DodgeLocationIndicator;

public class EFNDodgeAnimation_STEP_MOB extends ActionAnimation {
   public static final Function<DamageSource, ResultType> DODGEABLE_SOURCE_VALIDATOR = damagesource -> damagesource.getEntity() != null
         && !damagesource.is(DamageTypeTags.IS_EXPLOSION)
         && !damagesource.is(DamageTypes.MAGIC)
         && !damagesource.is(DamageTypeTags.BYPASSES_ARMOR)
         && !damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         && !damagesource.is(EpicFightDamageTypeTags.BYPASS_DODGE)
      ? ResultType.MISSED
      : ResultType.SUCCESS;

   public EFNDodgeAnimation_STEP_MOB(float transitionTime, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature) {
      this(transitionTime, 10.0F, accessor, armature);
   }

   public EFNDodgeAnimation_STEP_MOB(
      float transitionTime, float delayTime, AnimationAccessor<? extends ActionAnimation> accessor, AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, delayTime, accessor, armature);
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, 0.35F)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .addState(EntityState.INACTION, true)
         .newTimePair(0.0F, 0.25F)
         .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR)
         .addState(EntityState.PROJECTILE_IMPACT_RESULT, DodgeAnimation.IGNORE_ALL_PROJECTILES);
      this.addProperty(ActionAnimationProperty.AFFECT_SPEED, true);
   }

   public void begin(LivingEntityPatch<?> entitypatch) {
      super.begin(entitypatch);
      if (!entitypatch.isLogicalClient() && entitypatch != null) {
         ((LivingEntity)entitypatch.getOriginal()).level().addFreshEntity(new DodgeLocationIndicator(entitypatch));
      }
   }
}
