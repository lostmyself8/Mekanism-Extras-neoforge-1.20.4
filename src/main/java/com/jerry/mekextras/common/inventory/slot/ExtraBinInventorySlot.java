package com.jerry.mekextras.common.inventory.slot;

import com.jerry.mekextras.common.item.block.ExtraItemBlockBin;
import com.jerry.mekextras.common.tier.BTier;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.inventory.IInventorySlot;
import mekanism.common.attachments.containers.AttachedInventorySlots;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.item.block.ItemBlockBin;
import mekanism.common.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@NothingNullByDefault
public class ExtraBinInventorySlot extends BasicInventorySlot {
    private static final Predicate<@NotNull ItemStack> validator = stack -> !(stack.getItem() instanceof ExtraItemBlockBin) && !(stack.getItem() instanceof ItemBlockBin);

    @Nullable
    public static ExtraBinInventorySlot getForStack(@NotNull ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ExtraItemBlockBin) {
            AttachedInventorySlots attachment = ContainerType.ITEM.getAttachment(stack);
            if (attachment != null) {
                List<IInventorySlot> slots = attachment.getInventorySlots(null);
                if (slots.size() == 1) {
                    IInventorySlot slot = slots.get(0);
                    if (slot instanceof ExtraBinInventorySlot binSlot) {
                        return binSlot;
                    }
                }
            }
        }
        return null;
    }

    public static ExtraBinInventorySlot create(@Nullable IContentsListener listener, BTier tier) {
        Objects.requireNonNull(tier, "Bin tier cannot be null");
        return new ExtraBinInventorySlot(listener, tier);
    }

    private final boolean isCreative;
    private ItemStack lockStack = ItemStack.EMPTY;

    private ExtraBinInventorySlot(@Nullable IContentsListener listener, BTier tier) {
        super(tier.getStorage(), alwaysTrueBi, alwaysTrueBi, validator, listener, 0, 0);
        isCreative = false;
        obeyStackLimit = false;
    }

    @Override
    public @NotNull ItemStack insertItem(@NotNull ItemStack stack, @NotNull Action action, @NotNull AutomationType automationType) {
        if (isEmpty()) {
            if (isLocked() && !ItemHandlerHelper.canItemStacksStack(lockStack, stack)) {
                // When locked, we need to make sure the correct item type is being inserted
                return stack;
            } else if (isCreative && action.execute() && automationType != AutomationType.EXTERNAL) {
                //If a player manually inserts into a creative bin, that is empty we need to allow setting the type,
                // Note: We check that it is not external insertion because an empty creative bin acts as a "void" for automation
                ItemStack simulatedRemainder = super.insertItem(stack, Action.SIMULATE, automationType);
                if (simulatedRemainder.isEmpty()) {
                    //If we are able to insert it then set perform the action of setting it to full
                    setStackUnchecked(stack.copyWithCount(getLimit(stack)));
                }
                return simulatedRemainder;
            }
        }
        return super.insertItem(stack, action.combine(!isCreative), automationType);
    }

    @Override
    public ItemStack extractItem(int amount, Action action, AutomationType automationType) {
        return super.extractItem(amount, action.combine(!isCreative), automationType);
    }

    @Override
    public int setStackSize(int amount, Action action) {
        return super.setStackSize(amount, action.combine(!isCreative));
    }

    @Nullable
    @Override
    public InventoryContainerSlot createContainerSlot() {
        return null;
    }

    public ItemStack getBottomStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        return current.copyWithCount(Math.min(getCount(), current.getMaxStackSize()));
    }

    public boolean setLocked(boolean lock) {
        if (isCreative || isLocked() == lock || (lock && isEmpty())) {
            return false;
        }
        lockStack = lock ? current.copyWithCount(1) : ItemStack.EMPTY;
        return true;
    }

    public void setLockStack(@NotNull ItemStack stack) {
        lockStack = stack.copyWithCount(1);
    }

    public boolean isLocked() {
        return !lockStack.isEmpty();
    }

    public ItemStack getRenderStack() {
        return isLocked() ? getLockStack() : getStack();
    }

    public ItemStack getLockStack() {
        return lockStack;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        if (isLocked()) {
            CompoundTag stackTag = new CompoundTag();
            lockStack.save(stackTag);
            nbt.put(NBTConstants.LOCK_STACK, stackTag);
        }
        return nbt;
    }

    @Override
    public boolean isCompatible(IInventorySlot other) {
        return super.isCompatible(other) && isLocked() == ((ExtraBinInventorySlot) other).isLocked();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        NBTUtils.setItemStackOrEmpty(nbt, NBTConstants.LOCK_STACK, s -> this.lockStack = s);
        super.deserializeNBT(nbt);
    }
}
