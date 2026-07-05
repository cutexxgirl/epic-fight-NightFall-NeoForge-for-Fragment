package com.hm.efn.client;

import com.yesman.epicskills.client.gui.screen.CategorySlotTexture;

public enum EFNCategorySlotTextures implements CategorySlotTexture {
   EFN_ARTS(6, 6, 44, 44);

   private final int offsetX;
   private final int offsetY;
   private final int texWidth;
   private final int texHeight;
   private final int universalOrder;

   EFNCategorySlotTextures(int offsetX, int offsetY, int texWidth, int texHeight) {
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.texWidth = texWidth;
      this.texHeight = texHeight;
      this.universalOrder = CategorySlotTexture.ENUM_MANAGER.assign(this);
   }

   public int offsetX() {
      return this.offsetX;
   }

   public int offsetY() {
      return this.offsetY;
   }

   public int texWidth() {
      return this.texWidth;
   }

   public int texHeight() {
      return this.texHeight;
   }

   public int universalOrdinal() {
      return this.universalOrder;
   }
}
