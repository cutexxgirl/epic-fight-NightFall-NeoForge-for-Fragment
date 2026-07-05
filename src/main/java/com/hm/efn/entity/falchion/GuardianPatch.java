package com.hm.efn.entity.falchion;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hm.efn.gameasset.EFNStyles;
import com.hm.efn.gameasset.animations.EFNFalchionAnimations;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class GuardianPatch extends HumanoidMobPatch<GuardianEntity> {
   @Nullable
   private PlayerPatch<?> ownerPatch;

   public GuardianPatch(GuardianEntity original) {
      super(original, Factions.UNDEAD);
   }

   public void initAnimator(Animator animator) {
      super.initAnimator(animator);
      super.commonAggresiveMobAnimatorInit(animator);
   }

   protected void setWeaponMotions() {
      super.setWeaponMotions();
      this.weaponLivingMotions = Maps.newHashMap();
      this.weaponLivingMotions
         .put(
            WeaponCategories.LONGSWORD,
            ImmutableMap.of(
               EFNStyles.FALCHION,
               Set.of(
                  Pair.of(LivingMotions.WALK, Animations.BIPED_WALK_SPEAR),
                  Pair.of(LivingMotions.CHASE, EFNFalchionAnimations.FALCHION_RUN),
                  Pair.of(LivingMotions.IDLE, EFNFalchionAnimations.FALCHION_GUARDIAN_IDLE),
                  Pair.of(LivingMotions.RUN, EFNFalchionAnimations.FALCHION_RUN)
               )
            )
         );
   }

   public void updateMotion(boolean considerInaction) {
      super.commonAggressiveRangedMobUpdateMotion(considerInaction);
   }

   @Nullable
   public PlayerPatch<?> getOwnerPatch() {
      if (this.ownerPatch != null) {
         return this.ownerPatch;
      } else if (((GuardianEntity)this.getOriginal()).getOwner() != null) {
         this.ownerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(((GuardianEntity)this.getOriginal()).getOwner(), PlayerPatch.class);
         return this.ownerPatch;
      } else {
         return null;
      }
   }
}
