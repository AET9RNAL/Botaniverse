package com.aeternal.botaniverse.client.render.entity.spark.muspelheim;

import com.aeternal.botaniverse.client.core.handler.MiscellaneousIcons;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkMuspelheim;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class RenderSparkMuspelheim extends RenderSparkMuspelheimBase<EntitySparkMuspelheim> {

    public RenderSparkMuspelheim(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public TextureAtlasSprite getSpinningIcon(EntitySparkMuspelheim entity) {
        int upgrade = entity.getUpgrade().ordinal() - 1;
        return upgrade >= 0 && upgrade < MiscellaneousIcons.INSTANCE.sparkUpgradeIcons.length ? MiscellaneousIcons.INSTANCE.sparkUpgradeIcons[upgrade] : null;
    }

}
