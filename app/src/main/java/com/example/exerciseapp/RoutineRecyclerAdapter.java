package com.example.exerciseapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RoutineRecyclerAdapter extends RecyclerView.Adapter<RoutineRecyclerAdapter.RoutineViewHolder> {
    private RoutineList mRoutineList;
    String mRecentlyDeletedRoutineName;
    int mRecentlyDeletedRoutineId;
    int mRecentlyDeletedItemPosition;


    /*
     *
     */
    public class RoutineViewHolder extends RecyclerView.ViewHolder{
        private TextView routineName;
        private TextView routineEdit;

        public RoutineViewHolder(View view){
            super(view);
            routineName = view.findViewById(R.id.workoutNameTextView);
            routineEdit = view.findViewById(R.id.editWorkoutTextView);
        }
    }

    /*
     * Constructor
     */
    public RoutineRecyclerAdapter(RoutineList routineList){
        mRoutineList = routineList;
    }

    /*
     *
     */
    @Override
    public RoutineViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workout_row_view,viewGroup,false);
        return new RoutineViewHolder(itemView);
    }

    /*
     *
     */
    @Override
    public void onBindViewHolder(final RoutineViewHolder routineViewHolder, final int i) {
        // get routine name and ID and set them
        routineViewHolder.routineName.setText(mRoutineList.returnRoutineName(i));

        // get context of where the adapter will be active
        final Context context = routineViewHolder.routineEdit.getContext();

        routineViewHolder.routineEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PresetExerciseListActivity.class);
                intent.putExtra("routineId", mRoutineList.returnRoutineId(i));
                context.startActivity(intent);
            }
        });
    }

    /*
     *
     */
    @Override
    public int getItemCount() {
        return mRoutineList.size();
    }

    /*
     *
     */
    public void addRoutine(int routineId, String routineName){
        mRoutineList.addRoutine( routineId , routineName);
    }

    /*
     *
     */
    public void deleteItem(int position){
        // save recently deleted data items
        mRecentlyDeletedRoutineName = mRoutineList.returnRoutineName(position);
        mRecentlyDeletedRoutineId = mRoutineList.returnRoutineId(position);
        mRecentlyDeletedItemPosition = position;

        // delete the routine
        mRoutineList.removeRoutine(position);
        notifyItemRemoved(position);
    }

    /*
     *
     */
    public RoutineList getData(){
        return mRoutineList;
    }

    /*
     *
     */
    public void restoreItem(int routineId, String routineName, int position){
        mRoutineList.addRoutine(routineId, routineName, position);
        notifyItemInserted(position);
    }

    /*
     *
     */
    public String getItem(int position){
        return mRoutineList.returnRoutineName(position);

    }

}
