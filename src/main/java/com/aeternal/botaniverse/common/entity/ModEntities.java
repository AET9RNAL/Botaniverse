package com.aeternal.botaniverse.common.entity;

import net.minecraftforge.fml.common.registry.EntityRegistry;
import com.aeternal.botaniverse.Botaniverse;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkAlfheim;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkAsgard;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkMuspelheim;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkNilfheim;
import com.aeternal.botaniverse.common.lib.LibEntityNames;

public final class ModEntities {

    public static void init() {
        int id = 0;

        EntityRegistry.registerModEntity(LibEntityNames.SPARK_NILFHEIM_REGISTRY, EntitySparkNilfheim.class, LibEntityNames.SPARK_NILFHEIM, id++, Botaniverse.instance, 64, 10, false);
        EntityRegistry.registerModEntity(LibEntityNames.SPARK_MUSPELHEIM_REGISTRY, EntitySparkMuspelheim.class, LibEntityNames.SPARK_MUSPELHEIM, id++, Botaniverse.instance, 64, 10, false);
        EntityRegistry.registerModEntity(LibEntityNames.SPARK_ALFHEIM_REGISTRY, EntitySparkAlfheim.class, LibEntityNames.SPARK_ALFHEIM, id++, Botaniverse.instance, 64, 10, false);
        EntityRegistry.registerModEntity(LibEntityNames.SPARK_ASGARD_REGISTRY, EntitySparkAsgard.class, LibEntityNames.SPARK_ASGARD, id++, Botaniverse.instance, 64, 10, false);
    }

}
