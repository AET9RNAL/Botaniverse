package com.aeternal.botaniverse.common.item.moresparks;

import com.aeternal.botaniverse.common.entity.sparks.EntitySparkMuspelheim;
import com.aeternal.botaniverse.common.item.ItemMod;
import com.aeternal.botaniverse.common.lib.LibItemNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.spark.ISparkAttachable;


import javax.annotation.Nonnull;

public class ItemSparkMuspelheim extends ItemMod implements IManaGivingItem {

    public ItemSparkMuspelheim() {
        super(LibItemNames.SPARK_MUSPELHEIM);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xv, float yv, float zv) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof ISparkAttachable) {
            ISparkAttachable attach = (ISparkAttachable) tile;
            ItemStack stack = player.getHeldItem(hand);
            if(attach.canAttachSpark(stack) && attach.getAttachedSpark() == null) {
                if(!world.isRemote) {
                    stack.shrink(1);
                    EntitySparkMuspelheim spark = new EntitySparkMuspelheim(world);
                    spark.setPosition(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                    world.spawnEntity(spark);
                    attach.attachSpark(spark);
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }
}
