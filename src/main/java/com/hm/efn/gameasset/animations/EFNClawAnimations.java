package com.hm.efn.gameasset.animations;

import com.guhao.vix.camera.VIXCameraFOV;
import com.guhao.vix.client.event.ScreenEffectEngine;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.Claw;
import com.hm.efn.client.screeneffect.BlackWhiteFlashEffect;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffekUnits;
import com.hm.efn.util.ParticleEffectInvoker;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.damagesource.StunType;

public class EFNClawAnimations {
   public static final Collider CLAW = new OBBCollider(0.8, 0.8, 0.8, 0.0, 0.0, 0.4);
   public static final Collider CLAW_AUTO3 = new OBBCollider(1.5, 1.5, 1.5, 0.0, 0.5, 0.0);
   public static final Collider CLAW_ROAR = new OBBCollider(1.5, 1.5, 2.5, 0.0, 0.8, -1.5);
   public static AnimationAccessor<StaticAnimation> NF_CLAW_IDLE;
   public static AnimationAccessor<AvalonMovementAnimation> NF_CLAW_WALK;
   public static AnimationAccessor<AvalonMovementAnimation> NF_CLAW_RUN;
   public static AnimationAccessor<AvalonAttackAnimation> NF_CLAW_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> NF_CLAW_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> NF_CLAW_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> NF_CLAW_AIRSLASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_CLAW_DASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_CLAW_BEASTROAR;

   public static void build(AnimationBuilder builder) {
      ArmatureAccessor<Armature> Claw = ArmatureAccessor.create("efn", "weapon/nf_claw", Armature::new);
      NF_CLAW_IDLE = builder.nextAccessor("biped/nf_claw/nf_claw_idle", accessor -> new StaticAnimation(true, accessor, Claw));
      NF_CLAW_WALK = builder.nextAccessor("biped/nf_claw/nf_claw_walk", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, Claw, 1.0F));
      NF_CLAW_RUN = builder.nextAccessor("biped/nf_claw/nf_claw_run", accessor -> new AvalonMovementAnimation(0.1F, true, accessor, Claw, 0.8F));
      NF_CLAW_AUTO1 = builder.nextAccessor(
         "biped/nf_claw/nf_claw_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.12F,
               accessor,
               Claw,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(26, 34, 40, InteractionHand.MAIN_HAND, 0.8F, 0.8F, Claw.get().searchJointByName("Claw_R"), CLAW)
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_CLAW)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.43333334F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && ((LivingEntity)entityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              1.0707964F,
                              1.0F,
                              entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NF_CLAW_AUTO2 = builder.nextAccessor(
         "biped/nf_claw/nf_claw_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Claw,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(19, 29, 32, InteractionHand.MAIN_HAND, 0.6F, 0.6F, Claw.get().searchJointByName("Claw_R"), CLAW),
                  AvalonAnimationUtils.createSimplePhase(39, 55, 65, InteractionHand.MAIN_HAND, 0.7F, 0.7F, Claw.get().searchJointByName("Claw_L"), CLAW)
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_CLAW)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.31666666F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && ((LivingEntity)entityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              0.0F,
                              1.0F,
                              entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.65F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && ((LivingEntity)entityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              (float) (Math.PI / 4),
                              1.0F,
                              entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NF_CLAW_AUTO3 = builder.nextAccessor(
         "biped/nf_claw/nf_claw_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.05F,
               accessor,
               Claw,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(39, 51, 65, InteractionHand.MAIN_HAND, 1.0F, 1.0F, Claw.get().searchJointByName("Root"), CLAW_AUTO3)
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_CLAW)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(48, 8, 3.0F, 2.0F, 2.0F),
                  ParticleEffectInvoker.simpleGroundSplit(48, 1.0, 0.0, 0.0, 0.0, 1.8F, true),
                  InTimeEvent.create(
                     0.65F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && ((LivingEntity)entityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              1.0707964F,
                              1.0F,
                              entityPatch.getOriginal()
                           );
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              (float) (Math.PI / 2),
                              1.0F,
                              entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NF_CLAW_DASH = builder.nextAccessor(
         "biped/nf_claw/nf_claw_dash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Claw,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(20, 27, 45, InteractionHand.MAIN_HAND, 0.5F, 0.5F, Claw.get().searchJointByName("Claw_L"), CLAW),
                  AvalonAnimationUtils.createSimplePhase(23, 30, 45, InteractionHand.MAIN_HAND, 0.5F, 0.5F, Claw.get().searchJointByName("Claw_R"), CLAW)
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_CLAW)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.33333334F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && ((LivingEntity)entityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              0.0F,
                              1.0F,
                              entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.38333333F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && ((LivingEntity)entityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              (float) Math.PI,
                              1.0F,
                              entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NF_CLAW_AIRSLASH = builder.nextAccessor(
         "biped/nf_claw/nf_claw_airslash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Claw,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(20, 26, 40, InteractionHand.MAIN_HAND, 0.6F, 0.6F, Claw.get().searchJointByName("Claw_L"), CLAW),
                  AvalonAnimationUtils.createSimplePhase(41, 48, 56, InteractionHand.MAIN_HAND, 0.7F, 0.6F, Claw.get().searchJointByName("Claw_R"), CLAW)
               }
            )
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.25F}))
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_CLAW)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     0.33333334F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && ((LivingEntity)entityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              (float) (-Math.PI / 2),
                              1.0F,
                              entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.68333334F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()
                           && (Boolean)EFNClientConfig.CLAW_SKILL_VFX.get()
                           && ((LivingEntity)entityPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
                           com.hm.efn.client.effek.Claw.playClaw(
                              com.hm.efn.client.effek.Claw.Type.LEVEL1,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              (float)(EffekUnits.getRY(entityPatch) - (Math.PI / 2)),
                              (float) (Math.PI / 2),
                              1.0F,
                              entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  )
               }
            )
      );
      NF_CLAW_BEASTROAR = builder.nextAccessor(
         "biped/nf_claw/nf_claw_beastroar",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Claw,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(27, 33, 33, InteractionHand.MAIN_HAND, 0.6F, 0.6F, Claw.get().searchJointByName("Root"), CLAW_ROAR),
                  AvalonAnimationUtils.createSimplePhase(33, 39, 39, InteractionHand.MAIN_HAND, 0.6F, 0.6F, Claw.get().searchJointByName("Root"), CLAW_ROAR),
                  AvalonAnimationUtils.createSimplePhase(39, 45, 45, InteractionHand.MAIN_HAND, 0.6F, 0.6F, Claw.get().searchJointByName("Root"), CLAW_ROAR),
                  AvalonAnimationUtils.createSimplePhase(45, 51, 60, InteractionHand.MAIN_HAND, 0.6F, 0.6F, Claw.get().searchJointByName("Root"), CLAW_ROAR)
               }
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_CLAW)
            .addEvents(
               new AnimationEvent[]{
                  AvalonEventUtils.simpleCameraShake(27, 30, 4.0F, 4.0F, 2.0F),
                  ParticleEffectInvoker.simpleGroundSplit(29, 0.5, 0.0, 0.0, 0.0, 2.0F, true),
                  ParticleEffectInvoker.createBeastRoarEffect(27.0F),
                  ParticleEffectInvoker.createRoarShockwave(27.0F),
                  InTimeEvent.create(0.4F, (entitypatch, self, params) -> VIXCameraFOV.applyExplosionPulse(5.0F), Side.CLIENT),
                  InTimeEvent.create(
                     0.45F,
                     (entitypatch, self, params) -> {
                        BlackWhiteFlashEffect effect2 = new BlackWhiteFlashEffect(
                           ((LivingEntity)entitypatch.getOriginal()).position(), BlackWhiteFlashEffect.ImpactMode.LIGHT
                        );
                        ScreenEffectEngine.PushScreenEffectADD(effect2);
                     },
                     Side.CLIENT
                  )
               }
            )
      );
   }
}
