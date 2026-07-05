package com.guhao.vix.camera;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guhao.vix.util.JsonUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.Vec3;

public class CameraAnimation {
   public final FloatSheet x;
   public final FloatSheet y;
   public final FloatSheet z;
   public final FloatSheet rx;
   public final FloatSheet ry;
   public final FloatSheet rz;
   public final FloatSheet fov;
   public final float totalTime;

   public CameraAnimation(FloatSheet x, FloatSheet y, FloatSheet z, FloatSheet rx, FloatSheet ry, FloatSheet rz, FloatSheet fov) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.rx = rx;
      this.ry = ry;
      this.rz = rz;
      this.fov = fov;

      float maxTime = Math.max(x.getMaxTime(), y.getMaxTime());
      maxTime = Math.max(z.getMaxTime(), maxTime);
      maxTime = Math.max(rx.getMaxTime(), maxTime);
      maxTime = Math.max(ry.getMaxTime(), maxTime);
      maxTime = Math.max(fov.getMaxTime(), maxTime);
      this.totalTime = maxTime;
   }

   public static CameraAnimation load(ResourceLocation location) {
      try {
         Resource resource = getResource(location);

         try (InputStream inputStream = resource.open()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            float timeScale = json.has("time_scale") ? json.get("time_scale").getAsFloat() : 1.0F;

            JsonObject pos = json.getAsJsonObject("pos");
            FloatSheet x = new FloatSheet();
            FloatSheet y = new FloatSheet();
            FloatSheet z = new FloatSheet();
            x.getFromJson(pos, "x");
            y.getFromJson(pos, "y");
            z.getFromJson(pos, "z");

            JsonObject rot = json.getAsJsonObject("rot");
            FloatSheet rx = new FloatSheet();
            FloatSheet ry = new FloatSheet();
            FloatSheet rz = new FloatSheet();
            rx.getFromJson(rot, "rx");
            ry.getFromJson(rot, "ry");
            rz.getFromJson(rot, "rz");

            JsonObject fovObject = json.getAsJsonObject("fov");
            FloatSheet fov = new FloatSheet();
            fov.getFromJson(fovObject, "value");

            x.scaleTimes(timeScale);
            y.scaleTimes(timeScale);
            z.scaleTimes(timeScale);
            rx.scaleTimes(timeScale);
            ry.scaleTimes(timeScale);
            rz.scaleTimes(timeScale);
            fov.scaleTimes(timeScale);

            return new CameraAnimation(x, y, z, rx, ry, rz, fov);
         }
      } catch (IOException | ReflectiveOperationException e) {
         throw new RuntimeException(e);
      }
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

   public Pose getPose(float time) {
      return new Pose(
         this.x.getValueByTime(time),
         this.y.getValueByTime(time),
         this.z.getValueByTime(time),
         this.rx.getValueByTime(time),
         this.ry.getValueByTime(time),
         this.rz.getValueByTime(time),
         this.fov.getValueByTime(time)
      );
   }

   public abstract static class TimeSheet {
      public float[] timeSheet;

      public int getIndexByTime(float time) {
         if (time <= 0.0F) {
            return 0;
         }

         for (int i = 0; i < this.timeSheet.length; i++) {
            if (this.timeSheet[i] >= time) {
               return Math.max(i - 1, 0);
            }
         }

         return this.timeSheet.length - 1;
      }

      public void getFromJson(JsonObject json) {
         this.timeSheet = JsonUtils.getAsFloatArray(json.getAsJsonArray("time"));
      }

      public float getMaxTime() {
         return this.timeSheet[this.timeSheet.length - 1];
      }

      public void scaleTimes(float scale) {
         for (int i = 0; i < this.timeSheet.length; i++) {
            this.timeSheet[i] /= scale;
         }
      }
   }

   public static class FloatSheet extends TimeSheet {
      public float[] floatSheet;

      public float getValueByTime(float time) {
         int index = this.getIndexByTime(time);
         if (index == this.timeSheet.length - 1) {
            return this.floatSheet[index];
         }

         float timeDelta = this.timeSheet[index + 1] - this.timeSheet[index];
         if (timeDelta > 1.0E-5F) {
            float factor = (time - this.timeSheet[index]) / timeDelta;
            return this.floatSheet[index] * (1.0F - factor) + this.floatSheet[index + 1] * factor;
         }

         return this.floatSheet[index];
      }

      public void getFromJson(JsonObject json, String name) {
         this.getFromJson(json);
         this.floatSheet = JsonUtils.getAsFloatArray(json.getAsJsonArray(name));
      }
   }

   public static class Pose {
      public final Vec3 pos;
      public final float rotY;
      public final float rotX;
      public final float rotZ;
      public final float fov;

      public Pose(Vec3 pos, float rotX, float rotY, float rotZ, float fov) {
         this.pos = pos;
         this.rotY = rotY;
         this.rotX = rotX;
         this.rotZ = rotZ;
         this.fov = fov;
      }

      public Pose(float x, float y, float z, float rotX, float rotY, float rotZ, float fov) {
         this(new Vec3(x, y, z), rotX, rotY, rotZ, fov);
      }

      @Override
      public String toString() {
         return "Pose{pos=" + this.pos + ", rotY=" + this.rotY + ", rotX=" + this.rotX + ", rotZ=" + this.rotZ + ", fov=" + this.fov + "}";
      }
   }
}
