package com.aeternal.botaniverse.client.core.handler;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.client.render.IModelRegister;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.Locale;
import java.util.Map;
import java.util.function.IntFunction;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Constants.MOD_ID)
public final class ModelHandler {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {
        OBJLoader.INSTANCE.addDomain(Constants.MOD_ID.toLowerCase(Locale.ROOT));

        for (Block block : Block.REGISTRY) {
            if (block instanceof IModelRegister) {
                ((IModelRegister) block).registerModels();
            }
        }

        for (Item item : Item.REGISTRY) {
            if (item instanceof IModelRegister) {
                ((IModelRegister) item).registerModels();
            }
        }
    }

    public static void registerItemAllMeta(Item item, int range) {
        registerItemMetas(item, range, i -> item.getRegistryName().getPath());
    }

    public static void registerItemAppendMeta(Item item, int maxExclusive, String loc) {
        registerItemMetas(item, maxExclusive, i -> loc + i);
    }

    public static void registerItemMetas(Item item, int maxExclusive, IntFunction<String> metaToName) {
        for (int i = 0; i < maxExclusive; i++) {
            ModelLoader.setCustomModelResourceLocation(
                    item, i,
                    new ModelResourceLocation(Constants.MOD_ID + ":" + metaToName.apply(i), "inventory")
            );
        }
    }

    private static final Map<IRegistryDelegate<Block>, IStateMapper> customStateMappers = ReflectionHelper.getPrivateValue(ModelLoader.class, null, "customStateMappers");
    private static final DefaultStateMapper fallbackMapper = new DefaultStateMapper();

    private static ModelResourceLocation getMrlForState(IBlockState state) {
        return customStateMappers
                .getOrDefault(state.getBlock().delegate, fallbackMapper)
                .putStateModelLocations(state.getBlock())
                .get(state);
    }

    public static void registerBlockToState(Block b, int meta, IBlockState state) {
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(b),
                meta,
                getMrlForState(state)
        );
    }

    public static void registerBlockToState(Block b, int maxExclusive) {
        for(int i = 0; i < maxExclusive; i++)
            registerBlockToState(b, i, b.getStateFromMeta(i));
    }


    public static void registerCustomItemblock(Block b, int maxExclusive, IntFunction<String> metaToPath) {
        Item item = Item.getItemFromBlock(b);
        for (int i = 0; i < maxExclusive; i++) {
            ModelLoader.setCustomModelResourceLocation(
                    item, i,
                    new ModelResourceLocation(Constants.MOD_ID + ":itemblock/" + metaToPath.apply(i), "inventory")
            );
        }
    }

    private ModelHandler() {}
}
