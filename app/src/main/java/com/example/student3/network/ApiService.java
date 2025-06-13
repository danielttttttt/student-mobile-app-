package com.example.student3.network;

import com.example.student3.model.Announcement;
import com.example.student3.model.Course;
import com.example.student3.model.SimpleTodo;
import com.example.student3.model.UserProfile;
import com.example.student3.network.models.ApiResponse;
import com.example.student3.network.models.SyncRequest;
import com.example.student3.network.models.SyncResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API Service interface for network operations
 * 
 * This interface defines all REST API endpoints for the student app.
 * For school project demonstration, we'll use JSONPlaceholder as a mock API
 * or create simple mock responses.
 */
public interface ApiService {
    
    // Base URL for JSONPlaceholder (free testing API)
    String BASE_URL = "https://jsonplaceholder.typicode.com/";
    
    // Alternative: Mock server URL (if you set up your own)
    // String BASE_URL = "https://your-mock-server.com/api/";
    
    /**
     * Get all announcements from server
     * Maps to JSONPlaceholder posts endpoint for demo
     */
    @GET("posts")
    Call<List<Announcement>> getAnnouncements();
    
    /**
     * Get announcements for a specific student
     */
    @GET("posts")
    Call<List<Announcement>> getAnnouncementsForStudent(@Query("userId") int studentId);
    
    /**
     * Get courses for a specific student
     * In real implementation, this would be a proper endpoint
     */
    @GET("posts")
    Call<List<Course>> getStudentCourses(@Query("userId") int studentId);
    
    /**
     * Create a new todo item on server
     */
    @POST("posts")
    Call<SimpleTodo> createTodo(@Body SimpleTodo todo);
    
    /**
     * Update an existing todo item
     */
    @PUT("posts/{id}")
    Call<SimpleTodo> updateTodo(@Path("id") int todoId, @Body SimpleTodo todo);
    
    /**
     * Sync user profile with server
     */
    @PUT("users/{id}")
    Call<UserProfile> syncUserProfile(@Path("id") int userId, @Body UserProfile profile);
    
    /**
     * Get user profile from server
     */
    @GET("users/{id}")
    Call<UserProfile> getUserProfile(@Path("id") int userId);
    
    /**
     * Bulk sync operation - send local changes and get server updates
     * This would be a custom endpoint in a real implementation
     */
    @POST("sync")
    Call<SyncResponse> performBulkSync(@Body SyncRequest syncRequest);
    
    /**
     * Check server status and connectivity
     */
    @GET("posts/1")
    Call<ApiResponse> checkServerStatus();
    
    /**
     * Get latest app version info (for update notifications)
     */
    @GET("posts/1")
    Call<ApiResponse> getAppVersionInfo();
}
