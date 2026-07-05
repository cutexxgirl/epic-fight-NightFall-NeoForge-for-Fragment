package com.hm.efn.skill.passive;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.hm.efn.util.EFNSkillChecks;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.registry.entries.EpicFightAttributes;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class IndestructiblePassive extends PassiveSkill {
   private static final UUID EVENT_UUID = UUID.fromString("4074c6de-0268-11ee-be56-0242ac120721");
   private static final UUID IMMUNITY_UUID = UUID.fromString("4074c6de-0721-11ee-be56-0242ac120003");
   private static final UUID STAMINA_UUID = UUID.fromString("0721c6de-4074-11ee-be56-0242ac120004");
   private static final UUID STAMINA_LOCK_UUID = UUID.fromString("0721c6de-4074-11ee-be56-0242ac120005");
   private static final ResourceLocation STAMINA_LOCK_ID = ResourceLocation.fromNamespaceAndPath("efn", "indestructible_stamina_lock");
   private static final AttributeModifier INFINITE_STAMINA = new AttributeModifier(
      STAMINA_LOCK_ID, 1.14514191981E12, Operation.ADD_VALUE
   );

   public IndestructiblePassive(SkillBuilder<?> builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.SKILL_CAST_EVENT,
         EVENT_UUID,
         event -> {
            if (EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.DODGE) && !event.isStateExecutable()) {
               EntityState state = container.getExecutor().getEntityState();
               DynamicAnimation animation = (DynamicAnimation)Objects.requireNonNull(container.getExecutor().getAnimator().getPlayerFor(null))
                  .getRealAnimation()
                  .get();
               if (!state.hurt() && !state.knockDown() && animation instanceof AttackAnimation) {
                  event.setStateExecutable(true);
               }
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, IMMUNITY_UUID, event -> {
         if (event.getDamageSource() instanceof EpicFightDamageSource epicSource) {
            epicSource.setStunType(StunType.NONE);
         }
      }, -1);
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.STAMINA_CONSUME_EVENT, STAMINA_UUID, event -> {
         event.setAmount(0.0F);
         event.cancel();
      });
      AttributeInstance staminaAttribute = ((Player)container.getExecutor().getOriginal()).getAttribute(EpicFightAttributes.MAX_STAMINA);
      if (staminaAttribute != null && !staminaAttribute.hasModifier(STAMINA_LOCK_ID)) {
         staminaAttribute.addTransientModifier(INFINITE_STAMINA);
      }

      container.getExecutor().setStamina(container.getExecutor().getMaxStamina());
   }

   public void onRemoved(SkillContainer container) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.SKILL_CAST_EVENT, EVENT_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.STAMINA_CONSUME_EVENT, STAMINA_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, IMMUNITY_UUID, -1);
      Objects.requireNonNull(((Player)container.getExecutor().getOriginal()).getAttribute(EpicFightAttributes.MAX_STAMINA))
         .removeModifier(STAMINA_LOCK_ID);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDraw(SkillContainer container) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public List<Object> getTooltipArgsOfScreen(List<Object> list) {
      return list;
   }
}
