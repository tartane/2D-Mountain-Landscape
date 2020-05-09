package com.mountainlandscape.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.mountainlandscape.R;
import com.mountainlandscape.models.Layer;

public class AddEditLayerDialog {

    public enum EDialogMode {
        Add,
        Edit
    }

    public static void showDialog(EDialogMode mode, final Layer editLayer, int currentLayersCount, final Context mContext, final AddLayerDialogEvents callback) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_add_edit_layer);

        final EditText edtLayerName = dialog.findViewById(R.id.edtLayerName);
        final AppCompatRadioButton rbLayerMountain1 = dialog.findViewById(R.id.chkLayerMountain1);
        final AppCompatRadioButton rbLayerMountain2 = dialog.findViewById(R.id.chkLayerMountain2);
        final AppCompatRadioButton rbLayerMountain3 = dialog.findViewById(R.id.chkLayerMountain3);

        TextView txtDialogTitle = dialog.findViewById(R.id.txtDialogTitle);

        if(mode == EDialogMode.Add) {
            txtDialogTitle.setText(R.string.add_layer);
            edtLayerName.setText(mContext.getString(R.string.layer) + " " + String.valueOf(currentLayersCount + 1));
        } else {
            txtDialogTitle.setText(R.string.edit_layer);
            edtLayerName.setText(editLayer.getLayerName());
        }

        if(editLayer != null) {
            if (editLayer.getResourceId() == R.drawable.mountain_layer_1_crop) {
                rbLayerMountain1.setChecked(true);
            } else if (editLayer.getResourceId() == R.drawable.mountain_layer_2_crop) {
                rbLayerMountain2.setChecked(true);
            } else if (editLayer.getResourceId() == R.drawable.mountain_layer_3_crop) {
                rbLayerMountain3.setChecked(true);
            }
        }

        Button btnSaveLayer = dialog.findViewById(R.id.btnSaveLayer);
        btnSaveLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateLayerName(edtLayerName, mContext)) {
                    Layer layer = new Layer();

                    layer.setLayerName(edtLayerName.getText().toString());

                    if(rbLayerMountain1.isChecked()) {
                        layer.setResourceId(R.drawable.mountain_layer_1_crop);
                        layer.setShadowResourceId(0);
                    } else if(rbLayerMountain2.isChecked()) {
                        layer.setResourceId(R.drawable.mountain_layer_2_crop);
                        layer.setShadowResourceId(0);
                    } else if(rbLayerMountain3.isChecked()) {
                        layer.setResourceId(R.drawable.mountain_layer_3_crop);
                        layer.setShadowResourceId(R.drawable.mountain_layer_3_shadow_crop);
                    } else {
                        Toast.makeText(mContext, R.string.error_must_select_layer_type, Toast.LENGTH_LONG).show();
                        return;
                    }
                    /*
                    //TODO change this for a localized solution
                    String selectedLayer = spinnerLayers.getSelectedItem().toString();
                    if(selectedLayer.equals("Mountain 1")) {
                        layer.setResourceId(R.drawable.mountain_layer_1_crop);
                        layer.setShadowResourceId(0);
                    } else if(selectedLayer.equals("Mountain 2")) {
                        layer.setResourceId(R.drawable.mountain_layer_2_crop);
                        layer.setShadowResourceId(0);
                    } else if(selectedLayer.equals("Mountain 3")) {
                        layer.setResourceId(R.drawable.mountain_layer_3_crop);
                        layer.setShadowResourceId(R.drawable.mountain_layer_3_shadow_crop);
                    }*/

                    callback.onSaveLayerPressed(layer);
                    dialog.dismiss();
                }
            }

        });

        Button btnDelete = dialog.findViewById(R.id.btnDelete);
        if(mode == EDialogMode.Edit) {
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YesNoDialog.showDialog(mContext.getString(R.string.confirm_delete_layer) + " " + editLayer.getLayerName(), mContext, new YesNoDialog.YesNoDialogCallbacks() {
                        @Override
                        public void onPositiveButtonClicked() {
                            callback.onDeleteLayerPressed(editLayer);
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegativeButtonClicked() {

                        }
                    });
                }
            });
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    
    private static boolean validateLayerName(EditText edtLayerName, Context context) {
        if(edtLayerName.getText().toString().equals("")) {
            edtLayerName.setError(context.getString(R.string.error_layer_name_required));
            return false;
        } else if(edtLayerName.getText().length() > 50) {
            edtLayerName.setError(context.getString(R.string.error_layer_name_greater_than_50));
            return false;
        }
        
        return true;
    }


    public interface AddLayerDialogEvents {
        void onSaveLayerPressed(Layer layer);
        void onDeleteLayerPressed(Layer layer);
    }


}
