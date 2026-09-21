package net.dannyfather.mca_butchery.block.entity.models;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class MCAVillagerCorpseModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "villager_corpse_layer"), "main");
    private final ModelPart Waist;
    private final ModelPart Head;
    private final ModelPart Body;
    private final ModelPart Right_Arm;
    private final ModelPart Left_Arm;
    private final ModelPart Right_Leg;
    private final ModelPart Left_Leg;

    public MCAVillagerCorpseModel(ModelPart root) {
        this.Waist = root.getChild("Waist");
        this.Head = this.Waist.getChild("Head");
        this.Body = this.Waist.getChild("Body");
        this.Right_Arm = this.Waist.getChild("Right_Arm");
        this.Left_Arm = this.Waist.getChild("Left_Arm");
        this.Right_Leg = this.Waist.getChild("Right_Leg");
        this.Left_Leg = this.Waist.getChild("Left_Leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offsetAndRotation(8.6F, 8.0F, 2.25F, -3.1416F, 0.0F, 0.0F));

        PartDefinition Head = Waist.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-0.6F, -8.0F, -1.75F, 0.3927F, 0.0F, 0.0F));

        PartDefinition Body = Waist.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-0.6F, -8.0F, -1.75F));

        PartDefinition Right_Arm = Waist.addOrReplaceChild("Right_Arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.6F, -6.0F, -1.75F));

        PartDefinition Left_Arm = Waist.addOrReplaceChild("Left_Arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(4.4F, -6.0F, -1.75F));

        PartDefinition Right_Leg = Waist.addOrReplaceChild("Right_Leg", CubeListBuilder.create().texOffs(0, 16).addBox(-3.8672F, 0.7167F, 4.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-3.8672F, 0.7167F, 4.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.3665F, 0.0F));

        PartDefinition Left_Leg = Waist.addOrReplaceChild("Left_Leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.8F, 6.0F, 0.0F, -1.5708F, -0.3752F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    public void setVisibleParts(int state) {
        Head.visible = false;
        Body.visible = false;
        Right_Arm.visible = false;
        Left_Arm.visible = false;
        Right_Leg.visible = false;
        Left_Leg.visible = false;

        switch (state) {
            case 0 -> {
                Head.visible = true;
                Body.visible = true;
                Right_Arm.visible = true;
                Left_Arm.visible = true;
                Right_Leg.visible = true;
                Left_Leg.visible = true;
            }

            case 11 -> {
                Body.visible = true;
                Right_Arm.visible = true;
                Left_Arm.visible = true;
                Right_Leg.visible = true;
                Left_Leg.visible = true;
            }

            case 12 -> {
                Body.visible = true;
                Left_Arm.visible = true;
                Right_Leg.visible = true;
                Left_Leg.visible = true;
            }

            case 13,14,15,16,17,19 -> {
                Body.visible = true;
                Right_Leg.visible = true;
                Left_Leg.visible = true;
            }

            case 18 -> {
                Right_Leg.visible = true;
                Left_Leg.visible = true;
            }

        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int colour) {
        Waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, colour);
    }

}