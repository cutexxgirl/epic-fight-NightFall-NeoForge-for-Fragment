package com.hm.efn.gameasset.animations;

import com.hm.efn.animations.types.dodge.EFNDodgeAnimation_MURASAMA;
import com.hm.efn.animations.types.dodge.EFNDodgeAnimation_ROLL;
import com.hm.efn.animations.types.dodge.EFNDodgeAnimation_ROLL_MOB;
import com.hm.efn.animations.types.dodge.EFNDodgeAnimation_STEP;
import com.hm.efn.animations.types.dodge.EFNDodgeAnimation_STEP_MOB;
import com.hm.efn.animations.types.dodge.EFNDodgeAnimation_YAMATO;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.util.CameraLockUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Animations.ReusableSources;

public class EFNDodgeAnimations {
   public static AnimationAccessor<EFNDodgeAnimation_ROLL> DODGE_ROLL_F;
   public static AnimationAccessor<EFNDodgeAnimation_ROLL> DODGE_ROLL_B;
   public static AnimationAccessor<EFNDodgeAnimation_ROLL_MOB> DODGE_ROLL_F_MOB;
   public static AnimationAccessor<EFNDodgeAnimation_ROLL_MOB> DODGE_ROLL_B_MOB;
   public static AnimationAccessor<EFNDodgeAnimation_STEP> DODGE_STEP_F;
   public static AnimationAccessor<EFNDodgeAnimation_STEP> DODGE_STEP_B;
   public static AnimationAccessor<EFNDodgeAnimation_STEP> DODGE_STEP_L;
   public static AnimationAccessor<EFNDodgeAnimation_STEP> DODGE_STEP_R;
   public static AnimationAccessor<EFNDodgeAnimation_STEP_MOB> DODGE_STEP_F_MOB;
   public static AnimationAccessor<EFNDodgeAnimation_STEP_MOB> DODGE_STEP_B_MOB;
   public static AnimationAccessor<EFNDodgeAnimation_STEP_MOB> DODGE_STEP_L_MOB;
   public static AnimationAccessor<EFNDodgeAnimation_STEP_MOB> DODGE_STEP_R_MOB;
   public static AnimationAccessor<EFNDodgeAnimation_YAMATO> YAMATO_STEP_F;
   public static AnimationAccessor<EFNDodgeAnimation_YAMATO> YAMATO_STEP_B;
   public static AnimationAccessor<EFNDodgeAnimation_YAMATO> YAMATO_STEP_L;
   public static AnimationAccessor<EFNDodgeAnimation_YAMATO> YAMATO_STEP_R;
   public static AnimationAccessor<EFNDodgeAnimation_YAMATO> YAMATO_STEP_D;
   public static AnimationAccessor<EFNDodgeAnimation_YAMATO> YAMATO_STEP_U;
   public static AnimationAccessor<EFNDodgeAnimation_MURASAMA> MURASAMA_ROLL_F;
   public static AnimationAccessor<EFNDodgeAnimation_MURASAMA> MURASAMA_ROLL_B;
   public static AnimationAccessor<EFNDodgeAnimation_MURASAMA> MURASAMA_ROLL_F_AIR;
   public static AnimationAccessor<EFNDodgeAnimation_MURASAMA> MURASAMA_ROLL_F_EX;

   public static void build(AnimationBuilder builder) {
      DODGE_ROLL_F = builder.nextAccessor("biped/dodge/dodge_f", accessor -> new EFNDodgeAnimation_ROLL(0.05F, accessor, 0.6F, 0.8F, Armatures.BIPED));
      DODGE_ROLL_B = builder.nextAccessor("biped/dodge/dodge_b", accessor -> new EFNDodgeAnimation_ROLL(0.05F, accessor, 0.6F, 0.8F, Armatures.BIPED));
      DODGE_ROLL_F_MOB = builder.nextAccessor("biped/dodge_mob/dodge_f", accessor -> new EFNDodgeAnimation_ROLL_MOB(0.01F, accessor, Armatures.BIPED));
      DODGE_ROLL_B_MOB = builder.nextAccessor("biped/dodge_mob/dodge_b", accessor -> new EFNDodgeAnimation_ROLL_MOB(0.01F, accessor, Armatures.BIPED));
      DODGE_STEP_F = builder.nextAccessor("biped/dodge/step_f", accessor -> new EFNDodgeAnimation_STEP(0.05F, accessor, 0.6F, 1.65F, Armatures.BIPED));
      DODGE_STEP_B = builder.nextAccessor("biped/dodge/step_b", accessor -> new EFNDodgeAnimation_STEP(0.05F, accessor, 0.6F, 1.65F, Armatures.BIPED));
      DODGE_STEP_L = builder.nextAccessor("biped/dodge/step_l", accessor -> new EFNDodgeAnimation_STEP(0.05F, accessor, 0.6F, 1.65F, Armatures.BIPED));
      DODGE_STEP_R = builder.nextAccessor("biped/dodge/step_r", accessor -> new EFNDodgeAnimation_STEP(0.05F, accessor, 0.6F, 1.65F, Armatures.BIPED));
      DODGE_STEP_F_MOB = builder.nextAccessor("biped/dodge_mob/step_f", accessor -> new EFNDodgeAnimation_STEP_MOB(0.05F, accessor, Armatures.BIPED));
      DODGE_STEP_B_MOB = builder.nextAccessor("biped/dodge_mob/step_b", accessor -> new EFNDodgeAnimation_STEP_MOB(0.05F, accessor, Armatures.BIPED));
      DODGE_STEP_L_MOB = builder.nextAccessor("biped/dodge_mob/step_l", accessor -> new EFNDodgeAnimation_STEP_MOB(0.05F, accessor, Armatures.BIPED));
      DODGE_STEP_R_MOB = builder.nextAccessor("biped/dodge_mob/step_r", accessor -> new EFNDodgeAnimation_STEP_MOB(0.05F, accessor, Armatures.BIPED));
      MURASAMA_ROLL_F = builder.nextAccessor(
         "biped/dodge/hf_murasama_dodge_f", accessor -> new EFNDodgeAnimation_MURASAMA(0.05F, 0.5F, accessor, 0.6F, 0.8F, Armatures.BIPED)
      );
      MURASAMA_ROLL_F_EX = builder.nextAccessor(
         "biped/dodge/hf_murasama_dodge_f_ex",
         accessor -> (EFNDodgeAnimation_MURASAMA)new EFNDodgeAnimation_MURASAMA(0.1F, 0.5F, accessor, 0.6F, 0.8F, Armatures.BIPED)
            .newTimePair(0.0F, 9.223372E18F)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addStateRemoveOld(EntityState.LOOK_TARGET, true)
            .newTimePair(0.0F, 0.1F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(0.05F, ReusableSources.PLAY_SOUND, Side.SERVER).params((SoundEvent)EpicFightSounds.ROLL.get()),
                  InTimeEvent.create(0.55F, (livingEntityPatch, assetAccessor, animationParameters) -> CameraLockUtil.endLockOn(), Side.LOCAL_CLIENT)
               }
            )
            .addEvents(
               StaticAnimationProperty.ON_BEGIN_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> CameraLockUtil.startLockOn(360.0F, 3.0), Side.LOCAL_CLIENT)
               }
            )
            .addEvents(
               StaticAnimationProperty.ON_END_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create((livingEntityPatch, assetAccessor, animationParameters) -> CameraLockUtil.endLockOn(), Side.LOCAL_CLIENT)
               }
            )
      );
      MURASAMA_ROLL_F_AIR = builder.nextAccessor(
         "biped/dodge/hf_murasama_dodge_f_air",
         accessor -> (EFNDodgeAnimation_MURASAMA)new EFNDodgeAnimation_MURASAMA(0.1F, 0.5F, accessor, 0.6F, 0.8F, Armatures.BIPED)
            .newTimePair(0.0F, 0.2F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 9.223372E18F}))
      );
      MURASAMA_ROLL_B = builder.nextAccessor(
         "biped/dodge/hf_murasama_dodge_b", accessor -> new EFNDodgeAnimation_MURASAMA(0.05F, 0.5F, accessor, 0.6F, 0.8F, Armatures.BIPED)
      );
      YAMATO_STEP_F = builder.nextAccessor(
         "biped/dodge/dmcyamato_dodge_f",
         accessor -> (EFNDodgeAnimation_YAMATO)new EFNDodgeAnimation_YAMATO(0.1F, 0.3F, accessor, 0.6F, 1.65F, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.7F}))
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.07F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      YAMATO_STEP_B = builder.nextAccessor(
         "biped/dodge/dmcyamato_dodge_b",
         accessor -> (EFNDodgeAnimation_YAMATO)new EFNDodgeAnimation_YAMATO(0.1F, 0.3F, accessor, 0.6F, 1.65F, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.7F}))
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.07F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      YAMATO_STEP_L = builder.nextAccessor(
         "biped/dodge/dmcyamato_dodge_l",
         accessor -> (EFNDodgeAnimation_YAMATO)new EFNDodgeAnimation_YAMATO(0.1F, 0.3F, accessor, 0.6F, 1.65F, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.7F}))
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.07F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      YAMATO_STEP_R = builder.nextAccessor(
         "biped/dodge/dmcyamato_dodge_r",
         accessor -> (EFNDodgeAnimation_YAMATO)new EFNDodgeAnimation_YAMATO(0.1F, 0.3F, accessor, 0.6F, 1.65F, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.7F}))
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.07F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      YAMATO_STEP_D = builder.nextAccessor(
         "biped/dodge/dmcyamato_dodge_down",
         accessor -> (EFNDodgeAnimation_YAMATO)new EFNDodgeAnimation_YAMATO(0.1F, 0.3F, accessor, 0.6F, 1.65F, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.4F}))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.07F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      YAMATO_STEP_U = builder.nextAccessor(
         "biped/dodge/dmcyamato_dodge_up",
         accessor -> (EFNDodgeAnimation_YAMATO)new EFNDodgeAnimation_YAMATO(0.1F, 0.3F, accessor, 0.6F, 1.65F, Armatures.BIPED)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.4F}))
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.07F,
                     (entityPatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EFNParticles.NOWEAPON_AFTERIMAGE_BLUE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  )
               }
            )
      );
   }
}
