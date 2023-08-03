package com.acode.attendanceHome.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.R;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;

import java.util.ArrayList;
import java.util.List;

public class StudentReportAdopter extends RecyclerView.Adapter<StudentReportAdopter.AttendanceViewHolder> {
    private static final String TAG = "StudentsAdaptor";

    private List<DailyAttendance> dailyAttendanceList;


    public StudentReportAdopter(List<DailyAttendance> list) {
        this.dailyAttendanceList = list;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.studentreport_itemview, parent, false);
        return new AttendanceViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
            DailyAttendance currentAttendance = dailyAttendanceList.get(position);
            holder.dateInReport.setText(currentAttendance.getAttendanceDate());
            holder.statusInReport.setText(currentAttendance.getStudentStatus());

    }

    @Override
    public int getItemCount() {
        return dailyAttendanceList.size();

    }


    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        private final TextView dateInReport;
        private final TextView statusInReport;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            dateInReport = itemView.findViewById(R.id.dateInReport);
            statusInReport = itemView.findViewById(R.id.statusInReport);

        }
    }
    //SearchView Method:
    public void filterList(ArrayList<DailyAttendance> filterList){
        dailyAttendanceList = filterList;
        notifyDataSetChanged();
    }
}
