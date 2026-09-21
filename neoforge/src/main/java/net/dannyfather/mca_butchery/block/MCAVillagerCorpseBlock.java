package net.dannyfather.mca_butchery.block;

import net.dannyfather.mca_butchery.block.entity.MCAVillagerCorpseBlockEntity;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.dannyfather.mca_butchery.procedures.MCAVillagercorpsebleedingProcedure;
import net.dannyfather.mca_butchery.procedures.MCAVillagercorpsebrokenProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MCAVillagerCorpseBlock extends Block implements EntityBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public MCAVillagerCorpseBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.HONEY_BLOCK).strength(1f, 10f).lightLevel(s -> (new Object() {
            public int getLightLevel() {
                if (s.getValue(BLOCKSTATE) == 1)
                    return 0;
                return 0;
            }
        }.getLightLevel())).noOcclusion().isRedstoneConductor((bs, br, bp) -> false).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(BLOCKSTATE) == 1) {
            return switch (state.getValue(FACING)) {
                default -> box(4, 7.09215, 5.60072, 12, 19.09215, 9.60072);
                case NORTH -> box(4, 7.09215, 6.39928, 12, 19.09215, 10.39928);
                case EAST -> box(5.60072, 7.09215, 4, 9.60072, 19.09215, 12);
                case WEST -> box(6.39928, 7.09215, 4, 10.39928, 19.09215, 12);
            };
        }
        return switch (state.getValue(FACING)) {
            default -> box(4, 4.09215, 1.60072, 12, 16.09215, 5.60072);
            case NORTH -> box(4, 4.09215, 10.39928, 12, 16.09215, 14.39928);
            case EAST -> box(1.60072, 4.09215, 4, 5.60072, 16.09215, 12);
            case WEST -> box(10.39928, 4.09215, 4, 14.39928, 16.09215, 12);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, BLOCKSTATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof MCAVillagerCorpseBlockEntity corpse) {
                UUID villager = corpse.getVillager();
                String itemName = corpse.getItemName();
                if (itemName == null) {
                    itemName = Component.translatable("block.mca_butchery.villager_corpse").getString();
                }
                MCAVillagercorpsebrokenProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), blockstate, entity, villager, itemName);
            }
        }
        boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
        return retval;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockstate, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
        super.useWithoutItem(blockstate, world, pos, entity, hit);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        double hitX = hit.getLocation().x;
        double hitY = hit.getLocation().y;
        double hitZ = hit.getLocation().z;
        Direction direction = hit.getDirection();
        MCAVillagercorpsebleedingProcedure.execute(world, x, y, z, entity);
        return InteractionResult.SUCCESS;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MCAVillagerCorpseBlockEntity(pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MCAVillagerCorpseBlockEntity be) {
                Containers.dropContents(world, pos, be);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof MCAVillagerCorpseBlockEntity be)
            return AbstractContainerMenu.getRedstoneSignalFromContainer(be);
        else
            return 0;
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(Level level,BlockPos pos,BlockState state,@Nullable LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof MCAVillagerCorpseBlockEntity corpse) {
                UUID uuid = stack.get(MCAButcheryItems.VILLAGER_UUID);
                Component itemComponent = stack.get(DataComponents.CUSTOM_NAME);

                if (uuid != null) {
                    corpse.setVillager(uuid);
                }
                if (itemComponent != null) {
                    corpse.setItemName(itemComponent.getString());
                }
            }
        }
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {

        BlockState particleState = Blocks.NETHER_WART_BLOCK.defaultBlockState();

        RandomSource random = level.getRandom();

        Vec3 vecPos = entity.position();

        for (int i = 0; i < 2; i++) {
            double scale = 0.0002;
            double x = vecPos.x + random.nextDouble() * scale;
            double y = vecPos.y + random.nextDouble() * scale;
            double z = vecPos.z + random.nextDouble() * scale;

            double vx = (random.nextDouble() - 0.5) * scale;
            double vy = (random.nextDouble() - 0.5) * scale;
            double vz = (random.nextDouble() - 0.5) * scale;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, particleState), x, y, z, vx, vy, vz);
        }
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {

        BlockState particleState = Blocks.NETHER_WART_BLOCK.defaultBlockState();

        RandomSource random = level.getRandom();

        Vec3 vecPos = entity.position();

        for (int i = 0; i < 2; i++) {
            double scale = 0.0002;
            double x = vecPos.x + random.nextDouble() * scale;
            double y = vecPos.y + random.nextDouble() * scale;
            double z = vecPos.z + random.nextDouble() * scale;

            double vx = (random.nextDouble() - 0.5) * scale;
            double vy = (random.nextDouble() - 0.5) * scale;
            double vz = (random.nextDouble() - 0.5) * scale;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, particleState), x, y, z, vx, vy, vz);
        }
        return true;
    }

}
