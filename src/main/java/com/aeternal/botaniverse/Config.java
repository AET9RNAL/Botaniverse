package com.aeternal.botaniverse;

import net.minecraftforge.common.config.Configuration;

import java.io.File;


public final class Config {

    //Pools
    public static int PoolCapacityNilfheim;
    public static int PoolCapacityMuspelheim;
    public static int PoolCapacityAlfheim;
    public static int PoolCapacityAsgard;

    //Sparks
    public static int SparkNilfheimConductivity;
    public static int SparkMuspelheimConductivity;
    public static int SparkAlfheimConductivity;
    public static int SparkAsgardConductivity;

    public static int TransferIN_OUT_Global;
    public static byte NilfheimRange;
    public static byte MuspelheimRange;
    public static byte AlfheimRange;
    public static byte AsgardRange;


    public static boolean NilfheimPortalParticlesEnabled = true;
    public static void loadNormalConfig(final File configFile) {
        final Configuration config = new Configuration(configFile);
        try {
            config.load();

            //Category Pools
            config.setCategoryComment("Configuration Pools", "Change the capacites of the respective pools according to your needs. Don't put dumb values in here,You WILL crash!");
            //Capacity
            PoolCapacityNilfheim = config.getInt("PoolCapacityNilfheim", "Configuration Pools", 10000000, 0, Integer.MAX_VALUE, "Capacity for Nilfheim Pool");
            PoolCapacityMuspelheim = config.getInt("PoolCapacityMuspelheim", "Configuration Pools", 100000000, 0, Integer.MAX_VALUE, "Capacity for Muspelheim Pool");
            PoolCapacityAlfheim = config.getInt("PoolCapacityAlfheim", "Configuration Pools", 1000000000, 0, Integer.MAX_VALUE, "Capacity for Alfheim Pool");
            PoolCapacityAsgard = config.getInt("PoolCapacityAsgard", "Configuration Pools", Integer.MAX_VALUE, 0, Integer.MAX_VALUE, "Capacity for Asgard Pool");

            //Transfer rate to item
            TransferIN_OUT_Global = config.getInt("PoolTransferRateIN_OUT", "Configuration Pools", 10000, 0, Integer.MAX_VALUE, "This here changes how fast pools are able to transfer mana IN/OUT when charging or discharging mana items.(When you throw a mana tablet in the pool for ex.)This is a global rate, meaning it will affect each pool added by the mod.");
            //Category Sparks

            //Conductivity
            config.setCategoryComment("Configuration Sparks", "Change the parameters of sparks according to your needs. Don't put dumb values in here,You WILL crash!");
            SparkNilfheimConductivity = config.getInt("SparkNilfheimConductivity", "Configuration Sparks", 100000, 0, Integer.MAX_VALUE, "Conductivity of Nilfheim Spark");
            SparkMuspelheimConductivity = config.getInt("SparkMuspelheimConductivity", "Configuration Sparks", 250000, 0, Integer.MAX_VALUE, "Conductivity of Muspelheim Spark");
            SparkAlfheimConductivity = config.getInt("SparkAlfheimConductivity", "Configuration Sparks", 625000, 0, Integer.MAX_VALUE, "Conductivity of Alfheim Spark");
            SparkAsgardConductivity = config.getInt("SparkAsgardConductivity", "Configuration Sparks", 1562500, 0, Integer.MAX_VALUE, "Conductivity of Asgard Spark");

            //Range
            NilfheimRange = (byte) config.getInt("NilfheimRange", "Configuration Sparks", 18, 0, 127, "Range of Nilfheim Spark");
            MuspelheimRange = (byte) config.getInt("MuspelheimRange", "Configuration Sparks", 24, 0, 127, "Range of Muspelheim Spark");
            AlfheimRange  = (byte) config.getInt("AlfheimRange", "Configuration Sparks", 30, 0, 127, "Range of Alfheim Spark");
            AsgardRange = (byte) config.getInt("AsgardmRange", "Configuration Sparks", 36, 0, 127, "Range of Asgard Spark");
        } catch (Exception e) {
            Botaniverse.log.fatal("Fatal error reading config file.", e);
            throw new RuntimeException(e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

}





