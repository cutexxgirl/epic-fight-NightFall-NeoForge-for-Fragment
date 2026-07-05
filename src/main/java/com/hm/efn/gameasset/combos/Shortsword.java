package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.HitEvents;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNShortSwordAnimations;
import com.hm.efn.skill.weapon_innate.ShortSwordInnate;
import com.hm.efn.skill.weapon_passive.ShortswordPassive;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Shortsword {
   public static Skill shortsword;
   public static Skill shortsword_passive;
   public static ComboNode DodgeCounter;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_DASH)
         .setPriority(5)
         .addCondition(new EFNSprintingCondition())
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.65F));
      ComboNode Air = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_AIRSLASH)
         .setPriority(5)
         .addCondition(new JumpCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 3.0F));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_AUTO1)
         .setPriority(1)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.35F));
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_AUTO2).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.25F));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_AUTO3).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.35F));
      ComboNode Auto4 = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.55F));
      ComboNode Auto5 = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.35F));
      ComboNode Auto6 = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_AUTO6)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.8F))
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()));
      DodgeCounter = ComboNode.create();
      ComboNode DodgeCounter_1 = EFNComboNodes.create(() -> EFNShortSwordAnimations.NF_SHORTSWORD_AUTO3).setPriority(5);
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto2);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto3);
      ComboNode Attack4 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto4);
      ComboNode Attack5 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto5);
      ComboNode Attack6 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto6);
      root.key1(BasicAttack);
      Dash.key1(Attack3);
      Air.key1(BasicAttack);
      Auto1.key1(Attack2);
      Auto2.key1(Attack3);
      Auto3.key1(Attack4);
      Auto4.key1(Attack5);
      Auto5.key1(Attack6);
      Auto6.key1(BasicAttack);
      DodgeCounter.key1(DodgeCounter_1);
      DodgeCounter_1.key1(Attack4);
      shortsword = registryWorker.build(
         "shortsword",
         ShortSwordInnate::new,
         ComboBasicAttack.createComboBasicAttack(ShortSwordInnate::new).setResetTime(9).setMaxPressTime(1).setCombo(root).setShouldDrawGui(true)
      );
      shortsword_passive = registryWorker.build(
         "shortsword_passive",
         ShortswordPassive::new,
         PassiveSkill.createPassiveBuilder(builder -> new ShortswordPassive(builder)).setCategory(SkillCategories.WEAPON_PASSIVE).setResource(Resource.NONE)
      );
   }
}
