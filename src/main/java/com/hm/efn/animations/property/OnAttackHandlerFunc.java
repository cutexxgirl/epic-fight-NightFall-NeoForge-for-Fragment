package com.hm.efn.animations.property;

import net.minecraft.world.entity.LivingEntity;
import org.openjdk.nashorn.internal.objects.annotations.Function;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@FunctionalInterface
public interface OnAttackHandlerFunc {
   @Function
   void invoke(AttackAnimation var1, Phase var2, LivingEntityPatch<?> var3, LivingEntity var4, float var5);
}
