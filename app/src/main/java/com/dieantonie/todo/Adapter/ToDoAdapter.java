package com.dieantonie.todo.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dieantonie.todo.AddNewTask;
import com.dieantonie.todo.MainActivity;
import com.dieantonie.todo.Model.ToDoModel;
import com.dieantonie.todo.R;
import com.dieantonie.todo.Utils.DatabaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.activity = activity;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get task layout view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        // Render task view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // open database handler
        db.openDatabase();

        // load task item for position.
        final ToDoModel item = todoList.get(position);
        // set text for task view holder from item
        holder.task.setText(item.getTask());
        // set status for task view holder from item
        holder.task.setChecked(toBoolean(item.getStatus()));
        // set view holder status change event listener.
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // if view holder is checked
                 if (isChecked) {
                     // persist task item status
                     db.updateStatus(item.getId(), 1);
                 // if view holder is unchecked
                 } else {
                     // persist task item status
                     db.updateStatus(item.getId(), 0);
                 }
            }
        });
    }

    private boolean toBoolean(int n) {
        // convert int to bool
        return n != 0;
    }

    @Override
    public int getItemCount() {
        // this is fucking useless and literally isn't used anywhere
        return todoList.size();
    }

    public Context getContext() {
        // returns activity of adapter
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList) {
        // set adapter task list
        this.todoList = todoList;
        // Notify any registered observers that the data set has changed?
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        // Load item at position
        ToDoModel item = todoList.get(position);
        // Delete item from persistence layer
        db.deleteTask(item.getId());
        // Remove item from task list
        todoList.remove(position);
        // Notify any registered observers that the item previously located at position has been removed from the data set.
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        // Load item at position
        ToDoModel item = todoList.get(position);
        // Instantiate bundle
        Bundle bundle = new Bundle();
        // Bundle id
        bundle.putInt("id", item.getId());
        // Bundle task text
        bundle.putString("task", item.getTask());
        // Instantiate new task fragment
        AddNewTask fragment = new AddNewTask();
        // Load bundle into fragment
        fragment.setArguments(bundle);
        // Render fragment
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Declare task
        CheckBox task;

        // Instantiate view holder
        ViewHolder(View view) {
            // Recycler view holder
            super(view);
            // set task to checkbox view item
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
}
