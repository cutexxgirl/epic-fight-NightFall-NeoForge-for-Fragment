package com.hm.efn.gameasset.combos;

import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.skill.weapon_innate.ExampleInnate;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.api.combo.ComboNode.ComboTypes;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.conditions.SprintingCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class ExampleCombo {
   public static Skill exampleCombo;

   @SubscribeEvent
   public static void onSkillBuild(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode auto1 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO1);
      ComboNode auto2 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO2);
      ComboNode auto3 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO3);
      ComboNode auto4 = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AUTO4);
      ComboNode dash = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_DASH).setPriority(1).addCondition(new SprintingCondition());
      ComboNode airslash = EFNComboNodes.create(() -> EFNSwordAnimations.NF_SWORD_AIRSLASH).setPriority(2).addCondition(new JumpCondition());
      ComboNode skill = EFNComboNodes.create(() -> Animations.SWEEPING_EDGE);
      ComboNode basicAttack = ComboNode.create().addConditionNode(auto1).addConditionNode(dash).addConditionNode(airslash);
      ComboNode attack2 = ComboNode.create().addConditionNode(auto2).addConditionNode(dash).addConditionNode(airslash);
      ComboNode attack3 = ComboNode.create().addConditionNode(auto3).addConditionNode(dash).addConditionNode(airslash);
      ComboNode attack4 = ComboNode.create().addConditionNode(auto4).addConditionNode(dash).addConditionNode(airslash);
      root.key1(basicAttack);
      skill.key1(basicAttack);
      skill.key3(skill);
      auto1.key1(attack2);
      auto2.key1(attack3);
      auto3.key1(attack4);
      auto4.key1(basicAttack);
      airslash.key1(basicAttack);
      dash.key1(basicAttack);
      root.addChildToSubtree(ComboTypes.KEY_3, skill);
      exampleCombo = registryWorker.build("examplecombo", ExampleInnate::new, ComboBasicAttack.createComboBasicAttack(ExampleInnate::new).setCombo(root));
   }
}
