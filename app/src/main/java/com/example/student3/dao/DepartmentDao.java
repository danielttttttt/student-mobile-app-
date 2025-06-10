package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.Department;

import java.util.List;

@Dao
public interface DepartmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Department department);

    @Update
    void update(Department department);

    @Delete
    void delete(Department department);

    @Query("DELETE FROM departments")
    void deleteAll();

    @Query("SELECT * FROM departments WHERE departmentId = :id")
    LiveData<Department> getDepartmentById(int id);

    @Query("SELECT * FROM departments ORDER BY name ASC")
    LiveData<List<Department>> getAllDepartments();

    @Query("SELECT * FROM departments WHERE name LIKE '%' || :searchQuery || '%' OR code LIKE '%' || :searchQuery || '%'")
    LiveData<List<Department>> searchDepartments(String searchQuery);
}