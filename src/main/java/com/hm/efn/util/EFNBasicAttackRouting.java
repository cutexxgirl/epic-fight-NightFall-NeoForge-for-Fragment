package com.hm.efn.util;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public final class EFNBasicAttackRouting {
   private static final String EPICFIGHT_NAMESPACE = "epicfight";

   private EFNBasicAttackRouting() {
   }

   public static boolean shouldLetEpicFightBasicAttackRun(PlayerPatch<?> playerPatch, CapabilityItem capabilityItem) {
      if (playerPatch == null || capabilityItem == null || capabilityItem.isEmpty()) {
         return true;
      }

      List<AnimationAccessor<? extends AttackAnimation>> autoAttacks = capabilityItem.getAutoAttackMotion(playerPatch);
      if (autoAttacks == null || autoAttacks.isEmpty()) {
         return true;
      }

      for (AnimationAccessor<? extends AttackAnimation> autoAttack : autoAttacks) {
         if (!isVanillaEpicFightAnimation(autoAttack)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isVanillaEpicFightAnimation(AnimationAccessor<? extends AttackAnimation> animation) {
      if (animation == null) {
         return false;
      }

      ResourceLocation registryName = animation.registryName();
      return registryName != null && EPICFIGHT_NAMESPACE.equals(registryName.getNamespace());
   }
}
