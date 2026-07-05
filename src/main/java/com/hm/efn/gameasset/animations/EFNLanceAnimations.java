package com.hm.efn.gameasset.animations;

import com.guhao.vix.camera.VIXCameraFOV;
import com.guhao.vix.client.event.ScreenEffectEngine;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.effek.Background_1_Effek;
import com.hm.efn.client.effek.ChargingEffek;
import com.hm.efn.client.effek.Flame2Effek;
import com.hm.efn.client.effek.FlameEffek;
import com.hm.efn.client.screeneffect.BlackWhiteFlashEffect;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNExtraDamageInstance;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffectConditionParticleTrail;
import com.hm.efn.util.EffectEntityInvoker;
import com.hm.efn.util.EffekUnits;
import com.hm.efn.util.WeaponTrailGroundSplitter;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonMovementAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class EFNLanceAnimations {
   public static final Collider MEEN_LANCE_COLL = new OBBCollider(0.35, 0.6, 1.8, 0.0, 0.3, -0.7);
   public static final Collider MEEN_LANCE_1 = new OBBCollider(0.9, 0.9, 1.8, 0.0, 0.0, -1.2);
   public static final Collider MEEN_LANCE_CHARGE3 = new OBBCollider(2.0, 2.0, 2.0, 0.0, 0.0, 0.0);
   public static AnimationAccessor<StaticAnimation> NF_MEEN_IDLE;
   public static AnimationAccessor<AvalonMovementAnimation> NF_MEEN_WALK;
   public static AnimationAccessor<AvalonMovementAnimation> NF_MEEN_RUN;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_AUTO1;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_AUTO2;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_AUTO3;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_AUTO4;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_DASH;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_AIRSLASH;
   public static AnimationAccessor<AttackAnimation> NF_MEEN_CHARGING;
   public static AnimationAccessor<AttackAnimation> NF_MEEN_CHARGING_MOB;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_CHARGE1;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_CHARGE2;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_CHARGE3;
   public static AnimationAccessor<AvalonAttackAnimation> NF_MEEN_FINISHER;

   public static void build(AnimationBuilder builder) {
      NF_MEEN_IDLE = builder.nextAccessor("biped/nf_meen/nf_meen_idle", accessor -> new StaticAnimation(true, accessor, Armatures.BIPED));
      NF_MEEN_WALK = builder.nextAccessor(
         "biped/nf_meen/nf_meen_walk",
         accessor -> (AvalonMovementAnimation)new AvalonMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.5F)
            .addEvents(
               new AnimationEvent[]{
                  EffectConditionParticleTrail.buffedParticleTrail(
                     0,
                     200,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -1.85F),
                     new Vec3(0.0, 0.0, -1.9F),
                     2.0F,
                     1,
                     ParticleTypes.SMALL_FLAME,
                     0.6F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  )
               }
            )
      );
      NF_MEEN_RUN = builder.nextAccessor(
         "biped/nf_meen/nf_meen_run",
         accessor -> (AvalonMovementAnimation)new AvalonMovementAnimation(0.15F, true, accessor, Armatures.BIPED, 1.5F)
            .addEvents(
               new AnimationEvent[]{
                  EffectConditionParticleTrail.buffedParticleTrail(
                     0,
                     200,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -1.85F),
                     new Vec3(0.0, 0.0, -1.9F),
                     2.0F,
                     1,
                     ParticleTypes.SMALL_FLAME,
                     0.6F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  )
               }
            )
      );
      NF_MEEN_AUTO1 = builder.nextAccessor(
         "biped/nf_meen/nf_meen_auto1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.15F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     32, 40, 50, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_HIT.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
            .addEvents(
               new AnimationEvent[]{
                  EffectConditionParticleTrail.buffedParticleTrail(
                     32,
                     40,
                     InteractionHand.MAIN_HAND,
                     new Vec3(-0.2, 0.0, -1.7F),
                     new Vec3(0.1, 0.0, -2.15F),
                     10.0F,
                     1,
                     ParticleTypes.FLAME,
                     0.7F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  )
               }
            )
      );
      NF_MEEN_AUTO2 = builder.nextAccessor(
         "biped/nf_meen/nf_meen_auto2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     37, 44, 60, InteractionHand.MAIN_HAND, 0.8F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
            .addEvents(
               new AnimationEvent[]{
                  EffectConditionParticleTrail.buffedParticleTrail(
                     33,
                     44,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -1.5),
                     new Vec3(0.0, 0.0, -1.8F),
                     4.0F,
                     3,
                     ParticleTypes.LAVA,
                     0.2F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  )
               }
            )
      );
      NF_MEEN_AUTO3 = builder.nextAccessor(
         "biped/nf_meen/nf_meen_auto3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     40, 46, 55, InteractionHand.MAIN_HAND, 0.5F, 0.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     61, 70, 72, InteractionHand.MAIN_HAND, 0.5F, 0.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.clearFireWind(20),
                  EffectConditionParticleTrail.buffedParticleTrail(
                     30,
                     43,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -1.7F),
                     new Vec3(0.0, 0.0, -2.15F),
                     5.0F,
                     1,
                     ParticleTypes.FLAME,
                     0.3F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  ),
                  EffectConditionParticleTrail.buffedParticleTrail(
                     50,
                     69,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -1.7F),
                     new Vec3(0.0, 0.0, -2.15F),
                     5.0F,
                     1,
                     ParticleTypes.LAVA,
                     0.3F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  )
               }
            )
      );
      NF_MEEN_AUTO4 = builder.nextAccessor(
         "biped/nf_meen/nf_meen_auto4",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     30, 36, 50, InteractionHand.MAIN_HAND, 0.5F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     64, 70, 80, InteractionHand.MAIN_HAND, 0.5F, 0.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
            .addEvents(
               new AnimationEvent[]{
                  EffectConditionParticleTrail.buffedParticleTrail(
                     25,
                     40,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -2.0),
                     new Vec3(0.0, 0.0, -2.15F),
                     8.0F,
                     3,
                     ParticleTypes.FLAME,
                     0.5F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  ),
                  EffectConditionParticleTrail.buffedParticleTrail(
                     58,
                     72,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -2.0),
                     new Vec3(0.0, 0.0, -2.15F),
                     8.0F,
                     2,
                     ParticleTypes.LAVA,
                     0.1F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  )
               }
            )
      );
      NF_MEEN_DASH = builder.nextAccessor(
         "biped/nf_meen/nf_meen_dash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     30, 36, 42, InteractionHand.MAIN_HAND, 0.7F, 0.7F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     42, 48, 65, InteractionHand.MAIN_HAND, 0.7F, 0.7F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     65, 74, 90, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(ActionAnimationProperty.AFFECT_SPEED, true)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, false)
            .newTimePair(0.0F, 0.85F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
      );
      NF_MEEN_AIRSLASH = builder.nextAccessor(
         "biped/nf_meen/nf_meen_airslash",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     30, 40, 48, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.01F, 0.2F}))
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
      );
      NF_MEEN_CHARGING = builder.nextAccessor(
         "biped/nf_meen/nf_meen_charge_all",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.2F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.9F, 0.9F, 1.4F, 1.4F, 1.4F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                  new Phase(2.3F, 2.3F, 2.6F, 2.6F, 2.6F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                  new Phase(3.5F, 3.5F, 3.8F, 2.1474836E9F, 2.1474836E9F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
               }
            )
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .newTimePair(0.0F, 2.1474836E9F)
            .addState(EntityState.MOVEMENT_LOCKED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonFireWind(50, 0.0, 0.1, 0.0, 1.15F, 0.0F, 0.0F, 60.0F),
                  InTimeEvent.create(1.2F, (entityPatch, self, params) -> {
                     if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillContainer skillContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                        if (skillContainer != null) {
                           SkillDataManager skillDataManager = skillContainer.getDataManager();
                           if (skillDataManager.hasData(EFNSKillDataKeys.CHARGE_STAGE)) {
                              skillDataManager.setDataSync(EFNSKillDataKeys.CHARGE_STAGE, 1);
                           }
                        }
                     }
                  }, Side.SERVER),
                  InTimeEvent.create(2.4F, (entityPatch, self, params) -> {
                     if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillContainer skillContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                        if (skillContainer != null) {
                           SkillDataManager skillDataManager = skillContainer.getDataManager();
                           if (skillDataManager.hasData(EFNSKillDataKeys.CHARGE_STAGE)) {
                              skillDataManager.setDataSync(EFNSKillDataKeys.CHARGE_STAGE, 2);
                           }
                        }
                     }
                  }, Side.SERVER),
                  InTimeEvent.create(3.5F, (entityPatch, self, params) -> {
                     if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillContainer skillContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                        if (skillContainer != null) {
                           SkillDataManager skillDataManager = skillContainer.getDataManager();
                           if (skillDataManager.hasData(EFNSKillDataKeys.CHARGE_STAGE)) {
                              skillDataManager.setDataSync(EFNSKillDataKeys.CHARGE_STAGE, 3);
                           }
                        }
                     }
                  }, Side.SERVER),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_CHARGE_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL1, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     2.35F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_CHARGE_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL2, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     3.55F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_CHARGE_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL3, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     3.6F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_CHARGE_VFX.get()) {
                           Background_1_Effek.playBackground_1(
                              Background_1_Effek.Type.LEVEL3,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).getX(),
                              ((LivingEntity)entityPatch.getOriginal()).getY(),
                              ((LivingEntity)entityPatch.getOriginal()).getZ(),
                              1.0F
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 3, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160, 3, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 160, 10, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  ),
                  EffectConditionParticleTrail.buffedParticleTrail(
                     65,
                     230,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -2.25),
                     new Vec3(0.0, 0.0, -2.3F),
                     6.0F,
                     3,
                     ParticleTypes.SMALL_FLAME,
                     0.6F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  )
               }
            )
            .addEvents(
               ActionAnimationProperty.ON_END_EVENTS,
               new AnimationEvent[]{
                  SimpleEvent.create((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(NF_MEEN_CHARGE3), Side.SERVER)
               }
            )
      );
      NF_MEEN_CHARGING_MOB = builder.nextAccessor(
         "biped/nf_meen/nf_meen_charge_all_mob",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               new Phase[]{
                  new Phase(0.9F, 0.9F, 1.4F, 2.3F, 2.3F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                  new Phase(2.3F, 2.3F, 2.6F, 3.5F, 3.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                  new Phase(3.5F, 3.5F, 3.8F, 3.8F, 3.8F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                  new Phase(4.8F, 4.9F, 5.05F, 5.25F, 5.25F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null)
                     .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
                     .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
                     .addProperty(
                        AttackPhaseProperty.EXTRA_DAMAGE,
                        Set.of(
                           EFNExtraDamageInstance.LOST_HEALTH_DAMAGE_WITH_SCALING_CAP.create(new float[]{0.2F, 50.0F, 100.0F}),
                           ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])
                        )
                     )
                     .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
                     .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
                     .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
               }
            )
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .newTimePair(0.0F, 2.1474836E9F)
            .addStateRemoveOld(EntityState.INACTION, true)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
            .newTimePair(0.5F, 2.1474836E9F)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.summonFireWind(50, 0.0, 0.1, 0.0, 1.15F, 0.0F, 0.0F, 60.0F),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_CHARGE_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL1, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     2.35F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_CHARGE_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL2, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     3.55F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_CHARGE_VFX.get()) {
                           ChargingEffek.playCharging2(
                              ChargingEffek.Type.LEVEL3, entityPatch.getOriginal(), 0.0, 0.9F, 0.0, 0.8F, ((LivingEntity)entityPatch.getOriginal()).level()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     3.6F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_CHARGE_VFX.get()) {
                           Background_1_Effek.playBackground_1(
                              Background_1_Effek.Type.LEVEL3,
                              ((LivingEntity)entityPatch.getOriginal()).level(),
                              ((LivingEntity)entityPatch.getOriginal()).getX(),
                              ((LivingEntity)entityPatch.getOriginal()).getY(),
                              ((LivingEntity)entityPatch.getOriginal()).getZ(),
                              1.0F
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 3, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160, 3, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.7F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 160, 10, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  ),
                  EffectConditionParticleTrail.buffedParticleTrail(
                     65,
                     230,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -2.25),
                     new Vec3(0.0, 0.0, -2.3F),
                     6.0F,
                     3,
                     ParticleTypes.SMALL_FLAME,
                     0.6F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  ),
                  InTimeEvent.create(
                     4.8F,
                     (entitypatch, self, params) -> {
                        if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                           LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                           ClientLevel level = (ClientLevel)attacker.level();
                           float intensity = 2.5F;
                           int sphereParticles = (int)(200.0F * intensity);
                           int coneParticles = (int)(150.0F * intensity);
                           float lavaRatio = 0.4F;
                           Vec3 centerPos = attacker.position().add(0.0, 0.05, 0.0);
                           int totalRings = 5;
                           int particlesPerRing = 36;
                           float maxRadius = 5.0F;
                           float duration = 0.5F;
                           float currentTime = 0.0F;
                           currentTime += 0.05F;
                           float progress = Math.min(currentTime / duration, 1.0F);

                           for (int ring = 0; ring < totalRings; ring++) {
                              float radius = maxRadius * (ring + progress) / totalRings;

                              for (int i = 0; i < particlesPerRing; i++) {
                                 float angle = (float)((Math.PI * 2) * i / particlesPerRing);
                                 float xOffset = radius * Mth.cos(angle);
                                 float zOffset = radius * Mth.sin(angle);
                                 level.addParticle(
                                    ParticleTypes.FLAME,
                                    centerPos.x + xOffset,
                                    centerPos.y + 0.05F,
                                    centerPos.z + zOffset,
                                    xOffset * 0.1F,
                                    0.08F,
                                    zOffset * 0.1F
                                 );
                                 if (i % 5 == 0) {
                                    level.addParticle(
                                       ParticleTypes.LAVA,
                                       centerPos.x + xOffset,
                                       centerPos.y + 0.07F,
                                       centerPos.z + zOffset,
                                       xOffset * 0.15F,
                                       0.12F,
                                       zOffset * 0.15F
                                    );
                                 }
                              }

                              if (ring == totalRings - 1) {
                                 for (int i = 0; i < particlesPerRing / 2; i++) {
                                    float angle = (float)((Math.PI * 2) * i / (particlesPerRing / 2.0));
                                    float xOffset = radius * Mth.cos(angle);
                                    float zOffset = radius * Mth.sin(angle);
                                    level.addParticle(
                                       ParticleTypes.FIREWORK,
                                       centerPos.x + xOffset,
                                       centerPos.y + 0.1F,
                                       centerPos.z + zOffset,
                                       xOffset * 0.3F,
                                       0.2F,
                                       zOffset * 0.3F
                                    );
                                 }
                              }
                           }

                           OpenMatrix4f transformMatrix = entitypatch.getArmature()
                              .getBoundTransformFor(entitypatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR);
                           transformMatrix.translate(new Vec3f(-0.2F, 0.0F, 0.4F));
                           OpenMatrix4f.mul(
                              new OpenMatrix4f().rotate(-((float)Math.toRadians(attacker.yBodyRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F)),
                              transformMatrix,
                              transformMatrix
                           );

                           for (int i = 0; i < 70; i++) {
                              double theta = (Math.PI * 2) * Math.random();
                              double thetax = Math.acos(2.0 * Math.random() - 1.0);
                              float dx = (float)(0.1 * Math.sin(thetax) * Math.cos(theta));
                              float dy = (float)(0.1 * Math.sin(thetax) * Math.sin(theta));
                              float dz = (float)(0.1 * Math.cos(thetax));
                              level.addParticle(
                                 ParticleTypes.SMALL_FLAME,
                                 transformMatrix.m30 + attacker.getX(),
                                 transformMatrix.m31 + attacker.getY() + (float)(Math.random() * 2.9F),
                                 transformMatrix.m32 + attacker.getZ(),
                                 dx,
                                 dy,
                                 dz
                              );
                              if (i % 2 == 0) {
                                 level.addParticle(
                                    ParticleTypes.LAVA,
                                    transformMatrix.m30 + attacker.getX(),
                                    transformMatrix.m31 + attacker.getY() + (float)(Math.random() * 2.9F),
                                    transformMatrix.m32 + attacker.getZ(),
                                    dx,
                                    dy,
                                    dz
                                 );
                              }
                           }

                           transformMatrix = entitypatch.getArmature()
                              .getBoundTransformFor(entitypatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).chest);
                           OpenMatrix4f.mul(
                              new OpenMatrix4f().rotate(-((float)Math.toRadians(attacker.yBodyRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F)),
                              transformMatrix,
                              transformMatrix
                           );
                           transformMatrix.translate(new Vec3f(0.0F, 0.3F, -0.5F));
                           double r = 0.5 * intensity;
                           double t = 0.006;

                           for (int group = 0; group < 2; group++) {
                              float angle = group == 0 ? 110.0F : 70.0F;

                              for (int i = 0; i < coneParticles; i++) {
                                 double theta = (Math.PI * 2) * Math.random();
                                 double phi = (Math.random() - 0.2) * Math.PI * t / r;
                                 Vec3f direction = new Vec3f(
                                    (float)(r * Math.cos(phi) * Math.cos(theta)),
                                    (float)(r * Math.cos(phi) * Math.sin(theta)) * 1.5F,
                                    (float)(r * Math.sin(phi))
                                 );
                                 OpenMatrix4f rotation = new OpenMatrix4f()
                                    .rotate((float)Math.toRadians(-attacker.yBodyRotO + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F))
                                    .rotate((float)Math.toRadians(angle), new Vec3f(1.0F, 0.0F, 0.0F));
                                 OpenMatrix4f.transform3v(rotation, direction, direction);
                                 float speedVariation = 0.3F + 0.3F * (float)Math.random();
                                 direction.scale(speedVariation);
                                 ParticleOptions particle;
                                 if (Math.random() < 0.7) {
                                    particle = ParticleTypes.FLAME;
                                 } else if (Math.random() < 0.9) {
                                    particle = ParticleTypes.SMALL_FLAME;
                                 } else {
                                    particle = ParticleTypes.END_ROD;
                                 }

                                 level.addParticle(
                                    particle,
                                    transformMatrix.m30 + attacker.getX(),
                                    transformMatrix.m31 + attacker.getY() + 0.7F,
                                    transformMatrix.m32 + attacker.getZ(),
                                    direction.x * 1.3F,
                                    direction.y * 1.5F,
                                    direction.z * 1.3F
                                 );
                                 if (Math.random() < 0.2F) {
                                    Vec3f lavaDir = new Vec3f(direction.x, direction.y, direction.z).scale(0.7F);
                                    level.addParticle(
                                       ParticleTypes.LANDING_LAVA,
                                       transformMatrix.m30 + attacker.getX(),
                                       transformMatrix.m31 + attacker.getY(),
                                       transformMatrix.m32 + attacker.getZ(),
                                       lavaDir.x,
                                       lavaDir.y * 1.4F,
                                       lavaDir.z
                                    );
                                 }
                              }
                           }

                           for (int i = 0; i < 120.0F * intensity; i++) {
                              double angle = Math.random() * (Math.PI * 2);
                              double radius = 4.0 * intensity * Math.random();
                              Vec3 pos = new Vec3(radius * Math.cos(angle), 0.1, radius * Math.sin(angle)).add(attacker.position());
                              level.addParticle(
                                 Math.random() < 0.6 ? ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME,
                                 pos.x,
                                 pos.y,
                                 pos.z,
                                 (Math.random() - 0.5) * 0.25,
                                 0.15 + Math.random() * 0.4,
                                 (Math.random() - 0.5) * 0.25
                              );
                              if (i % 4 == 0) {
                                 level.addParticle(ParticleTypes.LAVA, pos.x, pos.y + 0.05, pos.z, 0.0, 0.03, 0.0);
                              }

                              if (i % 3 == 0) {
                                 level.addParticle(
                                    ParticleTypes.ENCHANT,
                                    pos.x,
                                    pos.y + 0.1,
                                    pos.z,
                                    (Math.random() - 0.5) * 0.1,
                                    0.1,
                                    (Math.random() - 0.5) * 0.1
                                 );
                              }
                           }
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     5.65F,
                     (entitypatch, self, params) -> {
                        if (!((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                           LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                           ServerLevel level = (ServerLevel)attacker.level();
                           double centerX = attacker.getX();
                           double centerY = attacker.getY() + 0.2;
                           double centerZ = attacker.getZ();
                           double baseRadius = 10.0;
                           double maxRadius = 18.0;
                           int waveCount = 4;
                           int particlesPerWave = 100;
                           double speed = 0.5;

                           for (int wave = 0; wave < waveCount; wave++) {
                              double progress = (double)wave / (waveCount - 1);
                              double radius = Mth.lerp(progress, baseRadius, maxRadius);
                              double verticalScale = 1.8 * Math.sin(progress * Math.PI);

                              for (int i = 0; i < particlesPerWave; i++) {
                                 double angle = (Math.PI * 2) * i / particlesPerWave;
                                 double randomSpread = 0.5 * (level.random.nextDouble() - 0.5);
                                 Vec3 pos = new Vec3(
                                       radius * Math.cos(angle + progress * 1.5 * Math.PI) + randomSpread,
                                       verticalScale * Math.sin(angle * 3.0 + wave * 0.7),
                                       radius * Math.sin(angle + progress * 1.5 * Math.PI) + randomSpread
                                    )
                                    .add(centerX, centerY, centerZ);
                                 Vec3 motion = pos.subtract(centerX, centerY, centerZ)
                                    .normalize()
                                    .scale(speed * (0.4 + 0.6 * (1.0 - progress)))
                                    .add(0.0, 0.2, 0.0);
                                 level.sendParticles(
                                    ParticleTypes.FLAME,
                                    pos.x,
                                    pos.y,
                                    pos.z,
                                    2,
                                    motion.x * 0.4,
                                    motion.y * 0.9,
                                    motion.z * 0.4,
                                    1.0
                                 );
                                 if (level.random.nextDouble() < 0.5) {
                                    level.sendParticles(
                                       ParticleTypes.ENCHANT,
                                       pos.x,
                                       pos.y + 0.3,
                                       pos.z,
                                       1,
                                       motion.x * 1.5,
                                       motion.y * 2.0,
                                       motion.z * 1.5,
                                       0.5
                                    );
                                 }
                              }
                           }

                           level.sendParticles(ParticleTypes.SMALL_FLAME, centerX, centerY + 0.5, centerZ, 30, 1.8, 0.8, 1.8, 0.9);
                           level.sendParticles(ParticleTypes.ENCHANT, centerX, centerY + 0.5, centerZ, 80, 2.0, 1.0, 2.0, 0.8);
                        }
                     },
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     4.6F,
                     (entitypatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     4.6F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 5, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     4.0F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.8F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(4.8F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.5F, 0.0F, 0.0F), Side.SERVER),
                  AvalonEventUtils.simpleGroundSplit(300, 0.0, 0.0, 0.0, 0.0, 5.0F, true),
                  AvalonEventUtils.simpleCameraShake(299, 60, 4.0F, 4.0F, 4.0F),
                  InTimeEvent.create(
                     4.6F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.MEEN_LANCE, 1000, 0, true, true, true)),
                     Side.SERVER
                  )
               }
            )
      );
      NF_MEEN_CHARGE1 = builder.nextAccessor(
         "biped/nf_meen/nf_meen_charge1",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.2F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     42, 80, 90, InteractionHand.MAIN_HAND, 2.0F, 1.5F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, MEEN_LANCE_1
                  )
               }
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(80.0F))
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(
               AttackPhaseProperty.EXTRA_DAMAGE,
               Set.of(
                  EFNExtraDamageInstance.LOST_HEALTH_DAMAGE_WITH_SCALING_CAP.create(new float[]{0.1F, 30.0F, 100.0F}),
                  ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])
               )
            )
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addStateRemoveOld(EntityState.SKILL_EXECUTABLE, false)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 10)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.clearFireWind(80),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 20, 10, false, false, false)),
                     Side.SERVER
                  ),
                  AvalonEventUtils.simpleCameraShake(53, 70, 7.0F, 6.0F, 6.0F),
                  EffectConditionParticleTrail.buffedParticleTrail(
                     56,
                     70,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -2.25),
                     new Vec3(0.0, 0.0, -2.3F),
                     12.0F,
                     3,
                     ParticleTypes.LAVA,
                     0.3F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  ),
                  EffectConditionParticleTrail.buffedParticleTrail(
                     52,
                     70,
                     InteractionHand.MAIN_HAND,
                     new Vec3(-0.2F, 0.0, -2.25),
                     new Vec3(-0.2F, 0.0, -2.3F),
                     8.0F,
                     10,
                     ParticleTypes.FLAME,
                     0.3F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  ),
                  WeaponTrailGroundSplitter.create(
                     54, 80, InteractionHand.MAIN_HAND, new Vec3(0.0, 0.0, -2.25), new Vec3(0.0, 0.0, -2.3F), 1.8F, ParticleTypes.FLAME, 0, 3.0F, 6.0F, 15
                  )
               }
            )
      );
      NF_MEEN_CHARGE2 = builder.nextAccessor(
         "biped/nf_meen/nf_meen_charge2",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.1F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     38, 56, 75, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, null
                  )
               }
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .addProperty(
               AttackPhaseProperty.EXTRA_DAMAGE,
               Set.of(
                  ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0]),
                  EFNExtraDamageInstance.LOST_HEALTH_DAMAGE_WITH_SCALING_CAP.create(new float[]{0.15F, 40.0F, 100.0F})
               )
            )
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
            .newTimePair(0.0F, 0.2F)
            .addStateRemoveOld(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
            .addEvents(
               new AnimationEvent[]{
                  EffectEntityInvoker.clearFireWind(75),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 20, 10, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  ),
                  AvalonEventUtils.simpleCameraShake(43, 40, 3.0F, 3.0F, 3.0F),
                  EffectConditionParticleTrail.buffedParticleTrail(
                     25,
                     55,
                     InteractionHand.MAIN_HAND,
                     new Vec3(0.0, 0.0, -2.29F),
                     new Vec3(0.0, 0.0, -2.3F),
                     8.0F,
                     4,
                     ParticleTypes.LAVA,
                     1.0F,
                     EFNMobEffectRegistry.MEEN_LANCE
                  )
               }
            )
      );
      NF_MEEN_CHARGE3 = builder.nextAccessor(
         "biped/nf_meen/nf_meen_charge3",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.1F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     59, 70, 100, InteractionHand.MAIN_HAND, 1.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, MEEN_LANCE_CHARGE3
                  )
               }
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
            .addProperty(
               AttackPhaseProperty.EXTRA_DAMAGE,
               Set.of(
                  EFNExtraDamageInstance.LOST_HEALTH_DAMAGE_WITH_SCALING_CAP.create(new float[]{0.2F, 50.0F, 100.0F}),
                  ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])
               )
            )
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLADE)
            .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, EFNAnimations.ATTACK_SPEED_CAP_MEEN)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     1.1F,
                     (entitypatch, self, params) -> {
                        if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                           LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                           ClientLevel level = (ClientLevel)attacker.level();
                           float intensity = 2.5F;
                           int sphereParticles = (int)(200.0F * intensity);
                           int coneParticles = (int)(150.0F * intensity);
                           float lavaRatio = 0.4F;
                           Vec3 centerPos = attacker.position().add(0.0, 0.05, 0.0);
                           int totalRings = 5;
                           int particlesPerRing = 36;
                           float maxRadius = 5.0F;
                           float duration = 0.5F;
                           float currentTime = 0.0F;
                           currentTime += 0.05F;
                           float progress = Math.min(currentTime / duration, 1.0F);

                           for (int ring = 0; ring < totalRings; ring++) {
                              float radius = maxRadius * (ring + progress) / totalRings;

                              for (int i = 0; i < particlesPerRing; i++) {
                                 float angle = (float)((Math.PI * 2) * i / particlesPerRing);
                                 float xOffset = radius * Mth.cos(angle);
                                 float zOffset = radius * Mth.sin(angle);
                                 level.addParticle(
                                    ParticleTypes.FLAME,
                                    centerPos.x + xOffset,
                                    centerPos.y + 0.05F,
                                    centerPos.z + zOffset,
                                    xOffset * 0.1F,
                                    0.08F,
                                    zOffset * 0.1F
                                 );
                                 if (i % 5 == 0) {
                                    level.addParticle(
                                       ParticleTypes.LAVA,
                                       centerPos.x + xOffset,
                                       centerPos.y + 0.07F,
                                       centerPos.z + zOffset,
                                       xOffset * 0.15F,
                                       0.12F,
                                       zOffset * 0.15F
                                    );
                                 }
                              }

                              if (ring == totalRings - 1) {
                                 for (int i = 0; i < particlesPerRing / 2; i++) {
                                    float angle = (float)((Math.PI * 2) * i / (particlesPerRing / 2.0));
                                    float xOffset = radius * Mth.cos(angle);
                                    float zOffset = radius * Mth.sin(angle);
                                    level.addParticle(
                                       ParticleTypes.FIREWORK,
                                       centerPos.x + xOffset,
                                       centerPos.y + 0.1F,
                                       centerPos.z + zOffset,
                                       xOffset * 0.3F,
                                       0.2F,
                                       zOffset * 0.3F
                                    );
                                 }
                              }
                           }

                           OpenMatrix4f transformMatrix = entitypatch.getArmature()
                              .getBoundTransformFor(entitypatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR);
                           transformMatrix.translate(new Vec3f(-0.2F, 0.0F, 0.4F));
                           OpenMatrix4f.mul(
                              new OpenMatrix4f().rotate(-((float)Math.toRadians(attacker.yBodyRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F)),
                              transformMatrix,
                              transformMatrix
                           );

                           for (int i = 0; i < 70; i++) {
                              double theta = (Math.PI * 2) * Math.random();
                              double thetax = Math.acos(2.0 * Math.random() - 1.0);
                              float dx = (float)(0.1 * Math.sin(thetax) * Math.cos(theta));
                              float dy = (float)(0.1 * Math.sin(thetax) * Math.sin(theta));
                              float dz = (float)(0.1 * Math.cos(thetax));
                              level.addParticle(
                                 ParticleTypes.SMALL_FLAME,
                                 transformMatrix.m30 + attacker.getX(),
                                 transformMatrix.m31 + attacker.getY() + (float)(Math.random() * 2.9F),
                                 transformMatrix.m32 + attacker.getZ(),
                                 dx,
                                 dy,
                                 dz
                              );
                              if (i % 2 == 0) {
                                 level.addParticle(
                                    ParticleTypes.LAVA,
                                    transformMatrix.m30 + attacker.getX(),
                                    transformMatrix.m31 + attacker.getY() + (float)(Math.random() * 2.9F),
                                    transformMatrix.m32 + attacker.getZ(),
                                    dx,
                                    dy,
                                    dz
                                 );
                              }
                           }

                           transformMatrix = entitypatch.getArmature()
                              .getBoundTransformFor(entitypatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).chest);
                           OpenMatrix4f.mul(
                              new OpenMatrix4f().rotate(-((float)Math.toRadians(attacker.yBodyRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F)),
                              transformMatrix,
                              transformMatrix
                           );
                           transformMatrix.translate(new Vec3f(0.0F, 0.3F, -0.5F));
                           double r = 0.5 * intensity;
                           double t = 0.006;

                           for (int group = 0; group < 2; group++) {
                              float angle = group == 0 ? 110.0F : 70.0F;

                              for (int i = 0; i < coneParticles; i++) {
                                 double theta = (Math.PI * 2) * Math.random();
                                 double phi = (Math.random() - 0.2) * Math.PI * t / r;
                                 Vec3f direction = new Vec3f(
                                    (float)(r * Math.cos(phi) * Math.cos(theta)),
                                    (float)(r * Math.cos(phi) * Math.sin(theta)) * 1.5F,
                                    (float)(r * Math.sin(phi))
                                 );
                                 OpenMatrix4f rotation = new OpenMatrix4f()
                                    .rotate((float)Math.toRadians(-attacker.yBodyRotO + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F))
                                    .rotate((float)Math.toRadians(angle), new Vec3f(1.0F, 0.0F, 0.0F));
                                 OpenMatrix4f.transform3v(rotation, direction, direction);
                                 float speedVariation = 0.3F + 0.3F * (float)Math.random();
                                 direction.scale(speedVariation);
                                 ParticleOptions particle;
                                 if (Math.random() < 0.7) {
                                    particle = ParticleTypes.FLAME;
                                 } else if (Math.random() < 0.9) {
                                    particle = ParticleTypes.SMALL_FLAME;
                                 } else {
                                    particle = ParticleTypes.END_ROD;
                                 }

                                 level.addParticle(
                                    particle,
                                    transformMatrix.m30 + attacker.getX(),
                                    transformMatrix.m31 + attacker.getY() + 0.7F,
                                    transformMatrix.m32 + attacker.getZ(),
                                    direction.x * 1.3F,
                                    direction.y * 1.5F,
                                    direction.z * 1.3F
                                 );
                                 if (Math.random() < 0.2F) {
                                    Vec3f lavaDir = new Vec3f(direction.x, direction.y, direction.z).scale(0.7F);
                                    level.addParticle(
                                       ParticleTypes.LANDING_LAVA,
                                       transformMatrix.m30 + attacker.getX(),
                                       transformMatrix.m31 + attacker.getY(),
                                       transformMatrix.m32 + attacker.getZ(),
                                       lavaDir.x,
                                       lavaDir.y * 1.4F,
                                       lavaDir.z
                                    );
                                 }
                              }
                           }

                           for (int i = 0; i < 120.0F * intensity; i++) {
                              double angle = Math.random() * (Math.PI * 2);
                              double radius = 4.0 * intensity * Math.random();
                              Vec3 pos = new Vec3(radius * Math.cos(angle), 0.1, radius * Math.sin(angle)).add(attacker.position());
                              level.addParticle(
                                 Math.random() < 0.6 ? ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME,
                                 pos.x,
                                 pos.y,
                                 pos.z,
                                 (Math.random() - 0.5) * 0.25,
                                 0.15 + Math.random() * 0.4,
                                 (Math.random() - 0.5) * 0.25
                              );
                              if (i % 4 == 0) {
                                 level.addParticle(ParticleTypes.LAVA, pos.x, pos.y + 0.05, pos.z, 0.0, 0.03, 0.0);
                              }

                              if (i % 3 == 0) {
                                 level.addParticle(
                                    ParticleTypes.ENCHANT,
                                    pos.x,
                                    pos.y + 0.1,
                                    pos.z,
                                    (Math.random() - 0.5) * 0.1,
                                    0.1,
                                    (Math.random() - 0.5) * 0.1
                                 );
                              }
                           }
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     1.85F,
                     (entitypatch, self, params) -> {
                        if (!((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                           LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                           ServerLevel level = (ServerLevel)attacker.level();
                           double centerX = attacker.getX();
                           double centerY = attacker.getY() + 0.2;
                           double centerZ = attacker.getZ();
                           double baseRadius = 10.0;
                           double maxRadius = 18.0;
                           int waveCount = 4;
                           int particlesPerWave = 100;
                           double speed = 0.5;

                           for (int wave = 0; wave < waveCount; wave++) {
                              double progress = (double)wave / (waveCount - 1);
                              double radius = Mth.lerp(progress, baseRadius, maxRadius);
                              double verticalScale = 1.8 * Math.sin(progress * Math.PI);

                              for (int i = 0; i < particlesPerWave; i++) {
                                 double angle = (Math.PI * 2) * i / particlesPerWave;
                                 double randomSpread = 0.5 * (level.random.nextDouble() - 0.5);
                                 Vec3 pos = new Vec3(
                                       radius * Math.cos(angle + progress * 1.5 * Math.PI) + randomSpread,
                                       verticalScale * Math.sin(angle * 3.0 + wave * 0.7),
                                       radius * Math.sin(angle + progress * 1.5 * Math.PI) + randomSpread
                                    )
                                    .add(centerX, centerY, centerZ);
                                 Vec3 motion = pos.subtract(centerX, centerY, centerZ)
                                    .normalize()
                                    .scale(speed * (0.4 + 0.6 * (1.0 - progress)))
                                    .add(0.0, 0.2, 0.0);
                                 level.sendParticles(
                                    ParticleTypes.FLAME,
                                    pos.x,
                                    pos.y,
                                    pos.z,
                                    2,
                                    motion.x * 0.4,
                                    motion.y * 0.9,
                                    motion.z * 0.4,
                                    1.0
                                 );
                                 if (level.random.nextDouble() < 0.5) {
                                    level.sendParticles(
                                       ParticleTypes.ENCHANT,
                                       pos.x,
                                       pos.y + 0.3,
                                       pos.z,
                                       1,
                                       motion.x * 1.5,
                                       motion.y * 2.0,
                                       motion.z * 1.5,
                                       0.5
                                    );
                                 }
                              }
                           }

                           level.sendParticles(ParticleTypes.SMALL_FLAME, centerX, centerY + 0.5, centerZ, 30, 1.8, 0.8, 1.8, 0.9);
                           level.sendParticles(ParticleTypes.ENCHANT, centerX, centerY + 0.5, centerZ, 80, 2.0, 1.0, 2.0, 0.8);
                        }
                     },
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, animation, params) -> {
                        LivingEntity entity = (LivingEntity)entitypatch.getOriginal();
                        entity.level()
                           .addParticle(
                              (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              Double.longBitsToDouble(entity.getId()),
                              0.0,
                              0.0
                           );
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.0F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 5, false, false, false)),
                     Side.BOTH
                  ),
                  InTimeEvent.create(
                     0.2F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.5F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(1.1F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.5F, 0.0F, 0.0F), Side.SERVER),
                  AvalonEventUtils.simpleGroundSplit(57, 0.0, 0.0, 0.0, 0.0, 5.0F, true),
                  AvalonEventUtils.simpleCameraShake(59, 60, 4.0F, 4.0F, 4.0F),
                  InTimeEvent.create(
                     0.1F,
                     (entitypatch, self, params) -> ((LivingEntity)entitypatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.MEEN_LANCE, 1000, 0, true, true, true)),
                     Side.SERVER
                  )
               }
            )
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
            .addStateRemoveOld(EntityState.CAN_SWITCH_HAND_ITEM, false)
            .addStateRemoveOld(EntityState.INACTION, true)
      );
      NF_MEEN_FINISHER = builder.nextAccessor(
         "biped/nf_meen/nf_meen_skill_max",
         accessor -> (AvalonAttackAnimation)new AvalonAttackAnimation(
               0.0F,
               accessor,
               Armatures.BIPED,
               1.0F,
               1.0F,
               new AvalonPhase[]{
                  AvalonAnimationUtils.createSimplePhase(
                     60, 80, 85, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, MEEN_LANCE_1
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     89, 115, 120, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).toolR, MEEN_LANCE_1
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     160, 166, 180, InteractionHand.MAIN_HAND, 0.2F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, MEEN_LANCE_1
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     214, 223, 224, InteractionHand.MAIN_HAND, 2.0F, 2.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, MEEN_LANCE_CHARGE3
                  ),
                  AvalonAnimationUtils.createSimplePhase(
                     225, 241, 310, InteractionHand.MAIN_HAND, 1.0F, 1.0F, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, MEEN_LANCE_CHARGE3
                  )
               }
            )
            .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(
               AttackPhaseProperty.SOURCE_TAG,
               Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.GUARD_PUNCTURE, EpicFightDamageTypeTags.BYPASS_DODGE)
            )
            .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
            .newTimePair(2.0F, 2.5F)
            .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addStateRemoveOld(EntityState.CAN_SWITCH_HAND_ITEM, false)
            .addStateRemoveOld(EntityState.TURNING_LOCKED, true)
            .addStateRemoveOld(EntityState.LOOK_TARGET, true)
            .addState(EntityState.ATTACK_RESULT, DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR)
            .addEvents(
               new AnimationEvent[]{
                  InTimeEvent.create(
                     1.9F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_FINISHER_VFX.get()) {
                           FlameEffek.playFlame(
                              FlameEffek.Type.LEVEL1, ((LivingEntity)entityPatch.getOriginal()).level(), 0.0, 0.15, 0.0, 1.0F, entityPatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     2.6F,
                     (entitypatch, self, params) -> {
                        if (!((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                           LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                           ServerLevel level = (ServerLevel)attacker.level();
                           double centerX = attacker.getX();
                           double centerY = attacker.getY() + 0.2;
                           double centerZ = attacker.getZ();
                           double baseRadius = 5.0;
                           double maxRadius = 10.0;
                           int waveCount = 6;
                           int particlesPerWave = 100;
                           double speed = 0.5;

                           for (int wave = 0; wave < waveCount; wave++) {
                              double progress = (double)wave / (waveCount - 1);
                              double radius = Mth.lerp(progress, baseRadius, maxRadius);
                              double verticalScale = 1.8 * Math.sin(progress * Math.PI);

                              for (int i = 0; i < particlesPerWave; i++) {
                                 double angle = (Math.PI * 2) * i / particlesPerWave;
                                 double randomSpread = 0.5 * (level.random.nextDouble() - 0.5);
                                 Vec3 pos = new Vec3(
                                       radius * Math.cos(angle + progress * 1.5 * Math.PI) + randomSpread,
                                       verticalScale * Math.sin(angle * 3.0 + wave * 0.7),
                                       radius * Math.sin(angle + progress * 1.5 * Math.PI) + randomSpread
                                    )
                                    .add(centerX, centerY, centerZ);
                                 Vec3 motion = pos.subtract(centerX, centerY, centerZ)
                                    .normalize()
                                    .scale(speed * (0.4 + 0.6 * (1.0 - progress)))
                                    .add(0.0, 0.2, 0.0);
                                 level.sendParticles(
                                    ParticleTypes.FLAME,
                                    pos.x,
                                    pos.y,
                                    pos.z,
                                    2,
                                    motion.x * 0.4,
                                    motion.y * 0.9,
                                    motion.z * 0.4,
                                    1.0
                                 );
                                 if (level.random.nextDouble() < 0.5) {
                                    level.sendParticles(
                                       ParticleTypes.LAVA,
                                       pos.x,
                                       pos.y + 0.3,
                                       pos.z,
                                       1,
                                       motion.x * 1.5,
                                       motion.y * 2.0,
                                       motion.z * 1.5,
                                       0.5
                                    );
                                 }
                              }
                           }

                           level.sendParticles(ParticleTypes.END_ROD, centerX, centerY + 0.5, centerZ, 30, 1.8, 0.8, 1.8, 0.9);
                           level.sendParticles(ParticleTypes.FLAME, centerX, centerY + 0.5, centerZ, 80, 2.0, 1.0, 2.0, 0.8);
                        }
                     },
                     Side.SERVER
                  ),
                  InTimeEvent.create(3.66F, (entityPatch, self, params) -> VIXCameraFOV.applyExplosionPulse(4.2F), Side.CLIENT),
                  InTimeEvent.create(
                     3.7F,
                     (entitypatch, self, params) -> {
                        if (((LivingEntity)entitypatch.getOriginal()).level().isClientSide()) {
                           LivingEntity attacker = (LivingEntity)entitypatch.getOriginal();
                           ClientLevel level = (ClientLevel)attacker.level();
                           BlackWhiteFlashEffect effect2 = new BlackWhiteFlashEffect(
                              ((LivingEntity)entitypatch.getOriginal()).position(), BlackWhiteFlashEffect.ImpactMode.HEAVY
                           );
                           ScreenEffectEngine.PushScreenEffectADD(effect2);
                           float intensity = 2.5F;
                           int sphereParticles = (int)(200.0F * intensity);
                           int coneParticles = (int)(150.0F * intensity);
                           float lavaRatio = 0.4F;
                           Vec3 centerPos = attacker.position().add(0.0, 0.05, 0.0);
                           int totalRings = 5;
                           int particlesPerRing = 36;
                           float maxRadius = 5.0F;
                           float duration = 0.5F;
                           float currentTime = 0.0F;
                           currentTime += 0.05F;
                           float progress = Math.min(currentTime / duration, 1.0F);

                           for (int ring = 0; ring < totalRings; ring++) {
                              float radius = maxRadius * (ring + progress) / totalRings;

                              for (int i = 0; i < particlesPerRing; i++) {
                                 float angle = (float)((Math.PI * 2) * i / particlesPerRing);
                                 float xOffset = radius * Mth.cos(angle);
                                 float zOffset = radius * Mth.sin(angle);
                                 level.addParticle(
                                    ParticleTypes.FLAME,
                                    centerPos.x + xOffset,
                                    centerPos.y + 0.05F,
                                    centerPos.z + zOffset,
                                    xOffset * 0.1F,
                                    0.08F,
                                    zOffset * 0.1F
                                 );
                                 if (i % 5 == 0) {
                                    level.addParticle(
                                       ParticleTypes.LAVA,
                                       centerPos.x + xOffset,
                                       centerPos.y + 0.07F,
                                       centerPos.z + zOffset,
                                       xOffset * 0.15F,
                                       0.12F,
                                       zOffset * 0.15F
                                    );
                                 }
                              }

                              if (ring == totalRings - 1) {
                                 for (int i = 0; i < particlesPerRing / 2; i++) {
                                    float angle = (float)((Math.PI * 2) * i / (particlesPerRing / 2.0));
                                    float xOffset = radius * Mth.cos(angle);
                                    float zOffset = radius * Mth.sin(angle);
                                    level.addParticle(
                                       ParticleTypes.FIREWORK,
                                       centerPos.x + xOffset,
                                       centerPos.y + 0.1F,
                                       centerPos.z + zOffset,
                                       xOffset * 0.3F,
                                       0.2F,
                                       zOffset * 0.3F
                                    );
                                 }
                              }
                           }

                           OpenMatrix4f transformMatrix = entitypatch.getArmature()
                              .getBoundTransformFor(entitypatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR);
                           transformMatrix.translate(new Vec3f(-0.2F, 0.0F, 0.4F));
                           OpenMatrix4f.mul(
                              new OpenMatrix4f().rotate(-((float)Math.toRadians(attacker.yBodyRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F)),
                              transformMatrix,
                              transformMatrix
                           );

                           for (int i = 0; i < 70; i++) {
                              double theta = (Math.PI * 2) * Math.random();
                              double thetax = Math.acos(2.0 * Math.random() - 1.0);
                              float dx = (float)(0.1 * Math.sin(thetax) * Math.cos(theta));
                              float dy = (float)(0.1 * Math.sin(thetax) * Math.sin(theta));
                              float dz = (float)(0.1 * Math.cos(thetax));
                              level.addParticle(
                                 ParticleTypes.SMALL_FLAME,
                                 transformMatrix.m30 + attacker.getX(),
                                 transformMatrix.m31 + attacker.getY() + (float)(Math.random() * 2.9F),
                                 transformMatrix.m32 + attacker.getZ(),
                                 dx,
                                 dy,
                                 dz
                              );
                              if (i % 2 == 0) {
                                 level.addParticle(
                                    ParticleTypes.LAVA,
                                    transformMatrix.m30 + attacker.getX(),
                                    transformMatrix.m31 + attacker.getY() + (float)(Math.random() * 2.9F),
                                    transformMatrix.m32 + attacker.getZ(),
                                    dx,
                                    dy,
                                    dz
                                 );
                              }
                           }

                           transformMatrix = entitypatch.getArmature()
                              .getBoundTransformFor(entitypatch.getAnimator().getPose(0.0F), ((HumanoidArmature)Armatures.BIPED.get()).handR);
                           OpenMatrix4f.mul(
                              new OpenMatrix4f().rotate(-((float)Math.toRadians(attacker.yBodyRotO + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F)),
                              transformMatrix,
                              transformMatrix
                           );
                           transformMatrix.translate(new Vec3f(0.0F, 0.3F, -0.5F));
                           double r = 0.5 * intensity;
                           double t = 0.006;

                           for (int group = 0; group < 2; group++) {
                              float angle = group == 0 ? 110.0F : 70.0F;

                              for (int i = 0; i < coneParticles; i++) {
                                 double theta = (Math.PI * 2) * Math.random();
                                 double phi = (Math.random() - 0.2) * Math.PI * t / r;
                                 Vec3f direction = new Vec3f(
                                    (float)(r * Math.cos(phi) * Math.cos(theta)),
                                    (float)(r * Math.cos(phi) * Math.sin(theta)) * 1.5F,
                                    (float)(r * Math.sin(phi))
                                 );
                                 OpenMatrix4f rotation = new OpenMatrix4f()
                                    .rotate((float)Math.toRadians(-attacker.yBodyRotO + 90.0F), new Vec3f(0.0F, 1.0F, 0.0F))
                                    .rotate((float)Math.toRadians(angle), new Vec3f(1.0F, 0.0F, 0.0F));
                                 OpenMatrix4f.transform3v(rotation, direction, direction);
                                 float speedVariation = 0.3F + 0.3F * (float)Math.random();
                                 direction.scale(speedVariation);
                                 ParticleOptions particle;
                                 if (Math.random() < 0.7) {
                                    particle = ParticleTypes.FLAME;
                                 } else if (Math.random() < 0.9) {
                                    particle = ParticleTypes.SMALL_FLAME;
                                 } else {
                                    particle = ParticleTypes.END_ROD;
                                 }

                                 level.addParticle(
                                    particle,
                                    transformMatrix.m30 + attacker.getX(),
                                    transformMatrix.m31 + attacker.getY() + 0.7F,
                                    transformMatrix.m32 + attacker.getZ(),
                                    direction.x * 1.3F,
                                    direction.y * 1.5F,
                                    direction.z * 1.3F
                                 );
                                 if (Math.random() < 0.2F) {
                                    Vec3f lavaDir = new Vec3f(direction.x, direction.y, direction.z).scale(0.7F);
                                    level.addParticle(
                                       ParticleTypes.LANDING_LAVA,
                                       transformMatrix.m30 + attacker.getX(),
                                       transformMatrix.m31 + attacker.getY(),
                                       transformMatrix.m32 + attacker.getZ(),
                                       lavaDir.x,
                                       lavaDir.y * 1.4F,
                                       lavaDir.z
                                    );
                                 }
                              }
                           }

                           for (int i = 0; i < 120.0F * intensity; i++) {
                              double angle = Math.random() * (Math.PI * 2);
                              double radius = 4.0 * intensity * Math.random();
                              Vec3 pos = new Vec3(radius * Math.cos(angle), 0.1, radius * Math.sin(angle)).add(attacker.position());
                              level.addParticle(
                                 Math.random() < 0.6 ? ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME,
                                 pos.x,
                                 pos.y,
                                 pos.z,
                                 (Math.random() - 0.5) * 0.25,
                                 0.15 + Math.random() * 0.4,
                                 (Math.random() - 0.5) * 0.25
                              );
                              if (i % 4 == 0) {
                                 level.addParticle(ParticleTypes.LAVA, pos.x, pos.y + 0.05, pos.z, 0.0, 0.03, 0.0);
                              }

                              if (i % 3 == 0) {
                                 level.addParticle(
                                    ParticleTypes.ENCHANT,
                                    pos.x,
                                    pos.y + 0.1,
                                    pos.z,
                                    (Math.random() - 0.5) * 0.1,
                                    0.1,
                                    (Math.random() - 0.5) * 0.1
                                 );
                              }
                           }
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     0.2F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.5F, 0.0F, 0.0F), Side.SERVER
                  ),
                  InTimeEvent.create(2.6F, (entitypatch, self, params) -> entitypatch.playSound(SoundEvents.TRIDENT_RIPTIDE_1, 1.4F, 0.0F, 0.0F), Side.SERVER),
                  InTimeEvent.create(3.6F, (entitypatch, self, params) -> {
                     entitypatch.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.5F, 0.0F, 0.0F);
                     entitypatch.playSound((SoundEvent)EFNSounds.MAGIC1.get(), 1.2F, 1.0F, 1.0F);
                  }, Side.SERVER),
                  InTimeEvent.create(
                     3.6F,
                     (entitypatch, self, params) -> {
                        if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SPEAR_FINISHER_VFX.get()) {
                           Flame2Effek.playFlame2(
                              Flame2Effek.Type.LEVEL1,
                              ((LivingEntity)entitypatch.getOriginal()).level(),
                              ((LivingEntity)entitypatch.getOriginal()).getX(),
                              ((LivingEntity)entitypatch.getOriginal()).getY() + 0.15,
                              ((LivingEntity)entitypatch.getOriginal()).getZ(),
                              1.0F,
                              entitypatch.getOriginal()
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  AvalonEventUtils.particleTrail(
                     56, 166, InteractionHand.MAIN_HAND, new Vec3(0.0, 0.0, -2.25), new Vec3(0.0, 0.0, -2.6F), 8.0F, 8, ParticleTypes.FLAME, 0.6F
                  ),
                  WeaponTrailGroundSplitter.create(
                     60, 80, InteractionHand.MAIN_HAND, new Vec3(0.0, 0.0, -2.25), new Vec3(0.0, 0.0, -2.3F), 1.2F, ParticleTypes.ENCHANT, 1, 3.0F, 4.0F, 7
                  ),
                  WeaponTrailGroundSplitter.create(
                     89, 115, InteractionHand.MAIN_HAND, new Vec3(0.0, 0.0, -2.25), new Vec3(0.0, 0.0, -2.3F), 1.2F, ParticleTypes.ENCHANT, 1, 3.0F, 4.0F, 7
                  ),
                  AvalonEventUtils.simpleGroundSplit(160, 0.0, 0.0, 0.0, 0.0, 4.0F, true),
                  AvalonEventUtils.simpleCameraShake(160, 30, 3.0F, 3.0F, 3.0F),
                  AvalonEventUtils.simpleGroundSplit(214, 0.0, 0.0, 0.0, 0.0, 5.0F, true),
                  AvalonEventUtils.simpleCameraShake(214, 60, 8.0F, 4.0F, 8.0F),
                  AvalonEventUtils.simpleGroundSplit(225, 0.0, 0.0, 0.0, 0.0, 7.0F, true)
               }
            )
      );
   }
}
