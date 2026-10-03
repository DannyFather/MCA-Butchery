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

public class MCAVillagerBoobsModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "villager_boobs_layer"), "main");
	private final ModelPart Waist;
	private final ModelPart Boobs;

	public MCAVillagerBoobsModel(ModelPart root) {
		this.Waist = root.getChild("Waist");
		this.Boobs = this.Waist.getChild("Boobs");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offsetAndRotation(8.1F, 17.95F, 9.05F, 3.0718F, 0.0F, 0.0F));

		PartDefinition Boobs = Waist.addOrReplaceChild("Boobs", CubeListBuilder.create().texOffs(20, 19).addBox(-3.0F, -1.134F, 0.2321F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(21, 19).addBox(3.0F, -1.134F, 0.2321F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(21, 20).addBox(-3.0F, -1.134F, 0.2321F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(22, 35).addBox(3.075F, -1.1163F, 0.339F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.25F))
				.texOffs(20, 35).addBox(-3.025F, -1.134F, 0.3571F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.25F))
				.texOffs(21, 36).addBox(-3.0F, -1.134F, 0.3321F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-0.1F, 0.3973F, -0.4516F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Body_Layer_r1 = Boobs.addOrReplaceChild("Body_Layer_r1", CubeListBuilder.create().texOffs(12, 40).mirror().addBox(-3.0F, 0.0246F, -1.1107F, 6.0F, 0.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, 2.866F, 2.2321F, 0.0F, 3.1416F, 0.0F));

		PartDefinition Body_r1 = Boobs.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(12, 24).mirror().addBox(-3.0F, 0.0F, -1.5F, 6.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 2.866F, 1.7321F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public void setVisible(boolean state) {
		Boobs.visible = state;
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		Waist.render(poseStack, buffer, packedLight, packedOverlay, color);
	}
}