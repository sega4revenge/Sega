package com.sega.vimarket.loader;

import com.github.mikephil.charting.utils.ValueFormatter;

/**a
 * Created by Sega on 08/09/2016.
 */
public class MyValueFormatter implements ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return Math.round(value)+"";
    }
}