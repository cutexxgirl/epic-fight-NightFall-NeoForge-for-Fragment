package com.hm.efn.comboevents.condition.state;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNMurasamaSheathCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) throws IllegalArgumentException {
      return null;
   }

   public CompoundTag serializePredicate() {
      return null;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      Animator animator = serverPlayerPatch.getAnimator();
      boolean isDodgeAnim = Objects.requireNonNull(animator.getPlayerFor(null)).getAnimation().get() instanceof DodgeAnimation;
      return !serverPlayerPatch.getEntityState().inaction() && !isDodgeAnim;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}
