package com.mountainlandscape.live_wallpaper;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.mountainlandscape.utilities.BitmapUtils;
import com.mountainlandscape.R;
import com.mountainlandscape.preferences.PrefUtils;
import com.mountainlandscape.preferences.Prefs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LiveWallpaperService extends WallpaperService {

    private Bitmap bitmap;
    List<SnowFlake> snowFlakes;
    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private class LiveWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }

        };
        private final Runnable slideShowRunner = new Runnable() {
            @Override
            public void run() {
                updateSlideShow();
            }

        };
        private Paint paint = new Paint();
        private boolean visible = true;
        private int width;
        private SharedPreferences mPreferences;
        private boolean prefsUpdated = true;
        private ELiveWallpaperType type;
        int height;
        private float FPS;

        //slideshow
        boolean slideshowWaiting = false;
        int[] themePresetsDrawables = {
                R.drawable.blue_night_theme_preset,
                R.drawable.blue_winter_theme_preset,
                R.drawable.green_forest_theme_preset,
                R.drawable.purple_night_theme_preset,
                R.drawable.red_sunny_theme_preset
        };
        int presetIndex = 0;
        int transitionTime = 30;

        //private boolean touchEnabled;

        public LiveWallpaperEngine() {

            mPreferences = PrefUtils.getPrefs(getApplicationContext());
            mPreferences.registerOnSharedPreferenceChangeListener(this);

            snowFlakes = new ArrayList<>();
            String file = getFilesDir() + "/wallpaper.png";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(file, options);

            //an attempt to fix the crash where bitmap is null.
            if(bitmap == null) {
                File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String wallpaperFile = downloadDirectory + "/wallpaper.png";
                bitmap = BitmapFactory.decodeFile(wallpaperFile, options);

            }

            //The app is really not able to read any of the saved wallpaper... grab anything
            if(bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.blue_night_theme_preset);
            }

            bitmap = BitmapUtils.resizeByHeight(bitmap, Resources.getSystem().getDisplayMetrics().heightPixels);
            type = ELiveWallpaperType.fromId(PrefUtils.get(getApplicationContext(), Prefs.CURRENT_LIVE_WALLPAPER_TYPE, 0));

            FPS = 1000 / PrefUtils.get(getApplicationContext(), Prefs.FPS, 45);

            handler.post(drawRunner);

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            this.width = width;
            this.height = height;
            super.onSurfaceChanged(holder, format, width, height);
        }

        private void drawSnowFall(Canvas canvas) {
            drawDefault(canvas);

            //init
            if(prefsUpdated) {
                snowFlakes.clear();
                int snowflakeCount = PrefUtils.get(getApplicationContext(), Prefs.SNOWFLAKE_COUNT, SnowFlake.COUNT_DEFAULT);
                for (int i = 0; i < snowflakeCount; i++) {
                    SnowFlake snowFlake = new SnowFlake(getApplicationContext());
                    snowFlake.draw(canvas);
                    snowFlakes.add(snowFlake);
                }

                prefsUpdated = false;
            }

            for (int i = 0; i < snowFlakes.size(); i++) {
                SnowFlake snowFlake = snowFlakes.get(i);
                snowFlake.update();
                snowFlake.draw(canvas);
            }
        }

        private void drawSlideShow(Canvas canvas) {
            //init
            if(prefsUpdated) {
                transitionTime = PrefUtils.get(getApplicationContext(), Prefs.TRANSITION_TIME, 30);
                updateSlideShow(); //to get the first preset
                prefsUpdated = false;
            }

            if(!slideshowWaiting) {
                slideshowWaiting = true;
                handler.removeCallbacks(slideShowRunner);
                handler.postDelayed(slideShowRunner, transitionTime * 1000);
            }

            drawDefault(canvas);
        }

        public Bitmap scaleCenterCrop(Bitmap source, int newHeight,
                                             int newWidth) {
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            float xScale = (float) newWidth / sourceWidth;
            float yScale = (float) newHeight / sourceHeight;
            float scale = Math.max(xScale, yScale);

            // Now get the size of the source bitmap when scaled
            float scaledWidth = scale * sourceWidth;
            float scaledHeight = scale * sourceHeight;

            float left = (newWidth - scaledWidth) / 2;
            float top = (newHeight - scaledHeight) / 2;

            RectF targetRect = new RectF(left, top, left + scaledWidth, top
                    + scaledHeight);

            Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
                    source.getConfig());
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(source, null, targetRect, null);

            return dest;
        }

        private void drawDefault(Canvas canvas) {
            Paint rectPaint = new Paint();
            rectPaint.setStyle(Paint.Style.FILL);
            rectPaint.setColor(Color.BLACK);
            canvas.drawRect(new Rect(0 , 0, this.width, this.height), rectPaint);

            bitmap = scaleCenterCrop(bitmap, this.height, this.width);
            canvas.drawBitmap(bitmap, null, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);
        }

        private void updateSlideShow() {
            slideshowWaiting = false;
            int nextPresetIndex = presetIndex % themePresetsDrawables.length;
            bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    themePresetsDrawables[nextPresetIndex]);

            //to prevent int overflow, reset the index after each cycle.
            if(nextPresetIndex == themePresetsDrawables.length - 1) {
                presetIndex = 0;
            } else {
                presetIndex++;
            }
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    switch(type) {
                        case None:
                            drawDefault(canvas);
                            break;
                        case SnowFall:
                            drawSnowFall(canvas);
                            break;
                        case SlideShow:
                            drawSlideShow(canvas);
                            break;
                    }
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                if(type == ELiveWallpaperType.SlideShow) {
                    handler.postDelayed(drawRunner, 1000); //slideshow has no animation, don't need to refresh often.
                } else {
                    handler.postDelayed(drawRunner, (long) FPS);
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            type = ELiveWallpaperType.fromId(PrefUtils.get(getApplicationContext(), Prefs.CURRENT_LIVE_WALLPAPER_TYPE, 0));
            FPS = 1000 / PrefUtils.get(getApplicationContext(), Prefs.FPS, 45);

            prefsUpdated = true;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
