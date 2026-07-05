package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AttackDamageIncreaseEffect extends MobEffect {
   public AttackDamageIncreaseEffect() {
      super(MobEffectCategory.BENEFICIAL, 16776960);
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, EffectAttributeModifiers.id("c16697ea-3470-48cc-9e4a-ace8491b6b2d"), 0.01, Operation.ADD_MULTIPLIED_TOTAL
      );
   }
}
