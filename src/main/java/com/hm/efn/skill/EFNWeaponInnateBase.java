package com.hm.efn.skill;

import com.hm.efn.util.EFNBasicAttackRouting;
import com.p1nero.invincible.skill.ComboBasicAttack;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.event.types.player.SkillCastEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class EFNWeaponInnateBase extends ComboBasicAttack {
   public EFNWeaponInnateBase(Builder builder) {
      super(builder);
   }

   @Override
   public void onSkillExecute(SkillCastEvent event, SkillContainer container) {
      ItemStack mainHandItem = event.getPlayerPatch().getOriginal().getMainHandItem();
      Optional<CapabilityItem> optionalCapabilityItem = EpicFightCapabilities.getItemCapability(mainHandItem);
      if (optionalCapabilityItem.isEmpty() || optionalCapabilityItem.get().isEmpty()) {
         return;
      }

      CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(mainHandItem);
      if (capabilityItem.getInnateSkill(event.getPlayerPatch(), mainHandItem) != this) {
         return;
      }

      Skill skill = event.getSkillContainer() != null ? event.getSkillContainer().getSkill() : null;
      if (skill != null
         && skill.getCategory().equals(SkillCategories.BASIC_ATTACK)
         && !event.getPlayerPatch().getOriginal().isPassenger()
         && !EFNBasicAttackRouting.shouldLetEpicFightBasicAttackRun(event.getPlayerPatch(), capabilityItem)) {
         event.cancel();
      }
   }
}
