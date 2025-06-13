package com.example.student3.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.student3.dao.SimpleTodoDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.SimpleTodo;

import java.util.List;

public class SimpleTodoRepository {
    private SimpleTodoDao todoDao;

    public SimpleTodoRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        todoDao = database.simpleTodoDao();
    }

    // Insert todo
    public void insert(SimpleTodo todo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            todoDao.insert(todo);
        });
    }

    // Update todo
    public void update(SimpleTodo todo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            todoDao.update(todo);
        });
    }

    // Delete todo
    public void delete(SimpleTodo todo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            todoDao.delete(todo);
        });
    }

    // Get all todos for a student
    public LiveData<List<SimpleTodo>> getTodosByStudent(int studentId) {
        return todoDao.getTodosByStudent(studentId);
    }

    // Get pending todos for a student
    public LiveData<List<SimpleTodo>> getPendingTodosByStudent(int studentId) {
        return todoDao.getPendingTodosByStudent(studentId);
    }

    // Get completed todos for a student
    public LiveData<List<SimpleTodo>> getCompletedTodosByStudent(int studentId) {
        return todoDao.getCompletedTodosByStudent(studentId);
    }

    // Quick toggle completion status
    public void toggleCompletionStatus(int todoId, boolean isCompleted) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            todoDao.updateCompletionStatus(todoId, isCompleted);
        });
    }
}
