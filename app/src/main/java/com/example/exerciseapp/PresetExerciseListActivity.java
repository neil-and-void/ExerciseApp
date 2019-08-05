package com.example.exerciseapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class PresetExerciseListActivity extends AppCompatActivity {

    private SQLiteDatabase mRoutinesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        int routineId = getIntent().getIntExtra("routineId", -1);

        Toast.makeText(this, Integer.toString(routineId), Toast.LENGTH_SHORT).show();

        mRoutinesDB = this.openOrCreateDatabase("Routines", MODE_PRIVATE, null);

        prepareAndReadPresetExercisesFromDatabase();

    }

    private void prepareAndReadPresetExercisesFromDatabase(){

        // todo
        Cursor c = mRoutinesDB.rawQuery("", null);

    }
}
