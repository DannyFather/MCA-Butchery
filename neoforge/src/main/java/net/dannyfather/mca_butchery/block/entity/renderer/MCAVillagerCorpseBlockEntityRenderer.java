package net.dannyfather.mca_butchery.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.MCAVillagerCorpseBlock;
import net.dannyfather.mca_butchery.block.entity.MCAVillagerCorpseBlockEntity;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerCorpseHangingModel;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerCorpseModel;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class MCAVillagerCorpseBlockEntityRenderer implements BlockEntityRenderer<MCAVillagerCorpseBlockEntity> {

    private final MCAVillagerCorpseModel model;
    private final MCAVillagerCorpseHangingModel hangingModel;
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");

    public MCAVillagerCorpseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new MCAVillagerCorpseModel(context.bakeLayer(MCAVillagerCorpseModel.LAYER_LOCATION));
        this.hangingModel = new MCAVillagerCorpseHangingModel<>(context.bakeLayer(MCAVillagerCorpseHangingModel.LAYER_LOCATION));
    }

    @Override
    public void render(MCAVillagerCorpseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Direction facing = blockEntity.getBlockState().getValue(MCAVillagerCorpseBlock.FACING);
        int state = blockEntity.getBlockState().getValue(MCAVillagerCorpseBlock.BLOCKSTATE);
        UUID villager = blockEntity.getVillager();


        poseStack.pushPose();

        poseStack.translate(0.5F, 0.0F, 0.5F);

        poseStack.mulPose( Axis.YP.rotationDegrees(-facing.toYRot()) );

        poseStack.translate(-0.5F, 0.0F, -0.5F);


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


        switch (state) {
            case 0 -> {
                poseStack.translate(0f,0.006f,-0.0234f);
                model.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
            }

            case 1 -> {
                poseStack.translate(0f,0.006f,0.0075f);
                hangingModel.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
            }
        }


        poseStack.popPose();

    }


}
