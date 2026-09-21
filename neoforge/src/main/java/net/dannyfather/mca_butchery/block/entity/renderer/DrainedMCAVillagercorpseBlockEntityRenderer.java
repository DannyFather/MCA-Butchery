package net.dannyfather.mca_butchery.block.entity.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.DrainedMCAVillagerCorpseBlock;
import net.dannyfather.mca_butchery.block.entity.DrainedMCAVillagerCorpseBlockEntity;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerCorpseHangingModel;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerCorpseModel;
import net.dannyfather.mca_butchery.block.entity.models.OrgansandBonesHangingModel;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.client.CorpseTexture;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.UUID;

public class DrainedMCAVillagercorpseBlockEntityRenderer implements BlockEntityRenderer<DrainedMCAVillagerCorpseBlockEntity> {

    private final MCAVillagerCorpseModel model;
    private final MCAVillagerCorpseHangingModel hangingModel;
    private final OrgansandBonesHangingModel organsandBonesHangingModel;

    private static final ResourceLocation DEFAULT_SKIN = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");
    private static final ResourceLocation BLOOD_SPLATTER = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/blood_splatter.png");
    private static final ResourceLocation BLOOD_SPLATTER_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/blood_splatter_mask.png");
    private static final ResourceLocation CLOTHING_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/clothing_mask.png");

    private static final ResourceLocation ORGANS_AND_BONES = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/organsandbones.png");
    private static final ResourceLocation CUT_OPEN_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_mask.png");
    private static final ResourceLocation CUT_OPEN_BLOOD = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_blood.png");
    private static final ResourceLocation CUT_OPEN_BLOOD_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_blood_mask.png");

    public DrainedMCAVillagercorpseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new MCAVillagerCorpseModel(context.bakeLayer(MCAVillagerCorpseModel.LAYER_LOCATION));
        this.hangingModel = new MCAVillagerCorpseHangingModel<>(context.bakeLayer(MCAVillagerCorpseHangingModel.LAYER_LOCATION));
        this.organsandBonesHangingModel = new OrgansandBonesHangingModel<>(context.bakeLayer(OrgansandBonesHangingModel.LAYER_LOCATION));
    }

    @Override
    public void render(DrainedMCAVillagerCorpseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        poseStack.translate(0.5F, 0.0F, 0.5F);
        Direction facing = blockEntity.getBlockState().getValue(DrainedMCAVillagerCorpseBlock.FACING);

        poseStack.mulPose( Axis.YP.rotationDegrees(-facing.toYRot()) );

        poseStack.translate(-0.5F, 0.0F, -0.5F);

        int state = blockEntity.getBlockState().getValue(DrainedMCAVillagerCorpseBlock.BLOCKSTATE);

        NativeImage defaultSkin = null;
        NativeImage bloodSplatter = null;
        NativeImage bloodSplatterMask = null;
        NativeImage clothingMask = null;
        NativeImage cutOpenMask = null;
        NativeImage cutOpenBlood = null;
        NativeImage cutOpenBloodMask = null;
        try {
            defaultSkin = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(DEFAULT_SKIN).open());
            bloodSplatter = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(BLOOD_SPLATTER).open());
            bloodSplatterMask = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(BLOOD_SPLATTER_MASK).open());
            clothingMask = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CLOTHING_MASK).open());
            cutOpenMask = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_MASK).open());
            cutOpenBlood = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_BLOOD).open());
            cutOpenBloodMask = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_BLOOD_MASK).open());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        UUID villager = blockEntity.getVillager();

        NativeImage chosenSkin = defaultSkin;

        if (villager != null) {
            NativeImage cachedTexture = ClientSkinCache.getImage(villager);

            if (cachedTexture == null) {
                if (!ClientSkinCache.hasRequested(villager)) {
                    ClientSkinCache.markRequested(villager);
                    MCAButcheryNetwork.requestVillagerSkin(villager);
                }

                poseStack.popPose();
                return;
            }

            chosenSkin = cachedTexture;

        }




        CorpseTexture drainedTexture = new CorpseTexture("corpse_drained",chosenSkin,bloodSplatter,clothingMask,bloodSplatterMask,false,true);
        CorpseTexture bloodyTexture = new CorpseTexture("corpse_bloody",chosenSkin,bloodSplatter,clothingMask,bloodSplatterMask,true,true);
        CorpseTexture cutOpenTexture = new CorpseTexture("corpse_cut_open",chosenSkin,cutOpenBlood,cutOpenMask,cutOpenBloodMask,true,true);

        organsandBonesHangingModel.setVisibleParts(state);
        model.setVisibleParts(state);
        hangingModel.setVisibleParts(state);

        switch (state) {
            case 0 -> {
                poseStack.translate(0f,0.006f,-0.0234f);
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(drainedTexture.getLocation()));
                model.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
                poseStack.popPose();
            }

            case 1-> {
                poseStack.translate(0f,0.006f,0.0075f);
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(drainedTexture.getLocation()));
                hangingModel.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
                poseStack.popPose();
            }

            case 2,3,4,5,6 -> {
                poseStack.translate(0f,0.006f,0.0075f);
                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, bloodyTexture.getLocation(),true);
            }

            case 7,8,9,10,20 -> {
                poseStack.translate(0f,0.006f,0.0075f);
                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, cutOpenTexture.getLocation(),true);
            }

            case 11,12,13 -> {
                poseStack.translate(0f,0.006f,-0.0234f);
                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, bloodyTexture.getLocation(),false);
            }

            case 14,15,16,17,18,19 -> {
                poseStack.translate(0f,0.006f,-0.0234f);
                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, cutOpenTexture.getLocation(),false);
            }


        }




    }

    private void cutOpen(DrainedMCAVillagerCorpseBlockEntity blockEntity,PoseStack poseStack,MultiBufferSource bufferSource ,int packedLight, int packedOverlay, ResourceLocation resourceLocation,boolean hanging) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(resourceLocation));
        if(hanging) {
            hangingModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        } else {
            model.renderToBuffer(poseStack,vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
        }
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        Direction facingInternals = blockEntity.getBlockState().getValue(DrainedMCAVillagerCorpseBlock.FACING);

        poseStack.mulPose( Axis.YP.rotationDegrees(-facingInternals.toYRot()) );

        poseStack.translate(-0.5F, 0.0F, -0.5F);
        if(hanging) {
            poseStack.scale(0.9f, 0.96f, 0.8f);
            poseStack.translate(0.055F, 0.02F, 0.12F);
        } else {
            poseStack.scale(0.9f, 0.96f, 0.8f);
            poseStack.translate(0.055F, -0.17F, -0.18F);
        }
        VertexConsumer internalsComsumer = bufferSource.getBuffer(RenderType.entityCutout(ORGANS_AND_BONES));
        organsandBonesHangingModel.renderToBuffer(poseStack, internalsComsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        poseStack.popPose();

    }


}
