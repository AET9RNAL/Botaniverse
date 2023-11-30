package com.aeternal.botaniverse.common.crafting;

import com.aeternal.botaniverse.common.item.ModVItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.lib.LibOreDict;

public final class BotaniverseRuneRecipes {



    public static RecipeRuneAltar recipeNilfheimRune;
    public static RecipeRuneAltar recipeMuspelheimRune;
    public static RecipeRuneAltar recipeAlfheimRune;
    public static RecipeRuneAltar recipeAsgardRune;


    public static void init() {
        final int costTier1 = 8000;
        final int costTier2 = 12000;
        final int costTier3 = 16000;
        final int costTier4 = 20000;

        recipeNilfheimRune = BotaniaAPI.registerRuneAltarRecipe(new ItemStack(ModVItems.morerune, 2, 0), costTier1, LibOreDict.RUNE[13],LibOreDict.RUNE[0],LibOreDict.RUNE[7], LibOreDict.MANA_STEEL );
        recipeMuspelheimRune = BotaniaAPI.registerRuneAltarRecipe(new ItemStack(ModVItems.morerune, 2, 1), costTier2, LibOreDict.RUNE[13],LibOreDict.RUNE[6],LibOreDict.RUNE[1], new ItemStack(Blocks.MAGMA));
        recipeAlfheimRune = BotaniaAPI.registerRuneAltarRecipe(new ItemStack(ModVItems.morerune, 2, 2), costTier3, LibOreDict.RUNE[9],LibOreDict.RUNE[5],LibOreDict.RUNE[3], LibOreDict.ELEMENTIUM);
        recipeAsgardRune = BotaniaAPI.registerRuneAltarRecipe(new ItemStack(ModVItems.morerune, 2, 3), costTier4, LibOreDict.RUNE[15],LibOreDict.RUNE[3],LibOreDict.RUNE[6], LibOreDict.GAIA_INGOT);
    }
}
