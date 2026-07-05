package com.hm.efn.comboevents.condition.input;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EFNUpKeyCondition implements Condition<PlayerPatch<?>> {
   private boolean checkRelease;

   public EFNUpKeyCondition() {
      this(false);
   }

   public EFNUpKeyCondition(boolean checkRelease) {
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
         boolean currentState = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keyUp.getKey().getValue()) == 1;
         return this.checkRelease != currentState;
      }

      SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      if (!dataManager.hasData(EFNSKillDataKeys.UP_KEY)) {
         return false;
      }

      boolean isPressed = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.UP_KEY);
      return this.checkRelease != isPressed;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}
