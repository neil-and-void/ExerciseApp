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
    private RoutineList mRoutineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        mRoutinesDB = this.openOrCreateDatabase("Routines", MODE_PRIVATE, null);

        // open or create DB and read workouts into mWorkoutArrayList
        prepareAndReadRoutinesFromDatabase();

        // take workouts in array list and populate recycler view with them
        populateRecyclerView();

        //
        enableSwipeToDeleteAndUndo();
    }

    /*
     *
     * function to allow button to add routines
     *
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


                        // get rid of whitespace
                        inputRoutineName =  inputRoutineName.replaceAll("\\s+","");

                        // check length of workout name
                        if (inputRoutineName.length() > 0){
                            boolean routineNameExistsAlready = false;

                            // check if the inputted workout has the same name as another workout
                            for (int i = 0; i < mRoutineRecyclerAdapter.getItemCount(); i++){
                                if (inputRoutineName.equals(mRoutineRecyclerAdapter.getItem(i))){
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
     *
     *
     *
     */
    private void enableSwipeToDeleteAndUndo() { // todo: implement undo to also undo deletion of that routines preset exercises
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {

            // detect which item in RecyclerView was swiped
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                // save item in placeholders and delete them
                final int position = viewHolder.getAdapterPosition();
                final String routineName = mRoutineRecyclerAdapter.getData().returnRoutineName(position);
                final int routineId = mRoutineRecyclerAdapter.getData().returnRoutineId(position);
                mRoutineRecyclerAdapter.deleteItem(position);
                mRoutinesDB.execSQL("DELETE FROM routines WHERE routine_id ='" + routineId + "'");

                Log.i("deleted item", routineName + Integer.toString(position));

                // prompts user to undo with snackbar
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Routine was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // undo the delete
                        mRoutineRecyclerAdapter.restoreItem(routineId, routineName, position);
                        mRoutinesDB.execSQL("INSERT INTO routines (routine_id,routine_name) VALUES (" + Integer.toString(routineId) + ",'" + routineName + "')");
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
     *
     * prepares database for use and read workouts from database
     *
     */
    private void prepareAndReadRoutinesFromDatabase() {
        try {
            mRoutineList = new RoutineList();

            // create preset_workouts table and preset_exercises table
            mRoutinesDB.execSQL("CREATE TABLE IF NOT EXISTS routines (routine_id INTEGER PRIMARY KEY, routine_name VARCHAR)");
            mRoutinesDB.execSQL("CREATE TABLE IF NOT EXISTS preset_exercises (preset_exercise_id INTEGER PRIMARY KEY, preset_exercise_name VARCHAR, routine_id INTEGER PRIMARY KEY, FOREIGN KEY(routine_id) REFERENCES routines(routine_id) ON DELETE CASCADE )");

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
                    mRoutineList.addRoutine(cursor.getInt(routineIdIndex), cursor.getString(routineNameIndex));

                    Log.i("databasePresetWorkouts", Integer.toString(cursor.getInt(routineIdIndex)) + " " + cursor.getString(routineNameIndex));

                } while (cursor.moveToNext());
            }

            // general exception catcher
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /*
     *
     * populate the recycler view with array of workouts
     *
     */
    private void populateRecyclerView(){
        mRoutineRecyclerAdapter = new RoutineRecyclerAdapter(mRoutineList);
        mRoutineRecyclerView = findViewById(R.id.workoutListRecyclerView);
        mRoutineRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRoutineRecyclerView.setAdapter(mRoutineRecyclerAdapter);

    }


}
