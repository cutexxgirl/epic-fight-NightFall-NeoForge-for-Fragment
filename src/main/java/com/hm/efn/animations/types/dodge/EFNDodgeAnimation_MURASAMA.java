package com.hm.efn.animations.types.dodge;

import java.util.function.Function;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;

public class EFNDodgeAnimation_MURASAMA extends DodgeAnimation {
   public static final Function<DamageSource, ResultType> DODGEABLE_SOURCE_VALIDATOR = damagesource -> damagesource.getEntity() != null
         && !damagesource.is(DamageTypeTags.IS_EXPLOSION)
         && !damagesource.is(DamageTypes.MAGIC)
         && !damagesource.is(DamageTypeTags.BYPASSES_ARMOR)
         && !damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         && !damagesource.is(EpicFightDamageTypeTags.BYPASS_DODGE)
      ? ResultType.MISSED
      : ResultType.SUCCESS;

   public EFNDodgeAnimation_MURASAMA(
      float transitionTime, AnimationAccessor<? extends DodgeAnimation> accessor, float width, float height, AssetAccessor<? extends Armature> armature
   ) {
      this(transitionTime, 10.0F, accessor, width, height, armature);
   }

   public EFNDodgeAnimation_MURASAMA(
      float transitionTime,
      float delayTime,
      AnimationAccessor<? extends DodgeAnimation> accessor,
      float width,
      float height,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, delayTime, accessor, width, height, armature);
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, delayTime)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.COMBO_ATTACKS_DOABLE, false)
         .addState(EntityState.SKILL_EXECUTABLE, false)
         .addState(EntityState.INACTION, true)
         .newTimePair(0.0F, 0.5F)
         .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR)
         .addState(EntityState.PROJECTILE_IMPACT_RESULT, IGNORE_ALL_PROJECTILES);
      this.addProperty(ActionAnimationProperty.AFFECT_SPEED, false);
      this.addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create(ReusableSources.RESTORE_BOUNDING_BOX, Side.BOTH)});
      this.addEvents(
         StaticAnimationProperty.TICK_EVENTS,
         new AnimationEvent[]{SimpleEvent.create(ReusableSources.RESIZE_BOUNDING_BOX, Side.BOTH).params(EntityDimensions.scalable(width, height))}
      );
   }
}
