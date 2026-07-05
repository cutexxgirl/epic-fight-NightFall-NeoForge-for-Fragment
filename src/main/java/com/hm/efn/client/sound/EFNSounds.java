package com.hm.efn.client.sound;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFNSounds {
   public static final DeferredRegister<SoundEvent> EFNSound = DeferredRegister.create(Registries.SOUND_EVENT, "efn");
   public static final Supplier<SoundEvent> ARCSLASH = registerSound("arcslash");
   public static final Supplier<SoundEvent> JUDGEMENTCUT = registerSound("judgementcut");
   public static final Supplier<SoundEvent> NOSOUND = registerSound("nosound");
   public static final Supplier<SoundEvent> PARRY = registerSound("parry");
   public static final Supplier<SoundEvent> YAMATO_IN = registerSound("yamato_in");
   public static final Supplier<SoundEvent> YAMATO = registerSound("yamato");
   public static final Supplier<SoundEvent> RAIDEN = registerSound("raiden");
   public static final Supplier<SoundEvent> MURASAMA_1 = registerSound("murasama_1");
   public static final Supplier<SoundEvent> MURASAMA_2 = registerSound("murasama_2");
   public static final Supplier<SoundEvent> MURASAMA_WHOOSH = registerSound("murasama_whoosh");
   public static final Supplier<SoundEvent> MORTAL_BLADE_SWORD_OUT = registerSound("mortal_blade_swordout");
   public static final Supplier<SoundEvent> MORTAL_BLADE_SWORD_IN = registerSound("mortal_blade_swordin");
   public static final Supplier<SoundEvent> MORTAL_BLADE_CHARGE1 = registerSound("mortal_blade_charge1");
   public static final Supplier<SoundEvent> MORTAL_BLADE_CHARGE2 = registerSound("mortal_blade_charge2");
   public static final Supplier<SoundEvent> MORTAL_BLADE_WHOOSH = registerSound("mortal_blade_whoosh");
   public static final Supplier<SoundEvent> SEKIRO_SKILL_GAINED = registerSound("sekiro_skill_gained");
   public static final Supplier<SoundEvent> CIRCULATE_QI = registerSound("circulate_qi");
   public static final Supplier<SoundEvent> DMC5_JC0 = registerSound("dmc5_jc0");
   public static final Supplier<SoundEvent> DMC5_JC1 = registerSound("dmc5_jc1");
   public static final Supplier<SoundEvent> DMC5_JC2 = registerSound("dmc5_jc2");
   public static final Supplier<SoundEvent> DOPPELGANGER_OPEN = registerSound("doppelganger_open");
   public static final Supplier<SoundEvent> DOPPELGANGER_CLOSE = registerSound("doppelganger_close");
   public static final Supplier<SoundEvent> DOPPELGANGER_SWITCH = registerSound("doppelganger_switch");
   public static final Supplier<SoundEvent> WHOOSH_LIGHT_1 = registerSound("whoosh_light_1");
   public static final Supplier<SoundEvent> WHOOSH_LIGHT_2 = registerSound("whoosh_light_2");
   public static final Supplier<SoundEvent> WHOOSH_LIGHT_3 = registerSound("whoosh_light_3");
   public static final Supplier<SoundEvent> WHOOSH_LIGHT_4 = registerSound("whoosh_light_4");
   public static final Supplier<SoundEvent> WHOOSH_HEAVY_1 = registerSound("whoosh_heavy_1");
   public static final Supplier<SoundEvent> WHOOSH_HEAVY_2 = registerSound("whoosh_heavy_2");
   public static final Supplier<SoundEvent> WHOOSH_HEAVY_3 = registerSound("whoosh_heavy_3");
   public static final Supplier<SoundEvent> WHOOSH_HEAVY_4 = registerSound("whoosh_heavy_4");
   public static final Supplier<SoundEvent> ELECTRIC = registerSound("electric");
   public static final Supplier<SoundEvent> MORTAL_BLADE_BLOODWHOOSH = registerSound("mortalblade_bloodwhoosh");
   public static final Supplier<SoundEvent> MAGIC1 = registerSound("magic1");
   public static final Supplier<SoundEvent> FIREWORKS = registerSound("fireworks");

   private static Supplier<SoundEvent> registerSound(String name) {
      ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("efn", name);
      return EFNSound.register(name, () -> SoundEvent.createVariableRangeEvent(resourceLocation));
   }
}
