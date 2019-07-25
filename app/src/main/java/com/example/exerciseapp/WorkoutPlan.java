package com.example.exerciseapp;

import java.util.ArrayList;

public class WorkoutPlan {

    private ArrayList<Workout> mWorkoutArray;

    public void WorkoutPlan(){
        mWorkoutArray = new ArrayList<>();
    }

    public void addWorkout(Workout workout){
        mWorkoutArray.add(workout);
    }
}
