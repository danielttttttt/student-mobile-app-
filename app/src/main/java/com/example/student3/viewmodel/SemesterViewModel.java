package com.example.student3.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student3.model.Semester;
import com.example.student3.repository.SemesterRepository;

import java.util.List;

public class SemesterViewModel extends AndroidViewModel {
    private final SemesterRepository repository;
    private final LiveData<List<Semester>> allSemesters;

    public SemesterViewModel(Application application) {
        super(application);
        repository = new SemesterRepository(application);
        allSemesters = repository.getAllSemesters();
    }

    public LiveData<List<Semester>> getAllSemesters() {
        return allSemesters;
    }

    public LiveData<Semester> getSemesterById(int semesterId) {
        return repository.getSemesterById(semesterId);
    }

    public LiveData<Semester> getCurrentSemester() {
        return repository.getCurrentSemester();
    }

    public void insert(Semester semester) {
        repository.insert(semester);
    }

    public void update(Semester semester) {
        repository.update(semester);
    }

    public void delete(Semester semester) {
        repository.delete(semester);
    }
}
