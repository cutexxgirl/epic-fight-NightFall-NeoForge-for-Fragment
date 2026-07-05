package com.hm.efn.gameasset;

import com.hm.efn.gameasset.combos.Aetherialdusk;
import com.hm.efn.gameasset.combos.Beastclaw;
import com.hm.efn.gameasset.combos.Bloodlust;
import com.hm.efn.gameasset.combos.BroadBlade;
import com.hm.efn.gameasset.combos.CrescentMoon;
import com.hm.efn.gameasset.combos.ExampleCombo;
import com.hm.efn.gameasset.combos.Exsiliumgladius;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Kusabimaru;
import com.hm.efn.gameasset.combos.Kusabimaru_Enhance;
import com.hm.efn.gameasset.combos.Meenlance;
import com.hm.efn.gameasset.combos.Murasama;
import com.hm.efn.gameasset.combos.Pioneer;
import com.hm.efn.gameasset.combos.Ruinsgreatsword;
import com.hm.efn.gameasset.combos.Shortsword;
import com.hm.efn.gameasset.combos.Yamato;
import net.neoforged.bus.api.IEventBus;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;

public final class EFNSkillBootstrap {
   private EFNSkillBootstrap() {
   }

   public static void register(IEventBus modEventBus) {
      SkillBuildEvent event = new SkillBuildEvent();
      EFNSkills.buildSkillEvent(event);
      Yamato.BuildSkills(event);
      Shortsword.BuildSkills(event);
      Ruinsgreatsword.BuildSkills(event);
      Pioneer.BuildSkills(event);
      Murasama.BuildSkills(event);
      HfBlade.BuildSkills(event);
      Exsiliumgladius.BuildSkills(event);
      Meenlance.BuildSkills(event);
      Kusabimaru.BuildSkills(event);
      Kusabimaru_Enhance.BuildSkills(event);
      Bloodlust.BuildSkills(event);
      ExampleCombo.onSkillBuild(event);
      Beastclaw.BuildSkills(event);
      Aetherialdusk.BuildSkills(event);
      BroadBlade.onSkillBuild(event);
      CrescentMoon.BuildSkills(event);
      SkillBuildEvent.registerAll(modEventBus);
   }
}
