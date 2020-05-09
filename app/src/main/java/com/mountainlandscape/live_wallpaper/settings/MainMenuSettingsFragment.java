package com.mountainlandscape.live_wallpaper.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mountainlandscape.R;
import com.mountainlandscape.live_wallpaper.ELiveWallpaperType;
import com.mountainlandscape.preferences.PrefUtils;
import com.mountainlandscape.preferences.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMenuSettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.cvSnowfall)
    CardView cvSnowfall;

    @BindView(R.id.cvSlideshow)
    CardView cvSlideshow;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.lblSlideshowCurrent)
    RelativeLayout lblSlideshowCurrent;

    @BindView(R.id.lblSnowfallCurrent)
    RelativeLayout lblSnowfallCurrent;

    private MainMenuSettingsEvents callback;

    public MainMenuSettingsFragment() {
        // Required empty public constructor
    }

    public static MainMenuSettingsFragment newInstance(String param1, String param2) {
        MainMenuSettingsFragment fragment = new MainMenuSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_settings, container, false);
        ButterKnife.bind(this, view);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle(R.string.live_wallpaper_settings);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        cvSlideshow.setOnClickListener(this);
        cvSnowfall.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        lblSlideshowCurrent.setVisibility(View.GONE);
        lblSnowfallCurrent.setVisibility(View.GONE);

        ELiveWallpaperType type = ELiveWallpaperType.fromId(PrefUtils.get(getContext(), Prefs.CURRENT_LIVE_WALLPAPER_TYPE, 0));
        switch(type) {
            case SnowFall:
                lblSnowfallCurrent.setVisibility(View.VISIBLE);
                break;
            case SlideShow:
                lblSlideshowCurrent.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof MainMenuSettingsEvents) {
            callback = (MainMenuSettingsEvents) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.cvSlideshow:
                callback.onItemSelected(ELiveWallpaperType.SlideShow);
                break;
            case R.id.cvSnowfall:
                callback.onItemSelected(ELiveWallpaperType.SnowFall);
                break;
        }
    }

    public interface MainMenuSettingsEvents {
        void onItemSelected(ELiveWallpaperType type);
    }
}
