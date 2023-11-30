
package com.aeternal.botaniverse.common.crafting;

import com.aeternal.botaniverse.Constants;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public final class ModCraftingRecipes {


    public static ResourceLocation recipePoolNilfheim;
    public static ResourceLocation recipePoolMuspelheim;
    public static ResourceLocation recipePoolAlfheim;
    public static ResourceLocation recipePoolAsgard;
    public static ResourceLocation recipeSparkNilfheim;
    public static ResourceLocation recipeSparkMuspelheim;
    public static ResourceLocation recipeSparkAlfheim;
    public static ResourceLocation recipeSparkAsgard;


    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> evt) {
        IForgeRegistry<IRecipe> r = evt.getRegistry();


    }
// = path("");
    public static void init() {
        // Can't do this in RegistryEvent.Register event handler since it seems JSON recipes aren't loaded yet
      //  recipesSpreader = allOfGroup(ModBlocks.spreader.getRegistryName());

        recipePoolNilfheim = path("morepool_0");
        recipePoolMuspelheim = path("morepool_1");
        recipePoolAlfheim = path("morepool_2");
        recipePoolAsgard = path("morepool_3");
        recipeSparkNilfheim = path("morespark_0");
        recipeSparkMuspelheim = path("morespark_1");
        recipeSparkAlfheim = path("morespark_2");
        recipeSparkAsgard = path("morespark_3");
    }



    private static ResourceLocation path(String path) {
        return new ResourceLocation(Constants.MOD_ID, path);
    }

    private static List<ResourceLocation> allOfGroup(ResourceLocation group) {
        String jsonGroup = group.toString();

        return ForgeRegistries.RECIPES.getEntries().stream()
                .filter(e -> jsonGroup.equals(e.getValue().getGroup()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
