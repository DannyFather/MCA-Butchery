package net.dannyfather.mca_butchery.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class MCAVillagerHeadArmorStandRenderer extends RenderLayer<ArmorStand, ArmorStandArmorModel> {
    private final MCAVillagerHeadModel<ArmorStand> model;

    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");

    public MCAVillagerHeadArmorStandRenderer(ArmorStandRenderer renderer,MCAVillagerHeadModel<ArmorStand> model) {
        super(renderer);
        this.model = model;
    }


    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ArmorStand livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if(stack.is(MCAButcheryItems.MCAVILLAGERHEAD.get())) {

            UUID villager = stack.get(MCAButcheryItems.VILLAGER_UUID);
            poseStack.pushPose();


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
            poseStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));

            poseStack.scale(1.192F, 1.192F, 1.192F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180f));
            poseStack.translate(-0.5f, -0.0625f, -0.5f);

            model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

            poseStack.popPose();
        }
    }
}
