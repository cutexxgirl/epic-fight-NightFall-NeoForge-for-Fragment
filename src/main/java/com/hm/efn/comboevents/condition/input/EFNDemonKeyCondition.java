package com.hm.efn.comboevents.condition.input;

import com.hm.efn.client.input.keymapping.EFNKeyMappings;
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

public class EFNDemonKeyCondition implements Condition<PlayerPatch<?>> {
   private final boolean checkRelease;

   public EFNDemonKeyCondition() {
      this(false);
   }

   public EFNDemonKeyCondition(boolean checkRelease) {
      this.checkRelease = checkRelease;
   }

   public Condition<PlayerPatch<?>> read(CompoundTag compoundTag) {
      boolean releaseCheck = compoundTag.contains("checkRelease") && compoundTag.getBoolean("checkRelease");
      return new EFNDemonKeyCondition(releaseCheck);
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
         boolean currentState = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), EFNKeyMappings.DEMON.getKey().getValue()) == 1;
         return this.checkRelease != currentState;
      }

      SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      if (!dataManager.hasData(EFNSKillDataKeys.DEMON_KEY)) {
         return false;
      }

      boolean isPressed = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.DEMON_KEY);
      return this.checkRelease != isPressed;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}
