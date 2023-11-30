package com.aeternal.botaniverse.compat.crafttweaker;

import com.aeternal.botaniverse.compat.core.ICTCompat;
import net.minecraftforge.fml.common.Loader;


public class CraftTweakerCompat
        implements ICTCompat
{
    private static ICTCompat ct = null;

    public static ICTCompat compat()
    {
        if(ct == null)
            if(Loader.isModLoaded("crafttweaker"))
                ct = new CTCompatImpl();
            else
                ct = new CraftTweakerCompat();
        return ct;
    }

    @Override
    public void onLoadComplete()
    {
    }

    @Override
    public void init()
    {
    }

    @Override
    public void addLateAction(Object action)
    {
    }
}