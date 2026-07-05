package com.hm.efn.animations.types.yamato;

import net.minecraft.core.registries.BuiltInRegistries;

import com.hm.efn.EFNCommonConfig;
import com.hm.efn.animations.types.stun.EFNStunAnimation;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.mixin.ArmaturesAccessor;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.HitAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.registry.entries.EpicFightMobEffects;

public class YamatoPhase extends AvalonPhase {
   private final Supplier<? extends StaticAnimation> groundHitAnimation;
   private final Supplier<? extends StaticAnimation> airHitAnimation;

   public YamatoPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public YamatoPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public YamatoPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public YamatoPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      float phaseArmorNegationMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, phaseArmorNegationMulti, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public YamatoPhase(
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(hand, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public YamatoPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public YamatoPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public YamatoPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public YamatoPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      float phaseArmorNegationMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, phaseArmorNegationMulti, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.airHitAnimation = airHitAnimation;
   }

   public void playHitAnimation(LivingEntityPatch<?> targetPatch, LivingEntityPatch<?> attackerPatch) {
      if (!this.shouldSkipHit(targetPatch, attackerPatch)) {
         LivingEntity target = (LivingEntity)targetPatch.getOriginal();
         AnimationPlayer animationPlayer = targetPatch.getAnimator().getPlayerFor(null);
         if (animationPlayer != null) {
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
            boolean isWhitelisted = entityId != null && ((List)EFNCommonConfig.YAMATO_STUNANIMATION_WHITELIST.get()).contains(entityId.toString());
            if (!isWhitelisted) {
               DynamicAnimation currentAnimation = (DynamicAnimation)animationPlayer.getRealAnimation().get();
               boolean isStunAnimation = currentAnimation instanceof EFNStunAnimation
                  || currentAnimation instanceof HitAnimation
                  || currentAnimation instanceof LongHitAnimation;
               if (!isStunAnimation && !targetPatch.isStunned()) {
                  return;
               }

               if (currentAnimation.equals(targetPatch.getHitAnimation(StunType.NEUTRALIZE))) {
                  return;
               }

               if (targetPatch.getEntityState().hurtLevel() >= 2) {
                  return;
               }
            }
         }

         Supplier<? extends StaticAnimation> animationSupplier = target.onGround() ? this.groundHitAnimation : this.airHitAnimation;
         if (animationSupplier != null && animationSupplier.get() != null) {
            targetPatch.playAnimationSynchronized(animationSupplier.get().getAccessor(), 0.0F);
         }
      }
   }

   private boolean shouldSkipHit(LivingEntityPatch<?> targetPatch, LivingEntityPatch<?> attackerPatch) {
      if (targetPatch != null && targetPatch.getOriginal() != null && attackerPatch != null) {
         LivingEntity target = (LivingEntity)targetPatch.getOriginal();
         LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
         if (target != attacker && target != attacker.getVehicle() && !attacker.getPassengers().contains(target)) {
            if (attacker instanceof DoppelgangerEntity doppelganger && target == doppelganger.getOwner()) {
               return true;
            } else if (target instanceof DoppelgangerEntity doppelganger && attacker == doppelganger.getOwner()) {
               return true;
            } else {
               if (target instanceof DoppelgangerEntity || target instanceof VFXEntity) {
                  return true;
               }

               if (!target.isInvulnerable()
                  && !target.hasEffect(EpicFightMobEffects.STUN_IMMUNITY)
                  && !target.hasEffect(EFNMobEffectRegistry.SIN_STUN_IMMUNITY)
                  && !target.hasEffect(EFNMobEffectRegistry.INVINCIBILITY_EFFECT)) {
                  if (!(target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
                     ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
                     if (entityId != null) {
                        String entityIdString = entityId.toString();
                        if (((List)EFNCommonConfig.YAMATO_STUNANIMATION_BLACKLIST.get()).contains(entityIdString)) {
                           return true;
                        }

                        if (((List)EFNCommonConfig.YAMATO_STUNANIMATION_WHITELIST.get()).contains(entityIdString)) {
                           return false;
                        }
                     }

                     Armature armature = targetPatch.getArmature();
                     Map<EntityType<?>, AssetAccessor<? extends Armature>> map = ArmaturesAccessor.getEntityTypeArmatureMapper();
                     AssetAccessor<? extends Armature> armatureAccessor = map.get(target.getType());
                     return !(armature instanceof HumanoidArmature) && armatureAccessor != Armatures.BIPED;
                  } else {
                     return true;
                  }
               } else {
                  return true;
               }
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
