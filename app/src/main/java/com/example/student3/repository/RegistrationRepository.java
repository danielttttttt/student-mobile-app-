package com.example.student3.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.student3.dao.RegistrationDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Registration;

import java.util.List;

public class RegistrationRepository {
    private final RegistrationDao registrationDao;
    private final LiveData<List<Registration>> allRegistrations;

    public RegistrationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        registrationDao = db.registrationDao();
        allRegistrations = registrationDao.getAllRegistrations();
    }

    public LiveData<List<Registration>> getAllRegistrations() {
        return allRegistrations;
    }

    public LiveData<Registration> getRegistrationById(int registrationId) {
        return registrationDao.getRegistrationById(registrationId);
    }

    public LiveData<List<Registration>> getRegistrationsByStudent(int studentId) {
        return registrationDao.getRegistrationsByStudent(studentId);
    }

    public LiveData<List<Registration>> getRegistrationsByCourse(int courseId) {
        return registrationDao.getRegistrationsByCourse(courseId);
    }

    public LiveData<Registration> getRegistrationByStudentAndCourse(int studentId, int courseId) {
        return registrationDao.getRegistrationByStudentAndCourse(studentId, courseId);
    }

    public LiveData<Integer> getRegisteredStudentCountForCourse(int courseId) {
        return registrationDao.getRegisteredStudentCountForCourse(courseId);
    }

    public void insert(Registration registration) {
        AppDatabase.databaseWriteExecutor.execute(() -> registrationDao.insert(registration));
    }

    public void update(Registration registration) {
        AppDatabase.databaseWriteExecutor.execute(() -> registrationDao.update(registration));
    }

    public void delete(Registration registration) {
        AppDatabase.databaseWriteExecutor.execute(() -> registrationDao.delete(registration));
    }
}
