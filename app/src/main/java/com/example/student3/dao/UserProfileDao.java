package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.UserProfile;

import java.util.List;

@Dao
public interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserProfile userProfile);

    @Update
    void update(UserProfile userProfile);

    @Delete
    void delete(UserProfile userProfile);

    @Query("DELETE FROM user_profiles")
    void deleteAll();

    @Query("SELECT * FROM user_profiles WHERE profileId = :id")
    LiveData<UserProfile> getUserProfileById(int id);

    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    LiveData<UserProfile> getUserProfileByUserId(int userId);

    @Query("SELECT * FROM user_profiles ORDER BY lastLogin DESC")
    LiveData<List<UserProfile>> getAllUserProfiles();
}