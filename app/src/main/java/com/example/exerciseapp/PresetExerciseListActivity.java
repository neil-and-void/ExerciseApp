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

public class PresetExerciseListActivity extends AppCompatActivity {

    private SQLiteDatabase mRoutinesDB;
    private int mRoutineId;
    private RoutineList mPresetExerciseList;
    private PresetExerciseRecyclerAdapter mPresetExerciseRecyclerAdapter;
    private RecyclerView mPresetExerciseRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        mRoutineId = getIntent().getIntExtra("routineId", -1);

        Toast.makeText(this, Integer.toString(mRoutineId), Toast.LENGTH_SHORT).show();

        mRoutinesDB = this.openOrCreateDatabase("Routines", MODE_PRIVATE, null);

        prepareAndReadPresetExercisesFromDatabase();

        populateRecyclerView();

        enableSwipeToDeleteAndUndo();

    }

    public void addPresetExercise(View view){
        final EditText input = new EditText(this);

        // prompt user
        final AlertDialog addRoutineDialog = new AlertDialog.Builder(this)
                .setView(input)
                .setTitle("Exercise Name")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        // create new OnShowListener to handle for preset workout name length
        addRoutineDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = addRoutineDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    // override onClick to ensure user enters at least 1 character in for workout name
                    @Override
                    public void onClick(View view) {
                        String inputExerciseName = input.getText().toString();


                        // get rid of whitespace
                        inputExerciseName =  inputExerciseName.replaceAll("\\s+","");

                        // check length of workout name
                        if (inputExerciseName.length() > 0){
                            boolean exerciseNameExistsAlready = false;

                            // check if the inputted workout has the same name as another workout
                            for (int i = 0; i < mPresetExerciseRecyclerAdapter.getItemCount(); i++){
                                if (inputExerciseName.equals(mPresetExerciseRecyclerAdapter.getItem(i))){
                                    exerciseNameExistsAlready = true;
                                    break;
                                }
                            }
                            if (!exerciseNameExistsAlready) {

                                //write to database
                                String sqlInsert = "INSERT INTO preset_exercises (preset_exercise_name, routine_id) VALUES ('" + inputExerciseName + "'," + mRoutineId + ")";
                                mRoutinesDB.execSQL(sqlInsert);

                                // add item to workout adapter data set
                                mPresetExerciseRecyclerAdapter.addPresetExercise(0, inputExerciseName);
                                mPresetExerciseRecyclerAdapter.notifyDataSetChanged();

                                //Dismiss once everything is OK.
                                addRoutineDialog.dismiss();
                            } else {
                                Toast.makeText(PresetExerciseListActivity.this, "Workout Name Already Exists", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // if user input is blank
                            Toast.makeText(PresetExerciseListActivity.this, "Workout Name Must At Least 1 Character", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        addRoutineDialog.show();
    }

    private void prepareAndReadPresetExercisesFromDatabase(){
        mPresetExerciseList = new RoutineList();

        // todo
        Cursor c = mRoutinesDB.rawQuery("SELECT * FROM preset_exercises WHERE routine_id =" + Integer.toString(mRoutineId), null);
        c.moveToFirst();

        int presetExerciseIdIndex = c.getColumnIndex("preset_exercise_id");
        int presetExerciseNameIndex = c.getColumnIndex("preset_exercise_name");

        if (c.getCount() > 0 && c != null) {
            do {

                // add to ArrayList that will hold routines and id's
                mPresetExerciseList.addRoutine(c.getInt(presetExerciseIdIndex), c.getString(presetExerciseNameIndex));


            } while (c.moveToNext());
        }
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {

            // detect which item in RecyclerView was swiped
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                // save item in placeholders and delete them
                final int position = viewHolder.getAdapterPosition();
                final String presetExerciseName = mPresetExerciseRecyclerAdapter.getData().returnRoutineName(position);
                final int presetExerciseId = mPresetExerciseRecyclerAdapter.getData().returnRoutineId(position);
                Log.i("asd;lfjk", Integer.toString(presetExerciseId));
                mPresetExerciseRecyclerAdapter.deleteItem(position);
                mRoutinesDB.execSQL("DELETE FROM preset_exercises WHERE preset_exercise_id = '" + presetExerciseId + "'");

                Log.i("deleted item", presetExerciseName + Integer.toString(position));

                // prompts user to undo with snackbar
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Routine was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // undo the delete
                        mPresetExerciseRecyclerAdapter.restoreItem(presetExerciseId, presetExerciseName, position);
                        mRoutinesDB.execSQL("INSERT INTO preset_exercises (preset_exercise_id ,routine_id, preset_exercise_name) VALUES (" + Integer.toString(presetExerciseId) + ", " +  Integer.toString(mRoutineId)+ ", '" + presetExerciseName + "')");
                        mPresetExerciseRecyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mPresetExerciseRecyclerView);
    }

    private void populateRecyclerView(){
        mPresetExerciseRecyclerAdapter = new PresetExerciseRecyclerAdapter(mPresetExerciseList);
        mPresetExerciseRecyclerView = findViewById(R.id.presetExerciseRecyclerView);
        mPresetExerciseRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mPresetExerciseRecyclerView.setAdapter(mPresetExerciseRecyclerAdapter);
    }
}
