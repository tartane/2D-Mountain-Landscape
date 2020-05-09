package com.mountainlandscape.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

public class CanvasView extends View {
    private int starsCount;
    private int starsColor;

    public CanvasView(Context context) {
        super(context);

    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public int getStarsCount() {
        return starsCount;
    }

    public void setStarsCount(int starsCount) {
        this.starsCount = starsCount;
    }

    public int getStarsColor() {
        return starsColor;
    }

    public void setStarsColor(int starsColor) {
        this.starsColor = starsColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;

            Paint paint = new Paint();
            paint.setColor(starsColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            //draw the stars
            for (int i = 0; i < starsCount; i++) {
                Random random = new Random();
                int alpha = random.nextInt(255) + 1;
                paint.setAlpha(alpha);

                int x = random.nextInt(screenWidth) + 1;
                int y = random.nextInt(screenHeight) + 1;
                int radius = random.nextInt(5) + 1;

                canvas.drawCircle(x, y, radius, paint);
            }
/*
        Bitmap.Config mBitmapConfig;
        mBitmapConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = mBitmapConfig;
        Bitmap ob = BitmapFactory.decodeResource(getResources(), R.drawable.mountain_layer_1, options);
        paint = new Paint();

        ColorFilter filter = new PorterDuffColorFilter(getResources().getColor(R.color.dark_blue), PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        canvas.drawBitmap(ob, 0, canvas.getHeight() - ob.getHeight(), paint);*/
    }
}
