package com.acode.attendanceHome;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.recyclerview.DailyAttendanceEditClassAdopter;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;
import com.google.android.gms.ads.FullScreenContentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DailyAttendanceEditActivity extends BaseActivity {
    private static final String TAG = "editClass";
    Intent intent;
    public static String classname;
    public static String receiveDate;
    public static List<DailyAttendance> dailyAttendanceList;
    private TextView clsEditReceiveDate;
    private RecyclerView clsEdit_recyclerview_xml;
    public static String getCurrentDate;
    private Button clsEdit_submitBtn;
    DailyAttendanceEditClassAdopter editClassAdopter;
    SearchView searchView_edit;
    LinearLayout ClsEdit_calendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_attendance_class_edit);

        //Assign Values:
        clsEditReceiveDate = findViewById(R.id.ClsEdit_recive_date);
        clsEdit_recyclerview_xml = findViewById(R.id.ClsEdit_recyclerview_xml);
        clsEdit_submitBtn = findViewById(R.id.clsEdit_submitBtn);
        clsEdit_submitBtn.setText("Update");
        searchView_edit = findViewById(R.id.searchView_edit);
        ClsEdit_calendarView = findViewById(R.id.ClsEdit_calendarView);

        //getIntent:
        intent = this.getIntent();
        classname = intent.getStringExtra(MainActivity.CLASS_NAME);
        receiveDate = intent.getStringExtra(MainActivity.DATE);
        clsEditReceiveDate.setText(receiveDate);
        getCurrentDate = receiveDate;
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFFF'> " + classname + " </font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color_main)));

        clsEdit_submitBtn.setOnClickListener(v -> {
            mViewModel.insertDailyAttendance(editClassAdopter.getEditSubmitList());
            Toast.makeText(this,"Successfully Saved",Toast.LENGTH_SHORT).show();

            //AdMob InterstitialAd:
            if (AdManager.mInterstitialAd != null) {
                AdManager.mInterstitialAd.show(DailyAttendanceEditActivity.this);

                AdManager.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        AdManager.mInterstitialAd = null;
                        AdManager.loadInterstitial(DailyAttendanceEditActivity.this);
                    }
                });
            }
            finish();
        });

        mViewModel.getAllStudentByClassNameAndDate(classname,receiveDate).observe(this, attendances -> {
            if (attendances != null) {
                Log.d(TAG, "onChanged: " + classname);
                dailyAttendanceList = attendances;
                Log.d(TAG, "onChanged: " + attendances.size());
                editClassAdopter = new DailyAttendanceEditClassAdopter(attendances);
                clsEdit_recyclerview_xml.setLayoutManager(new LinearLayoutManager(this));
                clsEdit_recyclerview_xml.setAdapter(editClassAdopter);
            }
        });

        //SearchView Methods:
        searchView_edit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        searchView_edit.setOnCloseListener(() -> {
            ClsEdit_calendarView.setVisibility(View.VISIBLE);
            return false;
        });

        searchView_edit.setOnSearchClickListener(v -> ClsEdit_calendarView.setVisibility(View.GONE));


    }

    //searchView Method:
    private void filter(String text) {
        ArrayList<DailyAttendance> filteredList = new ArrayList<>();
        for (DailyAttendance item : dailyAttendanceList) {
            String rn = String.valueOf(item.getStudentRollNo());
            if (item.getStudentName().toLowerCase().contains(text.toLowerCase()) || rn.contains(text)) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            editClassAdopter.filterList(filteredList);
        }
    }

}