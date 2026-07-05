package com.hm.efn.skill.passive;

import com.hm.efn.gameasset.EFNEnchantment;

import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.weapon_innate.ScytheSkill;
import com.hm.efn.util.EFNSkillChecks;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.ValueModifier.Multiplier;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class PreciseParryPassive extends PassiveSkill {
   private static final UUID EVENT_UUID = UUID.fromString("a1b2c3d4-5678-90ef-1234-56789abcdef2");
   private static final UUID DAMAGE_EVENT_UUID = UUID.fromString("b1b2c3d4-5678-90ef-1234-56789abcdef1");
   private static final float BUFF_DURATION = 0.7F;
   private float damageMultiplier = 1.5F;
   private float windowStart = 0.0F;
   private float windowEnd = 0.13F;

   public PreciseParryPassive(PreciseParryPassive.Builder builder) {
      super(builder);
   }

   public static PreciseParryPassive.Builder createPreciseParryBuilder() {
      return (PreciseParryPassive.Builder)new PreciseParryPassive.Builder().setCategory(SkillCategories.PASSIVE).setResource(Resource.NONE);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.damageMultiplier = parameters.contains("damage_multiplier") ? parameters.getFloat("damage_multiplier") : this.damageMultiplier;
      this.windowStart = parameters.contains("window_start") ? parameters.getFloat("window_start") : this.windowStart;
      this.windowEnd = parameters.contains("window_end") ? parameters.getFloat("window_end") : this.windowEnd;
      if (this.windowEnd <= this.windowStart) {
         this.windowEnd = this.windowStart + 0.05F;
      }
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      SkillDataManager data = container.getDataManager();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.SKILL_CAST_EVENT, EVENT_UUID, event -> {
         if (EFNSkillChecks.skill(event.getSkillContainer()) instanceof GuardSkill && !container.getExecutor().isLogicalClient()) {
            ServerPlayer player = (ServerPlayer)container.getExecutor().getOriginal();
            int currentTick = player.tickCount;
            data.setDataSync(EFNSKillDataKeys.DEFENSE_START_TICK, currentTick);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.TAKE_DAMAGE_EVENT_ATTACK,
         EVENT_UUID,
         event -> {
            if (event.isParried() && !container.getExecutor().isLogicalClient()) {
               ServerPlayer player = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
               SkillContainer weaponSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(SkillSlots.WEAPON_INNATE);
               SkillContainer scytheSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(EFNSkills.SCYTHE);
               if (weaponSkillContainer == null || weaponSkillContainer.getSkill() == null) {
                  return;
               }

               int currentTick = player.tickCount;
               int defenseStart = (Integer)data.getDataValue(EFNSKillDataKeys.DEFENSE_START_TICK);
               float elapsed = (currentTick - defenseStart) / 20.0F;
               int currentStack = weaponSkillContainer.getStack();
               int maxStack = weaponSkillContainer.getSkill().getMaxStack();
               int newCharge = Math.min(maxStack, currentStack + 1);
               int sweepingEdgeEnchantment = EFNEnchantment.getLevel(((Player)container.getExecutor().getOriginal()).getMainHandItem(), Enchantments.SWEEPING_EDGE);
               int effectDuration = Math.min(80, 30 + sweepingEdgeEnchantment * 10);
               SkillContainer efnParryContainer = container.getExecutor().getSkill(EFNSkills.EFN_PARRY);
               boolean haveEFNParry = efnParryContainer != null && efnParryContainer.hasSkill();
               if (elapsed >= this.windowStart && elapsed <= this.windowEnd) {
                  data.setDataSync(EFNSKillDataKeys.BUFF_ACTIVE, true);
                  data.setDataSync(EFNSKillDataKeys.BUFF_END_TICK, currentTick + 14);
                  if (!haveEFNParry) {
                     ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EFNSounds.PARRY.get(), 0.6F, 0.0F, 0.0F);
                  }

                  spawnCounterParticles((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal());
                  spawnBlockParticles((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal());
                  ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                     .addEffect(new MobEffectInstance(EFNMobEffectRegistry.RING, effectDuration, 0, false, false, false));
                  ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                     .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, effectDuration, 3, false, false, false));
                  ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                     .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, effectDuration, 2, false, false, false));
                  ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                     .addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 2, false, false, false));
                  weaponSkillContainer.getSkill().setStackSynchronize(weaponSkillContainer, newCharge);
                  if (scytheSkillContainer != null) {
                     SkillDataManager scytheSkillDataManager = scytheSkillContainer.getDataManager();
                     if (scytheSkillDataManager != null) {
                        float current = (Float)scytheSkillDataManager.getDataValue(EFNSKillDataKeys.SCYTHE_BLOOD_POWER);
                        scytheSkillDataManager.setDataSync(
                           EFNSKillDataKeys.SCYTHE_BLOOD_POWER,
                           Mth.clamp(current + ScytheSkill.getMaxBloodPower() * 0.03F, 0.0F, ScytheSkill.getMaxBloodPower())
                        );
                     }
                  }
               }
            }

            if ((Boolean)data.getDataValue(EFNSKillDataKeys.BUFF_ACTIVE)
               && ((ServerPlayerPatch)event.getPlayerPatch()).getEntityState().getLevel() <= 2) {
               event.cancel();
               event.setParried(true);
               event.setResult(ResultType.MISSED);
            }
         },
         -1
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.DEAL_DAMAGE_EVENT_ATTACK, DAMAGE_EVENT_UUID, event -> {
         if (!container.getExecutor().isLogicalClient()) {
            ServerPlayer player = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
            boolean isBuffActive = (Boolean)data.getDataValue(EFNSKillDataKeys.BUFF_ACTIVE);
            if (isBuffActive) {
               int currentTick = player.tickCount;
               int buffEnd = (Integer)data.getDataValue(EFNSKillDataKeys.BUFF_END_TICK);
               if (currentTick <= buffEnd) {
                  if (event.getDamageSource() instanceof EpicFightDamageSource) {
                     EpicFightDamageSource damageSource = event.getDamageSource();
                     damageSource.attachDamageModifier(ValueModifier.multiplier(this.damageMultiplier));
                     damageSource.attachArmorNegationModifier(new Multiplier(damageSource.getBaseArmorNegation() * 2.0F));
                     damageSource.setStunType(StunType.LONG);
                     damageSource.addRuntimeTag(EpicFightDamageTypeTags.GUARD_PUNCTURE);
                     ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EpicFightSounds.EVISCERATE.get(), 0.0F, 0.0F);
                     data.setDataSync(EFNSKillDataKeys.BUFF_ACTIVE, false);
                  }
               } else {
                  data.setDataSync(EFNSKillDataKeys.BUFF_ACTIVE, false);
               }
            }
         }
      });
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().isLogicalClient()) {
         ServerPlayer player = (ServerPlayer)container.getExecutor().getOriginal();
         SkillDataManager data = container.getDataManager();
         boolean isBuffActive = (Boolean)data.getDataValue(EFNSKillDataKeys.BUFF_ACTIVE);
         if (isBuffActive) {
            int currentTick = player.tickCount;
            int buffEnd = (Integer)data.getDataValue(EFNSKillDataKeys.BUFF_END_TICK);
            if (currentTick > buffEnd) {
               data.setDataSync(EFNSKillDataKeys.BUFF_ACTIVE, false);
            }
         }
      }
   }

   private static void spawnBlockParticles(ServerPlayer player) {
      ServerLevel level = (ServerLevel)player.level();
      Vec3 center = player.position().add(0.0, 1.5, 0.0);

      for (int i = 0; i < 12; i++) {
         double angle = Math.toRadians(i * 30);
         Vec3 pos = center.add(Math.sin(angle) * 1.5, Math.cos(angle) * 0.3, Math.cos(angle) * 1.5);
         level.sendParticles(ParticleTypes.WAX_OFF, pos.x, pos.y, pos.z, 2, 0.0, 0.0, 0.0, 1.0);
      }
   }

   private static void spawnCounterParticles(ServerPlayer player) {
      ((ServerLevel)player.level()).sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.5, player.getZ(), 15, 0.3, 0.5, 0.3, 0.5);
   }

   public void onRemoved(SkillContainer container) {
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, -1);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_ATTACK, DAMAGE_EVENT_UUID);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDraw(SkillContainer container) {
      return false;
   }

   public Skill getPriorSkill() {
      return EpicFightSkills.PARRYING.get();
   }

   public static class Builder extends SkillBuilder<PreciseParryPassive.Builder> {
      public Builder() {
         super(PreciseParryPassive::new);
      }
   }
}
