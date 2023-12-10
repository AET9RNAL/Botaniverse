package com.aeternal.botaniverse.common.block;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.blocks.BlockMorePool;
import com.aeternal.botaniverse.blocks.BlockMoreRock;
import com.aeternal.botaniverse.blocks.BlockMoreWood;
import com.aeternal.botaniverse.blocks.tiles.TileMorePool;
import com.aeternal.botaniverse.common.block.tile.TileMorePylon;
import com.aeternal.botaniverse.common.block.tile.TileMoreSpreader;
import com.aeternal.botaniverse.common.item.block.ItemBlockMorePool;
import com.aeternal.botaniverse.common.item.block.ItemBlockWithMetadataAndName;
import com.aeternal.botaniverse.common.lib.LibBlockNames;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraftforge.registries.IForgeRegistry;
import com.aeternal.botaniverse.common.block.mana.BlockMoreSpreader;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public final class ModVBlocks {
    public static final Block morespreader = new BlockMoreSpreader();
    public static final Block morepool = new BlockMorePool();
    public static final Block morepylon = new BlockMorePylon();
    public static final Block morerock = new BlockMoreRock();
    public static final Block morewood = new BlockMoreWood();


    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        IForgeRegistry<Block> r = evt.getRegistry();
        r.register(morespreader);
        r.register(morepool);
        r.register(morepylon);
        r.register(morerock);
        r.register(morewood);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> evt) {
        IForgeRegistry<Item> r = evt.getRegistry();
        r.register(new ItemBlockWithMetadataAndName(morespreader).setRegistryName(morespreader.getRegistryName()));
        r.register(new ItemBlockMorePool(morepool).setRegistryName(morepool.getRegistryName()));
        r.register(new ItemBlockWithMetadataAndName(morepylon).setRegistryName(morepylon.getRegistryName()));
        r.register(new ItemBlockWithMetadataAndName(morerock).setRegistryName(morerock.getRegistryName()));
        r.register(new ItemBlockWithMetadataAndName(morewood).setRegistryName(morewood.getRegistryName()));
        initTileEntities();
    }

    private static void initTileEntities() {

        registerTile(TileMoreSpreader.class, LibBlockNames.MORESPREADER);
        registerTile(TileMorePool.class, LibBlockNames.MOREPOOL);
        registerTile(TileMorePylon.class, LibBlockNames.MOREPYLON);
    }



    private static void registerTile(Class<? extends TileEntity> clazz, String key) {
        GameRegistry.registerTileEntity(clazz, Constants.PREFIX_MOD + key);
    }

}
