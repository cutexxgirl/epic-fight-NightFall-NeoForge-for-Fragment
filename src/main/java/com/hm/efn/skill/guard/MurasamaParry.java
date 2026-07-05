package com.hm.efn.skill.guard;

import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Murasama;
import com.hm.efn.util.EFNSkillChecks;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.GuardSkill.Builder;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class MurasamaParry extends EFNParryingSkill {
   public MurasamaParry(Builder builder) {
      super(builder);
   }

   private boolean isHoldingMurasama(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, Murasama.Murasama)
         || EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, HfBlade.HfBlade);
   }

   public static Builder createActiveGuardBuilder() {
      return GuardSkill.createGuardBuilder(MurasamaParry::new)
         .addGuardMotion(WeaponCategories.TACHI, (item, player) -> EFNMurasamaAnimations.HF_MURASAMA_GUARD_HIT)
         .addAdvancedGuardMotion(
            WeaponCategories.TACHI,
            (itemCap, playerpatch) -> List.of(
               EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2, EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3
            )
         );
   }

   public boolean canExecute(SkillContainer container) {
      return super.canExecute(container) && this.isHoldingMurasama(container);
   }
}
