package com.acode.attendanceHome.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.AdManager;
import com.acode.attendanceHome.BaseActivity;
import com.acode.attendanceHome.DailyAttendanceEditActivity;
import com.acode.attendanceHome.MainActivity;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.monthlyReport.MonthlyReportListActivity;
import com.acode.attendanceHome.recyclerview.ClassReportAdopter;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;
import com.google.android.gms.ads.FullScreenContentCallback;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class ReportFragment extends Fragment implements ClassReportAdopter.OnReportDateClickListener {

    private static final String TAG = "ReportFragment";
    CardView cardViewGraph;
    SearchView searchView_report;
    PieChartView piechart;
    PieChartData pieChartData;
    ImageView print_report;
    List<SliceValue> pieData = new ArrayList<>();
    List<DailyAttendance> dailyAttendanceList;
    ArrayList<DailyAttendance> filterList;
    ClassReportAdopter classReportAdopter;
    LinearLayout help_layOut;
    Intent intent;
    String classname;
    String sendDate;
    RecyclerView recyclerview_Clsreport_xml;
    TextView totalAttendance_clsReport, totalPresent_clsReport,
            totalAbsent_clsReport;
    private double presentPercentage = 0.0;
    private double absentPercentage = 0.0;
    private double leavePercentage = 0.0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        recyclerview_Clsreport_xml = view.findViewById(R.id.recyclerview_Clsreport_xml);
        totalAttendance_clsReport = view.findViewById(R.id.totalAttendance_clsReport);
        totalPresent_clsReport = view.findViewById(R.id.totalPresent_clsReport);
        totalAbsent_clsReport = view.findViewById(R.id.totalAbsent_clsReport);
        piechart = view.findViewById(R.id.piechart);
        searchView_report = view.findViewById(R.id.searchView_report);
        cardViewGraph = view.findViewById(R.id.cardViewGraph);
        print_report = view.findViewById(R.id.print_report);
        help_layOut = view.findViewById(R.id.help_layOut);


        //for get class name and subject through intent
        intent = requireActivity().getIntent();
        classname = intent.getStringExtra(MainActivity.CLASS_NAME);


        pieChartData = new PieChartData(pieData);

        print_report.setOnClickListener(v -> {
            intent = new Intent(getActivity(), MonthlyReportListActivity.class);
            intent.putExtra("cn", classname);
            startActivity(intent);

            //AdMob InterstitialAd:
            if (AdManager.mInterstitialAd != null) {
                AdManager.mInterstitialAd.show(requireActivity());

                AdManager.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        AdManager.mInterstitialAd = null;
                        AdManager.loadInterstitial(requireActivity());
                    }
                });
            }

        });

        BaseActivity.mViewModel.getAllClassAttendanceByCls(classname).observe(requireActivity(), dailyAttendanceList -> {
            pieData.clear();
            this.dailyAttendanceList = dailyAttendanceList;
            classReportAdopter = new ClassReportAdopter(getDistinctList(dailyAttendanceList),
                    dailyAttendanceList, this);
            recyclerview_Clsreport_xml.setLayoutManager(new LinearLayoutManager(requireActivity()));
            recyclerview_Clsreport_xml.setAdapter(classReportAdopter);
            if (!dailyAttendanceList.isEmpty()) {
                help_layOut.setVisibility(View.GONE);
                recyclerview_Clsreport_xml.setVisibility(View.VISIBLE);
            }

            //populated pieChart from database:
            setPieChartPercentage(dailyAttendanceList);
            setPieChartData(dailyAttendanceList);

        });

        return view;
    }

    private List<DailyAttendance> getDistinctList(List<DailyAttendance> list) {
        List<DailyAttendance> myList = new ArrayList<>();
        for (DailyAttendance dailyAttendance : list) {
            if (myList.isEmpty()) {
                myList.add(dailyAttendance);
            } else {
                if (!myList.get(myList.size() - 1).getAttendanceDate().equals(dailyAttendance.getAttendanceDate())) {
                    myList.add(dailyAttendance);
                }
            }
        }
        //SearchView Methods:
        searchView_report.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        searchView_report.setOnCloseListener(() -> {
            cardViewGraph.setVisibility(View.VISIBLE);
            if (filterList != null) {
                filterList.clear();
                filterList = null;
            }
            classReportAdopter.filterList((ArrayList<DailyAttendance>) getDistinctList(dailyAttendanceList));

            return false;
        });

        searchView_report.setOnSearchClickListener(v -> cardViewGraph.setVisibility(View.GONE));
        return myList;
    }

    //searchView Method:
    private void filter(String text) {
        ArrayList<DailyAttendance> filteredList = new ArrayList<>();
        for (DailyAttendance item : getDistinctList(dailyAttendanceList)) {
            String date = String.valueOf(item.getAttendanceDate());
            if (date.contains(text)) {
                filteredList.add(item);
                filterList = filteredList;

            }
        }

        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(requireActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            classReportAdopter.filterList(filteredList);
        }
    }

    private void setPieChartData(List<DailyAttendance> dataList) {

        int Present = 0;
        int Absent = 0;
        int Leave = 0;

        for (DailyAttendance dailyA : dataList) {
            switch (dailyA.getStudentStatus()) {
                case "Present":
                    Present++;
                    break;

                case "Absent":
                    Absent++;
                    break;

                case "Leave":
                    Leave++;
                    break;
            }
        }

        pieData.add(new SliceValue(Present, Color.GREEN).setLabel(String.format("%.2f", presentPercentage) + "%"));
        pieData.add(new SliceValue(Absent, Color.RED).setLabel(String.format("%.2f", absentPercentage) + "%"));
        pieData.add(new SliceValue(Leave, Color.YELLOW).setLabel(String.format("%.2f", leavePercentage) + "%"));

        pieChartData.setHasLabels(true).setValueLabelTextSize(8);
        pieChartData.setHasCenterCircle(true);
        pieChartData.setCenterCircleScale(0.5f);
        pieChartData.setCenterText1FontSize(9).setCenterText1Color(Color.parseColor("#FFFFFF"));
        pieChartData.setCenterText1("Attendance %");
        pieChartData.setCenterCircleColor(R.color.black);
        piechart.animate();
        piechart.setPieChartData(pieChartData);

    }

    private void setPieChartPercentage(List<DailyAttendance> dailyList) {
        int tpresent = 0;
        int tabsent = 0;
        int tleave = 0;

        for (DailyAttendance daily : dailyList) {
            switch (daily.getStudentStatus()) {
                case "Present":

                case "Late":
                    tpresent++;
                    break;

                case "Absent":
                    tabsent++;
                    break;

                case "Leave":
                    tleave++;
                    break;
            }
        }
        presentPercentage = (double) (tpresent * 100) / dailyList.size();
        absentPercentage = (double) (tabsent * 100) / dailyList.size();
        leavePercentage = (double) (tleave * 100) / dailyList.size();
        totalAttendance_clsReport.setText(String.valueOf(getDistinctList(dailyList).size()));
        totalPresent_clsReport.setText(String.valueOf(tpresent));
        totalAbsent_clsReport.setText(String.valueOf(tabsent));
    }

    @Override
    public void onDateClick(int position, View view) {
        if (filterList != null && !filterList.isEmpty()) {
            sendDate = getDistinctList(filterList).get(position).getAttendanceDate();

        } else {
            sendDate = getDistinctList(dailyAttendanceList).get(position).getAttendanceDate();
        }
        Log.d(TAG, "onDateClick: position = " + position);
        Intent intent = new Intent(getContext(), DailyAttendanceEditActivity.class);
        intent.putExtra(MainActivity.CLASS_NAME, classname);
        intent.putExtra(MainActivity.DATE, sendDate);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_anim);
        view.startAnimation(animation);
        startActivity(intent);
    }
}