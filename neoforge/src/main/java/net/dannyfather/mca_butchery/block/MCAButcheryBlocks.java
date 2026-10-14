package net.dannyfather.mca_butchery.block;

import net.dannyfather.mca_butchery.MCAButchery;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.minecraft.world.item.Items.registerBlock;

public class MCAButcheryBlocks {

    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(MCAButchery.MOD_ID);

    public static final DeferredHolder<Block, Block> MCAVILLAGERCORPSE = REGISTRY.register("villager_corpse", MCAVillagerCorpseBlock::new);
    public static final DeferredHolder<Block, Block> DRAINEDMCAVILLAGERCORPSE = REGISTRY.register("drained_villager_corpse", DrainedMCAVillagerCorpseBlock::new);
    public static final DeferredHolder<Block, Block> MCAVILLAGERHEAD = REGISTRY.register("villager_head", MCAVillagerHeadBlock::new);
    public static final DeferredBlock<Block> IRON_SPIKE = REGISTRY.register("iron_spike", () -> new IronSpikeBlock(BlockBehaviour.Properties.of().strength(5f,6f).sound(SoundType.METAL).requiresCorrectToolForDrops().randomTicks()));



}
