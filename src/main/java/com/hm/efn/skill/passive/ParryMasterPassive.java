package com.hm.efn.skill.passive;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.registry.entries.EpicFightSkills;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class ParryMasterPassive extends PassiveSkill {
   private static final UUID EVENT_UUID = UUID.fromString("a1b2c3d4-5678-90ef-1234-56789abcdef0");
   private static final UUID DAMAGE_EVENT_UUID = UUID.fromString("b1b2c3d4-5678-90ef-1234-56789abcdef0");
   private static final List<ParryMasterPassive.DelayedAnimationTask> ANIMATION_QUEUE = new ArrayList<>();
   private float chargeBonus = 0.05F;
   private float basePunctureChance = 0.1F;
   private float punctureChanceIncrement = 0.02F;
   private float maxPunctureChance = 0.4F;
   private float punctureResetThreshold = 0.2F;

   public ParryMasterPassive(SkillBuilder<?> builder) {
      super(builder);
   }

   public static ParryMasterPassive.Builder createParryMasterPassiveBuilder() {
      return (ParryMasterPassive.Builder)new ParryMasterPassive.Builder().setCategory(SkillCategories.PASSIVE).setResource(Resource.COOLDOWN);
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.chargeBonus = parameters.contains("charge_bonus") ? parameters.getFloat("charge_bonus") : this.chargeBonus;
      this.basePunctureChance = parameters.contains("base_puncture_chance") ? parameters.getFloat("base_puncture_chance") : this.basePunctureChance;
      this.punctureChanceIncrement = parameters.contains("puncture_chance_increment")
         ? parameters.getFloat("puncture_chance_increment")
         : this.punctureChanceIncrement;
      this.maxPunctureChance = parameters.contains("max_puncture_chance") ? parameters.getFloat("max_puncture_chance") : this.maxPunctureChance;
      this.punctureResetThreshold = parameters.contains("puncture_reset_threshold")
         ? parameters.getFloat("puncture_reset_threshold")
         : this.punctureResetThreshold;
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      SkillDataManager data = container.getDataManager();
      data.setData(EFNSKillDataKeys.BASE_PUNCTURE_CHANCE, this.basePunctureChance);
      data.setData(EFNSKillDataKeys.PUNCTURE_INCREMENT, this.punctureChanceIncrement);
      data.setData(EFNSKillDataKeys.MAX_PUNCTURE_CHANCE, this.maxPunctureChance);
      data.setData(EFNSKillDataKeys.CURRENT_PUNCTURE_CHANCE, this.basePunctureChance);
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, event -> {
         if (event.isParried() && !container.getExecutor().isLogicalClient()) {
            ServerPlayer player = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
            SkillContainer weaponSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(SkillSlots.WEAPON_INNATE);
            if (weaponSkillContainer != null && weaponSkillContainer.getSkill() != null) {
               float currentCharge = weaponSkillContainer.getResource();
               float maxCharge = weaponSkillContainer.getMaxResource();
               float newCharge = Math.min(maxCharge, currentCharge + maxCharge * this.chargeBonus);
               weaponSkillContainer.getSkill().setConsumptionSynchronize(weaponSkillContainer, newCharge);
            }

            float currentChance = (Float)data.getDataValue(EFNSKillDataKeys.CURRENT_PUNCTURE_CHANCE);
            float increment = (Float)data.getDataValue(EFNSKillDataKeys.PUNCTURE_INCREMENT);
            float maxChance = (Float)data.getDataValue(EFNSKillDataKeys.MAX_PUNCTURE_CHANCE);
            float newChance = Math.min(maxChance, currentChance + increment);
            data.setDataSync(EFNSKillDataKeys.CURRENT_PUNCTURE_CHANCE, newChance);
         }
      }, -1);
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.DEAL_DAMAGE_EVENT_ATTACK,
         DAMAGE_EVENT_UUID,
         event -> {
            if (!container.getExecutor().isLogicalClient() && container.getStack() > 0 && event.getDamageSource() instanceof EpicFightDamageSource) {
               if (event.getTarget() instanceof VFXEntity) {
                  return;
               }

               ServerPlayer player = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
               float currentChance = (Float)data.getDataValue(EFNSKillDataKeys.CURRENT_PUNCTURE_CHANCE);
               float randomValue = player.getRandom().nextFloat();
               boolean success = randomValue < currentChance;
               if (success) {
                  EpicFightDamageSource damageSource = event.getDamageSource();
                  damageSource.addRuntimeTag(EpicFightDamageTypeTags.GUARD_PUNCTURE);
                  damageSource.addRuntimeTag(EpicFightDamageTypeTags.BYPASS_DODGE);
                  damageSource.attachDamageModifier(ValueModifier.multiplier(2.0F));
                  damageSource.setBaseArmorNegation(100.0F);
                  damageSource.setStunType(StunType.LONG);
                  ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                     .addEffect(new MobEffectInstance(EFNMobEffectRegistry.INVINCIBILITY_EFFECT, 30, 0, false, false, false));
                  ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EpicFightSounds.NEUTRALIZE_MOBS.get(), 0.0F, 0.0F);
                  ANIMATION_QUEUE.add(new ParryMasterPassive.DelayedAnimationTask((ServerPlayerPatch)event.getPlayerPatch(), 6));
                  this.setStackSynchronize(container, container.getStack() - 1);
                  if (currentChance >= this.punctureResetThreshold) {
                     float baseChance = (Float)data.getDataValue(EFNSKillDataKeys.BASE_PUNCTURE_CHANCE);
                     data.setDataSync(EFNSKillDataKeys.CURRENT_PUNCTURE_CHANCE, baseChance);
                  }
               }
            }
         },
         -1
      );
   }

   public void onRemoved(SkillContainer container) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, -1);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.DEAL_DAMAGE_EVENT_ATTACK, DAMAGE_EVENT_UUID, -1);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      PoseStack poseStack = guiGraphics.pose();
      poseStack.pushPose();
      guiGraphics.blit(this.getSkillTexture(), (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
      float currentChance = (Float)container.getDataManager().getDataValue(EFNSKillDataKeys.CURRENT_PUNCTURE_CHANCE);
      String displayText = String.format("%d%%", (int)(currentChance * 100.0F));
      guiGraphics.drawString(gui.getFont(), displayText, x + 12.0F - gui.getFont().width(displayText) / 2.0F, y + 14.0F, 16777215, true);
      poseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDraw(SkillContainer container) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public List<Object> getTooltipArgsOfScreen(List<Object> list) {
      list.add(String.format("%.1f", this.basePunctureChance));
      return list;
   }

   public Skill getPriorSkill() {
      return EpicFightSkills.PARRYING.get();
   }

   @EventBusSubscriber(modid = "efn")
   public static class AnimationHandler {
      @SubscribeEvent
      public static void onServerTick(Post event) {
         if (!ParryMasterPassive.ANIMATION_QUEUE.isEmpty()) {
            long currentTick = event.getServer().overworld().getGameTime();
            Iterator<ParryMasterPassive.DelayedAnimationTask> iterator = ParryMasterPassive.ANIMATION_QUEUE.iterator();

            while (iterator.hasNext()) {
               ParryMasterPassive.DelayedAnimationTask task = iterator.next();
               if (currentTick >= task.executeTick) {
                  if (task.playerPatch.getSkill(EFNSkills.PARRY_MASTER) != null) {
                     Player player = (Player)task.playerPatch.getOriginal();
                     ItemStack mainHandItem = player.getMainHandItem();
                     ItemStack offHandItem = player.getOffhandItem();
                     boolean isValidWeapon = Stream.of(mainHandItem, offHandItem)
                        .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
                        .filter(Objects::nonNull)
                        .anyMatch(cap -> cap.getWeaponCategory() == WeaponCategories.TACHI || cap.getWeaponCategory() == WeaponCategories.UCHIGATANA);
                     if (isValidWeapon) {
                        task.playerPatch.playAnimationSynchronized(Animations.RUSHING_TEMPO3, 0.15F);
                        ((ServerPlayer)task.playerPatch.getOriginal())
                           .addEffect(new MobEffectInstance(EFNMobEffectRegistry.INVINCIBILITY_EFFECT, 20, 0, false, false, false));
                     }
                  }

                  iterator.remove();
               }
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
         if (!ParryMasterPassive.ANIMATION_QUEUE.isEmpty()) {
            Player player = event.getEntity();
            ParryMasterPassive.ANIMATION_QUEUE.removeIf(task -> task.playerPatch.getOriginal() == player);
         }
      }
   }

   public static class Builder extends SkillBuilder<ParryMasterPassive.Builder> {
      public Builder() {
         super(ParryMasterPassive::new);
      }
   }

   private static class DelayedAnimationTask {
      final ServerPlayerPatch playerPatch;
      final int executeTick;

      DelayedAnimationTask(ServerPlayerPatch playerPatch, int delayTicks) {
         this.playerPatch = playerPatch;
         this.executeTick = (int)(((ServerPlayer)playerPatch.getOriginal()).level().getGameTime() + delayTicks);
      }
   }
}
