package com.mountainlandscape;

import android.app.Application;

import com.mountainlandscape.preferences.PrefUtils;
import com.mountainlandscape.preferences.Prefs;

public class App extends Application {

    public static boolean isPro = false;

    @Override
    public void onCreate() {
        super.onCreate();

        isPro = PrefUtils.get(this, Prefs.IS_PRO, false);
    }
}
