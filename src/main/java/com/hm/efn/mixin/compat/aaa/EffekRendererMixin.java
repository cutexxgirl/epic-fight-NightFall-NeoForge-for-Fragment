package com.hm.efn.mixin.compat.aaa;

import com.guhao.vix.client.pipeline.PostEffectPipelines.Pipeline;
import com.hm.efn.client.effek.gaaa.RenderState;
import com.hm.efn.client.render.custom.BloomParticleRenderType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import mod.chloeprime.aaaparticles.api.client.effekseer.ParticleEmitter.Type;
import mod.chloeprime.aaaparticles.client.render.EffekRenderer;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EffekRenderer.class, remap = false)
public class EffekRendererMixin {
   @Unique
   private static final Deque<RenderState> epicFight_Nightfall$stateStack = new ArrayDeque<>();
   @Unique
   private static final Map<Pipeline, Predicate<ResourceLocation>> PIPELINE_RULES = new LinkedHashMap<>();

   @Inject(
      method = "lambda$draw$2",
      remap = false,
      at = @At(
         value = "INVOKE",
         target = "Lmod/chloeprime/aaaparticles/api/client/EffectDefinition;draw(Lmod/chloeprime/aaaparticles/api/client/effekseer/ParticleEmitter$Type;Lorg/joml/Vector3f;Lorg/joml/Vector3f;II[F[FFFLcom/mojang/blaze3d/pipeline/RenderTarget;)V"
      )
   )
   private static void efn$onEachParticleDraw(Type type, Camera camera, int w, int h, float realDelta, float partialTick, CallbackInfo ci) {
      if (type == Type.WORLD) {
         RenderState currentState = RenderState.capture();
         epicFight_Nightfall$stateStack.push(currentState);
      }
   }

   @Inject(
      method = "lambda$draw$2",
      remap = false,
      at = @At(
         value = "INVOKE",
         shift = Shift.AFTER,
         target = "Lmod/chloeprime/aaaparticles/api/client/EffectDefinition;draw(Lmod/chloeprime/aaaparticles/api/client/effekseer/ParticleEmitter$Type;Lorg/joml/Vector3f;Lorg/joml/Vector3f;II[F[FFFLcom/mojang/blaze3d/pipeline/RenderTarget;)V"
      )
   )
   private static void efn$afterEachParticleDraw(Type type, Camera camera, int w, int h, float realDelta, float partialTick, CallbackInfo ci) {
      if (!epicFight_Nightfall$stateStack.isEmpty()) {
         RenderState previousState = epicFight_Nightfall$stateStack.pop();
         previousState.restore();
      }
   }

   static {
      PIPELINE_RULES.put(BloomParticleRenderType.ppl, id -> {
         String path = id.getPath().toLowerCase();
         return path.contains("disorder") || path.contains("spark") || path.contains("fireworks") || path.contains("background");
      });
   }
}
