package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.HitEvents;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStackCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.comboevents.condition.weapons.MeenLanceIsChargingCondition;
import com.hm.efn.gameasset.animations.EFNLanceAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.MeenLanceInnate;
import com.hm.efn.skill.weapon_passive.MeenLancePassive;
import com.p1nero.invincible.api.events.BaseEvent;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.JumpCondition;
import com.p1nero.invincible.conditions.MobEffectCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.registry.entries.EpicFightMobEffects;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Meenlance {
   public static Skill Meenlance;
   public static Skill Meenlance_passive;
   public static ComboNode MeenExtendRoot_1;
   public static ComboNode MeenExtendRoot_2;
   public static ComboNode MeenExtendRoot_3;
   public static ComboNode MeenExtendRoot_4;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode Root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_DASH)
         .setPriority(5)
         .setConvertTime(-0.3F)
         .addCondition(new EFNSprintingCondition())
         .addHitEvent(HitEvents.createLostHealthDamage_Meen(0.01F));
      ComboNode Air = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_AIRSLASH)
         .setPriority(5)
         .addCondition(new JumpCondition())
         .addCondition(new EFNStaminaCondition(3.0F, true))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.3F, 3.0F))
         .addHitEvent(HitEvents.createLostHealthDamage_Meen(0.01F));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_AUTO1)
         .setPriority(1)
         .addCondition(new MeenLanceIsChargingCondition(false))
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.85F))
         .addHitEvent(HitEvents.consumeStack(-1))
         .addHitEvent(HitEvents.createLostHealthDamage_Meen(0.01F));
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_AUTO2)
         .addCondition(new MeenLanceIsChargingCondition(false))
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.0F))
         .addHitEvent(HitEvents.consumeStack(-1))
         .addHitEvent(HitEvents.createLostHealthDamage_Meen(0.01F));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_AUTO3)
         .addCondition(new MeenLanceIsChargingCondition(false))
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.25F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 30, 1, 0.0F))
         .addHitEvent(HitEvents.createLostHealthDamage_Meen(0.01F));
      ComboNode Auto4 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_AUTO4)
         .addCondition(new MeenLanceIsChargingCondition(false))
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.4F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EpicFightMobEffects.STUN_IMMUNITY, 30, 1, 0.0F))
         .addHitEvent(HitEvents.createLostHealthDamage_Meen(0.01F));
      ComboNode Charge_Strike_1 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_AUTO2)
         .setArmorNegation(200.0F)
         .setStunTypeModifier(StunType.HOLD)
         .setCanBeInterrupt(false)
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addHitEvent(HitEvents.consumeConsumption(-12.5F))
         .addHitEvent(HitEvents.createLostHealthDamage(0.1F));
      ComboNode Charge_Strike_2 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_AUTO4)
         .setArmorNegation(120.0F)
         .setStunTypeModifier(StunType.HOLD)
         .setCanBeInterrupt(false)
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addHitEvent(HitEvents.consumeConsumption(-7.5F))
         .addHitEvent(HitEvents.createLostHealthDamage(0.1F));
      ComboNode Charge_Strike_3 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_AUTO2)
         .setArmorNegation(200.0F)
         .setDamageMultiplier(ValueModifier.multiplier(1.2F))
         .setStunTypeModifier(StunType.LONG)
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.applyEffectAtTime(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 40, 1, 0.0F))
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addHitEvent(HitEvents.createLostHealthDamage(0.3F));
      ComboNode Finisher = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_FINISHER)
         .addCondition(new MobEffectCondition(false, EFNMobEffectRegistry.MEEN_LANCE, 0, Integer.MAX_VALUE))
         .addCondition(new EFNStackCondition(10, Integer.MAX_VALUE))
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .addTimeEvent(TimeEvents.timeConsumeStack(0.1F, 10))
         .addTimeEvent(
            new TimeStampedEvent(
               0.2F, livingEntityPatch -> ((Player)livingEntityPatch.getOriginal()).removeEffect(EFNMobEffectRegistry.MEEN_LANCE)
            )
         )
         .addHitEvent(HitEvents.createHitSound(() -> EpicFightSounds.BLADE_RUSH_FINISHER.get()))
         .addHitEvent(HitEvents.createLostHealthDamage(0.5F))
         .addHitEvent(new BaseEvent((entityPatch, entity) -> {
            if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
               container.getSkill().setStackSynchronize(container, 0);
               container.getSkill().setConsumptionSynchronize(container, 1.0F);
            }
         }));
      MeenExtendRoot_1 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_IDLE);
      MeenExtendRoot_2 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_IDLE);
      MeenExtendRoot_3 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_IDLE);
      MeenExtendRoot_4 = EFNComboNodes.create(() -> EFNLanceAnimations.NF_MEEN_IDLE);
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto2);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto3);
      ComboNode Attack4 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto4);
      Root.key1(BasicAttack);
      Dash.key1(BasicAttack);
      Air.key1(BasicAttack);
      Root.key3(Finisher);
      Dash.key3(Finisher);
      Air.key3(Finisher);
      Auto1.key1(Attack2);
      Auto1.key3(Finisher);
      Auto2.key1(Attack3);
      Auto2.key3(Finisher);
      Auto3.key1(Attack4);
      Auto3.key3(Finisher);
      Auto4.key1(BasicAttack);
      Auto4.key3(Finisher);
      Finisher.key1(Charge_Strike_1);
      Finisher.key3(Finisher);
      Charge_Strike_1.key1(Auto3);
      Charge_Strike_1.key3(Finisher);
      Charge_Strike_2.key1(BasicAttack);
      Charge_Strike_1.key3(Finisher);
      Charge_Strike_3.key1(BasicAttack);
      Charge_Strike_1.key3(Finisher);
      MeenExtendRoot_1.key1(Charge_Strike_1);
      MeenExtendRoot_1.key3(Finisher);
      MeenExtendRoot_2.key1(Charge_Strike_2);
      MeenExtendRoot_2.key3(Finisher);
      MeenExtendRoot_3.key1(Charge_Strike_3);
      MeenExtendRoot_3.key3(Finisher);
      MeenExtendRoot_4.key1(Attack4);
      MeenExtendRoot_4.key3(Finisher);
      Meenlance = registryWorker.build(
         "meenlance", MeenLanceInnate::new, ComboBasicAttack.createComboBasicAttack(MeenLanceInnate::new).setMaxPressTime(1).setResetTime(6).setCombo(Root).setShouldDrawGui(true)
      );
      Meenlance_passive = registryWorker.build(
         "meenlance_passive", MeenLancePassive::new, PassiveSkill.createPassiveBuilder(builder -> new MeenLancePassive(builder)).setCategory(SkillCategories.WEAPON_PASSIVE).setResource(Resource.NONE)
      );
   }
}
