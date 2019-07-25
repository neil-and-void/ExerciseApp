package com.example.exerciseapp;

// Exercise object that represents an exercise ie. exercise name, sets, reps, weight

public class Exercise {
    private String exerciseName;
    private int sets, reps, weight;

    public String getName(){ return exerciseName;}
    public void setName(String name){this.exerciseName = name; }

    public Integer getExerciseSets(){ return sets;}
    public void setExerciseSets(int setInt){this.sets = setInt;}

    public Integer getReps(){ return reps;}
    public void setReps(int repInt){this.reps = repInt;}

}
