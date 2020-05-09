package com.mountainlandscape.dialogs;

import android.content.Context;
import android.view.View;

import top.defaults.colorpicker.ColorPickerPopup;

public class ColorPickerDialog {
    public static void showDialog(int initialColor, View parentView, Context mContext, final ColorPickerPopup.ColorPickerObserver observer) {
        new ColorPickerPopup.Builder(mContext)
                .initialColor(initialColor) // Set initial color
                .enableAlpha(false) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(parentView, observer);
    }
}
