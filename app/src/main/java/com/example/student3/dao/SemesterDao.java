package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.Semester;

import java.util.List;

@Dao
public interface SemesterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Semester semester);

    @Update
    void update(Semester semester);

    @Delete
    void delete(Semester semester);

    @Query("DELETE FROM semesters")
    void deleteAll();

    @Query("SELECT * FROM semesters WHERE semesterId = :id")
    LiveData<Semester> getSemesterById(int id);

    @Query("SELECT * FROM semesters ORDER BY startDate DESC")
    LiveData<List<Semester>> getAllSemesters();

    @Query("SELECT * FROM semesters WHERE isCurrent = 1")
    LiveData<Semester> getCurrentSemester();
}