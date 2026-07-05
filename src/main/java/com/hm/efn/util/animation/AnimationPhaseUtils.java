package com.hm.efn.util.animation;

import java.util.ArrayList;
import java.util.List;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;

public class AnimationPhaseUtils {
   public static List<AnimationPhaseUtils.PhaseInfo> getAllPhases(AttackAnimation attackAnimation) {
      List<AnimationPhaseUtils.PhaseInfo> allPhases = new ArrayList<>();
      if (attackAnimation != null && attackAnimation.phases != null) {
         for (int i = 0; i < attackAnimation.phases.length; i++) {
            Phase phase = attackAnimation.phases[i];
            allPhases.add(new AnimationPhaseUtils.PhaseInfo(phase, i));
         }

         return allPhases;
      } else {
         return allPhases;
      }
   }

   public static List<AnimationPhaseUtils.PhaseInfo> getActivePhases(AttackAnimation attackAnimation, float elapsedTime) {
      List<AnimationPhaseUtils.PhaseInfo> activePhases = new ArrayList<>();
      if (attackAnimation != null && attackAnimation.phases != null) {
         for (int i = 0; i < attackAnimation.phases.length; i++) {
            Phase phase = attackAnimation.phases[i];
            if (elapsedTime >= phase.start && elapsedTime < phase.end) {
               activePhases.add(new AnimationPhaseUtils.PhaseInfo(phase, i));
            }
         }

         return activePhases;
      } else {
         return activePhases;
      }
   }

   public static int getActivePhaseOrder(AttackAnimation attackAnimation, float elapsedTime) {
      List<AnimationPhaseUtils.PhaseInfo> activePhases = getActivePhases(attackAnimation, elapsedTime);
      return activePhases.isEmpty() ? -1 : activePhases.get(0).order;
   }

   public static AnimationPhaseUtils.PhaseInfo getPhaseByOrder(AttackAnimation attackAnimation, int phaseOrder) {
      return attackAnimation != null && attackAnimation.phases != null && phaseOrder >= 0 && phaseOrder < attackAnimation.phases.length
         ? new AnimationPhaseUtils.PhaseInfo(attackAnimation.phases[phaseOrder], phaseOrder)
         : null;
   }

   public static boolean isInAttackWindow(AttackAnimation attackAnimation, float elapsedTime, int phaseOrder) {
      AnimationPhaseUtils.PhaseInfo phaseInfo = getPhaseByOrder(attackAnimation, phaseOrder);
      if (phaseInfo == null) {
         return false;
      }

      Phase phase = phaseInfo.phase;
      return elapsedTime >= phase.preDelay && elapsedTime <= phase.contact;
   }

   public static int getTotalPhaseCount(AttackAnimation attackAnimation) {
      return attackAnimation != null && attackAnimation.phases != null ? attackAnimation.phases.length : 0;
   }

   public static class PhaseInfo {
      public final Phase phase;
      public final int order;

      public PhaseInfo(Phase phase, int order) {
         this.phase = phase;
         this.order = order;
      }

      public float getStartTime() {
         return this.phase.start;
      }

      public float getEndTime() {
         return this.phase.end;
      }

      public float getDuration() {
         return this.phase.end - this.phase.start;
      }

      public float getAttackWindowStart() {
         return this.phase.preDelay;
      }

      public float getAttackWindowEnd() {
         return this.phase.contact;
      }

      public float getAttackWindowDuration() {
         return this.phase.contact - this.phase.preDelay;
      }

      public boolean isTimeInPhase(float elapsedTime) {
         return elapsedTime >= this.phase.start && elapsedTime < this.phase.end;
      }

      public boolean isTimeInAttackWindow(float elapsedTime) {
         return elapsedTime >= this.phase.preDelay && elapsedTime <= this.phase.contact;
      }
   }
}
