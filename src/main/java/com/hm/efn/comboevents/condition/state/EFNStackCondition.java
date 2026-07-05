package com.hm.efn.comboevents.condition.state;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNStackCondition implements Condition<ServerPlayerPatch> {
   private int min;
   private int max;

   public EFNStackCondition(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public EFNStackCondition() {
      this.min = 1;
      this.max = Integer.MAX_VALUE;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (!compoundTag.contains("min") && !compoundTag.contains("max")) {
         throw new IllegalArgumentException("custom player stack condition error: min or max not specified!");
      }

      this.min = compoundTag.getInt("min");
      this.max = compoundTag.getInt("max");
      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("min", this.min);
      tag.putInt("max", this.max);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      Player player = (Player)serverPlayerPatch.getOriginal();
      if (player.isCreative()) {
         return true;
      }

      int stack = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getStack();
      return stack >= this.min && stack <= this.max;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
