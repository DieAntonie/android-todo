package com.dieantonie.todo;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dieantonie.todo.Adapter.ToDoAdapter;
import com.dieantonie.todo.Model.ToDoModel;
import com.dieantonie.todo.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    private DatabaseHandler db;
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter taskAdapter;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Run AppCompatActivity onCreate(Bundle)
        super.onCreate(savedInstanceState);
        // Run AppCompatActivity setContentView(LayoutRes)
        setContentView(R.layout.activity_main);
        // Checks that this activity's ActionBar is not `null` and then hside it.
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Instantiate database handler
        db = new DatabaseHandler(this);
        // set the Writable Database for the database handler.
        db.openDatabase();

        // set task recycler view.
        tasksRecyclerView = findViewById(R.id.taskRecyclerView);
        // set task recycler view's layout manager.
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set task adapter.
        taskAdapter = new ToDoAdapter(db, this);
        // set task recycler view's adapter to task adapter.
        tasksRecyclerView.setAdapter(taskAdapter);

        // set item touch helper with recycler item touch helper using task adapter.
        ItemTouchHelper itemTouchHelper =
                new ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        // attach item touch helper to task recycler view.
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        // set floating action button.
        fab = findViewById(R.id.fab);

        // set task list from database handler's tasks.
        taskList = db.getAllTasks();
        // i'm assuming reversing the task list?
        Collections.reverse(taskList);

        // set the task adapters tasks from the reversed task list.
        taskAdapter.setTasks(taskList);

        // set the floating action button's on click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            // override on click.
            public void onClick(View view) {
                // Show new "add new task" bottom sheet dialog fragment
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        // set task list from database handler's tasks.
        taskList = db.getAllTasks();
        // i'm assuming reversing the task list?
        Collections.reverse(taskList);
        // set the task adapters tasks from the reversed task list.
        taskAdapter.setTasks(taskList);
        // notify the task adapter that the dataset has changed.
        taskAdapter.notifyDataSetChanged();
    }
}