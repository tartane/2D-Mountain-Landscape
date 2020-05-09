package com.mountainlandscape.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mountainlandscape.R;
import com.mountainlandscape.adapters.PresetsAdapter;
import com.mountainlandscape.models.CanvasOptions;
import com.mountainlandscape.models.PresetsAdapterViewModel;

import java.util.ArrayList;
import java.util.List;

public class PresetsDialog {

    public static void showDialog(final Context context, final PresetsDialogEvents callback) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_presets);

        List<PresetsAdapterViewModel> presets = loadPresets(context);

        final PresetsAdapter adapter = new PresetsAdapter(context, presets);
        adapter.setOnItemClickListener(new PresetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, CanvasOptions preset, int position) {
                callback.onPresetSelected(preset);
                dialog.dismiss();
            }

            @Override
            public void onItemDeleteClick(CanvasOptions item, int position) {
                adapter.deleteItem(position);
                callback.onPresetDeleted(item);
            }
        });
        RecyclerView rvPresets = dialog.findViewById(R.id.rvPresets);
        rvPresets.setLayoutManager(new LinearLayoutManager(context));
        rvPresets.setAdapter(adapter);


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        dialog.getWindow().setLayout((int) (metrics.widthPixels * .9), ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private static List<PresetsAdapterViewModel> loadPresets(Context context) {
        CanvasOptions canvasOptions = CanvasOptions.getInstance(context);
        canvasOptions.loadSavedCanvasOptions();

        List<CanvasOptions> customPresets = new ArrayList<>(canvasOptions.getSavedCanvasOptions());
        List<CanvasOptions> presets = new ArrayList<>();

        CanvasOptions options = new CanvasOptions(context);
        options.setAsBlueTheme();
        presets.add(options);

        options = new CanvasOptions(context);
        options.setAsBlueWinterTheme();
        presets.add(options);

        options = new CanvasOptions(context);
        options.setAsRedTheme();
        presets.add(options);

        options = new CanvasOptions(context);
        options.setAsGreenTheme();
        presets.add(options);

        options = new CanvasOptions(context);
        options.setAsPurpleTheme();
        presets.add(options);


        List<PresetsAdapterViewModel> presetViewModels = new ArrayList<>();
        presetViewModels.add(new PresetsAdapterViewModel("Presets"));
        for(int i = 0; i < presets.size(); i ++) {
            presetViewModels.add(new PresetsAdapterViewModel(presets.get(i), PresetsAdapter.TYPE_PRESET));
        }
        presetViewModels.add(new PresetsAdapterViewModel("Your Custom Presets"));
        for(int i = 0; i < customPresets.size(); i ++) {
            presetViewModels.add(new PresetsAdapterViewModel(customPresets.get(i), PresetsAdapter.TYPE_CUSTOM_PRESET));
        }

        if(customPresets.size() <= 0) {
            presetViewModels.add(new PresetsAdapterViewModel(null, PresetsAdapter.TYPE_NO_CUSTOM));
        }

        return presetViewModels;

    }

    public interface PresetsDialogEvents {
        void onPresetSelected(CanvasOptions preset);
        void onPresetDeleted(CanvasOptions preset);
    }
}
