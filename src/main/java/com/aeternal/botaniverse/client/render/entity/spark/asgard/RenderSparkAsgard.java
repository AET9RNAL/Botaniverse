package com.aeternal.botaniverse.client.render.entity.spark.asgard;

import com.aeternal.botaniverse.client.core.handler.MiscellaneousIcons;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkAsgard;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class RenderSparkAsgard extends RenderSparkAsgardBase<EntitySparkAsgard> {

    public RenderSparkAsgard(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public TextureAtlasSprite getSpinningIcon(EntitySparkAsgard entity) {
        int upgrade = entity.getUpgrade().ordinal() - 1;
        return upgrade >= 0 && upgrade < MiscellaneousIcons.INSTANCE.sparkUpgradeIcons.length ? MiscellaneousIcons.INSTANCE.sparkUpgradeIcons[upgrade] : null;
    }

}
