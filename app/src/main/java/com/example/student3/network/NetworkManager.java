package com.example.student3.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Network Manager for handling API connections and network utilities
 * 
 * This class provides:
 * - Retrofit instance configuration
 * - Network connectivity checking
 * - HTTP client setup with logging
 * - Error handling utilities
 */
public class NetworkManager {
    
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance;
    private static ApiService apiService;
    private Context context;
    
    private NetworkManager(Context context) {
        this.context = context.getApplicationContext();
        setupRetrofit();
    }
    
    /**
     * Get singleton instance of NetworkManager
     */
    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }
    
    /**
     * Get API service instance
     */
    public static ApiService getApiService() {
        return apiService;
    }
    
    /**
     * Setup Retrofit with proper configuration
     */
    private void setupRetrofit() {
        try {
            // Create HTTP logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Configure OkHttp client
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor);
            
            // Configure Gson for JSON parsing
            Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
            
            // Build Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
            
            // Create API service
            apiService = retrofit.create(ApiService.class);
            
            Log.d(TAG, "Retrofit setup completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Retrofit", e);
        }
    }
    
    /**
     * Check if device has internet connectivity
     */
    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
            
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability", e);
            return false;
        }
    }
    
    /**
     * Get network type (WiFi, Mobile, etc.)
     */
    public String getNetworkType() {
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    return activeNetworkInfo.getTypeName();
                }
            }
            
            return "No Connection";
        } catch (Exception e) {
            Log.e(TAG, "Error getting network type", e);
            return "Unknown";
        }
    }
    
    /**
     * Check if device is connected to WiFi
     */
    public boolean isWiFiConnected() {
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager != null) {
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return wifiInfo != null && wifiInfo.isConnected();
            }
            
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking WiFi connection", e);
            return false;
        }
    }
    
    /**
     * Get network status description for UI display
     */
    public String getNetworkStatusDescription() {
        if (isNetworkAvailable()) {
            return "Online (" + getNetworkType() + ")";
        } else {
            return "Offline";
        }
    }
}
