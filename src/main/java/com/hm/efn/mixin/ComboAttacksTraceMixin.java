package com.hm.efn.mixin;

import com.hm.efn.EFN;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.registry.entries.EpicFightSkillDataKeys;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.common.ComboAttacks;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@Mixin(value = ComboAttacks.class, remap = false)
public abstract class ComboAttacksTraceMixin {
   @Inject(method = "executeOnServer", at = @At("HEAD"))
   private void efn$traceExecuteOnServer(SkillContainer skillContainer, CompoundTag args, CallbackInfo ci) {
      ServerPlayerPatch executor = skillContainer.getServerExecutor();
      if (executor == null) {
         EFN.LOGGER.warn("[EFN/InputTrace] server combo execute skipped trace reason=no-executor slot={}", skillContainer.getSlot());
         return;
      }

      ItemStack mainHand = executor.getOriginal().getMainHandItem();
      CapabilityItem capability = executor.getPrimaryItemCapability();
      List<AnimationAccessor<? extends AttackAnimation>> autoAttacks = capability == null || capability.isEmpty()
         ? null
         : capability.getAutoAttackMotion(executor);
      int comboCounter = skillContainer.getDataManager().hasData(EpicFightSkillDataKeys.COMBO_COUNTER)
         ? skillContainer.getDataManager().getDataValue(EpicFightSkillDataKeys.COMBO_COUNTER)
         : -1;
      EFN.LOGGER.info(
         "[EFN/InputTrace] server combo execute slot={} skill={} item={} primaryHand={} capEmpty={} category={} autoCount={} comboCounter={} canBasic={} inAir={} onGround={} sprinting={} args={}",
         skillContainer.getSlot(),
         skillContainer.getSkill() == null ? "null" : skillContainer.getSkill().getRegistryName(),
         mainHand.isEmpty() ? "empty" : BuiltInRegistries.ITEM.getKey(mainHand.getItem()),
         executor.getPrimaryHand(),
         capability == null || capability.isEmpty(),
         capability == null || capability.isEmpty() ? "empty" : capability.getWeaponCategory(),
         autoAttacks == null ? 0 : autoAttacks.size(),
         comboCounter,
         executor.getEntityState().canBasicAttack(),
         executor.isInAir(),
         executor.getOriginal().onGround(),
         executor.getOriginal().isSprinting(),
         args
      );
   }
}
