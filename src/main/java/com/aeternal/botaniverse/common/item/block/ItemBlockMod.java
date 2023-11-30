package com.aeternal.botaniverse.common.item.block;

import com.aeternal.botaniverse.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockMod extends ItemBlock {

    public ItemBlockMod(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    public String getUnlocalizedNameInefficiently(@Nonnull ItemStack par1ItemStack) {
        return getUnlocalizedNameInefficiently_(par1ItemStack).replaceAll("tile.", "tile." + Constants.PREFIX_MOD);
    }

    public String getUnlocalizedNameInefficiently_(ItemStack stack) {
        return super.getUnlocalizedNameInefficiently(stack);
    }
}
