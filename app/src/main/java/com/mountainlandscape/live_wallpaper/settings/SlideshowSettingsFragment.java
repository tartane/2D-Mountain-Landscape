package com.mountainlandscape.live_wallpaper.settings;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mountainlandscape.App;
import com.mountainlandscape.R;
import com.mountainlandscape.live_wallpaper.ELiveWallpaperType;
import com.mountainlandscape.preferences.PrefUtils;
import com.mountainlandscape.preferences.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SlideshowSettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.btnRestoreDefault)
    Button btnRestoreDefault;

    @BindView(R.id.txtTransitionTime)
    TextView txtTransitionTime;

    @BindView(R.id.spinnerTransitionTime)
    Spinner spinnerTransitionTime;

    private final int DEFAULT_TRANSITION_TIME = 30; //in seconds

    public SlideshowSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slideshow_settings, container, false);
        ButterKnife.bind(this, view);

        toolbar.setTitle(R.string.slideshow_settings);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btnApply.setOnClickListener(this);
        btnRestoreDefault.setOnClickListener(this);

        initTransitionTimeSpinner();

        return view;
    }

    private void initTransitionTimeSpinner() {
        int transitionTime = PrefUtils.get(getContext(), Prefs.TRANSITION_TIME, DEFAULT_TRANSITION_TIME);

        switch(transitionTime) {
            case 5:
                spinnerTransitionTime.setSelection(0);
                break;
            case 15:
                spinnerTransitionTime.setSelection(1);
                break;
            case 30:
                spinnerTransitionTime.setSelection(2);
                break;
            case 60:
                spinnerTransitionTime.setSelection(3);
                break;
            case 60 * 5:
                spinnerTransitionTime.setSelection(4);
                break;
            case 60 * 15:
                spinnerTransitionTime.setSelection(5);
                break;
            case 60 * 30:
                spinnerTransitionTime.setSelection(6);
                break;
            case 60 * 60:
                spinnerTransitionTime.setSelection(7);
                break;
            case 60 * 60 * 5:
                spinnerTransitionTime.setSelection(8);
                break;
            case 60 * 60 * 24:
                spinnerTransitionTime.setSelection(9);
                break;
        }

    }

    private void setSelectionTransitionSpinner() {

    }

    //value in seconds
    private int getValueFromSpinner() {
        switch(spinnerTransitionTime.getSelectedItemPosition()) {
            case 0:
                return 5;
            case 1:
                return 15;
            case 2:
                return 30;
            case 3:
                return 60;
            case 4:
                return 60 * 5;
            case 5:
                return 60 * 15;
            case 6:
                return 60 * 30;
            case 7:
                return 60 * 60;
            case 8:
                return 60 * 60 * 5;
            case 9:
                return 60 * 60 * 24;
        }

        return 30;
    }

    private void saveSettingsToPrefs() {
        PrefUtils.save(getContext(), Prefs.TRANSITION_TIME, getValueFromSpinner());

        //save that the user is now using the snowfall.
        PrefUtils.save(getContext(), Prefs.CURRENT_LIVE_WALLPAPER_TYPE, ELiveWallpaperType.SlideShow.getId());
    }

    private void restoreDefault() {
        spinnerTransitionTime.setSelection(2);

        PrefUtils.save(getContext(), Prefs.TRANSITION_TIME, DEFAULT_TRANSITION_TIME);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnApply:
                if(App.isPro) {
                    saveSettingsToPrefs();
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), getString(R.string.upgrade_pro_access_feature), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnRestoreDefault:
                restoreDefault();
                break;
        }
    }
}
