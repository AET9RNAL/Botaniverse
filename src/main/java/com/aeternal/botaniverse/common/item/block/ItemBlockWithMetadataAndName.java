package com.aeternal.botaniverse.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockWithMetadataAndName extends ItemBlockMod {

    public ItemBlockWithMetadataAndName(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack) + stack.getItemDamage();
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

}
