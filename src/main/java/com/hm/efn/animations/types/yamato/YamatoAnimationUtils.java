package com.hm.efn.animations.types.yamato;

import java.util.function.Supplier;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.collider.Collider;

public class YamatoAnimationUtils {
   public static YamatoPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      float wait = waitFrame / 60.0F;
      return new YamatoPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, joint, collider, groundHitAnimation, airHitAnimation);
   }

   public static YamatoPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      float wait = waitFrame / 60.0F;
      return new YamatoPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, joint, collider, groundHitAnimation, airHitAnimation);
   }

   public static YamatoPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      float wait = waitFrame / 60.0F;
      return new YamatoPhase(
         0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, impactMulti, joint, collider, groundHitAnimation, airHitAnimation
      );
   }

   public static YamatoPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      float armorNegationMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      float wait = waitFrame / 60.0F;
      return new YamatoPhase(
         0.0F,
         start,
         start,
         end,
         wait,
         Float.MAX_VALUE,
         hand,
         damageMulti,
         impactMulti,
         armorNegationMulti,
         joint,
         collider,
         groundHitAnimation,
         airHitAnimation
      );
   }

   public static YamatoPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      float wait = waitFrame / 60.0F;
      return new YamatoPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, colliders, groundHitAnimation, airHitAnimation);
   }

   public static YamatoPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      float wait = waitFrame / 60.0F;
      return new YamatoPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, colliders, groundHitAnimation, airHitAnimation);
   }

   public static YamatoPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      float wait = waitFrame / 60.0F;
      return new YamatoPhase(0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, impactMulti, colliders, groundHitAnimation, airHitAnimation);
   }

   public static YamatoPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      float armorNegationMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      float wait = waitFrame / 60.0F;
      return new YamatoPhase(
         0.0F, start, start, end, wait, Float.MAX_VALUE, hand, damageMulti, impactMulti, armorNegationMulti, colliders, groundHitAnimation, airHitAnimation
      );
   }
}
