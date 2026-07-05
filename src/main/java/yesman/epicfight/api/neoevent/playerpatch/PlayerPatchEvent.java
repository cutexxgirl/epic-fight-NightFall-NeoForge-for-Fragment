package yesman.epicfight.api.neoevent.playerpatch;

import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class PlayerPatchEvent extends Event {
   private final PlayerPatch<?> playerPatch;

   public PlayerPatchEvent(PlayerPatch<?> playerPatch) {
      this.playerPatch = playerPatch;
   }

   public PlayerPatch<?> getPlayerPatch() {
      return this.playerPatch;
   }

   public static PlayerPatchEvent postAndFireSkillListeners(PlayerPatchEvent event) {
      NeoForge.EVENT_BUS.post(event);
      event.postEpicFightListeners();
      return event;
   }

   protected void postEpicFightListeners() {
   }
}
