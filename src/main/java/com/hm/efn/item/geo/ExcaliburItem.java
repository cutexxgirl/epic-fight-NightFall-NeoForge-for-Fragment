package com.hm.efn.item.geo;

import net.minecraft.core.Holder;

import com.hm.efn.client.renderer.geoItem.ExcaliburItemRenderer;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import yesman.epicfight.world.item.WeaponItem;

public class ExcaliburItem extends WeaponItem implements GeoItem {
   private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("use.activate");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ExcaliburItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
      super(properties.attributes(SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
      SingletonGeoAnimatable.registerSyncedAnimatable(this);
   }

   public boolean isDamageable(ItemStack stack) {
      return false;
   }

   public boolean isEnchantable(@NotNull ItemStack stack) {
      return true;
   }

   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return true;
   }

   public int getEnchantmentValue() {
      return 30;
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         private ExcaliburItemRenderer renderer = null;

         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            if (this.renderer == null) {
               this.renderer = new ExcaliburItemRenderer();
            }

            return this.renderer;
         }
      });
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController[]{new AnimationController(this, "idle", 0, animationState -> {
         animationState.getController().setAnimation(RawAnimation.begin().then("animation", LoopType.LOOP));
         return PlayState.CONTINUE;
      })});
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
