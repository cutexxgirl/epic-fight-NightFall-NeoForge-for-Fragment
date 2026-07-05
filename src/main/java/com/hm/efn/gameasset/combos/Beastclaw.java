package com.hm.efn.gameasset.combos;

import com.hm.efn.comboevents.HitEvents;
import com.hm.efn.comboevents.TimeEvents;
import com.hm.efn.comboevents.condition.state.EFNAirborneCondition;
import com.hm.efn.comboevents.condition.state.EFNStackCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.gameasset.animations.EFNClawAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.BeastclawInnate;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.conditions.SprintingCondition;
import com.p1nero.invincible.skill.ComboBasicAttack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.skill.Skill;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class Beastclaw {
   public static Skill beastclaw;
   public static ComboNode DodgeCounter;

   @SubscribeEvent
   public static void BuildSkills(SkillBuildEvent event) {
      ModRegistryWorker registryWorker = event.createRegistryWorker("efn");
      ComboNode root = ComboNode.create();
      ComboNode Dash = EFNComboNodes.create(() -> EFNClawAnimations.NF_CLAW_DASH).setPriority(5).addCondition(new SprintingCondition());
      ComboNode Air = EFNComboNodes.create(() -> EFNClawAnimations.NF_CLAW_AIRSLASH)
         .setPriority(6)
         .addCondition(new EFNAirborneCondition(false, 0.0F))
         .addCondition(new EFNStaminaCondition(1.0F, true))
         .addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.95F))
         .addTimeEvent(TimeEvents.timeConsumeStamina(0.2F, 1.0F));
      ComboNode Auto1 = EFNComboNodes.create(() -> EFNClawAnimations.NF_CLAW_AUTO1).setPriority(1).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(0.7F));
      ComboNode Auto2 = EFNComboNodes.create(() -> EFNClawAnimations.NF_CLAW_AUTO2).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.13F));
      ComboNode Auto3 = EFNComboNodes.create(() -> EFNClawAnimations.NF_CLAW_AUTO3).addTimeEvent(TimeEvents.TimeSimulationAttackEvent(1.13F));
      ComboNode Skill = EFNComboNodes.create(() -> EFNClawAnimations.NF_CLAW_BEASTROAR)
         .addCondition(new EFNStackCondition(1, Integer.MAX_VALUE))
         .addTimeEvent(TimeEvents.timeConsumeStack(0.2F, 1))
         .setNotCharge(true)
         .setCanBeInterrupt(false)
         .addTimeEvent(
            new TimeStampedEvent(
               0.1F,
               livingEntityPatch -> ((Player)livingEntityPatch.getOriginal())
                  .addEffect(new MobEffectInstance(EFNMobEffectRegistry.CLAW, 600, 0))
            )
         )
         .addTimeEvent(
            new TimeStampedEvent(
               0.1F,
               livingEntityPatch -> ((Player)livingEntityPatch.getOriginal())
                  .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 200, 0))
            )
         )
         .addHitEvent(HitEvents.createLostHealthDamage(0.1F));
      ComboNode DodgeCounter_1 = EFNComboNodes.create(() -> EFNClawAnimations.NF_CLAW_DASH).setPriority(5).setConvertTime(-0.1F);
      DodgeCounter = ComboNode.create();
      ComboNode BasicAttack = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto1);
      ComboNode Attack2 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto2);
      ComboNode Attack3 = ComboNode.create().addConditionNode(Dash).addConditionNode(Air).addConditionNode(Auto3);
      ComboNode Skills = ComboNode.create().addConditionNode(Skill);
      root.key1(BasicAttack);
      root.key3(Skills);
      Dash.key1(BasicAttack);
      Dash.key3(Skills);
      Air.key1(Attack2);
      Air.key3(Skills);
      Skill.key1(BasicAttack);
      Skill.key3(Skills);
      DodgeCounter.key1(DodgeCounter_1);
      DodgeCounter.key3(Skills);
      DodgeCounter_1.key1(BasicAttack);
      DodgeCounter_1.key3(Skills);
      Auto1.key1(Attack2);
      Auto1.key3(Skills);
      Auto2.key1(Attack3);
      Auto2.key3(Skills);
      Auto3.key1(BasicAttack);
      Auto3.key3(Skills);
      beastclaw = registryWorker.build(
         "beastclaw", BeastclawInnate::new, ComboBasicAttack.createComboBasicAttack(BeastclawInnate::new).setResetTime(7).setMaxPressTime(1).setCombo(root).setShouldDrawGui(true)
      );
   }
}
