package net.dannyfather.mca_butchery.block.entity;

import net.dannyfather.mca_butchery.item.MCAButcheryItems;
import net.mcreator.butchery.init.ButcheryModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
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

public class DrainedMCAVillagerCorpseBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    private NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
    private UUID villagerUUID;
    private String itemName;
    private Float bSize = 0f;
    private Float hSize = 1f;
    private Float wSize = 1f;

    public DrainedMCAVillagerCorpseBlockEntity(BlockPos position, BlockState state) {
        super(MCAButcheryBlockEntities.DRAINEDMCAVILLAGERCORPSE.get(), position, state);
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
        this.itemName = name;
        setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition,this.getBlockState(),this.getBlockState(),3);
        }
    }

    public String getItemName() {return this.itemName;}

    public void setBSize(float size) {
        this.bSize = size;
        setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition,this.getBlockState(),this.getBlockState(),3);
        }
    }

    public Float getBSize() {
        return this.bSize;
    }

    public void setHeight(float size) {
        this.hSize = size;
        setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition,this.getBlockState(),this.getBlockState(),3);
        }
    }

    public Float getHeight() {
        return this.hSize;
    }

    public void setWidth(float size) {
        this.wSize = size;
        setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition,this.getBlockState(),this.getBlockState(),3);
        }
    }

    public Float getWidth() {
        return this.wSize;
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(compound, lookupProvider);
        if (!this.tryLoadLootTable(compound))
            this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.stacks, lookupProvider);

        if (compound.contains("UUID")) {
            this.setVillager(compound.getUUID("UUID"));
        }

        if(compound.contains("CustomName")) {
            this.setItemName(compound.getString("CustomName"));
        }

        if(compound.contains("B_Size")) {
            this.setBSize(compound.getFloat("B_Size"));
        }

        if(compound.contains("H_Size")) {
            this.setHeight(compound.getFloat("H_Size"));
        }

        if(compound.contains("W_Size")) {
            this.setWidth(compound.getFloat("W_Size"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compound, lookupProvider);
        if (!this.trySaveLootTable(compound)) {
            ContainerHelper.saveAllItems(compound, this.stacks, lookupProvider);
        }

        if (this.getVillager() != null) {
            compound.putUUID("UUID", this.getVillager());
        }

        if (this.getItemName() != null) {
            compound.putString("CustomName", this.getItemName());
        }

        if(this.getBSize() != null) {
            compound.putFloat("B_Size", this.getBSize());
        }

        if(this.getHeight() != null) {
            compound.putFloat("H_Size", this.getHeight());
        }

        if(this.getWidth() != null) {
            compound.putFloat("W_Size", this.getWidth());
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        return this.saveWithFullMetadata(lookupProvider);
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.stacks)
            if (!itemstack.isEmpty())
                return false;
        return true;
    }

    @Override
    public Component getDefaultName() {
        return Component.literal("drained_villager_corpse");
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
        return Component.literal("Drained Human Corpse");
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

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(MCAButcheryItems.DRAINEDMCAVILLAGERCORPSE.get());

        if(this.getVillager() != null) {
            stack.set(MCAButcheryItems.VILLAGER_UUID,this.getVillager());
        }

        if(this.getItemName() != null) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(this.getItemName()).withStyle(style -> style.withItalic(false)));
        }

        if(this.getBSize() != null) {
            stack.set(MCAButcheryItems.B_SIZE, this.getBSize());
        }

        if(this.getHeight() != null) {
            stack.set(MCAButcheryItems.H_SIZE, this.getHeight());
        }

        if(this.getWidth() != null) {
            stack.set(MCAButcheryItems.W_SIZE, this.getWidth());
        }

        return stack;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack itemstack, Direction direction) {
        return true;
    }
}
