package com.example.student3.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.student3.R;
import com.example.student3.model.SimpleTodo;
import java.util.ArrayList;
import java.util.List;

public class SimpleTodoAdapter extends RecyclerView.Adapter<SimpleTodoAdapter.TodoViewHolder> {
    
    private List<SimpleTodo> todos = new ArrayList<>();
    private OnTodoClickListener listener;
    
    public interface OnTodoClickListener {
        void onTodoToggle(SimpleTodo todo, boolean isCompleted);
        void onTodoDelete(SimpleTodo todo);
    }
    
    public void setOnTodoClickListener(OnTodoClickListener listener) {
        this.listener = listener;
    }
    
    public void setTodos(List<SimpleTodo> todos) {
        this.todos = todos;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple_todo, parent, false);
        return new TodoViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        SimpleTodo todo = todos.get(position);
        holder.bind(todo);
    }
    
    @Override
    public int getItemCount() {
        return todos.size();
    }
    
    class TodoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTodoTitle;
        private CheckBox checkboxCompleted;
        private ImageButton btnDelete;
        
        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTodoTitle = itemView.findViewById(R.id.tvTodoTitle);
            checkboxCompleted = itemView.findViewById(R.id.checkboxCompleted);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            
            // Set click listeners
            checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null && buttonView.isPressed()) {
                    listener.onTodoToggle(todos.get(getAdapterPosition()), isChecked);
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoDelete(todos.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(SimpleTodo todo) {
            tvTodoTitle.setText(todo.getTitle());
            
            // Set completion status
            checkboxCompleted.setOnCheckedChangeListener(null);
            checkboxCompleted.setChecked(todo.isCompleted());
            checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null && buttonView.isPressed()) {
                    listener.onTodoToggle(todo, isChecked);
                }
            });
            
            // Apply strikethrough if completed
            if (todo.isCompleted()) {
                tvTodoTitle.setPaintFlags(tvTodoTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTodoTitle.setAlpha(0.6f);
            } else {
                tvTodoTitle.setPaintFlags(tvTodoTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTodoTitle.setAlpha(1.0f);
            }
        }
    }
}
