package com.hm.efn.registries;

import java.util.function.Supplier;
import com.hm.efn.item.custom.AetherialDuskDualSword_MainHandItem;
import com.hm.efn.item.custom.AirTachiItem;
import com.hm.efn.item.custom.BroadBladeItem;
import com.hm.efn.item.custom.CoTachiItem;
import com.hm.efn.item.custom.CrescentMoonItem;
import com.hm.efn.item.custom.DuskFireArmorItem;
import com.hm.efn.item.custom.ExsiliumgladiusItem;
import com.hm.efn.item.custom.FireExsiliumgladiusItem;
import com.hm.efn.item.custom.HfBladeItem;
import com.hm.efn.item.custom.HfMurasamaItem;
import com.hm.efn.item.custom.KusabimaruItem;
import com.hm.efn.item.custom.Meen_SpearItem;
import com.hm.efn.item.custom.NFShortSwordItem;
import com.hm.efn.item.custom.NFShortSwordTwoItem;
import com.hm.efn.item.custom.NfClawItem;
import com.hm.efn.item.custom.PioneerItem;
import com.hm.efn.item.custom.RuinfighterItem;
import com.hm.efn.item.custom.RuinsgreatswordItem;
import com.hm.efn.item.custom.ScytheItem;
import com.hm.efn.item.custom.ThornWheelItem;
import com.hm.efn.item.custom.YamatoItem;
import com.hm.efn.item.geo.ExcaliburItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.world.item.WeaponItem;

public class EFNItem {
   public static final DeferredRegister<Item> ITEM = DeferredRegister.create(Registries.ITEM, "efn");
   public static final DeferredRegister<CreativeModeTab> EFN_ITEM_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "efn");
   public static final DeferredRegister<CreativeModeTab> EFN_ITEM_TAB_EXTRA = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "efn");
   public static final Supplier<Item> TEST = ITEM.register("test", () -> new Item(new Properties().fireResistant().stacksTo(1)));
   public static final Supplier<WeaponItem> RUINSGREATSWORD = ITEM.register(
      "ruinsgreatsword", () -> new RuinsgreatswordItem(Tiers.NETHERITE, 11, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> THORNWHEEL = ITEM.register(
      "thornwheel", () -> new ThornWheelItem(Tiers.NETHERITE, 9, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> AETHERIAL_DUSK_DUALSWORD = ITEM.register(
      "nf_dual_sword", () -> new AetherialDuskDualSword_MainHandItem(Tiers.NETHERITE, 2, -2.4F, weaponProperties())
   );
   public static final Supplier<WeaponItem> MEEN_SPEAR = ITEM.register(
      "meen_spear", () -> new Meen_SpearItem(Tiers.NETHERITE, 6, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> MEEN_SPEAR_E = ITEM.register(
      "meen_spear_e", () -> new Meen_SpearItem(Tiers.NETHERITE, 6, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> SWORD_OF_PIONEER = ITEM.register(
      "sword_of_pioneer", () -> new PioneerItem(Tiers.NETHERITE, 2, -2.4F, weaponProperties())
   );
   public static final Supplier<WeaponItem> NF_SHORT_SWORD = ITEM.register(
      "nf_shortsword", () -> new NFShortSwordItem(Tiers.NETHERITE, 2, -2.4F, weaponProperties())
   );
   public static final Supplier<WeaponItem> NF_SHORT_SWORD_2 = ITEM.register(
      "nf_shortsword_2", () -> new NFShortSwordTwoItem(Tiers.NETHERITE, 2, -2.4F, weaponProperties())
   );
   public static final Supplier<WeaponItem> NF_SHORT_SWORD_E = ITEM.register(
      "nf_shortsword_e", () -> new NFShortSwordItem(Tiers.NETHERITE, 2, -2.4F, weaponProperties())
   );
   public static final Supplier<WeaponItem> NF_SHORT_SWORD_2_E = ITEM.register(
      "nf_shortsword_2_e", () -> new NFShortSwordTwoItem(Tiers.NETHERITE, 2, -2.4F, weaponProperties())
   );
   public static final Supplier<WeaponItem> EXSILIUMGLADIUS = ITEM.register(
      "exsiliumgladius", () -> new ExsiliumgladiusItem(Tiers.NETHERITE, 4, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> FIRE_EXSILIUMGLADIUS = ITEM.register(
      "fire_exsiliumgladius", () -> new FireExsiliumgladiusItem(Tiers.NETHERITE, 4, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> EXSILIUMGLADIUS_E = ITEM.register(
      "exsiliumgladius_e", () -> new ExsiliumgladiusItem(Tiers.NETHERITE, 4, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> FIRE_EXSILIUMGLADIUS_E = ITEM.register(
      "fire_exsiliumgladius_e", () -> new FireExsiliumgladiusItem(Tiers.NETHERITE, 4, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> AIR_TACHI = ITEM.register(
      "air_tachi", () -> new AirTachiItem(Tiers.NETHERITE, 5, -2.8F, weaponProperties())
   );
   public static final Supplier<WeaponItem> AIR_TACHI_E = ITEM.register(
      "air_tachi_e", () -> new AirTachiItem(Tiers.NETHERITE, 5, -2.8F, weaponProperties())
   );
   public static final Supplier<WeaponItem> ARC_TACHI = ITEM.register(
      "arc_tachi", () -> new AirTachiItem(Tiers.NETHERITE, 5, -2.8F, weaponProperties())
   );
   public static final Supplier<WeaponItem> CO_TACHI = ITEM.register(
      "co_tachi", () -> new CoTachiItem(Tiers.NETHERITE, 5, -2.8F, weaponProperties())
   );
   public static final Supplier<WeaponItem> KUSABIMARU = ITEM.register(
      "kusabimaru", () -> new KusabimaruItem(Tiers.NETHERITE, 5, -2.8F, weaponProperties())
   );
   public static final Supplier<WeaponItem> BROADBLADE = ITEM.register(
      "broadblade", () -> new BroadBladeItem(Tiers.NETHERITE, 5, -2.8F, weaponProperties())
   );
   public static final Supplier<WeaponItem> CRIMSON_MOON = ITEM.register(
      "crimson_moon", () -> new ScytheItem(Tiers.NETHERITE, 3, -2.6F, weaponProperties())
   );
   public static final Supplier<WeaponItem> CRIMSON_MOON_E = ITEM.register(
      "crimson_moon_e", () -> new ScytheItem(Tiers.NETHERITE, 3, -2.6F, weaponProperties())
   );
   public static final Supplier<WeaponItem> NF_CLAW = ITEM.register(
      "nf_claw", () -> new NfClawItem(Tiers.NETHERITE, 4, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> YAMATO_DMC4 = ITEM.register(
      "yamato_dmc4", () -> new YamatoItem(Tiers.NETHERITE, 0, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> YAMATO_DMC = ITEM.register(
      "yamato_dmc", () -> new YamatoItem(Tiers.NETHERITE, 0, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> YAMATO_DMC4_IN_SHEATH = ITEM.register(
      "yamato_dmc4_in_sheath", () -> new YamatoItem(Tiers.NETHERITE, 0, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> YAMATO_DMC_IN_SHEATH = ITEM.register(
      "yamato_dmc_in_sheath", () -> new YamatoItem(Tiers.NETHERITE, 0, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> HF_MURASAMA = ITEM.register(
      "hf_murasama", () -> new HfMurasamaItem(Tiers.NETHERITE, 6, -2.8F, weaponProperties())
   );
   public static final Supplier<WeaponItem> HF_BLADE = ITEM.register(
      "hf_blade", () -> new HfBladeItem(Tiers.NETHERITE, 9, -2.8F, weaponProperties())
   );
   public static final Supplier<WeaponItem> CRESCENT_MOON = ITEM.register(
      "crescent_moon", () -> new CrescentMoonItem(Tiers.NETHERITE, 7, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> CRESCENT_MOON_E = ITEM.register(
      "crescent_moon_e", () -> new CrescentMoonItem(Tiers.NETHERITE, 7, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> FLAG_BEARER = ITEM.register(
      "flag_bearer", () -> new CrescentMoonItem(Tiers.NETHERITE, 7, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> FLAG_BEARER_E = ITEM.register(
      "flag_bearer_e", () -> new CrescentMoonItem(Tiers.NETHERITE, 7, -3.0F, weaponProperties())
   );
   public static final Supplier<WeaponItem> EXCALIBUR = ITEM.register(
      "excalibur", () -> new ExcaliburItem(Tiers.NETHERITE, 6, -2.8F, weaponProperties())
   );
   public static final Supplier<Item> RUINFIGHTER_HELMET = ITEM.register("ruinfighter_helmet", RuinfighterItem.Helmet::new);
   public static final Supplier<Item> RUINFIGHTER_CHESTPLATE = ITEM.register("ruinfighter_chestplate", RuinfighterItem.Chestplate::new);
   public static final Supplier<Item> RUINFIGHTER_LEGGINGS = ITEM.register("ruinfighter_leggings", RuinfighterItem.Leggings::new);
   public static final Supplier<Item> RUINFIGHTER_BOOTS = ITEM.register("ruinfighter_boots", RuinfighterItem.Boots::new);
   public static final Supplier<Item> DUSKFIRE_HELMET = ITEM.register("duskfire_helmet", DuskFireArmorItem.Helmet::new);
   public static final Supplier<Item> DUSKFIRE_CHESTPLATE = ITEM.register("duskfire_chestplate", DuskFireArmorItem.Chestplate::new);
   public static final Supplier<Item> DUSKFIRE_LEGGINGS = ITEM.register("duskfire_leggings", DuskFireArmorItem.Leggings::new);
   public static final Supplier<Item> DUSKFIRE_BOOTS = ITEM.register("duskfire_boots", DuskFireArmorItem.Boots::new);
   public static final Supplier<Item> DEEPDARK_HEART = ITEM.register("deepdark_heart", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant()));
   public static final Supplier<Item> DUSKFIRE_INGOT = ITEM.register("duskfire_ingot", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant()));
   public static final Supplier<Item> HF_INGOT = ITEM.register("hf_ingot", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant()));
   public static final Supplier<Item> HF_INGOT_B = ITEM.register("hf_ingot_b", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant()));
   public static final Supplier<Item> HF_MURASAMA_BLADE = ITEM.register(
      "hf_murasama_blade", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant())
   );
   public static final Supplier<Item> HF_MURASAMA_SHEATH = ITEM.register(
      "hf_murasama_sheath", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant())
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEM_TAB_MAIN = EFN_ITEM_TAB.register(
      "efn_items",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("itemGroup.efn.items"))
         .icon(() -> new ItemStack((ItemLike)MEEN_SPEAR.get()))
         .displayItems((parameters, tabData) -> {
            tabData.accept((ItemLike)RUINSGREATSWORD.get());
            tabData.accept((ItemLike)MEEN_SPEAR.get());
            tabData.accept((ItemLike)AETHERIAL_DUSK_DUALSWORD.get());
            tabData.accept((ItemLike)SWORD_OF_PIONEER.get());
            tabData.accept((ItemLike)YAMATO_DMC_IN_SHEATH.get());
            tabData.accept((ItemLike)YAMATO_DMC4_IN_SHEATH.get());
            tabData.accept((ItemLike)NF_CLAW.get());
            tabData.accept((ItemLike)AIR_TACHI.get());
            tabData.accept((ItemLike)CO_TACHI.get());
            tabData.accept((ItemLike)NF_SHORT_SWORD.get());
            tabData.accept((ItemLike)NF_SHORT_SWORD_2.get());
            tabData.accept((ItemLike)EXSILIUMGLADIUS.get());
            tabData.accept((ItemLike)FIRE_EXSILIUMGLADIUS.get());
            tabData.accept((ItemLike)HF_MURASAMA.get());
            tabData.accept((ItemLike)HF_BLADE.get());
            tabData.accept((ItemLike)KUSABIMARU.get());
            tabData.accept((ItemLike)THORNWHEEL.get());
            tabData.accept((ItemLike)CRIMSON_MOON.get());
            tabData.accept((ItemLike)BROADBLADE.get());
            tabData.accept((ItemLike)CRESCENT_MOON.get());
            tabData.accept((ItemLike)RUINFIGHTER_HELMET.get());
            tabData.accept((ItemLike)RUINFIGHTER_CHESTPLATE.get());
            tabData.accept((ItemLike)RUINFIGHTER_LEGGINGS.get());
            tabData.accept((ItemLike)RUINFIGHTER_BOOTS.get());
            tabData.accept((ItemLike)DUSKFIRE_HELMET.get());
            tabData.accept((ItemLike)DUSKFIRE_CHESTPLATE.get());
            tabData.accept((ItemLike)DUSKFIRE_LEGGINGS.get());
            tabData.accept((ItemLike)DUSKFIRE_BOOTS.get());
            tabData.accept((ItemLike)DEEPDARK_HEART.get());
            tabData.accept((ItemLike)DUSKFIRE_INGOT.get());
            tabData.accept((ItemLike)HF_INGOT.get());
            tabData.accept((ItemLike)HF_INGOT_B.get());
            tabData.accept((ItemLike)HF_MURASAMA_BLADE.get());
            tabData.accept((ItemLike)HF_MURASAMA_SHEATH.get());
         })
         .build()
   );

   public static void register(IEventBus eventBus) {
      ITEM.register(eventBus);
   }

   private static Properties weaponProperties() {
      return new Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1);
   }
}
