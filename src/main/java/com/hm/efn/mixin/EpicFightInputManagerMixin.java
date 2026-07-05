package com.hm.efn.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.client.input.InputMode;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.api.client.input.controller.EpicFightControllerModProvider;
import yesman.epicfight.api.client.input.controller.IEpicFightControllerMod;
import yesman.epicfight.api.client.input.InputManager;

@Mixin(value = InputManager.class, remap = false)
public abstract class EpicFightInputManagerMixin {
   @Inject(method = "isBoundToSamePhysicalInput", at = @At("HEAD"), cancellable = true)
   private static void efn$compareKeyboardMouseInputsByValue(InputAction action, InputAction otherAction, CallbackInfoReturnable<Boolean> cir) {
      IEpicFightControllerMod controllerMod = EpicFightControllerModProvider.get();
      if (controllerMod != null && controllerMod.getInputMode() == InputMode.CONTROLLER) {
         return;
      }

      KeyMapping keyMapping = action.keyMapping();
      KeyMapping otherKeyMapping = otherAction.keyMapping();
      if (keyMapping == null || otherKeyMapping == null) {
         return;
      }

      InputConstants.Key key = keyMapping.getKey();
      InputConstants.Key otherKey = otherKeyMapping.getKey();
      if (key == null || otherKey == null || key.getValue() == InputConstants.UNKNOWN.getValue()) {
         return;
      }

      if (key.getType() == otherKey.getType() && key.getValue() == otherKey.getValue()) {
         cir.setReturnValue(true);
      }
   }
}
