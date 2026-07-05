package com.hm.efn.comboevents.condition.input;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.util.EFNInputKeyUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EFNSneakKeyCondition implements Condition<PlayerPatch<?>> {
   private boolean checkRelease;

   public EFNSneakKeyCondition() {
      this(false);
   }

   public EFNSneakKeyCondition(boolean checkRelease) {
      this.checkRelease = checkRelease;
   }

   public Condition<PlayerPatch<?>> read(CompoundTag compoundTag) {
      this.checkRelease = compoundTag.contains("checkRelease") && compoundTag.getBoolean("checkRelease");
      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("checkRelease", this.checkRelease);
      return tag;
   }

   public boolean predicate(PlayerPatch<?> playerPatch) {
      if (playerPatch == null || playerPatch.getSkill(SkillSlots.WEAPON_INNATE) == null) {
         return false;
      }

      if (playerPatch.isLogicalClient()) {
         boolean currentState = EFNInputKeyUtil.isDown(Minecraft.getInstance().options.keyShift);
         return this.checkRelease != currentState;
      }

      SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      if (!dataManager.hasData(EFNSKillDataKeys.SNEAK_KEY)) {
         return false;
      }

      boolean isPressed = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.SNEAK_KEY);
      return this.checkRelease != isPressed;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}
