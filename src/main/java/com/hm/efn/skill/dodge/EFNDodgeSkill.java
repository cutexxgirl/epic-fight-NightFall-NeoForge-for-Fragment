package com.hm.efn.skill.dodge;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.InputUtils;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;

public class EFNDodgeSkill extends Skill {
   public static final int DIRECTION_FORWARD = 0;
   public static final int DIRECTION_BACKWARD = 1;
   public static final int DIRECTION_LEFT = 2;
   public static final int DIRECTION_RIGHT = 3;
   public static final int DIRECTION_UP = 4;
   protected final Map<WeaponCategory, Map<Style, Map<Integer, Supplier<AnimationAccessor<? extends StaticAnimation>>>>> animations;
   protected final Supplier<AnimationAccessor<? extends StaticAnimation>> forwardAnim;
   protected final Supplier<AnimationAccessor<? extends StaticAnimation>> backwardAnim;
   protected final Supplier<AnimationAccessor<? extends StaticAnimation>> leftAnim;
   protected final Supplier<AnimationAccessor<? extends StaticAnimation>> rightAnim;
   protected final Supplier<AnimationAccessor<? extends StaticAnimation>> upAnim;

   public EFNDodgeSkill(EFNDodgeSkill.Builder builder) {
      super(builder);
      this.animations = builder.animations;
      this.forwardAnim = builder.forwardAnim;
      this.backwardAnim = builder.backwardAnim;
      this.leftAnim = builder.leftAnim;
      this.rightAnim = builder.rightAnim;
      this.upAnim = builder.upAnim;
   }

   public static EFNDodgeSkill.Builder createEFNDodgeBuilder(Function<EFNDodgeSkill.Builder, ? extends EFNDodgeSkill> constructor) {
      return (EFNDodgeSkill.Builder)new EFNDodgeSkill.Builder(constructor)
         .setCategory(SkillCategories.DODGE)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.STAMINA);
   }

   private AnimationAccessor<? extends StaticAnimation> getAnimationForWeapon(WeaponCategory weaponCategory, Style style, int direction) {
      if (this.animations.containsKey(weaponCategory)) {
         Map<Style, Map<Integer, Supplier<AnimationAccessor<? extends StaticAnimation>>>> styleMap = this.animations.get(weaponCategory);
         if (styleMap.containsKey(style)) {
            Map<Integer, Supplier<AnimationAccessor<? extends StaticAnimation>>> directionMap = styleMap.get(style);
            if (directionMap.containsKey(direction)) {
               return resolve(directionMap.get(direction));
            }
         }

         if (styleMap.containsKey(Styles.ONE_HAND)) {
            Map<Integer, Supplier<AnimationAccessor<? extends StaticAnimation>>> directionMap = styleMap.get(Styles.ONE_HAND);
            if (directionMap.containsKey(direction)) {
               return resolve(directionMap.get(direction));
            }
         }

         if (styleMap.containsKey(Styles.TWO_HAND)) {
            Map<Integer, Supplier<AnimationAccessor<? extends StaticAnimation>>> directionMap = styleMap.get(Styles.TWO_HAND);
            if (directionMap.containsKey(direction)) {
               return resolve(directionMap.get(direction));
            }
         }
      }

      return this.getDefaultAnimation(direction);
   }

   protected AnimationAccessor<? extends StaticAnimation> getDefaultAnimation(int direction) {
      return switch (direction) {
         case 0 -> resolve(this.forwardAnim);
         case 1 -> resolve(this.backwardAnim);
         case 2 -> resolve(this.leftAnim);
         case 3 -> resolve(this.rightAnim);
         case 4 -> resolve(this.upAnim);
         default -> resolve(this.forwardAnim);
      };
   }

   private static AnimationAccessor<? extends StaticAnimation> resolve(Supplier<AnimationAccessor<? extends StaticAnimation>> animation) {
      return animation == null ? null : animation.get();
   }

   @OnlyIn(Dist.CLIENT)
   public void gatherArguments(SkillContainer skillContainer, ControlEngine controlEngine, CompoundTag arguments) {
      LocalPlayerPatch executor = skillContainer.getClientExecutor();
      LocalPlayer localPlayer = (LocalPlayer)executor.getOriginal();
      float pulse = (float)executor.getOriginal().getAttributeValue(Attributes.SNEAKING_SPEED);
      Input input = localPlayer.input;
      InputUtils.sneakingTick(localPlayer, false, pulse);
      int forward = input.up ? 1 : 0;
      int backward = input.down ? -1 : 0;
      int left = input.left ? 1 : 0;
      int right = input.right ? -1 : 0;
      int vertical = forward + backward;
      int horizon = left + right;
      float yRot = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
      boolean isInAir = !((LocalPlayer)executor.getOriginal()).onGround();
      float degree;
      int animation;
      if (vertical == 0 && horizon == 0) {
         if (!isInAir) {
            animation = 4;
            degree = yRot;
         } else {
            animation = 0;
            degree = yRot;
         }
      } else if (vertical == 0) {
         if (this.getDefaultAnimation(DIRECTION_LEFT) != null && this.getDefaultAnimation(DIRECTION_RIGHT) != null) {
            animation = horizon >= 0 ? 2 : 3;
            degree = yRot;
         } else {
            animation = 0;
            degree = yRot + (horizon >= 0 ? -90.0F : 90.0F);
         }
      } else {
         animation = vertical >= 0 ? 0 : 1;
         degree = -(45 * vertical * horizon) + yRot;
      }

      arguments.putInt("animation", animation);
      arguments.putFloat("degree", degree);
      arguments.putBoolean("isInAir", isInAir);
   }

   @OnlyIn(Dist.CLIENT)
   public List<Object> getTooltipArgsOfScreen(List<Object> list) {
      list.add(net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(this.consumption));
      return list;
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
   }

   public void executeOnServer(SkillContainer skillContainer, CompoundTag args) {
      super.executeOnServer(skillContainer, args);
      ServerPlayerPatch executor = skillContainer.getServerExecutor();
      int direction = args.getInt("animation");
      float yRot = args.getFloat("degree");
      boolean isInAir = args.getBoolean("isInAir");
      CapabilityItem holdingItem = executor.getHoldingItemCapability(InteractionHand.MAIN_HAND);
      WeaponCategory weaponCategory = holdingItem.getWeaponCategory();
      Style style = holdingItem.getStyle(executor);
      AnimationAccessor<? extends StaticAnimation> animation = this.getAnimationForWeapon(weaponCategory, style, direction);
      if (animation != null) {
         executor.playAnimationSynchronized(animation, 0.0F);
         executor.setModelYRot(yRot, true);
      }
   }

   public boolean isExecutableState(PlayerPatch<?> executor) {
      EntityState playerState = executor.getEntityState();
      Level level = ((Player)executor.getOriginal()).level();
      BlockState blockState = level.getBlockState(((Player)executor.getOriginal()).getOnPos().below());
      return !executor.isInAir()
         && playerState.canUseSkill()
         && (
            !((Player)executor.getOriginal()).isInWater()
               || !blockState.isAir() && !blockState.is(Blocks.WATER) && !blockState.is(Blocks.LAVA)
         )
         && !((Player)executor.getOriginal()).onClimbable()
         && ((Player)executor.getOriginal()).getVehicle() == null;
   }

   public static class Builder extends SkillBuilder<EFNDodgeSkill.Builder> {
      protected final Map<WeaponCategory, Map<Style, Map<Integer, Supplier<AnimationAccessor<? extends StaticAnimation>>>>> animations = Maps.newHashMap();
      protected Supplier<AnimationAccessor<? extends StaticAnimation>> forwardAnim;
      protected Supplier<AnimationAccessor<? extends StaticAnimation>> backwardAnim;
      protected Supplier<AnimationAccessor<? extends StaticAnimation>> leftAnim;
      protected Supplier<AnimationAccessor<? extends StaticAnimation>> rightAnim;
      protected Supplier<AnimationAccessor<? extends StaticAnimation>> upAnim;

      public Builder(Function<EFNDodgeSkill.Builder, ? extends EFNDodgeSkill> constructor) {
         super(constructor);
      }

      private static Supplier<AnimationAccessor<? extends StaticAnimation>> lazy(
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> animation
      ) {
         return animation == null ? null : () -> animation.get();
      }

      public EFNDodgeSkill.Builder setDefaultAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim, AnimationAccessor<? extends StaticAnimation> backwardAnim
      ) {
         return this.setDefaultAnimations(() -> forwardAnim, () -> backwardAnim);
      }

      public EFNDodgeSkill.Builder setDefaultAnimations(
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> forwardAnim,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> backwardAnim
      ) {
         this.forwardAnim = lazy(forwardAnim);
         this.backwardAnim = lazy(backwardAnim);
         this.leftAnim = lazy(forwardAnim);
         this.rightAnim = lazy(forwardAnim);
         this.upAnim = lazy(forwardAnim);
         return this;
      }

      public EFNDodgeSkill.Builder setDefaultAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim
      ) {
         return this.setDefaultAnimations(() -> forwardAnim, () -> backwardAnim, () -> leftAnim, () -> rightAnim);
      }

      public EFNDodgeSkill.Builder setDefaultAnimations(
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> forwardAnim,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> backwardAnim,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> leftAnim,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> rightAnim
      ) {
         this.forwardAnim = lazy(forwardAnim);
         this.backwardAnim = lazy(backwardAnim);
         this.leftAnim = lazy(leftAnim);
         this.rightAnim = lazy(rightAnim);
         this.upAnim = lazy(forwardAnim);
         return this;
      }

      public EFNDodgeSkill.Builder setUpAnimation(AnimationAccessor<? extends StaticAnimation> upAnim) {
         return this.setUpAnimation(() -> upAnim);
      }

      public EFNDodgeSkill.Builder setUpAnimation(Supplier<? extends AnimationAccessor<? extends StaticAnimation>> upAnim) {
         this.upAnim = lazy(upAnim);
         return this;
      }

      public EFNDodgeSkill.Builder setDefaultAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         return this.setDefaultAnimations(() -> forwardAnim, () -> backwardAnim, () -> leftAnim, () -> rightAnim, () -> upAnim);
      }

      public EFNDodgeSkill.Builder setDefaultAnimations(
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> forwardAnim,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> backwardAnim,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> leftAnim,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> rightAnim,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> upAnim
      ) {
         this.forwardAnim = lazy(forwardAnim);
         this.backwardAnim = lazy(backwardAnim);
         this.leftAnim = lazy(leftAnim);
         this.rightAnim = lazy(rightAnim);
         this.upAnim = lazy(upAnim);
         return this;
      }

      public EFNDodgeSkill.Builder addAnimation(
         WeaponCategory weaponCategory, Style style, int direction, AnimationAccessor<? extends StaticAnimation> animation
      ) {
         return this.addAnimation(weaponCategory, style, direction, () -> animation);
      }

      public EFNDodgeSkill.Builder addAnimation(
         WeaponCategory weaponCategory,
         Style style,
         int direction,
         Supplier<? extends AnimationAccessor<? extends StaticAnimation>> animation
      ) {
         this.animations.computeIfAbsent(weaponCategory, k -> Maps.newHashMap());
         this.animations.get(weaponCategory).computeIfAbsent(style, k -> Maps.newHashMap());
         this.animations.get(weaponCategory).get(style).put(direction, lazy(animation));
         return this;
      }

      public EFNDodgeSkill.Builder addAnimationsForWeapon(
         WeaponCategory weaponCategory,
         Style style,
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim
      ) {
         this.addAnimation(weaponCategory, style, 0, forwardAnim);
         this.addAnimation(weaponCategory, style, 1, backwardAnim);
         this.addAnimation(weaponCategory, style, 2, forwardAnim);
         this.addAnimation(weaponCategory, style, 3, forwardAnim);
         this.addAnimation(weaponCategory, style, 4, forwardAnim);
         return this;
      }

      public EFNDodgeSkill.Builder addAnimationsForWeapon(
         WeaponCategory weaponCategory,
         Style style,
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim
      ) {
         this.addAnimation(weaponCategory, style, 0, forwardAnim);
         this.addAnimation(weaponCategory, style, 1, backwardAnim);
         this.addAnimation(weaponCategory, style, 2, leftAnim);
         this.addAnimation(weaponCategory, style, 3, rightAnim);
         this.addAnimation(weaponCategory, style, 4, forwardAnim);
         return this;
      }

      public EFNDodgeSkill.Builder addAnimationsForWeapon(
         WeaponCategory weaponCategory,
         Style style,
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         this.addAnimation(weaponCategory, style, 0, forwardAnim);
         this.addAnimation(weaponCategory, style, 1, backwardAnim);
         this.addAnimation(weaponCategory, style, 2, leftAnim);
         this.addAnimation(weaponCategory, style, 3, rightAnim);
         this.addAnimation(weaponCategory, style, 4, upAnim);
         return this;
      }

      public EFNDodgeSkill.Builder addAnimationsForWeaponAllStyles(
         WeaponCategory weaponCategory, AnimationAccessor<? extends StaticAnimation> forwardAnim, AnimationAccessor<? extends StaticAnimation> backwardAnim
      ) {
         this.addAnimationsForWeapon(weaponCategory, Styles.ONE_HAND, forwardAnim, backwardAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.TWO_HAND, forwardAnim, backwardAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.COMMON, forwardAnim, backwardAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.SHEATH, forwardAnim, backwardAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.OCHS, forwardAnim, backwardAnim);
         return this;
      }

      public EFNDodgeSkill.Builder addAnimationsForWeaponAllStyles(
         WeaponCategory weaponCategory,
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim
      ) {
         this.addAnimationsForWeapon(weaponCategory, Styles.ONE_HAND, forwardAnim, backwardAnim, leftAnim, rightAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.TWO_HAND, forwardAnim, backwardAnim, leftAnim, rightAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.COMMON, forwardAnim, backwardAnim, leftAnim, rightAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.SHEATH, forwardAnim, backwardAnim, leftAnim, rightAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.OCHS, forwardAnim, backwardAnim, leftAnim, rightAnim);
         return this;
      }

      public EFNDodgeSkill.Builder addAnimationsForWeaponAllStyles(
         WeaponCategory weaponCategory,
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         this.addAnimationsForWeapon(weaponCategory, Styles.ONE_HAND, forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.TWO_HAND, forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.COMMON, forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.SHEATH, forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
         this.addAnimationsForWeapon(weaponCategory, Styles.OCHS, forwardAnim, backwardAnim, leftAnim, rightAnim, upAnim);
         return this;
      }
   }
}
