package net.dannyfather.mca_butchery.block;

import net.dannyfather.mca_butchery.block.entity.MCAVillagerHeadBlockEntity;
import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MCAVillagerHeadBlock extends Block implements EntityBlock {
    public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 15);

    public MCAVillagerHeadBlock() {
        super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1f, 10f).lightLevel(s -> (new Object() {
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
                default -> box(4, 4, 8, 12, 12, 16);
                case EAST -> box(0,4,4,8,12,12);
                case SOUTH -> box(4, 4, 0, 12, 12, 8);
                case WEST -> box(8,4,4,16,12,12);
            };
        }
        return box(4, 0, 4, 12, 8, 12);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, BLOCKSTATE, ROTATION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        float yaw = context.getRotation();

        int rotation = Mth.floor((yaw + 11.25f)/22.5f) & 15;

        return super.getStateForPlacement(context)
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(BLOCKSTATE, clickedFace.getAxis().isHorizontal() ? 1 : 0)
                .setValue(ROTATION, rotation);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MCAVillagerHeadBlockEntity(pos,state);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof MCAVillagerHeadBlockEntity head && !entity.getAbilities().instabuild) {
                UUID villager = head.getVillager();
                String itemName = head.getItemName();
                if (itemName == null) {
                    itemName = Component.translatable("block.mca_butchery.villager_head").getString();
                }
                if (world instanceof ServerLevel _level) {
                    MCAVillagerHeadBlockEntity headBE = new MCAVillagerHeadBlockEntity(pos, blockstate);
                    if (villager != null){headBE.setVillager(villager);}
                    headBE.setItemName(itemName);
                    ItemStack item = headBE.toItemStack();
                    Containers.dropItemStack(_level, pos.getX(), pos.getY(), pos.getZ(), item);
                }
            }
        }
        boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
        return retval;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof MCAVillagerHeadBlockEntity head) {
                UUID uuid = stack.get(MCAButcheryItems.VILLAGER_UUID);
                Component itemComponent = stack.get(DataComponents.CUSTOM_NAME);

                if (uuid != null) {
                    head.setVillager(uuid);
                }
                if (itemComponent != null) {
                    head.setItemName(itemComponent.getString());
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
