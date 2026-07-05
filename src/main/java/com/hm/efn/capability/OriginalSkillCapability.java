package com.hm.efn.capability;

import com.hm.efn.EFN;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class OriginalSkillCapability {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EFN.MODID);
   public static final Supplier<AttachmentType<OriginalSkillMemory>> INSTANCE = ATTACHMENT_TYPES.register(
      "original_skill_memory", () -> AttachmentType.serializable(OriginalSkillMemory::new).copyOnDeath().build()
   );

   public static IOriginalSkillMemory get(Player player) {
      return player.getData(INSTANCE);
   }

   public interface IOriginalSkillMemory extends INBTSerializable<CompoundTag> {
      boolean hasSkill(String var1);

      void saveSkill(String var1, String var2);

      String getSkill(String var1);

      void removeSkill(String var1);

      CompoundTag serializeNBT();

      void deserializeNBT(CompoundTag var1);

      void copyFrom(IOriginalSkillMemory var1);
   }

   public static class OriginalSkillMemory implements OriginalSkillCapability.IOriginalSkillMemory {
      private final Map<String, String> memory = new HashMap<>();

      @Override
      public boolean hasSkill(String slotName) {
         return this.memory.containsKey(slotName);
      }

      @Override
      public void saveSkill(String slotName, String skillId) {
         this.memory.put(slotName, skillId == null ? "none" : skillId);
      }

      @Override
      public String getSkill(String slotName) {
         return this.memory.get(slotName);
      }

      @Override
      public void removeSkill(String slotName) {
         this.memory.remove(slotName);
      }

      @Override
      public CompoundTag serializeNBT() {
         CompoundTag tag = new CompoundTag();
         this.memory.forEach(tag::putString);
         return tag;
      }

      @Override
      public CompoundTag serializeNBT(HolderLookup.Provider provider) {
         return this.serializeNBT();
      }

      @Override
      public void deserializeNBT(CompoundTag nbt) {
         this.memory.clear();

         for (String key : nbt.getAllKeys()) {
            this.memory.put(key, nbt.getString(key));
         }
      }

      @Override
      public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
         this.deserializeNBT(nbt);
      }

      @Override
      public void copyFrom(IOriginalSkillMemory other) {
         this.deserializeNBT(other.serializeNBT());
      }
   }
}
