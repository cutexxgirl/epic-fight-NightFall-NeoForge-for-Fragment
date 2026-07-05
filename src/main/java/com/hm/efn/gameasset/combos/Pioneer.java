package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.HitEvents;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStackCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.skill.weapon_innate.PioneerInnate;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Pioneer {
   public static Skill pioneer;
   public static ComboNode Skill_2;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_DASH).setPriority(5).addCondition(new EFNSprintingCondition());
      ComboNode Air = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AIRSLASH)
         .setPriority(5)
         .addCondition(new JumpCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 3.0F));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO1)
         .setPriority(1)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.45F))
         .addTimeEvent(TimeEvents.startComboCounter(0.01F));
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO2).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.48F));
      ComboNode Auto2_Enhanced = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO2)
         .setPriority(2)
         .setArmorNegation(120.0F)
         .setStunTypeModifier(StunType.HOLD)
         .setCanBeInterrupt(false)
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.48F))
         .addHitEvent(HitEvents.createLostHealthDamage(0.04F))
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO3).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.55F));
      ComboNode Auto4 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO4)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.55F))
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()));
      Skill_2 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_SKILL_SECOND).setNotCharge(true).setCanBeInterrupt(false);
      ComboNode Skill_1 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_SKILL_FIRST)
         .addCondition(new EFNStackCondition(1, 2))
         .addTimeEvent(TimeEvents.timeConsumeStack(0.2F, 1))
         .addTimeEvent(TimeEvents.TimeSimulationComboNodeEvent(0.45F, Skill_2))
         .addHitEvent(HitEvents.createLostHealthDamage(0.05F))
         .setNotCharge(true)
         .setCanBeInterrupt(false);
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto2);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto3);
      ComboNode Attack4 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto4);
      ComboNode Skills = ComboNode.create().addConditionNode(Skill_1);
      root.key1(BasicAttack);
      root.key3(Skills);
      Dash.key1(Attack3);
      Dash.key3(Skills);
      Air.key1(Attack2);
      Air.key3(Skills);
      Skill_2.key1(Auto2_Enhanced);
      Skill_2.key3(Skills);
      Auto1.key1(Attack2);
      Auto1.key3(Skills);
      Auto2.key1(Attack3);
      Auto2.key3(Skills);
      Auto2_Enhanced.key1(Attack3);
      Auto2_Enhanced.key3(Skills);
      Auto3.key1(Attack4);
      Auto3.key3(Skills);
      Auto4.key1(BasicAttack);
      Auto4.key3(Skills);
      pioneer = registryWorker.build(
         "pioneer", PioneerInnate::new, ComboBasicAttack.createComboBasicAttack(PioneerInnate::new).setResetTime(7).setMaxPressTime(1).setCombo(root).setShouldDrawGui(true)
      );
   }
}
