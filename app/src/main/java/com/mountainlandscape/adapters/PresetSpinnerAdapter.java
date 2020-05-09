package com.mountainlandscape.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mountainlandscape.R;
import com.mountainlandscape.dialogs.ConfirmDeleteItemDialog;
import com.mountainlandscape.models.CanvasOptions;

import java.util.List;

public class PresetSpinnerAdapter extends ArrayAdapter<CanvasOptions> {
    private Context context;
    private List<CanvasOptions> canvasOptionsList;
    private LayoutInflater inflater;
    public PresetSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<CanvasOptions> objects) {
        super(context, resource, objects);
        this.context = context;
        this.canvasOptionsList = objects;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return canvasOptionsList.size();
    }

    @Override
    public CanvasOptions getItem(int position){
        return canvasOptionsList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CanvasOptions canvasOptions = canvasOptionsList.get(position);

        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.WHITE);
        label.setText(canvasOptions.getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        final CanvasOptions canvasOptions = canvasOptionsList.get(position);
        View row = inflater.inflate(R.layout.row_preset_spinner_dropdown, parent, false);
        TextView txtPresetName = row.findViewById(R.id.txtPresetName);
        txtPresetName.setText(canvasOptions.getName());
        RelativeLayout relLayDeletePreset = row.findViewById(R.id.relLayDeletePreset);
        if(canvasOptions.isUserPreset()) {
            relLayDeletePreset.setVisibility(View.VISIBLE);
            relLayDeletePreset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmDeleteItemDialog.showDialog(context, canvasOptions.getName(), new ConfirmDeleteItemDialog.ConfirmDeleteItemDialogEvents() {
                        @Override
                        public void onPositiveButtonClicked() {

                        }
                    });
                }
            });
        } else {
            relLayDeletePreset.setVisibility(View.INVISIBLE);
        }
        return row;
    }
}
