package com.example.exerciseapp;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDataAdapter extends RecyclerView.Adapter<ExerciseDataAdapter.ExerciseViewHolder> {
    private ArrayList<Exercise> mExerciseList;
    private String mRecentlyDeletedExerciseName;
    private int mRecentlyDeletedExerciseId;
    private int mRecentlyDeletedItemPosition;



    // Provide a reference to the views for each data item
    public class ExerciseViewHolder extends RecyclerView.ViewHolder{
        // each data item is an EditText
        private TextView exerciseName;
        private ArrayList<EditText> repsArray, weightArray;
        private EditText reps0, reps1, reps2, reps3, weight0, weight1, weight2, weight3;
        private ImageView deleteExerciseItemView;


        public ExerciseViewHolder(View view){
            super(view);
            exerciseName = view.findViewById(R.id.ExerciseNameTextView);

            // attach reps EditText to the Views
            reps0 = view.findViewById(R.id.RepEditText0);
            reps1 = view.findViewById(R.id.RepEditText1);
            reps2 = view.findViewById(R.id.RepEditText2);
            reps3 = view.findViewById(R.id.RepEditText3);

            // attach weight EditText to the views
            weight0 = view.findViewById(R.id.WeightEditText0);
            weight1 = view.findViewById(R.id.WeightEditText1);
            weight2 = view.findViewById(R.id.WeightEditText2);
            weight3 = view.findViewById(R.id.WeightEditText3);

            // add rep EditTexts to array for easier handling
            repsArray = new ArrayList<>();
            repsArray.add(reps0);
            repsArray.add(reps1);
            repsArray.add(reps2);
            repsArray.add(reps3);

            // add weight EditTexts to array for easier handling
            weightArray= new ArrayList<>();
            weightArray.add(weight0);
            weightArray.add(weight1);
            weightArray.add(weight2);
            weightArray.add(weight3);

            // attach delete Image Button
            deleteExerciseItemView = view.findViewById(R.id.deleteExerciseImageView);
            deleteExerciseItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i = 0; i < weightArray.size(); i++){
                        weightArray.get(i).setText("");
                        repsArray.get(i).setText("");
                    }
                    deleteItem(getAdapterPosition());
                }
            });

            // set text watchers to retrieve input sets and reps of each EditText
            for(int i = 0; i < 4; i++){
                final int pos = i;
                repsArray.get(i).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // add input reps to Exercise
                        int adapterPos = getAdapterPosition();
                        // fix bug when typing stuff and deleting it all in the edit text
                        int repCount = ( charSequence.toString().isEmpty() ? 0 : Integer.parseInt(charSequence.toString()));
                        int repPos = Integer.parseInt(repsArray.get(pos).getTag().toString());
                        mExerciseList.get(adapterPos).addReps(repCount, repPos);
                        Log.i("weight|", "adapterPos: " + Integer.toString(getAdapterPosition()) + "  | repPos" + Integer.toString(repPos) + "   |   which index: " + weightArray.get(pos).getTag().toString());

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Log.i("edit", editable.toString());
                    }
                });

                weightArray.get(i).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // add input weight to Exercises
                        int adapterPos = getAdapterPosition();
                        int weight = ( charSequence.toString().isEmpty() ? 0 : Integer.parseInt(charSequence.toString()));
                        int weightPos = Integer.parseInt(weightArray.get(pos).getTag().toString());
                        mExerciseList.get(adapterPos).addWeight(weight, weightPos);
                        Log.i("weight|", "adapterPos: " + Integer.toString(getAdapterPosition()) + "  | weightPos" + Integer.toString(weightPos) + "   |   which index: " + weightArray.get(pos).getTag().toString());

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        }
    }

    // Constructor
    public ExerciseDataAdapter(ArrayList<Exercise> exerciseArrayList){
        mExerciseList = exerciseArrayList;

    }

    // create viewHolder and inflate view
    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // create new view from xml layout file
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exercise_row_view, viewGroup, false);
        return new ExerciseViewHolder(itemView);
    }

    // bind data into viewHolder
    @Override
    public void onBindViewHolder(ExerciseViewHolder exerciseViewHolder, int position){
        exerciseViewHolder.setIsRecyclable(false);
        String exercise = mExerciseList.get(exerciseViewHolder.getAdapterPosition()).getName();
        exerciseViewHolder.exerciseName.setText(exercise);


    }

    @Override
    public int getItemCount() {
        return mExerciseList.size();
    }

    /*
     * add id-name object to the IdNameTupleList
     */
    public void addExercise(Exercise exercise){
        mExerciseList.add(exercise);
        notifyDataSetChanged();
    }


    /*
     * temporarily save item and remove it
     */
    public void deleteItem(int position){
        // save recently deleted data items
        mRecentlyDeletedExerciseName = mExerciseList.get(position).getName();
        mRecentlyDeletedExerciseId = mExerciseList.get(position).getId();
        mRecentlyDeletedItemPosition = position;

        // delete the routine
        mExerciseList.remove(position);
        notifyItemRemoved(position);
    }

    /*
     * return the adapter data list
     */
    public ArrayList<Exercise> getData(){
        return mExerciseList;
    }


}
