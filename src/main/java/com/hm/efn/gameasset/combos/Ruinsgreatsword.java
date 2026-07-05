package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStackCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.comboevents.condition.weapons.RuinGreatswordIsChargingCondition;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.skill.weapon_innate.RuinsGreatSwordInnate;
import com.hm.efn.skill.weapon_passive.RuinGreatSwordPassive;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Ruinsgreatsword {
   public static ComboNode RuinExtendRoot_1_GP;
   public static ComboNode RuinExtendRoot_1_Clash;
   public static ComboNode RuinExtendRoot_2_ChargeMin;
   public static ComboNode RuinExtendRoot_3_ChargeMax;
   public static Skill Ruinsgreatsword;
   public static Skill Ruinsgreatsword_passive;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNGreatSwordAnimations.NG_GREATSWORD_DASH)
         .addCondition(new RuinGreatswordIsChargingCondition(false))
         .setPriority(5)
         .addCondition(new EFNSprintingCondition());
      ComboNode Air_Normal = EFNComboNodes.create(() -> EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW)
         .addCondition(new RuinGreatswordIsChargingCondition(false))
         .addCondition(new JumpCondition())
         .addCondition(new EFNStaminaCondition(5.0F, true))
         .setDamageMultiplier(ValueModifier.multiplier(1.7F))
         .setPriority(5)
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 5.0F));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNGreatSwordAnimations.NG_GREATSWORD_AUTO1)
         .addCondition(new RuinGreatswordIsChargingCondition(false))
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.95F))
         .setPriority(1);
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNGreatSwordAnimations.NG_GREATSWORD_AUTO2)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.98F))
         .addCondition(new RuinGreatswordIsChargingCondition(false));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNGreatSwordAnimations.NG_GREATSWORD_AUTO3)
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.25F))
         .addCondition(new RuinGreatswordIsChargingCondition(false));
      ComboNode Skill = EFNComboNodes.create(() -> EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH)
         .addCondition(new RuinGreatswordIsChargingCondition(false))
         .setPriority(1)
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .addCondition(new EFNStackCondition(1, 2))
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .addTimeEvent(TimeEvents.timeConsumeStack(0.2F, 1));
      RuinExtendRoot_1_GP = EFNComboNodes.create(() -> Animations.BIPED_IDLE);
      RuinExtendRoot_1_Clash = EFNComboNodes.create(() -> Animations.BIPED_IDLE);
      RuinExtendRoot_2_ChargeMin = EFNComboNodes.create(() -> Animations.BIPED_IDLE);
      RuinExtendRoot_3_ChargeMax = EFNComboNodes.create(() -> Animations.BIPED_IDLE);
      ComboNode GP_Strike = EFNComboNodes.create(() -> EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_SECOND)
         .addCondition(new EFNStackCondition(1, 2))
         .setPriority(5)
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.timeConsumeStack(0.2F, 1));
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air_Normal).addConditionNode(Auto1);
      ComboNode BasicAttack_1 = ComboNode.create().addConditionNode(Dash).addConditionNode(GP_Strike).addConditionNode(Auto1);
      ComboNode BasicAttack_2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Auto1);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air_Normal).addConditionNode(Auto2);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air_Normal).addConditionNode(Auto3);
      ComboNode Skills = ComboNode.create().addConditionNode(Skill);
      root.key1(BasicAttack);
      root.key3(Skills);
      Dash.key1(BasicAttack);
      Air_Normal.key1(BasicAttack_2);
      Skill.key1(BasicAttack);
      Skill.key3(Skills);
      RuinExtendRoot_1_GP.key1(BasicAttack_1);
      RuinExtendRoot_1_GP.key3(Skills);
      RuinExtendRoot_1_Clash.key1(BasicAttack_2);
      RuinExtendRoot_1_Clash.key3(Skills);
      RuinExtendRoot_2_ChargeMin.key1(Attack2);
      RuinExtendRoot_2_ChargeMin.key3(Skills);
      RuinExtendRoot_3_ChargeMax.key1(BasicAttack_1);
      RuinExtendRoot_3_ChargeMax.key3(Skills);
      GP_Strike.key1(BasicAttack_2);
      GP_Strike.key3(Skills);
      Auto1.key1(Attack2);
      Auto1.key3(Skills);
      Auto2.key1(Attack3);
      Auto2.key3(Skills);
      Auto3.key1(BasicAttack);
      Auto3.key3(Skills);
      Ruinsgreatsword = registryWorker.build(
         "ruinsgreatsword",
         RuinsGreatSwordInnate::new,
         ComboBasicAttack.createComboBasicAttack(RuinsGreatSwordInnate::new).setCombo(root).setResetTime(7).setMaxPressTime(1).setShouldDrawGui(true)
      );
      Ruinsgreatsword_passive = registryWorker.build(
         "ruinsgreatsword_passive",
         RuinGreatSwordPassive::new,
         PassiveSkill.createPassiveBuilder(builder -> new RuinGreatSwordPassive(builder)).setCategory(SkillCategories.WEAPON_PASSIVE).setResource(Resource.NONE)
      );
   }
}
