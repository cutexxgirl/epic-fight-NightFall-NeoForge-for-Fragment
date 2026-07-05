package com.hm.efn.comboevents.condition.state;

import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EFNAirborneCondition implements Condition<PlayerPatch<?>> {
   private final boolean strictMode;
   private final float minAirborneDistance;

   public EFNAirborneCondition() {
      this(true, 0.5F);
   }

   public EFNAirborneCondition(boolean strictMode, float minAirborneDistance) {
      this.strictMode = strictMode;
      this.minAirborneDistance = minAirborneDistance;
   }

   public Condition<PlayerPatch<?>> read(CompoundTag compoundTag) {
      boolean strict = !compoundTag.contains("strictMode") || compoundTag.getBoolean("strictMode");
      float distance = compoundTag.contains("minDistance") ? compoundTag.getFloat("minDistance") : 1.7F;
      return new EFNAirborneCondition(strict, distance);
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("strictMode", this.strictMode);
      tag.putFloat("minDistance", this.minAirborneDistance);
      return tag;
   }

   public boolean predicate(PlayerPatch<?> playerPatch) {
      Player player = (Player)playerPatch.getOriginal();
      if (this.isFloatingInFluid(player)) {
         return true;
      } else if (player.onGround()) {
         return false;
      } else {
         return player.hasEffect(EFNMobEffectRegistry.VERTICALSTOP)
            ? true
            : !this.strictMode || this.checkStrictAirborne(player) && this.checkAirborneDistance(player);
      }
   }

   private boolean isFloatingInFluid(Player player) {
      boolean inWater = player.isInWater();
      boolean inLava = player.isInLava();
      if (!inWater && !inLava) {
         return false;
      }

      BlockPos footPos = BlockPos.containing(player.getX(), player.getY() - 0.1, player.getZ());
      BlockState blockState = player.level().getBlockState(footPos);
      FluidState fluidState = blockState.getFluidState();
      boolean isFootInFluid = !fluidState.isEmpty();
      boolean isFootAir = blockState.isAir();
      Vec3 motion = player.getDeltaMovement();
      boolean isMoving = motion.lengthSqr() > 0.001;
      return (isFootInFluid || isFootAir || isMoving) && !player.onGround();
   }

   private boolean checkStrictAirborne(Player player) {
      return !player.isInWater() && !player.onClimbable() && !player.isPassenger() && !player.isFallFlying() && !player.isInLava() && !player.getAbilities().flying;
   }

   private boolean checkAirborneDistance(Player player) {
      MutableBlockPos pos = new MutableBlockPos(player.getX(), player.getY() - 0.1, player.getZ());
      int searchDepth = 0;

      BlockState blockState;
      do {
         blockState = player.level().getBlockState(pos);
         pos.move(0, -1, 0);
         searchDepth++;
      } while ((blockState.isAir() || blockState.getBlock() instanceof BushBlock) && searchDepth < 10);

      float distance = (float)(player.getY() - pos.getY() - 1.0);
      return distance >= this.minAirborneDistance;
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
