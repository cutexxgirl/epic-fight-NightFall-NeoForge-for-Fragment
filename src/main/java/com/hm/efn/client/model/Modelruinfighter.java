package com.hm.efn.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class Modelruinfighter<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("modid", "ruinsfighter"), "main");
   public final ModelPart Boots_R;
   public final ModelPart Boots_L;
   public final ModelPart Leggings_R;
   public final ModelPart Leggings_L;
   public final ModelPart ArmsL;
   public final ModelPart ArmsR;
   public final ModelPart Body;
   public final ModelPart Helmet;

   public Modelruinfighter(ModelPart root) {
      this.Boots_R = root.getChild("Boots_R");
      this.Boots_L = root.getChild("Boots_L");
      this.Leggings_R = root.getChild("Leggings_R");
      this.Leggings_L = root.getChild("Leggings_L");
      this.ArmsL = root.getChild("ArmsL");
      this.ArmsR = root.getChild("ArmsR");
      this.Body = root.getChild("Body");
      this.Helmet = root.getChild("Helmet");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition Boots_R = partdefinition.addOrReplaceChild(
         "Boots_R",
         CubeListBuilder.create().texOffs(48, 49).addBox(-2.0F, 8.25F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.45F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition Boots_L = partdefinition.addOrReplaceChild(
         "Boots_L",
         CubeListBuilder.create().texOffs(32, 49).addBox(-2.0F, 8.25F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.45F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition Leggings_R = partdefinition.addOrReplaceChild(
         "Leggings_R",
         CubeListBuilder.create()
            .texOffs(50, 21)
            .addBox(-2.0F, 4.25F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.45F))
            .texOffs(0, 37)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition Leggings_L = partdefinition.addOrReplaceChild(
         "Leggings_L",
         CubeListBuilder.create()
            .texOffs(50, 27)
            .addBox(-2.0F, 4.25F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.45F))
            .texOffs(28, 33)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      PartDefinition ArmsL = partdefinition.addOrReplaceChild(
         "ArmsL",
         CubeListBuilder.create()
            .texOffs(50, 14)
            .addBox(-1.0F, 7.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.3F))
            .texOffs(44, 33)
            .addBox(-0.8F, -2.1F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.51F))
            .texOffs(16, 37)
            .addBox(0.5F, -1.9F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(1.1F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition ArmsR = partdefinition.addOrReplaceChild(
         "ArmsR",
         CubeListBuilder.create()
            .texOffs(50, 7)
            .addBox(-3.0F, 7.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.3F))
            .texOffs(16, 49)
            .addBox(-3.0F, 2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.3F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition LeftArmLayer_r1 = ArmsR.addOrReplaceChild(
         "LeftArmLayer_r1",
         CubeListBuilder.create().texOffs(44, 41).addBox(-4.5F, 0.5F, 0.5F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.51F)),
         PartPose.offsetAndRotation(1.4F, -2.6F, -2.5F, 0.0F, 0.0F, 0.2182F)
      );
      PartDefinition Body = partdefinition.addOrReplaceChild(
         "Body",
         CubeListBuilder.create()
            .texOffs(36, 0)
            .addBox(-3.5F, 0.1F, -1.5F, 7.0F, 4.0F, 3.0F, new CubeDeformation(1.1F))
            .texOffs(0, 12)
            .addBox(-4.5F, 0.0F, -2.5F, 9.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(0, 29)
            .addBox(-4.5F, 10.0F, -2.5F, 9.0F, 3.0F, 5.0F, new CubeDeformation(-0.15F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition BodyLayer_r1 = Body.addOrReplaceChild(
         "BodyLayer_r1",
         CubeListBuilder.create().texOffs(28, 12).addBox(-11.0F, 0.0F, -0.001F, 11.0F, 21.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(5.4F, -0.8F, 2.6F, 0.0436F, 0.0F, 0.0F)
      );
      PartDefinition Helmet = partdefinition.addOrReplaceChild(
         "Helmet",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -2.3F, -4.5F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.1F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
      this.Boots_R.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.Boots_L.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.Leggings_R.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.Leggings_L.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.ArmsL.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.ArmsR.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
      this.Helmet.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
   }
}
