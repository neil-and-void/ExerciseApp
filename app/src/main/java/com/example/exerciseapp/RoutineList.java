package com.example.exerciseapp;

import java.util.ArrayList;

// class that holds list of workout names for the adapter
// and list of ID's for accessing exercises
// allows array of ID's and name's to be interacted with as one object
public class RoutineList {
    private ArrayList<String> mRoutineNames;
    private ArrayList<Integer> mRoutineIds;

    public RoutineList(){
        mRoutineIds = new ArrayList<>();
        mRoutineNames = new ArrayList<>();
    }

    public String returnRoutineName(int position){ return mRoutineNames.get(position);}

    public int returnRoutineId(int position){return mRoutineIds.get(position);}

    public void addRoutine(int routineId, String routineName){
        mRoutineIds.add(routineId);
        mRoutineNames.add(routineName);
    }

    public void addRoutine(int routineId, String routineName, int position){
        mRoutineIds.add(position, routineId);
        mRoutineNames.add(position, routineName);
    }

    public void removeRoutine(int position){
        mRoutineNames.remove(position);
        mRoutineIds.remove(position);
    }

    public int size(){
        return mRoutineIds.size();
    }

}
