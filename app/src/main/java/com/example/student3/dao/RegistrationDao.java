package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.Registration;

import java.util.List;

@Dao
public interface RegistrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Registration registration);

    @Update
    void update(Registration registration);

    @Delete
    void delete(Registration registration);

    @Query("DELETE FROM registrations")
    void deleteAll();

    @Query("SELECT * FROM registrations WHERE registrationId = :id")
    LiveData<Registration> getRegistrationById(int id);

    @Query("SELECT * FROM registrations ORDER BY registrationDate DESC")
    LiveData<List<Registration>> getAllRegistrations();

    @Query("SELECT * FROM registrations WHERE studentId = :studentId ORDER BY registrationDate DESC")
    LiveData<List<Registration>> getRegistrationsByStudent(int studentId);

    @Query("SELECT * FROM registrations WHERE courseId = :courseId ORDER BY registrationDate DESC")
    LiveData<List<Registration>> getRegistrationsByCourse(int courseId);

    @Query("SELECT * FROM registrations WHERE studentId = :studentId AND courseId = :courseId ORDER BY registrationDate DESC LIMIT 1")
    LiveData<Registration> getRegistrationByStudentAndCourse(int studentId, int courseId);

    @Query("SELECT COUNT(*) FROM registrations WHERE courseId = :courseId AND status = 'REGISTERED'")
    LiveData<Integer> getRegisteredStudentCountForCourse(int courseId);
}