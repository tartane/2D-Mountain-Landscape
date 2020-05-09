package com.mountainlandscape.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.mountainlandscape.R;

public class AboutDialog {

    public static void showDialog(Context mContext) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_about);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
