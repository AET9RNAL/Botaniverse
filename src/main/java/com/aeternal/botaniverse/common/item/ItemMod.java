package com.aeternal.botaniverse.common.item;


import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.client.render.IModelRegister;
import com.aeternal.botaniverse.common.core.BotaniverseCreativeTab;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public abstract class ItemMod extends Item implements IModelRegister {

    public ItemMod(String name) {
        setCreativeTab(BotaniverseCreativeTab.INSTANCE);
        setRegistryName(new ResourceLocation(Constants.MOD_ID, name));
        setTranslationKey(name);

    }

    @Nonnull
    @Override
    public String getUnlocalizedNameInefficiently(@Nonnull ItemStack par1ItemStack) {
        return super.getUnlocalizedNameInefficiently(par1ItemStack).replaceAll("item\\.", "item." + Constants.PREFIX_MOD);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}