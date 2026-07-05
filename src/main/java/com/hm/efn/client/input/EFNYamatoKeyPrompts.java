package com.hm.efn.client.input;

import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.gameasset.EFNWeaponCategories;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.hm.efn.gameasset.combos.Yamato;
import com.hm.efn.skill.arts.JudgmentCutEndSkill;
import com.mafuyu404.smartkeyprompts.util.KeyUtils;
import com.mafuyu404.smartkeyprompts.util.PlayerUtils;
import com.mafuyu404.smartkeyprompts.util.PromptUtils;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class EFNYamatoKeyPrompts {
   private static final String YAMATO_ATTACK = "key.invincible.key1";
   private static final String YAMATO_SKILL = "key.invincible.key3";
   private static final String EFN_ARTS = "key.efn.arts";
   private static final String YAMATO_DEVIL = "key.efn.demon";
   private static final String YAMATO_ANGEL = "key.efn.angel";
   private static final String YAMATO_SUMMONED_SWORD = "key.efn.summoned_sword";
   private static final String YAMATO_F = "key.forward";
   private static final String YAMATO_B = "key.back";
   private static final String YAMATO_J = "key.jump";
   private static final String SNEAK = "key.sneak";
   private static final String PROMPT_GROUP = "efn_combat";
   private static final String TRANSLATION_KEY_REPAIDSLASH = "prompt.efn.repaidslash";
   private static final String TRANSLATION_KEY_FLARECUT = "prompt.efn.flarecut";
   private static final String TRANSLATION_KEY_FLARECUT_RISE = "prompt.efn.flarecut_rise";
   private static final String TRANSLATION_KEY_DIVORCE_1 = "prompt.efn.divorce1";
   private static final String TRANSLATION_KEY_DIVORCE_2 = "prompt.efn.divorce2";
   private static final String TRANSLATION_KEY_DIVORCE_3 = "prompt.efn.divorce3";
   private static final String TRANSLATION_KEY_VOLCANOL = "prompt.efn.volcanol";
   private static final String TRANSLATION_KEY_VOLCANOL_HOLD = "prompt.efn.volcanol_hold";
   private static final String TRANSLATION_KEY_UPPERSLASH = "prompt.efn.upperslash";
   private static final String TRANSLATION_KEY_UPPERSLASH_HOLD = "prompt.efn.upperslash_hold";
   private static final String TRANSLATION_KEY_HELMBREAKER = "prompt.efn.helmbreaker";
   private static final String TRANSLATION_KEY_JUDGEMENTCUT = "prompt.efn.judgementcut";
   private static final String TRANSLATION_KEY_JUDGEMENTCUT_END = "prompt.efn.judgementcut_end";
   private static final String TRANSLATION_KEY_ANGEL = "prompt.efn.angel";
   private static final String TRANSLATION_KEY_DEVIL = "prompt.efn.devil";
   private static final String TRANSLATION_KEY_SLASHER_1 = "prompt.efn.slasher1";
   private static final String TRANSLATION_KEY_SLASHER_2 = "prompt.efn.slasher2";
   private static final String TRANSLATION_KEY_SLASHER_3 = "prompt.efn.slasher3";
   private static final String TRANSLATION_KEY_AERIALRAVE_1 = "prompt.efn.aerialrave1";
   private static final String TRANSLATION_KEY_AERIALRAVE_2 = "prompt.efn.aerialrave2";
   private static final String TRANSLATION_KEY_AERIALRAVE_3 = "prompt.efn.aerialrave3";
   private static final String TRANSLATION_KEY_ORBIT_1 = "prompt.efn.orbit1";
   private static final String TRANSLATION_KEY_ORBIT_2 = "prompt.efn.orbit2";
   private static final String TRANSLATION_KEY_KILLERBEE = "prompt.efn.killerbee";
   private static final String TRANSLATION_KEY_DRIVE = "prompt.efn.drive";
   private static final String TRANSLATION_KEY_STOMP = "prompt.efn.stomp";
   private static final String TRANSLATION_KEY_FLUSH = "prompt.efn.flush";
   private static final String TRANSLATION_KEY_CROSSCUT_3_ALT = "prompt.efn.crosscut3_alt";
   private static final String TRANSLATION_KEY_CROSSCUT_4 = "prompt.efn.crosscut4";
   private static final String TRANSLATION_KEY_CROSSCUT_5 = "prompt.efn.crosscut5";
   private static final String TRANSLATION_KEY_DOPPELGANGER = "prompt.efn.doppelganger";
   private static final String TRANSLATION_KEY_SUMMONED_SWORD = "prompt.efn.summoned_sword";
   private static final String TRANSLATION_KEY_SUMMONED_SWORD_ANGLE = "prompt.efn.summoned_sword_angle";
   private static final String TRANSLATION_KEY_SUMMONED_SWORD_DEVIL = "prompt.efn.summoned_sword_devil";

   private static boolean isForwardKeyPressed() {
      return Minecraft.getInstance().options.keyUp.isDown();
   }

   private static boolean isSkillKeyPressed() {
      return InvincibleKeyMappings.KEY3.isDown();
   }

   public static boolean isHoldingYamato(Player player) {
      return player == null
         ? false
         : Stream.of(player.getMainHandItem(), player.getOffhandItem())
            .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
            .filter(Objects::nonNull)
            .anyMatch(cap -> cap.getWeaponCategory() == EFNWeaponCategories.EFN_YAMATO);
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void tick(Post event) {
      if (true) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            LocalPlayerPatch playerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
            if (playerPatch != null && playerPatch.isEpicFightMode() && Minecraft.getInstance().screen == null) {
               boolean isAngelPressed = KeyUtils.isKeyPressedOfDesc("key.efn.angel");
               boolean isDevilPressed = KeyUtils.isKeyPressedOfDesc("key.efn.demon");
               boolean isSkillKeyPressed = isSkillKeyPressed();
               boolean isSkillKeyHeld = isSkillKeyPressed && KeyUtils.isKeyPressedOfDesc("key.invincible.key3");
               KeyUtils.enableAllKeyMapping();
               Optional<DynamicAnimation> animationOpt = Optional.ofNullable(playerPatch.getAnimator().getPlayerFor(null))
                  .map(animator -> (DynamicAnimation)animator.getAnimation().get());
               SkillContainer yamatoSkill = playerPatch.getSkill(Yamato.yamato);
               if (yamatoSkill != null) {
                  if (isHoldingYamato(player)) {
                     if (animationOpt.isPresent() && PlayerUtils.isPlayerOnGround()) {
                        DynamicAnimation animation = animationOpt.get();
                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (isDevilPressed && elapsedTime < 0.85F) {
                              PromptUtils.addDesc("prompt.efn.divorce2")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key1"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO2)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (isDevilPressed && elapsedTime < 0.85F) {
                              PromptUtils.addDesc("prompt.efn.divorce3")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key1"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_NORMAL_AUTO1)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (!isAngelPressed && !isDevilPressed && elapsedTime < 0.55F) {
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.slasher2");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_NORMAL_AUTO2)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (!isAngelPressed && !isDevilPressed) {
                              if (elapsedTime < 0.45F) {
                                 PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.slasher3");
                              }

                              if (0.45F < elapsedTime && elapsedTime < 0.75F) {
                                 PromptUtils.addDesc("prompt.efn.crosscut3_alt")
                                    .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key1"))
                                    .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                    .withCustom(true)
                                    .toGroup("efn_combat");
                              }
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_EXTEND_AUTO3)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (!isAngelPressed && !isDevilPressed && elapsedTime < 0.85F) {
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.crosscut4");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_EXTEND_AUTO4)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (!isAngelPressed && !isDevilPressed && elapsedTime < 1.0F) {
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.crosscut5");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_REPAIDSLASH)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           PromptUtils.addDesc("prompt.efn.judgementcut")
                              .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.invincible.key1"))
                              .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                              .withCustom(true)
                              .toGroup("efn_combat");
                           if (elapsedTime < 0.4F) {
                              PromptUtils.addDesc("prompt.efn.divorce1")
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.efn.demon"))
                                 .forKey(KeyUtils.getKeyByDesc("key.efn.demon"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.flarecut")
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.forward"))
                                 .forKey(KeyUtils.getKeyByDesc("key.forward"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.volcanol")
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.back"))
                                 .forKey(KeyUtils.getKeyByDesc("key.back"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.upperslash")
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.invincible.key3"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key3"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.flarecut_rise")
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.jump"))
                                 .forKey(KeyUtils.getKeyByDesc("key.jump"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_VOLCANOL)) {
                           PromptUtils.addDesc("prompt.efn.volcanol_hold")
                              .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.back"))
                              .forKey(KeyUtils.getKeyByDesc("key.back"))
                              .withCustom(true)
                              .toGroup("efn_combat");
                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_FLARECUT)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (elapsedTime < 0.3F) {
                              PromptUtils.addDesc("prompt.efn.flarecut_rise")
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.invincible.key3"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key3"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                           }

                           return;
                        }
                     }

                     if (animationOpt.isPresent() && PlayerUtils.isPlayerInAir()) {
                        DynamicAnimation animation = animationOpt.get();
                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO1)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (!isAngelPressed && !isDevilPressed && elapsedTime < 0.5F) {
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.aerialrave2");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO2)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (!isAngelPressed && !isDevilPressed && elapsedTime < 0.5F) {
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.aerialrave3");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AIRFLUSH)) {
                           if (isAngelPressed && !isDevilPressed) {
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.aerialrave2");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_ORBIT_1)) {
                           float elapsedTime = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
                           if (isAngelPressed && elapsedTime < 0.6F) {
                              KeyUtils.enableKeyMapping("key.invincible.key1");
                              PromptUtils.addDesc("prompt.efn.orbit2")
                                 .withKeyAlias(
                                    KeyUtils.getKeyDisplayName("key.efn.angel")
                                       + " "
                                       + EFNYamatoKeyPrompts.PromptText.and()
                                       + " "
                                       + EFNYamatoKeyPrompts.PromptText.hold()
                                       + " "
                                       + KeyUtils.getKeyDisplayName("key.invincible.key1")
                                 )
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                           }

                           return;
                        }

                        if (animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DRIVE)) {
                           if (isDevilPressed) {
                              KeyUtils.enableKeyMapping("key.invincible.key3");
                              PromptUtils.addDesc("prompt.efn.stomp")
                                 .withKeyAlias(
                                    KeyUtils.getKeyDisplayName("key.efn.demon")
                                       + " "
                                       + EFNYamatoKeyPrompts.PromptText.and()
                                       + " "
                                       + EFNYamatoKeyPrompts.PromptText.hold()
                                       + " "
                                       + KeyUtils.getKeyDisplayName("key.invincible.key3")
                                 )
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key3"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                           }

                           return;
                        }
                     }

                     if (playerPatch.isEpicFightMode()) {
                        if (PlayerUtils.isPlayerOnGround()) {
                           if (!isAngelPressed && !isDevilPressed) {
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.slasher1");
                              PromptUtils.addDesc("prompt.efn.judgementcut")
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.hold() + " " + KeyUtils.getKeyDisplayName("key.invincible.key1"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.summoned_sword")
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.press() + " " + KeyUtils.getKeyDisplayName("key.efn.summoned_sword"))
                                 .forKey(KeyUtils.getKeyByDesc("key.efn.summoned_sword"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              if (isForwardKeyPressed()) {
                                 PromptUtils.addDesc("prompt.efn.upperslash_hold")
                                    .withKeyAlias(
                                       EFNYamatoKeyPrompts.PromptText.forward()
                                          + " "
                                          + EFNYamatoKeyPrompts.PromptText.and()
                                          + " "
                                          + KeyUtils.getKeyDisplayName("key.invincible.key3")
                                    )
                                    .forKey(KeyUtils.getKeyByDesc("key.invincible.key3"))
                                    .withCustom(true)
                                    .toGroup("efn_combat");
                              } else {
                                 PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key3"), "prompt.efn.upperslash");
                              }

                              if (playerPatch.getSkill(EFNSkills.JUDGEMENTCUTEND) != null
                                 && playerPatch.getSkill(EFNSkills.JUDGEMENTCUTEND).hasSkill()
                                 && JudgmentCutEndSkill.isYamatoUnlocked((Player)playerPatch.getOriginal())) {
                                 PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.arts"), "prompt.efn.judgementcut_end");
                              }

                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.angel"), "prompt.efn.angel");
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.demon"), "prompt.efn.devil");
                           }

                           if (isAngelPressed) {
                              String flarecutDesc = isSkillKeyHeld ? "prompt.efn.flarecut_rise" : "prompt.efn.flarecut";
                              PromptUtils.addDesc(flarecutDesc)
                                 .withKeyAlias(EFNYamatoKeyPrompts.PromptText.press_hold() + " " + KeyUtils.getKeyDisplayName("key.invincible.key3"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key3"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.repaidslash")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key1"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.summoned_sword_angle")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.efn.summoned_sword"))
                                 .forKey(KeyUtils.getKeyByDesc("key.efn.summoned_sword"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.angel"), "prompt.efn.angel");
                           }

                           if (isDevilPressed) {
                              String volcanolDesc = isSkillKeyHeld ? "prompt.efn.volcanol_hold" : "prompt.efn.volcanol";
                              String volcanolAlias = isSkillKeyHeld ? EFNYamatoKeyPrompts.PromptText.hold() : EFNYamatoKeyPrompts.PromptText.press();
                              PromptUtils.addDesc(volcanolDesc)
                                 .withKeyAlias(volcanolAlias + " " + KeyUtils.getKeyDisplayName("key.invincible.key3"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key3"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.divorce1")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key1"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.summoned_sword_devil")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.efn.summoned_sword"))
                                 .forKey(KeyUtils.getKeyByDesc("key.efn.summoned_sword"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.demon"), "prompt.efn.devil");
                           }
                        } else {
                           if (!isAngelPressed && !isDevilPressed) {
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key1"), "prompt.efn.aerialrave1");
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.invincible.key3"), "prompt.efn.helmbreaker");
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.angel"), "prompt.efn.angel");
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.demon"), "prompt.efn.devil");
                           }

                           if (isAngelPressed) {
                              PromptUtils.addDesc("prompt.efn.orbit1")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key1"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.flush")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key3"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key3"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.angel"), "prompt.efn.angel");
                           }

                           if (isDevilPressed) {
                              PromptUtils.addDesc("prompt.efn.killerbee")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key1"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key1"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.addDesc("prompt.efn.drive")
                                 .withKeyAlias(KeyUtils.getKeyDisplayName("key.invincible.key3"))
                                 .forKey(KeyUtils.getKeyByDesc("key.invincible.key3"))
                                 .withCustom(true)
                                 .toGroup("efn_combat");
                              PromptUtils.custom("efn_combat", KeyUtils.getKeyByDesc("key.efn.demon"), "prompt.efn.devil");
                           }
                        }
                     }

                     PromptUtils.show("efn", "key.efn.doppelganger");
                     if ((Boolean)yamatoSkill.getDataManager().getDataValue(EFNSKillDataKeys.HAVE_DOPPELGANGER)) {
                        PromptUtils.addDesc("prompt.efn.doppelganger")
                           .withKeyAlias(
                              EFNYamatoKeyPrompts.PromptText.press()
                                 + KeyUtils.getKeyDisplayName("key.sneak")
                                 + " "
                                 + EFNYamatoKeyPrompts.PromptText.and()
                                 + " "
                                 + KeyUtils.getKeyDisplayName("key.efn.doppelganger")
                           )
                           .forKey(KeyUtils.getKeyByDesc("key.efn.doppelganger"))
                           .withCustom(true)
                           .toGroup("efn");
                        PromptUtils.show("efn", "key.efn.doppelganger_delay");
                     }
                  }
               }
            }
         }
      }
   }

   public static class PromptText {
      public static String hold() {
         return isChinese() ? "长按" : "Hold";
      }

      public static String press() {
         return isChinese() ? "点按" : "Press";
      }

      public static String forward() {
         return isChinese() ? "前进" : "Forward";
      }

      public static String press_hold() {
         return isChinese() ? "点按 或 长按" : "Press Or Hold";
      }

      public static String and() {
         return "+";
      }

      private static boolean isChinese() {
         return Minecraft.getInstance().getLanguageManager().getSelected().equals("zh_cn") || Minecraft.getInstance().getLanguageManager().getSelected().equals("lzh");
      }

      public static class CN {
         public static final String HOLD = "长按";
         public static final String PRESS = "点按";
         public static final String PRESS_HOLD = "点按 或 长按";
         public static final String FORWARD = "前进";
         public static final String AND = "+";
      }

      public static class EN {
         public static final String HOLD = "Hold";
         public static final String PRESS = "Press";
         public static final String FORWARD = "Forward";
         public static final String PRESS_HOLD = "Press Or Hold";
         public static final String AND = "+";
      }
   }
}
