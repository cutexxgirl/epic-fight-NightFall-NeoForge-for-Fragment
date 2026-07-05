package com.hm.efn.mobeffects;

import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordPatch;
import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.event.types.entity.StunnedEvent;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(modid = "efn")
public class HeavyRainStunEffect extends MobEffect {
   private static final String EFN_STUN_PREV_TARGET_HEAVY_RAIN = "EFN_Stun_PrevTarget_heavy_rain";

   public HeavyRainStunEffect() {
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
            mob.getPersistentData().putUUID("EFN_Stun_PrevTarget_heavy_rain", mob.getTarget().getUUID());
            mob.setTarget(null);
         }
      }
      return true;
   }

   public void onMobRemoved(@NotNull LivingEntity pLivingEntity, int pAmplifier, Entity.RemovalReason reason) {
      super.onMobRemoved(pLivingEntity, pAmplifier, reason);
      if (pLivingEntity instanceof Mob mob && !mob.level().isClientSide() && mob.getPersistentData().hasUUID("EFN_Stun_PrevTarget_heavy_rain")) {
         UUID targetId = mob.getPersistentData().getUUID("EFN_Stun_PrevTarget_heavy_rain");
         if (mob.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(targetId) instanceof LivingEntity livingTarget && livingTarget.isAlive()) {
            mob.setTarget(livingTarget);
         }

         mob.getPersistentData().remove("EFN_Stun_PrevTarget_heavy_rain");
      }
   }

   @SubscribeEvent
   public static void onLivingAttack(LivingIncomingDamageEvent event) {
      LivingEntity target = event.getEntity();
      DamageSource source = event.getSource();
      Holder<MobEffect> heavyRainStun = EFNMobEffectRegistry.HEAVY_RAIN_STUN;
      if (source != null
         && !source.is(SinSummonedSwordPatch.HEAVY_RAIN_SWORD_DAMAGE)
         && !source.is(SinSummonedSwordPatch.BLAST_SWORD_DAMAGE)
         && !source.is(SinSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
         && target.hasEffect(heavyRainStun)) {
         target.removeEffect(heavyRainStun);
      }
   }

   @SubscribeEvent
   public static void onLivingKnockBack(LivingKnockBackEvent event) {
      Holder<MobEffect> heavyRainStun = EFNMobEffectRegistry.HEAVY_RAIN_STUN;
      if (event.getEntity().hasEffect(heavyRainStun)) {
         event.setCanceled(true);
      }
   }

   public static void onEntityStun(StunnedEvent event) {
      LivingEntityPatch<?> entityPatch = event.getEntityPatch();
      if (entityPatch != null) {
         LivingEntity livingEntity = (LivingEntity)entityPatch.getOriginal();
         Holder<MobEffect> heavyRainStun = EFNMobEffectRegistry.HEAVY_RAIN_STUN;

         if (livingEntity.hasEffect(heavyRainStun)) {
            DamageSource damageSource = event.getDamageSource();
            if (damageSource != null
               && (
                  damageSource.is(SinSummonedSwordPatch.HEAVY_RAIN_SWORD_DAMAGE)
                     || damageSource.is(SinSummonedSwordPatch.BLAST_SWORD_DAMAGE)
                     || damageSource.is(SinSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
               )) {
               event.getDamageSource().setStunType(StunType.NONE);
               event.cancel();
            }
         }
      }
   }
}
