package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.HitEvents;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.input.EFNKeyLongPressCondition;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStackCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNDualSwordAnimations;
import com.hm.efn.skill.weapon_innate.AetherialDuskDualSwordInnate;
import com.hm.efn.skill.weapon_passive.AetherialduskPassive;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Aetherialdusk {
   public static Skill Aetherialdusk;
   public static Skill Aetherialdusk_Passive;
   public static ComboNode DodgeCounter;
   public static ComboNode Skill_Extend;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_DASH).setPriority(5).addCondition(new EFNSprintingCondition());
      ComboNode Air = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_AIRSLASH)
         .setPriority(5)
         .addCondition(new JumpCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 3.0F));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_AUTO1)
         .setPriority(1)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.65F))
         .addHitEvent(HitEvents.knockBackFromEntity(0.1F));
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_AUTO2)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.65F))
         .addHitEvent(HitEvents.knockBackFromEntity(0.1F));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_AUTO3)
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.65F))
         .addHitEvent(HitEvents.knockBackFromEntity(0.1F))
         .addHitEvent(HitEvents.createLostHealthDamage(0.05F));
      ComboNode Auto4 = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_AUTO4)
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.75F))
         .addHitEvent(HitEvents.createLostHealthDamage(0.08F))
         .addHitEvent(HitEvents.knockBackFromEntity(0.15F));
      Skill_Extend = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_SKILL_EXTEND)
         .addCondition(new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.KEY3))
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .setPriority(5)
         .addHitEvent(HitEvents.createLostHealthDamage(0.1F))
         .addHitEvent(HitEvents.knockBackFromEntity(0.08F));
      ComboNode Skill = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_SKILL)
         .addCondition(new EFNStackCondition(1, 2))
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.timeConsumeStack(0.2F, 1))
         .addTimePeriodEvent(TimeEvents.TimePeriodSimulationComboNodeEvent(0.4F, 0.55F, Skill_Extend))
         .addHitEvent(HitEvents.addEffect(MobEffects.LEVITATION, 25, 3))
         .addHitEvent(HitEvents.addEffect(MobEffects.SLOW_FALLING, 30, 2))
         .addHitEvent(HitEvents.knockBackFromEntity(0.05F))
         .addHitEvent(HitEvents.createLostHealthDamage(0.1F));
      DodgeCounter = ComboNode.create();
      ComboNode DodgeCounter_1 = EFNComboNodes.create(() -> EFNDualSwordAnimations.NF_DUAL_STORMATK)
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, -3.0F))
         .setPriority(5);
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1);
      ComboNode BasicAttack_1 = ComboNode.create().addConditionNode(Dash).addConditionNode(Auto1);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto2);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto3);
      ComboNode Attack4 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto4);
      ComboNode Skills = ComboNode.create().addConditionNode(Skill);
      root.key1(BasicAttack);
      root.key3(Skills);
      Dash.key1(BasicAttack);
      Dash.key3(Skills);
      Air.key1(BasicAttack_1);
      Air.key3(Skills);
      Skill.key1(Attack4);
      Skill.key3(Skill);
      Skill_Extend.key1(BasicAttack);
      Skill_Extend.key3(Skill);
      Auto1.key1(Attack2);
      Auto1.key3(Skills);
      Auto2.key1(Attack3);
      Auto2.key3(Skills);
      Auto3.key1(Attack4);
      Auto3.key3(Skills);
      Auto4.key1(BasicAttack);
      Auto4.key3(Skills);
      DodgeCounter.key1(DodgeCounter_1);
      DodgeCounter.key3(Skills);
      DodgeCounter_1.key1(Attack3);
      DodgeCounter_1.key3(Skills);
      Aetherialdusk = registryWorker.build(
         "aetherialdusk",
         AetherialDuskDualSwordInnate::new,
         ComboBasicAttack.createComboBasicAttack(AetherialDuskDualSwordInnate::new).setResetTime(7).setMaxPressTime(1).setCombo(root).setShouldDrawGui(true)
      );
      Aetherialdusk_Passive = registryWorker.build(
         "aetherialdusk_passive",
         AetherialduskPassive::new,
         PassiveSkill.createPassiveBuilder(builder -> new AetherialduskPassive(builder)).setCategory(SkillCategories.WEAPON_PASSIVE).setResource(Resource.NONE)
      );
   }
}
