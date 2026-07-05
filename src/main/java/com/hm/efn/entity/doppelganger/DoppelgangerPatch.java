package com.hm.efn.entity.doppelganger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hm.efn.EFN;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNWeaponCategories;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Field;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class DoppelgangerPatch extends HumanoidMobPatch<DoppelgangerEntity> {
   @Nullable
   private PlayerPatch<?> ownerPatch;
   private static final TagKey<DamageType> DOPPELGANGER_ATTACK_TAG = TagKey.create(
      Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("epicfight", "doppelganger_attack")
   );
   private static final TagKey<DamageType> DOPPELGANGER_OWNER_ATTACK_TAG = TagKey.create(
      Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("epicfight", "doppelganger_owner_attack")
   );
   private static Field resultTypeField;
   private static Field damageField;

   public DoppelgangerPatch(DoppelgangerEntity original) {
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
            EFNWeaponCategories.EFN_YAMATO,
            ImmutableMap.of(
               Styles.TWO_HAND,
               Set.of(
                  Pair.of(LivingMotions.WALK, EFNYamatoAnimations.YAMATO_WALK),
                  Pair.of(LivingMotions.CHASE, EFNYamatoAnimations.YAMATO_RUN),
                  Pair.of(LivingMotions.IDLE, EFNYamatoAnimations.YAMATO_IDLE),
                  Pair.of(LivingMotions.RUN, EFNYamatoAnimations.YAMATO_RUN)
               )
            )
         );
   }

   public void updateMotion(boolean considerInaction) {
      super.commonAggressiveRangedMobUpdateMotion(considerInaction);
   }

   public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
      if (target == ((DoppelgangerEntity)this.getOriginal()).getOwner()) {
         return AttackResult.missed(0.0F);
      }

      AttackResult attackResult;
      if (this.getOwnerPatch() != null && this.shouldUseOwnerAttack()) {
         EpicFightDamageSource modifiedSource = damageSource.addRuntimeTag(this.createUniqueAttackIdentifier(true));
         attackResult = this.getOwnerPatch().attack(modifiedSource, target, hand);
      } else {
         damageSource.addRuntimeTag(this.createUniqueAttackIdentifier(false));
         attackResult = super.attack(damageSource, target, hand);
      }

      this.setDoppelgangerAttackResult(attackResult);
      return attackResult;
   }

   private void setDoppelgangerAttackResult(AttackResult attackResult) {
      try {
         if (resultTypeField != null && damageField != null) {
            resultTypeField.set(this, attackResult.resultType);
            damageField.set(this, attackResult.damage);
         }
      } catch (Exception e) {
         EFN.LOGGER.error("Failed to set doppelganger attack result", e);
      }
   }

   public EpicFightDamageSource getDamageSource(AnimationAccessor<? extends StaticAnimation> animation, InteractionHand hand) {
      if (this.getOwnerPatch() != null) {
         EpicFightDamageSource ownerSource = this.getOwnerPatch().getDamageSource(animation, hand);
         return ownerSource.addRuntimeTag(this.createUniqueAttackIdentifier(true));
      } else {
         return super.getDamageSource(animation, hand).addRuntimeTag(this.createUniqueAttackIdentifier(false));
      }
   }

   private TagKey<DamageType> createUniqueAttackIdentifier(boolean isOwnerAttack) {
      return isOwnerAttack ? DOPPELGANGER_OWNER_ATTACK_TAG : DOPPELGANGER_ATTACK_TAG;
   }

   public void preTick(Pre event) {
      super.preTick();
      PlayerPatch<?> patch = this.getOwnerPatch();
      if (patch != null) {
         LivingEntity ownerTarget = patch.getTarget();
         if (ownerTarget != null && ownerTarget.isAlive()) {
            this.setAttakTargetSync(ownerTarget);
         } else if (this.getTarget() != null) {
            this.setAttakTargetSync(null);
         }

         this.currentLivingMotion = patch.currentLivingMotion;
         SkillContainer innate = patch.getSkill(SkillSlots.WEAPON_INNATE);
         if (innate != null && innate.getDataManager().hasData(EFNSKillDataKeys.DOPPELGANGER_DELAY)) {
            Boolean isDelay = (Boolean)innate.getDataManager().getDataValue(EFNSKillDataKeys.DOPPELGANGER_DELAY);
            if (isDelay != null && !isDelay) {
               if (!patch.getEntityState().inaction() && this.state.inaction()) {
                  AssetAccessor<? extends StaticAnimation> currentAnim = ((DynamicAnimation)this.getAnimator().getPlayerFor(null).getAnimation().get())
                     .getRealAnimation();
                  this.animator.stopPlaying(currentAnim);
               } else {
                  EFN.queueServerWork(
                     20,
                     () -> {
                        if (!patch.getEntityState().inaction() && this.state.inaction()) {
                           AssetAccessor<? extends StaticAnimation> currentAnimx = ((DynamicAnimation)this.getAnimator()
                                 .getPlayerFor(null)
                                 .getAnimation()
                                 .get())
                              .getRealAnimation();
                           this.animator.stopPlaying(currentAnimx);
                        }
                     }
                  );
               }
            }
         }

         ((DoppelgangerEntity)this.getOriginal()).setNoGravity(this.getEntityState().inaction() || ((Player)patch.getOriginal()).isCreative());
      }
   }

   public boolean shouldUseOwnerAttack() {
      return true;
   }

   @Nullable
   public PlayerPatch<?> getOwnerPatch() {
      if (this.ownerPatch != null) {
         return this.ownerPatch;
      } else if (((DoppelgangerEntity)this.getOriginal()).getOwner() != null) {
         this.ownerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(((DoppelgangerEntity)this.getOriginal()).getOwner(), PlayerPatch.class);
         return this.ownerPatch;
      } else {
         return null;
      }
   }

   static {
      try {
         resultTypeField = LivingEntityPatch.class.getDeclaredField("lastAttackResultType");
         damageField = LivingEntityPatch.class.getDeclaredField("lastDealDamage");
         resultTypeField.setAccessible(true);
         damageField.setAccessible(true);
      } catch (Exception e) {
         EFN.LOGGER.error("Failed to init doppelganger reflection fields", e);
      }
   }
}
