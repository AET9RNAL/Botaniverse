package com.aeternal.botaniverse.common.item.materials;

import com.aeternal.botaniverse.client.core.handler.ModelHandler;
import com.aeternal.botaniverse.common.item.ItemMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.recipe.IFlowerComponent;
import com.aeternal.botaniverse.common.lib.LibItemNames;

import javax.annotation.Nonnull;

public class ItemMoreRune extends ItemMod implements IFlowerComponent {

    public ItemMoreRune() {
        super(LibItemNames.MORERUNE);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> stacks) {
        if(isInCreativeTab(tab)) {
            for(int i = 0; i < 4; i++)
                stacks.add(new ItemStack(this, 1, i));
        }
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        return getUnlocalizedNameLazy(stack) + stack.getItemDamage();
    }

    String getUnlocalizedNameLazy(ItemStack stack) {
        return super.getTranslationKey(stack);
    }

    @Override
    public int getParticleColor(ItemStack stack) {
        return 0xA8A8A8;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        String[] runeNames = { "nilfheim", "muspelheim", "alfheim", "asgard" };
        ModelHandler.registerItemMetas(this, runeNames.length, i -> "rune_" + runeNames[i]);
    }

}
