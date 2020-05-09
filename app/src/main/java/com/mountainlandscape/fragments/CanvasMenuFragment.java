package com.mountainlandscape.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mountainlandscape.R;
import com.mountainlandscape.interfaces.SimpleItemTouchHelperCallback;
import com.mountainlandscape.adapters.LayerAdapter;
import com.mountainlandscape.adapters.PresetSpinnerAdapter;
import com.mountainlandscape.dialogs.AddEditLayerDialog;
import com.mountainlandscape.dialogs.ColorPickerDialog;
import com.mountainlandscape.dialogs.PresetsDialog;
import com.mountainlandscape.dialogs.SavePresetDialog;
import com.mountainlandscape.interfaces.CanvasOptionsObserver;
import com.mountainlandscape.interfaces.OnStartDragListener;
import com.mountainlandscape.models.CanvasOptions;
import com.mountainlandscape.models.Layer;
import com.mountainlandscape.utilities.PixelUtils;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.defaults.colorpicker.ColorPickerPopup;

public class CanvasMenuFragment extends Fragment implements OnStartDragListener, View.OnClickListener, AddEditLayerDialog.AddLayerDialogEvents, SimpleItemTouchHelperCallback.TouchHelperEvents, LayerAdapter.LayerAdapterEvents, CanvasOptionsObserver {

    @BindView(R.id.rvLayers)
    RecyclerView rvLayers;

    @BindView(R.id.btnSavePreset)
    Button btnSavePreset;

    @BindView(R.id.btnAddLayer)
    Button btnAddLayer;

    @BindView(R.id.seekBarOffset)
    IndicatorSeekBar seekBarOffset;

    @BindView(R.id.relLayStartColor)
    RelativeLayout relLayStartColor;

    @BindView(R.id.txtStartColor)
    TextView txtStartColor;

    @BindView(R.id.viewStartColor)
    View viewStartColor;

    @BindView(R.id.relLayEndColor)
    RelativeLayout relLayEndColor;

    @BindView(R.id.txtEndColor)
    TextView txtEndColor;

    @BindView(R.id.viewEndColor)
    View viewEndColor;

    @BindView(R.id.relLayBackgroundStartColor)
    RelativeLayout relLayBackgroundStartColor;

    @BindView(R.id.relLayBackgroundEndColor)
    RelativeLayout relLayBackgroundEndColor;

    @BindView(R.id.txtBackgroundStartColor)
    TextView txtBackgroundStartColor;

    @BindView(R.id.txtBackgroundEndColor)
    TextView txtBackgroundEndColor;

    @BindView(R.id.viewBackgroundStartColor)
    View viewBackgroundStartColor;

    @BindView(R.id.viewBackgroundEndColor)
    View viewBackgroundEndColor;

    @BindView(R.id.relLayPlanetColor)
    RelativeLayout relLayPlanetColor;

    @BindView(R.id.txtPlanetColor)
    TextView txtPlanetColor;

    @BindView(R.id.viewPlanetColor)
    View viewPlanetColor;

    @BindView(R.id.chkPlanetCrater)
    CheckBox chkPlanetCrater;

    @BindView(R.id.chkShowPlanet)
    CheckBox chkShowPlanet;

    @BindView(R.id.seekBarStars)
    IndicatorSeekBar seekBarStars;

    @BindView(R.id.relLayStarsColor)
    RelativeLayout relLayStarsColor;

    @BindView(R.id.txtStarsColor)
    TextView txtStarsColor;

    @BindView(R.id.viewStarsColor)
    View viewStarsColor;

    @BindView(R.id.chkGradientColor)
    CheckBox chkGradientColor;

    @BindView(R.id.linLayBackgroundEndColor)
    LinearLayout linLayBackgroundEndColor;

    @BindView(R.id.txtBackgroundStartColorLabel)
    TextView txtBackgroundStartColorLabel;

    @BindView(R.id.chkFullScreen)
    CheckBox chkFullScreen;

    @BindView(R.id.txtPreset)
    TextView txtPreset;

    @BindView(R.id.linLayThemePreset)
    LinearLayout linLayThemePreset;

    private ItemTouchHelper mItemTouchHelper;
    private LayerAdapter adapter;
    private CanvasOptions canvasOptions;
    private boolean isCustomSelected = false;
    private CanvasMenuEvents callback;
    PresetSpinnerAdapter spinnerPresetAdapter;

    public CanvasMenuFragment() {
        // Required empty public constructor
    }

    public static CanvasMenuFragment newInstance() {
        CanvasMenuFragment fragment = new CanvasMenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        canvasOptions = CanvasOptions.getInstance(getActivity());
        canvasOptions.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canvas_menu, container, false);
        ButterKnife.bind(this, view);

        txtStartColor.setPaintFlags(txtStartColor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtEndColor.setPaintFlags(txtStartColor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtBackgroundStartColor.setPaintFlags(txtStartColor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtBackgroundEndColor.setPaintFlags(txtEndColor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtPlanetColor.setPaintFlags(txtPlanetColor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtStarsColor.setPaintFlags(txtPlanetColor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtPreset.setPaintFlags(txtPlanetColor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        relLayStartColor.setOnClickListener(this);
        relLayEndColor.setOnClickListener(this);
        relLayBackgroundStartColor.setOnClickListener(this);
        relLayBackgroundEndColor.setOnClickListener(this);
        relLayPlanetColor.setOnClickListener(this);
        relLayStarsColor.setOnClickListener(this);
        linLayThemePreset.setOnClickListener(this);

        chkPlanetCrater.setOnCheckedChangeListener(chkPlanetCraterListener);
        chkGradientColor.setOnCheckedChangeListener(chkGradientListener);
        chkShowPlanet.setOnCheckedChangeListener(chkShowPlanetListener);
        chkFullScreen.setOnCheckedChangeListener(chkFullScreenListener);

        disableSeekBarParentTouch(seekBarOffset);
        disableSeekBarParentTouch(seekBarStars);

        seekBarOffset.setMax(PixelUtils.getDPFromPixels(Resources.getSystem().getDisplayMetrics().heightPixels / 3));

        seekBarOffset.setOnSeekChangeListener(seekBarOffsetListener);
        seekBarStars.setOnSeekChangeListener(seekBarStarsListener);

        btnAddLayer.setOnClickListener(this);
        btnSavePreset.setOnClickListener(this);
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        seekBarOffset.setProgress(canvasOptions.getLayerOffset()); //this is the only value currently changed by orientation change
    }

    private OnSeekChangeListener seekBarStarsListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            canvasOptions.setStarsCount(seekParams.progress);
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            canvasOptions.setNotifyEnabled(false);
        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            canvasOptions.setNotifyEnabled(true);
            canvasOptions.notifyObservers(false, false);
        }
    };

    private OnSeekChangeListener seekBarOffsetListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            canvasOptions.setLayerOffset(seekParams.progress, false);
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

        }
    };

    private CompoundButton.OnCheckedChangeListener chkFullScreenListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if(callback != null) {
                callback.onFullScreenCheckedChange(checked);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener chkPlanetCraterListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            canvasOptions.setHasCraters(checked);
        }
    };

    private CompoundButton.OnCheckedChangeListener chkShowPlanetListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if(checked) {
                chkPlanetCrater.setVisibility(View.VISIBLE);
                relLayPlanetColor.setVisibility(View.VISIBLE);
            } else {
                chkPlanetCrater.setVisibility(View.GONE);
                relLayPlanetColor.setVisibility(View.GONE);
            }

            canvasOptions.setShowPlanet(checked);
        }
    };

    private CompoundButton.OnCheckedChangeListener chkGradientListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if(checked) {
                linLayBackgroundEndColor.setVisibility(View.VISIBLE);
                txtBackgroundStartColorLabel.setText(R.string.background_start_color);
                canvasOptions.setUseGradient(true);
            } else {
                linLayBackgroundEndColor.setVisibility(View.GONE);
                txtBackgroundStartColorLabel.setText(R.string.background_color);
                canvasOptions.setUseGradient(false);
            }
        }
    };

    private void disableSeekBarParentTouch(final IndicatorSeekBar seekBar) {
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                seekBar.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new LayerAdapter(new ArrayList<Layer>(),getActivity(), this, this);
        rvLayers.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvLayers.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvLayers);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CanvasOptions options = new CanvasOptions(getContext());
        options.setAsBlueTheme();
        applyPreset(options);
        txtPreset.setText(canvasOptions.getName());
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnSavePreset:
                //open save preset dialog
                SavePresetDialog.showDialog(getContext(), new SavePresetDialog.SavePresetDialogEvents() {
                    @Override
                    public void onSavePressed(String presetName) {
                        canvasOptions.setName(presetName);
                        canvasOptions.saveCurrentOptions();
                        txtPreset.setText(presetName);
                        String presetToast = String.format(getString(R.string.preset_toast), presetName);
                        Toast.makeText(getContext(), presetToast, Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.btnAddLayer:
                AddEditLayerDialog.showDialog(AddEditLayerDialog.EDialogMode.Add, null, adapter.getItemCount(), getActivity(), this);
                break;
            case R.id.relLayStartColor:
                openStartColorPickerDialog();
                break;
            case R.id.relLayEndColor:
                openEndColorPickerDialog();
                break;
            case R.id.relLayBackgroundStartColor:
                openBackgroundColorPickerDialog(canvasOptions.getBackgroundColorStart(), true);
                break;
            case R.id.relLayBackgroundEndColor:
                openBackgroundColorPickerDialog(canvasOptions.getBackgroundColorEnd(), false);
                break;
            case R.id.relLayPlanetColor:
                openPlanetColorPickerDialog();
                break;
            case R.id.relLayStarsColor:
                openStarsColorPickerDialog();
                break;
            case R.id.linLayThemePreset:
                openThemePresetDialog();
                break;
        }
    }

    private void openThemePresetDialog() {
        PresetsDialog.showDialog(getContext(), new PresetsDialog.PresetsDialogEvents() {
            @Override
            public void onPresetSelected(CanvasOptions preset) {
                applyPreset(preset);
            }

            @Override
            public void onPresetDeleted(CanvasOptions preset) {
                if(preset.getName().equals(canvasOptions.getName())) {
                    txtPreset.setText(getResources().getString(R.string.custom));
                }
            }
        });
    }

    private void applyPreset(CanvasOptions preset) {
        CanvasOptions.applyPreset(preset);

        viewStartColor.setBackgroundColor(canvasOptions.getStartColor());
        viewEndColor.setBackgroundColor(canvasOptions.getEndColor());
        viewBackgroundStartColor.setBackgroundColor(canvasOptions.getBackgroundColorStart());
        viewBackgroundEndColor.setBackgroundColor(canvasOptions.getBackgroundColorEnd());
        viewPlanetColor.setBackgroundColor(canvasOptions.getPlanetColor());
        viewStarsColor.setBackgroundColor(canvasOptions.getStarsColor());

        //remove listener to prevent triggering a drawcanvas event
        chkPlanetCrater.setOnCheckedChangeListener(null);
        chkGradientColor.setOnCheckedChangeListener(null);
        chkShowPlanet.setOnCheckedChangeListener(null);

        chkPlanetCrater.setChecked(canvasOptions.hasCraters());
        chkGradientColor.setChecked(canvasOptions.isUseGradient());
        chkShowPlanet.setChecked(canvasOptions.isShowPlanet());

        chkPlanetCrater.setOnCheckedChangeListener(chkPlanetCraterListener);
        chkGradientColor.setOnCheckedChangeListener(chkGradientListener);
        chkShowPlanet.setOnCheckedChangeListener(chkShowPlanetListener);

        if (canvasOptions.isUseGradient()) {
            linLayBackgroundEndColor.setVisibility(View.VISIBLE);
        } else {
            linLayBackgroundEndColor.setVisibility(View.GONE);
        }

        adapter.clear();
        adapter.addAllItems(canvasOptions.getLayers());

        //remove seek change listener to prevent triggering draw canvas event
        seekBarOffset.setOnSeekChangeListener(null);
        seekBarStars.setOnSeekChangeListener(null);

        seekBarOffset.setProgress(canvasOptions.getLayerOffset());
        seekBarStars.setProgress(canvasOptions.getStarsCount());

        seekBarOffset.setOnSeekChangeListener(seekBarOffsetListener);
        seekBarStars.setOnSeekChangeListener(seekBarStarsListener);

        txtPreset.setText(canvasOptions.getName());


    }

    private void openBackgroundColorPickerDialog(int color, final boolean isStartColor) {
        ColorPickerDialog.showDialog(color, getView(), getActivity(), new ColorPickerPopup.ColorPickerObserver() {
            @Override
            public void onColorPicked(int color) {
                if(canvasOptions.isUseGradient()) {
                    if(isStartColor) {
                        canvasOptions.setBackgroundColorStart(color);
                        viewBackgroundStartColor.setBackgroundColor(color);
                    } else {
                        canvasOptions.setBackgroundColorEnd(color);
                        viewBackgroundEndColor.setBackgroundColor(color);
                    }
                } else {
                    canvasOptions.setBackgroundColor(color);
                    viewBackgroundStartColor.setBackgroundColor(color);
                }
            }

            @Override
            public void onColor(int color, boolean fromUser) {

            }
        });
    }

    private void openStartColorPickerDialog() {
        ColorPickerDialog.showDialog(canvasOptions.getStartColor(), getView(), getActivity(), new ColorPickerPopup.ColorPickerObserver() {
            @Override
            public void onColorPicked(int color) {
                canvasOptions.setStartColor(color);
                viewStartColor.setBackgroundColor(color);
            }

            @Override
            public void onColor(int color, boolean fromUser) {

            }
        });
    }

    private void openEndColorPickerDialog() {
        ColorPickerDialog.showDialog(canvasOptions.getEndColor(), getView(), getActivity(), new ColorPickerPopup.ColorPickerObserver() {
            @Override
            public void onColorPicked(int color) {
                canvasOptions.setEndColor(color);
                viewEndColor.setBackgroundColor(color);
            }

            @Override
            public void onColor(int color, boolean fromUser) {

            }
        });
    }

    private void openPlanetColorPickerDialog() {
        ColorPickerDialog.showDialog(canvasOptions.getPlanetColor(), getView(), getActivity(), new ColorPickerPopup.ColorPickerObserver() {
            @Override
            public void onColorPicked(int color) {
                canvasOptions.setPlanetColor(color);
                viewPlanetColor.setBackgroundColor(color);
            }

            @Override
            public void onColor(int color, boolean fromUser) {

            }
        });
    }

    private void openStarsColorPickerDialog() {
        ColorPickerDialog.showDialog(canvasOptions.getStarsColor(), getView(), getActivity(), new ColorPickerPopup.ColorPickerObserver() {
            @Override
            public void onColorPicked(int color) {
                canvasOptions.setStarsColor(color);
                viewStarsColor.setBackgroundColor(color);
            }

            @Override
            public void onColor(int color, boolean fromUser) {

            }
        });
    }


    @Override
    public void onSaveLayerPressed(Layer layer) {
        canvasOptions.addLayer(layer);
    }

    @Override
    public void onDeleteLayerPressed(Layer layer) {
        //Should never be triggered here. we don't delete in this context.
    }

    @Override
    public void onItemMoved(int from, int to) {
        List<Layer> layers = adapter.getItems();
        canvasOptions.setLayers(layers);
    }

    @Override
    public void onLayerUpdated(Layer layer) {
        List<Layer> layers = adapter.getItems();
        canvasOptions.setLayers(layers);
    }

    @Override
    public void onCanvasOptionsUpdated(boolean layerUpdated, boolean isPreset) {
        if(!isPreset) {
            txtPreset.setText("Custom");

            if(layerUpdated) {
                List<Layer> layers = new ArrayList<>(canvasOptions.getLayers());
                adapter.clear();
                adapter.addAllItems(layers);
            }
        }


    }

    public void setCanvasMenuCallback(CanvasMenuEvents callback) {
        this.callback = callback;
    }

    @Override
    public void onLayerOffsetUpdated(boolean isPreset) {
        if(!isPreset) {
            txtPreset.setText("Custom");
        }
    }

    public interface CanvasMenuEvents {
        void onFullScreenCheckedChange(boolean isChecked);
    }
}
