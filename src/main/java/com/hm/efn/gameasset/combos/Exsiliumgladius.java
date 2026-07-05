package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNExsiliumgladiusAnimations;
import com.hm.efn.skill.weapon_innate.ExsiliumgladiusInnate;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.Skill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Exsiliumgladius {
   public static Skill exsiliumgladius;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_DASH)
         .setPriority(5)
         .addCondition(new EFNSprintingCondition())
         .setDamageMultiplier(ValueModifier.multiplier(0.8F));
      ComboNode Air = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AIRSLASH)
         .setPriority(5)
         .addCondition(new JumpCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 3.0F))
         .setDamageMultiplier(ValueModifier.multiplier(0.8F));
      ComboNode A = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_A).setPriority(2).setDamageMultiplier(ValueModifier.multiplier(0.7F));
      ComboNode AA = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AA).setDamageMultiplier(ValueModifier.multiplier(0.7F));
      ComboNode AAA = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AAA).setDamageMultiplier(ValueModifier.multiplier(0.8F));
      ComboNode AAAA = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AAAA).setDamageMultiplier(ValueModifier.multiplier(1.0F));
      ComboNode AB = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AB).setDamageMultiplier(ValueModifier.multiplier(0.8F));
      ComboNode ABA = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABA).setDamageMultiplier(ValueModifier.multiplier(0.9F));
      ComboNode ABAA = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABAA).setDamageMultiplier(ValueModifier.multiplier(1.2F));
      ComboNode ABAB = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABAB).setDamageMultiplier(ValueModifier.multiplier(1.5F));
      ComboNode ABB = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABB).setDamageMultiplier(ValueModifier.multiplier(1.1F));
      ComboNode ABBA = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABBA).setDamageMultiplier(ValueModifier.multiplier(1.3F));
      ComboNode ABBB = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABBB).setDamageMultiplier(ValueModifier.multiplier(2.0F));
      ComboNode AAB = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AAB).setDamageMultiplier(ValueModifier.multiplier(1.0F));
      ComboNode AABA = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AABA).setDamageMultiplier(ValueModifier.multiplier(1.3F));
      ComboNode AABB = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AABB).setDamageMultiplier(ValueModifier.multiplier(2.0F));
      ComboNode AAAB = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AAAB).setDamageMultiplier(ValueModifier.multiplier(1.5F));
      ComboNode D = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_D).setDamageMultiplier(ValueModifier.multiplier(0.8F));
      ComboNode DD = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_DD).setDamageMultiplier(ValueModifier.multiplier(0.8F));
      ComboNode DDD = EFNComboNodes.create(() -> EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_DDD).setDamageMultiplier(ValueModifier.multiplier(0.8F));
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(A);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(AA);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(AAA);
      ComboNode Attack4 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(AAAA);
      root.key1(BasicAttack);
      Dash.key1(Attack2);
      Air.key1(Attack2);
      root.key3(D);
      ABAA.key1(BasicAttack);
      ABAB.key1(BasicAttack);
      ABBA.key1(BasicAttack);
      ABBB.key1(BasicAttack);
      AAB.key1(BasicAttack);
      AAAB.key1(BasicAttack);
      AABA.key1(BasicAttack);
      AABB.key1(BasicAttack);
      A.key1(Attack2);
      A.key3(AB);
      A.key1_3(AB);
      AB.key1(ABA);
      AB.key3(ABB);
      AB.key1_3(ABB);
      ABA.key1(ABAA);
      ABA.key3(ABAB);
      ABA.key1_3(ABAB);
      AA.key1(Attack3);
      AA.key3(AAB);
      AA.key1_3(AAB);
      AAB.key1(AABA);
      AAB.key3(AABB);
      AAB.key1_3(AABB);
      AAA.key1(Attack4);
      AAA.key3(AAAB);
      AAA.key1_3(AAAB);
      ABB.key1(ABBA);
      ABB.key3(ABBB);
      ABB.key1_3(ABBB);
      AAAA.key1(BasicAttack);
      D.key3(DD);
      D.key1(BasicAttack);
      DD.key3(DDD);
      DD.key1(BasicAttack);
      DDD.key1(BasicAttack);
      DDD.key3(DD);
      exsiliumgladius = registryWorker.build(
         "exsiliumgladius",
         ExsiliumgladiusInnate::new,
         ComboBasicAttack.createComboBasicAttack(ExsiliumgladiusInnate::new).setResetTime(7).setMaxPressTime(1).setCombo(root).setShouldDrawGui(true)
      );
   }
}
