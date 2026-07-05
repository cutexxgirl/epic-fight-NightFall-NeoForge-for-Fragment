package com.hm.efn.comboevents.condition.state;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EFNTargetHealthCondition implements Condition<PlayerPatch<?>> {
   public Condition<PlayerPatch<?>> read(CompoundTag compoundTag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(PlayerPatch<?> playerPatch) {
      return playerPatch.getTarget() != null && playerPatch.getTarget().getHealth() <= playerPatch.getTarget().getMaxHealth() * 0.2;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
