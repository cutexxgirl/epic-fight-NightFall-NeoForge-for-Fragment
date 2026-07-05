package com.hm.efn.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME)
public class EFNCommands {
   @SubscribeEvent
   public static void registerCommands(RegisterCommandsEvent event) {
      CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
      EFNVFXCommand.register(dispatcher);
      TickCommand.register(dispatcher);
      FireworksCommand.register(dispatcher);
   }
}
