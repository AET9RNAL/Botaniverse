package com.aeternal.botaniverse.common.item;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.common.item.materials.ItemMoreRune;
import com.aeternal.botaniverse.common.item.moresparks.ItemSparkAlfheim;
import com.aeternal.botaniverse.common.item.moresparks.ItemSparkAsgard;
import com.aeternal.botaniverse.common.item.moresparks.ItemSparkMuspelheim;
import com.aeternal.botaniverse.common.item.moresparks.ItemSparkNilfheim;
import com.aeternal.botaniverse.common.lib.LibOreDict;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;


@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public final class ModVItems {
//Sparks
    public static final Item spark_nilfheim = new ItemSparkNilfheim();
    public static final Item spark_muspelheim = new ItemSparkMuspelheim();
    public static final Item spark_alfheim = new ItemSparkAlfheim();
    public static final Item spark_asgard = new ItemSparkAsgard();
    public static final Item morerune = new ItemMoreRune();


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt) {
        IForgeRegistry<Item> r = evt.getRegistry();
        r.register(spark_nilfheim);
        r.register(spark_muspelheim);
        r.register(spark_alfheim);
        r.register(spark_asgard);
        r.register(morerune);
        registerOreDictionary();
    }

    private static void registerOreDictionary() {

        for(int i = 0; i < 4; i++) {
            OreDictionary.registerOre(LibOreDict.MORERUNE[i], new ItemStack(morerune, 1, i));
        }


    }

    }

