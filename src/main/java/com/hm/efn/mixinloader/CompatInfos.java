package com.hm.efn.mixinloader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import net.neoforged.fml.loading.FMLLoader;

public class CompatInfos {
   public static final HashMap<CompatInfos.MixinClassName, CompatInfos.CompatMixinInfo> CompatMixins = Maps.newHashMap();
   static final List<CompatInfos.AbstractCompatMod> CompatMods = Lists.newArrayList();
   public static CompatInfos.AbstractCompatMod CATACLYSM_MOD;
   public static CompatInfos.AbstractCompatMod LIONFISHAPI;
   public static CompatInfos.AbstractCompatMod CITADEL;
   public static CompatInfos.AbstractCompatMod GECKOLIB;
   public static CompatInfos.AbstractCompatMod WOM;
   public static CompatInfos.AbstractCompatMod AAA;

   static void register() {
      WOM = new CompatInfos.CompatMod("wom", m -> new CompatInfos.CompatMixinInfo(m, "BasicMultipleAttackAnimationMixin") {});
      LIONFISHAPI = new CompatInfos.CompatMod("lionfishapi", m -> new CompatInfos.CompatMixinInfo(m, "AnimationHandlerMixin") {});
      CITADEL = new CompatInfos.CompatMod("citadel", m -> new CompatInfos.CompatMixinInfo(m, "AnimationHandlerMixin") {});
      GECKOLIB = new CompatInfos.CompatMod("geckolib", m -> new CompatInfos.CompatMixinInfo(m, "AnimationProcessorMixin") {});
      CATACLYSM_MOD = new CompatInfos.CompatMod(
         "cataclysm", m -> new CompatInfos.CompatMixinInfo(m, "MixinLLibraryBossMonster"), m -> new CompatInfos.CompatMixinInfo(m, "MixinIABossMonster") {}
      );
      AAA = new CompatInfos.CompatMod("aaa_particles", m -> new CompatInfos.CompatMixinInfo(m, "EffekRendererMixin") {});
   }

   public static void initCompatInfo() {
      register();
      CompatMods.forEach(CompatInfos.AbstractCompatMod::check);
   }

   static String getClassName(String classPath) {
      String[] s = classPath.split("\\.");
      return s[s.length - 1];
   }

   public static boolean shouldMixin(String targetClassName, String mixinClassName_) {
      String mixinClassName = getClassName(mixinClassName_);
      if (CompatMixins.containsKey(CompatInfos.MixinClassName.of(mixinClassName))) {
         boolean should = CompatMixins.get(CompatInfos.MixinClassName.of(mixinClassName)).shouldApplyMixin();
         if (should) {
            System.out.println("[EFN Mixin Loader]Apply Compat Mixin: " + mixinClassName_ + ".class -> " + targetClassName + ".class");
         } else {
            System.out.println("[EFN Mixin Loader]Skip Mixin: " + mixinClassName_ + ".class -> " + targetClassName + ".class");
         }

         return should;
      } else {
         System.out.println("[EFN Mixin Loader]Apply Default Mixin: " + mixinClassName_ + ".class -> " + targetClassName + ".class");
         return true;
      }
   }

   public abstract static class AbstractCompatMod {
      protected boolean loaded;

      public AbstractCompatMod() {
         CompatInfos.CompatMods.add(this);
      }

      public abstract void check();

      public boolean isLoaded() {
         return this.loaded;
      }
   }

   public static class CompatMixinInfo {
      protected final CompatInfos.AbstractCompatMod mod;

      public CompatMixinInfo(CompatInfos.AbstractCompatMod mod, String mixinClass) {
         this.mod = mod;
         CompatInfos.CompatMixins.put(CompatInfos.MixinClassName.of(mixinClass), this);
      }

      public boolean shouldApplyMixin() {
         return this.mod.isLoaded();
      }
   }

   public static class CompatMod extends CompatInfos.AbstractCompatMod {
      final String modid;

      public CompatMod(String modid, String... mixinClasses) {
         this.modid = modid;

         for (int i = 0; i < mixinClasses.length; i++) {
            new CompatInfos.CompatMixinInfo(this, mixinClasses[i]);
         }
      }

      @SafeVarargs
      public CompatMod(String modid, Function<CompatInfos.AbstractCompatMod, CompatInfos.CompatMixinInfo>... mixinClasses) {
         this.modid = modid;

         for (Function<CompatInfos.AbstractCompatMod, CompatInfos.CompatMixinInfo> mixinClass : mixinClasses) {
            mixinClass.apply(this);
         }
      }

      @Override
      public void check() {
         this.loaded = FMLLoader.getLoadingModList().getModFileById(this.modid) != null;
      }

      @Override
      public boolean isLoaded() {
         return this.loaded;
      }
   }

   public record MixinClassName(String className) {
      public static CompatInfos.MixinClassName of(String n) {
         return new CompatInfos.MixinClassName(n);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return o instanceof CompatInfos.MixinClassName that ? this.className.equals(that.className) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.className.hashCode();
      }
   }
}
