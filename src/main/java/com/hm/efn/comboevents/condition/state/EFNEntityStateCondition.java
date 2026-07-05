package com.hm.efn.comboevents.condition.state;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNEntityStateCondition implements Condition<ServerPlayerPatch> {
   private int minLevel;
   private int maxLevel;
   private EFNEntityStateCondition.ComparisonType comparisonType;

   public EFNEntityStateCondition(int minLevel, int maxLevel, EFNEntityStateCondition.ComparisonType comparisonType) {
      this.minLevel = minLevel;
      this.maxLevel = maxLevel;
      this.comparisonType = comparisonType;
   }

   public EFNEntityStateCondition() {
      this.minLevel = 0;
      this.maxLevel = 0;
      this.comparisonType = EFNEntityStateCondition.ComparisonType.EQUAL;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      if (!compoundTag.contains("minLevel") && !compoundTag.contains("maxLevel") && !compoundTag.contains("comparisonType")) {
         throw new IllegalArgumentException("EFN entity state condition error: parameters not specified!");
      }

      this.minLevel = compoundTag.getInt("minLevel");
      this.maxLevel = compoundTag.getInt("maxLevel");
      this.comparisonType = EFNEntityStateCondition.ComparisonType.valueOf(compoundTag.getString("comparisonType"));
      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("minLevel", this.minLevel);
      tag.putInt("maxLevel", this.maxLevel);
      tag.putString("comparisonType", this.comparisonType.name());
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      if (serverPlayerPatch == null) {
         return false;
      }

      EntityState entityState = serverPlayerPatch.getEntityState();
      if (entityState == null) {
         return false;
      }

      int phaseLevel = entityState.getLevel();

      return switch (this.comparisonType) {
         case EQUAL -> phaseLevel == this.minLevel;
         case GREATER_THAN -> phaseLevel > this.minLevel;
         case LESS_THAN -> phaseLevel < this.minLevel;
         case GREATER_THAN_OR_EQUAL -> phaseLevel >= this.minLevel;
         case LESS_THAN_OR_EQUAL -> phaseLevel <= this.minLevel;
         case BETWEEN -> phaseLevel >= this.minLevel && phaseLevel <= this.maxLevel;
         case OUTSIDE -> phaseLevel < this.minLevel || phaseLevel > this.maxLevel;
      };
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }

   public enum ComparisonType {
      EQUAL,
      GREATER_THAN,
      LESS_THAN,
      GREATER_THAN_OR_EQUAL,
      LESS_THAN_OR_EQUAL,
      BETWEEN,
      OUTSIDE;
   }
}
