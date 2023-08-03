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

import com.acode.attendanceHome.R;
import com.acode.attendanceHome.fragments.AttendanceFragment;
import com.acode.attendanceHome.roomDataBase.Attendance;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;

import java.util.ArrayList;
import java.util.List;

public class StudentAttendanceAdopter extends RecyclerView.Adapter<StudentAttendanceAdopter.AttendanceViewHolder> {
    private static final String TAG = "StudentsAdaptor";

    private List<Attendance> attendanceList;


    public StudentAttendanceAdopter(List<Attendance> list) {
        this.attendanceList = list;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_attendance_item, parent, false);
        return new AttendanceViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Attendance currentStudents = attendanceList.get(position);
        holder.student_name.setText(currentStudents.getName());
        holder.rollNo.setText(String.valueOf(currentStudents.getRollNo()));
        String st = currentStudents.getStudent_status();
        getstatus(st, holder);

        holder.present.setOnClickListener(v -> {
            currentStudents.setStudent_status("Present");
            Log.d(TAG, "onBindViewHolder: " + currentStudents.getName() + currentStudents.getStudent_status());
        });

        holder.leave.setOnClickListener(v -> {
            currentStudents.setStudent_status("Leave");
        });

        holder.late.setOnClickListener(v -> {
            currentStudents.setStudent_status("Late");
        });

        holder.absent.setOnClickListener(v -> {
            currentStudents.setStudent_status("Absent");
        });

    }

    @Override
    public int getItemCount() {
        return attendanceList.size();

    }

    private void getstatus(String status, AttendanceViewHolder holder) {

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
        private RadioButton radioButton, present, late, absent, leave;


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

    public List<DailyAttendance> getSubmitList() {
        List<DailyAttendance> newlist = new ArrayList<>();
        if (attendanceList.size() > 0) {
            for (int i = 0; i <= attendanceList.size() - 1; i++) {
                Attendance attendance = attendanceList.get(i);
                String name = attendance.getName();
                String clss = attendance.getClassName();
                int rolno = attendance.getRollNo();
                String stt = attendance.getStudent_status();
                String date = AttendanceFragment.getCurrentDate;

                DailyAttendance obj = new DailyAttendance(rolno, name, clss, date, stt);
                newlist.add(obj);
            }
        }
        return newlist;

    }
    //SearchView Method:
    public void filterList(ArrayList<Attendance> filterList){
        attendanceList = filterList;
        notifyDataSetChanged();
    }
    //change attendanceStatus:
    public void changeStatus(String status) {
        for (Attendance attendance : attendanceList) {
            attendance.setStudent_status(status);
        }
        notifyDataSetChanged();
    }
}
