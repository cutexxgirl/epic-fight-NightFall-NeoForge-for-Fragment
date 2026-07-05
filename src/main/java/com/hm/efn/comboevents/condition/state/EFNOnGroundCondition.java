package com.hm.efn.comboevents.condition.state;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNOnGroundCondition implements Condition<ServerPlayerPatch> {
   private final boolean strictMode;

   public EFNOnGroundCondition() {
      this(false);
   }

   public EFNOnGroundCondition(boolean strictMode) {
      this.strictMode = strictMode;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      boolean strict = !compoundTag.contains("strictMode") || compoundTag.getBoolean("strictMode");
      return new EFNOnGroundCondition(strict);
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("strictMode", this.strictMode);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      Player player = (Player)serverPlayerPatch.getOriginal();
      if (!player.onGround()) {
         return false;
      } else {
         return this.strictMode ? this.checkStrictGroundCondition(player) : true;
      }
   }

   private boolean checkStrictGroundCondition(Player player) {
      return !player.isInWater() && !player.isInLava() ? !player.onClimbable() : false;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
