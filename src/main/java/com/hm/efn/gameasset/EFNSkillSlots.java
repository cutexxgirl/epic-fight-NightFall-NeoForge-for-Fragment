package com.hm.efn.gameasset;

import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public enum EFNSkillSlots implements SkillSlot {
   EFN_ARTS(EFNSkillCategories.EFN_ARTS),
   EFN_SEKIRO(EFNSkillCategories.EFN_SEKIRO),
   EFN_ZANSETSU(EFNSkillCategories.EFN_ZANSETSU),
   JUDGMENTCUT_END(EFNSkillCategories.JUDGMENTCUT_END);

   final EFNSkillCategories category;
   final int id;

   EFNSkillSlots(EFNSkillCategories category) {
      this.category = category;
      this.id = SkillSlot.ENUM_MANAGER.assign(this);
   }

   public SkillCategory category() {
      return this.category;
   }

   public int universalOrdinal() {
      return this.id;
   }
}
