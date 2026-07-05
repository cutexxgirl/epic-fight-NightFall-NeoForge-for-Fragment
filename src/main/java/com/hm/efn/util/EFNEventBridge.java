package com.hm.efn.util;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.event.EntityEventListener;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.api.event.EventHook;
import yesman.epicfight.api.event.IdentifierProvider;
import yesman.epicfight.api.event.LivingEntityPatchEvent;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public final class EFNEventBridge {
   private EFNEventBridge() {
   }

   @FunctionalInterface
   public interface LegacyEventHandler<T> {
      void fire(T event);
   }

   public static <T> void addEventListener(EntityEventListener listener, EventType<T> eventType, UUID id, LegacyEventHandler<T> handler) {
      addEventListener(listener, eventType, id, handler, 0);
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public static <T> void addEventListener(EntityEventListener listener, EventType<T> eventType, UUID id, LegacyEventHandler<T> handler, int priority) {
      listener.registerEvent(
         (EventHook)eventType.hook(),
         event -> handler.fire(eventType.wrap((LivingEntityPatchEvent)event)),
         identifier(id),
         priority
      );
   }

   public static void removeListener(EntityEventListener listener, EventType<?> eventType, UUID id) {
      listener.removeListenersBelongTo(identifier(id));
   }

   public static void removeListener(EntityEventListener listener, EventType<?> eventType, UUID id, int priority) {
      removeListener(listener, eventType, id);
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public static <T> T triggerEvents(EntityEventListener listener, EventType<T> eventType, T event) {
      if (event instanceof Event epicFightEvent) {
         ((EventHook)eventType.hook()).postWithListener(epicFightEvent, listener);
      }

      return event;
   }

   private static IdentifierProvider identifier(UUID id) {
      return IdentifierProvider.constant(ResourceLocation.fromNamespaceAndPath("efn", id.toString()));
   }
}
