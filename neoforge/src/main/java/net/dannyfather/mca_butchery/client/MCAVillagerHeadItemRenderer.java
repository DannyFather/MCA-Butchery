package net.dannyfather.mca_butchery.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class MCAVillagerHeadItemRenderer extends BlockEntityWithoutLevelRenderer {

    private MCAVillagerHeadModel<?> model;

    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");

    public MCAVillagerHeadItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (this.model == null) {
            this.model = new MCAVillagerHeadModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(MCAVillagerHeadModel.LAYER_LOCATION));
        }

        UUID villager = stack.get(MCAButcheryItems.VILLAGER_UUID);

        poseStack.pushPose();

        switch (displayContext) {
            case GROUND -> {
                poseStack.translate(0.25f, 0.25f, 0.25f);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }

            case GUI -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(30f));
                poseStack.translate(-0.21f, 0.3f, 0f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45f));
            }

            case THIRD_PERSON_RIGHT_HAND  -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(45f));
                poseStack.mulPose(Axis.YP.rotationDegrees(45f));
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0.4f,1.165f,0f);
            }

            case THIRD_PERSON_LEFT_HAND  -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(45f));
                poseStack.mulPose(Axis.YP.rotationDegrees(-45f));
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0.01f,1.165f,-1.4f);
            }

            case FIXED -> {
                poseStack.translate(0.5f,0f,0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                poseStack.translate(-0.5f,0.25f,-0.5f);
            }

         }

        VertexConsumer vertexConsumer;

        if(villager != null) {
            ResourceLocation texture = ClientSkinCache.get(villager);
            if(texture == null) {
                if(!ClientSkinCache.hasRequested(villager)) {
                    ClientSkinCache.markRequested(villager);
                    MCAButcheryNetwork.requestVillagerSkin(villager);
                }
                poseStack.popPose();
                return;
            }
            vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
        } else {
            vertexConsumer  = bufferSource.getBuffer(RenderType.entityCutout(DEFAULT_TEXTURE));
        }

        this.model.renderToBuffer(poseStack,vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);

        poseStack.popPose();

    }
}