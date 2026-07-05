package com.hm.efn.mobeffects;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class StunEffect extends MobEffect {
   private static final String PREV_TARGET_KEY = "EFN_Stun_PrevTarget";

   public StunEffect() {
      super(MobEffectCategory.NEUTRAL, 5904538);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity owner, int amplifier) {
      owner.setDeltaMovement(Vec3.ZERO);
      owner.hurtMarked = true;
      if (owner.tickCount % 5 == 0) {
         owner.setPos(owner.xOld, owner.yOld, owner.zOld);
      }

      owner.setSprinting(false);
      owner.setShiftKeyDown(false);
      if (owner instanceof Mob mob) {
         mob.getNavigation().stop();
         if (mob.getTarget() != null) {
            mob.getPersistentData().putUUID("EFN_Stun_PrevTarget", mob.getTarget().getUUID());
            mob.setTarget(null);
         }
      }
      return true;
   }

   public void onMobRemoved(@NotNull LivingEntity pLivingEntity, int pAmplifier, Entity.RemovalReason reason) {
      super.onMobRemoved(pLivingEntity, pAmplifier, reason);
      if (pLivingEntity instanceof Mob mob && !mob.level().isClientSide() && mob.getPersistentData().hasUUID("EFN_Stun_PrevTarget")) {
         UUID targetId = mob.getPersistentData().getUUID("EFN_Stun_PrevTarget");
         if (mob.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(targetId) instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
            mob.setTarget(livingTarget);
         }

         mob.getPersistentData().remove("EFN_Stun_PrevTarget");
      }
   }
}
