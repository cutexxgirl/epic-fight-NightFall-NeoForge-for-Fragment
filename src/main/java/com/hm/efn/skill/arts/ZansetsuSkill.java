package com.hm.efn.skill.arts;

import com.hm.efn.client.input.keymapping.EFNKeyMappings;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkillCategories;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Murasama;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EFNSkillChecks;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.network.client.CPSkillRequest.WorkType;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.registry.entries.EpicFightMobEffects;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class ZansetsuSkill extends PassiveSkill {
   private static final UUID SKILL_EXECUTE_UUID = UUID.fromString("a416c93a-42cb-22eb-b178-0721ac170003");
   private float consumptionRatio;
   private int durationPerStack;

   public ZansetsuSkill(ZansetsuSkill.Builder builder) {
      super(builder);
   }

   public static ZansetsuSkill.Builder createZansetsuBuilder() {
      return (ZansetsuSkill.Builder)new ZansetsuSkill.Builder()
         .setCategory(EFNSkillCategories.EFN_ZANSETSU)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.NONE);
   }

   public int getDurationPerStack() {
      return this.durationPerStack;
   }

   public float getConsumptionRatio() {
      return this.consumptionRatio;
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.consumptionRatio = parameters.contains("consumption_ratio") ? parameters.getFloat("consumption_ratio") : 0.5F;
      this.durationPerStack = parameters.contains("duration_per_stack") ? parameters.getInt("duration_per_stack") : 20;
      if (this.consumptionRatio < 0.0F || this.consumptionRatio > 1.0F) {
         this.consumptionRatio = 0.5F;
      }

      if (this.durationPerStack < 1) {
         this.durationPerStack = 20;
      }
   }

   public boolean canExecute(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      boolean isCreative = ((Player)executor.getOriginal()).isCreative();
      SkillContainer weaponInnateSkill = executor.getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnateSkill != null && weaponInnateSkill.getSkill() != null) {
         SkillContainer murasamaSkill = executor.getSkill(Murasama.Murasama);
         SkillContainer hfBladeSkill = executor.getSkill(HfBlade.HfBlade);
         boolean hasAnyValidSkill = murasamaSkill != null && murasamaSkill.hasSkill() || hfBladeSkill != null && hfBladeSkill.hasSkill();
         if (!hasAnyValidSkill) {
            return false;
         }

         AnimationPlayer animationPlayer = executor.getAnimator().getPlayerFor(null);
         AssetAccessor<? extends StaticAnimation> realAnim = animationPlayer != null ? animationPlayer.getRealAnimation() : null;
         if (realAnim != null
            && (
               realAnim.equals(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU_AIR)
                  || realAnim.equals(EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU)
                  || realAnim.equals(EFNHfBladeAnimations.HF_BLADE_ZANDATSU)
                  || realAnim.equals(EFNHfBladeAnimations.HF_BLADE_ZANDATSU_AIR)
            )) {
            return false;
         }

         if (!this.isHoldingWeapon(container)) {
            return false;
         }

         SkillDataManager innateDataManager = weaponInnateSkill.getDataManager();
         if (innateDataManager == null) {
            return false;
         }

         boolean isZansetsuActive = innateDataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
            && (Boolean)innateDataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
         return isZansetsuActive ? true : weaponInnateSkill.getStack() >= 1 || isCreative;
      } else {
         return false;
      }
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, Murasama.Murasama)
         || EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, HfBlade.HfBlade);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, SKILL_EXECUTE_UUID, event -> {
         if (event.getSkillContainer() == container && !event.isStateExecutable()) {
            event.setStateExecutable(true);
         }
      });
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, SKILL_EXECUTE_UUID);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (container.getExecutor().isLogicalClient()) {
         this.handleKeyInput(container);
      } else {
         this.handleStackConsumption(container);
      }

      if (container.getExecutor().isStunned()) {
         this.resetZansetsuStates(container.getDataManager());
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void handleKeyInput(SkillContainer container) {
      boolean isArtsKeyPressed = EFNKeyMappings.EFN_ARTS.isDown();
      boolean wasPressed = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.ARTS_KEY);
      if (isArtsKeyPressed != wasPressed) {
         container.getDataManager().setDataSync(EFNSKillDataKeys.ARTS_KEY, isArtsKeyPressed);
         CPSkillRequest packet = new CPSkillRequest((SkillSlot)SkillSlot.ENUM_MANAGER.get(container.getSlot().universalOrdinal()), WorkType.CAST);
         EpicFightNetworkManager.sendToServer(packet);
      }
   }

   private void handleStackConsumption(SkillContainer container) {
      SkillContainer weaponInnateContainer = container.getExecutor().getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnateContainer != null && weaponInnateContainer.hasSkill()) {
         SkillDataManager dataManager = weaponInnateContainer.getDataManager();
         if (dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
            && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)) {
            int currentTimer = (Integer)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER);
            if (currentTimer > 0 && currentTimer % this.durationPerStack == 0) {
               int currentStack = weaponInnateContainer.getStack();
               if (currentStack > 0) {
                  weaponInnateContainer.getSkill().setStackSynchronize(weaponInnateContainer, currentStack - 1);
               }
            }
         }
      }
   }

   public void executeOnServer(SkillContainer container, CompoundTag args) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillContainer weaponInnateContainer = executor.getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnateContainer != null && weaponInnateContainer.hasSkill()) {
         SkillDataManager dataManager = weaponInnateContainer.getDataManager();
         boolean isCurrentlyActive = dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
            && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
         boolean serverZansetsuActive = (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.ARTS_KEY);
         if (serverZansetsuActive && !isCurrentlyActive) {
            this.openZansetsu(weaponInnateContainer, dataManager);
            ((Player)container.getExecutor().getOriginal()).addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 2, false, false, false));
            ((Player)container.getExecutor().getOriginal())
               .addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY, 60, 2, false, false, false));
            ((Player)container.getExecutor().getOriginal())
               .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 60, 2, false, false, false));
            container.getExecutor().playSound(SoundEvents.BEACON_POWER_SELECT, 0.7F, 1.0F, 1.0F);
         } else if (!serverZansetsuActive && isCurrentlyActive) {
            this.closeZansetsu(dataManager);
            container.getExecutor().playAnimationSynchronized(EFNMurasamaAnimations.HF_MURASAMA_IDLE_COMBAT, 0.15F);
            ((Player)container.getExecutor().getOriginal()).removeEffect(EFNMobEffectRegistry.VERTICALSTOP);
            container.getExecutor().playSound(SoundEvents.BEACON_DEACTIVATE, 0.5F, 1.0F, 1.0F);
         }
      }
   }

   private void openZansetsu(SkillContainer weaponInnateContainer, SkillDataManager dataManager) {
      int currentStack = weaponInnateContainer.getStack();
      boolean isCreative = ((Player)weaponInnateContainer.getExecutor().getOriginal()).isCreative();
      int zansetsuDuration = currentStack * this.durationPerStack;
      dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE, true);
      dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_PAIR_FLAG, false);
      dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER, zansetsuDuration);
      dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER, 0);
      dataManager.setDataSync(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE, false);
      if (!isCreative) {
         float maxResource = weaponInnateContainer.getMaxResource();
         float consumption = maxResource * this.consumptionRatio;
         weaponInnateContainer.getSkill().setConsumptionSynchronize(weaponInnateContainer, consumption);
      }
   }

   private void closeZansetsu(SkillDataManager dataManager) {
      this.resetZansetsuStates(dataManager);
   }

   private void resetZansetsuStates(SkillDataManager dataManager) {
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_PAIR_FLAG);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_ATTACK_COUNTER);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_SECTOR);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_ZANSETSU_SLASH_RELEASED);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_OPPOSITE_TRIGGERED);
      this.resetStateKey(dataManager, EFNSKillDataKeys.MURASAMA_LAST_TRIGGERED_SECTOR);
   }

   private <T> void resetStateKey(SkillDataManager dataManager, DeferredHolder<SkillDataKey<?>, ? extends SkillDataKey<T>> dataKey) {
      T defaultValue = dataKey.value().defaultValue();
      if (dataManager.hasData(dataKey) && !Objects.equals(dataManager.getDataValue(dataKey), defaultValue)) {
         dataManager.setDataSync(dataKey, defaultValue);
      }
   }

   public boolean shouldDraw(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillContainer skillContainer = executor.getSkill(Murasama.Murasama);
      SkillContainer skillContainer1 = executor.getSkill(HfBlade.HfBlade);
      return skillContainer != null && skillContainer.hasSkill() || skillContainer1 != null && skillContainer1.hasSkill();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
      SkillContainer weaponInnateSkill = container.getExecutor().getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponInnateSkill != null) {
         SkillDataManager dataManager = weaponInnateSkill.getDataManager();
         boolean isZansetsuActive = dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
            && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
         if (isZansetsuActive) {
            int remainingTicks = (Integer)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_TIMER);
            float remainingSeconds = remainingTicks / 20.0F;
            String timeText = String.format("%.1fs", remainingSeconds);
            int textWidth = gui.getFont().width(timeText);
            guiGraphics.drawString(gui.getFont(), timeText, x + 12.0F - textWidth / 2.0F, y + 8.0F, 16776960, true);
         } else {
            int currentStack = weaponInnateSkill.getStack();
            float zansetsuDuration = currentStack * (this.durationPerStack / 20.0F);
            String durationText = String.format("%.1fs", zansetsuDuration);
            int textWidth = gui.getFont().width(durationText);
            guiGraphics.drawString(gui.getFont(), durationText, x + 12.0F - textWidth / 2.0F, y + 8.0F, 16777215, true);
         }
      }

      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void onScreen(LocalPlayerPatch localPlayerPatch, float resolutionX, float resolutionY) {
      SkillContainer murasamaSkill = localPlayerPatch.getSkill(Murasama.Murasama);
      SkillContainer hfBladeSkill = localPlayerPatch.getSkill(HfBlade.HfBlade);
      if (murasamaSkill != null || hfBladeSkill != null) {
         int currentTime = ((LocalPlayer)localPlayerPatch.getOriginal()).tickCount;
         boolean murasamaActive = false;
         boolean murasamaZandatsu = false;
         if (murasamaSkill != null) {
            SkillDataManager data = murasamaSkill.getDataManager();
            murasamaActive = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
            murasamaZandatsu = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE);
         }

         boolean hfActive = false;
         boolean hfZandatsu = false;
         if (hfBladeSkill != null) {
            SkillDataManager data = hfBladeSkill.getDataManager();
            hfActive = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
            hfZandatsu = (Boolean)data.getDataValue(EFNSKillDataKeys.MURASAMA_ZANDATSU_AVAILABLE);
         }

         if (!murasamaZandatsu && !hfZandatsu) {
            if (!murasamaActive && !hfActive) {
               if (murasamaSkill != null) {
                  this.handleFadeOutEffects(murasamaSkill.getDataManager(), currentTime, resolutionX, resolutionY, false);
               }

               if (hfBladeSkill != null) {
                  this.handleFadeOutEffects(hfBladeSkill.getDataManager(), currentTime, resolutionX, resolutionY, true);
               }
            } else if (murasamaActive) {
               this.handleZansetsuOverlay(murasamaSkill.getDataManager(), currentTime, resolutionX, resolutionY, false);
            } else {
               this.handleZansetsuOverlay(hfBladeSkill.getDataManager(), currentTime, resolutionX, resolutionY, true);
            }
         } else {
            if (murasamaZandatsu) {
               this.handleZandatsuOverlay(murasamaSkill.getDataManager(), currentTime, resolutionX, resolutionY, false);
            } else {
               this.handleZandatsuOverlay(hfBladeSkill.getDataManager(), currentTime, resolutionX, resolutionY, true);
            }

            if (murasamaSkill != null) {
               this.resetOverlayState(
                  murasamaSkill.getDataManager(),
                  EFNSKillDataKeys.MURASAMA_ZANSETSU_START_TIME,
                  EFNSKillDataKeys.MURASAMA_ZANSETSU_END_TIME
               );
            }

            if (hfBladeSkill != null) {
               this.resetOverlayState(
                  hfBladeSkill.getDataManager(),
                  EFNSKillDataKeys.MURASAMA_ZANSETSU_START_TIME,
                  EFNSKillDataKeys.MURASAMA_ZANSETSU_END_TIME
               );
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void handleZansetsuOverlay(SkillDataManager dataManager, int currentTime, float resolutionX, float resolutionY, boolean isHf) {
      if (!dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_START_TIME)
         || (Integer)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_START_TIME) == 0) {
         dataManager.setData(EFNSKillDataKeys.MURASAMA_ZANSETSU_START_TIME, currentTime);
         dataManager.setData(EFNSKillDataKeys.MURASAMA_ZANSETSU_END_TIME, 0);
      }

      int startTime = (Integer)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_START_TIME);
      int imageIndex = this.calculateImageIndex(currentTime, startTime, true);
      this.drawOverlay("zansetsu", imageIndex, resolutionX, resolutionY, isHf);
   }

   @OnlyIn(Dist.CLIENT)
   private void handleZandatsuOverlay(SkillDataManager dataManager, int currentTime, float resolutionX, float resolutionY, boolean isHf) {
      if (!dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANDATSU_START_TIME)
         || (Integer)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANDATSU_START_TIME) == 0) {
         dataManager.setData(EFNSKillDataKeys.MURASAMA_ZANDATSU_START_TIME, currentTime);
         dataManager.setData(EFNSKillDataKeys.MURASAMA_ZANDATSU_END_TIME, 0);
      }

      int startTime = (Integer)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANDATSU_START_TIME);
      int imageIndex = this.calculateImageIndex(currentTime, startTime, true);
      this.drawOverlay("zandatsu", imageIndex, resolutionX, resolutionY, isHf);
   }

   @OnlyIn(Dist.CLIENT)
   private void handleFadeOutEffects(SkillDataManager dataManager, int currentTime, float resolutionX, float resolutionY, boolean isHf) {
      this.handleOverlayFadeOut(
         dataManager,
         currentTime,
         resolutionX,
         resolutionY,
         EFNSKillDataKeys.MURASAMA_ZANSETSU_START_TIME,
         EFNSKillDataKeys.MURASAMA_ZANSETSU_END_TIME,
         "zansetsu",
         isHf
      );
      this.handleOverlayFadeOut(
         dataManager,
         currentTime,
         resolutionX,
         resolutionY,
         EFNSKillDataKeys.MURASAMA_ZANDATSU_START_TIME,
         EFNSKillDataKeys.MURASAMA_ZANDATSU_END_TIME,
         "zandatsu",
         isHf
      );
   }

   @OnlyIn(Dist.CLIENT)
   private void handleOverlayFadeOut(
      SkillDataManager dataManager,
      int currentTime,
      float resolutionX,
      float resolutionY,
      DeferredHolder<SkillDataKey<?>, ? extends SkillDataKey<Integer>> startTimeKey,
      DeferredHolder<SkillDataKey<?>, ? extends SkillDataKey<Integer>> endTimeKey,
      String overlayType,
      boolean isHf
   ) {
      if (dataManager.hasData(startTimeKey) && (Integer)dataManager.getDataValue(startTimeKey) != 0) {
         if (!dataManager.hasData(endTimeKey) || (Integer)dataManager.getDataValue(endTimeKey) == 0) {
            dataManager.setData(endTimeKey, currentTime);
         }

         int endTime = (Integer)dataManager.getDataValue(endTimeKey);
         int fadeOutTime = Math.max(0, currentTime - endTime);
         int imageIndex = Math.max(0, 5 - Math.min(5, fadeOutTime));
         if (imageIndex > 0) {
            this.drawOverlay(overlayType, imageIndex, resolutionX, resolutionY, isHf);
         } else {
            this.resetOverlayState(dataManager, startTimeKey, endTimeKey);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void drawOverlay(String overlayType, int imageIndex, float resolutionX, float resolutionY, boolean isHfBlade) {
      String fileName = overlayType + "_" + (imageIndex + 1) + ".png";
      String pathPrefix = isHfBlade ? "textures/gui/overlay/hf_blade/" : "textures/gui/overlay/hf_murasama/";
      ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("efn", pathPrefix + fileName);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, texture);
      GlStateManager._enableBlend();
      GlStateManager._disableDepthTest();
      GlStateManager._blendFunc(770, 771);
      Tesselator tessellation = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tessellation.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.addVertex(0.0F, 0.0F, 1.0F).setUv(0.0F, 0.0F);
      bufferbuilder.addVertex(0.0F, resolutionY, 1.0F).setUv(0.0F, 1.0F);
      bufferbuilder.addVertex(resolutionX, resolutionY, 1.0F).setUv(1.0F, 1.0F);
      bufferbuilder.addVertex(resolutionX, 0.0F, 1.0F).setUv(1.0F, 0.0F);
      BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
      GlStateManager._enableDepthTest();
   }

   private int calculateImageIndex(int currentTime, int startTime, boolean isFadeIn) {
      int elapsedTime = Math.max(0, currentTime - startTime);
      return isFadeIn ? Math.min(5, elapsedTime) : Math.max(0, 5 - Math.min(5, elapsedTime));
   }

   private void resetOverlayState(
      SkillDataManager dataManager,
      DeferredHolder<SkillDataKey<?>, ? extends SkillDataKey<Integer>> startTimeKey,
      DeferredHolder<SkillDataKey<?>, ? extends SkillDataKey<Integer>> endTimeKey
   ) {
      dataManager.setData(startTimeKey, 0);
      dataManager.setData(endTimeKey, 0);
   }

   @OnlyIn(Dist.CLIENT)
   public List<Object> getTooltipArgsOfScreen(List<Object> list) {
      list.add(EFNKeyMappings.EFN_ARTS.getTranslatedKeyMessage());
      list.add(this.durationPerStack / 20.0F);
      list.add(EFNKeyMappings.ZANSETSU_INPUT_SWITCH.getTranslatedKeyMessage());
      return list;
   }

   public static class Builder extends SkillBuilder<ZansetsuSkill.Builder> {
      public Builder() {
         super(ZansetsuSkill::new);
      }
   }
}
