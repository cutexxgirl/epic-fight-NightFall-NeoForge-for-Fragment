package com.hm.efn.gameasset;

import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.skill.SkillCategory;

public enum EFNSkillCategories implements SkillCategory {
   EFN_ARTS(true, true, true, ResourceLocation.fromNamespaceAndPath("efn", "skillbook_efn_arts")),
   EFN_SEKIRO(true, true, true, ResourceLocation.fromNamespaceAndPath("efn", "skillbook_efn_sekiro")),
   EFN_ZANSETSU(true, true, true, ResourceLocation.fromNamespaceAndPath("efn", "skillbook_efn_zansetsu")),
   JUDGMENTCUT_END(true, true, true, ResourceLocation.fromNamespaceAndPath("efn", "skillbook_judgementcut_end"));

   final boolean shouldSave;
   final boolean shouldSynchronize;
   final boolean learnable;
   final ResourceLocation bookIcon;
   final int id;

   EFNSkillCategories(boolean shouldSave, boolean shouldSyncronizedAllPlayers, boolean learnable, ResourceLocation bookIcon) {
      this.shouldSave = shouldSave;
      this.shouldSynchronize = shouldSyncronizedAllPlayers;
      this.learnable = learnable;
      this.id = SkillCategory.ENUM_MANAGER.assign(this);
      this.bookIcon = bookIcon;
   }

   public boolean shouldSave() {
      return this.shouldSave;
   }

   public boolean shouldSynchronize() {
      return this.shouldSynchronize;
   }

   public boolean learnable() {
      return this.learnable;
   }

   public int universalOrdinal() {
      return this.id;
   }

   public ResourceLocation bookIcon() {
      return this.bookIcon == null ? SkillCategory.DEFAULT_BOOK_ICON : this.bookIcon;
   }
}
