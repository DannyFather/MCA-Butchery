package net.dannyfather.mca_butchery.block.entity.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.IronSpikeBlock;
import net.dannyfather.mca_butchery.block.entity.IronSpikeBlockEntity;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.client.CorpseTexture;
import net.dannyfather.mca_butchery.config.MCAButcheryCommonConfig;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SkullBlock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class IronSpikeBlockEntityRenderer implements BlockEntityRenderer<IronSpikeBlockEntity> {

    private final MCAVillagerHeadModel model;
    private final SkullModelBase skeletonSkull;

    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");
    private static final ResourceLocation BLOOD_SPLATTER = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/blood_splatter.png");
    private static final ResourceLocation BLOOD_SPLATTER_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/blood_splatter_mask.png");
    private static final ResourceLocation CUT_OPEN_BLOOD_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_blood_mask.png");
    private static final ResourceLocation CUT_OPEN_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_mask.png");
    private static final ResourceLocation CLOTHING_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/clothing_mask.png");
    private static final ResourceLocation DECAYED_SKULL = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/decayed_skull.png");

    public IronSpikeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        Map<SkullBlock.Type, SkullModelBase> skullModels = SkullBlockRenderer.createSkullRenderers(context.getModelSet());
        this.model = new MCAVillagerHeadModel(context.bakeLayer(MCAVillagerHeadModel.LAYER_LOCATION));
        this.skeletonSkull = skullModels.get(SkullBlock.Types.SKELETON);
    }

    @Override
    public void render(IronSpikeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        int hasHead = blockEntity.getBlockState().getValue(IronSpikeBlock.HASHEAD);
        if(hasHead == 1) {

            int rotation = blockEntity.getBlockState().getValue(IronSpikeBlock.ROTATION);

            UUID villager = blockEntity.getVillager();
            Float hSize = blockEntity.getHeight();
            Float wSize = blockEntity.getWidth();
            Integer face = blockEntity.getFace();
            Integer decayState = blockEntity.getDecayState();


            RandomSource random = RandomSource.create(blockEntity.getBlockPos().asLong());
            float randomFloat = random.nextFloat();


            poseStack.pushPose();

            poseStack.translate(0.5F, 0.0F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotation * 22.5f));
            poseStack.mulPose(Axis.XP.rotationDegrees(randomFloat*10f - 5f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomFloat*10f - 5f));
            poseStack.scale(wSize, hSize, wSize);
            poseStack.translate(-0.5F, 0.4F + 0.3f * randomFloat, -0.5F);
            if(decayState < 4) {
                MCAVillagerHeadBlockEntityRenderer.renderHead(villager, model, bufferSource, poseStack, packedLight, packedOverlay, decayState, face);
            } else {
                SkullBlockRenderer.renderSkull(null,180.0F, 0.0F, poseStack, bufferSource, packedLight, skeletonSkull, SkullBlockRenderer.getRenderType(SkullBlock.Types.SKELETON,null));
                poseStack.popPose();
            }
        }
    }


}