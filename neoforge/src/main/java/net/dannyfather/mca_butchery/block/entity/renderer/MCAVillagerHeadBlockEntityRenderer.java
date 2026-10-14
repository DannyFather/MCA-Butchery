package net.dannyfather.mca_butchery.block.entity.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.MCAVillagerHeadBlock;
import net.dannyfather.mca_butchery.block.entity.MCAVillagerHeadBlockEntity;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.client.ClientSkinCache;
import net.dannyfather.mca_butchery.client.CorpseTexture;
import net.dannyfather.mca_butchery.config.MCAButcheryCommonConfig;
import net.dannyfather.mca_butchery.network.MCAButcheryClientNetwork;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class MCAVillagerHeadBlockEntityRenderer implements BlockEntityRenderer<MCAVillagerHeadBlockEntity> {

    private final MCAVillagerHeadModel model;
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");
    private static final ResourceLocation BLOOD_SPLATTER = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/blood_splatter.png");
    private static final ResourceLocation BLOOD_SPLATTER_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/blood_splatter_mask.png");
    private static final ResourceLocation CUT_OPEN_BLOOD_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_blood_mask.png");
    private static final ResourceLocation CUT_OPEN_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/cut_open_mask.png");
    private static final ResourceLocation CLOTHING_MASK = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/clothing_mask.png");
    private static final ResourceLocation DECAYED_SKULL = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/decayed_skull.png");

    public MCAVillagerHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new MCAVillagerHeadModel(context.bakeLayer(MCAVillagerHeadModel.LAYER_LOCATION));
    }

    @Override
    public void render(MCAVillagerHeadBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Direction facing = blockEntity.getBlockState().getValue(MCAVillagerHeadBlock.FACING);
        int state = blockEntity.getBlockState().getValue(MCAVillagerHeadBlock.BLOCKSTATE);
        int rotation = blockEntity.getBlockState().getValue(MCAVillagerHeadBlock.ROTATION);
        UUID villager = blockEntity.getVillager();
        Float hSize = blockEntity.getHeight();
        Float wSize = blockEntity.getWidth();
        Integer face = blockEntity.getFace();
        Integer decayState = blockEntity.getDecayState();


        poseStack.pushPose();

        switch (state) {

            case 0 -> {
                poseStack.translate(0.5F, 0.0F, 0.5F);
                poseStack.mulPose( Axis.YP.rotationDegrees(180f - rotation * 22.5f ) );
                poseStack.scale(wSize,hSize,wSize);
                poseStack.translate(-0.5F, 0.0F, -0.5F);
            }

            case 1 -> {

                poseStack.translate(0.5F, 0.0F, 0.5F);
                poseStack.mulPose( Axis.YP.rotationDegrees(-facing.toYRot()) );
                poseStack.translate(0F, 0.5F, -0.5F);
                poseStack.scale(wSize,hSize,wSize);
                poseStack.translate(-0.5f,-0.25f,-0.25f);
            }

        }
        renderHead(villager, model, bufferSource, poseStack, packedLight, packedOverlay, decayState, face);

    }

    public static void renderHead(UUID villager, MCAVillagerHeadModel<?> model, MultiBufferSource bufferSource, PoseStack poseStack, int packedLight, int packedOverlay, Integer decayState, Integer face) {
        VertexConsumer vertexConsumer;
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

            int dState = 0;
            if(decayState != null) {
                dState = decayState;
            }
            ResourceLocation texture = ClientSkinCache.get(villager + "_decay_" + dState);
            if(texture == null) {
                NativeImage image = ClientSkinCache.getImage(villager.toString());
                if(!ClientSkinCache.hasRequested(villager + "_decay_" + dState)) {
                    ClientSkinCache.markRequested(villager + "_decay_" + dState);
                    if(face != 0 && (decayState != null && decayState > 0 || MCAButcheryCommonConfig.GREY_EYES.get())) {
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
                    boolean drained = false;
                    switch (dState) {
                        case 1 -> {
                            drained = true;
                        }
                        case 2 -> {
                            drained = true;
                            blood = true;
                            try {
                                image2 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(BLOOD_SPLATTER).open());
                                image3 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CLOTHING_MASK).open());
                                image4 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(BLOOD_SPLATTER_MASK).open());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case 3 -> {
                            drained = true;
                            blood = true;
                            try {
                                image2 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(DECAYED_SKULL).open());
                                image3 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_MASK).open());
                                image4 = NativeImage.read(Minecraft.getInstance().getResourceManager().getResourceOrThrow(CUT_OPEN_BLOOD_MASK).open());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    CorpseTexture corpseTexture = new CorpseTexture(villager + "_decay_" + dState, image, image2, image3, image4, blood, drained, dState);
                    ClientSkinCache.put(villager + "_decay_" + dState, corpseTexture.getLocation(), corpseTexture.getDynamicTexture().getPixels());
                }
                poseStack.popPose();
                return;
            } else {
                vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
                model.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);
            }


        } else {
            vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(DEFAULT_TEXTURE));
            model.renderToBuffer(poseStack, vertexConsumer,packedLight,packedOverlay,0xFFFFFFFF);

        }
        poseStack.popPose();
    }
}


