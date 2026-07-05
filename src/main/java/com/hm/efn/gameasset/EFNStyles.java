package com.hm.efn.gameasset;

import yesman.epicfight.world.capabilities.item.Style;

public enum EFNStyles implements Style {
   FALCHION(false),
   BOARD_BLADE(false);

   final boolean canUseOffhand;
   final int id = Style.ENUM_MANAGER.assign(this);

   EFNStyles(boolean canUseOffhand) {
      this.canUseOffhand = canUseOffhand;
   }

   public int universalOrdinal() {
      return this.id;
   }

   public boolean canUseOffhand() {
      return this.canUseOffhand;
   }
}
