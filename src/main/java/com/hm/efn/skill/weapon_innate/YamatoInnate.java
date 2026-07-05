package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.client.input.keymapping.EFNKeyMappings;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.entity.effect.BlastSummonedSwordEntity;
import com.hm.efn.entity.effect.HeavyRainSwordEntity;
import com.hm.efn.entity.effect.SummonedSwordEntity_In;
import com.hm.efn.entity.effect.SummonedSwordEntity_Out;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.p1nero.invincible.api.combo.ComboNode;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.PlayerInputState;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class YamatoInnate extends EFNWeaponInnateBase {
   private static final int QUICK_THRESHOLD = 7;
   private static final int JUST_THRESHOLD = 13;
   private static final int MAX_CHARGE_TICKS = 210;
   private static final UUID JUDGECUT_LISTENER_UUID = UUID.fromString("d34db35f-5a2d-4b3a-9f5c-1919810c2d1a");
   private static final UUID ANIMATION_INTERRUPT_LISTENER_UUID = UUID.fromString("d34db23f-5a1d-4b3a-9f2c-1919810c2d1a");
   private static final UUID DAMAGE_LISTENER_UUID = UUID.fromString("d34db23f-5a1d-4b3a-9f3c-1919810c2d1a");
   private boolean hasAutoTriggered = false;
   private boolean needenchantment_summon_sword;
   private boolean needenchantment_doppelganger;

   public YamatoInnate(Builder builder) {
      super(builder);
   }

   private static AnimationAccessor<? extends AvalonAttackAnimation> judgeCutAnimation() {
      return EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL;
   }

   private static AnimationAccessor<? extends AvalonAttackAnimation> quickAnimation() {
      return EFNYamatoAnimations.YAMATO_JUDEMENCUT;
   }

   private static AnimationAccessor<? extends AvalonAttackAnimation> justAnimation() {
      return EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST;
   }

   private static AnimationAccessor<? extends AvalonAttackAnimation> chargeAnimation() {
      return EFNYamatoAnimations.YAMATO_JUDEMENCUT_CHARGE;
   }

   private static boolean isAnimation(StaticAnimation animation, AnimationAccessor<? extends StaticAnimation> accessor) {
      if (animation == null || accessor == null) {
         return false;
      }

      StaticAnimation resolved = accessor.get();
      return animation == resolved || animation.getRealAnimation().equals(accessor);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.needenchantment_summon_sword = parameters.getBoolean("needenchantment_summon_sword");
      this.needenchantment_doppelganger = parameters.getBoolean("needenchantment_doppelganger");
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      SkillDataManager data = container.getDataManager();
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, JUDGECUT_LISTENER_UUID, event -> {
         if (isAnimation(event.getAnimation(), judgeCutAnimation())) {
            data.setDataSync(EFNSKillDataKeys.IS_CHARGING, true);
            data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
            this.hasAutoTriggered = false;
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.ANIMATION_BEGIN_EVENT,
            ANIMATION_INTERRUPT_LISTENER_UUID,
            event -> {
               if ((Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_CHARGING)
                  && !this.isChargeRelatedAnimation(event.getAnimation())) {
                  this.cancelCharging(container);
               }
            }
         );
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_HURT, DAMAGE_LISTENER_UUID, event -> {
         if (event.getDamageSource().getAnimation().equals(EFNYamatoAnimations.YAMATO_AIRFLUSH)) {
            LivingEntity target = event.getTarget();
            int skillStack = container.getStack();
            boolean isCreative = ((Player)container.getExecutor().getOriginal()).isCreative();
            if ((skillStack >= 1 || isCreative) && target != null) {
               SummonedSwordEntity_In.summonAtTargetWaist(container.getServerExecutor(), target, new Vec3(0.0, 0.0, 0.0), 1.6F);
               container.getSkill().setStackSynchronize(container, skillStack - 1);
               target.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 2));
            }
         }

         if (event.getDamageSource().getAnimation().equals(EFNYamatoAnimations.YAMATO_KILLERBEE)) {
            boolean isDemon = (Boolean)data.getDataValue(EFNSKillDataKeys.DEMON_KEY);
            if (!isDemon) {
               ((ServerPlayerPatch)event.getPlayerPatch()).playAnimationSynchronized(EFNYamatoAnimations.YAMATO_KILLERBEE_HIT, 0.0F);
               float currentStamina = ((ServerPlayerPatch)event.getPlayerPatch()).getStamina();
               ((ServerPlayerPatch)event.getPlayerPatch()).setStamina(currentStamina + 6.0F);
               this.updateComboNode((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal(), Yamato.Yamato_root);
            }
         }
      });
   }

   private boolean isChargeRelatedAnimation(StaticAnimation animation) {
      return isAnimation(animation, judgeCutAnimation())
         || isAnimation(animation, quickAnimation())
         || isAnimation(animation, justAnimation())
         || isAnimation(animation, chargeAnimation());
   }

   private void cancelCharging(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      if (data.hasData(EFNSKillDataKeys.IS_CHARGING) && data.hasData(EFNSKillDataKeys.IS_PRESSING)) {
         data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
         data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, 0);
         if (container.getExecutor() instanceof ServerPlayerPatch serverPatch && data.hasData(EFNSKillDataKeys.IS_PRESSING)) {
            data.setDataSync(EFNSKillDataKeys.IS_PRESSING, false);
         }

         this.hasAutoTriggered = false;
      }
   }

   public void onRemoved(SkillContainer container) {
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, JUDGECUT_LISTENER_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ANIMATION_BEGIN_EVENT, ANIMATION_INTERRUPT_LISTENER_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_HURT, DAMAGE_LISTENER_UUID);
      super.onRemoved(container);
   }

   public void updateContainer(SkillContainer container) {
      Skill skill = container.getSkill();
      if (container.getStack() >= skill.getMaxStack() && container.getResource() > 0.0F && !container.getExecutor().isLogicalClient()) {
         skill.setStackSynchronize(container, skill.getMaxStack() - 1);
      }

      int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
      if (currentCooldown > 0) {
         container.getDataManager().setData(EFNSKillDataKeys.COOLDOWN, currentCooldown - 1);
      }

      int currentCooldown_1 = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN_1);
      if (currentCooldown_1 > 0) {
         container.getDataManager().setData(EFNSKillDataKeys.COOLDOWN_1, currentCooldown_1 - 1);
      }

      int currentCooldown_2 = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN_2);
      if (currentCooldown_2 > 0) {
         container.getDataManager().setData(EFNSKillDataKeys.COOLDOWN_2, currentCooldown_2 - 1);
      }

      if (container.getExecutor().isLogicalClient()) {
         this.handleClientInput(container);
      } else {
         this.validateServerState(container);
         this.handleServerSummonSword(container);
         this.handleServerDoppelganger(container);
      }

      super.updateContainer(container);
   }

   private void handleServerSummonSword(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      Player player = (Player)container.getExecutor().getOriginal();
      ItemStack mainHandItem = player.getMainHandItem();
      boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_SUMMONED_SWORD) > 0;
      boolean canSummonSword = player.isCreative() || !this.needenchantment_summon_sword || hasEnchantment;
      if ((Boolean)data.getDataValue(EFNSKillDataKeys.SUMMON_SWORD) && canSummonSword) {
         if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
            float maxStamina = serverPlayerPatch.getMaxStamina();
            float staminaCost = maxStamina * 0.05F;
            float currentStamina = serverPlayerPatch.getStamina();
            boolean isCreative = ((Player)container.getExecutor().getOriginal()).isCreative();
            if (!isCreative) {
               if (currentStamina < staminaCost) {
                  data.setDataSync(EFNSKillDataKeys.SUMMON_SWORD, false);
                  return;
               }

               serverPlayerPatch.setStamina(currentStamina - staminaCost);
               serverPlayerPatch.setStaminaRegenAwaitTicks(15);
            }
         }

         boolean isAngel = (Boolean)data.getDataValue(EFNSKillDataKeys.ANGEL_KEY);
         boolean isDemon = (Boolean)data.getDataValue(EFNSKillDataKeys.DEMON_KEY);
         boolean isUp = (Boolean)data.getDataValue(EFNSKillDataKeys.UP_KEY);
         boolean isDown = (Boolean)data.getDataValue(EFNSKillDataKeys.DOWN_KEY);
         boolean isSneak = (Boolean)data.getDataValue(EFNSKillDataKeys.SNEAK_KEY);
         boolean hasHeavyRainEnchant = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_HEAVY_RAIN) > 0;
         SinSummonedSwordEntity.summon(container.getServerExecutor(), isAngel, isDemon);
         LivingEntity target = container.getServerExecutor().getTarget();
         int skillStack = container.getStack();
         boolean isCreative = ((Player)container.getExecutor().getOriginal()).isCreative();
         boolean inCoolDown = (Integer)data.getDataValue(EFNSKillDataKeys.COOLDOWN) > 0;
         boolean inCoolDown_1 = (Integer)data.getDataValue(EFNSKillDataKeys.COOLDOWN_1) > 0;
         boolean inCoolDown_2 = (Integer)data.getDataValue(EFNSKillDataKeys.COOLDOWN_2) > 0;
         if (isUp && (skillStack >= 1 || isCreative) && !inCoolDown_1) {
            BlastSummonedSwordEntity.summon(
               ((ServerPlayer)container.getServerExecutor().getOriginal()).level(), (LivingEntity)container.getServerExecutor().getOriginal()
            );
            container.getExecutor().playSound(SoundEvents.TRIDENT_RETURN, 1.5F, 1.0F, 1.0F);
            container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN_1, 20);
            if (!isCreative) {
               container.getSkill().setStackSynchronize(container, --skillStack);
            }
         }

         if (isDown && (skillStack >= 1 || isCreative) && target != null) {
            SummonedSwordEntity_In.summonAtTargetWaist(container.getServerExecutor(), target, new Vec3(0.0, 0.0, 0.0), 1.6F);
            if (!isCreative) {
               container.getSkill().setStackSynchronize(container, --skillStack);
            }

            container.getExecutor().playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 5));
         }

         if (isSneak) {
            if (hasHeavyRainEnchant) {
               if ((skillStack >= 3 || isCreative) && !inCoolDown_2) {
                  container.getExecutor().playSound(SoundEvents.TRIDENT_RETURN, 1.5F, 1.0F, 1.0F);
                  if (target != null) {
                     HeavyRainSwordEntity.summon(
                        ((ServerPlayer)container.getServerExecutor().getOriginal()).level(),
                        (LivingEntity)container.getServerExecutor().getOriginal(),
                        target
                     );
                     target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 255));
                  } else {
                     HeavyRainSwordEntity.summon(
                        ((ServerPlayer)container.getServerExecutor().getOriginal()).level(), (LivingEntity)container.getServerExecutor().getOriginal()
                     );
                  }

                  if (!isCreative) {
                     skillStack -= 3;
                     container.getSkill().setStackSynchronize(container, skillStack);
                  }

                  container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN_2, 30);
               }
            } else if ((skillStack >= 5 || isCreative) && !inCoolDown) {
               if (!isCreative) {
                  skillStack -= 5;
                  container.getSkill().setStackSynchronize(container, skillStack);
               }

               container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, 600);
               container.getExecutor().playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F);
               SummonedSwordEntity_Out.summonAtWaist(container.getServerExecutor(), new Vec3(0.0, 0.0, 0.0), 1.6F);
            }
         }

         data.setDataSync(EFNSKillDataKeys.SUMMON_SWORD, false);
      }
   }

   private void handleServerDoppelganger(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      Player player = (Player)container.getExecutor().getOriginal();
      ItemStack mainHandItem = player.getMainHandItem();
      boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_DOPPELGANGER) > 0;
      boolean canSummonDoppelganger = player.isCreative() || !this.needenchantment_doppelganger || hasEnchantment;
      boolean intentToHaveOut = (Boolean)data.getDataValue(EFNSKillDataKeys.DOPPELGANGER);
      boolean isOut = (Boolean)data.getDataValue(EFNSKillDataKeys.HAVE_DOPPELGANGER);
      if (intentToHaveOut && !isOut) {
         boolean condition = container.getStack() >= 1 || player.isCreative();
         if (canSummonDoppelganger && condition) {
            boolean isSneaking = (Boolean)data.getDataValue(EFNSKillDataKeys.SNEAK_KEY) || player.isShiftKeyDown() || player.isCrouching();
            DoppelgangerEntity.summon(container.getServerExecutor(), isSneaking);
            data.setDataSync(EFNSKillDataKeys.HAVE_DOPPELGANGER, true);
         } else {
            data.setDataSync(EFNSKillDataKeys.DOPPELGANGER, false);
         }
      } else if (!intentToHaveOut && isOut) {
         this.destroy(container);
         data.setDataSync(EFNSKillDataKeys.HAVE_DOPPELGANGER, false);
      }

      if ((Boolean)data.getDataValue(EFNSKillDataKeys.HAVE_DOPPELGANGER)) {
         if (player.tickCount % 20 == 0 && !this.actuallyHasDoppelganger(container)) {
            data.setDataSync(EFNSKillDataKeys.HAVE_DOPPELGANGER, false);
            data.setDataSync(EFNSKillDataKeys.DOPPELGANGER, false);
            return;
         }

         float consume = container.getMaxResource() / 60.0F;
         if (container.getResource() <= 0.0F) {
            Skill skill = container.getSkill();
            skill.setStackSynchronize(container, container.getStack());
            if (container.getStack() <= 0 && !player.isCreative()) {
               this.destroy(container);
               data.setDataSync(EFNSKillDataKeys.HAVE_DOPPELGANGER, false);
               data.setDataSync(EFNSKillDataKeys.DOPPELGANGER, false);
               return;
            }

            container.setResource(container.getMaxResource() - 0.01F);
         } else if (!player.isCreative()) {
            container.setResource(Math.max(0.0F, container.getResource() - consume));
         }
      }
   }

   private boolean actuallyHasDoppelganger(SkillContainer container) {
      Level level = ((Player)container.getExecutor().getOriginal()).level();
      double range = 30.0;

      for (Entity entity : level.getEntities(
         container.getExecutor().getOriginal(),
         ((Player)container.getExecutor().getOriginal()).getBoundingBox().inflate(range),
         entityx -> entityx instanceof DoppelgangerEntity
      )) {
         DoppelgangerEntity doppelganger = (DoppelgangerEntity)entity;
         if (doppelganger.getOwner() != null && doppelganger.getOwner().equals(container.getExecutor().getOriginal())) {
            return true;
         }
      }

      return false;
   }

   public void destroy(SkillContainer container) {
      Level level = ((Player)container.getExecutor().getOriginal()).level();
      double range = 30.0;

      for (Entity entity : level.getEntities(
         container.getExecutor().getOriginal(),
         ((Player)container.getExecutor().getOriginal()).getBoundingBox().inflate(range),
         entityx -> entityx instanceof DoppelgangerEntity
      )) {
         DoppelgangerEntity doppelganger = (DoppelgangerEntity)entity;
         if (doppelganger.getOwner() != null && doppelganger.getOwner().equals(container.getExecutor().getOriginal())) {
            container.getServerExecutor().playSound((SoundEvent)EFNSounds.DOPPELGANGER_CLOSE.get(), 1.0F, 1.0F, 1.0F);
            DoppelgangerEntity.spawnDarkParticles(((ServerPlayer)container.getServerExecutor().getOriginal()).serverLevel(), entity.position());
            entity.discard();
         }
      }
   }

   private void handleClientInput(SkillContainer container) {
      LocalPlayer localPlayer = (LocalPlayer)container.getClientExecutor().getOriginal();
      SkillDataManager data = container.getDataManager();
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      PlayerInputState inputState = InputManager.getInputState(localPlayer);
      CapabilityItem itemCapability = EpicFightCapabilities.getItemStackCapability(itemstack);
      if (itemCapability != null && itemCapability.getInnateSkill(container.getExecutor(), itemstack) == this) {
         if (Minecraft.getInstance().options.keyJump.isDown()
            && localPlayer.onGround()
            && !localPlayer.isInWater()
            && !((DynamicAnimation)Objects.requireNonNull(container.getExecutor().getAnimator().getPlayerFor(null)).getAnimation().get())
               .getRealAnimation()
               .equals(EFNYamatoAnimations.YAMATO_REPAIDSLASH)
            && container.getExecutor().getEntityState().inaction()
            && container.getExecutor().getEntityState().canBasicAttack()) {
            inputState = inputState.withJumping(true);
            localPlayer.setJumping(false);
            localPlayer.jumpFromGround();
            InputManager.setInputState(inputState);
            AssetAccessor<? extends StaticAnimation> jumpAnimation = container.getClientExecutor().getClientAnimator().getJumpAnimation();
            container.getClientExecutor().playAnimationInClientSide(jumpAnimation, 0.0F);
         }

         if ((Boolean)data.getDataValue(EFNSKillDataKeys.IS_CHARGING)) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            boolean isLeftMouseDown = GLFW.glfwGetMouseButton(windowHandle, 0) == 1;
            if (!isLeftMouseDown) {
               int chargeTime = (Integer)data.getDataValue(EFNSKillDataKeys.CHARGE_TICKS);
               this.releaseAttack(container, chargeTime);
               data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
            }
         }

         while (EFNKeyMappings.DOPPELGANGER.consumeClick()) {
            if ((Boolean)data.getDataValue(EFNSKillDataKeys.HAVE_DOPPELGANGER) && localPlayer.isShiftKeyDown()) {
               int current = (Integer)data.getDataValue(EFNSKillDataKeys.DOPPELGANGER_STYLE);
               data.setDataSync(EFNSKillDataKeys.DOPPELGANGER_STYLE, current == 0 ? 1 : 0);
            } else {
               boolean currentIntent = (Boolean)data.getDataValue(EFNSKillDataKeys.DOPPELGANGER);
               data.setDataSync(EFNSKillDataKeys.DOPPELGANGER, !currentIntent);
            }
         }

         while (EFNKeyMappings.DOPPELGANGER_DELAY.consumeClick()) {
            if ((Boolean)data.getDataValue(EFNSKillDataKeys.HAVE_DOPPELGANGER)) {
               boolean currentDelay = (Boolean)data.getDataValue(EFNSKillDataKeys.DOPPELGANGER_DELAY);
               data.setDataSync(EFNSKillDataKeys.DOPPELGANGER_DELAY, !currentDelay);
               container.getExecutor().playSound((SoundEvent)EFNSounds.DOPPELGANGER_SWITCH.get(), 1.0F, 1.0F, 1.0F);
            }
         }
      }
   }

   private void releaseAttack(SkillContainer container, int chargeTime) {
      PlayerPatch<?> executor = container.getExecutor();
      float currentStamina = executor.getStamina();
      float MaxStamina = executor.getMaxStamina();
      if (chargeTime <= 7) {
         AnimationAccessor<? extends AvalonAttackAnimation> animation = quickAnimation();
         if (animation == null) {
            return;
         }

         executor.playAnimationSynchronized(animation, 0.05F);
         executor.setStamina(Math.max(0.0F, currentStamina - 0.1F * MaxStamina));
         executor.playSound((SoundEvent)EFNSounds.JUDGEMENTCUT.get(), 0.6F, 0.0F, 0.0F);
      } else if (chargeTime <= 13) {
         AnimationAccessor<? extends AvalonAttackAnimation> animation = justAnimation();
         if (animation == null) {
            return;
         }

         executor.playAnimationSynchronized(animation, 0.05F);
         executor.setStamina(Math.min(currentStamina + 0.5F * MaxStamina, MaxStamina));
         executor.getSkill(this).setStack(executor.getSkill(this).getStack() + 1);
         executor.playSound((SoundEvent)EFNSounds.JUDGEMENTCUT.get(), 0.6F, 0.0F, 0.0F);
      } else {
         AnimationAccessor<? extends AvalonAttackAnimation> animation = chargeAnimation();
         if (animation == null) {
            return;
         }

         executor.playAnimationSynchronized(animation, 0.05F);
         executor.setStamina(Math.min(currentStamina + 0.3F * MaxStamina, MaxStamina));
         executor.playSound((SoundEvent)EFNSounds.JUDGEMENTCUT.get(), 0.6F, 0.0F, 0.0F);
      }
   }

   private void validateServerState(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      if ((Boolean)data.getDataValue(EFNSKillDataKeys.IS_CHARGING)) {
         int currentTime = (Integer)data.getDataValue(EFNSKillDataKeys.CHARGE_TICKS);
         int newTime = Math.min(currentTime + 1, 210);
         data.setDataSync(EFNSKillDataKeys.CHARGE_TICKS, newTime);
         if (newTime >= 210) {
            data.setDataSync(EFNSKillDataKeys.IS_CHARGING, false);
            this.hasAutoTriggered = true;
         }
      }
   }

   private void updateComboNode(ServerPlayer player, ComboNode node) {
      InvincibleAttachments.getPlayer(player).setCurrentNode(node);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      list.add(
         Component.translatable("skill.efn.yamato.tooltip1")
            .withStyle(ChatFormatting.BOLD)
            .append("A·")
            .append(InvincibleKeyMappings.KEY1.getTranslatedKeyMessage())
            .append(" ")
            .append("S·")
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.yamato.tooltip2").withStyle(ChatFormatting.GRAY));
      list.add(Component.translatable("skill.efn.yamato.tooltip3").withStyle(ChatFormatting.GRAY));
      list.add(
         Component.translatable("skill.efn.yamato.tooltip4")
            .withStyle(ChatFormatting.AQUA)
            .withStyle(ChatFormatting.UNDERLINE)
            .append(EFNKeyMappings.ANGEL.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.yamato.tooltip5").withStyle(ChatFormatting.GRAY));
      list.add(Component.translatable("skill.efn.yamato.tooltip6").withStyle(ChatFormatting.GRAY));
      list.add(
         Component.translatable("skill.efn.yamato.tooltip7")
            .withStyle(ChatFormatting.RED)
            .withStyle(ChatFormatting.UNDERLINE)
            .append(EFNKeyMappings.DEMON.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.yamato.tooltip8").withStyle(ChatFormatting.GRAY));
      list.add(Component.translatable("skill.efn.yamato.tooltip9").withStyle(ChatFormatting.GRAY));
      list.add(Component.translatable("skill.efn.yamato.tooltip10").withStyle(ChatFormatting.BOLD));
      list.add(
         Component.translatable("skill.efn.yamato.tooltip14")
            .withStyle(ChatFormatting.AQUA)
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.UNDERLINE)
            .append(EFNKeyMappings.EFN_ARTS.getTranslatedKeyMessage())
      );
      list.add(
         Component.translatable("skill.efn.yamato.tooltip11")
            .withStyle(ChatFormatting.BLUE)
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.UNDERLINE)
            .append(EFNKeyMappings.SUMMONED_SWORD.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.yamato.tooltip12").withStyle(ChatFormatting.GRAY));
      list.add(
         Component.translatable("skill.efn.yamato.tooltip13")
            .withStyle(ChatFormatting.GRAY)
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.UNDERLINE)
            .append(EFNKeyMappings.DOPPELGANGER.getTranslatedKeyMessage())
      );
      return list;
   }
}
