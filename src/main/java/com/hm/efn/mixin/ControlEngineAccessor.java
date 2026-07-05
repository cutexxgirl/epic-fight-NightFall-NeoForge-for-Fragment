package com.hm.efn.mixin;

import java.util.Set;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import yesman.epicfight.api.client.input.action.InputAction;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.skill.SkillSlot;

@Mixin(value = ControlEngine.class, remap = false)
public interface ControlEngineAccessor {
   @Accessor("weaponInnatePressCounter")
   void efn$setWeaponInnatePressCounter(int value);

   @Accessor("weaponInnatePressToggle")
   void efn$setWeaponInnatePressToggle(boolean value);

   @Accessor("attackLightPressToggle")
   void efn$setAttackLightPressToggle(boolean value);

   @Accessor("packetsToSend")
   Set<CustomPacketPayload> efn$getPacketsToSend();

   @Invoker("reserveKey")
   void efn$invokeReserveKey(SkillSlot skillSlot, InputAction inputAction);
}
