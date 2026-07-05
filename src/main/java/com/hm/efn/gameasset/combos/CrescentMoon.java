package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStackCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNFalchionAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.CrescentMoonInnate;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.CooldownCondition;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.conditions.MobEffectCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.skill.Skill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class CrescentMoon {
   public static Skill crescentmoon;
   public static ComboNode Strike_Extend;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNFalchionAnimations.FALCHION_DASHATTACK).setPriority(5).addCondition(new EFNSprintingCondition());
      ComboNode Air = EFNComboNodes.create(() -> EFNFalchionAnimations.FALCHION_AIRSLASH)
         .setPriority(5)
         .addCondition(new JumpCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 3.0F));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNFalchionAnimations.FALCHION_AUTO1).setPriority(1).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.3F));
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNFalchionAnimations.FALCHION_AUTO2).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.2F));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNFalchionAnimations.FALCHION_AUTO3).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.3F));
      ComboNode Skill = EFNComboNodes.create(() -> EFNFalchionAnimations.FALCHION_SKILL)
         .setCanBeInterrupt(false)
         .addCondition(new EFNStackCondition(1, Integer.MAX_VALUE))
         .addCondition(new CooldownCondition(false))
         .setCooldown(600);
      Strike_Extend = EFNComboNodes.create(() -> EFNFalchionAnimations.FALCHION_EX2)
         .setPriority(10)
         .setCanBeInterrupt(false)
         .addCondition(new MobEffectCondition(false, EFNMobEffectRegistry.COMBO_EXECUTE_WINDOW, 0, Integer.MAX_VALUE));
      ComboNode Strike = EFNComboNodes.create(() -> EFNFalchionAnimations.FALCHION_STRIKE)
         .setPriority(10)
         .addCondition(new EFNStaminaCondition(4.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.15F, 4.0F));
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1);
      ComboNode BasicAttack_Strike = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1).addConditionNode(Strike_Extend);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto2);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto3);
      ComboNode Skills = ComboNode.create().addConditionNode(Skill);
      root.key1(BasicAttack);
      root.key3(Skills);
      root.key4(Strike);
      Skill.key1(BasicAttack);
      Skill.key3(Skills);
      Skill.key4(Strike);
      Dash.key1(BasicAttack);
      Dash.key3(Skills);
      Dash.key4(Strike);
      Strike.key1(BasicAttack_Strike);
      Strike.key3(Skills);
      Strike.key4(Strike);
      Strike_Extend.key1(BasicAttack);
      Strike_Extend.key3(Skills);
      Strike_Extend.key4(Strike);
      Air.key1(BasicAttack);
      Air.key3(Skills);
      Air.key4(Strike);
      Auto1.key1(Attack2);
      Auto1.key3(Skills);
      Auto1.key4(Strike);
      Auto2.key1(Attack3);
      Auto2.key3(Skills);
      Auto2.key4(Strike);
      Auto3.key1(BasicAttack);
      Auto3.key3(Skills);
      Auto3.key4(Strike);
      crescentmoon = registryWorker.build(
         "crescentmoon",
         CrescentMoonInnate::new,
         ComboBasicAttack.createComboBasicAttack(CrescentMoonInnate::new).setResetTime(7).setReserveTime(9).setMaxPressTime(1).setCombo(root).setShouldDrawGui(true)
      );
   }
}
