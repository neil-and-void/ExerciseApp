package com.example.exerciseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    String mWorkoutName;
    String[] mWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create new alert dialog and allow user to select split
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

        AlertDialog dialog = builder.create();
        builder.show();

    }

}
