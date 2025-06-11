package com.example.student3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for locale and language management.
 * 
 * Features:
 * - Language switching with persistence
 * - Locale-aware date and time formatting
 * - RTL language support detection
 * - Language preference storage
 * 
 * @author DANN4 Development Team
 * @version 1.0
 * @since 2025
 */
public class LocaleUtils {
    
    private static final String PREFS_NAME = "locale_prefs";
    private static final String KEY_LANGUAGE = "selected_language";
    
    // Supported languages
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_AMHARIC = "am";
    
    /**
     * Supported language information.
     */
    public static class LanguageInfo {
        public final String code;
        public final String displayName;
        public final String nativeName;
        public final boolean isRTL;
        
        public LanguageInfo(String code, String displayName, String nativeName, boolean isRTL) {
            this.code = code;
            this.displayName = displayName;
            this.nativeName = nativeName;
            this.isRTL = isRTL;
        }
    }
    
    // Available languages
    private static final LanguageInfo[] SUPPORTED_LANGUAGES = {
        new LanguageInfo(LANGUAGE_ENGLISH, "English", "English", false),
        new LanguageInfo(LANGUAGE_AMHARIC, "Amharic", "አማርኛ", false)
    };
    
    /**
     * Gets all supported languages.
     */
    public static LanguageInfo[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES.clone();
    }
    
    /**
     * Gets language info by code.
     */
    public static LanguageInfo getLanguageInfo(String languageCode) {
        for (LanguageInfo info : SUPPORTED_LANGUAGES) {
            if (info.code.equals(languageCode)) {
                return info;
            }
        }
        return SUPPORTED_LANGUAGES[0]; // Default to English
    }
    
    /**
     * Sets the application locale.
     * 
     * @param context Application context
     * @param languageCode Language code (e.g., "en", "am")
     */
    public static void setLocale(Context context, String languageCode) {
        // Save preference
        saveLanguagePreference(context, languageCode);
        
        // Apply locale using AppCompatDelegate
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
    
    /**
     * Gets the current application locale.
     */
    public static String getCurrentLanguage(Context context) {
        // First check saved preference
        String savedLanguage = getSavedLanguagePreference(context);
        if (savedLanguage != null) {
            return savedLanguage;
        }
        
        // Then check current app locale
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();
        if (!currentLocales.isEmpty()) {
            return currentLocales.get(0).getLanguage();
        }
        
        // Default to system locale
        return Locale.getDefault().getLanguage();
    }
    
    /**
     * Saves language preference to SharedPreferences.
     */
    private static void saveLanguagePreference(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }
    
    /**
     * Gets saved language preference from SharedPreferences.
     */
    private static String getSavedLanguagePreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, null);
    }
    
    /**
     * Checks if current language is RTL (Right-to-Left).
     */
    public static boolean isRTL(Context context) {
        String currentLang = getCurrentLanguage(context);
        LanguageInfo info = getLanguageInfo(currentLang);
        return info.isRTL;
    }
    
    /**
     * Gets locale-aware date formatter.
     */
    public static DateFormat getDateFormat(Context context) {
        Locale locale = getCurrentLocale(context);
        return DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    }
    
    /**
     * Gets locale-aware time formatter.
     */
    public static DateFormat getTimeFormat(Context context) {
        Locale locale = getCurrentLocale(context);
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale);
    }
    
    /**
     * Gets locale-aware date and time formatter.
     */
    public static DateFormat getDateTimeFormat(Context context) {
        Locale locale = getCurrentLocale(context);
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
    }
    
    /**
     * Formats date according to current locale.
     */
    public static String formatDate(Context context, Date date) {
        if (date == null) return "";
        return getDateFormat(context).format(date);
    }
    
    /**
     * Formats time according to current locale.
     */
    public static String formatTime(Context context, Date date) {
        if (date == null) return "";
        return getTimeFormat(context).format(date);
    }
    
    /**
     * Formats date and time according to current locale.
     */
    public static String formatDateTime(Context context, Date date) {
        if (date == null) return "";
        return getDateTimeFormat(context).format(date);
    }
    
    /**
     * Gets custom date format pattern for current locale.
     */
    public static String getCustomDateFormat(Context context, String pattern) {
        Locale locale = getCurrentLocale(context);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
        return formatter.toPattern();
    }
    
    /**
     * Formats date with custom pattern according to current locale.
     */
    public static String formatDateWithPattern(Context context, Date date, String pattern) {
        if (date == null) return "";
        Locale locale = getCurrentLocale(context);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
        return formatter.format(date);
    }
    
    /**
     * Gets the current locale object.
     */
    public static Locale getCurrentLocale(Context context) {
        String languageCode = getCurrentLanguage(context);
        return new Locale(languageCode);
    }
    
    /**
     * Gets display name of current language in its native script.
     */
    public static String getCurrentLanguageDisplayName(Context context) {
        String currentLang = getCurrentLanguage(context);
        LanguageInfo info = getLanguageInfo(currentLang);
        return info.nativeName;
    }
    
    /**
     * Checks if a language is supported.
     */
    public static boolean isLanguageSupported(String languageCode) {
        for (LanguageInfo info : SUPPORTED_LANGUAGES) {
            if (info.code.equals(languageCode)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the default language code.
     */
    public static String getDefaultLanguage() {
        return LANGUAGE_ENGLISH;
    }
    
    /**
     * Updates configuration with new locale (for older Android versions).
     */
    @SuppressWarnings("deprecation")
    public static Context updateConfigurationLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        }
    }
    
    /**
     * Gets language names array for spinner/dropdown.
     */
    public static String[] getLanguageNames() {
        String[] names = new String[SUPPORTED_LANGUAGES.length];
        for (int i = 0; i < SUPPORTED_LANGUAGES.length; i++) {
            names[i] = SUPPORTED_LANGUAGES[i].nativeName;
        }
        return names;
    }
    
    /**
     * Gets language codes array.
     */
    public static String[] getLanguageCodes() {
        String[] codes = new String[SUPPORTED_LANGUAGES.length];
        for (int i = 0; i < SUPPORTED_LANGUAGES.length; i++) {
            codes[i] = SUPPORTED_LANGUAGES[i].code;
        }
        return codes;
    }
}
