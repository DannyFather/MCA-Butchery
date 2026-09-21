package net.dannyfather.mca_butchery.block.entity;

import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.stream.IntStream;

public class MCAVillagerHeadBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    private NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
    private UUID villagerUUID;
    private String customName;

    public MCAVillagerHeadBlockEntity(BlockPos position, BlockState state) {
        super(MCAButcheryBlockEntities.MCAVILLAGERHEAD.get(), position, state);
    }


    public void setVillager(UUID villager) {
        this.villagerUUID = villager;
        setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition,this.getBlockState(),this.getBlockState(),3);
        }
    }

    public UUID getVillager() {
        return this.villagerUUID;
    }

    public void setItemName(String name) {
        this.customName = name;
        setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition,this.getBlockState(),this.getBlockState(),3);
        }
    }

    public String getItemName() { return this.customName; }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(compound, lookupProvider);
        if (compound.contains("UUID")) {
            this.setVillager(compound.getUUID("UUID"));
        }

        if (compound.contains("CustomName")) {
            this.setItemName(compound.getString("CustomName"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compound, lookupProvider);

        if (this.getVillager() != null) {
            compound.putUUID("UUID", this.getVillager());
        }

        if (this.getItemName() != null) {
            compound.putString("CustomName", this.getItemName());
        }
    }


    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(MCAButcheryItems.MCAVILLAGERHEAD.get());

        if(this.getVillager() != null) {
            stack.set(MCAButcheryItems.VILLAGER_UUID,this.getVillager());
        }

        if(this.getItemName() != null) {
            stack.set(DataComponents.CUSTOM_NAME,Component.literal(this.getItemName()).withStyle(style -> style.withItalic(false)));
        }

        return stack;
    }



    @Override
    public Component getDefaultName() {
        return Component.literal("villager_head");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        return this.saveWithFullMetadata(lookupProvider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return ChestMenu.threeRows(id, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Human Head");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(0, this.getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemstack, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemstack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack itemstack, Direction direction) {
        return true;
    }
}
