package com.hm.efn.skill.sekiro;

import com.guhao.efn_enhance.gameassets.animations.EFN_ESekiroAnimations;
import com.hm.efn.client.input.keymapping.EFNKeyMappings;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkillCategories;
import com.hm.efn.gameasset.combos.Kusabimaru;
import com.hm.efn.gameasset.combos.Kusabimaru_Enhance;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
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
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public abstract class EFNSekiroArtSkill extends Skill {
   private static final UUID ACTION_EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0242ac171234");
   protected static final boolean IS_ENHANCE_LOADED = ModList.get().isLoaded("efn_enhance");
   protected List<AnimationAccessor<? extends StaticAnimation>> comboAnimations = new ArrayList<>();
   protected float staminaCost = 5.0F;
   protected int cooldown = 1200;
   protected int stackCost = 3;

   public static SkillBuilder<?> createSekiroArtBuilder(Function<SkillBuilder<?>, ? extends EFNSekiroArtSkill> constructor) {
      return new SkillBuilder<SkillBuilder<?>>(constructor)
         .setCategory(EFNSkillCategories.EFN_SEKIRO)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.NONE);
   }

   public EFNSekiroArtSkill(SkillBuilder<?> builder) {
      super(builder);
   }

   public abstract void setComboAnimations();

   protected List<AnimationAccessor<? extends StaticAnimation>> getComboAnimations() {
      this.comboAnimations.clear();
      this.setComboAnimations();
      this.comboAnimations.removeIf(Objects::isNull);
      return this.comboAnimations;
   }

   public float getStaminaCost() {
      return this.staminaCost;
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public int getStackCost() {
      return this.stackCost;
   }

   private boolean isInvalidWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      CapabilityItem itemCapability = EpicFightCapabilities.getItemStackCapability(itemstack);
      if (itemCapability == null) {
         return true;
      }

      Skill weaponInnate = itemCapability.getInnateSkill(container.getExecutor(), itemstack);
      return IS_ENHANCE_LOADED ? weaponInnate != Kusabimaru_Enhance.kusabimaru_enhance : weaponInnate != Kusabimaru.kusabimaru;
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      com.hm.efn.util.EFNEventBridge.addEventListener(container.getExecutor().getEventListener(), 
            EventType.ACTION_EVENT_SERVER,
            ACTION_EVENT_UUID,
            event -> {
               int comboIndex = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.SEKIRO_ART_INDEX);
               AnimationAccessor<? extends StaticAnimation> currentAnimation = event.getAnimation();
               if (comboIndex > 0 && !this.getComboAnimations().contains(currentAnimation)) {
                  container.getDataManager()
                     .setDataSync(
                        EFNSKillDataKeys.SEKIRO_ART_INDEX, (Integer)(EFNSKillDataKeys.SEKIRO_ART_INDEX).value().defaultValue()
                     );
               }
            }
         );
   }

   public void onRemoved(SkillContainer container) {
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.ACTION_EVENT_SERVER, ACTION_EVENT_UUID);
   }

   public boolean canExecute(SkillContainer container) {
      if (this.isInvalidWeapon(container)) {
         return false;
      }

      PlayerPatch<?> executor = container.getExecutor();
      boolean isCreative = ((Player)executor.getOriginal()).isCreative();
      if (isCreative) {
         return true;
      }

      boolean hasEnoughStack = this.getWeaponStack(executor) >= this.getStackCost();
      boolean hasEnoughStamina = executor.getStamina() >= this.getStaminaCost();
      boolean isOnCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN) > 0;
      int comboIndex = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.SEKIRO_ART_INDEX);
      return comboIndex > 0
         ? hasEnoughStamina && this.isExecutableState(executor)
         : hasEnoughStack && hasEnoughStamina && !isOnCooldown && this.isExecutableState(executor);
   }

   public boolean isExecutableState(PlayerPatch<?> executor) {
      WeaponCategories categories = (WeaponCategories)executor.getAdvancedHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory();
      boolean holdWeapon = categories != WeaponCategories.NOT_WEAPON && categories != WeaponCategories.FIST;
      return holdWeapon && !((Player)executor.getOriginal()).isSpectator() && !executor.isInAir() && executor.getEntityState().canUseSkill();
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (container.getExecutor().isLogicalClient()) {
         this.handleClientInput(container);
      }

      if (!container.getExecutor().isLogicalClient()) {
         int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
         if (currentCooldown > 0) {
            container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, currentCooldown - 1);
         }

         if (container.getExecutor().getTickSinceLastAction() > 16
            && (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.SEKIRO_ART_INDEX) > 0) {
            container.getDataManager()
               .setDataSync(
                  EFNSKillDataKeys.SEKIRO_ART_INDEX, (Integer)(EFNSKillDataKeys.SEKIRO_ART_INDEX).value().defaultValue()
               );
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void handleClientInput(SkillContainer container) {
      int buffer = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.ARTS_INPUT_BUFFER);
      if (buffer > 0) {
         container.getDataManager().setData(EFNSKillDataKeys.ARTS_INPUT_BUFFER, buffer - 1);
      }

      if (EFNKeyMappings.EFN_ARTS.consumeClick()) {
         buffer = 10;
         container.getDataManager().setData(EFNSKillDataKeys.ARTS_INPUT_BUFFER, buffer);
      }

      if (buffer > 0) {
         int comboIndex = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.SEKIRO_ART_INDEX);
         boolean canDoCombo = comboIndex > 0 && this.isInComboCancelWindow(container);
         boolean canStartNew = comboIndex == 0 && this.canExecute(container);
         if (canDoCombo || canStartNew) {
            CPSkillRequest packet = new CPSkillRequest((SkillSlot)SkillSlot.ENUM_MANAGER.get(container.getSlot().universalOrdinal()), WorkType.CAST);
            EpicFightNetworkManager.sendToServer(packet);
            container.getDataManager().setData(EFNSKillDataKeys.ARTS_INPUT_BUFFER, 0);
         }
      }
   }

   private boolean isInComboCancelWindow(SkillContainer container) {
      return container.getExecutor().getEntityState().getLevel() > 2;
   }

   public void executeOnServer(SkillContainer container, CompoundTag args) {
      PlayerPatch<?> executor = container.getExecutor();
      int comboIndex = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.SEKIRO_ART_INDEX);
      boolean isCreative = ((Player)executor.getOriginal()).isCreative();
      if (IS_ENHANCE_LOADED && ((Player)executor.getOriginal()).isShiftKeyDown()) {
         AnimationAccessor<? extends StaticAnimation> animation = EFN_ESekiroAnimations.WU_SHEN_ULTIMATE_TECHNIQUE_THE_THREE_FORMS_OF_ZERO_INTENT;
         if (animation == null) {
            return;
         }

         if (!isCreative) {
            if (executor.getStamina() < this.getStaminaCost()) {
               return;
            }

            executor.setStamina(0.0F);
            this.consumeWeaponStack(container);
            container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, this.getCooldown());
            executor.setStaminaRegenAwaitTicks(5);
         }

         container.getExecutor().playAnimationSynchronized(animation, 0.1F);
      } else {
         if (!isCreative) {
            float currentStamina = executor.getStamina();
            if (currentStamina < this.getStaminaCost()) {
               return;
            }

            executor.setStamina(currentStamina - this.getStaminaCost());
            executor.setStaminaRegenAwaitTicks(5);
         }

         if (comboIndex == 0 && !isCreative) {
            this.consumeWeaponStack(container);
            container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, this.getCooldown());
         }

         List<AnimationAccessor<? extends StaticAnimation>> comboAnimations = this.getComboAnimations();
         if (comboIndex >= 0 && comboIndex < comboAnimations.size()) {
            container.getExecutor().playAnimationSynchronized((AssetAccessor)comboAnimations.get(comboIndex), 0.0F);
            int nextIndex = comboIndex + 1;
            if (nextIndex >= comboAnimations.size()) {
               nextIndex = 0;
            }

            container.getDataManager().setDataSync(EFNSKillDataKeys.SEKIRO_ART_INDEX, nextIndex);
         } else {
            container.getDataManager().setDataSync(EFNSKillDataKeys.SEKIRO_ART_INDEX, 0);
         }
      }
   }

   private int getWeaponStack(PlayerPatch<?> executor) {
      SkillContainer skillContainer = IS_ENHANCE_LOADED ? executor.getSkill(Kusabimaru_Enhance.kusabimaru_enhance) : executor.getSkill(Kusabimaru.kusabimaru);
      return skillContainer != null ? skillContainer.getStack() : 0;
   }

   private void consumeWeaponStack(SkillContainer container) {
      SkillContainer weaponSkillContainer = container.getExecutor().getSkill(SkillSlots.WEAPON_INNATE);
      if (weaponSkillContainer != null && weaponSkillContainer.hasSkill()) {
         int currentStack = weaponSkillContainer.getStack();
         int newCharge = Math.max(0, currentStack - this.getStackCost());
         weaponSkillContainer.getSkill().setStackSynchronize(weaponSkillContainer, newCharge);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDraw(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillContainer skillContainer = IS_ENHANCE_LOADED ? executor.getSkill(Kusabimaru_Enhance.kusabimaru_enhance) : executor.getSkill(Kusabimaru.kusabimaru);
      if (skillContainer != null && skillContainer.hasSkill()) {
         boolean hasEnoughStack = skillContainer.getStack() >= this.getStackCost();
         boolean hasEnoughStamina = executor.getStamina() >= this.getStaminaCost();
         boolean isOnCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN) > 0;
         return !hasEnoughStack || !hasEnoughStamina || isOnCooldown;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public List<Object> getTooltipArgsOfScreen(List<Object> list) {
      list.add(EFNKeyMappings.EFN_ARTS.getTranslatedKeyMessage());
      list.add(this.getStaminaCost());
      list.add(this.getCooldown() / 20.0);
      list.add(this.getStackCost());
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
      PlayerPatch<?> executor = container.getExecutor();
      boolean hasEnoughStamina = executor.getStamina() >= this.getStaminaCost();
      boolean hasEnoughStack = this.getWeaponStack(executor) >= this.getStackCost();
      if (!hasEnoughStamina) {
         guiGraphics.drawString(gui.getFont(), "No Stamina", x + 2.0F, y + 8.0F, 16733525, true);
      } else if (!hasEnoughStack) {
         guiGraphics.drawString(gui.getFont(), "No Stack", x + 2.0F, y + 5.0F, 16733525, true);
      } else {
         int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
         if (currentCooldown > 0) {
            guiGraphics.drawString(gui.getFont(), String.format("%.1f", currentCooldown / 20.0), x + 2.0F, y + 8.0F, 16777215, true);
         }
      }

      poseStack.popPose();
   }
}
