package com.hm.efn.compat.epicfight.eventlistener;

import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public final class DealDamageEvent {
   private DealDamageEvent() {
   }

   public abstract static class LegacyDealDamage<T extends yesman.epicfight.api.event.types.entity.DealDamageEvent> extends LegacyPatchEvent<T> {
      protected LegacyDealDamage(T delegate) {
         super(delegate);
      }

      public LivingEntity getTarget() {
         return this.delegate.getTarget();
      }

      public EpicFightDamageSource getDamageSource() {
         return this.delegate.getDamageSource();
      }

      public float getAttackDamage() {
         if (this.delegate instanceof yesman.epicfight.api.event.types.entity.DealDamageEvent.Pre pre) {
            return pre.getModifiedDamage();
         }

         if (this.delegate instanceof yesman.epicfight.api.event.types.entity.DealDamageEvent.Post post) {
            return post.getModifiedDamage();
         }

         return this.delegate.getOriginalDamage();
      }

      public void setAttackDamage(float amount) {
         if (this.delegate instanceof yesman.epicfight.api.event.types.entity.DealDamageEvent.Pre pre) {
            pre.setModifiedDamage(amount);
         } else if (this.delegate instanceof yesman.epicfight.api.event.types.entity.DealDamageEvent.Post post) {
            post.setModifiedDamage(amount);
         }
      }
   }

   public static final class Attack extends LegacyDealDamage<yesman.epicfight.api.event.types.entity.DealDamageEvent.Income> {
      public Attack(yesman.epicfight.api.event.types.entity.DealDamageEvent.Income delegate) {
         super(delegate);
      }
   }

   public static final class Hurt extends LegacyDealDamage<yesman.epicfight.api.event.types.entity.DealDamageEvent.Pre> {
      public Hurt(yesman.epicfight.api.event.types.entity.DealDamageEvent.Pre delegate) {
         super(delegate);
      }
   }

   public static final class Damage extends LegacyDealDamage<yesman.epicfight.api.event.types.entity.DealDamageEvent.Post> {
      public Damage(yesman.epicfight.api.event.types.entity.DealDamageEvent.Post delegate) {
         super(delegate);
      }
   }
}
