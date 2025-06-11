package com.example.student3;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Application class to force light theme globally
 * This prevents any dark theme related crashes
 */
public class StudentApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // FORCE LIGHT THEME GLOBALLY - This will prevent all dark theme crashes
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
