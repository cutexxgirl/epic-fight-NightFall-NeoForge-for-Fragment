package com.hm.efn.impl;

import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public interface IArmatureAccessorGetter {
   AssetAccessor<? extends Armature> epicFight_Nightfall$getArmatureAccessor(EntityPatch<?> var1);
}
