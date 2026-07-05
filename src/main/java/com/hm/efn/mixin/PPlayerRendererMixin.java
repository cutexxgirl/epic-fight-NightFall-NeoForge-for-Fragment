package com.hm.efn.mixin;

import com.hm.efn.item.custom.DuskFireArmorItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@OnlyIn(Dist.CLIENT)
@Mixin(PPlayerRenderer.class)
public class PPlayerRendererMixin {
   @Inject(
      method = "prepareModel(Lyesman/epicfight/client/mesh/HumanoidMesh;Lnet/minecraft/client/player/AbstractClientPlayer;Lyesman/epicfight/client/world/capabilites/entitypatch/player/AbstractClientPlayerPatch;Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;)V",
      at = @At("TAIL"),
      remap = false
   )
   private void efn$hidePlayerModelPartsForDuskFire(
      HumanoidMesh mesh, AbstractClientPlayer entity, AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch, PlayerRenderer renderer, CallbackInfo ci
   ) {
      this.efn$hidePlayerModelParts(entity, mesh);
   }

   @Unique
   private void efn$hidePlayerModelParts(Player player, HumanoidMesh mesh) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (slot.isArmor()) {
            ItemStack itemstack = player.getItemBySlot(slot);
            if (itemstack.getItem() instanceof DuskFireArmorItem) {
               switch (slot) {
                  case HEAD:
                     this.efn$hideHeadParts(mesh);
                     break;
                  case CHEST:
                     this.efn$hideChestParts(mesh);
                     break;
                  case LEGS:
                     this.efn$hideLegParts(mesh);
               }
            }
         }
      }
   }

   @Unique
   private void efn$hideHeadParts(HumanoidMesh mesh) {
      if (mesh.hat != null) {
         mesh.hat.setHidden(true);
      }
   }

   @Unique
   private void efn$hideChestParts(HumanoidMesh mesh) {
      if (mesh.jacket != null) {
         mesh.jacket.setHidden(true);
      }

      if (mesh.leftSleeve != null) {
         mesh.leftSleeve.setHidden(true);
      }

      if (mesh.rightSleeve != null) {
         mesh.rightSleeve.setHidden(true);
      }
   }

   @Unique
   private void efn$hideLegParts(HumanoidMesh mesh) {
      if (mesh.leftPants != null) {
         mesh.leftPants.setHidden(true);
      }

      if (mesh.rightPants != null) {
         mesh.rightPants.setHidden(true);
      }
   }
}
