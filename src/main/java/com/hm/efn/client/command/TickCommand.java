package com.hm.efn.client.command;

import com.hm.efn.event.TickChange;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class TickCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tick").requires(source -> source.hasPermission(2)))
            .then(
               Commands.literal("set")
                  .then(
                     Commands.argument("percent", FloatArgumentType.floatArg(0.001F, 1.0E8F))
                        .executes(context -> setTickSpeed(context, FloatArgumentType.getFloat(context, "percent")))
                  )
            )
      );
   }

   private static int setTickSpeed(CommandContext<CommandSourceStack> context, float percent) {
      TickChange.requestChange(percent);
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("§a游戏速度已设置为: §e" + percent), true);
      return 1;
   }
}
