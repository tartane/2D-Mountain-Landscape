package com.mountainlandscape.live_wallpaper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mountainlandscape.utilities.Randomizer;
import com.mountainlandscape.preferences.PrefUtils;
import com.mountainlandscape.preferences.Prefs;

public class SnowFlake {

    public static final int COUNT_DEFAULT = 100;

    public static final int MIN_RADIUS_DEFAULT = 4;
    public static final int MAX_RADIUS_DEFAULT = 12;

    public static final int MIN_SPEED_DEFAULT = 10;
    public static final int MAX_SPEED_DEFAULT = 20;


    private int MIN_ALPHA = 100;
    private int MAX_ALPHA = 255;

    private int MIN_RADIUS = 4;
    private int MAX_RADIUS = 12;

    private int MAX_ANGLE = 5;

    private int MIN_SPEED = 10;
    private int MAX_SPEED = 20;

    private int x;
    private int y;
    private int radius;
    private Paint paint;
    private double speedX;
    private double speedY;

    private int screenWidth = 0;
    private int screenHeight = 0;

    public SnowFlake(Context context) {
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        MIN_RADIUS = PrefUtils.get(context, Prefs.SNOW_MIN_SIZE, MIN_RADIUS);
        MAX_RADIUS = PrefUtils.get(context, Prefs.SNOW_MAX_SIZE, MAX_RADIUS);

        MIN_SPEED = PrefUtils.get(context, Prefs.SNOW_MIN_SPEED, MIN_SPEED);
        MAX_SPEED = PrefUtils.get(context, Prefs.SNOW_MAX_SPEED, MAX_SPEED);

        reset();
    }

    public void reset() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        int alpha = Randomizer.randomInt(MIN_ALPHA, MAX_ALPHA);
        paint.setAlpha(alpha);

        this.x = Randomizer.randomInt(0, screenWidth);
        this.y = -Randomizer.randomInt(0, screenHeight);
        this.radius = Randomizer.randomInt(MIN_RADIUS, MAX_RADIUS);

        double speed = (Double.valueOf((radius - MIN_RADIUS)) / Double.valueOf((MAX_RADIUS - MIN_RADIUS)) * (MAX_SPEED - MIN_SPEED) + MIN_SPEED);
        double angle = Math.toRadians(Randomizer.randomDouble(MAX_ANGLE) * Randomizer.randomSignum());

        speedX = speed * Math.sin(angle);
        speedY = speed * Math.cos(angle);
    }

    public void update() {
        this.x += speedX;
        this.y += speedY;

        if(this.y > screenHeight) {
             reset();
        }
    }

    public void draw(Canvas canvas) {

        canvas.drawCircle(x, y, radius, paint);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
