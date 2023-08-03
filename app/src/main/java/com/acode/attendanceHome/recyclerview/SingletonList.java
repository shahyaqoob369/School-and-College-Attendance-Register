package com.acode.attendanceHome.recyclerview;

import android.util.Log;

import com.acode.attendanceHome.roomDataBase.Attendance;

import java.util.ArrayList;
import java.util.List;

public class SingletonList {
    private static final String TAG = "SingletonList";

    private final List<Attendance> myAttendanceList;

    private static SingletonList instance;

    private SingletonList() {
        this.myAttendanceList = new ArrayList<Attendance>();
    }

    public static SingletonList getInstance() {
        Log.d(TAG, "getInstance: : instance Created");
        if (instance == null) {
            instance = new SingletonList();
        }
        return instance;
    }

    public List<Attendance> getMyAttendanceList() {
        return myAttendanceList;
    }
}
