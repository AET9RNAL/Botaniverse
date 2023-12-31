package com.aeternal.botaniverse.common.block.tile;

import com.aeternal.botaniverse.Botaniverse;
import com.aeternal.botaniverse.Config;
import com.aeternal.botaniverse.api.state.BotaniverseStateProps;
import com.aeternal.botaniverse.api.state.enums.AlfheimPortalState;
import com.aeternal.botaniverse.api.state.enums.MorePylonVariant;
import com.aeternal.botaniverse.common.block.ModVBlocks;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.helper.Vector3;

import java.util.Random;

public class TileMorePylon extends TileEntity implements ITickable {

    boolean activated = false;
    BlockPos centerPos;
    int ticks = 0;

    @Override
    public void update() {
        ++ticks;

        if(world.getBlockState(getPos()).getBlock() != ModVBlocks.morepylon)
            return;

        MorePylonVariant variant = world.getBlockState(getPos()).getValue(BotaniverseStateProps.MORE_PYLON_VARIANT);

        if(activated && world.isRemote) {
            if(world.getBlockState(centerPos).getBlock() != getBlockForMeta()
                    || variant == MorePylonVariant.NILFHEIM && (portalOff() || world.getBlockState(getPos().down()).getBlock() != ModBlocks.pool)) {
                activated = false;
                return;
            }

            Vector3 centerBlock = new Vector3(centerPos.getX() + 0.5, centerPos.getY() + 0.75 + (Math.random() - 0.5 * 0.25), centerPos.getZ() + 0.5);

            if(variant == MorePylonVariant.NILFHEIM) {
                if(Config.NilfheimPortalParticlesEnabled) {
                    double worldTime = ticks;
                    worldTime += new Random(pos.hashCode()).nextInt(1000);
                    worldTime /= 5;

                    float r = 0.75F + (float) Math.random() * 0.05F;
                    double x = pos.getX() + 0.5 + Math.cos(worldTime) * r;
                    double z = pos.getZ() + 0.5 + Math.sin(worldTime) * r;

                    Vector3 ourCoords = new Vector3(x, pos.getY() + 0.25, z);
                    centerBlock = centerBlock.subtract(new Vector3(0, 0.5, 0));
                    Vector3 movementVector = centerBlock.subtract(ourCoords).normalize().multiply(0.2);

                    Botania.proxy.wispFX(x, pos.getY() + 0.25, z, (float) Math.random() * 0.25F, 0.75F + (float) Math.random() * 0.25F, (float) Math.random() * 0.25F, 0.25F + (float) Math.random() * 0.1F, -0.075F - (float) Math.random() * 0.015F);
                    if(world.rand.nextInt(3) == 0)
                        Botania.proxy.wispFX(x, pos.getY() + 0.25, z, (float) Math.random() * 0.25F, 0.75F + (float) Math.random() * 0.25F, (float) Math.random() * 0.25F, 0.25F + (float) Math.random() * 0.1F, (float) movementVector.x, (float) movementVector.y, (float) movementVector.z);
                }
            } else {
                Vector3 ourCoords = Vector3.fromTileEntityCenter(this).add(0, 1 + (Math.random() - 0.5 * 0.25), 0);
                Vector3 movementVector = centerBlock.subtract(ourCoords).normalize().multiply(0.2);

                Block block = world.getBlockState(pos.down()).getBlock();
                if(block == ModBlocks.flower || block == ModBlocks.shinyFlower) {
                    int hex = world.getBlockState(pos.down()).getValue(BotaniaStateProps.COLOR).getColorValue();
                    int r = (hex & 0xFF0000) >> 16;
                    int g = (hex & 0xFF00) >> 8;
                    int b = hex & 0xFF;

                    if(world.rand.nextInt(4) == 0)
                        Botania.proxy.sparkleFX(centerBlock.x + (Math.random() - 0.5) * 0.5, centerBlock.y, centerBlock.z + (Math.random() - 0.5) * 0.5, r / 255F, g / 255F, b / 255F, (float) Math.random(), 8);

                    Botania.proxy.wispFX(pos.getX() + 0.5 + (Math.random() - 0.5) * 0.25, pos.getY() - 0.5, pos.getZ() + 0.5 + (Math.random() - 0.5) * 0.25, r / 255F, g / 255F, b / 255F, (float) Math.random() / 3F, -0.04F);
                    Botania.proxy.wispFX(pos.getX() + 0.5 + (Math.random() - 0.5) * 0.125, pos.getY() + 1.5, pos.getZ() + 0.5 + (Math.random() - 0.5) * 0.125, r / 255F, g / 255F, b / 255F, (float) Math.random() / 5F, -0.001F);
                    Botania.proxy.wispFX(pos.getX() + 0.5 + (Math.random() - 0.5) * 0.25, pos.getY() + 1.5, pos.getZ() + 0.5 + (Math.random() - 0.5) * 0.25, r / 255F, g / 255F, b / 255F, (float) Math.random() / 8F, (float) movementVector.x, (float) movementVector.y, (float) movementVector.z);
                }
            }
        }

        if(world.rand.nextBoolean() && world.isRemote)
            Botania.proxy.sparkleFX(pos.getX() + Math.random(), pos.getY() + Math.random() * 1.5, pos.getZ() + Math.random(), variant == MorePylonVariant.NILFHEIM ? 1F : 0.5F, variant == MorePylonVariant.MUSPELHEIM ? 1F : 0.5F, variant == MorePylonVariant.MUSPELHEIM ? 0.5F : 1F, (float) Math.random(), 2);
    }

    private Block getBlockForMeta() {
        return world.getBlockState(pos).getValue(BotaniverseStateProps.MORE_PYLON_VARIANT) == MorePylonVariant.NILFHEIM ? ModBlocks.enchanter : ModBlocks.alfPortal;
    }

        private boolean portalOff() {
        return world.getBlockState(centerPos).getBlock() != ModBlocks.alfPortal
                || world.getBlockState(centerPos).getValue(BotaniverseStateProps.ALFHEIMPORTAL_STATE) == AlfheimPortalState.OFF;
    }

}