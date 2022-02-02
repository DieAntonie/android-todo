package com.dieantonie.todo;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dieantonie.todo.Model.ToDoModel;
import com.dieantonie.todo.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionButtonDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle saveInstanceState) {
        // Run bottom sheet DialogFragment onCreate(Bundle)
        super.onCreate(saveInstanceState);
        // Run bottom sheet DialogFragment setStyle(STYLE_NORMAL, DialogStyle)
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        // set view from new_task layout, detached from root.
        View view = inflater.inflate(R.layout.new_task, container, false);
        // set bottom sheet DialogFragment's dialog window's input mode to SOFT_INPUT_ADJUST_RESIZE.
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        // Run bottom sheet DialogFragment onViewCreated(View, Bundle)
        super.onViewCreated(view, saveInstanceState);
        // set task text to "new task tex".
        newTaskText = Objects.requireNonNull(getView()).findViewById(R.id.newTaskText);
        // set task save button to "new task button".
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);

        // reset is update to false
        boolean isUpdate = false;

        // set bundle to args
        final Bundle bundle = getArguments();
        // if there are arguments
        if (bundle != null) {
            // set updated indicator
            isUpdate = true;
            // get task string from args
            String task = bundle.getString("task");
            // set task text
            newTaskText.setText(task);
            // if valid task text
            if (task.length() > 0) {
                // activate new task save button
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.design_default_color_primary_dark));
            }
        }

        // instantiate new database handler
        db = new DatabaseHandler(getActivity());
        // set the Writable Database for the database handler.
        db.openDatabase();

        // set text change listner on task text
        newTaskText.addTextChangedListener(new TextWatcher() {
            // disable before Text Changed event
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            // override Text Changed event
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // toggle task save button if valid task text
                if (charSequence.toString().equals((""))) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.design_default_color_primary_dark));
                }
            }

            // disable after Text Changed event
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // final update check? as opposed to new task
        boolean finalIsUpdate = isUpdate;
        // set task save button on click listener
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            // override on click event
            @Override
            public void onClick(View v) {
                // get task text
                String text = newTaskText.getText().toString();
                // validate is update
                if (finalIsUpdate) {
                    // update task changes
                    db.updateTask(bundle.getInt("id"), text);
                } else {
                    // instantiate new task
                    ToDoModel task = new ToDoModel();
                    // set new task text
                    task.setTask(text);
                    // set status unchecked
                    task.setStatus(0);
                    // persist new task
                    db.insertTask(task);
                }
                // run bottom sheet DialogFragment dismiss()
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        // get activity
        Activity activity = getActivity();
        // validate if activity is Dialog Close Listener
        if (activity instanceof DialogCloseListener) {
            // run dialog close listener handler
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }
}
