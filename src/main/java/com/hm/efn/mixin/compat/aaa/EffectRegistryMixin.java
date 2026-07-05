package com.hm.efn.mixin.compat.aaa;

import com.hm.efn.EFN;
import java.lang.reflect.Field;
import mod.chloeprime.aaaparticles.api.client.EffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EffectRegistry.class, remap = false)
public abstract class EffectRegistryMixin {
   @Unique
   private static boolean epicFight_Nightfall$warnedAboutEarlyClear;

   @Inject(method = "clearAllPlaying", at = @At("HEAD"), cancellable = true, remap = false)
   private static void efn$skipLazyEffekseerInitOnEarlyClear(CallbackInfo ci) {
      if (!epicFight_Nightfall$globalManagersInitialized()) {
         if (!epicFight_Nightfall$warnedAboutEarlyClear) {
            EFN.LOGGER.warn("[EFN/AAA] Skipping AAAParticles clearAllPlaying before Effekseer managers are initialized");
            epicFight_Nightfall$warnedAboutEarlyClear = true;
         }

         ci.cancel();
      }
   }

   @Unique
   private static boolean epicFight_Nightfall$globalManagersInitialized() {
      Object supplier = EffectDefinitionAccessor.efn$getTheOneManagers();
      if (supplier == null) {
         return false;
      }

      Class<?> supplierClass = supplier.getClass();
      try {
         Field initialized = epicFight_Nightfall$findField(supplierClass, "initialized");
         if (initialized != null) {
            initialized.setAccessible(true);
            return initialized.getBoolean(supplier);
         }

         Field delegate = epicFight_Nightfall$findField(supplierClass, "delegate");
         if (delegate == null) {
            return false;
         }

         delegate.setAccessible(true);
         Object currentDelegate = delegate.get(supplier);
         if (currentDelegate == null) {
            return true;
         }

         Field successSentinel = epicFight_Nightfall$findField(supplierClass, "SUCCESSFULLY_COMPUTED");
         if (successSentinel == null) {
            return false;
         }

         successSentinel.setAccessible(true);
         return currentDelegate == successSentinel.get(null);
      } catch (ReflectiveOperationException | RuntimeException exception) {
         if (!epicFight_Nightfall$warnedAboutEarlyClear) {
            EFN.LOGGER.warn("[EFN/AAA] Could not inspect AAAParticles Effekseer manager state; skipping early clear defensively", exception);
            epicFight_Nightfall$warnedAboutEarlyClear = true;
         }

         return false;
      }
   }

   @Unique
   private static Field epicFight_Nightfall$findField(Class<?> owner, String name) {
      Class<?> current = owner;
      while (current != null) {
         try {
            return current.getDeclaredField(name);
         } catch (NoSuchFieldException ignored) {
            current = current.getSuperclass();
         }
      }

      return null;
   }
}
