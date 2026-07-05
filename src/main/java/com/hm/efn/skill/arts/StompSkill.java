package com.hm.efn.skill.arts;

import com.hm.efn.client.input.keymapping.EFNKeyMappings;
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
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
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
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class StompSkill extends PassiveSkill {
   private float stamina_cost;
   private int cooldown;

   public StompSkill(StompSkill.Builder builder) {
      super(builder);
   }

   public static StompSkill.Builder createStompBuilder() {
      return (StompSkill.Builder)new StompSkill.Builder()
         .setCategory(EFNSkillCategories.EFN_ARTS)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.NONE);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.cooldown = parameters.getInt("cooldown");
      this.stamina_cost = parameters.getInt("stamina_cost");
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
      boolean isCreative = ((Player)executor.getOriginal()).isCreative();
      return isOnGround && (isCreative || (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN) <= 0);
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
      PlayerPatch<?> executor = container.getExecutor();
      if (!((Player)executor.getOriginal()).isCreative()) {
         float currentStamina = executor.getStamina();
         executor.setStamina(currentStamina - this.stamina_cost);
         executor.setStaminaRegenAwaitTicks(5);
      }

      container.getDataManager().setDataSync(EFNSKillDataKeys.COOLDOWN, this.cooldown);
      container.getServerExecutor().playAnimationSynchronized(EFNSkillAnimations.STOMP, 0.1F);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDraw(SkillContainer container) {
      PlayerPatch<?> executor = container.getExecutor();
      boolean hasEnoughStamina = executor.getStamina() >= this.stamina_cost;
      boolean isOnCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN) > 0;
      return !hasEnoughStamina || isOnCooldown;
   }

   @OnlyIn(Dist.CLIENT)
   public List<Object> getTooltipArgsOfScreen(List<Object> list) {
      list.add(EFNKeyMappings.EFN_ARTS.getTranslatedKeyMessage());
      list.add(this.stamina_cost);
      list.add(this.cooldown / 20.0);
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
      PlayerPatch<?> executer = container.getExecutor();
      boolean hasEnoughStamina = executer.getStamina() >= this.stamina_cost;
      if (!hasEnoughStamina) {
         guiGraphics.drawString(gui.getFont(), "No Stamina", x + 2.0F, y + 8.0F, 16733525, true);
      } else {
         int currentCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.COOLDOWN);
         if (currentCooldown > 0) {
            guiGraphics.drawString(gui.getFont(), String.format("%.1f", currentCooldown / 20.0), x + 2.0F, y + 8.0F, 16777215, true);
         }
      }

      poseStack.popPose();
   }

   public static class Builder extends SkillBuilder<StompSkill.Builder> {
      public Builder() {
         super(StompSkill::new);
      }
   }
}
