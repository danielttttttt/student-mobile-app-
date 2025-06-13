# BroadcastReceiver Implementation - Battery Monitor

## Overview
This implementation adds a **BatteryLevelReceiver** to demonstrate BroadcastReceiver functionality as required by the Android Application Code Implementation Guidelines.

## What Was Added

### 1. BroadcastReceiver Class
**File:** `app/src/main/java/com/example/student3/receiver/BatteryLevelReceiver.java`

**Features:**
- Monitors `ACTION_BATTERY_LOW` and `ACTION_BATTERY_OKAY` system events
- Handles `ACTION_BATTERY_CHANGED` for detailed battery information
- Shows notifications and toasts to inform users about battery status
- Demonstrates proper Android system integration

**Key Methods:**
- `onReceive()` - Main receiver method that handles different battery events
- `handleBatteryLow()` - Shows warnings when battery is low
- `handleBatteryOkay()` - Notifies when battery returns to normal
- `handleBatteryChanged()` - Provides detailed battery level monitoring

### 2. Utility Class
**File:** `app/src/main/java/com/example/student3/utils/BatteryMonitorUtil.java`

**Features:**
- Dynamic receiver registration/unregistration
- Battery level and status checking methods
- Battery conservation logic for app optimization
- Charging status detection

**Key Methods:**
- `registerBatteryReceiver()` / `unregisterBatteryReceiver()` - Dynamic management
- `getCurrentBatteryLevel()` - Get current battery percentage
- `shouldConserveBattery()` - Determine if app should reduce operations
- `getBatteryStatusDescription()` - Human-readable battery status

### 3. UI Demonstration
**File:** `app/src/main/java/com/example/student3/ui/settings/BatteryStatusFragment.java`
**Layout:** `app/src/main/res/layout/fragment_battery_status.xml`

**Features:**
- Real-time battery status display
- Battery advice based on current level
- Educational information about how BroadcastReceiver works
- Testing instructions for developers

### 4. Integration
**Modified:** `app/src/main/java/com/example/student3/ui/MainActivity.java`

**Changes:**
- Added battery monitoring initialization in `onCreate()`
- Added cleanup in `onDestroy()`
- Demonstrates proper lifecycle management

### 5. Manifest Registration
**Modified:** `app/src/main/AndroidManifest.xml`

**Added:**
```xml
<receiver
    android:name=".receiver.BatteryLevelReceiver"
    android:enabled="true"
    android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BATTERY_LOW" />
        <action android:name="android.intent.action.BATTERY_OKAY" />
    </intent-filter>
</receiver>
```

### 6. Unit Tests
**File:** `app/src/test/java/com/example/student3/BatteryMonitorTest.java`

**Features:**
- Tests for different battery events
- Demonstrates how to test BroadcastReceiver components
- Shows proper testing structure for Android system integration

## How It Works

### Static Registration (AndroidManifest.xml)
- Automatically receives `BATTERY_LOW` and `BATTERY_OKAY` system broadcasts
- Works even when app is not running
- Registered at app installation time

### Dynamic Registration (Runtime)
- Registers for `BATTERY_CHANGED` events when app is active
- Provides detailed battery information
- Unregistered when app is destroyed to save resources

### Battery-Aware Functionality
- App can reduce background operations when battery is low
- Shows user-friendly notifications about battery status
- Demonstrates responsible resource usage

## Testing Instructions

### On Android Emulator:
1. Open Extended Controls (⋯ button in emulator toolbar)
2. Go to Battery section
3. Change battery level to below 20%
4. Observe notifications and app behavior
5. Change back to higher level to see "battery okay" notification

### On Real Device:
1. Let battery drain naturally or use battery testing apps
2. Watch for notifications when battery reaches low levels
3. Check app logs for battery events

### Using ADB Commands:
```bash
# Simulate low battery
adb shell dumpsys battery set level 15

# Simulate battery okay
adb shell dumpsys battery set level 50

# Reset to actual battery level
adb shell dumpsys battery reset
```

## Educational Value

This implementation demonstrates:
- **System Integration**: How Android apps respond to system events
- **Resource Management**: Battery-aware app development
- **User Experience**: Providing helpful battery information
- **Best Practices**: Proper receiver registration/cleanup
- **Testing**: How to test system integration components

## Compliance with Guidelines

✅ **BroadcastReceiver**: Implemented and functional
✅ **System Events**: Handles battery low/okay events  
✅ **Practical Use**: Battery monitoring for student app
✅ **Testing**: Unit tests included
✅ **Documentation**: Complete implementation guide

This implementation fulfills the BroadcastReceiver requirement in the Android Application Code Implementation Guidelines while providing practical, educational value for a student mobile app project.
