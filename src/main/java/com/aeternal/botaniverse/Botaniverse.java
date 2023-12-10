package com.aeternal.botaniverse;

import com.aeternal.botaniverse.client.render.tile.RenderTileMorePylon;
import com.aeternal.botaniverse.common.crafting.BotaniverseRuneRecipes;
import com.aeternal.botaniverse.common.crafting.ModCraftingRecipes;
import com.aeternal.botaniverse.common.entity.ModEntities;
import com.aeternal.botaniverse.compat.core.ICTCompat;
import com.aeternal.botaniverse.compat.crafttweaker.CraftTweakerCompat;
import com.aeternal.botaniverse.init.LexiconBV;
import com.aeternal.botaniverse.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION, dependencies = "required-after:hammercore;required-after:botania")

public class Botaniverse
{
    @Mod.Instance(Constants.MOD_ID)
    public static Botaniverse instance;
    public static Logger log;
    @SidedProxy(serverSide = "com.aeternal.botaniverse.proxy.CommonProxy", clientSide = "com.aeternal.botaniverse.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static final Logger LOG = LogManager.getLogger(Constants.MOD_ID);




    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(proxy);
        proxy.preInit();
        ModEntities.init();
        Config.loadNormalConfig(e.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init();
     //   RecipesBV.init();
        LexiconBV.init();
        BotaniverseRuneRecipes.init();
        ModCraftingRecipes.init();
        ICTCompat ictc = CraftTweakerCompat.compat();
        if(ictc != null) ictc.init();
    //    TileEntityItemStackRenderer.instance = new RenderTileMorePylon.ForwardingTEISR(TileEntityItemStackRenderer.instance);
    }


    @EventHandler
    public void postinit(FMLPostInitializationEvent e){
        proxy.postInit();

    }
    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent e)
    {
        ICTCompat ictc = CraftTweakerCompat.compat();
        if(ictc != null) ictc.onLoadComplete();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e)
    {
    }



}