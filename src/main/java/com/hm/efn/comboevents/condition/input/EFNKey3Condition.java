package com.hm.efn.comboevents.condition.input;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.p1nero.invincible.client.InvincibleKeyMappings;
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

public class EFNKey3Condition implements Condition<PlayerPatch<?>> {
   private boolean checkRelease;

   public EFNKey3Condition() {
      this(false);
   }

   public EFNKey3Condition(boolean checkRelease) {
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
         boolean currentState = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), InvincibleKeyMappings.KEY3.getKey().getValue()) == 1;
         return this.checkRelease != currentState;
      }

      SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
      if (!dataManager.hasData(EFNSKillDataKeys.KEY3_PRESS)) {
         return false;
      }

      boolean isPressed = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.KEY3_PRESS);
      return this.checkRelease != isPressed;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return List.of();
   }
}
