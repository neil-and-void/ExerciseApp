package com.example.exerciseapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RoutineRecyclerAdapter extends RecyclerView.Adapter<RoutineRecyclerAdapter.RoutineViewHolder> {
    private IdNameTupleList mRoutineList;
    private String mRecentlyDeletedRoutineName;
    private int mRecentlyDeletedRoutineId;
    private int mRecentlyDeletedItemPosition;


    /*
     * view holder for custom recycler view item
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
    public RoutineRecyclerAdapter(IdNameTupleList routineList){
        mRoutineList = routineList;
    }


    /*
     * Attach layout file to view holder
     */
    @Override
    public RoutineViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.routine_row_view,viewGroup,false);
        return new RoutineViewHolder(itemView);
    }


    /*
     * display item from the adapter to the view holder
     */
    @Override
    public void onBindViewHolder(final RoutineViewHolder routineViewHolder, final int i) {
        // get routine name and ID and set them
        routineViewHolder.routineName.setText(mRoutineList.returnName(i));

        // get context of where the adapter will be active
        final Context context = routineViewHolder.routineEdit.getContext();

        routineViewHolder.routineEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PresetExerciseListActivity.class);
                intent.putExtra("routineId", mRoutineList.returnId(i));
                context.startActivity(intent);
            }
        });
    }


    /*
     * return size of IdNameTupleList
     */
    @Override
    public int getItemCount() {
        return mRoutineList.size();
    }


    /*
     * add id-name object to the IdNameTupleList
     */
    public void addRoutine(int routineId, String routineName){ mRoutineList.add( routineId , routineName);}


    /*
     * temporarily save item and remove it
     */
    public void deleteItem(int position){
        // save recently deleted data items
        mRecentlyDeletedRoutineName = mRoutineList.returnName(position);
        mRecentlyDeletedRoutineId = mRoutineList.returnId(position);
        mRecentlyDeletedItemPosition = position;

        // delete the routine
        mRoutineList.remove(position);
        notifyItemRemoved(position);
    }


    /*
     * return the adapter data list
     */
    public IdNameTupleList getData(){
        return mRoutineList;
    }


    /*
     * add item back adapter data list
     */
    public void restoreItem(int routineId, String routineName, int position){
        mRoutineList.add(routineId, routineName, position);
        notifyItemInserted(position);
    }


    /*
     * return item from position
     */
    public String getItem(int position){return mRoutineList.returnName(position);}

}
