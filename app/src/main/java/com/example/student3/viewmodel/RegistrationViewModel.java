package com.example.student3.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student3.model.Registration;
import com.example.student3.repository.RegistrationRepository;

import java.util.List;

public class RegistrationViewModel extends AndroidViewModel {
    private final RegistrationRepository repository;
    private final LiveData<List<Registration>> allRegistrations;

    public RegistrationViewModel(Application application) {
        super(application);
        repository = new RegistrationRepository(application);
        allRegistrations = repository.getAllRegistrations();
    }

    public LiveData<List<Registration>> getAllRegistrations() {
        return allRegistrations;
    }

    public LiveData<Registration> getRegistrationById(int registrationId) {
        return repository.getRegistrationById(registrationId);
    }

    public LiveData<List<Registration>> getRegistrationsByStudent(int studentId) {
        return repository.getRegistrationsByStudent(studentId);
    }

    public LiveData<List<Registration>> getRegistrationsByCourse(int courseId) {
        return repository.getRegistrationsByCourse(courseId);
    }

    public LiveData<Registration> getRegistrationByStudentAndCourse(int studentId, int courseId) {
        return repository.getRegistrationByStudentAndCourse(studentId, courseId);
    }

    public LiveData<Integer> getRegisteredStudentCountForCourse(int courseId) {
        return repository.getRegisteredStudentCountForCourse(courseId);
    }

    public void insert(Registration registration) {
        repository.insert(registration);
    }

    public void update(Registration registration) {
        repository.update(registration);
    }

    public void delete(Registration registration) {
        repository.delete(registration);
    }
}
