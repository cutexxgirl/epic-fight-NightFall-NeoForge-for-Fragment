package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class Shiver extends MobEffect {
   public Shiver() {
      super(MobEffectCategory.NEUTRAL, 8900331);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, EffectAttributeModifiers.id("7107DE5E-7CE8-4030-940E-514C1F160890"), -0.1, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, EffectAttributeModifiers.id("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9"), -0.1, Operation.ADD_MULTIPLIED_TOTAL
      );
   }
}
