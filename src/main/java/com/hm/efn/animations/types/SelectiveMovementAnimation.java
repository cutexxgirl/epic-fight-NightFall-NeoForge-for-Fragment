package com.hm.efn.animations.types;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationVariables;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationVariables.IndependentVariableKey;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.Layer.LayerType;
import yesman.epicfight.api.client.animation.Layer.Priority;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SelectiveMovementAnimation extends MovementAnimation {
   public static final IndependentVariableKey<Integer> PREVIOUS_STATE = AnimationVariables.unsyncIndependent(animator -> 0, true);
   private final Function<LivingEntityPatch<?>, Integer> selector;
   private final List<AssetAccessor<? extends StaticAnimation>> animationsInEachState;

   @SafeVarargs
   public SelectiveMovementAnimation(
      Function<LivingEntityPatch<?>, Integer> selector,
      AnimationAccessor<? extends SelectiveMovementAnimation> accessor,
      AssetAccessor<? extends StaticAnimation>... selectOptions
   ) {
      super(0.15F, false, accessor, null);
      this.selector = selector;
      this.animationsInEachState = List.of(selectOptions);

      for (AssetAccessor<? extends StaticAnimation> subAnimation : this.animationsInEachState) {
         ((StaticAnimation)Objects.requireNonNull((StaticAnimation)subAnimation.get()))
            .addEvents(new AnimationEvent[]{SimpleEvent.create((entitypatch, animation, params) -> this.handleAnimationEnd(entitypatch), Side.BOTH)});
      }
   }

   private void handleAnimationEnd(LivingEntityPatch<?> entitypatch) {
      int currentStateId = this.selector.apply(entitypatch);
      Optional<Integer> prevState = entitypatch.getAnimator().getVariables().get(PREVIOUS_STATE, this.getAccessor());
      prevState.ifPresentOrElse(prevStateId -> {
         if (prevStateId != currentStateId) {
            entitypatch.getAnimator().playAnimation(this.animationsInEachState.get(currentStateId), 0.0F);
            entitypatch.getAnimator().getVariables().put(PREVIOUS_STATE, this.getAccessor(), currentStateId);
         } else {
            entitypatch.getAnimator().playAnimation(this.animationsInEachState.get(currentStateId), 0.0F);
         }
      }, () -> {
         entitypatch.getAnimator().playAnimation(this.animationsInEachState.get(0), 0.0F);
         entitypatch.getAnimator().getVariables().put(PREVIOUS_STATE, this.getAccessor(), 0);
      });
   }

   public void begin(LivingEntityPatch<?> entitypatch) {
      super.begin(entitypatch);
      int result = this.selector.apply(entitypatch);
      entitypatch.getAnimator().getVariables().put(PREVIOUS_STATE, this.getAccessor(), result);
      entitypatch.getAnimator().playAnimation(this.animationsInEachState.get(result), 0.0F);
   }

   public void tick(LivingEntityPatch<?> entitypatch) {
      super.tick(entitypatch);
      int currentStateId = this.selector.apply(entitypatch);
      Optional<Integer> prevState = entitypatch.getAnimator().getVariables().get(PREVIOUS_STATE, this.getAccessor());
      prevState.ifPresent(prevStateId -> {
         if (prevStateId != currentStateId) {
            entitypatch.getAnimator().playAnimation(this.animationsInEachState.get(currentStateId), 0.0F);
            entitypatch.getAnimator().getVariables().put(PREVIOUS_STATE, this.getAccessor(), currentStateId);
         }
      });
   }

   public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
      if (animation.isLinkAnimation()) {
         return 1.0F;
      }

      float movementSpeed = 1.0F;
      if (Math.abs(((LivingEntity)entitypatch.getOriginal()).walkAnimation.speed() - ((LivingEntity)entitypatch.getOriginal()).walkAnimation.speed(1.0F))
         < 0.007F) {
         movementSpeed *= ((LivingEntity)entitypatch.getOriginal()).walkAnimation.speed() * 1.16F;
      }

      return movementSpeed;
   }

   public boolean isMetaAnimation() {
      return true;
   }

   public List<AssetAccessor<? extends StaticAnimation>> getSubAnimations() {
      return this.animationsInEachState;
   }

   @OnlyIn(Dist.CLIENT)
   public Priority getPriority() {
      return ((StaticAnimation)Objects.requireNonNull((StaticAnimation)this.animationsInEachState.get(0).get())).getPriority();
   }

   @OnlyIn(Dist.CLIENT)
   public LayerType getLayerType() {
      return ((StaticAnimation)Objects.requireNonNull((StaticAnimation)this.animationsInEachState.get(0).get())).getLayerType();
   }

   public boolean canBePlayedReverse() {
      return true;
   }
}
