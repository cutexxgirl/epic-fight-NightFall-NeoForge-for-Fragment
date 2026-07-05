package yesman.epicfight.api.neoevent;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.Event;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public class EntityPatchRegistryEvent extends Event {
   private final yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent delegate;

   public EntityPatchRegistryEvent(yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent delegate) {
      this.delegate = delegate;
   }

   public Map<EntityType<?>, Function<Entity, EntityPatch<?>>> getTypeEntry() {
      return this.delegate.getTypeEntry();
   }

   public <T extends Entity> void registerEntityPatch(EntityType<T> entityType, Function<T, EntityPatch<T>> entityPatchFactory) {
      this.delegate.registerEntityPatch(entityType, entityPatchFactory);
   }
}
