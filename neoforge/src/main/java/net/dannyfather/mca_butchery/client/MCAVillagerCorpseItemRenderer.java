package net.dannyfather.mca_butchery.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerCorpseModel;
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

public class MCAVillagerCorpseItemRenderer extends BlockEntityWithoutLevelRenderer {

    private MCAVillagerCorpseModel<?> model;

    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");

    public MCAVillagerCorpseItemRenderer(BlockEntityRenderDispatcher dispatcher,EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (this.model == null) {
            this.model = new MCAVillagerCorpseModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(MCAVillagerCorpseModel.LAYER_LOCATION));
        }

        UUID villager = stack.get(MCAButcheryItems.VILLAGER_UUID);

        poseStack.pushPose();

        switch (displayContext) {
            case GROUND -> {
                poseStack.translate(0.0f, 0.1f, 0f);
                poseStack.scale(1.1f, 1.1f, 1.1f);
            }

            case GUI -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(30f));
                poseStack.translate(0.2f, 0.2f, 0f);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45f));
            }

            case THIRD_PERSON_RIGHT_HAND  -> {
                poseStack.translate(0.5F, 0.0F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(160f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
                poseStack.translate(-1.2F, -0.2F, -0.3F);
            }

            case THIRD_PERSON_LEFT_HAND  -> {
                poseStack.translate(0.5F, 0.0F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(160f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90f));
                poseStack.translate(0.2F, -0.2F, -0.3F);
            }

            case FIRST_PERSON_RIGHT_HAND  -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
                poseStack.translate(-0.1F, -0.66F, -0.8F);
            }

            case FIRST_PERSON_LEFT_HAND  -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90f));
                poseStack.translate(-0.9F, 0.34F, -0.8F);
            }

            case FIXED -> {
                poseStack.translate(0.5f,0f,0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                poseStack.translate(-0.5f,0f,-0.2f);
            }

        }
        this.model.setVisibleParts(0);
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