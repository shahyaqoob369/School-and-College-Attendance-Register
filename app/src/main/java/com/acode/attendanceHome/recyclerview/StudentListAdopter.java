package com.acode.attendanceHome.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.R;
import com.acode.attendanceHome.roomDataBase.Attendance;
import com.acode.attendanceHome.roomDataBase.AttendanceViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class StudentListAdopter extends RecyclerView.Adapter<StudentListAdopter.AttendanceViewHolder> {
    private static final String TAG = "studentList";
    private List<Attendance> list;
    private final StudentRecyclerOnClickListener mStudentRecyclerOnClickListener;
    boolean isEnable = false;
    boolean isSelectAll = false;
    List<Attendance> selectList = new ArrayList<>();
    List<Attendance> searchList;
    private final Context context;
    AttendanceViewModel myViewModel;
    private final PassCurrentAttendance passCurrentAttendance;
    private final CardView bottomMenu_Card;
    private View rootV;


    public StudentListAdopter(
            Context context,
            View rootV,
            List<Attendance> list,
            AttendanceViewModel myViewModel,
            CardView bottomMenu_Card,
            StudentRecyclerOnClickListener studentRecyclerOnClickListener,
            PassCurrentAttendance passCurrentAttendance) {
        this.list = list;
        this.mStudentRecyclerOnClickListener = studentRecyclerOnClickListener;
        this.context = context;
        this.myViewModel = myViewModel;
        this.searchList = new ArrayList<>(list);
        this.passCurrentAttendance = passCurrentAttendance;
        this.bottomMenu_Card = bottomMenu_Card;
        this.rootV = rootV;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_item, parent, false);
        return new AttendanceViewHolder(itemView, mStudentRecyclerOnClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Attendance currentStudents = list.get(position);
        setStruckOffColor(holder,currentStudents.isStruckOff());
        holder.studentNameItem_nameList.setText(currentStudents.getName());
        holder.studentRollNoItem_nameList.setText(String.valueOf(currentStudents.getRollNo()));

        //set OnLongPress:
        holder.itemView.setOnLongClickListener(v -> {
            passCurrentAttendance.onPassClick(currentStudents);
            //check condition:
            if (!isEnable) {
                //When action mode in not enable
                //Initialize action mode
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        //Initialize menu inflater
                        MenuInflater menuInflater = mode.getMenuInflater();
                        //Inflate menu
                        menuInflater.inflate(R.menu.menu_long_press, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        //When action mode is prepare
                        //Set isEnable true
                        isEnable = true;
                        //clickMethod
                        ClickItem(holder);
                        //Set observer on get text method:
                        myViewModel.getCount().observe((LifecycleOwner) context, s -> mode.setTitle(s +"/"+list.size()+ " Selected"));
                        Log.d(TAG, "onPrepareActionMode: LongPress");
                        return true;
                    }

                    @SuppressLint({"NotifyDataSetChanged", "NonConstantResourceId"})
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        Attendance undoStudent = null;
                        //When click on action mode item
                        //Get item
                        int id = item.getItemId();
                        //Use switch condition
                        switch (id) {
                            case R.id.menu_longpress_delete:
                                //When click on delete
                                //Use for loop
                                if (selectList.size() > 0){
                                    int forLoopIsEnd = 0;
                                    for (Attendance attendance : selectList) {
                                        forLoopIsEnd ++;
                                        //Remove selected item from array list:
                                        // remove selected item list
                                        undoStudent = attendance;
                                        list.remove(attendance);
                                        myViewModel.deleteStu(attendance);

                                        Log.d(TAG, "for loop size " + forLoopIsEnd);
                                    }
                                    Toast.makeText(context, selectList.size() + " student deleted", Toast.LENGTH_SHORT).show();
                                    //Showing SnackBar with UNDO option:
                                    Attendance finalUndoStudent = undoStudent;
                                    if (selectList.size() == 1) {
                                        Snackbar.make(rootV, currentStudents.getName() + " deleted ", Snackbar.LENGTH_LONG)
                                                .setAction("Undo", v1 -> {
                                                    myViewModel.insert(finalUndoStudent);
                                                }).show();
                                    }
                                    //Check condition
                                    list.size();
                                    mode.finish();
                                }
                                break;

                            case R.id.menu_longPress_selectAll:

                                if (selectList.size() == list.size()) {
                                    //When all item selected
                                    //Set isSelectAll false
                                    isSelectAll = false;
                                    //Clear select arrayList
                                    selectList.clear();
                                } else {
                                    //When all item unselected
                                    //Set isSelectAll true
                                    isSelectAll = true;
                                    //Clear select array list
                                    selectList.clear();
                                    //add all value
                                    selectList.addAll(list);
                                    bottomMenu_Card.setVisibility(View.GONE);
                                }
                                myViewModel.setSelectionCount(String.valueOf(selectList.size()));
                                // notify adapter
                                notifyDataSetChanged();
                                break;
                        }
                        return true;
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        // when action mode is destroy
                        // set isEnable false
                        isEnable = false;
                        // set isSelectAll false
                        isSelectAll = false;
                        // clear select array list
                        selectList.clear();
                        // notify adapter
                        notifyDataSetChanged();
                        bottomMenu_Card.setVisibility(View.GONE);
                    }
                };
                //Start action mode
                ((AppCompatActivity) v.getContext()).startActionMode(callback);
            } else {
                //When action mode is already enabled
                //Call method
                ClickItem(holder);
            }
            return true;
        });
        holder.itemView.setOnClickListener(view -> {
            //Check Condition
            if (isEnable) {
                //When action mode is enable
                //Call method:
                ClickItem(holder);
            } else {
                mStudentRecyclerOnClickListener.StudentListOnClick(holder.getAdapterPosition(), holder.itemView);
            }
        });
        //Check condition
        if (isSelectAll) {
            //When all value selected
            //Visible all check box
            holder.iv_check_box.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
        } else {
            //When all value not selected
            //hide check box
            holder.iv_check_box.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void setStruckOffColor(AttendanceViewHolder holder, boolean isStruckOff){
        if (isStruckOff){
            holder.studentNameItem_nameList.setTextColor(context.getResources().getColor(R.color.struckOffcolor));
            holder.studentRollNoItem_nameList.setTextColor(context.getResources().getColor(R.color.struckOffcolor));
        } else {
            holder.studentNameItem_nameList.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.studentRollNoItem_nameList.setTextColor(context.getResources().getColor(R.color.textColor));
        }
    }

    private void ClickItem(AttendanceViewHolder holder) {
        Log.d(TAG, "ClickItem: "+ selectList.size());

        //Get selected item value:
        Attendance attendance = list.get(holder.getAdapterPosition());
        if (holder.iv_check_box.getVisibility() == View.GONE) {
            //When item not selected
            //Visible check box image
            holder.iv_check_box.setVisibility(View.VISIBLE);
            //set background color
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
            //add value
            selectList.add(attendance);
        } else {
            //When item selected
            //Hide check box image
            holder.iv_check_box.setVisibility(View.GONE);
            //Set background color
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            //remove value from list
            selectList.remove(attendance);
        }
        //set text on view model
        Log.d(TAG, "list = " + selectList.size());
        myViewModel.setSelectionCount(String.valueOf(selectList.size()));
        if (selectList.size() == 1 ){
            bottomMenu_Card.setVisibility(View.VISIBLE);
        } else {
            bottomMenu_Card.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameItem_nameList, studentRollNoItem_nameList;
        StudentRecyclerOnClickListener studentRecyclerOnClickListener;
        ImageView iv_check_box;
        ConstraintLayout mainConstraintlayout;

        public AttendanceViewHolder(@NonNull View itemView, StudentRecyclerOnClickListener studentRecyclerOnClickListener) {
            super(itemView);
            studentNameItem_nameList = itemView.findViewById(R.id.studentNameItem_nameList);
            studentRollNoItem_nameList = itemView.findViewById(R.id.studentRollNoItem_nameList);
            this.studentRecyclerOnClickListener = studentRecyclerOnClickListener;
            mainConstraintlayout = itemView.findViewById(R.id.mainConstraintlayout);

            iv_check_box = itemView.findViewById(R.id.iv_check_box);
        }
    }

    public interface StudentRecyclerOnClickListener {
        void StudentListOnClick(int position, View view);
    }

    public interface PassCurrentAttendance {
        void onPassClick(Attendance attendance);
    }
    //SearchView Method:
    public void filterList(ArrayList<Attendance> filterList){
        list = filterList;
        notifyDataSetChanged();
    }

}

