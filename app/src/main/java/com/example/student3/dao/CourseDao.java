package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("DELETE FROM courses")
    void deleteAll();

    @Query("SELECT * FROM courses WHERE courseId = :id")
    LiveData<Course> getCourseById(int id);

    @Query("SELECT * FROM courses ORDER BY title ASC")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM courses WHERE departmentId = :departmentId ORDER BY title ASC")
    LiveData<List<Course>> getCoursesByDepartment(int departmentId);

    @Query("SELECT * FROM courses WHERE instructorId = :instructorId ORDER BY title ASC")
    LiveData<List<Course>> getCoursesByInstructor(int instructorId);

    @Query("SELECT * FROM courses WHERE semesterId = :semesterId ORDER BY title ASC")
    LiveData<List<Course>> getCoursesBySemester(int semesterId);

    @Query("SELECT * FROM courses WHERE courseCode LIKE '%' || :searchQuery || '%' OR title LIKE '%' || :searchQuery || '%'")
    LiveData<List<Course>> searchCourses(String searchQuery);

    @Query("SELECT * FROM courses WHERE departmentId = :departmentId AND (courseCode LIKE '%' || :searchQuery || '%' OR title LIKE '%' || :searchQuery || '%') ORDER BY title ASC")
    LiveData<List<Course>> searchCoursesByDepartment(String searchQuery, int departmentId);

    @Query("SELECT c.* FROM courses c " +
           "INNER JOIN registrations r ON c.courseId = r.courseId " +
           "WHERE r.studentId = :studentId AND r.status = 'REGISTERED' " +
           "ORDER BY r.registrationDate DESC")
    LiveData<List<Course>> getRegisteredCoursesByStudent(int studentId);

    @Query("SELECT c.* FROM courses c " +
           "INNER JOIN registrations r ON c.courseId = r.courseId " +
           "WHERE r.studentId = :studentId AND r.status = 'REGISTERED' AND c.departmentId = :departmentId " +
           "ORDER BY r.registrationDate DESC")
    LiveData<List<Course>> getRegisteredCoursesByStudentAndDepartment(int studentId, int departmentId);
}