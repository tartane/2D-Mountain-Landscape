package com.mountainlandscape.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mountainlandscape.R;

public class YesNoDialog {

    public static void showDialog(String question, Context context, final YesNoDialogCallbacks callback) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_yesno);

        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        TextView txtQuestion = dialog.findViewById(R.id.txtQuestion);

        txtQuestion.setText(question);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPositiveButtonClicked();
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onNegativeButtonClicked();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    interface YesNoDialogCallbacks {
        void onPositiveButtonClicked();
        void onNegativeButtonClicked();
    }
}
