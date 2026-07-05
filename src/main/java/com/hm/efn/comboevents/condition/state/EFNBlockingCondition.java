package com.hm.efn.comboevents.condition.state;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.modules.HoldableSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNBlockingCondition implements Condition<ServerPlayerPatch> {
   private final boolean checkBlocking;

   public EFNBlockingCondition(boolean checkBlocking) {
      this.checkBlocking = checkBlocking;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      boolean checkBlocking = compoundTag.getBoolean("checkBlocking");
      return new EFNBlockingCondition(checkBlocking);
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("checkBlocking", this.checkBlocking);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      if (serverPlayerPatch == null) {
         return false;
      }

      HoldableSkill holdingSkill = serverPlayerPatch.getHoldingSkill();
      boolean isBlocking = holdingSkill instanceof GuardSkill;
      return this.checkBlocking ? isBlocking : !isBlocking;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }

   @Override
   public String toString() {
      return "BlockingCondition{checkBlocking=" + this.checkBlocking + "}";
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         EFNBlockingCondition that = (EFNBlockingCondition)obj;
         return this.checkBlocking == that.checkBlocking;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.checkBlocking);
   }
}
