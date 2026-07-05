package com.hm.efn.gameasset;

import yesman.epicfight.world.capabilities.item.WeaponCategory;

public enum EFNWeaponCategories implements WeaponCategory {
   EFN_YAMATO,
   EFN_HF_MURASAMA;

   final int id = WeaponCategory.ENUM_MANAGER.assign(this);

   public int universalOrdinal() {
      return this.id;
   }
}
