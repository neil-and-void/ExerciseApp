package com.example.exerciseapp;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

// keep track of button states
enum ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

public class SwipeController extends ItemTouchHelper.Callback {
    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE; // not visible at first
    private static final float buttonWidth = 300;

    // tell item touch helper what movements to handle
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    // not implementing this method so ignore
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    //
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
    }

    // blocks items from being completely swiped out
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection){
        if (swipeBack){
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    // check how far the user has swiped left or right and if conditions met, show button
    private void setTouchListener(final Canvas c,
                                  final RecyclerView recyclerView,
                                  final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY,
                                  final int actionState, final boolean isCurrentlyActive) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    if (dX < -buttonWidth) {
                        buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                    }
                    else if (dX > buttonWidth) {
                        buttonShowedState  = ButtonsState.LEFT_VISIBLE;
                    }
                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }




}
