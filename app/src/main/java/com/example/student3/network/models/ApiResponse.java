package com.example.student3.network.models;

import com.google.gson.annotations.SerializedName;

/**
 * Generic API response wrapper
 * 
 * This class wraps all API responses with common fields like
 * success status, message, and error information.
 */
public class ApiResponse<T> {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private T data;
    
    @SerializedName("error_code")
    private String errorCode;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    // Default constructor
    public ApiResponse() {}
    
    // Constructor for success response
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Constructor for error response
    public ApiResponse(boolean success, String message, String errorCode) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Check if response has data
     */
    public boolean hasData() {
        return data != null;
    }
    
    /**
     * Check if response is an error
     */
    public boolean isError() {
        return !success || errorCode != null;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errorCode='" + errorCode + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
