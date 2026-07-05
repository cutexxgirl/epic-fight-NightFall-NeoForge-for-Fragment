package com.hm.efn.mixin.compat.cataclysm;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.LLibrary_Boss_Monster", remap = false)
public abstract class MixinLLibraryBossMonster {
   @Inject(method = "canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At("HEAD"), cancellable = true, remap = false)
   private void efn$allowYamatoStunEffect(MobEffectInstance p_34192_, CallbackInfoReturnable<Boolean> cir) {
      if (p_34192_.getEffect() == EFNMobEffectRegistry.STUN
         || p_34192_.getEffect() == EFNMobEffectRegistry.STOP
         || p_34192_.getEffect() == EFNMobEffectRegistry.HEAVY_RAIN_STUN) {
         cir.setReturnValue(true);
         cir.cancel();
      }
   }
}
