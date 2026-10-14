package net.dannyfather.mca_butchery.block.entity.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.conczin.mca.Config;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.DrainedMCAVillagerCorpseBlock;
import net.dannyfather.mca_butchery.block.entity.DrainedMCAVillagerCorpseBlockEntity;
import net.dannyfather.mca_butchery.block.entity.models.*;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.client.CorpseTexture;
import net.dannyfather.mca_butchery.config.MCAButcheryCommonConfig;
import net.dannyfather.mca_butchery.network.MCAButcheryClientNetwork;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
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
    private final MCAVillagerCorpseModelSlim slimModel;
    private final MCAVillagerCorpseHangingModelSlim slimHangingModel;
    private final OrgansandBonesHangingModel organsandBonesHangingModel;
    private final MCAVillagerBoobsModel villagerBoobsModel;

    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");
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
        this.slimModel = new MCAVillagerCorpseModelSlim(context.bakeLayer(MCAVillagerCorpseModelSlim.LAYER_LOCATION));
        this.slimHangingModel = new MCAVillagerCorpseHangingModelSlim(context.bakeLayer(MCAVillagerCorpseHangingModelSlim.LAYER_LOCATION));
        this.organsandBonesHangingModel = new OrgansandBonesHangingModel<>(context.bakeLayer(OrgansandBonesHangingModel.LAYER_LOCATION));
        this.villagerBoobsModel = new MCAVillagerBoobsModel<>(context.bakeLayer(MCAVillagerBoobsModel.LAYER_LOCATION));
    }

    @Override
    public void render(DrainedMCAVillagerCorpseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = blockEntity.getBlockState().getValue(DrainedMCAVillagerCorpseBlock.FACING);
        UUID villager = blockEntity.getVillager();
        Float bSize = blockEntity.getBSize();
        Float hSize = blockEntity.getHeight();
        Float wSize = blockEntity.getWidth();
        Integer face = blockEntity.getFace();
        Boolean slim = blockEntity.getSlim();

        poseStack.pushPose();

        poseStack.translate(0.5F, 0.5F, 0.5F);

        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        poseStack.translate(-0.5F, -0.5F, -0.5F);

        int state = blockEntity.getBlockState().getValue(DrainedMCAVillagerCorpseBlock.BLOCKSTATE);

        boolean hanging = (state >= 1 && state <= 10) || state == 20;

        villagerBoobsModel.setVisible((state != 18) && bSize > 0);

        if(hanging) {
            if (slim) {
                slimModel.setVisibleParts(state);
                renderDrainedBody(villager, slimModel, villagerBoobsModel, bufferSource, poseStack, packedLight, packedOverlay, bSize, hSize, wSize, face, state,true);
            } else {
                hangingModel.setVisibleParts(state);
                renderDrainedBody(villager, hangingModel, villagerBoobsModel, bufferSource, poseStack, packedLight, packedOverlay, bSize, hSize, wSize, face, state, true);
            }
        } else {
            if (slim) {
                slimModel.setVisibleParts(state);
                renderDrainedBody(villager, slimModel, villagerBoobsModel, bufferSource, poseStack, packedLight, packedOverlay, bSize, hSize, wSize, face,state, false);
            } else {
                model.setVisibleParts(state);
                renderDrainedBody(villager, model, villagerBoobsModel, bufferSource, poseStack, packedLight, packedOverlay, bSize, hSize, wSize, face, state, false);
            }
        }

        if((state >= 2 && state <= 20)){
            poseStack.pushPose();

            if(hanging) {
                poseStack.translate(0.5F, 0.7F, 0.5F);
                poseStack.scale(wSize, hSize, wSize);
                poseStack.translate(-0.5F, -0.7F, -0.5F);
                poseStack.translate(0.5F, 0.7F, 0.5F);
                poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
                poseStack.translate(-0.5F, -0.7F, -0.5F);
                poseStack.scale(0.9f, 0.96f, 0.8f);
                poseStack.translate(0.055F, 0.02F, 0.12F);
            } else {
                poseStack.translate(0.5F, 0F, 0.5F);
                poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
                poseStack.translate(-0.5F, 0.0F, -0.5F);
                poseStack.scale(0.9f, 0.96f, 0.8f);
                poseStack.translate(0.555F, (0.25F * hSize) - 0.17F, 0.25F * wSize);
                poseStack.scale(wSize, hSize, wSize);
                poseStack.translate(-0.5F, 0.006f - (0.25F * hSize), -0.2f - 0.25F * wSize);
            }

            organsandBonesHangingModel.setVisibleParts(state);
            VertexConsumer internalsComsumer = bufferSource.getBuffer(RenderType.entityCutout(ORGANS_AND_BONES));
            organsandBonesHangingModel.renderToBuffer(poseStack, internalsComsumer, packedLight, packedOverlay, 0xFFFFFFFF);
            poseStack.popPose();
        }

//        NativeImage defaultSkin = null;
//        NativeImage bloodSplatter = null;
//        NativeImage bloodSplatterMask = null;
//        NativeImage clothingMask = null;
//        NativeImage cutOpenMask = null;
//        NativeImage cutOpenBlood = null;
//        NativeImage cutOpenBloodMask = null;
//        try {
//            defaultSkin = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(DEFAULT_SKIN).open());
//            bloodSplatter = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(BLOOD_SPLATTER).open());
//            bloodSplatterMask = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(BLOOD_SPLATTER_MASK).open());
//            clothingMask = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CLOTHING_MASK).open());
//            cutOpenMask = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_MASK).open());
//            cutOpenBlood = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_BLOOD).open());
//            cutOpenBloodMask = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_BLOOD_MASK).open());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        NativeImage chosenSkin = defaultSkin;
//        organsandBonesHangingModel.setVisibleParts(state);
//
//        if(slim) {
//            slimModel.setVisibleParts(state);
//            slimHangingModel.setVisibleParts(state);
//        } else {
//            model.setVisibleParts(state);
//            hangingModel.setVisibleParts(state);
//        }
//
//        if (villager != null) {
//            NativeImage cachedTexture = ClientSkinCache.getImage(villager.toString());
//
//            if (cachedTexture == null) {
//                if (!ClientSkinCache.hasRequested(villager.toString())) {
//                    ClientSkinCache.markRequested(villager.toString());
//                    MCAButcheryNetwork.requestVillagerSkin(villager);
//                }
//                switch (state) {
//                    case 0 -> {
//                        CorpseTexture drainedTexture = new CorpseTexture("corpse_drained_" + villager, chosenSkin, bloodSplatter, clothingMask, bloodSplatterMask, false, true,0);
//                        VertexConsumer drainedConsumer = bufferSource.getBuffer(RenderType.entityCutout(drainedTexture.getLocation()));
//                        setSittingModel(poseStack, packedLight, packedOverlay, hSize, wSize, slim, drainedConsumer, slimModel, model);
//                        poseStack.popPose();
//                    }
//
//                    case 1 -> {
//                        CorpseTexture drainedTexture = new CorpseTexture("corpse_drained_" + villager, chosenSkin, bloodSplatter, clothingMask, bloodSplatterMask, false, true,0);
//                        VertexConsumer drainedConsumer = bufferSource.getBuffer(RenderType.entityCutout(drainedTexture.getLocation()));
//                        setHangingModel(poseStack, packedLight, packedOverlay, hSize, wSize, slim, drainedConsumer, slimHangingModel, hangingModel);
//                        poseStack.popPose();
//                    }
//
//                    case 2,3,4,5,6 -> {
//                        CorpseTexture bloodyTexture = new CorpseTexture("corpse_bloody_" + villager,chosenSkin,bloodSplatter,clothingMask,bloodSplatterMask,true,true,0);
//                        cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, bloodyTexture.getLocation(),true, bSize, hSize, wSize, slim);
//                    }
//
//                    case 7,8,9,10,20 -> {
//                        CorpseTexture cutOpenTexture = new CorpseTexture("corpse_cut_open_"+ villager ,chosenSkin,cutOpenBlood,cutOpenMask,cutOpenBloodMask,true,true,0);
//                        cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, cutOpenTexture.getLocation(),true, bSize, hSize, wSize, slim);
//                    }
//
//                    case 11,12,13 -> {
//                        CorpseTexture bloodyTexture = new CorpseTexture("corpse_bloody_" + villager,chosenSkin,bloodSplatter,clothingMask,bloodSplatterMask,true,true,0);
//                        cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, bloodyTexture.getLocation(),false, bSize, hSize, wSize, slim);
//                    }
//
//                    case 14, 15, 16, 17, 19, 18 -> {
//                        CorpseTexture cutOpenTexture = new CorpseTexture("corpse_cut_open_"+ villager ,chosenSkin,cutOpenBlood,cutOpenMask,cutOpenBloodMask,true,true,0);
//                        cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, cutOpenTexture.getLocation(),false, bSize, hSize, wSize, slim);
//                    }
//
//                }
//
//                return;
//            }
//
//            if(face != 0) {
//                try (NativeImage eyesImage = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID,"textures/entity/villager_eyes_" + face + ".png")).open())) {
//                    cachedTexture = CorpseTexture.blendEyes(cachedTexture, eyesImage);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            chosenSkin = cachedTexture;
//
//        }
//
//
//
//
//
//        boolean boobsEnabled = Config.loadOrCreate().enableBoobs;
//        villagerBoobsModel.setVisible(bSize > 0 && boobsEnabled);
//
//        switch (state) {
//            case 0 -> {
//                CorpseTexture drainedTexture = new CorpseTexture("corpse_drained_" + villager,chosenSkin,bloodSplatter,clothingMask,bloodSplatterMask,false,true,0);
//                VertexConsumer drainedConsumer = bufferSource.getBuffer(RenderType.entityCutout(drainedTexture.getLocation()));
//                setSittingModel(poseStack, packedLight, packedOverlay, hSize, wSize, slim, drainedConsumer, slimModel, model);
//                poseStack.translate(0.5F, 0.8F, 0.5F);
//                poseStack.scale(1f + (0.23f * bSize),0.6f + (0.7f * bSize),0.6f + (0.7f * bSize));
//                poseStack.translate(-0.5f, -0.98F, -0.5f);
//                poseStack.translate(0f,0f, - 0.38f + (0.24f * bSize));
//                villagerBoobsModel.renderToBuffer(poseStack, drainedConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
//                poseStack.popPose();
//            }
//
//            case 1-> {
//                CorpseTexture drainedTexture = new CorpseTexture("corpse_drained_" + villager,chosenSkin,bloodSplatter,clothingMask,bloodSplatterMask,false,true,0);
//                VertexConsumer drainedConsumer = bufferSource.getBuffer(RenderType.entityCutout(drainedTexture.getLocation()));
//                setHangingModel(poseStack, packedLight, packedOverlay, hSize, wSize, slim, drainedConsumer, slimHangingModel, hangingModel);
//                poseStack.translate(0.5F, 1F, 0.5F);
//                poseStack.scale(1f + (0.23f * bSize),0.6f + (0.7f * bSize),0.6f + (0.7f * bSize));
//                poseStack.translate(-0.5f, -1F, -0.49f);
//                villagerBoobsModel.renderToBuffer(poseStack, drainedConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
//                poseStack.popPose();
//            }
//
//            case 2,3,4,5,6 -> {
//                CorpseTexture bloodyTexture = new CorpseTexture("corpse_bloody_" + villager,chosenSkin,bloodSplatter,clothingMask,bloodSplatterMask,true,true,0);
//                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, bloodyTexture.getLocation(),true, bSize, hSize, wSize, slim);
//            }
//
//            case 7,8,9,10,20 -> {
//                CorpseTexture cutOpenTexture = new CorpseTexture("corpse_cut_open_"+ villager ,chosenSkin,cutOpenBlood,cutOpenMask,cutOpenBloodMask,true,true,0);
//                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, cutOpenTexture.getLocation(),true, bSize, hSize, wSize, slim);
//            }
//
//            case 11,12,13 -> {
//                CorpseTexture bloodyTexture = new CorpseTexture("corpse_bloody_" + villager,chosenSkin,bloodSplatter,clothingMask,bloodSplatterMask,true,true,0);
//                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, bloodyTexture.getLocation(),false, bSize, hSize, wSize, slim);
//            }
//
//            case 14,15,16,17,19 -> {
//                CorpseTexture cutOpenTexture = new CorpseTexture("corpse_cut_open_"+ villager ,chosenSkin,cutOpenBlood,cutOpenMask,cutOpenBloodMask,true,true,0);
//                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, cutOpenTexture.getLocation(),false, bSize, hSize, wSize, slim);
//            }
//
//            case 18 -> {
//                CorpseTexture cutOpenTexture = new CorpseTexture("corpse_cut_open_"+ villager ,chosenSkin,cutOpenBlood,cutOpenMask,cutOpenBloodMask,true,true,0);
//                villagerBoobsModel.setVisible(false);
//                cutOpen(blockEntity, poseStack, bufferSource, packedLight, packedOverlay, cutOpenTexture.getLocation(),false, bSize, hSize, wSize, slim);
//            }
//
//
//        }
//
//
//
//
//    }
//
//    private void cutOpen(DrainedMCAVillagerCorpseBlockEntity blockEntity,PoseStack poseStack,MultiBufferSource bufferSource ,int packedLight, int packedOverlay, ResourceLocation resourceLocation,boolean hanging, Float bSize, Float hSize, Float wSize, Boolean slim) {
//        Direction facingInternals = blockEntity.getBlockState().getValue(DrainedMCAVillagerCorpseBlock.FACING);
//        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(resourceLocation));
//        if(hanging) {
//            poseStack.translate(0.5F, 0.7F, 0.5F);
//            poseStack.scale(wSize,hSize,wSize);
//            poseStack.translate(-0.5F, -0.7F, -0.5F);
//            poseStack.translate(0f,0.006f,0.0075f);
//            if (slim) {
//                slimHangingModel.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
//            } else {
//                hangingModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
//            }
//            poseStack.translate(0.5F, 1F, 0.5F);
//            poseStack.scale(1f + (0.23f * bSize),0.6f + (0.7f * bSize),0.6f + (0.7f * bSize));
//            poseStack.translate(-0.5f, -1F, -0.49f);
//            villagerBoobsModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
//        } else {
//            poseStack.translate(0.5F, 0F, 0.25F * wSize);
//            poseStack.scale(wSize,hSize,wSize);
//            poseStack.translate(-0.5F, 0F, -0.25F * wSize);
//            poseStack.translate(0f,0.006f,-0.0234f);
//            if (slim) {
//                slimModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
//            } else {
//                model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
//            }
//            poseStack.translate(0.5F, 0.8F, 0.5F);
//            poseStack.scale(1f + (0.23f * bSize),0.6f + (0.7f * bSize),0.6f + (0.7f * bSize));
//            poseStack.translate(-0.5f, -0.98F, -0.5f);
//            poseStack.translate(0f,0f, - 0.38f + (0.24f * bSize));
//            villagerBoobsModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
//        }
//        poseStack.popPose();
//        poseStack.pushPose();
//        if(hanging) {
//            poseStack.translate(0.5F, 0.7F, 0.5F);
//            poseStack.scale(wSize,hSize,wSize);
//            poseStack.translate(-0.5F, -0.7F, -0.5F);
//            poseStack.translate(0.5F, 0.7F, 0.5F);
//            poseStack.mulPose( Axis.YP.rotationDegrees(-facingInternals.toYRot()) );
//            poseStack.translate(-0.5F, -0.7F, -0.5F);
//            poseStack.scale(0.9f, 0.96f, 0.8f);
//            poseStack.translate(0.055F, 0.02F, 0.12F);
//        } else {
//
//            poseStack.translate(0.5F, 0F, 0.5F);
//            poseStack.mulPose( Axis.YP.rotationDegrees(-facingInternals.toYRot()) );
//            poseStack.translate(-0.5F, 0.0F, -0.5F);
//            poseStack.scale(0.9f, 0.96f, 0.8f);
//            poseStack.translate(0.555F, (0.25F * hSize) - 0.17F, 0.25F * wSize);
//            poseStack.scale(wSize,hSize,wSize);
//            poseStack.translate(-0.5F, 0.006f - (0.25F * hSize), - 0.2f -0.25F * wSize);
//
//        }
//        VertexConsumer internalsComsumer = bufferSource.getBuffer(RenderType.entityCutout(ORGANS_AND_BONES));
//        organsandBonesHangingModel.renderToBuffer(poseStack, internalsComsumer, packedLight, packedOverlay, 0xFFFFFFFF);
//        poseStack.popPose();
//
//    }
//

    }

    public static void renderDrainedBody(UUID villager, EntityModel<?> model, EntityModel<?> boobsModel, MultiBufferSource bufferSource, PoseStack poseStack, int packedLight, int packedOverlay, Float bSize, Float hSize, Float wSize, Integer face, int state, boolean hanging) {
        VertexConsumer vertexConsumer;

        if(hSize != null && wSize != null){
            poseStack.translate(0.5F,  hanging ? 0.7f : 0F,  hanging ? 0.5f : 0.25F * wSize);
            poseStack.scale(wSize,hSize,wSize);
            poseStack.translate(-0.5F, hanging ? -0.7f : 0F, hanging ? -0.5f : -0.25F * wSize);
            poseStack.translate(0f,0.006f, hanging ? 0.0075f : -0.0234f);

        }

        if(villager != null) {
            ResourceLocation skinTexture = ClientSkinCache.get(villager.toString());
            if(skinTexture == null) {
                if(!ClientSkinCache.hasRequested(villager.toString())) {
                    ClientSkinCache.markRequested(villager.toString());
                    MCAButcheryNetwork.requestVillagerSkin(villager);
                }
                vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(DEFAULT_TEXTURE));
                model.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
                poseStack.popPose();
                return;
            }
            ResourceLocation texture = ClientSkinCache.get(villager + "_drained_corpse_" + state);
            if(texture == null) {
                NativeImage image = ClientSkinCache.getImage(villager.toString());
                if(!ClientSkinCache.hasRequested(villager + "_drained_corpse_" + state)) {
                    ClientSkinCache.markRequested(villager + "_drained_corpse_" + state);
                    if(face != 0) {
                        NativeImage eyesImage = null;
                        try {
                            eyesImage = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID,"textures/entity/villager_eyes_" + face + ".png")).open());

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        image = CorpseTexture.blendEyes(ClientSkinCache.getImage(villager.toString()), eyesImage);
                    }
                    NativeImage image2 = image;
                    NativeImage image3 = image;
                    NativeImage image4 = image;
                    boolean blood = false;
                    switch (state) {
                        case 2,3,4,5,6,11,12,13 -> {
                            blood = true;
                            try {
                                image2 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(BLOOD_SPLATTER).open());
                                image3 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CLOTHING_MASK).open());
                                image4 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(BLOOD_SPLATTER_MASK).open());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case 7,8,9,10,14,15,16,17,19,20 -> {
                            blood = true;
                            try {
                                image2 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_BLOOD).open());
                                image3 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_MASK).open());
                                image4 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_BLOOD_MASK).open());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    CorpseTexture corpseTexture = new CorpseTexture(villager + "_drained_corpse_" + state, image, image2, image3, image4, blood, true, 0);
                    ClientSkinCache.put(villager + "_drained_corpse_" + state, corpseTexture.getLocation(), corpseTexture.getDynamicTexture().getPixels());
                }
                poseStack.popPose();
                return;
            } else {
                vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
                model.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
                poseStack.translate(0.5F, hanging ? 1f : 0.8F, 0.5F);
                poseStack.scale(1f + (0.23f * bSize),0.6f + (0.7f * bSize),0.6f + (0.7f * bSize));
                poseStack.translate(-0.5f, hanging ? -1f :-0.98F, hanging ? -0.49f : -0.88f + (0.24f * bSize));
//            poseStack.translate(0.5F, 1F, 0.5F);
//            poseStack.scale(1f + (0.23f * bSize),0.6f + (0.7f * bSize),0.6f + (0.7f * bSize));
//            poseStack.translate(-0.5f, -1F, -0.49f);
                boobsModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
            }


        } else {
            vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(DEFAULT_TEXTURE));
            model.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);

        }


        poseStack.popPose();
    }
}
