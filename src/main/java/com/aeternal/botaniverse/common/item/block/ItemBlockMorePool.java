package com.aeternal.botaniverse.common.item.block;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.api.state.enums.MorePoolVariant;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;


public class ItemBlockMorePool extends ItemBlockWithMetadataAndName {

    public ItemBlockMorePool(Block par2Block) {
        super(par2Block);
        addPropertyOverride(new ResourceLocation(Constants.MOD_ID, "full"), (stack, worldIn, entityIn) -> {
            boolean renderFull = stack.getItemDamage() == MorePoolVariant.NILFHEIM.ordinal() || stack.hasTagCompound() && stack.getTagCompound().getBoolean("RenderFull");
            return renderFull ? 1F : 0F;
        });
    }

    /*@SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack par1ItemStack, World world, @Nonnull List<String> stacks, @Nonnull ITooltipFlag flag) {
        if(par1ItemStack.getItemDamage() == 1)
            for(int i = 0; i < 2; i++)
                stacks.add(I18n.format("botaniversemisc.MuspelheimPool" + i));
    }*/


}
