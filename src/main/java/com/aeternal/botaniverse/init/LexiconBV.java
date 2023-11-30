package com.aeternal.botaniverse.init;


import com.aeternal.botaniverse.common.crafting.ModCraftingRecipes;
import net.minecraft.item.ItemStack;
import com.aeternal.botaniverse.common.block.ModVBlocks;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.common.lexicon.BLexiconCategory;
import vazkii.botania.common.lexicon.BasicLexiconEntry;
import vazkii.botania.common.lexicon.page.PageCraftingRecipe;
import vazkii.botania.common.lexicon.page.PageText;

public class LexiconBV
{
    public static LexiconCategory categoryBotaniverse;

    public static LexiconEntry muspelheim_pool;


    public static void init()
    {
        BotaniaAPI.addCategory(categoryBotaniverse = new BLexiconCategory("botaniverse", 6));

        // Entries

        // Additional

        // Muspelheim Mana Pool
        /*muspelheim_pool = new BasicLexiconEntry("bv_muspelheim_pool", categoryBotaniverse).setPriority().setLexiconPages(new PageText("1"), new PageCraftingRecipe("2", CraftingRecipes.recipePoolNilfheim));
        muspelheim_pool.setIcon(new ItemStack(ModVBlocks.morepool));*/

    }
}