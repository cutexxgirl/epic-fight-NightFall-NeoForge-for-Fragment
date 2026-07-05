package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AttackSpeedIncreaseEffect extends MobEffect {
   public AttackSpeedIncreaseEffect() {
      super(MobEffectCategory.BENEFICIAL, 16711935);
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED, EffectAttributeModifiers.id("b9d3b884-3608-4e9f-8d42-42dfa983c14f"), 0.01, Operation.ADD_MULTIPLIED_TOTAL
      );
   }
}
