package com.hm.efn.comboevents.condition.state;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNStaminaCondition implements Condition<ServerPlayerPatch> {
   private final float stamina;
   private final boolean isLarger;

   public EFNStaminaCondition(float stamina, boolean isLarger) {
      this.stamina = stamina;
      this.isLarger = isLarger;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return new EFNStaminaCondition(compoundTag.getFloat("stamina"), compoundTag.getBoolean("isLarger"));
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putFloat("stamina", this.stamina);
      tag.putBoolean("isLarger", this.isLarger);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (playerPatch == null) {
         return false;
      } else {
         Player player = (Player)playerPatch.getOriginal();
         if (player.isCreative()) {
            return true;
         } else {
            return this.isLarger ? playerPatch.getStamina() >= this.stamina : playerPatch.getStamina() <= this.stamina;
         }
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
