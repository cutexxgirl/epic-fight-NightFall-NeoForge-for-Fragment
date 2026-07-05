package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.hm.efn.util.ItemStackData;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.modules.HoldableSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class KusabimaruInnate extends EFNWeaponInnateBase {
   private static final UUID TAKE_DAMAGE_EVENT_ATTACK_UUID = UUID.fromString("f082557a-b2f9-11ab-8529-1242ac130004");
   private static final UUID SWORDOUT_EVENT_HURT_UUID = UUID.fromString("f082557a-b2f9-22ab-8529-1242ac130004");
   private static final UUID SWORDOUT_EVENT_GUARD_UUID = UUID.fromString("f082557a-b2f9-33ab-8529-1242ac130004");
   private static final UUID SWORDOUT_EVENT_DODGE_UUID = UUID.fromString("f082557a-b2f9-44ab-8529-1242ac130004");

   public KusabimaruInnate(Builder builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      EntityEventListener listener = container.getExecutor().getEventListener();
      this.setKusabimaruSheathMesh(container, false);
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_GUARD_UUID, event -> {
         if (EFNSkillChecks.skill(event.getSkillContainer()) instanceof HoldableSkill && this.isHoldingWeapon(container)) {
            this.setKusabimaruSheathMesh(container, false);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.TAKE_DAMAGE_EVENT_ATTACK,
         TAKE_DAMAGE_EVENT_ATTACK_UUID,
         event -> {
            Animator animator = ((ServerPlayerPatch)event.getPlayerPatch()).getAnimator();
            AssetAccessor<? extends StaticAnimation> currentAnim = ((DynamicAnimation)Objects.requireNonNull(animator.getPlayerFor(null)).getAnimation().get())
               .getRealAnimation();
            int entityState = ((ServerPlayerPatch)event.getPlayerPatch()).getEntityState().getLevel();
            boolean skillAnim = currentAnim.equals(EFNSekiroAnimations.ICHIMONJI_1)
               || currentAnim.equals(EFNSekiroAnimations.ICHIMONJI_2)
               || currentAnim.equals(EFNSekiroAnimations.DRAGON_FLASH);
            boolean mortalBlade = currentAnim.equals(EFNSekiroAnimations.MORTAL_BLADE_1) || currentAnim.equals(EFNSekiroAnimations.MORTAL_BLADE_2);
            if (entityState == 1 && skillAnim && event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
               epicFightDamageSource.setStunType(StunType.NONE);
            }

            if (mortalBlade && event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
               epicFightDamageSource.setStunType(StunType.NONE);
            }
         },
         -1
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.TAKE_DAMAGE_EVENT_HURT, SWORDOUT_EVENT_HURT_UUID, event -> this.setKusabimaruSheathMesh(container, false), -1);
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_DODGE_UUID, event -> {
         if (EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.DODGE) && this.isHoldingWeapon(container)) {
            this.setKusabimaruSheathMesh(container, false);
         }
      });
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_HURT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_ATTACK, TAKE_DAMAGE_EVENT_ATTACK_UUID, -1);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.TAKE_DAMAGE_EVENT_HURT, SWORDOUT_EVENT_GUARD_UUID, -1);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_DODGE_UUID);
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, this);
   }

   public void setKusabimaruSheathMesh(SkillContainer container, boolean isSheath) {
      ItemStack mainHandItem = ((Player)container.getExecutor().getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
      ItemStackData.putInt(mainHandItem, "kusabimaru_sheath", isSheath ? 1 : 0);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.kusabimaru.tooltip").withStyle(ChatFormatting.RED));
      list.add(
         Component.translatable("skill.efn.kusabimaru.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.kusabimaru.tooltip2"));
      list.add(Component.translatable("skill.efn.kusabimaru.tooltip3"));
      list.add(Component.translatable("skill.efn.kusabimaru.tooltip4"));
      list.add(Component.translatable("skill.efn.kusabimaru.tooltip5"));
      if (ModList.get().isLoaded("efn_enhance")) {
         list.add(Component.translatable("skill.efn.kusabimaru.tooltip6"));
      }

      return list;
   }
}
