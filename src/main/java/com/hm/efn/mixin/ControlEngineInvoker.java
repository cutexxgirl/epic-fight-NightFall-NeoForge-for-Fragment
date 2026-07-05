package com.hm.efn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import yesman.epicfight.client.events.engine.ControlEngine;

@Mixin(value = ControlEngine.class, remap = false)
public interface ControlEngineInvoker {
   @Invoker("maybeAttack")
   void efn$invokeMaybeAttack();
}
