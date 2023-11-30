package com.aeternal.botaniverse.common.block;

import com.aeternal.botaniverse.api.state.BotaniverseStateProps;
import com.aeternal.botaniverse.api.state.enums.MorePylonVariant;
import com.aeternal.botaniverse.client.core.handler.ModelHandler;
import com.aeternal.botaniverse.common.block.tile.TileMorePylon;
import com.aeternal.botaniverse.common.lib.LibBlockNames;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.common.lexicon.LexiconData;

import javax.annotation.Nonnull;

/*@Optional.Interface(modid = "thaumcraft", iface = "thaumcraft.api.crafting.IInfusionStabiliser", striprefs = true)*/
public class BlockMorePylon extends BlockMod implements ILexiconable/*, IInfusionStabiliser*/ {

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 21.0/16, 0.875);

    public BlockMorePylon() {
        super(Material.IRON, LibBlockNames.MOREPYLON);
        setHardness(5.5F);
        setSoundType(SoundType.METAL);
        setLightLevel(0.5F);
        setDefaultState(blockState.getBaseState().withProperty(BotaniverseStateProps.MORE_PYLON_VARIANT, MorePylonVariant.NILFHEIM));
        BotaniaAPI.blacklistBlockFromGaiaGuardian(this);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return AABB;
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BotaniverseStateProps.MORE_PYLON_VARIANT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BotaniverseStateProps.MORE_PYLON_VARIANT).ordinal();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta > MorePylonVariant.values().length) {
            meta = 0;
        }
        return getDefaultState().withProperty(BotaniverseStateProps.MORE_PYLON_VARIANT, MorePylonVariant.values()[meta]);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs par2, NonNullList<ItemStack> par3) {
        for(int i = 0; i < MorePylonVariant.values().length; i++)
            par3.add(new ItemStack(this, 1, i));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this || state.getValue(BotaniverseStateProps.MORE_PYLON_VARIANT) == MorePylonVariant.NILFHEIM) {
            return 8;
        } else {
            return 15;
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileMorePylon();
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        MorePylonVariant variant = world.getBlockState(pos).getValue(BotaniverseStateProps.MORE_PYLON_VARIANT);
        return variant == MorePylonVariant.NILFHEIM ? LexiconData.pylon : variant == MorePylonVariant.MUSPELHEIM ? LexiconData.alfhomancyIntro : LexiconData.gaiaRitual;
    }

    /*@Override
    public boolean canStabaliseInfusion(World world, BlockPos pos) {
        return ConfigHandler.enableThaumcraftStablizers;
    }*/

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        String[] names = { "nilfheim", "muspelheim", "alfheim", "asgard" };
        ModelHandler.registerCustomItemblock(this, names.length, i -> "pylon_" + names[i]);
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return BlockFaceShape.UNDEFINED;
    }
}
