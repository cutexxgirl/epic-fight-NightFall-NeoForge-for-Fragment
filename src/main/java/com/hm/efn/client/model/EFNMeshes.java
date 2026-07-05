package com.hm.efn.client.model;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;

public class EFNMeshes implements PreparableReloadListener {
   public static final MeshAccessor<SkinnedMesh> YAMATO_SPHERE = MeshAccessor.create(
      "efn", "particle/yamato_sphere", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new)
   );
   public static final MeshAccessor<SkinnedMesh> YAMATO_FLOOR = MeshAccessor.create(
      "efn", "particle/yamato_floor", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new)
   );
   public static final MeshAccessor<SkinnedMesh> BLOCK = MeshAccessor.create(
      "efn", "particle/block", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new)
   );

   @NotNull
   public CompletableFuture<Void> reload(
      PreparationBarrier stage,
      ResourceManager resourceManager,
      ProfilerFiller preparationsProfiler,
      ProfilerFiller reloadProfiler,
      Executor backgroundExecutor,
      Executor gameExecutor
   ) {
      return CompletableFuture.runAsync(() -> Meshes.reload(resourceManager), gameExecutor).thenCompose(stage::wait);
   }
}
