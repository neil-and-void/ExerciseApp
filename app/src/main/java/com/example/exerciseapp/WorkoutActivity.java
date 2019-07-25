package com.example.exerciseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class WorkoutActivity extends AppCompatActivity {

    String mWorkoutName;
    String[] mWorkouts;
    private ExerciseDataAdapter mAdapter;

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

        // prompt user to choose workout day

    }

    public void setupAlertDialog(){
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

        mWorkouts = getResources().getStringArray(R.array.splits);

        builder.setSingleChoiceItems(mWorkouts, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("message", mWorkouts[which]);
                mWorkoutName = mWorkouts[which];

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
        Log.i("asdjlk;f","ADAPTER QWORKD");

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

}
