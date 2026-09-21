package net.dannyfather.mca_butchery.block;

import net.dannyfather.mca_butchery.MCAButchery;
import net.dannyfather.mca_butchery.block.entity.models.MCAVillagerHeadModel;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MCAButcheryBlocks {

    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(MCAButchery.MOD_ID);

    public static final DeferredHolder<Block, Block> MCAVILLAGERCORPSE = REGISTRY.register("villager_corpse", MCAVillagerCorpseBlock::new);
    public static final DeferredHolder<Block, Block> DRAINEDMCAVILLAGERCORPSE = REGISTRY.register("drained_villager_corpse", DrainedMCAVillagerCorpseBlock::new);
    public static final DeferredHolder<Block, Block> MCAVILLAGERHEAD = REGISTRY.register("villager_head", MCAVillagerHeadBlock::new);

}
