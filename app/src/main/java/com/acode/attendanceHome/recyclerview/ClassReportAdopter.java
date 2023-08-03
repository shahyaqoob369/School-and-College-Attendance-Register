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

public class ClassReportAdopter extends RecyclerView.Adapter<ClassReportAdopter.AttendanceViewHolder> {
    private static final String TAG = "ClassAdaptor";

    private List<DailyAttendance> list;
    private final List<DailyAttendance> fullList;
    private final OnReportDateClickListener onReportDateClickListener;


    public ClassReportAdopter(List<DailyAttendance> list, List<DailyAttendance> fullList, OnReportDateClickListener onReportDateClickListener) {
        this.list = list;
        this.fullList = fullList;
        this.onReportDateClickListener = onReportDateClickListener;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.classreport_itemview, parent, false);
        return new AttendanceViewHolder(itemView, onReportDateClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
            DailyAttendance currentAttendance = list.get(position);
            holder.dateInClassReport.setText(currentAttendance.getAttendanceDate());
            holder.classPercentage.setText(String.valueOf(getPercentage(currentAttendance.getAttendanceDate())));

    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    private double getPercentage(String date){
        List<DailyAttendance> list = new ArrayList<>();
        int totalPrsent = 0;
        double percentage = 0.0;
        if (!fullList.isEmpty()) {
            for (DailyAttendance d : fullList) {
                if (date.equals(d.getAttendanceDate())){
                    list.add(d);
                    if (d.getStudentStatus().equals("Present")){
                        totalPrsent++;
                    }
                }
            }

            percentage = (double) (totalPrsent * 100 ) / list.size();
        }
        return percentage;
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView dateInClassReport;
        private final TextView classPercentage;
        private final OnReportDateClickListener onReportDateClickListener;

        public AttendanceViewHolder(@NonNull View itemView, OnReportDateClickListener onReportDateClickListener) {
            super(itemView);
            dateInClassReport = itemView.findViewById(R.id.dateInClassReport);
            classPercentage = itemView.findViewById(R.id.classPercentage);
            this.onReportDateClickListener = onReportDateClickListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onReportDateClickListener.onDateClick(getAdapterPosition(), v);
        }
    }

    public interface OnReportDateClickListener {
        void onDateClick(int position, View view);
    }

    //SearchView Method:
    public void filterList(ArrayList<DailyAttendance> filterList){
        list = filterList;
        notifyDataSetChanged();
    }
}
