package com.mountainlandscape.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.mountainlandscape.R;

public class SavePresetDialog {
    
    public static void showDialog(final Context context, final SavePresetDialogEvents callback) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_save_preset);

        Button btnSavePreset = dialog.findViewById(R.id.btnSavePreset);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        final EditText edtPresetName = dialog.findViewById(R.id.edtPresetName);

        btnSavePreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edtPresetName.getText().toString().equals("")) {
                    callback.onSavePressed(edtPresetName.getText().toString());
                    dialog.dismiss();
                } else {
                    edtPresetName.setError(context.getString(R.string.error_preset_name_required));
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        
        dialog.show();

    }

    public interface SavePresetDialogEvents {
        void onSavePressed(String presetName);
    }
}
