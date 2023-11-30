package com.aeternal.botaniverse.client.render.entity.spark.alfheim;

import com.aeternal.botaniverse.client.core.handler.MiscellaneousIcons;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkAlfheim;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class RenderSparkAlfheim extends RenderSparkAlfheimBase<EntitySparkAlfheim> {

    public RenderSparkAlfheim(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public TextureAtlasSprite getSpinningIcon(EntitySparkAlfheim entity) {
        int upgrade = entity.getUpgrade().ordinal() - 1;
        return upgrade >= 0 && upgrade < MiscellaneousIcons.INSTANCE.sparkUpgradeIcons.length ? MiscellaneousIcons.INSTANCE.sparkUpgradeIcons[upgrade] : null;
    }

}
