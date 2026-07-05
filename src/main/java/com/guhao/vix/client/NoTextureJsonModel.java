package com.guhao.vix.client;

import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class NoTextureJsonModel {
   static float u0 = 0.0F;
   static float u1 = 1.0F;
   static float v0 = 0.0F;
   static float v1 = 1.0F;

   public List<vec3f> Positions = new ArrayList<>();
   public List<Triangle> Face = new ArrayList<>();

   private final Vector3f tmpV1 = new Vector3f();
   private final Vector3f tmpV2 = new Vector3f();
   private final Vector3f tmpV3 = new Vector3f();

   public static NoTextureJsonModel loadFromJson(ResourceLocation location) {
      try {
         Resource resource = getResource(location);

         try (InputStreamReader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
            NoTextureJsonModel model = new Gson().fromJson(reader, NoTextureJsonModel.class);
            for (Triangle triangle : model.Face) {
               triangle.UpdateNormal(model.Positions);
            }

            return model;
         }
      } catch (IOException | ReflectiveOperationException e) {
         throw new RuntimeException(e);
      }
   }

   public void render(VertexConsumer consumer, Quaternionf rotation, Vector3f translation, float scale, Color color, int light) {
      this.renderImpl(consumer, rotation, translation, scale, color, light, false);
   }

   public void renderNeg(VertexConsumer consumer, Quaternionf rotation, Vector3f translation, float scale, Color color, int light) {
      this.renderImpl(consumer, rotation, translation, scale, color, light, true);
   }

   private void renderImpl(VertexConsumer consumer, Quaternionf rotation, Vector3f translation, float scale, Color color, int light, boolean reverse) {
      int red = color.getRed();
      int green = color.getGreen();
      int blue = color.getBlue();
      int alpha = color.getAlpha();

      for (Triangle triangle : this.Face) {
         vec3f vertex1 = this.Positions.get(triangle.x - 1);
         vec3f vertex2 = this.Positions.get(triangle.y - 1);
         vec3f vertex3 = this.Positions.get(triangle.z - 1);

         this.tmpV1.set(vertex1.x, vertex1.y, vertex1.z);
         this.tmpV2.set(vertex2.x, vertex2.y, vertex2.z);
         this.tmpV3.set(vertex3.x, vertex3.y, vertex3.z);

         if (rotation != null) {
            this.tmpV1.rotate(rotation);
            this.tmpV2.rotate(rotation);
            this.tmpV3.rotate(rotation);
         }

         this.tmpV1.mul(scale).add(translation);
         this.tmpV2.mul(scale).add(translation);
         this.tmpV3.mul(scale).add(translation);

         if (reverse) {
            emitVertex(consumer, this.tmpV3, red, green, blue, alpha, u0, v1, light);
            emitVertex(consumer, this.tmpV2, red, green, blue, alpha, u0, v0, light);
            emitVertex(consumer, this.tmpV1, red, green, blue, alpha, u1, v0, light);
         } else {
            emitVertex(consumer, this.tmpV1, red, green, blue, alpha, u0, v1, light);
            emitVertex(consumer, this.tmpV2, red, green, blue, alpha, u0, v0, light);
            emitVertex(consumer, this.tmpV3, red, green, blue, alpha, u1, v0, light);
         }
      }
   }

   private static void emitVertex(VertexConsumer consumer, Vector3f vertex, int red, int green, int blue, int alpha, float u, float v, int light) {
      consumer.addVertex(vertex.x(), vertex.y(), vertex.z()).setColor(red, green, blue, alpha).setUv(u, v).setLight(light);
   }

   @SuppressWarnings("unchecked")
   private static Resource getResource(ResourceLocation location) throws ReflectiveOperationException {
      Object minecraft = invoke(Minecraft.class.getMethod("getInstance"), null);
      Object resourceManager = invoke(Minecraft.class.getMethod("getResourceManager"), minecraft);
      Optional<Resource> resource = (Optional<Resource>)invoke(
         resourceManager.getClass().getMethod("getResource", ResourceLocation.class),
         resourceManager,
         location
      );
      return resource.orElseThrow();
   }

   private static Object invoke(java.lang.reflect.Method method, Object target, Object... args) throws ReflectiveOperationException {
      try {
         return method.invoke(target, args);
      } catch (InvocationTargetException e) {
         Throwable cause = e.getCause();
         if (cause instanceof RuntimeException runtimeException) {
            throw runtimeException;
         }

         if (cause instanceof Error error) {
            throw error;
         }

         throw e;
      }
   }

   public static class Triangle {
      public int x;
      public int y;
      public int z;
      public vec3f Normal;

      public void UpdateNormal(List<vec3f> positions) {
         Vector3f p1 = positions.get(this.x - 1).toBugJumpFormat();
         Vector3f p2 = positions.get(this.y - 1).toBugJumpFormat();
         Vector3f p3 = positions.get(this.z - 1).toBugJumpFormat();
         p1.sub(p2).normalize();
         p2.sub(p3).normalize();
         p1.cross(p2).normalize();
         this.Normal = new vec3f(p1.x(), p1.y(), p1.z());
      }
   }

   public static class vec3f {
      public float x;
      public float y;
      public float z;

      public vec3f(float x, float y, float z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public Vector3f toBugJumpFormat() {
         return new Vector3f(this.x, this.y, this.z);
      }
   }
}
