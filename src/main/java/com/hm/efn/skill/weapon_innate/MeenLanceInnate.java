package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.event.DuskFireArmorHelper;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNLanceAnimations;
import com.hm.efn.gameasset.combos.Meenlance;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.modules.HoldableSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class MeenLanceInnate extends EFNWeaponInnateBase {
   public static final int MAX_CHARGING_TICKS = 60;
   public static final int MIN_CHARGING_TICKS = 13;
   private static final int CONSUMPTION_TICK_INTERVAL = 20;
   private static final UUID ANIMATION_EVENT_UUID = UUID.fromString("d34db33f-5a1d-4b3a-9f1c-8e3a7b114514");
   private static final UUID ENERGY_CHECK_UUID = UUID.fromString("d34db33f-5a1d-4b3a-9f1c-8e1145142d1a");
   private static final UUID DISABLE_GUARD_UUID = UUID.fromString("d34db33f-5a1d-4b3a-9f1c-8e2245142d1a");
   private static final UUID ANIMATION_INTERRUPT_LISTENER_UUID = UUID.fromString("d34db23f-5a1d-4b3a-9f2c-1415810c2d1a");
   private boolean hasAutoTriggered = false;
   private int consumptionTickCounter = 0;

   private static AnimationAccessor<? extends StaticAnimation> chargingAnimation() {
      return EFNLanceAnimations.NF_MEEN_CHARGING;
   }

   private static AnimationAccessor<? extends AttackAnimation> fullChargeAnimation() {
      return EFNLanceAnimations.NF_MEEN_CHARGE3;
   }

   private static AnimationAccessor<? extends AttackAnimation> midChargeAnimation() {
      return EFNLanceAnimations.NF_MEEN_CHARGE2;
   }

   private static AnimationAccessor<? extends AttackAnimation> lowChargeAnimation() {
      return EFNLanceAnimations.NF_MEEN_CHARGE1;
   }

   private static AnimationAccessor<? extends AttackAnimation> minChargeAnimation() {
      return EFNLanceAnimations.NF_MEEN_AUTO3;
   }

   public MeenLanceInnate(Builder builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.ATTACK_PHASE_END_EVENT,
            ANIMATION_EVENT_UUID,
            event -> {
               StaticAnimation animation = (StaticAnimation)event.getAnimation().get();
               PlayerPatch<?> executor = container.getExecutor();
               SkillDataManager dataManager = container.getDataManager();
               if (this.isAnimation(animation, fullChargeAnimation())
                  || this.isAnimation(animation, midChargeAnimation())
                  || this.isAnimation(animation, lowChargeAnimation())
                  || this.isAnimation(animation, minChargeAnimation())) {
                  if (this.isAnimation(animation, minChargeAnimation()) && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.IS_CHARGING)) {
                     return;
                  }

                  this.resetChargeState(dataManager);
                  if (executor instanceof ServerPlayerPatch serverPatch) {
                     if (((ServerPlayer)serverPatch.getOriginal()).isCreative()) {
                        return;
                     }

                     int cost = 0;
                     if (this.isAnimation(animation, fullChargeAnimation())) {
                        cost = 10;
                     } else if (this.isAnimation(animation, midChargeAnimation())) {
                        cost = 5;
                     } else if (this.isAnimation(animation, lowChargeAnimation())) {
                        cost = 3;
                     }

                     if (cost > 0) {
                        SkillContainer weaponInnate = serverPatch.getSkill(Meenlance.Meenlance);
                        if (weaponInnate != null && weaponInnate.getStack() >= cost) {
                           weaponInnate.getSkill().setStackSynchronize(weaponInnate, weaponInnate.getStack() - cost);
                        }
                     }
                  }
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.ACTION_EVENT_SERVER, ENERGY_CHECK_UUID, event -> {
         if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_PRESSING)) {
            if (container.getExecutor() instanceof ServerPlayerPatch serverPatch && ((ServerPlayer)serverPatch.getOriginal()).isCreative()) {
               return;
            }

            SkillContainer weaponInnate = container.getExecutor().getSkill(Meenlance.Meenlance);
            if (weaponInnate == null) {
               container.getDataManager().setData(EFNSKillDataKeys.IS_PRESSING, false);
               this.hasAutoTriggered = true;
            }
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.SKILL_CAST_EVENT,
            DISABLE_GUARD_UUID,
            event -> {
               if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_CHARGING)
                  && EFNSkillChecks.skill(event.getSkillContainer()) instanceof HoldableSkill) {
                  event.cancel();
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.ANIMATION_BEGIN_EVENT,
            ANIMATION_INTERRUPT_LISTENER_UUID,
            event -> {
               if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_CHARGING)
                  && !this.isChargeRelatedAnimation(event.getAnimation())) {
                  this.resetChargeState(container.getDataManager());
               }
            }
         );
   }

   public void onRemoved(SkillContainer container) {
      this.hasAutoTriggered = false;
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ATTACK_PHASE_END_EVENT, ANIMATION_EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ACTION_EVENT_SERVER, ENERGY_CHECK_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, DISABLE_GUARD_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, ANIMATION_INTERRUPT_LISTENER_UUID);
      super.onRemoved(container);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().isLogicalClient() && container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
         ServerPlayer serverPlayer = (ServerPlayer)serverPlayerPatch.getOriginal();
         boolean wearingFullSet = DuskFireArmorHelper.isWearingFullDuskFireArmor(serverPlayer);
         if (wearingFullSet) {
            this.consumptionTickCounter++;
            if (this.consumptionTickCounter >= 20) {
               this.consumptionTickCounter = 0;
               float currentConsumption = container.getResource();
               float maxConsumption = container.getMaxResource();
               float newConsumption = Math.min(currentConsumption + 5.0F, maxConsumption);
               if (newConsumption > currentConsumption) {
                  this.setConsumptionSynchronize(container, newConsumption);
               }
            }
         } else if (this.consumptionTickCounter > 0) {
            this.consumptionTickCounter = 0;
         }
      }

      if (!this.isPlayingFullChargeAnimation(container)) {
         if (container.getExecutor().isLogicalClient()) {
            if (container.getExecutor().getEntityState().canBasicAttack()) {
               this.handleClientInput(container);
            }
         } else {
            this.validateServerState(container);
         }
      }
   }

   private boolean isPlayingFullChargeAnimation(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      AnimationAccessor<? extends AttackAnimation> fullCharge = fullChargeAnimation();
      if (fullCharge == null) {
         return false;
      }

      AnimationPlayer animPlayer = executor.getAnimator().getPlayerFor(fullCharge);
      return animPlayer != null && animPlayer.getAnimation() == fullCharge.get();
   }

   private void handleClientInput(SkillContainer container) {
      LocalPlayer localPlayer = (LocalPlayer)container.getClientExecutor().getOriginal();
      SkillDataManager dataManager = container.getDataManager();
      if (!localPlayer.level().isClientSide || Minecraft.getInstance().screen == null) {
         if (!((DynamicAnimation)container.getClientExecutor().getClientAnimator().baseLayer.animationPlayer.getAnimation().get())
            .getRealAnimation()
            .equals(fullChargeAnimation())) {
            ItemStack heldItem = localPlayer.getMainHandItem();
            boolean isHoldingSpear = EFNSkillChecks.isInnateSkill(container.getExecutor(), heldItem, this);
            if (!isHoldingSpear && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.IS_CHARGING)) {
               this.resetChargeState(dataManager);
            } else {
               if (isHoldingSpear) {
                  boolean isKeyDown = false;
                  boolean wasPressing = (Boolean)dataManager.getDataValue(EFNSKillDataKeys.IS_PRESSING);
                  if (localPlayer.level().isClientSide) {
                     long windowHandle = Minecraft.getInstance().getWindow().getWindow();
                     boolean isRightMouseDown = GLFW.glfwGetMouseButton(windowHandle, 1) == 1;
                     boolean isLeftMouseDown = GLFW.glfwGetMouseButton(windowHandle, 0) == 1;
                     isKeyDown = isRightMouseDown && isLeftMouseDown;
                  }

                  if (isKeyDown != wasPressing) {
                     if (localPlayer.level().isClientSide) {
                        dataManager.setDataSync(EFNSKillDataKeys.IS_PRESSING, isKeyDown);
                     }

                     if (isKeyDown) {
                        SkillContainer weaponInnate = container.getExecutor().getSkill(SkillSlots.WEAPON_INNATE);
                        if (weaponInnate == null || weaponInnate.getStack() < 10 && !localPlayer.isCreative()) {
                           this.playAnimation(container.getExecutor(), minChargeAnimation(), -0.3F);
                        } else {
                           this.startCharging(container, localPlayer, dataManager);
                        }
                     } else {
                        int chargeTime = (Integer)dataManager.getDataValue(EFNSKillDataKeys.CHARGE_TICKS);
                        if (!this.hasAutoTriggered) {
                           this.releaseAttack(container, chargeTime, localPlayer);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isChargeRelatedAnimation(StaticAnimation animation) {
      return this.isAnimation(animation, chargingAnimation())
         || this.isAnimation(animation, fullChargeAnimation())
         || this.isAnimation(animation, midChargeAnimation())
         || this.isAnimation(animation, lowChargeAnimation());
   }

   private void startCharging(SkillContainer container, LocalPlayer localPlayer, SkillDataManager skillDataManager) {
      PlayerPatch<?> executor = container.getExecutor();
      skillDataManager.setDataSync(EFNSKillDataKeys.IS_CHARGING, true);
      skillDataManager.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
      SkillContainer skillContainer = container.getExecutor().getSkill(SkillSlots.GUARD);
      if (skillContainer != null && skillContainer.getSkill() instanceof HoldableSkill && skillContainer.isActivated()) {
         skillContainer.deactivate();
      }

      this.hasAutoTriggered = false;
      this.playAnimation(executor, chargingAnimation(), 0.0F);
   }

   private void releaseAttack(SkillContainer container, int chargeTime, LocalPlayer localPlayer) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillDataManager dataManager = container.getDataManager();
      if (chargeTime <= 13) {
         this.playAnimation(executor, minChargeAnimation(), -0.3F);
         this.resetChargeState(dataManager);
         dataManager.setDataSync(EFNSKillDataKeys.IS_PRESSING, false);
      } else {
         this.playAttackAnimation(container);
      }
   }

   private void resetChargeState(SkillDataManager data) {
      if (data.hasData(EFNSKillDataKeys.IS_CHARGING) && (Boolean)data.getDataValue(EFNSKillDataKeys.IS_CHARGING)) {
         data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
      }

      if (data.hasData(EFNSKillDataKeys.IS_PRESSING) && (Boolean)data.getDataValue(EFNSKillDataKeys.IS_PRESSING)) {
         data.setDataSync(EFNSKillDataKeys.IS_PRESSING, false);
      }

      data.setData(EFNSKillDataKeys.CHARGE_TICKS, 0);
      data.setData(EFNSKillDataKeys.CHARGE_STAGE, 0);
      this.hasAutoTriggered = true;
   }

   private void playAttackAnimation(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillDataManager skillDataManager = container.getDataManager();
      int chargeState = (Integer)skillDataManager.getDataValue(EFNSKillDataKeys.CHARGE_STAGE);
      if (chargeState >= 3) {
         this.playAnimation(executor, fullChargeAnimation(), -0.05F);
      } else if (chargeState == 2) {
         this.playAnimation(executor, midChargeAnimation(), -0.05F);
         executor.playSound(SoundEvents.TRIDENT_RIPTIDE_2, 120.0F, 0.0F, 0.0F);
      } else if (chargeState == 1) {
         this.playAnimation(executor, lowChargeAnimation(), -0.05F);
         executor.playSound(SoundEvents.TRIDENT_RIPTIDE_1, 120.0F, 0.0F, 0.0F);
      }

      this.resetChargeState(skillDataManager);
   }

   private void validateServerState(SkillContainer container) {
      ServerPlayerPatch serverExecutor = container.getServerExecutor();
      SkillDataManager skillDataManager = container.getDataManager();
      if ((Boolean)skillDataManager.getDataValue(EFNSKillDataKeys.IS_CHARGING)) {
         int currentTime = (Integer)skillDataManager.getDataValue(EFNSKillDataKeys.CHARGE_TICKS);
         int newTime = Math.min(currentTime + 1, 60);
         skillDataManager.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, newTime);
      }
   }

   private void playAnimation(PlayerPatch<?> executor, AnimationAccessor<? extends StaticAnimation> animation, float convertTime) {
      if (animation != null) {
         executor.playAnimationSynchronized(animation, convertTime);
      }
   }

   private boolean isAnimation(StaticAnimation animation, AnimationAccessor<? extends StaticAnimation> accessor) {
      return animation != null && accessor != null && (animation == accessor.get() || animation.getRealAnimation().equals(accessor));
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      list.add(Component.translatable("skill.efn.meenlance.tooltip").withStyle(ChatFormatting.GOLD));
      list.add(Component.translatable("skill.efn.meenlance.tooltip1").withStyle(ChatFormatting.GOLD));
      list.add(Component.translatable("skill.efn.meenlance.tooltip2"));
      list.add(Component.translatable("skill.efn.meenlance.tooltip3").withStyle(ChatFormatting.RED));
      list.add(Component.translatable("skill.efn.meenlance.tooltip4"));
      return list;
   }
}
