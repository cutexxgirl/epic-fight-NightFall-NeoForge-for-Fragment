package com.hm.efn.gameasset.combos;

import com.guhao.efn_enhance.gameassets.animations.EFN_EBroadBladeAnimations;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNTargetHealthCondition;
import com.hm.efn.gameasset.animations.EFNBroadBladeAnimations;
import com.hm.efn.skill.weapon_innate.BroadBladeInnate;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.conditions.ParrySuccessCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.skill.Skill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class BroadBlade {
   public static Skill broadblade;
   public static ComboNode root;

   @SubscribeEvent
   public static void onSkillBuild(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      root = ComboNode.create();
      ComboNode dashSlash = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_DASHSLASH).setPriority(1).addCondition(new EFNSprintingCondition());
      ComboNode airSlash = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AIRSLASH).setPriority(2).addCondition(new JumpCondition());
      ComboNode counter = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_COUNTER).setPriority(10).addCondition(new ParrySuccessCondition());
      ComboNode execute = null;
      if (ModList.get().isLoaded("efn_enhance")) {
         execute = EFNComboNodes.create(() -> EFN_EBroadBladeAnimations.BROADBLADE_EXECUTE).setPriority(100).addCondition(new EFNTargetHealthCondition());
      }

      ComboNode auto1 = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AUTO1);
      ComboNode auto2 = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AUTO2);
      ComboNode auto3 = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AUTO3);
      ComboNode auto4 = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AUTO4);
      ComboNode auto5 = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AUTO5);
      ComboNode auto6 = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AUTO6);
      ComboNode auto7 = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AUTO7);
      ComboNode auto8 = EFNComboNodes.create(() -> EFNBroadBladeAnimations.BROADBLADE_AUTO8);
      ComboNode basicAttack = ComboNode.create().addConditionNode(auto1).addConditionNode(dashSlash).addConditionNode(airSlash);
      ComboNode attack2 = ComboNode.create().addConditionNode(auto2).addConditionNode(dashSlash).addConditionNode(airSlash);
      ComboNode attack3 = ComboNode.create().addConditionNode(auto3).addConditionNode(dashSlash).addConditionNode(airSlash);
      ComboNode attack4 = ComboNode.create().addConditionNode(auto4).addConditionNode(dashSlash).addConditionNode(airSlash);
      ComboNode attack5 = ComboNode.create().addConditionNode(auto5).addConditionNode(dashSlash).addConditionNode(airSlash);
      ComboNode attack6 = ComboNode.create().addConditionNode(auto6).addConditionNode(dashSlash).addConditionNode(airSlash);
      ComboNode attack7 = ComboNode.create().addConditionNode(auto7).addConditionNode(dashSlash).addConditionNode(airSlash);
      ComboNode attack8 = ComboNode.create().addConditionNode(auto8).addConditionNode(dashSlash).addConditionNode(airSlash);
      ComboNode Skills = ComboNode.create().addConditionNode(counter);
      root.key1(basicAttack);
      root.key3(Skills);
      if (ModList.get().isLoaded("efn_enhance")) {
         assert execute != null;
         root.key4(execute);
      }

      counter.key1(attack3);
      counter.key3(Skills);
      dashSlash.key1(basicAttack);
      dashSlash.key3(Skills);
      airSlash.key1(basicAttack);
      airSlash.key3(Skills);
      auto1.key1(attack2);
      auto1.key3(Skills);
      auto2.key1(attack3);
      auto2.key3(Skills);
      auto3.key1(attack4);
      auto3.key3(Skills);
      auto4.key1(attack5);
      auto4.key3(Skills);
      auto5.key1(attack6);
      auto5.key3(Skills);
      auto6.key1(attack7);
      auto6.key3(Skills);
      auto7.key1(attack8);
      auto7.key3(Skills);
      auto8.key1(basicAttack);
      auto8.key3(Skills);
      if (ModList.get().isLoaded("efn_enhance")) {
         assert execute != null;
         execute.key1(basicAttack);
      }

      broadblade = registryWorker.build(
         "broadblade", BroadBladeInnate::new, ComboBasicAttack.createComboBasicAttack(BroadBladeInnate::new).setCombo(root).setMaxPressTime(1).setShouldDrawGui(false)
      );
   }
}
