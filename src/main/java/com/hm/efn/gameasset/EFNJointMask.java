package com.hm.efn.gameasset;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.client.animation.property.JointMask.JointMaskSet;

@OnlyIn(Dist.CLIENT)
public class EFNJointMask {
   public static final JointMaskSet WHEEL_UPPER_JOINTS_WITH_ROOT = JointMaskSet.of(
      new JointMask[]{
         JointMask.of("Root", JointMask.KEEP_CHILD_LOCROT),
         JointMask.of("Torso"),
         JointMask.of("Chest"),
         JointMask.of("Head"),
         JointMask.of("Shoulder_R"),
         JointMask.of("Arm_R"),
         JointMask.of("Hand_R"),
         JointMask.of("Elbow_R"),
         JointMask.of("wheel"),
         JointMask.of("Tool_R"),
         JointMask.of("Shoulder_L"),
         JointMask.of("Arm_L"),
         JointMask.of("Hand_L"),
         JointMask.of("Elbow_L"),
         JointMask.of("Tool_L")
      }
   );
   public static final JointMaskEntry WHEEL_ATTACK_MASK = JointMaskEntry.builder().defaultMask(WHEEL_UPPER_JOINTS_WITH_ROOT).create();
}
