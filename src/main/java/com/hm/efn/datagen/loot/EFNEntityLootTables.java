package com.hm.efn.datagen.loot;

import com.hm.efn.registries.EFNItem;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

public class EFNEntityLootTables extends EntityLootSubProvider {
   protected EFNEntityLootTables(HolderLookup.Provider registries) {
      super(FeatureFlags.REGISTRY.allFlags(), registries);
   }

   public void generate() {
      this.add(EntityType.WARDEN, this.createWardenLootTable());
   }

   private Builder createWardenLootTable() {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  LootItem.lootTableItem((ItemLike)EFNItem.DEEPDARK_HEART.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
               )
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(LootItem.lootTableItem(Items.SCULK_CATALYST).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F))))
         );
   }

   private net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder createDamageSourceFromPlayerCondition() {
      return DamageSourceCondition.hasDamageSource(
         net.minecraft.advancements.critereon.DamageSourcePredicate.Builder.damageType()
            .source(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityType.PLAYER))
      );
   }

   public Builder emptyLootTable() {
      return LootTable.lootTable();
   }

   public Builder fromEntityLootTable(EntityType<?> parent) {
      return LootTable.lootTable()
         .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(NestedLootTable.lootTableReference(parent.getDefaultLootTable())));
   }

   @NotNull
   protected Stream<EntityType<?>> getKnownEntityTypes() {
      return Stream.of(EntityType.WARDEN);
   }
}
