package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.HitEvents;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.input.EFNAngelKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNDemonKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNDownKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNJumpKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNKeyLongPressCondition;
import com.hm.efn.comboevents.condition.input.EFNUpKeyCondition;
import com.hm.efn.comboevents.condition.state.EFNAirborneCondition;
import com.hm.efn.comboevents.condition.state.EFNOnGroundCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.YamatoInnate;
import com.hm.efn.skill.weapon_passive.YamatoPassive;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.PressIntervalCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Yamato {
   public static Skill yamato;
   public static Skill yamato_passive;
   public static ComboNode Yamato_root;
   public static ComboNode Judgement_Cut;
   public static ComboNode Flare_Rise;
   public static ComboNode Flare_Rise_Rapid;
   public static ComboNode Volcano_hold;
   public static ComboNode Volcano_hold_Rapid;
   public static ComboNode Orbit_Ex;
   public static ComboNode RepaidSlashCombo;
   public static ComboNode RepaidSlashCombo_Re;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent skillBuildEvent) {
      ModRegistryWorker registryWorker = skillBuildEvent.createRegistryWorker("efn");
      Yamato_root = ComboNode.create();
      Judgement_Cut = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL)
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY1))
         .setPriority(10);
      Flare_Rise = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_FLARECUT_RISING)
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.15F))
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .setDamageMultiplier(ValueModifier.multiplier(0.9F));
      Flare_Rise_Rapid = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_FLARECUT_RISING)
         .addCondition(new EFNJumpKeyCondition())
         .addCondition(new EFNOnGroundCondition())
         .addCondition(new EFNStaminaCondition(2.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 2.0F))
         .setConvertTime(-0.05F)
         .setDamageMultiplier(ValueModifier.multiplier(0.9F))
         .setPriority(12);
      Volcano_hold = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_VOLCANOL_CHARGE)
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .setDamageMultiplier(ValueModifier.multiplier(2.5F))
         .addTimeEvent(TimeEvents.summonBlastSwordMid(0.1F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.45F, Judgement_Cut));
      Volcano_hold_Rapid = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_VOLCANOL_CHARGE)
         .addCondition(new EFNDownKeyCondition())
         .setPriority(12)
         .setDamageMultiplier(ValueModifier.multiplier(1.5F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.45F, Judgement_Cut));
      Orbit_Ex = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_ORBIT_2)
         .addCondition(new EFNAirborneCondition(false, 0.1F))
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.1F))
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY1));
      ComboNode Aerialrave_Auto1 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO1)
         .setPriority(15)
         .setDamageMultiplier(ValueModifier.multiplier(0.65F))
         .addCondition(new EFNAirborneCondition())
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.5F, Judgement_Cut));
      ComboNode Aerialrave_Auto2 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO2)
         .setPriority(20)
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.5F, Judgement_Cut));
      ComboNode Aerialrave_Auto3 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO3)
         .setPriority(20)
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, -3.0F))
         .addTimeEvent(TimeEvents.summonBlastSwordMid(0.15F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.7F, Judgement_Cut));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_NORMAL_AUTO1)
         .addCondition(new EFNOnGroundCondition())
         .setDamageMultiplier(ValueModifier.multiplier(0.5F))
         .setPriority(1)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.6F, Judgement_Cut));
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_NORMAL_AUTO2)
         .addCondition(new EFNOnGroundCondition())
         .setDamageMultiplier(ValueModifier.multiplier(0.5F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.6F, Judgement_Cut));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_NORMAL_AUTO3)
         .addCondition(new EFNOnGroundCondition())
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .setPriority(1)
         .addTimeEvent(TimeEvents.summonBlastSwordMid(0.05F))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, -3.0F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.6F, Judgement_Cut));
      ComboNode Extend_Auto3 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_EXTEND_AUTO3)
         .addCondition(new EFNOnGroundCondition())
         .addCondition(new PressIntervalCondition(415L, 1000L))
         .setDamageMultiplier(ValueModifier.multiplier(0.85F))
         .setPriority(2)
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.15F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.7F, Judgement_Cut));
      ComboNode Extend_Auto4 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_EXTEND_AUTO4)
         .addCondition(new EFNOnGroundCondition())
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.85F, Judgement_Cut));
      ComboNode Extend_Auto5 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_EXTEND_AUTO5)
         .addCondition(new EFNOnGroundCondition())
         .setDamageMultiplier(ValueModifier.multiplier(1.5F))
         .addTimeEvent(TimeEvents.summonBlastSwordMid(0.15F))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, -3.0F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.7F, Judgement_Cut));
      ComboNode Divorce_Auto1 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1)
         .addCondition(new EFNOnGroundCondition())
         .addCondition(new EFNDemonKeyCondition())
         .setDamageMultiplier(ValueModifier.multiplier(2.0F))
         .addTimeEvent(TimeEvents.summonBlastSwordLite(0.2F))
         .setPriority(9)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.7F, Judgement_Cut));
      ComboNode Divorce_Auto2 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_DIVORCE_AUTO2)
         .addCondition(new EFNOnGroundCondition())
         .addCondition(new EFNDemonKeyCondition())
         .setDamageMultiplier(ValueModifier.multiplier(2.5F))
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.15F))
         .setPriority(9)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.0F, Judgement_Cut));
      ComboNode Divorce_Auto3 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_DIVORCE_AUTO3)
         .addCondition(new EFNOnGroundCondition())
         .addCondition(new EFNDemonKeyCondition())
         .setDamageMultiplier(ValueModifier.multiplier(3.0F))
         .setPriority(9)
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, -3.0F))
         .addTimeEvent(TimeEvents.summonDamoclesSword(0.15F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.0F, Judgement_Cut));
      ComboNode Volcano_cut = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_VOLCANOL)
         .addCondition(new EFNDemonKeyCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 3.0F))
         .setPriority(20)
         .setDamageMultiplier(ValueModifier.multiplier(1.5F))
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.15F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.0F, Judgement_Cut))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.7F, Volcano_hold));
      ComboNode Helmbreaker = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_HELMBREAKER)
         .addCondition(new EFNAirborneCondition())
         .addCondition(new EFNDemonKeyCondition(true))
         .addCondition(new EFNAngelKeyCondition(true))
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .setPriority(20)
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.1F))
         .addTimeEvent(new TimeStampedEvent(0.0F, livingEntityPatch -> ((Player)livingEntityPatch.getOriginal()).removeEffect(MobEffects.SLOW_FALLING)))
         .addTimeEvent(
            new TimeStampedEvent(
               0.0F, livingEntityPatch -> ((Player)livingEntityPatch.getOriginal()).removeEffect(EFNMobEffectRegistry.VERTICALSTOP)
            )
         )
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.5F, Judgement_Cut));
      ComboNode KillerBee = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_KILLERBEE)
         .addCondition(new EFNAirborneCondition())
         .addCondition(new EFNDemonKeyCondition())
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .setPriority(21)
         .addTimeEvent(TimeEvents.summonBlastSwordLite(0.05F))
         .addTimeEvent(new TimeStampedEvent(0.0F, livingEntityPatch -> ((Player)livingEntityPatch.getOriginal()).removeEffect(MobEffects.SLOW_FALLING)))
         .addTimeEvent(
            new TimeStampedEvent(
               0.0F, livingEntityPatch -> ((Player)livingEntityPatch.getOriginal()).removeEffect(EFNMobEffectRegistry.VERTICALSTOP)
            )
         );
      ComboNode Orbit_1 = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_ORBIT_1)
         .addCondition(new EFNAirborneCondition())
         .addCondition(new EFNAngelKeyCondition())
         .addCondition(new EFNStaminaCondition(2.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 2.0F))
         .setDamageMultiplier(ValueModifier.multiplier(0.7F))
         .setPriority(21)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.55F, Orbit_Ex));
      ComboNode Drive = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_DRIVE)
         .addCondition(new EFNAirborneCondition())
         .addCondition(new EFNDemonKeyCondition())
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.1F))
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .setPriority(21);
      ComboNode Stomp = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_STOMP)
         .addCondition(new EFNDemonKeyCondition())
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .setPriority(50)
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, -2.0F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.6F, Judgement_Cut));
      ComboNode Flush = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_AIRFLUSH)
         .addCondition(new EFNAirborneCondition(false, 0.1F))
         .addCondition(new EFNAngelKeyCondition())
         .setDamageMultiplier(ValueModifier.multiplier(0.3F))
         .setPriority(20);
      ComboNode Flare = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_FLARECUT)
         .addCondition(new EFNAngelKeyCondition())
         .addCondition(new EFNOnGroundCondition())
         .setDamageMultiplier(ValueModifier.multiplier(0.8F))
         .setPriority(20)
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.25F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.23F, Flare_Rise))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.1F, Judgement_Cut))
         .addHitEvent(HitEvents.addEffect(EFNMobEffectRegistry.HORIZONTAL_STOP, 1, 5));
      ComboNode Flare_Repaid = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_FLARECUT)
         .addCondition(new EFNUpKeyCondition())
         .addCondition(new EFNOnGroundCondition())
         .setDamageMultiplier(ValueModifier.multiplier(0.9F))
         .setPriority(11)
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.29F, Flare_Rise))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.0F, Judgement_Cut))
         .addHitEvent(HitEvents.addEffect(EFNMobEffectRegistry.HORIZONTAL_STOP, 1, 5));
      ComboNode Flare_Repaid_Re = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_FLARECUT)
         .addCondition(new EFNUpKeyCondition())
         .addCondition(new EFNOnGroundCondition())
         .setDamageMultiplier(ValueModifier.multiplier(0.9F))
         .setConvertTime(-0.15F)
         .setPriority(11)
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.35F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.29F, Flare_Rise))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.0F, Judgement_Cut))
         .addHitEvent(HitEvents.addEffect(EFNMobEffectRegistry.HORIZONTAL_STOP, 1, 5));
      ComboNode Volcano_Repaid = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_VOLCANOL)
         .addCondition(new EFNDownKeyCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 3.0F))
         .setPriority(12)
         .setDamageMultiplier(ValueModifier.multiplier(1.0F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.0F, Judgement_Cut))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.7F, Volcano_hold_Rapid));
      ComboNode UpperSlash = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_UPPERSLASH)
         .addCondition(new EFNOnGroundCondition())
         .setPriority(10)
         .addTimeEvent(TimeEvents.summonBlastSwordLite(0.1F))
         .addHitEvent(HitEvents.addEffect(MobEffects.SLOW_FALLING, 1, 60))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.35F, Judgement_Cut));
      ComboNode UpperSlash_Rise = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_UPPERSLASH_HOLD)
         .addCondition(new EFNOnGroundCondition())
         .addCondition(new EFNUpKeyCondition())
         .addTimeEvent(TimeEvents.summonBlastSwordLite(0.1F))
         .setPriority(11);
      ComboNode UpperSlash_Repaid = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_UPPERSLASH)
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .setPriority(12);
      ComboNode Divorce_Repaid = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1)
         .addCondition(new EFNDemonKeyCondition())
         .setPriority(12)
         .setDamageMultiplier(ValueModifier.multiplier(1.3F))
         .addTimeEvent(TimeEvents.summonBlastSwordLite(0.2F))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.7F, Judgement_Cut));
      RepaidSlashCombo = ComboNode.create()
         .addConditionNode(Judgement_Cut)
         .addConditionNode(Flare_Repaid)
         .addConditionNode(Flare_Rise_Rapid)
         .addConditionNode(Volcano_Repaid)
         .addConditionNode(UpperSlash_Repaid)
         .addConditionNode(Divorce_Repaid);
      RepaidSlashCombo_Re = ComboNode.create()
         .addConditionNode(Judgement_Cut)
         .addConditionNode(Flare_Repaid_Re)
         .addConditionNode(Flare_Rise_Rapid)
         .addConditionNode(Volcano_Repaid)
         .addConditionNode(UpperSlash_Repaid)
         .addConditionNode(Divorce_Repaid);
      ComboNode RepaidSlash = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_REPAIDSLASH)
         .addCondition(new EFNAngelKeyCondition())
         .setPriority(11)
         .addTimeEvent(TimeEvents.applyEffectAtTime(MobEffects.MOVEMENT_SPEED, 30, 1, 0.0F))
         .addTimeEvent(TimeEvents.summonHeavyRainLite(0.15F))
         .addTimePeriodEvent(TimeEvents.TimePeriodSimulationComboNodeEvent(0.2F, 0.6F, RepaidSlashCombo))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.1F, Judgement_Cut));
      ComboNode RepaidSlash_Re = EFNComboNodes.create(() -> EFNYamatoAnimations.YAMATO_REPAIDSLASH)
         .addCondition(new EFNAngelKeyCondition())
         .addCondition(new EFNStaminaCondition(2.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 2.0F))
         .setConvertTime(-0.15F)
         .setPriority(20)
         .addTimeEvent(TimeEvents.applyEffectAtTime(MobEffects.MOVEMENT_SPEED, 30, 1, 0.0F))
         .addTimePeriodEvent(TimeEvents.TimePeriodSimulationComboNodeEvent(0.2F, 0.6F, RepaidSlashCombo_Re))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(1.1F, Judgement_Cut));
      ComboNode BasicAttack = ComboNode.create()
         .addConditionNode(Aerialrave_Auto1)
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto1)
         .addConditionNode(RepaidSlash)
         .addConditionNode(Auto1)
         .addConditionNode(Judgement_Cut);
      ComboNode BasicAttack_2 = ComboNode.create()
         .addConditionNode(RepaidSlash_Re)
         .addConditionNode(Divorce_Auto1)
         .addConditionNode(Auto1)
         .addConditionNode(Judgement_Cut);
      ComboNode Attack2 = ComboNode.create()
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto1)
         .addConditionNode(RepaidSlash)
         .addConditionNode(Auto2);
      ComboNode AirAttack2 = ComboNode.create()
         .addConditionNode(Aerialrave_Auto2)
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto1)
         .addConditionNode(RepaidSlash);
      ComboNode Attack3 = ComboNode.create()
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto1)
         .addConditionNode(RepaidSlash)
         .addConditionNode(Auto3)
         .addConditionNode(Extend_Auto3);
      ComboNode AirAttack3 = ComboNode.create()
         .addConditionNode(Aerialrave_Auto3)
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto1)
         .addConditionNode(RepaidSlash);
      ComboNode Divorce_Attack2 = ComboNode.create()
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto2)
         .addConditionNode(Auto1)
         .addConditionNode(RepaidSlash);
      ComboNode Divorce_Attack3 = ComboNode.create()
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto3)
         .addConditionNode(Auto1)
         .addConditionNode(RepaidSlash);
      ComboNode Attack4 = ComboNode.create()
         .addConditionNode(RepaidSlash)
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto1)
         .addConditionNode(Extend_Auto4);
      ComboNode Attack5 = ComboNode.create()
         .addConditionNode(RepaidSlash)
         .addConditionNode(Orbit_1)
         .addConditionNode(KillerBee)
         .addConditionNode(Divorce_Auto1)
         .addConditionNode(Extend_Auto5);
      ComboNode Skills = ComboNode.create()
         .addConditionNode(Flare)
         .addConditionNode(UpperSlash)
         .addConditionNode(UpperSlash_Rise)
         .addConditionNode(Drive)
         .addConditionNode(Flush)
         .addConditionNode(Helmbreaker)
         .addConditionNode(Volcano_cut);
      ComboNode Skills_Drive = ComboNode.create()
         .addConditionNode(Flare)
         .addConditionNode(UpperSlash)
         .addConditionNode(UpperSlash_Rise)
         .addConditionNode(Drive)
         .addConditionNode(Stomp)
         .addConditionNode(Flush)
         .addConditionNode(Helmbreaker)
         .addConditionNode(Volcano_cut);
      Yamato_root.key1(BasicAttack);
      Yamato_root.key3(Skills);
      Yamato_root.key1_3(Skills);
      Aerialrave_Auto1.key1(AirAttack2);
      Aerialrave_Auto1.key3(Skills);
      Aerialrave_Auto1.key1_3(Skills);
      Aerialrave_Auto2.key1(AirAttack3);
      Aerialrave_Auto2.key3(Skills);
      Aerialrave_Auto2.key1_3(Skills);
      Aerialrave_Auto3.key1(BasicAttack);
      Aerialrave_Auto3.key3(Skills);
      Aerialrave_Auto3.key1_3(Helmbreaker);
      Orbit_1.key1(BasicAttack);
      Orbit_1.key3(Skills);
      Orbit_1.key1_3(Skills);
      Orbit_Ex.key1(BasicAttack);
      Orbit_Ex.key3(Skills);
      Orbit_Ex.key1_3(Skills);
      KillerBee.key1(BasicAttack);
      KillerBee.key3(Skills);
      KillerBee.key1_3(Skills);
      Divorce_Auto1.key1(Divorce_Attack2);
      Divorce_Auto1.key3(Skills);
      Divorce_Auto1.key1_3(Skills);
      Divorce_Repaid.key1(Divorce_Attack2);
      Divorce_Repaid.key3(Skills);
      Divorce_Repaid.key1_3(Skills);
      Divorce_Auto2.key1(Divorce_Attack3);
      Divorce_Auto2.key3(Skills);
      Divorce_Auto2.key1_3(Skills);
      Divorce_Auto3.key1(BasicAttack);
      Divorce_Auto3.key3(Skills);
      Drive.key1(BasicAttack);
      Drive.key3(Skills_Drive);
      Stomp.key1(Attack4);
      Stomp.key3(Skills);
      Helmbreaker.key1(Attack4);
      Helmbreaker.key3(Skills);
      Flush.key1(Attack2);
      Flush.key3(Skills);
      RepaidSlash.key1(BasicAttack_2);
      RepaidSlash.key3(Skills);
      RepaidSlash_Re.key1(BasicAttack_2);
      RepaidSlash_Re.key3(Skills);
      Flare.key1(BasicAttack);
      Flare.key3(Skills);
      Flare_Repaid.key1(BasicAttack);
      Flare_Repaid.key3(Skills);
      Flare_Repaid_Re.key1(BasicAttack);
      Flare_Repaid_Re.key3(Skills);
      Flare_Rise_Rapid.key1(BasicAttack);
      Flare_Rise_Rapid.key3(Skills);
      Flare_Rise.key1(BasicAttack);
      Flare_Rise.key3(Skills);
      UpperSlash.key1(BasicAttack);
      UpperSlash.key3(Skills);
      UpperSlash_Repaid.key1(BasicAttack);
      UpperSlash_Repaid.key3(Skills);
      UpperSlash_Rise.key1(BasicAttack);
      UpperSlash_Rise.key3(Skills);
      Judgement_Cut.key1(BasicAttack);
      Judgement_Cut.key3(Skills);
      Volcano_cut.key1(Attack4);
      Volcano_cut.key3(Skills);
      Volcano_hold.key1(Attack4);
      Volcano_hold.key3(Skills);
      Volcano_Repaid.key1(Attack4);
      Volcano_Repaid.key3(Skills);
      Volcano_hold_Rapid.key1(Attack4);
      Volcano_hold_Rapid.key3(Skills);
      Auto1.key1(Attack2);
      Auto1.key3(Skills);
      Auto3.key1_3(Skills);
      Auto2.key1(Attack3);
      Auto2.key3(Skills);
      Auto3.key1_3(Skills);
      Auto3.key1(BasicAttack);
      Auto3.key3(Skills);
      Auto3.key1_3(Skills);
      Extend_Auto3.key1(Attack4);
      Extend_Auto3.key3(Skills);
      Extend_Auto3.key1_3(Skills);
      Extend_Auto4.key1(Attack5);
      Extend_Auto4.key3(Skills);
      Extend_Auto4.key1_3(Skills);
      Extend_Auto5.key1(BasicAttack);
      Extend_Auto5.key3(Skills);
      Extend_Auto5.key1_3(Skills);
      yamato = registryWorker.build(
         "yamato",
         YamatoInnate::new,
         ComboBasicAttack.createComboBasicAttack(YamatoInnate::new).setResetTime(7).setReserveTime(11).setMaxPressTime(1).setCombo(Yamato_root).setShouldDrawGui(false)
      );
      yamato_passive = registryWorker.build(
         "yamato_passive", YamatoPassive::new, PassiveSkill.createPassiveBuilder(builder -> new YamatoPassive(builder)).setCategory(SkillCategories.WEAPON_PASSIVE).setResource(Resource.NONE)
      );
   }
}
