package com.acode.attendanceHome.monthlyReport;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;

import com.acode.attendanceHome.BaseActivity;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.recyclerview.MonthlyReportAdaptor;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MonthlyReportListActivity extends BaseActivity {
    private static final String TAG = "MonthlyReportList";
    private ListView sheet_list;
    private MonthlyReportAdaptor adapter;
    private List<DailyAttendance> listItems = new ArrayList<>();
    private String classname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report_list);

        classname = getIntent().getStringExtra("cn");

        //Set ActionBar:
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>" + classname + " Monthly Report </font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color_main)));



        sheet_list = findViewById(R.id.sheet_list);

        mViewModel.getAttendanceForMonthlyReport(classname).observe(this, list -> {
            listItems = getDistinctList(list);
            adapter = new MonthlyReportAdaptor(this,listItems);
            sheet_list.setAdapter(adapter);

        });

        sheet_list.setOnItemClickListener(
                (parent, view, position, id) ->
                        OpenMonthlyAttendanceSheet(position));
    }

    private void OpenMonthlyAttendanceSheet(int position) {


        Intent intent = new Intent(this, MonthlyAttendanceActivity.class);

       intent.putExtra("date",listItems.get(position).getAttendanceDate());
       intent.putExtra("month",getMonth(listItems.get(position).getAttendanceDate()));
       intent.putExtra("cn",classname);
       startActivity(intent);
    }

    private List<DailyAttendance> getDistinctList(List<DailyAttendance> list) {
        List<DailyAttendance> myList = new ArrayList<>();
        for (DailyAttendance dailyAttendance : list) {
            if (myList.isEmpty()) {
                myList.add(dailyAttendance);
            } else {
               boolean isMatched = false;
                for (DailyAttendance d :myList) {
                    if (getMonth(d.getAttendanceDate()).equals(
                            getMonth(dailyAttendance.getAttendanceDate()))) {
                        isMatched = true;
                        break;
                    }
                }
                if (!isMatched) { myList.add(dailyAttendance); }
            }
        }
        return myList;
    }

    private String getMonth(String attendanceDate) {
        return attendanceDate.length() >2 ? attendanceDate.substring(3) : "";
    }
}