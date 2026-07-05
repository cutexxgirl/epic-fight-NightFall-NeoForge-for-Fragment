package com.hm.efn.comboevents.condition.state;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EFNSprintingCondition implements Condition<ServerPlayerPatch> {
   private final boolean requireOnGround;
   private final boolean allowInWater;

   public EFNSprintingCondition() {
      this(true, true);
   }

   public EFNSprintingCondition(boolean requireOnGround, boolean allowInWater) {
      this.requireOnGround = requireOnGround;
      this.allowInWater = allowInWater;
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      boolean requireOnGround = !compoundTag.contains("requireOnGround") || compoundTag.getBoolean("requireOnGround");
      boolean allowInWater = !compoundTag.contains("allowInWater") || compoundTag.getBoolean("allowInWater");
      return new EFNSprintingCondition(requireOnGround, allowInWater);
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("requireOnGround", this.requireOnGround);
      tag.putBoolean("allowInWater", this.allowInWater);
      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
      return !player.isSprinting() ? false : !this.requireOnGround || this.checkGroundCondition(player);
   }

   private boolean checkGroundCondition(Player player) {
      return this.allowInWater && player.isInWater() ? true : player.onGround();
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
