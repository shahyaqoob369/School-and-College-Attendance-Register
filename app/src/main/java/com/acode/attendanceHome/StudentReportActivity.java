package com.acode.attendanceHome;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.recyclerview.StudentReportAdopter;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentReportActivity extends BaseActivity {
    private static final String TAG = "StudentReportActivity";
    TextView report_studentName, report_studentRollNo,
            total_report, present_percentage, present_report, absent_report, leave_report;
    SearchView searchView;
    RecyclerView report_recyclerview_xml;
    StudentReportAdopter studentReportAdopter;
    LinearLayout reportLayOut;
    private List<DailyAttendance> dailyAttendanceList = new ArrayList<>();

    private int totalPresent = 0;
    private int totalAbsent = 0;
    private int totalLeave = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);

        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFFF'> STUDENT REPORT </font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color_main)));

        //intent student name and roll number:
        Intent intent = getIntent();
        String studentName = intent.getStringExtra(BaseActivity.EXTRA_NAME);
        int studentRN = intent.getIntExtra(BaseActivity.EXTRA_RN, 0);
        String studentCls = intent.getStringExtra(BaseActivity.EXTRA_CLS);


        //all textView find by id:
        report_studentName = findViewById(R.id.report_studentName);
        report_studentRollNo = findViewById(R.id.report_studentRollNo);
        total_report = findViewById(R.id.total_report);
        present_percentage = findViewById(R.id.present_percentage);
        present_report = findViewById(R.id.present_report);
        absent_report = findViewById(R.id.absent_report);
        leave_report = findViewById(R.id.leave_report);
        report_recyclerview_xml = findViewById(R.id.report_recyclerview_xml);
        searchView = findViewById(R.id.searchView);
        reportLayOut = findViewById(R.id.reportLayOut);

        report_studentName.setText(studentName);
        report_studentRollNo.setText(String.valueOf(studentRN));

        mViewModel.getAllStudentAttendanceByNm(studentName, studentRN, studentCls).observe(this, attendances -> {
            Log.d(TAG, "onCreateSIZ: " + attendances.size());
            studentReportAdopter = new StudentReportAdopter(attendances);
            dailyAttendanceList = attendances;
            report_recyclerview_xml.setLayoutManager(new LinearLayoutManager(StudentReportActivity.this));
            report_recyclerview_xml.setAdapter(studentReportAdopter);
            attendanceStatus(attendances);
            Log.d(TAG, "obserrver called");
        });

        //SearchView Methods:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            reportLayOut.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnSearchClickListener(v -> reportLayOut.setVisibility(View.GONE));
    }

    //searchView Method:
    private void filter(String text){
        ArrayList<DailyAttendance> filteredList = new ArrayList<>();
        for (DailyAttendance item : dailyAttendanceList){
            String  date = String.valueOf(item.getAttendanceDate());
            if (date.contains(text)){
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()){
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            studentReportAdopter.filterList(filteredList);
        }
    }

    private String getPresentPercentage(int value, int total) {
        double percentage = (double) (value * 100 ) / total;
        return String.valueOf(percentage);
    }


    private void attendanceStatus(List<DailyAttendance> newDailyAttendanceList) {
        Log.d(TAG, "attendanceStatus: called");
        for (DailyAttendance d:newDailyAttendanceList) {
            Log.d(TAG, "attendanceStatus: " + d.getStudentStatus());
            switch (d.getStudentStatus()) {
                case "Absent":
                    totalAbsent++;
                    Log.d(TAG, "attendanceStatus: absent ");
                    break;


                case "Present":
                    totalPresent++;
                    Log.d(TAG, "attendanceStatus: present");
                    break;


                case "Leave":
                    totalLeave++;
                    break;

                case "Late":
                    totalPresent++;
                    break;
            }
        }
        
            absent_report.setText(String.valueOf(totalAbsent));
            present_report.setText(String.valueOf(totalPresent));
            leave_report.setText(String.valueOf(totalLeave));
            total_report.setText(String.valueOf(newDailyAttendanceList.size()));
            present_percentage.setText(getPresentPercentage(totalPresent, newDailyAttendanceList.size()));
            Log.d(TAG, "atttt: " + getPresentPercentage(totalPresent, newDailyAttendanceList.size()));
        }


}