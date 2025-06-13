package com.example.student3.ui.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.student3.R;
import com.example.student3.utils.BatteryMonitorUtil;

/**
 * Fragment to demonstrate battery monitoring functionality
 * 
 * This fragment shows:
 * - Current battery level and status
 * - Battery monitoring capabilities
 * - How the app responds to battery changes
 * 
 * Educational Purpose: Demonstrates BroadcastReceiver integration
 * and battery-aware app development.
 */
public class BatteryStatusFragment extends Fragment {
    
    private TextView batteryLevelText;
    private TextView batteryStatusText;
    private TextView batteryAdviceText;
    private Button refreshButton;
    private Handler handler;
    private Runnable updateRunnable;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battery_status, container, false);
        
        initializeViews(view);
        setupUpdateHandler();
        updateBatteryInfo();
        
        return view;
    }
    
    private void initializeViews(View view) {
        batteryLevelText = view.findViewById(R.id.battery_level_text);
        batteryStatusText = view.findViewById(R.id.battery_status_text);
        batteryAdviceText = view.findViewById(R.id.battery_advice_text);
        refreshButton = view.findViewById(R.id.refresh_button);
        
        refreshButton.setOnClickListener(v -> updateBatteryInfo());
    }
    
    private void setupUpdateHandler() {
        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateBatteryInfo();
                // Update every 30 seconds while fragment is visible
                handler.postDelayed(this, 30000);
            }
        };
    }
    
    private void updateBatteryInfo() {
        if (getContext() == null) return;
        
        // Get current battery information
        int batteryLevel = BatteryMonitorUtil.getCurrentBatteryLevel(getContext());
        String batteryStatus = BatteryMonitorUtil.getBatteryStatusDescription(getContext());
        boolean isCharging = BatteryMonitorUtil.isCharging(getContext());
        boolean shouldConserve = BatteryMonitorUtil.shouldConserveBattery(getContext());
        
        // Update UI
        if (batteryLevel != -1) {
            batteryLevelText.setText(String.format("Battery Level: %d%%", batteryLevel));
        } else {
            batteryLevelText.setText("Battery Level: Unknown");
        }
        
        batteryStatusText.setText("Status: " + batteryStatus);
        
        // Provide battery advice
        String advice = getBatteryAdvice(batteryLevel, isCharging, shouldConserve);
        batteryAdviceText.setText(advice);
    }
    
    private String getBatteryAdvice(int batteryLevel, boolean isCharging, boolean shouldConserve) {
        if (batteryLevel == -1) {
            return "Unable to determine battery status.";
        }
        
        if (isCharging) {
            return "✓ Device is charging. All app features are available.";
        } else if (batteryLevel < 10) {
            return "⚠️ Critical battery level! Please charge your device immediately. " +
                   "The app may limit background operations to preserve battery.";
        } else if (batteryLevel < 20) {
            return "⚠️ Low battery level. Consider charging your device soon. " +
                   "Some background features may be reduced to save power.";
        } else if (shouldConserve) {
            return "💡 Battery conservation mode is active. " +
                   "Background sync and notifications may be reduced.";
        } else {
            return "✓ Battery level is good. All app features are running normally.";
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Start periodic updates when fragment becomes visible
        if (handler != null && updateRunnable != null) {
            handler.post(updateRunnable);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Stop periodic updates when fragment is not visible
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
        handler = null;
        updateRunnable = null;
    }
}
