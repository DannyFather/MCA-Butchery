package net.dannyfather.mca_butchery.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.block.entity.renderer.MCAVillagerHeadBlockEntityRenderer;
import net.dannyfather.mca_butchery.config.MCAButcheryCommonConfig;
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
import net.neoforged.fml.ModList;

import java.io.IOException;
import java.util.UUID;

public class MCAVillagerHeadItemRenderer extends BlockEntityWithoutLevelRenderer {

    private MCAVillagerHeadModel<?> model;

    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");
    private static final ResourceLocation BLOOD_SPLATTER = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/blood_splatter.png");
    private static final ResourceLocation BLOOD_SPLATTER_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/blood_splatter_mask.png");
    private static final ResourceLocation CUT_OPEN_BLOOD_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_blood_mask.png");
    private static final ResourceLocation CUT_OPEN_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_mask.png");
    private static final ResourceLocation CLOTHING_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/clothing_mask.png");
    private static final ResourceLocation DECAYED_SKULL = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/decayed_skull.png");

    public MCAVillagerHeadItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (this.model == null) {
            this.model = new MCAVillagerHeadModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(MCAVillagerHeadModel.LAYER_LOCATION));
        }

        if (displayContext == ItemDisplayContext.HEAD) {
            return;
        }

        UUID villager = stack.get(MCAButcheryItems.VILLAGER_UUID);
        Float hSize = stack.get(MCAButcheryItems.H_SIZE);
        Float wSize = stack.get(MCAButcheryItems.W_SIZE);
        Integer face = stack.get(MCAButcheryItems.FACE);
        Integer decayState = stack.get(MCAButcheryItems.DECAY_STATE);

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

            case THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(45f));
                poseStack.mulPose(Axis.YP.rotationDegrees(45f));
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0.4f, 1.165f, 0f);
            }

            case THIRD_PERSON_LEFT_HAND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(45f));
                poseStack.mulPose(Axis.YP.rotationDegrees(-45f));
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0.01f, 1.165f, -1.4f);
            }

            case FIXED -> {
                poseStack.translate(0.5f, 0f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                poseStack.translate(-0.5f, 0.25f, -0.5f);
            }

            case FIRST_PERSON_RIGHT_HAND -> {
                if (ModList.get().isLoaded("holdmyitemsnf")) {
                    poseStack.translate(0.5f, 0f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                    poseStack.scale(0.4f, 0.4f, 0.4f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(90f));
                    poseStack.translate(-0.5f, 0.8f, -0.1f);
                }
            }

            case FIRST_PERSON_LEFT_HAND -> {
                if (ModList.get().isLoaded("holdmyitemsnf")) {
                    poseStack.translate(0.5f, 0f, 0.5f);
                    poseStack.scale(0.4f, 0.4f, 0.4f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(90f));
                    poseStack.translate(-0.5f, 0.8f, -0.1f);
                }
            }


        }

        MCAVillagerHeadBlockEntityRenderer.renderHead(villager,model,bufferSource, poseStack,packedLight,packedOverlay,decayState,face);

    }
}