package com.aeternal.botaniverse.blocks;

import com.aeternal.botaniverse.api.state.BotaniverseStateProps;
import com.aeternal.botaniverse.api.state.enums.MoreRockVariant;
import com.aeternal.botaniverse.api.state.enums.MoreWoodVariant;
import com.aeternal.botaniverse.common.block.BlockMod;
import com.aeternal.botaniverse.common.lib.LibBlockNames;
import com.aeternal.botaniverse.client.core.handler.ModelHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.common.lexicon.LexiconData;


import javax.annotation.Nonnull;

public class BlockMoreWood extends BlockMod implements ILexiconable {

    public BlockMoreWood() {
        super(Material.WOOD, LibBlockNames.MOREWOOD);
        setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(BotaniverseStateProps.MORE_WOOD_VARIANT, MoreWoodVariant.NILFHEIM));
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BotaniverseStateProps.MORE_WOOD_VARIANT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BotaniverseStateProps.MORE_WOOD_VARIANT).ordinal();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta >= MoreRockVariant.values().length) {
            meta = 0;
        }
        return getDefaultState().withProperty(BotaniverseStateProps.MORE_WOOD_VARIANT,MoreWoodVariant.values()[meta]);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> stacks) {
        for(int i = 0; i < MoreWoodVariant.values().length; i++)
            stacks.add(new ItemStack(this, 1, i));
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, getMetaFromState(state));
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        boolean isDefaultVariant = world.getBlockState(pos).getValue(BotaniverseStateProps.MORE_WOOD_VARIANT) == MoreWoodVariant.NILFHEIM;
        return isDefaultVariant ? LexiconData.pureDaisy : LexiconData.decorativeBlocks;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelHandler.registerBlockToState(this, MoreRockVariant.values().length);
    }

}
