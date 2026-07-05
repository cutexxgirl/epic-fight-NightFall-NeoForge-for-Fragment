package com.hm.efn.mixin;

import java.util.Map;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

@Mixin(value = Armatures.class, remap = false)
public interface ArmaturesAccessor {
   @Accessor(value = "ENTITY_TYPE_ARMATURE_MAPPER", remap = false)
   static Map<EntityType<?>, AssetAccessor<? extends Armature>> getEntityTypeArmatureMapper() {
      throw new UnsupportedOperationException();
   }
}
