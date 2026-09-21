package net.dannyfather.mca_butchery.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.MCAVillagerHeadBlock;
import net.dannyfather.mca_butchery.block.entity.MCAVillagerHeadBlockEntity;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class MCAVillagerHeadBlockEntityRenderer implements BlockEntityRenderer<MCAVillagerHeadBlockEntity> {

    private final MCAVillagerHeadModel model;
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");

    public MCAVillagerHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new MCAVillagerHeadModel(context.bakeLayer(MCAVillagerHeadModel.LAYER_LOCATION));
    }

    @Override
    public void render(MCAVillagerHeadBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Direction facing = blockEntity.getBlockState().getValue(MCAVillagerHeadBlock.FACING);
        int state = blockEntity.getBlockState().getValue(MCAVillagerHeadBlock.BLOCKSTATE);
        int rotation = blockEntity.getBlockState().getValue(MCAVillagerHeadBlock.ROTATION);
        UUID villager = blockEntity.getVillager();


        poseStack.pushPose();

        switch (state) {

            case 0 -> {
                poseStack.translate(0.5F, 0.0F, 0.5F);
                poseStack.mulPose( Axis.YP.rotationDegrees(180f - rotation * 22.5f ) );
                poseStack.translate(-0.5F, 0.0F, -0.5F);
            }

            case 1 -> {

                poseStack.translate(0.5F, 0.0F, 0.5F);
                poseStack.mulPose( Axis.YP.rotationDegrees(-facing.toYRot()) );
                poseStack.translate(-0.5F, 0.0F, -0.5F);
                poseStack.translate(0f,0.25f,-0.25f);
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

        model.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);

        poseStack.popPose();

    }


}
