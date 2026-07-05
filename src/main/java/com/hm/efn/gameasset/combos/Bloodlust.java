package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.HitEvents;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNTachiAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.BloodlustInnate;
import com.hm.efn.skill.weapon_passive.BloodlustPassive;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.conditions.PlayerPhaseCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
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
public class Bloodlust {
   public static Skill Bloodlust;
   public static Skill Bloodlust_Passive;
   public static ComboNode DodgeCounter;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_DASH)
         .setPriority(5)
         .addCondition(new EFNSprintingCondition())
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.65F))
         .addHitEvent(HitEvents.createLostHealthDamage_Blood(0.012F));
      ComboNode Air = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_AIRSLASH)
         .setPriority(5)
         .addCondition(new JumpCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addHitEvent(HitEvents.createLostHealthDamage_Blood(0.012F))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 3.0F));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_AUTO1)
         .setPriority(1)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.55F))
         .addHitEvent(HitEvents.createLostHealthDamage_Blood(0.012F));
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_AUTO2)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.55F))
         .addHitEvent(HitEvents.createLostHealthDamage_Blood(0.012F));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_AUTO3)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.65F))
         .addHitEvent(HitEvents.createLostHealthDamage_Blood(0.012F));
      ComboNode Auto4 = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_AUTO4)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.7F))
         .addHitEvent(HitEvents.createLostHealthDamage_Blood(0.012F));
      ComboNode Auto5 = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_AUTO5)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.7F))
         .addHitEvent(HitEvents.createLostHealthDamage_Blood(0.012F));
      ComboNode Skill_Start = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_BLOODLUST)
         .addCondition(new PlayerPhaseCondition(0, 1))
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .setNewPhase(2)
         .setPriority(1)
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.BLODDLUST, -1, 1, 0.1F))
         .addHitEvent(HitEvents.createLostHealthDamage(0.15F));
      ComboNode Skill_End = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_BLOODLUST_END)
         .addCondition(new PlayerPhaseCondition(2, 2))
         .setNewPhase(1)
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .setPriority(2)
         .addTimeEvent(TimeEvents.removeEffectAtTime(EFNMobEffectRegistry.BLODDLUST, 0.1F))
         .addHitEvent(HitEvents.createLostHealthDamage(0.15F));
      DodgeCounter = ComboNode.create();
      ComboNode DodgeCounter_1 = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_DASH).setPriority(5);
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto2);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto3);
      ComboNode Attack4 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto4);
      ComboNode Attack5 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto5);
      ComboNode Skills = ComboNode.create().addConditionNode(Skill_Start).addConditionNode(Skill_End);
      root.key1(BasicAttack);
      root.key3(Skills);
      Dash.key1(Attack2);
      Dash.key3(Skills);
      Air.key1(BasicAttack);
      Air.key3(Skills);
      Skill_Start.key1(Attack5);
      Skill_Start.key3(Skills);
      Skill_End.key1(Attack5);
      Skill_End.key3(Skills);
      DodgeCounter.key1(DodgeCounter_1);
      DodgeCounter.key3(Skills);
      DodgeCounter_1.key1(Attack2);
      DodgeCounter_1.key3(Skills);
      Auto1.key1(Attack2);
      Auto1.key3(Skills);
      Auto2.key1(Attack3);
      Auto2.key3(Skills);
      Auto3.key1(Attack4);
      Auto3.key3(Skills);
      Auto4.key1(Attack5);
      Auto4.key3(Skills);
      Auto5.key1(BasicAttack);
      Auto5.key3(Skills);
      Bloodlust = registryWorker.build(
         "bloodlust",
         BloodlustInnate::new,
         ComboBasicAttack.createComboBasicAttack(BloodlustInnate::new).setResetTime(7).setReserveTime(9).setMaxPressTime(1).setCombo(root).setShouldDrawGui(true)
      );
      Bloodlust_Passive = registryWorker.build(
         "bloodlust_passive", BloodlustPassive::new, PassiveSkill.createPassiveBuilder(builder -> new BloodlustPassive(builder)).setCategory(SkillCategories.WEAPON_PASSIVE).setResource(Resource.NONE)
      );
   }
}
