package net.dannyfather.mca_butchery.client;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

public class MCAButcheryClientBlockExtensions implements IClientBlockExtensions {

    @Override
    public boolean addDestroyEffects(BlockState state,Level level,BlockPos pos,ParticleEngine manager) {
        BlockState particleState = Blocks.NETHER_WART_BLOCK.defaultBlockState();

        RandomSource random = level.getRandom();

        for (int i = 0; i < 10; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            double vx = (random.nextDouble()) * 0.2;
            double vy = (random.nextDouble()) * 0.2;
            double vz = (random.nextDouble()) * 0.2;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK,particleState), x, y, z, vx, vy, vz);
        }

        return true;
    }



    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        if (!(target instanceof net.minecraft.world.phys.BlockHitResult hit)) {
            return false;
        }

        BlockState particleState = Blocks.NETHER_WART_BLOCK.defaultBlockState();

        RandomSource random = level.getRandom();
        Vec3 pos = hit.getLocation();
        net.minecraft.core.Direction face = hit.getDirection();

            double jitterX = (random.nextDouble() - 0.5) * 0.7;
            double jitterY = (random.nextDouble() - 0.5) * 0.7;
            double jitterZ = (random.nextDouble() - 0.5) * 0.7;

            // Keep particles on the hit face, with slight outward offset.
            double x = pos.x() + (face.getAxis() != net.minecraft.core.Direction.Axis.X ? jitterX : 0) + face.getStepX() * 0.01;
            double y = pos.y() + (face.getAxis() != net.minecraft.core.Direction.Axis.Y ? jitterY : 0) + face.getStepY() * 0.01;
            double z = pos.z() + (face.getAxis() != net.minecraft.core.Direction.Axis.Z ? jitterZ : 0) + face.getStepZ() * 0.01;

            // Small burst moving away from the block face.
            double vx = face.getStepX() * 0.05 + (random.nextDouble() - 0.5) * 0.04;
            double vy = face.getStepY() * 0.05 + (random.nextDouble() - 0.5) * 0.04;
            double vz = face.getStepZ() * 0.05 + (random.nextDouble() - 0.5) * 0.04;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, particleState), x, y, z, vx, vy, vz);

        return true;
    }





}
