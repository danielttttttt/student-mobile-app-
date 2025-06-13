package com.example.student3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.student3.R;
import com.example.student3.utils.BackgroundNotificationManager;
import com.example.student3.utils.BatteryMonitorUtil;
import com.example.student3.utils.NotificationHelper;
import com.example.student3.utils.LocaleUtils;
import com.example.student3.utils.UserSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private UserSession userSession;
    private NotificationHelper notificationHelper;
    private BackgroundNotificationManager backgroundNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FORCE LIGHT THEME - Disable dark mode completely
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize UserSession
        userSession = new UserSession(this);

        // Initialize NotificationHelper
        notificationHelper = new NotificationHelper(this);

        // Initialize BackgroundNotificationManager
        backgroundNotificationManager = new BackgroundNotificationManager(this);

        // Check if user is logged in
        if (!userSession.isLoggedIn()) {
            Log.d(TAG, "User not logged in, redirecting to login");
            // User is not logged in, redirect to login activity
            Intent intent = new Intent(this, SecureLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Log.d(TAG, "User is logged in: " + userSession.getCurrentUserEmail());

        // Start background notification checking for logged-in users
        if (backgroundNotificationManager.isBackgroundCheckingEnabled()) {
            backgroundNotificationManager.startBackgroundChecking();
            Log.d(TAG, "Background notification checking started");
        }

        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Get the NavHostFragment and its NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found!");
        }
        NavController navController = navHostFragment.getNavController();

        // Set up the BottomNavigationView with the NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Initialize battery monitoring
        initializeBatteryMonitoring();
    }

    /**
     * Initialize battery monitoring functionality
     * This demonstrates BroadcastReceiver usage for system events
     */
    private void initializeBatteryMonitoring() {
        try {
            // Register dynamic battery receiver for detailed monitoring
            BatteryMonitorUtil.registerBatteryReceiver(this);

            // Log current battery status for debugging
            String batteryStatus = BatteryMonitorUtil.getBatteryStatusDescription(this);
            Log.d(TAG, "Current battery status: " + batteryStatus);

            // Check if we should conserve battery and adjust app behavior
            if (BatteryMonitorUtil.shouldConserveBattery(this)) {
                Log.i(TAG, "Battery conservation mode activated");
                // In a real app, you might:
                // - Reduce background sync frequency
                // - Disable non-essential animations
                // - Postpone heavy operations
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize battery monitoring", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check authentication status when activity resumes
        if (!userSession.isLoggedIn()) {
            Log.d(TAG, "User session expired, redirecting to login");
            Intent intent = new Intent(this, SecureLoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_language_english) {
            LocaleUtils.setLocale(this, LocaleUtils.LANGUAGE_ENGLISH);
            recreate(); // Restart activity to apply language change
            return true;
        } else if (id == R.id.action_language_amharic) {
            LocaleUtils.setLocale(this, LocaleUtils.LANGUAGE_AMHARIC);
            recreate(); // Restart activity to apply language change
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean up battery monitoring
        try {
            BatteryMonitorUtil.unregisterBatteryReceiver(this);
            Log.d(TAG, "Battery monitoring cleaned up");
        } catch (Exception e) {
            Log.e(TAG, "Failed to cleanup battery monitoring", e);
        }
    }
}