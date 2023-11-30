package com.aeternal.botaniverse.api.mana.spark;

import com.aeternal.botaniverse.Config;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import vazkii.botania.api.mana.spark.ISparkEntity;

import java.util.List;

public final class AdditionalSparkHelper {

    public static final int NILFHEIM_SPARK_SCAN_RANGE = Config.NilfheimRange;
    public static final int MUSPELHEIM_SPARK_SCAN_RANGE = Config.MuspelheimRange;
    public static final int ALFHEIM_SPARK_SCAN_RANGE = Config.AlfheimRange;
    public static final int ASGARD_SPARK_SCAN_RANGE = Config.AsgardRange;

    public static List<ISparkEntity> getSparksAroundNilfheim(World world, double x, double y, double z) {
        return AdditionalSparkHelper.getEntitiesAroundNilfheim(ISparkEntity.class, world, x, y, z);
    }

    public static <T> List<T> getEntitiesAroundNilfheim(Class<? extends T> clazz, World world, double x, double y, double z) {
        int r = NILFHEIM_SPARK_SCAN_RANGE;
        List entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r), Predicates.instanceOf(clazz));
        return entities;
    }
    public static List<ISparkEntity> getSparksAroundMuspelheim(World world, double x, double y, double z) {
        return AdditionalSparkHelper.getEntitiesAroundMuspelheim(ISparkEntity.class, world, x, y, z);
    }

    public static <T> List<T> getEntitiesAroundMuspelheim(Class<? extends T> clazz, World world, double x, double y, double z) {
        int r = MUSPELHEIM_SPARK_SCAN_RANGE;
        List entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r), Predicates.instanceOf(clazz));
        return entities;
    }
    public static List<ISparkEntity> getSparksAroundAlfheim(World world, double x, double y, double z) {
        return AdditionalSparkHelper.getEntitiesAroundAlfheim(ISparkEntity.class, world, x, y, z);
    }

    public static <T> List<T> getEntitiesAroundAlfheim(Class<? extends T> clazz, World world, double x, double y, double z) {
        int r = ALFHEIM_SPARK_SCAN_RANGE;
        List entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r), Predicates.instanceOf(clazz));
        return entities;
    }
    public static List<ISparkEntity> getSparksAroundAsgard(World world, double x, double y, double z) {
        return AdditionalSparkHelper.getEntitiesAroundAsgard(ISparkEntity.class, world, x, y, z);
    }

    public static <T> List<T> getEntitiesAroundAsgard(Class<? extends T> clazz, World world, double x, double y, double z) {
        int r = ASGARD_SPARK_SCAN_RANGE;
        List entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x - r, y - r, z - r, x + r, y + r, z + r), Predicates.instanceOf(clazz));
        return entities;
    }
}
