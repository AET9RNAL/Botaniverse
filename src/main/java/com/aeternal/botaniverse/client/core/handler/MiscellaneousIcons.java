package com.aeternal.botaniverse.client.core.handler;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.aeternal.botaniverse.client.core.helper.IconHelper;
import vazkii.botania.common.item.ItemSparkUpgrade;

public class MiscellaneousIcons {

    public static final MiscellaneousIcons INSTANCE = new MiscellaneousIcons();

    public TextureAtlasSprite
            manaWater,
            sparkWorldIcon,
            sparkWorldIconMuspelheim,
            sparkWorldIconAlfheim,
            sparkWorldIconAsgard;



    public TextureAtlasSprite[] sparkUpgradeIcons;


    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre evt) {
        sparkWorldIcon = IconHelper.forName(evt.getMap(), "spark_nilfheim", "items");
        sparkWorldIconMuspelheim = IconHelper.forName(evt.getMap(), "spark_muspelheim", "items");
        sparkWorldIconAlfheim = IconHelper.forName(evt.getMap(), "spark_alfheim", "items");
        sparkWorldIconAsgard = IconHelper.forName(evt.getMap(), "spark_asgard", "items");



        sparkUpgradeIcons = new TextureAtlasSprite[ItemSparkUpgrade.VARIANTS];
        manaWater = com.aeternal.botaniverse.client.core.helper.IconHelper.forName(evt.getMap(), "mana_water", "blocks");
        for(int i = 0; i < ItemSparkUpgrade.VARIANTS; i++) {
            sparkUpgradeIcons[i] = IconHelper.forName(evt.getMap(), "spark_upgrade_rune_" + i, "items");
        }


    }

    private MiscellaneousIcons() {}
}
