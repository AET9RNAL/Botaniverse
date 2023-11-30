package com.aeternal.botaniverse.common.lib;

import com.aeternal.botaniverse.Constants;
import net.minecraft.util.ResourceLocation;


public final class LibEntityNames {

    public static final String SPARK_NILFHEIM = Constants.PREFIX_MOD + "spark";
    public static final String SPARK_MUSPELHEIM = Constants.PREFIX_MOD + "spark";
    public static final String SPARK_ALFHEIM = Constants.PREFIX_MOD + "spark";
    public static final String SPARK_ASGARD = Constants.PREFIX_MOD + "spark";
    public static final ResourceLocation SPARK_NILFHEIM_REGISTRY = makeName("spark_nilfheim");
    public static final ResourceLocation SPARK_MUSPELHEIM_REGISTRY = makeName("spark_muspelheim");
    public static final ResourceLocation SPARK_ALFHEIM_REGISTRY = makeName("spark_alfheim");
    public static final ResourceLocation SPARK_ASGARD_REGISTRY = makeName("spark_asgard");


    private static ResourceLocation makeName(String s) {
        return new ResourceLocation(Constants.MOD_ID, s);
    }

}
