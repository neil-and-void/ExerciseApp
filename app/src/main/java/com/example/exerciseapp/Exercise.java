package com.example.exerciseapp;

// Exercise object that represents an exercise ie. exercise name, id, sets, reps, weight

public class Exercise {
    private int id;
    private String exerciseName;
    private int[] repsArray = {0,0,0,0};
    private int[] weightArray = {0,0,0,0};

    /*
     * constructor
     */
    public Exercise(){
    }

    public String getName(){ return exerciseName;}

    public Integer getId() { return id; }

    public void setId(int id) { this.id = id;}

    public void setName(String name){this.exerciseName = name;}

    public void addReps(int repCount, int pos){repsArray[pos] = repCount;}
    public void addWeight(int weight, int pos){weightArray[pos] = weight;}

    public int[] getWeightArray(){return weightArray;}
    public int[] getRepsArray(){return repsArray;}

    public Integer getReps(int pos){
        return repsArray[pos];
    }
    public Integer getWeight(int pos){
        return weightArray[pos];
    }


}
