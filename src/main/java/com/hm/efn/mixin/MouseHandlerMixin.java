package com.hm.efn.mixin;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
   @Shadow(remap = false)
   private double accumulatedDX;
   @Shadow(remap = false)
   private double accumulatedDY;
   @Shadow(remap = false)
   @Final
   private Minecraft minecraft;

   @Unique
   private static boolean epicFight_Nightfall$isInZansetsuMode() {
      if (Minecraft.getInstance().player == null) {
         return false;
      }

      PlayerPatch<?> entityPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, PlayerPatch.class);
      if (entityPatch == null) {
         return false;
      }

      SkillContainer skill = entityPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (skill == null) {
         return false;
      }

      SkillDataManager dataManager = skill.getDataManager();
      return dataManager.hasData(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE)
         && (Boolean)dataManager.getDataValue(EFNSKillDataKeys.MURASAMA_ZANSETSU_ACTIVE);
   }

   @Inject(method = "turnPlayer", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
   private void onTurnPlayer(CallbackInfo ci) {
      if (epicFight_Nightfall$isInZansetsuMode()) {
         if (Minecraft.getInstance().mouseHandler.isRightPressed()) {
            return;
         }

         this.accumulatedDX = 0.0;
         this.accumulatedDY = 0.0;
         ci.cancel();
      }
   }
}
