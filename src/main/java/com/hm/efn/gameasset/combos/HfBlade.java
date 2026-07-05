package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.HitEvents;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.input.EFNDownKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNKeyLongPressCondition;
import com.hm.efn.comboevents.condition.input.EFNSneakKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNUpKeyCondition;
import com.hm.efn.comboevents.condition.state.EFNAirborneCondition;
import com.hm.efn.comboevents.condition.state.EFNBlockingCondition;
import com.hm.efn.comboevents.condition.state.EFNMurasamaSheathCondition;
import com.hm.efn.comboevents.condition.state.EFNOnGroundCondition;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.comboevents.condition.weapons.BackwardForwardCondition;
import com.hm.efn.comboevents.condition.weapons.DoubleTapSprintCondition;
import com.hm.efn.comboevents.condition.weapons.JumpCountCondition;
import com.hm.efn.comboevents.condition.weapons.MurasamaSheathCondition;
import com.hm.efn.comboevents.condition.weapons.MurasamaZansetsuCondition;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.HfBladeInnate;
import com.hm.efn.skill.weapon_passive.HfBladePassive;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.ParrySuccessCondition;
import com.p1nero.invincible.conditions.SprintingCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.registry.entries.EpicFightMobEffects;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class HfBlade {
   public static Skill HfBlade;
   public static Skill HfBlade_Passive;
   public static ComboNode DodgeCounter;
   public static ComboNode Air_Dodge;
   public static ComboNode HfBlade_root;
   public static ComboNode Y_CHARGE_SET;
   public static ComboNode Y_CHARGE_AIR;
   public static ComboNode XY_CHARGE;
   public static ComboNode XXY_CHARGE;
   public static ComboNode XXXY_CHARGE;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      HfBlade_root = ComboNode.create();
      Y_CHARGE_AIR = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y_CHARGE_AIR)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .addCondition(new EFNAirborneCondition(true, 1.0F))
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(2.5F))
         .setImpactMultiplier(2.0F)
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      XY_CHARGE = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XY_CHARGE)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(2.5F))
         .setImpactMultiplier(3.0F)
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addHitEvent(HitEvents.addEffect(MobEffects.SLOW_FALLING, 1, 60))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      XXY_CHARGE = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XXY_CHARGE)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(1.25F))
         .setImpactMultiplier(1.5F)
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      XXXY_CHARGE = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XXXY_CHARGE)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(2.0F))
         .setImpactMultiplier(1.7F)
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode SHEATH_IN = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_SHEATH_IN)
         .addCondition(new EFNBlockingCondition(false))
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new MurasamaSheathCondition(false))
         .addCondition(new DoubleTapSprintCondition(false, true))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new EFNMurasamaSheathCondition())
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .setPriority(1);
      ComboNode SHEATH_IN_RUN = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_SHEATH_IN_RUN)
         .addCondition(new EFNBlockingCondition(false))
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new MurasamaSheathCondition(false))
         .addCondition(new DoubleTapSprintCondition(false, true))
         .addCondition(new EFNSprintingCondition())
         .addCondition(new EFNOnGroundCondition(false))
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .setPriority(2);
      ComboNode SWORD_OUT = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_SWORD_OUT)
         .addCondition(new EFNBlockingCondition(false))
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new MurasamaSheathCondition(true))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new EFNMurasamaSheathCondition())
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .setPriority(1);
      ComboNode TAUNT = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_TAUNT)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNSneakKeyCondition())
         .setCanBeInterrupt(false)
         .setPriority(10000);
      ComboNode COUNTER = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_COUNTER)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new ParrySuccessCondition())
         .addCondition(new EFNUpKeyCondition())
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(50)
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .setDamageMultiplier(ValueModifier.multiplier(1.5F))
         .setImpactMultiplier(1.5F)
         .addHitEvent(HitEvents.createHitSoundTarget(() -> EpicFightSounds.NEUTRALIZE_MOBS.get(), 2.5F, 0.0F, 0.1F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode Y_KICK = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_KICK_Y)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new BackwardForwardCondition())
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(20)
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .setDamageMultiplier(ValueModifier.multiplier(0.75F))
         .setImpactMultiplier(1.5F)
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true))
         .addTimeEvent(TimeEvents.TimeSimulationSkillEvent(0.75F));
      ComboNode Y_DASH_SP = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_DASH_Y_SP)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNSprintingCondition())
         .addCondition(new DoubleTapSprintCondition())
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(20)
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(1.4F))
         .setImpactMultiplier(1.4F)
         .addHitEvent(HitEvents.addEffect(MobEffects.SLOW_FALLING, 1, 60))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.2F, false, true));
      ComboNode Y_DASH = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_DASH_Y)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNSprintingCondition())
         .addCondition(new MurasamaSheathCondition(true))
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(10)
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .setImpactMultiplier(1.1F)
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.85F, XXXY_CHARGE))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode Y_SHEATH = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XY)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new MurasamaSheathCondition(true))
         .setPriority(9)
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(1.3F))
         .setImpactMultiplier(1.3F)
         .setConvertTime(-0.3F)
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.5F, false, true));
      ComboNode Y_SHEATH_DASH = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y_CHARGE)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new MurasamaSheathCondition(true))
         .addCondition(new EFNSneakKeyCondition())
         .setNotCharge(true)
         .setPriority(10)
         .setConvertTime(0.2F)
         .setDamageMultiplier(ValueModifier.multiplier(2.5F))
         .setImpactMultiplier(2.0F)
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.45F, false, true));
      ComboNode Y_SHEATH_DASH_THROUGH = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y_CHARGE_THROUGH)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new MurasamaSheathCondition(true))
         .addCondition(new EFNSneakKeyCondition())
         .addCondition(new EFNUpKeyCondition())
         .setNotCharge(true)
         .setPriority(11)
         .setConvertTime(0.2F)
         .setDamageMultiplier(ValueModifier.multiplier(2.5F))
         .setImpactMultiplier(2.0F)
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.45F, false, true));
      ComboNode Y_CHARGE = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y_CHARGE)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .setNotCharge(true)
         .setPriority(1)
         .setDamageMultiplier(ValueModifier.multiplier(2.5F))
         .setImpactMultiplier(2.0F)
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode Y_CHARGE_THROUGH = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y_CHARGE_THROUGH)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .addCondition(new EFNUpKeyCondition())
         .setNotCharge(true)
         .setPriority(2)
         .setDamageMultiplier(ValueModifier.multiplier(2.5F))
         .setImpactMultiplier(2.0F)
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode Y_AIR = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y_AIR)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNAirborneCondition(true, 1.0F))
         .setNotCharge(true)
         .setPriority(20)
         .setDamageMultiplier(ValueModifier.multiplier(1.3F))
         .setImpactMultiplier(1.3F)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.4F, Y_CHARGE_AIR))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode SHEATH_X = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new MurasamaSheathCondition(true))
         .setPriority(4)
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .setImpactMultiplier(1.2F)
         .setConvertTime(-0.3F)
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.5F, false, true));
      ComboNode DASH_X = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_DASH_X)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new MurasamaSheathCondition(true))
         .addCondition(new SprintingCondition())
         .setPriority(5)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .setImpactMultiplier(1.1F)
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode DASH_X_SP = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_DASH_X)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new DoubleTapSprintCondition())
         .addCondition(new SprintingCondition())
         .setPriority(5)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .setImpactMultiplier(1.1F)
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode X = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_X)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(1)
         .setDamageMultiplier(ValueModifier.multiplier(0.7F))
         .setImpactMultiplier(0.7F)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.4F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode XY = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XY)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(5)
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .setImpactMultiplier(1.1F)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.25F, XY_CHARGE))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode XY_DASH_X = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XY)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(5)
         .setNotCharge(true)
         .setConvertTime(-0.1F)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .setImpactMultiplier(1.1F)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.25F, XY_CHARGE))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode XX = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XX)
         .addCondition(new MurasamaZansetsuCondition(false))
         .setDamageMultiplier(ValueModifier.multiplier(0.85F))
         .setImpactMultiplier(0.85F)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.4F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode XXY = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XXY)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(5)
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .setImpactMultiplier(1.2F)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.2F, XXY_CHARGE))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode XXX = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XXX)
         .addCondition(new MurasamaZansetsuCondition(false))
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .setImpactMultiplier(1.2F)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.3F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode XXXY = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XXXY)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(5)
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .setImpactMultiplier(1.0F)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.5F, XXXY_CHARGE))
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode XXXX = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XXXX)
         .addCondition(new MurasamaZansetsuCondition(false))
         .setDamageMultiplier(ValueModifier.multiplier(1.35F))
         .setImpactMultiplier(1.35F)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.9F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode Air_X = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_X_AIR)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNAirborneCondition(true, 1.0F))
         .addCondition(new EFNStaminaCondition(2.0F, true))
         .setPriority(10)
         .setDamageMultiplier(ValueModifier.multiplier(0.6F))
         .setImpactMultiplier(0.6F)
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 2.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode Air_XX = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XX_AIR)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNAirborneCondition(true, 1.0F))
         .addCondition(new EFNStaminaCondition(2.0F, true))
         .setPriority(10)
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .setImpactMultiplier(1.0F)
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 2.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode Air_XX_DOUBLEJUMP = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_XX_AIR)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new JumpCountCondition(1))
         .addCondition(new EFNAirborneCondition(true, 1.0F))
         .addCondition(new EFNStaminaCondition(2.0F, true))
         .setPriority(20)
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .setImpactMultiplier(1.0F)
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 2.0F))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      DodgeCounter = ComboNode.create();
      Air_Dodge = ComboNode.create();
      Y_CHARGE_SET = ComboNode.create().addConditionNode(Y_CHARGE).addConditionNode(Y_CHARGE_THROUGH);
      ComboNode Y = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .setPriority(1)
         .setNotCharge(true)
         .setDamageMultiplier(ValueModifier.multiplier(1.1F))
         .setImpactMultiplier(1.1F)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.38F, Y_CHARGE_SET))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode Y_COMBO = EFNComboNodes.create(() -> EFNHfBladeAnimations.HF_BLADE_Y)
         .addCondition(new MurasamaZansetsuCondition(false))
         .addCondition(new EFNOnGroundCondition(false))
         .addCondition(new EFNDownKeyCondition())
         .setPriority(8)
         .setNotCharge(true)
         .setConvertTime(-0.05F)
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .setImpactMultiplier(1.2F)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.38F, Y_CHARGE_SET))
         .addTimeEvent(TimeEvents.setMurasamaSheath(0.1F, false, true));
      ComboNode AirAttack = ComboNode.create().addConditionNode(Air_XX).addConditionNode(X);
      ComboNode AirAttack_1 = ComboNode.create().addConditionNode(Air_X).addConditionNode(X);
      ComboNode BasicAttack = ComboNode.create()
         .addConditionNode(Air_X)
         .addConditionNode(Air_XX_DOUBLEJUMP)
         .addConditionNode(DASH_X)
         .addConditionNode(DASH_X_SP)
         .addConditionNode(X)
         .addConditionNode(SHEATH_X);
      ComboNode BasicAttack_1 = ComboNode.create()
         .addConditionNode(Air_XX)
         .addConditionNode(Air_XX_DOUBLEJUMP)
         .addConditionNode(DASH_X)
         .addConditionNode(DASH_X_SP)
         .addConditionNode(X)
         .addConditionNode(SHEATH_X);
      ComboNode Attack2 = ComboNode.create()
         .addConditionNode(Air_X)
         .addConditionNode(Air_XX_DOUBLEJUMP)
         .addConditionNode(DASH_X)
         .addConditionNode(DASH_X_SP)
         .addConditionNode(XX);
      ComboNode Attack3 = ComboNode.create()
         .addConditionNode(Air_X)
         .addConditionNode(Air_XX_DOUBLEJUMP)
         .addConditionNode(DASH_X)
         .addConditionNode(DASH_X_SP)
         .addConditionNode(XXX);
      ComboNode Attack4 = ComboNode.create()
         .addConditionNode(Air_X)
         .addConditionNode(Air_XX_DOUBLEJUMP)
         .addConditionNode(DASH_X)
         .addConditionNode(DASH_X_SP)
         .addConditionNode(XXXX);
      ComboNode ComboSKill_X = ComboNode.create()
         .addConditionNode(XY)
         .addConditionNode(Y_COMBO)
         .addConditionNode(Y_AIR)
         .addConditionNode(Y_DASH_SP)
         .addConditionNode(Y_DASH)
         .addConditionNode(Y_KICK)
         .addConditionNode(COUNTER);
      ComboNode ComboSKill_XX = ComboNode.create()
         .addConditionNode(XXY)
         .addConditionNode(Y_COMBO)
         .addConditionNode(Y_AIR)
         .addConditionNode(Y_DASH_SP)
         .addConditionNode(Y_DASH)
         .addConditionNode(Y_KICK)
         .addConditionNode(COUNTER);
      ComboNode ComboSKill_XXX_1 = ComboNode.create()
         .addConditionNode(XXXY)
         .addConditionNode(Y_COMBO)
         .addConditionNode(Y_AIR)
         .addConditionNode(Y_DASH_SP)
         .addConditionNode(Y_DASH)
         .addConditionNode(Y_KICK)
         .addConditionNode(COUNTER);
      ComboNode ComboSKill_XXX_2 = ComboNode.create()
         .addConditionNode(XXXY)
         .addConditionNode(Y_COMBO)
         .addConditionNode(Y_AIR)
         .addConditionNode(Y_DASH_SP)
         .addConditionNode(Y_DASH)
         .addConditionNode(COUNTER);
      ComboNode Skill = ComboNode.create()
         .addConditionNode(Y)
         .addConditionNode(Y_AIR)
         .addConditionNode(Y_DASH_SP)
         .addConditionNode(Y_DASH)
         .addConditionNode(Y_KICK)
         .addConditionNode(Y_SHEATH)
         .addConditionNode(Y_SHEATH_DASH)
         .addConditionNode(Y_SHEATH_DASH_THROUGH)
         .addConditionNode(COUNTER);
      ComboNode Sheath = ComboNode.create().addConditionNode(SHEATH_IN).addConditionNode(SHEATH_IN_RUN).addConditionNode(SWORD_OUT).addConditionNode(TAUNT);
      HfBlade_root.key1(BasicAttack);
      HfBlade_root.key1_3(Skill);
      HfBlade_root.key3(Skill);
      HfBlade_root.key4(Sheath);
      SHEATH_IN.key1(BasicAttack);
      SHEATH_IN.key1_3(Skill);
      SHEATH_IN.key3(Skill);
      SHEATH_IN.key4(Sheath);
      SHEATH_IN_RUN.key1(BasicAttack);
      SHEATH_IN_RUN.key1_3(Skill);
      SHEATH_IN_RUN.key3(Skill);
      SHEATH_IN_RUN.key4(Sheath);
      SWORD_OUT.key1(BasicAttack);
      SWORD_OUT.key1_3(Skill);
      SWORD_OUT.key3(Skill);
      SWORD_OUT.key4(Sheath);
      TAUNT.key1(BasicAttack);
      TAUNT.key1_3(Skill);
      TAUNT.key3(Skill);
      TAUNT.key4(Sheath);
      COUNTER.key1(Attack4);
      COUNTER.key1_3(Skill);
      COUNTER.key3(Skill);
      Y_DASH_SP.key1(Attack2);
      Y_DASH_SP.key1_3(Skill);
      Y_DASH_SP.key3(Skill);
      Y_DASH.key1(BasicAttack);
      Y_DASH.key1_3(Skill);
      Y_DASH.key3(Skill);
      Y_KICK.key1(Attack4);
      Y_KICK.key1_3(ComboSKill_XXX_2);
      Y_KICK.key3(ComboSKill_XXX_2);
      Y_SHEATH.key1(BasicAttack);
      Y_SHEATH.key1_3(Skill);
      Y_SHEATH.key3(Skill);
      Y_SHEATH_DASH.key1(BasicAttack);
      Y_SHEATH_DASH.key1_3(Skill);
      Y_SHEATH_DASH.key3(Skill);
      Y_SHEATH_DASH_THROUGH.key1(BasicAttack);
      Y_SHEATH_DASH_THROUGH.key1_3(Skill);
      Y_SHEATH_DASH_THROUGH.key3(Skill);
      DASH_X.key1(BasicAttack);
      DASH_X.key1_3(XY_DASH_X);
      DASH_X.key3(XY_DASH_X);
      DASH_X_SP.key1(BasicAttack);
      DASH_X_SP.key1_3(XY_DASH_X);
      DASH_X_SP.key3(XY_DASH_X);
      SHEATH_X.key1(BasicAttack);
      SHEATH_X.key1_3(Skill);
      SHEATH_X.key3(Skill);
      Air_X.key1(AirAttack);
      Air_X.key1_3(Skill);
      Air_X.key3(Skill);
      Air_XX.key1(AirAttack_1);
      Air_XX.key1_3(Skill);
      Air_XX.key3(Skill);
      Air_XX_DOUBLEJUMP.key1(AirAttack_1);
      Air_XX_DOUBLEJUMP.key1_3(Skill);
      Air_XX_DOUBLEJUMP.key3(Skill);
      Y_AIR.key1(BasicAttack);
      Y_AIR.key1_3(Skill);
      Y_AIR.key3(Skill);
      Y_CHARGE_AIR.key1(BasicAttack_1);
      Y_CHARGE_AIR.key1_3(Skill);
      Y_CHARGE_AIR.key3(Skill);
      DodgeCounter.key1(BasicAttack);
      DodgeCounter.key1_3(Skill);
      DodgeCounter.key3(Skill);
      DodgeCounter.key4(Sheath);
      Air_Dodge.key1(Air_XX);
      Air_Dodge.key1_3(Skill);
      Air_Dodge.key3(Skill);
      Air_Dodge.key4(Sheath);
      Y.key1(BasicAttack);
      Y.key1_3(Skill);
      Y.key3(Skill);
      Y_CHARGE.key1(BasicAttack);
      Y_CHARGE.key1_3(Skill);
      Y_CHARGE.key3(Skill);
      Y_CHARGE_THROUGH.key1(BasicAttack);
      Y_CHARGE_THROUGH.key1_3(Skill);
      Y_CHARGE_THROUGH.key3(Skill);
      Y_COMBO.key1(BasicAttack);
      Y_COMBO.key1_3(Skill);
      Y_COMBO.key3(Skill);
      X.key1(Attack2);
      X.key1_3(ComboSKill_X);
      X.key3(ComboSKill_X);
      XY.key1(BasicAttack);
      XY.key1_3(Skill);
      XY.key3(Skill);
      XY_DASH_X.key1(BasicAttack);
      XY_DASH_X.key1_3(Skill);
      XY_DASH_X.key3(Skill);
      XY_CHARGE.key1(BasicAttack);
      XY_CHARGE.key1_3(Skill);
      XY_CHARGE.key3(Skill);
      XX.key1(Attack3);
      XX.key1_3(ComboSKill_XX);
      XX.key3(ComboSKill_XX);
      XXY.key1(BasicAttack);
      XXY.key1_3(Skill);
      XXY.key3(Skill);
      XXY_CHARGE.key1(BasicAttack);
      XXY_CHARGE.key1_3(Skill);
      XXY_CHARGE.key3(Skill);
      XXX.key1(Attack4);
      XXX.key1_3(ComboSKill_XXX_1);
      XXX.key3(ComboSKill_XXX_1);
      XXXY.key1(BasicAttack);
      XXXY.key1_3(Skill);
      XXXY.key3(Skill);
      XXXY_CHARGE.key1(BasicAttack);
      XXXY_CHARGE.key1_3(Skill);
      XXXY_CHARGE.key3(Skill);
      XXXX.key1(BasicAttack);
      XXXX.key1_3(Skill);
      XXXX.key3(Skill);
      HfBlade = registryWorker.build(
         "hf_blade",
         HfBladeInnate::new,
         ComboBasicAttack.createComboBasicAttack(HfBladeInnate::new).setMaxPressTime(1).setReserveTime(11).setCombo(HfBlade_root).setShouldDrawGui(true)
      );
      HfBlade_Passive = registryWorker.build(
         "hf_blade_passive", HfBladePassive::new, PassiveSkill.createPassiveBuilder(builder -> new HfBladePassive(builder)).setCategory(SkillCategories.WEAPON_PASSIVE).setResource(Resource.NONE)
      );
   }
}
