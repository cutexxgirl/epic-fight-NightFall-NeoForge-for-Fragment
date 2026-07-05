package com.hm.efn.animations.property;

import java.util.function.Supplier;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;

public class EFNAnimationProperties {
   public static final ActionAnimationProperty<EFNAnimationProperties.SpecialPhase> MOVE_ROOT_PHASE = new ActionAnimationProperty();
   public static final ActionAnimationProperty<EFNAnimationProperties.SpecialPhase> INVISIBLE_PHASE = new ActionAnimationProperty();
   public static final AttackAnimationProperty<OnAttackHandlerFunc> AFTER_DAMAGE_ENTITY = new AttackAnimationProperty();
   public static final StaticAnimationProperty<Supplier<StaticAnimation>> ITEM_ANIMATION = new StaticAnimationProperty();

   public record SpecialPhase(float start, float end) {
      public boolean isInPhase(float t) {
         return t >= this.start && t <= this.end;
      }
   }
}
