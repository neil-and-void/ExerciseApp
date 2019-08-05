package com.example.exerciseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class WorkoutActivity extends AppCompatActivity {

    String mWorkoutName;
    private ExerciseDataAdapter mAdapter;
    SQLiteDatabase mRoutinesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupAlertDialog();

        setExerciseDataAdapter();
        setupRecyclerView();

        // add exercise to workout
        FloatingActionButton addExercise = findViewById(R.id.fab);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                // prompt to choose from a list of exercises for the given day
                Exercise exercise = new Exercise();
                exercise.setName("leg press");
                exercise.setReps(45);
                exercise.setExerciseSets(324);
                mAdapter.addData(exercise);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    private void setupAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this);

        // add workout
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //TODO: add workout to DB
                Log.i("workout", "start " + mWorkoutName);

            }
        });

        // cancel workout
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // TODO: go back to previous screen
                Log.i("workout", "cancel");

            }
        });

        // read preset workouts from database
        final List<String> items = queryWorkouts();
        builder.setSingleChoiceItems(items.toArray(new String[items.size()]), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // get the set of exercises for the selected workout from the database
            }
        });

        builder.show();
    }

    // read data from DB
    // place data into correct Exercise object attributes
    // create Arraylist of Exercises
    private void setExerciseDataAdapter(){

        ArrayList<Exercise> exercises = new ArrayList<>();

        Exercise e = new Exercise();
        e.setName("Legs");
        e.setExerciseSets(4);
        e.setReps(10);

        exercises.add(e);

        e.setName("chest");
        e.setExerciseSets(4);
        e.setReps(8);

        exercises.add(e);

        //todo: read from SQLite DB
        mAdapter = new ExerciseDataAdapter(exercises);

    }

    private void setupRecyclerView(){

        // Instantiate RecyclerView and set adapter and layout manager
        RecyclerView recyclerView = findViewById(R.id.ExerciseRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // attach helper to RecyclerView
        SwipeController swipeToDeleteCallback = new SwipeController();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private List<String> queryWorkouts(){
        try{
            // List to be returned
            List<String> workoutList = new ArrayList<>();

            // open database to work with
            mRoutinesDB = this.openOrCreateDatabase("Routines", MODE_PRIVATE, null);

            // query for preset_workout_names
            Cursor c = mRoutinesDB.rawQuery("SELECT routine_name FROM routines", null);
            int presetWorkoutNameIndex = c.getColumnIndex("routine_name");
            c.moveToFirst();

            // check if there is data returned in query
            if (c.getCount() > 0 && c != null) {
                do {
                    // add to workout
                    workoutList.add(c.getString(presetWorkoutNameIndex));

                } while (c.moveToNext());

                return workoutList;

            } else {
                // todo: handle null returned from query
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    
}
