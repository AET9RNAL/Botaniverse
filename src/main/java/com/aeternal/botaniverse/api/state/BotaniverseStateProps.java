package com.aeternal.botaniverse.api.state;

import com.aeternal.botaniverse.api.state.enums.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.EnumDyeColor;
import vazkii.botania.api.state.enums.AlfPortalState;

public final class BotaniverseStateProps {
    // BlockPool
    public static final PropertyEnum<MorePoolVariant> MOREPOOL_VARIANT = PropertyEnum.create("variant", MorePoolVariant.class);
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
    public static final PropertyEnum<MoreRockVariant> MORE_ROCK_VARIANT = PropertyEnum.create("variant", MoreRockVariant.class);
    public static final PropertyEnum<MoreWoodVariant> MORE_WOOD_VARIANT = PropertyEnum.create("variant", MoreWoodVariant.class);
    public static final PropertyEnum<MorePylonVariant> MORE_PYLON_VARIANT = PropertyEnum.create("variant", MorePylonVariant.class);
    // BlockAlfPortal
    public static final PropertyEnum<AlfheimPortalState> ALFHEIMPORTAL_STATE = PropertyEnum.create("state", AlfheimPortalState.class);
    private BotaniverseStateProps() {
    }

}
