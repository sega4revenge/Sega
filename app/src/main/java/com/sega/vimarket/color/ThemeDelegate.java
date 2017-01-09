package com.sega.vimarket.color;

import android.content.Context;
import android.support.annotation.StyleRes;

public class ThemeDelegate {
    private Colorful.ThemeColor primaryColor;
    private Colorful.ThemeColor accentColor;
    private boolean translucent;
    private boolean dark;
    private int styleRes;

    ThemeDelegate(Context context, Colorful.ThemeColor primary, Colorful.ThemeColor accent, boolean translucent, boolean dark) {
        this.primaryColor=primary;
        this.accentColor=accent;
        this.translucent=translucent;
        this.dark=dark;
        long curTime = System.currentTimeMillis();
        styleRes = context.getResources().getIdentifier(
                (dark ? "DARK" : "LIGHT") +
                primaryColor.ordinal() +
                "T" +
                accentColor.ordinal(),
                "style", context.getPackageName());

    }

    public @StyleRes
    int getStyle() {
        return styleRes;
    }

    public Colorful.ThemeColor getPrimaryColor() {
        return primaryColor;
    }

    public Colorful.ThemeColor getAccentColor() {
        return accentColor;
    }

    public boolean isTranslucent() {
        return translucent;
    }
    
    public boolean isDark() {
        return dark;
    }
}
