package com.hm.efn.client.command;

import com.hm.efn.util.EffectEntityInvoker;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class EFNVFXCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("efn")
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("secludedAttack").then(Commands.literal("enable").executes(ctx -> {
                     LivingEntity player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                     EffectEntityInvoker.setSecludedAttackEnabled(player, true);
                     ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("§a[EFN] enabled SecludedAttack for you"), false);
                     return 1;
                  }))).then(Commands.literal("disable").executes(ctx -> {
                     LivingEntity player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                     EffectEntityInvoker.setSecludedAttackEnabled(player, false);
                     ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("§c[EFN] disabled SecludedAttack for you"), false);
                     return 1;
                  })))
                  .executes(
                     ctx -> {
                        LivingEntity player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                        boolean current = EffectEntityInvoker.isSecludedAttackEnabled(player);
                        ((CommandSourceStack)ctx.getSource())
                           .sendSuccess(() -> Component.literal("§6[EFN] your SecludedAttack state: " + (current ? "§aenabled" : "§cdisabled")), false);
                        return 1;
                     }
                  )
            )
      );
   }
}
