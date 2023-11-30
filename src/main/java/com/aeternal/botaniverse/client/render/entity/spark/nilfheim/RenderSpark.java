
package com.aeternal.botaniverse.client.render.entity.spark.nilfheim;

import com.aeternal.botaniverse.client.core.handler.MiscellaneousIcons;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkNilfheim;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;



public class RenderSpark extends RenderSparkBase<EntitySparkNilfheim> {

    public RenderSpark(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public TextureAtlasSprite getSpinningIcon(EntitySparkNilfheim entity) {
        int upgrade = entity.getUpgrade().ordinal() - 1;
        return upgrade >= 0 && upgrade < MiscellaneousIcons.INSTANCE.sparkUpgradeIcons.length ? MiscellaneousIcons.INSTANCE.sparkUpgradeIcons[upgrade] : null;
    }

}
