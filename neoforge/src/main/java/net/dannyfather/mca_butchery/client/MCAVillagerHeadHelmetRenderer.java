package net.dannyfather.mca_butchery.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.block.entity.renderer.MCAVillagerHeadBlockEntityRenderer;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.dannyfather.mca_butchery.network.MCAButcheryNetwork;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class MCAVillagerHeadHelmetRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final MCAVillagerHeadModel<AbstractClientPlayer> model;

    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(MCAButchery.MOD_ID, "textures/entity/villager_corpse.png");

    public MCAVillagerHeadHelmetRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, MCAVillagerHeadModel<AbstractClientPlayer> model) {
        super(renderer);
        this.model = model;
    }


    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if(!stack.is(MCAButcheryItems.MCAVILLAGERHEAD.get())) {return;}

        UUID villager = stack.get(MCAButcheryItems.VILLAGER_UUID);
        Integer face = stack.get(MCAButcheryItems.FACE);
        Integer decayState = stack.get(MCAButcheryItems.DECAY_STATE);

        poseStack.pushPose();


        VertexConsumer vertexConsumer;
        poseStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
        poseStack.translate(0f,livingEntity.isCrouching() ? 0.25f : 0f,0f);
        poseStack.mulPose(Axis.XP.rotationDegrees((livingEntity.isVisuallyCrawling() || livingEntity.isSwimming()) ? -45 : headPitch));

        poseStack.scale(1.192F, 1.192F, 1.192F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180f));
        poseStack.translate(-0.5f,0f,-0.5f);


        MCAVillagerHeadBlockEntityRenderer.renderHead(villager,model, bufferSource,poseStack,packedLight, OverlayTexture.NO_OVERLAY, decayState, face );
    }
}
