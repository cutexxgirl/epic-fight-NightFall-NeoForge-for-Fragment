package com.hm.efn.gameasset;

import com.p1nero.invincible.api.combo.ComboType;
import java.util.ArrayList;
import java.util.List;

public enum EFNComboTypes implements ComboType {
   KEY_DOPPELGANGER,
   KEY_SUMMONED_SWORD,
   SIMULATION_KEY1,
   SIMULATION_KEY2,
   KEY_EFN_ARTS;

   final int id;
   final List<ComboType> subTypes = new ArrayList<>();

   EFNComboTypes() {
      this.id = ComboType.ENUM_MANAGER.assign(this);
   }

   public List<ComboType> getSubTypes() {
      return this.subTypes;
   }

   public int universalOrdinal() {
      return this.id;
   }
}
