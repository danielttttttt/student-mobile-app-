package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);

    @Query("DELETE FROM students")
    void deleteAll();

    @Query("SELECT * FROM students WHERE studentId = :id")
    LiveData<Student> getStudentById(int id);

    @Query("SELECT * FROM students ORDER BY lastName ASC")
    LiveData<List<Student>> getAllStudents();

    @Query("SELECT * FROM students WHERE departmentId = :departmentId ORDER BY lastName ASC")
    LiveData<List<Student>> getStudentsByDepartment(int departmentId);

    @Query("SELECT * FROM students WHERE firstName LIKE '%' || :searchQuery || '%' OR lastName LIKE '%' || :searchQuery || '%'")
    LiveData<List<Student>> searchStudents(String searchQuery);

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    Student getStudentByEmail(String email);

    @Query("SELECT COUNT(*) FROM students WHERE email = :email")
    int getEmailCount(String email);

    @Query("SELECT EXISTS(SELECT 1 FROM students WHERE email = :email)")
    boolean emailExists(String email);

    @Query("UPDATE students SET loginAttempts = :attempts WHERE email = :email")
    void updateLoginAttempts(String email, int attempts);

    @Query("UPDATE students SET accountLocked = :locked WHERE email = :email")
    void updateAccountLockStatus(String email, boolean locked);

    @Query("UPDATE students SET lastLoginDate = :loginDate WHERE email = :email")
    void updateLastLoginDate(String email, String loginDate);

    @Query("UPDATE students SET loginAttempts = 0, accountLocked = 0 WHERE email = :email")
    void resetLoginAttempts(String email);

    @Query("UPDATE students SET profileImagePath = :profileImagePath WHERE studentId = :studentId")
    void updateProfileImagePath(int studentId, String profileImagePath);

}