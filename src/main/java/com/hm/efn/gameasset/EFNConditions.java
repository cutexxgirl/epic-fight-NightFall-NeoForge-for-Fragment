package com.hm.efn.gameasset;

import com.hm.efn.comboevents.condition.input.EFNAngelKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNDemonKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNDownKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNJumpKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNKeyLongPressCondition;
import com.hm.efn.comboevents.condition.input.EFNSneakKeyCondition;
import com.hm.efn.comboevents.condition.input.EFNUpKeyCondition;
import com.hm.efn.comboevents.condition.state.EFNAirborneCondition;
import com.hm.efn.comboevents.condition.state.EFNComboCounterCondition;
import com.hm.efn.comboevents.condition.state.EFNOnGroundCondition;
import com.hm.efn.comboevents.condition.state.EFNSprintingCondition;
import com.hm.efn.comboevents.condition.state.EFNStackCondition;
import com.hm.efn.comboevents.condition.state.EFNStaminaCondition;
import com.hm.efn.comboevents.condition.weapons.BackwardForwardCondition;
import com.hm.efn.comboevents.condition.weapons.DoubleTapSprintCondition;
import com.hm.efn.comboevents.condition.weapons.MeenLanceIsChargingCondition;
import com.hm.efn.comboevents.condition.weapons.RuinGreatswordIsChargingCondition;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.data.conditions.Condition;

public class EFNConditions {
   public static final DeferredRegister<Supplier<Condition<?>>> CONDITIONS = DeferredRegister.create(
      ResourceLocation.fromNamespaceAndPath("epicfight", "conditions"), "efn"
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_STAMINA_CONDITION = CONDITIONS.register(
      "efn_stamina_condition", () -> () -> new EFNStaminaCondition(0.0F, true)
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_STACK_CONDITION = CONDITIONS.register("efn_stack_condition", () -> EFNStackCondition::new);
   public static final Supplier<Supplier<Condition<?>>> EFN_AIRBORNE_CONDITION = CONDITIONS.register(
      "efn_airborne_condition", () -> EFNAirborneCondition::new
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_ONGROUND_CONDITION = CONDITIONS.register(
      "efn_onground_condition", () -> EFNOnGroundCondition::new
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_DOWNKEY_CONDITION = CONDITIONS.register(
      "efn_downkey_condition", () -> EFNDownKeyCondition::new
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_DOUBLETAP_SPRINT_CONDITION = CONDITIONS.register(
      "efn_doubletap_sprint_condition", () -> DoubleTapSprintCondition::new
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_FORWARDBACKWARD_CONDITION = CONDITIONS.register(
      "efn_forwardbackward_condition", () -> BackwardForwardCondition::new
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_UPKEY_CONDITION = CONDITIONS.register("efn_upkey_condition", () -> EFNUpKeyCondition::new);
   public static final Supplier<Supplier<Condition<?>>> EFN_SNEAK_CONDITION = CONDITIONS.register("efn_sneak_condition", () -> EFNSneakKeyCondition::new);
   public static final Supplier<Supplier<Condition<?>>> EFN_JUMP_CONDITION = CONDITIONS.register("efn_jump_condition", () -> EFNJumpKeyCondition::new);
   public static final Supplier<Supplier<Condition<?>>> EFN_SPRINTING_CONDITION = CONDITIONS.register(
      "efn_sprinting_condition", () -> EFNSprintingCondition::new
   );
   public static final Supplier<Supplier<Condition<?>>> YAMATO_ANGELKEY_CONDITION = CONDITIONS.register(
      "yamato_angelkey_condition", () -> EFNAngelKeyCondition::new
   );
   public static final Supplier<Supplier<Condition<?>>> YAMATO_DEMONKEY_CONDITION = CONDITIONS.register(
      "yamato_demonkey_condition", () -> EFNDemonKeyCondition::new
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_KEYLONGPRESS_CONDITION = CONDITIONS.register(
      "efn_keylongpress_condition", () -> () -> new EFNKeyLongPressCondition(EFNKeyLongPressCondition.TargetKey.INNATE)
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_RUINGREATSWORD_CHARGING_CONDITION = CONDITIONS.register(
      "efn_ruingreatsword_charging_condition", () -> () -> new RuinGreatswordIsChargingCondition(true)
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_MEENLANCE_CHARGING_CONDITION = CONDITIONS.register(
      "efn_meenlance_charging_condition", () -> () -> new MeenLanceIsChargingCondition(true)
   );
   public static final Supplier<Supplier<Condition<?>>> EFN_COMBOCOUNTER_CONDITION = CONDITIONS.register(
      "efn_combocounter_condition", () -> () -> new EFNComboCounterCondition(0.0F, 100.0F)
   );
}
