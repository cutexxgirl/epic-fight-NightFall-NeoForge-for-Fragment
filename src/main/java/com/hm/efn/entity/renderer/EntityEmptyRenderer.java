package com.hm.efn.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityEmptyRenderer<T extends Entity> extends EntityRenderer<T> {
   public EntityEmptyRenderer(Context context) {
      super(context);
   }

   public void render(@NotNull T entity, float yaw, float partial, @NotNull PoseStack pose, @NotNull MultiBufferSource buf, int light) {
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull T entity) {
      return DefaultPlayerSkin.getDefaultTexture();
   }
}
