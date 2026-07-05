package com.hm.efn.client.model;

import com.hm.efn.entity.EFNEntity;
import net.minecraft.world.entity.EntityType;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;

public class EFNArmatures {
   public static ArmatureAccessor<Armature> KUSABIMARU_FUSHIGIRI = ArmatureAccessor.create("efn", "weapon/kusabimaru_fushigiri", Armature::new);
   public static ArmatureAccessor<Armature> THORNWHEEL = ArmatureAccessor.create("efn", "weapon/thornwheel", Armature::new);

   public static void registerArmatures() {
      Armatures.registerEntityTypeArmature((EntityType)EFNEntity.DOPPELGANGER.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)EFNEntity.GUARDIAN.get(), Armatures.BIPED);
   }
}
