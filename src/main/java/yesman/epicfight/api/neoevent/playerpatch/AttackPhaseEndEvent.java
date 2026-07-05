package yesman.epicfight.api.neoevent.playerpatch;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AttackPhaseEndEvent extends PlayerPatchEvent {
   private final AnimationAccessor<? extends AttackAnimation> animation;
   private final AttackAnimation.Phase phase;
   private final int phaseOrder;
   private final boolean animationTerminated;

   public AttackPhaseEndEvent(
      ServerPlayerPatch playerPatch,
      AnimationAccessor<? extends AttackAnimation> animation,
      AttackAnimation.Phase phase,
      int phaseOrder
   ) {
      this(playerPatch, animation, phase, phaseOrder, false);
   }

   public AttackPhaseEndEvent(
      PlayerPatch<?> playerPatch,
      AnimationAccessor<? extends AttackAnimation> animation,
      AttackAnimation.Phase phase,
      int phaseOrder
   ) {
      this(playerPatch, animation, phase, phaseOrder, false);
   }

   public AttackPhaseEndEvent(
      PlayerPatch<?> playerPatch,
      AnimationAccessor<? extends AttackAnimation> animation,
      AttackAnimation.Phase phase,
      int phaseOrder,
      boolean animationTerminated
   ) {
      super(playerPatch);
      this.animation = animation;
      this.phase = phase;
      this.phaseOrder = phaseOrder;
      this.animationTerminated = animationTerminated;
   }

   public AnimationAccessor<? extends AttackAnimation> getAnimation() {
      return this.animation;
   }

   public AttackAnimation.Phase getPhase() {
      return this.phase;
   }

   public int getPhaseOrder() {
      return this.phaseOrder;
   }

   public boolean isAnimationTerminated() {
      return this.animationTerminated;
   }

   @Override
   @SuppressWarnings({"rawtypes", "unchecked"})
   protected void postEpicFightListeners() {
      if (this.getPlayerPatch() == null) {
         return;
      }

      yesman.epicfight.api.event.types.animation.AttackPhaseEndEvent modernEvent =
         new yesman.epicfight.api.event.types.animation.AttackPhaseEndEvent(
            this.getPlayerPatch(),
            this.animation,
            this.phase,
            this.phaseOrder,
            this.animationTerminated
         );

      ((yesman.epicfight.api.event.EventHook)EpicFightEventHooks.Animation.ATTACK_PHASE_END)
         .postWithListener(modernEvent, this.getPlayerPatch().getEventListener());
   }
}
