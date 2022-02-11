package com.dieantonie.todo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dieantonie.todo.Model.ToDoModel;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the older tables
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create table again
        onCreate(db);
    }

    public void openDatabase() {
        // set database
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task) {
        // declare content values
        ContentValues cv = new ContentValues();
        // Set content value task
        cv.put(TASK, task.getTask());
        // set content value task status
        cv.put(STATUS, 0);
        // Persist task data
        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> getAllTasks() {
        // Declare task list
        List<ToDoModel> taskList = new ArrayList<>();
        // Declare cursor
        Cursor cur = null;
        // begin database transaction
        db.beginTransaction();
        try {
            // Query table
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            // Check if data returned
            if (cur != null) {
                // Start cursor from top
                if (cur.moveToFirst()) {
                    do {
                        // Instantiate task
                        ToDoModel task = new ToDoModel();
                        // Set task id from retrieved data
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        // Set task text from retrieved data
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        // Set task status from retrieved data
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        // Add task to task list
                        taskList.add(task);
                    // continue to next database item
                    } while (cur.moveToNext());
                }
            }
        }
        finally {
            // close database transaction
            db.endTransaction();
            // check cursor is not null?
            assert cur != null;
            // close cursor
            cur.close();
        }
        // return compiled task list
        return taskList;
    }

    public void updateStatus(int id, int status) {
        // Declare content values
        ContentValues cv = new ContentValues();
        // update task status
        cv.put(STATUS, status);
        // Persist changes for task
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf((id))});
    }

    public void updateTask(int id, String task) {
        // Declare content values
        ContentValues cv = new ContentValues();
        // update task text
        cv.put(TASK, task);
        // Persist changes for task
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf((id))});
    }

    public void deleteTask(int id) {
        // Remove task from persistence
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf((id))});
    }
}
