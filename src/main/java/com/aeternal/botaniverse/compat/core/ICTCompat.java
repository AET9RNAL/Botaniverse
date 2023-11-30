package com.aeternal.botaniverse.compat.core;

public interface ICTCompat
{
    void onLoadComplete();

    void init();

    void addLateAction(Object action);
}