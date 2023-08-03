package com.acode.attendanceHome.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.AdManager;
import com.acode.attendanceHome.BaseActivity;
import com.acode.attendanceHome.Host_Activity;
import com.acode.attendanceHome.MainActivity;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.dialogs.IsSubmitAttendance;
import com.acode.attendanceHome.recyclerview.StudentAttendanceAdopter;
import com.acode.attendanceHome.roomDataBase.Attendance;
import com.acode.attendanceHome.roomDataBase.AttendanceViewModel;
import com.acode.attendanceHome.roomDataBase.AttendanceViewModelFactory;
import com.google.android.gms.ads.FullScreenContentCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceFragment extends Fragment {
    private static final String TAG = "AttendanceFragment";
    public static String classname;
    public static List<Attendance> attendanceList;
    Intent intent;
    TextView current_date;
    public static String getCurrentDate = "TEST";
    Button submit_attendance_btn;
    StudentAttendanceAdopter studentAdopter;
    LinearLayout calendarView;
    String isDateAdded = "";
    SearchView searchView;
    ImageView status_menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attandence, container, false);


        attendanceList = new ArrayList<>();
        current_date = view.findViewById(R.id.current_date);
        calendarView = view.findViewById(R.id.calendarView);
        RecyclerView recyclerView = view.findViewById(R.id.student_recyclerview_xml);

        searchView = view.findViewById(R.id.searchView);
        status_menu = view.findViewById(R.id.status_menu);

        //for get class name and subject through intent
        intent = requireActivity().getIntent();
        classname = intent.getStringExtra(MainActivity.CLASS_NAME);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault());
        String simpleDate = dateFormat.format(date);
        current_date.setText(simpleDate);
        getCurrentDate = simpleDate;

        submit_attendance_btn = view.findViewById(R.id.submit_attendance);
        submit_attendance_btn.setOnClickListener(v -> {
            Log.d(TAG, "onCreateView: " + isDateAdded + "===" + getCurrentDate);
            if (!isDateAdded.equals(getCurrentDate)) {
                BaseActivity.mViewModel.insertDailyAttendance(studentAdopter.getSubmitList());
                Toast.makeText(requireContext(), "Submitted", Toast.LENGTH_SHORT).show();
                Host_Activity.viewPager.setCurrentItem(2);

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

            } else {
                IsAttendanceSubmit();
            }
        });

        AttendanceViewModel attendanceViewModel = new ViewModelProvider(
                this, new AttendanceViewModelFactory(requireActivity().getApplication(), classname)).get(AttendanceViewModel.class);

        attendanceViewModel.getallStudentsByClsNm(classname).observe(requireActivity(), attendances -> {
            if (attendances != null) {
                Log.d(TAG, "onChanged: " + classname);
                attendanceList = attendances;
                Log.d(TAG, "onChanged: " + attendances.size());
                studentAdopter = new StudentAttendanceAdopter(attendances);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                recyclerView.setAdapter(studentAdopter);
                if (attendanceList != null) {
                    if (!attendanceList.isEmpty()) {
                        submit_attendance_btn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        calendarView.setOnClickListener(v -> datePicker());

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
            calendarView.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnSearchClickListener(v -> calendarView.setVisibility(View.GONE));

        //Status_Change menu ClickListener:
        status_menu.setOnClickListener(v -> {
            Log.d(TAG, "onCreateView: clicked");
            // Initializing the popup menu and giving the reference as current context
            PopupMenu popupMenu = new PopupMenu(getActivity(), status_menu);
            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.status_change_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {

                    case R.id.all_present:
                        studentAdopter.changeStatus(requireActivity().getResources().getString(R.string.present));
                        break;

                    case R.id.all_absent:
                        studentAdopter.changeStatus(requireActivity().getResources().getString(R.string.absent));
                        break;

                    case R.id.all_leave:
                        studentAdopter.changeStatus(requireActivity().getResources().getString(R.string.leave));
                        break;

                    case R.id.all_late:
                        studentAdopter.changeStatus(requireActivity().getResources().getString(R.string.late));
                        break;
                }
                return true;
            });
            //showing Popup Menu:
            popupMenu.show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //not re-submit attendance button dialog:
        BaseActivity.mViewModel.isAttendanceAdded(getCurrentDate, classname).observe(requireActivity(), isDate -> {
            Log.d(TAG, "onResume: " + isDate);
            if (isDate != null) {
                isDateAdded = isDate;
            }
        });
        Log.d(TAG, "onResume: called");
    }

    //not re-submit attendance button alertdialog:
    public void IsAttendanceSubmit() {
        DialogFragment newFragment = new IsSubmitAttendance();
        newFragment.show(getChildFragmentManager(), "isSubmitted");
    }

    private void datePicker() {
        try {
            Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(1);
            dialog.setContentView(R.layout.attendancefregment_dateficker);
            DatePicker datePicker = dialog.findViewById(R.id.date_picker_attendFreg);
            Button btn_selectDate = dialog.findViewById(R.id.btn_selectDate);
            btn_selectDate.setOnClickListener(v -> {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                month = month + 1;
                StringBuilder d = new StringBuilder();
                StringBuilder date = new StringBuilder();
                if (day < 10) {
                    d.append("0").append(day).append("-");
                } else {
                    d.append(day).append("-");
                }
                date.append(d).append(stringMonth(month)).append("-").append(year);
                current_date.setText(date.toString());
                getCurrentDate = date.toString();
                BaseActivity.mViewModel.isAttendanceAdded(getCurrentDate, classname).observe(requireActivity(), isDate -> {
                    Log.d(TAG, "DatePicker: " + isDate);
                    if (isDate != null) {
                        isDateAdded = isDate;
                    }
                });
                dialog.dismiss();
            });
            dialog.show();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    //searchView Method:
    private void filter(String text) {
        ArrayList<Attendance> filteredList = new ArrayList<>();
        for (Attendance item : attendanceList) {
            String rn = String.valueOf(item.getRollNo());
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || rn.contains(text)) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(requireActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            studentAdopter.filterList(filteredList);
        }
    }

    //month change into String:
    private String stringMonth(int month) {
        String returnMonth = "";
        switch (month) {
            case 1:
                returnMonth = "January";
                break;

            case 2:
                returnMonth = "February";
                break;

            case 3:
                returnMonth = "March";
                break;

            case 4:
                returnMonth = "April";
                break;

            case 5:
                returnMonth = "May";
                break;

            case 6:
                returnMonth = "June";
                break;

            case 7:
                returnMonth = "July";
                break;

            case 8:
                returnMonth = "August";
                break;

            case 9:
                returnMonth = "September";
                break;

            case 10:
                returnMonth = "October";
                break;

            case 11:
                returnMonth = "November";
                break;

            case 12:
                returnMonth = "December";
                break;
        }
        return returnMonth;
    }


}