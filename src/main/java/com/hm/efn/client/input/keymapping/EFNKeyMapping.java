package com.hm.efn.client.input.keymapping;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.client.ClientEngine;

public class EFNKeyMapping extends KeyMapping {
   public EFNKeyMapping(String pName, int pKeyCode, String pCategory) {
      super(pName, pKeyCode, pCategory);
   }

   public EFNKeyMapping(String pName, Type pType, int pKeyCode, String pCategory) {
      super(pName, pType, pKeyCode, pCategory);
   }

   public EFNKeyMapping(String description, IKeyConflictContext keyConflictContext, Type inputType, int keyCode, String category) {
      super(description, keyConflictContext, inputType, keyCode, category);
   }

   public EFNKeyMapping(String description, IKeyConflictContext keyConflictContext, Key keyCode, String category) {
      super(description, keyConflictContext, keyCode, category);
   }

   public EFNKeyMapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, Type inputType, int keyCode, String category) {
      super(description, keyConflictContext, keyModifier, inputType, keyCode, category);
   }

   public EFNKeyMapping(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, Key keyCode, String category) {
      super(description, keyConflictContext, keyModifier, keyCode, category);
   }

   public boolean isActiveAndMatches(@NotNull Key keyCode) {
      return super.isActiveAndMatches(keyCode) && ClientEngine.getInstance().isEpicFightMode();
   }
}
