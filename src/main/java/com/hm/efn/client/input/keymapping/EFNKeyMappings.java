package com.hm.efn.client.input.keymapping;

import com.hm.efn.gameasset.EFNComboTypes;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.p1nero.invincible.client.InputManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class EFNKeyMappings {
   public static final KeyMapping DOPPELGANGER = new EFNKeyMapping("key.efn.doppelganger", 86, "key.efn.category");
   public static final KeyMapping DOPPELGANGER_DELAY = new EFNKeyMapping("key.efn.doppelganger_delay", 90, "key.efn.category");
   public static final KeyMapping ANGEL = new EFNKeyMapping("key.efn.angel", 341, "key.efn.category");
   public static final KeyMapping DEMON = new EFNKeyMapping("key.efn.demon", Type.MOUSE, 1, "key.efn.category");
   public static final KeyMapping EFN_ARTS = new EFNKeyMapping("key.efn.arts", 67, "key.efn.category");
   public static final KeyMapping SUMMONED_SWORD = new EFNKeyMapping("key.efn.summoned_sword", Type.MOUSE, 4, "key.efn.category");
   public static final KeyMapping ZANSETSU_INPUT_SWITCH = new EFNKeyMapping("key.efn.zansetsu_input_switch", 341, "key.efn.category");

   @SubscribeEvent
   public static void registerKeys(RegisterKeyMappingsEvent event) {
      event.register(DOPPELGANGER);
      event.register(DOPPELGANGER_DELAY);
      event.register(SUMMONED_SWORD);
      event.register(ZANSETSU_INPUT_SWITCH);
      event.register(EFN_ARTS);
      event.register(ANGEL);
      event.register(DEMON);
      InputManager.register(EFNComboTypes.KEY_DOPPELGANGER, DOPPELGANGER);
      InputManager.register(EFNComboTypes.KEY_EFN_ARTS, EFN_ARTS);
      InputManager.register(EFNComboTypes.KEY_SUMMONED_SWORD, SUMMONED_SWORD);
   }

   public static Component getName(KeyMapping keyMapping) {
      return keyMapping.getTranslatedKeyMessage();
   }

   public static Component getTranslatableDoppelganger() {
      return getName(DOPPELGANGER);
   }

   public static Component getTranslatableEfn_arts() {
      return getName(EFN_ARTS);
   }

   public static Component getTranslatableSummoned_sword() {
      return getName(SUMMONED_SWORD);
   }
}
