package com.hm.efn.compat.epicfight.eventlistener;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.api.utils.AttackResult.ResultType;

public final class TakeDamageEvent {
   private TakeDamageEvent() {
   }

   public static class Hurt extends LegacyPatchEvent<yesman.epicfight.api.event.types.entity.TakeDamageEvent.Pre> {
      public Hurt(yesman.epicfight.api.event.types.entity.TakeDamageEvent.Pre delegate) {
         super(delegate);
      }

      public DamageSource getDamageSource() {
         return this.delegate.getDamageSource();
      }

      public float getDamage() {
         return this.delegate.getDamage();
      }
   }

   public static class Damage extends LegacyPatchEvent<yesman.epicfight.api.event.types.entity.TakeDamageEvent.Post> {
      public Damage(yesman.epicfight.api.event.types.entity.TakeDamageEvent.Post delegate) {
         super(delegate);
      }

      public DamageSource getDamageSource() {
         return this.delegate.getDamageSource();
      }

      public float getDamage() {
         return this.delegate.getDamage();
      }
   }

   public static class Attack extends LegacyPatchEvent<yesman.epicfight.api.event.types.entity.TakeDamageEvent.Income> {
      public Attack(yesman.epicfight.api.event.types.entity.TakeDamageEvent.Income delegate) {
         super(delegate);
      }

      public DamageSource getDamageSource() {
         return this.delegate.getDamageSource();
      }

      public float getDamage() {
         return this.delegate.getDamage();
      }

      public ResultType getResult() {
         return this.delegate.getResult();
      }

      public void setResult(ResultType result) {
         this.delegate.setResult(result);
      }

      public boolean isParried() {
         return this.delegate.isParried();
      }

      public void setParried(boolean parried) {
         this.delegate.setParried(parried);
      }

      public yesman.epicfight.api.event.types.entity.TakeDamageEvent.Income unwrap() {
         return this.delegate;
      }
   }
}
