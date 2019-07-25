package com.example.exerciseapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

public class WorkoutRecyclerAdapter extends RecyclerView.Adapter<WorkoutRecyclerAdapter.WorkoutViewHolder> {
    private ArrayList<String> mWorkouts;

    public class WorkoutViewHolder extends RecyclerView.ViewHolder{
        private EditText workoutName;

        public WorkoutViewHolder(View view){
            super(view);
            workoutName = view.findViewById(R.id.workoutNameTextView);

        }

    }

    public WorkoutRecyclerAdapter(ArrayList<String> workouts){
        this.mWorkouts = workouts;

    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workout_row_view,viewGroup,false);
        return new WorkoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder workoutViewHolder, int i) {
        String workout = mWorkouts.get(i);
        workoutViewHolder.workoutName.setText(workout);

    }

    @Override
    public int getItemCount() {
        return mWorkouts.size();
    }

    public void addWorkout(String workoutName){
        mWorkouts.add(workoutName);

    }



}
