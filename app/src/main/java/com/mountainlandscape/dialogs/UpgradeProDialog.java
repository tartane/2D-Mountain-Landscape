package com.mountainlandscape.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import com.mountainlandscape.R;

public class UpgradeProDialog {

    public static void showDialog(Context mContext, final UpgradeProDialogEvents callback) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_upgrade_pro);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnUpgradePro = dialog.findViewById(R.id.btnUpgradePro);
        btnUpgradePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onUpgradeProClicked();
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.onUpgradeProDismissed();
            }
        });

        dialog.show();
    }

    public interface UpgradeProDialogEvents {
        void onUpgradeProClicked();
        void onUpgradeProDismissed();
    }
}
