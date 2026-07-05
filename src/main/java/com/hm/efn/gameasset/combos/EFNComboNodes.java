package com.hm.efn.gameasset.combos;

import com.p1nero.invincible.api.combo.ComboNode;
import java.util.function.Supplier;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;

public final class EFNComboNodes {
   private EFNComboNodes() {
   }

   public static ComboNode create(Supplier<? extends AnimationAccessor<? extends StaticAnimation>> animationAccessor) {
      ComboNode node = ComboNode.createNode(null);
      node.setAnimationAccessorSupplier(() -> animationAccessor.get());
      return node;
   }
}
