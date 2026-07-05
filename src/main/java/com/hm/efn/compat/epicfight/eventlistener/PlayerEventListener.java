package com.hm.efn.compat.epicfight.eventlistener;

import java.util.function.Function;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.EventHook;
import yesman.epicfight.api.event.LivingEntityPatchEvent;
import yesman.epicfight.api.event.types.player.SkillConsumeEvent;

public final class PlayerEventListener {
   private PlayerEventListener() {
   }

   public static final class EventType<T> {
      public static final EventType<TakeDamageEvent.Attack> TAKE_DAMAGE_EVENT_ATTACK = new EventType<>(
         EpicFightEventHooks.Entity.TAKE_DAMAGE_INCOME, event -> new TakeDamageEvent.Attack((yesman.epicfight.api.event.types.entity.TakeDamageEvent.Income)event)
      );
      public static final EventType<TakeDamageEvent.Hurt> TAKE_DAMAGE_EVENT_HURT = new EventType<>(
         EpicFightEventHooks.Entity.TAKE_DAMAGE_PRE, event -> new TakeDamageEvent.Hurt((yesman.epicfight.api.event.types.entity.TakeDamageEvent.Pre)event)
      );
      public static final EventType<TakeDamageEvent.Damage> TAKE_DAMAGE_EVENT_DAMAGE = new EventType<>(
         EpicFightEventHooks.Entity.TAKE_DAMAGE_POST, event -> new TakeDamageEvent.Damage((yesman.epicfight.api.event.types.entity.TakeDamageEvent.Post)event)
      );
      public static final EventType<DealDamageEvent.Attack> DEAL_DAMAGE_EVENT_ATTACK = new EventType<>(
         EpicFightEventHooks.Entity.DELIVER_DAMAGE_INCOME, event -> new DealDamageEvent.Attack((yesman.epicfight.api.event.types.entity.DealDamageEvent.Income)event)
      );
      public static final EventType<DealDamageEvent.Hurt> DEAL_DAMAGE_EVENT_HURT = new EventType<>(
         EpicFightEventHooks.Entity.DELIVER_DAMAGE_PRE, event -> new DealDamageEvent.Hurt((yesman.epicfight.api.event.types.entity.DealDamageEvent.Pre)event)
      );
      public static final EventType<DealDamageEvent.Damage> DEAL_DAMAGE_EVENT_DAMAGE = new EventType<>(
         EpicFightEventHooks.Entity.DELIVER_DAMAGE_POST, event -> new DealDamageEvent.Damage((yesman.epicfight.api.event.types.entity.DealDamageEvent.Post)event)
      );
      public static final EventType<ActionEvent> ACTION_EVENT_SERVER = new EventType<>(
         EpicFightEventHooks.Animation.START_ACTION, event -> new ActionEvent((yesman.epicfight.api.event.types.animation.StartActionEvent)event)
      );
      public static final EventType<ActionEvent> ACTION_EVENT_CLIENT = ACTION_EVENT_SERVER;
      public static final EventType<AnimationEvent> ANIMATION_BEGIN_EVENT = new EventType<>(
         EpicFightEventHooks.Animation.BEGIN, event -> AnimationEvent.begin((yesman.epicfight.api.event.types.animation.AnimationBeginEvent)event)
      );
      public static final EventType<AnimationEvent> ANIMATION_END_EVENT = new EventType<>(
         EpicFightEventHooks.Animation.END, event -> AnimationEvent.end((yesman.epicfight.api.event.types.animation.AnimationEndEvent)event)
      );
      public static final EventType<AttackPhaseEndEvent> ATTACK_PHASE_END_EVENT = new EventType<>(
         EpicFightEventHooks.Animation.ATTACK_PHASE_END,
         event -> new AttackPhaseEndEvent((yesman.epicfight.api.event.types.animation.AttackPhaseEndEvent)event)
      );
      public static final EventType<yesman.epicfight.api.event.types.player.SkillCastEvent> SKILL_CAST_EVENT = new EventType<>(
         EpicFightEventHooks.Player.CAST_SKILL, event -> (yesman.epicfight.api.event.types.player.SkillCastEvent)event
      );
      public static final EventType<yesman.epicfight.api.event.types.player.SkillCancelEvent> SKILL_CANCEL_EVENT = new EventType<>(
         EpicFightEventHooks.Player.CANCEL_SKILL, event -> (yesman.epicfight.api.event.types.player.SkillCancelEvent)event
      );
      public static final EventType<SkillConsumeEvent> SKILL_CONSUME_EVENT = new EventType<>(
         EpicFightEventHooks.Player.CONSUME_SKILL, event -> (SkillConsumeEvent)event
      );
      public static final EventType<SkillConsumeEvent> STAMINA_CONSUME_EVENT = SKILL_CONSUME_EVENT;
      public static final EventType<DodgeSuccessEvent> DODGE_SUCCESS_EVENT = new EventType<>(
         EpicFightEventHooks.Entity.ON_DODGE, event -> new DodgeSuccessEvent((yesman.epicfight.api.event.types.entity.DodgeEvent)event)
      );
      public static final EventType<FallEvent> FALL_EVENT = new EventType<>(
         EpicFightEventHooks.Entity.ON_FALL, event -> new FallEvent((yesman.epicfight.api.event.types.entity.FallEvent)event)
      );
      public static final EventType<PlayerKilledEvent> PLAYER_KILLED_EVENT = new EventType<>(
         EpicFightEventHooks.Entity.KILL_ENTITY, event -> new PlayerKilledEvent((yesman.epicfight.api.event.types.entity.KillEntityEvent)event)
      );
      public static final EventType<MovementInputEvent> MOVEMENT_INPUT_EVENT = new EventType<>(
         EpicFightClientEventHooks.Control.MAPPED_MOVEMENT_INPUT_UPDATE,
         event -> new MovementInputEvent((yesman.epicfight.api.client.event.types.control.MappedMovementInputUpdateEvent)event)
      );

      private final EventHook<? extends LivingEntityPatchEvent> hook;
      private final Function<LivingEntityPatchEvent, T> wrapper;

      private EventType(EventHook<? extends LivingEntityPatchEvent> hook, Function<LivingEntityPatchEvent, T> wrapper) {
         this.hook = hook;
         this.wrapper = wrapper;
      }

      public EventHook<? extends LivingEntityPatchEvent> hook() {
         return this.hook;
      }

      public T wrap(LivingEntityPatchEvent event) {
         return this.wrapper.apply(event);
      }
   }
}
