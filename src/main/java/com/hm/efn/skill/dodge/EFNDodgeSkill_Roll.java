package com.hm.efn.skill.dodge;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.registry.entries.EpicFightAttributes;

public class EFNDodgeSkill_Roll extends EFNDodgeSkill {
   private static final UUID STAMINA_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-11ed-a05b-0242ac120115");
   private static final ResourceLocation STAMINA_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("efn", "roll_skill_stamina_bonus");
   private static final float STAMINA_BONUS = 6.0F;
   private static final String STAMINA_MODIFIER_NAME = "EFN Roll Skill Stamina Bonus";

   public EFNDodgeSkill_Roll(EFNDodgeSkill.Builder builder) {
      super(builder);
   }

   @Override
   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
         AttributeModifier modifier = new AttributeModifier(STAMINA_MODIFIER_ID, STAMINA_BONUS, Operation.ADD_VALUE);
         Objects.requireNonNull(((ServerPlayer)serverPlayerPatch.getOriginal()).getAttribute(EpicFightAttributes.MAX_STAMINA)).addTransientModifier(modifier);
      }
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
         Objects.requireNonNull(((ServerPlayer)serverPlayerPatch.getOriginal()).getAttribute(EpicFightAttributes.MAX_STAMINA))
            .removeModifier(STAMINA_MODIFIER_ID);
      }
   }

   @Override
   public void executeOnServer(SkillContainer container, CompoundTag args) {
      super.executeOnServer(container, args);
      container.getExecutor().playSound((SoundEvent)EpicFightSounds.ROLL.get(), 1.0F, 1.0F);
   }
}
