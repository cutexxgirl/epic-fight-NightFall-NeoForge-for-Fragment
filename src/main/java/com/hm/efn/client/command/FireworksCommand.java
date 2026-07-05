package com.hm.efn.client.command;

import com.hm.efn.EFN;
import com.hm.efn.event.PlayerRandomFireworksServerEvent;
import com.hm.efn.network.FireworksPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class FireworksCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("efn")
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fireworks").requires(source -> source.hasPermission(2)))
                        .then(Commands.literal("on").executes(ctx -> setFireworks((CommandSourceStack)ctx.getSource(), true))))
                     .then(Commands.literal("off").executes(ctx -> setFireworks((CommandSourceStack)ctx.getSource(), false))))
                  .then(Commands.literal("status").executes(ctx -> {
                     boolean enabled = PlayerRandomFireworksServerEvent.isEnabled();
                     ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("烟花秀当前状态: " + (enabled ? "§a开启" : "§c关闭")), false);
                     return 1;
                  }))
            )
      );
   }

   private static int setFireworks(CommandSourceStack source, boolean enabled) {
      PlayerRandomFireworksServerEvent.setEnabled(enabled);
      source.getServer();

      for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
         EFN.sendToPlayer(new FireworksPacket(enabled), player);
      }

      source.sendSuccess(() -> Component.literal("烟花秀已" + (enabled ? "§a开启" : "§c关闭") + "，已同步给所有在线玩家"), true);
      return 1;
   }
}
