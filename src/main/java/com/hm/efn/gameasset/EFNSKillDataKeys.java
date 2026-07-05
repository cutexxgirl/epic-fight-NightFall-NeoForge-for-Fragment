package com.hm.efn.gameasset;

import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.skill.arts.ExecuteSkill;
import com.hm.efn.skill.arts.JudgmentCutEndSkill;
import com.hm.efn.skill.arts.StompSkill;
import com.hm.efn.skill.arts.ZansetsuSkill;
import com.hm.efn.skill.dodge.MurasamaDodge;
import com.hm.efn.skill.guard.EFNParryingSkill;
import com.hm.efn.skill.passive.ParryMasterPassive;
import com.hm.efn.skill.passive.PreciseParryPassive;
import com.hm.efn.skill.sekiro.EFNSekiroArtSkill;
import com.hm.efn.skill.weapon_innate.HfBladeInnate;
import com.hm.efn.skill.weapon_innate.MurasamaInnate;
import com.hm.efn.skill.weapon_innate.ScytheSkill;
import com.hm.efn.skill.weapon_innate.ShortSwordInnate;
import com.hm.efn.skill.weapon_innate.ThornWheelSkill;
import com.hm.efn.skill.weapon_innate.YamatoInnate;
import com.hm.efn.skill.weapon_passive.HfBladePassive;
import com.hm.efn.skill.weapon_passive.MurasamaPassive;
import com.hm.efn.skill.weapon_passive.YamatoPassive;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.hm.efn.compat.epicfight.utils.PacketBufferCodec;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.SkillDataKey;

public class EFNSKillDataKeys {
   public static final DeferredRegister<SkillDataKey<?>> DATA_KEYS = DeferredRegister.create(EpicFightRegistries.SKILL_DATA_KEY, "efn");
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> IS_PRESSING = DATA_KEYS.register(
      "is_pressing", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> IS_CHARGING = DATA_KEYS.register(
      "is_charging", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> CHARGE_TICKS = DATA_KEYS.register(
      "charge_ticks", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> CHARGE_STAGE = DATA_KEYS.register(
      "charge_stage", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> CHARGE_POWER = DATA_KEYS.register(
      "charge_power", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> CHARGE_MESSAGE = DATA_KEYS.register(
      "charge_message", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> FULL_CHARGE_MESSAGE = DATA_KEYS.register(
      "full_charge_message", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> ENERGY_CHECK = DATA_KEYS.register(
      "energy_check", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> FORCE_RELEASE = DATA_KEYS.register(
      "force_release", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> DEFENSE_START_TICK = DATA_KEYS.register(
      "defense_start_tick", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{PreciseParryPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> BUFF_END_TICK = DATA_KEYS.register(
      "buff_end_tick", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{PreciseParryPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> BUFF_ACTIVE = DATA_KEYS.register(
      "buff_active", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{PreciseParryPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> CURRENT_PUNCTURE_CHANCE = DATA_KEYS.register(
      "current_puncture_chance", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, true, new Class[]{ParryMasterPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> BASE_PUNCTURE_CHANCE = DATA_KEYS.register(
      "base_puncture_chance", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, false, new Class[]{ParryMasterPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> PUNCTURE_INCREMENT = DATA_KEYS.register(
      "puncture_increment", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, false, new Class[]{ParryMasterPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> MAX_PUNCTURE_CHANCE = DATA_KEYS.register(
      "max_puncture_chance", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, false, new Class[]{ParryMasterPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> COMBO_COUNTER = DATA_KEYS.register(
      "combo_counter", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> SEKIRO_ART_INDEX = DATA_KEYS.register(
      "sekiro_art_index", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNSekiroArtSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> ARTS_INPUT_BUFFER = DATA_KEYS.register(
      "arts_input_buffer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNSekiroArtSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> ANGEL_KEY = DATA_KEYS.register(
      "angel_key", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> DEMON_KEY = DATA_KEYS.register(
      "demon_key", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> GUARD_KEY = DATA_KEYS.register(
      "guard_key", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> SPRINT_KEY = DATA_KEYS.register(
      "sprint_key", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> UP_KEY = DATA_KEYS.register(
      "up_key", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> DOWN_KEY = DATA_KEYS.register(
      "down_key", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> JUMP_KEY = DATA_KEYS.register(
      "jump_key", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> SNEAK_KEY = DATA_KEYS.register(
      "sneak_key", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> ARTS_KEY = DATA_KEYS.register(
      "arts_key",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class, ZansetsuSkill.class, EFNSekiroArtSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> ARTS_KEY_PRESSED = DATA_KEYS.register(
      "arts_key_pressed",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class, ZansetsuSkill.class, EFNSekiroArtSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> UP_KEY_PRESSED_LAST_TICK = DATA_KEYS.register(
      "up_key_pressed_last_tick",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> IS_DOUBLE_TAP_SPRINT = DATA_KEYS.register(
      "is_double_tap_sprint",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> DOUBLE_TAP_TIMER = DATA_KEYS.register(
      "double_tap_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> WAS_DOUBLE_TAP_SPRINT = DATA_KEYS.register(
      "was_double_tap_sprint",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> JUMP_KEY_PRESSED_LAST_TICK = DATA_KEYS.register(
      "jump_key_pressed_last_tick",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaDodge.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> JUMP_COUNT = DATA_KEYS.register(
      "jump_count",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaDodge.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> BACKWARD_FORWARD_SUCCESS = DATA_KEYS.register(
      "forward_backward_success",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> BACKWARD_FORWARD_WINDOW_ACTIVE = DATA_KEYS.register(
      "forward_backward_window_active",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> BACKWARD_PRESSED_LAST_TICK = DATA_KEYS.register(
      "forward_pressed_last_tick",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> FORWARD_PRESSED_LAST_TICK = DATA_KEYS.register(
      "backward_pressed_last_tick",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> BACKWARD_FORWARD_TIMER = DATA_KEYS.register(
      "forward_backward_timer",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> BACKWARD_FORWARD_END_TICK = DATA_KEYS.register(
      "forward_backward_end_tick",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> BACKWARD_FORWARD_REMAINING_TICKS = DATA_KEYS.register(
      "forward_backward_remaining_ticks",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{MurasamaPassive.class, HfBladePassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> COOLDOWN = DATA_KEYS.register(
      "cooldown",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         true,
         new Class[]{EFNWeaponInnateBase.class, JudgmentCutEndSkill.class, ExecuteSkill.class, StompSkill.class, EFNSekiroArtSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> COOLDOWN_1 = DATA_KEYS.register(
      "cooldown_1",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         true,
         new Class[]{EFNWeaponInnateBase.class, JudgmentCutEndSkill.class, ExecuteSkill.class, StompSkill.class, EFNSekiroArtSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> COOLDOWN_2 = DATA_KEYS.register(
      "cooldown_2",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         true,
         new Class[]{EFNWeaponInnateBase.class, JudgmentCutEndSkill.class, ExecuteSkill.class, StompSkill.class, EFNSekiroArtSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_AIR_BORNE = DATA_KEYS.register(
      "murasama_air_borne",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaInnate.class, HfBladeInnate.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<List<EFNSKillDataKeys.DamageRecord>>> DAMAGE_HISTORY = DATA_KEYS.register(
      "damage_history", () -> SkillDataKey.createSkillDataKey(new PacketBufferCodec<List<EFNSKillDataKeys.DamageRecord>>() {
         public List<EFNSKillDataKeys.DamageRecord> decode(FriendlyByteBuf buf) {
            int size = buf.readInt();
            List<EFNSKillDataKeys.DamageRecord> list = new ArrayList<>();

            for (int i = 0; i < size; i++) {
               list.add(new EFNSKillDataKeys.DamageRecord(buf.readFloat(), buf.readInt()));
            }

            return list;
         }

         public void encode(List<EFNSKillDataKeys.DamageRecord> records, FriendlyByteBuf buf) {
            buf.writeInt(records.size());

            for (EFNSKillDataKeys.DamageRecord record : records) {
               buf.writeFloat(record.damage);
               buf.writeInt(record.timestamp);
            }
         }
      }, new ArrayList(), false, new Class[]{ShortSwordInnate.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> IS_REPLAY_DAMAGE = DATA_KEYS.register(
      "is_replay_damage", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{ShortSwordInnate.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> REPLAY_ENHANCED = DATA_KEYS.register(
      "replay_enhanced", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{ShortSwordInnate.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> HAVE_DOPPELGANGER = DATA_KEYS.register(
      "have_doppelganger", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{YamatoInnate.class, YamatoPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> DOPPELGANGER_DELAY = DATA_KEYS.register(
      "doppelganger_delay", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{YamatoInnate.class, YamatoPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> DOPPELGANGER_STYLE = DATA_KEYS.register(
      "doppelganger_style", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{YamatoInnate.class, YamatoPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> DOPPELGANGER_TICK = DATA_KEYS.register(
      "doppelganger_tick", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{YamatoInnate.class, YamatoPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> DOPPELGANGER = DATA_KEYS.register(
      "press_doppelganger", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{YamatoInnate.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> SUMMON_SWORD = DATA_KEYS.register(
      "press_summon_sword",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{YamatoInnate.class, EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> PARRY_COOLDOWN = DATA_KEYS.register(
      "parry_cooldown", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{YamatoPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> STUN_COOLDOWN = DATA_KEYS.register(
      "stun_cooldown", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{YamatoPassive.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_FORWARD_DODGE = DATA_KEYS.register(
      "murasama_forward_dodge",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, MurasamaDodge.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_KEY_LEFT_DOWN = DATA_KEYS.register(
      "murasama_key_left_down",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_KEY_RIGHT_DOWN = DATA_KEYS.register(
      "murasama_key_right_down",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_ATTACK_KEY_PRESSED = DATA_KEYS.register(
      "murasama_left_mouse_pressed",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_ZANSETSU_INPUT_MODE = DATA_KEYS.register(
      "murasama_zansetsu_input_mode",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         false,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_INPUT_MODE_SWITCH_PRESSED = DATA_KEYS.register(
      "murasama_input_mode_switch_pressed",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         false,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_ZANSETSU_ACTIVE = DATA_KEYS.register(
      "murasama_zansetsu_active",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_PAIR_FLAG = DATA_KEYS.register(
      "murasama_pair_flag",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_ZANSETSU_SLASH_RELEASED = DATA_KEYS.register(
      "murasama_zansetsu_slash_release",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_OPPOSITE_TRIGGERED = DATA_KEYS.register(
      "murasama_opposite_triggered",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_ZANDATSU_AVAILABLE = DATA_KEYS.register(
      "murasama_zandatsu_available",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN,
         false,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MANUAL_UNSHEATH = DATA_KEYS.register(
      "manual_unsheath",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> MURASAMA_SHEATH = DATA_KEYS.register(
      "murasama_sheath",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.BOOLEAN, false, true, new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_SHEATH_TIMER = DATA_KEYS.register(
      "murasama_sheath_timer",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER, 0, true, new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_ZANSETSU_TIMER = DATA_KEYS.register(
      "murasama_zansetsu_timer",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_ZANSETSU_SECTOR = DATA_KEYS.register(
      "murasama_zansetsu_sector",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_ZANSETSU_ATTACK_COUNTER = DATA_KEYS.register(
      "murasama_zansetsu_attack_counter",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_LAST_TRIGGERED_SECTOR = DATA_KEYS.register(
      "murasama_last_triggered_sector",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         true,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_ZANSETSU_START_TIME = DATA_KEYS.register(
      "murasama_zansetsu_start_time",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         false,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_ZANSETSU_END_TIME = DATA_KEYS.register(
      "murasama_zansetsu_end_time",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         false,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_ZANDATSU_START_TIME = DATA_KEYS.register(
      "murasama_zandatsu_start_time",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         false,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> MURASAMA_ZANDATSU_END_TIME = DATA_KEYS.register(
      "murasama_zandatsu_end_time",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER,
         0,
         false,
         new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class, ZansetsuSkill.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> SPRINT_SHEATH_TIMER = DATA_KEYS.register(
      "sprint_sheath_timer",
      () -> SkillDataKey.createSkillDataKey(
         PacketBufferCodec.INTEGER, 0, false, new Class[]{MurasamaPassive.class, HfBladePassive.class, MurasamaInnate.class, HfBladeInnate.class}
      )
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> SCYTHE_SHEATH = DATA_KEYS.register(
      "scythe_sheath", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, true, true, new Class[]{ScytheSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> SCYTHE_BLOOD_POWER = DATA_KEYS.register(
      "scythe_blood_power", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, true, new Class[]{ScytheSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> THORNWHEEL_LOOP_AVAILABLE = DATA_KEYS.register(
      "thornwheel_loop_available", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{ThornWheelSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> THORNWHEEL_LOOPING = DATA_KEYS.register(
      "thornwheel_loop", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{ThornWheelSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> EFN_LAST_ACTIVE = DATA_KEYS.register(
      "efn_last_active", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> EFN_STARTING_PARRY = DATA_KEYS.register(
      "efn_starting_parry", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> EFN_LAST_PARRY_TIME = DATA_KEYS.register(
      "efn_last_parry_time", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> EFN_DELAY_PROTECTION_END = DATA_KEYS.register(
      "efn_delay_protection_end", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> EFN_PARRY_MOTION_COUNTER = DATA_KEYS.register(
      "efn_parry_motion_counter", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> EFN_SHAKE_PENALTY_COUNT = DATA_KEYS.register(
      "efn_shake_penalty_count", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> EFN_LAST_GUARD_START_TICK = DATA_KEYS.register(
      "efn_last_guard_start_tick", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> EFN_LAST_SUCCESSFUL_DEFENSE_TICK = DATA_KEYS.register(
      "efn_last_successful_defense_tick", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> EFN_SHAKE_DETECTED = DATA_KEYS.register(
      "efn_shake_detected", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> EFN_FIRST_SHAKE = DATA_KEYS.register(
      "efn_first_shake", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, true, false, new Class[]{EFNParryingSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> KEY1_PRESS = DATA_KEYS.register(
      "press_key1", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> KEY2_PRESS = DATA_KEYS.register(
      "press_key2", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> KEY3_PRESS = DATA_KEYS.register(
      "press_key3", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> KEY4_PRESS = DATA_KEYS.register(
      "press_key4", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> INNATE_PRESS = DATA_KEYS.register(
      "press_innate",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class, ThornWheelSkill.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> KEY1_LONG_PRESS = DATA_KEYS.register(
      "pressing_key1", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> KEY2_LONG_PRESS = DATA_KEYS.register(
      "pressing_key2", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> KEY3_LONG_PRESS = DATA_KEYS.register(
      "pressing_key3", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> KEY4_LONG_PRESS = DATA_KEYS.register(
      "pressing_key4", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> INNATE_LONG_PRESS = DATA_KEYS.register(
      "pressing_innate", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> KEY1_PRESS_TIMER = DATA_KEYS.register(
      "key1_press_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> KEY2_PRESS_TIMER = DATA_KEYS.register(
      "key2_press_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> KEY3_PRESS_TIMER = DATA_KEYS.register(
      "key3_press_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> KEY4_PRESS_TIMER = DATA_KEYS.register(
      "key4_press_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> INNATE_PRESS_TIMER = DATA_KEYS.register(
      "innate_press_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{EFNWeaponInnateBase.class})
   );
   public static final DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> WAS_LONG_PRESSED = DATA_KEYS.register(
      "was_long_pressed", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{EFNWeaponInnateBase.class})
   );

   public static DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> getKeyPress(int keyNum) {
      return switch (keyNum) {
         case 1 -> KEY1_PRESS;
         case 2 -> KEY2_PRESS;
         case 3 -> KEY3_PRESS;
         case 4 -> KEY4_PRESS;
         case 5 -> INNATE_PRESS;
         default -> throw new IllegalArgumentException("Invalid key number: " + keyNum);
      };
   }

   public static DeferredHolder<SkillDataKey<?>, SkillDataKey<Boolean>> getKeyLongPress(int keyNum) {
      return switch (keyNum) {
         case 1 -> KEY1_LONG_PRESS;
         case 2 -> KEY2_LONG_PRESS;
         case 3 -> KEY3_LONG_PRESS;
         case 4 -> KEY4_LONG_PRESS;
         case 5 -> INNATE_LONG_PRESS;
         default -> throw new IllegalArgumentException("Invalid key number: " + keyNum);
      };
   }

   public static DeferredHolder<SkillDataKey<?>, SkillDataKey<Integer>> getKeyPressTimer(int keyNum) {
      return switch (keyNum) {
         case 1 -> KEY1_PRESS_TIMER;
         case 2 -> KEY2_PRESS_TIMER;
         case 3 -> KEY3_PRESS_TIMER;
         case 4 -> KEY4_PRESS_TIMER;
         case 5 -> INNATE_PRESS_TIMER;
         default -> throw new IllegalArgumentException("Invalid key number: " + keyNum);
      };
   }

   public record DamageRecord(float damage, int timestamp) {
      public boolean isExpired(int currentTick) {
         return currentTick - this.timestamp > 70;
      }
   }
}
