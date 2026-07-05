package com.hm.efn.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
   @Invoker(value = "setPosition", remap = false)
   void invokeSetPosition(double var1, double var3, double var5);
}
