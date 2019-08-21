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

        setupRecyclerView();

        // add exercise to workout
        FloatingActionButton addExercise = findViewById(R.id.fab);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // todo: add new

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
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
                readPresetExercisesFromDB(1);

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

    private void setupRecyclerView(){

        // Instantiate RecyclerView and set adapter and layout manager
        mAdapter = new ExerciseDataAdapter();
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

    private void readPresetExercisesFromDB(int routineId){
        // todo: read preset exercises from database and put into the list for the alert dialog
        Cursor cursor = mRoutinesDB.rawQuery("SELECT * FROM preset_exercises WHERE routine_id = " + Integer.toString(routineId), null);

        int exerciseNameIndex = cursor.getColumnIndex("preset_exercise_name");
        int exerciseIdIndex = cursor.getColumnIndex("preset_exercise_id");

        cursor.moveToFirst();

        if (cursor != null) {
            do {
                Log.i("exercise database read", cursor.getString(exerciseNameIndex) + " " + Integer.toString(cursor.getInt(exerciseIdIndex)));
            } while (cursor.moveToNext());
        }
    }
    
}
