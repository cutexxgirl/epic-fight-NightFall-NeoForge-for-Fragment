package com.hm.efn.gameasset;

import com.guhao.efn_enhance.gameassets.animations.EFN_ESekiroAnimations;
import com.hm.efn.capability.AdvanceWeaponCapability;
import com.hm.efn.gameasset.animations.EFNBroadBladeAnimations;
import com.hm.efn.gameasset.animations.EFNClawAnimations;
import com.hm.efn.gameasset.animations.EFNDualSwordAnimations;
import com.hm.efn.gameasset.animations.EFNExsiliumgladiusAnimations;
import com.hm.efn.gameasset.animations.EFNFalchionAnimations;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import com.hm.efn.gameasset.animations.EFNLanceAnimations;
import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.hm.efn.gameasset.animations.EFNScytheAnimations;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import com.hm.efn.gameasset.animations.EFNShortSwordAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.gameasset.animations.EFNTachiAnimations;
import com.hm.efn.gameasset.animations.EFNThornWheelAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.hm.efn.gameasset.combos.Aetherialdusk;
import com.hm.efn.gameasset.combos.Beastclaw;
import com.hm.efn.gameasset.combos.Bloodlust;
import com.hm.efn.gameasset.combos.BroadBlade;
import com.hm.efn.gameasset.combos.CrescentMoon;
import com.hm.efn.gameasset.combos.Exsiliumgladius;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Kusabimaru;
import com.hm.efn.gameasset.combos.Kusabimaru_Enhance;
import com.hm.efn.gameasset.combos.Meenlance;
import com.hm.efn.gameasset.combos.Murasama;
import com.hm.efn.gameasset.combos.Pioneer;
import com.hm.efn.gameasset.combos.Ruinsgreatsword;
import com.hm.efn.gameasset.combos.Shortsword;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.item.custom.ExsiliumgladiusItem;
import com.hm.efn.item.custom.FireExsiliumgladiusItem;
import com.hm.efn.particle.EFNParticles;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.event.types.registry.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class EFNWeaponCapabilityPresets {
   public static final Function<Item, ? extends Builder<?>> RUINSGREATSWORD = item -> WeaponCapability.builder()
      .category(WeaponCategories.GREATSWORD)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(new OBBCollider(0.75, 0.75, 1.75, 0.0, 0.0, -1.0))
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{
            EFNGreatSwordAnimations.NG_GREATSWORD_AUTO1,
            EFNGreatSwordAnimations.NG_GREATSWORD_AUTO2,
            EFNGreatSwordAnimations.NG_GREATSWORD_AUTO3,
            EFNGreatSwordAnimations.NG_GREATSWORD_DASH,
            EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW
         }
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Ruinsgreatsword.Ruinsgreatsword)
      .passiveSkill(Ruinsgreatsword.Ruinsgreatsword_passive)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.GREATSWORD_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNGreatSwordAnimations.NG_GREATSWORD_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNGreatSwordAnimations.NG_GREATSWOED_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNGreatSwordAnimations.NG_GREATSWORD_RUN)
      .reach(0.7F)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> THORNWHEEL = item -> WeaponCapability.builder()
      .category(WeaponCategories.GREATSWORD)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(new OBBCollider(0.75, 0.75, 1.75, 0.0, 0.0, -1.0))
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .reach(0.6F)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{
            EFNThornWheelAnimations.THORNWHEEL_AUTO1,
            EFNThornWheelAnimations.THORNWHEEL_AUTO2,
            EFNThornWheelAnimations.THORNWHEEL_AUTO3,
            Animations.GREATSWORD_DASH,
            Animations.GREATSWORD_AIR_SLASH
         }
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> EFNSkills.THORNWHEEL)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.GREATSWORD_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNThornWheelAnimations.THORNWHEEL_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNGreatSwordAnimations.NG_GREATSWOED_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNGreatSwordAnimations.NG_GREATSWORD_RUN);
   public static final Function<Item, ? extends Builder<?>> CRESCENTMOON = item -> WeaponCapability.builder()
      .category(WeaponCategories.LONGSWORD)
      .styleProvider(entityPatch -> EFNStyles.FALCHION)
      .collider(EFNFalchionAnimations.FALCHION_ATTACK_COLL)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .reach(0.3F)
      .newStyleCombo(
         EFNStyles.FALCHION,
         new AnimationAccessor[]{
            EFNFalchionAnimations.FALCHION_AUTO1,
            EFNFalchionAnimations.FALCHION_AUTO2,
            EFNFalchionAnimations.FALCHION_AUTO3,
            EFNFalchionAnimations.FALCHION_DASHATTACK,
            EFNFalchionAnimations.FALCHION_AIRSLASH
         }
      )
      .innateSkill(EFNStyles.FALCHION, itemstack -> CrescentMoon.crescentmoon)
      .livingMotionModifier(EFNStyles.FALCHION, LivingMotions.BLOCK, Animations.SPEAR_GUARD)
      .livingMotionModifier(EFNStyles.FALCHION, LivingMotions.IDLE, EFNFalchionAnimations.FALCHION_IDLE)
      .livingMotionModifier(EFNStyles.FALCHION, LivingMotions.WALK, Animations.BIPED_WALK_SPEAR)
      .livingMotionModifier(EFNStyles.FALCHION, LivingMotions.RUN, EFNFalchionAnimations.FALCHION_RUN);
   public static final Function<Item, ? extends Builder<?>> MEENLANCE = item -> WeaponCapability.builder()
      .category(WeaponCategories.SPEAR)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(EFNLanceAnimations.MEEN_LANCE_COLL)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{
            EFNLanceAnimations.NF_MEEN_AUTO1,
            EFNLanceAnimations.NF_MEEN_AUTO2,
            EFNLanceAnimations.NF_MEEN_AUTO3,
            EFNLanceAnimations.NF_MEEN_DASH,
            EFNLanceAnimations.NF_MEEN_AIRSLASH
         }
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Meenlance.Meenlance)
      .passiveSkill(Meenlance.Meenlance_passive)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SPEAR_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNLanceAnimations.NF_MEEN_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNLanceAnimations.NF_MEEN_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNLanceAnimations.NF_MEEN_RUN)
      .reach(0.0F)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> AETHERIAL_DUSK_DUALSWORD = item -> WeaponCapability.builder()
      .category(WeaponCategories.SWORD)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(ColliderPreset.UCHIGATANA)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{
            EFNDualSwordAnimations.NF_DUAL_AUTO1,
            EFNDualSwordAnimations.NF_DUAL_AUTO2,
            EFNDualSwordAnimations.NF_DUAL_AUTO3,
            EFNDualSwordAnimations.NF_DUAL_AUTO4,
            EFNDualSwordAnimations.NF_DUAL_DASH,
            EFNDualSwordAnimations.NF_DUAL_AIRSLASH
         }
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Aetherialdusk.Aetherialdusk)
      .passiveSkill(Aetherialdusk.Aetherialdusk_Passive)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SWORD_DUAL_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNDualSwordAnimations.NF_DUAL_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNDualSwordAnimations.NF_DUAL_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNDualSwordAnimations.NF_DUAL_RUN)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> EXSILIUMGLADIUS = item -> WeaponCapability.builder()
      .category(WeaponCategories.SWORD)
      .styleProvider(playerpatch -> {
         CapabilityItem mainHandCap = playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
         CapabilityItem offHandCap = playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND);
         ItemStack mainHandStack = ((LivingEntity)playerpatch.getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
         ItemStack offHandStack = ((LivingEntity)playerpatch.getOriginal()).getItemInHand(InteractionHand.OFF_HAND);
         boolean isSpecialCombo = mainHandStack.getItem() instanceof ExsiliumgladiusItem && offHandStack.getItem() instanceof FireExsiliumgladiusItem;
         return isSpecialCombo && offHandCap.getWeaponCategory() == WeaponCategories.SWORD ? Styles.TWO_HAND : Styles.ONE_HAND;
      })
      .collider(ColliderPreset.TACHI)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(true)
      .newStyleCombo(
         Styles.ONE_HAND,
         new AnimationAccessor[]{Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH}
      )
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH}
      )
      .innateSkill(Styles.ONE_HAND, itemstack -> EpicFightSkills.SWEEPING_EDGE.get())
      .innateSkill(Styles.TWO_HAND, itemstack -> Exsiliumgladius.exsiliumgladius)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SWORD_DUAL_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_RUN)
      .comboCancel(style -> false)
      .weaponCombinationPredicator(
         entitypatch -> EpicFightCapabilities.getItemStackCapability(((LivingEntity)entitypatch.getOriginal()).getOffhandItem()).getWeaponCategory()
            == WeaponCategories.SWORD
      );
   public static final Function<Item, ? extends Builder<?>> PIONEER = item -> WeaponCapability.builder()
      .category(WeaponCategories.UCHIGATANA)
      .styleProvider(entityPatch -> Styles.ONE_HAND)
      .collider(ColliderPreset.UCHIGATANA)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.ONE_HAND,
         new AnimationAccessor[]{
            EFNSwordAnimations.NF_SWORD_AUTO1,
            EFNSwordAnimations.NF_SWORD_AUTO2,
            EFNSwordAnimations.NF_SWORD_AUTO3,
            EFNSwordAnimations.NF_SWORD_AUTO4,
            EFNSwordAnimations.NF_SWORD_DASH,
            EFNSwordAnimations.NF_SWORD_AIRSLASH
         }
      )
      .innateSkill(Styles.ONE_HAND, itemstack -> Pioneer.pioneer)
      .livingMotionModifier(Styles.ONE_HAND, LivingMotions.BLOCK, EFNSwordAnimations.NF_SWORD_GUARD)
      .livingMotionModifier(Styles.ONE_HAND, LivingMotions.IDLE, EFNSwordAnimations.NF_SWORD_IDLE)
      .livingMotionModifier(Styles.ONE_HAND, LivingMotions.WALK, EFNSwordAnimations.NF_SWORD_WALK)
      .livingMotionModifier(Styles.ONE_HAND, LivingMotions.RUN, EFNSwordAnimations.NF_SWORD_RUN)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> BROADBLADE = item -> WeaponCapability.builder()
      .category(WeaponCategories.LONGSWORD)
      .styleProvider(entityPatch -> EFNStyles.BOARD_BLADE)
      .collider(EFNBroadBladeAnimations.BROADBLADE)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .reach(0.01F)
      .newStyleCombo(
         EFNStyles.BOARD_BLADE,
         new AnimationAccessor[]{Animations.TACHI_AUTO1, Animations.TACHI_AUTO2, Animations.TACHI_AUTO3, Animations.TACHI_DASH, Animations.LONGSWORD_AIR_SLASH}
      )
      .innateSkill(EFNStyles.BOARD_BLADE, itemstack -> BroadBlade.broadblade)
      .livingMotionModifier(EFNStyles.BOARD_BLADE, LivingMotions.IDLE, EFNBroadBladeAnimations.BROADBLADE_IDLE)
      .livingMotionModifier(EFNStyles.BOARD_BLADE, LivingMotions.KNEEL, EFNBroadBladeAnimations.BROADBLADE_KNEEL)
      .livingMotionModifier(EFNStyles.BOARD_BLADE, LivingMotions.SNEAK, EFNBroadBladeAnimations.BROADBLADE_SNEAK)
      .livingMotionModifier(EFNStyles.BOARD_BLADE, LivingMotions.WALK, EFNBroadBladeAnimations.BROADBLADE_WALK)
      .livingMotionModifier(EFNStyles.BOARD_BLADE, LivingMotions.RUN, EFNBroadBladeAnimations.BROADBLADE_RUN)
      .livingMotionModifier(EFNStyles.BOARD_BLADE, LivingMotions.BLOCK, EFNBroadBladeAnimations.BROADBLADE_GUARD);
   public static final Function<Item, ? extends Builder<?>> SHORTSWORD = item -> WeaponCapability.builder()
      .category(WeaponCategories.SWORD)
      .styleProvider(entityPatch -> Styles.ONE_HAND)
      .collider(ColliderPreset.SWORD)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.ONE_HAND,
         new AnimationAccessor[]{
            EFNShortSwordAnimations.NF_SHORTSWORD_AUTO1,
            EFNShortSwordAnimations.NF_SHORTSWORD_AUTO2,
            EFNShortSwordAnimations.NF_SHORTSWORD_AUTO3,
            EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,
            EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,
            EFNShortSwordAnimations.NF_SHORTSWORD_AUTO6,
            EFNShortSwordAnimations.NF_SHORTSWORD_DASH,
            EFNShortSwordAnimations.NF_SHORTSWORD_AIRSLASH
         }
      )
      .innateSkill(Styles.ONE_HAND, itemstack -> Shortsword.shortsword)
      .passiveSkill(Shortsword.shortsword_passive)
      .livingMotionModifier(Styles.ONE_HAND, LivingMotions.BLOCK, EFNSwordAnimations.NF_SWORD_GUARD)
      .livingMotionModifier(Styles.ONE_HAND, LivingMotions.IDLE, EFNSwordAnimations.NF_SWORD_IDLE)
      .livingMotionModifier(Styles.ONE_HAND, LivingMotions.WALK, EFNSwordAnimations.NF_SWORD_WALK)
      .livingMotionModifier(Styles.ONE_HAND, LivingMotions.RUN, EFNSwordAnimations.NF_SWORD_RUN)
      .reach(0.1F)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> BLOODLUST = item -> WeaponCapability.builder()
      .category(WeaponCategories.TACHI)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(ColliderPreset.TACHI)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EFNParticles.BLOOD_HIT.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{
            EFNTachiAnimations.NF_TACHI_AUTO1,
            EFNTachiAnimations.NF_TACHI_AUTO2,
            EFNTachiAnimations.NF_TACHI_AUTO3,
            EFNTachiAnimations.NF_TACHI_AUTO4,
            EFNTachiAnimations.NF_TACHI_AUTO5,
            EFNTachiAnimations.NF_TACHI_DASH,
            EFNTachiAnimations.NF_TACHI_AIRSLASH
         }
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Bloodlust.Bloodlust)
      .passiveSkill(Bloodlust.Bloodlust_Passive)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.LONGSWORD_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNTachiAnimations.NF_TACHI_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNTachiAnimations.NF_TACHI_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNTachiAnimations.NF_TACHI_RUN)
      .reach(1.0F)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> KUSABIMARU = item -> WeaponCapability.builder()
      .category(WeaponCategories.UCHIGATANA)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(ColliderPreset.UCHIGATANA)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{
            EFNSekiroAnimations.KUSABIMARU_AUTO1,
            EFNSekiroAnimations.KUSABIMARU_AUTO2,
            EFNSekiroAnimations.KUSABIMARU_AUTO3,
            EFNSekiroAnimations.KUSABIMARU_AUTO4,
            EFNSekiroAnimations.KUSABIMARU_AUTO5,
            EFNSwordAnimations.NF_SWORD_DASH,
            EFNTachiAnimations.NF_TACHI_AIRSLASH
         }
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Kusabimaru.kusabimaru)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, EFNSwordAnimations.NF_SWORD_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNSekiroAnimations.KUSABIMARU_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNSwordAnimations.NF_SWORD_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNSwordAnimations.NF_SWORD_RUN);
   private static final MultiOBBCollider KUSABIMARU_ENHANCE_COLLIDER = new MultiOBBCollider(7, 0.4, 0.4, 1.85, 0.0, 0.0, -0.7);
   public static final Function<Item, ? extends Builder<?>> KUSABIMARU_ENHANCE = item -> WeaponCapability.builder()
      .category(WeaponCategories.UCHIGATANA)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(KUSABIMARU_ENHANCE_COLLIDER)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{
            EFN_ESekiroAnimations.KUSABIMARU_AUTO1,
            EFN_ESekiroAnimations.KUSABIMARU_AUTO2,
            EFN_ESekiroAnimations.KUSABIMARU_AUTO3,
            EFN_ESekiroAnimations.KUSABIMARU_AUTO4,
            EFN_ESekiroAnimations.KUSABIMARU_AUTO5,
            EFNSwordAnimations.NF_SWORD_DASH,
            EFNTachiAnimations.NF_TACHI_AIRSLASH
         }
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Kusabimaru_Enhance.kusabimaru_enhance)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, EFNSwordAnimations.NF_SWORD_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNSekiroAnimations.KUSABIMARU_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNSwordAnimations.NF_SWORD_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNSwordAnimations.NF_SWORD_RUN);
   public static final Function<Item, ? extends Builder<?>> HF_MURASAMA = item -> AdvanceWeaponCapability.builder()
      .category(WeaponCategories.TACHI)
      .styleProvider(
         entityPatch -> {
            if (entityPatch instanceof PlayerPatch<?> playerPatch) {
               SkillContainer container = playerPatch.getSkill(Murasama.Murasama_Passive);
               if (container != null
                  && !container.isEmpty()
                  && container.getDataManager().hasData(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT)
                  && (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT)) {
                  return Styles.COMMON;
               }

               if (container != null
                  && !container.isEmpty()
                  && container.getDataManager().hasData(EFNSKillDataKeys.MURASAMA_SHEATH)
                  && (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_SHEATH)) {
                  return Styles.SHEATH;
               }
            }

            return Styles.TWO_HAND;
         }
      )
      .collider(new MultiOBBCollider(3, 0.45, 0.45, 1.25, 0.0, 0.0, -0.95))
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EFNParticles.MURASAMA_HIT.get())
      .canBePlacedOffhand(false)
      .newAdvanceStyleCombo(
         Styles.TWO_HAND, Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH
      )
      .newAdvanceStyleCombo(
         Styles.COMMON, Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH
      )
      .newAdvanceStyleCombo(
         Styles.SHEATH, Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Murasama.Murasama)
      .innateSkill(Styles.COMMON, itemstack -> Murasama.Murasama)
      .innateSkill(Styles.SHEATH, itemstack -> Murasama.Murasama)
      .passiveSkill(Murasama.Murasama_Passive)
      .exclusiveDodge(EFNSkills.MURASAMA_DODGE)
      .exclusiveGuard(EFNSkills.MURASAMA_PARRY)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, EFNMurasamaAnimations.HF_MURASAMA_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNMurasamaAnimations.HF_MURASAMA_IDLE_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.CREATIVE_IDLE, EFNMurasamaAnimations.HF_MURASAMA_IDLE_AIR)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, EFNMurasamaAnimations.HF_MURASAMA_JUMP_FIRST)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.FALL, EFNMurasamaAnimations.HF_MURASAMA_FALL_FIRST)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.LANDING_RECOVERY, EFNMurasamaAnimations.HF_MURASAMA_IDLE_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNMurasamaAnimations.HF_MURASAMA_WALK_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNMurasamaAnimations.HF_MURASAMA_RUN_COMBAT_1)
      .livingMotionModifier(Styles.COMMON, LivingMotions.BLOCK, EFNMurasamaAnimations.HF_MURASAMA_GUARD)
      .livingMotionModifier(Styles.COMMON, LivingMotions.IDLE, EFNMurasamaAnimations.HF_MURASAMA_IDLE_COMBAT)
      .livingMotionModifier(Styles.COMMON, LivingMotions.CREATIVE_IDLE, EFNMurasamaAnimations.HF_MURASAMA_IDLE_AIR)
      .livingMotionModifier(Styles.COMMON, LivingMotions.JUMP, EFNMurasamaAnimations.HF_MURASAMA_JUMP_FIRST)
      .livingMotionModifier(Styles.COMMON, LivingMotions.FALL, EFNMurasamaAnimations.HF_MURASAMA_FALL_FIRST)
      .livingMotionModifier(Styles.COMMON, LivingMotions.LANDING_RECOVERY, EFNMurasamaAnimations.HF_MURASAMA_IDLE_COMBAT)
      .livingMotionModifier(Styles.COMMON, LivingMotions.WALK, EFNMurasamaAnimations.HF_MURASAMA_WALK_COMBAT)
      .livingMotionModifier(Styles.COMMON, LivingMotions.RUN, EFNMurasamaAnimations.HF_MURASAMA_RUN_COMBAT_2)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.BLOCK, EFNMurasamaAnimations.HF_MURASAMA_GUARD)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.IDLE, EFNMurasamaAnimations.HF_MURASAMA_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.CREATIVE_IDLE, EFNMurasamaAnimations.HF_MURASAMA_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.KNEEL, EFNMurasamaAnimations.HF_MURASAMA_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.SNEAK, EFNMurasamaAnimations.HF_MURASAMA_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.LANDING_RECOVERY, EFNMurasamaAnimations.HF_MURASAMA_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.SWIM, EFNMurasamaAnimations.HF_MURASAMA_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.FLOAT, EFNMurasamaAnimations.HF_MURASAMA_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.JUMP, Animations.BIPED_JUMP)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.FALL, EFNMurasamaAnimations.HF_MURASAMA_FALL_FIRST)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.WALK, EFNMurasamaAnimations.HF_MURASAMA_WALK_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.CHASE, EFNMurasamaAnimations.HF_MURASAMA_RUN_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.RUN, EFNMurasamaAnimations.HF_MURASAMA_RUN_SHEATH)
      .reach(0.35F)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> HF_BLADE = item -> AdvanceWeaponCapability.builder()
      .category(WeaponCategories.TACHI)
      .styleProvider(
         entityPatch -> {
            if (entityPatch instanceof PlayerPatch<?> playerPatch) {
               SkillContainer container = playerPatch.getSkill(HfBlade.HfBlade_Passive);
               if (container != null
                  && !container.isEmpty()
                  && container.getDataManager().hasData(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT)
                  && (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT)) {
                  return Styles.COMMON;
               }

               if (container != null
                  && !container.isEmpty()
                  && container.getDataManager().hasData(EFNSKillDataKeys.MURASAMA_SHEATH)
                  && (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.MURASAMA_SHEATH)) {
                  return Styles.SHEATH;
               }
            }

            return Styles.TWO_HAND;
         }
      )
      .collider(new MultiOBBCollider(3, 0.45, 0.45, 1.25, 0.0, 0.0, -0.95))
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EFNParticles.HF_BLADE_HIT.get())
      .canBePlacedOffhand(false)
      .newAdvanceStyleCombo(
         Styles.TWO_HAND, Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH
      )
      .newAdvanceStyleCombo(
         Styles.COMMON, Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH
      )
      .newAdvanceStyleCombo(
         Styles.SHEATH, Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> HfBlade.HfBlade)
      .innateSkill(Styles.COMMON, itemstack -> HfBlade.HfBlade)
      .innateSkill(Styles.SHEATH, itemstack -> HfBlade.HfBlade)
      .passiveSkill(HfBlade.HfBlade_Passive)
      .exclusiveDodge(EFNSkills.MURASAMA_DODGE)
      .exclusiveGuard(EFNSkills.MURASAMA_PARRY)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, EFNHfBladeAnimations.HF_BLADE_GUARD)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.CREATIVE_IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_AIR)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, EFNHfBladeAnimations.HF_BLADE_JUMP_FIRST)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.FALL, EFNHfBladeAnimations.HF_BLADE_FALL_FIRST)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.LANDING_RECOVERY, EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNHfBladeAnimations.HF_BLADE_WALK_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNHfBladeAnimations.HF_BLADE_RUN_COMBAT_1)
      .livingMotionModifier(Styles.COMMON, LivingMotions.BLOCK, EFNHfBladeAnimations.HF_BLADE_GUARD)
      .livingMotionModifier(Styles.COMMON, LivingMotions.IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT)
      .livingMotionModifier(Styles.COMMON, LivingMotions.CREATIVE_IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_AIR)
      .livingMotionModifier(Styles.COMMON, LivingMotions.JUMP, EFNHfBladeAnimations.HF_BLADE_JUMP_FIRST)
      .livingMotionModifier(Styles.COMMON, LivingMotions.FALL, EFNHfBladeAnimations.HF_BLADE_FALL_FIRST)
      .livingMotionModifier(Styles.COMMON, LivingMotions.LANDING_RECOVERY, EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT)
      .livingMotionModifier(Styles.COMMON, LivingMotions.WALK, EFNHfBladeAnimations.HF_BLADE_WALK_COMBAT)
      .livingMotionModifier(Styles.COMMON, LivingMotions.RUN, EFNHfBladeAnimations.HF_BLADE_RUN_COMBAT_2)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.BLOCK, EFNHfBladeAnimations.HF_BLADE_GUARD)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.CREATIVE_IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.KNEEL, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.SNEAK, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.LANDING_RECOVERY, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.SWIM, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.FLOAT, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.JUMP, Animations.BIPED_JUMP)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.FALL, EFNHfBladeAnimations.HF_BLADE_FALL_FIRST)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.WALK, EFNHfBladeAnimations.HF_BLADE_WALK_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.CHASE, EFNHfBladeAnimations.HF_BLADE_RUN_SHEATH)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.RUN, EFNHfBladeAnimations.HF_BLADE_RUN_SHEATH)
      .reach(0.35F)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> BEASTCLAW = item -> WeaponCapability.builder()
      .category(WeaponCategories.FIST)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(ColliderPreset.TACHI)
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
      .canBePlacedOffhand(false)
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH}
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Beastclaw.beastclaw)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNClawAnimations.NF_CLAW_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNClawAnimations.NF_CLAW_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNClawAnimations.NF_CLAW_RUN)
      .reach(0.1F)
      .comboCancel(style -> false);
   public static final Function<Item, ? extends Builder<?>> SCYTHE = item -> WeaponCapability.builder()
      .category(WeaponCategories.LONGSWORD)
      .styleProvider(
         entitypatch -> {
            if (entitypatch instanceof PlayerPatch<?> playerpatch) {
               SkillContainer container = playerpatch.getSkill(EFNSkills.SCYTHE);
               if (container != null
                  && !container.isEmpty()
                  && (Boolean)container.getDataManager().getDataValue(EFNSKillDataKeys.SCYTHE_SHEATH)) {
                  return Styles.SHEATH;
               }
            }

            return Styles.TWO_HAND;
         }
      )
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .collider(ColliderPreset.LONGSWORD)
      .canBePlacedOffhand(false)
      .reach(0.45F)
      .newStyleCombo(
         Styles.SHEATH, new AnimationAccessor[]{EFNScytheAnimations.SCYTHE_AUTO1, EFNScytheAnimations.SCYTHE_AUTO1, EFNScytheAnimations.SCYTHE_AUTO1}
      )
      .newStyleCombo(
         Styles.TWO_HAND,
         new AnimationAccessor[]{
            EFNScytheAnimations.SCYTHE_AUTO1,
            EFNScytheAnimations.SCYTHE_AUTO2,
            EFNScytheAnimations.SCYTHE_AUTO3,
            EFNScytheAnimations.SCYTHE_AUTO4,
            EFNScytheAnimations.SCYTHE_AUTO5,
            EFNScytheAnimations.SCYTHE_DASH,
            EFNScytheAnimations.SCYTHE_AIR_SLASH
         }
      )
      .innateSkill(Styles.SHEATH, itemstack -> EFNSkills.SCYTHE)
      .innateSkill(Styles.TWO_HAND, itemstack -> EFNSkills.SCYTHE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, EFNScytheAnimations.SCYTHE_BLOCK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNScytheAnimations.SCYTHE_IDLE_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNScytheAnimations.SCYTHE_WALK_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, EFNScytheAnimations.SCYTHE_RUN_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNScytheAnimations.SCYTHE_RUN_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, EFNScytheAnimations.SCYTHE_IDLE_COMBAT)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, EFNScytheAnimations.SCYTHE_WALK_COMBAT)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.IDLE, EFNScytheAnimations.SCYTHE_IDLE_NORMAL)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.WALK, EFNScytheAnimations.SCYTHE_WALK_NORMAL)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.CHASE, EFNScytheAnimations.SCYTHE_RUN_NORMAL)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.RUN, EFNScytheAnimations.SCYTHE_RUN_NORMAL)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.KNEEL, EFNScytheAnimations.SCYTHE_IDLE_NORMAL)
      .livingMotionModifier(Styles.SHEATH, LivingMotions.SNEAK, EFNScytheAnimations.SCYTHE_WALK_NORMAL);
   public static final Function<Item, ? extends Builder<?>> YAMATO = item -> AdvanceWeaponCapability.builder()
      .category(EFNWeaponCategories.EFN_YAMATO)
      .styleProvider(entityPatch -> Styles.TWO_HAND)
      .collider(new MultiOBBCollider(3, 0.4, 0.4, 1.35, 0.0, 0.0, -0.95))
      .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
      .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
      .hitParticle((HitParticleType)EFNParticles.ARC_HIT.get())
      .canBePlacedOffhand(false)
      .newAdvanceStyleCombo(
         Styles.TWO_HAND, Animations.SWORD_AUTO1, Animations.SWORD_AUTO2, Animations.SWORD_AUTO3, Animations.SWORD_DASH, Animations.SWORD_AIR_SLASH
      )
      .innateSkill(Styles.TWO_HAND, itemstack -> Yamato.yamato)
      .passiveSkill(Yamato.yamato_passive)
      .exclusiveDodge(EFNSkills.YAMATO_DODGE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EFNYamatoAnimations.YAMATO_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, EFNYamatoAnimations.YAMATO_KNEEL)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, EFNYamatoAnimations.YAMATO_SNEAK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, EFNYamatoAnimations.YAMATO_JUMP)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.CREATIVE_IDLE, EFNYamatoAnimations.YAMATO_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.LANDING_RECOVERY, EFNYamatoAnimations.YAMATO_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.FALL, EFNYamatoAnimations.YAMATO_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.FLOAT, EFNYamatoAnimations.YAMATO_IDLE)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EFNYamatoAnimations.YAMATO_WALK)
      .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EFNYamatoAnimations.YAMATO_RUN)
      .comboCancel(style -> false);

   public static void register(WeaponCapabilityPresetRegistryEvent event) {
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "ruinsgreatsword"), RUINSGREATSWORD);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "thornwheel"), THORNWHEEL);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "crescentmoon"), CRESCENTMOON);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "meenlance"), MEENLANCE);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "aetherialdusk"), AETHERIAL_DUSK_DUALSWORD);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "exsiliumgladius"), EXSILIUMGLADIUS);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "pioneer"), PIONEER);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "broadblade"), BROADBLADE);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "shortsword"), SHORTSWORD);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "bloodlust"), BLOODLUST);
      if (ModList.get().isLoaded("efn_enhance")) {
         event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "kusabimaru"), KUSABIMARU_ENHANCE);
      } else {
         event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "kusabimaru"), KUSABIMARU);
      }

      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "murasama"), HF_MURASAMA);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "hf_blade"), HF_BLADE);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "beastclaw"), BEASTCLAW);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "scythe"), SCYTHE);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("efn", "yamato"), YAMATO);
   }
}
