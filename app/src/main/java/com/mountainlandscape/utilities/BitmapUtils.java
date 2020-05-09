package com.mountainlandscape.utilities;

import android.graphics.Bitmap;

public class BitmapUtils {
    public static Bitmap resizeByHeight(Bitmap image, int height) {
        float ratio = (float) image.getHeight() / (float) image.getWidth();
        int newWidth = Math.round(height / ratio);
        return Bitmap.createScaledBitmap(image, newWidth, height, true);
    }

}
