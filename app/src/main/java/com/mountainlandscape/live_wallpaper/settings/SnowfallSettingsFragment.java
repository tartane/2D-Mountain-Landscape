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
import com.mountainlandscape.live_wallpaper.SnowFlake;
import com.mountainlandscape.preferences.PrefUtils;
import com.mountainlandscape.preferences.Prefs;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.bendik.simplerangeview.SimpleRangeView;

public class SnowfallSettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.txtSnowFlakesSpeed)
    TextView txtSnowFlakesSpeed;

    @BindView(R.id.snowFlakesSpeedRangeBar)
    SimpleRangeView snowFlakesSpeedRangeBar;

    @BindView(R.id.txtSnowFlakesSize)
    TextView txtSnowFlakesSize;

    @BindView(R.id.snowFlakesSizeRangeBar)
    SimpleRangeView snowFlakesSizeRangeBar;

    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.btnRestoreDefault)
    Button btnRestoreDefault;

    @BindView(R.id.seekBarSnowflakeCount)
    IndicatorSeekBar seekBarSnowflakeCount;

    @BindView(R.id.txtSnowflakeCount)
    TextView txtSnowflakeCount;

    @BindView(R.id.spinnerFPS)
    Spinner spinnerFPS;

    public SnowfallSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snowfall_settings, container, false);
        ButterKnife.bind(this, view);

        toolbar.setTitle(R.string.snowfall_settings);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        btnApply.setOnClickListener(this);
        btnRestoreDefault.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setupFPSSpinner();
        setupSnowflakesSizeRangeBar();
        setupSnowflakesSpeedRangeBar();
        setupSnowflakesCountSeekBar();
    }

    private void setupFPSSpinner() {
        int fps = PrefUtils.get(getContext(), Prefs.FPS, 45);
        switch(fps) {
            case 15:
                spinnerFPS.setSelection(0);
                break;
            case 30:
                spinnerFPS.setSelection(1);
                break;
            case 45:
                spinnerFPS.setSelection(2);
                break;
            case 60:
                spinnerFPS.setSelection(3);
                break;
        }
    }

    private int getFPSSpinnerValue() {
        switch(spinnerFPS.getSelectedItemPosition()) {
            case 0:
                return 15;
            case 1:
                return 30;
            case 2:
                return 45;
            case 3:
                return 60;
            default:
                return 30;
        }
    }

    private void setupSnowflakesCountSeekBar() {
        int count = PrefUtils.get(getContext(), Prefs.SNOWFLAKE_COUNT, SnowFlake.COUNT_DEFAULT);

        txtSnowflakeCount.setText("Snowflake count (" + count + ")");

        seekBarSnowflakeCount.setProgress(count);

        seekBarSnowflakeCount.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                txtSnowflakeCount.setText("Snowflake count (" + seekBarSnowflakeCount.getProgress() + ")");
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
    }

    private void setupSnowflakesSizeRangeBar() {
        int snowMinSize = PrefUtils.get(getContext(), Prefs.SNOW_MIN_SIZE, SnowFlake.MIN_RADIUS_DEFAULT);
        int snowMaxSize = PrefUtils.get(getContext(), Prefs.SNOW_MAX_SIZE, SnowFlake.MAX_RADIUS_DEFAULT);

        txtSnowFlakesSize.setText("Snow flakes size range (" + snowMinSize + ", " + snowMaxSize + ")");

        snowFlakesSizeRangeBar.setStart(snowMinSize);
        snowFlakesSizeRangeBar.setEnd(snowMaxSize);

        snowFlakesSizeRangeBar.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener() {
            @Override
            public void onStartRangeChanged(@NotNull SimpleRangeView rangeView, int start) {
                txtSnowFlakesSize.setText("Snow flakes size range (" + start + ", " + rangeView.getEnd() + ")");
            }

            @Override
            public void onEndRangeChanged(@NotNull SimpleRangeView rangeView, int end) {
                txtSnowFlakesSize.setText("Snow flakes size range (" + rangeView.getStart() + ", " + end + ")");
            }
        });

    }

    private void setupSnowflakesSpeedRangeBar() {
        int snowMinSpeed = PrefUtils.get(getContext(), Prefs.SNOW_MIN_SPEED, SnowFlake.MIN_SPEED_DEFAULT);
        int snowMaxSpeed = PrefUtils.get(getContext(), Prefs.SNOW_MAX_SPEED, SnowFlake.MAX_SPEED_DEFAULT);

        txtSnowFlakesSpeed.setText("Snow flakes fall speed range (" + snowMinSpeed + ", " + snowMaxSpeed + ")");

        snowFlakesSpeedRangeBar.setStart(snowMinSpeed);
        snowFlakesSpeedRangeBar.setEnd(snowMaxSpeed);

        snowFlakesSpeedRangeBar.setOnTrackRangeListener(new SimpleRangeView.OnTrackRangeListener() {
            @Override
            public void onStartRangeChanged(@NotNull SimpleRangeView rangeView, int start) {
                txtSnowFlakesSpeed.setText("Snow flakes fall speed range (" + start + ", " + rangeView.getEnd() + ")");
            }

            @Override
            public void onEndRangeChanged(@NotNull SimpleRangeView rangeView, int end) {
                txtSnowFlakesSpeed.setText("Snow flakes fall speed range (" + rangeView.getStart() + ", " + end + ")");
            }
        });
    }

    private void saveSettingsToPrefs() {
        PrefUtils.save(getContext(), Prefs.FPS, getFPSSpinnerValue());

        PrefUtils.save(getContext(), Prefs.SNOW_MIN_SPEED, snowFlakesSpeedRangeBar.getStart());
        PrefUtils.save(getContext(), Prefs.SNOW_MAX_SPEED, snowFlakesSpeedRangeBar.getEnd());

        PrefUtils.save(getContext(), Prefs.SNOW_MIN_SIZE, snowFlakesSizeRangeBar.getStart());
        PrefUtils.save(getContext(), Prefs.SNOW_MAX_SIZE, snowFlakesSizeRangeBar.getEnd());

        PrefUtils.save(getContext(), Prefs.SNOWFLAKE_COUNT, seekBarSnowflakeCount.getProgress());

        //save that the user is now using the snowfall.
        PrefUtils.save(getContext(), Prefs.CURRENT_LIVE_WALLPAPER_TYPE, ELiveWallpaperType.SnowFall.getId());
    }

    private void restoreDefault() {
        spinnerFPS.setSelection(2);

        snowFlakesSizeRangeBar.setStart(SnowFlake.MIN_RADIUS_DEFAULT);
        snowFlakesSizeRangeBar.setEnd(SnowFlake.MAX_RADIUS_DEFAULT);

        snowFlakesSpeedRangeBar.setStart(SnowFlake.MIN_SPEED_DEFAULT);
        snowFlakesSpeedRangeBar.setEnd(SnowFlake.MAX_SPEED_DEFAULT);

        seekBarSnowflakeCount.setProgress(SnowFlake.COUNT_DEFAULT);

        txtSnowflakeCount.setText("Snowflake count (" + SnowFlake.COUNT_DEFAULT + ")");
        txtSnowFlakesSpeed.setText("Snow flakes fall speed range (" + SnowFlake.MIN_SPEED_DEFAULT + ", " + SnowFlake.MAX_SPEED_DEFAULT + ")");
        txtSnowFlakesSize.setText("Snow flakes size range (" + SnowFlake.MIN_RADIUS_DEFAULT + ", " + SnowFlake.MAX_RADIUS_DEFAULT + ")");
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
