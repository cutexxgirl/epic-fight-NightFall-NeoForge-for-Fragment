package com.guhao.vix.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class MathUtils {
   public static final Double PI2 = Math.PI * 2.0;
   public static final Quaternionf Quat_One = new Quaternionf();
   public static final float RADIANS_TO_DEGREES = (float)(Math.PI / 180.0);

   public static Vec3 ToCylindricalCoordinate(Vec3 vector) {
      double theta = Math.atan2(-vector.z(), vector.x());
      double radius = Math.sqrt(vector.x() * vector.x() + vector.z() * vector.z());
      return new Vec3(radius, theta, vector.y());
   }

   public static Vec3 LerpMinCylindrical(Vec3 from, Vec3 to, float pct) {
      double thetaDelta = (to.y() - from.y()) % PI2;
      if (thetaDelta > 0.0) {
         thetaDelta -= PI2;
         if (Math.abs(thetaDelta) > thetaDelta + PI2) {
            thetaDelta += PI2;
         }
      }

      return new Vec3(
         from.x() * (1.0F - pct) + to.x() * pct,
         from.y() + thetaDelta * pct,
         from.z() * (1.0F - pct) + to.z() * pct
      );
   }

   public static Quaternionf fromEuler(float y, float x, float z) {
      return new Quaternionf().rotateZ(z).rotateX(x).rotateY(y);
   }

   public static float lerpBetween(float from, float to, float pct) {
      float delta = to - from;
      while (delta < -180.0F) {
         delta += 360.0F;
      }

      while (delta >= 180.0F) {
         delta -= 360.0F;
      }

      return from + pct * delta;
   }

   public static Vec3 ToCartesianCoordinates(Vec3 cylindrical) {
      return new Vec3(
         cylindrical.x() * Math.cos(cylindrical.y()),
         cylindrical.z(),
         cylindrical.x() * Math.sin(-cylindrical.y())
      );
   }

   public static float toDegrees(float angle) {
      return angle * RADIANS_TO_DEGREES;
   }
}
