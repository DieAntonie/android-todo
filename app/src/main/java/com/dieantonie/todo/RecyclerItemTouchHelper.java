package com.dieantonie.todo;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dieantonie.todo.Adapter.ToDoAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;

    public RecyclerItemTouchHelper(ToDoAdapter adapter) {
        // specifying possible swipe directions?
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        // set adapter to instantiate ToDoAdapter
        this.adapter = adapter;
    }

    @Override
    // Called when ItemTouchHelper wants to move the dragged item from its old position to the new position.
    // If this method returns true, ItemTouchHelper assumes viewHolder has been moved to the adapter position of target ViewHolder.
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // ItemTouchHelper assumes viewHolder won't be moved to the adapter position of target ViewHolder.
        return false;
    }

    @Override
    // ItemTouchHelper.Callback Called when a ViewHolder is swiped by the user.
    // If you are returning relative directions (START , END) from the getMovementFlags(RecyclerView, RecyclerView.ViewHolder) method, this method will also use relative directions. Otherwise, it will use absolute directions.
    // If you don't support swiping, this method will never be called.
    // ItemTouchHelper will keep a reference to the View until it is detached from RecyclerView. As soon as it is detached, ItemTouchHelper will call clearView(RecyclerView, RecyclerView.ViewHolder).
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        // Get the Adapter position of the item represented by this ViewHolder.
        final int position = viewHolder.getAdapterPosition();
        // If Swiped left // Delete
        if (direction == ItemTouchHelper.LEFT) {
            // Instantiate new Alert dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            // Set alert title
            builder.setTitle("Delete Task");
            // set alert message
            builder.setMessage("Are you sure you want to delete this Task?");
            // set "positive" button text and action
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    // delete item from adapter at position.
                    adapter.deleteItem(position);
                }
            });
            // set "negative" button text and action
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Don't know why this would be necessary
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            // create alert from alert builder
            AlertDialog dialog = builder.create();
            // show alert
            dialog.show();
        // If Swiped right // edit
        } else {
            // edit item at position
            adapter.editItem(position);
        }
    }

    @Override
    // Called by ItemTouchHelper on RecyclerView's onDraw callback.
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentActivity) {
        // run super
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentActivity);

        // declare icon
        Drawable icon;
        // declare background
        ColorDrawable background;
        // set item view
        View itemView = viewHolder.itemView;
        // set background corner offset
        int backgroundCornerOffSet = 20;
        // if swiped right // edit
        if (dX > 0) {
            // set icon to edit icon
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit_24);
            // set background to gree
            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.design_default_color_primary_dark));
        // if swiped left // delete
        } else {
            // set icon to trashcan
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete_24);
            // set background to red
            background = new ColorDrawable(Color.RED);
        }

        // check if icon is set
        assert icon != null;
        // set icon margin
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        // set icon top position
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        // set icon bottom position
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        //if swiped right // edit
        if (dX > 0) {
            // position icon to the left
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            // set background position to the left
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int)dX) + backgroundCornerOffSet, itemView.getBottom());
        //if swiped left // delete
        } else if (dX < 0) {
            // position icon to the right
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            // set background position to the right
            background.setBounds(itemView.getRight() + ((int)dX) - backgroundCornerOffSet,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {
            // "hide" background
            background.setBounds(0,0,0,0);
        }
        // render background
        background.draw(canvas);
        // render icon
        icon.draw(canvas);
    }
}
