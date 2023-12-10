package com.aeternal.botaniverse.client.render.tile;

import com.aeternal.botaniverse.Constants;
import com.aeternal.botaniverse.api.state.BotaniverseStateProps;
import com.aeternal.botaniverse.api.state.enums.MorePylonVariant;
import com.aeternal.botaniverse.client.model.*;
import com.aeternal.botaniverse.common.block.ModVBlocks;
import com.aeternal.botaniverse.common.block.tile.TileMorePylon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.lexicon.multiblock.IMultiblockRenderHook;
import vazkii.botania.api.lexicon.multiblock.Multiblock;
import vazkii.botania.api.lexicon.multiblock.component.MultiblockComponent;
import com.aeternal.botaniverse.client.core.handler.MultiblockRenderHandler;
import vazkii.botania.client.core.helper.ShaderHelper;
import com.aeternal.botaniverse.client.core.handler.ClientTickHandler;
import javax.annotation.Nonnull;
import java.util.Random;


public class RenderTileMorePylon extends TileEntitySpecialRenderer<TileMorePylon> implements IMultiblockRenderHook {

    private static final ResourceLocation NILFHEIM_TEXTURE = new ResourceLocation(Constants.MODEL_PYLON_NILFHEIM);
    private static final ResourceLocation MUSPELHEIM_TEXTURE = new ResourceLocation(Constants.MODEL_PYLON_MUSPELHEIM);
    private static final ResourceLocation ALFHEIM_TEXTURE = new ResourceLocation(Constants.MODEL_PYLON_ALFHEIM);
    private static final ResourceLocation ASGARD_TEXTURE = new ResourceLocation(Constants.MODEL_PYLON_ASGARD);



    private final ModelPylonNilfheim nilfheimModel = new ModelPylonNilfheim();
    private final ModelPylonMuspelheim muspelheimModel = new ModelPylonMuspelheim();
    private final ModelPylonAlfheim alfheimModel = new ModelPylonAlfheim();
    private final ModelPylonAsgard asgardModel = new ModelPylonAsgard();

    // Overrides for when we call this TESR without an actual pylon
    private static MorePylonVariant forceVariant = MorePylonVariant.NILFHEIM;

    @Override
    public void render(@Nonnull TileMorePylon morepylon, double d0, double d1, double d2, float pticks, int digProgress, float unused) {
        boolean renderingItem = morepylon == RenderTileMorePylon.ForwardingTEISR.DUMMY;

        if(!renderingItem && (!morepylon.getWorld().isBlockLoaded(morepylon.getPos(), false) || morepylon.getWorld().getBlockState(morepylon.getPos()).getBlock() != ModVBlocks.morepylon))
            return;

        renderMorePylon(morepylon, d0, d1, d2, pticks, renderingItem);
    }

    private void renderMorePylon(@Nonnull TileMorePylon morepylon, double d0, double d1, double d2, float pticks, boolean renderingItem) {
        MorePylonVariant type = renderingItem ? forceVariant : ModVBlocks.morepylon.getStateFromMeta(morepylon.getBlockMetadata()).getValue(BotaniverseStateProps.MORE_PYLON_VARIANT);
        IPylonModel model;
        switch(type) {
            default:
            case NILFHEIM: {
                model = nilfheimModel;
                Minecraft.getMinecraft().renderEngine.bindTexture(NILFHEIM_TEXTURE);
                break;
            }
            case MUSPELHEIM: {
                model = muspelheimModel;
                Minecraft.getMinecraft().renderEngine.bindTexture(MUSPELHEIM_TEXTURE);
                break;
            }
            case ALFHEIM: {
                model = alfheimModel;
                Minecraft.getMinecraft().renderEngine.bindTexture(ALFHEIM_TEXTURE);
                break;
            }
            case ASGARD: {
                model = asgardModel;
                Minecraft.getMinecraft().renderEngine.bindTexture(ASGARD_TEXTURE);
                break;
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        float a = MultiblockRenderHandler.rendering ? 0.6F : 1F;
        GlStateManager.color(1F, 1F, 1F, a);

        double worldTime = (double) (ClientTickHandler.ticksInGame + pticks);

        worldTime += renderingItem ? 0 : new Random(morepylon.getPos().hashCode()).nextInt(360);

        GlStateManager.translate(d0, d1 + (renderingItem ? 1.35 : 1.5), d2);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 0F, -0.5F);
        if(!renderingItem)
            GlStateManager.rotate((float) worldTime * 1.5F, 0F, 1F, 0F);

        model.renderRing();
        if(!renderingItem)
            GlStateManager.translate(0D, Math.sin(worldTime / 20D) / 20 - 0.025, 0D);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        if(!renderingItem)
            GlStateManager.translate(0D, Math.sin(worldTime / 20D) / 17.5, 0D);

        GlStateManager.translate(0.5F, 0F, -0.5F);
        if(!renderingItem)
            GlStateManager.rotate((float) -worldTime, 0F, 1F, 0F);

        GlStateManager.disableCull();
        GlStateManager.disableAlpha();

        if(!renderingItem)
            ShaderHelper.useShader(ShaderHelper.pylonGlow);
        model.renderCrystal();
        if(!renderingItem)
            ShaderHelper.releaseShader();

        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableRescaleNormal();
        GlStateManager.popMatrix();
    }

    // Dirty hack to make the TEISR aware of the stack metas
    public static class ForwardingTEISR extends TileEntityItemStackRenderer {
        private static final TileMorePylon DUMMY = new TileMorePylon();

        private final TileEntityItemStackRenderer compose;

        public ForwardingTEISR(TileEntityItemStackRenderer compose) {
            this.compose = compose;
        }

        @Override
        public void renderByItem(ItemStack stack, float partialTicks) {
            if(stack.getItem() == Item.getItemFromBlock(ModVBlocks.morepylon)) {
                RenderTileMorePylon.forceVariant = MorePylonVariant.values()[MathHelper.clamp(stack.getItemDamage(), 0, MorePylonVariant.values().length)];
                TileEntityRendererDispatcher.instance.render(DUMMY, 0, 0, 0, partialTicks);
            } else {
                compose.renderByItem(stack, partialTicks);
            }
        }
    }

    @Override
    public void renderBlockForMultiblock(IBlockAccess world, Multiblock mb, IBlockState state, MultiblockComponent comp) {
        forceVariant = state.getValue(BotaniverseStateProps.MORE_PYLON_VARIANT);
        GlStateManager.translate(-0.5, -0.25, -0.5);
        renderMorePylon((TileMorePylon) comp.getTileEntity(), 0, 0, 0, 0, true);
        forceVariant = MorePylonVariant.NILFHEIM;
    }

    @Override
    public boolean needsTranslate(IBlockState state) {
        return true;
    }
}
