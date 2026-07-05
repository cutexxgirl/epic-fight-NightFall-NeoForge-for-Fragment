package com.hm.efn.network;

import com.hm.efn.EFN;
import com.hm.efn.registries.EFNItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayTotemAnimationPacket() implements CustomPacketPayload {
   public static final Type<PlayTotemAnimationPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EFN.MODID, "play_totem_animation"));
   public static final StreamCodec<RegistryFriendlyByteBuf, PlayTotemAnimationPacket> STREAM_CODEC = StreamCodec.unit(new PlayTotemAnimationPacket());

   @Override
   public @NotNull Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handleClientbound(PlayTotemAnimationPacket packet, IPayloadContext context) {
      context.enqueueWork(ClientHandler::handlePacket);
   }

   private static final class ClientHandler {
      private static void handlePacket() {
         Minecraft mc = Minecraft.getInstance();
         LocalPlayer player = mc.player;
         if (player != null) {
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();
            ItemStack itemToDisplay = mainHandItem.is((Item)EFNItem.BROADBLADE.get()) ? mainHandItem : offHandItem;
            mc.gameRenderer.displayItemActivation(itemToDisplay);
            player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, player.getSoundSource(), 1.0F, 1.0F);
         }
      }
   }
}
