package com.aeternal.botaniverse.blocks;

import com.aeternal.botaniverse.api.state.BotaniverseStateProps;
import com.aeternal.botaniverse.api.state.enums.MorePoolVariant;
import com.aeternal.botaniverse.blocks.tiles.TileMorePool;
import com.aeternal.botaniverse.client.core.handler.ModelHandler;
import com.aeternal.botaniverse.common.block.BlockMod;
import com.aeternal.botaniverse.common.lib.LibBlockNames;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.lexicon.LexiconData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockMorePool extends BlockMod implements IWandHUD, IWandable, ILexiconable {
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);

    public BlockMorePool() {
        super(Material.ROCK, LibBlockNames.MOREPOOL);
        setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        BotaniaAPI.blacklistBlockFromMagnet(this, Short.MAX_VALUE);
        setDefaultState(blockState.getBaseState()
                .withProperty(BotaniverseStateProps.MOREPOOL_VARIANT, MorePoolVariant.NILFHEIM)
                .withProperty(BotaniverseStateProps.COLOR, EnumDyeColor.WHITE));
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BotaniverseStateProps.MOREPOOL_VARIANT, BotaniverseStateProps.COLOR);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BotaniverseStateProps.MOREPOOL_VARIANT).ordinal();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta > MorePoolVariant.values().length) {
            meta = 0;
        }
        return getDefaultState().withProperty(BotaniverseStateProps.MOREPOOL_VARIANT, MorePoolVariant.values()[meta]);
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        if (te instanceof TileMorePool) {
            return state.withProperty(BotaniverseStateProps.COLOR, ((TileMorePool) te).color);
        } else {
            return state.withProperty(BotaniverseStateProps.COLOR, EnumDyeColor.WHITE);
        }
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return AABB;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getBlock().getMetaFromState(state);
    }

    // If harvesting, delay setting block to air so getDrops can read the TE
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest)
            return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileMorePool && !((TileMorePool) te).fragile) {
            super.getDrops(drops, world, pos, state, fortune);
        }
    }

    // After getDrops reads the TE, then delete the block
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Override
    public void getSubBlocks(CreativeTabs par2, NonNullList<ItemStack> par3) {
        par3.add(new ItemStack(this, 1, 0));
        par3.add(new ItemStack(this, 1, 1));
        par3.add(new ItemStack(this, 1, 2));
        par3.add(new ItemStack(this, 1, 3));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileMorePool();
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if(entity instanceof EntityItem) {
            TileMorePool tile = (TileMorePool) world.getTileEntity(pos);
            if(tile.collideEntityItem((EntityItem) entity))
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
        }
    }

    private static final AxisAlignedBB BOTTOM_AABB = new AxisAlignedBB(0, 0, 0, 1, 1/16.0, 1);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0, 0, 15/16.0, 1, 0.5, 1);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1/16.0);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 0, 0, 1/16.0, 0.5, 1);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(15/16.0, 0, 0, 1, 0.5, 1);


    @Override
    public void addCollisionBoxToList(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> boxes, Entity entity, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, boxes, BOTTOM_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, NORTH_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, SOUTH_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, WEST_AABB);
        addCollisionBoxToList(pos, entityBox, boxes, EAST_AABB);
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileMorePool pool = (TileMorePool) world.getTileEntity(pos);
        return TileMorePool.calculateComparatorLevel(pool.getCurrentMana(), pool.manaCap);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
        ((TileMorePool) world.getTileEntity(pos)).renderHUD(mc, res);
    }

    @Override
    public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        ((TileMorePool) world.getTileEntity(pos)).onWanded(player, stack);
        return true;
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        return world.getBlockState(pos).getValue(BotaniverseStateProps.MOREPOOL_VARIANT) == MorePoolVariant.MUSPELHEIM ? LexiconData.rainbowRod : LexiconData.pool;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(BotaniverseStateProps.COLOR).build());
        ModelHandler.registerBlockToState(this, MorePoolVariant.values().length);
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
