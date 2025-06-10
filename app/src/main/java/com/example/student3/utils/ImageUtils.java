package com.example.student3.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for image processing and optimization.
 * 
 * Features:
 * - Image compression with quality control
 * - Automatic image rotation based on EXIF data
 * - Size validation and resizing
 * - Memory-efficient image loading
 * 
 * @author DANN4 Development Team
 * @version 1.0
 * @since 2024
 */
public class ImageUtils {
    
    private static final String TAG = "ImageUtils";
    
    // Image constraints
    public static final int MAX_IMAGE_WIDTH = 1024;
    public static final int MAX_IMAGE_HEIGHT = 1024;
    public static final int JPEG_QUALITY = 85;
    public static final long MAX_FILE_SIZE_MB = 5; // 5MB
    public static final long MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;
    
    /**
     * Compresses and optimizes an image from URI.
     * 
     * @param context Application context
     * @param imageUri Source image URI
     * @param outputFile Target file for compressed image
     * @return true if compression successful, false otherwise
     */
    public static boolean compressImage(Context context, Uri imageUri, File outputFile) {
        try {
            // Load and decode image with proper sampling
            Bitmap bitmap = loadOptimizedBitmap(context, imageUri);
            if (bitmap == null) {
                Log.e(TAG, "Failed to load bitmap from URI");
                return false;
            }
            
            // Apply rotation if needed
            bitmap = rotateImageIfRequired(context, bitmap, imageUri);
            
            // Resize if too large
            bitmap = resizeImageIfNeeded(bitmap);
            
            // Compress and save
            return saveBitmapToFile(bitmap, outputFile);
            
        } catch (Exception e) {
            Log.e(TAG, "Error compressing image", e);
            return false;
        }
    }
    
    /**
     * Loads bitmap with memory-efficient sampling.
     */
    private static Bitmap loadOptimizedBitmap(Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;
            
            // First decode to get dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            
            // Calculate sample size
            options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565; // Use less memory
            
            // Decode with sampling
            inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            
            return bitmap;
            
        } catch (IOException e) {
            Log.e(TAG, "Error loading bitmap", e);
            return null;
        }
    }
    
    /**
     * Calculates optimal sample size for image loading.
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        return inSampleSize;
    }
    
    /**
     * Rotates image based on EXIF orientation data.
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap bitmap, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return bitmap;
            
            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            inputStream.close();
            
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading EXIF data", e);
            return bitmap;
        }
    }
    
    /**
     * Rotates bitmap by specified degrees.
     */
    private static Bitmap rotateImage(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    
    /**
     * Resizes image if it exceeds maximum dimensions.
     */
    private static Bitmap resizeImageIfNeeded(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        if (width <= MAX_IMAGE_WIDTH && height <= MAX_IMAGE_HEIGHT) {
            return bitmap;
        }
        
        float ratio = Math.min((float) MAX_IMAGE_WIDTH / width, (float) MAX_IMAGE_HEIGHT / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
    
    /**
     * Saves bitmap to file with compression.
     */
    private static boolean saveBitmapToFile(Bitmap bitmap, File outputFile) {
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            return bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap to file", e);
            return false;
        }
    }
    
    /**
     * Validates if image file size is within limits.
     */
    public static boolean isImageSizeValid(Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return false;
            
            long size = inputStream.available();
            inputStream.close();
            
            return size <= MAX_FILE_SIZE_BYTES;
        } catch (IOException e) {
            Log.e(TAG, "Error checking image size", e);
            return false;
        }
    }
    
    /**
     * Generates unique filename for profile images.
     */
    public static String generateProfileImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "PROFILE_" + timeStamp + ".jpg";
    }
    
    /**
     * Gets formatted file size string.
     */
    public static String getFormattedFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0);
        return String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0));
    }
}
