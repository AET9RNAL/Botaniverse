package com.aeternal.botaniverse.proxy;

import com.aeternal.botaniverse.client.core.handler.MiscellaneousIcons;
import com.aeternal.botaniverse.client.render.entity.spark.alfheim.RenderSparkAlfheim;
import com.aeternal.botaniverse.client.render.entity.spark.asgard.RenderSparkAsgard;
import com.aeternal.botaniverse.client.render.entity.spark.muspelheim.RenderSparkMuspelheim;
import com.aeternal.botaniverse.client.render.entity.spark.nilfheim.RenderSpark;
import com.aeternal.botaniverse.client.render.tile.RenderTileMorePylon;
import com.aeternal.botaniverse.common.block.tile.TileMorePylon;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkAlfheim;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkAsgard;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkMuspelheim;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkNilfheim;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import com.aeternal.botaniverse.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.ClientTickHandler;
import java.awt.*;

public class ClientProxy extends CommonProxy {
    public static final VertexFormat POSITION_TEX_LMAP_NORMAL =
            new VertexFormat()
                    .addElement(DefaultVertexFormats.POSITION_3F)
                    .addElement(DefaultVertexFormats.TEX_2F)
                    .addElement(DefaultVertexFormats.TEX_2S)
                    .addElement(DefaultVertexFormats.NORMAL_3B);
    public static final VertexFormat POSITION_TEX_LMAP =
            new VertexFormat()
                    .addElement(DefaultVertexFormats.POSITION_3F)
                    .addElement(DefaultVertexFormats.TEX_2F)
                    .addElement(DefaultVertexFormats.TEX_2S);


    private static final ModelBiped EMPTY_MODEL = new ModelBiped();
    static {
        EMPTY_MODEL.setVisible(false);
    }
    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(MiscellaneousIcons.INSTANCE);
       initRenderers();
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
    //    TileEntityItemStackRenderer.instance = new RenderTileMorePylon.ForwardingTEISR(TileEntityItemStackRenderer.instance);
    }




    @Override
    public void postInit() {
        super.postInit();

    }

    private void initRenderers() {
        RenderTileMorePylon renderTileMorePylon = new RenderTileMorePylon();
        RenderingRegistry.registerEntityRenderingHandler(EntitySparkNilfheim.class, RenderSpark::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySparkMuspelheim.class, RenderSparkMuspelheim::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySparkAlfheim.class, RenderSparkAlfheim::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySparkAsgard.class, RenderSparkAsgard::new);
        ClientRegistry.bindTileEntitySpecialRenderer(TileMorePylon.class, renderTileMorePylon);
    }


    @SubscribeEvent
    public void onDrawScreenPost(RenderGameOverlayEvent.Post event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        Profiler profiler = mc.profiler;
        ItemStack main = mc.player.getHeldItemMainhand();
        ItemStack offhand = mc.player.getHeldItemOffhand();

    }

    @SubscribeEvent
    public void onToolTipRender(RenderTooltipEvent.PostText evt)
    {
        if(evt.getStack().isEmpty())
            return;

        ItemStack stack = evt.getStack();
        Minecraft mc = Minecraft.getMinecraft();
        int width = evt.getWidth();
        int height = 3;
        int tooltipX = evt.getX();
        int tooltipY = evt.getY() - 4;
        FontRenderer font = evt.getFontRenderer();

    }
    private static void drawManaBar(ItemStack stack, float fraction, int mouseX, int mouseY, int width, int height, float hue)
    {
        int manaBarWidth = (int) Math.ceil(width * fraction);

        GlStateManager.disableDepth();
        Gui.drawRect(mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        Gui.drawRect(mouseX, mouseY - height, mouseX + manaBarWidth, mouseY, Color.HSBtoRGB(hue, ((float) Math.sin((ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.2) + 1F) * 0.1F + 0.6F, 1F));
        Gui.drawRect(mouseX + manaBarWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);
    }
}