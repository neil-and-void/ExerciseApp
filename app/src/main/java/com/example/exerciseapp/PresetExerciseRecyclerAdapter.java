package com.example.exerciseapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class PresetExerciseRecyclerAdapter extends RecyclerView.Adapter<PresetExerciseRecyclerAdapter.PresetExerciseViewHolder> {

    public class PresetExerciseViewHolder extends RecyclerView.ViewHolder {


        public PresetExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public PresetExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PresetExerciseViewHolder presetExerciseViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
