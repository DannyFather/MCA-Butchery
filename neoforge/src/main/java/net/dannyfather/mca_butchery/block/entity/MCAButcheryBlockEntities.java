package net.dannyfather.mca_butchery.block.entity;

import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.MCAButcheryBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MCAButcheryBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MCAButchery.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MCAVillagerCorpseBlockEntity>> MCAVILLAGERCORPSE = register("villager_corpse", MCAButcheryBlocks.MCAVILLAGERCORPSE, MCAVillagerCorpseBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DrainedMCAVillagerCorpseBlockEntity>> DRAINEDMCAVILLAGERCORPSE = register("drained_villager_corpse", MCAButcheryBlocks.DRAINEDMCAVILLAGERCORPSE, DrainedMCAVillagerCorpseBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MCAVillagerHeadBlockEntity>> MCAVILLAGERHEAD = register("villager_head", MCAButcheryBlocks.MCAVILLAGERHEAD, MCAVillagerHeadBlockEntity::new);

    // Start of user code block custom block entities
    // End of user code block custom block entities
    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<T> supplier) {
        return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MCAVILLAGERCORPSE.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DRAINEDMCAVILLAGERCORPSE.get(), SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MCAVILLAGERHEAD.get(), SidedInvWrapper::new);
    }
}
