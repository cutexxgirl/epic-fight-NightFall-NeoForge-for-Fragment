package com.hm.efn.comboevents.condition.weapons;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.combos.Ruinsgreatsword;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class RuinGreatswordIsChargingCondition implements Condition<ServerPlayerPatch> {
   private final boolean shouldBeCharging;

   public RuinGreatswordIsChargingCondition(boolean shouldBeCharging) {
      this.shouldBeCharging = shouldBeCharging;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return new RuinGreatswordIsChargingCondition(compoundTag.getBoolean("shouldBeCharging"));
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("shouldBeCharging", this.shouldBeCharging);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch playerPatch) {
      if (playerPatch != null && playerPatch.getSkill(Ruinsgreatsword.Ruinsgreatsword) != null) {
         Boolean isCharging = (Boolean)playerPatch.getSkill(Ruinsgreatsword.Ruinsgreatsword)
            .getDataManager()
            .getDataValue(EFNSKillDataKeys.IS_CHARGING);
         return isCharging != null && this.shouldBeCharging == isCharging;
      } else {
         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
