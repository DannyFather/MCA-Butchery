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

            double vx = (random.nextDouble() - 0.5) * 0.2;
            double vy = (random.nextDouble() - 0.5) * 0.2;
            double vz = (random.nextDouble() - 0.5) * 0.2;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK,particleState), x, y, z, vx, vy, vz);
        }

        return true;
    }



    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        BlockState particleState = Blocks.NETHER_WART_BLOCK.defaultBlockState();

        RandomSource random = level.getRandom();
        BlockPos pos = new BlockPos((int) target.getLocation().x,(int) target.getLocation().y,(int) target.getLocation().z - 1);

        for (int i = 0; i < 2; i++) {
            double scale = 0.0002;
            double x = pos.getX() + 0.5 + random.nextDouble() * scale;
            double y = pos.getY() + 0.1 + random.nextDouble() * scale;
            double z = pos.getZ() + 0.5 + random.nextDouble() * scale;

            double vx = (random.nextDouble() - 0.5) * scale;
            double vy = (random.nextDouble() - 0.5) * scale;
            double vz = (random.nextDouble() - 0.5) * scale;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK,particleState), x, y, z, vx, vy, vz);
        }
        return true;
    }





}
