package com.acxiom.librarymgmt;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class LibraryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Force Light Mode for the entire application
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
