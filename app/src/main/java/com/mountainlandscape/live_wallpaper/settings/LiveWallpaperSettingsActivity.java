package com.mountainlandscape.live_wallpaper.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.mountainlandscape.R;
import com.mountainlandscape.live_wallpaper.ELiveWallpaperType;

import butterknife.ButterKnife;

public class LiveWallpaperSettingsActivity extends AppCompatActivity implements MainMenuSettingsFragment.MainMenuSettingsEvents {

    private MainMenuSettingsFragment mainMenuSettingsFragment;
    private SnowfallSettingsFragment snowfallSettingsFragment;
    private SlideshowSettingsFragment slideshowSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_wallpaper_settings);
        ButterKnife.bind(this);

        mainMenuSettingsFragment = new MainMenuSettingsFragment();
        snowfallSettingsFragment = new SnowfallSettingsFragment();
        slideshowSettingsFragment = new SlideshowSettingsFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mainMenuSettingsFragment).commit();

    }

    @Override
    public void onItemSelected(ELiveWallpaperType type) {
        switch(type) {
            case SnowFall:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, snowfallSettingsFragment, "snowfall")
                        .addToBackStack(snowfallSettingsFragment.getClass().getName())
                        .commit();
                break;
            case SlideShow:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, slideshowSettingsFragment, "slideshow")
                        .addToBackStack(slideshowSettingsFragment.getClass().getName())
                        .commit();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

}
