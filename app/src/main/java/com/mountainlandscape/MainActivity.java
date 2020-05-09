package com.mountainlandscape;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mountainlandscape.custom.CanvasView;
import com.mountainlandscape.custom.ScrollableLayerView;
import com.mountainlandscape.dialogs.AboutDialog;
import com.mountainlandscape.dialogs.SaveDialog;
import com.mountainlandscape.dialogs.SaveWallpaperDialog;
import com.mountainlandscape.dialogs.ThankYouDialog;
import com.mountainlandscape.dialogs.UpgradeProDialog;
import com.mountainlandscape.fragments.CanvasMenuFragment;
import com.mountainlandscape.interfaces.CanvasOptionsObserver;
import com.mountainlandscape.interfaces.HorizontalScrollViewScrollEvents;
import com.mountainlandscape.live_wallpaper.LiveWallpaperService;
import com.mountainlandscape.models.CanvasOptions;
import com.mountainlandscape.models.Layer;
import com.mountainlandscape.preferences.PrefUtils;
import com.mountainlandscape.preferences.Prefs;
import com.mountainlandscape.utilities.ColorUtils;
import com.mountainlandscape.utilities.PixelUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CanvasOptionsObserver, SaveDialog.SaveDialogEvents, PurchasesUpdatedListener, NavigationView.OnNavigationItemSelectedListener, CanvasMenuFragment.CanvasMenuEvents {

    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private final int EXTERNAL_STORAGE_LIVE_WALLPAPER_PERMISSION_CODE = 2;

    @BindView(R.id.relLayLayers)
    RelativeLayout relLayLayers;

    @BindView(R.id.imgPlanet)
    ImageView imgPlanet;

    @BindView(R.id.imgPlanetCrater)
    ImageView imgPlanetCrater;

    @BindView(R.id.canvasView)
    CanvasView canvasView;

    @BindView(R.id.relLayMain)
    RelativeLayout relLayMain;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.relLayMenuContainer)
    RelativeLayout relLayMenuContainer;

    @BindView(R.id.adView)
    AdView adView;

    @BindView(R.id.navView)
    NavigationView navView;

    @BindView(R.id.txtWatermarkLeft)
    TextView txtWatermarkLeft;

    @BindView(R.id.txtWatermarkRight)
    TextView txtWatermarkRight;

    CanvasMenuFragment fragCanvasMenu;

    ArrayList<ScrollableLayerView> scrollableLayerViews;
    private ActionBarDrawerToggle mDrawerToggle;

    private Bitmap mWallpaper; // the generated wallpaper
    private BillingClient billingClient;
    private final String PRO_SKU_ID = "pro";
    private boolean isPreset = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;
    boolean shouldShowInterstitialAd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setSupportActionBar(toolbar);
        setupAdmob();

        if(App.isPro) {
            hideFreeUserAdsAndFeatures();
        }

        scrollableLayerViews = new ArrayList<>();
        navView.setNavigationItemSelectedListener(this);

        initDrawerLayout();

        CanvasOptions.getInstance(this).addObserver(this);

        fragCanvasMenu = (CanvasMenuFragment) getSupportFragmentManager().findFragmentById(R.id.fragCanvasMenu);
        if(fragCanvasMenu != null)
            fragCanvasMenu.setCanvasMenuCallback(this);

    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this).setListener(this).build();
        startBillingClientConnection();
    }

    private void startBillingClientConnection(){
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    List<String> skuList = new ArrayList<> ();
                    skuList.add(PRO_SKU_ID);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                                    if (responseCode == BillingClient.BillingResponse.OK
                                            && skuDetailsList != null) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            String sku = skuDetails.getSku();
                                            String price = skuDetails.getPrice();
                                            if (sku.equals(PRO_SKU_ID)) {
                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                        .setSkuDetails(skuDetails)
                                                        .build();
                                                int code = billingClient.launchBillingFlow(MainActivity.this, flowParams);
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Snackbar.make(findViewById(android.R.id.content), R.string.unable_to_connect_to_google_play, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startBillingClientConnection();
                            }
                        })
                        .show();
            }
        });
    }

    private void setupAdmob() {
        MobileAds.initialize(this, getString(R.string.admob_appid));


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.fullscreen_adUnitId));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdListener(new AdListener() {
             @Override
             public void onAdLoaded() {
                 super.onAdLoaded();
                 //Fix the moutain not in the right position bug (after ad loads)
                 drawCanvas(false);
             }
        });
        adView.loadAd(adRequest);
    }


    private void initDrawerLayout() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();


        int orientation = getResources().getConfiguration().orientation;
        int width = 0;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = getResources().getDisplayMetrics().widthPixels / 3;
        } else {
            width = getResources().getDisplayMetrics().widthPixels / 2;
        }

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) relLayMenuContainer.getLayoutParams();
        params.width = width;
        relLayMenuContainer.setLayoutParams(params);

        drawerLayout.setScrimColor(Color.TRANSPARENT);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_pro:
                UpgradeProDialog.showDialog(this, new UpgradeProDialog.UpgradeProDialogEvents() {
                    @Override
                    public void onUpgradeProClicked() {
                        setupBillingClient();
                    }

                    @Override
                    public void onUpgradeProDismissed() {

                    }
                });
                break;
            case R.id.menu_about:
                AboutDialog.showDialog(this);
                break;
            case R.id.menu_rate:
                rateApp();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intentMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        intentMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(intentMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_settings:
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
                else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
                break;
            case R.id.menu_save:
                if(!App.isPro) {
                    if (shouldShowInterstitialAd) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }
                        shouldShowInterstitialAd = false;
                    } else {
                        shouldShowInterstitialAd = true;
                    }
                }
                adView.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                drawCanvas(false);//fix the wallpaper redraw
                SaveDialog.showDialog(this, this);
                break;
        }
        return true;
    }

    private void generateBitmapFromActivity() {
        int width = relLayMain.getWidth();
        int height = relLayMain.getHeight();

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        relLayMain.draw(new Canvas(image));

        mWallpaper = image;


        if(!App.isPro)
            adView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

    }

    private void setImageAsWallpaper() {
        final WallpaperManager wallPaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SaveWallpaperDialog.showDialog(this, new SaveWallpaperDialog.SaveWallpaperDialogEvents() {
                    @Override
                    public void onOptionSelected(SaveWallpaperDialog.ESaveWallpaperOptions option) {
                        switch(option) {
                            case HomeScreen:
                                try {
                                    wallPaperManager.setBitmap(mWallpaper, null, true, WallpaperManager.FLAG_SYSTEM);
                                    Toast.makeText(MainActivity.this, getString(R.string.home_screen_wallpaper_has_been_set), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case LockScreen:
                                try {
                                    wallPaperManager.setBitmap(mWallpaper, null, true, WallpaperManager.FLAG_LOCK);
                                    Toast.makeText(MainActivity.this, getString(R.string.lock_screen_wallpaper_has_been_set), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case HomeAndLockScreen:
                                try {
                                    wallPaperManager.setBitmap(mWallpaper, null, true, WallpaperManager.FLAG_LOCK | WallpaperManager.FLAG_SYSTEM);
                                    Toast.makeText(MainActivity.this, getString(R.string.home_and_lock_screen_wallpaper_have_been_set), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });

                drawCanvas(false);//fix the wallpaper redraw
            } else {
                wallPaperManager.setBitmap(mWallpaper);
                Toast.makeText(MainActivity.this, getString(R.string.wallpaper_has_been_set), Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch(requestCode) {
                case EXTERNAL_STORAGE_PERMISSION_CODE:
                    saveBitmapOnDevice();
                    break;
                case EXTERNAL_STORAGE_LIVE_WALLPAPER_PERMISSION_CODE:
                    configureLiveWallpaper();
                    break;

            }
        }

    }

    private boolean checkExternalStoragePermission(int code) {
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
                return false;
            }
        }
        return true;
    }

    private void saveBitmapOnDevice() {
        File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(downloadDirectory + "/wallpaper.png");
            mWallpaper.compress(Bitmap.CompressFormat.PNG, 100, out);

            Toast.makeText(this, "Saved to downloads folder.", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Crashlytics.getInstance().logException(e);
            e.printStackTrace();
        }

        File internalDirectory = getFilesDir();
        try {
            out = new FileOutputStream(internalDirectory + "/wallpaper.png");
            mWallpaper.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            Crashlytics.getInstance().logException(e);
            e.printStackTrace();
        }


    }

    private void moveLayers() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;

        Random random = new Random();
        for(ScrollableLayerView view:scrollableLayerViews) {
            int scrollx = random.nextInt(4000 - screenWidth) + 1;
            view.scrollTo(scrollx, 0);
        }
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            /*
            case R.id.lay:
                generateImage();
                canvasView.invalidate();
                break;*/
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(this.isPreset) {
            int offset = getResources().getInteger(R.integer.default_layer_offset);
            CanvasOptions.getInstance(this).setLayerOffset(offset, true);
        }


        int planetSize = getResources().getDimensionPixelSize(R.dimen.moon_size);
        imgPlanet.getLayoutParams().height = planetSize;
        imgPlanet.getLayoutParams().width = planetSize;

        imgPlanetCrater.getLayoutParams().height = planetSize;
        imgPlanetCrater.getLayoutParams().width = planetSize;

        imgPlanet.requestLayout();
        imgPlanetCrater.requestLayout();

        canvasView.invalidate();
    }

    private void drawCanvas(boolean layerUpdated) {
        CanvasOptions canvasOptions = CanvasOptions.getInstance(this);

        //clear the layer canvas
        if(layerUpdated) {
            scrollableLayerViews = new ArrayList<>();
            relLayLayers.removeAllViews();
        }

        //redraw all layers
        for(int i = 0; i < canvasOptions.getLayers().size(); i++) {

            final Layer layer = canvasOptions.getLayers().get(i);

            final ScrollableLayerView view;
            if(layerUpdated || scrollableLayerViews.size() <= i) {
                view = new ScrollableLayerView(this, new HorizontalScrollViewScrollEvents() {
                    @Override
                    public void onScrollChanged(View v) {
                        layer.setScrollX(v.getScrollX());
                    }
                });
            } else {
                view = scrollableLayerViews.get(i);
            }

            int offset;
            if(i == canvasOptions.getLayers().size() - 1) {
                if(canvasOptions.getLayerOffset() > CanvasOptions.DEFAULT_OFFSET) {
                    offset = PixelUtils.getPixelsFromDp(this, Math.round((canvasOptions.getLayerOffset() - CanvasOptions.DEFAULT_OFFSET) * 0.5f));
                } else {
                    offset = 0;
                }
            } else {
                offset = PixelUtils.getPixelsFromDp(this, canvasOptions.getLayerOffset() * (canvasOptions.getLayers().size() - i - 1));
            }
            view.setLayerOffset(offset);
            view.addImage(layer.getResourceId());
            view.addShadowImage(layer.getShadowResourceId());

            view.post(new Runnable() {
                @Override
                public void run() {
                    view.getHorizontalScrollView().scrollTo(layer.getScrollX(), 0);
                }
            });

            float ratio = ((float) (canvasOptions.getLayers().size() - i)) / canvasOptions.getLayers().size();
            int percent = Math.round(ratio * 100);
            int color = ColorUtils.getColorOfDegradate(canvasOptions.getStartColor(), canvasOptions.getEndColor(), percent);
            view.setImageColorFilter(color);

            if(layerUpdated) {
                relLayLayers.addView(view);
                scrollableLayerViews.add(view);
            }
        }

        if(canvasView.getStarsCount() != canvasOptions.getStarsCount() || canvasView.getStarsColor() != canvasOptions.getStarsColor()) {
            canvasView.setStarsCount(canvasOptions.getStarsCount());
            canvasView.setStarsColor(canvasOptions.getStarsColor());
            canvasView.invalidate();
        }

        if(canvasOptions.isShowPlanet()) {
            imgPlanet.setVisibility(View.VISIBLE);
            imgPlanet.setColorFilter(canvasOptions.getPlanetColor());

            if (canvasOptions.hasCraters()) {
                int planetCraterColor = ColorUtils.getColorOfDegradate(canvasOptions.getPlanetColor(), Color.BLACK, 20);
                imgPlanetCrater.setColorFilter(planetCraterColor);
                imgPlanetCrater.setVisibility(View.VISIBLE);
            } else {
                imgPlanetCrater.setVisibility(View.GONE);
            }
        } else {
            imgPlanet.setVisibility(View.GONE);
            imgPlanetCrater.setVisibility(View.GONE);
        }

        if(canvasOptions.isUseGradient()) {
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{
                    canvasOptions.getBackgroundColorStart(),
                    canvasOptions.getBackgroundColorEnd()
            });

            if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                canvasView.setBackgroundDrawable(gradientDrawable);
            } else {
                canvasView.setBackground(gradientDrawable);
            }
        } else {
            canvasView.setBackgroundColor(canvasOptions.getBackgroundColorStart());
        }
    }

    @Override
    public void onLayerOffsetUpdated(boolean isPreset) {
        int offset = CanvasOptions.getInstance(this).getLayerOffset();
        int calculatedOffset;
        for(int i = 0; i < scrollableLayerViews.size(); i++) {
            if(i == scrollableLayerViews.size() - 1) {
                if(offset > CanvasOptions.DEFAULT_OFFSET) {
                    calculatedOffset = PixelUtils.getPixelsFromDp(this, Math.round((offset - CanvasOptions.DEFAULT_OFFSET) * 0.5f));
                } else {
                    calculatedOffset = 0;
                }
            } else {
                calculatedOffset = PixelUtils.getPixelsFromDp(this, offset * (scrollableLayerViews.size() - i - 1));
            }
            scrollableLayerViews.get(i).setLayerOffset(calculatedOffset);
        }
    }

    private void configureLiveWallpaper() {
        saveBitmapOnDevice();

        Intent intent = new Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(MainActivity.this, LiveWallpaperService.class));
        startActivity(intent);
    }

    @Override
    public void onActionSelected(SaveDialog.ESaveType saveType) {
        switch(saveType) {
            case ConfigureLiveWallpaper:
                if(App.isPro) {
                    generateBitmapFromActivity();
                    if (checkExternalStoragePermission(EXTERNAL_STORAGE_LIVE_WALLPAPER_PERMISSION_CODE)) {
                        configureLiveWallpaper();
                    }
                } else {
                    UpgradeProDialog.showDialog(this, new UpgradeProDialog.UpgradeProDialogEvents() {
                        @Override
                        public void onUpgradeProClicked() {
                            setupBillingClient();
                        }

                        @Override
                        public void onUpgradeProDismissed() {
                            if(!App.isPro)
                                adView.setVisibility(View.VISIBLE);
                            toolbar.setVisibility(View.VISIBLE);
                        }
                    });
                }
                break;
            case SaveAndSetAsWallpaper:
                generateBitmapFromActivity();
                setImageAsWallpaper();
                if(checkExternalStoragePermission(EXTERNAL_STORAGE_PERMISSION_CODE)) {
                    saveBitmapOnDevice();
                }
                break;
            case SaveOnDevice:
                generateBitmapFromActivity();
                if(checkExternalStoragePermission(EXTERNAL_STORAGE_PERMISSION_CODE)) {
                    saveBitmapOnDevice();
                }
                break;
            case SetAsWallpaper:
                generateBitmapFromActivity();
                setImageAsWallpaper();
                break;
            case Dismiss:
                if(!App.isPro)
                    adView.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                drawCanvas(false);//fix the wallpaper redraw
                break;
        }
    }

    @Override
    public void onCanvasOptionsUpdated(boolean layerUpdated, boolean isPreset) {
        this.isPreset = isPreset;
        drawCanvas(layerUpdated);
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else if(responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED){
            setPro();
            Snackbar.make(findViewById(android.R.id.content), R.string.you_already_own_that_item_purchase_restored, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .show();
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_unknown_with_purchase, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startBillingClientConnection();
                        }
                    })
                    .show();
        }
    }

    private void setPro(){
        PrefUtils.save(this, Prefs.IS_PRO, true);
        App.isPro = true;
        hideFreeUserAdsAndFeatures();
        ThankYouDialog.showDialog(this);
    }

    private void handlePurchase(Purchase purchase) {
        if(purchase.getSku().equals(PRO_SKU_ID)) {
            setPro();
        }
    }

    private void hideFreeUserAdsAndFeatures() {
        adView.setVisibility(View.GONE);
        navView.getMenu().findItem(R.id.menu_pro).setVisible(false);
        txtWatermarkLeft.setVisibility(View.GONE);
        txtWatermarkRight.setVisibility(View.GONE);
    }

    @Override
    public void onFullScreenCheckedChange(boolean isChecked) {
        if(isChecked) {
            toolbar.setVisibility(View.GONE);
            adView.setVisibility(View.GONE);
            Toast.makeText(this, R.string.swipe_to_open_menus, Toast.LENGTH_LONG).show();
        } else {
            toolbar.setVisibility(View.VISIBLE);
            if(!App.isPro)
                adView.setVisibility(View.VISIBLE);
        }

        drawCanvas(false);//fix the wallpaper redraw
    }
}
