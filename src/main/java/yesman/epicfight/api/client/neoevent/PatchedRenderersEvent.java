package yesman.epicfight.api.client.neoevent;

import com.google.gson.JsonElement;
import java.util.function.Function;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.Event;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

public abstract class PatchedRenderersEvent extends Event {
   public static class RegisterItemRenderer extends PatchedRenderersEvent {
      private final yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent.Item delegate;

      public RegisterItemRenderer(yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent.Item delegate) {
         this.delegate = delegate;
      }

      public void addItemRenderer(ResourceLocation rl, Function<JsonElement, RenderItemBase> provider) {
         this.delegate.addItemRenderer(rl, provider);
      }
   }

   @SuppressWarnings("rawtypes")
   public static class Add extends PatchedRenderersEvent {
      private final yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent.AddEntity delegate;

      public Add(yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent.AddEntity delegate) {
         this.delegate = delegate;
      }

      public void addPatchedEntityRenderer(EntityType<?> entityType, Function<EntityType<?>, PatchedEntityRenderer> provider) {
         this.delegate.addPatchedEntityRenderer(entityType, provider);
      }

      public EntityRendererProvider.Context getContext() {
         return this.delegate.getContext();
      }
   }
}
