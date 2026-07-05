package com.hm.efn.skill.weapon_passive;

import com.hm.efn.EFN;
import com.hm.efn.EFNCommonConfig;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.doppelganger.DoppelgangerEntity;
import com.hm.efn.entity.doppelganger.DoppelgangerPatch;
import com.hm.efn.event.TickChange;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkillSlots;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MainFrameAnimation;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.registry.entries.EpicFightMobEffects;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;
import com.hm.efn.compat.epicfight.eventlistener.TakeDamageEvent.Attack;

public class YamatoPassive extends PassiveSkill {
   private static final UUID DODGE_RESET_UUID = UUID.fromString("e5f6a1b2-c1d8-11cd-a05b-0242ac120018");
   private static final UUID DODGE_CANCLE_UUID = UUID.fromString("4704c6de-0268-11ee-be56-0242ac120721");
   private static final UUID PARRY_STUN_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
   private static final Map<LivingEntity, Attack> parriedAttacks = new ConcurrentHashMap<>();
   private int parrycooldown;
   private boolean needenchantment;

   public YamatoPassive(SkillBuilder<?> builder) {
      super(builder);
   }

   public static boolean wasRecentlyParried(LivingEntity attacker) {
      return parriedAttacks.containsKey(attacker);
   }

   public static Attack getParryEvent(LivingEntity attacker) {
      return parriedAttacks.get(attacker);
   }

   public static void clearParryRecord(LivingEntity attacker) {
      parriedAttacks.remove(attacker);
   }

   private static void spawnBlockParticles(ServerPlayer player) {
      ServerLevel level = (ServerLevel)player.level();
      Vec3 center = player.position().add(0.0, 1.5, 0.0);

      for (int i = 0; i < 12; i++) {
         double angle = Math.toRadians(i * 30);
         Vec3 pos = center.add(Math.sin(angle) * 1.5, Math.cos(angle) * 0.3, Math.cos(angle) * 1.5);
         level.sendParticles(ParticleTypes.WAX_OFF, pos.x, pos.y, pos.z, 2, 0.0, 0.0, 0.0, 1.0);
      }
   }

   private static void spawnCounterParticles(ServerPlayer player) {
      ((ServerLevel)player.level()).sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.5, player.getZ(), 15, 0.3, 0.5, 0.3, 0.5);
   }

   private static void setJudgementCutEndSkill(PlayerPatch<?> executer) {
      SkillContainer artContainer = executer.getSkill(EFNSkillSlots.JUDGMENTCUT_END);
      if (artContainer != null && EFNSkills.JUDGEMENTCUTEND != null && artContainer.getSkill() != EFNSkills.JUDGEMENTCUTEND) {
         artContainer.setSkill(EFNSkills.JUDGEMENTCUTEND);
      }
   }

   public void loadDatapackParameters(CompoundTag parameters) {
      super.loadDatapackParameters(parameters);
      this.parrycooldown = parameters.getInt("parrycooldown");
      this.needenchantment = parameters.getBoolean("needenchantment");
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      EntityEventListener listener = container.getExecutor().getEventListener();
      PlayerPatch<?> executer = container.getExecutor();
      Player player = (Player)executer.getOriginal();
      setJudgementCutEndSkill(executer);
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.TAKE_DAMAGE_EVENT_ATTACK,
         PARRY_STUN_UUID,
         event -> {
            if (event.getDamageSource().getEntity() == ((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()) {
               event.setResult(ResultType.MISSED);
               event.cancel();
            } else {
               ItemStack mainHandItem = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
               boolean hasEnchantment = EFNEnchantment.getLevel(mainHandItem, EFNEnchantment.YAMATO_GUARD) > 0;
               boolean needEnchantment;
               if (player.isCreative()) {
                  needEnchantment = true;
               } else {
                  needEnchantment = !this.needenchantment || hasEnchantment;
               }

               if (Objects.requireNonNull(((ServerPlayerPatch)event.getPlayerPatch()).getAnimator().getPlayerFor(null)).getRealAnimation()
                  != EFNAnimations.DMC5_V_JC) {
                  int phaseLevel = ((ServerPlayerPatch)event.getPlayerPatch()).getEntityState().getLevel();
                  DamageSource damageSource = event.getDamageSource();
                  if (event.getDamage() > 0.0F
                     && phaseLevel > 0
                     && phaseLevel < 3
                     && (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.PARRY_COOLDOWN) <= 0
                     && this.isBlockableSource(damageSource)
                     && needEnchantment) {
                     LivingEntity attacker = damageSource.getDirectEntity() instanceof LivingEntity ? (LivingEntity)damageSource.getDirectEntity() : null;
                     Vec3 viewVector = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).getViewVector(1.0F);
                     Vec3 attackDirection = Optional.ofNullable(damageSource.getSourcePosition())
                        .map(pos -> pos.subtract(((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).position()).normalize())
                        .orElseGet(() -> attacker != null ? attacker.getLookAngle() : viewVector);
                     SkillContainer weaponSkillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(SkillSlots.WEAPON_INNATE);
                     if (weaponSkillContainer != null && weaponSkillContainer.getSkill() != null) {
                        float currentStack = weaponSkillContainer.getStack();
                        float maxStack = weaponSkillContainer.getSkill().getMaxStack();
                        float newStack = Math.min(maxStack, currentStack + 1.0F);
                        weaponSkillContainer.getSkill().setConsumptionSynchronize(weaponSkillContainer, newStack);
                     }

                     if (attackDirection.dot(viewVector) > 0.0) {
                        ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)EFNSounds.PARRY.get(), 1.0F, 1.0F);
                        event.cancel();
                        event.setParried(true);
                        event.setResult(ResultType.MISSED);
                        ((ServerPlayerPatch)event.getPlayerPatch()).resetActionTick();
                        ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                           .addEffect(new MobEffectInstance(EFNMobEffectRegistry.INVINCIBILITY_EFFECT, 30, 1, false, false, false));
                        if (attacker != null) {
                           attacker.removeEffect(EFNMobEffectRegistry.SIN_STUN_IMMUNITY);
                           attacker.removeEffect(EpicFightMobEffects.STUN_IMMUNITY);
                           attacker.removeEffect(MobEffects.DAMAGE_RESISTANCE);
                        }

                        container.getDataManager().setDataSync(EFNSKillDataKeys.PARRY_COOLDOWN, this.parrycooldown);
                        if ((Boolean)EFNCommonConfig.ENABLE_YAMATO_PARRY_TIMESLOWDOWN.get()) {
                           Level level = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).level();
                           MinecraftServer server = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).getServer();
                           if (server == null) {
                              return;
                           }

                           int globalPlayerCount = server.getPlayerCount();
                           boolean isDedicatedServer = level.getServer() != null && level.getServer().isDedicatedServer();
                           if (!isDedicatedServer) {
                              ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
                                 .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 11, 5, false, false, false));
                              int BegindelayTicks = (Integer)EFNCommonConfig.YAMATO_PARRY_TIMESLOW_DELAY_BEGIN.get();
                              int EndTicks = (Integer)EFNCommonConfig.YAMATO_PARRY_TIMESLOW_DELAY_END.get();
                              int amplifier = (Integer)EFNCommonConfig.YAMATO_PARRY_TIMESLOW_AMPLIFIER.get();
                              if (globalPlayerCount <= 1) {
                                 EFN.queueServerWork(BegindelayTicks, () -> TickChange.requestChange(amplifier));
                                 EFN.queueServerWork(EndTicks, () -> TickChange.requestChange(20.0F));
                              }
                           }
                        }

                        spawnCounterParticles((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal());
                        spawnBlockParticles((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal());
                        this.applyParryStun(event, attacker, container);
                     }
                  }
               }
            }
         },
         -1
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.SKILL_CAST_EVENT,
         DODGE_CANCLE_UUID,
         event -> {
            if (EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.DODGE) && !event.isStateExecutable()) {
               EntityState state = container.getExecutor().getEntityState();
               DynamicAnimation animation = (DynamicAnimation)Objects.requireNonNull(container.getExecutor().getAnimator().getPlayerFor(null))
                  .getRealAnimation()
                  .get();
               if (animation instanceof ActionAnimation && !(animation instanceof DodgeAnimation) && !event.isStateExecutable()) {
                  event.setStateExecutable(true);
               }
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.SKILL_CAST_EVENT, DODGE_RESET_UUID, event -> {
         if (EFNSkillChecks.skill(event.getSkillContainer()) instanceof DodgeSkill) {
            InvincibleAttachments.getPlayer(player).setCurrentNode(Yamato.Yamato_root);
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.ACTION_EVENT_SERVER,
         DODGE_RESET_UUID,
         event -> {
            Level level = ((Player)container.getExecutor().getOriginal()).level();
            double range = 20.0;

            for (Entity entity : level.getEntities(
               container.getExecutor().getOriginal(), ((Player)container.getExecutor().getOriginal()).getBoundingBox().inflate(range), entityx -> true
            )) {
               if (entity instanceof DoppelgangerEntity doppelganger
                  && doppelganger.getOwner() != null
                  && doppelganger.getOwner().equals(container.getExecutor().getOriginal())) {
                  DoppelgangerPatch doppelgangerPatch = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(doppelganger, DoppelgangerPatch.class);
                  SkillContainer innate = container.getExecutor().getSkill(SkillSlots.WEAPON_INNATE);
                  if (!(Boolean)innate.getDataManager().getDataValue(EFNSKillDataKeys.DOPPELGANGER_DELAY)) {
                     if (doppelgangerPatch != null) {
                        doppelgangerPatch.playAnimationSynchronized(((MainFrameAnimation)event.getAnimation().get()).getAccessor(), 0.0F);
                     }

                     if (doppelgangerPatch != null && doppelgangerPatch.getTarget() != null) {
                        ((DoppelgangerEntity)doppelgangerPatch.getOriginal())
                           .lookAt(
                              Anchor.EYES,
                              new Vec3(
                                 doppelgangerPatch.getTarget().getX(),
                                 doppelgangerPatch.getTarget().getEyeY() + 0.1,
                                 doppelgangerPatch.getTarget().getZ()
                              )
                           );
                     }
                  } else {
                     EFN.queueServerWork(
                        20,
                        () -> {
                           if (doppelgangerPatch != null) {
                              doppelgangerPatch.playAnimationSynchronized(((MainFrameAnimation)event.getAnimation().get()).getAccessor(), 0.0F);
                           }

                           if (doppelgangerPatch.getTarget() != null) {
                              ((DoppelgangerEntity)doppelgangerPatch.getOriginal())
                                 .lookAt(
                                    Anchor.EYES,
                                    new Vec3(
                                       doppelgangerPatch.getTarget().getX(),
                                       doppelgangerPatch.getTarget().getEyeY() + 0.1,
                                       doppelgangerPatch.getTarget().getZ()
                                    )
                                 );
                           }
                        }
                     );
                  }
               }
            }
         },
         999
      );
   }

   public void onRemoved(SkillContainer container) {
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.SKILL_CAST_EVENT, DODGE_CANCLE_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.SKILL_CAST_EVENT, DODGE_RESET_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.TAKE_DAMAGE_EVENT_ATTACK, PARRY_STUN_UUID, -1);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.ACTION_EVENT_SERVER, DODGE_RESET_UUID, 0);
   }

   private void applyParryStun(Attack event, LivingEntity attacker, SkillContainer container) {
      if (attacker != null && attacker.isAlive()) {
         if (container.getDataManager().hasData(EFNSKillDataKeys.STUN_COOLDOWN)) {
            int cooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.STUN_COOLDOWN);
            if (cooldown > 0) {
               return;
            }
         }

         parriedAttacks.put(attacker, event);
         EFN.queueServerWork(100, () -> parriedAttacks.remove(attacker));
         EFN.LOGGER.debug("Recorded parry against {}", attacker.getName().getString());
      }
   }

   private void handleCombatStaminaRegen(PlayerPatch<?> playerPatch) {
      if (playerPatch.getEntityState().inaction()) {
         float stamina = playerPatch.getStamina();
         float maxStamina = playerPatch.getMaxStamina();
         if (stamina < maxStamina) {
            float regenAmount = maxStamina * 0.04F / 20.0F;
            playerPatch.setStamina(Math.min(stamina + regenAmount, maxStamina));
            if (playerPatch.getStaminaRegenAwaitTicks() > 10) {
               playerPatch.setStaminaRegenAwaitTicks(10);
            }
         }
      }
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      setJudgementCutEndSkill(container.getExecutor());
      if (!container.getExecutor().isLogicalClient()) {
         this.handleCombatStaminaRegen(container.getExecutor());
      }

      if (!container.getExecutor().isLogicalClient()
         && container.getDataManager().hasData(EFNSKillDataKeys.STUN_COOLDOWN)
         && container.getDataManager().hasData(EFNSKillDataKeys.PARRY_COOLDOWN)) {
         int parryCooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.PARRY_COOLDOWN);
         int cooldown = (Integer)container.getDataManager().getDataValue(EFNSKillDataKeys.STUN_COOLDOWN);
         if (cooldown > 0) {
            container.getDataManager().setData(EFNSKillDataKeys.STUN_COOLDOWN, cooldown - 1);
         }

         if (parryCooldown > 0) {
            container.getDataManager().setData(EFNSKillDataKeys.PARRY_COOLDOWN, parryCooldown - 1);
         }
      }
   }

   private boolean isBlockableSource(DamageSource damageSource) {
      return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.is(DamageTypeTags.IS_FIRE);
   }
}
