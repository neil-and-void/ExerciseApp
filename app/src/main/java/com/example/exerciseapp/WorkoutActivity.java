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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class WorkoutActivity extends AppCompatActivity {

    private boolean workoutGoingOnCurrently = true;
    private String mWorkoutName;
    private ExerciseDataAdapter mExerciseRecyclerAdapter;
    private RecyclerView mExerciseRecyclerView;
    private SQLiteDatabase mRoutinesDB;
    private IdNameTupleList mRoutineList;
    private IdNameTupleList mPresetExerciseList; // list for the presets
    private ArrayList<Exercise> mExerciseList; // list for the exercises being done in the workout
    // NOTE: might need an array list of exercise objects to keep track of workout even when jumping to different screens in the app and
    // for saving to database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // check if there is a current workout going on

        // read routines from database
        queryRoutines();

        // prompt user to select the routine for the workout
        setupRoutineAlertDialog();

        // setup recycler view with custom items
        setupRecyclerView();

        setupAddExerciseButton();

        restoreCurrentWorkout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_EndWorkout) {

            // end workout
            // save workout
            saveRoutine();

        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * setup alert dialog to prompt user to choose the routine for the workout
     */
    private void setupRoutineAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this);

        // display the list of routines onto the alert dialog to choose from
        builder.setSingleChoiceItems(mRoutineList.getNameList().toArray(new String[mRoutineList.getNameList().size()]), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // get the set of exercises for the selected workout from the database

            }
        });


        // confirm routine for the workout
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //TODO: add workout to DB and get the preset exercises ready to add
                // get the set of exercises for the selected workout from the database
                dialog.dismiss();
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                mWorkoutName = mRoutineList.returnName(selectedPosition);
                Toast.makeText(getApplicationContext(), "Started " + mWorkoutName + " Workout", Toast.LENGTH_LONG).show();
                queryPresetExercises(mRoutineList.returnId(selectedPosition));
            }
        });

        // cancel workout
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // TODO: go back to previous screen
            }
        });

        builder.show();
    }


    /*
     * setup button to prompt user with what exercise to add
     */
    private void setupAddExerciseButton(){
        // add exercise to workout
        FloatingActionButton addExercise = findViewById(R.id.fab);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                // prompt user to add an exercise
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this);
                builder.setSingleChoiceItems(mPresetExerciseList.getNameList().toArray(new String[mPresetExerciseList.getNameList().size()]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                // set functionality of positive button
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        Exercise exercise = new Exercise();
                        exercise.setName(mPresetExerciseList.returnName(selectedPosition));
                        exercise.setId(mPresetExerciseList.returnId(selectedPosition));
                        mExerciseRecyclerAdapter.addExercise(exercise);
                        // add that id to the array
                    }
                });
                // set functionality of negative button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });
    }

    /*
     *
     */
    private void setupRecyclerView(){
        // Instantiate RecyclerView and set adapter and layout manager
        mExerciseList = new ArrayList<>();
        mExerciseRecyclerAdapter = new ExerciseDataAdapter(mExerciseList);
        mExerciseRecyclerView = findViewById(R.id.ExerciseRecyclerView);
        mExerciseRecyclerView.setAdapter(mExerciseRecyclerAdapter);
        mExerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    /*
     *
     */
    private void queryRoutines(){
        mRoutineList = new IdNameTupleList();
        // open database to work with
        mRoutinesDB = this.openOrCreateDatabase("Routines", MODE_PRIVATE, null);
        try{


            // query for preset_workout_names
            Cursor c = mRoutinesDB.rawQuery("SELECT * FROM routines", null);
            int routineNameIndex = c.getColumnIndex("routine_name");
            int routineIdIndex = c.getColumnIndex("routine_id");
            c.moveToFirst();

            // check if there is data returned in query
            if (c.getCount() > 0 && c != null) {
                do {
                    // add to workout
                    mRoutineList.add(c.getInt(routineIdIndex), c.getString(routineNameIndex));

                } while (c.moveToNext());


            } else {
                // todo: handle null returned from query, make a toast
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void queryPresetExercises(int routineId){
        mPresetExerciseList = new IdNameTupleList();
        // query for the exercises that belong to the routineId
        Cursor cursor = mRoutinesDB.rawQuery("SELECT * FROM preset_exercises WHERE routine_id = " + Integer.toString(routineId), null);
        int exerciseNameIndex = cursor.getColumnIndex("preset_exercise_name");
        int exerciseIdIndex = cursor.getColumnIndex("preset_exercise_id");

        cursor.moveToFirst();

        // check if cursor returned non-null query
        if (cursor != null) {
            do {

                // add to our list to use for adding exercises to workout
                mPresetExerciseList.add(cursor.getInt(exerciseIdIndex), cursor.getString(exerciseNameIndex));
            } while (cursor.moveToNext());
        }
    }

    private void saveRoutine(){
        // iterate through all items in the recycler view
        /*
        for(Exercise exercise: mExerciseRecyclerAdapter.getData()){
            String s1 = "";
            String s2 = "";
            for(int j = 0; j < 4; j++){
                s1 += exercise.getReps(j).toString();
                s2 += exercise.getWeight(j).toString();
            }

            Log.i("exercise attributes", exercise.getName() + " reps " + s1 + " weight "  + s2);
        }*/

        // get and format date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = Calendar.getInstance().getTime();
        String formattedDate = simpleDateFormat.format(date);

        // generate ID
        String workoutId = UUID.randomUUID().toString();

        // store workout_id, workout and date to database
        mRoutinesDB.execSQL("INSERT INTO workouts (workout_id, workout_name, date) VALUES ('" + workoutId + "','" + mWorkoutName + "','" + formattedDate + "')");

        for (Exercise exercise: mExerciseRecyclerAdapter.getData()){
            for(int i = 0; i < mExerciseRecyclerAdapter.getData().size(); i++){
                mRoutinesDB.execSQL("INSERT  INTO exercises(exercise_name, workout_id, reps, weight) VALUES ('" + exercise.getName() + "','" + workoutId + "'," + Integer.toString(exercise.getReps(i)) + "," + Integer.toString(exercise.getWeight(i)) + ")");

            }
        }

        // go back to main activity
        workoutGoingOnCurrently = false;

    }

    // keeps current workout from disappearing if the user goes back to any of the other screens
    private void restoreCurrentWorkout() {
        Cursor cursor = mRoutinesDB.rawQuery("SELECT * FROM workouts", null);
        int workoutIdIndex = cursor.getColumnIndex("workout_id");
        int workoutNameIndex = cursor.getColumnIndex("workout_name");
        int dateIndex = cursor.getColumnIndex("date");

        cursor.moveToFirst();

        do {

            Log.i("read the database", (cursor.getString(workoutIdIndex)) + " " + cursor.getString(workoutNameIndex) + " " + cursor.getString(dateIndex));

        } while(cursor.moveToNext());
    }
    
}
