package com.acode.attendanceHome.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.DailyAttendanceEditActivity;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;

import java.util.ArrayList;
import java.util.List;

public class DailyAttendanceEditClassAdopter extends RecyclerView.Adapter<DailyAttendanceEditClassAdopter.AttendanceViewHolder> {
    private static final String TAG = "StudentsAdaptor";

    private List<DailyAttendance> dailyAttendanceList;


    public DailyAttendanceEditClassAdopter(List<DailyAttendance> list) {
        this.dailyAttendanceList = list;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_attendance_edit_item, parent, false);
        return new AttendanceViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        DailyAttendance currentStudents = dailyAttendanceList.get(position);
        holder.student_name.setText(currentStudents.getStudentName());
        holder.rollNo.setText(String.valueOf(currentStudents.getStudentRollNo()));
        String st = currentStudents.getStudentStatus();
        getStatus(st, holder);

        holder.present.setOnClickListener(v -> {
            currentStudents.setStudentStatus("Present");
            Log.d(TAG, "onBindViewHolder: " + currentStudents.getStudentName() + currentStudents.getStudentStatus());
        });

        holder.leave.setOnClickListener(v -> {
            currentStudents.setStudentStatus("Leave");
        });

        holder.late.setOnClickListener(v -> {
            currentStudents.setStudentStatus("Late");
        });

        holder.absent.setOnClickListener(v -> {
            currentStudents.setStudentStatus("Absent");
        });

    }

    @Override
    public int getItemCount() {
        return dailyAttendanceList.size();

    }

    private void getStatus(String status, AttendanceViewHolder holder) {

        if (status.equals("Present")) {
            holder.present.setChecked(true);
        } else if (status.equals("Late")) {
            holder.late.setChecked(true);
        } else if (status.equals("Absent")) {
            holder.absent.setChecked(true);
        } else if (status.equals("Leave")) {
            holder.leave.setChecked(true);
        } else {
            holder.present.setChecked(true);
        }

    }


    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        private final TextView student_name;
        private final TextView rollNo;
        private RadioGroup radioGroup;
        private final RadioButton radioButton, present, late, absent, leave;


        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            student_name = itemView.findViewById(R.id.student_name);
            rollNo = itemView.findViewById(R.id.rollNo);
            radioGroup = itemView.findViewById(R.id.radiagroup);
            present = itemView.findViewById(R.id.present_check);
            late = itemView.findViewById(R.id.late_check);
            absent = itemView.findViewById(R.id.absent_check);
            leave = itemView.findViewById(R.id.leave_check);
            radioButton = itemView.findViewById(radioGroup.getCheckedRadioButtonId());

        }
    }

    public List<DailyAttendance> getEditSubmitList() {
        List<DailyAttendance> newlist = new ArrayList<>();
        if (dailyAttendanceList.size() > 0) {
            for (int i = 0; i <= dailyAttendanceList.size() - 1; i++) {
                DailyAttendance attendance = dailyAttendanceList.get(i);
                String name = attendance.getStudentName();
                String clss = attendance.getStudentClass();
                int rolno = attendance.getStudentRollNo();
                String stt = attendance.getStudentStatus();
                String date = DailyAttendanceEditActivity.getCurrentDate;
                int sno  = attendance.getsNo();

                DailyAttendance obj = new DailyAttendance(sno,rolno, name, clss, date, stt);
                newlist.add(obj);
            }
        }
        return newlist;

    }
    //SearchView Method:
    public void filterList(ArrayList<DailyAttendance> filterList){
        dailyAttendanceList = filterList;
        notifyDataSetChanged();
    }


}
