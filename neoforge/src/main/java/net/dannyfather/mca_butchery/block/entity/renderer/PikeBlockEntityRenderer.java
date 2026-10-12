package net.dannyfather.mca_butchery.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.PikeBlock;
import net.dannyfather.mca_butchery.block.entity.PikeBlockEntity;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.UUID;

public class PikeBlockEntityRenderer implements BlockEntityRenderer<PikeBlockEntity> {

    private final MCAVillagerHeadModel model;
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");

    public PikeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new MCAVillagerHeadModel(context.bakeLayer(MCAVillagerHeadModel.LAYER_LOCATION));
    }

    @Override
    public void render(PikeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        int hasHead = blockEntity.getBlockState().getValue(PikeBlock.HASHEAD);

        int rotation = blockEntity.getBlockState().getValue(PikeBlock.ROTATION);

        UUID villager = blockEntity.getVillager();
        Float hSize = blockEntity.getHeight();
        Float wSize = blockEntity.getWidth();


        RandomSource random = RandomSource.create(blockEntity.getBlockPos().asLong());
        float randomFloat = random.nextFloat();


        poseStack.pushPose();

        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotation * 22.5f));
        poseStack.mulPose(Axis.XP.rotationDegrees(randomFloat*10f - 5f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(randomFloat*10f - 5f));
        poseStack.scale(wSize, hSize, wSize);
        poseStack.translate(-0.5F, 0.6F + 0.1f * randomFloat, -0.5F);


        VertexConsumer vertexConsumer;

        if (villager != null) {
            ResourceLocation texture = ClientSkinCache.get(villager);
            if (texture == null) {
                if (!ClientSkinCache.hasRequested(villager)) {
                    ClientSkinCache.markRequested(villager);
                    MCAButcheryNetwork.requestVillagerSkin(villager);
                }
                poseStack.popPose();
                return;
            }
            vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
        } else {
            vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(DEFAULT_TEXTURE));
        }

        if(hasHead == 1) {
            model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        }
        poseStack.popPose();
    }


}
