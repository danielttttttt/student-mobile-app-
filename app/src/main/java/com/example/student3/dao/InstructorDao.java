package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.Instructor;

import java.util.List;

@Dao
public interface InstructorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Instructor instructor);

    @Update
    void update(Instructor instructor);

    @Delete
    void delete(Instructor instructor);

    @Query("DELETE FROM instructors")
    void deleteAll();

    @Query("SELECT * FROM instructors WHERE instructorId = :id")
    LiveData<Instructor> getInstructorById(int id);

    @Query("SELECT * FROM instructors ORDER BY lastName ASC")
    LiveData<List<Instructor>> getAllInstructors();

    @Query("SELECT * FROM instructors WHERE departmentId = :departmentId ORDER BY lastName ASC")
    LiveData<List<Instructor>> getInstructorsByDepartment(int departmentId);

    @Query("SELECT * FROM instructors WHERE firstName LIKE '%' || :searchQuery || '%' OR lastName LIKE '%' || :searchQuery || '%'")
    LiveData<List<Instructor>> searchInstructors(String searchQuery);
}