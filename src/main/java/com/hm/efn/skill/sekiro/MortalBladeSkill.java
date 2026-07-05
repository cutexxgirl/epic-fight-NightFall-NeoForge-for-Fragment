package com.hm.efn.skill.sekiro;

import com.guhao.efn_enhance.gameassets.animations.EFN_ESekiroAnimations;
import com.hm.efn.gameasset.EFNSkillCategories;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.ModList;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;

public class MortalBladeSkill extends EFNSekiroArtSkill {
   private int cooldown = 600;
   private int cooldown_enhance = 400;
   private int stack_cost = 3;
   private float stamina_cost = 3.0F;

   public MortalBladeSkill(MortalBladeSkill.Builder builder) {
      super(builder);
   }

   public static MortalBladeSkill.Builder createMortalBladeBuilder() {
      return (MortalBladeSkill.Builder)new MortalBladeSkill.Builder()
         .setCategory(EFNSkillCategories.EFN_SEKIRO)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.NONE);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      if (parameters.contains("cooldown")) {
         this.cooldown = parameters.getInt("cooldown");
      }

      if (parameters.contains("cooldown_enhance")) {
         this.cooldown_enhance = parameters.getInt("cooldown_enhance");
      }

      if (parameters.contains("stack_cost")) {
         this.stack_cost = parameters.getInt("stack_cost");
      }

      if (parameters.contains("stamina_cost")) {
         this.stamina_cost = parameters.getFloat("stamina_cost");
      }
   }

   @Override
   public void setComboAnimations() {
      this.comboAnimations.add(EFNSekiroAnimations.MORTAL_BLADE_1);
      this.comboAnimations.add(EFNSekiroAnimations.MORTAL_BLADE_2);
      if (ModList.get().isLoaded("efn_enhance")) {
         this.comboAnimations.add(EFN_ESekiroAnimations.OPEN_MORTAL_BLADE_1);
      }
   }

   @Override
   public float getStaminaCost() {
      return this.stamina_cost;
   }

   @Override
   public int getCooldown() {
      return ModList.get().isLoaded("efn_enhance") ? this.cooldown_enhance : this.cooldown;
   }

   @Override
   public int getStackCost() {
      return this.stack_cost;
   }

   public static class Builder extends SkillBuilder<MortalBladeSkill.Builder> {
      public Builder() {
         super(MortalBladeSkill::new);
      }
   }
}
