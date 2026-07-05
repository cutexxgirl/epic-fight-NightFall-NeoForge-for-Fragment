package com.hm.efn.mixin;

import java.util.Objects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.particle.EntityAfterimageParticle.AdrenalineParticleProvider;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(AdrenalineParticleProvider.class)
public abstract class AdrenalineParticleProviderMixin {
   @Inject(method = "createParticle*", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private void onCreateAdrenalineParticle(
      SimpleParticleType type,
      ClientLevel level,
      double x,
      double y,
      double z,
      double xSpeed,
      double ySpeed,
      double zSpeed,
      CallbackInfoReturnable<Particle> cir
   ) {
      if (level.getEntity((int)Double.doubleToLongBits(xSpeed)) instanceof Player player) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch != null && !Objects.equals(playerPatch.getArmature().toString(), Armatures.BIPED.registryName().toString())) {
            cir.setReturnValue(null);
            cir.cancel();
         }
      }
   }
}
