package com.jerry.mekextras.common.item.block;

import com.jerry.mekextras.common.block.attribute.ExtraAttribute;
import com.jerry.mekextras.common.tier.BTier;
import com.jerry.mekextras.common.inventory.slot.ExtraBinInventorySlot;
import com.jerry.mekextras.common.block.basic.ExtraBlockBin;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.item.interfaces.IDroppableContents;
import mekanism.common.util.text.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExtraItemBlockBin extends ExtraItemBlockTooltip<ExtraBlockBin> implements IDroppableContents.IDroppableAttachmentContents {
    public ExtraItemBlockBin(ExtraBlockBin block) {
        super(block, new Item.Properties());
    }

    @Override
    public BTier getAdvanceTier() {
        return ExtraAttribute.getAdvanceTier(getBlock(), BTier.class);
    }

    @Override
    protected void addStats(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        ExtraBinInventorySlot slot = ExtraBinInventorySlot.getForStack(stack);
        BTier tier = getAdvanceTier();
        if (slot != null && tier != null) {
            if (slot.isEmpty()) {
                tooltip.add(MekanismLang.EMPTY.translateColored(EnumColor.DARK_RED));
            } else {
                tooltip.add(MekanismLang.STORING.translateColored(EnumColor.BRIGHT_GREEN, EnumColor.GRAY, slot.getStack()));
                tooltip.add(MekanismLang.ITEM_AMOUNT.translateColored(EnumColor.PURPLE, EnumColor.GRAY, TextUtils.format(slot.getCount())));
            }
            if (slot.isLocked()) {
                tooltip.add(MekanismLang.LOCKED.translateColored(EnumColor.AQUA, EnumColor.GRAY, slot.getLockStack()));
            }
            tooltip.add(MekanismLang.CAPACITY_ITEMS.translateColored(EnumColor.INDIGO, EnumColor.GRAY, TextUtils.format(tier.getStorage())));
        }
    }

    @Override
    public boolean canContentsDrop(ItemStack stack) {
        return true;
    }
}
