package com.example.exerciseapp;

import java.util.ArrayList;
import java.util.List;

// class that holds list of workout names
// and list of ID's for accessing routines and exercises
// allows array of ID's and name's to be interacted with as a single object
public class IdNameTupleList {
    private ArrayList<String> mRoutineNames;
    private ArrayList<Integer> mRoutineIds;

    /*
     * constructor
     */
    public IdNameTupleList(){
        mRoutineIds = new ArrayList<>();
        mRoutineNames = new ArrayList<>();
    }

    public ArrayList<String> getNameList(){
        return mRoutineNames;
    }

    /*
     * return name from position
     */
    public String returnName(int position){ return mRoutineNames.get(position);}

    /*
     * return id from position
     */
    public int returnId(int position){return mRoutineIds.get(position);}

    /*
     * add an id-name object to the list
     */
    public void add(int Id, String Name){
        mRoutineIds.add(Id);
        mRoutineNames.add(Name);
    }

    /*
     * add an id-name object to the list at a position
     */
    public void add(int Id, String Name, int position){
        mRoutineIds.set(position, Id);
        mRoutineNames.set(position, Name);
    }

    /*
     * remove an id-name object from the list
     */
    public void remove(int position){
        mRoutineNames.remove(position);
        mRoutineIds.remove(position);
    }

    /*
     * return size
     */
    public int size(){
        return mRoutineIds.size();
    }

    /*
     *
     */
    public void clear(){
        mRoutineIds.clear();
        mRoutineNames.clear();
    }

}
