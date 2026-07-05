package com.hm.efn.compat.epicfight.forgeevent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;

public class SkillBuildEvent extends Event {
   private static final Map<String, DeferredRegister<Skill>> REGISTRIES = new LinkedHashMap<>();

   public ModRegistryWorker createRegistryWorker(String modId) {
      return new ModRegistryWorker(modId, registry(modId));
   }

   public static void registerAll(IEventBus modEventBus) {
      REGISTRIES.values().forEach(registry -> registry.register(modEventBus));
   }

   private static DeferredRegister<Skill> registry(String modId) {
      return REGISTRIES.computeIfAbsent(modId, id -> DeferredRegister.create(EpicFightRegistries.Keys.SKILL, id));
   }

   public static final class ModRegistryWorker {
      private final String modId;
      private final DeferredRegister<Skill> registry;

      private ModRegistryWorker(String modId, DeferredRegister<Skill> registry) {
         this.modId = modId;
         this.registry = registry;
      }

      public <T extends Skill, B extends SkillBuilder<?>> T build(String name, Function<B, T> constructor, B builder) {
         ResourceLocation key = ResourceLocation.fromNamespaceAndPath(this.modId, name);
         T skill = builder.build(key);
         this.registry.register(name, ignored -> skill);
         return skill;
      }
   }
}
