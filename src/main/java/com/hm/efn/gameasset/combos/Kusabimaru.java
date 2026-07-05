package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.input.EFNSneakKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNSprintKeyCondition;
import com.hm.efn.comboevents.condition.state.EFNAirborneCondition;
import com.hm.efn.comboevents.condition.state.EFNOnGroundCondition;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStackCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import com.hm.efn.gameasset.animations.EFNTachiAnimations;
import com.hm.efn.skill.weapon_innate.KusabimaruInnate;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Kusabimaru {
   public static Skill kusabimaru;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      if (!ModList.get().isLoaded("efn_enhance")) {
         ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
         ComboNode root = ComboNode.create();
         ComboNode Dash = EFNComboNodes.create(() -> Animations.TACHI_DASH)
            .setPriority(5)
            .addCondition(new EFNSprintingCondition())
            .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.0F));
         ComboNode Air = EFNComboNodes.create(() -> EFNTachiAnimations.NF_TACHI_AIRSLASH)
            .setPriority(5)
            .addCondition(new JumpCondition())
            .addCondition(new EFNStaminaCondition(3.0F, true))
            .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 3.0F));
         ComboNode Auto1 = EFNComboNodes.create(() -> EFNSekiroAnimations.KUSABIMARU_AUTO1).setPriority(1).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.65F));
         ComboNode Auto2 = EFNComboNodes.create(() -> EFNSekiroAnimations.KUSABIMARU_AUTO2).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.55F));
         ComboNode Auto3 = EFNComboNodes.create(() -> EFNSekiroAnimations.KUSABIMARU_AUTO3).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.7F));
         ComboNode Auto4 = EFNComboNodes.create(() -> EFNSekiroAnimations.KUSABIMARU_AUTO4).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.55F));
         ComboNode Auto5 = EFNComboNodes.create(() -> EFNSekiroAnimations.KUSABIMARU_AUTO5).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.55F));
         ComboNode Skill = EFNComboNodes.create(() -> EFNSekiroAnimations.DRAGON_FLASH)
            .setPriority(1)
            .setNotCharge(true)
            .addCondition(new EFNStackCondition(2, Integer.MAX_VALUE))
            .addCondition(new EFNOnGroundCondition())
            .addTimeEvent(TimeEvents.timeConsumeStack(0.2F, 2));
         ComboNode Skill_Combo_1 = EFNComboNodes.create(() -> EFNSekiroAnimations.ICHIMONJI_1)
            .setPriority(5)
            .setNotCharge(true)
            .setDamageMultiplier(ValueModifier.multiplier(1.3F))
            .setStunTypeModifier(StunType.LONG)
            .addCondition(new EFNSneakKeyCondition())
            .addCondition(new EFNOnGroundCondition())
            .addCondition(new EFNStackCondition(1, Integer.MAX_VALUE))
            .addTimeEvent(TimeEvents.timeConsumeStack(0.2F, 1))
            .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, -5.0F));
         ComboNode Skill_Combo_2 = EFNComboNodes.create(() -> EFNSekiroAnimations.ICHIMONJI_2)
            .setPriority(5)
            .setNotCharge(true)
            .setDamageMultiplier(ValueModifier.multiplier(1.3F))
            .setStunTypeModifier(StunType.LONG)
            .addCondition(new EFNOnGroundCondition())
            .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, -5.0F));
         ComboNode Skill_Dash = EFNComboNodes.create(() -> EFNSekiroAnimations.SHADOW_RUSH)
            .setPriority(5)
            .setNotCharge(true)
            .addCondition(new EFNStaminaCondition(4.5F, true))
            .addCondition(new EFNOnGroundCondition())
            .addCondition(new EFNSprintKeyCondition())
            .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 4.5F));
         ComboNode Skill_Air = EFNComboNodes.create(() -> EFNSekiroAnimations.SAKURA_DANCE)
            .setPriority(5)
            .setNotCharge(true)
            .addCondition(new EFNStackCondition(2, Integer.MAX_VALUE))
            .addCondition(new EFNAirborneCondition(true, 0.5F))
            .addTimeEvent(TimeEvents.timeConsumeStack(0.2F, 2));
         ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1);
         ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto2);
         ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto3);
         ComboNode Attack4 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto4);
         ComboNode Attack5 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto5);
         ComboNode Skills = ComboNode.create().addConditionNode(Skill).addConditionNode(Skill_Dash).addConditionNode(Skill_Air).addConditionNode(Skill_Combo_1);
         root.key1(BasicAttack);
         root.key3(Skills);
         Dash.key1(BasicAttack);
         Dash.key3(Skills);
         Air.key1(BasicAttack);
         Air.key3(Skills);
         Skill.key1(BasicAttack);
         Skill.key3(Skills);
         Skill_Combo_1.key1(BasicAttack);
         Skill_Combo_1.key3(Skill_Combo_2);
         Skill_Combo_2.key1(BasicAttack);
         Skill_Combo_2.key3(Skills);
         Skill_Dash.key1(BasicAttack);
         Skill_Dash.key3(Skills);
         Skill_Air.key1(BasicAttack);
         Skill_Air.key3(Skills);
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
         kusabimaru = registryWorker.build(
            "kusabimaru", KusabimaruInnate::new, ComboBasicAttack.createComboBasicAttack(KusabimaruInnate::new).setMaxPressTime(1).setCombo(root).setShouldDrawGui(true)
         );
      }
   }
}
