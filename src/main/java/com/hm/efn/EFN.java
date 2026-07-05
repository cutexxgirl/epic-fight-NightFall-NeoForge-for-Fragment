package com.hm.efn;

import com.hm.efn.client.input.EFNYamatoKeyPrompts;
import com.hm.efn.client.model.ACGModel;
import com.hm.efn.client.render.screens.devil.DevilRenderEvent;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.compat.EpicSkillsCompat;
import com.hm.efn.capability.OriginalSkillCapability;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.event.TickChange;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.EFNConditions;
import com.hm.efn.gameasset.EFNEnchantment;
import com.hm.efn.gameasset.EFNEpicFightEvents;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkillBootstrap;
import com.hm.efn.gameasset.EFNSkillCategories;
import com.hm.efn.gameasset.EFNSkillSlots;
import com.hm.efn.gameasset.EFNWeaponsGuardSkillCompat;
import com.hm.efn.network.EFNNetwork;
import com.hm.efn.particle.EFNParticles;
import com.hm.efn.registries.EFNItem;
import com.hm.efn.registries.EFNLootModifier;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.registries.PostPasses;
import com.hm.efn.util.ModDetectionUtil;
import com.hm.efn.world.advancements.EFNAchievements;
import com.merlin204.avalon.main.AvalonMOD;
import java.util.Collection;
import java.util.Iterator;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.client.renderer.shader.compute.loader.ComputeShaderProvider;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.main.EpicFightExtensions;
import yesman.epicfight.main.EpicFightSharedConstants;

@Mod("efn")
public class EFN {
   public static final String MODID = "efn";
   public static final Logger LOGGER = LogManager.getLogger("efn");
   private static final Collection<SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
   private final ModContainer modContainer;

   public EFN(IEventBus modEventBus, ModContainer modContainer) {
      this.modContainer = modContainer;
      IEventBus forgeEventBus = NeoForge.EVENT_BUS;
      forgeEventBus.register(this);
      AnimationManager.addNoWarningModId(MODID);
      AnimationManager.addNoWarningModId("epic_fight_avalon");
      this.initializeModules(modEventBus, forgeEventBus);
      this.setupConfigAndNetwork();
      AvalonMOD.beMerlin = false;
   }

   public static void queueServerWork(int tick, Runnable action) {
      workQueue.add(new SimpleEntry<>(action, tick));
   }

   private void initializeModules(IEventBus modEventBus, IEventBus forgeEventBus) {
      EFNAchievements.init();
      modEventBus.addListener(this::commonSetup);
      modEventBus.addListener(this::onClientSetup);
      this.registerRegistries(modEventBus);
      this.modContainer.registerExtensionPoint(EpicFightExtensions.class, (Supplier<EpicFightExtensions>)() -> new EpicFightExtensions(EFNItem.ITEM_TAB_MAIN));
      EFNEpicFightEvents.register();
      this.setupSkillSystem();
      modEventBus.addListener(EFNNetwork::registerPackets);
      this.setupTickSystem();
   }

   private void registerRegistries(IEventBus modEventBus) {
      EFNItem.register(modEventBus);
      EFNItem.EFN_ITEM_TAB.register(modEventBus);
      EFNItem.EFN_ITEM_TAB_EXTRA.register(modEventBus);
      EFNEntity.ENTITIES.register(modEventBus);
      EFNSounds.EFNSound.register(modEventBus);
      EFNParticles.PARTICLES.register(modEventBus);
      EFNMobEffectRegistry.EFFECTS.register(modEventBus);
      OriginalSkillCapability.ATTACHMENT_TYPES.register(modEventBus);
      EFNLootModifier.LOOT_MODIFIERS.register(modEventBus);
      EFNConditions.CONDITIONS.register(modEventBus);
      EFNSKillDataKeys.DATA_KEYS.register(modEventBus);
      EFNSkillBootstrap.register(modEventBus);
      EFNEnchantment.ENCHANTMENTS.register(modEventBus);
      if (FMLEnvironment.dist == Dist.CLIENT) {
         modEventBus.addListener(PostPasses::register);
      }
   }

   private void setupSkillSystem() {
      EFNSkillSlots.ENUM_MANAGER.registerEnumCls("efn", EFNSkillSlots.class);
      EFNSkillCategories.ENUM_MANAGER.registerEnumCls("efn", EFNSkillCategories.class);
      if (EpicFightSharedConstants.isPhysicalClient() && ModList.get().isLoaded("epicskills")) {
         EpicSkillsCompat.registerCategorySlotTexture();
      }
   }

   private void setupConfigAndNetwork() {
      this.modContainer.registerConfig(Type.COMMON, EFNCommonConfig.SPEC, "efn-common.toml");
      this.modContainer.registerConfig(Type.CLIENT, EFNClientConfig.SPEC, "efn-client.toml");
   }

   private void setupTickSystem() {
      if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
         TickChange.init();
      } else if (FMLEnvironment.dist == Dist.CLIENT) {
         if (Minecraft.getInstance() != null) {
            TickChange.init();
         }
      }
   }

   public static void sendToPlayer(CustomPacketPayload msg, ServerPlayer player) {
      PacketDistributor.sendToPlayer(player, msg);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {});
   }

   private void onClientSetup(FMLClientSetupEvent event) {
      EFNClientConfig.initWarningSystem();
      LOGGER.info("Warning system initialized - AAA Particles warning enabled: {}", EFNClientConfig.isAAAWarningEnabled());
      LOGGER.info("AAA Particles installed: {}", ModDetectionUtil.isTargetModInstalled());
      EFNAnimations.LoadCamAnims();
      ACGModel.LoadOtherModel();
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, DevilRenderEvent::onRenderDevilBar);
      event.enqueueWork(() -> {
         if (ComputeShaderProvider.supportComputeShader()) {
            ClientConfig.ACTIVATE_COMPUTE_SHADER.set(true);
         }

         if (ModList.get().isLoaded("smartkeyprompts")) {
            NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, EFNYamatoKeyPrompts::tick);
         }
      });
   }

   @SubscribeEvent
   public void tick(Post event) {
      if (true) {
         this.processWorkQueue();
      }
   }

   private void processWorkQueue() {
      Iterator<SimpleEntry<Runnable, Integer>> iterator = workQueue.iterator();

      while (iterator.hasNext()) {
         SimpleEntry<Runnable, Integer> work = iterator.next();
         int remaining = work.getValue() - 1;
         work.setValue(remaining);
         if (remaining == 0) {
            iterator.remove();
            work.getKey().run();
         }
      }
   }
}
