package net.dannyfather.mca_butchery.block;

import net.dannyfather.mca_butchery.block.entity.IronSpikeBlockEntity;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class IronSpikeBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty HASFENCEABOVE = IntegerProperty.create("hasfenceabove", 0, 1);
    public static final IntegerProperty HASHEAD = IntegerProperty.create("hashead", 0, 1);
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 15);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty EMBALMED = BooleanProperty.create("embalmed");

    public IronSpikeBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED,false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HASFENCEABOVE,HASHEAD,ROTATION,WATERLOGGED,EMBALMED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(state.getValue(HASHEAD) == 1 && level instanceof ServerLevel serverLevel && serverLevel.getBlockEntity(pos) instanceof IronSpikeBlockEntity pikeBlockEntity && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pikeBlockEntity.toItemStack());
                serverLevel.setBlock(pos, state.setValue(HASHEAD, 0), 3);

                return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.is(MCAButcheryItems.MCAVILLAGERHEAD.get())) {
            float yaw = player.getYRot();
            int rotation = Mth.floor((yaw + 11.25f)/22.5f) & 15;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof IronSpikeBlockEntity pikeBlockEntity) {

                BlockState block = level.getBlockState(pos);
                if(block.getValue(HASHEAD).equals(1)) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pikeBlockEntity.toItemStack());
                }

                if(stack.get(MCAButcheryItems.H_SIZE) != null) {
                    pikeBlockEntity.setHeight(stack.get(MCAButcheryItems.H_SIZE));
                }

                if(stack.get(MCAButcheryItems.W_SIZE) != null) {
                    pikeBlockEntity.setWidth(stack.get(MCAButcheryItems.W_SIZE));
                }

                if(stack.get(MCAButcheryItems.FACE) != null) {
                    pikeBlockEntity.setFace(stack.get(MCAButcheryItems.FACE));
                }

                if(stack.get(MCAButcheryItems.DECAY_STATE) != null) {
                    pikeBlockEntity.setDecayState(stack.get(MCAButcheryItems.DECAY_STATE));
                }

                if(stack.get(MCAButcheryItems.VILLAGER_UUID) != null) {
                    pikeBlockEntity.setVillager(stack.get(MCAButcheryItems.VILLAGER_UUID));
                }
                if(stack.get(DataComponents.CUSTOM_NAME) != null){
                    pikeBlockEntity.setItemName(Objects.requireNonNull(stack.get(DataComponents.CUSTOM_NAME)).getString());
                }
                level.setBlockEntity(pikeBlockEntity);

            }
            if(!player.getAbilities().instabuild){
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 1f, 1f);
            BlockState particleState = Blocks.NETHER_WART_BLOCK.defaultBlockState();

            RandomSource random = level.getRandom();

            for (int i = 0; i < 10; i++) {
                double scale = 0.0002;
                double x = pos.getX() + 0.5 + random.nextDouble() * scale;
                double y = pos.getY() + 0.5 + random.nextDouble() * scale;
                double z = pos.getZ() + 0.5 + random.nextDouble() * scale;

                double vx = (random.nextDouble() - 0.5) * scale;
                double vy = (random.nextDouble() - 0.5) * scale;
                double vz = (random.nextDouble() - 0.5) * scale;

                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, particleState), x, y, z, vx, vy, vz);
            }

            level.setBlock(pos,state.setValue(HASHEAD,1).setValue(ROTATION, rotation).setValue(EMBALMED,false),3);

            return ItemInteractionResult.CONSUME;
        } else if (stack.is(Items.SKELETON_SKULL.asItem())) {
            float yaw = player.getYRot();
            int rotation = Mth.floor((yaw + 11.25f)/22.5f) & 15;

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof IronSpikeBlockEntity pikeBlockEntity) {

                BlockState block = level.getBlockState(pos);
                if(block.getValue(HASHEAD).equals(1)) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pikeBlockEntity.toItemStack());
                }


                pikeBlockEntity.setDecayState(4);

                if(stack.get(DataComponents.CUSTOM_NAME) != null){
                    pikeBlockEntity.setItemName(Objects.requireNonNull(stack.get(DataComponents.CUSTOM_NAME)).getString());
                }
                level.setBlockEntity(pikeBlockEntity);

            }
            if(!player.getAbilities().instabuild){
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1f, 1f);
            level.setBlock(pos,state.setValue(HASHEAD,1).setValue(ROTATION, rotation),3);

            return ItemInteractionResult.CONSUME;
        }else if (stack.is(Items.HONEYCOMB.asItem()) && !state.getValue(EMBALMED)) {
            BlockState block = level.getBlockState(pos);
            level.setBlock(pos,block.setValue(EMBALMED,true),3);
            if(!player.getAbilities().instabuild){
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1f, 1f);
            RandomSource random = level.getRandom();
            for (int i = 0; i < 10; i++) {
                double scale = 1;
                double x = pos.getX() + random.nextDouble() * scale;
                double y = pos.getY() + 0.5 + random.nextDouble() * scale;
                double z = pos.getZ() + random.nextDouble() * scale;

                double vx = (random.nextDouble() - 0.5) * scale;
                double vy = (random.nextDouble() - 0.5) * scale;
                double vz = (random.nextDouble() - 0.5) * scale;

                level.addParticle(ParticleTypes.WAX_ON, x, y, z, vx, vy, vz);
            }
            return ItemInteractionResult.CONSUME;
        }else if (stack.is(ItemTags.AXES) && stack.isDamageableItem() && state.getValue(EMBALMED)) {
            BlockState block = level.getBlockState(pos);
            level.setBlock(pos,block.setValue(EMBALMED,false),3);
            level.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1f, 1f);
            RandomSource random = level.getRandom();
            for (int i = 0; i < 10; i++) {
                double scale = 1;
                double x = pos.getX() + random.nextDouble() * scale;
                double y = pos.getY() + 0.5 + random.nextDouble() * scale;
                double z = pos.getZ() + random.nextDouble() * scale;

                double vx = (random.nextDouble() - 0.5) * scale;
                double vy = (random.nextDouble() - 0.5) * scale;
                double vz = (random.nextDouble() - 0.5) * scale;

                level.addParticle(ParticleTypes.WAX_OFF, x, y, z, vx, vy, vz);
            }
            EquipmentSlot slot = stack.getEquipmentSlot();
            stack.hurtAndBreak(
                    1,
                    player, slot != null ? slot : EquipmentSlot.MAINHAND
            );
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        boolean hasFenceAbove = false;
        if (context.getLevel() instanceof ServerLevel level) {
            Block block = level.getBlockState(context.getClickedPos().above()).getBlock();
            if (!(block instanceof AirBlock || block instanceof LiquidBlock)) {
                hasFenceAbove = true;
            }
        }

        float yaw = context.getRotation();
        int rotation = Mth.floor((yaw + 11.25f)/22.5f) & 15;
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());

        BlockState blockState = super.getStateForPlacement(context);
        return blockState.setValue(HASFENCEABOVE, hasFenceAbove ? 1 : 0).setValue(ROTATION, rotation).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(EMBALMED,false);
    }
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if(level instanceof ServerLevel serverLevel) {
            if (neighborPos.equals(pos.above())) {
                Block block = serverLevel.getBlockState(pos.above()).getBlock();
                boolean hasFenceAbove = !(block instanceof AirBlock || block instanceof LiquidBlock);
                serverLevel.setBlock(pos, state.setValue(HASFENCEABOVE, hasFenceAbove ? 1 : 0), 3);
            } else if (neighborPos.equals(pos.below()) && (neighborBlock instanceof AirBlock || neighborBlock instanceof LiquidBlock)) {
                ItemStack item = new ItemStack(state.getBlock().asItem(),1);
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
                if(state.getValue(HASHEAD) == 1 && level.getBlockEntity(pos) instanceof IronSpikeBlockEntity pikeBlockEntity) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pikeBlockEntity.toItemStack());
                }
                level.levelEvent(2001, pos, Block.getId(state));
                level.destroyBlock(pos, false);
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if(level instanceof ServerLevel serverLevel) {
            if (direction == Direction.UP) {
                Block block = serverLevel.getBlockState(pos.above()).getBlock();
                boolean hasFenceAbove = !(block instanceof AirBlock || block instanceof LiquidBlock);
                serverLevel.setBlock(pos, state.setValue(HASFENCEABOVE, hasFenceAbove ? 1 : 0), 3);
            } else if(direction == Direction.DOWN && (neighborState.getBlock() instanceof AirBlock || neighborState.getBlock() instanceof LiquidBlock)) {
                ItemStack item = new ItemStack(state.getBlock().asItem(),1);
                Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item);
                if(state.getValue(HASHEAD) == 1 && level.getBlockEntity(pos) instanceof IronSpikeBlockEntity pikeBlockEntity) {
                    Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), pikeBlockEntity.toItemStack());
                }
                level.levelEvent(2001, pos, Block.getId(state));
                level.destroyBlock(pos, false);

            }
        }

        if(state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        boolean isEmpty =(level.getBlockState(pos.below()).isAir() || level.getBlockState(pos.below()).getBlock() instanceof LiquidBlock );
        return !isEmpty;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
        if (world instanceof ServerLevel level) {
            if(!entity.getAbilities().instabuild){
                ItemStack item = new ItemStack(blockstate.getBlock().asItem(),1);
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
            }
            if(blockstate.getValue(HASHEAD) == 1 && level.getBlockEntity(pos) instanceof IronSpikeBlockEntity pikeBlockEntity) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pikeBlockEntity.toItemStack());
            }
        }
        boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
        return retval;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(7, 0, 7, 9, 16.5, 9);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IronSpikeBlockEntity(pos,state);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(HASHEAD) == 1) {
            if(level.getBlockEntity(pos) instanceof IronSpikeBlockEntity blockEntity) {
                int decayState = blockEntity.getDecayState();
                if (random.nextInt(64) == 0 && decayState < 4 && !state.getValue(EMBALMED)) {
                    blockEntity.setDecayState(decayState + 1);
                }
            }
        }
    }
}
