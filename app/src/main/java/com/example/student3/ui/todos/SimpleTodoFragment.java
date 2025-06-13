package com.example.student3.ui.todos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.student3.R;
import com.example.student3.adapter.SimpleTodoAdapter;
import com.example.student3.model.SimpleTodo;
import com.example.student3.utils.UserSession;
import com.example.student3.viewmodel.SimpleTodoViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SimpleTodoFragment extends Fragment implements SimpleTodoAdapter.OnTodoClickListener {
    
    private EditText etNewTodo;
    private Button btnAddTodo;
    private Button btnShowAll;
    private Button btnShowPending;
    private Button btnShowCompleted;
    private RecyclerView recyclerTodos;
    private TextView tvEmptyState;
    
    private SimpleTodoViewModel todoViewModel;
    private SimpleTodoAdapter adapter;
    private UserSession userSession;
    private String currentFilter = "ALL";
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_todo, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        etNewTodo = view.findViewById(R.id.etNewTodo);
        btnAddTodo = view.findViewById(R.id.btnAddTodo);
        btnShowAll = view.findViewById(R.id.btnShowAll);
        btnShowPending = view.findViewById(R.id.btnShowPending);
        btnShowCompleted = view.findViewById(R.id.btnShowCompleted);
        recyclerTodos = view.findViewById(R.id.recyclerTodos);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        
        // Initialize UserSession and ViewModel
        userSession = new UserSession(requireContext());
        todoViewModel = new ViewModelProvider(this).get(SimpleTodoViewModel.class);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup click listeners
        setupClickListeners();
        
        // Load todos
        loadTodos();
    }
    
    private void setupRecyclerView() {
        adapter = new SimpleTodoAdapter();
        adapter.setOnTodoClickListener(this);
        recyclerTodos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerTodos.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        btnAddTodo.setOnClickListener(v -> addNewTodo());
        btnShowAll.setOnClickListener(v -> filterTodos("ALL"));
        btnShowPending.setOnClickListener(v -> filterTodos("PENDING"));
        btnShowCompleted.setOnClickListener(v -> filterTodos("COMPLETED"));
    }
    
    private void addNewTodo() {
        String title = etNewTodo.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a task", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!userSession.isLoggedIn()) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int studentId = userSession.getCurrentUserId();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        
        SimpleTodo todo = new SimpleTodo(studentId, title, currentDate);
        todoViewModel.insert(todo);
        
        etNewTodo.setText("");
        Toast.makeText(getContext(), "Task added!", Toast.LENGTH_SHORT).show();
    }
    
    private void filterTodos(String filter) {
        currentFilter = filter;
        updateFilterButtons();
        loadTodos();
    }
    
    private void updateFilterButtons() {
        // Reset all buttons
        btnShowAll.setBackgroundTintList(null);
        btnShowPending.setBackgroundTintList(null);
        btnShowCompleted.setBackgroundTintList(null);
        
        // Highlight selected button
        switch (currentFilter) {
            case "ALL":
                btnShowAll.setBackgroundTintList(getResources().getColorStateList(R.color.primary, null));
                break;
            case "PENDING":
                btnShowPending.setBackgroundTintList(getResources().getColorStateList(R.color.primary, null));
                break;
            case "COMPLETED":
                btnShowCompleted.setBackgroundTintList(getResources().getColorStateList(R.color.primary, null));
                break;
        }
    }
    
    private void loadTodos() {
        if (!userSession.isLoggedIn()) {
            showEmptyState();
            return;
        }
        
        int studentId = userSession.getCurrentUserId();
        
        switch (currentFilter) {
            case "PENDING":
                todoViewModel.getPendingTodosByStudent(studentId)
                        .observe(getViewLifecycleOwner(), this::updateTodoList);
                break;
            case "COMPLETED":
                todoViewModel.getCompletedTodosByStudent(studentId)
                        .observe(getViewLifecycleOwner(), this::updateTodoList);
                break;
            default: // ALL
                todoViewModel.getTodosByStudent(studentId)
                        .observe(getViewLifecycleOwner(), this::updateTodoList);
                break;
        }
    }
    
    private void updateTodoList(List<SimpleTodo> todos) {
        if (todos == null || todos.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            adapter.setTodos(todos);
        }
    }
    
    private void showEmptyState() {
        recyclerTodos.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }
    
    private void hideEmptyState() {
        recyclerTodos.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
    }
    
    @Override
    public void onTodoToggle(SimpleTodo todo, boolean isCompleted) {
        todo.setCompleted(isCompleted);
        todoViewModel.update(todo);
        
        String message = isCompleted ? "Task completed!" : "Task marked as pending";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onTodoDelete(SimpleTodo todo) {
        todoViewModel.delete(todo);
        Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
    }
}
