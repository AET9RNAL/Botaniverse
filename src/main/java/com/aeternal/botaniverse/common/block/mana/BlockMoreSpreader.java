package com.aeternal.botaniverse.common.block.mana;

import com.aeternal.botaniverse.api.state.BotaniverseStateProps;
import com.aeternal.botaniverse.api.state.enums.MoreSpreaderVariant;
import com.aeternal.botaniverse.client.core.handler.ModelHandler;
import com.aeternal.botaniverse.common.block.tile.TileMoreSpreader;
import com.aeternal.botaniverse.common.lib.LibBlockNames;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.mana.ILens;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.api.wand.IWireframeAABBProvider;
import com.aeternal.botaniverse.common.block.BlockMod;
import vazkii.botania.common.core.helper.InventoryHelper;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;

public class BlockMoreSpreader extends BlockMod implements IWandable, IWandHUD, /*ILexiconable,*/ IWireframeAABBProvider {

    public BlockMoreSpreader() {
        super(Material.WOOD, LibBlockNames.MORESPREADER);
        setHardness(2.0F);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(BotaniverseStateProps.MORE_SPREADER_VARIANT, MoreSpreaderVariant.NILFHEIM));
    }

    @Nonnull
    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BotaniverseStateProps.MORE_SPREADER_VARIANT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BotaniverseStateProps.MORE_SPREADER_VARIANT).ordinal();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta > MoreSpreaderVariant.values().length) {
            meta = 0;
        }
        return getDefaultState().withProperty(BotaniverseStateProps.MORE_SPREADER_VARIANT, MoreSpreaderVariant.values()[meta]);
    }

    @Override
    public void getSubBlocks(CreativeTabs par2, NonNullList<ItemStack> par3) {
        for(int i = 0; i < 5; i++)
            par3.add(new ItemStack(this, 1, i));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
        EnumFacing orientation = EnumFacing.getDirectionFromEntityLiving(pos, par5EntityLivingBase);
        TileMoreSpreader spreader = (TileMoreSpreader) world.getTileEntity(pos);
        world.setBlockState(pos, getStateFromMeta(par6ItemStack.getItemDamage()), 1 | 2);

        switch(orientation) {
            case DOWN:
                spreader.rotationY = -90F;
                break;
            case UP:
                spreader.rotationY = 90F;
                break;
            case NORTH:
                spreader.rotationX = 270F;
                break;
            case SOUTH:
                spreader.rotationX = 90F;
                break;
            case WEST:
                break;
            default:
                spreader.rotationX = 180F;
                break;
        }
    }

    @Override
    public int damageDropped(IBlockState par1) {
        return getMetaFromState(par1);
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing par6, float par7, float par8, float par9) {
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TileMoreSpreader))
            return false;

        TileMoreSpreader spreader = (TileMoreSpreader) tile;
        ItemStack lens = spreader.getItemHandler().getStackInSlot(0);
        ItemStack heldItem = player.getHeldItem(hand);
        boolean isHeldItemLens = !heldItem.isEmpty() && heldItem.getItem() instanceof ILens;
        boolean wool = !heldItem.isEmpty() && heldItem.getItem() == Item.getItemFromBlock(Blocks.WOOL);

        if(!heldItem.isEmpty())
            if(heldItem.getItem() == ModItems.twigWand)
                return false;

        if(lens.isEmpty() && isHeldItemLens) {
            if (!player.capabilities.isCreativeMode)
                player.setHeldItem(hand, ItemStack.EMPTY);

            spreader.getItemHandler().setStackInSlot(0, heldItem.copy());
            spreader.markDirty();
        } else if(!lens.isEmpty() && !wool) {
            ItemHandlerHelper.giveItemToPlayer(player, lens);
            spreader.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
            spreader.markDirty();
        }

        if(wool && spreader.paddingColor == -1) {
            spreader.paddingColor = heldItem.getItemDamage();
            heldItem.shrink(1);
            if(heldItem.isEmpty())
                player.setHeldItem(hand, ItemStack.EMPTY);
        } else if(heldItem.isEmpty() && spreader.paddingColor != -1 && lens.isEmpty()) {
            ItemStack pad = new ItemStack(Blocks.WOOL, 1, spreader.paddingColor);
            ItemHandlerHelper.giveItemToPlayer(player, pad);
            spreader.paddingColor = -1;
            spreader.markDirty();
        }

        return true;
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TileMoreSpreader))
            return;

        TileMoreSpreader inv = (TileMoreSpreader) tile;

        if(inv.paddingColor != -1) {
            net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.WOOL, 1, inv.paddingColor));
        }

        InventoryHelper.dropInventory(inv, world, state, pos);

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        ((TileMoreSpreader) world.getTileEntity(pos)).onWanded(player, stack);
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileMoreSpreader();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
        ((TileMoreSpreader) world.getTileEntity(pos)).renderHUD(mc, res);
    }

    /*@Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        MoreSpreaderVariant variant = world.getBlockState(pos).getValue(BotaniverseStateProps.MORE_SPREADER_VARIANT);
        return variant == MoreSpreaderVariant.NILFHEIM ? LexiconData.spreader : variant == MoreSpreaderVariant.REDSTONE ? LexiconData.redstoneSpreader : LexiconData.dreamwoodSpreader;
    }*/

    @Override
    public AxisAlignedBB getWireframeAABB(World world, BlockPos pos) {
        return FULL_BLOCK_AABB.offset(pos).shrink(1.0/16.0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelHandler.registerBlockToState(this, MoreSpreaderVariant.values().length);
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return BlockFaceShape.UNDEFINED;
    }
}
