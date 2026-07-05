package com.hm.efn.gameasset;

import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.skill.arts.ExecuteSkill;
import com.hm.efn.skill.arts.JudgmentCutEndSkill;
import com.hm.efn.skill.arts.StompSkill;
import com.hm.efn.skill.arts.ZansetsuSkill;
import com.hm.efn.skill.dodge.EFNDodgeSkill;
import com.hm.efn.skill.dodge.EFNDodgeSkill_Roll;
import com.hm.efn.skill.dodge.EFNDodgeSkill_Step;
import com.hm.efn.skill.dodge.MurasamaDodge;
import com.hm.efn.skill.dodge.YamatoDodge;
import com.hm.efn.skill.guard.EFNParryingSkill;
import com.hm.efn.skill.guard.MurasamaParry;
import com.hm.efn.skill.passive.IndestructiblePassive;
import com.hm.efn.skill.passive.ParryMasterPassive;
import com.hm.efn.skill.passive.PreciseParryPassive;
import com.hm.efn.skill.sekiro.MortalBladeSkill;
import com.hm.efn.skill.weapon_innate.ScytheSkill;
import com.hm.efn.skill.weapon_innate.ThornWheelSkill;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent;
import com.hm.efn.compat.epicfight.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.dodge.DodgeSkill.Builder;

@EventBusSubscriber(modid = "efn", bus = Bus.MOD)
public class EFNSkills {
   public static Skill SCYTHE;
   public static Skill THORNWHEEL;
   public static Skill EFN_DODGE_ROLL;
   public static Skill EFN_DODGE_STEP;
   public static Skill YAMATO_DODGE;
   public static Skill MURASAMA_DODGE;
   public static Skill INDESTRUCTIBLE;
   public static Skill PARRY_MASTER;
   public static Skill PRECISE_PARRY;
   public static Skill JUDGEMENTCUTEND;
   public static Skill ZANSETSU;
   public static Skill STOMP;
   public static Skill EXECUTION;
   public static Skill AVATAR;
   public static Skill EFN_PARRY;
   public static Skill MURASAMA_PARRY;
   public static Skill MORTAL_BLADE;

   private EFNSkills() {
   }

   @SubscribeEvent(priority = EventPriority.HIGH)
   public static void buildSkillEvent(SkillBuildEvent build) {
      ModRegistryWorker modRegistry = build.createRegistryWorker("efn");
      SCYTHE = modRegistry.build("scythe_skill", ScytheSkill::new, ScytheSkill.createWeaponInnateBuilder(ScytheSkill::new));
      THORNWHEEL = modRegistry.build("thornwheel", ThornWheelSkill::new, ThornWheelSkill.createWeaponInnateBuilder(ThornWheelSkill::new));
      EFN_DODGE_ROLL = modRegistry.build(
         "efn_dodge",
         EFNDodgeSkill_Roll::new,
         (EFNDodgeSkill.Builder)EFNDodgeSkill_Roll.createEFNDodgeBuilder(EFNDodgeSkill_Roll::new)
            .setDefaultAnimations(
               () -> EFNDodgeAnimations.DODGE_ROLL_F,
               () -> EFNDodgeAnimations.DODGE_ROLL_B,
               () -> null,
               () -> null,
               () -> EFNDodgeAnimations.DODGE_ROLL_B
            )
            
      );
      EFN_DODGE_STEP = modRegistry.build(
         "efn_step",
         EFNDodgeSkill_Step::new,
         (EFNDodgeSkill.Builder)EFNDodgeSkill_Step.createEFNDodgeBuilder(EFNDodgeSkill_Step::new)
            .setDefaultAnimations(
               () -> EFNDodgeAnimations.DODGE_STEP_F,
               () -> EFNDodgeAnimations.DODGE_STEP_B,
               () -> EFNDodgeAnimations.DODGE_STEP_L,
               () -> EFNDodgeAnimations.DODGE_STEP_R,
               () -> EFNDodgeAnimations.DODGE_STEP_B
            )
            
      );
      YAMATO_DODGE = modRegistry.build(
         "yamato_step",
         YamatoDodge::new,
         YamatoDodge.createDodgeBuilder(YamatoDodge::new)
            .setAnimations(
               new AnimationAccessor[]{
                  EFNDodgeAnimations.YAMATO_STEP_F,
                  EFNDodgeAnimations.YAMATO_STEP_B,
                  EFNDodgeAnimations.YAMATO_STEP_L,
                  EFNDodgeAnimations.YAMATO_STEP_R,
                  EFNDodgeAnimations.YAMATO_STEP_U,
                  EFNDodgeAnimations.YAMATO_STEP_D
               }
            )
      );
      MURASAMA_DODGE = modRegistry.build(
         "murasama_dodge",
         MurasamaDodge::new,
         (Builder)MurasamaDodge.createDodgeBuilder(MurasamaDodge::new)
            .setAnimations(
               new AnimationAccessor[]{EFNDodgeAnimations.MURASAMA_ROLL_F, EFNDodgeAnimations.MURASAMA_ROLL_B, EFNDodgeAnimations.MURASAMA_ROLL_F_AIR}
            )
            
      );
      INDESTRUCTIBLE = modRegistry.build(
         "indestructible",
         IndestructiblePassive::new,
         IndestructiblePassive.createPassiveBuilder(builder -> new IndestructiblePassive(builder))
      );
      PARRY_MASTER = modRegistry.build(
         "parry_master",
         ParryMasterPassive::new,
         ParryMasterPassive.createParryMasterPassiveBuilder().setResource(Resource.COOLDOWN)
      );
      PRECISE_PARRY = modRegistry.build(
         "precise_parry",
         PreciseParryPassive::new,
         (PreciseParryPassive.Builder)PreciseParryPassive.createPreciseParryBuilder()
      );
      JUDGEMENTCUTEND = modRegistry.build(
         "judgementcutend",
         JudgmentCutEndSkill::new,
         (JudgmentCutEndSkill.Builder)JudgmentCutEndSkill.createJudgmentCutEndBuilder()
            .setActivateType(ActivateType.ONE_SHOT)
            
      );
      STOMP = modRegistry.build(
         "stomp",
         StompSkill::new,
         (StompSkill.Builder)StompSkill.createStompBuilder()
            .setActivateType(ActivateType.ONE_SHOT)
            
      );
      ZANSETSU = modRegistry.build(
         "zansetsu",
         ZansetsuSkill::new,
         (ZansetsuSkill.Builder)ZansetsuSkill.createZansetsuBuilder()
            .setActivateType(ActivateType.ONE_SHOT)
            
      );
      EXECUTION = modRegistry.build(
         "execution",
         ExecuteSkill::new,
         (ExecuteSkill.Builder)ExecuteSkill.createExecuteBuilder()
            .setActivateType(ActivateType.ONE_SHOT)
            
      );
      EFN_PARRY = modRegistry.build("efn_parry", EFNParryingSkill::new, EFNParryingSkill.createActiveGuardBuilder());
      MURASAMA_PARRY = modRegistry.build("murasama_parry", MurasamaParry::new, MurasamaParry.createActiveGuardBuilder());
      MORTAL_BLADE = modRegistry.build(
         "mortal_blade",
         MortalBladeSkill::new,
         MortalBladeSkill.createMortalBladeBuilder().setActivateType(ActivateType.ONE_SHOT)
      );
   }
}
