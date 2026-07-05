package com.hm.efn.mixin.compat.aaa;

import java.util.function.Supplier;
import mod.chloeprime.aaaparticles.api.client.EffectDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EffectDefinition.class, remap = false)
public interface EffectDefinitionAccessor {
   @Accessor("THE_ONE_MANAGERS")
   static Supplier<?> efn$getTheOneManagers() {
      throw new AssertionError();
   }
}
