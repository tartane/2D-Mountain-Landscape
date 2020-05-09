package com.mountainlandscape.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mountainlandscape.R;

public class SaveDialog {

    public enum ESaveType {
        ConfigureLiveWallpaper,
        SaveAndSetAsWallpaper,
        SaveOnDevice,
        SetAsWallpaper,
        Dismiss
    }

    public static void showDialog(final Context context, final SaveDialogEvents callback) {
        final CharSequence[] items = {context.getString(R.string.configure_live_wallpaper), context.getString(R.string.save_and_set_as_wallpaper), context.getString(R.string.save_on_device), context.getString(R.string.set_as_wallpaper), context.getString(R.string.cancel)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if(items[position].equals(context.getString(R.string.configure_live_wallpaper))) {
                    callback.onActionSelected(ESaveType.ConfigureLiveWallpaper);
                } else if(items[position].equals(context.getString(R.string.save_and_set_as_wallpaper))){
                    callback.onActionSelected(ESaveType.SaveAndSetAsWallpaper);
                } else if(items[position].equals(context.getString(R.string.save_on_device))) {
                    callback.onActionSelected(ESaveType.SaveOnDevice);
                } else if(items[position].equals(context.getString(R.string.set_as_wallpaper))) {
                    callback.onActionSelected(ESaveType.SetAsWallpaper);
                } else if(items[position].equals(context.getString(R.string.cancel))) {
                    callback.onActionSelected(ESaveType.Dismiss);
                }
            }
        });
        builder.setTitle(R.string.save_options);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                callback.onActionSelected(ESaveType.Dismiss);
            }
        });

        builder.create().show();
    }

    public interface SaveDialogEvents {
        void onActionSelected(ESaveType saveType);
    }
}
