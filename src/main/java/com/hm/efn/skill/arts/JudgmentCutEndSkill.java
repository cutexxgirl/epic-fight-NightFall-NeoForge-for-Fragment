package com.hm.efn.skill.arts;

import com.hm.efn.EFNCommonConfig;
import com.hm.efn.client.input.keymapping.EFNKeyMappings;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkillCategories;
import com.hm.efn.gameasset.EFNWeaponCategories;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.item.custom.YamatoItem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.network.client.CPSkillRequest.WorkType;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class JudgmentCutEndSkill extends PassiveSkill {
   private int cooldown;
   private boolean needenchantment;

   public JudgmentCutEndSkill(JudgmentCutEndSkill.Builder builder) {
      super(builder);
   }

   public static JudgmentCutEndSkill.Builder createJudgmentCutEndBuilder() {
      return (JudgmentCutEndSkill.Builder)new JudgmentCutEndSkill.Builder()
         .setCategory(EFNSkillCategories.JUDGMENTCUT_END)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.NONE);
   }

   public static boolean isHoldingYamato(Player player) {
      return player == null
         ? false
         : Stream.of(player.getMainHandItem(), player.getOffhandItem())
            .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
            .filter(Objects::nonNull)
            .anyMatch(cap -> cap.getWeaponCategory() == EFNWeaponCategories.EFN_YAMATO);
   }

   public static boolean isYamatoUnlocked(Player player) {
      if (!(Boolean)EFNCommonConfig.YAMATO_REWARD_ENABLED.get()) {
         return false;
      }

      int rewardThreshold = (Integer)EFNCommonConfig.YAMATO_REWARD_KILL_THRESHOLD.get();
      if (rewardThreshold <= 0) {
         return false;
      }

      for (ItemStack itemStack : new ItemStack[]{player.getMainHandItem(), player.getOffhandItem()}) {
         if (itemStack.getItem() instanceof YamatoItem) {
            int killCount = YamatoItem.getKillCount(itemStack);
            if (killCount >= rewardThreshold) {
               return true;
            }
         }
      }

      return false;
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.cooldown = parameters.getInt("cooldown");
      this.needenchantment = parameters.getBoolean("needenchantment");
   }

   public boolean canExecute(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      Player player = (Player)executor.getOriginal();
      boolean onGround = player.onGround();
      boolean holdingYamato = isHoldingYamato(player);
      ItemStack mainHandItem = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_JUDGEMENT_CUT_END) > 0;
      if (player.isCreative()) {
         return onGround && holdingYamato;
      }

      boolean cooldownReady = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN) <= 0;
      boolean yamatoUnlocked = isYamatoUnlocked(player);
      boolean baseConditions = onGround && holdingYamato && yamatoUnlocked && cooldownReady;
      return !this.needenchantment ? baseConditions : baseConditions && hasEnchantment;
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
      if (EFNKeyMappings.EFN_ARTS.isDown()) {
         CPSkillRequest packet = new CPSkillRequest((SkillSlot)SkillSlot.ENUM_MANAGER.get(container.getSlot().universalOrdinal()), WorkType.CAST);
         EpicFightNetworkManager.sendToServer(packet);
      }
   }

   public void executeOnServer(SkillContainer container, CompoundTag args) {
      super.executeOnServer(container, args);
      container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, this.cooldown);
      if (ModList.get().isLoaded("efn_enhance")) {
         container.getServerExecutor().playAnimationSynchronized(EFNAnimations.DMC5_V_JC, -1.55F);
      } else {
         container.getServerExecutor().playAnimationSynchronized(EFNAnimations.DMC5_V_JC, 0.1F);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDraw(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      SkillContainer yamatoSkillContainer = executor.getSkill(Yamato.yamato);
      return yamatoSkillContainer != null
         && yamatoSkillContainer.hasSkill()
         && (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN) > 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
      guiGraphics.drawString(
         gui.getFont(),
         String.format("%.1f", ((Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN)).intValue() / 20.0),
         x + 2.0F,
         y + 8.0F,
         16777215,
         true
      );
      poseStack.popPose();
   }

   public List<Object> getTooltipArgsOfScreen(List<Object> list) {
      list.add(EFNKeyMappings.EFN_ARTS.getTranslatedKeyMessage());
      list.add(this.cooldown / 20.0);
      return list;
   }

   public static class Builder extends SkillBuilder<JudgmentCutEndSkill.Builder> {
      public Builder() {
         super(JudgmentCutEndSkill::new);
      }
   }
}
