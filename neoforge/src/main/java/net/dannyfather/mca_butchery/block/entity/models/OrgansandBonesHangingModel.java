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

public class OrgansandBonesHangingModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "organs_and_bones_hanging_layer"), "main");
    private final ModelPart Waist;
    private final ModelPart Heart;
    private final ModelPart Intestines;
    private final ModelPart Liver;
    private final ModelPart Stomach;
    private final ModelPart Body;
    private final ModelPart Bones;

    public OrgansandBonesHangingModel(ModelPart root) {
        this.Waist = root.getChild("Waist");
        this.Heart = this.Waist.getChild("Heart");
        this.Intestines = this.Waist.getChild("Intestines");
        this.Liver = this.Waist.getChild("Liver");
        this.Stomach = this.Waist.getChild("Stomach");
        this.Body = this.Waist.getChild("Body");
        this.Bones = this.Waist.getChild("Bones");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offsetAndRotation(8.0F, 11.0F, 7.5F, -3.1416F, 0.0F, 0.0F));

        PartDefinition Heart = Waist.addOrReplaceChild("Heart", CubeListBuilder.create().texOffs(29, 20).addBox(-0.5F, 1.0F, -0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition Intestines = Waist.addOrReplaceChild("Intestines", CubeListBuilder.create().texOffs(42, 41).addBox(-4.5F, -1.0F, -1.5F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -1.0F, 0.5F));

        PartDefinition Liver = Waist.addOrReplaceChild("Liver", CubeListBuilder.create(), PartPose.offset(1.5F, -1.0F, 0.5F));

        PartDefinition cube_r1 = Liver.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(42, 47).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 0.25F, 0.0F, 0.0F, -1.5708F));

        PartDefinition Stomach = Waist.addOrReplaceChild("Stomach", CubeListBuilder.create().texOffs(52, 47).addBox(0.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -4.0F, 0.5F));

        PartDefinition Body = Waist.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 20).addBox(-4.1F, -24.1F, -2.2F, 8.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 16).addBox(4.1F, -24.1F, -2.1F, 0.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 16).addBox(-4.2F, -24.1F, -2.1F, 0.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 16).addBox(-4.1F, -24.2F, -2.1F, 8.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition Body_r1 = Body.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(20, 20).addBox(-4.0F, -3.0F, 0.0F, 8.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, -21.1F, 2.1F, 3.1416F, 0.0F, 3.1416F));

        PartDefinition Body_r2 = Body.addOrReplaceChild("Body_r2", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, -36.05F, -0.1F, 3.1416F, 0.0F, 0.0F));

        PartDefinition Bones = Waist.addOrReplaceChild("Bones", CubeListBuilder.create().texOffs(26, 35).addBox(12.3F, -19.5F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 35).addBox(2.5F, -19.5F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 46).addBox(5.3F, -9.1F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 46).addBox(9.7F, -9.1F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 38).addBox(7.45F, -22.25F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 13.0F, -4.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    public void setVisibleParts(int state) {
        Heart.visible = false;
        Body.visible = false;
        Stomach.visible = false;
        Liver.visible = false;
        Intestines.visible = false;
        Bones.visible = false;

        switch (state) {
            case 2,3,4,5,6,11,12,13 -> {
                Bones.visible = true;
            }

            case 7,14 -> {
                Heart.visible = true;
                Body.visible = true;
                Stomach.visible = true;
                Liver.visible = true;
                Intestines.visible = true;
                Bones.visible = true;
            }

            case 8,15 -> {
                Body.visible = true;
                Stomach.visible = true;
                Liver.visible = true;
                Intestines.visible = true;
                Bones.visible = true;
            }

            case 9,16 -> {
                Body.visible = true;
                Liver.visible = true;
                Intestines.visible = true;
                Bones.visible = true;
            }

            case 10,17 -> {
                Body.visible = true;
                Intestines.visible = true;
                Bones.visible = true;
            }

            case 19,20 -> {
                Body.visible = true;
                Bones.visible = true;
            }

        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        Waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}