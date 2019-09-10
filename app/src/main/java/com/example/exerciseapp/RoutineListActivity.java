package com.example.exerciseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RoutineListActivity extends AppCompatActivity {

    private SQLiteDatabase mRoutinesDB;
    private RoutineRecyclerAdapter mRoutineRecyclerAdapter;
    private RecyclerView mRoutineRecyclerView;
    private IdNameTupleList mRoutineList;
    private IdNameTupleList mRecentlyDeletedPresetExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        mRoutinesDB = this.openOrCreateDatabase("Routines", MODE_PRIVATE, null);

        // open or create DB and read workouts into mWorkoutArrayList
        prepareAndReadRoutinesFromDatabase();

        // take workouts in array list and populate recycler view with them
        populateRecyclerView();

        enableSwipeToDeleteAndUndo();
    }


    /*
     * function to allow button to add routines
     */
    public void addRoutine(View view){
        final EditText input = new EditText(this);

        // prompt user
        final AlertDialog addRoutineDialog = new AlertDialog.Builder(this)
                .setView(input)
                .setTitle("Routine Name")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        // create new OnShowListener to handle for workout name length
        addRoutineDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = addRoutineDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    // override onClick to ensure user enters at least 1 character in for workout name
                    @Override
                    public void onClick(View view) {
                        String inputRoutineName = input.getText().toString();
                        String formattedString = inputRoutineName.replaceAll("\\s+", "");

                        // check length of workout name
                        if (inputRoutineName.length() > 0){
                            boolean routineNameExistsAlready = false;

                            // check if the inputted workout has the same name as another workout
                            for (int i = 0; i < mRoutineRecyclerAdapter.getItemCount(); i++){
                                if (formattedString.equals(mRoutineRecyclerAdapter.getItem(i).replaceAll("\\s+", ""))){
                                    routineNameExistsAlready = true;
                                    break;
                                }
                            }
                            if (!routineNameExistsAlready) {

                                //write to database
                                String sqlInsert = "INSERT INTO routines (routine_name) VALUES ('" + inputRoutineName + "')";
                                mRoutinesDB.execSQL(sqlInsert);

                                // read back with newly generated Id
                                Cursor c = mRoutinesDB.rawQuery("SELECT routine_id FROM routines WHERE routine_name = '" + inputRoutineName + "'",null);
                                int idColIndex = c.getColumnIndex("routine_id");
                                c.moveToFirst();

                                // add item to workout adapter data set
                                mRoutineRecyclerAdapter.addRoutine(c.getInt(idColIndex), inputRoutineName);
                                mRoutineRecyclerAdapter.notifyDataSetChanged();

                                c.close();

                                //Dismiss once everything is OK.
                                addRoutineDialog.dismiss();
                            } else {
                                Toast.makeText(RoutineListActivity.this, "Workout Name Already Exists", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // if user input is blank
                            Toast.makeText(RoutineListActivity.this, "Workout Name Must At Least 1 Character", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        addRoutineDialog.show();
    }


    /*
     * allow for swiping to delete and undo
     */
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {

            // detect which item in RecyclerView was swiped
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                // save item in placeholders and delete them
                final int position = viewHolder.getAdapterPosition();
                final String routineName = mRoutineRecyclerAdapter.getData().returnName(position);
                final int routineId = mRoutineRecyclerAdapter.getData().returnId(position);
                mRecentlyDeletedPresetExercises = savePresetExercises(routineId);
                temporarilyDeleteItem(routineId,position);

                Log.i("deleted item", routineName + Integer.toString(position));

                // prompts user to undo with snackbar
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Routine was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // undo the deletion
                        restoreItem(routineId, routineName, position);
                        mRoutineRecyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRoutineRecyclerView);
    }


    /*
     * prepares database for use and read workouts from database
     */
    private void prepareAndReadRoutinesFromDatabase() {
        try {
            mRoutineList = new IdNameTupleList();

            // create routines table and preset_exercises table
            mRoutinesDB.execSQL("CREATE TABLE IF NOT EXISTS routines (routine_id INTEGER PRIMARY KEY, routine_name VARCHAR)");
            mRoutinesDB.execSQL("CREATE TABLE IF NOT EXISTS preset_exercises (preset_exercise_id INTEGER PRIMARY KEY AUTOINCREMENT, preset_exercise_name VARCHAR, routine_id INTEGER, FOREIGN KEY(routine_id) REFERENCES routines(routine_id) ON DELETE CASCADE )");

            // create workout and exercises tables
            mRoutinesDB.execSQL("Drop table workouts");
            mRoutinesDB.execSQL("CREATE TABLE IF NOT EXISTS workouts (workout_id TEXT PRIMARY KEY, workout_name VARCHAR,date VARCHAR)");
            mRoutinesDB.execSQL("CREATE TABLE IF NOT EXISTS exercises (exercise_id INTEGER PRIMARY KEY, exercise_name VARCHAR, reps INTEGER, weight INTEGER,  workout_id VARCHAR, FOREIGN KEY (workout_id) REFERENCES workouts (workout_id))");


            // allow for cascading on delete
            mRoutinesDB.setForeignKeyConstraintsEnabled(true);

            // query for routine_id and routine_name
            Cursor cursor = mRoutinesDB.rawQuery("SELECT * from routines", null);
            int routineIdIndex = cursor.getColumnIndex("routine_id");
            int routineNameIndex = cursor.getColumnIndex("routine_name");

            cursor.moveToFirst();
            // check if query has returned anything
            if (cursor.getCount() > 0 && cursor != null) {
                do {

                    // add to ArrayList that will hold routines and id's
                    mRoutineList.add(cursor.getInt(routineIdIndex), cursor.getString(routineNameIndex));

                    Log.i("databasePresetWorkouts", Integer.toString(cursor.getInt(routineIdIndex)) + " " + cursor.getString(routineNameIndex));

                } while (cursor.moveToNext());
            }
            cursor.close();

            // general exception catcher
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /*
     * populate the recycler view with array of workouts
     */
    private void populateRecyclerView(){
        mRoutineRecyclerAdapter = new RoutineRecyclerAdapter(mRoutineList);
        mRoutineRecyclerView = findViewById(R.id.workoutListRecyclerView);
        mRoutineRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRoutineRecyclerView.setAdapter(mRoutineRecyclerAdapter);

    }


    /*
     * temporarily save exercises for deleted routine
     */
    private IdNameTupleList savePresetExercises(int routineId){

        IdNameTupleList presetExerciseList = new IdNameTupleList();

        // save preset exercises
        Cursor cursor = mRoutinesDB.rawQuery("SELECT * FROM preset_exercises WHERE routine_id = '" + Integer.toString(routineId) + "'", null);
        int presetExerciseNameIndex = cursor.getColumnIndex("preset_exercise_name");
        int presetExerciseIdIndex = cursor.getColumnIndex("preset_exercise_id");

        cursor.moveToFirst();

        if (cursor.getCount() > 0 && cursor != null) {
            do{
                presetExerciseList.add(cursor.getInt(presetExerciseIdIndex), cursor.getString(presetExerciseNameIndex));
            } while(cursor.moveToNext());
        }

        cursor.close();
        return presetExerciseList;
    }


    /*
     * temporarily remove routine and its exercises from array and delete from database
     */
    private void temporarilyDeleteItem(int routineId, int position){
        // remove item from adapter and database
        mRoutineRecyclerAdapter.deleteItem(position);
        mRoutinesDB.execSQL("DELETE FROM routines WHERE routine_id ='" + routineId + "'");
    }


    /*
     * restore an item that has been recently deleted
     */
    private void restoreItem(int routineId, String routineName, int position){
        mRoutineRecyclerAdapter.restoreItem(routineId, routineName, position);
        mRoutinesDB.execSQL("INSERT INTO routines (routine_id,routine_name) VALUES (" + Integer.toString(routineId) + ",'" + routineName + "')");

        // read back into database the preset exercises
        for(int i = 0; i < mRecentlyDeletedPresetExercises.size(); i++){
            Log.i("deleted exercises", mRecentlyDeletedPresetExercises.returnName(i));
            mRoutinesDB.execSQL("INSERT INTO preset_exercises (preset_exercise_id, preset_exercise_name, routine_id) VALUES (" + mRecentlyDeletedPresetExercises.returnId(i) + ",'" + mRecentlyDeletedPresetExercises.returnName(i)  + "'," + routineId + ")");
        }
    }

}
