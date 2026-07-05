package com.hm.efn.event;

import com.hm.efn.EFN;
import com.hm.efn.network.PlayTotemAnimationPacket;
import com.hm.efn.registries.EFNItem;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.ParticleEffectInvoker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME)
public class EntityEvent {
   @SubscribeEvent
   public static void onLivingDamaged(Pre event) {
      DamageSource damageSource = event.getSource();
      LivingEntity hitEntity = event.getEntity();
      if (!damageSource.is(DamageTypeTags.BYPASSES_EFFECTS)) {
         MobEffectInstance effectInstance = hitEntity.getEffect(EFNMobEffectRegistry.DAMAGE_REDUCTION);
         if (effectInstance != null) {
            int amplifier = effectInstance.getAmplifier();
            float reduction = Mth.clamp(amplifier * 0.01F, 0.0F, 1.0F);
            event.setNewDamage(event.getNewDamage() * (1.0F - reduction));
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGH)
   public static void onPlayerDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof Player player) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         ItemStack mainHandItem = player.getMainHandItem();
         ItemStack offHandItem = player.getOffhandItem();
         boolean hasBroadBlade = mainHandItem.is((Item)EFNItem.BROADBLADE.get()) || offHandItem.is((Item)EFNItem.BROADBLADE.get());
         if (hasBroadBlade) {
            if (player.hasEffect(EFNMobEffectRegistry.BATTLE_CONTINUATION) || player.hasEffect(EFNMobEffectRegistry.DIE)) {
               return;
            }

            event.setCanceled(true);
            player.addEffect(new MobEffectInstance(EFNMobEffectRegistry.BATTLE_CONTINUATION, 300, 0));
            player.setHealth(1.0F);
            if (playerPatch != null) {
               ParticleEffectInvoker.groundSplit(playerPatch, 0.0, 0.0, 0.0, 0.0, 3.0F, true, false, false, true);
            }

            if (player instanceof ServerPlayer serverPlayer) {
               EFN.sendToPlayer(new PlayTotemAnimationPacket(), serverPlayer);
               ItemStack usedItem = mainHandItem.is((Item)EFNItem.BROADBLADE.get()) ? mainHandItem : offHandItem;
               serverPlayer.awardStat(Stats.ITEM_USED.get(usedItem.getItem()));
            }
         }
      }
   }
}
