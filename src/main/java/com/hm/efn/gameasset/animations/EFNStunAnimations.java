package com.hm.efn.gameasset.animations;

import com.hm.efn.animations.types.stun.EFNStunAnimation;
import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;

public class EFNStunAnimations {
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITUP_GROUND_0;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITUP_0;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITUP_1;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITUP_2;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITUP_3;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HIT_GROUND_L0;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HIT_GROUND_R0;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HIT_AIR_L0;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HIT_AIR_R0;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITDOWN_0;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITDOWN_1;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITDOWN_AIR;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITBACK_1;
   public static AnimationAccessor<EFNStunAnimation> BIPED_HITBACK_AIR;

   public static void build(AnimationBuilder builder) {
      BIPED_HITUP_GROUND_0 = builder.nextAccessor(
         "biped/yamato/biped_hitup_ground0",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.3F}))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.3F && elapsedTime < 0.35F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITUP_0 = builder.nextAccessor(
         "biped/yamato/biped_hitup_0",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.4F}))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.4F && elapsedTime < 0.45F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITUP_1 = builder.nextAccessor(
         "biped/yamato/biped_hitup_1",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.4F}))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.4F && elapsedTime < 0.45F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITUP_2 = builder.nextAccessor(
         "biped/yamato/biped_hitup_2",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.6F}))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.6F && elapsedTime < 0.65F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 7, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITUP_3 = builder.nextAccessor(
         "biped/yamato/biped_hitup_3",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.7F}))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.7F && elapsedTime < 0.75F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HIT_GROUND_R0 = builder.nextAccessor(
         "biped/yamato/biped_hit_ground_r0",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HIT_GROUND_L0 = builder.nextAccessor(
         "biped/yamato/biped_hit_ground_l0",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HIT_AIR_R0 = builder.nextAccessor(
         "biped/yamato/biped_hit_air_r0",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.4F}))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.5F && elapsedTime < 0.55F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HIT_AIR_L0 = builder.nextAccessor(
         "biped/yamato/biped_hit_air_l0",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.4F}))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.5F && elapsedTime < 0.55F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 10, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITBACK_1 = builder.nextAccessor(
         "biped/yamato/biped_hitback_1",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 3, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITBACK_AIR = builder.nextAccessor(
         "biped/yamato/biped_hitback_air",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.5F}))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.6F && elapsedTime < 0.75F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 1, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITDOWN_0 = builder.nextAccessor(
         "biped/yamato/biped_hitdown_0",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 20, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITDOWN_1 = builder.nextAccessor(
         "biped/yamato/biped_hitdown_1",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 20, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
      BIPED_HITDOWN_AIR = builder.nextAccessor(
         "biped/yamato/biped_hitdown_air",
         accessor -> (EFNStunAnimation)new EFNStunAnimation(0.01F, accessor, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.13F}))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
               if (elapsedTime >= 0.14F && elapsedTime < 0.17F) {
                  float dpx = (float)((LivingEntity)entitypatch.getOriginal()).getX();
                  float dpy = (float)((LivingEntity)entitypatch.getOriginal()).getY();
                  float dpz = (float)((LivingEntity)entitypatch.getOriginal()).getZ();
                  BlockState block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, dpy, dpz));

                  while ((block.getBlock() instanceof BushBlock || block.isAir()) && !block.is(Blocks.VOID_AIR)) {
                     block = ((LivingEntity)entitypatch.getOriginal()).level().getBlockState(new MutableBlockPos(dpx, --dpy, dpz));
                  }

                  float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).getY() - dpy) - 1.0, 0.0);
                  return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
               } else {
                  return speed;
               }
            })
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.KNOCKBACKRESISTANT, 20, 1, false, false, false)),
                     Side.SERVER
                  )
               }
            )
      );
   }
}
