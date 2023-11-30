package com.aeternal.botaniverse.common.core;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.common.block.ModVBlocks;
import com.aeternal.botaniverse.common.item.ModVItems;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public final class BotaniverseCreativeTab extends CreativeTabs {

    public static final BotaniverseCreativeTab INSTANCE = new BotaniverseCreativeTab();

    NonNullList<ItemStack> list;

    public BotaniverseCreativeTab() {
        super(Constants.MOD_ID);
        setNoTitle();
        //setBackgroundImageName(LibResources.GUI_CREATIVE);
    }

    @Nonnull
    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(ModVBlocks.morepool);
    }

    @Override
    public void displayAllRelevantItems(@Nonnull NonNullList<ItemStack> list) {
        this.list = list;
        addItem(ModVItems.spark_nilfheim);
        addItem(ModVItems.spark_muspelheim);
        addItem(ModVItems.spark_alfheim);
        addItem(ModVItems.spark_asgard);
        addItem(ModVItems.morerune);
        addBlock(ModVBlocks.morepool);
        addBlock(ModVBlocks.morepylon);
        addBlock(ModVBlocks.morerock);
        addBlock(ModVBlocks.morewood);
    }

    private void addItem(Item item) {
        item.getSubItems(this, list);
    }

    private void addBlock(Block block) {
        ItemStack stack = new ItemStack(block);
        block.getSubBlocks(this, list);

    }

}