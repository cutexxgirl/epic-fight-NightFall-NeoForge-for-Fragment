package com.hm.efn.skill.arts;

import com.hm.efn.client.input.keymapping.EFNKeyMappings;
import com.hm.efn.entity.skill.SoulHuntOrb;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkillCategories;
import com.hm.efn.gameasset.EFNSkillSlots;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Kusabimaru;
import com.hm.efn.gameasset.combos.Kusabimaru_Enhance;
import com.hm.efn.gameasset.combos.Murasama;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.network.EFNNetworkHandler;
import com.hm.efn.network.SoulAfterimagePacket;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EFNSkillChecks;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.network.client.CPSkillRequest.WorkType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerKilledEvent;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class ExecuteSkill extends PassiveSkill {
   private static final UUID KILL_EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0171ac170004");
   private static final UUID DAMAGE_EVENT_UUID = UUID.fromString("a416c93a-42cb-22eb-b378-0171ac170004");
   private static final UUID ANIMATION_BEGIN_EVENT_UUID = UUID.fromString("a416c93a-42cb-33eb-b378-0171ac170004");
   private static final UUID PARRY_EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0721ac170005");
   private static final UUID DODGE_EVENT_UUID = UUID.fromString("a416c93a-42cb-22eb-b378-0721ac170005");
   private int cooldown;
   private int parryCooldownReduction;
   private int dodgeSuccessCooldownReduction;
   private int killCooldownReduction;
   private int skillKillCooldownReduction;
   private int neutralizeTargetHitCooldownReduction;
   private int skillKillStackReward;
   private int neutralizeTargetHitStackReward;
   private float neutralizeTargetHitDamageMultiplier;
   private float neutralizeTargetHitImpactMultiplier;
   private List<LivingEntity> currentNeutralizeEntities = new ArrayList<>();
   private boolean wasArtsKeyDown;

   public ExecuteSkill(ExecuteSkill.Builder builder) {
      super(builder);
   }

   public static ExecuteSkill.Builder createExecuteBuilder() {
      return (ExecuteSkill.Builder)new ExecuteSkill.Builder()
         .setCategory(EFNSkillCategories.EFN_ARTS)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.NONE);
   }

   @NotNull
   private static SoulHuntOrb getSoulHuntOrb(int i, Player original, LivingEntity target) {
      double offsetX = (i - 1) * 0.5;
      double offsetY = i * 0.3;
      double offsetZ = i % 2 == 0 ? 0.3 : -0.3;
      return new SoulHuntOrb(original, target.getX() + offsetX, target.getY() + target.getBbHeight() * 0.5 + offsetY, target.getZ() + offsetZ);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.cooldown = parameters.getInt("cooldown");
      this.parryCooldownReduction = parameters.getInt("parry_cooldown_reduction");
      this.dodgeSuccessCooldownReduction = parameters.getInt("dodge_cooldown_reduction");
      this.killCooldownReduction = parameters.getInt("kill_cooldown_reduction");
      this.skillKillCooldownReduction = parameters.getInt("skill_kill_cooldown_reduction");
      this.neutralizeTargetHitCooldownReduction = parameters.getInt("neutralize_target_hit_cooldown_reduction");
      this.skillKillStackReward = parameters.getInt("skill_kill_stack_reward");
      this.neutralizeTargetHitStackReward = parameters.getInt("neutralize_target_hit_stack_reward");
      this.neutralizeTargetHitDamageMultiplier = parameters.getFloat("neutralize_target_hit_damage_multiplier");
      this.neutralizeTargetHitImpactMultiplier = parameters.getFloat("neutralize_target_hit_impact_multiplier");
   }

   public boolean canExecute(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillContainer yamatoSkillContainer = executor.getSkill(Yamato.yamato);
      if (yamatoSkillContainer != null && yamatoSkillContainer.hasSkill()) {
         return false;
      }

      SkillContainer kusabimaruSkillContainer = executor.getSkill(Kusabimaru.kusabimaru);
      SkillContainer sekiroSkillContainer = executor.getSkill(EFNSkillSlots.EFN_SEKIRO);
      if (sekiroSkillContainer != null && sekiroSkillContainer.hasSkill() && kusabimaruSkillContainer != null && kusabimaruSkillContainer.hasSkill()) {
         return false;
      }

      if (ModList.get().isLoaded("efn_enhance")) {
         SkillContainer kusabimaruEnhanceSkillContainer = executor.getSkill(Kusabimaru_Enhance.kusabimaru_enhance);
         SkillContainer sekiroSkillContainer2 = executor.getSkill(EFNSkillSlots.EFN_SEKIRO);
         if (sekiroSkillContainer2 != null
            && sekiroSkillContainer2.hasSkill()
            && kusabimaruEnhanceSkillContainer != null
            && kusabimaruEnhanceSkillContainer.hasSkill()) {
            return false;
         }
      }

      SkillContainer murasamaSkillContainer = executor.getSkill(Murasama.Murasama);
      SkillContainer hfBladeSkillContainer = executor.getSkill(HfBlade.HfBlade);
      SkillContainer zansetsuSkillContainer = executor.getSkill(EFNSkills.ZANSETSU);
      if (murasamaSkillContainer != null && murasamaSkillContainer.hasSkill() && zansetsuSkillContainer != null && zansetsuSkillContainer.hasSkill()) {
         return false;
      }

      if (hfBladeSkillContainer != null && hfBladeSkillContainer.hasSkill() && zansetsuSkillContainer != null && zansetsuSkillContainer.hasSkill()) {
         return false;
      }

      boolean isOnGround = ((Player)executor.getOriginal()).onGround();
      boolean isHoldingWeapon = this.isHoldingWeapon(container);
      boolean isCreative = ((Player)executor.getOriginal()).isCreative();
      return isHoldingWeapon
         && isOnGround
         && (isCreative || (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN) <= 0);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      EntityEventListener listener = container.getExecutor().getEventListener();
      if (!container.getExecutor().isLogicalClient()) {
         com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.ANIMATION_BEGIN_EVENT, ANIMATION_BEGIN_EVENT_UUID, event -> {
            StaticAnimation skillAnimation = (StaticAnimation)EFNSkillAnimations.EXECUTION.get();
            if (skillAnimation != null && skillAnimation.equals(event.getAnimation())) {
               this.currentNeutralizeEntities.clear();
               this.currentNeutralizeEntities = this.findNeutralizeEntities(event.getPlayerPatch());
            }
         });
         com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
            EventType.TAKE_DAMAGE_EVENT_ATTACK,
            PARRY_EVENT_UUID,
            event -> {
               if (event.isParried() && !container.getExecutor().isLogicalClient()) {
                  ServerPlayer serverplayer = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
                  int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
                  if (currentCooldown > 0) {
                     int newCooldown = Math.max(0, currentCooldown - this.parryCooldownReduction);
                     container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, newCooldown);
                     Vec3 sourcePosition = event.getDamageSource().getSourcePosition();
                     if (sourcePosition != null) {
                        serverplayer.serverLevel()
                           .sendParticles(
                              ParticleTypes.SOUL,
                              sourcePosition.x(),
                              sourcePosition.y() + 1.4,
                              sourcePosition.z(),
                              4,
                              0.0,
                              0.0,
                              0.0,
                              0.1
                           );
                     }
                  }
               }
            },
            -1
         );
         com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
            EventType.DODGE_SUCCESS_EVENT,
            DODGE_EVENT_UUID,
            event -> {
               ServerPlayer serverplayer = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
               int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
               if (currentCooldown > 0) {
                  int newCooldown = Math.max(0, currentCooldown - this.dodgeSuccessCooldownReduction);
                  container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, newCooldown);
                  serverplayer.serverLevel()
                     .sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        event.getLocation().x(),
                        event.getLocation().y() + 1.4,
                        event.getLocation().z(),
                        4,
                        0.0,
                        0.0,
                        0.0,
                        0.075
                     );
               }
            }
         );
         com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
            EventType.DEAL_DAMAGE_EVENT_ATTACK,
            DAMAGE_EVENT_UUID,
            event -> {
               SkillContainer weaponSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(SkillSlots.WEAPON_INNATE);
               if (weaponSkillContainer != null && event.getDamageSource().getEntity() != null) {
                  Skill skill = weaponSkillContainer.getSkill();
                  if (skill != null
                     && event.getDamageSource().getAnimation() != null
                     && event.getDamageSource().getAnimation().equals(EFNSkillAnimations.EXECUTION)) {
                     event.getTarget().removeEffect(EFNMobEffectRegistry.STOP);
                     boolean hitNeutralizeEntity = this.isTargetInNeutralizeList(event.getTarget());
                     if (hitNeutralizeEntity) {
                        int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
                        int currentStack = weaponSkillContainer.getStack();
                        int maxStack = skill.getMaxStack();
                        int newCharge = Math.min(maxStack, currentStack + this.neutralizeTargetHitStackReward);
                        if (currentCooldown > 0) {
                           int newCooldown = Math.max(0, currentCooldown - this.neutralizeTargetHitCooldownReduction);
                           container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, newCooldown);
                        }

                        if (event.getDamageSource() instanceof EpicFightDamageSource) {
                           EpicFightDamageSource damageSource = event.getDamageSource();
                           damageSource.attachDamageModifier(ValueModifier.multiplier(this.neutralizeTargetHitDamageMultiplier));
                           damageSource.attachImpactModifier(ValueModifier.multiplier(this.neutralizeTargetHitImpactMultiplier));
                           ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                              .addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 2, false, false, false));
                           ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                              .addEffect(new MobEffectInstance(EFNMobEffectRegistry.GRADUAL_HEAL, 100, 10, false, false, false));
                           ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                              .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1, false, false, false));
                           ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                              .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1, false, false, false));
                           ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                              .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 1, false, false, false));
                        }

                        skill.setStackSynchronize(weaponSkillContainer, newCharge);
                     }

                     this.currentNeutralizeEntities.clear();
                  }
               }
            }
         );
         com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
            EventType.PLAYER_KILLED_EVENT,
            KILL_EVENT_UUID,
            event -> {
               SkillContainer weaponSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(SkillSlots.WEAPON_INNATE);
               if (weaponSkillContainer != null && event.getDamageSource().getEntity() != null) {
                  ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)event.getPlayerPatch();
                  ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
                  LivingEntity target = event.getKilledEntity();
                  Skill skill = weaponSkillContainer.getSkill();
                  if (skill != null) {
                     int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
                     int currentStack = weaponSkillContainer.getStack();
                     int maxStack = skill.getMaxStack();
                     int newCharge = Math.min(maxStack, currentStack + this.skillKillStackReward);
                     boolean isSkillKill = this.isSkillAnimationKill(event);
                     if (isSkillKill) {
                        target.removeEffect(EFNMobEffectRegistry.STOP);
                        ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                           .addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 2, false, false, false));
                        ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                           .addEffect(new MobEffectInstance(EFNMobEffectRegistry.GRADUAL_HEAL, 100, 10, false, false, false));
                        ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                           .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1, false, false, false));
                        ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                           .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1, false, false, false));
                        ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                           .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 1, false, false, false));
                        skill.setStackSynchronize(weaponSkillContainer, newCharge);
                        if (!serverPlayer.level().isClientSide()) {
                           SoulAfterimagePacket packet = new SoulAfterimagePacket(target);
                           EFNNetworkHandler.sendToAllPlayersTrackingEntity(target, packet);
                        }
                     }

                     if (currentCooldown > 0) {
                        int reductionAmount = isSkillKill ? this.skillKillCooldownReduction : this.killCooldownReduction;
                        int newCooldown = Math.max(0, currentCooldown - reductionAmount);
                        int orbCount = isSkillKill ? 3 : 1;
                        container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, newCooldown);

                        for (int i = 0; i < orbCount; i++) {
                           SoulHuntOrb soulHuntOrb = getSoulHuntOrb(i, serverPlayer, target);
                           serverPlayer.level().addFreshEntity(soulHuntOrb);
                           ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                              .addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 0, false, false, false));
                           ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                              .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false, false));
                        }
                     }
                  }
               }
            }
         );
      }
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.innateSkill(container.getExecutor(), itemstack) != null;
   }

   private List<LivingEntity> findNeutralizeEntities(PlayerPatch<?> playerPatch) {
      LivingEntity player = (LivingEntity)playerPatch.getOriginal();
      float neutralizeDetectionRange = 10.0F;
      AABB detectionArea = player.getBoundingBox().inflate(neutralizeDetectionRange);
      List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, detectionArea, entityx -> entityx != player && entityx.isAlive());
      List<LivingEntity> neutralizeEntities = new ArrayList<>();

      for (LivingEntity entity : nearbyEntities) {
         LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (entityPatch != null) {
            AssetAccessor<? extends StaticAnimation> hitAnimation = entityPatch.getHitAnimation(StunType.NEUTRALIZE);
            if (hitAnimation != null && this.isEntityInNeutralizeState(entityPatch)) {
               neutralizeEntities.add(entity);
            }
         }
      }

      return neutralizeEntities;
   }

   private boolean isTargetInNeutralizeList(LivingEntity target) {
      for (LivingEntity entity : this.currentNeutralizeEntities) {
         if (entity == target) {
            return true;
         }
      }

      return false;
   }

   private boolean isEntityInNeutralizeState(LivingEntityPatch<?> entityPatch) {
      AnimationPlayer animPlayer = entityPatch.getAnimator().getPlayerFor(null);
      if (animPlayer != null) {
         DynamicAnimation animation = animPlayer.getAnimation().orElse(null);
         AssetAccessor<? extends StaticAnimation> neutralizeAnimationAccessor = entityPatch.getHitAnimation(StunType.NEUTRALIZE);
         StaticAnimation neutralizeAnimation = neutralizeAnimationAccessor != null ? neutralizeAnimationAccessor.orElse(null) : null;
         if (animation != null && neutralizeAnimation != null) {
            ResourceLocation currentAnimationId = animation.getRegistryName();
            ResourceLocation neutralizeAnimationId = neutralizeAnimation.getRegistryName();
            return currentAnimationId != null && currentAnimationId.equals(neutralizeAnimationId);
         }
      }

      return false;
   }

   private boolean isSkillAnimationKill(PlayerKilledEvent event) {
      if (!(event.getDamageSource() instanceof EpicFightDamageSource epicDamageSource)) {
         return false;
      } else {
         StaticAnimation skillAnimation = (StaticAnimation)EFNSkillAnimations.EXECUTION.get();
         StaticAnimation killingAnimation = epicDamageSource.getAnimation().orElse(null);
         return skillAnimation != null && skillAnimation.equals(killingAnimation);
      }
   }

   public void onRemoved(SkillContainer container) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, PARRY_EVENT_UUID, -1);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.DODGE_SUCCESS_EVENT, DODGE_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.PLAYER_KILLED_EVENT, KILL_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.DEAL_DAMAGE_EVENT_ATTACK, DAMAGE_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.ANIMATION_BEGIN_EVENT, ANIMATION_BEGIN_EVENT_UUID);
      this.currentNeutralizeEntities.clear();
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (container.getExecutor().isLogicalClient()) {
         this.handleKeyInput(container);
      }

      int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
      if (currentCooldown > 0) {
         container.getDataManager().setData(EFNSKillDataKeys.COOLDOWN, currentCooldown - 1);
      }
   }

   private void handleKeyInput(SkillContainer container) {
      boolean isArtsKeyDown = EFNKeyMappings.EFN_ARTS.isDown();
      boolean shouldCast = isArtsKeyDown && !this.wasArtsKeyDown;
      this.wasArtsKeyDown = isArtsKeyDown;
      if (shouldCast && this.canExecute(container)) {
         CPSkillRequest packet = new CPSkillRequest((SkillSlot)SkillSlot.ENUM_MANAGER.get(container.getSlot().universalOrdinal()), WorkType.CAST);
         EpicFightNetworkManager.sendToServer(packet);
      }
   }

   public void executeOnServer(SkillContainer container, CompoundTag args) {
      super.executeOnServer(container, args);
      container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, this.cooldown);
      container.getServerExecutor().playAnimationSynchronized(EFNSkillAnimations.EXECUTION, 0.1F);

      for (LivingEntity entity : this.findNeutralizeEntities(container.getExecutor())) {
         entity.addEffect(new MobEffectInstance(EFNMobEffectRegistry.STOP, 100, 0, false, false, false));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDraw(SkillContainer container) {
      return (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN) > 0;
   }

   @OnlyIn(Dist.CLIENT)
   public List<Object> getTooltipArgsOfScreen(List<Object> list) {
      list.add(EFNKeyMappings.EFN_ARTS.getTranslatedKeyMessage());
      list.add(this.cooldown / 20.0);
      list.add(this.killCooldownReduction / 20.0);
      list.add(this.skillKillCooldownReduction / 20.0);
      list.add(this.skillKillStackReward);
      list.add(this.neutralizeTargetHitDamageMultiplier);
      list.add(this.neutralizeTargetHitImpactMultiplier);
      list.add(this.neutralizeTargetHitCooldownReduction / 20.0);
      list.add(this.neutralizeTargetHitStackReward);
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
      int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
      if (currentCooldown > 0) {
         guiGraphics.drawString(gui.getFont(), String.format("%.1f", currentCooldown / 20.0), x + 2.0F, y + 8.0F, 16777215, true);
      }

      poseStack.popPose();
   }

   public static class Builder extends SkillBuilder<ExecuteSkill.Builder> {
      public Builder() {
         super(ExecuteSkill::new);
      }
   }
}
