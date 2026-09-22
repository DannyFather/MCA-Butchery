package net.dannyfather.mca_butchery;

import com.mojang.logging.LogUtils;
import net.dannyfather.mca_butchery.block.MCAButcheryBlocks;
import net.dannyfather.mca_butchery.block.entity.MCAButcheryBlockEntities;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerCorpseHangingModel;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerCorpseModel;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.dannyfather.mca_butchery.block.entity.models.OrgansandBonesHangingModel;
import net.dannyfather.mca_butchery.block.entity.renderer.DrainedMCAVillagercorpseBlockEntityRenderer;
import net.dannyfather.mca_butchery.block.entity.renderer.MCAVillagerHeadBlockEntityRenderer;
import net.dannyfather.mca_butchery.block.entity.renderer.MCAVillagerCorpseBlockEntityRenderer;
import net.dannyfather.mca_butchery.client.*;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.mcreator.butchery.configuration.ButcheryconfigConfiguration;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import org.slf4j.Logger;

@Mod(MCAButchery.MOD_ID)
public class MCAButchery {

    public static final String MOD_ID = "mca_butchery";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MCAButchery(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::onRegisterPayloadHandlers);

        MCAButcheryItems.REGISTRY.register(modEventBus);
        MCAButcheryBlocks.REGISTRY.register(modEventBus);
        MCAButcheryBlockEntities.REGISTRY.register(modEventBus);

        MCAButcheryItems.DATA_COMPONENTS.register(modEventBus);
    }

    @SubscribeEvent
    public void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
    }

    @EventBusSubscriber(modid = MOD_ID,bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
    public static class ClientModEvents {

        private static MCAVillagerCorpseItemRenderer corpseItemRenderer;
        private static DrainedMCAVillagerCorpseItemRenderer drainedCorpseItemRenderer;
        private static MCAVillagerHeadItemRenderer headItemRenderer;

        @SubscribeEvent
        public static void registerLayerDefinitions(
                EntityRenderersEvent.RegisterLayerDefinitions event
        ) {
            event.registerLayerDefinition(
                    MCAVillagerCorpseModel.LAYER_LOCATION,
                    MCAVillagerCorpseModel::createBodyLayer
            );

            event.registerLayerDefinition(
                    MCAVillagerCorpseHangingModel.LAYER_LOCATION,
                    MCAVillagerCorpseHangingModel::createBodyLayer
            );

            event.registerLayerDefinition(
                    OrgansandBonesHangingModel.LAYER_LOCATION,
                    OrgansandBonesHangingModel::createBodyLayer
            );


            event.registerLayerDefinition(
                    MCAVillagerHeadModel.LAYER_LOCATION,
                    MCAVillagerHeadModel::createBodyLayer
            );
        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(
                    MCAButcheryBlockEntities.MCAVILLAGERCORPSE.get(),
                    MCAVillagerCorpseBlockEntityRenderer::new
            );

            event.registerBlockEntityRenderer(
                    MCAButcheryBlockEntities.DRAINEDMCAVILLAGERCORPSE.get(),
                    DrainedMCAVillagercorpseBlockEntityRenderer::new
            );

            event.registerBlockEntityRenderer(
                    MCAButcheryBlockEntities.MCAVILLAGERHEAD.get(),
                    MCAVillagerHeadBlockEntityRenderer::new
            );

        }



        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerItem(
                    new IClientItemExtensions() {
                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            if (corpseItemRenderer == null) {
                                corpseItemRenderer = new MCAVillagerCorpseItemRenderer(
                                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                                        Minecraft.getInstance().getEntityModels()
                                );
                            }

                            return corpseItemRenderer;
                        }
                    },
                    MCAButcheryItems.MCAVILLAGERCORPSE.get()
            );

            event.registerItem(
                    new IClientItemExtensions() {
                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            if (drainedCorpseItemRenderer == null) {
                                drainedCorpseItemRenderer = new DrainedMCAVillagerCorpseItemRenderer(
                                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                                        Minecraft.getInstance().getEntityModels()
                                );
                            }

                            return drainedCorpseItemRenderer;
                        }
                    },
                    MCAButcheryItems.DRAINEDMCAVILLAGERCORPSE.get()
            );

            event.registerItem(
                    new IClientItemExtensions() {
                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            if (headItemRenderer == null) {
                                headItemRenderer = new MCAVillagerHeadItemRenderer(
                                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                                        Minecraft.getInstance().getEntityModels()
                                );
                            }

                            return headItemRenderer;
                        }

                        @Override
                        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity,ItemStack itemStack,EquipmentSlot equipmentSlot,HumanoidModel<?> original) {
                            if (equipmentSlot == EquipmentSlot.HEAD) {
                                original.head.visible = false;
                            }

                            return original;
                        }
                    },
                    MCAButcheryItems.MCAVILLAGERHEAD.get()
            );

            event.registerBlock(new MCAButcheryClientBlockExtensions(),MCAButcheryBlocks.MCAVILLAGERCORPSE.get());
            event.registerBlock(new MCAButcheryClientBlockExtensions(),MCAButcheryBlocks.DRAINEDMCAVILLAGERCORPSE.get());
            event.registerBlock(new MCAButcheryClientBlockExtensions(),MCAButcheryBlocks.MCAVILLAGERHEAD.get());

        }


        @SubscribeEvent
        public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
            for (var skin : event.getSkins()) {
                if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {

                    playerRenderer.addLayer(new MCAVillagerHeadHelmetRenderer(playerRenderer,createHeadModel(event)));
                }
            }

            if (event.getRenderer(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer armorStandRenderer) {
                armorStandRenderer.addLayer(new MCAVillagerHeadArmorStandRenderer(armorStandRenderer,createHeadModel(event)));
            }
        }

        private static <T extends Entity> MCAVillagerHeadModel<T> createHeadModel(
                EntityRenderersEvent.AddLayers event) {

            return new MCAVillagerHeadModel<>(
                    event.getEntityModels().bakeLayer(
                            MCAVillagerHeadModel.LAYER_LOCATION
                    )
            );
        }
    }

}
