package com.example.exerciseapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PresetExerciseRecyclerAdapter extends RecyclerView.Adapter<PresetExerciseRecyclerAdapter.PresetExerciseViewHolder> {

    private RoutineList mPresetExerciseList;
    String mRecentlyDeletedPresetExerciseName;
    int mRecentlyDeletedPresetExerciseId;
    int mRecentlyDeletedItemPosition;

    /*
    *
    * view holder for custom rows in the recycler view
    *
     */
    public class PresetExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView routineName;

        public PresetExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            routineName = itemView.findViewById(R.id.presetExerciseNameTextView);
        }
    }

    /*
     * Constructor
     */
    public PresetExerciseRecyclerAdapter(RoutineList presetExerciseList){mPresetExerciseList = presetExerciseList;}

    /*
     * Attach layout file to view holder
     */
    @NonNull
    @Override
    public PresetExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.preset_exercise_row_view,viewGroup,false);
        return new PresetExerciseViewHolder(itemView);
    }

    /*
     * display item of the adapter to the view holder
     */
    @Override
    public void onBindViewHolder(@NonNull PresetExerciseViewHolder presetExerciseViewHolder, int i) {
        // set the text of the TextView in the view holder
        presetExerciseViewHolder.routineName.setText(mPresetExerciseList.returnRoutineName(i));

    }

    /*
     * returns item count
     */
    @Override
    public int getItemCount() {
        return mPresetExerciseList.size();
    }

    /*
     * add preset exercise
     */
    public void addPresetExercise(int presetExerciseId, String presetExerciseName){
        mPresetExerciseList.addRoutine(presetExerciseId, presetExerciseName);
    }

    /*
     * deletes item and saves deleted data temporarily
     */
    public void deleteItem(int position){
        // save recently deleted data items
        mRecentlyDeletedPresetExerciseName = mPresetExerciseList.returnRoutineName(position);
        mRecentlyDeletedPresetExerciseId = mPresetExerciseList.returnRoutineId(position);
        mRecentlyDeletedItemPosition = position;

        // delete
        mPresetExerciseList.removeRoutine(position);
        notifyItemRemoved(position);
    }

    /*
     * return list of names of preset exercises
     */
    public RoutineList getData(){
        return mPresetExerciseList;

    }

    /*
     * restores item to its prior position in the array
     */
    public void restoreItem(int presetExerciseId, String presetExerciseName, int position){
        mPresetExerciseList.addRoutine(presetExerciseId, presetExerciseName, position);
        notifyItemInserted(position);

    }

    /*
     * returns a specific preset exercise name at given position
     */
    public String getItem(int position){
        return mPresetExerciseList.returnRoutineName(position);

    }



}
