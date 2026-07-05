package com.hm.efn.compat;

import com.hm.efn.client.EFNCategorySlotTextures;
import com.yesman.epicskills.client.gui.screen.CategorySlotTexture;

public class EpicSkillsCompat {
   public static void registerCategorySlotTexture() {
      CategorySlotTexture.ENUM_MANAGER.registerEnumCls("efn", EFNCategorySlotTextures.class);
   }
}
