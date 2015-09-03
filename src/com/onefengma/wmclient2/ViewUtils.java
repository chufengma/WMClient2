package com.onefengma.wmclient2;

import android.util.DisplayMetrics;

public class ViewUtils {
    public static int dipToPix(DisplayMetrics metrics, int dip) {
        int paddingPixels = (int) ((dip * (metrics.density)) + .5);
        return paddingPixels;
    }
}
