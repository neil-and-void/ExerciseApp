package com.example.exerciseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class WorkoutListActivity extends AppCompatActivity {

    private ArrayList<String> mWorkouts = new ArrayList<>();
    SQLiteDatabase presetWorkoutsDB;
    private WorkoutRecyclerAdapter mWorkoutAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        // create SQLite DB
        prepareDatabase();

        mWorkoutAdapter = new WorkoutRecyclerAdapter(mWorkouts);

        RecyclerView recyclerView = findViewById(R.id.workoutListRecyclerView);
        recyclerView.setAdapter(mWorkoutAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        SwipeController swipeToDeleteCallback = new SwipeController();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    /*
     *
     * function to allow button to add workouts
     *
     */
    public void editExercises(View view){
        Log.i("exerciseEdit", "ues");
        // todo: open new intent with exercises based on the workout we want to edit exercises for
    }

    /*
     *
     * function to allow button to add workouts
     *
     */
    public void addWorkout(View view){
        final EditText input = new EditText(this);
        final Button addWorkoutButton = findViewById(R.id.addWorkoutButton);

        // prompt user
        final AlertDialog addWorkoutDialog = new AlertDialog.Builder(this)
                .setView(input)
                .setTitle("Workout Name")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        addWorkoutDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) addWorkoutDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    // override onClick to ensure user enters at least 1 character in for workout name
                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        String inputWorkoutName = input.getText().toString();

                        if (inputWorkoutName.length() > 0){
                            // add item to workout adapter data set
                            mWorkoutAdapter.addWorkout(inputWorkoutName);
                            mWorkoutAdapter.notifyDataSetChanged();

                            //write to database
                            String sqlInsert = "INSERT INTO preset_workouts (preset_workout_name) VALUES ('" + inputWorkoutName + "')" ;
                            presetWorkoutsDB.execSQL(sqlInsert);


                            //Dismiss once everything is OK.
                            addWorkoutDialog.dismiss();

                        } else {
                            // if user input is blank
                            Toast.makeText(WorkoutListActivity.this, "Workout Name Must At Least 1 Character", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        addWorkoutDialog.show();

    }

    /*
     *
     * function to allow button to add workouts
     *
     */
    public void setWorkoutDataAdapter(ArrayList<String> workouts){


    }

    /*
    *
    * prepares database for use
    *
    */
    public void prepareDatabase(){
        Log.i("HELLO", "CAN YOU HEAR ME");
        try {
            presetWorkoutsDB = this.openOrCreateDatabase("Workouts", MODE_PRIVATE, null);

            // create preset_workouts table and preset_exercises table
            presetWorkoutsDB.execSQL("CREATE TABLE IF NOT EXISTS preset_workouts (preset_workout_id INTEGER PRIMARY KEY, preset_workout_name VARCHAR)");
            presetWorkoutsDB.execSQL("CREATE TABLE IF NOT EXISTS preset_exercises (preset_exercise_id INTEGER PRIMARY KEY, exercise_name VARCHAR, preset_workout_id INTEGER,FOREIGN KEY(preset_workout_id) REFERENCES preset_workouts(preset_workout_id) ON DELETE CASCADE )");

            // allow for cascading on delete
            presetWorkoutsDB.setForeignKeyConstraintsEnabled(true);

            // query for workoutId and workoutName
            Cursor cursor = presetWorkoutsDB.rawQuery("SELECT * from preset_workouts", null);
            int presetWorkoutIDIndex = cursor.getColumnIndex("preset_workout_id");
            int presetWorkoutNameIndex = cursor.getColumnIndex("preset_workout_name");

            cursor.moveToFirst();

            // check if query has returned anything
            if (cursor.getCount() > 0 && cursor != null) {
                do {
                    //todo: read previously set workouts from database into workouts ArrayList
                    Log.i("databaseStrings", Integer.toString(cursor.getInt(presetWorkoutIDIndex)) + " " + cursor.getString(presetWorkoutNameIndex));
                    mWorkouts.add(cursor.getString(presetWorkoutNameIndex));

                } while (cursor.moveToNext());
            }

        // general exception catcher
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
