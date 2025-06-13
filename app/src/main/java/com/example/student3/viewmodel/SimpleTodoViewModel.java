package com.example.student3.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student3.model.SimpleTodo;
import com.example.student3.repository.SimpleTodoRepository;

import java.util.List;

public class SimpleTodoViewModel extends AndroidViewModel {
    private SimpleTodoRepository repository;

    public SimpleTodoViewModel(@NonNull Application application) {
        super(application);
        repository = new SimpleTodoRepository(application);
    }

    // Insert todo
    public void insert(SimpleTodo todo) {
        repository.insert(todo);
    }

    // Update todo
    public void update(SimpleTodo todo) {
        repository.update(todo);
    }

    // Delete todo
    public void delete(SimpleTodo todo) {
        repository.delete(todo);
    }

    // Get all todos for a student
    public LiveData<List<SimpleTodo>> getTodosByStudent(int studentId) {
        return repository.getTodosByStudent(studentId);
    }

    // Get pending todos for a student
    public LiveData<List<SimpleTodo>> getPendingTodosByStudent(int studentId) {
        return repository.getPendingTodosByStudent(studentId);
    }

    // Get completed todos for a student
    public LiveData<List<SimpleTodo>> getCompletedTodosByStudent(int studentId) {
        return repository.getCompletedTodosByStudent(studentId);
    }

    // Quick toggle completion status
    public void toggleCompletionStatus(int todoId, boolean isCompleted) {
        repository.toggleCompletionStatus(todoId, isCompleted);
    }
}
