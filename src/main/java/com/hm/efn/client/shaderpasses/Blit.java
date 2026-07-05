package com.hm.efn.client.shaderpasses;

import com.guhao.vix.client.shaderpasses.PostPassBase;
import java.io.IOException;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.packs.resources.ResourceManager;

public class Blit extends PostPassBase {
   public Blit(ResourceManager rsmgr) throws IOException {
      super(new EffectInstance(rsmgr, "efn:blit"));
   }
}
