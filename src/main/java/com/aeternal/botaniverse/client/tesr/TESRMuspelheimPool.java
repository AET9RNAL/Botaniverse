package com.aeternal.botaniverse.client.tesr;


import com.aeternal.botaniverse.api.state.enums.MorePoolVariant;
import com.aeternal.botaniverse.blocks.tiles.TileMorePool;
import com.aeternal.botaniverse.client.core.handler.MiscellaneousIcons;
import com.aeternal.botaniverse.common.block.ModVBlocks;
import com.aeternal.botaniverse.proxy.ClientProxy;
import com.zeitheron.hammercore.annotations.AtTESR;
import com.zeitheron.hammercore.client.render.tesr.TESR;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.mana.IPoolOverlayProvider;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MultiblockRenderHandler;
import vazkii.botania.client.core.helper.ShaderHelper;

@AtTESR(TileMorePool.class)
public class TESRMuspelheimPool extends TESR<TileMorePool>
{
    // Overrides for when we call this TESR without an actual pool
    public static MorePoolVariant forceVariant = MorePoolVariant.NILFHEIM;
    public static int forceManaNumber = -1;
    private static final ResourceLocation TEXTURE_NILFHEIM = new ResourceLocation("botaniverse", "textures/blocks/dreanrock.png");
    private static final ResourceLocation TEXTURE_MUSPELHEIM = new ResourceLocation("botaniverse", "textures/blocks/muspelheimrock.png");
    private static final ResourceLocation TEXTURE_ALFHEIM = new ResourceLocation("botaniverse", "textures/blocks/alfheimrock.png");
    private static final ResourceLocation TEXTURE_ASGARD = new ResourceLocation("botaniverse", "textures/blocks/asgardrock.png");
    @Override
    public void renderTileEntityAt(TileMorePool pool, double d0, double d1, double d2, float f, ResourceLocation destroyStage, float alpha)
    {
        if(pool != null && (!pool.getWorld().isBlockLoaded(pool.getPos(), false) || pool.getWorld().getBlockState(pool.getPos()).getBlock() != ModVBlocks.morepool))
            return;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableRescaleNormal();
        float a = MultiblockRenderHandler.rendering ? 0.6F : 1F;

        GlStateManager.color(1F, 1F, 1F, a);
        if(pool == null)
        { // A null pool means we are calling the TESR without a pool (on a
            // minecart). Adjust accordingly
            GlStateManager.translate(0, 0, -1);
        } else
        {
            GlStateManager.translate(d0, d1, d2);
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        int color = 0xFFFFFF;

        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.color(1, 1, 1, a);
        GlStateManager.enableRescaleNormal();

        int mana = pool == null ? forceManaNumber : pool.getCurrentMana();
        int cap = -1;

        if (pool != null) {
            switch (pool.getPoolType()) {
                case NILFHEIM:
                    cap = TileMorePool.MAX_MANA_NILFHEIM;
                    break;
                case MUSPELHEIM:
                    cap = TileMorePool.MAX_MANA_MUSPELHEIM;
                    break;
                case ALFHEIM:
                    cap = TileMorePool.MAX_MANA_ALFHEIM;
                    break;
                case ASGARD:
                    cap = TileMorePool.MAX_MANA_ASGARD;
                    break;
                default:
                    cap = -1;
                    break;
            }
        }

        if (cap == -1) {
            // Handle cases where capacity is not available or pool is null
            // You might want to use a default value or skip rendering for such cases
            return;
        }

        float waterLevel = (float) mana / (float) cap * 0.4F;

        float s = 1F / 16F;
        float v = 1F / 8F;
        float w = -v * 3.5F;

        if(pool != null)
        {
            Block below = pool.getWorld().getBlockState(pool.getPos().down()).getBlock();
            if(below instanceof IPoolOverlayProvider)
            {
                TextureAtlasSprite overlay = ((IPoolOverlayProvider) below).getIcon(pool.getWorld(), pool.getPos());
                if(overlay != null)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GlStateManager.disableAlpha();
                    GlStateManager.color(1F, 1F, 1F, a * (float) ((Math.sin((ClientTickHandler.ticksInGame + f) / 20.0) + 1) * 0.3 + 0.2));
                    GlStateManager.translate(-0.5F, -1F - 0.43F, -0.5F);
                    GlStateManager.rotate(90F, 1F, 0F, 0F);
                    GlStateManager.scale(s, s, s);

                    renderIcon(0, 0, overlay, 16, 16, 240);

                    GlStateManager.enableAlpha();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        }

        if(waterLevel > 0)
        {
            s = 1F / 256F * 14F;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableAlpha();
            GlStateManager.color(1F, 1F, 1F, a);
            GlStateManager.translate(w, -1F - (0.43F - waterLevel), w);
            GlStateManager.rotate(90F, 1F, 0F, 0F);
            GlStateManager.scale(s, s, s);

            ShaderHelper.useShader(ShaderHelper.manaPool);
            renderIcon(0, 0, MiscellaneousIcons.INSTANCE.manaWater, 16, 16, 240);
            ShaderHelper.releaseShader();

            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        forceVariant = MorePoolVariant.NILFHEIM;
        forceManaNumber = -1;
    }

    public void renderIcon(int par1, int par2, TextureAtlasSprite par3Icon, int par4, int par5, int brightness)
    {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, ClientProxy.POSITION_TEX_LMAP);
        tessellator.getBuffer().pos(par1 + 0, par2 + par5, 0).tex(par3Icon.getMinU(), par3Icon.getMaxV()).lightmap(brightness, brightness).endVertex();
        tessellator.getBuffer().pos(par1 + par4, par2 + par5, 0).tex(par3Icon.getMaxU(), par3Icon.getMaxV()).lightmap(brightness, brightness).endVertex();
        tessellator.getBuffer().pos(par1 + par4, par2 + 0, 0).tex(par3Icon.getMaxU(), par3Icon.getMinV()).lightmap(brightness, brightness).endVertex();
        tessellator.getBuffer().pos(par1 + 0, par2 + 0, 0).tex(par3Icon.getMinU(), par3Icon.getMinV()).lightmap(brightness, brightness).endVertex();
        tessellator.draw();
    }
}

