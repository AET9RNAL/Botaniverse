package com.aeternal.botaniverse.client.render.tile;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.client.core.handler.ClientTickHandler;
import com.aeternal.botaniverse.client.model.ModelMoreSpreader;
import com.aeternal.botaniverse.common.block.ModVBlocks;
import com.aeternal.botaniverse.common.block.tile.TileMoreSpreader;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;


import javax.annotation.Nonnull;

import static vazkii.botania.client.gui.lexicon.GuiLexicon.texture;

public class RenderTileMoreSpreader extends TileEntitySpecialRenderer<TileMoreSpreader> {

    private static final ResourceLocation textureN = new ResourceLocation(Constants.MODEL_SPREADER_NILFHEIM);
    private static final ResourceLocation textureM = new ResourceLocation(Constants.MODEL_SPREADER_MUSPELHEIM);
    private static final ResourceLocation textureA = new ResourceLocation(Constants.MODEL_SPREADER_ALFHEIM);
    private static final ResourceLocation textureAS = new ResourceLocation(Constants.MODEL_SPREADER_ASGARD);
    private static final ResourceLocation textureC = new ResourceLocation(Constants.MODEL_SPREADER_CREATIVE);


    private static final ModelMoreSpreader model = new ModelMoreSpreader();

    @Override
    public void render(@Nonnull TileMoreSpreader morespreader, double d0, double d1, double d2, float ticks, int digProgress, float unused) {
        if(!morespreader.getWorld().isBlockLoaded(morespreader.getPos(), false)
                || morespreader.getWorld().getBlockState(morespreader.getPos()).getBlock() != ModVBlocks.morespreader)
            return;

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.translate(d0, d1, d2);

        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.rotate(morespreader.rotationX + 90F, 0F, 1F, 0F);
        GlStateManager.translate(0F, -1F, 0F);
        GlStateManager.rotate(morespreader.rotationY, 1F, 0F, 0F);
        GlStateManager.translate(0F, 1F, 0F);

        ResourceLocation r = morespreader.isNILFHEIM_SPREADER() ? textureN : morespreader.isMUSPELHEIM_SPREADER() ? textureM : morespreader.isALFHEIM_SPREADER() ? textureA : morespreader.isASGARD_SPREADER() ? textureAS : morespreader.isCREATIVE_SPREADER() ? textureC : texture;


        Minecraft.getMinecraft().renderEngine.bindTexture(r);
        GlStateManager.scale(1F, -1F, -1F);

        double time = ClientTickHandler.ticksInGame + ticks;


        model.render();
        GlStateManager.color(1F, 1F, 1F);

        GlStateManager.pushMatrix();
        double worldTicks = morespreader.getWorld() == null ? 0 : time;
        GlStateManager.rotate((float) worldTicks % 360, 0F, 1F, 0F);
        GlStateManager.translate(0F, (float) Math.sin(worldTicks / 20.0) * 0.05F, 0F);
        model.renderCube();
        GlStateManager.popMatrix();
        GlStateManager.scale(1F, -1F, -1F);
        ItemStack stack = morespreader.getItemHandler().getStackInSlot(0);

        if(!stack.isEmpty()) {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            stack.getItem();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -1F, -0.4675F);
            GlStateManager.rotate(180, 0, 0, 1);
            GlStateManager.rotate(180, 1, 0, 0);
            GlStateManager.scale(1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
            GlStateManager.popMatrix();
        }

        if(morespreader.paddingColor != -1) {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            IBlockState carpet = Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.byMetadata(morespreader.paddingColor));

            GlStateManager.translate(-0.5F, -0.5F, 0.5F);
            float f = 1 / 16F;

            GlStateManager.translate(0, -f - 0.001, 0);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(carpet, 1.0F);
            GlStateManager.translate(0, f + 0.001, 0);
            GlStateManager.rotate(-90, 0, 1, 0);

            GlStateManager.translate(-0.001, 0, 0);
            GlStateManager.rotate(270, 0, 0, 1);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(carpet, 1.0F);
            GlStateManager.translate(0, 0.001, 0);
            GlStateManager.rotate(-90, 0, 1, 0);

            GlStateManager.translate(0, 15 * f + 0.001, -0.001);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(carpet, 1.0F);
            GlStateManager.translate(0, -0.001, 0.001);
            GlStateManager.rotate(-90, 0, 1, 0);

            GlStateManager.translate(15 * f + 0.001, f, 0.001);
            GlStateManager.rotate(270, 0, 0, 1);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(carpet, 1.0F);
            GlStateManager.translate(-0.001, 0, -0.001);
            GlStateManager.rotate(-90, 0, 1, 0);

            GlStateManager.translate(-0.001, -1 + f + 0.001, -f + 0.001);
            GlStateManager.rotate(90, 1, 0, 0);
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(carpet, 1.0F);
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.popMatrix();
    }

}
