package com.mountainlandscape.utilities;

import android.animation.ArgbEvaluator;

public class ColorUtils {
    /**
     * Get the color between two given colors
     * @param colorStart int color start of degradate
     * @param colorEnd int color end of degradate
     * @param percent int percent to apply (0 to 100)
     * @return int color of degradate for given percent
     */
    public static int getColorOfDegradate(int colorStart, int colorEnd, int percent){
        float fraction = ((float) percent) / 100;
        return (int) new ArgbEvaluator().evaluate(fraction, colorStart, colorEnd);
    }
}
