package com.mountainlandscape.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mountainlandscape.R;

public class ConfirmDeleteItemDialog {
    public static void showDialog(Context mContext, String itemName, final ConfirmDeleteItemDialogEvents callback) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_confirm_delete_item);

        TextView txtQuestion = dialog.findViewById(R.id.txtQuestion);
        txtQuestion.setText(String.format(mContext.getString(R.string.are_you_sure_you_want_to_delete), itemName));

        Button btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPositiveButtonClicked();
            }
        });

        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface ConfirmDeleteItemDialogEvents {
        void onPositiveButtonClicked();
    }
}
