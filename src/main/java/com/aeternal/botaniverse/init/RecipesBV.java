/*
package com.aeternal.botaniverse.init;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.common.block.ModVBlocks;
import com.zeitheron.hammercore.internal.SimpleRegistration;
import com.zeitheron.hammercore.utils.recipes.helper.RecipeRegistry;
import com.zeitheron.hammercore.utils.recipes.helper.RegisterRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.lib.LibOreDict;


@RegisterRecipes(modid = Constants.MOD_ID)
public class RecipesBV
        extends RecipeRegistry
{
    public static final ResourceLocation muspelheim_pool = new ResourceLocation(Constants.MOD_ID, "muspelheim_pool");


    @Override
    public void crafting()
    {
        ModContainer prev = Loader.instance().activeModContainer();
        Loader.instance().setActiveModContainer(getOwner());
        Loader.instance().setActiveModContainer(prev);
        recipe(SimpleRegistration.parseShapedRecipe(new ItemStack(ModVBlocks.morepool), "ddd", "dpd", "ttt", 'd', new ItemStack(LibOreDict.RUNE), 'p', new ItemStack(ModBlocks.pool), 't', LibOreDict.TERRASTEEL_NUGGET).setRegistryName(muspelheim_pool));

    }

    public static void init()
    {

    }
}

*/
