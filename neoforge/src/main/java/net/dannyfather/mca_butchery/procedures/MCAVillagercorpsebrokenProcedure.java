package net.dannyfather.mca_butchery.procedures;

import net.dannyfather.mca_butchery.block.entity.MCAVillagerCorpseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.UUID;

public class MCAVillagercorpsebrokenProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity, UUID uuid, String name) {
        if (entity == null)
            return;
        if ((blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip1 ? blockstate.getValue(_getip1) : -1) == 0
                || (blockstate.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _getip3 ? blockstate.getValue(_getip3) : -1) == 1) {
            if (!(entity instanceof Player _plr ? _plr.getAbilities().instabuild : false)) {
                if (world instanceof ServerLevel _level) {
                    BlockPos pos = new BlockPos((int) x,(int) y,(int) z);
                    MCAVillagerCorpseBlockEntity corpseBE = new MCAVillagerCorpseBlockEntity(pos, blockstate);
                    if (uuid != null) {
                        corpseBE.setVillager(uuid);
                    }
                    corpseBE.setItemName(name);
                    ItemStack item = corpseBE.toItemStack();
                    Containers.dropItemStack(_level, pos.getX(), pos.getY(), pos.getZ(), item);
                }
            }
        }
    }
}
