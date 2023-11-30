package com.aeternal.botaniverse.common.block;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.common.core.BotaniverseCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.aeternal.botaniverse.client.core.handler.ModelHandler;
import com.aeternal.botaniverse.client.render.IModelRegister;


public abstract class BlockMod extends Block implements IModelRegister {

    public BlockMod(Material par2Material, String name) {
        super(par2Material);
        setTranslationKey(name);
        setRegistryName(new ResourceLocation(Constants.MOD_ID, name));
        if(registerInCreative())
            setCreativeTab(BotaniverseCreativeTab.INSTANCE);
    }

    protected boolean registerInCreative() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        if(Item.getItemFromBlock(this) != Items.AIR)
            ModelHandler.registerBlockToState(this, 0, getDefaultState());
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        super.eventReceived(state, world, pos, id, param);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }
}
