package com.example.exerciseapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDataAdapter extends RecyclerView.Adapter<ExerciseDataAdapter.ExerciseViewHolder> {
    private List<Exercise> mExerciseSet;

    // Provide a reference to the views fo each data item
    public class ExerciseViewHolder extends RecyclerView.ViewHolder{
        // each data item is an EditText
        private EditText exerciseName, sets, reps;

        public ExerciseViewHolder(View view){
            super(view);
            exerciseName = view.findViewById(R.id.ExerciseTextEdit);
            sets = view.findViewById(R.id.setsEditText);
            reps = view.findViewById(R.id.repsEditText);
        }
    }

    // Constructor
    public ExerciseDataAdapter(List<Exercise> ExerciseSet){
        this.mExerciseSet = ExerciseSet;

    }

    // create viewHolder and inflate view
    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // create new view from xml layout file
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercise_row_view, viewGroup, false);
        return new ExerciseViewHolder(itemView);
    }

    // bind data into viewHolder
    @Override
    public void onBindViewHolder(ExerciseViewHolder exerciseViewHolder, int position){
        Exercise exercise = mExerciseSet.get(position);
        exerciseViewHolder.exerciseName.setText(exercise.getName());
        exerciseViewHolder.sets.setText(exercise.getExerciseSets().toString());
        exerciseViewHolder.reps.setText(exercise.getReps().toString());

    }

    @Override
    public int getItemCount() {
        return mExerciseSet.size();
    }

    public void addData(Exercise exercise){
        mExerciseSet.add(exercise);

    }


}
