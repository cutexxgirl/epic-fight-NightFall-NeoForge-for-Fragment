package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import com.hm.efn.client.model.Modelruinfighter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public abstract class RuinfighterItem extends ArmorItem {
   private static final Holder<ArmorMaterial> RUINFIGHTER_MATERIAL = Holder.direct(
      new ArmorMaterial(
         Map.of(Type.BOOTS, 5, Type.LEGGINGS, 9, Type.CHESTPLATE, 11, Type.HELMET, 5),
         50,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.of(),
         List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("efn", "ruinfighter"))),
         4.5F,
         0.2F
      )
   );

   public RuinfighterItem(Type type, Properties properties) {
      super(RUINFIGHTER_MATERIAL, type, properties.rarity(Rarity.EPIC).fireResistant());
   }

   public boolean isDamageable(ItemStack stack) {
      return false;
   }

   public static class Boots extends RuinfighterItem {
      public Boots() {
         super(Type.BOOTS, new Properties());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               @OnlyIn(Dist.CLIENT)
               public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                  HumanoidModel armorModel = new HumanoidModel(
                     new ModelPart(
                        Collections.emptyList(),
                        Map.of(
                           "left_leg",
                           (new Modelruinfighter(Minecraft.getInstance().getEntityModels().bakeLayer(Modelruinfighter.LAYER_LOCATION))).Boots_L,
                           "right_leg",
                           (new Modelruinfighter(Minecraft.getInstance().getEntityModels().bakeLayer(Modelruinfighter.LAYER_LOCATION))).Boots_R,
                           "head",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "hat",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "body",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap())
                        )
                     )
                  );
                  armorModel.crouching = living.isShiftKeyDown();
                  armorModel.riding = defaultModel.riding;
                  armorModel.young = living.isBaby();
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "efn:textures/armor/ruinfighter.png";
      }

      public boolean isEnchantable(@NotNull ItemStack stack) {
         return true;
      }

      public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
         return true;
      }
   }

   public static class Chestplate extends RuinfighterItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "efn:textures/armor/ruinfighter_mesh.png";
      }

      public boolean isEnchantable(@NotNull ItemStack stack) {
         return true;
      }

      public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
         return true;
      }
   }

   public static class Helmet extends RuinfighterItem {
      public Helmet() {
         super(Type.HELMET, new Properties());
      }

      public void initializeClient(Consumer<IClientItemExtensions> consumer) {
         consumer.accept(
            new IClientItemExtensions() {
               public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                  HumanoidModel armorModel = new HumanoidModel(
                     new ModelPart(
                        Collections.emptyList(),
                        Map.of(
                           "head",
                           (new Modelruinfighter(Minecraft.getInstance().getEntityModels().bakeLayer(Modelruinfighter.LAYER_LOCATION))).Helmet,
                           "hat",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "body",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_arm",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "right_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                           "left_leg",
                           new ModelPart(Collections.emptyList(), Collections.emptyMap())
                        )
                     )
                  );
                  armorModel.crouching = living.isShiftKeyDown();
                  armorModel.riding = defaultModel.riding;
                  armorModel.young = living.isBaby();
                  return armorModel;
               }
            }
         );
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "efn:textures/armor/ruinfighter.png";
      }

      public boolean isEnchantable(@NotNull ItemStack stack) {
         return true;
      }

      public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
         return true;
      }
   }

   public static class Leggings extends RuinfighterItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties());
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "efn:textures/armor/ruinfighter_mesh.png";
      }

      public boolean isEnchantable(@NotNull ItemStack stack) {
         return true;
      }

      public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
         return true;
      }
   }
}
