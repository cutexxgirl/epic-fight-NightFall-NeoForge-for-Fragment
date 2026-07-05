package com.hm.efn.gameasset.animations;

import com.guhao.vix.camera.VIXCameraFOV;
import com.guhao.vix.camera.VIXCameraFOV.Easing;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.animations.types.sekiro.SekiroArtsAnimation;
import com.hm.efn.animations.types.sekiro.SekiroAttackAnimation;
import com.hm.efn.client.effek.DragonFlashEffek;
import com.hm.efn.client.effek.DragonFlashFinishEffek;
import com.hm.efn.client.effek.MistEffek;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.EFNVFXManagers;
import com.hm.efn.gameasset.EFNExtraDamageInstance;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffekUnits;
import com.hm.efn.util.ItemStackData;
import com.hm.efn.util.ParticleEffectInvoker;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.merlin204.avalon.util.AvalonEventUtils;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordGetter;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class EFNSekiroAnimations {
   public static final Collider MORTAL_BLADE = new MultiOBBCollider(3, 1.5, 7.0, 1.5, 0.0, 7.0, 0.0);
   public static final Collider SAKURA_DANCE_COLL = new MultiOBBCollider(3, 1.0, 2.0, 1.0, 0.0, 2.0, 0.0);
   public static final Collider DRAGON_FLASH_COLL = new MultiOBBCollider(3, 1.0, 2.5, 1.0, 0.0, 2.5, 0.0);
   public static final Collider ICHIMONJI = new MultiOBBCollider(5, 0.4, 0.4, 1.2, 0.0, 0.0, -0.7);
   public static AnimationAccessor<StaticAnimation> KUSABIMARU_IDLE;
   public static AnimationAccessor<MovementAnimation> KUSABIMARU_WALK;
   public static AnimationAccessor<MovementAnimation> KUSABIMARU_RUN;
   public static AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO1;
   public static AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO2;
   public static AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO3;
   public static AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO4;
   public static AnimationAccessor<SekiroAttackAnimation> KUSABIMARU_AUTO5;
   public static AnimationAccessor<SekiroAttackAnimation> DRAGON_FLASH;
   public static AnimationAccessor<SekiroAttackAnimation> SHADOW_RUSH;
   public static AnimationAccessor<SekiroAttackAnimation> ICHIMONJI_1;
   public static AnimationAccessor<SekiroAttackAnimation> ICHIMONJI_2;
   public static AnimationAccessor<AttackAnimation> SAKURA_DANCE;
   public static AnimationAccessor<AttackAnimation> RUSHING_TEMPO;
   public static AnimationAccessor<SekiroArtsAnimation> MORTAL_BLADE_1;
   public static AnimationAccessor<SekiroArtsAnimation> MORTAL_BLADE_2;
   static ArmatureAccessor<Armature> Sekiro = ArmatureAccessor.create("efn", "weapon/kusabimaru", Armature::new);
   static Joint kusabimaru = Sekiro.get().searchJointByName("Tool_R");
   static Joint mortalBlade = Sekiro.get().searchJointByName("Mortal_Blade");
   static Joint Slash = Sekiro.get().searchJointByName("Tool_L");
   public static final MoveCoordGetter KUSABIMARU_MODEL_COORD = (animation, entitypatch, coord, prevElapsedTime, elapsedTime) -> {
      JointTransform oJt = coord.getInterpolatedTransform(prevElapsedTime);
      JointTransform jt = coord.getInterpolatedTransform(elapsedTime);
      Vec4f prevpos = new Vec4f(oJt.translation());
      Vec4f currentpos = new Vec4f(jt.translation());
      OpenMatrix4f rotationTransform = entitypatch.getModelMatrix(1.0F).removeTranslation().removeScale();
      OpenMatrix4f localTransform = entitypatch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
      rotationTransform.mulBack(localTransform);
      currentpos.transform(rotationTransform);
      prevpos.transform(rotationTransform);
      boolean hasNoGravity = ((LivingEntity)entitypatch.getOriginal()).isNoGravity();
      boolean moveVertical = animation.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false)
         || animation.getProperty(ActionAnimationProperty.COORD).isPresent();
      float dx = prevpos.x - currentpos.x;
      float dy = !moveVertical && !hasNoGravity ? 0.0F : currentpos.y - prevpos.y;
      float dz = prevpos.z - currentpos.z;
      dx = Math.abs(dx) > 1.0E-4F ? dx : 0.0F;
      dz = Math.abs(dz) > 1.0E-4F ? dz : 0.0F;
      return new Vec3f(dx * 1.4F, dy, dz * 1.4F);
   };

   public static InTimeEvent setKusabimaruSheathMesh(float triggerTime, boolean isSheath) {
      return InTimeEvent.create(triggerTime, (entityPatch, self, params) -> {
         CapabilityItem mainHandCap = entityPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
         if (!mainHandCap.isEmpty()) {
            ItemStack mainHandItem = ((LivingEntity)entityPatch.getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
            ItemStackData.putInt(mainHandItem, "kusabimaru_sheath", isSheath ? 1 : 0);
         }
      }, Side.BOTH);
   }

   public static SimpleEvent setKusabimaruSheathMesh(boolean isSheath) {
      return SimpleEvent.create((entityPatch, self, params) -> {
         CapabilityItem mainHandCap = entityPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
         if (!mainHandCap.isEmpty()) {
            ItemStack mainHandItem = ((LivingEntity)entityPatch.getOriginal()).getItemInHand(InteractionHand.MAIN_HAND);
            ItemStackData.putInt(mainHandItem, "kusabimaru_sheath", isSheath ? 1 : 0);
         }
      }, Side.BOTH);
   }

   public static void build(AnimationBuilder builder) {
      KUSABIMARU_IDLE = builder.nextAccessor(
         "biped/sekiro/kusabimaru/kusabimaru_idle",
         accessor -> new StaticAnimation(true, accessor, Sekiro).addEvents(new AnimationEvent[]{setKusabimaruSheathMesh(0.0F, false)})
      );
      KUSABIMARU_WALK = builder.nextAccessor(
         "biped/sekiro/kusabimaru/kusabimaru_walk",
         accessor -> (MovementAnimation)new MovementAnimation(true, accessor, Sekiro).addEvents(new AnimationEvent[]{setKusabimaruSheathMesh(0.0F, false)})
      );
      KUSABIMARU_RUN = builder.nextAccessor(
         "biped/sekiro/kusabimaru/kusabimaru_run",
         accessor -> (MovementAnimation)new MovementAnimation(true, accessor, Sekiro).addEvents(new AnimationEvent[]{setKusabimaruSheathMesh(0.0F, false)})
      );
      KUSABIMARU_AUTO1 = builder.nextAccessor(
         "biped/sekiro/kusabimaru/kusabimaru_auto1",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.12F, accessor, Sekiro, 1.0F, 1.0F, AvalonAnimationUtils.createSimplePhase(25, 32, 36, InteractionHand.MAIN_HAND, 1.0F, 1.0F, kusabimaru, null)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_1.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.08F)
      );
      KUSABIMARU_AUTO2 = builder.nextAccessor(
         "biped/sekiro/kusabimaru/kusabimaru_auto2",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.05F,
               accessor,
               Sekiro,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(20, 26, 30, InteractionHand.MAIN_HAND, 1.05F, 1.05F, kusabimaru, null)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_4.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.08F)
      );
      KUSABIMARU_AUTO3 = builder.nextAccessor(
         "biped/sekiro/kusabimaru/kusabimaru_auto3",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.05F, accessor, Sekiro, 1.0F, 1.0F, AvalonAnimationUtils.createSimplePhase(26, 33, 38, InteractionHand.MAIN_HAND, 1.1F, 1.1F, kusabimaru, null)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_2.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
      );
      KUSABIMARU_AUTO4 = builder.nextAccessor(
         "biped/sekiro/kusabimaru/kusabimaru_auto4",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.05F,
               accessor,
               Sekiro,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(15, 24, 33, InteractionHand.MAIN_HAND, 1.15F, 1.15F, kusabimaru, null)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_3.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.05F)
      );
      KUSABIMARU_AUTO5 = builder.nextAccessor(
         "biped/sekiro/kusabimaru/kusabimaru_auto5",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.05F, accessor, Sekiro, 1.0F, 1.0F, AvalonAnimationUtils.createSimplePhase(18, 29, 34, InteractionHand.MAIN_HAND, 1.3F, 1.3F, kusabimaru, null)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_LIGHT_4.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.04F)
      );
      DRAGON_FLASH = builder.nextAccessor(
         "biped/sekiro/kusabimaru/dragon_flash",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.1F,
               accessor,
               Sekiro,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(96, 107, 150, InteractionHand.MAIN_HAND, 3.0F, 1.5F, Slash, DRAGON_FLASH_COLL)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.MORTAL_BLADE_WHOOSH.get())
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               new AnimationEvent[]{
                  setKusabimaruSheathMesh(0.5F, true),
                  InTimeEvent.create(
                     0.01F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 10, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.65F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()) {
                           if ((Boolean)EFNClientConfig.SEKIRO_DRAGONFLASH_VFX.get()) {
                              Level level = ((LivingEntity)entityPatch.getOriginal()).level();
                              AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
                              if (player != null) {
                                 float prevElapsedTime = player.getPrevElapsedTime();
                                 float elapsedTime = player.getElapsedTime();
                                 float step = (elapsedTime - prevElapsedTime) / 10.0F;
                                 Vec3f pointOffset = Vec3f.fromDoubleVector(Vec3.ZERO);
                                 Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(entityPatch, kusabimaru, step, pointOffset);
                                 Random random = new Random();

                                 for (int i = 0; i < 2; i++) {
                                    float rx = random.nextFloat(-90.0F, 90.0F);
                                    float ry = random.nextFloat(-90.0F, 90.0F);
                                    float rz = random.nextFloat(-90.0F, 90.0F);
                                    DragonFlashEffek.playDragonFlash(
                                       DragonFlashEffek.Type.LEVEL1,
                                       level,
                                       worldPos.x() - ((LivingEntity)entityPatch.getOriginal()).getX(),
                                       worldPos.y() - ((LivingEntity)entityPatch.getOriginal()).getY(),
                                       worldPos.z() - ((LivingEntity)entityPatch.getOriginal()).getZ(),
                                       rx,
                                       ry,
                                       rz,
                                       1.15F,
                                       entityPatch.getOriginal()
                                    );
                                 }

                                 float rx2 = random.nextFloat(-90.0F, 90.0F);
                                 float ry2 = random.nextFloat(-90.0F, 90.0F);
                                 float rz2 = random.nextFloat(-90.0F, 90.0F);
                                 DragonFlashEffek.playDragonFlash(
                                    DragonFlashEffek.Type.LEVEL2,
                                    level,
                                    worldPos.x() - ((LivingEntity)entityPatch.getOriginal()).getX(),
                                    worldPos.y() - ((LivingEntity)entityPatch.getOriginal()).getY(),
                                    worldPos.z() - ((LivingEntity)entityPatch.getOriginal()).getZ(),
                                    rx2,
                                    ry2,
                                    rz2,
                                    1.15F,
                                    entityPatch.getOriginal()
                                 );
                                 float rx3 = random.nextFloat(-90.0F, 90.0F);
                                 float ry3 = random.nextFloat(-90.0F, 90.0F);
                                 float rz3 = random.nextFloat(-90.0F, 90.0F);
                                 DragonFlashEffek.playDragonFlash(
                                    DragonFlashEffek.Type.LEVEL3,
                                    level,
                                    worldPos.x() - ((LivingEntity)entityPatch.getOriginal()).getX(),
                                    worldPos.y() - ((LivingEntity)entityPatch.getOriginal()).getY(),
                                    worldPos.z() - ((LivingEntity)entityPatch.getOriginal()).getZ(),
                                    rx3,
                                    ry3,
                                    rz3,
                                    1.15F,
                                    entityPatch.getOriginal()
                                 );
                              }
                           }
                        }
                     },
                     Side.CLIENT
                  ),
                  InTimeEvent.create(
                     1.61667F,
                     (entityPatch, self, params) -> {
                        if (EffekUnits.VFXENABLE()) {
                           Level level = ((LivingEntity)entityPatch.getOriginal()).level();
                           LivingEntity original = (LivingEntity)entityPatch.getOriginal();
                           DragonFlashFinishEffek.playDragonFlashFinish(
                              DragonFlashFinishEffek.Type.LEVEL1,
                              level,
                              ((LivingEntity)entityPatch.getOriginal()).position().x(),
                              ((LivingEntity)entityPatch.getOriginal()).position().y(),
                              ((LivingEntity)entityPatch.getOriginal()).position().z(),
                              0.0F,
                              EffekUnits.getRY(entityPatch),
                              0.0F,
                              1.0F
                           );
                        }
                     },
                     Side.CLIENT
                  ),
                  ParticleEffectInvoker.CustomGroundSplit(110, 2.0, 0.0, 0.0, 0.0, 2.0F, true, false, true, false),
                  ParticleEffectInvoker.createDragonFlashBurst(110.0F, 2.0F),
                  ParticleEffectInvoker.CustomGroundSplit(120, 5.0, 0.0, 0.0, 0.0, 2.0F, true, true, true, false),
                  ParticleEffectInvoker.createDragonFlashBurst(120.0F, 5.0F),
                  ParticleEffectInvoker.CustomGroundSplit(130, 8.0, 0.0, 0.0, 0.0, 2.0F, true, false, true, false),
                  ParticleEffectInvoker.createDragonFlashBurst(130.0F, 8.0F),
                  ParticleEffectInvoker.CustomGroundSplit(140, 11.0, 0.0, 0.0, 0.0, 2.0F, true, true, true, false),
                  ParticleEffectInvoker.createDragonFlashBurst(140.0F, 11.0F),
                  ParticleEffectInvoker.CustomGroundSplit(150, 14.0, 0.0, 0.0, 0.0, 2.0F, true, false, true, false),
                  ParticleEffectInvoker.createDragonFlashBurst(150.0F, 14.0F),
                  ParticleEffectInvoker.CustomGroundSplit(160, 17.0, 0.0, 0.0, 0.0, 2.0F, true, false, true, false),
                  ParticleEffectInvoker.createDragonFlashBurst(160.0F, 17.0F),
                  serverPlaySound(30, (SoundEvent)EFNSounds.MORTAL_BLADE_SWORD_OUT.get(), 1.2F),
                  serverPlaySound(31, (SoundEvent)EFNSounds.CIRCULATE_QI.get(), 1.0F),
                  serverPlaySound(100, SoundEvents.ENDER_DRAGON_AMBIENT, 0.5F),
                  setKusabimaruSheathMesh(1.35F, false),
                  EFNVFXManagers.summonVFX(EFNVFXManagers.DRAGON_FLASH_SLASH, 96, 1.0, 0.0, 0.0, 1.0F, Vec3f.ZERO)
               }
            )
      );
      SHADOW_RUSH = builder.nextAccessor(
         "biped/sekiro/kusabimaru/shadow_rush",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.1F, accessor, Sekiro, 1.0F, 1.0F, AvalonAnimationUtils.createSimplePhase(43, 51, 60, InteractionHand.MAIN_HAND, 2.0F, 2.0F, kusabimaru, null)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_4.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
      );
      ICHIMONJI_1 = builder.nextAccessor(
         "biped/sekiro/kusabimaru/ichimonji_1",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.1F,
               accessor,
               Sekiro,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(80, 90, 120, InteractionHand.MAIN_HAND, 1.0F, 1.0F, kusabimaru, ICHIMONJI)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(ActionAnimationProperty.COORD_GET, KUSABIMARU_MODEL_COORD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .newTimePair(0.0F, 1.8F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.CustomGroundSplit(82, 4.0, 0.0, 0.0, 0.0, 2.0F, true, false, true, false),
                  InTimeEvent.create(1.4167F, (entityPatch, self, params) -> {
                     if (EffekUnits.VFXENABLE()) {
                        new Random();
                     }
                  }, Side.CLIENT)
               }
            )
      );
      ICHIMONJI_2 = builder.nextAccessor(
         "biped/sekiro/kusabimaru/ichimonji_2",
         accessor -> (SekiroAttackAnimation)new SekiroAttackAnimation(
               0.1F,
               accessor,
               Sekiro,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(50, 60, 70, InteractionHand.MAIN_HAND, 1.0F, 1.0F, kusabimaru, ICHIMONJI)
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addEvents(
               new AnimationEvent[]{
                  ParticleEffectInvoker.CustomGroundSplit(55, 4.0, 0.0, 0.0, 0.0, 2.0F, true, false, true, false),
                  InTimeEvent.create(0.33F, (entityPatch, self, params) -> {
                     if (EffekUnits.VFXENABLE()) {
                        new Random();
                     }
                  }, Side.CLIENT)
               }
            )
      );
      SAKURA_DANCE = builder.nextAccessor(
         "biped/sekiro/kusabimaru/sakura_dance",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.1F,
               accessor,
               Sekiro,
               new Phase[]{
                  new Phase(0.0F, 0.34444445F, 0.44444445F, 0.5555556F, 0.5555556F, Slash, SAKURA_DANCE_COLL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.0F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_1.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                  new Phase(0.5555556F, 0.5555556F, 0.7222222F, 1.1111112F, 1.1111112F, Slash, SAKURA_DANCE_COLL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.5F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.5F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get()),
                  new Phase(1.1111112F, 1.1111112F, 1.2555555F, 1.6666666F, 2.5222223F, Slash, SAKURA_DANCE_COLL)
                     .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.8F))
                     .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.8F))
                     .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.WHOOSH_HEAVY_2.get())
                     .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
               }
            )
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addProperty(AttackAnimationProperty.MOVE_VERTICAL, true)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 5)
            .addProperty(AttackAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.6666666F}))
            .addProperty(
               StaticAnimationProperty.ON_BEGIN_EVENTS,
               List.of(SimpleEvent.create((ep, anim, objs) -> VIXCameraFOV.pulseZoom(1.5F, 60, 0.1F, Easing.EASE_OUT_CUBIC, Easing.EASE_IN_QUAD), Side.CLIENT))
            )
            .newTimePair(0.0F, 1.6666666F)
            .addStateRemoveOld(EntityState.UPDATE_LIVING_MOTION, false)
            .newTimePair(0.0F, Float.MAX_VALUE)
            .addState(
               EntityState.ATTACK_RESULT,
               (Function<DamageSource, ResultType>)damageSource -> damageSource.is(DamageTypeTags.IS_FALL) ? ResultType.MISSED : ResultType.SUCCESS
            )
      );
      RUSHING_TEMPO = builder.nextAccessor(
         "biped/sekiro/kusabimaru/rushing_tempo",
         accessor -> (AttackAnimation)new AttackAnimation(
               0.05F, 0.0F, 0.2F, 0.25F, 0.6F, null, ((HumanoidArmature)Armatures.BIPED.get()).toolR, accessor, Armatures.BIPED
            )
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EpicFightSounds.WHOOSH_SHARP.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.BLADE_RUSH_FINISHER.get())
            .addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
            .addProperty(ActionAnimationProperty.COORD_SET_BEGIN, null)
            .addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION)
            .addProperty(ActionAnimationProperty.ENTITY_YROT_PROVIDER, MoveCoordFunctions.LOOK_DEST)
            .newTimePair(0.0F, 0.4F)
            .addStateRemoveOld(EntityState.COMBO_ATTACKS_DOABLE, false)
            .addEvents(new AnimationEvent[]{AvalonEventUtils.simpleGroundSplit(15, 1.0, 0.0, 0.0, 0.0, 2.0F, true)})
      );
      MORTAL_BLADE_1 = builder.nextAccessor(
         "biped/sekiro/fushigiri/fushigiri_1",
         accessor -> (SekiroArtsAnimation)new SekiroArtsAnimation(
               0.15F,
               accessor,
               Sekiro,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(110, 122, 135, InteractionHand.MAIN_HAND, 2.0F, 3.0F, Slash, MORTAL_BLADE)
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
            .addProperty(
               AttackPhaseProperty.SOURCE_TAG,
               Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.GUARD_PUNCTURE)
            )
            .addProperty(
               AttackPhaseProperty.EXTRA_DAMAGE,
               Set.of(
                  EFNExtraDamageInstance.MAX_HEALTH_PERCENTAGE_DAMAGE.create(new float[]{0.025F, 50.0F, 200.0F}),
                  EFNExtraDamageInstance.EXTRA_DAMAGE.create(new float[]{30.0F}),
                  ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 6)
            .addEvents(
               new AnimationEvent[]{
                  setKusabimaruSheathMesh(0.1F, true),
                  AvalonEventUtils.simpleSound(1, (SoundEvent)EFNSounds.MORTAL_BLADE_SWORD_OUT.get(), 1.3F, 1.0F),
                  AvalonEventUtils.simpleSound(25, (SoundEvent)EFNSounds.MORTAL_BLADE_CHARGE1.get(), 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(102, (SoundEvent)EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get(), 1.3F, 1.0F),
                  setKusabimaruSheathMesh(3.3333333F, false),
                  localPlaySound(193, (SoundEvent)EFNSounds.MORTAL_BLADE_SWORD_IN.get(), 1.2F),
                  AvalonEventUtils.simpleCameraShake(103, 10, 4.0F, 2.0F, 8.0F),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 10, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 55, 2, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 55, 1, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(0.01F, (entityPatch, self, params) -> {
                     if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SEKIRO_MORTAL_BLADE_VFX.get()) {
                        MistEffek.playMist(
                           MistEffek.Type.MIST, ((LivingEntity)entityPatch.getOriginal()).level(), 0.0, 0.8, 0.0, 0.35F, entityPatch.getOriginal()
                        );
                     }
                  }, Side.CLIENT),
                  mortalBladeChargeParticleTrail(
                     20, 100, new Vec3(0.0, 0.0, 0.0), new Vec3(0.0, 1.6, 0.0), 10.0F, 10, (ParticleOptions)EFNParticles.MORTAL_BLADE_CHARGE_RED.get(), 0.1F
                  ),
                  mortalBladeChargeParticleTrail(20, 90, new Vec3(0.0, 0.15, 0.0), new Vec3(0.0, 0.4, 0.0), 1.0F, 2, ParticleTypes.SMOKE, 0.055F),
                  mortalBladeParticleTrail(
                     112, 123, new Vec3(-0.7, 11.0, 0.0), new Vec3(-0.7, 12.0, 0.0), 50.0F, 30, (ParticleOptions)EFNParticles.MORTAL_BLADE.get(), 0.25F
                  )
               }
            )
      );
      MORTAL_BLADE_2 = builder.nextAccessor(
         "biped/sekiro/fushigiri/fushigiri_2",
         accessor -> (SekiroArtsAnimation)new SekiroArtsAnimation(
               0.15F,
               accessor,
               Sekiro,
               1.0F,
               1.0F,
               AvalonAnimationUtils.createSimplePhase(103, 118, 135, InteractionHand.MAIN_HAND, 2.0F, 3.0F, Slash, MORTAL_BLADE)
            )
            .addProperty(AttackPhaseProperty.HIT_SOUND, (SoundEvent)EpicFightSounds.EVISCERATE.get())
            .addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
            .addProperty(AttackPhaseProperty.SWING_SOUND, (SoundEvent)EFNSounds.NOSOUND.get())
            .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.setter(100.0F))
            .addProperty(
               AttackPhaseProperty.SOURCE_TAG,
               Set.of(EpicFightDamageTypeTags.WEAPON_INNATE, EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.GUARD_PUNCTURE)
            )
            .addProperty(
               AttackPhaseProperty.EXTRA_DAMAGE,
               Set.of(
                  EFNExtraDamageInstance.MAX_HEALTH_PERCENTAGE_DAMAGE.create(new float[]{0.025F, 50.0F, 200.0F}),
                  EFNExtraDamageInstance.EXTRA_DAMAGE.create(new float[]{30.0F}),
                  ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])
               )
            )
            .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
            .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.0F)
            .addProperty(ActionAnimationProperty.CANCELABLE_MOVE, true)
            .addProperty(AttackAnimationProperty.EXTRA_COLLIDERS, 2)
            .addEvents(
               new AnimationEvent[]{
                  setKusabimaruSheathMesh(0.1F, true),
                  AvalonEventUtils.simpleSound(25, (SoundEvent)EFNSounds.MORTAL_BLADE_CHARGE2.get(), 1.0F, 1.0F),
                  AvalonEventUtils.simpleSound(101, (SoundEvent)EFNSounds.MORTAL_BLADE_BLOODWHOOSH.get(), 1.3F, 1.0F),
                  setKusabimaruSheathMesh(3.3333333F, false),
                  localPlaySound(195, (SoundEvent)EFNSounds.MORTAL_BLADE_SWORD_IN.get(), 1.2F),
                  AvalonEventUtils.simpleCameraShake(103, 10, 4.0F, 2.0F, 8.0F),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 10, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 55, 2, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(
                     0.1F,
                     (entityPatch, self, params) -> ((LivingEntity)entityPatch.getOriginal())
                        .addEffect(new MobEffectInstance(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 55, 1, false, false, false)),
                     Side.SERVER
                  ),
                  InTimeEvent.create(0.01F, (entityPatch, self, params) -> {
                     if (EffekUnits.VFXENABLE() && (Boolean)EFNClientConfig.SEKIRO_MORTAL_BLADE_VFX.get()) {
                        MistEffek.playMist(
                           MistEffek.Type.MIST, ((LivingEntity)entityPatch.getOriginal()).level(), 0.0, 1.45, 0.0, 0.35F, entityPatch.getOriginal()
                        );
                     }
                  }, Side.CLIENT),
                  mortalBladeChargeParticleTrail(
                     25, 100, new Vec3(0.0, 0.0, 0.0), new Vec3(0.0, 1.6, 0.0), 10.0F, 10, (ParticleOptions)EFNParticles.MORTAL_BLADE_CHARGE_RED.get(), 0.1F
                  ),
                  mortalBladeChargeParticleTrail(35, 70, new Vec3(-0.1, 0.4, 0.1F), new Vec3(-0.1, 1.0, 0.1F), 1.0F, 5, ParticleTypes.SMOKE, 0.1F),
                  mortalBladeParticleTrail(
                     104, 121, new Vec3(-0.6, 11.0, 0.0), new Vec3(-0.6, 12.0, 0.0), 50.0F, 30, (ParticleOptions)EFNParticles.MORTAL_BLADE.get(), 0.35F
                  )
               }
            )
      );
   }

   public static InPeriodEvent mortalBladeChargeParticleTrail(
      int startFrame, int endFrame, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, ParticleOptions particleOptions, float random
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      Joint finalJoint = mortalBlade;
      return InPeriodEvent.create(
         start,
         end,
         (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = 0.0F;
            if (player != null) {
               prevElapsedTime = player.getPrevElapsedTime();
            }

            float elapsedTime = 0.0F;
            if (player != null) {
               elapsedTime = player.getElapsedTime();
            }

            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3f trailDirection = new Vec3f(
               (float)(endOffset.x - startOffset.x),
               (float)(endOffset.y - startOffset.y),
               (float)(endOffset.z - startOffset.z)
            );

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
               for (int i = 0; i <= particleCount; i++) {
                  float ratio = (float)i / particleCount;
                  Vec3f pointOffset = new Vec3f(
                     (float)(startOffset.x + trailDirection.x * ratio),
                     (float)(startOffset.y + trailDirection.y * ratio),
                     (float)(startOffset.z + trailDirection.z * ratio)
                  );
                  double randX = (Math.random() - 0.5) * random;
                  double randY = (Math.random() - 0.5) * random;
                  double randZ = (Math.random() - 0.5) * random;
                  Vec3 worldPos = getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                  if (((LivingEntity)entityPatch.getOriginal()).level().isClientSide) {
                     ((LivingEntity)entityPatch.getOriginal())
                        .level()
                        .addParticle(particleOptions, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0, 0.0, 0.0);
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InPeriodEvent mortalBladeParticleTrail(
      int startFrame, int endFrame, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, ParticleOptions particleOptions, float random
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      Joint finalJoint = Slash;
      return InPeriodEvent.create(
         start,
         end,
         (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = 0.0F;
            if (player != null) {
               prevElapsedTime = player.getPrevElapsedTime();
            }

            float elapsedTime = 0.0F;
            if (player != null) {
               elapsedTime = player.getElapsedTime();
            }

            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3f trailDirection = new Vec3f(
               (float)(endOffset.x - startOffset.x),
               (float)(endOffset.y - startOffset.y),
               (float)(endOffset.z - startOffset.z)
            );

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
               for (int i = 0; i <= particleCount; i++) {
                  float ratio = (float)i / particleCount;
                  Vec3f pointOffset = new Vec3f(
                     (float)(startOffset.x + trailDirection.x * ratio),
                     (float)(startOffset.y + trailDirection.y * ratio),
                     (float)(startOffset.z + trailDirection.z * ratio)
                  );
                  double randX = (Math.random() - 0.5) * random;
                  double randY = (Math.random() - 0.5) * random;
                  double randZ = (Math.random() - 0.5) * random;
                  Vec3 worldPos = getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                  if (((LivingEntity)entityPatch.getOriginal()).level().isClientSide) {
                     ((LivingEntity)entityPatch.getOriginal())
                        .level()
                        .addParticle(particleOptions, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0, 0.0, 0.0);
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InPeriodEvent sakuraDanceParticleTrail(
      int startFrame, int endFrame, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, ParticleOptions particleOptions, float random
   ) {
      float start = startFrame / 90.0F;
      float end = endFrame / 90.0F;
      Joint finalJoint = Slash;
      return InPeriodEvent.create(
         start,
         end,
         (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
            float prevElapsedTime = 0.0F;
            if (player != null) {
               prevElapsedTime = player.getPrevElapsedTime();
            }

            float elapsedTime = 0.0F;
            if (player != null) {
               elapsedTime = player.getElapsedTime();
            }

            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3f trailDirection = new Vec3f(
               (float)(endOffset.x - startOffset.x),
               (float)(endOffset.y - startOffset.y),
               (float)(endOffset.z - startOffset.z)
            );

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
               for (int i = 0; i <= particleCount; i++) {
                  float ratio = (float)i / particleCount;
                  Vec3f pointOffset = new Vec3f(
                     (float)(startOffset.x + trailDirection.x * ratio),
                     (float)(startOffset.y + trailDirection.y * ratio),
                     (float)(startOffset.z + trailDirection.z * ratio)
                  );
                  double randX = (Math.random() - 0.5) * random;
                  double randY = (Math.random() - 0.5) * random;
                  double randZ = (Math.random() - 0.5) * random;
                  Vec3 worldPos = getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                  if (((LivingEntity)entityPatch.getOriginal()).level().isClientSide) {
                     ((LivingEntity)entityPatch.getOriginal())
                        .level()
                        .addParticle(particleOptions, worldPos.x + randX, worldPos.y + randY, worldPos.z + randZ, 0.0, 0.0, 0.0);
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent localPlaySound(int startFrame, SoundEvent soundEvent, float volume) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> entityPatch.playSound(soundEvent, volume, 1.0F, 1.0F), Side.LOCAL_CLIENT);
   }

   public static InTimeEvent serverPlaySound(int startFrame, SoundEvent soundEvent, float volume) {
      float start = startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> entityPatch.playSound(soundEvent, volume, 1.0F, 1.0F), Side.SERVER);
   }

   public static Vec3 getJointWorldRawPos(LivingEntityPatch<?> entityPatch, Joint joint, float time, Vec3f offset) {
      Animator animator = entityPatch.getAnimator();
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      Pose pose = ((DynamicAnimation)animator.getPlayerFor((AssetAccessor)null).getAnimation().get()).getRawPose(time);
      OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(pose, joint);
      transformMatrix.translate(offset);
      OpenMatrix4f rotation = new OpenMatrix4f().rotate(-((float)Math.toRadians(entityPatch.getYRot() + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
      OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
      return new Vec3(
         transformMatrix.m30 + (float)entity.getX(), transformMatrix.m31 + (float)entity.getY(), transformMatrix.m32 + (float)entity.getZ()
      );
   }

   public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint, Vec3f offset, float partialTicks) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(entityPatch.getAnimator().getPose(partialTicks), joint);
      transformMatrix.translate(offset);
      OpenMatrix4f rotation = new OpenMatrix4f().rotate(-((float)Math.toRadians(entityPatch.getYRot() + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
      OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
      return new Vec3(
         transformMatrix.m30 + (float)entity.getX(), transformMatrix.m31 + (float)entity.getY(), transformMatrix.m32 + (float)entity.getZ()
      );
   }
}
