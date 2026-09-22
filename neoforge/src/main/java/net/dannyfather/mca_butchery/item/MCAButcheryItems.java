package net.dannyfather.mca_butchery.item;

import com.mojang.serialization.Codec;
import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.MCAButcheryBlocks;
import net.dannyfather.mca_butchery.block.MCAVillagerHeadBlock;
import net.dannyfather.mca_butchery.client.MCAVillagerCorpseItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;


public class MCAButcheryItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(MCAButchery.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, MCAButchery.MOD_ID);

    public static final DeferredItem<Item> MCAVILLAGERCORPSE = block(MCAButcheryBlocks.MCAVILLAGERCORPSE, new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> DRAINEDMCAVILLAGERCORPSE = block(MCAButcheryBlocks.DRAINEDMCAVILLAGERCORPSE, new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> MCAVILLAGERHEAD = REGISTRY.register("villager_head",
            () -> new MCAVillagerHeadItem(MCAButcheryBlocks.MCAVILLAGERHEAD.get(), new Item.Properties()));

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,MCAButchery.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> VILLAGER_UUID =
            DATA_COMPONENTS.register("villager_uuid",
                    () -> DataComponentType.<UUID>builder().persistent(Codec.STRING.xmap(UUID::fromString,UUID::toString)).build()
            );

    private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, Item.Properties properties) {
        return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ItemsClientSideHandler {
        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {

        }
    }
}
