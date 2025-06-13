package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.SimpleTodo;

import java.util.List;

@Dao
public interface SimpleTodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SimpleTodo todo);

    @Update
    void update(SimpleTodo todo);

    @Delete
    void delete(SimpleTodo todo);

    @Query("DELETE FROM todos")
    void deleteAll();

    // Get all todos for a specific student
    @Query("SELECT * FROM todos WHERE studentId = :studentId ORDER BY createdDate DESC")
    LiveData<List<SimpleTodo>> getTodosByStudent(int studentId);

    // Get pending todos for a student
    @Query("SELECT * FROM todos WHERE studentId = :studentId AND isCompleted = 0 ORDER BY createdDate DESC")
    LiveData<List<SimpleTodo>> getPendingTodosByStudent(int studentId);

    // Get completed todos for a student
    @Query("SELECT * FROM todos WHERE studentId = :studentId AND isCompleted = 1 ORDER BY createdDate DESC")
    LiveData<List<SimpleTodo>> getCompletedTodosByStudent(int studentId);

    // Quick toggle completion status
    @Query("UPDATE todos SET isCompleted = :isCompleted WHERE todoId = :todoId")
    void updateCompletionStatus(int todoId, boolean isCompleted);

    // Synchronous methods for background operations
    @Query("SELECT * FROM todos ORDER BY createdDate DESC")
    List<SimpleTodo> getAllTodosSync();

    @Query("SELECT * FROM todos WHERE studentId = :studentId ORDER BY createdDate DESC")
    List<SimpleTodo> getTodosByStudentSync(int studentId);
}
